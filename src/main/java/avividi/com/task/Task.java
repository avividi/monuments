package avividi.com.task;

import avividi.com.Board;
import avividi.com.gameitems.Maldar;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.PointAxial;

public interface Task {

  boolean planningAndFeasibility(Board board, PointAxial unitPos, Unit unit);
  void performStep (Board board, PointAxial unitPos, Unit unit);

  boolean isComplete();

  int getPriority();
}
