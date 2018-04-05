package avividi.com.task;

import avividi.com.Board;
import avividi.com.gameitems.Maldar;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.PointAxial;

public interface Task {

  void performStep (Board board, PointAxial self, Unit unit);

  boolean isComplete();

  int getPriority();
}
