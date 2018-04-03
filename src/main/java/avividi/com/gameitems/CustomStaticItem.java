package avividi.com.gameitems;

import avividi.com.Board;
import avividi.com.GameItem;
import avividi.com.hexgeometry.PointAxial;

public class CustomStaticItem implements GameItem {

  private final String image;

  public CustomStaticItem(String image) {
    this.image = image;
  }


  @Override
  public void clickAction(Board board, PointAxial self) {

  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

  }

  @Override
  public String getImageName() {
    return image;
  }
}
