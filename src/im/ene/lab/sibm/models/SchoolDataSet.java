package im.ene.lab.sibm.models;

import im.ene.lab.sibm.util.NDataUtils;

public class SchoolDataSet {

	private School[] schools;

	public SchoolDataSet() {
	}

	public SchoolDataSet(School[] schools) {
		super();
		this.schools = schools;
	}

	public School[] getSchools() {
		return schools;
	}

	public void setSchools(School[] schools) {
		this.schools = schools;
	}

	@Override
	public String toString() {
		return NDataUtils.GSON.toJson(this);
	}
}
