package im.ene.lab.sibm.map;

import im.ene.lab.sibm.map.ksj.shelter.SchoolDataLoaderImpl;
import im.ene.lab.sibm.models.School;
import im.ene.lab.sibm.models.SchoolDataSet;
import im.ene.lab.sibm.models.School.SchoolType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;

import com.github.filosganga.geogson.gson.GeometryAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Test {

	public static void main(String[] args) throws IOException {
		String dir = args.length > 0 ? args[0] : "data";

		File root = new File(dir);
		if (!root.exists())
			root.mkdirs();

		// InputStream in = new FileInputStream(new File("sibm/japan.geojson"));
		//
		// Gson gson = new GsonBuilder()
		// .registerTypeAdapterFactory(new GeometryAdapterFactory())
		// .create();
		//
		// FeatureJSON fj = new FeatureJSON();
		// SimpleFeatureCollection sm = (SimpleFeatureCollection)
		// fj.readFeatureCollection(in);
		// SimpleFeatureIterator smi = sm.features();
		//
		// while (smi.hasNext()) {
		// SimpleFeature f = smi.next();
		// System.out.println(f.getAttribute("nam_ja"));
		// }

		SchoolDataLoaderImpl loader = new SchoolDataLoaderImpl(dir);
		SchoolDataSet setTokyo = loader.getSchoolDataSet(13);
		for (School sh : setTokyo.getSchools()) {
			if (sh.getType() == SchoolType.OTH)
				System.out.println(sh.getName());
		}

		// mgr.getAreaDataset();
		// mgr.getRailwayDataset();
		// mgr.getBusDataset();
	}
}
