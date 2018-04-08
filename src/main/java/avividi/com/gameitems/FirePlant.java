package avividi.com.gameitems;

import avividi.com.Board;
import avividi.com.DayStage;
import avividi.com.hexgeometry.Grid;
import avividi.com.hexgeometry.PointAxial;
import avividi.com.item.DriedFireplantItem;
import avividi.com.item.Item;

import java.util.Optional;

public class FirePlant implements InteractingItem {

  private boolean linkedToTask = false;

  @Override
  public void endOfTurnAction(Board board, PointAxial self, DayStage stage) {
  }

  @Override
  public String getImageName() {
    return "fireplant";
  }

  @Override
  public boolean linkedToTask() {
    return linkedToTask;
  }

  @Override
  public void setLinkedToTask(boolean linked) {
    linkedToTask = linked;
  }
}
