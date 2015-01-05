package im.ene.lab.rdf.bench.ksj.map.ksj.handler;

import im.ene.lab.rdf.bench.ksj.map.ksj.BusRoute;
import im.ene.lab.rdf.bench.ksj.map.ksj.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 国土数値情報JPGIS2.1(GML)形式のバスルート(線)を読み込むための DefaultHandlerの継承クラス
 * 
 * @author fujiwara
 */
public class HandlerN07 extends KsjHandler {

	public BusRoute[] getBusRoutes() {
		List<BusRoute> ret = new ArrayList<BusRoute>();
		for (Data data : this.getDataMap().values()) {
			assert (data instanceof BusRoute);
			ret.add((BusRoute) data);
		}
		return ret.toArray(new BusRoute[ret.size()]);
	}

	@Override
	protected boolean checkData() {
		boolean ret = true;
		for (Data data : this.getDataMap().values()) {
			if (!(data instanceof BusRoute)) {
				System.out.println(this.getClass() + ": " + data.getClass());
				ret = false;
				break;
			}
		}
		return ret;
	}
}
