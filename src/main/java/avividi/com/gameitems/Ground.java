package avividi.com.gameitems;

import avividi.com.Board;
import avividi.com.GameItem;
import avividi.com.hexgeometry.Grid;
import avividi.com.hexgeometry.PointAxial;

public class Ground implements GameItem {
  @Override
  public void clickAction(Board board, PointAxial self) {
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {
  }

  @Override
  public String getImageName() {
    return "grounddirt";
  }

  @Override
  public boolean isGround () {
    return true;
  }

  @Override
  public boolean clickAble() {
    return false;
  }
}
