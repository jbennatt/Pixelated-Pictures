package pixelated_pictures.processor.testing;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import pixelated_pictures.database.PictureDB;
import pixelated_pictures.database.testing.DummyDB;
import pixelated_pictures.processor.PixelatePictureBrute;

public class PixelatedBruteTest {
	private static final String TEST_PIC = "C:\\Users\\benna\\eclipse\\Projects\\Web Apps\\Programming Portfolio\\Pics\\Max and Cat.JPG";
	// static final String TEST_PIC = "G:\\Pictures\\2018\\04-21-2018 Ben's
	// First Communion\\DSC_0003_Mod.JPG";
	private static final String OUTPUT_FILE = "G:\\pixelated pictures\\Max and Cat - Pixelated.png";
	private static final String ROOT_DIR = "G:\\Pictures";
	private static final String DB_DIR = "parallel db";

	private static final String DB_FILE = "db-test parallel\\dbFile.txt";

	private static final int NUM_CHOOSE = 10;

	public static void main(String... args)
			throws IOException, InterruptedException, ExecutionException {
		// final File rootDir = new File(ROOT_DIR);
		// final File dbDir = new File(DB_DIR);

		long time = System.currentTimeMillis();
		final PictureDB db = DummyDB.readDB(new File(DB_FILE));
		time = System.currentTimeMillis() - time;
		System.out.println(
				"read " + db.size() + " entries into DB in " + time + " ms");

		// dbDir.mkdir();
		//
		// final DBProcessorParallel dbProc = new DBProcessorParallel();
		//
		// long time = System.currentTimeMillis();
		//
		// dbProc.processFiles(rootDir, db, dbDir);
		//
		// time = System.currentTimeMillis() - time;
		// System.out.println(
		// "Processed " + db.size() + " files in " + time / 1000.0 + " s");

		final BufferedImage src = ImageIO.read(new File(TEST_PIC));

		time = System.currentTimeMillis();
		final BufferedImage output = PixelatePictureBrute.pixelatePicture(src,
				db, NUM_CHOOSE);

		time = System.currentTimeMillis() - time;
		System.out.println("Pixelated picture in " + time / 1000.0 + " s");
		final File outputFile = new File(OUTPUT_FILE);

		outputFile.createNewFile();

		ImageIO.write(output, "PNG", new File(OUTPUT_FILE));
		System.out.println("wrote file");
	}
}
