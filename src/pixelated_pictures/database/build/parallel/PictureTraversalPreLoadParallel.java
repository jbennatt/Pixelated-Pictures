package pixelated_pictures.database.build.parallel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import pixelated_pictures.database.build.PictureFileTraversal;

/**
 * This is a clone of PictureFileTraversal except that it accepts a number of
 * threads which determines how many poison pills to add to the list (to signal
 * the end of each thread).
 * 
 * @author Jared F. Bennatt
 *
 */
public class PictureTraversalPreLoadParallel implements Callable<Object> {
	public static final int CAPACITY = 1028;

	public static final PictureFileResult NULL_FILE = PictureFileResult.NULL_FILE;

	private final BlockingQueue<PictureFileResult> fileQueue = new LinkedBlockingQueue<>(
			CAPACITY);
	private final LinkedList<File> folders = new LinkedList<>();
	public final int numThreads;
	private Queue<ExecutorService> executors = new LinkedList<>();

	public PictureTraversalPreLoadParallel(final int numThreads) {
		this.numThreads = numThreads;
	}

	public void traverseDirectory(final File dir) throws IOException {
		folders.clear();

		// check if it's really a directory
		if (dir.isDirectory())
			folders.add(dir);
		else if (isPictureFile(dir))
			this.putFile(new PictureFileResult(dir));

		final ExecutorService executor = Executors.newSingleThreadExecutor();
		executors.add(executor);

		executor.submit(this);

		// start a new thread to run this in the background
		// (new Thread(this)).start();
	}

	public Object call() throws IOException {
		while (!folders.isEmpty()) {
			// remove next folder from queue (it will be dir to begin with)
			final File curDir = folders.pollFirst();

			// get this list of files from the current directory
			final File[] files = curDir.listFiles();

			if (files != null) {
				for (final File file : files) {
					// just like above with dir.
					// if it's a directory add it to the directory queue
					// otherwise if it's a picture file add to the queue
					// of picture files to be consumed.
					if (file.isDirectory()) {
						folders.addLast(file);
						// System.err.println("added directory: " + file);
					} else if (isPictureFile(file)) {
						// System.err.print("putting picture file: " + file +
						// "...");
						final PictureFileResult result = new PictureFileResult(
								file);
						this.putFile(result);
						// System.err.println("done");
					}
					// fileQueue.put(file) blocks if fileQueue is full (above
					// the
					// given capacity, default set to 1028, as of right now).

					// I'm assuming whatever processes these files will consume
					// them
					// at a slower rate than this routine can pump out file
					// names.
					// So instead of needlessly filling up a gigantic queue of
					// files, this will serve to slow this routine down until
					// whatever is consuming the filenames can "catch up".
				}
			}
		}

		// put a stop in for each thread
		for (int i = 0; i < numThreads; ++i)
			// This is the "poison-pill" approach
			this.putFile(NULL_FILE);

		return null;
	}

	private void putFile(final PictureFileResult file) {
		try {
			fileQueue.put(file);
		} catch (InterruptedException e) {
			// hopefully this doesn't happen
			e.printStackTrace();
		}
	}

	public synchronized PictureFileResult next() {
		try {
			return fileQueue.take();
			// if fileQueue is empty, this blocks until something is put into
			// it. This method shouldn't be called after NULL_FILE is returned
			// (the poison pill)
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isPictureFile(final File file) {
		// Taken from answer at StackOverflow:
		// https://stackoverflow.com/questions/9643228/test-if-a-file-is-an-image-file

		/* @formatter:off
		 * import javax.activation.MimetypesFileTypeMap;
		 * 
		 * import java.io.File;
		 * 
		 * class Untitled {
		 * 
		 * 	public static void main(String[] args) {
		 * 
		 * 		String filepath = "/the/file/path/image.jpg";
		 * 		File f = new File(filepath);
		 * 		String mimetype= new MimetypesFileTypeMap().getContentType(f);
		 * 		String type = mimetype.split("/")[0];
		 * 		if(type.equals("image"))
		 * 				System.out.println("It's an image"); 
		 * 		else
		 * 				System.out.println("It's NOT an image"); 
		 * 	} 
		 * }
		 * @formatter:on
		 */
		final String mimeType = new MimetypesFileTypeMap().getContentType(file);
		final String type = mimeType.split("/")[0];

		return type.equals("image");
	}

	public synchronized void shutdown() {
		ExecutorService exec;
		while ((exec = executors.poll()) != null) {
			exec.shutdown();
		}
	}
}