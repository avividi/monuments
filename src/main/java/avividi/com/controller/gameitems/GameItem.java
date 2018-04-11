package avividi.com.controller.gameitems;

import avividi.com.controller.Board;
import avividi.com.controller.DayStage;
import avividi.com.controller.HexItem;
import avividi.com.controller.hexgeometry.PointAxial;

public interface GameItem extends HexItem {

  void endOfTurnAction(Board board, PointAxial self, DayStage stage);

  default boolean passable() {
    return false;
  }
}
