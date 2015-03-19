package im.ene.lab.sibm.models;

import im.ene.lab.sibm.util.NDataUtils;

import java.util.ArrayList;
import java.util.List;

import ucar.nc2.grib.grib2.Grib2Gds.LatLon;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.vividsolutions.jts.geom.Point;

public class NPrefecture {

	public static final String BASE_PREF = "http://lab.ene.im/SIBM/thing/prefecture/";

	private Resource resource;

	private final int code;

	private final String name;

	private ArrayList<ShelterPoint> shelterPoints;

	private int pointCount = 0;

	private int averageCapacity = 0;

	private NPoint center;

	public NPoint getCenter() {
		return center;
	}

	public void setCenter(NPoint center) {
		this.center = center;
	}

	public ArrayList<ShelterPoint> getShelterPoints() {
		return this.shelterPoints;
	}

	public void setShelterPoint(ShelterPoint point) {
		this.shelterPoints = new ArrayList<ShelterPoint>();
		this.shelterPoints.add(point);
		this.resource.addProperty(NProperty.hasShelterPoint,
				point.getResource());
		this.resource.getModel().add(point.getResource().getModel());
	}

	public void setShelterPoints(ArrayList<ShelterPoint> shelterPoints) {
		if (shelterPoints == null || shelterPoints.size() < 1)
			return;

		this.shelterPoints = shelterPoints;
		for (int i = 0; i < shelterPoints.size(); i++) {
			int cap = shelterPoints.get(i).getSeatingCapacity();
			if (cap < 0)
				cap = 0;
			averageCapacity += cap;
		}

		pointCount = shelterPoints.size();
		averageCapacity = averageCapacity / pointCount;

		for (ShelterPoint point : shelterPoints) {
			this.resource.addProperty(NProperty.hasShelterPoint,
					point.getResource());
			this.resource.getModel().add(point.getResource().getModel());
		}
	}

	public void setShelterPoints(ShelterPoint[] shelterPoints) {
		if (shelterPoints == null || shelterPoints.length < 1)
			return;

		this.shelterPoints = new ArrayList<ShelterPoint>(shelterPoints.length);
		// this.shelterPoints = new ShelterPoint[shelterPoints.length];

		for (int i = 0; i < shelterPoints.length; i++) {
			this.shelterPoints.add(shelterPoints[i]);
			int cap = shelterPoints[i].getSeatingCapacity();
			if (cap < 0)
				cap = 0;
			averageCapacity += cap;
		}

		pointCount = shelterPoints.length;
		averageCapacity = averageCapacity / pointCount;

		for (ShelterPoint point : shelterPoints) {
			this.resource.addProperty(NProperty.hasShelterPoint,
					point.getResource());
			this.resource.getModel().add(point.getResource().getModel());
		}
	}

	public int getShelterPointCount() {
		return pointCount;
	}

	public int getAverageCapacity() {
		return averageCapacity;
	}

	private Model model = NDataUtils.createModel();

	public NPrefecture(String name, int code) {
		this.name = name;
		this.code = code;

		this.resource = model.createResource(BASE_PREF + name);
		this.resource.addLiteral(NProperty.CODE, code);
		this.resource.addLiteral(NProperty.NAME, name);
	}

	public int getCode() {
		return this.code;
	}

	public String getName() {
		return this.name;
	}

	public Resource getResource() {
		return this.resource;
	}

}
