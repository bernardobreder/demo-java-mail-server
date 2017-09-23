package breder.generic.email.gui.sending;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import breder.email.MailContent;
import breder.email.MailInfo;
import breder.email.gmail.GMailBox;
import breder.generic.email.gui.util.FileUtil;
import breder.generic.email.gui.util.ImageUtil;
import breder.generic.email.gui.util.InputStreamUtil;
import breder.generic.email.gui.util.RemoteTask;

public class SendingTask extends RemoteTask {

  private final JFrame parent;
  private final String username;
  private final char[] password;
  private final String to;
  private final String subject;
  private final File[] files;
  private final int index;
  private final String message;

  public static final long ATTACH_SIZE = 10 * 1024 * 1024;

  public SendingTask(JFrame parent, String username, char[] password,
    String to, String subject, File[] files, int index, String message) {
    this.parent = parent;
    this.username = username;
    this.password = password;
    this.to = to;
    this.subject = subject;
    this.files = files;
    this.index = index;
    this.message = message;
  }

  private static String[] fixTo(String tos) {
    tos = tos.toLowerCase();
    List<String> list = new ArrayList<String>();
    StringBuilder sb = new StringBuilder(tos);
    int index = tos.indexOf('@');
    while (index >= 0) {
      int begin = index - 1;
      int end = index + 1;
      while (begin > 0) {
        char charAt = sb.charAt(begin);
        if (isEmailLetter(charAt)) {
          begin--;
        }
        else {
          break;
        }
      }
      while (end < sb.length()) {
        char charAt = sb.charAt(end);
        if (isEmailLetter(charAt)) {
          end++;
        }
        else {
          break;
        }
      }
      list.add(sb.substring(begin + 1, end));
      sb.delete(begin + 1, end);
      index = sb.indexOf("@");
    }
    return list.toArray(new String[0]);
  }

