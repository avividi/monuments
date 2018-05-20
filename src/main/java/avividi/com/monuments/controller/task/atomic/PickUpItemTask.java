package avividi.com.monuments.controller.task.atomic;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.ItemGiver;
import com.google.common.base.Preconditions;


public class PickUpItemTask implements Task {

  private final Hexagon<ItemGiver> giver;
  private final Class<? extends Item> itemType;
  private boolean aborted;
  private boolean isComplete = false;
  private int timeCount;

  public PickUpItemTask(Hexagon<ItemGiver> giver, Class<? extends Item> itemType) {
    this.giver = giver;
    this.itemType = itemType;
    timeCount = giver.getObj().pickUpTime();
  }

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    if (--timeCount > 0) return true;
    isComplete = true;

    Preconditions.checkState(PointAxial.distance(giver.getPosAxial(), unit.getPosAxial()) <= 1);
    Preconditions.checkState(!unit.getObj().getItem().isPresent());

    if (!board.getOthers().getByAxial(giver.getPosAxial()).isPresent()) {
      System.out.println("  aborting, supplier not present");
      this.aborted = true;
      return false;
    }

    Item item = giver.getObj().pickUpItem(board,  giver.getPosAxial(), itemType).orElse(null);
    if (item == null) {
      System.out.println("  aborting, item not present");
      this.aborted = true;
      return false;
    }
    unit.getObj().setItem(item);
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
