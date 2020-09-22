package pixelated_pictures.database;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.ResampleOp;

import pixelated_pictures.database.build.parallel.PictureFileResult;

public class PictureProcessor {
	public static final int SIZE = 50;

	public final int dataDim;
	public final int pixels;

	public PictureProcessor(final PictureDB db) {
		this.pixels = db.pixels();
		this.dataDim = db.dataDim();
	}

	public PictureDatum processImage(final String inputPath,
			final String dbPath) throws IOException {
		return this.processImage(new File(inputPath), new File(dbPath));
	}

	public PictureDatum processImage(final PictureFileResult result,
			final File dbFile) throws IOException {
		return this.processImage(result.getImage(), result.getFile(), dbFile);
	}

	public PictureDatum processImage(final BufferedImage inputImage,
			final File inputFile, final File dbFile) throws IOException {
		BufferedImage croppedImage;
		ResampleOp resOp = new ResampleOp(pixels, pixels);

		BufferedImage processedImage;

		int square;
		int xOrigin = 0;
		int yOrigin = 0;
		final int width = inputImage.getWidth();
		final int height = inputImage.getHeight();

		if (width < height) {
			/*
			 * If the width is smaller, then you will use it to make a square
			 * and the entire width will be used (meaning that the xOrigin = 0)
			 * 
			 * Center along the height...there should be equal lengths on top
			 * and bottom to fill in the gap from (height - width)...take half
			 * to get the amount to go from top left corner
			 * 
			 * 
			 * ================ | | | clipped | | | |--------------| <-- yOrigin
			 * | | | viewing | | area | | | |--------------| | | | clipped | | |
			 * ================
			 */
			square = width;
			yOrigin = (height - width) / 2;
		} else {
			/*
			 * opposite of above...now the full height will be used, so the
			 * yOrigin = 0 and xOrigin is half of the difference between the
			 * height and width because now there are bars clipped off of the
			 * left and right sides
			 * 
			 * ================================= | | | | | c | | c | | l | | l |
			 * | i | | i | | p | viewing area | p | | p | | p | | e | | e | | d
			 * | | d | | | | | ================================= ^ | | xOrigin
			 */
			square = height;
			xOrigin = (width - height) / 2;
		}

		// note the total number of pixels involved in this average
		// final int total = square * square;
		//
		// final int stopX = xOrigin + square;
		// final int stopY = yOrigin + square;

		croppedImage = new BufferedImage(square, square,
				BufferedImage.TYPE_INT_RGB);

		// // iterate through all of the pixels in the viewing area
		// for (int i = xOrigin; i < stopX; ++i) {
		// for (int j = yOrigin; j < stopY; ++j) {
		// // get this pixel's color
		//// Color thisColor = new Color(inputImage.getRGB(i, j));
		//
		// // add to the running total
		// // red += thisColor.getRed();
		// // green += thisColor.getGreen();
		// // blue += thisColor.getBlue();
		// }
		// }

		// take the averages
		// red /= total;
		// green /= total;
		// blue /= total;

		// get the graphics object for the image you are going to write
		Graphics g = croppedImage.getGraphics();

		// draw the image onto processedImage, resizing and cropping from the
		// viewing area
		g.drawImage(inputImage, 0, 0, square, square, xOrigin, yOrigin,
				xOrigin + square, yOrigin + square, null);

		// dispose of all graphics objects created (since there will likely be A
		// LOT)
		g.dispose();

		// use java-image-scaling library to resize cropped image onto processed
		// image (so it's now size-by-size).
		processedImage = resOp.filter(croppedImage, null);

		ImageIO.write(processedImage, "PNG", dbFile);

		final int pixelWidth = this.dataDim < MIN_WIDTH ? MIN_WIDTH
				: this.dataDim;
		ResampleOp pixelResOp = new ResampleOp(pixelWidth, pixelWidth);

		final BufferedImage pixelImage = pixelResOp.filter(processedImage,
				null);

		// add this picture info to the database
		// db.addPicture(dbFile.getAbsolutePath(), (int) red, (int) green,
		// (int) blue);

		return new PictureDatum(inputFile, dbFile, pixelImage, this.dataDim);

	}

	/**
	 * This is meant to be called the first (and hopefully only time), to
	 * analyze a picture then save it as a smaller, cropped picture that can be
	 * loaded quickly later.
	 * 
	 * @param inputFile
	 *            picture to be analyzed
	 * @param dbFile
	 *            destination of cropped image (original is NOT modified)
	 * @param db
	 *            database to put this information into
	 * @throws IOException
	 */
	public PictureDatum processImage(final File inputFile, final File dbFile)
			throws IOException {
		BufferedImage inputImage = ImageIO.read(inputFile);
		return this.processImage(inputImage, inputFile, dbFile);
	}

	public static final int MIN_WIDTH = 3;
}
