package avividi.com.monuments.hexgeometry;

import com.google.common.collect.ImmutableList;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PointAxial extends Point2 {

  public static PointAxial E = new PointAxial(1, 0, 0);
  public static PointAxial SE = new PointAxial(0, 1, 0);
  public static PointAxial SW = new PointAxial(-1, 1, 0);
  public static PointAxial W = new PointAxial(-1, 0, 0);
  public static PointAxial NW = new PointAxial(0, -1, 0);
  public static PointAxial NE = new PointAxial(1, -1, 0);

  public static PointAxial UP = new PointAxial(0, 0, 1);
  public static PointAxial DOWN = new PointAxial(0, 0, -1);

  public static AxialDirection[] allDirections = AxialDirection.values();
  public static List<PointAxial> allDirectionsList = ImmutableList.of(E, SE, SW, W, NW, NE, UP, DOWN);
  public static Stream<PointAxial> cardinalDirectionsStream =
      Stream.of(E, SE, SW, W, NW, NE);
  private final int layer;


  public PointAxial(int c, int r, int layer) {
    super(c, r);
    this.layer = layer;
  }

  public PointAxial (PointCube pointCube, int layer) {
    super(pointCube.getX(), pointCube.getZ());
    this.layer = layer;
  }

  public PointAxial add (int x, int y, int layer) {
    return new PointAxial(this.getX() + x, this.getY() + y, this.layer + layer);
  }

  public PointAxial add (PointAxial point) {
    return add(point.getX(), point.getY(), point.layer);
  }

  public PointAxial subtract (PointAxial point) {
    return add(-point.getX(), -point.getY(), -point.layer);
  }


  public PointAxial multiply (int factor) {
    return new PointAxial(this.getX() * factor, this.getY() * factor, layer);
  }

  public static int distance (PointAxial p1, PointAxial p2) {
    return PointCube.distance(new PointCube(p1), new PointCube(p2));
  }

  public static PointAxial getDirection (PointAxial p1, PointAxial p2) {

    int distanceX = p2.getX() - p1.getX();
    int distanceY = p2.getY() - p1.getY();
    int distanceLayer = p2.layer - p1.layer;

    return new PointAxial(Integer.signum(distanceX), Integer.signum(distanceY), Integer.signum(distanceLayer));
  }

  public static PointAxial reverse (PointAxial point) {
   return new PointAxial(-point.getX(), -point.getY(), -point.layer);
  }

  public static Comparator<PointAxial> comparingPoint(PointAxial origin) {
    return Comparator.comparingInt(p -> PointAxial.distance(origin, p));
  }

  public int getLayer() {
    return layer;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    PointAxial axial = (PointAxial) o;
    return layer == axial.layer;
  }

  @Override
  public int hashCode() {

    return Objects.hash(super.hashCode(), layer);
  }
}
