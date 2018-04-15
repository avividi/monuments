package avividi.com.controller.task.atomic;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;

public interface Task {
  boolean perform(Board board, Hexagon<Unit> unit);

  boolean shouldAbort();
  boolean isComplete();
}
