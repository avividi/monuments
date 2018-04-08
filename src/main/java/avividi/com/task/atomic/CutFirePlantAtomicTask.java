package avividi.com.task.atomic;

import avividi.com.Board;
import avividi.com.gameitems.FirePlant;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;
import avividi.com.item.DriedFireplantItem;
import avividi.com.task.atomic.AtomicTask;
import com.google.common.base.Preconditions;

public class CutFirePlantAtomicTask implements AtomicTask {

  Hexagon<FirePlant> plant;

  public CutFirePlantAtomicTask(Hexagon<FirePlant> plant) {
    this.plant = plant;
  }

  @Override
  public boolean perform(Board board, Unit unit, PointAxial unitPos) {
    Preconditions.checkState(plant.getObj().linkedToTask());
    Preconditions.checkState(PointAxial.distance(plant.getPosAxial(), unitPos) == 1);
    Preconditions.checkState(!unit.getItem().isPresent());

    Preconditions.checkNotNull(board.getOthers().clearHex(plant.getPosAxial()));
    unit.setItem(new DriedFireplantItem());
    plant.getObj().setLinkedToTask(false);
    return true;
  }
}
