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

  public static AxialDirection fromPoints (PointAxial p1, PointAxial p2) {
    PointAxial dir = PointAxial.getDirection(p1, p2);

    if (dir.equals(PointAxial.W)) return W;
    else if (dir.equals(PointAxial.NW)) return NW;
    else if (dir.equals(PointAxial.NE)) return NE;
    else if (dir.equals(PointAxial.E)) return E;
    else if (dir.equals(PointAxial.SE)) return SE;
    else if (dir.equals(PointAxial.SW)) return SW;
    else if (dir.equals(PointAxial.UP)) return UP;
    else if (dir.equals(PointAxial.DOWN)) return DOWN;

    throw new IllegalStateException(dir.toString() + "not a direction");
  }

  public final static AxialDirection[] cardinalDirections = {E, SE, SW, W, NW, NE};
}
