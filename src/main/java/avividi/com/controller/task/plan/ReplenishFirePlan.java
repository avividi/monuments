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
    //starts by finding a path from the units current position to the fire.
    //all though this path is not used, it saves looping through all plants paths in case the unit is blocked in.
   if (!findPath(board, unit.getPosAxial(), fire.getPosAxial()).isPresent()) return false;


    Optional<List<PointAxial>> unitToPlantPathOpt = board.getOther(FirePlant.class).stream()
        .filter(p -> !p.getObj().linkedToTask())
        .sorted(Hexagon.compareDistance(fire.getPosAxial()))
        .map(hex -> findPath(board, unit.getPosAxial(), hex.getPosAxial()))
        .filter(Optional::isPresent).map(Optional::get)
        .findFirst();
    if (!unitToPlantPathOpt.isPresent()) return false;


    List<PointAxial> unitToPlantPath = unitToPlantPathOpt.get();
    Hexagon<InteractingItem> firePlantHex = board.getOthers().getByAxial(unitToPlantPath.get(unitToPlantPath.size() -1))
        .get();
    firePlant = firePlantHex.getObj();

    Optional<List<PointAxial>> plantToFirePath = findPath(board, unit.getPosAxial(), firePlantHex.getPosAxial());
    if (!plantToFirePath.isPresent()) return false;

    List<PointAxial> firePath = plantToFirePath.get();
    firePath.remove(firePath.size() - 1);
    PointAxial toFireStart = firePath.get(firePath.size()-1);

    Optional<List<PointAxial>> toFireOpt = findPath(board, toFireStart, this.fire.getPosAxial());
    if (!toFireOpt.isPresent()) return false;

    List<Task> toFire = MaldarMoveTask.fromPoints(toFireOpt.get());
    toFire.remove(toFire.size() - 1);

    plan = MaldarMoveTask.fromPoints(firePath);
    plan.add(new CutFirePlantTask( firePlantHex));
    plan.addAll(toFire);
    plan.add(new ReplenishFireTask(this.fire));

    this.fire.getObj().setLinkedToTask(true);
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
    Preconditions.checkState(!plan.isEmpty());

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
    isComplete = true;
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
