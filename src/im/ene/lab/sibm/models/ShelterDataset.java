package im.ene.lab.sibm.models;

import im.ene.lab.sibm.util.NDataUtils;

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
		return NDataUtils.GSON.toJson(this);
	}

}
