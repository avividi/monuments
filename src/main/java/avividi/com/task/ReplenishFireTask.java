package avividi.com.task;

import avividi.com.AStar;
import avividi.com.Board;
import avividi.com.gameitems.Fire;
import avividi.com.gameitems.FirePlant;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ReplenishFireTask implements Task {

  private Fire fire;
  PointAxial fireLocation;
  List<AtomicTask> plan;
  private boolean isComplete = false;

  public ReplenishFireTask(Fire fire, PointAxial fireLocation) {
    this.fire = fire;
    this.fireLocation = fireLocation;
  }

  @Override
  public boolean planningAndFeasibility(Board board, PointAxial unitPos, Unit unit) {
    Optional<Hexagon<FirePlant>> plant = board.getOthers().getHexagons(FirePlant.class)
        .filter(p -> !p.getObj().linkedToTask())
        .min(Comparator.comparingInt(p -> PointAxial.distance(unitPos, p.getPosAxial())));
    if (!plant.isPresent()) return false;

    Optional<List<PointAxial>> toPlantOpt = new AStar(board).withOrigin(unitPos).withDestination(plant.get().getPosAxial())
        .get();

    if (!toPlantOpt.isPresent()) return false;

    List<PointAxial> toPlant = toPlantOpt.get();
    toPlant.remove(toPlant.size() - 1);
    PointAxial toFireStart = toPlant.get(toPlant.size()-1);

    plan = AtomicMoveTask.fromPoints(toPlant);
    plan.add(new CutFirePlantAtomicTask(plant.get().getObj(), plant.get().getPosAxial()));

    Optional<List<PointAxial>> toFireOpt = new AStar(board).withOrigin(toFireStart).withDestination(fireLocation)
        .get();

    if (!toFireOpt.isPresent()) return false;

    List<AtomicTask> toFire = AtomicMoveTask.fromPoints(toFireOpt.get());
    toFire.remove(toFire.size() - 1);

    plan.addAll(toFire);
    plan.add(new ReplenishFireAtomicTask(fire, fireLocation));

    return true;
  }

  @Override
  public void performStep(Board board, PointAxial self, Unit unit) {
    Preconditions.checkState(!plan.isEmpty());
    if (plan.get(0).perform(board, unit, self)) {
      plan.remove(0);
    }
    isComplete = plan.isEmpty();
  }

  @Override
  public boolean isComplete() {
    return isComplete;
  }

  @Override
  public int getPriority() {
    return 0;
  }
}
