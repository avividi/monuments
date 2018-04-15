package avividi.com.controller.gameitems.staticitems;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.util.RandomUtil;

public class Ground implements GameItem {

  private final String image;

  public Ground() {
    image = RandomUtil.get().nextBoolean() ? "grounddirt" : "grounddirt2";
  }

  @Override
  public String getImageName() {
    return image;
  }

  @Override
  public boolean passable() {
    return true;
  }
}
