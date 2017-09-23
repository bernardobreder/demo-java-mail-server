package breder.email.gmail;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * Classe que armazena todos os contatos de uma conta gmail
 * 
 * 
 * @author bbreder
 */
public class GmailContact {

  /**
   * Serviço gmail
   */
  private ContactsService service;

  /**
   * Nome do usuario
   */
  private String username;

  /**
   * Construtor padrão
   * 
   * @param username
   * @param password
   * @throws IOException
   */
  public GmailContact(String username, char[] password) throws IOException {
    try {
      this.username = username;
      this.service = new ContactsService("example");
      service.setUserCredentials(this.username, new String(password));
    }
    catch (AuthenticationException e) {
      throw new IOException(e);
    }
  }

  /**
   * Retorna a lista de email do contato
   * 
   * @return lista de emails
   * @throws IOException
   */
  public List<String> getEmails() throws IOException {
    try {
      List<String> emails = new ArrayList<String>();

      // Create query and submit a request
      URL feedUrl =
        new URL("https://www.google.com/m8/feeds/contacts/default/full");
      Query myQuery = new Query(feedUrl);
      myQuery.setMaxResults(4000);
      ContactFeed resultFeed = service.query(myQuery, ContactFeed.class);
      System.out.println("Numero de contatos: "
        + resultFeed.getEntries().size());
      for (ContactEntry entry : resultFeed.getEntries()) {
        for (Email email : entry.getEmailAddresses()) {
          String address = email.getAddress().trim();
          if (!address.equals(this.username)) {
            emails.add(email.getAddress());
          }
        }
      }
      return emails;
    }
    catch (ServiceException e) {
      throw new IOException(e);
    }
  }

  /**
   * Metodo de teste
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    String username = "bernardobreder@gmail.com";
    String password = "";
    GmailContact main = new GmailContact(username, password.toCharArray());
    for (String email : main.getEmails()) {
      System.out.println(email);
    }
  }

}
