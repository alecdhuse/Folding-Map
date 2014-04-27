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

import co.foldingmap.map.vector.Coordinate;
import java.awt.Point;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;


/**
 * The Intersection class provides methods for determining
 * the intersection point of two lines and the intersection
 * point of a line and a GeneralPath object.
 *
 * @author      Jesus M. Salvo Jr.
 */
public class Intersection {

  /**
   * Returns the intersection point of two lines.
   *
   * @param   line1   First line
   * @param   line2   Second line
   * @return  The Point object where the two lines intersect. This method
   * returns null if the two lines do not intersect.
   * @throws  <tt>MultipleIntersectionException</tt> when the two lines
   * have more than one intersection point.
   */
  static public Point getIntersection( Line2D line1, Line2D line2 )
    throws Exception
  {
    double dyline1, dxline1;
    double dyline2, dxline2, e, f;
    double x1line1, y1line1, x2line1, y2line1;
    double x1line2, y1line2, x2line2, y2line2;

    if ( !line1.intersectsLine( line2 ) )
      return null;

    /* first, check to see if the segments intersect by parameterization
       on s and t; if s and t are both between [0,1], then the
       segments intersect */
    x1line1 = (double)line1.getX1();
    y1line1 = (double)line1.getY1();
    x2line1 = (double)line1.getX2();
    y2line1 = (double)line1.getY2();

    x1line2 = (double)line2.getX1();
    y1line2 = (double)line2.getY1();
    x2line2 = (double)line2.getX2();
    y2line2 = (double)line2.getY2();

    /* check to see if the segments have any endpoints in common. If they do,
       then return the endpoints as the intersection point */
    if ((x1line1==x1line2) && (y1line1==y1line2))
    {
      return (new Point( (int) x1line1, (int) y1line1));
    }
    if ((x1line1==x2line2) && (y1line1==y2line2))
    {
      return (new Point( (int) x1line1, (int) y1line1));
    }
    if ((x2line1==x1line2) && (y2line1==y1line2))
    {
      return (new Point( (int) x2line1, (int) y2line1));
    }
    if ((x2line1==x2line2) && (y2line1==y2line2))
    {
      return (new Point( (int) x2line1, (int) y2line1));
    }

    dyline1 = -( y2line1 - y1line1 );
    dxline1 = x2line1 - x1line1;

    dyline2 = -( y2line2 - y1line2 );
    dxline2 = x2line2 - x1line2;

    e = -(dyline1 * x1line1) - (dxline1 * y1line1);
    f = -(dyline2 * x1line2) - (dxline2 * y1line2);

    /* compute the intersection point using
      ax+by+e = 0 and cx+dy+f = 0

      If there is more than 1 intersection point between two lines,
    */
    if( (dyline1 * dxline2 - dyline2 * dxline1) == 0 )
        throw new Exception();
    return (new Point(
      (int) (-(e * dxline2 - dxline1 * f)/(dyline1 * dxline2 - dyline2 * dxline1)),
      (int) (-(dyline1 * f - dyline2 * e)/(dyline1 * dxline2 - dyline2 * dxline1))));
  }

  /**
   * Returns the intersection point of a line and a GeneralPath. Given that
   * a line can intersect more than one segment of a GeneralPath, this method
   * will return the intersection point of the first segment, returned by
   * a PathIterator, and the line. No consideration is taken if the line
   * intersects more than one segment of the GeneralPath since this is internally
   * used by salvo.jesus.graph.VisualEdge wherein the edge of a line
   * will intersect only one segment of the VisualVertex's GeneralPath.
   *
   * @param   line1   First line
   * @param   path    GeneralPath representing line segments
   * @return  The Point object where the line and the GeneralPath object intersect.
   * This method returns null if they do not intersect.
   */
  static public boolean getIntersection( Line2D line, GeneralPath path ){
    boolean         intersects = false;
    Line2D.Double   rectline = new Line2D.Double();
    Point           intersectpoint = null;
    Point           testintersectpoint = null;
    Point           previousintersectpoint;
    Point           lineorigin = new Point( (int) line.getX1(), (int) line.getY1() );
    FlatteningPathIterator    pathiterator;
    double          coords[] = new double[6];
    int             segmenttype;
    double          prevx = -1, prevy = -1;

    // Iterate through the segments
    pathiterator = new FlatteningPathIterator( path.getPathIterator( null ), 1.0 );
    while( !pathiterator.isDone() ){
      segmenttype = pathiterator.currentSegment( coords );
      // Since we are are iterating through a GeneralPath, all segments
      // are guaranteed to be a line.
      switch (segmenttype){
        case PathIterator.SEG_MOVETO:
          prevx = coords[0]; prevy = coords[1];
          break;
        case PathIterator.SEG_QUADTO:
        case PathIterator.SEG_CUBICTO:
        case PathIterator.SEG_LINETO:
          // If prevx or prev = -1, then we do not have an initial SEG_MOVETO yet.
          // Therefore, the if block does not get executed and the coordinates
          // are treated as if it were a SEG_MOVETO.
          if( prevx != -1 && prevy != 1 ){
            // Check if the line segment intersects with the line in the argument
            rectline.setLine( prevx, prevy, coords[0], coords[1] );

            // Workaround at the moment
            try {
                testintersectpoint = getIntersection( line, rectline );
            }
            catch( Exception ex ) {
                intersects = true;
            }

            if (testintersectpoint != null)
                intersects = true;

          }
          prevx = coords[0]; prevy = coords[1];
          break;
      }
      pathiterator.next();
    }
    return intersects;
  }

  /**
   *
   * @param c1  First coordinate in the first line
   * @param c2  Second coordinate in the first line
   * @param c3  First coordinate in the second line
   * @param c4  Second coordinate in the second line
   * @return
   */
  static public Coordinate getIntersection(Coordinate c1, Coordinate c2, Coordinate c3, Coordinate c4) {
      Coordinate intersectionCoordinate;
      Line2D     line1, line2;
      Point      intersectionPoint;

      intersectionCoordinate = Coordinate.UNKNOWN_COORDINATE;

      try {
        line1 = new Line2D.Double(c1.getAsPoint2D(), c2.getAsPoint2D());
        line2 = new Line2D.Double(c3.getAsPoint2D(), c4.getAsPoint2D());
        
        intersectionPoint       = getIntersection(line1, line2);
        intersectionCoordinate  = new Coordinate(0, intersectionPoint.y, intersectionPoint.x);
      } catch (Exception e) {
        System.err.println("Error in Intersection.getIntersection - " + e);
      }

      return intersectionCoordinate;
  }
}