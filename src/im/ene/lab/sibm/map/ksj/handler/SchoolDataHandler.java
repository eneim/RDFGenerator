package im.ene.lab.sibm.map.ksj.handler;

import im.ene.lab.sibm.map.ksj.Data;
import im.ene.lab.sibm.models.School;

import java.util.ArrayList;
import java.util.List;

public class SchoolDataHandler extends KsjHandler {

	@Override
	protected boolean checkData() {
		boolean ret = true;
		for (Data data : this.getDataMap().values()) {
			if (!(data instanceof School)) {
				System.out.println(this.getClass() + ": " + data.getClass());
				ret = false;
				break;
			}
		}
		return ret;
	}

	public School[] getSchools() {
		List<School> ret = new ArrayList<School>();

		for (Data data : this.getDataMap().values()) {
			// System.out.println(data.toString());
			assert (data instanceof School) : data.getClass();
			ret.add((School) data);
		}

		return ret.toArray(new School[ret.size()]);
	}
}
