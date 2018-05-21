package avividi.com.monuments.hexgeometry.layer;

import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.hexgeometry.PointCube;
import com.google.common.collect.ImmutableList;

import java.util.Comparator;
import java.util.List;

public class PointLayer {

  private final PointAxial pointAxial;
  private final int layer;

  public static PointLayer E = new PointLayer(PointAxial.E, 0);
  public static PointLayer SE = new PointLayer(PointAxial.SE, 0);
  public static PointLayer SW = new PointLayer(PointAxial.SW, 0);
  public static PointLayer W = new PointLayer(PointAxial.W, 0);
  public static PointLayer NW = new PointLayer(PointAxial.NW, 0);
  public static PointLayer NE = new PointLayer(PointAxial.NE, 0);

  public static PointLayer UP = new PointLayer(0, 0, 1);
  public static PointLayer DOWN = new PointLayer(0, 0, -1);

  public static List<PointLayer> allDirections = ImmutableList.of(E, SE, SW, W, NW, NE, UP, DOWN);


  public PointLayer(int c, int r, int layer) {
    this.pointAxial = new PointAxial(c, r);
    this.layer = layer;
  }

  public PointLayer(PointAxial pointAxial , int layer) {
    this.pointAxial = pointAxial;
    this.layer = layer;
  }

  public PointLayer add (int x, int y, int layer) {
    return new PointLayer(pointAxial.getX() + x, pointAxial.getY() + y, this.layer + layer);
  }

  public PointLayer add (PointLayer point) {
    return add(point.getX(), point.getY(), point.getLayer());
  }

  public PointLayer subtract (PointLayer point) {
    return add(-point.getX(), -point.getY(), -point.getLayer());
  }


  public PointLayer multiply (int factor) {
    return new PointLayer(this.getX() * factor, this.getY() * factor, this.getLayer() * layer);
  }

  public static int distance (PointLayer p1, PointLayer p2) {
    return PointAxial.distance(p1.pointAxial, p2.pointAxial) + Math.abs(p1.layer - p2.layer);
  }

  public static PointLayer getDirection (PointLayer p1, PointLayer p2) {

    int distanceX = p2.getX() - p1.getX();
    int distanceY = p2.getY() - p1.getY();
    int distanceLayer = p1.layer - p2.layer;

    return new PointLayer(Integer.signum(distanceX), Integer.signum(distanceY), Integer.signum(distanceLayer));
  }

  public static PointLayer reverse (PointLayer point) {
   return new PointLayer(-point.getX(), -point.getY(), -point.layer);
  }

  public static Comparator<PointLayer> comparingPoint(PointLayer origin) {
    return Comparator.comparingInt(p -> PointLayer.distance(origin, p));
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



}
