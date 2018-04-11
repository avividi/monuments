package avividi.com.controller.gameitems;

import avividi.com.controller.Board;
import avividi.com.controller.DayStage;
import avividi.com.controller.hexgeometry.PointAxial;

public class CustomStaticItem implements InteractingItem {

  private final String image;

  public CustomStaticItem(String image) {
    this.image = image;
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self, DayStage stage) {

  }

  @Override
  public String getImageName() {
    return image;
  }
}
