package avividi.com.controller.task.atomic;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.Unit;
import avividi.com.controller.hexgeometry.Hexagon;

public class NoOpAtomicTask implements AtomicTask{

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    return true;
  }

  @Override
  public boolean abortSuggested() {
    return false;
  }
}
