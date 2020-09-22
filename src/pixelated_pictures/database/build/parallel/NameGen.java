package pixelated_pictures.database.build.parallel;

public class NameGen {
	private int count = 0;

	public synchronized String nextName() {
		return "pic" + ++count + ".png";
	}
}
