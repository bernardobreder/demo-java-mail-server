import javax.swing.SwingUtilities;

import breder.generic.email.gui.login.LoginFrame;
import breder.generic.email.gui.util.LookAndFeel;

public class Main {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        LookAndFeel.getInstance().installSeaglass();
        new LoginFrame().setVisible(true);
      }
    });
  }
}
