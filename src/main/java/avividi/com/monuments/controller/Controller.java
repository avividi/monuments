package avividi.com.monuments.controller;


import avividi.com.monuments.controller.clock.ClockStage;
import avividi.com.monuments.controller.userinput.UserAction;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.Point2d;

import java.util.List;
import java.util.stream.Stream;

public interface Controller {

  Stream<Hexagon<? extends HexItem>> getHexagons();
  ClockStage getDayStage();

  Point2d getPosition2d(double imageHeight, double x, double y, double padding);

  void makeAction(UserAction action, boolean secondary);
  List<UserAction> getSelectUserActions();
  int setUpperDisplayLayer(int layer);

  void oneTick();
  String alertText();
}
