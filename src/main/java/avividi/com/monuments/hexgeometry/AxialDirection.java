package avividi.com.monuments.hexgeometry;

public enum AxialDirection {

  W(PointAxial.W),
  NW(PointAxial.NW),
  NE(PointAxial.NE),
  E(PointAxial.E),
  SE(PointAxial.SE),
  SW(PointAxial.SW),
  UP(PointAxial.UP),
  DOWN (PointAxial.DOWN);

  public final PointAxial dir;

  AxialDirection(PointAxial dir) {
    this.dir = dir;
  }
}
