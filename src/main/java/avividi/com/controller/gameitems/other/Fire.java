package avividi.com.controller.gameitems.other;

import avividi.com.controller.Board;
import avividi.com.controller.DayStage;
import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.hexgeometry.Grid;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.task.ReplenishFireTask;
import avividi.com.controller.task.Task;

import java.util.Optional;

public class Fire implements InteractingItem {

  private final static int startLife = 1000;
//private final static int startLife = 80;
  private final static int fireLow = 500;
  private final int startingFlickerPauseCount = 6;
  private int flickerPauseCount = startingFlickerPauseCount;

  private int life = startLife;
  private String image = "fire1";
  private boolean linkedToTask;
  private int waitForReTask;

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {
    if (life > 0) life--;
    image = calculateImage();

  }

  private String calculateImage () {

    if (--flickerPauseCount != 0) return image;
    flickerPauseCount = startingFlickerPauseCount;
    if (life > fireLow) return "fire1".equals(image) ? "fire2" : "fire1";
    if (life > 0) return  "firelow1".equals(image) ? "firelow2" : "firelow1";
    else return "fire-no";
  }

  @Override
  public String getImageName() {
    return image;
  }

  @Override
  public Optional<Task> checkForTasks(Grid<? extends GameItem> grid, PointAxial self) {
    if (linkedToTask || life > fireLow || life <= 0 || waitForReTask-- > 0) return Optional.empty();

    waitForReTask = 50;

    return Optional.of(new ReplenishFireTask(new Hexagon<>(this, self, null)));
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
    return life <= 0;
  }

  @Override
  public boolean passable() {
    return life <= 0;
  }

  public boolean burning() {
    return life > 0;
  }
}