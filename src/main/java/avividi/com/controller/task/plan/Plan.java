package avividi.com.controller.task.plan;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.task.atomic.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface Plan {

  default List<Hexagon<Unit>> chooseFromPool(Set<Hexagon<Unit>> pool) {
    return new ArrayList<>(pool);
  }
  boolean planningAndFeasibility(Board board, Hexagon<Unit> unit);
  void performStep (Board board, Hexagon<Unit> unit);
  Task getNextAtomicTask();
  void addNoOp();

  boolean isComplete();

  int getPriority();
}
