package es.ehubio.proteomics.io;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.ehubio.MathUtil;
import es.ehubio.Strings;
import es.ehubio.io.CsvUtils;
import es.ehubio.proteomics.AmbiguityGroup;
import es.ehubio.proteomics.DecoyBase;
import es.ehubio.proteomics.Decoyable;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.ProteinGroup;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.Score;
import es.ehubio.proteomics.ScoreType;
import es.ehubio.proteomics.pipeline.ScoreIntegrator.ModelFitness;
import gnu.jpdf.PDFJob;

public class EhubioCsv extends MsMsFile {
	//private final static Logger logger = Logger.getLogger(EhubioCsv.class.getName());
	private final MsMsData data;
	private final static char SEP = '\t';
	private final static char INTER = ',';
	private ScoreType psmScoreType = ScoreType.XTANDEM_EVALUE;	

	public EhubioCsv( MsMsData data ) {
		this.data = data;
	}
	
	public ScoreType getPsmScoreType() {
		return psmScoreType;
	}

	public void setPsmScoreType(ScoreType psmScoreType) {
		this.psmScoreType = psmScoreType;
	}

	@Override
	protected boolean savePath(String dir) throws Exception {
		File file = new File(dir);
		if( !file.isDirectory() )
			dir = file.getParent();
		//logger.info(String.format("Saving CSVs to '%s' ...", dir));
		savePsms(new File(dir,"psms.csv").getAbsolutePath());
		savePeptides(new File(dir,"peptides.csv").getAbsolutePath());
		saveProteins(new File(dir,"proteins.csv").getAbsolutePath());
		saveTranscripts(new File(dir,"transcripts.csv").getAbsolutePath());
		saveGenes(new File(dir,"genes.csv").getAbsolutePath());
		saveAmbiguityGroups(new File(dir,"groups.csv").getAbsolutePath());
		return true;
	}			

	private void savePsms( String path ) throws IOException {		
		PrintWriter pw = new PrintWriter(path);
		pw.println(CsvUtils.getCsv(SEP,
			"id","decoy","calcMz","expMz","charge","file","spectrum",
			psmScoreType.getName(),
			ScoreType.PSM_P_VALUE.getName(),
			ScoreType.PSM_LOCAL_FDR.getName(),
			ScoreType.PSM_Q_VALUE.getName(),
			ScoreType.PSM_FDR_SCORE.getName(),
			ScoreType.LPS_SCORE.getName(),
			"passThreshold"
			));
		for( Psm psm : data.getPsms() )
			pw.println(CsvUtils.getCsv(SEP,
				psm.getId(), Boolean.TRUE.equals(psm.getDecoy()), psm.getCalcMz(), psm.getExpMz(), psm.getCharge(),
				psm.getSpectrum().getFileName(), psm.getSpectrum().getFileId(),
				getScore(psm,psmScoreType),
				getScore(psm,ScoreType.PSM_P_VALUE),
				getScore(psm,ScoreType.PSM_LOCAL_FDR),
				getScore(psm,ScoreType.PSM_Q_VALUE),
				getScore(psm,ScoreType.PSM_FDR_SCORE),
				getScore(psm,ScoreType.LPS_SCORE),
				psm.isPassThreshold()
				));
		pw.close();
	}
	
	private void savePeptides(String path) throws IOException {
		PrintWriter pw = new PrintWriter(path);
		pw.println(CsvUtils.getCsv(SEP,
			"id","decoy","confidence","sequence","ptms","psms","#psms",
			ScoreType.PEPTIDE_P_VALUE.getName(),
			ScoreType.PEPTIDE_LOCAL_FDR.getName(),
			ScoreType.PEPTIDE_Q_VALUE.getName(),
			ScoreType.PEPTIDE_FDR_SCORE.getName(),
			ScoreType.LPP_SCORE.getName(),
			"passThreshold"
			));
		for( Peptide peptide : data.getPeptides() )
			pw.println(CsvUtils.getCsv(SEP,
				peptide.getId(), Boolean.TRUE.equals(peptide.getDecoy()), peptide.getConfidence(), peptide.getSequence(), peptide.getMassSequence(),
				CsvUtils.getCsv(INTER, peptide.getPsms().toArray()),
				peptide.getPsms().size(),
				getScore(peptide,ScoreType.PEPTIDE_P_VALUE),
				getScore(peptide,ScoreType.PEPTIDE_LOCAL_FDR),
				getScore(peptide,ScoreType.PEPTIDE_Q_VALUE),
				getScore(peptide,ScoreType.PEPTIDE_FDR_SCORE),
				getScore(peptide,ScoreType.LPP_SCORE),
				peptide.isPassThreshold()
				));
		pw.close();
	}
	
