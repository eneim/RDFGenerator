package im.ene.lab.rdf.bench.ksj.map.ksj.handler;

import im.ene.lab.rdf.bench.ksj.map.ksj.CityArea;
import im.ene.lab.rdf.bench.ksj.map.ksj.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 国土数値情報JPGIS2.1(GML)形式の行政区画(面)を読み込むための DefaultHandlerの継承クラス
 * 
 * @author fujiwara
 */
public class HandlerN03 extends KsjHandler {

	public CityArea[] getAreas() {
		List<CityArea> list = new ArrayList<CityArea>();
		for (Data data : this.getDataMap().values()) {
			list.add((CityArea) data);
		}
		assert (!list.isEmpty());
		return list.toArray(new CityArea[list.size()]);
	}

	@Override
	protected boolean checkData() {
		boolean ret = true;
		for (Data data : this.getDataMap().values()) {
			if (!(data instanceof CityArea)) {
				System.out.println(this.getClass() + ": " + data.getClass());
				ret = false;
				break;
			}
		}
		return ret;
	}

}
