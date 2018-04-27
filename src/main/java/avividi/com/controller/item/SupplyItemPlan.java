package avividi.com.controller.item;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.pathing.AStar;
import avividi.com.controller.task.atomic.Task;
import avividi.com.controller.task.plan.Plan;

import java.util.List;
import java.util.Optional;

public class SupplyItemPlan<T extends Item> implements Plan {

  private Hexagon<? extends ItemGiver<T>> supply;
  private final Hexagon<? extends ItemTaker<T>> repository;

  public SupplyItemPlan (Hexagon<? extends ItemTaker<T>> repository) {
    this.repository = repository;
  }

  @Override
  public boolean planningAndFeasibility(Board board, Hexagon<Unit> unit) {
    //starts by finding a path from the units current position to the fire.
    //all though this path is not used, it saves looping through all plants paths in case the unit is blocked in.
    if (!findPath(board, unit.getPosAxial(), supply.getPosAxial()).isPresent()) return false;

    return false;
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

  }

  @Override
  public void abort() {

  }

  @Override
  public Task getNextAtomicTask() {
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
