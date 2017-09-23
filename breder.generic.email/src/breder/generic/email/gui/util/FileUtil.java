package breder.generic.email.gui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class FileUtil {

  public static File[] list(File dir, String ext) {
    List<File> list = new ArrayList<File>();
    list(dir, ext, list);
    return list.toArray(new File[list.size()]);
  }

  private static void list(File dir, String ext, List<File> list) {
    File[] files = dir.listFiles();
    if (files != null) {
      for (File file : files) {
        if (!file.isHidden()) {
          if (file.isDirectory()) {
            list(file, ext, list);
          }
          else if (file.isFile()) {
            if (ext == null
              || (ext != null && file.getName().endsWith("." + ext))) {
              list.add(file);
            }
          }
        }
      }
    }
  }

}
