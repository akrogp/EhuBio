package es.ehubio.dubase.bl;

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

import es.ehubio.dubase.dl.entities.Experiment;

@LocalBean
@Stateless
public class Submitter {
	@Resource(name="es.ehubio.dubase.rcptTo")
	private String rcptTo;
	
	public void submitProteomics(Experiment e, String notes) throws Exception {
		sendMail(e, notes);
	}
	
	public void submitManual(Experiment e, String notes, String accessions, String genes) throws Exception {
	}

	private void sendMail(Experiment e, String notes) throws Exception {
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
		String msg = notes == null || notes.isEmpty() ?
			"Please consider the attached experiment for its inclussion into DUBase" : notes;		
		bodyPart.setContent(msg, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(bodyPart);
		message.setContent(multipart);
		Transport.send(message);
	}
}
