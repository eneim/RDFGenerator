package im.ene.lab.sibm;

import im.ene.lab.sibm.map.ksj.shelter.ShelterDataLoaderImpl;
import im.ene.lab.sibm.models.NPerson;
import im.ene.lab.sibm.models.NProperty;
import im.ene.lab.sibm.models.NUserType;
import im.ene.lab.sibm.models.Prefecture;
import im.ene.lab.sibm.models.ShelterPoint;
import im.ene.lab.sibm.util.RandomProfileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SIBM {
	public static void main(String[] args) throws IOException {
		/*
		 * at titech
		 */
//		System.setProperty("http.proxyHost", "proxy.noc.titech.ac.jp");
//		System.setProperty("http.proxyPort", "3128");

		String dir = args.length > 0 ? args[0] : "sibm";

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

		int code = 12;

		Prefecture prefDataset = shelterLoader.getPrefectureData(code);

		File file = new File(dir + File.separatorChar + prefDataset.getName()
				+ ".txt");
		//
		// // if file doesnt exists, then create it
		// if (!file.exists()) {
		// file.createNewFile();
		// }
		//

		RandomProfileUtil pUtil = new RandomProfileUtil();
		int max = 10;
		while (max > 0) {
			max--;
			NPerson p = pUtil.getDefault();

			synchronized (p) {
				p.setType(NUserType.TYPES[(int) (Math.random() * 3)]);

				ShelterPoint randomPoint = prefDataset.getShelterPoints()[0];
				p.getResource().addProperty(NProperty.stayAt,
						randomPoint.getResource());
			}

		}

		FileOutputStream outFile = new FileOutputStream(file);
		prefDataset.getResource().getModel().write(outFile, "Turtle");

		//
		// // FileWriter fw = new FileWriter(file.getAbsoluteFile(), false);
		// // BufferedWriter bw = new BufferedWriter(fw);
		// // bw.write(tokyoData.toString());
		// // bw.close();
		//

		// DataUtil.MODEL.setNsPrefix("ksj",
		// "http://nlftp.mlit.go.jp/ksj/schemas/ksj-app/");
		// DataUtil.MODEL.setNsPrefix("gml", "http://www.opengis.net/gml/3.2/");

		// tokyoData.toRDF(DataUtil.MODEL).write(outFile, "RDF/XML");
		outFile.flush();
		outFile.close();
		// RandomProfileUtil profile = new RandomProfileUtil();
		// NPeople[] profiles = profile.getDefault(); // 2 people at a time
		//
		// if (profiles != null && profiles.length >= 1)
		// System.out.println(DataUtil.GSON.toJson(profiles[0]));

	}
	
	private static boolean validate(String command) {
		return command.startsWith("-");	
	}
}
