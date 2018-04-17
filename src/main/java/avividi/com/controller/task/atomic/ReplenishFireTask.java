package avividi.com.controller.task.atomic;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.gameitems.other.Fire;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;

import static avividi.com.controller.Ticks.TTask.TReplenishFireTask.time;


public class ReplenishFireTask implements Task {

  private final Hexagon<InteractingItem> fire;
  private boolean aborted = false;
  private boolean isComplete = false;
  private int timeCount = time;

  public ReplenishFireTask(Hexagon<InteractingItem> fire) {

    this.fire = fire;
  }

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    Preconditions.checkState(PointAxial.distance(fire.getPosAxial(), unit.getPosAxial()) == 1);

    if (--timeCount > 0) return true;

    boolean success = ((Fire) this.fire.getObj()).replenish();
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
