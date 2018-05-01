package avividi.com.monuments.controller.gamehex.unit;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.item.BoulderItem;
import avividi.com.monuments.controller.item.DriedPlantItem;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.task.plan.DefaultLeisurePlan;
import avividi.com.monuments.controller.task.plan.Plan;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

public class Maldar implements Unit {  //Striver

  private Plan currentPlan;
  private Item heldItem;
  private HexItem.Transform transform = HexItem.Transform.none;

  public Maldar(ObjectNode json) {
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

    if (currentPlan == null || currentPlan.isComplete()) {
      currentPlan = new DefaultLeisurePlan();
    }
    currentPlan.performStep(board, new Hexagon<>(this, self, null));
    if (currentPlan.isComplete()) this.currentPlan = null;

  }
  @Override
  public List<String> getImageNames() {
    return ImmutableList.of(
        getItem().map(this::itemToImage).orElse("striver"))
        ;
  }


  private String itemToImage(Item item) {
    if (item instanceof DriedPlantItem) return "driedPlantItem/driedPlantItem-striver" ;
    if (item instanceof BoulderItem) return "boulder/boulder-striver";

    return "striver";
  }

  @Override
  public void setItem(Item item) {
    this.heldItem = item;
  }

  @Override
  public void dropItem(Board board, PointAxial self) {
    if (heldItem != null) {
      heldItem.dropItem(board, self);
      heldItem = null;
    }
  }

  @Override
  public Optional<Item> getItem() {
    return Optional.ofNullable(heldItem);
  }

  @Override
  public void assignPlan(Plan plan) {
    this.currentPlan = plan;
  }

  @Override
  public void kill(Board board, PointAxial self) {
    if (this.currentPlan == null) return;
    this.currentPlan.abort(board, self);
  }

  @Override
  public Plan getPlan() {
    return currentPlan;
  }

  @Override
  public boolean isFriendly() {
    return true;
  }

  @Override
  public void setTransform(HexItem.Transform transform) {
    this.transform = transform;
  }

  @Override
  public HexItem.Transform getTransform() {
    return transform;
  }

  @Override
  public boolean passable() {
    return false;
  }
}
