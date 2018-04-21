package avividi.com.controller.gameitems.other;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.item.FireplantItem;
import avividi.com.controller.item.Item;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Plot extends InteractingItem {

  private boolean passable = false;
  private Class<? extends Item> holdingType = FireplantItem.class;

  public Plot(ObjectNode json) {
    super(json);
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of("plot", "firePlantItems");
  }

  @Override
  public boolean passable() {
    return passable;
  }
}
