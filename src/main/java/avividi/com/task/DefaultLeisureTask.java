package avividi.com.task;

import avividi.com.AStar;
import avividi.com.Board;
import avividi.com.gameitems.Fire;
import avividi.com.gameitems.InteractingItem;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;
import avividi.com.task.atomic.AtomicMoveTask;
import avividi.com.task.atomic.AtomicTask;
import avividi.com.task.atomic.NoOpAtomicTask;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class DefaultLeisureTask implements Task {

  Random random = new Random();

  List<AtomicTask> plan = new ArrayList<>();

  @Override
  public boolean planningAndFeasibility(Board board, Hexagon<Unit> unit) {
    return false;
  }

  @Override
  public void performStep(Board board, Hexagon<Unit> unit) {
    Optional<Hexagon<Fire>> fire = board.getOthers().getHexagons(Fire.class)
        .min(Hexagon.compareDistance(unit.getPosAxial()));

    if (!fire.isPresent() || PointAxial.distance(fire.get().getPosAxial(), unit.getPosAxial()) <= 2) {
      if (random.nextDouble() > 0.90) {
        randomMove(board, unit);
        plan.clear();
      }
      return;
    }

    if (plan.isEmpty()) plan =
        AtomicMoveTask.fromPoints(findPath(board, unit.getPosAxial(), fire.get().getPosAxial()).orElse(new ArrayList<>()));
    if (!plan.isEmpty()) {
      if (plan.get(0).perform(board, unit)) {
        plan.remove(0);
      }
      else {
//        randomMove(board, unit, self);
      }
    }
  }

  @Override
  public AtomicTask getNextAtomicTask() {
    return plan.get(0);
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
    PointAxial dir = PointAxial.allDirections.get(random.nextInt(PointAxial.allDirections.size()));
    if (board.hexIsFree(unit.getPosAxial().add(dir))) makeMove(board, unit, unit.getPosAxial().add(dir));
  }

  private void makeMove (Board board, Hexagon<Unit> unit, PointAxial to){
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
