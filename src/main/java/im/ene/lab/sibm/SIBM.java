package im.ene.lab.sibm;

import im.ene.lab.sibm.generator.Generator;
import im.ene.lab.sibm.map.ksj.shelter.SchoolDataLoaderImpl;
import im.ene.lab.sibm.map.ksj.shelter.ShelterDataLoaderImpl;
import im.ene.lab.sibm.models.NPerson;
import im.ene.lab.sibm.models.NPoint;
import im.ene.lab.sibm.models.NPrefecture;
import im.ene.lab.sibm.models.NProperty;
import im.ene.lab.sibm.models.School;
import im.ene.lab.sibm.models.SchoolDataSet;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.query.spatial.EntityDefinition;
import org.apache.jena.query.spatial.SpatialDatasetFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

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
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.ResourceUtils;

public class SIBM {

	public static final String dir = "sibm";

	public static void main(String... args) throws IOException {
		int prefCode = 10;
		double range = 10;

		String[] queryFile = null;
		boolean willImport = false;
		// benchmark(shelterCount);

		if (args.length <= 1) {
			System.out
					.println("Invalid shelter count. \nUsage: java -jar SIBM.jar -pref xx -range [-i] [-qf] [filenames...]");
			return;
		}

		prefCode = Integer.valueOf(args[1]);
		range = Double.valueOf(args[3]);

		if (args.length >= 4 && args[4].equals("-i")) {
			willImport = true;

			if (args.length >= 7) {
				queryFile = new String[args.length - 6];

				for (int i = 0; i < queryFile.length; i++)
					queryFile[i] = args[i + 6];
			}
		}

		try {
			System.out
					.println(benchmark(prefCode, range, willImport, queryFile));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String benchmark(int prefCode, double range,
			boolean willImport, String[] queryFile) throws Exception {
		long start = System.nanoTime();

		Builder builder = new Builder(prefCode, range);

		builder
		// .execute()
		.execute3();

		if (willImport)
			builder.importToDatabase();

		if (queryFile != null && queryFile.length > 0) {
			for (String file : queryFile) {
				try {
					builder.query(file);
				} catch (Exception er) {
					er.printStackTrace();
				}
			}

			builder.queryDistance(20);
		}

		builder = null;
		return "total runtime: " + (System.nanoTime() - start) / 1000000
				+ " ms";
	}

	@Deprecated
	public static String benchmark(int count, boolean willImport,
			String[] queryFile) throws Exception {
		int complexity = 0;
		long start = System.nanoTime();

		Builder builder = new Builder(count);
		builder.setupStrategy().setComplexity(complexity)
		// .execute()
				.execute2();

		if (willImport)
			builder.importToDatabase();

		if (queryFile != null && queryFile.length > 0) {
			for (String file : queryFile) {
				try {
					builder.query(file);
				} catch (Exception er) {
					er.printStackTrace();
				}
			}

			builder.queryDistance(20);

		}

		builder = null;
		return count + ", total runtime: " + (System.nanoTime() - start)
				/ 1000000 + " ms";
	}

	public static class Builder {
		// strategy builder
		@Deprecated
		private int shelterPointCount;

		// change to range builder
		private double range;

		private int prefCode;

		private List<NPoint> sPrefCenterList = new ArrayList<NPoint>();
		// log file
		private final NFileUtils logFile;

		private int[] regionCodes;
		private int personCount;
		private int complexity; // 0: normal, 1: complex, with family
								// relationship

		private List<Region> regions = new ArrayList<Region>();

		private final int sRegionCount = 9;

		private final int MAX_PREF = 47;

		private final int sMaxShelterCount = 125928;

		private int sDefaultAverageCapacity = 500;

		private int[] regionIndexMap = new int[sRegionCount];

		// max people per family
		private final int sMaxPagesize = 8;

		private ShelterDataLoaderImpl shelterLoader;

		private ShelterPoint[] pages;
		private int[] pageIndex;

		private int shelterNum;

		private final String fileRoot;
		private File dataBase = null;

		SchoolDataLoaderImpl loader = new SchoolDataLoaderImpl("data");
		private SchoolDataSet sSchoolDataset = null;

		private int validate(int input, int max) {
			if (input > max)
				input = max;

			return input;
		}

		public Builder(int prefCode, double range) {
			this.prefCode = validate(prefCode, MAX_PREF);
			this.range = range;

			String globalTime = NFileUtils.fileDate.format(new Date());

			this.fileRoot = dir + File.separatorChar + "gen_" + prefCode + "_"
					+ range + "_km_" + globalTime;

			this.logFile = new NFileUtils("log_" + prefCode + "_" + range
					+ "_km_" + globalTime + ".txt");

			this.dataBase = new File(dir + File.separatorChar + "data_"
					+ prefCode + "_" + range + "_km_" + globalTime);
			if (!dataBase.exists())
				dataBase.mkdirs();

			// init
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

			shelterLoader = new ShelterDataLoaderImpl(dir + File.separatorChar
					+ "original", dir + File.separatorChar + "csv");
		}

		@Deprecated
		public Builder(int shelterCount) {
			String globalTime = NFileUtils.fileDate.format(new Date());

			this.shelterPointCount = validate(shelterCount, sMaxShelterCount);
			this.fileRoot = dir + File.separatorChar + "gen_"
					+ this.shelterPointCount + "_" + globalTime;

			this.logFile = new NFileUtils("log_" + shelterCount + "_"
					+ globalTime + ".txt");

			this.dataBase = new File(dir + File.separatorChar + "data_"
					+ this.shelterPointCount + "_" + globalTime);
			if (!dataBase.exists())
				dataBase.mkdirs();

			// init
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

			shelterLoader = new ShelterDataLoaderImpl(dir + File.separatorChar
					+ "original", dir + File.separatorChar + "csv");
		}

		public Builder setComplexity(int complexity) {
			int c = validate(complexity, 1);
			this.complexity = c;
			return this;
		}

		// TODO improve this

		public Builder setupStrategy() {
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

		@Deprecated
		public Builder execute() throws IOException {
			// input: number of shelter;
			// input: data complexity;
			// strategy: list of prefecture and number of shelter point each
			// need to generate
			// scale = -1 equal to 5000 people

			int counter = this.shelterPointCount;

			int regionIndex = 0;
			int shelterCount = 0;
			int personCount = 0;
			int tripleCount = 0;

			this.logFile.start(true);
			this.logFile.writeLine("--- start generating ---");
			this.logFile.writeLine("Input shelter count: "
					+ this.shelterPointCount);

			long start = System.nanoTime();

			while (true) {
				if (regionIndex >= sRegionCount) {
					break;
				}

				if (regionIndexMap[regionIndex] == 0) {
					break;
				}

				if (counter <= 0) {
					break;
				}

				// regionIndexMap[regionCount] > 0
				for (Prefecture pref : regions.get(regionIndex).prefectures) {
					NPrefecture prefDataset = shelterLoader.getPrefectureData(
							pref.id, counter);

					sDefaultAverageCapacity = prefDataset.getAverageCapacity();

					sPrefCenterList.add(prefDataset.getCenter());

					ArrayList<ShelterPoint> pageList = prefDataset
							.getShelterPoints();

					// "paging"
					while (pageList.size() > 0) {
						synchronized (pageList) {
							int pageSize = validate(pageList.size(),
									sMaxPagesize);

							pages = new ShelterPoint[pageSize];
							pageIndex = new int[pageSize];

							for (int i = 0; i < pageSize; i++) {
								pageIndex[i] = pageList.size();
								pages[i] = pageList.remove(0);
							}

							int max = getMaxSeat();
							shelterCount += pages.length;
							counter -= pages.length;
							// TODO
							personCount += genPeople(max);
							// TODO
							tripleCount += save(pref.nameEn,
									prefDataset.getShelterPointCount());
							// end
							pages = null;
							pageIndex = null;
						}
					}

					if (counter <= 0) {
						this.logFile.writeLine("Output shelter count: "
								+ shelterCount);
						this.logFile.writeLine("Output people count: "
								+ personCount);
						this.logFile.writeLine("Output triple count: "
								+ tripleCount);

						System.out.println("Output shelter count: "
								+ shelterCount);
						System.out.println("Output people count: "
								+ personCount);
						System.out.println("Output triple count: "
								+ tripleCount);

						// break;
					}
				}

				regionIndex++;
			}

			this.logFile.writeLine("Generate time: "
					+ (System.nanoTime() - start) / 1000000 + " ms");
			this.logFile.writeLine("--- stop generating ---");
			this.logFile.end();

			return this;
		}

		@Deprecated
		public Builder execute2() throws Exception {
			// input: number of shelter;
			// input: data complexity;
			// strategy: list of prefecture and number of shelter point each
			// need to generate
			// scale = -1 equal to 5000 people

			int counter = this.shelterPointCount;

			int shelterCount = 0;
			int personCount = 0;
			int tripleCount = 0;

			// TODO
			int ranRegionIndex = Generator.genRandomInt(0, sRegionCount - 1);
			int ranPrefIndex = Generator.genRandomInt(0,
					regions.get(ranRegionIndex).prefectures.size() - 1) - 1;

			this.logFile.start(true);
			this.logFile.writeLine("--- start generating ---");
			this.logFile.writeLine("Input shelter count: "
					+ this.shelterPointCount);

			long start = System.nanoTime();

			while (counter > 0) {
				ranPrefIndex++;

				if (ranPrefIndex > MAX_PREF)
					ranPrefIndex = ranPrefIndex % MAX_PREF;

				// regionIndexMap[regionCount] > 0
				for (Prefecture pref : regions.get(ranPrefIndex).prefectures) {
					final NPrefecture prefDataset = shelterLoader
							.getPrefectureData(pref.id, counter);
					sSchoolDataset = loader.getSchoolDataSet(pref.id);

					Collections.sort(sSchoolDataset.getSchoolList(),
							new Comparator<School>() {

								@Override
								public int compare(School o1, School o2) {
									double dis = NPoint.distance(
											o1.getGeoPoint(),
											prefDataset.getCenter())
											- NPoint.distance(o2.getGeoPoint(),
													prefDataset.getCenter());
									return dis <= 0 ? -1 : 1;
								}
							});

					sDefaultAverageCapacity = prefDataset.getAverageCapacity();
					sPrefCenterList.add(prefDataset.getCenter());

					ArrayList<ShelterPoint> pageList = prefDataset
							.getShelterPoints();

					// "paging"
					while (pageList != null && pageList.size() > 0) {
						synchronized (pageList) {
							int pageSize = validate(pageList.size(),
									sMaxPagesize);

							pages = new ShelterPoint[pageSize];
							pageIndex = new int[pageSize];

							for (int i = 0; i < pageSize; i++) {
								pageIndex[i] = pageList.size();
								pages[i] = pageList.remove(0);
							}

							int max = getMaxSeat();
							shelterCount += pages.length;
							counter -= pages.length;
							// TODO
							personCount += genPeople(max);
							// TODO
							tripleCount += save(pref.nameEn,
									prefDataset.getShelterPointCount());
							// end
							pages = null;
							pageIndex = null;
						}
					}
				}
			}

			this.logFile.writeLine("Output shelter count: " + shelterCount);
			this.logFile.writeLine("Output people count: " + personCount);
			this.logFile.writeLine("Output triple count: " + tripleCount);

			System.out.println("Output shelter count: " + shelterCount);
			System.out.println("Output people count: " + personCount);
			System.out.println("Output triple count: " + tripleCount);

			this.logFile.writeLine("Generate time: "
					+ (System.nanoTime() - start) / 1000000 + " ms");
			this.logFile.writeLine("--- stop generating ---");
			this.logFile.end();

			return this;
		}

		public Builder execute3() throws Exception {
			// input: number of shelter;
			// input: data complexity;
			// strategy: list of prefecture and number of shelter point each
			// need to generate
			// scale = -1 equal to 5000 people

			int prefCode = this.prefCode;

			int shelterCount = 0;
			int personCount = 0;
			int tripleCount = 0;

			// TODO
			// int ranRegionIndex = Generator.genRandomInt(0, sRegionCount - 1);
			// int ranPrefIndex = Generator.genRandomInt(0,
			// regions.get(ranRegionIndex).prefectures.size() - 1) - 1;

			this.logFile.start(true);
			this.logFile.writeLine("--- start generating ---");
			this.logFile.writeLine("Input pref code: " + this.prefCode);

			long start = System.nanoTime();

			Prefecture pref = null;
			for (int i = 0; i < regions.size(); i++) {
				for (Prefecture p : regions.get(i).prefectures) {
					if (p.id == prefCode) {
						pref = p;
						break;
					}
				}
			}

			final NPrefecture prefDataset = shelterLoader.getPrefectureData(
					prefCode, range * 1000);
			sSchoolDataset = loader.getSchoolDataSet(prefCode);

			Collections.sort(sSchoolDataset.getSchoolList(),
					new Comparator<School>() {

						@Override
						public int compare(School o1, School o2) {
							double dis = NPoint.distance(o1.getGeoPoint(),
									prefDataset.getCenter())
									- NPoint.distance(o2.getGeoPoint(),
											prefDataset.getCenter());
							return dis <= 0 ? -1 : 1;
						}
					});

			System.out.println(prefDataset.getCenter().getLat() + "," + prefDataset.getCenter().getLng());
			
			sDefaultAverageCapacity = prefDataset.getAverageCapacity();
			sPrefCenterList.add(prefDataset.getCenter());

			ArrayList<ShelterPoint> pageList = prefDataset.getShelterPoints();

			// "paging"
			while (pageList != null && pageList.size() > 0) {
				synchronized (pageList) {
					int pageSize = validate(pageList.size(), sMaxPagesize);

					pages = new ShelterPoint[pageSize];
					pageIndex = new int[pageSize];

					for (int i = 0; i < pageSize; i++) {
						pageIndex[i] = pageList.size();
						pages[i] = pageList.remove(0);
					}

					int max = getMaxSeat();
					shelterCount += pages.length;
					// counter -= pages.length;
					// TODO
					personCount += genPeople(max);
					// TODO
					tripleCount += save(pref.nameEn,
							prefDataset.getShelterPointCount());
					// end
					pages = null;
					pageIndex = null;
				}
			}

			this.shelterPointCount = shelterCount;
			
			this.logFile.writeLine("Output shelter count: " + shelterCount);
			this.logFile.writeLine("Output people count: " + personCount);
			this.logFile.writeLine("Output triple count: " + tripleCount);

			System.out.println("Output shelter count: " + shelterCount);
			System.out.println("Output people count: " + personCount);
			System.out.println("Output triple count: " + tripleCount);

			this.logFile.writeLine("Generate time: "
					+ (System.nanoTime() - start) / 1000000 + " ms");
			this.logFile.writeLine("--- stop generating ---");
			this.logFile.end();

			return this;
		}

		public Builder importToDatabase() throws LabelExistsException,
				IOException {
			Dataset dataset = TDBFactory.createDataset(dataBase.getPath());
			dataset.getContext().set(TDB.symUnionDefaultGraph, true);

			File fileDir = new File(this.fileRoot);
			if (!fileDir.exists()) {
				// no file, end
				return this;
			}

			this.logFile.start(true);
			this.logFile.writeLine("--- start importing ---");

			long start = System.nanoTime();

			File[] subFolders = fileDir.listFiles();

			dataset.begin(ReadWrite.WRITE);

			for (File sub : subFolders) {
				if (sub.isFile())
					continue;
				else {
					// load file from folder to model;
					File[] dataFiles = sub.listFiles();
					if (dataFiles.length == 0)
						return this;

					for (File file : dataFiles) {
						dataset.addNamedModel(
								FilenameUtils.getBaseName(file.getName()),
								FileManager.get().loadModel(
										file.getCanonicalPath()));
					}

					dataFiles = null;
				}
			}

			dataset.commit();
			dataset.end();

			this.logFile.writeLine("Import time: "
					+ (System.nanoTime() - start) / 1000000 + " ms");

			dataset.begin(ReadWrite.READ);

			this.logFile.writeLine("Data triple count: "
					+ dataset.getNamedModel("urn:x-arq:UnionGraph").getGraph()
							.size());
			this.logFile.writeLine("Data size: "
					+ NDataUtils.folderSizeMB(dataBase) + " MB");
			this.logFile.writeLine("--- stop importing ---");
			this.logFile.end();

			dataset.end();
			dataset.close();
			return this;
		}

		private int save(String prefName, int prefShelterCount)
				throws IOException {
			File dir_ = new File(fileRoot + File.separatorChar + prefName);

			if (!dir_.exists())
				dir_.mkdirs();

			int tripleCount = 0;
			for (int i = 0; i < pages.length; i++) {
				ShelterPoint point = pages[i];
				Model model = point.getResource().getModel();

				String fileName = dir_.getPath() + File.separatorChar
						+ prefName + "_" + point.getAdministrativeAreaCode()
						+ "_" + (prefShelterCount - pageIndex[i]) + ".ttl";

				File file = new File(fileName);
				if (!file.exists()) {
					file.createNewFile();
				}

				BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file)), 1024 * 8);
				model.write(wr, "Turtle");
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

			// save school info
			if (this.sSchoolDataset != null) {
				Model schoolModel = NDataUtils.createModel();
				for (School s : this.sSchoolDataset.getSchoolList()) {
					schoolModel.add(s.getResource().getModel());
				}

				String fileName = dir_.getPath() + File.separatorChar
						+ prefName + "_schools.ttl";

				File file = new File(fileName);
				if (!file.exists()) {
					file.createNewFile();
				}

				BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(file)), 1024 * 8);
				schoolModel.write(wr, "Turtle");
				wr.close();

				schoolModel.close();
				file = null;
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

					synchronized (family) {
						for (NPerson p : family) {

							School school = null;
							if (this.sSchoolDataset != null) {
								for (int i = 0; i < this.sSchoolDataset
										.getSchoolList().size(); i++) {
									school = this.sSchoolDataset
											.getSchoolList().get(i);
									if (school.getType().equals(
											p.getSchoolType())) {
										break;
									}
								}
							}

							if (p.getProfile() != null) {
								int index = Generator.genRandomInt(0,
										pages.length - 1);
								ShelterPoint randomPoint = pages[index];

								// rename for easier usage
								ResourceUtils.renameResource(
										randomPoint.getResource(),
										ShelterPoint.BASE_SHELTER + "S"
												+ pageIndex[index]);
								Resource res = p.getResource();
								if (res != null) {
									if (school != null) {
										res.addProperty(NProperty.studyAt,
												school.getResource());

										// randomPoint.getResource().getModel()
										// .add(s.getResource().getModel());
									}

									res.addProperty(NProperty.stayAt,
											randomPoint.getResource());
									randomPoint.getResource().getModel()
											.add(res.getModel());

								}
							}
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

			this.logFile.start(true);
			this.logFile.writeLine("--- start querying ---");
			this.logFile.writeLine("--- query file: " + queryFile + "---");

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

			try {
				QueryExecution qExec = QueryExecutionFactory.create(select,
						dataset);
				rs = qExec.execSelect();
				ResultSetFormatter
						.outputAsCSV(new FileOutputStream(rsFile), rs);
				// ResultSetFormatter.out(rs);
			} catch (Exception er) {
				er.printStackTrace();
			}

			dataset.end();

			String log = queryFile + "," + (System.nanoTime() - start)
					/ 1000000;

			this.logFile.writeLine(log);
			this.logFile.writeLine("--- stop querying ---");
			this.logFile.end();

			System.out.println(log);

			dataset.close();
			return this;
		}

		private Builder queryDistance(int kilometer) throws Exception {
			if (sPrefCenterList.size() < 1)
				return this;

			NPoint center = sPrefCenterList.get(Generator.genRandomInt(0,
					sPrefCenterList.size() - 1));

			// def geo field
			EntityDefinition entDef = new EntityDefinition("entityField",
					"geoField");
			Resource lat = ResourceFactory.createResource(NProperty.latitude
					.toString());
			Resource lng = ResourceFactory.createResource(NProperty.longtitude
					.toString());
			entDef.addSpatialPredicatePair(lat, lng);

			// create spartial dataset
			Dataset dataset = TDBFactory.createDataset(this.dataBase.getPath());
			dataset.getContext().set(TDB.symUnionDefaultGraph, true);

			File indexPath = new File(this.dataBase.getPath() + "index");
			if (!indexPath.exists())
				indexPath.mkdirs();

			Directory dir_ = FSDirectory.open(indexPath);
			Dataset spatialDataset = SpatialDatasetFactory.createLucene(
					dataset, dir_, entDef);

			this.logFile.start(true);
			this.logFile.writeLine("--- start querying ---");
			this.logFile.writeLine("--- query distance: " + kilometer
					+ " km---");

			long start = System.nanoTime();
			spatialDataset.begin(ReadWrite.READ);

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

			String queryParam = String.format("(%f %f %d 'km')",
					center.getLat(), center.getLng(), kilometer);

			String queryString = "PREFIX spatial: <http://jena.apache.org/spatial#>\n"
					+ "PREFIX	geo:   <http://lab.ene.im/SIBM/property/geo#>\n"
					+ "PREFIX sibm:  <http://lab.ene.im/SIBM/property#>\n\n"
					+ "PREFIX 	rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
					+ "SELECT ?x {\n"
					+
					// ?place spatial:withinCircle (51.46 2.6 10 'km') .
					"?x geo:geopoint ?point .\n"
					+ "?point spatial:nearby "
					+ queryParam + ";\n}";

			System.out.println(queryString);

			ResultSet rs;

			try {
				Query q = QueryFactory.create(queryString);
				QueryExecution qExec = QueryExecutionFactory.create(q,
						spatialDataset);
				rs = qExec.execSelect();
				ResultSetFormatter
						.outputAsCSV(new FileOutputStream(rsFile), rs);
				// ResultSetFormatter.out(rs);
			} finally {
				spatialDataset.end();
			}

			dataset.end();

			this.logFile.writeLine("Query time: " + (System.nanoTime() - start)
					/ 1000000 + " ms");
			this.logFile.writeLine("--- stop querying ---");
			this.logFile.end();

			System.out.println("Shelter count: " + this.shelterPointCount
					+ " - Query by distance: " + kilometer + " - Query time: "
					+ (System.nanoTime() - start) / 1000000 + "ms");

			dataset.close();
			return this;

		}

		private int getMaxSeat() {
			int max = 0;
			if (pages == null || pages.length == 0)
				return max;

			for (ShelterPoint p : pages) {
				if (p.getSeatingCapacity() >= 0)
					max += p.getSeatingCapacity();
				else
					max += sDefaultAverageCapacity;
			}

			return Generator.genRandomInt(max / 2, max);
		}
	}

}
