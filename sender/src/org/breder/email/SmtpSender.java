package org.breder.email;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

/**
 * Processador de Stmp
 *
 * @author bbreder
 *
 */
public class SmtpSender {

  /** Dominio Local */
  private static final String LOCAL_DOMAIN = "mydomain.com";
  /** Saída */
  private PrintWriter out;
  /** Entrada */
  private BufferedReader in;
  /** Origem */
  private EmailAddress from;
  /** Destino */
  private EmailAddress to;
  /** Lista de Linhas */
  private List<String> dataSet;

  /**
   * Construtor
   *
   * @param date
   * @param from
   * @param to
   * @param subject
   * @param content
   */
  public SmtpSender(Date date, EmailAddress from, EmailAddress to,
    String subject, String content) {
    this.from = from;
    this.to = to;
    Random random = new Random(System.currentTimeMillis());
    this.dataSet = new ArrayList<String>();
    this.dataSet.add("X-RecievedDate: " + date);
    this.dataSet.add("Message-ID: " + "<" + random.nextInt(Integer.MAX_VALUE)
      + "." + random.nextInt(Integer.MAX_VALUE) + ".mail>");
    this.dataSet.add("Date: " + date);
    this.dataSet.add("From: " + from.toString());
    this.dataSet.add("To: " + to.toString());
    this.dataSet.add("Subject: " + subject);
    this.dataSet.add("Content-Type: " + "text/html; charset=utf-8");
    this.dataSet.add("");
    this.dataSet.add(content);
  }

  /**
   * Handles delivery of messages to addresses not handled by this server.
   *
   * @throws IOException
   */
  public void send() throws IOException {
    Socket socket = connect(this.to);
    try {
      socket.setSoTimeout(60 * 1000);
      this.out = new PrintWriter(socket.getOutputStream(), true);
      this.in = new BufferedReader(new InputStreamReader(socket
        .getInputStream()));
      this.sendIntro();
      this.sendData();
      this.sendClose();
    }
    finally {
      socket.close();
    }
  }

  // ***************************************************************
  // Private Interface
  // ***************************************************************

  /**
   * Determines the MX entries for this domain and attempts to open a socket. If
   * no connections can be opened, a SystemException is thrown.
   *
   * @param address
   * @return socket
   * @throws IOException
   */
  private Socket connect(EmailAddress address) throws IOException {
    Record[] records = new Lookup(address.getDomain(), Type.MX).run();
    if (records == null) {
      throw new IOException("Could not lookup to any SMTP server for domain: "
        + address.getDomain());
    }
    String[] mxEntries = new String[records.length];
    int priority = 0;
    int nextPriority = Integer.MAX_VALUE;
    int mxIndex = 0;
    while (mxIndex < mxEntries.length) {
      for (int i = 0; i < records.length; i++) {
        MXRecord mx = (MXRecord) records[i];
        if (mx.getPriority() == priority) {
          mxEntries[mxIndex++] = mx.getTarget().toString();
          if (mxIndex >= mxEntries.length) {
            break;
          }
        }
        else if (mx.getPriority() < nextPriority && mx
          .getPriority() > priority) {
          nextPriority = mx.getPriority();
        }
      }
      priority = nextPriority;
      nextPriority = Short.MAX_VALUE;
    }
    for (int index = 0; index < mxEntries.length; index++) {
      String mxEntry = mxEntries[index];
      int port = 25;
      int indexPort = mxEntry.indexOf(":");
      if (indexPort >= 0) {
        try {
          port = Integer.parseInt(mxEntry.substring(indexPort + 1));
        }
        catch (Exception e) {
        }
        if (indexPort == 0) {
          mxEntry = "localhost";
          mxEntries[index] = mxEntry + mxEntries[index];
        }
        else {
          mxEntry = mxEntry.substring(0, indexPort);
        }
      }
      try {
        return new Socket(mxEntry, port);
      }
      catch (Exception e) {
      }
    }
    throw new IOException("Could not connect to any SMTP server for domain: "
      + address.getDomain());
  }

  /**
   * This method sends all the commands neccessary to prepare the remote server
   * to recieve the data command.
   *
   * @throws IOException
   */
  private void sendIntro() throws IOException {
    String lastCode = null;
    if (!(lastCode = read()).startsWith("220")) {
      throw new RuntimeException("Error talking to remote Server, code="
        + lastCode);
    }
    write("EHLO " + LOCAL_DOMAIN);
    if (!(lastCode = read()).startsWith("250")) {
      write("HELO " + LOCAL_DOMAIN);
      if (!(lastCode = read()).startsWith("250")) {
        throw new RuntimeException("Error talking to remote Server, code="
          + lastCode);
      }
    }
    write("MAIL FROM:<" + this.from.getAddress() + ">");
    if (!(lastCode = read()).startsWith("250")) {
      throw new IOException("Error talking to remote Server, code=" + lastCode);
    }
    write("RCPT TO:<" + this.to.getAddress() + ">");
    if (!(lastCode = read()).startsWith("250")) {
      throw new IOException("Error talking to remote Server, code=" + lastCode);
    }
  }

  /**
   * This method sends the data command and all the message data to the remote
   * server.
   *
   * @throws IOException
   */
  private void sendData() throws IOException {
    write("DATA");
    if (!read().startsWith("354")) {
      throw new RuntimeException("Error talking to remote Server");
    }
    for (String line : this.dataSet) {
      write(line);
    }
    write(".");
    if (!read().startsWith("250")) {
      throw new RuntimeException("Error talking to remote Server");
    }
  }

  /**
   * Fecha o envio
   *
   * @throws IOException
   */
  private void sendClose() throws IOException {
    write("QUIT");
    if (!read().startsWith("221")) {
      throw new RuntimeException("Error talking to remote Server");
    }
  }

  /**
   * Returns the response code generated by the server. This method will handle
   * multi-line responses, but will only log the responses, and discard the
   * text, returning only the 3 digit response code.
   *
   * @return 3 digit response string.
   * @throws IOException
   */
  private String read() throws IOException {
    String inputText = in.readLine();
    if (inputText == null) {
      inputText = "";
    }
    else {
      inputText = inputText.trim();
    }
    if (inputText.length() < 3) {
      throw new IOException("SMTP Response too short. Aborting Send. Response: "
        + inputText);
    }
    String responseCode = inputText.substring(0, 3);
    while ((inputText.length() >= 4) && inputText.substring(3, 4).equals("-")) {
      inputText = in.readLine().trim();
    }
    return responseCode;
  }

  /**
   * Writes the specified output message to the client.
   *
   * @param message
   */
  private void write(String message) {
    out.print(message + "\r\n");
    out.flush();
  }

}