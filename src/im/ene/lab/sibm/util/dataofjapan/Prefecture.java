package im.ene.lab.sibm.util.dataofjapan;

public class Prefecture {

	public int id;

	public String nameJp;

	public String nameEn;

	public int region;
	
	public Integer shelterCount;

	public Prefecture(int id, String nameJp, String nameEn, int region) {
		this.id = id;
		this.nameJp = nameJp;
		this.nameEn = nameEn;
		this.region = region;
	}

	public void setShelterCount(int count) {
		this.shelterCount = Integer.valueOf(count);
	}
	
	@Override
	public String toString() {
		return this.id + "-" + this.nameJp + "-" + this.nameEn + "-"
				+ this.region;
	}

	public static Prefecture fromString(String string) {
		String[] parts = string.split(",");
		return new Prefecture(Integer.valueOf(parts[0]), parts[1], parts[2],
				Integer.valueOf(parts[3]));
	}
}
