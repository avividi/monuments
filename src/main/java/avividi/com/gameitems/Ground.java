package avividi.com.gameitems;

import avividi.com.Board;
import avividi.com.DayStage;
import avividi.com.hexgeometry.PointAxial;

public class Ground implements GameItem {

  @Override
  public void endOfTurnAction(Board board, PointAxial self, DayStage stage) {
  }

  @Override
  public String getImageName() {
    return "grounddirt";
  }

  @Override
  public boolean passable() {
    return true;
  }
}
