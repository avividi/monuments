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

  public ReplenishFireAtomicTask(Hexagon<Fire> fire) {

    this.fire = fire;
  }

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    Preconditions.checkState(PointAxial.distance(fire.getPosAxial(), unit.getPosAxial()) == 1);
    boolean success = this.fire.getObj().replenish();
    this.fire.getObj().setLinkedToTask(false);
    if (success) {
      unit.getObj().setItem(null);
      return true;
    }
    this.aborted = true;
    //todo drop item?
    unit.getObj().setItem(null);
    return false;
  }

  @Override
  public boolean abortSuggested() {
    return aborted;
  }
}
