package es.ehubio.panalyzer.html;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import es.ehubio.db.fasta.Fasta;
import es.ehubio.io.CsvUtils;
import es.ehubio.panalyzer.Configuration.Replicate;
import es.ehubio.panalyzer.MainModel;
import es.ehubio.panalyzer.MainModel.CountReport;
import es.ehubio.panalyzer.MainModel.FdrReport;
import es.ehubio.proteomics.Peptide;
import es.ehubio.proteomics.Protein;
import es.ehubio.proteomics.Psm;
import es.ehubio.proteomics.ScoreType;

public class HtmlReport {
	private static final String DATA = "html";
	private static final String STYLE = "style.css";
	private static final String INDEX = "index.html";
	private static final String CONFIG = "config.html";
	private static final String PROTEINS = "proteins.html";
	private static final int TAB_INDEX = 1;
	private static final int TAB_CONFIG = 2;	
	private static final int TAB_PROTEINS = 3;
	private static final String SEP = ", ";
	
	private final MainModel model;
	private File htmlDir;
	private File htmlFile;
	
	public HtmlReport( MainModel model ) {
		this.model = model;		
	}
	
	public void create() throws IOException {
		String exp = model.getConfig().getDescription();
		htmlDir = new File(model.getConfig().getOutput(),DATA);		
		htmlDir.mkdir();		
		htmlFile = new File(htmlDir,PROTEINS);
		
		Reader input = new InputStreamReader(HtmlReport.class.getResource(STYLE).openStream());
		Writer output = new PrintWriter(new File(htmlDir,STYLE));
		IOUtils.copy(input, output);
		input.close();
		output.close();
		
		input = new InputStreamReader(HtmlReport.class.getResource(INDEX).openStream());
		output = new PrintWriter(new File(model.getConfig().getOutput(),INDEX));
		IOUtils.copy(input, output);
		input.close();
		output.close();
		
		writeFile(INDEX, String.format("PAnalyzer results for %s",exp), TAB_INDEX, getSummary());
		writeFile(CONFIG, String.format("PAnalyzer configuration for %s",exp), TAB_CONFIG, getConfig());
		writeFile(PROTEINS, String.format("Proteins in %s",exp), TAB_PROTEINS, getProteinList());
		for( Protein protein : model.getData().getProteins() )
			writeFile(
				String.format("%s.html", protein.getAccession()),
				String.format("Protein details for %s", protein.getAccession()),
				TAB_PROTEINS,
				getProteinDetails(protein));
	}			

	public File getHtmlFile() {
		return htmlFile;
	}
	
	private void writeFile( String file, String title, int tab, String content ) throws IOException {
		StringWriter buffer = new StringWriter();
		Reader input = new InputStreamReader(HtmlReport.class.getResource("skel.html").openStream());
		Writer output = new PrintWriter(buffer);
		IOUtils.copy(input, output);
		input.close();
		output.close();
		String str = buffer.toString();
		str = str.replaceAll("@TITLE", title);
		str = str.replaceAll("@TAB", tab+"");
		str = str.replaceAll("@CONTENT", content);		
		PrintWriter pw = new PrintWriter(new File(htmlDir,file));
		pw.print(str);
		pw.close();
	}
	
	private String getConfig() {
		HtmlTable table = new HtmlTable();
		table.setTitle("Analysis Configuration");
		table.addRow("Software",String.format("<a href=\"%s\">%s</a>", MainModel.URL, MainModel.SIGNATURE));
		table.addRow("Experiment",model.getConfig().getDescription());
		table.addPropertyRow(model.getConfig(),
			"psmRankThreshold","psmScore","bestPsmPerPrecursor","bestPsmPerPeptide","psmFdr","psmScoreThreshold",
			"minPeptideLength","uniquePeptides","peptideFdr","peptideScoreThreshold","minPeptideReplicates",
			"proteinFdr","proteinScoreThreshold","minProteinReplicates","groupFdr","groupScoreThreshold",
			"decoyRegex");
		table.addRow("inputs",getInputsLinks());		
		table.addPropertyRow(model.getConfig(),"filterDecoys");
		table.addRow("output",
			String.format("<a href=\"file://%1$s\">%1$s</a>", model.getConfig().getOutput()));
		return table.render();
	}
	
	private String getInputsLinks() {
		HtmlTable table = new HtmlTable();
		table.setStyle("border:none; width:100%");
		table.setColStyle(0, "white-space: nowrap");
		table.setColStyle(1, "width: 99%");
		for( Replicate replicate : model.getConfig().getReplicates() ) {
			List<String> list = new ArrayList<>();
			for( String input : replicate.getFractions() )
				list.add(String.format("<a href=\"file://%1$s\">%1$s</a>", input));
			table.addRow(replicate.getName(),CsvUtils.getCsv(SEP, list.toArray()));
		}
		return table.render(true,false);
	}

