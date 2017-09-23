package breder.email.gmail;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Classe respons�vel por enviar email.
 * 
 * 
 * @author bbreder
 */
public class GMailSender {

  /**
   * Envia um email para um destinat�rio no formato html
   * 
   * @param to
   * @param subject
   * @param message
   * @throws IOException
   */
  public static void sendMail(String from, char[] password, String to,
    String subject, String message, List<File> files) throws IOException {
    try {
      Message msg =
        new MimeMessage(GmailManager.getInstance().getSmtpSession());
      msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
      msg.setFrom(new InternetAddress(from));
      msg.setSubject(subject);
      msg.setSentDate(new Date());
      if (files != null && files.size() > 0) {
        Multipart mp = new MimeMultipart();
        MimeBodyPart mbp1 = new MimeBodyPart();
        mbp1.setText(message);
        mp.addBodyPart(mbp1);
        for (File file : files) {
          MimeBodyPart mbp2 = new MimeBodyPart();
          FileDataSource fds = new FileDataSource(file);
          mbp2.setDataHandler(new DataHandler(fds));
          mbp2.setFileName(fds.getName());
          mp.addBodyPart(mbp2);
        }
        msg.setContent(mp);
      }
      else {
        msg.setContent(new String(message.getBytes("UTF-8")),
          "text/html; charset=UTF-8");
      }
      msg.saveChanges();
      Transport tr = GmailManager.getInstance().buildTransport(from, password);
      tr.sendMessage(msg, msg.getAllRecipients());
      tr.close();
    }
    catch (IOException e) {
      throw e;
    }
    catch (Exception e) {
      throw new IOException(e);
    }
  }

}
