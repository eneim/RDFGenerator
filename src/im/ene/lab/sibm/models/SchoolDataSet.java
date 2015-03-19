package im.ene.lab.sibm.models;

import java.util.ArrayList;
import java.util.Arrays;

import im.ene.lab.sibm.util.NDataUtils;

public class SchoolDataSet {

	private School[] schools;

	public SchoolDataSet() {
	}

	private ArrayList<School> schoolList;

	public ArrayList<School> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(ArrayList<School> schoolList) {
		this.schoolList = schoolList;
	}

	public SchoolDataSet(School[] schools) {
		super();
		this.schools = schools;
		setSchools(schools);
	}

	public School[] getSchools() {
		return schools;
	}

	public void setSchools(School[] schools) {
		this.schools = schools;
		this.schoolList = new ArrayList<School>(schools.length);
		for (School sc : schools) {
			this.schoolList.add(sc);
		}
	}

	@Override
	public String toString() {
		return NDataUtils.GSON.toJson(this);
	}
}
