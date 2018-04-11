package avividi.com.controller.task;

import avividi.com.controller.AStar;
import avividi.com.controller.Board;
import avividi.com.controller.gameitems.Fire;
import avividi.com.controller.gameitems.FirePlant;
import avividi.com.controller.gameitems.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.task.atomic.*;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ReplenishFireTask implements Task {

  private Hexagon<Fire> fire;
  private List<AtomicTask> plan;
  private boolean isComplete = false;

  public ReplenishFireTask(Hexagon<Fire> fire) {
    this.fire = fire;
  }

  @Override
  public List<Hexagon<Unit>> chooseFromPool(Set<Hexagon<Unit>> pool) {
    return pool.stream()
        .sorted(Hexagon.compareDistance(fire.getPosAxial()))
        .collect(Collectors.toList());
  }

  @Override
  public boolean planningAndFeasibility(Board board, Hexagon<Unit> unit) {
    Optional<Hexagon<FirePlant>> plantOpt = board.getOthers().getHexagons(FirePlant.class)
        .filter(p -> !p.getObj().linkedToTask())
        .min(Hexagon.compareDistance(fire.getPosAxial()));
    if (!plantOpt.isPresent()) return false;

    Hexagon<FirePlant> plant = plantOpt.get();
    Optional<List<PointAxial>> toPlantOpt = findPath(board, unit.getPosAxial(), plant.getPosAxial());
    if (!toPlantOpt.isPresent()) return false;

    List<PointAxial> toPlant = toPlantOpt.get();
    toPlant.remove(toPlant.size() - 1);
    PointAxial toFireStart = toPlant.get(toPlant.size()-1);

    Optional<List<PointAxial>> toFireOpt = findPath(board, toFireStart, fire.getPosAxial());
    if (!toFireOpt.isPresent()) return false;

    List<AtomicTask> toFire = AtomicMoveTask.fromPoints(toFireOpt.get());
    toFire.remove(toFire.size() - 1);

    plan = AtomicMoveTask.fromPoints(toPlant);
    plan.add(new CutFirePlantAtomicTask(plant));
    plan.addAll(toFire);
    plan.add(new ReplenishFireAtomicTask(fire));

    fire.getObj().setLinkedToTask(true);
    plant.getObj().setLinkedToTask(true);

    return true;
  }

  private Optional<List<PointAxial>> findPath(Board board, PointAxial p1, PointAxial p2) {
    return new AStar(board).withOrigin(p1).withDestination(p2)
        .get();
  }


  @Override
  public void performStep(Board board, Hexagon<Unit> unit) {
    Preconditions.checkState(!plan.isEmpty());
    if (plan.get(0).perform(board, unit)) {
      plan.remove(0);
    }
    else {
      if (plan.get(0).abortSuggested()) {
        plan.clear();
        System.out.println("plan aborted");
      }
    }
    isComplete = plan.isEmpty();
  }

  @Override
  public AtomicTask getNextAtomicTask() {
    return plan.get(0);
  }

  @Override
  public void addNoOp() {
    plan.add(0, new NoOpAtomicTask());
  }

  @Override
  public boolean isComplete() {
    return isComplete;
  }

  @Override
  public int getPriority() {
    return 2;
  }
}
