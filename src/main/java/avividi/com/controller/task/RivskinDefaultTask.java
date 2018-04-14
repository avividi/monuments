package avividi.com.controller.task;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.pathing.AStar;
import avividi.com.controller.task.atomic.AtomicMoveTask;
import avividi.com.controller.task.atomic.AtomicTask;
import avividi.com.controller.util.DirectionTransformUtil;
import avividi.com.controller.util.RandomUtil;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RivskinDefaultTask implements Task {

  List<AtomicTask> plan = new ArrayList<>();


  @Override
  public boolean planningAndFeasibility(Board board, Hexagon<Unit> unit) {
return true;

  }

  private Optional<List<PointAxial>> findPath(Board board, PointAxial p1, PointAxial p2) {
    return new AStar(board).withOrigin(p1).withDestination(p2)
        .get();
  }

  private void randomMove (Board board, Hexagon<Unit> unit) {
    PointAxial dir = PointAxial.allDirections.get(RandomUtil.get().nextInt(PointAxial.allDirections.size()));
    PointAxial newPos = unit.getPosAxial().add(dir);
    if (board.hexIsFree(newPos)) {
      makeMove(board, unit, dir);
    }
    if (!board.getGround().getByAxial(newPos).isPresent()) {
      Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
    }
  }

  private void makeMove (Board board, Hexagon<Unit> unit, PointAxial dir){
    PointAxial to = unit.getPosAxial().add(dir);
    unit.getObj().setTransform(DirectionTransformUtil.getTransform(dir));
    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
    Preconditions.checkState(board.getUnits().setHex(unit.getObj(), to));
  }

  @Override
  public void performStep(Board board, Hexagon<Unit> unit) {

    if (plan.isEmpty()) {
      PointAxial edge = board.getSpawnEdges().get(RandomUtil.get().nextInt(board.getSpawnEdges().size()));
      plan = AtomicMoveTask.fromPoints(findPath(board, unit.getPosAxial(), edge).orElse(new ArrayList<>()));
    }

  }

  @Override
  public AtomicTask getNextAtomicTask() {
    return null;
  }

  @Override
  public void addNoOp() {

  }

  @Override
  public boolean isComplete() {
    return false;
  }

  @Override
  public int getPriority() {
    return 0;
  }
}
