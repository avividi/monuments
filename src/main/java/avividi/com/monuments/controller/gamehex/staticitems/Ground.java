package avividi.com.monuments.controller.gamehex.staticitems;

import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.util.RandomUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Ground implements GameHex {

  private final String image;

  public Ground(ObjectNode json) {
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
