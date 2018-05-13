package avividi.com.monuments.controller.task.plan;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.other.Fire;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.controller.pathing.AStar;
import avividi.com.monuments.controller.task.atomic.*;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ReviveFirePlan implements Plan {


  private final Hexagon<Fire> fire;
  private List<Task> plan = new ArrayList<>();
  private boolean isComplete = false;

  @Override
  public List<Hexagon<Unit>> chooseFromPool(Board board, Set<Hexagon<Unit>> pool) {

    return pool.stream()
        .filter(u -> board.isInSameSector(fire.getPosAxial(), u.getPosAxial()))
        .sorted(Hexagon.compareDistance(fire.getPosAxial()))
        .collect(Collectors.toList());
  }

  public ReviveFirePlan(Hexagon<Fire> fire) {
    this.fire = fire;
  }

  @Override
  public boolean planningAndFeasibility(Board board, Hexagon<Unit> unit) {

    if (!unit.getPosAxial().equals(fire.getPosAxial())) {
      Optional<List<PointAxial>> path = findPath(board, unit.getPosAxial(), fire.getPosAxial());
      if (!path.isPresent()) return false;
      plan = MaldarMoveTask.fromPoints(path.get());
    }


    plan.add(new ReviveFireTask(fire));

    this.fire.getObj().setReviving(true);

    return true;
  }

  private Optional<List<PointAxial>> findPath(Board board, PointAxial p1, PointAxial p2) {
    return AStar.builder()
        .withOrigin(p1)
        .withDestination(p2)
        .withIsPathable(board::hexIsPathAble)
        .get();
  }

  @Override
  public void performStep(Board board, Hexagon<Unit> unit) {
    Preconditions.checkState(!plan.isEmpty());

    Task next = plan.get(0);

    if (next.perform(board, unit) && next.isComplete()) {
      plan.remove(0);
    }
    else if (next.shouldAbort()) abort(board, unit.getPosAxial());

    if (plan.size() == 1 && plan.get(0) instanceof ReviveFireTask) {
      moveAwayFromFire(board).ifPresent(t -> plan.add(t));
    }
    isComplete = plan.isEmpty();
  }

  private Optional<SimpleMoveTask> moveAwayFromFire(Board board) {
    Optional<PointAxial> dir = PointAxial.allDirections.stream().filter(board::hexIsFree).findAny();
    if (!dir.isPresent()) dir = PointAxial.allDirections.stream().filter(board::hexIsPathAble).findAny();
    return dir.map(d -> new SimpleMoveTask(d, 1));
  }

  @Override
  public void abort(Board board, PointAxial position) {
    plan.clear();
    isComplete = true;
    this.fire.getObj().setReviving(false);
    System.out.println("plan aborted");
  }

  @Override
  public Task getNextAtomicTask() {
    return plan.get(0);
  }

  @Override
  public void addNoOp() {
    plan.add(0, new NoOpTask());

  }

  @Override
  public boolean isComplete() {
    return isComplete;
  }

  @Override
  public int getPriority() {
    return 5;
  }
}