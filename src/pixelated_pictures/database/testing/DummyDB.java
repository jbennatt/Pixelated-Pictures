package pixelated_pictures.database.testing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;

import pixelated_pictures.database.PictureDB;
import pixelated_pictures.database.PictureDatum;
import pixelated_pictures.database.PictureResult;

public class DummyDB implements PictureDB {

	public static final int DEF_PIXELS = 50;
	public static final int DEF_DATA_DIM = 3;

	public final int pixels, dataDim;
	private final ArrayList<PictureDatum> db = new ArrayList<>();

	public DummyDB() {
		this(DEF_PIXELS, DEF_DATA_DIM);
	}

	public DummyDB(final int pixels, final int dataDim) {
		this.pixels = pixels;
		this.dataDim = dataDim;
	}

	@Override
	public Iterator<PictureDatum> iterator() {
		return db.iterator();
	}

	@Override
	public void find(BufferedImage src, int x0, int y0, int amount,
			TreeSet<PictureResult> results) {
		results.clear();
		float supremum = Float.NEGATIVE_INFINITY;

		for (int i = 0; i < amount && i < db.size(); ++i) {
			final PictureResult newResult = new PictureResult(db.get(i), src,
					x0, y0);
			if (newResult.dist > supremum)
				supremum = newResult.dist;

			results.add(newResult);
		}

		for (int i = amount; i < db.size(); ++i) {
			final PictureResult newResult = new PictureResult(db.get(i), src,
					x0, y0);
			if (newResult.dist < supremum) {
				// need to remove last, add, this new result, and then get new
				// supremum (from new last)

				results.pollLast();// just throw away last element
				results.add(newResult);
				supremum = results.last().dist;
			}
		}

	}

	public static PictureDB readDB(final File dbFile)
			throws FileNotFoundException {
		final Scanner scanner = new Scanner(dbFile);

		String line = scanner.nextLine();
		Scanner lineScanner = new Scanner(line);

		final int pixels = lineScanner.nextInt();
		final int dataDim = lineScanner.nextInt();
		final int size = lineScanner.nextInt();

		lineScanner.close();

		final PictureDB db = new DummyDB(pixels, dataDim);

		for (int i = 0; i < size; ++i) {
			final File src = new File(scanner.nextLine().trim());
			final File thumb = new File(scanner.nextLine().trim());

			final int[][] red = new int[dataDim][dataDim];
			final int[][] green = new int[dataDim][dataDim];
			final int[][] blue = new int[dataDim][dataDim];
			for (int x = 0; x < dataDim; ++x)
				for (int y = 0; y < dataDim; ++y) {
					line = scanner.nextLine();
					lineScanner = new Scanner(line);
					red[x][y] = lineScanner.nextInt();
					green[x][y] = lineScanner.nextInt();
					blue[x][y] = lineScanner.nextInt();
					lineScanner.close();
				}

			db.addPicture(new PictureDatum(src, thumb, red, green, blue));
		}

		scanner.close();

		return db;
	}

	public void writeDBInfo(final File dbFile) throws IOException {
		final PrintWriter pw = new PrintWriter(new FileWriter(dbFile));

		pw.println(pixels + " " + dataDim + " " + db.size());

		for (final PictureDatum datum : db) {
			pw.println(datum.src.getAbsolutePath());
			pw.println(datum.thumb.getAbsolutePath());
			for (int i = 0; i < dataDim; ++i) {
				for (int j = 0; j < dataDim; ++j) {
					pw.println(datum.getRed(i, j) + " " + datum.getGreen(i, j)
							+ " " + datum.getBlue(i, j));
				}
			}
		}

		pw.close();
	}

	@Override
	public int dataDim() {
		return this.dataDim;
	}

	@Override
	public int pixels() {
		return pixels;
	}

	@Override
	public int size() {
		return db.size();
	}

	@Override
	public void addPicture(PictureDatum datum) {
		db.add(datum);
	}

}
