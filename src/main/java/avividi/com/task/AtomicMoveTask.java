package avividi.com.task;

import avividi.com.Board;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AtomicMoveTask implements AtomicTask {

  private final PointAxial dir;

  public AtomicMoveTask(PointAxial dir) {
    this.dir = dir;
  }

  @Override
  public boolean perform(Board board, Unit unit, PointAxial unitPos) {

    PointAxial newPos = unitPos.add(dir);

    if (!board.hexIsFree(newPos)) {
      return false;
    }

    Preconditions.checkNotNull(board.getUnits().clearHex(unitPos));
    Preconditions.checkState(board.getUnits().setHex(unit, unitPos.add(dir)));

    return true;
  }

  public static List<AtomicTask> fromPoints(List<PointAxial> pointAxials) {
    List<AtomicTask> tasks = new ArrayList<>();
    PointAxial prev = null;
    for (PointAxial p : pointAxials) {
       if (prev != null) {
         tasks.add(new AtomicMoveTask(PointAxial.getDirection(prev, p)));
       }
       prev = p;
    }
    return tasks;
  }
}
