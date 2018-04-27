package avividi.com.controller.gameitems.other;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.item.FireplantItem;
import avividi.com.controller.item.Item;
import avividi.com.controller.item.ItemGiver;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

public class FirePlant extends InteractingItem implements ItemGiver<FireplantItem> {

  private boolean reserved = false;
  private boolean alive = true;

  public FirePlant(ObjectNode json) {
    super(json);
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of("fireplant");
  }

  @Override
  public boolean linkedToTask() {
    return reserved;
  }

  @Override
  public void setLinkedToTask(boolean linked) {
    reserved = linked;
  }

  @Override
  public boolean hasItem() {
    return reserved;
  }

  @Override
  public void reserveGetItem() {
    reserved = true;
  }

  @Override
  public void unReserveGetItem() {
    reserved = false;
  }

  @Override
  public Optional<FireplantItem> getItem() {
    if (alive) return Optional.of(new FireplantItem());
    alive = false;
    return Optional.empty();
  }

  @Override
  public Class<? extends Item> getItemClass() {
    return FireplantItem.class;
  }
}
