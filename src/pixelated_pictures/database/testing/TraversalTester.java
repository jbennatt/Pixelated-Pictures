package pixelated_pictures.database.testing;

import java.io.File;

import pixelated_pictures.database.build.PictureFileTraversal;

public class TraversalTester {
	private static final String TEST_DIR = "G:\\Pictures";

	public static void main(String... args) {
		final File dir = new File(TEST_DIR);
		final PictureFileTraversal pft = new PictureFileTraversal();

		File file;

		long time = System.currentTimeMillis();
		pft.traverseDirectory(dir);

		int count = 0;

		while (true) {
			// System.err.print("fetching...");
			file = pft.next();
			// System.err.println(file);
			if (file == PictureFileTraversal.NULL_FILE)
				break;
			System.out.println(file);
			++count;
		}

		time = System.currentTimeMillis() - time;

		System.out.println("found " + count + " files!");
		System.out.println("in " + time / 1000.0 + " s");
	}
}
