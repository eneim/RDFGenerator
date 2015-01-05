package im.ene.lab.rdf.bench.ksj.map.ksj.handler;

import im.ene.lab.rdf.bench.ksj.map.ksj.BusStop;
import im.ene.lab.rdf.bench.ksj.map.ksj.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 国土数値情報JPGIS2.1(GML)形式のバス停留所(点)を読み込むための DefaultHandlerの継承クラス
 * 
 * @author fujiwara
 */
public class HandlerP11 extends KsjHandler {

	public BusStop[] getBusStopArray() {
		List<BusStop> ret = new ArrayList<BusStop>();

		for (Data data : this.getDataMap().values()) {
			assert (data instanceof BusStop) : data.getClass();
			ret.add((BusStop) data);
		}

		return ret.toArray(new BusStop[ret.size()]);
	}

	@Override
	protected boolean checkData() {
		boolean ret = true;
		for (Data data : this.getDataMap().values()) {
			if (!(data instanceof BusStop)) {
				System.out.println(this.getClass() + ": " + data.getClass());
				ret = false;
				break;
			}
		}
		return ret;
	}

}
