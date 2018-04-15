package avividi.com.controller.task.atomic;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.util.DirectionTransformUtil;
import avividi.com.controller.util.RandomUtil;
import com.google.common.base.Preconditions;

public class RandomMoveTask implements Task {

  private int steps = 1;
  private boolean isComplete = false;

  public RandomMoveTask() {

  }

  public RandomMoveTask(int steps) {
    this.steps = steps;
  }

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    if (--steps > 0) return true;
    isComplete = true;

    return randomMove(board, unit);
  }

  private boolean randomMove (Board board, Hexagon<Unit> unit) {
    PointAxial dir = PointAxial.allDirections.get(RandomUtil.get().nextInt(PointAxial.allDirections.size()));
    return board.hexIsFree(unit.getPosAxial().add(dir)) && makeMove(board, unit, dir);
  }

  private boolean makeMove (Board board, Hexagon<Unit> unit, PointAxial dir){
    PointAxial to = unit.getPosAxial().add(dir);
    unit.getObj().setTransform(DirectionTransformUtil.getTransform(dir));

    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
    Preconditions.checkState(board.getUnits().setHex(unit.getObj(), to));
    return true;
  }

  @Override
  public boolean shouldAbort() {
    return false;
  }

  @Override
  public boolean isComplete() {
    return isComplete;
  }
}
