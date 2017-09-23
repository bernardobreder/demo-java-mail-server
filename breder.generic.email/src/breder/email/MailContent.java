package breder.email;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MailContent {

	private MailInfo info;

	private String message;

	private final List<File> files;

	public MailContent(MailInfo info, String message) {
		super();
		this.info = info;
		this.message = message;
		this.files = new ArrayList<File>();
	}

	public boolean add(File e) {
		return files.add(e);
	}

	public MailInfo getInfo() {
		return info;
	}

	public void setInfo(MailInfo info) {
		this.info = info;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<File> getFiles() {
		return files;
	}

}
