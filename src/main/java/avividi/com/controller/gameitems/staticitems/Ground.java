package avividi.com.controller.gameitems.staticitems;

import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.util.RandomUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Ground extends GameItem {

  private final String image;

  public Ground(ObjectNode json) {
    super(json);
    image = RandomUtil.get().nextBoolean() ? "grounddirt" : "grounddirt2";
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of(image);
  }

  @Override
  public boolean passable() {
    return true;
  }
}
