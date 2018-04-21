package avividi.com.controller.gameitems;

import avividi.com.controller.Board;
import avividi.com.controller.hexgeometry.Grid;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.task.plan.Plan;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;

public abstract class InteractingItem extends GameItem  {

  public InteractingItem(ObjectNode json) {
    super(json);
  }

  abstract public void endOfTurnAction(Board board, PointAxial self);

  public boolean linkedToTask () {return false;};
  public void setLinkedToTask(boolean linked) {};

  public Optional<Plan> checkForTasks(Grid<? extends GameItem> grid, PointAxial self) { return Optional.empty(); };
}
