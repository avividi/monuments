package avividi.com.monuments.controller.task.atomic;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.AxialDirection;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.util.DirectionTransformUtil;
import avividi.com.monuments.controller.util.RandomUtil;
import com.google.common.base.Preconditions;

import java.util.Arrays;

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

  //todo this won't work for up down still
  private boolean randomMove (Board board, Hexagon<Unit> unit) {
    AxialDirection dir = Arrays.asList(PointAxial.allDirections).get(RandomUtil.get().nextInt(PointAxial.allDirections.length));
    return board.hexIsPathAble(unit.getPosAxial(), dir) && makeMove(board, unit, dir.dir);
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
