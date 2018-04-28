package avividi.com.controller.gameitems;
import avividi.com.controller.Board;
import avividi.com.controller.HexItem;
import avividi.com.controller.hexgeometry.PointAxial;

public interface GameItem extends HexItem {

  default void postLoadCalculation (Board board, PointAxial self) {

  }

  boolean passable();
}
