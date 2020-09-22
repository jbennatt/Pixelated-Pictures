package pixelated_pictures.database;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

public class PictureDatum {
	public final File src;
	public final File thumb;
	final int[][] red, green, blue;

	public PictureDatum(final File src, final File thumb, final int[][] red,
			final int[][] green, final int[][] blue) {
		this.src = src;
		this.thumb = thumb;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	PictureDatum(final File src, final File thumb,
			final BufferedImage pixelImage, final int dataDim) {

		this.src = src;
		this.thumb = thumb;

		this.red = new int[dataDim][dataDim];
		this.green = new int[dataDim][dataDim];
		this.blue = new int[dataDim][dataDim];

		float r = 0, g = 0, b = 0;

		for (int i = 0; i < red.length; ++i) {
			for (int j = 0; j < red[0].length; ++j) {
				final Color pixel = new Color(pixelImage.getRGB(i, j));
				r += red[i][j] = pixel.getRed();
				g += green[i][j] = pixel.getGreen();
				b += blue[i][j] = pixel.getBlue();
			}
		}
	}

	public int getRed(final int i, final int j) {
		return red[i][j];
	}

	public int getGreen(final int i, final int j) {
		return green[i][j];
	}

	public int getBlue(final int i, final int j) {
		return blue[i][j];
	}
}
