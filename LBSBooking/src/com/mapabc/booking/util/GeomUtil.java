package com.mapabc.booking.util;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with ChengLi
 * User: ChengLi
 * Date: 13-2-18
 * Time: 上午11:34
 * GEOM对象常用方法
 */
public class GeomUtil {
    /**
     * 由字符串生成点对象
     *
     * @param txt 格式为 POINT(121.123123 31.123123)
     * @return
     * @throws Exception
     */
    public static Point pointFromText(String txt) throws Exception {
        String tmp = txt;
        WKTReader fromText = new WKTReader();
        Point geom = (Point) fromText.read(tmp);
        geom.setSRID(4326);
        return geom;
    }
    
    public static String [] pointToText(String txt)throws Exception {
    	String values = txt.replace("POINT (", "").replace(")", "");
    	String latLon [] = values.split(" ");
    	return latLon;
    }
    
    public static String [] lineToText(String txt)throws Exception {
    	String values = txt.replace("LINESTRING (", "").replace(")", "").replace(", ", ";");
    	String latLon [] = values.split(";");
    	return latLon;
    }

    /**
     * Geom对象转成LngLat格式字符串
     *
     * @param geometry
     * @return
     * @throws Exception
     */
    public static String textFromGeom(Geometry geometry) throws Exception {
        String strPoint = geometry.toText();
        return strPoint.replaceAll("POINT", "").replaceAll("POLYGON", "").replaceAll("LINESTRING", "").trim()
                .replaceAll("\\(", "").replaceAll("\\)", "").replaceAll(", ", ";").replaceAll(",", ";")
                .replaceAll(" ", ",");
    }

    /**
     * 由字符串生成面对象
     *
     * @param txt 格式为 POLYGON ((121.123123 31.123123,121.123122 31.222111,......))
     * @return
     * @throws Exception
     */
    public static Polygon polygonFromText(String txt) throws Exception {
        WKTReader fromText = new WKTReader();
        Polygon geom = (Polygon) fromText.read(txt);
        geom.setSRID(4326);
        return geom;
    }

    /**
     * 由字符串生成线对象
     *
     * @param txt 格式为 LINESTRING(121.123123 21.123123,121.222333 21.222333,......)
     * @return
     * @throws Exception
     */
    public static LineString lineStringFromText(String txt) throws Exception {
        WKTReader fromText = new WKTReader();
        LineString geom = (LineString) fromText.read(txt);
        geom.setSRID(4326);
        return geom;
    }


    /**
     * 由JTS的GEOM对象字符串转换为LngLat格式字符串
     *
     * @param strGeom GEOM对象字符串
     * @return
     */
    public static String polygonGeom2lnglat(String strGeom) {
        return strGeom.replaceAll("POLYGON", "")
                .replaceAll("\\(\\(", "").replaceAll("\\)\\)", "").trim()
                .replaceAll(", ", ";").replaceAll(",", ";").replaceAll(" ", ",")+";";
    }

    /**
     * 由LngLat字符串生成Geom的Polygon字符串
     *
     * @param lnglat
     * @return
     */
    public static String lnglat2PolygonGeom(String lnglat) {
        String geom = lnglat.replaceAll(",", " ").replaceAll(";", ",");
        return "POLYGON ((" + (lnglat.endsWith(";") ? geom.substring(0, geom.length() - 1) :geom) + "))";
    }

    /**
     * 由LngLat字符串生成Geom的LineString字符串
     *
     * @param lnglat
     * @return
     */
    public static String lnglat2LinestringGeom(String lnglat) {
        String geom = lnglat.replaceAll(",", " ").replaceAll(";", ",");
        return "LINESTRING(" + geom.substring(0, geom.length() - 1) + ")";
    }
    
    /**
     * 由LngLat字符串生成Geom的Point字符串
     *
     * @param lnglat
     * @return
     */
    public static String lnglat2PointGeom(String lnglat) {
        String geom = lnglat.replaceAll(",", " ").replaceAll(";", ",");
        return "POINT(" + geom.substring(0, geom.length() - 1) + ")";
    }

    public static String linestringGeom2lnglat(String strGeom) {
        return strGeom.replaceAll("LINESTRING", "")
                .replaceAll("\\(", "").replaceAll("\\)", "").trim()
                .replaceAll(", ", ";").replaceAll(",", ";").replaceAll(" ", ",");
    }

    public static String pointGeom2lnglat(String strGeom) {
        return strGeom.replaceAll("POINT", "")
                .replaceAll("\\(", "").replaceAll("\\)", "").trim()
                .replaceAll(", ", ";").replaceAll(",", ";").replaceAll(" ", ",");
    }

    public static String geom2lngLat(String geom) {
        if (geom.startsWith("POLYGON")) {
            return polygonGeom2lnglat(geom);
        } else if (geom.startsWith("LINESTRING")) {
            return linestringGeom2lnglat(geom);
        } else if (geom.startsWith("POINT")) {
            return pointGeom2lnglat(geom);
        }
        return "";
    }

