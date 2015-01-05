package im.ene.lab.rdf.bench.ksj.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DataUtil {

	public static final Gson GSON = new GsonBuilder().setPrettyPrinting()
			.create();

	public static final Model MODEL = ModelFactory.createDefaultModel();

	public static File gzip(File file) {
		File ret = null;
		try {
			String gzipPath = file.getPath() + ".gz";
			String tmpPath = file.getPath() + ".gz.tmp";
			File gzipFile = new File(gzipPath);
			OutputStream out = new GZIPOutputStream(new FileOutputStream(
					tmpPath));
			try {
				InputStream in = new FileInputStream(file);
				try {
					copy(in, out);
				} finally {
					in.close();
				}
			} finally {
				out.close();
			}
			if (new File(tmpPath).renameTo(gzipFile)) {
				ret = gzipFile;
				if (!file.delete()) {
					System.out.println(file + " => " + tmpPath);
					throw new IllegalStateException();
				}
			}
		} catch (IOException e) {
			ret = null;
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * オブジェクトを直列化してファイルに保存します。 衝突を避けるため.tmpファイルに保存後、リネームします。
	 * 
	 * @param path
	 *            保存ファイルパス
	 * @param obj
	 *            シリアライズ可能なオブジェクト
	 * @return 保存の成否
	 */
	public static boolean writeSerializable(String path, Object obj) {
		boolean ret = false;
		File file = new File(path + ".tmp");
		if (!file.getParentFile().isDirectory()
				&& !file.getParentFile().mkdirs()) {
			return false;
		}
		try {
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(file));
			try {
				out.writeObject(obj);
				out.flush();
			} finally {
				out.close();
			}
			if (!file.renameTo(new File(path))) {
				if (!file.delete()) {
					throw new IllegalStateException("Failure of delete: "
							+ file);
				}
				return ret;
			}
		} catch (Exception e) {
			ret = false;
			e.printStackTrace();
			if (file.isFile() && !file.delete()) {
				throw new IllegalStateException("Failure of delete: " + file);
			}
			File file2 = new File(path);
			if (file2.isFile() && !file2.delete()) {
				throw new IllegalStateException("Failure of delete: " + file);
			}
		}
		return ret;
	}

	/**
	 * ファイルから直列化して保存されたオブジェクトを読み込みます。
	 * 
	 * @param path
	 *            ファイルパス
	 * @param c
	 *            読み込むクラス
	 * @return 読み込んだオブジェクトのインスタンス
	 */
	public static <T> T readSerializable(String path, Class<T> c) {
		T ret = null;
		File file = new File(path);
		if (file.isFile()) {
			try {
				ObjectInputStream in = null;
				try {
					in = new ObjectInputStream(new FileInputStream(file));
					Object obj = in.readObject();
					ret = c.cast(obj);
				} finally {
					if (in != null) {
						in.close();
					}
				}
			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": "
						+ e.getMessage());
				ret = null;
				if (file.isFile() && !file.delete()) {
					throw new IllegalStateException("Failure of delete: "
							+ file);
				}
			}
		}
		return ret;
	}

	/**
	 * 圧縮ファイルを展開します。
	 * 
	 * @param zip
	 *            展開するファイル
	 * @param dir
	 *            展開先ディレクトリ
	 * @param filter
	 *            展開対象を絞り込むためのフィルター
	 * @return 展開したファイル配列
	 * @throws IOException
	 *             入出力エラー
	 */
	public static List<File> unzip(File zip, File dir, FileFilter filter) {
		List<File> extracted = new ArrayList<File>();
		try {
			ZipInputStream in = new ZipInputStream(new FileInputStream(zip));
			try {
				ZipEntry entry;
				while ((entry = in.getNextEntry()) != null) {
					String entryPath = entry.getName();
					/* 出力先ファイル */
					File outFile = new File(dir.getPath() + File.separatorChar
							+ entryPath);
					if (filter == null || filter.accept(outFile)) {
						if (!outFile.exists()
								|| entry.getSize() != outFile.length()) {
							/* entryPathにディレクトリを含む場合があるので */
							File dirParent = outFile.getParentFile();
							if (!dirParent.isDirectory() && !dirParent.mkdirs()) {
								throw new IOException("Failure of mkdirs: "
										+ dirParent);
							}
							// ディレクトリはmkdirで作成する必要がある
							if (entryPath.endsWith(File.separator)) {
								if (!outFile.mkdirs()) {
									throw new IOException("Failure of mkdirs: "
											+ outFile);
								}
							} else {
								FileOutputStream out = null;
								try {
									out = new FileOutputStream(outFile);
									copy(in, out);
								} finally {
									if (out != null) {
										out.close();
									}
								}
							}
						}
						extracted.add(outFile);
					}
				}
			} finally {
				if (in != null) {
					in.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			extracted = null;
		}
		return extracted;
	}

	/**
	 * ファイルのコピーを行います。 入出力のストリームは閉じないので注意が必要です。
	 * 
	 * @param in
	 *            入力ストリーム
	 * @param out
	 *            出力ストリーム
	 * @throws IOException
	 *             入出力エラー
	 */
	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		final byte buf[] = new byte[1024];
		int size;
		while ((size = in.read(buf)) != -1) {
			out.write(buf, 0, size);
			out.flush();
		}
	}

	/**
	 * ファイルをダウンロードします。
	 * 
	 * @param url
	 *            URL
	 * @param file
	 *            ダウンロード先のファイル
	 * @return ダウンロードできればtrue
	 * @throws IOException
	 *             入出力エラー
	 */
	public static boolean download(URL url, File file) {
		boolean ret = true;
		try {
			File tmp = new File(file.getPath() + ".tmp");

			URLConnection connect = url.openConnection();
			InputStream in = connect.getInputStream();
			try {
				// ファイルのチェック（ファイルサイズの確認）
				int contentLength = connect.getContentLength();
				if (contentLength != file.length()) {
					if (!file.getParentFile().isDirectory()) {
						file.getParentFile().mkdirs();
					}
					OutputStream out = new BufferedOutputStream(
							new FileOutputStream(tmp));
					try {
						copy(in, out);
					} finally {
						out.close();
					}
				}
			} finally {
				in.close();
			}
			if (!tmp.renameTo(file)) {
				throw new IllegalStateException();
			}
		} catch (IOException e) {
			ret = false;
		}
		return ret;
	}

	public static boolean hasExtracted(File dir, FileFilter filter) {
		File[] files = dir.listFiles(filter);
		boolean ret = false;
		for (File file : files) {
			if (file.isFile()
					|| (file.isDirectory() && hasExtracted(file, filter))) {
				ret = true;
				break;
			}
		}
		return ret;
	}

}
