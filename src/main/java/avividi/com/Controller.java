package avividi.com;


import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.Point2d;

import java.util.stream.Stream;

public interface Controller {

  Board getBoard();

  Stream<Hexagon<? extends HexItem>> getHexagons();

  void giveInput(Point2d point2d);

  void addListener(ControllerListener listener);

  int getActionsLeft();
}
