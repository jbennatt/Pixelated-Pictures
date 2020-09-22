package pixelated_pictures.database.testing;

import java.io.File;
import java.io.IOException;

import pixelated_pictures.database.PictureDB;
import pixelated_pictures.database.PictureDatum;
import pixelated_pictures.database.build.DBProcessorSerial;

public class DBSerialTester {
	static final String TEST_DIR = "db-test serial";
	// static final String TEST_ROOT = "G:\\Pictures";
	static final String TEST_ROOT = "Pictures";

	public static void main(String... args) throws IOException {
		final File dbDir = new File(TEST_DIR);

		if (!dbDir.exists())
			dbDir.mkdir();

		final PictureDB db = new DummyDB();
		final File root = new File(TEST_ROOT);

		long time = System.currentTimeMillis();
		DBProcessorSerial.processFiles(root, db, dbDir);
		time = System.currentTimeMillis() - time;
		System.out.println(
				"Processed " + db.size() + " files in " + time / 1000.0 + " s");

		long size = 0;
		for (final PictureDatum datum : db)
			size += datum.src.length();

		System.out.println(
				"Total size of pictures: " + (size / 1000000.0) + " MB");
	}
}
