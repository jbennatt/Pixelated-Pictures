package pixelated_pictures.database;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class PictureResult implements Comparable<PictureResult> {
	private static int count = Integer.MIN_VALUE;

	public final PictureDatum datum;
	public final float dist;

	private final int id = count++;

	public PictureResult(final PictureDatum datum, final BufferedImage src,
			final int x0, final int y0) {
		this.datum = datum;
		this.dist = PictureDB.dist(datum, src, x0, y0);
	}

	// public PictureResult(final PictureDatum datum, final Color compare) {
	// this.datum = datum;
	// this.dist = PictureDB.dist(compare, datum);
	// }

	public PictureResult(final PictureDatum datum, final int dist) {
		this.datum = datum;
		this.dist = dist;
	}

	@Override
	public int compareTo(PictureResult c) {
		final int cmp = Float.compare(dist, c.dist);
		if (cmp == 0)
			return Integer.compare(id, c.id);
		// else just return distance comparison
		return cmp;
	}
}
