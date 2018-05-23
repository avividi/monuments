package avividi.com.monuments.controller.gamehex.staticitems.scaffolding;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static avividi.com.monuments.hexgeometry.PointAxial.UP;

public class Ladder implements GameHex {

  private final GameHex background;
  private final List<String> images;

  public Ladder(Board board, PointAxial self) {
    this.background = board.getStatics().getByAxial(self).orElse(null).getObj();
    images = new ArrayList<>(background.getImageNames());
    images.add("scaffolding/scaffolding-ladder");

    board.addLayerAbove(self.getLayer());
    board.getStatics()
        .setHex(new UpperLadder()
            , self.add(UP));
  }

  @Override
  public boolean passable() {
    return true;
  }

  @Override
  public List<String> getImageNames() {
    return images;
  }

  @Override
  public boolean givesPassageUp() {
    return true;
  }

  class UpperLadder implements GameHex {

    @Override
    public boolean passable() {
      return true;
    }

    @Override
    public boolean renderAble() {
      return false;
    }

    @Override
    public boolean givesPassageDown() {
      return true;
    }

    @Override
    public List<String> getImageNames() {
      return Collections.emptyList();
    }
  }
}