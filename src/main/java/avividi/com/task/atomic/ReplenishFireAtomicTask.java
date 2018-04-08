package avividi.com.task.atomic;

import avividi.com.Board;
import avividi.com.gameitems.Fire;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;

public class ReplenishFireAtomicTask implements AtomicTask {

  private final Hexagon<Fire> fire;

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
    return false;
  }
}
