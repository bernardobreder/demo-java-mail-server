package breder.generic.email.gui.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import breder.dtl.swing.test.DTLFrame;
import breder.generic.email.gui.logining.LoginingFrame;

public class LoginFrame extends DTLFrame {

  public LoginFrame() {
    this.getLoginButton().addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        onLoginAction();
      }
    });
  }

  protected void onLoginAction() {
    String username = this.getUsernameField().getText();
    char[] password = this.getPasswordField().getPassword();
    new LoginingFrame(username, password).setVisible(true);
    this.dispose();
  }

  public JButton getLoginButton() {
    return this.get("bt_login");
  }

  public JTextField getUsernameField() {
    return this.get("tf_username");
  }

  public JPasswordField getPasswordField() {
    return this.get("tf_password");
  }

  public static void main(String[] args) throws IOException, ParseException,
    InterruptedException {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        LoginFrame frame = new LoginFrame();
        frame.setVisible(true);
      }
    });
  }

}
