package avividi.com.controller;


import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.Point2d;

import java.util.stream.Stream;

public interface Controller {
  void addListener(ControllerListener listener);

  Stream<Hexagon<? extends HexItem>> getHexagons();
  DayStage getDayStage();

  Point2d getPosition2d(double imageHeight, double x, double y, double padding);
  void makeAction(UserAction action);

  void oneStep();
}
