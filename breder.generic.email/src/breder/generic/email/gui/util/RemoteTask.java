package breder.generic.email.gui.util;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public abstract class RemoteTask {

  private boolean stoped;

  private int progress;

  private JProgressBar progressBar;

  public RemoteTask start() {
    new Thread(this.getClass().getSimpleName()) {
      @Override
      public void run() {
        try {
          final Boolean[] array = new Boolean[1];
          SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
              array[0] = check();
            }
          });
          if (array[0]) {
            perform();
            addProgress(100);
            if (!isStoped()) {
              SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  updateUI();
                }
              });
            }
          }
        }
        catch (Throwable e) {
          if (!isStoped()) {
            handler(e);
          }
        }
      }
    }.start();
    return this;
  }

  public abstract boolean check();

  public abstract void updateUI();

  public abstract void perform() throws Exception;

  public abstract void stoped();

  public abstract void handler(Throwable e);

  public void stop() {
    this.stoped = true;
    this.stoped();
  }

  /**
   * Retorna
   * 
   * @return stoped
   */
  public boolean isStoped() {
    return stoped;
  }

  /**
   * Adiciona um progresso
   * 
   * @param add
   */
  public void addProgress(int add) {
    this.progress = Math.min(100, this.progress + add);
    if (this.progressBar != null) {
      this.progressBar.setValue(progress);
    }
  }

  /**
   * @param progressBar
   * @return owner
   */
  public RemoteTask setProgressBar(JProgressBar progressBar) {
    this.progressBar = progressBar;
    this.progressBar.setMaximum(100);
    return this;
  }

}
