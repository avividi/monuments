package avividi.com;

import avividi.com.hexgeometry.Grid;
import avividi.com.hexgeometry.PointAxial;

public interface GameItem extends HexItem{

  void clickAction(Board board, PointAxial self);

  void endOfTurnAction(Board board, PointAxial self);

  default boolean isGround() {
    return false;
  }

  default boolean clickAble() {
    return false;
  }
}
