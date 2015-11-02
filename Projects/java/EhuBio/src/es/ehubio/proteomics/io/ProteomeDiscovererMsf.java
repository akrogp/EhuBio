package es.ehubio.proteomics.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.HeaderParser;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Ptm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.Spectrum;
import es.ehubio.proteomics.Spectrum.Peak;
import es.ehubio.proteomics.pipeline.Filter;
import es.ehubio.proteomics.pipeline.Fragmenter;
import es.ehubio.proteomics.thermo.MassSpectrum;

public class ProteomeDiscovererMsf extends MsMsFile {
	private final static Logger logger = Logger.getLogger(ProteomeDiscovererMsf.class.getName());
	
	public enum PeptideConfidenceLevel {
		HIGH(3,0.01),
		MIDDLE(2,0.05),
		LOW(1,1.0);
		
		private PeptideConfidenceLevel( int level, double fdr ) {
			this.level = level;
			this.fdr = fdr;
		}
		
		public int getLevel() {
			return level;
		}

		public double getFdr() {
			return fdr;
		}

		private final int level;
		private final double fdr;
	}
	
	@Override
	protected MsMsData loadPath(String path, boolean loadFragments) throws Exception {
		Class.forName("org.sqlite.JDBC");
		Connection con = DriverManager.getConnection("jdbc:sqlite:"+path);
		logger.info("Connected to MSF file using SQLite");
		
		JAXBContext jaxbContext = JAXBContext.newInstance(MassSpectrum.class);
		peakReader = jaxbContext.createUnmarshaller();
				 			
		Map<Integer,Spectrum> spectra = loadSpectra(con, loadFragments);
		Map<Integer,List<Ptm>> ptms = loadPtms(con);
		Map<Integer,Peptide> peptides = loadPeptides(con, ptms, spectra);
		Map<Integer,Protein> proteins = loadProteins(con);
		loadRelations(con,peptides,proteins);		
		
		MsMsData data = new MsMsData();
		data.loadFromSpectra(spectra.values());
		
		Filter filter = new Filter(data);
		filter.run();
		
		if( loadFragments )
			matchFragments(con,data.getSpectra());
		
		con.close();
		
		return data;
	}
	
	@Override
	protected boolean checkSignatureStream(InputStream input) throws Exception {
		byte[] sig = new byte[SIG.length()];
		input.read(sig);
		String sigStr = new String(sig);
		return sigStr.equals(SIG);		
	}

	@Override
	public String getFilenameExtension() {
		return "msf";
	}

	private void loadRelations(Connection con, Map<Integer, Peptide> peptides, Map<Integer, Protein> proteins) throws SQLException {
		for( Entry<Integer, Peptide> entry : peptides.entrySet() ) {
			Peptide peptide = entry.getValue();
			List<Integer> proteinIds = getProteins(con, entry.getKey(), peptide.getDecoy());
			/*if( proteinIds.isEmpty() )
				System.out.println(String.format("%s (%s)", entry.getKey(), peptide.getDecoy()));*/
			for( int proteinId : proteinIds ) {
				Protein protein = proteins.get(proteinId);
				protein.linkPeptide(peptide);
			}
		}
	}
	
	private List<Integer> getProteins(Connection con, int peptide, boolean decoy ) throws SQLException {
		List<Integer> result = new ArrayList<>();
		Statement statement = con.createStatement();
		ResultSet relations = statement.executeQuery(String.format(
			"SELECT ProteinID FROM PeptidesProteins%s WHERE PeptideID=%d;", decoy?"_decoy":"", peptide));
		while( relations.next() )
			result.add(relations.getInt(1));
		return result;
	}

	private Map<Integer,Spectrum> loadSpectra(Connection con, boolean loadFragments) throws SQLException {
		Map<Integer,Spectrum> result = new HashMap<>();
		Statement statement = con.createStatement();
		ResultSet fileInfos;
		try {
			fileInfos = statement.executeQuery("SELECT FileName FROM SpectrumFileInfos;");
		} catch( SQLException e ) {
			fileInfos = statement.executeQuery("SELECT FileName FROM FileInfos;");
		}
		fileInfos.next();
		String fileName = fileInfos.getString(1);
		statement = con.createStatement();
		//ResultSet spectra = statement.executeQuery("SELECT * FROM SpectrumHeaders;");
		ResultSet spectra = statement.executeQuery("SELECT SpectrumID, FirstScan, RetentionTime, Spectrum  FROM SpectrumHeaders, Spectra WHERE Spectra.UniqueSpectrumID=SpectrumHeaders.UniqueSpectrumID;");
		while( spectra.next() ) {
			Spectrum spectrum = new Spectrum();
			spectrum.setFileName(fileName);
			spectrum.setScan(spectra.getInt("FirstScan")+"");
			spectrum.setFileId(spectrum.getScan());
			spectrum.setRt(spectra.getDouble("RetentionTime"));
			if( loadFragments )
				try {
					loadPeaks(spectrum, spectra.getBytes("Spectrum"));
				} catch (IOException | JAXBException e) {
					e.printStackTrace();
				}
			result.put(spectra.getInt("SpectrumID"), spectrum);
		}
		return result;
	}
	
