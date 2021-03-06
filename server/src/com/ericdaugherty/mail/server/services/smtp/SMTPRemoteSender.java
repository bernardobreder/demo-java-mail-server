/******************************************************************************
 * $Workfile: SMTPRemoteSender.java $ $Revision: 170 $ $Author: edaugherty $
 * $Date: 2008-01-19 16:39:16 -0600 (Sat, 19 Jan 2008) $
 *
 ******************************************************************************
 * This program is a 100% Java Email Server.
 ******************************************************************************
 * Copyright (C) 2001, Eric Daugherty All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 ******************************************************************************
 * For current versions and more information, please visit:
 * http://www.ericdaugherty.com/java/mail
 *
 * or contact the author at: java@ericdaugherty.com
 *
 ******************************************************************************
 * This program is based on the CSRMail project written by Calvin Smith.
 * http://crsemail.sourceforge.net/
 *****************************************************************************/

package com.ericdaugherty.mail.server.services.smtp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
// Java Imports
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

// Log imports
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// DNS imports
import org.xbill.DNS.Lookup;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import com.ericdaugherty.mail.server.configuration.ConfigurationManager;
import com.ericdaugherty.mail.server.configuration.DefaultSmtpServer;
import com.ericdaugherty.mail.server.errors.NotFoundException;
// Local Imports
import com.ericdaugherty.mail.server.info.EmailAddress;

/**
 * This class handles sending messages to external SMTP servers for delivery.
 *
 * @author Eric Daugherty
 */
public class SMTPRemoteSender {

  //***************************************************************
  // Variables
  //***************************************************************

  /** Logger */
  private static Log log = LogFactory.getLog(SMTPRemoteSender.class.getName());

  /** ConfigurationManager */
  private static ConfigurationManager configurationManager = ConfigurationManager.getInstance();

  /** Writer to sent data to the client */
  private PrintWriter out;
  /** Reader to read data from the client */
  private BufferedReader in;

  // Credentials for authentication with the default SMTP server
  private String username = "";
  private String password = "";

  //***************************************************************
  // Public Interface
  //***************************************************************

  //***************************************************************
  // Constructor(s)

  public SMTPRemoteSender() {

  }

  //***************************************************************
  // Methods

  /**
   * Handles delivery of messages to addresses not handled by this server.
   */
  public void sendMessage(EmailAddress address, SMTPMessage message) throws NotFoundException, RuntimeException {

    //Open the connection to the server.
    Socket socket = connect(address);

    // Set the timeout so reads do not hang forever.
    try {
      socket.setSoTimeout(60 * 1000);
    }
    catch (SocketException e) {
      throw new RuntimeException("Unable to set the Socket SO Timeout: " + e.getMessage());
    }

    try {
      try {
        //Get the input and output streams.
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        //Perform initial commands
        sendIntro(address, message);

        //Send message data
        sendData(message);

        //Close the connection.
        sendClose();
      }
      catch (IOException ioe) {
        throw new RuntimeException("IOException occured while talking to remote domain: " + address.getDomain());
      }
    }
    finally {
      if (socket != null) {
        try {
          socket.close();
        }
        catch (IOException ioe) {
          log.error("Error closing socket: " + ioe);
        }
      }
    }
  }

  //***************************************************************
  // Private Interface
  //***************************************************************

