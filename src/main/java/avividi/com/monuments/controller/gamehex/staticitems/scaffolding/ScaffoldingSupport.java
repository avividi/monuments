package avividi.com.monuments.controller.gamehex.staticitems.scaffolding;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static avividi.com.monuments.hexgeometry.PointAxial.UP;

public class ScaffoldingSupport implements GameHex {

  private final GameHex background;
  private final List<String> images;

  public ScaffoldingSupport(Board board, PointAxial self) {
    this.background = board.getStatics().getByAxial(self).orElse(null).getObj();
    images = new ArrayList<>(background.getImageNames());
    images.add("wall3/wall3-full");

    board.addLayerAbove(self.getLayer());
    board.getStatics()
        .setHex(new UpperScaffolding()
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

  class UpperScaffolding implements GameHex {

    @Override
    public boolean passable() {
      return true;
    }

    @Override
    public boolean buildable() {
      return true;
    }

    @Override
    public List<String> getImageNames() {
      return ImmutableList.of("wall3/wall3-full");
    }
  }
}
