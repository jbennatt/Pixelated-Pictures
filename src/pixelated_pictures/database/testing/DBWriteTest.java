package pixelated_pictures.database.testing;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import pixelated_pictures.database.PictureDB;
import pixelated_pictures.database.build.DBProcessorParallel;

public class DBWriteTest {
	static final String TEST_DIR = "db-test parallel";
	static final String DB_FILE_NAME = "dbFile.txt";

	public static void main(String... args)
			throws IOException, InterruptedException, ExecutionException {
		final File dbDir = new File(TEST_DIR);

		if (!dbDir.exists())
			dbDir.mkdir();

		final PictureDB db = new DummyDB();
		final File root = new File("G:\\Pictures");

		long time = System.currentTimeMillis();

		final DBProcessorParallel proc = new DBProcessorParallel(db);

		proc.processFiles(root, dbDir);
		time = System.currentTimeMillis() - time;
		System.out.println(
				"Processed " + db.size() + " files in " + time / 1000.0 + " s");

		final File dbFile = new File(TEST_DIR + "\\" + DB_FILE_NAME);
		((DummyDB) db).writeDBInfo(dbFile);
		System.out.println("wrote db info to file: " + dbFile);
	}

}
