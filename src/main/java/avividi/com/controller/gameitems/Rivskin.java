package avividi.com.controller.gameitems;

import avividi.com.controller.Board;
import avividi.com.controller.DayStage;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.item.Item;
import avividi.com.controller.task.Task;

import java.util.Optional;

public class Rivskin implements Unit {

  @Override
  public void setItem(Item item) {
    throw new IllegalStateException();
  }

  @Override
  public Optional<Item> getItem() {
    return Optional.empty();
  }

  @Override
  public void assignTask(Task task) {

  }

  @Override
  public Task getTask() {
    return null;
  }

  @Override
  public boolean isFriendly() {
    return false;
  }

  @Override
  public void setTransform(Transform transform) {

  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self, DayStage stage) {

  }

  @Override
  public String getImageName() {
    return "bonewolf";
  }

  @Override
  public boolean affectedByLight() {
    return false;
  }
}
