package com.mapabc.booking.util;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author Administrator
 *
 */

public class PolygonUtility {
    public static final double MIN_INCLUDED_ANGLE = 100.0;
    public static final double MAX_INCLUDED_ANGLE = 150.00;
    private Map<Coordinate,Integer>	g_mapNforks = new HashMap<Coordinate, Integer>();
    /**
     *
     */
    public PolygonUtility() {
        // TODO Auto-generated constructor stub
    }

    private void addToForksMap(Coordinate coord) {
        if(g_mapNforks.isEmpty()){
            g_mapNforks.put(coord, 1);
        }else{
            Iterator<Entry<Coordinate,Integer>> iter = g_mapNforks.entrySet().iterator();
            while(iter.hasNext()){
                Entry<Coordinate,Integer> entry = iter.next();
                Coordinate key = entry.getKey();
                if(coord.equals(key) == true){
                    g_mapNforks.put(key, entry.getValue()+1);
                    return;
                }
            }
            g_mapNforks.put(coord, 1);
        }

        return;
    }

    private void createForksMap(List<List<Coordinate>> lines) {
        if(lines == null || lines.size() == 0){
            return;
        }

        //循环查找并对岔路口做映射
        for(List<Coordinate> line : lines){
            addToForksMap(line.get(0));
            addToForksMap(line.get(line.size()-1));
        }

        //删除岔路口小于3的映射
        Iterator<Entry<Coordinate,Integer>> iter = g_mapNforks.entrySet().iterator();
        while(iter.hasNext()){
            Entry<Coordinate,Integer> entry = iter.next();
            Integer count = entry.getValue();
            if(count < 3){
                iter.remove();
                iter = g_mapNforks.entrySet().iterator();
            }
        }

        return;
    }

    private boolean head2tail(List<List<Coordinate>> lines,int procedure) throws Exception {
        boolean bfind = false;
        CoordUtility utility = new CoordUtility();
        for(int i=0;i<lines.size();i++){
            List<Coordinate> line1 = lines.get(i);
            Coordinate coord1 = line1.get(0);
            //岔路口判断
            if (g_mapNforks.containsKey(coord1) == false) {
                int index = -1;
                double maxLength = 0.0;
                double maxAngle = 0.0;
                for (int j = i+1; j < lines.size(); j++) {
                    List<Coordinate> line2 = lines.get(j);
                    Coordinate coord2 = line2.get(line2.size()-1);
                    //判断头尾节点是否相同
                    if (coord1.equals(coord2) == true) {
                        double length = utility.getDistance(line2);
                        double angle = utility.getStraightAngle(coord1,line1.get(1),line2.get(line2.size()-2));
                        if (procedure==0 && angle >= MIN_INCLUDED_ANGLE) {
                            index = j;
                            break;
                        }
                        else if (procedure == 1 && angle >= MAX_INCLUDED_ANGLE) {
                            if (angle >= maxAngle) {
                                index = j;
                                maxAngle = angle;
                            }
                        }
                        else if(procedure == 2 && angle >= MIN_INCLUDED_ANGLE) {
                            if (length >= maxLength) {
                                index = j;
                                maxLength = length;
                            }
                        }
                    }
                }

                if (index >= 0) {
                    bfind = true;
                    line1.remove(0);
                    line1.addAll(0, lines.get(index));
                    lines.remove(index);
                    --i;
                }
            }
        }

        return bfind;
    }

    private boolean tail2head(List<List<Coordinate>> lines,int procedure) throws Exception {
        boolean bfind = false;
        CoordUtility utility = new CoordUtility();
        for(int i=0;i<lines.size();i++){
            List<Coordinate> line1 = lines.get(i);
            Coordinate coord1 = line1.get(line1.size()-1);
            //岔路口判断
            if (g_mapNforks.containsKey(coord1) == false) {
                int index = -1;
                double maxLength = 0.0;
                double maxAngle = 0.0;
                for (int j = i+1; j < lines.size(); j++) {
                    List<Coordinate> line2 = lines.get(j);
                    Coordinate coord2 = line2.get(0);
                    //判断头尾节点是否相同
                    if (coord1.equals(coord2) == true) {
                        double length = utility.getDistance(line2);
                        double angle = utility.getStraightAngle(coord1,line1.get(line1.size()-2),line2.get(1));
                        if (procedure==0 && angle >= MIN_INCLUDED_ANGLE) {
                            index = j;
                            break;
                        }
                        else if (procedure == 1 && angle >= MAX_INCLUDED_ANGLE) {
                            if (angle >= maxAngle) {
                                index = j;
                                maxAngle = angle;
                            }
                        }
                        else if(procedure == 2 && angle >= MIN_INCLUDED_ANGLE) {
                            if (length >= maxLength) {
                                index = j;
                                maxLength = length;
                            }
                        }
                    }
                }

                if (index >= 0) {
                    bfind = true;
                    line1.remove(line1.size()-1);
                    line1.addAll(line1.size(), lines.get(index));
                    lines.remove(index);
                    --i;
                }
            }

        }

        return bfind;
    }

    private boolean head2head(List<List<Coordinate>> lines,int procedure) throws Exception {
        boolean bfind = false;
        CoordUtility utility = new CoordUtility();
        for(int i=0;i<lines.size();i++){
            List<Coordinate> line1 = lines.get(i);
            Coordinate coord1 = line1.get(0);
            //岔路口判断
            if (g_mapNforks.containsKey(coord1) == false) {
                int index = -1;
                double maxLength = 0.0;
                double maxAngle = 0.0;
                for (int j = i+1; j < lines.size(); j++) {
                    List<Coordinate> line2 = lines.get(j);
                    Coordinate coord2 = line2.get(0);
                    //判断头尾节点是否相同
                    if (coord1.equals(coord2) == true) {
                        double length = utility.getDistance(line2);
                        double angle = utility.getStraightAngle(coord1,line1.get(1),line2.get(1));
                        if (procedure==0 && angle >= MIN_INCLUDED_ANGLE) {
                            index = j;
                            break;
                        }
                        else if (procedure == 1 && angle >= MAX_INCLUDED_ANGLE) {
                            if (angle >= maxAngle) {
                                index = j;
                                maxAngle = angle;
                            }
                        }
                        else if(procedure == 2 && angle >= MIN_INCLUDED_ANGLE) {
                            if (length >= maxLength) {
                                index = j;
                                maxLength = length;
                            }
                        }
                    }
                }

                if (index >= 0) {
                    bfind = true;
                    Collections.reverse(lines.get(index));
                    line1.remove(0);
                    line1.addAll(0, lines.get(index));
                    lines.remove(index);
                    --i;
                }
            }

        }

        return bfind;
    }

