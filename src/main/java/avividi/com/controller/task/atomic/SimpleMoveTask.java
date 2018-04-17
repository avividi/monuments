package avividi.com.controller.task.atomic;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.util.DirectionTransformUtil;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;

import static avividi.com.controller.Ticks.TTask.TSimpleMoveTask.defaultTime;

public class SimpleMoveTask implements Task {

  private final PointAxial dir;
  private boolean isComplete = false;
  private boolean shouldAbort = false;
  private int timeCount;

  public SimpleMoveTask(PointAxial dir) {
    this.dir = dir;
    timeCount = defaultTime;
  }

  public SimpleMoveTask(PointAxial dir, int timeCount) {
    this.dir = dir;
    this.timeCount = timeCount;
  }
  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    if (--timeCount > 0) return true;

    PointAxial newPos = unit.getPosAxial().add(dir);

    if (board.getUnits().getByAxial(newPos).isPresent()) {
      shouldAbort = true;
      return false;
    }

    isComplete = true;

    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));

    unit.getObj().setTransform(DirectionTransformUtil.getTransform(dir));
    Preconditions.checkState(board.getUnits().setHex(unit.getObj(), newPos));

    return true;
  }

  @Override
  public boolean shouldAbort() {
    return shouldAbort;
  }

  @Override
  public boolean isComplete() {
    return isComplete;
  }

  public static List<Task> fromPoints(List<PointAxial> pointAxials, int steps) {
    List<Task> tasks = new ArrayList<>();
    PointAxial prev = null;
    for (PointAxial p : pointAxials) {
      if (prev != null) {
        tasks.add(new SimpleMoveTask(PointAxial.getDirection(prev, p), steps));
      }
      prev = p;
    }
    return tasks;
  }
}
