package pixelated_pictures.database.build;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import pixelated_pictures.database.PictureDB;
import pixelated_pictures.database.PictureDatum;
import pixelated_pictures.database.PictureProcessor;
import pixelated_pictures.database.build.parallel.DBProcessorParallelCall;
import pixelated_pictures.database.build.parallel.DBProcessorParallelPreLoadCall;
import pixelated_pictures.database.build.parallel.NameGen;
import pixelated_pictures.database.build.parallel.PictureFileTraversalParallel;
import pixelated_pictures.database.build.parallel.PictureTraversalPreLoadParallel;

public class DBProcessorParalellPreLoad {
	public static final int SIZE = 150;

	public final ExecutorService picProcExec;
	public final int numThreads;
	private final PictureProcessor picProc;
	private final PictureDB db;

	public DBProcessorParalellPreLoad(final PictureDB db) {
		this(Executors
				.newFixedThreadPool(Runtime.getRuntime().availableProcessors()),
				Runtime.getRuntime().availableProcessors(), db);
	}

	public DBProcessorParalellPreLoad(final ExecutorService executor,
			final int numThreads, final PictureDB db) {
		this.picProcExec = executor;
		this.numThreads = numThreads;
		this.db = db;
		this.picProc = new PictureProcessor(db);
	}

	/**
	 * Followed example from
	 * https://winterbe.com/posts/2015/04/07/java8-concurrency-tutorial-thread-executor-examples/
	 * 
	 * @param rootDir
	 * @param db
	 * @param dbDir
	 * @param size
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public void processFiles(final File rootDir, final File dbDir)
			throws IOException, InterruptedException, ExecutionException {
		final Collection<Callable<List<PictureDatum>>> threads = new ArrayList<>(
				numThreads);

		final PictureTraversalPreLoadParallel pft = new PictureTraversalPreLoadParallel(
				numThreads);

		final NameGen nameGen = new NameGen();

		// create picture processing threads
		for (int i = 0; i < numThreads; ++i)
			threads.add(new DBProcessorParallelPreLoadCall(pft, picProc, dbDir,
					nameGen));

		// start file traversal
		pft.traverseDirectory(rootDir);

		// invoke each picture processor
		List<Future<List<PictureDatum>>> futures = picProcExec
				.invokeAll(threads);

		// combine results into database
		for (final Future<List<PictureDatum>> future : futures) {
			final List<PictureDatum> list = future.get();

			for (final PictureDatum datum : list)
				db.addPicture(datum);
		}

		// end pft's thread/s
		pft.shutdown();
		picProcExec.shutdown();
	}
}
