package avividi.com.monuments.controller.gamehex.other.buildmarker;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.staticitems.AutoWall;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.hexgeometry.PointAxial;

import java.util.function.Supplier;

public class WallBuildMarker extends BuildMarker {

  private final Supplier<AutoWall> result;

  public WallBuildMarker(Class<? extends Item> itemType,
                         int amount,
                         int buildTime,
                         int priority,
                         Supplier<AutoWall> result) {
    super(itemType, amount, buildTime, priority);
    this.result = result;
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {
    if (fullFilled()) {
      AutoWall builtWall = result.get();
      if (!builtWall.passable()) board.setShouldCalculateSectors();
      board.getOthers().clearHex(self);
      board.getGround().setHex(builtWall, self);
      builtWall.recalculateWallGraph(board, self);
    }
  }
}
