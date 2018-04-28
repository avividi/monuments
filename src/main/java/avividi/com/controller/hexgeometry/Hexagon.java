package avividi.com.controller.hexgeometry;

import java.util.Comparator;

public class Hexagon<T> {
  private final PointAxial pointAxial;
  private final Point2d point2d;
  private final T c;

  public Hexagon(T c, PointAxial pointAxial, Point2d point2d) {
    this.pointAxial = pointAxial;
    this.point2d = point2d;
    this.c = c;
  }

  public <U> Hexagon<U> copy (U u) {
    return new Hexagon<>(u, pointAxial, point2d);
  }

  public <U> Hexagon<U> as () {
    return new Hexagon<>((U) getObj(), pointAxial, point2d);
  }

  public PointAxial getPosAxial() {
    return pointAxial;
  }

  public Point2d getPos2d() {
    return point2d;
  }

  public T getObj() {
    return c;
  }

  @Override
  public String toString() {
    return c + pointAxial.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Hexagon<?> hexagon = (Hexagon<?>) o;

    return pointAxial.equals(hexagon.pointAxial)
        && this.c == hexagon.getObj();
  }

  @Override
  public int hashCode() {
    return pointAxial.hashCode();
  }

  public static <T> Comparator<Hexagon<T>> compareDistance(PointAxial origin) {
    return Comparator.comparingInt(p -> PointAxial.distance(origin, p.getPosAxial()));
  }

}
