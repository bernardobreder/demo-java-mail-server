package breder.generic.email.gui.sending;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import breder.dtl.swing.test.DTLFrame;
import breder.generic.email.gui.util.RemoteTask;

public class SendingFrame extends DTLFrame {

  private final String username;
  private final char[] password;
  private RemoteTask task;

  public SendingFrame(JFrame parent, String username, char[] password,
    String to, String subject, File[] files, int index, String message) {
    this.username = username;
    this.password = password;
    this.task =
      new SendingTask(this, username, password, to, subject, files, index,
        message).setProgressBar(this.getProgressBar()).start();
    this.getCancelButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onCancelAction();
      }
    });
    this.getHideButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onHideAction();
      }
    });
  }

  protected void onHideAction() {
    this.dispose();
  }

  protected void onCancelAction() {
    this.task.stop();
  }

  public JButton getCancelButton() {
    return this.get("bt_cancel");
  }

  public JProgressBar getProgressBar() {
    return this.get("pb_title");
  }

  public JButton getHideButton() {
    return this.get("bt_hide");
  }

  public static void main(String[] args) throws IOException, ParseException,
    InterruptedException {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        SendingFrame frame =
          new SendingFrame(null, "test@test.com", "abc".toCharArray(),
            "test@test.com", "Enviando Arquivo", new File[] {}, 0, "Texto");
        frame.setVisible(true);
      }
    });
  }

}
