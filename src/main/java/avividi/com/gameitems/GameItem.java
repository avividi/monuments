package avividi.com.gameitems;

import avividi.com.Board;
import avividi.com.HexItem;
import avividi.com.hexgeometry.PointAxial;

public interface GameItem extends HexItem {

  void endOfTurnAction(Board board, PointAxial self);

  default boolean passable() {
    return false;
  }
}