	private void loadPeaks( Spectrum spectrum, byte[] bytes ) throws IOException, JAXBException {
		ZipInputStream is = new ZipInputStream(new ByteArrayInputStream(bytes));
		if( is.getNextEntry() == null )
			return;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		IOUtils.copy(is, os);
		is.close();
		os.close();
		MassSpectrum msf = (MassSpectrum)peakReader.unmarshal(new StringReader(os.toString("UTF-8")));
		for( MassSpectrum.PeakCentroids.Peak centroid : msf.getPeakCentroids().getPeak() ) {
			Peak peak = new Peak();
			peak.setCharge(centroid.getZ());
			peak.setMz(centroid.getX());
			peak.setIntensity(centroid.getY());
			spectrum.getPeaks().add(peak);
		}
	}

	private Map<Integer,Peptide> loadPeptides(Connection con, Map<Integer,List<Ptm>> ptms, Map<Integer,Spectrum> spectra) throws SQLException {
		Map<Integer,Peptide> target = loadPeptides(con,false,ptms,spectra); 
		/*Map<Integer,Peptide> decoy = loadPeptides(con,true,ptms,spectra); 
		target.putAll(decoy);*/
		return target;
	}

	private Map<Integer, Peptide> loadPeptides(Connection con, boolean decoy, Map<Integer,List<Ptm>> ptms, Map<Integer,Spectrum> spectra) throws SQLException {
		Map<Integer,Peptide> result = new HashMap<>();
		Map<String,Peptide> mapPeptides = new HashMap<>();
		Statement statement = con.createStatement();
		ResultSet scores = statement.executeQuery("SELECT ScoreID FROM ProcessingNodeScores WHERE ScoreName=\"XCorr\";");
		scores.next();
		int xcorr = scores.getInt(1);
		
		statement = con.createStatement();
		ResultSet entries = statement.executeQuery(String.format(
			"SELECT Peptides%1$s.PeptideID AS id, Sequence, Peptides%1$s.SpectrumID, SearchEngineRank, ConfidenceLevel, SpectrumHeaders.Charge AS zExp, SpectrumHeaders.Mass AS mExp, MassPeaks.Charge AS zCalc, MassPeaks.Mass AS mzCalc, ScoreValue AS xcorr "+
			"FROM Peptides%1$s, SpectrumHeaders, MassPeaks, PeptideScores%1$s "+
			"WHERE SpectrumHeaders.SpectrumID=Peptides%1$s.SpectrumID AND MassPeaks.MassPeakID=SpectrumHeaders.MassPeakID AND PeptideScores%1$s.PeptideID=Peptides%1$s.PeptideID AND ScoreID=%2$d;",
			decoy?"_decoy":"",xcorr));
		while( entries.next() ) {
			int id = entries.getInt("id");
			Peptide newPeptide = new Peptide();			
			newPeptide.setSequence(entries.getString("Sequence"));
			newPeptide.setDecoy(decoy);
			List<Ptm> list = ptms.get(id);
			if( list != null )
				newPeptide.getPtms().addAll(list);
			String idStr = newPeptide.getUniqueString();
			Peptide peptide = mapPeptides.get(idStr);
			if( peptide == null ) {
				peptide = newPeptide;
				result.put(id, peptide);
				mapPeptides.put(idStr, peptide);
			}
			Spectrum spectrum = spectra.get(entries.getInt("SpectrumID"));
			Psm psm = new Psm();
			psm.linkSpectrum(spectrum);
			psm.linkPeptide(peptide);
			psm.setRank(entries.getInt("SearchEngineRank"));
			psm.setCharge(entries.getInt("zCalc"));
			psm.setCalcMz(entries.getDouble("mzCalc"));
			psm.setExpMz(entries.getDouble("mExp")/psm.getCharge());
			psm.setScore(new Score(ScoreType.SEQUEST_XCORR, entries.getDouble("xcorr")));
			psm.setScore(new Score(ScoreType.PEPTIDE_MSF_CONFIDENCE, entries.getInt("ConfidenceLevel")));
		}
		return result;
	}
	
	private Map<Integer,List<Ptm>> loadPtms(Connection con) throws SQLException {
		Map<Integer, Ptm> ptmTypes = new HashMap<>();
		Statement statement = con.createStatement();
		ResultSet ptms = statement.executeQuery("SELECT * FROM AminoAcidModifications;");
		while( ptms.next() ) {
			Ptm ptm = new Ptm();
			ptm.setMassDelta(ptms.getDouble("DeltaMass"));
			ptm.setName(ptms.getString("ModificationName"));
			ptmTypes.put(ptms.getInt("AminoAcidModificationID"), ptm);
		}
		
		Map<Integer,List<Ptm>> map = loadPtms(con, false, ptmTypes);
		map.putAll(loadPtms(con, true, ptmTypes));
		return map;
	}

