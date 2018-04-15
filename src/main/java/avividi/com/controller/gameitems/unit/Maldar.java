package avividi.com.controller.gameitems.unit;

import avividi.com.controller.Board;
import avividi.com.controller.HexItem;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.item.BoulderItem;
import avividi.com.controller.item.DriedFireplantItem;
import avividi.com.controller.item.Item;
import avividi.com.controller.task.plan.DefaultLeisurePlan;
import avividi.com.controller.task.plan.Plan;

import java.util.Optional;

public class Maldar implements Unit {  //Striver

  private Plan currentTask;
  private Item heldItem;
  private HexItem.Transform transform = HexItem.Transform.none;

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

    if (currentTask == null) {
      currentTask = new DefaultLeisurePlan();
    }
    currentTask.performStep(board, new Hexagon<>(this, self, null));
    if (currentTask.isComplete()) this.currentTask = null;

  }
  @Override
  public String getImageName() {
    return getItem().map(this::itemToImage).orElse("striver");
  }


  private String itemToImage(Item item) {
    if (item instanceof DriedFireplantItem) return "striverfireplant" ;
    if (item instanceof BoulderItem) return "striverboulder";

    return "striver";
  }

  @Override
  public void setItem(Item item) {
    this.heldItem = item;
  }

  @Override
  public Optional<Item> getItem() {
    return Optional.ofNullable(heldItem);
  }

  @Override
  public void assignTask(Plan task) {
    this.currentTask = task;
  }

  @Override
  public void kill() {
    if (this.currentTask == null) return;
    this.currentTask.abort();
  }

  @Override
  public Plan getPlan() {
    return currentTask;
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
}
