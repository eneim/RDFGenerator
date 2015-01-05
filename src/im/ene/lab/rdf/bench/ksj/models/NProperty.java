package im.ene.lab.rdf.bench.ksj.models;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class NProperty {

	protected static final String uri_ksj = "http://nlftp.mlit.go.jp/ksj/schemas/ksj-app/";

	protected static final String uri_gml = "http://www.opengis.net/gml/3.2/";

	private static Model m = ModelFactory.createDefaultModel();

	public static String getURI() {
		return uri_ksj;
	}

	public static final Property earthquakeHazard;
	public static final Property tsunamiHazard;
	public static final Property windAndFloodDamage;
	public static final Property volcanicHazard;
	public static final Property other;
	public static final Property notSpecified;

	public static final Property name = m.createProperty(uri_ksj + "name");
	public static final Property administrativeAreaCode = m
			.createProperty(uri_ksj + "administrativeAreaCode");
	public static final Property address = m
			.createProperty(uri_ksj + "address");
	public static final Property facilityType = m.createProperty(uri_ksj
			+ "facilityType");
	public static final Property seatingCapacity = m.createProperty(uri_ksj
			+ "seatingCapacity");
	public static final Property facilityScale = m.createProperty(uri_ksj
			+ "facilityScale");
	public static final Property shelterPoint = m.createProperty(uri_ksj
			+ "shelterPoint");
	public static final Property hazardClassification = m
			.createProperty(uri_ksj + "hazardClassification");

	public static final Property geopoint = m.createProperty(uri_gml
			+ "geopoint");
	public static final Property latitude;
	public static final Property longtitude;

	static {
		earthquakeHazard = m.createProperty(uri_ksj + "earthquakeHazard");
		tsunamiHazard = m.createProperty(uri_ksj + "tsunamiHazard");
		windAndFloodDamage = m.createProperty(uri_ksj + "windAndFloodDamage");
		volcanicHazard = m.createProperty(uri_ksj + "volcanicHazard");
		other = m.createProperty(uri_ksj + "other");
		notSpecified = m.createProperty(uri_ksj + "notSpecified");

		latitude = m.createProperty(uri_gml + "latitude");
		longtitude = m.createProperty(uri_gml + "longtitude");
	}

}