  private static boolean isEmailLetter(char charAt) {
    return Character.isLetter(charAt) || Character.isDigit(charAt)
      || charAt == '_' || charAt == '.' || charAt == '$' || charAt == '-';
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean check() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void updateUI() {
    JOptionPane.showMessageDialog(this.parent, "Mensagem enviada com Sucesso");
    this.parent.dispose();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void perform() throws Exception {
    List<List<File>> list = this.buildFiles(index == 0);
    for (int n = 0; n < list.size(); n++) {
      GMailBox box = new GMailBox(username, password);
      List<File> files = list.get(n);
      MailInfo info = new MailInfo(username, subject);
      for (String email : fixTo(to)) {
        info.addTo(email);
      }
      MailContent content = new MailContent(info, message);
      for (File file : files) {
        content.add(file);
      }
      if (this.isStoped()) {
        return;
      }
      box.send(info, content);
      this.addProgress(100 / list.size());
    }
  }

  private BufferedImage isImage(File file) {
    try {
      return ImageIO.read(file);
    }
    catch (Exception e) {
    }
    return null;
  }

  private List<List<File>> buildFiles(boolean compress) {
    List<File> allFiles = new ArrayList<File>();
    for (File file : this.files) {
      if (file.isDirectory()) {
        allFiles.addAll(Arrays.asList(FileUtil.list(file, null)));
      }
      else {
        allFiles.add(file);
      }
    }
    List<List<File>> list = new ArrayList<List<File>>();
    long size = 0;
    List<File> fs = null;
    for (int n = 0; n < allFiles.size(); n++) {
      File file = allFiles.get(n);
      if (compress) {
        try {
          BufferedImage image = this.isImage(file);
          if (image != null) {
            String ext = file.getName().substring(file.getName().length() - 3);
            File tempFile =
              File.createTempFile(file.getName() + "_", "." + ext, file
                .getParentFile());
            InputStream input;
            if (image.getWidth() > image.getHeight()) {
              input = ImageUtil.scaleImage(image, 640, 480);
            }
            else {
              input = ImageUtil.scaleImage(image, 480, 640);
            }
            FileOutputStream output = new FileOutputStream(tempFile);
            InputStreamUtil.copyStream(input, output);
            output.close();
            input.close();
            tempFile.deleteOnExit();
            file = tempFile;
            System.gc();
          }
        }
        catch (Exception e) {
          e.printStackTrace();
        }
      }
      if (file.length() > ATTACH_SIZE) {
        continue;
      }
      if (fs == null || (index > 0 && fs.size() >= index)
        || size + file.length() > ATTACH_SIZE) {
        list.add(fs = new ArrayList<File>());
        size = 0;
      }
      size += file.length();
      fs.add(file);
    }
    return list;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void handler(Throwable e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(this.parent,
      "Algum erro desconhecido ocorreu no envio. "
        + "A operação foi cancelada.");
    this.parent.dispose();
  }

  @Override
  public void stoped() {
    this.parent.dispose();
  }

  public static void main(String[] args) {
    System.out
      .println(Arrays
        .toString(fixTo("<ananda_muller@yahoo.com.br>, 'Nayro Alencar <' <nayro@vm.uff.br>, 'Angelita' <angelitaguedes@gmail.com>, 'Afonso Júnior Vellasco F. da Cruz' <afonso.vellasco@estaleiromaua.ind.br>, 'Alberto Mansur Ghetti' <albertoghetti@hotmail.com>, 'Alexandre Figueredo' <alex.c.figueiredo@gmail.com>, 'Aline Vieira' <vetvieira2004@hotmail.com>, 'Daniel B. Macieira' <danielmacieira@gmail.com>, 'Bernardo' <bernardobreder@gmail.com>, 'Barbara Bianca' <babinpereira@gmail.com>, 'Barbara Bianca monitora' <barbarabnp@uol.com.br>, 'Bethânia Bastos' <bfbastos@yahoo.com>, 'Bárbara Neil' <barbaraneil@hotmail.com>, 'Eduardo Borges' <viana.eb@gmail.com>, 'Camila Machado' <camilascm@yahoo.com.br>, 'Camila Longa' <mila.longa@gmail.com>, 'Carol Dill' <caroldill.vet@gmail.com>, 'Carolina Siqueira monitlora' <cf.siqueira@uol.com.br>, 'Celso Fasura' <celsofasura@gmail.com>, 'clarisse donath' <clarissedonath@yahoo.com.br>, 'Cris' <crishmoreira@gmail.com>, 'Daniela Prado' <reikiprado@yahoo.com.br>, 'Daniela Sousa' <risadani@hotmail.com>, 'Elida Lab' <elidinha_oi@hotmail.com>, 'Eloisa Holanda' <eloisaholanda@gmail.com>, 'Eduardo Platais' <eduardoplatais@gmail.com>, 'Fabio Monteiro' <fmontbarros@gmail.com>, 'Fabíola Rezende Lab UFF' <fabiolarezendelima@hotmail.com>, 'FLAVIO FERNANDO BATISTA MOUTINHO' <flavio_moutinho@yahoo.com.br>, 'Gabriel Bobany de Queiroz' <bobany@gmail.com>, 'Graziele' <graziele.vet@gmail.com>, 'Gabriel Martins' <gmartins@vm.uff.br>, 'Gabriela Freitas UFF' <gabiba_freitas@hotmail.com>, 'Gabriela Oliveira e Silva' <gabi_veterinaria@yahoo.com.br>, 'Gabriela Vieira' <bi_cravocanela@yahoo.com.br>, 'Gil Vicente O. da Silva' <silvagvo@gmail.com>, 'Gracy Canto Gomes' <gracycg@globo.com>, 'Gracy Canto Gomes Marcello' <gcgmarcello@gmail.com>, 'Isabela Poubel' <isapoubel@gmail.com>, 'Ilma' <ilmavetuff@gmail.com>, 'Isabela Berlandez' <isabelaberlandez@gmail.com>, 'juliana kühner' <jskuhner@yahoo.com>, 'Janaina' <jana.rib@ig.com.br>, 'Joana Valle' <joana@solutionaweb.com.br>, 'Julia SOZED' <juliasozed@gmail.com>, 'Júlia Dourado' <juba_uvinha@hotmail.com>, 'Júlia Dourado' <juliasoccio@hotmail.com>, 'Julia Ramalho' <ramalho.julia@gmail.com>, 'Lili Munay' <liviakindlovits@gmail.com>, 'Leticia Del-Penho Sinedino P.' <letdelpenho@yahoo.com.br>, 'Laboratório Clínico Veterinário UM' <turmasextoperiodo@gmail.com>, 'Laís Grego' <laisgrego@yahoo.com.br>, 'lcerro' <lcerro@gmail.com>, 'Leticia Costa' <levetuff@gmail.com>, 'Leticia UFF' <lelekauff@hotmail.com>, 'Liza Gershony' <liza_gershony@yahoo.com>, 'Márcia de Souza Xavier' <marciasouzaxavier@gmail.com>, 'Namir Moreira' <vetnamir@hotmail.com>, 'Denis Otaka' <otaka@veterinario.med.br>, 'Oswaldo Maciel' <oswaldolcm@gmail.com>, 'pamela valente' <pamppam6@hotmail.com>, 'Pedro Velho' <pbvelho@yahoo.com.br>, 'ronaldo rafael' <ronaldo.roliveira@gmail.com>, 'Rafaela Guedes' <rafaelarezendeguedes@gmail.com>, 'Maíra Cardoso' <mairadc@gmail.com>, 'Raphael Azevedo' <raphael.azevedo@globo.com>, 'Renan' <renanmedvet@gmail.com>, 'Shanna' <s.destri@gmail.com>, 'Suzana Lab' <suvetleite@gmail.com>, 'Tatiana Didonet' <tatididonet@yahoo.com.br>, 'Tandara Machado' <tandara_machado@yahoo.com.br>, 'Tassia de Vasconcelos' <tassia.vasconcelos@gmail.com>, 'Thais Brasil Vieira' <brasil.tha@ig.com.br>, 'Táya Oliveira' <tayafo@hotmail.com>, 'Thais' <thaisbrasil@oi.com.br>, <uffvet@hotmail.com>, 'Vanessa Viscardi mestrado' <vanessaviscardi@yahoo.com.br")));
  }

}
