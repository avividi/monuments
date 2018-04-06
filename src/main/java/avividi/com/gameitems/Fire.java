package avividi.com.gameitems;

import avividi.com.Board;
import avividi.com.hexgeometry.Grid;
import avividi.com.hexgeometry.PointAxial;
import avividi.com.task.ReplenishFireTask;
import avividi.com.task.Task;

import java.util.Optional;

public class Fire implements InteractingItem {

  private final static int startLife = 60;
  private final static int fireLow = 50;

  private int life = startLife;
  private String image = "fire1";
  private boolean taskGiven;

  @Override
  public void clickAction(Board board, PointAxial self) {
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {
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
    if (taskGiven || life > fireLow) return Optional.empty();

    taskGiven = true;
    return Optional.of(new ReplenishFireTask(this, self));
  }

  public void replenish() {
    life = startLife;
    taskGiven = false;
  }
}
