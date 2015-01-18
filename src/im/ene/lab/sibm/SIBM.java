package im.ene.lab.sibm;

import im.ene.lab.sibm.generator.Generator;
import im.ene.lab.sibm.map.ksj.shelter.ShelterDataLoaderImpl;
import im.ene.lab.sibm.models.NPerson;
import im.ene.lab.sibm.models.NPrefecture;
import im.ene.lab.sibm.models.NProperty;
import im.ene.lab.sibm.models.ShelterPoint;
import im.ene.lab.sibm.util.NDataUtils;
import im.ene.lab.sibm.util.NFileUtils;
import im.ene.lab.sibm.util.dataofjapan.Prefecture;
import im.ene.lab.sibm.util.dataofjapan.Region;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.google.gson.reflect.TypeToken;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class SIBM {

	public static final String dir = "sibm";

	public static void main(String... args) throws IOException {
		int shelterCount = 10;

		// benchmark(shelterCount);

		if (args.length <= 1) {
			System.out
					.println("Invalid shelter count. \nUsage: java -jar SIBM.jar -count 100");
			return;
		} else {
			if (!args[0].startsWith("-count")) {
				System.out
						.println("Invalid shelter count. \nUsage: java -jar SIBM.jar -count 100");
				return;
			} else {
				shelterCount = Integer.valueOf(args[1]);
			}
		}

		System.out.println(benchmark(shelterCount));

	}

	public static String benchmark(int count) throws IOException {
		int complexity = 0;
		int shelterCount = count;
		long start = System.nanoTime();
		(new Builder()).init().setShelterCount(shelterCount)
				.setComplexity(complexity).execute();
		return count + "," + (System.nanoTime() - start) / 1000000;
	}

	public static class Builder {
		// strategy builder
		private int shelterPointCount;

		private boolean isGenerateByShelterCount = true; // true by default;
		private boolean isGenerateByPrefCode = false;
		private boolean isGenerateByRegionCode = false;

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

		private ShelterPoint[] pages;
		private int[] pageIndex;

		private int validate(int input, int max) {
			if (input > max)
				input = max;

			return input;
		}

		public Builder() {

		}

		public Builder init() {
			FileReader reader = null;
			try {
				reader = new FileReader(dir + File.separatorChar
						+ "regions.json");

				this.regions = NDataUtils.GSON.fromJson(reader,
						new TypeToken<List<Region>>() {
						}.getType());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}

			File dataFolder = new File(dir + File.separatorChar + "gen");
			if (!dataFolder.exists())
				dataFolder.mkdirs();

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
			// scale = -1 equal to 5000 people

			NFileUtils readme = new NFileUtils("log.txt");
			readme.start(true);

			int regionCount = 0;
			int counter = this.shelterPointCount;
			int shelterCount = 0;
			int personCount = 0;
			int tripleCount = 0;

			readme.writeLine("Input shelter count: " + this.shelterPointCount);

			while (regionCount < MAX_REGION) {
				if (regionIndexMap[regionCount] == 0) {
					break;
				}

				// regionIndexMap[regionCount] > 0
				for (Prefecture pref : regions.get(regionCount).prefectures) {
					NPrefecture prefDataset = shelterLoader.getPrefectureData(
							pref.id, counter);

					System.out.println("Average capacity: "
							+ prefDataset.getAverageCapacity());
					ArrayList<ShelterPoint> pageList = prefDataset
							.getShelterPoints();

					Collections
							.shuffle(pageList, new Random(System.nanoTime()));

					// "paging"
					while (pageList.size() > 0) {
						synchronized (pageList) {
							int len = pageList.size() >= 10 ? 10 : pageList
									.size();
							pages = new ShelterPoint[len];
							pageIndex = new int[len];

							for (int i = 0; i < len; i++) {
								pageIndex[i] = pageList.size();
								pages[i] = pageList.remove(0);
							}

							int max = pages.length
									* prefDataset.getAverageCapacity() / 3;
							max = Generator.genRandomInt(max / 3, max);
							shelterCount += pages.length;
							// TODO
							personCount += genPeople(max);
							counter -= pages.length;

							// TODO
							tripleCount += save(pref, prefDataset);
							// end
							
							pages = null;
							pageIndex = null;
						}
					}

					System.out.println(pref.id + " | " + pref.nameEn + " | "
							+ prefDataset.getShelterPointCount());

					if (counter <= 0) {
						readme.writeLine("Output shelter count: "
								+ shelterCount);
						readme.writeLine("Output people count: " + personCount);
						readme.writeLine("Output triple count: " + tripleCount);

						System.out.println("Output shelter count: "
								+ shelterCount);
						System.out.println("Output people count: "
								+ personCount);
						System.out.println("Output triple count: "
								+ tripleCount);

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

		private int save(Prefecture pref, NPrefecture prefDataset)
				throws IOException {
			int tripleCount = 0;
			for (int i = 0; i < pages.length; i++) {
				ShelterPoint point = pages[i];
				String fileName = dir + File.separatorChar + "gen"
						+ File.separatorChar + pref.nameEn + "_"
						+ point.getAdministrativeAreaCode() + "_"
						+ (prefDataset.getShelterPointCount() - pageIndex[i])
						+ ".ttl";

				File file = new File(fileName);
				if (!file.exists()) {
					file.createNewFile();
				}

				BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file)), 1024 * 8);
				// FileOutputStream outFile = new FileOutputStream(file);
				Model model = point.getResource().getModel();
				// model.write(outFile, "Turtle");
				model.write(wr, "Turtle");
				// outFile.close();
				wr.close();

				int size = model.getGraph().size();
				tripleCount += size;

				System.out.println("writing: " + fileName + " | "
						+ pageIndex[i] + " - counter: "
						+ prefDataset.getShelterPoints().size());

				model = null;
				file = null;
				point = null;
			}

			try {
				System.gc();
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return tripleCount;
		}

		private int genPeople(int max) {
			int personCount = 0;
			while (max > 0) {
				max--;
				try {
					// NPerson p = Generator.genPerson();
					NPerson[] family = Generator.genFamily(
							Generator.genLastName(),
							Generator.genRandomInt(0, 2));

					personCount += family.length;
					for (NPerson p : family)
						if (p != null && p.getProfile() != null)
							synchronized (p) {
								int index = Generator.genRandomInt(0,
										pages.length - 1);

								ShelterPoint randomPoint = pages[index];
								Resource res = p.getResource();
								if (res != null) {
									res.addProperty(NProperty.stayAt,
											randomPoint.getResource());
									randomPoint.getResource().getModel()
											.add(res.getModel());
								}
							}
					family = null;
				} catch (Exception er) {
					er.printStackTrace();
				}
			}

			return personCount;
		}
	}
}
