package pixelated_pictures.database.testing;

import java.io.File;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import pixelated_pictures.database.PictureDB;
import pixelated_pictures.database.PictureDatum;
import pixelated_pictures.database.build.DBProcessorParallel;

public class DBParallelTester {

	static final String TEST_DIR = "testing-parallel";

	public static void main(String... args)
			throws IOException, InterruptedException, ExecutionException {
		final File dbDir = new File(TEST_DIR);

		if (!dbDir.exists())
			dbDir.mkdir();

		final PictureDB db = new DummyDB();
		// final File root = new File("G:\\Pictures");
		final File root = new File("Pictures");

		long time = System.currentTimeMillis();

		final DBProcessorParallel proc = new DBProcessorParallel(db);

		proc.processFiles(root, dbDir);
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
