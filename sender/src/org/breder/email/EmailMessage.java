package org.breder.email;

import java.util.List;

/**
 * Mensagem
 * 
 * @author bernardobreder
 */
public class EmailMessage {

	private final List<String> from;

	private final List<String> to;

	private final String subject;

	private final String message;

	/**
	 * Construtor
	 * 
	 * @param from
	 * @param to
	 * @param subject
	 * @param message
	 */
	public EmailMessage(List<String> from, List<String> to, String subject, String message) {
		super();
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.message = message;
	}

	/**
	 * Retorna
	 * 
	 * @return from
	 */
	public List<String> getFrom() {
		return from;
	}

	/**
	 * Retorna
	 * 
	 * @return to
	 */
	public List<String> getTo() {
		return to;
	}

	/**
	 * Retorna
	 * 
	 * @return subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Retorna
	 * 
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((to == null) ? 0 : to.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		EmailMessage other = (EmailMessage) obj;
		if (from == null) {
			if (other.from != null) {
				return false;
			}
		} else if (!from.equals(other.from)) {
			return false;
		}
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.equals(other.message)) {
			return false;
		}
		if (subject == null) {
			if (other.subject != null) {
				return false;
			}
		} else if (!subject.equals(other.subject)) {
			return false;
		}
		if (to == null) {
			if (other.to != null) {
				return false;
			}
		} else if (!to.equals(other.to)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "EmailMessage [subject=" + subject + "]";
	}

}
