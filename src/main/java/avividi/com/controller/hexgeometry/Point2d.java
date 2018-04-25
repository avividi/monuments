package avividi.com.controller.hexgeometry;

public class Point2d extends Point2 {

  public static Point2d E = new Point2d(2, 0);
  public static Point2d SE = new Point2d(1, 1);
  public static Point2d SW = new Point2d(-1, 1);
  public static Point2d W = new Point2d(-2, 0);
  public static Point2d NW = new Point2d(-1, -1);
  public static Point2d NE = new Point2d(1, -1);


  public Point2d(int x, int y) {
    super(x, y);
  }

  @Override
  public Point2d add (int x, int y) {
    return new Point2d(this.getX() + x, this.getY() + y);
  }

  public Point2d add (Point2d point) {
    return add(point.getX(), point.getY());
  }

  public Point2d subtract (Point2d point) {
    return add(-point.getX(), -point.getY());
  }

  public Point2d add(PointAxial vector) {
    return new Point2d(
        getX() + vector.getX() * 2 + vector.getY(),
        getY() + vector.getY()
    );
  }
}
