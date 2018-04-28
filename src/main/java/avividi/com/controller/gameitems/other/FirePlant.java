package avividi.com.controller.gameitems.other;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.Interactor;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.item.DriedPlantItem;
import avividi.com.controller.item.Item;
import avividi.com.controller.item.SingleItemGiver;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static avividi.com.controller.Ticks.TOthers.TFirePlant.pickUpTime;

public class FirePlant extends SingleItemGiver implements Interactor {

  public FirePlant(ObjectNode json) {

  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of("fireplant");
  }

  @Override
  protected Class<? extends Item> getItemType() {
    return DriedPlantItem.class;
  }

  @Override
  protected Item getItem() {
    return new DriedPlantItem();
  }

  @Override
  public int pickUpTime() {
    return pickUpTime;
  }


  @Override
  public boolean passable() {
    return false;
  }
}
