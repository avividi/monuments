package avividi.com.controller.gameitems.staticitems;

import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.util.RandomUtil;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Ground implements GameItem {

  private final String image;

  public Ground() {
    image = RandomUtil.get().nextBoolean() ? "grounddirt" : "grounddirt2";
  }

  @Override
  public List<String> getImageName() {
    return ImmutableList.of(image);
  }

  @Override
  public boolean passable() {
    return true;
  }
}
