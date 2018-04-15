package avividi.com.controller.task.plan;

import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.pathing.AStar;
import avividi.com.controller.Board;
import avividi.com.controller.gameitems.other.FirePlant;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.task.atomic.*;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ReplenishFirePlan implements Plan {

  private Hexagon<InteractingItem> fire;
  private InteractingItem firePlant;
  private List<Task> plan;
  private boolean isComplete = false;

  public ReplenishFirePlan(Hexagon<InteractingItem> fire) {
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
    Optional<Hexagon<InteractingItem>> plantOpt = board.getOther(FirePlant.class).stream()
        .filter(p -> !p.getObj().linkedToTask())
        .min(Hexagon.compareDistance(fire.getPosAxial()));
    if (!plantOpt.isPresent()) return false;

    Hexagon<InteractingItem> firePlantHex = plantOpt.get();
    firePlant = firePlantHex.getObj();
    Optional<List<PointAxial>> toPlantOpt = findPath(board, unit.getPosAxial(), firePlantHex.getPosAxial());
    if (!toPlantOpt.isPresent()) return false;

    List<PointAxial> toPlant = toPlantOpt.get();
    toPlant.remove(toPlant.size() - 1);
    PointAxial toFireStart = toPlant.get(toPlant.size()-1);

    Optional<List<PointAxial>> toFireOpt = findPath(board, toFireStart, fire.getPosAxial());
    if (!toFireOpt.isPresent()) return false;

    List<Task> toFire = MaldarMoveTask.fromPoints(toFireOpt.get());
    toFire.remove(toFire.size() - 1);

    plan = MaldarMoveTask.fromPoints(toPlant);
    plan.add(new CutFirePlantTask( firePlantHex));
    plan.addAll(toFire);
    plan.add(new ReplenishFireTask(fire));

    fire.getObj().setLinkedToTask(true);
    firePlantHex.getObj().setLinkedToTask(true);

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
    if (plan.isEmpty()) return;
//    Preconditions.checkState(!plan.isEmpty());

    Task next = plan.get(0);

    if (next.perform(board, unit) && next.isComplete()) plan.remove(0);
    else if (next.shouldAbort()) abort();
    isComplete = plan.isEmpty();
  }

  @Override
  public void abort() {
    fire.getObj().setLinkedToTask(false);
    firePlant.setLinkedToTask(false);
    plan.clear();
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
    return 2;
  }
}
