package im.ene.lab.sibm.map.ksj.shelter;

import im.ene.lab.sibm.models.ShelterDataset;
import im.ene.lab.sibm.models.ShelterPoint;

import java.io.File;

public interface ShelterDataLoader {

	/**
	 * get all shelter datasets
	 */
	public ShelterDataset[] getShelterDataset();

	/**
	 * get shelter dataset by prefecture code
	 */
	public ShelterDataset getShelterDataset(int prefCode);

	public ShelterPoint[] readShelterGML(int code);

	public File getFile(int type, int code);

	public File[] getKsjFile(final int type, final int code);

}
