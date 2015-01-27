package im.ene.lab.sibm.util.dataofjapan;

import im.ene.lab.sibm.map.ksj.shelter.ShelterDataLoaderImpl;
import im.ene.lab.sibm.models.NPrefecture;
import im.ene.lab.sibm.util.NDataUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DOJParser {

	public static final String DIR = "sibm";

	public static void main(String[] args) {
		execute();
	}

	public static void execute() {
		BufferedReader prefBuffer = null;
		List<Prefecture> prefs = new ArrayList<Prefecture>();
		try {
			prefBuffer = new BufferedReader(new FileReader(new File(
					"src/im/ene/lab/sibm/util/dataofjapan/prefectures.csv")));

			String line;
			prefBuffer.readLine(); // first line
			while ((line = prefBuffer.readLine()) != null) {
				Prefecture pref = Prefecture.fromString(line);
				prefs.add(pref);
			}
		} catch (Exception er) {
			er.printStackTrace();
		}

		List<Region> regions = new ArrayList<Region>();
		BufferedReader regionBuffer = null;
		try {
			regionBuffer = new BufferedReader(new FileReader(new File(
					"src/im/ene/lab/sibm/util/dataofjapan/regions.csv")));
			String line;
			regionBuffer.readLine(); // first line
			while ((line = regionBuffer.readLine()) != null) {
				Region region = Region.fromString(line);
				regions.add(region);
			}
		} catch (Exception er) {
			er.printStackTrace();
		}

		for (Prefecture pref : prefs) {
			regions.get(pref.region).addPref(pref);
		}

		// load data from net, update pref info and region info
		ShelterDataLoaderImpl shelterLoader = new ShelterDataLoaderImpl(DIR
				+ File.separatorChar + "original", DIR + File.separatorChar
				+ "csv");

		long start = System.nanoTime();
		for (int i = 0; i < 47; i++) {
			NPrefecture prefDataset = shelterLoader.getPrefectureData(i + 1);
			Prefecture pref = prefs.get(i);
			pref.setShelterCount(prefDataset.getShelterPointCount());
			regions.get(pref.region).shelterCount += prefDataset
					.getShelterPointCount();
		}

		System.out.println("Time: " + (System.nanoTime() - start) / 1000000
				+ " ms");

		FileWriter fileOut = null;
		try {
			fileOut = new FileWriter(new File(DIR + File.separatorChar
					+ "regions.json"));
			fileOut.write(NDataUtils.GSON.toJson(regions));

			fileOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileOut != null)
				try {
					fileOut.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
