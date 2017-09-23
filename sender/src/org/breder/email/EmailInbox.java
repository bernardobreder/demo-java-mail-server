package org.breder.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Email
 * 
 * @author Tecgraf
 */
public class EmailInbox {

	/**
	 * Lista os emails
	 * 
	 * @param host
	 * @param username
	 * @param password
	 * @return lista de messagem
	 * @throws MessagingException
	 * @throws IOException
	 */
	public static List<Message> list(String host, String username, String password) throws MessagingException, IOException {
		Store store = getStore(host, username, password);
		Folder inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_WRITE);
		List<Message> list = new ArrayList<Message>();
		Message[] messages = inbox.getMessages();
		for (int i = 0; i < messages.length; i++) {
			Message message = messages[i];
			message.getContent();
			list.add(0, message);
		}
		inbox.close(false);
		store.close();
		return list;
	}

	/**
	 * Envia um email
	 * 
	 * @param host
	 * @param username
	 * @param password
	 * @param message
	 * @throws MessagingException
	 */
	public static void send(String host, final String username, final String password, EmailMessage message) throws MessagingException {
		Session session = getSession("smtp." + host, username, password);
		Transport transport = session.getTransport("smtp");
		transport.connect();
		String from = message.getFrom().get(0);
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from));
		for (String toStr : message.getTo()) {
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toStr));
		}
		msg.setSentDate(new Date());
		msg.setSubject(message.getSubject());
		msg.setContent(message.getMessage(), "text/html; charset=UTF-8");
		transport.sendMessage(msg, msg.getAllRecipients());
		transport.close();
	}

	/**
	 * @param host
	 * @param username
	 * @param password
	 * @return
	 */
	private static Session getSession(String host, final String username, final String password) {
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		return session;
	}

	/**
	 * Remove um email
	 * 
	 * @param host
	 * @param username
	 * @param password
	 * @param message
	 * @throws MessagingException
	 * @throws IOException
	 */
	public static void remove(String host, String username, String password, Message message) throws MessagingException, IOException {
		Store store = getStore(host, username, password);
		Folder inbox = store.getFolder("INBOX");
		inbox.open(Folder.READ_WRITE);
		Message[] messages = inbox.getMessages();
		for (int i = 0; i < messages.length; i++) {
			Message msg = messages[i];
			if (Arrays.equals(msg.getFrom(), message.getFrom())) {
				if (Arrays.equals(msg.getAllRecipients(), message.getAllRecipients())) {
					if (msg.getSubject().equals(message.getSubject())) {
						if (getContent(msg.getContent()).equals(getContent(message.getContent()))) {
							msg.setFlag(Flag.DELETED, true);
						}
					}
				}
			}
		}
		inbox.close(true);
		store.close();
	}

	/**
	 * Recupera o Store
	 * 
	 * @param host
	 * @param username
	 * @param password
	 * @return store
	 * @throws MessagingException
	 */
	private static Store getStore(String host, String username, String password) throws MessagingException {
		Session session = getSession("pop." + host, username, password);
		Store store = session.getStore("pop3");
		store.connect(host, username, password);
		return store;
	}

	/**
	 * Retorna a lista de msg
	 * 
	 * @param content
	 * @return msgs
	 * @throws MessagingException
	 * @throws IOException
	 */
	private static List<String> getContent(Object content) throws IOException, MessagingException {
		List<String> list = new ArrayList<String>();
		if (content instanceof MimeMultipart) {
			MimeMultipart msg = (MimeMultipart) content;
			for (int n = 0; n < msg.getCount(); n++) {
				BodyPart bodyPart = msg.getBodyPart(n);
				list.addAll(getContent(bodyPart.getContent()));
			}
		} else {
			list.add(content.toString());
		}
		return list;
	}

	/**
	 * Testador
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String host = "gmail.com";
		String username = "bernardobreder@gmail.com";
		String password = "";
		String message = "<a href='http://dl.dropbox.com/u/5580759/index.js'>test</a>";
		List<String> froms = Arrays.asList("bernardobreder@gmail.com");
		List<String> tos = Arrays.asList("guilhermebreder@hotmail.com");
		String subject = "Teste";
		send(host, username, password, new EmailMessage(froms, tos, subject, message));
		// List<Message> msgs = list(host, username, password);
		// for (Message msg : msgs) {
		// System.out.println(msg.getSubject());
		// System.out.println(Arrays.toString(msg.getFrom()));
		// System.out.println(Arrays.toString(msg.getReplyTo()));
		// System.out.println(Arrays.toString(msg.getAllRecipients()));
		// System.out.println("------------------");
		// }
		// remove(host, username, password, msgs.get(0));
	}

}
