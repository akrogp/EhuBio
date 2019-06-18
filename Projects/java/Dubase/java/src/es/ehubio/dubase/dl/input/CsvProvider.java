package es.ehubio.dubase.dl.input;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.db.fasta.Fasta.SequenceType;
import es.ehubio.dubase.dl.entities.Ambiguity;
import es.ehubio.dubase.dl.entities.EvScore;
import es.ehubio.dubase.dl.entities.Evidence;
import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.entities.Gene;
import es.ehubio.dubase.dl.entities.ModType;
import es.ehubio.dubase.dl.entities.Modification;
import es.ehubio.dubase.dl.entities.Protein;
import es.ehubio.dubase.dl.entities.RepScore;
import es.ehubio.dubase.dl.entities.Replicate;
import es.ehubio.dubase.dl.entities.SupportingFile;
import es.ehubio.io.CsvReader;

public class CsvProvider implements Provider {

	@Override
	public List<Evidence> loadEvidences(String dir, Experiment exp) throws Exception {
		SupportingFile csv = exp.getSupportingFiles().stream()
				.filter(file -> file.getFileType().getId() == FileType.UGO_CSV.ordinal())
				.findFirst()
				.get();		
		SupportingFile fasta = exp.getSupportingFiles().stream()
				.filter(file -> file.getFileType().getId() == FileType.FASTA.ordinal())
				.findFirst()
				.get();
		return loadUgoEvidences(exp, new File(dir,csv.getName()), new File(dir,fasta.getName()));
	}
	
	private List<Evidence> loadUgoEvidences(Experiment exp, File csvFile, File fastaFile) throws Exception {		
		List<Evidence> evs = new ArrayList<>();		
		Map<String, Fasta> mapFasta = Fasta.readEntries(fastaFile.getAbsolutePath(), SequenceType.PROTEIN).stream()
			.collect(Collectors.toMap(Fasta::getAccession, Function.identity()));
		
		try( CsvReader csv = new CsvReader("\t", true, false) ) {
			csv.open(csvFile.getAbsolutePath());
			
			while( csv.readLine() != null ) {
				Evidence ev = new Evidence();
				ev.setExperimentBean(exp);
				int uniq = setPeptides(ev, csv);
				int size = getGroupSize(uniq, csv);
				setAmbiguities(ev, csv, mapFasta, size);
				addEvScores(ev, csv);
				setRepScores(ev, csv);
				setModifications(ev, csv);				
				evs.add(ev);
			}
		}
		return evs;
	}
	
	private void setModifications(Evidence ev, CsvReader csv) {
		ev.setModifications(new ArrayList<>());
		if( csv.getFields().length <= IDX_MODS )
			return;
		ModType modType = new ModType();
		modType.setId(es.ehubio.dubase.dl.input.ModType.GLYGLY.ordinal());
		for( String pos : csv.getField(IDX_MODS).split(";") ) {
			Modification mod = new Modification();
			mod.setModType(modType);
			mod.setPosition(Integer.parseInt(pos));
			ev.addModification(mod);
		}
	}

	private void setRepScores(Evidence ev, CsvReader csv) {
		ev.setRepScores(new ArrayList<>());
		int i = IDX_REPS;
		for( es.ehubio.dubase.dl.entities.Condition cond : ev.getExperimentBean().getConditions() )
			for( Replicate rep : cond.getReplicates() ) {
				ev.addRepScore(newRepScore(rep, ScoreType.LFQ_INTENSITY, csv.getDoubleField(i), csv.getIntField(i+1) == 1));
				i += 2;
			}
	}
	
	private RepScore newRepScore(Replicate rep, ScoreType type, Number value, boolean imputed) {
		es.ehubio.dubase.dl.entities.ScoreType dbType = new es.ehubio.dubase.dl.entities.ScoreType();
		dbType.setId(type.ordinal());
		
		RepScore score = new RepScore();
		score.setReplicateBean(rep);
		score.setScoreType(dbType);
		score.setValue(value == null ? null : value.doubleValue());
		score.setImputed(imputed);
		return score;
	}

	private EvScore newEvScore(ScoreType type, Number value) {
		es.ehubio.dubase.dl.entities.ScoreType dbType = new es.ehubio.dubase.dl.entities.ScoreType();
		dbType.setId(type.ordinal());
		
		EvScore score = new EvScore();
		score.setScoreType(dbType);
		score.setValue(value == null ? null : value.doubleValue());
		return score;
	}
	
	private void addEvScores(Evidence ev, CsvReader csv) {
		ev.addEvScore(newEvScore(ScoreType.MOL_WEIGHT, csv.getDoubleField(IDX_MOL_WEIGHT)));
		ev.addEvScore(newEvScore(ScoreType.SEQ_COVERAGE, csv.getDoubleField(IDX_SEQ_COVER)));
		ev.addEvScore(newEvScore(ScoreType.FOLD_CHANGE, Math.log(csv.getDoubleField(IDX_FOLD_CHANGE))/LOG2));
		ev.addEvScore(newEvScore(ScoreType.P_VALUE, -Math.log10(csv.getDoubleField(IDX_P_VALUE))));
	}

	private int getGroupSize(int uniq, CsvReader csv) {
		String[] uniqCounts = csv.getField(IDX_UNIQ_COUNTS).split(";");
		int count = 0;
		for( String tmp : uniqCounts )
			if( Integer.parseInt(tmp) == uniq )
				count++;
			else
				break;
		return count;
	}

	private int setPeptides(Evidence ev, CsvReader csv) {
		ev.setEvScores(new ArrayList<>());
		ev.addEvScore(newEvScore(ScoreType.TOTAL_PEPTS, csv.getIntField(IDX_TOTAL_PEPTS)));		
		int uniqPepts = csv.getIntField(IDX_UNIQ_PEPTS);
		ev.addEvScore(newEvScore(ScoreType.UNIQ_PEPTS, uniqPepts));		
		return uniqPepts;		
	}

	private void setAmbiguities(Evidence ev, CsvReader csv, Map<String, Fasta> mapFasta, int groupSize) throws Exception {
		ev.setAmbiguities(new ArrayList<>());
		String[] accs = csv.getField(IDX_PROTS).split(";");
		for( String acc : accs ) {
			Fasta fasta = mapFasta.get(acc);
			if( fasta == null )
				throw new Exception(acc + " not found in the fasta database");
			
			Gene gene = new Gene();
			gene.setName(fasta.getGeneName() == null ? acc : fasta.getGeneName());
			
			Protein prot = new Protein();
			prot.setAccession(acc);
			prot.setName(fasta.getProteinName());
			prot.setDescription(fasta.getDescription());
			prot.setGeneBean(gene);
			
			Ambiguity amb = new Ambiguity();
			amb.setProteinBean(prot);			
			ev.addAmbiguity(amb);
			
			if( --groupSize <= 0 )
				break;
		}
	}

	//private static final int IDX_GENES = 1;
	//private static final int IDX_DESC = 2;
	private static final int IDX_TOTAL_PEPTS = 3;
	private static final int IDX_UNIQ_PEPTS = 4;
	private static final int IDX_PROTS = 6;
	private static final int IDX_UNIQ_COUNTS = 9;
	private static final int IDX_MOL_WEIGHT = 10;
	private static final int IDX_SEQ_COVER = 11;
	private static final int IDX_FOLD_CHANGE = 12;
	private static final int IDX_P_VALUE = 13;
	//private static final int IDX_SAMPLE = 14;	
	//private static final int IDX_CONTROL = 20;
	private static final int IDX_REPS = 14;
	private static final int IDX_MODS = 26;
	
	private static final double LOG2 = Math.log(2);
}
