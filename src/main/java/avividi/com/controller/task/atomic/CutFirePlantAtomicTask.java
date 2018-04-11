package avividi.com.controller.task.atomic;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.FirePlant;
import avividi.com.controller.gameitems.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.item.DriedFireplantItem;
import com.google.common.base.Preconditions;

public class CutFirePlantAtomicTask implements AtomicTask {

  Hexagon<FirePlant> plant;
  private boolean abort;

  public CutFirePlantAtomicTask(Hexagon<FirePlant> plant) {
    this.plant = plant;
  }

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    Preconditions.checkState(plant.getObj().linkedToTask());
    Preconditions.checkState(PointAxial.distance(plant.getPosAxial(), unit.getPosAxial()) == 1);
    Preconditions.checkState(!unit.getObj().getItem().isPresent());

    if (board.getOthers().clearHex(plant.getPosAxial()) == null) {
      this.abort = true;
      return false;
    }
    unit.getObj().setItem(new DriedFireplantItem());
    plant.getObj().setLinkedToTask(false);
    return true;
  }

  @Override
  public boolean abortSuggested() {
    return abort;
  }
}
