package org.breder.email;

import java.io.IOException;

/**
 * Represents a full email address, including username and domain. This class
 * performs conversions between a full email address, and a username and domain.
 */
public class EmailAddress {

  /** Usuário */
  private String username = "";
  /** Dominio */
  private String domain = "";
  /** Vazio */
  private boolean isEmpty = false;

  /**
   * Creates an empty email address. This is possible form SMTP messages that
   * have no MAIL FROM address.
   */
  public EmailAddress() {
    this.isEmpty = true;
  }

  /**
   * Creates a new instance of this class using a single string that contains
   * the full email address (joe@mydomain.com).
   * 
   * @param fullAddress
   * @throws IOException
   */
  public EmailAddress(String fullAddress) throws IOException {
    this.setFullAddress(fullAddress);
  }

  /**
   * Creates a new instance of this class using a username string and an address
   * string.
   * 
   * @param username
   * @param domain
   */
  public EmailAddress(String username, String domain) {
    setUsername(username);
    setDomain(domain);
  }

  /**
   * @return usuário
   */
  public String getUsername() {
    if (isEmpty) {
      return "";
    }
    else {
      return username;
    }
  }

  /**
   * @param username
   */
  public void setUsername(String username) {
    this.isEmpty = false;
    this.username = username.trim().toLowerCase();
  }

  /**
   * @return dominio
   */
  public String getDomain() {
    if (isEmpty) {
      return "";
    }
    else {
      return domain;
    }
  }

  /**
   * @param domain
   */
  public void setDomain(String domain) {
    this.isEmpty = false;
    this.domain = domain.trim().toLowerCase();
  }

  /**
   * @return endereço
   */
  public String getAddress() {
    return getFullAddress(getUsername(), getDomain());
  }

  /**
   * @param fullAddress
   * @throws IOException
   */
  public void setAddress(String fullAddress) throws IOException {
    setFullAddress(fullAddress);
  }

  /**
   * Combines a username and domain into a single email address.
   * 
   * @param username
   * @param domain
   * @return nome completo
   */
  private String getFullAddress(String username, String domain) {
    if (isEmpty) {
      return "";
    }
    else {
      StringBuffer fullAddress = new StringBuffer(username);
      fullAddress.append("@");
      fullAddress.append(domain);
      return fullAddress.toString();
    }
  }

  /**
   * Parses a full address into a username and password for storage.
   * 
   * @param fullAddress
   * @throws IOException
   */
  private void setFullAddress(String fullAddress) throws IOException {
    int index = fullAddress.indexOf("@");
    if (index == -1) {
      throw new IOException();
    }
    setUsername(fullAddress.substring(0, index));
    setDomain(fullAddress.substring(index + 1));
    isEmpty = false;
  }

  /**
   * Override tostring to return the full address
   */
  @Override
  public String toString() {
    return getAddress();
  }

}