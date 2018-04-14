package avividi.com.controller.gameitems.other;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.hexgeometry.PointAxial;

public class Boulder implements InteractingItem {
  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

  }

  @Override
  public String getImageName() {
    return "boulder";
  }
}
