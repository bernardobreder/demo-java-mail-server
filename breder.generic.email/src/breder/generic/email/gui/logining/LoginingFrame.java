package breder.generic.email.gui.logining;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import breder.dtl.el.parse.ParseException;
import breder.dtl.swing.test.DTLFrame;
import breder.generic.email.gui.util.RemoteTask;

public class LoginingFrame extends DTLFrame {

  private RemoteTask task;

  public LoginingFrame(String username, char[] password) {
    this.task =
      new LoginingTask(this, username, password).setProgressBar(
        this.getProgressBar()).start();
    this.getCancelButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onCancelAction();
      }
    });
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

  public static void main(String[] args) throws IOException, ParseException,
    InterruptedException {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        LoginingFrame frame = new LoginingFrame(null, null);
        frame.setVisible(true);
      }
    });
  }

}
