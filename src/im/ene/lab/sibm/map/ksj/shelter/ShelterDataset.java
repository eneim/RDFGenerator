package im.ene.lab.sibm.map.ksj.shelter;

import im.ene.lab.sibm.models.Prefix;
import im.ene.lab.sibm.util.DataUtil;

import java.io.UnsupportedEncodingException;

import com.hp.hpl.jena.rdf.model.Model;

public class ShelterDataset {

	public final Prefix prefix = new Prefix("dataset",
			"http://nlftp.mlit.go.jp/ksj/schemas/ksj-app/dataset");

	private ShelterPoint[] shelterPoints;

	public ShelterDataset() {

	}

	public ShelterDataset(ShelterPoint[] points) {
		super();
		this.shelterPoints = points;
	}

	public ShelterPoint[] getShelterPoints() {
		return shelterPoints;
	}

	public void setShelterPoints(ShelterPoint[] shelterPoints) {
		this.shelterPoints = shelterPoints;
	}

	@Override
	public String toString() {
		return DataUtil.GSON.toJson(this);
	}

	public Model toRDF(Model model) throws UnsupportedEncodingException {
		for (ShelterPoint point : this.getShelterPoints()) {
			model.add(point.getModel());
		}

		return model;
	}
}