    /**
     * 由LngLat格式的面对象，获取两个外包多边形，供大图打印使用
     *
     * @param lnglats LngLat格式的面对象
     * @return 两个外包多边形
     * @throws Exception
     */
    public static List<String> getEnvelopeList(String lnglats) throws Exception {
        Polygon polygon = polygonFromText(lnglat2PolygonGeom(lnglats));
        Geometry geometry = polygon.getEnvelope().buffer(0.1d).getEnvelope();
        String envelopeStr = polygonGeom2lnglat(geometry.toString());
        String[] envelope = envelopeStr.split(";");
        String[] points = lnglats.split(";");
        List<String> pointList = new ArrayList<String>();
        Collections.addAll(pointList, points);
        Collections.sort(pointList, new PointSNComparator());
        String maxN = pointList.get(pointList.size() - 1);
        String maxS = pointList.get(0);
        Collections.sort(pointList, new PointEWComparator());
        String maxE = pointList.get(pointList.size() - 1);
        String maxW = pointList.get(0);

        if (maxE.equals(maxN) || maxE.equals(maxS)) {
            maxE = pointList.get(pointList.size() - 2);
        }
        if (maxW.equals(maxN) || maxW.equals(maxS)) {
            maxW = pointList.get(1);
        }

        String newPolygonE = maxN + ";" + envelope[2] + ";" + envelope[1] + ";" + envelope[0] + ";" + maxS + ";";
        String newPolygonW = maxN + ";" + envelope[2] + ";" + envelope[3] + ";" + envelope[0] + ";" + maxS + ";";
        int maxNIndex = -1;  // 北
        int maxSIndex = -1;  // 南
        int maxEIndex = -1;  // 东
        int maxWIndex = -1;  // 西
        for (int i = 0; i < points.length; i++) {
            String point = points[i];
            if (point.equals(maxN)) {
                maxNIndex = i;
            }
            if (point.equals(maxS)) {
                maxSIndex = i;
            }
            if (point.equals(maxE)) {
                maxEIndex = i;
            }
            if (point.equals(maxW)) {
                maxWIndex = i;
            }
        }

        if (maxNIndex < maxSIndex) {
            if (maxNIndex == maxEIndex || maxSIndex == maxEIndex) {
                if (maxNIndex < maxWIndex && maxWIndex < maxSIndex) {
                    for (int i = maxSIndex; i > maxNIndex; i--) {
                        newPolygonE += points[i] + ";";
                    }
                    for (int i = maxSIndex; i < points.length; i++) {
                        newPolygonW += points[i] + ";";
                    }
                    for (int i = 0; i <= maxNIndex; i++) {
                        newPolygonW += points[i] + ";";
                    }
                } else {
                    for (int i = maxSIndex; i > maxNIndex; i--) {
                        newPolygonW += points[i] + ";";
                    }
                    for (int i = maxSIndex; i < points.length; i++) {
                        newPolygonE += points[i] + ";";
                    }
                    for (int i = 0; i <= maxNIndex; i++) {
                        newPolygonE += points[i] + ";";
                    }
                }
            } else {
                if (maxNIndex < maxEIndex && maxEIndex < maxSIndex) {
                    for (int i = maxSIndex; i > maxNIndex; i--) {
                        newPolygonW += points[i] + ";";
                    }
                    for (int i = maxSIndex; i < points.length; i++) {
                        newPolygonE += points[i] + ";";
                    }
                    for (int i = 0; i <= maxNIndex; i++) {
                        newPolygonE += points[i] + ";";
                    }
                } else {
                    for (int i = maxSIndex; i > maxNIndex; i--) {
                        newPolygonE += points[i] + ";";
                    }
                    for (int i = maxSIndex; i < points.length; i++) {
                        newPolygonW += points[i] + ";";
                    }
                    for (int i = 0; i <= maxNIndex; i++) {
                        newPolygonW += points[i] + ";";
                    }
                }
            }
        } else {
            //S < N
            if (maxSIndex == maxEIndex || maxNIndex == maxEIndex) {
                if (maxSIndex < maxWIndex && maxWIndex < maxEIndex) {
                    for (int i = maxSIndex; i <= maxNIndex; i++) {
                        newPolygonE += points[i] + ";";
                    }
                    for (int i = maxSIndex; i > -1; i--) {
                        newPolygonW += points[i] + ";";
                    }
                    for (int i = points.length - 1; i >= maxNIndex; i--) {
                        newPolygonW += points[i] + ";";
                    }
                } else {
                    for (int i = maxSIndex; i < maxNIndex; i++) {
                        newPolygonW += points[i] + ";";
                    }
                    for (int i = maxSIndex; i > -1; i--) {
                        newPolygonE += points[i] + ";";
                    }

                    for (int i = points.length - 1; i >= maxNIndex; i--) {
                        newPolygonE += points[i] + ";";
                    }
                }
            } else {
                if (maxSIndex < maxEIndex && maxEIndex < maxNIndex) {
                    for (int i = maxSIndex; i <= maxNIndex; i++) {
                        newPolygonW += points[i] + ";";
                    }
                    for (int i = maxSIndex; i > -1; i--) {
                        newPolygonE += points[i] + ";";
                    }
                    for (int i = points.length - 1; i >= maxNIndex; i--) {
                        newPolygonE += points[i] + ";";
                    }
                } else {
                    for (int i = maxSIndex; i < maxNIndex; i++) {
                        newPolygonE += points[i] + ";";
                    }
                    for (int i = maxSIndex; i > -1; i--) {
                        newPolygonW += points[i] + ";";
                    }

                    for (int i = points.length - 1; i >= maxNIndex; i--) {
                        newPolygonW += points[i] + ";";
                    }

                }
            }

        }
        List<String> ret = new ArrayList<String>();
        ret.add(newPolygonE);
        ret.add(newPolygonW);
        return ret;
    }
}
