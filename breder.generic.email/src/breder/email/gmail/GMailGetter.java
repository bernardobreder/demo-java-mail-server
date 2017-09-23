package breder.email.gmail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;

import breder.email.MailInfo;

/**
 * Classe respons�vel por enviar email.
 * 
 * 
 * @author bbreder
 */
public class GMailGetter {

	/**
	 * Envia um email para um destinat�rio no formato html
	 * 
	 * @param tos
	 * @param subject
	 * @param message
	 * @throws IOException
	 */
	public static List<MailInfo> getMails(String username, String password)
			throws IOException {
		try {
			Store store = GmailManager.getInstance().buildStore(username,
					password);
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_ONLY);
			Message[] messages = folder.getMessages();
			List<MailInfo> infos = new ArrayList<MailInfo>();
			for (Message message : messages) {
				String from = username;
				String subject = message.getSubject();
				MailInfo info = new MailInfo(from, subject);
				for (Address address : message.getReplyTo()) {
					info.addTo(address.toString());
				}
				infos.add(info);
			}
			return infos;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	public static void main(String[] args) throws IOException {
		List<MailInfo> mails = GMailGetter.getMails("bernardobreder@gmail.com", "B_24813612_b");
		for(MailInfo info : mails){
			System.out.println(info.getSubject());
		}
	}
}
