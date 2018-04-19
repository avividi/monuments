package avividi.com.controller.gameitems.other;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.hexgeometry.PointAxial;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Brick implements InteractingItem {
  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

  }

  @Override
  public List<String> getImageName() {
    return ImmutableList.of("brick");
  }
}
