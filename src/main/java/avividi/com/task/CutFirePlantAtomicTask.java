package avividi.com.task;

import avividi.com.Board;
import avividi.com.gameitems.FirePlant;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.PointAxial;
import avividi.com.item.DriedFireplantItem;

public class CutFirePlantAtomicTask implements AtomicTask {

  FirePlant plant;
  PointAxial plantPos;

  public CutFirePlantAtomicTask(FirePlant plant, PointAxial plantPos) {
    this.plantPos = plantPos;
    this.plant = plant;
  }

  @Override
  public boolean perform(Board board, Unit unit, PointAxial unitPos) {
    // TODO preconds?

    board.getOthers().clearHex(plantPos);
    unit.setItem(new DriedFireplantItem());
    return true;
  }
}
