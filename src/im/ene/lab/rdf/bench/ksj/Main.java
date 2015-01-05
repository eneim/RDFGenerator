package im.ene.lab.rdf.bench.ksj;

import im.ene.lab.rdf.bench.ksj.map.KsjDataManager;
import im.ene.lab.rdf.bench.ksj.map.ksj.shelter.ShelterDataLoaderImpl;
import im.ene.lab.rdf.bench.ksj.map.ksj.shelter.ShelterDataset;
import im.ene.lab.rdf.bench.ksj.util.DataUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		String dir = args.length > 0 ? args[0] : "shelter_data";

		KsjDataManager mgr = new KsjDataManager(dir + File.separatorChar
				+ "org", dir + File.separatorChar + "csv");

		ShelterDataLoaderImpl shelterLoader = new ShelterDataLoaderImpl(dir
				+ File.separatorChar + "original", dir + File.separatorChar
				+ "csv");

		System.out.println(dir);
		// mgr.getJapanPolygon();
		// mgr.getAreaDataset();
		// mgr.getRailwayDataset();
		// mgr.getBusDataset(13);
		// mgr.getKsjFile(20);

		ShelterDataset tokyoData = shelterLoader.getShelterDataset(13);
		// System.out.println(tokyoData.toString());

		File file = new File("tokyo.txt");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream outFile = new FileOutputStream(file);

		// FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
		// BufferedWriter bw = new BufferedWriter(fw);
		// bw.write(tokyoData.toString());
		// bw.close();

		tokyoData.toRDF(DataUtil.MODEL).write(outFile, "Turtle");
		// outFile.flush();
		outFile.close();

	}
}
