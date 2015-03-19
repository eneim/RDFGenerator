package im.ene.lab.sibm.models;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class JPolygons {

	@SerializedName("type")
	private String type;
	
	@SerializedName("features")
	private ArrayList<JPolygon> features;
}
