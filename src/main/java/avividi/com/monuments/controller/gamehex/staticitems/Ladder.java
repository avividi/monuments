package avividi.com.monuments.controller.gamehex.staticitems;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

import static avividi.com.monuments.hexgeometry.PointAxial.UP;

public class Ladder implements GameHex {

  private final GameHex background;
  private final List<String> images;

  public Ladder(Board board, PointAxial self) {
    this.background = board.getStatics().getByAxial(self).orElse(null).getObj();
    images = new ArrayList<>(background.getImageNames());
    images.add("rough-floor");

    board.addLayerAbove(self.getLayer());
    board.getStatics()
        .setHex(new CustomStaticItem(ImmutableList.of("ladder"), Transform.none, true, false, false)
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
}
