package breder.generic.email.gui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import breder.dtl.swing.test.DTLFrame;
import breder.generic.email.gui.login.LoginFrame;
import breder.generic.email.gui.sending.SendingFrame;

public class MainFrame extends DTLFrame {

  private final String username;
  private final char[] password;
  private File[] files = new File[0];

  public MainFrame(String username, char[] password) {
    this.username = username;
    this.password = password;
    this.setTitle(username);
    this.buildAction();
  }

  private void buildAction() {
    this.getSendButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onExecuteAction();
      }
    });
    this.getQuitButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        MainFrame.this.dispose();
      }
    });
    this.getLogoutButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onLogoutAction();
      }
    });
    this.getAttachButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onAttachAction();
      }
    });
  }

  protected void onLogoutAction() {
    new LoginFrame().setVisible(true);
    this.dispose();
  }

  protected void onAttachAction() {
    JFileChooser fc = new JFileChooser();
    fc.setMultiSelectionEnabled(true);
    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    int dialog = fc.showOpenDialog(this);
    if (dialog == JFileChooser.APPROVE_OPTION
      && fc.getSelectedFiles().length > 0) {
      this.files = fc.getSelectedFiles();
      String text = Arrays.toString(this.files);
      text = text.substring(1, text.length() - 1).trim();
      this.getAttachField().setText(text);
    }
  }

  protected void onExecuteAction() {
    String to = this.getToField().getText();
    String subject = this.getSubjectField().getText();
    int index = this.getAttachCombo().getSelectedIndex();
    String message = this.getMessageField().getText();
    new SendingFrame(this, username, password, to, subject, files, index,
      message).setVisible(true);
  }

  public JComboBox getAttachCombo() {
    return this.get("cb_attach");
  }

  public JTextField getAttachField() {
    return this.get("tf_attach");
  }

  public JTextField getToField() {
    return this.get("tf_to");
  }

  public JTextField getSubjectField() {
    return this.get("tf_subject");
  }

  public JTextArea getMessageField() {
    JScrollPane scroll = this.get("tf_message");
    return (JTextArea) scroll.getViewport().getView();
  }

  public JButton getSendButton() {
    return this.get("bt_exec");
  }

  public JButton getAttachButton() {
    return this.get("bt_attach");
  }

  public JButton getQuitButton() {
    return this.get("bt_logout");
  }

  public JButton getLogoutButton() {
    return this.get("bt_logout");
  }

  public static void main(String[] args) throws IOException, ParseException,
    InterruptedException {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        MainFrame frame = new MainFrame("test@test.com", "abc".toCharArray());
        frame.setVisible(true);
      }
    });
  }

}
