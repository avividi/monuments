package avividi.com.gameitems;

import avividi.com.Board;
import avividi.com.hexgeometry.PointAxial;

public class FirePlant implements InteractingItem {
  @Override
  public void clickAction(Board board, PointAxial self) {
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {
  }

  @Override
  public String getImageName() {
    return "fireplant";
  }
}
