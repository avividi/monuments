package avividi.com.gameitems;

import avividi.com.Board;
import avividi.com.DayStage;
import avividi.com.HexItem;
import avividi.com.hexgeometry.PointAxial;

public interface GameItem extends HexItem {

  void endOfTurnAction(Board board, PointAxial self, DayStage stage);

  default boolean passable() {
    return false;
  }
}
