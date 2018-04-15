package avividi.com.controller.gameitems.unit;

import avividi.com.controller.Board;
import avividi.com.controller.HexItem;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.item.Item;
import avividi.com.controller.task.atomic.RandomMoveTask;
import avividi.com.controller.task.atomic.Task;
import avividi.com.controller.task.plan.Plan;

import java.util.List;
import java.util.Optional;

public class Rivskin implements Unit {

  private HexItem.Transform transform = HexItem.Transform.none;

  int steps = 25;
  int rePlanCounter;
  List<Task> plan;

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {
    if (plan == null && --rePlanCounter > 0) replan();
    else {

    }
  }

  private void replan() {
    rePlanCounter = 25;
  }

  @Override
  public boolean isFriendly() {
    return false;
  }

  @Override
  public void setTransform(HexItem.Transform transform) {
    this.transform = transform;
  }

  @Override
  public Transform getTransform() {
    return transform;
  }

  @Override
  public String getImageName() {
    return "bonewolf";
  }

  @Override
  public boolean affectedByLight() {
    return false;
  }

  @Override
  public void setItem(Item item) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Optional<Item> getItem() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void assignTask(Plan task) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Plan getPlan() {
    throw new UnsupportedOperationException();
  }

}
