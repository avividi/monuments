package avividi.com.controller.task.atomic;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;

public class NoOpTask implements Task {

  private boolean isComplete = false;

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {


    isComplete = true;
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
