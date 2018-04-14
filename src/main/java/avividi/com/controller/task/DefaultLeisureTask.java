package avividi.com.controller.task;

import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.pathing.AStar;
import avividi.com.controller.Board;
import avividi.com.controller.util.DirectionTransformUtil;
import avividi.com.controller.gameitems.other.Fire;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.task.atomic.AtomicMoveTask;
import avividi.com.controller.task.atomic.AtomicTask;
import avividi.com.controller.task.atomic.NoOpAtomicTask;
import avividi.com.controller.util.RandomUtil;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultLeisureTask implements Task {


  List<AtomicTask> plan = new ArrayList<>();

  @Override
  public boolean planningAndFeasibility(Board board, Hexagon<Unit> unit) {
    return false;
  }


  @Override
  public void performStep(Board board, Hexagon<Unit> unit) {
    if (plan.size() >= 1 && plan.get(0) instanceof NoOpAtomicTask) {
      plan.get(0).perform(board, unit);
      plan.remove(0);
      return;
    }

    Optional<Hexagon<Fire>> fire = board.getBurningFires().stream()
        .min(Hexagon.compareDistance(unit.getPosAxial()));

    if (!fire.isPresent() || PointAxial.distance(fire.get().getPosAxial(), unit.getPosAxial()) <= 2) {
      if (RandomUtil.get().nextDouble() > 0.97) {
        randomMove(board, unit);
      }
      plan.clear();
      return;
    }

    if (plan.isEmpty()) plan =
        AtomicMoveTask.fromPoints(findPath(board, unit.getPosAxial(), fire.get().getPosAxial()).orElse(new ArrayList<>()));
    if (!plan.isEmpty()) {
      if (plan.get(0).perform(board, unit)) {
        if (plan.get(0).isComplete()) plan.remove(0);
      }
      else {
        randomMove(board, unit);
        plan.clear();
      }
    }


    if (PointAxial.distance(fire.get().getPosAxial(), unit.getPosAxial()) <= 2) plan.clear();
  }


  @Override
  public AtomicTask getNextAtomicTask() {
    return plan.isEmpty() ? null : plan.get(0);
  }

  @Override
  public void addNoOp() {
    plan.add(0, new NoOpAtomicTask());
  }

  private Optional<List<PointAxial>> findPath(Board board, PointAxial p1, PointAxial p2) {
    return new AStar(board).withOrigin(p1).withDestination(p2)
        .get();
  }


  private void randomMove (Board board, Hexagon<Unit> unit) {
    PointAxial dir = PointAxial.allDirections.get(RandomUtil.get().nextInt(PointAxial.allDirections.size()));
    if (board.hexIsFree(unit.getPosAxial().add(dir))) makeMove(board, unit, dir);
  }

  private void makeMove (Board board, Hexagon<Unit> unit, PointAxial dir){
//    Preconditions.checkState(board.getUnits().getByAxial(unit.getPosAxial()).filter(u -> u.getObj() == unit.getObj()).isPresent());
    PointAxial to = unit.getPosAxial().add(dir);

    unit.getObj().setTransform(DirectionTransformUtil.getTransform(dir));

//    Preconditions.checkState(board.getUnits().clearHex(to) == null);

    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
    Preconditions.checkState(board.getUnits().setHex(unit.getObj(), to));
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
