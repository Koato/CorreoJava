package Envio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Correo {
	public static void main(String[] args) throws AddressException, UnsupportedEncodingException {
//		String destinatario = "i201622650@cibertec.edu.pe";
		InternetAddress[] destinatarios = { new InternetAddress("i201622650@cibertec.edu.pe", "Cibertec") };
		String asunto = "Correo de prueba enviado desde Java";
		String cuerpo = "Esta es una prueba de correo...";

		enviarConGMail(destinatarios, asunto, cuerpo);
	}

	private static void enviarConGMail(InternetAddress[] destinatarios, String asunto, String cuerpo) {
		String remitente = "andy.gomez.2124@gmail.com"; // Para la dirección nomcuenta@gmail.com
		String clave = "ordinola109"; // La clave de la cuenta

		Properties props = System.getProperties();
		props.put("mail.smtp.host", "smtp.gmail.com"); // El servidor SMTP de Google
//		props.put("mail.smtp.host", "outlook.office365.com"); // El servidor SMTP de Outlook
//		props.put("mail.smtp.host", "smtp.live.com"); // El servidor SMTP de Hotmail
		props.put("mail.smtp.user", remitente);
		props.put("mail.smtp.clave", clave);
		props.put("mail.smtp.auth", "true"); // Usar autenticación mediante usuario y clave
		props.put("mail.smtp.starttls.enable", "true"); // Para conectar de manera segura al servidor SMTP
		props.put("mail.smtp.port", "587"); // El puerto SMTP seguro de Google y Outlook
//		props.put("mail.smtp.port", "25"); // El puerto SMTP seguro de Hotmail

		Session session = Session.getDefaultInstance(props);
		session.setDebug(true);
		MimeMessage message = new MimeMessage(session);

		try {
			// anexo los archivos adjuntos
			MimeMultipart multiParte = new MimeMultipart();
			multiParte.addBodyPart(adjuntarImagen());
			multiParte.addBodyPart(adjuntarDocumento());
			multiParte.addBodyPart(designCorreo());
			message.setFrom(new InternetAddress(remitente, "Gmail"));
			// destinatarios
			message.setRecipients(Message.RecipientType.TO, destinatarios);
			// con copia
			message.addRecipient(Message.RecipientType.CC, new InternetAddress("speed21@outlook.com", "Kato"));
			// con copia oculta
			message.addRecipient(Message.RecipientType.BCC, new InternetAddress("andy@pecano.pe", "Pecano"));
			// asigno el asunto del correo
			message.setSubject(asunto);
			// asigno el cuerpo del correo
			multiParte.addBodyPart(cuerpoCorreo(cuerpo));
			// se agrega la informacion adjunta
			message.setContent(multiParte);
			message.setSentDate(new Date());

			Transport transport = session.getTransport("smtp");
			transport.connect("smtp.gmail.com", remitente, clave);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			System.out.println("Mensaje enviado exitosamente");
		} catch (MessagingException me) {
			me.printStackTrace(); // Si se produce un error
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static BodyPart cuerpoCorreo(String mensaje) throws MessagingException {
		BodyPart cuerpo = new MimeBodyPart();
		cuerpo.setText("Cuerpo del correo en el mensaje ");
		return cuerpo;
	}

	private static BodyPart adjuntarImagen() throws MessagingException {
		BodyPart adjunto = new MimeBodyPart();
		// Cargamos la imagen
		adjunto.setDataHandler(new DataHandler(new FileDataSource("L:/ciclismo.jpeg")));
		// Opcional. De esta forma transmitimos al receptor el nombre original del
		// fichero de imagen.
		adjunto.setFileName("ciclismo.jpeg");
		return adjunto;
	}

	private static BodyPart adjuntarDocumento() throws MessagingException, IOException {
		MimeBodyPart adjunto = new MimeBodyPart();
		adjunto.attachFile("L:/Requisitos.pdf");
		return adjunto;
	}

	private static BodyPart designCorreo() throws MessagingException, IOException {
		String cadena;
		FileReader f = new FileReader("L:/invoice.html");
		BufferedReader b = new BufferedReader(f);
		StringBuilder texto = new StringBuilder();
		while ((cadena = b.readLine()) != null) {
			texto.append(cadena);
		}
		b.close();
//		System.out.println(texto.toString());
//		System.exit(0);
		MimeBodyPart adjunto = new MimeBodyPart();
		adjunto.setContent(texto.toString(), "text/html");
		return adjunto;
	}
}