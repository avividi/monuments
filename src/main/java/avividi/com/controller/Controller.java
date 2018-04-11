package avividi.com.controller;


import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.Point2d;

import java.util.stream.Stream;

public interface Controller {
  Stream<Hexagon<? extends HexItem>> getHexagons();

  Point2d getPosition2d(double imageHeight, double x, double y, double padding);
  void giveInput(Point2d point2d);

  void addListener(ControllerListener listener);

  int getActionsLeft();

  void oneStep();

  DayStage getDayStage();
}
