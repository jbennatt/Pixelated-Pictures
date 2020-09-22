package pixelated_pictures.database.build;

import java.io.File;
import java.io.IOException;

import pixelated_pictures.database.PictureDB;
import pixelated_pictures.database.PictureDatum;
import pixelated_pictures.database.PictureProcessor;

public class DBProcessorSerial {

	public static void processFiles(final File rootDir, PictureDB db,
			final File dbDir) throws IOException {
		final PictureFileTraversal pft = new PictureFileTraversal();
		pft.traverseDirectory(rootDir);
		final PictureProcessor picProc = new PictureProcessor(db);

		File pic;
		int count = 1;

		while ((pic = pft.next()) != PictureFileTraversal.NULL_FILE) {
			final File dbFile = new File(
					dbDir.getAbsolutePath() + "\\pic" + count++ + ".png");
			System.err.println(dbFile.getAbsolutePath());
			dbFile.createNewFile();

			final PictureDatum datum = picProc.processImage(pic, dbFile);
			db.addPicture(datum);
			System.err.println((count - 1) + ": added " + pic);
		}

		pft.shutdown();
	}
}
