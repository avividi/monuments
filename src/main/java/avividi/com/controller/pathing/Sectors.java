package avividi.com.controller.pathing;

import avividi.com.controller.hexgeometry.PointAxial;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class Sectors {

  private Map<PointAxial, Integer> sectors = new HashMap<>();

  public Sectors(Predicate<PointAxial> isPathable) {

  }
}
