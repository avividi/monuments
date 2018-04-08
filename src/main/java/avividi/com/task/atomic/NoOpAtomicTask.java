package avividi.com.task.atomic;

import avividi.com.Board;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;

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
