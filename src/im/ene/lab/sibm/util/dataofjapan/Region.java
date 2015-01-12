package im.ene.lab.sibm.util.dataofjapan;

import java.util.ArrayList;
import java.util.List;

public class Region {

	public int id;

	public String nameJp;

	public String nameEn;

	public List<Prefecture> prefectures = new ArrayList<Prefecture>();

	public int shelterCount = 0;
	
	public Region(int id, String nameJp, String nameEn) {
		this.id = id;
		this.nameEn = nameEn;
		this.nameJp = nameJp;
	}

	public void addPref(Prefecture pref) {
		this.prefectures.add(pref);
	}
	
	public static Region fromString(String string) {
		String[] parts = string.split(",");
		return new Region(Integer.valueOf(parts[0]), parts[1], parts[2]);
	}
}
