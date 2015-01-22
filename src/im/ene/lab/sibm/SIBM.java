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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.google.gson.reflect.TypeToken;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.LabelExistsException;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;

public class SIBM {

	public static final String dir = "sibm";

	public static void main(String... args) throws IOException {
		int shelterCount = 10;
		String queryFile = null;

		// benchmark(shelterCount);

		if (args.length <= 1) {
			System.out
					.println("Invalid shelter count. \nUsage: java -jar SIBM.jar -count 100");
			return;
		}

		shelterCount = Integer.valueOf(args[1]);

		if (args.length >= 4)
			queryFile = args[3];

		try {
			System.out.println(benchmark(shelterCount, queryFile));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String benchmark(int count, String queryFile)
			throws Exception {
		int complexity = 0;
		long start = System.nanoTime();
		(new Builder(count)).init().setup().setComplexity(complexity).execute()
				.importToDatabase().query(queryFile);

		return count + "," + (System.nanoTime() - start) / 1000000;
	}

	public static class Builder {
		// strategy builder
		private final int shelterPointCount;

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

		// max people per family
		private final int PAGE_SIZE = 8;

		private ShelterDataLoaderImpl shelterLoader;

		private ShelterPoint[] pages;
		private int[] pageIndex;

		private int shelterNum;

		// private Dataset dataset;

		private File dataBase = null;

		private int validate(int input, int max) {
			if (input > max)
				input = max;

			return input;
		}

		public Builder(int shelterCount) {
			this.shelterPointCount = validate(shelterCount, MAX_SHELTER_POINT);
		}

		public Builder init() {
			shelterNum = 0;
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(dir + File.separatorChar
								+ "regions.json"))));

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

		public Builder setup() {
			// create strategy by input count
			int count = this.shelterPointCount;

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

		public Builder execute() throws IOException {
			// input: number of shelter;
			// input: data complexity;
			// strategy: list of prefecture and number of shelter point each
			// need to generate
			// scale = -1 equal to 5000 people

			NFileUtils readme = new NFileUtils("log.txt");
			readme.start(true);

			int counter = this.shelterPointCount;

			int regionIndex = 0;
			int shelterCount = 0;
			int personCount = 0;
			int tripleCount = 0;

			readme.writeLine("Input shelter count: " + this.shelterPointCount);

			while (regionIndex < MAX_REGION) {
				if (regionIndexMap[regionIndex] == 0) {
					break;
				}

				// regionIndexMap[regionCount] > 0
				for (Prefecture pref : regions.get(regionIndex).prefectures) {
					NPrefecture prefDataset = shelterLoader.getPrefectureData(
							pref.id, counter);

					System.out.println("Average capacity: "
							+ prefDataset.getAverageCapacity());
					ArrayList<ShelterPoint> pageList = prefDataset
							.getShelterPoints();

					// "paging"
					while (pageList.size() > 0) {
						synchronized (pageList) {
							int len = pageList.size() >= PAGE_SIZE ? PAGE_SIZE
									: pageList.size();
							pages = new ShelterPoint[len];
							pageIndex = new int[len];

							for (int i = 0; i < len; i++) {
								pageIndex[i] = pageList.size();
								pages[i] = pageList.remove(0);
							}

							int max = getMaxSeat();
							max = Generator.genRandomInt(max / 2, max);
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

				regionIndex++;
				if (counter <= 0) {
					readme.end();
					break;
				}
			}

			return this;
		}

		public Builder importToDatabase() throws LabelExistsException,
				IOException {
			this.dataBase = new File(dir + File.separatorChar + "data_"
					+ this.shelterPointCount + "_" + System.currentTimeMillis());
			if (!dataBase.exists())
				dataBase.mkdirs();

			Dataset dataset = TDBFactory.createDataset(dataBase.getPath());
			dataset.getContext().set(TDB.symUnionDefaultGraph, true);

			File fileDir = new File(dir + File.separatorChar + "gen_"
					+ this.shelterPointCount);
			if (!fileDir.exists()) {
				// no file, end
				return this;
			}

			File[] subFolders = fileDir.listFiles();
			System.out.println(subFolders.length);
			for (File sub : subFolders) {
				if (sub.isFile())
					continue;
				else {
					// load file from folder to model;
					File[] dataFiles = sub.listFiles();
					if (dataFiles.length == 0)
						return this;

					dataset.begin(ReadWrite.WRITE);

					for (File file : dataFiles) {
						dataset.addNamedModel(
								FilenameUtils.getBaseName(file.getName()),
								FileManager.get().loadModel(
										file.getCanonicalPath()));
					}

				}
			}

			dataset.commit();
			dataset.end();

			dataset.begin(ReadWrite.READ);
			Iterator<String> graphs = dataset.listNames();
			while (graphs.hasNext()) {
				String g = graphs.next();
				System.out.println(g + " - "
						+ dataset.getNamedModel(g).getGraph().size());
			}

			System.out.println("default - "
					+ dataset.getNamedModel("urn:x-arq:UnionGraph").getGraph()
							.size());

			dataset.end();
			System.out.println("data size: " + NDataUtils.folderSize(dataBase));

			dataset.close();
			return this;
		}

		private int save(Prefecture pref, NPrefecture prefDataset)
				throws IOException {
			int tripleCount = 0;
			for (int i = 0; i < pages.length; i++) {
				ShelterPoint point = pages[i];

				Model model = point.getResource().getModel();

				File dir_ = new File(dir + File.separatorChar + "gen_"
						+ this.shelterPointCount + File.separatorChar
						+ pref.nameEn);

				if (!dir_.exists())
					dir_.mkdirs();

				String fileName = dir_.getPath() + File.separatorChar
						+ pref.nameEn + "_" + point.getAdministrativeAreaCode()
						+ "_"
						+ (prefDataset.getShelterPointCount() - pageIndex[i])
						+ ".ttl";

				File file = new File(fileName);
				if (!file.exists()) {
					file.createNewFile();
				}

				BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file)), 1024 * 8);
				// FileOutputStream outFile = new FileOutputStream(file);
				// model.write(outFile, "Turtle");
				model.write(wr, "Turtle");
				// outFile.close();
				wr.close();

				int size = model.getGraph().size();
				tripleCount += size;

				System.out.println("file: " + file.getName() + " - finished: "
						+ ++shelterNum);

				model.close();
				model = null;
				file = null;
				point = null;
			}

			try {
				System.gc();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return tripleCount;
		}

		private int genPeople(int max) {
			int personCount_ = 0;
			while (max > 0) {
				try {
					// NPerson p = Generator.genPerson();
					NPerson[] family = Generator.genFamily(
							Generator.genLastName(),
							Generator.genRandomInt(0, 2));

					personCount_ += family.length;
					max -= family.length;
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

									// res.getModel().close();
								}
							}
					family = null;
				} catch (Exception er) {
					er.printStackTrace();
				}
			}

			return personCount_;
		}

		private Builder query(String queryFile) throws Exception {
			if (this.dataBase == null)
				return this;

			if (queryFile == null)
				return this;

			Dataset dataset = TDBFactory.createDataset(this.dataBase.getPath());
			dataset.getContext().set(TDB.symUnionDefaultGraph, true);

			long start = System.nanoTime();
			dataset.begin(ReadWrite.READ);

			File qFile = new File(dir + File.separatorChar + "query"
					+ File.separatorChar + queryFile);
			if (!qFile.exists()) {
				System.out.println("query file not found");
				throw new FileNotFoundException("query file not found");
			}

			File resultDir = new File(dir + File.separatorChar + "result");
			if (!resultDir.exists())
				resultDir.mkdirs();

			File rsFile = new File(resultDir.getPath() + File.separatorChar
					+ dataBase.getName() + "queryresult.csv");
			if (rsFile.exists())
				rsFile.delete();
			else {
				rsFile.createNewFile();
			}

			String query = FileUtils.readFileToString(qFile);
			Query select = QueryFactory.create(query);

			ResultSet rs;

			try (QueryExecution qExec = QueryExecutionFactory.create(select,
					dataset)) {
				rs = qExec.execSelect();
				ResultSetFormatter
						.outputAsCSV(new FileOutputStream(rsFile), rs);
				// ResultSetFormatter.out(rs);
			}

			dataset.end();

			System.out.println("Shelter count: " + this.shelterPointCount
					+ " - Query file: " + queryFile + " - Query time: "
					+ (System.nanoTime() - start) / 1000000 + "ms");

			dataset.close();
			return this;
		}

		private int getMaxSeat() {
			if (pages == null || pages.length == 0)
				return 0;

			int max = 0;
			for (ShelterPoint p : pages) {
				if (p.getSeatingCapacity() >= 0)
					max += p.getSeatingCapacity();
				else
					max += 500;
			}

			return max;
		}
	}

}
