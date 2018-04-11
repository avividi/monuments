package avividi.com.controller;

import avividi.com.controller.hexgeometry.PointAxial;

import static avividi.com.controller.hexgeometry.PointAxial.NW;
import static avividi.com.controller.hexgeometry.PointAxial.SW;
import static avividi.com.controller.hexgeometry.PointAxial.W;

public class DirectionTransformUtil {

  public static HexItem.Transform getTransform (PointAxial direction) {
    if (NW.equals(direction)
        || W.equals(direction)
        || SW.equals(direction)) return HexItem.Transform.flipped;
    return HexItem.Transform.none;
  }
}
