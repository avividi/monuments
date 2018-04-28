package avividi.com.controller.item;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.task.atomic.Task;
import com.google.common.base.Preconditions;

public class DeliverItemTask implements Task {

  private final Hexagon<ItemTaker> repository;
  private final Class<? extends Item> itemType;
  private boolean aborted = false;
  private boolean isComplete = false;
  private int timeCount;

  public DeliverItemTask(Hexagon<ItemTaker> repository, Class<? extends Item> itemType) {
    this.repository = repository;
    this.itemType = itemType;
    timeCount = repository.getObj().deliveryTime();
  }

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    Preconditions.checkState(PointAxial.distance(repository.getPosAxial(), unit.getPosAxial()) == 1);
    Preconditions.checkState(unit.getObj().getItem().filter(i -> i.getClass().equals(itemType)).isPresent());

    if (--timeCount > 0) return true;

    boolean success = this.repository.getObj().deliverItem(unit.getObj().getItem().orElseThrow(IllegalStateException::new));
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
