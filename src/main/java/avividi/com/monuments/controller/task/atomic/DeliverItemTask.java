package avividi.com.monuments.controller.task.atomic;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.ItemTaker;
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
    Preconditions.checkState(PointAxial.distance(repository.getPosAxial(), unit.getPosAxial()) <= 1);
    Preconditions.checkState(unit.getObj().getItem().filter(i -> i.getClass().equals(itemType)).isPresent());

    if (!board.getOthers().getByAxial(repository.getPosAxial())
        .filter(h -> h.getObj() == repository.getObj()).isPresent()) {
      this.aborted = true;
      return false;
    }

    if (--timeCount > 0) return true;

    boolean success =
        this.repository.getObj().deliverItem(unit.getObj().getItem().orElseThrow(IllegalStateException::new));
    if (success) {
      unit.getObj().setItem(null);
      isComplete = true;
      return true;
    }
    this.aborted = true;
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
