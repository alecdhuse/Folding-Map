/* 
 * Copyright (C) 2014 Alec Dhuse
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.foldingmap.map.vector;

import co.foldingmap.Logger;
import co.foldingmap.map.MapView;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * A bunch of Static methods used for various calculations.
 * 
 * @author Alec
 */
public class CoordinateMath {
    
    /**
     * Calculates the bearing in degrease from 0 to 360
     *
     * @param c1        The coordinate from which to find the Azimuth to c2
     * @param c2        The coordinate the calculate the Azimuth to.
     *
     * @return float   The Azimuth in degrees from (0-360)
     */
    public static float calculateAzimuth(Coordinate c1, Coordinate c2) {
        double azimuthDegrees, azimuthRadians;
        float  x, y;
        double lat1 = c1.getLatitudeInRadians();
        double lon1 = c1.getLongitudeInRadians();
        double lat2 = c2.getLatitudeInRadians();
        double lon2 = c2.getLongitudeInRadians();

        if (lon1 == lon2) {
            return lat1 > lat2 ? 180 : 0;
        }

        // Taken from "Map Projections - A Working Manual", page 30, equation 5-4b.
        // The atan2() function is used in place of the traditional atan(y/x) to simplify the case when x==0.
        y = (float) (Math.cos(lat2) * Math.sin(lon2 - lon1));
        x = (float) (Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1));
        azimuthRadians = Math.atan2(y, x);

        azimuthDegrees = Double.isNaN(azimuthRadians) ? 0 : Math.toDegrees(azimuthRadians);

