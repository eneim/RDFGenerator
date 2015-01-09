package im.ene.lab.sibm.map.ksj;

public interface Data {

	/**
	 * GMLタグに対応したデータを紐づける
	 * 
	 * @param tag
	 *            GMLタグ
	 * @param obj
	 *            対応するオブジェクト
	 */
	public void link(String tag, Object obj);

	public void setName(String name);
}
