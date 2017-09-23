package breder.generic.email.gui.util;

import javax.swing.UIManager;

/**
 * Implementa��o default de lookandfeel
 * 
 * @author Bernardo Breder
 * 
 */
public class LookAndFeel {

  private static final LookAndFeel instance = new LookAndFeel();

  private LookAndFeel() {
  }

  /**
   * {@inheritDoc}
   */
  public void installNimbus() {
    this.installLookAndFell("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
  }

  /**
   * {@inheritDoc}
   */
  public void installNative() {
    this.installLookAndFell(UIManager.getSystemLookAndFeelClassName());
  }

  /**
   * {@inheritDoc}
   */
  public void installSeaglass() {
    this.installLookAndFell("com.seaglasslookandfeel.SeaGlassLookAndFeel");
  }

  /**
   * {@inheritDoc}
   */
  public void installMetal() {
    this.installLookAndFell("javax.swing.plaf.metal.MetalLookAndFeel");
  }

  private void installLookAndFell(String name) {
    try {
      UIManager.setLookAndFeel(name);
    }
    catch (Exception e) {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch (Exception e1) {
      }
    }
  }

  /**
   * Objeto singleton
   * 
   * @return
   */
  public static LookAndFeel getInstance() {
    return instance;
  }
}
