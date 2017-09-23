package org.breder.email;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Bean class used to store incoming SMTP message on disk (via Java
 * Serialization) for delivery by the SMTPSender thread.
 * 
 * @author Eric Daugherty
 */
public class Email implements Serializable {

  /** Data do email */
  private Date date;
  /** Endereço de origem */
  private EmailAddress fromAddress;
  /** Endereços de destino */
  private List<EmailAddress> toAddresses = new ArrayList<EmailAddress>();
  /** Assunto */
  private String subject;
  /** Conteúdo */
  private String content;

  /**
   * Construtor
   * 
   * @param fromAddress
   * @param toAddresses
   * @param subject
   * @param content
   */
  public Email(EmailAddress fromAddress, List<EmailAddress> toAddresses,
    String subject, String content) {
    this(new Date(), fromAddress, toAddresses, subject, content);
  }

  /**
   * Construtor
   * 
   * @param date
   * @param fromAddress
   * @param toAddresses
   * @param subject
   * @param content
   */
  public Email(Date date, EmailAddress fromAddress,
    List<EmailAddress> toAddresses, String subject, String content) {
    super();
    this.date = date;
    this.fromAddress = fromAddress;
    this.toAddresses = toAddresses;
    this.subject = subject;
    this.content = content;
  }

  /**
   * Retorna
   * 
   * @return date
   */
  public Date getDate() {
    return date;
  }

  /**
   * @param date
   */
  public void setDate(Date date) {
    this.date = date;
  }

  /**
   * Retorna
   * 
   * @return fromAddress
   */
  public EmailAddress getFromAddress() {
    return fromAddress;
  }

  /**
   * @param fromAddress
   */
  public void setFromAddress(EmailAddress fromAddress) {
    this.fromAddress = fromAddress;
  }

  /**
   * Retorna
   * 
   * @return toAddresses
   */
  public List<EmailAddress> getToAddresses() {
    return toAddresses;
  }

  /**
   * @param toAddresses
   */
  public void setToAddresses(List<EmailAddress> toAddresses) {
    this.toAddresses = toAddresses;
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
   * @param subject
   */
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Retorna
   * 
   * @return content
   */
  public String getContent() {
    return content;
  }

  /**
   * @param content
   */
  public void setContent(String content) {
    this.content = content;
  }

}
