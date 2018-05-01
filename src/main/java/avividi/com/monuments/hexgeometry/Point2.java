package avividi.com.monuments.hexgeometry;

public class Point2 {

  private final int x;
  private final int y;

  public Point2(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  @Override
  public String toString() {
    return "{"+ x + ","  +y + "}";
  }

  public Point2 add (int x, int y) {
    return new Point2(this.x + x, this.y + y);
  }

  public Point2 add (Point2 point) {
    return new Point2(this.getX() + point.getX(), this.getY() + point.getY());
  }

  public Point2 multiply (int factor) {
    return new Point2d(x * factor, y * factor);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Point2 point2 = (Point2) o;

    if (x != point2.x) return false;
    return y == point2.y;
  }

  @Override
  public int hashCode() {
    int result = x;
    result = 31 * result + y;
    return result;
  }
}