	private void saveProteins(String path) throws IOException {
		PrintWriter pw = new PrintWriter(path);
		pw.println(CsvUtils.getCsv(SEP,
			"accession","decoy","confidence","peptides","#peptides",
			ScoreType.PROTEIN_P_VALUE.getName(),
			ScoreType.PROTEIN_LOCAL_FDR.getName(),
			ScoreType.PROTEIN_Q_VALUE.getName(),
			ScoreType.PROTEIN_FDR_SCORE.getName(),
			ScoreType.LPQCORR_SCORE.getName(),
			ScoreType.LPQ_SCORE.getName(),
			ScoreType.MQ_EVALUE.getName(),
			ScoreType.MQ_OVALUE.getName(),
			ScoreType.NQ_EVALUE.getName(),
			ScoreType.NQ_OVALUE.getName(),
			//psmScoreType.getName(),
			//"#protBestPsm",
			//"pepString",
			"passThreshold"
			));
		for( Protein protein : data.getProteins() ) {
			//Psm bestPsm = protein.getBestPsm(psmScoreType);
			pw.println(CsvUtils.getCsv(SEP,
				protein.getAccession(), Boolean.TRUE.equals(protein.getDecoy()), protein.getConfidence(),
				CsvUtils.getCsv(INTER, protein.getPeptides().toArray()),
				protein.getPeptides().size(),
				getScore(protein,ScoreType.PROTEIN_P_VALUE),
				getScore(protein,ScoreType.PROTEIN_LOCAL_FDR),
				getScore(protein,ScoreType.PROTEIN_Q_VALUE),
				getScore(protein,ScoreType.PROTEIN_FDR_SCORE),
				getScore(protein,ScoreType.LPQCORR_SCORE),
				getScore(protein,ScoreType.LPQ_SCORE),
				getScore(protein,ScoreType.MQ_EVALUE),
				getScore(protein,ScoreType.MQ_OVALUE),
				getScore(protein,ScoreType.NQ_EVALUE),
				getScore(protein,ScoreType.NQ_OVALUE),
				//getScore(bestPsm,psmScoreType),
				//bestPsm.getPeptide().getProteins().size(),
				//bestPsm.getPeptide().getSequence(),
				protein.isPassThreshold()
				));
		}
		pw.close();
	}
	
	private void saveTranscripts(String path) throws FileNotFoundException {
		saveProteinGroup(path, data.getTranscripts());
	}
	
	private void saveGenes(String path) throws FileNotFoundException {
		saveProteinGroup(path, data.getGenes());
	}
	
	private void saveProteinGroup(String path, Collection<? extends ProteinGroup> groups) throws FileNotFoundException {
		if( groups.isEmpty() )
			return;
		PrintWriter pw = new PrintWriter(path);
		pw.println(CsvUtils.getCsv(SEP,
			"accession","name","decoy","confidence","proteins","#proteins","peptides","#peptides",
			ScoreType.GROUP_P_VALUE.getName(),
			ScoreType.GROUP_LOCAL_FDR.getName(),
			ScoreType.GROUP_Q_VALUE.getName(),
			ScoreType.GROUP_FDR_SCORE.getName(),
			ScoreType.LPGCORR_SCORE.getName(),
			ScoreType.LPG_SCORE.getName(),
			ScoreType.MG_EVALUE.getName(),
			ScoreType.MG_OVALUE.getName(),
			"passThreshold"
			));
		for( ProteinGroup group : groups )
			pw.println(CsvUtils.getCsv(SEP,
				group.getAccession(), group.getName(), Boolean.TRUE.equals(group.getDecoy()), group.getConfidence(),
				CsvUtils.getCsv(INTER, group.getProteins().toArray()),
				group.getProteins().size(),
				CsvUtils.getCsv(INTER, group.getPeptides().toArray()),
				group.getPeptides().size(),
				getScore(group,ScoreType.GROUP_P_VALUE),
				getScore(group,ScoreType.GROUP_LOCAL_FDR),
				getScore(group,ScoreType.GROUP_Q_VALUE),
				getScore(group,ScoreType.GROUP_FDR_SCORE),
				getScore(group,ScoreType.LPGCORR_SCORE),
				getScore(group,ScoreType.LPG_SCORE),
				getScore(group,ScoreType.MG_EVALUE),
				getScore(group,ScoreType.MG_OVALUE),
				group.isPassThreshold()
				));
		pw.close();
	}

