package breder.generic.email.gui.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * Utilitario para imagem
 * 
 * 
 * @author bbreder
 */
public class ImageUtil {

  /**
   * Redimensiona uma imagem
   * 
   * @param image
   * @param width
   * @param height
   * @return imagem redimensionada
   * @throws Exception
   */
  public static InputStream scaleImage(BufferedImage image, int width,
    int height) throws Exception {
    ImageIO.setUseCache(true);
    // Draw the scaled image
    BufferedImage thumbImage =
      new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2D = thumbImage.createGraphics();
    g2D.drawImage(image, 0, 0, width, height, null);
    // Write the scaled image to the outputstream
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
    JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(thumbImage);
    int quality = 100; // Use between 1 and 100, with 100 being highest quality
    quality = Math.max(0, Math.min(quality, 100));
    param.setQuality(quality / 100.0f, false);
    encoder.setJPEGEncodeParam(param);
    encoder.encode(thumbImage);
    ImageIO.write(thumbImage, "jpg", out);
    // Read the outputstream into the inputstream for the return value
    ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
    g2D.dispose();
    out.close();
    return bis;
  }

  /**
   * Redimensiona uma imagem
   * 
   * @param input
   * @param width
   * @param height
   * @return imagem redimensionada
   * @throws Exception
   */
  public static InputStream scaleImage(InputStream input, int width, int height)
    throws Exception {
    return scaleImage(ImageIO.read(input), width, height);
  }

}
