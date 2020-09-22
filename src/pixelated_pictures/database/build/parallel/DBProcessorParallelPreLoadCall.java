package pixelated_pictures.database.build.parallel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import pixelated_pictures.database.PictureDatum;
import pixelated_pictures.database.PictureProcessor;
import pixelated_pictures.database.build.PictureFileTraversal;

public class DBProcessorParallelPreLoadCall
		implements Callable<List<PictureDatum>> {

	private final PictureTraversalPreLoadParallel pft;
	private final File dbDir;
	private final NameGen nameGen;
	private final PictureProcessor picProc;

	public DBProcessorParallelPreLoadCall(
			final PictureTraversalPreLoadParallel pft2,
			PictureProcessor picProc, final File dbDir, final NameGen nameGen) {
		this.picProc = picProc;
		this.pft = pft2;
		this.dbDir = dbDir;
		this.nameGen = nameGen;
	}

	/**
	 * Keeps pulling pictures from pft until pft (PictureFileTraversalParallel)
	 * gives a stop signal. Process each image and insert it into the database
	 * given at construction.
	 */
	@Override
	public List<PictureDatum> call() throws IOException {
		final ArrayList<PictureDatum> list = new ArrayList<>();
		PictureFileResult pic;

		while ((pic = pft
				.next()) != PictureTraversalPreLoadParallel.NULL_FILE) {
			// create file where picture will be saved.
			final File dbFile = new File(
					dbDir.getAbsolutePath() + "\\" + nameGen.nextName());

			// print what you're doing
			System.err.println(pic.file.getAbsolutePath() + " --> "
					+ dbFile.getAbsolutePath());

			// create file if it doesn't exist.
			dbFile.createNewFile();

			// add to this list of PictureDatum's. Use call from
			// PictureProcessor
			list.add(picProc.processImage(pic, dbFile));

			// print confirmation that this was successful.
			System.err.println("added " + pic);
		}

		return list;
	}

}
