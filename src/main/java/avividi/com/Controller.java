package avividi.com;


import avividi.com.hexgeometry.Grid;
import avividi.com.hexgeometry.Point2d;

public interface Controller {

  Board getBoard();

  void giveInput(Point2d point2d);

  void addListener(ControllerListener listener);

  int getActionsLeft();
}
