package im.ene.lab.sibm.map.ksj.shelter;

public enum HazardType {
	EARTHQUAKE(true), TSUNAMI(true), WIND_FLOOD_DAMAGE(true), VOLCANIC(true), OTHER(
			true), NOTSPECIFIED(true);

	private boolean isAvailable;

	private HazardType(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public void setAvailability(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public boolean isAvailable() {
		return this.isAvailable;
	}
}