	private Map<Integer,List<Ptm>> loadPtms(Connection con, boolean decoy, Map<Integer,Ptm> ptmTypes) throws SQLException {
		Map<Integer,List<Ptm>> map = new HashMap<>();		
		Statement statement = con.createStatement();
		ResultSet ptms = statement.executeQuery(String.format(
			"SELECT PeptidesAminoAcidModifications%1$s.PeptideID AS id, AminoAcidModificationID, Position, Sequence "+
			"FROM PeptidesAminoAcidModifications%1$s, Peptides%1$s "+
			"WHERE Peptides%1$s.PeptideID=PeptidesAminoAcidModifications%1$s.PeptideID;",
			decoy?"_decoy":""));		
		while( ptms.next() ) {
			Ptm ptm = new Ptm();
			Ptm type = ptmTypes.get(ptms.getInt("AminoAcidModificationID"));
			ptm.setMassDelta(type.getMassDelta());
			ptm.setName(type.getName());
			ptm.setPosition(ptms.getInt("Position")+1);
			ptm.setResidues(ptms.getString("Sequence").charAt(ptm.getPosition()-1)+"");
			int id = ptms.getInt("id");
			List<Ptm> list = map.get(id);
			if( list == null ) {
				list = new ArrayList<>();
				map.put(id, list);
			}
			list.add(ptm);
		}
		return map;
	}

	private Map<Integer,Protein> loadProteins(Connection con) throws SQLException {
		Map<Integer,Protein> result = new HashMap<>();
		Statement statement = con.createStatement();
		ResultSet proteins = statement.executeQuery("SELECT * FROM Proteins;");
		while( proteins.next() ) {
			Protein protein = new Protein();
			protein.setSequence(proteins.getString("Sequence"));
			int id = proteins.getInt("ProteinID");
			Statement statement2 = con.createStatement();
			ResultSet descriptions = statement2.executeQuery(String.format(
				"SELECT Description FROM ProteinAnnotations WHERE ProteinID=%d;", id));
			if( !descriptions.next() )
				protein.setAccession(""+id);
			else {
				String description = descriptions.getString(1);
				if( description.startsWith(">") )
					description = description.substring(1);
				HeaderParser parser = Fasta.guessParser(description);
				if( parser == null )
					protein.setAccession(description);
				else {
					protein.setAccession(parser.getAccession());
					protein.setDescription(parser.getDescription());
					protein.setName(parser.getProteinName());
				}
			}
			result.put(id, protein);
		}		
		return result;
	}
	
	private void matchFragments(Connection con, Set<Spectrum> spectra) throws SQLException {
		boolean a = checkIons(con, 'a');
		boolean b = checkIons(con, 'b');
		boolean c = checkIons(con, 'c');
		boolean d = checkIons(con, 'd');
		boolean v = checkIons(con, 'v');
		boolean w = checkIons(con, 'w');
		boolean x = checkIons(con, 'x');
		boolean y = checkIons(con, 'y');
		boolean z = checkIons(con, 'z');
		double error = getFragmentError(con);
		boolean lossesA = a ? checkNeutralLosses(con, 'a') : false;
		boolean lossesB = b ? checkNeutralLosses(con, 'b') : false;
		boolean lossesY = y ? checkNeutralLosses(con, 'y') : false;
		
		for( Spectrum spectrum : spectra ) {
			for( Psm psm : spectrum.getPsms() ) {
				Fragmenter frag = new Fragmenter(psm);
				frag.addPrecursorIons(false,false);
				if( a ) frag.addAIons(lossesA,lossesA);
				if( b ) frag.addBIons(lossesB,lossesB);
				if( c ) frag.addCIons();
				if( d ) frag.addDIons();
				if( v ) frag.addVIons();
				if( w ) frag.addWIons();
				if( x ) frag.addXIons();
				if( y ) frag.addYIons(lossesY,lossesY);
				if( z ) frag.addZIons();
				psm.setIons(frag.match(error,true));
			}
		}
	}
	
	private boolean checkIons( Connection con, Character ch ) throws SQLException {
		Statement statement = con.createStatement();
		ResultSet ions = statement.executeQuery(String.format(
			"SELECT ParameterValue FROM ProcessingNodeParameters WHERE ParameterName=\"IonSerie%c\";",
			Character.toUpperCase(ch)));
		ions.next();
		return ions.getString(1).equals("1");
	}
	
	private boolean checkNeutralLosses( Connection con, Character ch ) throws SQLException {
		Statement statement = con.createStatement();
		ResultSet losses = statement.executeQuery(String.format(
			"SELECT ParameterValue FROM ProcessingNodeParameters WHERE ParameterName=\"UseNeutral%cIons\";",
			Character.toUpperCase(ch)));
		losses.next();
		return losses.getString(1).equalsIgnoreCase("True");
	}
	
	private double getFragmentError( Connection con ) throws SQLException {
		Statement statement = con.createStatement();
		ResultSet error = statement.executeQuery("SELECT ParameterValue FROM ProcessingNodeParameters WHERE ParameterName=\"FragmentTolerance\";");
		error.next();
		return Double.parseDouble(error.getString(1).split(" ")[0]);
	}

	private static final String SIG = "SQLite format";
	private Unmarshaller peakReader;
}
