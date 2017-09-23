package breder.email;

import java.io.IOException;
import java.util.List;

public interface MailBox {

	public List<MailInfo> getInbox() throws IOException;

	public MailContent getContent(MailInfo info) throws IOException;

	public void send(MailInfo info, MailContent content) throws IOException;

	public List<String> getContacts() throws IOException;

	public void refresh() throws IOException;

}
