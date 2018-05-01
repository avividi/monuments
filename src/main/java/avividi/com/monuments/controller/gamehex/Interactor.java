package avividi.com.monuments.controller.gamehex;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.hexgeometry.Grid;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.task.plan.Plan;

import java.util.Optional;

public interface Interactor extends GameHex {


  void endOfTurnAction(Board board, PointAxial self);

  default Optional<Plan> checkForPlan(Grid<? extends GameHex> grid, PointAxial self) { return Optional.empty(); };
}
