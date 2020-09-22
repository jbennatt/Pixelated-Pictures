package pixelated_pictures.processor;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.ResampleOp;

import pixelated_pictures.database.PictureDB;
import pixelated_pictures.database.PictureResult;

public class PixelatePictureBrute {
	public static BufferedImage pixelatePicture(final BufferedImage image,
			PictureDB db, final int numResults) throws IOException {
		final int pixels = db.pixels();
		final int dataDim = db.dataDim();
		final int pixelWidth = (int) Math.ceil(image.getWidth() / pixels);
		final int pixelHeight = (int) Math.ceil(image.getHeight() / pixels);

		final BufferedImage result = new BufferedImage(pixels * pixelWidth,
				pixels * pixelHeight, BufferedImage.TYPE_INT_ARGB);

		final ResampleOp pixelateInputResOp = new ResampleOp(
				pixelWidth * dataDim, pixelHeight * dataDim);

		final BufferedImage pixelatedInput = pixelateInputResOp.filter(image,
				null);

		final Graphics g = result.getGraphics();
		for (int i = 0; i < pixelWidth; ++i)
			for (int j = 0; j < pixelHeight; ++j)
				drawPixelPicture(i, j, pixelatedInput, db, g, numResults);

		g.dispose();

		return result;
	}

	private static TreeSet<PictureResult> results = new TreeSet<>();

	public static void drawPixelPicture(final int i, final int j,
			final BufferedImage src, final PictureDB db, final Graphics dstG,
			final int numResults) throws IOException {
		final int pixels = db.pixels();
		// final PictureDatum datum = db.find(pixelColor);
		db.find(src, i * db.dataDim(), j * db.dataDim(), numResults, results);
		randomPoll(numResults);
		final Image pixelImage = ImageIO.read(results.pollFirst().datum.thumb);
		dstG.drawImage(pixelImage, i * pixels, j * pixels, null);
	}

	private static void randomPoll(final int n) {
		final int polls = (int) Math.ceil(Math.random() * n);

		for (int i = 0; i < polls && results.size() > 1; ++i)
			results.pollFirst();
	}
}
