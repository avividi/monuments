package avividi.com.controller.task.atomic;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.item.DriedFireplantItem;
import com.google.common.base.Preconditions;

public class CutFirePlantTask implements Task {

  Hexagon<InteractingItem> plant;
  private boolean abort;
  private boolean isComplete = false;
  private int steps = 8;

  public CutFirePlantTask(Hexagon<InteractingItem> plant) {
    this.plant = plant;
  }

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    if (--steps > 0) return true;

    Preconditions.checkState(plant.getObj().linkedToTask());
    Preconditions.checkState(PointAxial.distance(plant.getPosAxial(), unit.getPosAxial()) == 1);
    Preconditions.checkState(!unit.getObj().getItem().isPresent());

    if (board.getOthers().clearHex(plant.getPosAxial()) == null) {
      this.abort = true;
      return false;
    }
    unit.getObj().setItem(new DriedFireplantItem());
    plant.getObj().setLinkedToTask(false);

    isComplete = true;
    return true;
  }

  @Override
  public boolean shouldAbort() {
    return abort;
  }

  @Override
  public boolean isComplete() {
    return isComplete;
  }
}
