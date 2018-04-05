package avividi.com.gameitems;

import avividi.com.*;
import avividi.com.hexgeometry.PointAxial;
import avividi.com.task.DefaultLeisureTask;
import avividi.com.task.Task;

public class Maldar implements Unit {
  Task currentTask;

  @Override
  public void clickAction(Board board, PointAxial self) {
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

    if (currentTask == null) {
      currentTask = new DefaultLeisureTask();
    }
    currentTask.performStep(board, self, this);

  }
  @Override
  public String getImageName() {
    return "slave";
  }


  @Override
  public void assignTask(Task task) {

  }
}
