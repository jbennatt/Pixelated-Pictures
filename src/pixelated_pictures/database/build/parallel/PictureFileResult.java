package pixelated_pictures.database.build.parallel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PictureFileResult {
	public static PictureFileResult NULL_FILE = new PictureFileResult();

	final File file;
	final BufferedImage image;

	private PictureFileResult() {
		file = null;
		image = null;
	}

	public PictureFileResult(final File pictureFile) throws IOException {
		this.file = pictureFile;
		this.image = ImageIO.read(pictureFile);
	}

	public BufferedImage getImage() {
		return image;
	}

	public File getFile() {
		return file;
	}
}
