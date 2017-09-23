package breder.email.gmail;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

public class GmailManager {

  private static final GmailManager instance = new GmailManager();

  /**
   * Host do servidor de email
   */
  private String HOST_SMTP = "smtp.gmail.com";

  /**
   * Host do servidor de email
   */
  private String HOST_POP = "pop.gmail.com";

  /**
   * Porta do servidor de email
   */
  private String PORT = "465";

  private Session smtpSession;

  private Session popSession;

  private GmailManager() {
    {
      Properties props = new Properties();
      props.put("mail.transport.protocol", "smtp");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", HOST_SMTP);
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.port", PORT);
      props.put("mail.smtp.socketFactory.port", PORT);
      props.put("mail.smtp.socketFactory.class",
        "javax.net.ssl.SSLSocketFactory");
      props.put("mail.smtp.socketFactory.fallback", "false");
      this.smtpSession = Session.getDefaultInstance(props, null);
      this.smtpSession.addProvider(new Provider(Provider.Type.TRANSPORT,
        "smtp", "com.sun.mail.smtp.SMTPTransport", "Sun Microsystems, Inc",
        null));
    }
    {
      String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
      Properties pop3Props = new Properties();
      pop3Props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
      pop3Props.setProperty("mail.pop3.socketFactory.fallback", "false");
      pop3Props.setProperty("mail.pop3.port", "995");
      pop3Props.setProperty("mail.pop3.socketFactory.port", "995");
      this.popSession = Session.getInstance(pop3Props, null);
    }
  }

  public Transport buildTransport(String username, char[] password)
    throws IOException {
    try {
      Transport tr = this.getSmtpSession().getTransport("smtp");
      tr.connect(HOST_SMTP, username, new String(password));
      return tr;
    }
    catch (Exception e) {
      throw new IOException(e);
    }
  }

  public Store buildStore(String username, String password) throws IOException {
    try {
      Store store = this.popSession.getStore("pop3");
      store.connect(HOST_POP, 995, username, password);
      return store;
    }
    catch (Exception e) {
      throw new IOException(e);
    }
  }

  public Session getSmtpSession() {
    return smtpSession;
  }

  public static GmailManager getInstance() {
    return instance;
  }

}
