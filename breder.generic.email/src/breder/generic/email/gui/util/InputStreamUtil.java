package breder.generic.email.gui.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class InputStreamUtil {

	public static byte[] getBytes(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		InputStreamUtil.copyStream(input, output);
		return output.toByteArray();
	}

	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {
		byte[] bytes = new byte[1024];
		while (true) {
			int n = input.read(bytes);
			if (n == -1)
				break;
			output.write(bytes, 0, n);
		}
	}

	public static void extractZip(ZipInputStream input, File dir)
			throws IOException {
		if (dir.exists() && !dir.isDirectory()) {
			throw new IllegalArgumentException("dir is not a folder : "
					+ dir.getAbsolutePath());
		}
		for (;;) {
			ZipEntry entity = input.getNextEntry();
			if (entity == null) {
				break;
			}
			File file = new File(dir, entity.getName());
			if (entity.isDirectory()) {
				file.mkdirs();
			} else {
				FileOutputStream output = new FileOutputStream(file);
				InputStreamUtil.copyStream(input, output);
				output.close();
			}
			input.closeEntry();
		}
	}

	public static InputStream getResource(String resource) {
		return InputStreamUtil.class.getClassLoader().getResourceAsStream(
				resource);
	}

}
