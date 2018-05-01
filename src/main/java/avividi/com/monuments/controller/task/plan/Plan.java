package avividi.com.monuments.controller.task.plan;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.task.atomic.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface Plan {

  default List<Hexagon<Unit>> chooseFromPool(Board board, Set<Hexagon<Unit>> pool) {
    return new ArrayList<>(pool);
  }
  boolean planningAndFeasibility(Board board, Hexagon<Unit> unit);
  void performStep (Board board, Hexagon<Unit> unit);
  void abort(Board board, PointAxial position);
  Task getNextAtomicTask();
  void addNoOp();

  boolean isComplete();

  int getPriority();
}
