package avividi.com.monuments.controller.util;

import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.hexgeometry.PointAxial;

import static avividi.com.monuments.hexgeometry.PointAxial.NW;
import static avividi.com.monuments.hexgeometry.PointAxial.SW;
import static avividi.com.monuments.hexgeometry.PointAxial.W;

public class DirectionTransformUtil {

  public static HexItem.Transform getTransform (PointAxial direction) {
    if (NW.equals(direction)
        || W.equals(direction)
        || SW.equals(direction)) return HexItem.Transform.flipped;
    return HexItem.Transform.none;
  }
}
