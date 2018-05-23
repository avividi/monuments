package avividi.com.monuments.controller.task.plan;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.controller.item.food.FoodGiver;
import avividi.com.monuments.controller.pathing.AStar;
import avividi.com.monuments.controller.task.atomic.*;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.*;

public class EatPlan implements Plan {

  private Hexagon<Unit> unit;
  private Hexagon<FoodGiver> repo;
  private List<Task> plan = new ArrayList<>();
  private boolean isComplete = false;

  public EatPlan(Hexagon<Unit> unit) {
    this.unit = unit;
  }

  @Override
  public List<Hexagon<Unit>> chooseFromPool(Board board, Set<Hexagon<Unit>> pool) {
    return pool.contains(unit) ? ImmutableList.of(unit) : Collections.emptyList();
  }

  @Override
  public boolean planningAndFeasibility(Board board, Hexagon<Unit> unit) {

     repo = board.getFoodSources().stream()
         .filter(h -> board.isInSameSector(h.getPosAxial(), unit.getPosAxial()))
         .filter(h -> h.getObj().hasAvailableFood())
         .min(Hexagon.compareDistance(unit.getPosAxial()))
         .orElse(null);

     if (repo == null) return false;

     List<PointAxial> path = findPath(board, unit.getPosAxial(), repo.getPosAxial())
         .orElse(null);
     if (path == null) return false;

     if (!board.getOthers().getByAxial(repo.getPosAxial()).get().getObj().passable()) {
       path.remove(path.size() - 1);
     }

     plan.addAll(MaldarMoveTask.fromPoints(path));
     plan.add(new PickUpItemTask(repo.as(), repo.getObj().getItemPickupType()));
     plan.add(new EatTask());

     repo.getObj().reservePickUpItem(repo.getObj().getItemPickupType());

    return true;
  }

  private Optional<List<PointAxial>> findPath(Board board, PointAxial p1, PointAxial p2) {
    return AStar.builder()
        .withOrigin(p1)
        .withDestination(p2)
        .withIsPathable(board::hexIsPathAblePlanning)
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
    isComplete = plan.isEmpty();
  }

  @Override
  public void abort(Board board, PointAxial position) {
    repo.getObj().unReservePickUpItem(repo.getObj().getItemPickupType());
    plan.clear();
    isComplete = true;
    System.out.println("eat plan aborted");
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
    return 6;
  }
}
