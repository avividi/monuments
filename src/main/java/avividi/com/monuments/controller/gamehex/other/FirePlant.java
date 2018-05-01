package avividi.com.monuments.controller.gamehex.other;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.item.DriedPlantItem;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.SingleItemGiver;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static avividi.com.monuments.controller.Ticks.TOthers.TFirePlant.pickUpTime;

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
