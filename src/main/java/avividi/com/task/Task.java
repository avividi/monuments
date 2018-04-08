package avividi.com.task;

import avividi.com.Board;
import avividi.com.gameitems.Maldar;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;
import avividi.com.task.atomic.AtomicTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Task {

  default List<Hexagon<Unit>> chooseFromPool(Set<Hexagon<Unit>> pool) {
    return new ArrayList<>(pool);
  }
  boolean planningAndFeasibility(Board board, Hexagon<Unit> unit);
  void performStep (Board board, Hexagon<Unit> unit);
  AtomicTask getNextAtomicTask();
  void addNoOp();

  boolean isComplete();

  int getPriority();
}
