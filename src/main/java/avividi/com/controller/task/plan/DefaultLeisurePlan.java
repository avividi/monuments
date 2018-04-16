package avividi.com.controller.task.plan;

import avividi.com.controller.pathing.AStar;
import avividi.com.controller.Board;
import avividi.com.controller.task.atomic.RandomMoveTask;
import avividi.com.controller.util.DirectionTransformUtil;
import avividi.com.controller.gameitems.other.Fire;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.task.atomic.MaldarMoveTask;
import avividi.com.controller.task.atomic.Task;
import avividi.com.controller.task.atomic.NoOpTask;
import avividi.com.controller.util.RandomUtil;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DefaultLeisurePlan implements Plan {


  List<Task> plan = new ArrayList<>();

  @Override
  public boolean planningAndFeasibility(Board board, Hexagon<Unit> unit) {
    return false;
  }


  @Override
  public void performStep(Board board, Hexagon<Unit> unit) {
    if (plan.size() >= 1 && plan.get(0) instanceof NoOpTask) {
      plan.get(0).perform(board, unit);
      plan.remove(0);
      return;
    }

    Optional<Hexagon<Fire>> fire = board.getBurningFires().stream()
        .min(Hexagon.compareDistance(unit.getPosAxial()));

    if (!fire.isPresent() || PointAxial.distance(fire.get().getPosAxial(), unit.getPosAxial()) <= 2) {
      if (RandomUtil.get().nextDouble() > 0.97) {
        new RandomMoveTask().perform(board, unit);
      }
      plan.clear();
      return;
    }

    if (plan.isEmpty()) plan =
        MaldarMoveTask.fromPoints(findPath(board, unit.getPosAxial(), fire.get().getPosAxial()).orElse(new ArrayList<>()));
    if (!plan.isEmpty()) {
      if (plan.get(0).perform(board, unit)) {
        if (plan.get(0).isComplete()) plan.remove(0);
      }
      else {
        new RandomMoveTask().perform(board, unit);
        plan.clear();
      }
    }


    if (PointAxial.distance(fire.get().getPosAxial(), unit.getPosAxial()) <= 2) plan.clear();
  }

  @Override
  public void abort() {
    plan.clear();


  }

  private Optional<List<PointAxial>> findPath(Board board, PointAxial p1, PointAxial p2) {
    return AStar.builder()
        .withOrigin(p1)
        .withDestination(p2)
        .withIsPathable(board::hexIsPathAble)
        .get();
  }


  @Override
  public Task getNextAtomicTask() {
    return plan.isEmpty() ? null : plan.get(0);
  }

  @Override
  public void addNoOp() {
    plan.add(0, new NoOpTask());
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
