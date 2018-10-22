package avividi.com.monuments.sqrgeometry;

public enum TravelDirection {

  N(new Point3(0, 1, 0)),
  NE(new Point3(1, 1, 0)),
  E(new Point3(1, 0, 0)),
  SE(new Point3(1, -1, 0)),
  S(new Point3(0, -1, 0)),
  SW(new Point3(-1, -1, 0)),
  W(new Point3(-1, 0, 0)),
  NW(new Point3(-1, 1, 0)),
  UP(new Point3(0, 0, 1)),
  DOWN(new Point3(0, 0, -1));

  public final Point3 dir;

  TravelDirection(Point3 dir) {
    this.dir = dir;
  }

  public static TravelDirection fromPoints (Point3 p1, Point3 p2) {
    Point3 dir = Point3.direction(p1, p2);

    if (dir.equals(N.dir)) return N;
    else if (dir.equals(NE.dir)) return NE;
    else if (dir.equals(E.dir)) return E;
    else if (dir.equals(SE.dir)) return SE;
    else if (dir.equals(S.dir)) return S;
    else if (dir.equals(SW.dir)) return SW;
    else if (dir.equals(W.dir)) return W;
    else if (dir.equals(NW.dir)) return NW;
    else if (dir.equals(UP.dir)) return UP;
    else if (dir.equals(DOWN.dir)) return DOWN;

    throw new IllegalStateException(dir.toString() + "not a travel direction");
  }

  public final static TravelDirection[] cardinalDirections = {N, NE, E, SE, S, SW, W, NW};
}
