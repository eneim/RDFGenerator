package im.ene.lab.sibm.map.ksj.shelter;

import im.ene.lab.sibm.map.ksj.handler.SchoolDataHandler;
import im.ene.lab.sibm.models.School;
import im.ene.lab.sibm.models.SchoolDataSet;
import im.ene.lab.sibm.util.GeneralFileFilter;
import im.ene.lab.sibm.util.NDataUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.IllegalSelectorException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SchoolDataLoaderImpl implements SchoolDataLoader {

	private static final String[] KSJ_URL_FORMAT_LIST = {
			null, // 0
			null, // 1
			"N02/N02-11/N02-11_GML.zip", // 2
			// "N03/N03-11/N03-120331_%02d_GML.zip", // 3
			"N03/N03-14/N03-140401_%02d_GML.zip",
			// sample
			// http://nlftp.mlit.go.jp/ksj/gml/data/N03/N03-14/N03-140401_13_GML.zip
			null, // 4
			null, // 5
			null, // 6
			"N07/N07-11/N07-11_%02d_GML.zip", // 7
			null, // 8
			null, // 9,
			null, // 10,
			"P11/P11-10/P11-10_%02d_GML.zip", // 11
			null, null, null, null, null, null, null,
			null, // 12 ~ 19
			"P20/P20-12/P20-12_%02d_GML.zip", // 20
			null, null, null, null, null, null, null, null,
			"P29/P29-13/P29-13_%02d.zip" };

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
			"P20", // 20
			null, null, null, null, null, null, null, null, // 21~28
			"P29" };

	/**
	 * オリジナルファイルの保存フォルダ
	 */
	private final String orgDir;

	private final SAXParserFactory factory;

	private static final String KSJ_URL_BASE = "http://nlftp.mlit.go.jp/ksj/gml/data/";

	public SchoolDataLoaderImpl(String orgDir) {
		this.orgDir = orgDir;
		this.factory = SAXParserFactory.newInstance();
	}

	@Override
	public SchoolDataSet[] getSchoolDataSets() {
		SchoolDataSet[] dataSets = new SchoolDataSet[47];
		for (int i = 0; i < dataSets.length; i++) {
			dataSets[i] = getSchoolDataSet(i + 1);
		}

		return dataSets;
	}

	@Override
	public SchoolDataSet getSchoolDataSet(int prefCode) {
		SchoolDataSet dataSet = new SchoolDataSet();
		School[] schools = readSchoolGML(prefCode);
		dataSet.setSchools(schools);
		return dataSet;
	}

	@Override
	public School[] readSchoolGML(int code) {
		long t0 = System.nanoTime();

		File file = new File(this.orgDir
				+ File.separatorChar
				+ String.format("%02d" + File.separatorChar
						+ "P29-13_%02d.xml.gz", code, code));
		getKsjFile(29, code);

		// extract files
		School[] schools = null;
		try {
			SAXParser parser = this.factory.newSAXParser();
			SchoolDataHandler handler = new SchoolDataHandler();
			parser.parse(new GZIPInputStream(new FileInputStream(file)),
					handler);
			schools = handler.getSchools();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.printf("P20 %02d: %dms\n", code,
				(System.nanoTime() - t0) / 1000000);
		return schools;
	}

	@Override
	public File getFile(int type, int code) {
		return new File(this.orgDir
				+ File.separatorChar
				+ String.format("%02d" + File.separatorChar
						+ KSJ_TYPE_FORMAT[type] + "-%02d.zip", code, code));
	}

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
