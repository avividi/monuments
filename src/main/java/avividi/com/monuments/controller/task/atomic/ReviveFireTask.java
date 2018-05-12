package avividi.com.monuments.controller.task.atomic;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.other.Fire;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;

public class ReviveFireTask implements Task {

  private Hexagon<Fire> fire;
  private int reviveTime = 150;
  private boolean isComplete = false;
  private boolean aborted = false;

  public ReviveFireTask (Hexagon<Fire> fire) {
    this.fire = fire;
  }

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    Preconditions.checkState(PointAxial.distance(fire.getPosAxial(), unit.getPosAxial()) == 0);
    if (!board.getOthers().getByAxial(fire.getPosAxial()).isPresent()) {
      aborted = true;
      return false;
    }

    if (--reviveTime > 0) return true;

    fire.getObj().revive(board);
    isComplete = true;
    return true;
  }

  @Override
  public boolean shouldAbort() {
    return aborted;
  }

  @Override
  public boolean isComplete() {
    return isComplete;
  }
}
