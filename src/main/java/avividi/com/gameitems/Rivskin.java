package avividi.com.gameitems;

import avividi.com.Board;
import avividi.com.DayStage;
import avividi.com.HexItem;
import avividi.com.hexgeometry.PointAxial;
import avividi.com.item.Item;
import avividi.com.task.Task;

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
