package avividi.com.monuments.controller.task.atomic;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.Hexagon;

public interface Task {
  boolean perform(Board board, Hexagon<Unit> unit);

  boolean shouldAbort();
  boolean isComplete();
}
