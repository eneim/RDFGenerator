package im.ene.lab.sibm.models;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class JPolygon {

	@SerializedName("type")
	private String type;
	
	@SerializedName("properties")
	private JProperty properties;
	
	@SerializedName("geometry")
	private JGeometry geometry;
	
	private static class JProperty {
		
		@SerializedName("nam")
		private String nam;
		
		@SerializedName("nam_ja")
		private String namJa;
		
		@SerializedName("id")
		private int id;
	}
	
	private static class JGeometry {
		
		@SerializedName("type")
		private String type;
		
		@SerializedName("coordinates")
		private ArrayList<JCoordinate> coordinates;
	}
	
	private static class JCoordinate {
		
		private ArrayList<JPoint> jpoints;
	}
	
	private static class JPoint {
		
		private double[] points = new double[2];
	}
}
