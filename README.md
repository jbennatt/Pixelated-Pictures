# Pixelated-Pictures
A project that can be used to create a "pixelated picture" of pictures. The input picture will be recreated by using an array of pictures (instead of pixels). See [example](https://github.com/jbennatt/Pixelated-Pictures/blob/master/example-picture.png).

# Dependencies
[java image scaling](https://mvnrepository.com/artifact/com.mortennobel/java-image-scaling). Although I realize this is old and not very popular, at the time, I used it because it was recommended in a StackOverflow post.

## Usage
This project is not in a finished state. To see how to use it, Look at the file [src/pixelated_pictures/database/testing/DBWriteTest.java](https://github.com/jbennatt/Pixelated-Pictures/blob/master/src/pixelated_pictures/database/testing/DBWriteTest.java) to see how to create a db folder and file. This routine creates a folder, `db-test parallel`, which will contain the scaled down pictures and a file, `dbFile.txt`, which contains information for each of the (scaled down) files. On `line 22`, you will see where to specify where to search for pictures to be used for pixelation. Change `G:\\Pictures` to the root directory where all the pictures you wish to use are (the more there are the better the program will work).

After that is done, [src/pixelated_pictures/processor/testing/PixelatedBruteTest.java](https://github.com/jbennatt/Pixelated-Pictures/blob/master/src/pixelated_pictures/processor/testing/PixelatedBruteTest.java) shows how to generate a pixelated picture. `ROOT_DIR` and `DB_DIR` are not used (ignore them). You only need `DB_FILE` and it should point to the directory created above where the file created above will be located (there's a bunch of commented out code which creates the database each run--which is unnecessary if you've already created the `DB_FILE`). Next choose a picture to pixelate by changing `TEST_PIC` and then change `OUTPUT_FILE` to the file you want it saved as.
