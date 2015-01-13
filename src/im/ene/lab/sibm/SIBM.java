package im.ene.lab.sibm;

import im.ene.lab.sibm.map.ksj.shelter.ShelterDataLoaderImpl;
import im.ene.lab.sibm.models.NPrefecture;
import im.ene.lab.sibm.util.DataUtil;
import im.ene.lab.sibm.util.dataofjapan.Prefecture;
import im.ene.lab.sibm.util.dataofjapan.Region;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.reflect.TypeToken;

public class SIBM {

	public static final String dir = "sibm";

	public static void main(String[] args) throws IOException {
		(new Builder()).init().setShelterCount(4).setComplexity(0).execute();
	}

	public static class Builder {
		// strategy builder
		private int shelterPointCount;

		private int[] regionCodes;

		private int personCount;

		private int complexity; // 0: normal, 1: complex, with family
								// relationship

		private List<Region> regions = new ArrayList<Region>();

		private final int MAX_REGION = 9;

		private final int MAX_PREF = 47;

		private final int MAX_SHELTER_POINT = 125928;

		private int[] regionIndexMap = new int[MAX_REGION];

		private ShelterDataLoaderImpl shelterLoader;

		private int validate(int input, int max) {
			if (input > max)
				input = max;

			return input;
		}

		public Builder() {

		}

		public Builder init() {
			FileReader reader;
			try {
				reader = new FileReader(dir + File.separatorChar
						+ "regions.json");

				this.regions = DataUtil.GSON.fromJson(reader,
						new TypeToken<List<Region>>() {
						}.getType());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			shelterLoader = new ShelterDataLoaderImpl(dir + File.separatorChar
					+ "original", dir + File.separatorChar + "csv");
			return this;
		}

		public Builder setComplexity(int complexity) {
			int c = validate(complexity, 1);
			this.complexity = c;
			return this;
		}

		public Builder setShelterCount(int count) {
			// create strategy by input count
			count = validate(count, MAX_SHELTER_POINT);

			this.shelterPointCount = count;

			for (int i = 0; i < regions.size(); i++) {
				Region region = regions.get(i);
				count -= region.shelterCount;

				if (count < 0) {
					regionIndexMap[i] = count + region.shelterCount;
					break;
				} else {
					regionIndexMap[i] = region.shelterCount;
					continue;
				}
			}

			return this;
		}

		public void execute() throws IOException {
			// input: number of shelter;
			// input: data complexity;
			// strategy: list of prefecture and number of shelter point each
			// need to generate
			// scale = -1 --> 5000

			NFileUtils readme = new NFileUtils("readme.txt");
			readme.start(true);
			
			int regionCount = 0;

			int counter = this.shelterPointCount;

			while (regionCount < MAX_REGION) {
				if (regionIndexMap[regionCount] == 0) {
					break;
				}

				// regionIndexMap[regionCount] > 0
				for (Prefecture pref : regions.get(regionCount).prefectures) {
					NPrefecture prefDataset = null;

					prefDataset = shelterLoader.getPrefectureData(pref.id,
							counter);
					counter -= prefDataset.getShelterPointCount();

					System.out.println(pref.id + " | " + pref.nameJp + " | "
							+ prefDataset.getShelterPointCount());

					readme.writeLine(pref.id + " | " + pref.nameJp + " | "
							+ prefDataset.getShelterPointCount());

					File file = new File(dir + File.separatorChar + prefDataset.getName()
							+ ".txt");
					if (!file.exists()) {
						file.createNewFile();
					}
					
					FileOutputStream outFile = new FileOutputStream(file);
					prefDataset.getResource().getModel().write(outFile, "Turtle");
					outFile.close();
					
					if (counter <= 0) {
						readme.end();
						break;
					}
				}

				regionCount++;
				if (counter <= 0) {
					readme.end();
					break;
				}

			}
		}
	}
}
