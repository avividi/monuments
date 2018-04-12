package avividi.com.controller.task;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.task.atomic.AtomicTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface Task {

  default List<Hexagon<Unit>> chooseFromPool(Set<Hexagon<Unit>> pool) {
    return new ArrayList<>(pool);
  }
  boolean planningAndFeasibility(Board board, Hexagon<Unit> unit);
  void performStep (Board board, Hexagon<Unit> unit);
  void performStepForceComplete(Board board, Hexagon<Unit> unit);
  AtomicTask getNextAtomicTask();
  void addNoOp();

  boolean isComplete();

  int getPriority();
}
