package avividi.com.controller.gameitems;

import avividi.com.controller.Board;
import avividi.com.controller.hexgeometry.Grid;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.task.plan.Plan;

import java.util.Optional;

public interface InteractingItem extends GameItem {

  void endOfTurnAction(Board board, PointAxial self);

  default boolean linkedToTask () {return false;};
  default void setLinkedToTask(boolean linked) {};

  default Optional<Plan> checkForTasks(Grid<? extends GameItem> grid, PointAxial self) { return Optional.empty(); };
}
