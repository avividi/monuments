package avividi.com.controller.task.atomic;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.Fire;
import avividi.com.controller.gameitems.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;

public class ReplenishFireAtomicTask implements AtomicTask {

  private final Hexagon<Fire> fire;
  private boolean aborted = false;
  private boolean isComplete = false;
  private int steps = 3;

  public ReplenishFireAtomicTask(Hexagon<Fire> fire) {

    this.fire = fire;
  }

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    Preconditions.checkState(PointAxial.distance(fire.getPosAxial(), unit.getPosAxial()) == 1);

    if (--steps > 0) return true;

    boolean success = this.fire.getObj().replenish();
    this.fire.getObj().setLinkedToTask(false);
    if (success) {
      unit.getObj().setItem(null);
      isComplete = true;
      return true;
    }
    this.aborted = true;
    //todo drop item?
    unit.getObj().setItem(null);
    return false;
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
