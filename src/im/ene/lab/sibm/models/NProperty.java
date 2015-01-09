package im.ene.lab.sibm.models;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;

public class NProperty {

	protected static final String sibm = "http://lab.ene.im/SIBM/property#";

	protected static final String sibm_geo = "http://lab.ene.im/SIBM/property/geo#";

	private static Model m = ModelFactory.createDefaultModel();

	// public static String getURI() {
	// return sibm;
	// }

	public static final Property ID;
	public static final Property CODE;
	public static final Property hasShelterPoint;

	public static final Property earthquakeHazard;
	public static final Property tsunamiHazard;
	public static final Property windAndFloodDamage;
	public static final Property volcanicHazard;
	public static final Property other;
	public static final Property notSpecified;

	public static final Property NAME = m.createProperty(sibm + "name");
	public static final Property administrativeAreaCode = m.createProperty(sibm
			+ "administrativeAreaCode");
	public static final Property address = m.createProperty(sibm + "address");
	public static final Property facilityType = m.createProperty(sibm
			+ "facilityType");
	public static final Property seatingCapacity = m.createProperty(sibm
			+ "seatingCapacity");
	public static final Property facilityScale = m.createProperty(sibm
			+ "facilityScale");
	public static final Property shelterPoint = m.createProperty(sibm
			+ "shelterPoint");
	public static final Property hazardClassification = m.createProperty(sibm
			+ "hazardClassification");

	public static final Property geopoint = m.createProperty(sibm_geo
			+ "geopoint");
	public static final Property latitude;
	public static final Property longtitude;

	static {
		earthquakeHazard = m.createProperty(sibm + "earthquakeHazard");
		tsunamiHazard = m.createProperty(sibm + "tsunamiHazard");
		windAndFloodDamage = m.createProperty(sibm + "windAndFloodDamage");
		volcanicHazard = m.createProperty(sibm + "volcanicHazard");
		other = m.createProperty(sibm + "other");
		notSpecified = m.createProperty(sibm + "notSpecified");

		latitude = m.createProperty(sibm_geo + "latitude");
		longtitude = m.createProperty(sibm_geo + "longtitude");

		ID = m.createProperty(sibm + "ID");
		CODE = m.createProperty(sibm + "CODE");
		hasShelterPoint = m.createProperty(sibm + "hasShelterPoint");
	}

}
