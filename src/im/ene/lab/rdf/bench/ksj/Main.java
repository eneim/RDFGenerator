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
		int mb = 1024 * 1024;
		long mem;
		Runtime runtime = Runtime.getRuntime();
		// Print free memory
		mem = runtime.freeMemory();
		System.out.println("Free Memory before:" + mem / mb);

		/*
		 * at titech
		 */
		System.setProperty("http.proxyHost", "proxy.noc.titech.ac.jp");
		System.setProperty("http.proxyPort", "3128");

		String dir = args.length > 0 ? args[0] : "shelter_data";

		// KsjDataManager mgr = new KsjDataManager(dir + File.separatorChar
		// + "org", dir + File.separatorChar + "csv");

		ShelterDataLoaderImpl shelterLoader = new ShelterDataLoaderImpl(dir
				+ File.separatorChar + "original", dir + File.separatorChar
				+ "csv");

		// System.out.println(dir);
		// mgr.getJapanPolygon();
		// mgr.getAreaDataset();
		// mgr.getRailwayDataset();
		// mgr.getBusDataset(13);
		// mgr.getKsjFile(20);

		int code = 10;
		
		ShelterDataset tokyoData = shelterLoader.getShelterDataset(code);
		// System.out.println(tokyoData.toString());
		//
		File file = new File(dir + File.separatorChar + code + ".txt");
		//
		// // if file doesnt exists, then create it
		// if (!file.exists()) {
		// file.createNewFile();
		// }
		//
		FileOutputStream outFile = new FileOutputStream(file);
		//
		// // FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
		// // BufferedWriter bw = new BufferedWriter(fw);
		// // bw.write(tokyoData.toString());
		// // bw.close();
		//
		
		DataUtil.MODEL.setNsPrefix("ksj", "http://nlftp.mlit.go.jp/ksj/schemas/ksj-app/");
		DataUtil.MODEL.setNsPrefix("gml", "http://www.opengis.net/gml/3.2/");
		
		tokyoData.toRDF(DataUtil.MODEL).write(outFile, "RDF/XML");
		outFile.flush();

		long memAfter = runtime.freeMemory();
		// Print free memory
		System.out.println("Free Memory after:" + memAfter / mb);
		System.out.println("Used mem:" + (mem - memAfter) / mb);
		
		outFile.close();
		// RandomProfileUtil profile = new RandomProfileUtil();
		// NPeople[] profiles = profile.getDefault(); // 2 people at a time
		//
		// if (profiles != null && profiles.length >= 1)
		// System.out.println(DataUtil.GSON.toJson(profiles[0]));

	}
}