	private String getSummary() {
		HtmlTable counts = new HtmlTable();
		counts.setTitle("Counts");
		counts.setHeader("Type","Target","Decoy","Total");
		counts.setColStyle(1, "text-align: right");
		counts.setColStyle(2, "text-align: right");
		counts.setColStyle(3, "text-align: right");
		for( CountReport count : model.getCountReport() )
			counts.addRow(count.getTitle(),count.getTarget()+"",count.getDecoy()+"",count.getTotal()+"");
		
		HtmlTable fdrs = new HtmlTable();
		fdrs.setTitle("Global FDRs");
		fdrs.setHeader("Level","Value","Threshold");
		fdrs.setColStyle(1, "text-align: right");
		fdrs.setColStyle(2, "text-align: right");
		for( FdrReport fdr : model.getFdrReport() )
			fdrs.addRow(fdr.getTitle(),fdr.getValue(),fdr.getThreshold());
		
		return counts.render()+"\n<br/>\n"+fdrs.render();
	}
	
	private String getProteinList() {
		List<Protein> proteins = new ArrayList<>(model.getData().getProteins());
		Collections.sort(proteins, new Comparator<Protein>() {
			@Override
			public int compare(Protein o1, Protein o2) {
				int diff = o1.getConfidence().compareTo(o2.getConfidence());
				if( diff != 0 )
					return diff;
				return o1.getAccession().compareToIgnoreCase(o2.getAccession());
			}
		});
		HtmlTable table = new HtmlTable();
		table.setTitle("Protein List");
		table.setHeader(
			"Accession",
			//"Name",
			"Evidence",
			"Peptide list (unique, discriminating*, non-discriminating**)",
			"Description");
		for( Protein protein : proteins ) {
			table.addRow(
				String.format("<a href=\"%1$s.html\">%1$s</a>", protein.getAccession()),
				//protein.getName(),
				protein.getConfidence().toString(),
				CsvUtils.getCsv(SEP, protein.getPeptides().toArray()),				
				trim(protein.getDescription(),120));
		}
		return table.render();
	}
	
	private String getProteinDetails(Protein protein) {
		HtmlTable table = new HtmlTable();
		//table.setColStyle(0, "white-space: nowrap");
		//table.setColStyle(1, "width: 99%");
		table.setTitle(String.format("Protein %s", protein.getAccession()));
		table.addRow("Name",protein.getName()==null?"":protein.getName());
		table.addRow("Description",protein.getDescription()==null?"":protein.getDescription());
		table.addRow("Sequence",String.format("<pre>%s</pre>", Fasta.formatSequence(protein.getSequence(), 10)));
		table.addRow("Evidence",protein.getConfidence().toString());
		table.addRow("Peptide list",getPeptideLinks(protein.getPeptides()));
		table.addRow("Peptides",getPeptidesDetails(protein));
		return table.render();
	}
	
	private String getPeptideLinks(Set<Peptide> peptides) {
		List<String> list = new ArrayList<>();
		for( Peptide peptide : peptides )
			list.add(String.format("<a href=\"#pep%d\">%s</a>", peptide.getId(), peptide.toString()));
		return CsvUtils.getCsv(SEP, list.toArray());
	}

	private String getPeptidesDetails(Protein protein) {
		HtmlTable table = new HtmlTable();
		table.setStyle("border:none; width:100%");
		table.setColStyle(0, "white-space: nowrap");
		table.setColStyle(1, "width: 99%");
		boolean odd = false;
		for( Peptide peptide : protein.getPeptides() ) {
			table.addRow(peptide.toString(),getPeptideDetails(peptide,odd));
			odd = !odd;
		}
		return table.render(false,true);
	}

	private String getPeptideDetails(Peptide peptide, boolean odd) {
		HtmlTable table = new HtmlTable();
		table.setStyle("border:none; width:100%");
		table.setColStyle(0, "white-space: nowrap");
		table.setColStyle(1, "width: 99%");
		table.setHold(true);
		table.addRow(
			String.format("<a name=\"pep%d\">Type</a>",peptide.getId()),
			peptide.getConfidence().toString());
		table.addRow("Proteins",getProteinLinks(peptide.getProteins()));
		table.addRow("Sequence",peptide.getSequence());
		table.addRow("Modifications",peptide.getPtms().isEmpty()?"none":CsvUtils.getCsv(SEP, peptide.getPtms().toArray()));
		table.addRow("PSMs score (rank)",getPsmsDetails(peptide));
		table.addRow("Replicates",CsvUtils.getCsv(SEP, peptide.getReplicates().toArray()));
		return table.render(odd,true);
	}

	private String getPsmsDetails(Peptide peptide) {
		ScoreType psmScore = model.getConfig().getPsmScore();
		List<String> list = new ArrayList<>();
		for( Psm psm : peptide.getPsms() )
			list.add(String.format("%s (%s)",psm.getScoreByType(psmScore).getValue(),psm.getRank()));
		return String.format("%s: %s", psmScore.getName(), CsvUtils.getCsv(", ", list.toArray()));
	}

	private String getProteinLinks(Set<Protein> proteins) {
		List<String> list = new ArrayList<>();
		for( Protein protein : proteins )
			list.add(String.format("<a href=\"%1$s.html\">%1$s</a>", protein.getAccession()));
		return CsvUtils.getCsv(SEP, list.toArray());
	}

	private String trim( String str, int max ) {
		if( str == null )
			return "";
		if( str.length() < max )
			return str;
		return String.format("%s...", str.substring(0,max-2));
	}
}