	private void saveAmbiguityGroups(String path) throws IOException {
		PrintWriter pw = new PrintWriter(path);
		String item = Strings.plural(data.getAmbiguityLevel().getName());
		pw.println(CsvUtils.getCsv(SEP,
			"id","name","decoy","confidence",item,"#"+item,"peptides","#peptides",
			ScoreType.GROUP_P_VALUE.getName(),
			ScoreType.GROUP_LOCAL_FDR.getName(),
			ScoreType.GROUP_Q_VALUE.getName(),
			ScoreType.GROUP_FDR_SCORE.getName(),
			ScoreType.LPGCORR_SCORE.getName(),
			ScoreType.LPG_SCORE.getName(),
			ScoreType.MG_EVALUE.getName(),
			ScoreType.MG_OVALUE.getName(),
			"passThreshold"
			));
		for( AmbiguityGroup group : data.getGroups() )
			pw.println(CsvUtils.getCsv(SEP,
				group.getId(), group.buildName(), Boolean.TRUE.equals(group.getDecoy()), group.getConfidence(),
				CsvUtils.getCsv(INTER, group.getItems().toArray()),
				group.size(),
				CsvUtils.getCsv(INTER, group.getParts().toArray()),
				group.getParts().size(),
				getScore(group,ScoreType.GROUP_P_VALUE),
				getScore(group,ScoreType.GROUP_LOCAL_FDR),
				getScore(group,ScoreType.GROUP_Q_VALUE),
				getScore(group,ScoreType.GROUP_FDR_SCORE),
				getScore(group,ScoreType.LPGCORR_SCORE),
				getScore(group,ScoreType.LPG_SCORE),
				getScore(group,ScoreType.MG_EVALUE),
				getScore(group,ScoreType.MG_OVALUE),
				group.isPassThreshold()
				));
		pw.close();
	}
	
	public static void saveModel(MsMsData data, String dirPath, ModelFitness fitness) throws IOException {
		saveModel(data, new File(dirPath, "modelNq.pdf").getAbsolutePath(), fitness, false);
		saveModel(data, new File(dirPath, "modelMq.pdf").getAbsolutePath(), fitness, true);
	}
	
	public static void saveModel(MsMsData data, String path, ModelFitness fitness, boolean shared) throws IOException {
		PDFJob job = new PDFJob(new FileOutputStream(path),data.getTitle());
		Graphics g = job.getGraphics(PageFormat.LANDSCAPE);
		
		double[] x = new double[data.getDecoyProteinCount()];
		double[] y = new double[x.length];
		int i = 0;
		for( Protein protein : data.getProteins() ) {
			if( protein.isTarget() )
				continue;
			x[i] = protein.getScoreByType(shared?ScoreType.MQ_EVALUE:ScoreType.NQ_EVALUE).getValue();
			y[i++] = protein.getScoreByType(shared?ScoreType.MQ_OVALUE:ScoreType.NQ_OVALUE).getValue();
		}
		if( shared )
			drawPlot(g, data.getTitle(), "Mq(exp)", x, "Mq(obs)", y, fitness.getMm(), fitness.getR2m());
		else
			drawPlot(g, data.getTitle(), "Nq(exp)", x, "Nq(obs)", y, fitness.getNm(), fitness.getR2n());
		
		g.dispose();
		job.end();
	}
	
