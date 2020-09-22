package pixelated_pictures.database;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * @author Jared F. Bennatt
 *
 */
public interface PictureDB extends Iterable<PictureDatum> {

	public void find(final BufferedImage src, final int i, final int j,
			final int amount, final TreeSet<PictureResult> results);

	public void addPicture(final PictureDatum datum);

	/**
	 * Size of each picture in data base (each picture is a square: pixels X
	 * pixles)
	 * 
	 * @return
	 */
	public int pixels();

	/**
	 * This is the number of pictures in the database
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Dimensions of "fine analysis". Each picture pixel is broken into a square
	 * sub-array of pixels. This is then used in finding the best picture for
	 * each spot.
	 * 
	 * @return
	 */
	public int dataDim();

	public static float dist(final PictureDatum pd, final BufferedImage origin,
			final int x0, final int y0) {
		float dist = 0;

		for (int i = 0; i < pd.red.length; ++i) {
			for (int j = 0; j < pd.red[0].length; ++j) {
				final Color original = new Color(origin.getRGB(x0 + i, y0 + j));
				dist += PictureDB.dist(original, pd.red[i][j], pd.green[i][j],
						pd.blue[i][j]);
			}
		}

		return dist;
	}

	public static float dist(final float r1, final float g1, final float b1,
			final float r2, final float g2, final float b2) {
		return sqr(r1 - r2) + sqr(g1 - g2) + sqr(b1 - b2);
	}

	public static float dist(final Color color, final float r, final float g,
			final float b) {
		return sqr(color.getRed() - r) + sqr(color.getGreen() - g)
				+ sqr(color.getBlue() - b);
	}

	public static float dist(final float r1, final float g1, final float b1,
			final Color color) {
		return sqr(r1 - color.getRed()) + sqr(g1 - color.getGreen())
				+ sqr(b1 - color.getBlue());
	}

	// public static float dist(final Color color, final PictureDatum pd) {
	// return PictureDB.dist(pd.rav, pd.gav, pd.bav, color);
	// }

	public static float dist(final Color c1, final Color c2) {
		return sqr(c1.getRed() - c2.getRed())
				+ sqr(c1.getGreen() - c2.getGreen())
				+ sqr(c1.getBlue() - c2.getBlue());
	}

	public static float sqr(final float x) {
		return x * x;
	}
}
