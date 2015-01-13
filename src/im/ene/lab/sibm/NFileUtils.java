package im.ene.lab.sibm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NFileUtils {

	// NFileUtils readme = new NFileUtils("readme.txt");
	// readme.start();
	// readme.writeLine();
	// readme.end();

	private final String dir = "sibm";

	private final File file;

	private FileWriter fileWriter;

	private boolean isStarted = false, isEnded = false;

	public NFileUtils(String name) {
		this.file = new File(dir + File.separatorChar + name);
	}

	public void start(boolean append) throws IOException {
		if (isStarted)
			return;

		isStarted = true;
		if (!file.exists()) {
			file.createNewFile();
			fileWriter = new FileWriter(file, append);
		} else
			fileWriter = new FileWriter(file, false);
	}

	public void writeLine(String line) throws IOException {
		this.fileWriter.write(line);
		this.fileWriter.write("\n");
	}

	public void end() throws IOException {
		if (isEnded)
			return;

		isEnded = true;
		this.fileWriter.flush();
		this.fileWriter.close();
	}

	public boolean isStarted() {
		return this.isStarted;
	}

	public boolean isEnded() {
		return this.isEnded;
	}
}
