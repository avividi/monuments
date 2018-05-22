package avividi.com.monuments.controller.gamehex.unit;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.clock.ClockStage;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.task.plan.Plan;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

public class DeadMaldar implements Unit {

  private int timeUntilSkeleton = ClockStage.cycleSize / 100 * 7;
  private int deadCount;
  private String image = "striver-dead1";

  public DeadMaldar(ObjectNode json) {
  }

  @Override
  public void everyTickAction(Board board, PointAxial self) {
  }

  @Override
  public void every100TickAction(Board board, PointAxial self) {

    if (timeUntilSkeleton <= deadCount) {
      image = "striver-dead2";
    }
    else deadCount++;
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of(image);
  }


  @Override
  public boolean passable() {
    return false;
  }

  @Override
  public void setItem(Item item) {

  }

  @Override
  public void dropItem(Board board, PointAxial self) {

  }

  @Override
  public Optional<Item> getItem() {
    return Optional.empty();
  }

  @Override
  public void assignPlan(Plan task) {

  }

  @Override
  public void kill(Board board, PointAxial self) {

  }

  @Override
  public Plan getPlan() {
    return null;
  }

  @Override
  public boolean isFriendly() {
    return false;
  }
}
