package breder.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MailInfo {

	private Date date;

	private String from;

	private final List<String> tos;

	private String subject;

	public MailInfo(String from, String subject) {
		super();
		this.date = new Date();
		this.from = from;
		this.tos = new ArrayList<String>();
		this.subject = subject;
	}

	public boolean addTo(String e) {
		return tos.add(e);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public List<String> getTos() {
		return tos;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