	public static void saveModel(Collection<? extends Decoyable> items, ScoreType obsType, ScoreType expType, String title, String path) throws FileNotFoundException {
		List<Double> obs = new ArrayList<>(items.size());
		List<Double> exp = new ArrayList<>(items.size());
		double[] x = new double[items.size()];
		double[] y = new double[items.size()];
		int i = 0;
		for( Decoyable item : items ) {
			x[i] = item.getScoreByType(expType).getValue();
			exp.add(x[i]);
			y[i] = item.getScoreByType(obsType).getValue();
			obs.add(y[i]);
			i++;
		}
		double mean = MathUtil.mean(obs);
		double r2 = MathUtil.r2(obs, exp);
		PDFJob job = new PDFJob(new FileOutputStream(path),title);
		Graphics g = job.getGraphics(PageFormat.LANDSCAPE);		
		drawPlot(g, title, expType.getName(), x, obsType.getName(), y, mean, r2);
		g.dispose();
		job.end();
	}
	
	private static void drawPlot(Graphics g, String title, String xlabel, double[] x, String ylabel, double[] y, double mean, double r2 ) {		
		Color cAxes = Color.BLACK;
		Color cExp = Color.MAGENTA;
		Color cMean = Color.RED;
		Color cData = Color.BLUE;
		
		Rectangle bounds = g.getClipBounds();
		g.setClip(new Rectangle(0, 0, bounds.x+bounds.width, bounds.y+bounds.height));
		bounds = g.getClipBounds();
		int w = bounds.width;
		int h = bounds.height;
		
		Font f = new Font("TimesRoman", Font.PLAIN, 16);
		g.setFont(f);
		FontMetrics fm = g.getFontMetrics(f);
		int x0 = fm.stringWidth(ylabel);
		int y0 = h-1-fm.getHeight();
		
		double max = x[0];
		for( int i = 1; i < x.length; i++ ) {
			if( x[i] > max )
				max = x[i];
			if( y[i] > max )
				max = y[i];
		}
		double xmax = max/(w-x0);
		double ymax = max/y0;
				
		g.setColor(cAxes);		
		g.drawLine(x0, 0, x0, y0);
		g.drawLine(x0, y0, w-1, y0);
		g.drawString(xlabel,(w-fm.stringWidth(xlabel))/2,h-1);		
		g.drawString(ylabel, 0, (h-fm.getHeight())/2);
		g.drawString(title, x0+(w-x0-fm.stringWidth(title))/2, fm.getHeight());

		g.setColor(cMean);
		int yMean = y0-(int)Math.round(mean/ymax);
		g.drawLine(x0, yMean, w-1, yMean);
		String strMean = String.format("mean=%.1f", mean);
		g.drawString(strMean, w-fm.stringWidth(strMean), yMean-1);
		g.setColor(cExp);
		g.drawLine(x0, y0, w-1, 0);
		
		g.setColor(cData);				
		String strMax = String.format("%.0f", max);
		g.drawString(strMax, x0-fm.stringWidth(strMax), fm.getHeight());
		g.drawString(strMax, w-fm.stringWidth(strMax)-1, h-1);		
		for( int i = 0; i < x.length; i++ )
			g.fillRect((int)Math.round(x[i]/xmax)+x0-2, y0-(int)Math.round(y[i]/ymax)-2, 4, 4);
		g.drawString(String.format("R^2=%.2f", r2), x0+10, 3*fm.getHeight());
	}
	
	/*private static void drawStringRotated(Graphics g, double x, double y, double degress, String str) {
		Graphics2D g2 = (Graphics2D)g;
		degress = degress/180*Math.PI;
		g2.translate(x, y);
		g2.rotate(degress);
		g2.drawString(str, 0, 0);
		g2.rotate(-degress);
		g2.translate(-x, -y);		
	}*/
	
	private static Object getScore( DecoyBase item, ScoreType type ) {
		Score score = item.getScoreByType(type);
		if( score == null )
			return "";
		return score.getValue();
	}

	@Override
	public String getFilenameExtension() {
		return "tsv";
	}
}