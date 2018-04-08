package avividi.com.task.atomic;

import avividi.com.Board;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;

public interface AtomicTask {

  boolean perform(Board board, Hexagon<Unit> unit);

  boolean abortSuggested();
}
