package avividi.com.controller.gameitems;

import avividi.com.controller.Board;
import avividi.com.controller.DayStage;
import avividi.com.controller.hexgeometry.PointAxial;

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
