package im.ene.lab.sibm.map.ksj;

public abstract class NData {

	/**
	 * GMLタグに対応したデータを紐づける
	 * 
	 * @param tag
	 *            GMLタグ
	 * @param obj
	 *            対応するオブジェクト
	 */
	public abstract void link(String tag, Object obj);

}
