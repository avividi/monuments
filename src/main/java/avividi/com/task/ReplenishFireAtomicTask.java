package avividi.com.task;

import avividi.com.Board;
import avividi.com.gameitems.Fire;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.PointAxial;

public class ReplenishFireAtomicTask implements AtomicTask {

  private final Fire fire;
  private final PointAxial firePos;

  public ReplenishFireAtomicTask(Fire fire, PointAxial firePos) {

    this.fire = fire;
    this.firePos = firePos;
  }

  @Override
  public boolean perform(Board board, Unit unit, PointAxial unitPos) {
    this.fire.replenish();
    unit.setItem(null);
    return true;
  }
}
