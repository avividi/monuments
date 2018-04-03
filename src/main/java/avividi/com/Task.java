package avividi.com;

import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.PointAxial;

public interface Task {

  void performStep (Board board, PointAxial self, Unit unit);

  boolean isComplete();
}
