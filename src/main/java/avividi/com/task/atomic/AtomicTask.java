package avividi.com.task.atomic;

import avividi.com.Board;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.PointAxial;

public interface AtomicTask {

  boolean perform (Board board, Unit unit, PointAxial unitPos);
}
