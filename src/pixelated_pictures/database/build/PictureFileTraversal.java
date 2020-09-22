package pixelated_pictures.database.build;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.activation.MimetypesFileTypeMap;

/**
 * This class traverses a given directory and all sub-directories and provides a
 * next() function which can be used to pop off files from a directory search.
 * 
 * @author Jared F. Bennatt
 *
 */
public class PictureFileTraversal implements Callable<Object> {
	public static final File NULL_FILE = new File("");

	private final BlockingQueue<File> fileQueue;
	private final LinkedList<File> folders = new LinkedList<>();
	private final Queue<ExecutorService> executors = new LinkedList<>();

	public PictureFileTraversal() {
		this.fileQueue = new LinkedBlockingQueue<>();
	}

	/**
	 * Starts a thread which start to fill up the internal queue full of picture
	 * files to be processed.
	 * 
	 * @param dir
	 *            directory to be searched (and all subdirectories) or file to
	 *            be processed.
	 */
	public void traverseDirectory(final File dir) {
		folders.clear();

		// check if it's really a directory
		if (dir.isDirectory())
			folders.add(dir);
		else if (isPictureFile(dir))
			this.putFile(dir);

		final ExecutorService executor = Executors.newSingleThreadExecutor();
		executors.add(executor);

		executor.submit(this);

		// start a new thread to run this in the background

		// (new Thread(this)).start();
	}

	/**
	 * Thread call. Keep going until there are no more folders to check.
	 */
	public Object call() {
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
						this.putFile(file);
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

		// This is the "poison-pill" approach
		this.putFile(NULL_FILE);

		return null;
	}

	private void putFile(final File file) {
		try {
			fileQueue.put(file);
		} catch (InterruptedException e) {
			// hopefully this doesn't happen
			e.printStackTrace();
		}
	}

	public File next() {
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
		ExecutorService executor;
		while ((executor = executors.poll()) != null) {
			executor.shutdown();
		}
	}
}
