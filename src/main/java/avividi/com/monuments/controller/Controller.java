package avividi.com.monuments.controller;


import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.Point2d;

import java.util.stream.Stream;

public interface Controller {
  void addListener(ControllerListener listener);

  Stream<Hexagon<? extends HexItem>> getHexagons();
  DayStage getDayStage();

  Point2d getPosition2d(double imageHeight, double x, double y, double padding);

  void makeAction(UserAction action, int intensity);

  void oneStep();
}
