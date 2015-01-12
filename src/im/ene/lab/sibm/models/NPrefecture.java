package im.ene.lab.sibm.models;

import java.util.ArrayList;
import java.util.List;

import im.ene.lab.sibm.util.DataUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class NPrefecture {

	public static final String BASE_PREF = "http://lab.ene.im/SIBM/thing/prefecture/";

	private Resource resource;

	private final int code;

	private final String name;

	private ShelterPoint[] shelterPoints;

	private List<ShelterPoint> invalidPoinst = new ArrayList<ShelterPoint>();
	
	private int pointCount = 0;
	
	public ShelterPoint[] getShelterPoints() {
		return shelterPoints;
	}

	public void setShelterPoint(ShelterPoint point) {
		this.shelterPoints = new ShelterPoint[] { point };
		this.resource.addProperty(NProperty.hasShelterPoint,
				point.getResource());
		this.resource.getModel().add(point.getResource().getModel());
	}

	public void setShelterPoints(ShelterPoint[] shelterPoints) {
		if (shelterPoints == null || shelterPoints.length < 1)
			return;
		
		this.shelterPoints = shelterPoints;
		pointCount = shelterPoints.length;
		for (ShelterPoint point : shelterPoints) {
			this.resource.addProperty(NProperty.hasShelterPoint,
					point.getResource());
			this.resource.getModel().add(point.getResource().getModel());
			
			if (point.getSeatingCapacity() < 0)
				this.invalidPoinst.add(point);
		}
	}

	public int getShelterPointCount() {
		return pointCount;
	}
	
	public int getInvalidPointCount() {
		return this.invalidPoinst.size();
	}
	
	private Model model = DataUtil.createModel();

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
