package avividi.com.controller.gameitems.other;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.hexgeometry.PointAxial;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Boulder extends InteractingItem {
  public Boulder(ObjectNode json) {
    super(json);
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of("boulder");
  }
}
