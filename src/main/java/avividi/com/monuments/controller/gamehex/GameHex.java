package avividi.com.monuments.controller.gamehex;
import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.hexgeometry.PointAxial;

public interface GameHex extends HexItem {

  default void postLoadCalculation (Board board, PointAxial self) {

  }

  boolean passable();
}
