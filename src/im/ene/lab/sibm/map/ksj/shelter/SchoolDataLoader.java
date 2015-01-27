package im.ene.lab.sibm.map.ksj.shelter;

import im.ene.lab.sibm.models.School;
import im.ene.lab.sibm.models.SchoolDataSet;

import java.io.File;

public interface SchoolDataLoader {

	public SchoolDataSet[] getSchoolDataSets();
	
	public SchoolDataSet getSchoolDataSet(int prefCode);
	
	public School[] readSchoolGML(int prefCode);
	
	public File getFile(int type, int code);

	public File[] getKsjFile(final int type, final int code);
}
