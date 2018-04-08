package avividi.com;

import avividi.com.hexgeometry.PointAxial;

import static avividi.com.hexgeometry.PointAxial.NW;
import static avividi.com.hexgeometry.PointAxial.SW;
import static avividi.com.hexgeometry.PointAxial.W;

public class DirectionTransformUtil {

  public static HexItem.Transform getTransform (PointAxial direction) {
    if (NW.equals(direction)
        || W.equals(direction)
        || SW.equals(direction)) return HexItem.Transform.flipped;
    return HexItem.Transform.none;
  }
}
