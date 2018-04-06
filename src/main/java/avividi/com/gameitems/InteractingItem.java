package avividi.com.gameitems;

import avividi.com.hexgeometry.Grid;
import avividi.com.hexgeometry.PointAxial;
import avividi.com.item.Item;
import avividi.com.task.Task;

import java.util.Optional;

public interface InteractingItem extends GameItem {

  default boolean linkedToTask () {return false;};
  default void setLinkedToTask(boolean linked) {};

  default Optional<Task> checkForTasks(Grid<? extends GameItem> grid, PointAxial self) { return Optional.empty(); };
}
