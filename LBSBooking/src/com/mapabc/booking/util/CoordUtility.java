/**
 * 
 */
package com.mapabc.booking.util;

import com.vividsolutions.jts.geom.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 */
public class CoordUtility {

	private static final double PI = 3.14159265;
	/**
	 * 
	 */
	public CoordUtility() {
		// TODO Auto-generated constructor stub
	}
	
	private List<Coordinate> string2ListCoord(String strLine) {
		if (strLine == null || strLine.trim().length() <= 0) {
			return null;
		}
		
		List<Coordinate> lst = new ArrayList<Coordinate>();
		String[] aryStrings = strLine.split(",|;");
		if (aryStrings.length < 2) {
			return null;
		}
		
		for (int i = 0; i < aryStrings.length; i+=2) {
			Coordinate coord = new Coordinate();
			coord.x = Double.valueOf(aryStrings[i]);
			coord.y = Double.valueOf(aryStrings[i+1]);
			lst.add(coord);
		}
		
		if (lst.size() <= 0) {
			return null;
		}

		return lst;
	}
	
	
	public LineString string2LineString(String strLine) {
		List<Coordinate> lstCoord = string2ListCoord(strLine);
		if (lstCoord == null || lstCoord.size() <= 0) {
			return null;
		}
		
		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate[] coords  = (Coordinate[])lstCoord.toArray(new Coordinate[lstCoord.size()]);
		LineString line = geometryFactory.createLineString(coords);
		return line;
	}
	
	public LinearRing string2LinearRing(String strLine) {
		List<Coordinate> lstCoord = string2ListCoord(strLine);
		if (lstCoord == null || lstCoord.size() <= 0) {
			return null;
		}
		
		GeometryFactory geometryFactory = new GeometryFactory();
		Coordinate[] coords  = (Coordinate[])lstCoord.toArray(new Coordinate[lstCoord.size()]);
		LinearRing line = geometryFactory.createLinearRing(coords);
		return line;
	}
	
	public Polygon string2Polygon(String strLine) {
		LinearRing linearRing = string2LinearRing(strLine);
		if (linearRing == null) {
			return null;
		}
		
		GeometryFactory geometryFactory = new GeometryFactory();
		Polygon polygon = geometryFactory.createPolygon(linearRing, null);
		return polygon;
	}
	
	public List<List<Coordinate>> listString2DblistCoord(List<String> lineList) {
		if (lineList == null || lineList.size() <= 0) {
			return null;
		}
		
		List<List<Coordinate>> dbListCoord = new ArrayList<List<Coordinate>>();
		for(String line : lineList){
			List<Coordinate> lstCoord = string2ListCoord(line);
			if (lstCoord != null && lstCoord.size() > 0) {
				dbListCoord.add(lstCoord);
			}
		}
		
		if (dbListCoord.size() <= 0) {
			return null;
		}
		
		return dbListCoord;
	}

	public String listCoord2String(List<Coordinate> coords) {
		if (coords == null || coords.size() <= 0) {
			return null;
		}
		
		StringBuffer strCoords = new StringBuffer();
		for(Coordinate coord : coords){
			strCoords.append(coord.x+","+coord.y+";");
		}
		
		if (strCoords.length() > 0) {
			strCoords.deleteCharAt(strCoords.length()-1);
		}
		return strCoords.toString();
	}
	
	public String arrayCoord2String(Coordinate[] coords) {
		if (coords == null || coords.length <= 0) {
			return null;
		}
		
		StringBuffer strCoords = new StringBuffer();
		for(Coordinate coord : coords){
			strCoords.append(coord.x+","+coord.y+";");
		}
		
		if (strCoords.length() > 0) {
			strCoords.deleteCharAt(strCoords.length()-1);
		}
		
		return strCoords.toString();
	}
	
	public double getStraightAngle(Coordinate coord, Coordinate coord1, Coordinate coord2) {
		double cosfi,fi,norm;    
	    double dsx = coord1.x - coord.x;    
	    double dsy = coord1.y - coord.y;    
	    double dex = coord2.x - coord.x;    
	    double dey = coord2.y - coord.y;    
	   
	    cosfi = dsx*dex+dsy*dey;    
	    norm = (dsx*dsx+dsy*dsy)*(dex*dex+dey*dey);    
	    cosfi /= Math.sqrt(norm);    
	   
	    if (cosfi >=  1.0 ) 
	    	return 0.0;    
	    if (cosfi <= -1.0 ) 
	    	return 180.0;    
	   
	    fi = Math.acos(cosfi);    
	    return Math.abs(fi*180.0/PI);    
	}
	
	public double getDistance(List<Coordinate> lCoordinates) {
		double distance = 0.0;
		GeometryFactory geometryFactory = new GeometryFactory();
		for (int i = 0; i < lCoordinates.size()-1; i++) {
			Point p1 = geometryFactory.createPoint(lCoordinates.get(i));
	        Point p2 = geometryFactory.createPoint(lCoordinates.get(i+1));
	        distance += p1.distance(p2);
		}
        
        return distance;
	}
	
	
}
