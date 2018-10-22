package avividi.com.monuments.sqrgeometry;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiFunction;

public class Point3 {
  private final int x, y, z;

  public Point3(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Point3 applyOperator (Point3 p, BiFunction<Integer, Integer, Integer> operator) {
    return new Point3(operator.apply(x, p.x), operator.apply(y, p.y), operator.apply(z, p.z));
  }

  public Point3 applyOperator (int number, BiFunction<Integer, Integer, Integer> operator) {
    return new Point3(operator.apply(x, number), operator.apply(y, number), operator.apply(z, number));
  }

  public Point3 add(Point3 p) {
    return applyOperator(p, (a,b) -> a + b);
  }

  public Point3 subtract(Point3 p) {
    return applyOperator(p, (a,b) -> a - b);
  }

  public Point3 multiply(int factor) {
    return  applyOperator(factor, (a,f) -> a * f);
  }

  public Point3 divide(int divident) {
    return  applyOperator(divident, (a,d) -> a / d);
  }

  public static double distance (Point3 p1, Point3 p2) {
    Point3 sub = p2.subtract(p1);

    return Math.sqrt(Math.pow(sub.x, 2) + Math.pow(sub.y, 2) + Math.pow(sub.z, 2));
  }

  public static Point3 direction (Point3 p1, Point3 p2) {
    Point3 sub = p2.subtract(p1);

    return new Point3(Integer.signum(sub.x), Integer.signum(sub.y), Integer.signum(sub.z));
  }

  public static Comparator<Point3> comparingPoint(Point3 origin) {
    return Comparator.comparingDouble(p -> Point3.distance(origin, p));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Point3 point3 = (Point3) o;
    return x == point3.x &&
        y == point3.y &&
        z == point3.z;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, z);
  }
}
