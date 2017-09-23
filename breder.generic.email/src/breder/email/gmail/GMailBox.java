package breder.email.gmail;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import breder.email.MailBox;
import breder.email.MailContent;
import breder.email.MailInfo;

public class GMailBox implements MailBox {

  private String username;

  private char[] password;

  private List<MailInfo> infos;

  private Map<String, MailContent> contents;

  private List<String> contacts;

  public GMailBox(String username, char[] password) throws IOException {
    this.username = username.toLowerCase().trim();
    this.password = password;
    if (!this.username.endsWith("@gmail.com")) {
      throw new IllegalArgumentException("username not a email");
    }
    GmailManager.getInstance().buildTransport(username, password);
    new Thread("GMail - Contact") {
      @Override
      public void run() {
        try {
          GMailBox.this.getContacts();
        }
        catch (IOException e) {
        }
      }
    }.start();
  }

  @Override
  public List<MailInfo> getInbox() throws IOException {
    if (this.infos == null) {
    }
    return this.infos;
  }

  @Override
  public MailContent getContent(MailInfo info) throws IOException {
    MailContent content = this.contents.get(info);
    if (content == null) {

    }
    return content;
  }

  @Override
  public synchronized List<String> getContacts() throws IOException {
    if (this.contacts == null) {
      GmailContact contact = new GmailContact(username, password);
      this.contacts = contact.getEmails();
    }
    return this.contacts;
  }

  @Override
  public void send(MailInfo info, MailContent content) throws IOException {
    info.setFrom(this.username);
    for (String to : info.getTos()) {
      GMailSender.sendMail(info.getFrom(), password, to, info.getSubject(),
        content.getMessage(), content.getFiles());
    }
  }

  @Override
  public void refresh() throws IOException {
    this.infos = null;
  }

}
