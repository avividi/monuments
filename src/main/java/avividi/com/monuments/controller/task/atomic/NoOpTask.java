package avividi.com.monuments.controller.task.atomic;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.Hexagon;

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
