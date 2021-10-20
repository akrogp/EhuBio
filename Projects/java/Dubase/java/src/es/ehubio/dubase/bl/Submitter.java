package es.ehubio.dubase.bl;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import es.ehubio.dubase.dl.entities.Experiment;
import es.ehubio.dubase.dl.input.Metafile;
import es.ehubio.dubase.dl.input.providers.UgoManualProvider.Header;

@LocalBean
@Stateless
public class Submitter {
	@Resource(name="es.ehubio.dubase.rcptTo")
	private String rcptTo;
	@Resource(name="es.ehubio.dubase.tmpDir")
	private String tmpDir;
	
	public String submitProteomics(Experiment e, String notes) throws Exception {
		File file = Files.createTempFile(Paths.get(tmpDir), "DXP-request-", ".xml").toFile();
		Metafile.save(e, file);
		sendMail(e, notes, file);
		return file.getName();
	}
	
	public String submitManual(Experiment e, String notes, String accessions, String genes) throws Exception {
		File file = Files.createTempFile(Paths.get(tmpDir), "DXP-request-", ".xlsx").toFile();
		try(
			FileOutputStream fos = new FileOutputStream(file);
			Workbook workbook = new XSSFWorkbook();
		) {			
			Sheet sheet = workbook.createSheet();
			Row headerRow = sheet.createRow(0);
			int column = 0;
			for( Header header : Header.values() )
				headerRow.createCell(column++).setCellValue(header.toString());
			Row dataRow = sheet.createRow(1);
			dataRow.createCell(Header.DUB.ordinal()).setCellValue(e.getEnzymeBean().getGene());
			dataRow.createCell(Header.DOI.ordinal()).setCellValue(e.getPublications().get(0).getDoi());
			dataRow.createCell(Header.PMID.ordinal()).setCellValue(e.getPublications().get(0).getPmid());
			dataRow.createCell(Header.Gene.ordinal()).setCellValue(genes);
			dataRow.createCell(Header.UniProt.ordinal()).setCellValue(accessions);
			dataRow.createCell(Header.Organism.ordinal()).setCellValue(e.getCellBean().getTaxonBean().getId());
			dataRow.createCell(Header.Cell.ordinal()).setCellValue(e.getCellBean().getName());
			dataRow.createCell(Header.Figure.ordinal()).setCellValue(e.getSupportingFiles().get(0).getName());
			dataRow.createCell(Header.FigureURL.ordinal()).setCellValue(e.getSupportingFiles().get(0).getUrl());
			dataRow.createCell(Header.ProteasomeInhibition.ordinal()).setCellValue(Boolean.TRUE.equals(e.getMethodBean().getProteasomeInhibition()) ? 1 : 0);
			dataRow.createCell(Header.Method.ordinal()).setCellValue(e.getMethodBean().getSubtype().getId());
			workbook.write(fos);
			sendMail(e, notes, file);
			return file.getName();
		}
	}

	private void sendMail(Experiment e, String notes, File attachment ) throws Exception {
		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.ehu.es");
		prop.put("mail.smtp.port", "25");
		Session session = Session.getInstance(prop);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(e.getAuthorBean().getMail(), e.getAuthorBean().getName()));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(rcptTo));
		message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse("gorka.prieto@ehu.eus"));
		message.setSubject("DUBase submission");

		MimeBodyPart bodyPart = new MimeBodyPart();
		StringBuilder msg = new StringBuilder(notes == null || notes.isEmpty() ?
			"Please consider this experiment for its inclussion into DUBase." : notes);
		msg.append("<br/><br/>--- See experiment details in the attached file ---");
		msg.append("<br/><br/>Thanks,");
		msg.append("<br/>DUBase submission form.");
		bodyPart.setContent(msg.toString(), "text/html");
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(bodyPart);
		
		if( attachment != null ) {
			MimeBodyPart attachmentPart = new MimeBodyPart();
			attachmentPart.attachFile(attachment);
			multipart.addBodyPart(attachmentPart);
		}
		
		message.setContent(multipart);
		Transport.send(message);
	}
}
