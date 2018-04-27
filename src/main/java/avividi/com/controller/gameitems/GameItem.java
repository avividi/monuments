package avividi.com.controller.gameitems;
import avividi.com.controller.Board;
import avividi.com.controller.HexItem;
import avividi.com.controller.hexgeometry.PointAxial;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class GameItem implements HexItem {

  public GameItem(ObjectNode json) {}

  int x = 0;

  public void postLoadCalculation (Board board, PointAxial self) {

  }

  public boolean passable() {
    return false;
  }
}
