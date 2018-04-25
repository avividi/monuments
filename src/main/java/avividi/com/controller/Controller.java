package avividi.com.controller;


import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.Point2d;

import java.util.stream.Stream;

public interface Controller {
  void addListener(ControllerListener listener);

  Stream<Hexagon<? extends GameItem>> getHexagons();
  DayStage getDayStage();

  Point2d getPosition2d(double imageHeight, double x, double y, double padding);
  void makeAction(UserAction action);

  void oneStep();
}