    private boolean tail2tail(List<List<Coordinate>> lines,int procedure) throws Exception {
        boolean bfind = false;
        CoordUtility utility = new CoordUtility();
        for(int i=0;i<lines.size();i++){
            List<Coordinate> line1 = lines.get(i);
            Coordinate coord1 = line1.get(line1.size()-1);
            //岔路口判断
            if (g_mapNforks.containsKey(coord1) == false) {
                int index = -1;
                double maxLength = 0.0;
                double maxAngle = 0.0;
                for (int j = i+1; j < lines.size(); j++) {
                    List<Coordinate> line2 = lines.get(j);
                    Coordinate coord2 = line2.get(line2.size()-1);
                    //判断头尾节点是否相同
                    if (coord1.equals(coord2) == true) {
                        double length = utility.getDistance(line2);
                        double angle = utility.getStraightAngle(coord1,line1.get(line1.size()-2),line2.get(line2.size()-2));
                        if (procedure==0 && angle >= MIN_INCLUDED_ANGLE) {
                            index = j;
                            break;
                        }
                        else if (procedure == 1 && angle >= MAX_INCLUDED_ANGLE) {
                            if (angle >= maxAngle) {
                                index = j;
                                maxAngle = angle;
                            }
                        }
                        else if(procedure == 2 && angle >= MIN_INCLUDED_ANGLE) {
                            if (length >= maxLength) {
                                index = j;
                                maxLength = length;
                            }
                        }
                    }
                }

                if (index >= 0){
                    bfind = true;
                    Collections.reverse(lines.get(index));
                    line1.remove(line1.size()-1);
                    line1.addAll(line1.size(), lines.get(index));
                    lines.remove(index);
                    --i;
                }
            }
        }

        return bfind;
    }



    private int findpos(Geometry geo, Point point) {
        int num = geo.getNumGeometries();
        for (int i = 0; i < num; i++) {
            Geometry geometry = geo.getGeometryN(i);
            Coordinate[] coords = geometry.getCoordinates();
            int end = coords.length-1;
            if(coords[end].x == point.getX() && coords[end].y == point.getY()){
                return i;
            }
        }
        return -1;
    }

    private void filterInvalidGeometry(List<Geometry> geometries) {
        Map<Coordinate,Integer> hasMap = new HashMap<Coordinate, Integer>();
        for(Geometry geo : geometries){
            LineString ls = (LineString)geo;
            Point s = ls.getStartPoint();
            Point e = ls.getEndPoint();

            if (hasMap.containsKey(s.getCoordinate())) {
                hasMap.put(s.getCoordinate(), hasMap.get(s.getCoordinate()) + 1);
            } else {
                hasMap.put(s.getCoordinate(), 1);
            }

            if (hasMap.containsKey(e.getCoordinate())) {
                hasMap.put(e.getCoordinate(), hasMap.get(e.getCoordinate()) + 1);
            } else {
                hasMap.put(e.getCoordinate(), 1);
            }
        }

        for(int i = 0; i < geometries.size(); ){
            LineString ls = (LineString) geometries.get(i);
            Point s = ls.getStartPoint();
            Point e = ls.getEndPoint();
            if(hasMap.get(s.getCoordinate())<2 || hasMap.get(e.getCoordinate())<2){
                geometries.remove(i);
                i = 0;
            }
            else {
                i++;
            }
        }

        return;
    }

    interface InterOption{
        boolean checkGeometry(Geometry geo);
    }
    private InterOption interOption = new InterOption(){
        @Override
        public boolean checkGeometry(Geometry geo) {
            if (geo == null ||  geo.isEmpty() || !geo.isValid()) {
                return false;
            }

            return  true;
        }};

    private boolean intersection(List<Geometry> lstGeo1, List<Geometry> lstGeo2, InterOption opt) {
        int nsize = lstGeo1.size();
        for (int i = 0; i < lstGeo1.size(); i++) {
            LineString line1 = (LineString)lstGeo1.get(i);
            for (int j = 0; j < lstGeo2.size(); j++) {
                LineString line2 = (LineString)lstGeo2.get(j);
                Geometry inter = line1.intersection(line2);
                if(!opt.checkGeometry(inter)){
                    continue;
                }

                Geometry geo = line1.union(line2);
                int pos = findpos(geo, line1.getEndPoint());
                if (pos < 0) {
                    return false;
                }

                lstGeo1.clear();
                lstGeo2.clear();

                int num = geo.getNumGeometries();
                for (int k = 0; k <= pos; k++) {
                    if (nsize >= 2 && pos >= 1) {
                        if(i==0)
                        {
                            lstGeo1.add(geo.getGeometryN(++k));
                            break;
                        }
                        else if (i==(nsize-1))
                        {
                            lstGeo1.add(geo.getGeometryN(0));
                            break;
                        }
                    }
                    else {
                        lstGeo1.add(geo.getGeometryN(k));
                    }
                }

                for (int k = pos+1; k < num; k++) {
                    lstGeo2.add(geo.getGeometryN(k));
                }

                return true;
            }
        }

        return false;
    }

    public String checkIntersection(String line1, String line2) {
        if (line1 == null || line1.trim().length() <= 0
                ||line2 == null || line2.trim().length() <= 0) {
            return null;
        }

        CoordUtility utility = new CoordUtility();
        LineString ls1 = utility.string2LineString(line1);
        LineString ls2 = utility.string2LineString(line2);
        Geometry geo = ls1.intersection(ls2);
        if(geo == null|| geo.isEmpty() || !geo.isValid()){
            return null;
        }

        Coordinate[] coords = geo.getCoordinates();
        return utility.arrayCoord2String(coords);
    }

    private void sortByDistance(List<List<Coordinate>> lstList, final Point point) {

        final GeometryFactory geometryFactory = new GeometryFactory();
        Collections.sort(lstList, new Comparator<List<Coordinate>>(){
            @Override
            public int compare(List<Coordinate> l1, List<Coordinate> l2) {

                Coordinate[] coords1  = (Coordinate[])l1.toArray(new Coordinate[l1.size()]);
                LineString lineString1 = geometryFactory.createLineString(coords1);

                Coordinate[] coords2  = (Coordinate[])l2.toArray(new Coordinate[l2.size()]);
                LineString lineString2 = geometryFactory.createLineString(coords2);

                return (lineString1.distance(point)>lineString2.distance(point)?1:-1);
            }
        });

        return;
    }

    public List<String> genRoutePolyline(List<String> sections, String strPoint, boolean bDotSel) throws Exception{
        if (sections == null || sections.size() <= 0) {
            return null;
        }

        //按照距离排序
        String[] aryStrings = strPoint.split(",|;");
        if (aryStrings.length < 2) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coord = new Coordinate();
        coord.x = Double.valueOf(aryStrings[0]);
        coord.y = Double.valueOf(aryStrings[1]);
        Point geoPoint = geometryFactory.createPoint(coord);

        //格式转化
        CoordUtility utility = new CoordUtility();
        List<List<Coordinate>> lstList = utility.listString2DblistCoord(sections);
        if (lstList == null) {
            return null;
        }
        //创建交叉路口映射
        createForksMap(lstList);
        //排序
        sortByDistance(lstList,geoPoint);
        //路段拼接
        for (int i = 0; i < 3; i++) {
            while(true){
                if(!head2tail(lstList, i)
                        && !tail2head(lstList, i)
                        && !head2head(lstList, i)
                        && !tail2tail(lstList, i)){
                    break;
                }
            }
            sortByDistance(lstList,geoPoint);
            if (i == 0) {
                g_mapNforks.clear();
            }
        }

        List<String> lines = new ArrayList<String>();
        for (int j = 0; j < lstList.size(); j++) {
            if(bDotSel){
                List<Coordinate> lst = lstList.get(j);
                if (lst == null || lst.size() <= 0) {
                    continue;
                }
                Coordinate[] coords1  = (Coordinate[])lst.toArray(new Coordinate[lst.size()]);
                LineString lineString = geometryFactory.createLineString(coords1);
                double dis = lineString.distance(geoPoint);
                if (dis*111319.55 >= 10) {
                    continue;
                }
            }

            String strLine = utility.listCoord2String(lstList.get(j));
            if (strLine == null) {
                continue;
            }
            lines.add(strLine);
            if (lines.size() >= 2) {
                //break;
            }
        }

        return lines;
    }

