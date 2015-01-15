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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Resource;

public class SIBM {

	public static final String dir = "sibm";

	public static void main(String[] args) throws IOException {
		int shelterCount = 20;

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

			NFileUtils readme = new NFileUtils("readme.txt");
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
					int max = prefDataset.getShelterPointCount() * 250;
					max = Generator.genRandomInt(max / 2, max);

					shelterCount += prefDataset.getShelterPointCount();

					while (max > 0) {
						max--;
						try {
							// NPerson p = Generator.genPerson();
							NPerson[] family = Generator.genFamily(
									Generator.genLastName(),
									Generator.genRandomInt(0, 2));

							personCount += family.length;
							for (NPerson p : family)
								if (p != null)
									synchronized (p) {
										ShelterPoint randomPoint = prefDataset
												.getShelterPoints()[Generator
												.genRandomInt(
														0,
														prefDataset
																.getShelterPointCount() - 1)];

										Resource res = p.getResource();
										if (res != null) {
											res.addProperty(NProperty.stayAt,
													randomPoint.getResource());
											randomPoint.getResource()
													.getModel()
													.add(res.getModel());
										}
									}
						} catch (Exception er) {
							er.printStackTrace();
						}
					}

					counter -= prefDataset.getShelterPointCount();

					System.out.println(pref.id + " | " + pref.nameEn + " | "
							+ prefDataset.getShelterPointCount());

					// readme.writeLine(pref.id + " | " + pref.nameEn + " | "
					// + prefDataset.getShelterPointCount());

					for (int i = 0; i < prefDataset.getShelterPointCount(); i++) {
						ShelterPoint point = prefDataset.getShelterPoints()[i];
						File file = new File(dir + File.separatorChar + "gen"
								+ File.separatorChar + pref.nameEn + "_"
								+ point.getAdministrativeAreaCode() + "_" + i
								+ ".txt");
						if (!file.exists()) {
							file.createNewFile();
						}

						FileOutputStream outFile = new FileOutputStream(file);
						point.getResource().getModel().write(outFile, "Turtle");
						outFile.close();

						Iterator<Triple> triples = point.getResource()
								.getModel().getGraph()
								.find(Node.ANY, Node.ANY, Node.ANY);
						while (triples.hasNext()) {
							triples.next();
							tripleCount++;
						}
					}

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
	}
}