        if (azimuthDegrees < 0) {
            return (float) (360 + azimuthDegrees);
        } else {
            return (float) azimuthDegrees;
        }

    }    
    
    /**
     * Calculates the slope between two Coordinates.
     * 
     * @param c1
     * @param c2
     * @return 
     */
    public static float calculateSlope(Coordinate c1, Coordinate c2) {
        double rise, run, slope;

        rise  = c2.getLatitude()  - c1.getLatitude();
        run   = c2.getLongitude() - c1.getLongitude();
        slope = rise / run;

        return (float) slope;
    }    
    
    /**
     * Calculates the area a polygon represented by an ArrayList of Point2D.
     * 
     * @param points
     * @return 
     */
    public static double calculateSignedPolygonArea(ArrayList<Point2D> points) {
        double      signedArea, x1, x2, y1, y2;
        int         j, n;
        Point2D     p1, p2;

        n          = points.size();
        signedArea = 0;

        for (int i = 0; i < n; i++) {
            j  = (i + 1) % n;
            p1 = points.get(i);
            p2 = points.get(j);
            x1 = p1.getX();
            x2 = p2.getX();
            y1 = p1.getY();
            y2 = p2.getY();

            signedArea += (x1 * y2) - (y1 * x2);
        }

        signedArea *= .5;

        return signedArea;
    }    
    
    /**
     * Converts Degree, Minute, Second coordinates to decimal.
     * 
     * @param degreeString 
     *          In the format 44.0° 30.0' 13.720000000003552"
     * @return 
     */
    public static float convertHourToDecimal(String degreeString) { 
        float           degree, minute, seconds;
        String          currentToken;
        StringTokenizer st;
        
        st      = new StringTokenizer(degreeString);
        degree  = 0;        
        minute  = 0;
        seconds = 0;
        
        while (st.hasMoreTokens()) {
            currentToken = st.nextToken();
            
            if (currentToken.contains("°")) {
                degree = Float.parseFloat(currentToken.substring(0, currentToken.length() -1));
            } else if (currentToken.contains("'") && currentToken.contains("\"")) {
                String[] split = currentToken.split("'");
                
                minute = Float.parseFloat( split[0].substring(0, split[0].length()));
                seconds = Float.parseFloat(split[1].substring(0, split[1].length() -1));
            } else if (currentToken.contains("'")) {
                minute = Float.parseFloat(currentToken.substring(0, currentToken.length() -1));
            } else if (currentToken.contains("\"")) {
                seconds = Float.parseFloat(currentToken.substring(0, currentToken.length() -1));
            }
        }
        
        minute  = minute  / 60f;
        seconds = seconds / 3600f;
        
        return degree + minute + seconds;
    }    
    
    /**
     * Performs the Ramer–Douglas–Peucker algorithm on an Array of Points representing a line.
     * This function is geared towards lines, to eliminate points that are less then the epsilon.
     * 
     * @param points
     * @param epsilon   The min threshold for keeping a point.
     * @return 
     */
    public static Point2D[] douglasPeuckerLine(Point2D[] points, double epsilon) {
        double    maxDistance              = Double.MAX_VALUE;
        double    perpendicularDistance;
        int       maxIndex                 = 0;
        Point2D[] filteredPoints;
        
        try {
            //Find the point with the max distance
            for (int i = 2; i < points.length  - 1; i++) {
                perpendicularDistance = CoordinateMath.perpendicularDistance(points[i], points[1], points[points.length - 1]);

                if (perpendicularDistance < maxDistance) {
                    maxIndex = i;
                    maxDistance = perpendicularDistance;
                }            
            }

            if (maxDistance <= epsilon) {
                Point2D[] leftRecursiveResults  = douglasPeuckerLine(Arrays.copyOfRange(points, 1, maxIndex), epsilon);
                Point2D[] rightRecursiveResults = douglasPeuckerLine(Arrays.copyOfRange(points, maxIndex, points.length - 1), epsilon);

                //Concatenate left and right
                filteredPoints = new Point2D[leftRecursiveResults.length + rightRecursiveResults.length];
                System.arraycopy(leftRecursiveResults,  0, filteredPoints, 0, leftRecursiveResults.length);
                System.arraycopy(rightRecursiveResults, 0, filteredPoints, leftRecursiveResults.length, rightRecursiveResults.length);            
            } else {
                filteredPoints = points;
            }

            return filteredPoints;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in CoordinateMath.douglasPeuckerLLine - " + e);
            return points;
        }
    }
    
    /**
     * Returns the maximum distance between Coordinates in the list.
     * 
     * @param clist
     * @return 
     */
    public static float getMaxDistances(CoordinateList<Coordinate> clist) {
        float   currentDistance, maxDistance, totalDistance;
        
        //init
        totalDistance = 0;
        maxDistance   = 0;
        
        for (int i = 1; i < clist.size(); i++) {
            currentDistance = getDistance(clist.get(i-1), clist.get(i));
            
            if (maxDistance < currentDistance)
                maxDistance = currentDistance;
        }
        
        return maxDistance;
    }    
    
    /**
     * Calculates the geodetic distance between the two points according to the
     * ellipsoid model of WGS84. Altitude is neglected from calculations.
     *
     * The implementation will calculate this as exactly as it can. However, it
     * is required that the result is within 0.35% of the correct result.
     *
     * Haversine Formula (from R.W. Sinnott, "Virtues of the Haversine", Sky
     * and Telescope, vol. 68, no. 2, 1984, p. 159):
     *
     * See the following URL for more info on calculating distances:
     * http://www.census.gov/cgi-bin/geo/gisfaq?Q5.1
     *
     * @param c1
     *            the Coordinates of point 1
     * @param c2
     *            the Coordinates of point 2
     *
     * @return the distance to the destination in meters
     * @throws java.lang.NullPointerException
     *             if the parameter is null
     */
    public static float getDistance(Coordinate c1, Coordinate c2) {
        float earthRadius = 6371; //In KM

        if (c1.equals(c2)) {
            return 0.0f;
        } else {
            double lat1 = Math.toRadians(c1.getLatitude());
            double lon1 = Math.toRadians(c1.getLongitude());
            double lat2 = Math.toRadians(c2.getLatitude());
            double lon2 = Math.toRadians(c2.getLongitude());

            double dlon = (lon2 - lon1);
            double dlat = (lat2 - lat1);

            double a = (Math.sin(dlat / 2.0f)) * (Math.sin(dlat / 2.0f))
            + (Math.cos(lat1) * Math.cos(lat2) * (Math.sin(dlon / 2.0f)))
            * (Math.cos(lat1) * Math.cos(lat2) * (Math.sin(dlon / 2.0f)));

            double c  = 2.0f * Math.asin(Math.min(1.0, Math.sqrt(a)));
            double km = earthRadius * c;

            return (float) (km * 1000);
        }
    }        
    
    /**
     * Calculates the distance of a path of Coordinates in a CoordinateList.
     * The values are returned as a float array, where each index corresponds
     * with the index of a coordinate in the passed CoordinateList.  The value
     * is the distance, in meters, of that coordinate along the path from the start 
     * coordinate.
     * 
     * @param clist
     * @return 
     */
    public static float[] getDistancesM(CoordinateList<Coordinate> clist) {
        float[] distances;
        float   currentDistance, totalDistance;
        
        //init
        distances     = new float[clist.size()];
        distances[0]  = 0;
        totalDistance = 0;
        
        for (int i = 1; i < clist.size(); i++) {
            currentDistance = getDistance(clist.get(i-1), clist.get(i));
            totalDistance  += currentDistance;
            distances[i]    = totalDistance;
        }
        
        return distances;
    }
    
    /**
     * Calculates the distance of a path of Coordinates in a CoordinateList.
     * The values are returned as a float array, where each index corresponds
     * with the index of a coordinate in the passed CoordinateList.  The value
     * is the distance, in KM, of that coordinate along the path from the start 
     * coordinate.
     * 
     * @param clist
     * @return 
     */
    public static float[] getDistancesKM(CoordinateList<Coordinate> clist) {  
        float[] distances;
        float   currentDistance, totalDistance;
        
        //init
        distances     = new float[clist.size()];
        distances[0]  = 0;
        totalDistance = 0;
        
        for (int i = 1; i < clist.size(); i++) {
            currentDistance = getDistance(clist.get(i-1), clist.get(i)) / 1000f;
            totalDistance  += currentDistance;
            distances[i]    = totalDistance;
        }
        
        return distances;        
    }  
    
    /**
     * Returns an ArrayList of Point2D in relative space used for calculating 
     * polygon areas.
     * 
     * @param objects
     * @return 
     */
    public static ArrayList<Point2D> getRelitivePoints(VectorObjectList objects) {
        ArrayList<Point2D>  points;
        Coordinate          baseCoordinateX, baseCoordinateY, compareCoordinate;
        float               baseLatitude, baseLongitude;
        float               x, y;
        MapPoint            currentPoint;

        points = new ArrayList<Point2D>();

        baseLatitude  = objects.getSouthernMostLatitude();
        baseLongitude = objects.getWesternMostLongitude();

        for (int i = 0; i < objects.size(); i++) {
            currentPoint      = (MapPoint) objects.get(i);
            compareCoordinate = currentPoint.getCoordinateList().get(0);
            baseCoordinateX   = new Coordinate(0f, compareCoordinate.getLatitude(), baseLongitude);
            baseCoordinateY   = new Coordinate(0f, baseLatitude, compareCoordinate.getLongitude());
           
            x = getDistance(baseCoordinateX, compareCoordinate);
            y = getDistance(baseCoordinateY, compareCoordinate);

            points.add(new Point2D.Double(x, y));
        }

        return points;
    }    
    
    /**
     * Determines if a given Point is an end point of a Line2D vertex.
     * 
     * @param p
     * @param vertex
     * @return 
     */
    public static boolean isPointVertexEndPoint(Point p, Line2D vertex) {
        boolean isEndPoint = false;
        int     vertexX1   = (int) vertex.getX1();
        int     vertexY1   = (int) vertex.getY1();
        int     vertexX2   = (int) vertex.getX2();
        int     vertexY2   = (int) vertex.getY2();

        if ((p.getX() == vertexX1) && (p.getY() == vertexY1))
            isEndPoint = true;

        if ((p.getX() == vertexX2) && (p.getY() == vertexY2))
            isEndPoint = true;

        return isEndPoint;
    }

    /**
     * Gives the length of a given LineString.
     * 
     * @param lsToMeasure
     * @return The length in Meters.
     */
    public static double measureLineStringLength(LineString lsToMeasure) {    
        Coordinate                  currentCoordinate;
        CoordinateList<Coordinate>  coordinates;
        double                      length;
        

        length      = 0.0;
        coordinates = lsToMeasure.getCoordinateList();

        for (int i = 1; i < coordinates.size(); i++)
            length += CoordinateMath.getDistance(coordinates.get(i-1), coordinates.get(i));

        return length;
    }    
    
    /**
     * Returns the area of a Polygon.
     * 
     * @param pToMeasure
     * @param mapView
     * @return The Polygon's Area.
     */
    public static double measurePolygonArea(Polygon pToMeasure, MapView mapView)
    {
        double area;

        VectorObjectList       componetPoints = pToMeasure.getMapPoints();
        ArrayList<Point2D>  relitivePoints = getRelitivePoints(componetPoints);

        area = calculateSignedPolygonArea(relitivePoints);

        return Math.abs(area);
    }    
    
    public static double measurePolygonPerimeter(Polygon pToMeasure) {
        Coordinate                  currentCoordinate;
        CoordinateList<Coordinate>  coordinates;
        double                      length;

        length      = 0.0;
        coordinates = pToMeasure.getCoordinateList();

        for (int i = 1; i < coordinates.size(); i++)
            length += CoordinateMath.getDistance(coordinates.get(i-1), coordinates.get(i));

        return length;
    }    
    
    /**
     * Finds the perpendicular distance of a point in respect to a line.
     * 
     * @param point
     * @param p1    Start point of the line
     * @param p2    End point of the line
     * @return 
     */
    public static double perpendicularDistance(Point2D point, Point2D p1, Point2D p2) {
        double slope     = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
        double intercept = p1.getY() - (slope * p1.getX());
        double result    = Math.abs(slope * point.getX() - point.getY() + intercept) / Math.sqrt(Math.pow(slope, 2) + 1);
        
        return result;
    }
    
    /**
     * Tests to see if vertices of a polygon cross
     * @param vertices
     * @return 
     */
    public static boolean testPolygonVertices(ArrayList<Line2D> vertices) {
        boolean verticesCross       = false;
        Point   intersectionPoint;

        for (Line2D testVertex: vertices) {
            for (Line2D compareVertex: vertices) {
                if (testVertex != compareVertex) {
                    try {
                        intersectionPoint = Intersection.getIntersection(testVertex, compareVertex);

                        if (intersectionPoint == null) {
                            //
                        } else {
                            if ((CoordinateMath.isPointVertexEndPoint(intersectionPoint, testVertex))) {
                                //
                            } else {
                                verticesCross = true;
                            }
                        }

                    } catch (Exception e) {
                        //multiple intersections
                        verticesCross = true;
                    } // end try / catch block
                }
            } //end compareVertex loop
        } //end testVertex loop

        return verticesCross;
    }    
}
