package im.ene.lab.sibm.map.ksj.shelter;

import im.ene.lab.sibm.map.ksj.handler.ShelterDataHandler;
import im.ene.lab.sibm.models.NPoint;
import im.ene.lab.sibm.models.NPrefecture;
import im.ene.lab.sibm.models.ShelterDataset;
import im.ene.lab.sibm.models.ShelterPoint;
import im.ene.lab.sibm.util.GeneralFileFilter;
import im.ene.lab.sibm.util.NDataUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.IllegalSelectorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ShelterDataLoaderImpl implements ShelterDataLoader {

	private static final String[] KSJ_TYPE_FORMAT = { null, // 0
			null, // 1
			"N02", // 2
			"N03", // 3
			null, // 4
			null, // 5
			null, // 6
			"N07", // 7
			null, // 8
			null, // 9
			null, // 10
			"P11", // 11
			null, // 12
			null, // 13
			null, // 14
			null, // 15
			null, // 16
			null, // 17
			null, // 18
			null, // 19
			"P20" // 20
	};

	private static final String[] KSJ_URL_FORMAT_LIST = { null, // 0
			null, // 1
			"N02/N02-11/N02-11_GML.zip", // 2
			"N03/N03-11/N03-120331_%02d_GML.zip", // 3
			null, // 4
			null, // 5
			null, // 6
			"N07/N07-11/N07-11_%02d_GML.zip", // 7
			null, // 8
			null, // 9,
			null, // 10,
			"P11/P11-10/P11-10_%02d_GML.zip", // 11
			null, null, null, null, null, null, null, null, // 12 ~ 19
			"P20/P20-12/P20-12_%02d_GML.zip" // 20
	};

	/**
	 * ファイルの文字コード
	 */
	private static final String CHARSET = "MS932";

	/**
	 * オリジナルファイルの保存フォルダ
	 */
	private final String orgDir;

	/**
	 * CSVファイルの保存ディレクトリ
	 */
	private String csvDir;

	private final SAXParserFactory factory;

	private static final String KSJ_URL_BASE = "http://nlftp.mlit.go.jp/ksj/gml/data/";

	public ShelterDataLoaderImpl(String orgDir, String csvDir) {
		this.orgDir = orgDir;
		this.csvDir = csvDir;
		this.factory = SAXParserFactory.newInstance();
		// System.out.println("org: " + new File(this.orgDir).getPath());
		// System.out.println("csv: " + new File(this.csvDir).getPath());
	}

	@Override
	public ShelterDataset[] getShelterDataset() {
		ShelterDataset[] dataSets = new ShelterDataset[47];
		for (int i = 0; i < dataSets.length; i++) {
			dataSets[i] = getShelterDataset(i);
		}

		return dataSets;
	}

	@Override
	public ShelterDataset getShelterDataset(int prefCode) {
		ShelterDataset dataSet = new ShelterDataset();
		ShelterPoint[] points = readShelterGML(prefCode);
		dataSet.setShelterPoints(points);
		return dataSet;
	}

	public ShelterDataset getShelterDataset(int prefCode, int maxPointCount) {
		ShelterDataset dataSet = new ShelterDataset();
		ShelterPoint[] points = readShelterGML(prefCode);

		if (points.length > maxPointCount) {
			points = Arrays.copyOf(points, maxPointCount);
		}

		dataSet.setShelterPoints(points);
		return dataSet;
	}

	public NPrefecture getPrefectureData(int code) {
		if (!NDataUtils.PREFS.containsKey(code))
			return null;

		ShelterPoint[] points = readShelterGML(code);
		NPrefecture pref = new NPrefecture(NDataUtils.PREFS.get(code), code);
		pref.setShelterPoints(points);
		// pref.setShelterPoint(points[0]);
		return pref;
	}

	public NPrefecture getPrefectureData(int code, double range) {
		if (!NDataUtils.PREFS.containsKey(code))
			return null;

		ShelterPoint[] points = readShelterGML(code);

		double x = 0, y = 0;

		for (ShelterPoint point : points) {
			x += point.getGeoPoint().getLat();
			y += point.getGeoPoint().getLng();
		}

		final NPoint center = new NPoint(x / points.length, y / points.length);

		List<ShelterPoint> sPoints = Arrays.asList(points);

		Collections.sort(sPoints, new Comparator<ShelterPoint>() {

			@Override
			public int compare(ShelterPoint o1, ShelterPoint o2) {
				double dis = NPoint.distance(o1.getGeoPoint(), center)
						- NPoint.distance(o2.getGeoPoint(), center);
				return dis <= 0 ? -1 : 1;
			}
		});

		NPrefecture pref = new NPrefecture(NDataUtils.PREFS.get(code), code);
		pref.setCenter(center);
		ArrayList<ShelterPoint> prefPoinst = new ArrayList<ShelterPoint>();
		for (int i = 0; i < sPoints.size(); i++) {
			if (NPoint.distance(sPoints.get(i).getGeoPoint(), center) <= range)
				prefPoinst.add(sPoints.get(i));
		}

		pref.setShelterPoints(prefPoinst);

		return pref;
	}
	
	@Deprecated
	public NPrefecture getPrefectureData(int code, int max) {
		if (!NDataUtils.PREFS.containsKey(code))
			return null;

		ShelterPoint[] points = readShelterGML(code);

		double x = 0, y = 0;

		for (ShelterPoint point : points) {
			x += point.getGeoPoint().getLat();
			y += point.getGeoPoint().getLng();
		}

		final NPoint center = new NPoint(x / points.length, y / points.length);

		List<ShelterPoint> sPoints = Arrays.asList(points);

		Collections.sort(sPoints, new Comparator<ShelterPoint>() {

			@Override
			public int compare(ShelterPoint o1, ShelterPoint o2) {
				double dis = NPoint.distance(o1.getGeoPoint(), center)
						- NPoint.distance(o2.getGeoPoint(), center);
				return dis <= 0 ? -1 : 1;
			}
		});

		NPrefecture pref = new NPrefecture(NDataUtils.PREFS.get(code), code);
		pref.setCenter(center);
		int len = sPoints.size() > max ? max : sPoints.size();
		ArrayList<ShelterPoint> prefPoinst = new ArrayList<ShelterPoint>(len);
		for (int i = 0; i < len; i++) {
			prefPoinst.add(sPoints.get(i));
		}

		pref.setShelterPoints(prefPoinst);

		return pref;
	}

	@Override
	public ShelterPoint[] readShelterGML(int code) {
		long t0 = System.nanoTime();

		File file = new File(this.orgDir
				+ File.separatorChar
				+ String.format("%02d" + File.separatorChar
						+ "P20-12_%02d.xml.gz", code, code));

		getKsjFile(20, code);

		// extract files
		ShelterPoint[] points = null;
		try {
			SAXParser parser = this.factory.newSAXParser();
			ShelterDataHandler handler = new ShelterDataHandler();
			parser.parse(new GZIPInputStream(new FileInputStream(file)),
					handler);
			points = handler.getShelterPoints();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.printf("P20 %02d: %dms\n", code,
				(System.nanoTime() - t0) / 1000000);
		// System.out.println("Data retrieved: " + (points == null ? "null" :
		// ""));

		return points;
	}

	@Override
	public File getFile(int type, int code) {
		return new File(this.orgDir
				+ File.separatorChar
				+ String.format("%02d" + File.separatorChar
						+ KSJ_TYPE_FORMAT[type] + "-%02d.zip", code, code));
	}

	/**
	 * download files from server in this implements, we are about to call
	 * getKsjFile(20);
	 */
	@Override
	public File[] getKsjFile(final int type, final int code) {
		File zip = getFile(type, code);
		File dir = zip.getParentFile();
		if (!dir.isDirectory() && !dir.mkdirs()) {
			throw new IllegalStateException();
		}
		File[] ret = null;
		FileFilter filter = new FileFilter() {
			String regexFile = String.format(KSJ_TYPE_FORMAT[type]
					+ "-(?:\\d+_)?%02d(?:.+)?\\.xml(:?\\.gz)?", code);

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().matches(regexFile)
						|| pathname.getName().endsWith("GML");
			}
		};

		try {
			if (!NDataUtils.hasExtracted(dir, filter)) {
				/*
				 * 圧縮ファイルが残っている or ディレクトリが存在しない or ディレクトリ内のファイルが存在しない or
				 * ディレクトリの内容が正確でない（チェックできてない）
				 */
				URL url = new URL(KSJ_URL_BASE
						+ String.format(KSJ_URL_FORMAT_LIST[type], code));
				long t0 = System.currentTimeMillis();
				System.out.printf("DL: %s ...\n", url.getPath());
				if (!NDataUtils.download(url, zip))
					return null;
				System.out.printf("DL: %s / %dms\n", url.getPath(),
						(System.currentTimeMillis() - t0));
			}

			if (zip.exists()) {
				// ファイルの展開
				long t0 = System.currentTimeMillis();
				List<File> extracted = NDataUtils.unzip(zip, dir, filter);
				System.out.printf("unzip: %s / %dms\n", zip.getName(),
						(System.currentTimeMillis() - t0));
				for (File file : extracted) {
					if (file.exists()) {
						File parent = file.getParentFile();
						if (!dir.equals(parent)) {
							for (File child : parent.listFiles()) {
								if (!child.renameTo(new File(dir, child
										.getName()))) {
									throw new IllegalSelectorException();
								}
							}
							if (!parent.delete()) {
								throw new IllegalStateException();
							}
						}
					}
				}
				if (!zip.delete()) {
					throw new IllegalStateException();
				}
			}

			File[] listFiles = dir.listFiles(new GeneralFileFilter("xml"));
			File[] zipFiles = new File[listFiles.length];
			for (int i = 0; i < listFiles.length; i++) {
				File file = listFiles[i];
				zipFiles[i] = NDataUtils.gzip(file);
			}
			ret = zipFiles;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ret;
	}

}
