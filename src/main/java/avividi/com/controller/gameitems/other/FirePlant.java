package avividi.com.controller.gameitems.other;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.Interactor;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.item.FireplantItem;
import avividi.com.controller.item.Item;
import avividi.com.controller.item.ItemGiver;
import avividi.com.controller.item.SingleItemGiver;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    return FireplantItem.class;
  }

  @Override
  protected Item getItem() {
    return new FireplantItem();
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
