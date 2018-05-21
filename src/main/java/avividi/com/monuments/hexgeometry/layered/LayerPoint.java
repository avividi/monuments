package avividi.com.monuments.hexgeometry.layered;

import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.collect.ImmutableList;

import java.util.Comparator;
import java.util.List;

public class LayerPoint {

  private final PointAxial pointAxial;
  private final int layer;

  public static LayerPoint E = new LayerPoint(PointAxial.E, 0);
  public static LayerPoint SE = new LayerPoint(PointAxial.SE, 0);
  public static LayerPoint SW = new LayerPoint(PointAxial.SW, 0);
  public static LayerPoint W = new LayerPoint(PointAxial.W, 0);
  public static LayerPoint NW = new LayerPoint(PointAxial.NW, 0);
  public static LayerPoint NE = new LayerPoint(PointAxial.NE, 0);

  public static LayerPoint UP = new LayerPoint(0, 0, 1);
  public static LayerPoint DOWN = new LayerPoint(0, 0, -1);

  public static List<LayerPoint> allDirections = ImmutableList.of(E, SE, SW, W, NW, NE, UP, DOWN);


  public LayerPoint(int c, int r, int layer) {
    this.pointAxial = new PointAxial(c, r);
    this.layer = layer;
  }

  public LayerPoint(PointAxial pointAxial , int layer) {
    this.pointAxial = pointAxial;
    this.layer = layer;
  }

  public LayerPoint add (int x, int y, int layer) {
    return new LayerPoint(pointAxial.getX() + x, pointAxial.getY() + y, this.layer + layer);
  }

  public LayerPoint add (LayerPoint point) {
    return add(point.getX(), point.getY(), point.getLayer());
  }

  public LayerPoint subtract (LayerPoint point) {
    return add(-point.getX(), -point.getY(), -point.getLayer());
  }


  public LayerPoint multiply (int factor) {
    return new LayerPoint(this.getX() * factor, this.getY() * factor, this.getLayer() * layer);
  }

  public static int distance (LayerPoint p1, LayerPoint p2) {
    return PointAxial.distance(p1.pointAxial, p2.pointAxial) + Math.abs(p1.layer - p2.layer);
  }

  public static LayerPoint getDirection (LayerPoint p1, LayerPoint p2) {

    int distanceX = p2.getX() - p1.getX();
    int distanceY = p2.getY() - p1.getY();
    int distanceLayer = p1.layer - p2.layer;

    return new LayerPoint(Integer.signum(distanceX), Integer.signum(distanceY), Integer.signum(distanceLayer));
  }

  public static LayerPoint reverse (LayerPoint point) {
   return new LayerPoint(-point.getX(), -point.getY(), -point.layer);
  }

  public static Comparator<LayerPoint> comparingPoint(LayerPoint origin) {
    return Comparator.comparingInt(p -> LayerPoint.distance(origin, p));
  }


  public int getX() {
    return pointAxial.getX();
  }

  public int getY() {
    return pointAxial.getY();
  }

  public int getLayer() {
    return layer;
  }

  public PointAxial getPointAxial() {
    return pointAxial;
  }
}
