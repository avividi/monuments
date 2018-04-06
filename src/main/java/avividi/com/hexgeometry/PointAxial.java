package avividi.com.hexgeometry;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class PointAxial extends Point2 {

  public static PointAxial E = new PointAxial(1, 0);
  public static PointAxial SE = new PointAxial(0, 1);
  public static PointAxial SW = new PointAxial(-1, 1);
  public static PointAxial W = new PointAxial(-1, 0);
  public static PointAxial NW = new PointAxial(0, -1);
  public static PointAxial NE = new PointAxial(1, -1);
  public static List<PointAxial> allDirections = ImmutableList.of(E, SE, SW, W, NW, NE);


  public PointAxial(int c, int r) {
    super(c, r);
  }

  public PointAxial (PointCube pointCube) {
    super(pointCube.getX(), pointCube.getZ());
  }

  @Override
  public PointAxial add (int x, int y) {
    return new PointAxial(this.getX() + x, this.getY() + y);
  }

  public PointAxial add (PointAxial point) {
    return new PointAxial(this.getX() + point.getX(), this.getY() + point.getY());
  }


  public PointAxial multiply (int factor) {
    return new PointAxial(this.getX() * factor, this.getY() * factor);
  }

  public static int distance (PointAxial p1, PointAxial p2) {
    return PointCube.distance(new PointCube(p1), new PointCube(p2));
  }

  public static PointAxial getDirection (PointAxial p1, PointAxial p2) {

    int distanceX = p2.getX() - p1.getX();
    int distanceY = p2.getY() - p1.getY();

    return new PointAxial(Integer.signum(distanceX), Integer.signum(distanceY));
  }

  public static PointAxial reverse (PointAxial point) {
   return new PointAxial(-point.getX(), -point.getY());
  }

}
