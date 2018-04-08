package avividi.com.task;

import avividi.com.Board;
import avividi.com.gameitems.Maldar;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Task {

  default Optional<Hexagon<Unit>> chooseFromPool(Set<Hexagon<Unit>> pool) {
    return pool.stream().findAny();
  }
  boolean planningAndFeasibility(Board board, Hexagon<Unit> unit);
  void performStep (Board board, PointAxial unitPos, Unit unit);

  boolean isComplete();

  int getPriority();
}
