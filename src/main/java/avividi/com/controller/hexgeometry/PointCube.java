package avividi.com.controller.hexgeometry;

public class PointCube {

  private int x, y, z;

  public PointCube(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public PointCube (PointAxial pointAxial) {
    this.x = pointAxial.getX();
    this.z = pointAxial.getY();
    this.y = -x - z;
  }

  public static int distance (PointCube p1, PointCube p2) {
    return max3(Math.abs(p1.z - p2.z),
                Math.abs(p1.x - p2.x),
                Math.abs(p1.y - p2.y));
  }

  private static int max3 (int p1, int p2, int p3) {
    return Math.max(p1, Math.max(p2, p3));
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZ() {
    return z;
  }
}
