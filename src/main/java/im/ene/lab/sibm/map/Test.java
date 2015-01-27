package im.ene.lab.sibm.map;

import java.io.File;

public class Test {

	public static void main(String[] args) {
		String dir = args.length > 0 ? args[0] : "data";

		File root = new File(dir);
		if (!root.exists())
			root.mkdirs();

		KsjDataManager mgr = new KsjDataManager(dir + File.separatorChar
				+ "org", dir + File.separatorChar + "csv");

		System.out.println(dir);
		mgr.getJapanPolygon();
		// mgr.getAreaDataset();
		// mgr.getRailwayDataset();
		// mgr.getBusDataset();
	}
}
