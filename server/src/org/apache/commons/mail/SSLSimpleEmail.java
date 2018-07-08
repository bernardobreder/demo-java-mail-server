package org.apache.commons.mail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLSimpleEmail {

  private final static int SMTP_PORT = 465;

  InetAddress mailHost;

  InetAddress localhost;

  BufferedReader in;

  PrintWriter out;

  public static void main(String[] args) throws UnknownHostException, IOException {
    new SSLSimpleEmail("smtp.gmail.com").send(new InputStreamReader(new ByteArrayInputStream("Teste de email"
      .getBytes())), "user@mydomain.com", "bernardobreder@gmail.com");
    SSLSocket socket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(InetAddress
      .getByName("smtp.gmail.com"), 465);
    socket.close();
  }

  public SSLSimpleEmail(String host) throws UnknownHostException {
    mailHost = InetAddress.getByName(host);
    localhost = InetAddress.getLocalHost();
  }

  public boolean send(Reader msgFileReader, String from, String to) throws IOException {
    SSLSocket smtpPipe = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(mailHost, 465);
    try {
      InputStream inn;
      OutputStream outt;
      BufferedReader msg;
      msg = new BufferedReader(msgFileReader);
      if (smtpPipe == null) {
        return false;
      }
      inn = smtpPipe.getInputStream();
      outt = smtpPipe.getOutputStream();
      in = new BufferedReader(new InputStreamReader(inn));
      out = new PrintWriter(new OutputStreamWriter(outt), true);
      if (inn == null || outt == null) {
        System.out.println("Failed to open streams to socket.");
        return false;
      }
      String initialID = in.readLine();
      System.out.println(initialID);
      System.out.println("HELO " + localhost.getHostName());
      out.println("HELO " + localhost.getHostName());
      String welcome = in.readLine();
      System.out.println(welcome);
      System.out.println("MAIL From:<" + from + ">");
      out.println("MAIL From:<" + from + ">");
      String senderOK = in.readLine();
      System.out.println(senderOK);
      System.out.println("RCPT TO:<" + to + ">");
      out.println("RCPT TO:<" + to + ">");
      String recipientOK = in.readLine();
      System.out.println(recipientOK);
      System.out.println("DATA");
      out.println("DATA");
      String line;
      while ((line = msg.readLine()) != null) {
        out.println(line);
      }
      System.out.println(".");
      out.println(".");
      String acceptedOK = in.readLine();
      System.out.println(acceptedOK);
      System.out.println("QUIT");
      out.println("QUIT");
      return true;
    }
    finally {
      smtpPipe.close();
    }
  }
}
