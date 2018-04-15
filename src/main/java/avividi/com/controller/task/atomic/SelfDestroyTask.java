package avividi.com.controller.task.atomic;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import com.google.common.base.Preconditions;

public class SelfDestroyTask implements Task {

  private int steps = 5;
  private boolean isComplete = false;

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    if (--steps > 0) return true;
    isComplete = true;
    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
    return true;
  }

  @Override
  public boolean shouldAbort() {
    return false;
  }

  @Override
  public boolean isComplete() {
    return isComplete;
  }
}