    public List<String> genRoutePolygon(List<String> lines) {
        if (lines == null || lines.size() < 2) {
            return null;
        }

        CoordUtility 	utility = new CoordUtility();
        List<List<Geometry>> lstGeometrys = new ArrayList<List<Geometry>>();
        for (String line : lines) {
            List<Geometry> geos = new ArrayList<Geometry>();
            geos.add(utility.string2LineString(line));
            lstGeometrys.add(geos);
        }

        //线段相交处理
        int size = lstGeometrys.size();
        for (int i = 0; i < size-1; i++) {
            if(intersection(lstGeometrys.get(i), lstGeometrys.get(i+1),interOption) == false)
                return null;
        }
        if(size > 2 && intersection(lstGeometrys.get(size-1),lstGeometrys.get(0),interOption) == false)
            return null;
        //过滤无效线段
        List<Geometry> geometries = new ArrayList<Geometry>();
        for (int i = 0; i < size; i++) {
            geometries.addAll(lstGeometrys.get(i));
        }
        filterInvalidGeometry(geometries);

        //多变形分析
        Polygonizer polygoizer = new Polygonizer();
        polygoizer.add(geometries);
        @SuppressWarnings("unchecked")
        List<Polygon> ploygons = (List<Polygon>) polygoizer.getPolygons();
        if (ploygons == null || ploygons.size() <= 0) {
            return null;
        }

        List<String> listString = new ArrayList<String>();
        for(int i=0; i<ploygons.size(); i++){
            Polygon polygon = ploygons.get(i);
            Coordinate[] coords = polygon.getCoordinates();
            listString.add(utility.arrayCoord2String(coords));
        }

        return listString;
    }

    public List<String> splitPolygon(String polygon, String string) {
        if (string == null || string.length() <= 0
                || polygon == null || polygon.length() <= 0) {
            return null;
        }

        //拆分线段为一条线
        CoordUtility utility = new CoordUtility();
        LineString lineString1 = utility.string2LineString(polygon);
        LineString lineString2 = utility.string2LineString(string);
        if (lineString1 == null || lineString2 == null) {
            return null;
        }

        //判断是否存在交点及过滤无效线段
        List<Geometry> geometries1 = new ArrayList<Geometry>();
        List<Geometry> geometries2 = new ArrayList<Geometry>();
        geometries1.add(lineString1);
        geometries2.add(lineString2);
        InterOption option = new InterOption(){
            @Override
            public boolean checkGeometry(Geometry geo) {
                if (geo == null ||  geo.isEmpty() || !geo.isValid() || !(geo instanceof MultiPoint)) {
                    return false;
                }

                return  true;
            }};
        if (intersection(geometries1,geometries2,option) == false) {
            return null;
        }
        filterInvalidGeometry(geometries2);		//过滤分割线无效线段
        //去除不在多边形内的线段
		/*Polygon geoPloygon = utility.string2Polygon(polygon);
		for(int i = 0; i < geometries2.size(); ){
			LineString ls = (LineString) geometries2.get(i);
			if(ls.getCoordinates().length <= 2 || ls.within(geoPloygon)){
				i++;
			}
			else {
				geometries2.remove(i);
			}
		}*/

        if (geometries1.size() <= 0 || geometries2.size() <= 0) {
            return null;
        }

        //多边形分析处理
        List<String> lStrings = new ArrayList<String>();
        Polygonizer polygoizer = new Polygonizer();
        polygoizer.add(geometries1);
        polygoizer.add(geometries2);
        @SuppressWarnings("unchecked")
        List<Polygon> ploygons = (List<Polygon>) polygoizer.getPolygons();
        if (ploygons == null || ploygons.size() <= 0) {
            return null;
        }

        for(int j=0; j<ploygons.size(); j++){
            Polygon poly = ploygons.get(j);
            Coordinate[] coords = poly.getCoordinates();
            Point centroid = poly.getCentroid();
            String strPoly= utility.arrayCoord2String(coords);
            strPoly += "|" + centroid.getX() + "," + centroid.getY();
            lStrings.add(strPoly);
        }
        //System.out.println(lStrings);
        return lStrings;
    }

    /**
     * 判断多边形是否相交
     * @param string1
     * @param string2
     * @return 0：不相交  1：相切 2：相交 3：相交又相切
     */
    public int polygonRelation(String string1, String string2)
    {
        if (string1 == null || string2 == null) {
            return 0;
        }

        CoordUtility utility = new CoordUtility();
        Polygon polygon1 = utility.string2Polygon(string1);
        Polygon polygon2 = utility.string2Polygon(string2);
        if (polygon1 == null || polygon2 == null) {
            return 0;
        }

        int relation = 0;
        Geometry geometrys = polygon1.intersection(polygon2);
        if(geometrys == null || geometrys.isEmpty() || !geometrys.isValid()) {
            return 0;
        }
        else  {
            for (int i = 0; i < geometrys.getNumGeometries(); i++) {
                Geometry geometry = geometrys.getGeometryN(i);
                if (geometry instanceof LineString) {
                    relation |= 0x0001;
                }
                else if (geometry instanceof Polygon) {
                    relation |= 0x0002;
                }
            }

        }

        return relation;
    }


