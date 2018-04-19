package avividi.com.controller.gameitems.other;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.hexgeometry.PointAxial;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class FirePlant implements InteractingItem {

  private boolean linkedToTask = false;

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {
  }

  @Override
  public List<String> getImageName() {
    return ImmutableList.of("fireplant");
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
