package avividi.com.gameitems;

import avividi.com.Board;
import avividi.com.GameItem;
import avividi.com.hexgeometry.Grid;
import avividi.com.hexgeometry.PointAxial;

public class Fire implements GameItem {

  private int life = 200;
  private String image = "fire1";

  @Override
  public void clickAction(Board board, PointAxial self) {
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {
    if (life > 0) life--;
    image = calculateImage();

  }

  private String calculateImage () {

    if (life > 50) return "fire1".equals(image) ? "fire2" : "fire1";
    if (life > 0) return  "firelow1".equals(image) ? "firelow2" : "firelow1";
    else return "fire-no";
  }

  @Override
  public String getImageName() {
    return image;
  }

  @Override
  public boolean isGround () {
    return false;
  }

  @Override
  public boolean clickAble() {
    return false;
  }
}
