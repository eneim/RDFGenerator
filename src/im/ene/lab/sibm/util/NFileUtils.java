package im.ene.lab.sibm.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class NFileUtils {

	// NFileUtils readme = new NFileUtils("readme.txt");
	// readme.start();
	// readme.writeLine();
	// readme.end();

	public static final SimpleDateFormat fileDate = new SimpleDateFormat(
			"yyyy_MM_dd_HH_mm_ss");

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

		isEnded = false;
		isStarted = true;
		if (!file.exists()) {
			file.createNewFile();
			fileWriter = new FileWriter(file, append);
		} else
			fileWriter = new FileWriter(file, append);
	}

	public void writeLine(String line) throws IOException {
		this.fileWriter.write(line);
		this.fileWriter.write("\r\n");
	}

	public void end() throws IOException {
		if (isEnded)
			return;

		isStarted = false;
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