  /**
   * Determines the MX entries for this domain and attempts to open a socket. If
   * no connections can be opened, a SystemException is thrown.
   */
  private Socket connect(EmailAddress address) {

    Socket socket = null;

    String[] mxEntries = null;

    String domain = address.getDomain();

    //Check to see if a default smtp server is configured before performing
    //the DNS lookup.
    if (configurationManager.isDefaultSmtpServerEnabled()) {
      DefaultSmtpServer[] defaultMXEntries = configurationManager.getDefaultSmtpServers();
      for (int index = 0; index < defaultMXEntries.length; index++) {

        DefaultSmtpServer mxEntry = defaultMXEntries[index];
        try {
          socket = new Socket(mxEntry.getHost(), mxEntry.getPort());
          username = mxEntry.getUsername();
          password = mxEntry.getPassword();
          return socket;
        }
        catch (Exception e) {
          log.debug("Connection to SMTP Server: " + mxEntry + " failed with exception: " + e);
        }
      }
    }
    else {
      try {
        // Lookup the MX Entries

        // Doing a general lookup, clear DNS passwords.
        username = null;
        password = null;

        Record[] records = new Lookup(domain, Type.MX).run();
        if (records == null) {
          records = new Record[0];
          log.warn("DNS Lookup for domain: " + domain + " failed.");
        }

        // Convert the MX Entries to strings and sort them in order
        // of priority.
        mxEntries = new String[records.length];
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
            else if (mx.getPriority() < nextPriority && mx.getPriority() > priority) {
              nextPriority = mx.getPriority();
            }
          }
          priority = nextPriority;
          nextPriority = Integer.MAX_VALUE;
        }
      }
      catch (TextParseException e) {
        throw new RuntimeException("TextParseException while looking up domian MX Entry: " + e.getMessage());
      }

    }

    for (int index = 0; index < mxEntries.length; index++) {

      String mxEntry = mxEntries[index];
      int port = 25;

      // Extract the server and the port if the syntax server:port is used
      int indexPort = mxEntry.indexOf(":");
      if (indexPort >= 0) {
        try {
          port = Integer.parseInt(mxEntry.substring(indexPort + 1));
        }
        catch (Exception e) {
          System.out.println("Invalid defaultsmtpserver port: " + mxEntry.substring(indexPort + 1) + " - " + e);
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
        socket = new Socket(mxEntry, port);
        return socket;
      }
      catch (Exception e) {
        log.debug("Connection to SMTP Server: " + mxEntries[index] + " failed with exception: " + e);
      }
    }
    throw new RuntimeException("Could not connect to any SMTP server for domain: " + domain);
  }

  /**
   * This method sends all the commands neccessary to prepare the remote server
   * to recieve the data command.
   */
  private void sendIntro(EmailAddress address, SMTPMessage message) {

    //Check to make sure remote server introduced itself with appropriate message.
    String lastCode = null;
    read(220, "Error talking to remote Server");

    // First try ehlo
    write("EHLO " + configurationManager.getLocalDomains()[0]);
    if (!(lastCode = read()).startsWith("250")) {
      //Send HELO command to remote server.
      write("HELO " + configurationManager.getLocalDomains()[0]);
      if (!(lastCode = read()).startsWith("250")) {
        throw new RuntimeException("Error talking to remote Server, code=" + lastCode);
      }
    }
    else if (username != null) {
      // The EHLO was ok.
      write("AUTH LOGIN");
      if (!(lastCode = read()).startsWith("334")) {
        throw new RuntimeException("Error talking to remote Server, code=" + lastCode);
      }

      // Write the username.
      write(new String(Base64.encodeBase64(username.getBytes())));
      if (!(lastCode = read()).startsWith("334")) {
        throw new RuntimeException("Error talking to remote Server, code=" + lastCode);
      }

      // Write the password.
      write(new String(Base64.encodeBase64(password.getBytes())));
      if (!(lastCode = read()).startsWith("235")) {
        throw new RuntimeException("Error talking to remote Server, code=" + lastCode);
      }
    }

    //Send MAIL FROM: command
    write("MAIL FROM:<" + message.getFromAddress().getAddress() + ">");
    if (!(lastCode = read()).startsWith("250")) {
      throw new RuntimeException("Error talking to remote Server, code=" + lastCode);
    }

    //Send RCTP TO: command
    write("RCPT TO:<" + address.getAddress() + ">");
    if (!(lastCode = read()).startsWith("250")) {
      throw new RuntimeException("Error talking to remote Server, code=" + lastCode);
    }
  }

  /**
   * This method sends the data command and all the message data to the remote
   * server.
   */
  private void sendData(SMTPMessage message) {

    //Send Data command
    write("DATA");
    read(354, "Error talking to remote Server");

    //Get the data to write.
    List dataLines = message.getDataLines();
    int numDataLines = dataLines.size();

    //Write the data.
    for (int index = 0; index < numDataLines; index++) {
      write((String) dataLines.get(index));
    }

    //Send the command end data transmission.
    write(".");

    read(250, "Error talking to remote Server");
  }

  private void read(int expectedCode, String messageError) {
    String string = read();
    if (!string.startsWith(Integer.toString(expectedCode))) {
      throw new RuntimeException("[" + string + "] " + messageError);
    }
  }

  private void sendClose() {
    write("QUIT");
    read(221, "Error talking to remote Server");
  }

  /**
   * Returns the response code generated by the server. This method will handle
   * multi-line responses, but will only log the responses, and discard the
   * text, returning only the 3 digit response code.
   *
   * @return 3 digit response string.
   */
  private String read() {
    try {
      String responseCode;

      //Read in the first line.  This is the only line
      //we really care about, since the response code
      //must be the same on all lines.
      String inputText = in.readLine();
      if (inputText == null) {
        inputText = "";
      }
      else {
        inputText = inputText.trim();
      }

      if (log.isDebugEnabled()) {
        log.debug("Read Input: " + inputText);
      }
      if (inputText.length() < 3) {
        throw new RuntimeException("SMTP Response too short. Aborting Send. Response: " + inputText);
      }

      //Strip of the response code.
      responseCode = inputText.substring(0, 3);

      //Handle Multi-Line Responses.
      while ((inputText.length() >= 4) && inputText.substring(3, 4).equals("-")) {
        inputText = in.readLine().trim();
        if (log.isDebugEnabled()) {
          log.debug("Read Input: " + inputText);
        }
      }

      return responseCode;
    }
    catch (IOException ioe) {
      log.error("Error reading from socket.", ioe);
      throw new RuntimeException();
    }
  }

  /**
   * Writes the specified output message to the client.
   */
  private void write(String message) {
    out.print(message + "\r\n");
    out.flush();
  }

}
//EOF