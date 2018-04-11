package avividi.com.controller.gameitems;

import avividi.com.controller.hexgeometry.Grid;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.task.Task;

import java.util.Optional;

public interface InteractingItem extends GameItem {

  default boolean linkedToTask () {return false;};
  default void setLinkedToTask(boolean linked) {};

  default Optional<Task> checkForTasks(Grid<? extends GameItem> grid, PointAxial self) { return Optional.empty(); };
}
