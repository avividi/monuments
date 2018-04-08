package avividi.com.gameitems;

import avividi.com.Board;
import avividi.com.DayStage;
import avividi.com.hexgeometry.Grid;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;
import avividi.com.task.ReplenishFireTask;
import avividi.com.task.Task;

import java.util.Optional;

public class Fire implements InteractingItem {

  private final static int startLife = 300;
  private final static int fireLow = 80;

  private int life = startLife;
  private String image = "fire1";
  private boolean linkedToTask;

  @Override
  public void endOfTurnAction(Board board, PointAxial self, DayStage stage) {
    if (life > 0) life--;
    image = calculateImage();

  }

  private String calculateImage () {
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
    if (linkedToTask || life > fireLow || life <= 0) return Optional.empty();
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
    return false;
  }

  public boolean burning() {
    return life > 0;
  }
}
