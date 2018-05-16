package avividi.com.monuments.controller.task.atomic;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.Hexagon;
import com.google.common.base.Preconditions;


public class SelfDestroyTask implements Task {

  public static final int tick_moveTime = 5;

  private int timeCount = tick_moveTime;
  private boolean isComplete = false;

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    if (--timeCount > 0) return true;
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
