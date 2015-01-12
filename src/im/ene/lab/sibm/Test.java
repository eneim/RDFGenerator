package im.ene.lab.sibm;

import im.ene.lab.sibm.util.DataUtil;
import im.ene.lab.sibm.util.dataofjapan.Region;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import com.google.gson.reflect.TypeToken;

public class Test {

	public static void main(String[] args) throws FileNotFoundException {
		/*
		 * at titech
		 */
		// System.setProperty("http.proxyHost", "proxy.noc.titech.ac.jp");
		// System.setProperty("http.proxyPort", "3128");

		String dir = "sibm";

		// ShelterDataLoaderImpl shelterLoader = new ShelterDataLoaderImpl(dir
		// + File.separatorChar + "original", dir + File.separatorChar
		// + "csv");
		//
		// int code = 12;
		//
		// NPrefecture prefDataset = shelterLoader.getPrefectureData(code);
		//
		// File file = new File(dir + File.separatorChar + prefDataset.getName()
		// + ".txt");
		// //
		// // // if file doesnt exists, then create it
		// // if (!file.exists()) {
		// // file.createNewFile();
		// // }
		// //
		//
		// RandomProfileUtil pUtil = new RandomProfileUtil();
		// int max = 30;
		// while (max > 0) {
		// max--;
		//
		// try {
		// // NPerson p = pUtil.getDefault();
		//
		// NPerson p = Generator.genPerson();
		//
		// if (p != null)
		// synchronized (p) {
		// // p.setType(NUserType.TYPES[(int) (Math.random() *
		// // 3)]);
		//
		// ShelterPoint randomPoint = prefDataset
		// .getShelterPoints()[Generator.genRandomInt(0,
		// prefDataset.getShelterPointCount() - 1)];
		//
		// prefDataset
		// .getResource()
		// .getModel()
		// .add(p.getResource()
		// .addProperty(NProperty.stayAt,
		// randomPoint.getResource())
		// .getModel());
		// }
		// } catch (Exception er) {
		// er.printStackTrace();
		// }
		//
		// }
		//
		// FileOutputStream outFile = new FileOutputStream(file);
		// prefDataset.getResource().getModel().write(outFile, "Turtle");
		//
		// //
		// // // FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
		// // // BufferedWriter bw = new BufferedWriter(fw);
		// // // bw.write(tokyoData.toString());
		// // // bw.close();
		// //
		//
		// // DataUtil.MODEL.setNsPrefix("ksj",
		// // "http://nlftp.mlit.go.jp/ksj/schemas/ksj-app/");
		// // DataUtil.MODEL.setNsPrefix("gml",
		// "http://www.opengis.net/gml/3.2/");
		//
		// // tokyoData.toRDF(DataUtil.MODEL).write(outFile, "RDF/XML");
		// outFile.flush();
		// outFile.close();
		// RandomProfileUtil profile = new RandomProfileUtil();
		// NPeople[] profiles = profile.getDefault(); // 2 people at a time
		//
		// if (profiles != null && profiles.length >= 1)
		// System.out.println(DataUtil.GSON.toJson(profiles[0]));

		FileReader reader = new FileReader(dir + File.separatorChar
				+ "regions.json");
		List<Region> regions = DataUtil.GSON.fromJson(reader,
				new TypeToken<List<Region>>() {
				}.getType());
		
		int total = 0;
		
		for (Region region : regions) {
			total += region.shelterCount;
		}
		
		System.out.print(total);
	}

}