    public String mergePolygon(String string1, String string2, int mergeType) {
        if (string1 == null || string2 == null) {
            return null;
        }

        CoordUtility utility = new CoordUtility();
        Polygon polygon1 = utility.string2Polygon(string1);
        Polygon polygon2 = utility.string2Polygon(string2);
        if (polygon1 == null || polygon2 == null) {
            return null;
        }

        if(polygon1.intersects(polygon2) == true)
        {
            //两个相交多边形合并
            Geometry geometry = null;
            switch (mergeType) {
                case 0:
                    geometry = polygon1.union(polygon2);
                    break;
                case 1:
                    geometry = polygon1.difference(polygon2);
                    break;
                case 2:
                    geometry = polygon2.difference(polygon1);
                    break;
                default:
                    return null;
            }

            if (geometry != null && geometry.getNumGeometries() > 0) {
                Geometry polygon = geometry.getGeometryN(0); //合并的最大多边形
                Coordinate[] coords = polygon.getCoordinates();
                Point centroid = polygon.getCentroid();
                String strPoly= utility.arrayCoord2String(coords);
                strPoly += "|" + centroid.getX() + "," + centroid.getY();
                return strPoly;
            }
        }

        return null;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        PolygonUtility polygon = new PolygonUtility();
        List<String> lines = new ArrayList<String>();

        //生成道路
		/*lines.add("121.4691989,31.1944625;121.4696775,31.19471528");
		lines.add("121.4673975,31.19325028;121.4682078,31.19370361;121.4688867,31.19407639");
		lines.add("121.4649197,31.19208306;121.4650711,31.19223028;121.4655956,31.19253306");
		lines.add("121.4672658,31.19349;121.4672678,31.19349111;121.4681428,31.19398889");
		lines.add("121.4666281,31.19312972;121.4668736,31.19315833");
		lines.add("121.467315,31.19340083;121.4668736,31.19315833");
		lines.add("121.4655956,31.19253306;121.4665653,31.19309333;121.4666164,31.19312306;121.4666281,31.19312972");
		lines.add("121.4666281,31.19312972;121.4672658,31.19349");
		lines.add("121.4681428,31.19398889;121.4687086,31.19429556;121.4689689,31.19443667;121.4691989,31.1944625");
		lines.add("121.4650414,31.19191778;121.4651958,31.19201306;121.4652178,31.19202639");
		lines.add("121.46395,31.19135583;121.4642533,31.19159639;121.4644333,31.19173917;121.4649197,31.19208306");
		lines.add("121.4409508,31.17781361;121.4420128,31.17864278");
		lines.add("121.4409508,31.17781361;121.4411892,31.17772528");
		lines.add("121.4409878,31.17766944;121.4411892,31.17772528");
		lines.add("121.4409878,31.17766944;121.4409508,31.17781361");
		lines.add("121.4378769,31.17930333;121.4384306,31.17898278");
		lines.add("121.4384306,31.17898278;121.4391,31.17859667");
		lines.add("121.4409508,31.17781361;121.4409886,31.17794389;121.4419489,31.17870444");
		lines.add("121.4366983,31.17998722;121.4371586,31.17972028;121.4373619,31.17960222;121.4378769,31.17930333");
		lines.add("121.4419489,31.17870444;121.4421256,31.17883056;121.4427231,31.17927528");
		lines.add("121.4411892,31.17772528;121.4415006,31.17803306;121.4416567,31.17815167;121.4418636,31.17830917;121.4422033,31.17853472;121.4425122,31.17876556");
		lines.add("121.4433572,31.17940222;121.4443358,31.18013556;121.4447447,31.18044667");
		lines.add("121.4463075,31.1819375;121.4472994,31.18275444");
		lines.add("121.4427231,31.17927528;121.4446017,31.18062667;121.4446975,31.18065139");
		lines.add("121.4425122,31.17876556;121.4433572,31.17940222");
		lines.add("121.4525336,31.18600861;121.4529019,31.18610083;121.4543831,31.18648528;121.4551675,31.18668667");
		lines.add("121.4579319,31.18754778;121.45868,31.18798222");
		lines.add("121.45868,31.18798222;121.4591661,31.1882775");
		lines.add("121.4624997,31.19030111;121.4629339,31.19057361;121.4632503,31.19079944;121.4634556,31.19096389;121.46395,31.19135583");
		lines.add("121.4617069,31.18959194;121.4627297,31.19021");
		lines.add("121.4496792,31.18445028;121.4498567,31.18458111;121.4503056,31.18488583;121.45049,31.18499611;121.45074,31.18513444;121.4507669,31.18514806");
		lines.add("121.4564522,31.18679722;121.4565172,31.18681556");
		lines.add("121.4565172,31.18681556;121.4568961,31.18692278;121.4572239,31.18702389;121.4574033,31.18708528;121.4575297,31.18713639;121.4575803,31.18715861");
		lines.add("121.4564169,31.18705194;121.4563869,31.18704444;121.4563569,31.18703639");
		lines.add("121.4362019,31.1802975;121.4366983,31.17998722");
		lines.add("121.4601353,31.18886694;121.4601789,31.18889333");
		lines.add("121.4602742,31.18872306;121.4613411,31.18937083;121.4617069,31.18959194");
		lines.add("121.4599289,31.18851333;121.4602292,31.18869556");
		lines.add("121.4602292,31.18869556;121.4602742,31.18872306");
		lines.add("121.4400994,31.17813083;121.4409508,31.17781361");
		lines.add("121.4394058,31.178275;121.4395675,31.1782025");
		lines.add("121.4395675,31.1782025;121.4399311,31.17803972;121.4409878,31.17766944");
		lines.add("121.4464389,31.1817925;121.4474183,31.18259917");
		lines.add("121.4696775,31.19471528;121.4698914,31.19481889");
		lines.add("121.4698914,31.19481889;121.4699497,31.19484722");
		lines.add("121.4699883,31.19464667;121.4699375,31.19462139");
		lines.add("121.4699375,31.19462139;121.4697022,31.19450444");
		lines.add("121.4559883,31.18669028;121.4558847,31.18659194;121.4554936,31.18649361;121.4552833,31.18644");
		lines.add("121.4552833,31.18644;121.4537819,31.18605639;121.4528503,31.18580722;121.4520378,31.18557806;121.4516475,31.18545194;121.4515083,31.18548139");
		lines.add("121.4541097,31.18621944;121.4547136,31.18637472;121.4552594,31.18651417;121.4552628,31.186515;121.4555706,31.18659361;121.4559883,31.18669028");
		lines.add("121.4559883,31.18669028;121.4564522,31.18679722");
		lines.add("121.4361753,31.18012306;121.4365967,31.17988028");
		lines.add("121.4365967,31.17988028;121.4371725,31.17954889;121.4372447,31.17950722;121.4378131,31.1791825");
		lines.add("121.4378131,31.1791825;121.4383881,31.178855");
		lines.add("121.4383881,31.178855;121.4390464,31.17847972");
		lines.add("121.4390464,31.17847972;121.4394058,31.178275");
		lines.add("121.4391,31.17859667;121.4394617,31.17842083");
		lines.add("121.4394617,31.17842083;121.4399233,31.17819639;121.4400994,31.17813083");
		lines.add("121.4420128,31.17864278;121.4421981,31.17877444;121.4423817,31.17891194");
		lines.add("121.4423817,31.17891194;121.4427328,31.179175;121.4432314,31.17954722");
		lines.add("121.4432314,31.17954722;121.4432544,31.17956444;121.4446483,31.1805675;121.4446975,31.18065139");
		lines.add("121.4480606,31.1833875;121.4481053,31.18342472;121.4486336,31.18386167");
		lines.add("121.4506731,31.18530861;121.4507264,31.18533722;121.4509383,31.18544611;121.4511681,31.18554778;121.4513125,31.18560667;121.4514281,31.18565278");
		lines.add("121.4514281,31.18565278;121.4514911,31.1856775;121.4519078,31.18582861;121.4522678,31.18593722;121.4525336,31.18600861");
		lines.add("121.4515083,31.18548139;121.4516472,31.18553583;121.4520081,31.18566861;121.4523922,31.18577472;121.4525408,31.18581139;121.452575,31.18581972");
		lines.add("121.452575,31.18581972;121.4527386,31.18586028;121.4531117,31.18595778;121.4541097,31.18621944");
		lines.add("121.4551675,31.18668667;121.4555311,31.186785;121.4559206,31.18690361");
		lines.add("121.4559206,31.18690361;121.4563569,31.18703639");
		lines.add("121.4564169,31.18705194;121.4567136,31.18712222;121.4570897,31.18723028;121.4574503,31.18734944;121.4574897,31.18736333");
		lines.add("121.4574897,31.18736333;121.4575733,31.1873925;121.4577964,31.18748889;121.4579319,31.18754778");
		lines.add("121.4575803,31.18715861;121.4576444,31.18718694;121.4578011,31.18725472;121.4579839,31.187345;121.4580244,31.18736861");
		lines.add("121.4580244,31.18736861;121.4587964,31.18781972");
		lines.add("121.4587964,31.18781972;121.4592656,31.18810694");
		lines.add("121.4592656,31.18810694;121.4599289,31.18851333");
		lines.add("121.4598275,31.18867944;121.4601353,31.18886694");
		lines.add("121.4591661,31.1882775;121.4596736,31.18858583;121.4598275,31.18867944");
		lines.add("121.4601789,31.18889333;121.4611067,31.18945806;121.4616014,31.18975722");
		lines.add("121.4616014,31.18975722;121.4624997,31.19030111");
		lines.add("121.4627297,31.19021;121.4631106,31.19045778;121.4634131,31.19067139;121.4637008,31.19089333;121.4641072,31.19121861");
		lines.add("121.4641072,31.19121861;121.4642019,31.19129472;121.4643325,31.19139583;121.4645856,31.19159194;121.4648786,31.19180694;121.4650414,31.19191778");
		lines.add("121.4651347,31.19218639;121.4651197,31.19217778;121.4649197,31.19208306");
		lines.add("121.4660872,31.19252944;121.4664789,31.19274444;121.4669742,31.19301667");
		lines.add("121.4669742,31.19301667;121.4673975,31.19325028");
		lines.add("121.4691989,31.1944625;121.4690322,31.19433917;121.4689258,31.19428278;121.4688058,31.19421861");
		lines.add("121.4688058,31.19421861;121.4686689,31.19414528;121.4673186,31.19340306;121.467315,31.19340083");
		lines.add("121.4697022,31.19450444;121.4692997,31.19429278");
		lines.add("121.4692997,31.19429278;121.4688867,31.19407639");
		lines.add("121.4488853,31.18381361;121.4491964,31.18406722;121.4493194,31.18416861;121.4495058,31.1843225;121.4496792,31.18445028");
		lines.add("121.4507669,31.18514806;121.4507944,31.18516194;121.4508078,31.18516806;121.4513511,31.18541694;121.4515083,31.18548139");
		lines.add("121.4506731,31.18530861;121.4506478,31.18529389;121.4502761,31.18509333;121.4499475,31.18488639;121.4497994,31.18479139;121.4496542,31.18467972;121.4495294,31.18458333");
		lines.add("121.4446975,31.18065139;121.4449047,31.180815");
		lines.add("121.4449047,31.180815;121.4458961,31.18159861;121.4463075,31.1819375");
		lines.add("121.4447447,31.18044667;121.4450258,31.1806675");
		lines.add("121.4450258,31.1806675;121.4450717,31.18070361;121.4460044,31.18143333;121.4464389,31.1817925");
		lines.add("121.4673975,31.19325028;121.4675122,31.19323639;121.46821,31.19361444");
		lines.add("121.46821,31.19361444;121.4688556,31.19396389;121.4688867,31.19407639");
		lines.add("121.4472994,31.18275444;121.4473575,31.18280222;121.4476414,31.18303861");
		lines.add("121.4476414,31.18303861;121.4480606,31.1833875");
		lines.add("121.4474183,31.18259917;121.4476672,31.18280417;121.4477725,31.18289111");
		lines.add("121.4477725,31.18289111;121.4482133,31.18325694");
		lines.add("121.4486336,31.18386167;121.4487869,31.18398583;121.4493133,31.18441222;121.4494206,31.18449917;121.4495294,31.18458333");
		lines.add("121.4482133,31.18325694;121.4487694,31.18371528;121.4488222,31.18375889;121.4488853,31.18381361");
		lines.add("121.4652178,31.19202639;121.4653725,31.19212194;121.4658372,31.19238694");
		lines.add("121.4658372,31.19238694;121.4660872,31.19252944");
		lines.add("121.4668736,31.19315833;121.4664161,31.19290694;121.4657439,31.19252889");
		lines.add("121.4657439,31.19252889;121.4651347,31.19218639");

		try {
			List<String> lStrings = polygon.genRoutePolyline(lines,"121.4668736,31.19315833");
			for (int i = 0; i < lStrings.size(); i++) {
				//System.out.print(lStrings.get(i) + "\r");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		*/

        //生成多边形
		/*lines.add("121.492629,31.186964;121.492663,31.186882;121.492797,31.186451;121.493061,31.185208;121.493298,31.184137;121.493442,31.183506;121.493651,31.182590;121.493719,31.182289;121.493842,31.181711;121.493981,31.181093;121.493992,31.181035;121.494182,31.180194;121.494318,31.179624;121.494488,31.178828;121.494508,31.178723;121.494542,31.178550;121.494562,31.178457;121.494975,31.176620;121.495046,31.176288;121.495138,31.175825;121.495193,31.175555;121.495315,31.174899;121.495380,31.174360;121.495440,31.173898;121.495485,31.173550;121.495542,31.173193;121.495581,31.172976;121.495595,31.172898;121.495641,31.172654;121.495698,31.172377;121.495760,31.172163;121.495991,31.171424;121.496012,31.171348;121.496313,31.170346;121.496442,31.169920;121.496444,31.169913;121.496595,31.169401;121.496714,31.168928;121.496803,31.168442;121.496828,31.168244;121.496848,31.168057;121.497089,31.164805;121.497100,31.164638;121.497166,31.163540;121.497228,31.162932;121.497263,31.162637;121.497329,31.162253;121.497434,31.161811;121.497550,31.161407;121.497682,31.160991;121.497958,31.160271;121.498137,31.159782;121.498344,31.159270;121.498503,31.158950;121.498612,31.158739;121.498919,31.158263;121.499153,31.157946;121.499434,31.157636;121.500005,31.157008;121.500209,31.156802;121.500374,31.156636;121.500445,31.156562;121.500516,31.156483;121.500731,31.156228;121.500758,31.156192;121.500793,31.156141;121.500821,31.156094;121.500987,31.155802;121.501059,31.155668;121.501168,31.155487;121.501289,31.155269;121.501413,31.155047;121.501557,31.154765;121.501855,31.154213;121.501898,31.154138;121.502045,31.153870;121.502089,31.153795;121.502163,31.153697;121.502295,31.153522;121.502386,31.153420;121.502508,31.153278;121.502625,31.153158;121.502725,31.153063;121.502770,31.153020;121.504189,31.152008;121.504425,31.151844;121.505008,31.151395;121.505236,31.151163;121.505437,31.150914;121.505616,31.150636;121.505626,31.150621;121.505837,31.150230;121.505917,31.150055;121.506414,31.148974;121.506569,31.148632;121.506697,31.148354;121.506858,31.148033;121.507146,31.147637;121.507826,31.146702;121.508320,31.146081;121.509067,31.145141;121.509242,31.144924;121.510086,31.143861;121.510738,31.143024;121.510876,31.142848;121.511101,31.142563;121.511991,31.141437;121.512383,31.140840;121.512941,31.139968;121.513271,31.139441;121.513458,31.139138;121.514002,31.138270;121.514027,31.138233;121.514058,31.138191;121.514065,31.138181;121.514731,31.137097;121.515181,31.136376;121.515201,31.136345;121.515388,31.136051;121.515766,31.135451;121.516109,31.134920;121.516489,31.134629;121.516585,31.134540;121.516711,31.134416;121.516765,31.134359;121.516794,31.134321;121.516830,31.134278;121.516854,31.134240;121.516871,31.134209;121.516886,31.134175;121.516891,31.134164;121.516906,31.134127;121.516925,31.134074;121.516975,31.133889;121.517158,31.133183;121.517240,31.132873;121.517285,31.132701;121.517300,31.132646;121.517318,31.132588;121.517332,31.132549;121.517347,31.132512;121.517375,31.132449;121.517416,31.132380;121.517489,31.132268;121.517555,31.132172;121.517840,31.131777;121.517974,31.131584;121.518291,31.131130;121.518949,31.130248;121.519146,31.129975;121.519262,31.129827;121.519401,31.129638;121.519556,31.129420;121.519713,31.129194;121.519768,31.129113;121.519808,31.129056;121.519917,31.128880;121.520069,31.128610;121.520174,31.128432;121.520423,31.128001;121.520537,31.127795;121.520606,31.127678;121.520647,31.127614;121.520695,31.127539;121.520762,31.127441;121.520821,31.127353;121.520845,31.127318;121.520873,31.127281;121.520900,31.127249;121.520943,31.127205;121.520986,31.127159;121.521023,31.127126;121.521065,31.127090;121.521121,31.127052;121.521156,31.127029;121.521201,31.127004;121.521296,31.126956;121.521392,31.126924;121.521487,31.126898;121.521676,31.126851;121.521835,31.126813;121.521990,31.126778;121.522061,31.126764;121.522174,31.126742;121.522355,31.126711;121.522516,31.126684;121.522758,31.126643;121.523003,31.126597;121.523172,31.126567;121.523462,31.126515;121.523634,31.126481;121.523799,31.126451;121.523936,31.126421;121.524081,31.126396;121.524247,31.126376;121.524325,31.126370;121.524415,31.126358;121.524611,31.126342;121.524746,31.126342;121.524898,31.126345;121.525095,31.126338;121.525305,31.126330;121.525604,31.126315;121.525916,31.126301;121.525994,31.126297;121.526431,31.126283;121.526583,31.126278;121.526708,31.126273;121.526772,31.126269;121.527184,31.126240;121.527474,31.126226;121.527561,31.126221;121.527610,31.126218;121.527646,31.126216;121.527648,31.126215;121.527689,31.126211;121.527725,31.126206;121.527763,31.126201;121.527798,31.126194;121.527938,31.126165;121.528061,31.126138;121.528086,31.126132;121.528177,31.126111;121.528281,31.126082;121.528389,31.126050;121.528492,31.126014;121.528613,31.125968;121.528703,31.125929;121.528933,31.125826;121.529081,31.125762;121.529347,31.125643;121.529395,31.125624;121.529443,31.125605;121.529544,31.125567;121.529620,31.125547;121.529706,31.125526;121.529787,31.125507;121.529869,31.125490;121.529968,31.125479;121.530061,31.125468;121.530303,31.125443;121.530341,31.125439;121.530727,31.125399;121.530959,31.125378;121.531057,31.125367;121.531152,31.125353;121.531313,31.125330;121.531445,31.125314;121.531587,31.125297;121.531717,31.125280;121.531863,31.125265;121.531924,31.125257;121.531980,31.125246;121.532038,31.125232;121.532084,31.125216;121.532143,31.125193;121.532143,31.125193;121.532199,31.125166;121.532251,31.125136;121.532349,31.125074;121.532385,31.125048;121.532528,31.124945;121.532669,31.124836;121.532825,31.124710;121.533083,31.124507;121.533302,31.124339;121.533484,31.124197;121.533673,31.124056;121.533745,31.124004;121.533834,31.123938;121.533884,31.123896;121.533936,31.123858;121.533950,31.123848;121.534043,31.123782;121.534166,31.123709;121.534230,31.123674;121.534345,31.123625;121.534436,31.123584;121.534545,31.123536;121.534640,31.123494;121.534759,31.123442;121.534878,31.123388;121.535013,31.123333;121.535181,31.123261;121.535384,31.123173;121.535685,31.123043;121.535754,31.123013;121.535943,31.122930;121.535969,31.122919;121.536876,31.122507;121.537492,31.122244;121.538019,31.121955;121.538138,31.121891;121.538207,31.121853;121.539268,31.121281;121.539543,31.121145;121.540227,31.120805;121.541278,31.120260;121.542766,31.119492;121.543132,31.119286;121.543195,31.119255;121.543262,31.119225;121.543337,31.119199;121.543443,31.119169;121.543537,31.119145;121.543641,31.119115;121.543762,31.119094");
		lines.add("121.548198,31.179886;121.548132,31.179884;121.547566,31.179865;121.546821,31.179831;121.546231,31.179816;121.545473,31.179791;121.544612,31.179757;121.544035,31.179736;121.542879,31.179696;121.542482,31.179690;121.542128,31.179669;121.541968,31.179648;121.541545,31.179582;121.540920,31.179465;121.540918,31.179465;121.540440,31.179384;121.540291,31.179358;121.539821,31.179278;121.539518,31.179226;121.538750,31.179089;121.538393,31.179020;121.538339,31.179009;121.537340,31.178841;121.536489,31.178690;121.535901,31.178581;121.535475,31.178503;121.535216,31.178458;121.535120,31.178442;121.534687,31.178364;121.533799,31.178222;121.533777,31.178218;121.533680,31.178201;121.533649,31.178194;121.533436,31.178152;121.532893,31.178061;121.532477,31.177986;121.530466,31.177623;121.530293,31.177588;121.530087,31.177538;121.529950,31.177506;121.529856,31.177486;121.529583,31.177420;121.529363,31.177355;121.529350,31.177351;121.528983,31.177236;121.528567,31.177087;121.528363,31.177013;121.528120,31.176919;121.527725,31.176781;121.526831,31.176462;121.526583,31.176370;121.526446,31.176320;121.525912,31.176132;121.525517,31.176017;121.525487,31.176009;121.525225,31.175933;121.524686,31.175803;121.524049,31.175650;121.523367,31.175465;121.522828,31.175319;121.522377,31.175198;121.522351,31.175191;121.522316,31.175183;121.522296,31.175177;121.522027,31.175105;121.521590,31.174989;121.520632,31.174708;121.519862,31.174496;121.519831,31.174488;121.519412,31.174378;121.519295,31.174352;121.519186,31.174333;121.518848,31.174263;121.518679,31.174222;121.518200,31.174076;121.517860,31.173972;121.517311,31.173790;121.516960,31.173676;121.516452,31.173519;121.515876,31.173350;121.515694,31.173308;121.515237,31.173205;121.515130,31.173181;121.514667,31.173124;121.514402,31.173096;121.514128,31.173095;121.513140,31.173005;121.512086,31.172905;121.510772,31.172780;121.510680,31.172770;121.509747,31.172688;121.509721,31.172686;121.508981,31.172618;121.508131,31.172538;121.506736,31.172408;121.505122,31.172269;121.504426,31.172177;121.503410,31.172099;121.503402,31.172098;121.502597,31.172015;121.501950,31.171956;121.500386,31.171813;121.499065,31.171693;121.497880,31.171586;121.497620,31.171563;121.497225,31.171530;121.496106,31.171434;121.495991,31.171424;121.495250,31.171366;121.494226,31.171286;121.493963,31.171265;121.493253,31.171195;121.492745,31.171147;121.491873,31.171065;121.491423,31.171022;121.490731,31.170953;121.489916,31.170836;121.489757,31.170808;121.488883,31.170658;121.488792,31.170630;121.488631,31.170594;121.487151,31.170351;121.486958,31.170320;121.485924,31.170142;121.485643,31.170094;121.483870,31.169783;121.483809,31.169772");
		lines.add("121.501189,31.133071;121.501004,31.133530;121.500973,31.133611;121.500936,31.133711;121.500903,31.133803;121.500878,31.133888;121.500845,31.133988;121.500818,31.134076;121.500792,31.134178;121.500783,31.134212;121.500756,31.134336;121.500690,31.134644;121.500653,31.134805;121.500634,31.134868;121.500618,31.134920;121.500599,31.134973;121.500581,31.135019;121.500558,31.135077;121.500535,31.135128;121.500502,31.135195;121.500457,31.135278;121.500421,31.135335;121.500384,31.135391;121.500355,31.135429;121.500308,31.135493;121.500273,31.135538;121.500238,31.135578;121.500202,31.135623;121.500174,31.135653;121.500033,31.135800;121.499936,31.135892;121.499473,31.136331;121.499142,31.136647;121.498811,31.136957;121.498518,31.137239;121.498012,31.137711;121.497853,31.137864;121.497818,31.137898;121.497798,31.137918;121.497779,31.137951;121.497776,31.137995;121.497783,31.138028;121.497803,31.138071;121.497799,31.138226;121.497793,31.138258;121.497787,31.138290;121.497229,31.139732;121.496957,31.140423;121.496863,31.140661;121.496634,31.141244;121.496284,31.142126;121.496235,31.142268;121.496171,31.142460;121.495693,31.143686;121.495192,31.144991;121.494902,31.145746;121.494829,31.145949;121.494755,31.146159;121.494616,31.146491;121.494525,31.146751;121.494502,31.146809;121.494445,31.147047;121.494346,31.147365;121.494224,31.147691;121.494113,31.147991;121.493940,31.148380;121.493750,31.148720;121.493705,31.148800;121.493262,31.149534;121.493166,31.149701;121.493069,31.149866;121.492789,31.150343;121.492651,31.150591;121.492624,31.150644;121.492378,31.151076;121.492301,31.151224;121.492270,31.151294;121.492238,31.151367;121.492139,31.151626;121.491990,31.152013;121.491958,31.152118;121.491916,31.152232;121.491682,31.152863;121.491491,31.153375;121.491490,31.153379;121.491339,31.153792;121.491248,31.154037;121.491148,31.154307;121.491081,31.154530;121.491053,31.154625;121.491020,31.154783;121.490991,31.154911;121.490979,31.155029;121.490955,31.155354;121.490922,31.155948;121.490895,31.156415;121.490881,31.156619;121.490866,31.156849;121.490842,31.157261;121.490835,31.157390;121.490806,31.157837;121.490790,31.158092;121.490783,31.158254;121.490763,31.158641;121.490721,31.159280;121.490747,31.159970;121.490773,31.160483;121.490808,31.161015;121.490836,31.161815;121.490838,31.161865;121.490854,31.162507;121.490858,31.162677;121.490854,31.162809;121.490835,31.163116;121.490835,31.163119;121.490807,31.163385;121.490773,31.163570;121.490740,31.163717;121.490685,31.163908;121.490616,31.164096;121.490388,31.164658;121.490280,31.164906;121.490098,31.165321;121.490016,31.165618;121.489820,31.166447;121.489593,31.167552;121.489272,31.168745;121.489271,31.168748;121.489158,31.169196;121.489061,31.169579;121.488900,31.170581;121.488883,31.170658;121.488672,31.171521;121.488660,31.171572;121.488594,31.171837;121.488490,31.172256;121.488414,31.172586;121.488310,31.173009;121.488204,31.173339;121.487982,31.173959;121.487778,31.174554;121.487610,31.175040;121.487448,31.175350;121.487375,31.175464");
		lines.add("121.539776,31.165071;121.539223,31.164912;121.539068,31.164865;121.539009,31.164847;121.538817,31.164783;121.538620,31.164715;121.538409,31.164625;121.538188,31.164520;121.537564,31.164225;121.536288,31.163622;121.535821,31.163432;121.535230,31.163193;121.533800,31.162706;121.533606,31.162645;121.531805,31.162075;121.531742,31.162056;121.529688,31.161209;121.529043,31.160978;121.528202,31.160713;121.526893,31.160407;121.525630,31.160133;121.525216,31.160043;121.524552,31.159897;121.524070,31.159791;121.523922,31.159763;121.523783,31.159736;121.523676,31.159715;121.523618,31.159700;121.523571,31.159686;121.523406,31.159631;121.522333,31.159414;121.521073,31.159128;121.518255,31.158479;121.517382,31.158257;121.516619,31.158071;121.516262,31.157985;121.516199,31.157970;121.511573,31.156916;121.511350,31.156866;121.510970,31.156780;121.510509,31.156677;121.508766,31.156287;121.508323,31.156170;121.507224,31.155857;121.505225,31.155241;121.504425,31.154936;121.504091,31.154845;121.503020,31.154545;121.502875,31.154504;121.502623,31.154433;121.502622,31.154433;121.502035,31.154265;121.501855,31.154213;121.501218,31.154010;121.500469,31.153774;121.500222,31.153696;121.498517,31.153172;121.497452,31.152857;121.497123,31.152758;121.496410,31.152545;121.494455,31.151954;121.493896,31.151785;121.492270,31.151294;121.492211,31.151240");
		lines.add("121.492629,31.186964;121.492663,31.186882;121.492797,31.186451;121.493061,31.185208;121.493298,31.184137;121.493442,31.183506;121.493651,31.182590;121.493719,31.182289;121.493842,31.181711;121.493981,31.181093;121.493992,31.181035;121.494182,31.180194;121.494318,31.179624;121.494488,31.178828;121.494508,31.178723;121.494542,31.178550;121.494562,31.178457;121.494975,31.176620;121.495046,31.176288;121.495138,31.175825;121.495193,31.175555;121.495315,31.174899;121.495380,31.174360;121.495440,31.173898;121.495485,31.173550;121.495542,31.173193;121.495581,31.172976;121.495595,31.172898;121.495641,31.172654;121.495698,31.172377;121.495760,31.172163;121.495991,31.171424;121.496012,31.171348;121.496313,31.170346;121.496442,31.169920;121.496444,31.169913;121.496595,31.169401;121.496714,31.168928;121.496803,31.168442;121.496828,31.168244;121.496848,31.168057;121.497089,31.164805;121.497100,31.164638;121.497166,31.163540;121.497228,31.162932;121.497263,31.162637;121.497329,31.162253;121.497434,31.161811;121.497550,31.161407;121.497682,31.160991;121.497958,31.160271;121.498137,31.159782;121.498344,31.159270;121.498503,31.158950;121.498612,31.158739;121.498919,31.158263;121.499153,31.157946;121.499434,31.157636;121.500005,31.157008;121.500209,31.156802;121.500374,31.156636;121.500445,31.156562;121.500516,31.156483;121.500731,31.156228;121.500758,31.156192;121.500793,31.156141;121.500821,31.156094;121.500987,31.155802;121.501059,31.155668;121.501168,31.155487;121.501289,31.155269;121.501413,31.155047;121.501557,31.154765;121.501855,31.154213;121.501898,31.154138;121.502045,31.153870;121.502089,31.153795;121.502163,31.153697;121.502295,31.153522;121.502386,31.153420;121.502508,31.153278;121.502625,31.153158;121.502725,31.153063;121.502770,31.153020;121.504189,31.152008;121.504425,31.151844;121.505008,31.151395;121.505236,31.151163;121.505437,31.150914;121.505616,31.150636;121.505626,31.150621;121.505837,31.150230;121.505917,31.150055;121.506414,31.148974;121.506569,31.148632;121.506697,31.148354;121.506858,31.148033;121.507146,31.147637;121.507826,31.146702;121.508320,31.146081;121.509067,31.145141;121.509242,31.144924;121.510086,31.143861;121.510738,31.143024;121.510876,31.142848;121.511101,31.142563;121.511991,31.141437;121.512383,31.140840;121.512941,31.139968;121.513271,31.139441;121.513458,31.139138;121.514002,31.138270;121.514027,31.138233;121.514058,31.138191;121.514065,31.138181;121.514731,31.137097;121.515181,31.136376;121.515201,31.136345;121.515388,31.136051;121.515766,31.135451;121.516109,31.134920;121.516489,31.134629;121.516585,31.134540;121.516711,31.134416;121.516765,31.134359;121.516794,31.134321;121.516830,31.134278;121.516854,31.134240;121.516871,31.134209;121.516886,31.134175;121.516891,31.134164;121.516906,31.134127;121.516925,31.134074;121.516975,31.133889;121.517158,31.133183;121.517240,31.132873;121.517285,31.132701;121.517300,31.132646;121.517318,31.132588;121.517332,31.132549;121.517347,31.132512;121.517375,31.132449;121.517416,31.132380;121.517489,31.132268;121.517555,31.132172;121.517840,31.131777;121.517974,31.131584;121.518291,31.131130;121.518949,31.130248;121.519146,31.129975;121.519262,31.129827;121.519401,31.129638;121.519556,31.129420;121.519713,31.129194;121.519768,31.129113;121.519808,31.129056;121.519917,31.128880;121.520069,31.128610;121.520174,31.128432;121.520423,31.128001;121.520537,31.127795;121.520606,31.127678;121.520647,31.127614;121.520695,31.127539;121.520762,31.127441;121.520821,31.127353;121.520845,31.127318;121.520873,31.127281;121.520900,31.127249;121.520943,31.127205;121.520986,31.127159;121.521023,31.127126;121.521065,31.127090;121.521121,31.127052;121.521156,31.127029;121.521201,31.127004;121.521296,31.126956;121.521392,31.126924;121.521487,31.126898;121.521676,31.126851;121.521835,31.126813;121.521990,31.126778;121.522061,31.126764;121.522174,31.126742;121.522355,31.126711;121.522516,31.126684;121.522758,31.126643;121.523003,31.126597;121.523172,31.126567;121.523462,31.126515;121.523634,31.126481;121.523799,31.126451;121.523936,31.126421;121.524081,31.126396;121.524247,31.126376;121.524325,31.126370;121.524415,31.126358;121.524611,31.126342;121.524746,31.126342;121.524898,31.126345;121.525095,31.126338;121.525305,31.126330;121.525604,31.126315;121.525916,31.126301;121.525994,31.126297;121.526431,31.126283;121.526583,31.126278;121.526708,31.126273;121.526772,31.126269;121.527184,31.126240;121.527474,31.126226;121.527561,31.126221;121.527610,31.126218;121.527646,31.126216;121.527648,31.126215;121.527689,31.126211;121.527725,31.126206;121.527763,31.126201;121.527798,31.126194;121.527938,31.126165;121.528061,31.126138;121.528086,31.126132;121.528177,31.126111;121.528281,31.126082;121.528389,31.126050;121.528492,31.126014;121.528613,31.125968;121.528703,31.125929;121.528933,31.125826;121.529081,31.125762;121.529347,31.125643;121.529395,31.125624;121.529443,31.125605;121.529544,31.125567;121.529620,31.125547;121.529706,31.125526;121.529787,31.125507;121.529869,31.125490;121.529968,31.125479;121.530061,31.125468;121.530303,31.125443;121.530341,31.125439;121.530727,31.125399;121.530959,31.125378;121.531057,31.125367;121.531152,31.125353;121.531313,31.125330;121.531445,31.125314;121.531587,31.125297;121.531717,31.125280;121.531863,31.125265;121.531924,31.125257;121.531980,31.125246;121.532038,31.125232;121.532084,31.125216;121.532143,31.125193;121.532143,31.125193;121.532199,31.125166;121.532251,31.125136;121.532349,31.125074;121.532385,31.125048;121.532528,31.124945;121.532669,31.124836;121.532825,31.124710;121.533083,31.124507;121.533302,31.124339;121.533484,31.124197;121.533673,31.124056;121.533745,31.124004;121.533834,31.123938;121.533884,31.123896;121.533936,31.123858;121.533950,31.123848;121.534043,31.123782;121.534166,31.123709;121.534230,31.123674;121.534345,31.123625;121.534436,31.123584;121.534545,31.123536;121.534640,31.123494;121.534759,31.123442;121.534878,31.123388;121.535013,31.123333;121.535181,31.123261;121.535384,31.123173;121.535685,31.123043;121.535754,31.123013;121.535943,31.122930;121.535969,31.122919;121.536876,31.122507;121.537492,31.122244;121.538019,31.121955;121.538138,31.121891;121.538207,31.121853;121.539268,31.121281;121.539543,31.121145;121.540227,31.120805;121.541278,31.120260;121.542766,31.119492;121.543132,31.119286;121.543195,31.119255;121.543262,31.119225;121.543337,31.119199;121.543443,31.119169;121.543537,31.119145;121.543641,31.119115;121.543762,31.119094");

		try {
			List<String> lStrings = polygon.genRoutePolygon(lines);
			for (int i = 0; i < lStrings.size(); i++) {
				System.out.print(lStrings.get(i) + "\r");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}*/

        //多边形拆分
		/*String strPolygon = "121.42900106,31.18357707;121.42823282,31.18392361;121.42837067,31.18698971;121.43183771,31.18829253;121.43317506,31.18744691;121.43333974,31.18568589;121.43313254,31.18464447;121.43213811,31.18480475;121.42900106,31.18357707;121.42900106,31.18357707";
		String strLine = "121.433976,31.192581;121.434149,31.19211;121.434194,31.191935;121.434207,31.191853;121.434221,31.191669;121.434221,31.191482;121.434185,31.191074;121.434131,31.190638;121.434065,31.190003;121.433871,31.189077;121.433784,31.188801;121.433462,31.188073;121.433286,31.187721;121.432464,31.18616;121.432091,31.185441;121.431896,31.185063;121.431258,31.183838;121.431019,31.183418";
		List<String> polysList = polygon.splitPolygon(strPolygon, strLine);
		for (int i = 0; i < polysList.size(); i++) {
			System.out.print(polysList.get(i) + "\r");
		}*/

        //多边形合并测试
        //String ploy1 = "116.34171497,39.96799593;116.3372001,39.96860298;116.33694956,39.96865866;116.33934393,39.97250028;116.35181084,39.97259894;116.34483903,39.96814252;116.34171497,39.96799593";
        //String ploy2 = "116.34171497,39.96799593;116.3372001,39.96860298;116.33694956,39.96865866;116.34099317,39.97481085;116.34440987,39.97005013;116.34483903,39.96814252;116.34171497,39.96799593";
        //String str = polygon.mergePolygon(ploy1, ploy2,0);
        //System.out.print(str);
    }

}
