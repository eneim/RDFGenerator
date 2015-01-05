package im.ene.lab.rdf.bench.ksj.map.ksj.shelter;

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
