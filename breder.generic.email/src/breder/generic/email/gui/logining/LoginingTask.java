package breder.generic.email.gui.logining;

import javax.swing.JFrame;

import breder.email.gmail.GMailBox;
import breder.generic.email.gui.login.LoginFrame;
import breder.generic.email.gui.main.MainFrame;
import breder.generic.email.gui.util.RemoteTask;

public class LoginingTask extends RemoteTask {

  private String username;

  private char[] password;

  private boolean logined = false;

  private JFrame parent;

  public LoginingTask(JFrame parent, String username, char[] password) {
    this.parent = parent;
    this.username = username;
    this.password = password;
  }

  @Override
  public boolean check() {
    return true;
  }

  @Override
  public void updateUI() {
    new MainFrame(username, password).setVisible(true);
    this.parent.dispose();
  }

  @Override
  public void perform() throws Exception {
    new GMailBox(username, password);
    this.logined = true;
  }

  @Override
  public void handler(Throwable e) {
    e.printStackTrace();
    LoginFrame frame = new LoginFrame();
    frame.getUsernameField().setText(username);
    frame.setVisible(true);
    this.parent.dispose();
  }

  @Override
  public void stoped() {
    this.handler(new InterruptedException());
  }

}
