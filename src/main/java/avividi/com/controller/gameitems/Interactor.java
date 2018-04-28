package avividi.com.controller.gameitems;

import avividi.com.controller.Board;
import avividi.com.controller.hexgeometry.Grid;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.task.plan.Plan;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;

public interface Interactor extends GameItem  {


  void endOfTurnAction(Board board, PointAxial self);

  default Optional<Plan> checkForPlan(Grid<? extends GameItem> grid, PointAxial self) { return Optional.empty(); };
}
