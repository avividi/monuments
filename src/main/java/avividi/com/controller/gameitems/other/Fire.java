package avividi.com.controller.gameitems.other;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.hexgeometry.Grid;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.task.plan.ReplenishFirePlan;
import avividi.com.controller.task.plan.Plan;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

import static avividi.com.controller.Ticks.TOthers.TFire.*;

public class Fire extends InteractingItem {

  private int flickerPauseCount = flickerPause;

  private int life = startLife;
  private String image = "fire1";
  private boolean linkedToTask;
  private int waitForReTaskCount;

  public Fire(ObjectNode json) {
    super(json);
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {
    if (life > 0) life--;
    image = calculateImage();

  }

  private String calculateImage () {

    if (--flickerPauseCount != 0) return image;
    flickerPauseCount = flickerPause;
    if (life > indicateLifeLow) return "fire1".equals(image) ? "fire2" : "fire1";
    if (life > 0) return  "firelow1".equals(image) ? "firelow2" : "firelow1";
    else return "fire-no";
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of(image);
  }

  @Override
  public Optional<Plan> checkForTasks(Grid<? extends GameItem> grid, PointAxial self) {
    waitForReTaskCount--;
    if (linkedToTask || life > indicateLifeLow || life <= 0) return Optional.empty();

    if (waitForReTaskCount-- > 0) return Optional.empty();
    waitForReTaskCount = waitForReTask;

    return Optional.of(new ReplenishFirePlan(new Hexagon<>(this, self, null)));
  }

  public boolean replenish() {
    if (life <= 0) {
      return false;
    }
    life = startLife;
    return true;
  }

  @Override
  public boolean linkedToTask() {
    return linkedToTask;
  }

  @Override
  public void setLinkedToTask(boolean linked) {
    this.linkedToTask = linked;
  }

  @Override
  public boolean affectedByLight() {
    return false;
  }

  @Override
  public boolean passable() {
    return life <= 0;
  }

  public boolean burning() {
    return life > 0;
  }
}
