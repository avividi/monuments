package avividi.com.controller.item;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.pathing.AStar;
import avividi.com.controller.task.atomic.MaldarMoveTask;
import avividi.com.controller.task.atomic.NoOpTask;
import avividi.com.controller.task.atomic.Task;
import avividi.com.controller.task.plan.Plan;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Optional;

public class SupplyItemPlan<T extends Item> implements Plan {

  private final Class<T> itemType;
  private final Hexagon<ItemTaker> repository;
  private List<Task> plan;
  private boolean isComplete = false;

  private Hexagon<ItemGiver> supplier;

  public SupplyItemPlan (Hexagon<ItemTaker> repository, Class<T> itemType) {
    this.repository = repository;
    this.itemType = itemType;
  }

  @Override
  public boolean planningAndFeasibility(Board board, Hexagon<Unit> unit) {
    Preconditions.checkState(repository.getObj().acceptsItems(itemType));
    //starts by finding a path from the units current position to the fire.
    //all though this path is not used, it saves looping through all plants paths in case the unit is blocked in.
    if (!findPath(board, unit.getPosAxial(), repository.getPosAxial()).isPresent()) return false;

    Optional<List<PointAxial>> unitToItemPathOpt = board.getItemGiver(itemType).stream()
        .filter(p -> p.getObj() != repository.getObj()) //don't deliver to itself
        .filter(p -> p.getObj().hasAvailableItem(itemType))
        .sorted(Hexagon.compareDistance(repository.getPosAxial()))
        .map(hex -> {
          supplier = hex;
          return findPath(board, unit.getPosAxial(), hex.getPosAxial());
        })
        .filter(Optional::isPresent).map(Optional::get)
        .findFirst();
    if (!unitToItemPathOpt.isPresent()) return false;

    List<PointAxial> unitToSupplierPath = unitToItemPathOpt.get();
    unitToSupplierPath.remove(unitToSupplierPath.size() - 1);//remove last so he doesn't step on the repository

    PointAxial toRepoStart = unitToSupplierPath.get(unitToSupplierPath.size()-1);
    Optional<List<PointAxial>> supplierToRepoPathOpt = findPath(board, toRepoStart, repository.getPosAxial());
    if (!supplierToRepoPathOpt.isPresent()) return false;
//
    List<Task> supplierToRepoTask = MaldarMoveTask.fromPoints(supplierToRepoPathOpt.get());
    supplierToRepoTask.remove(supplierToRepoTask.size() - 1);


    supplier.getObj().reservePickUpItem(itemType);
    repository.getObj().reserveDeliverItem(itemType);
//
    plan = MaldarMoveTask.fromPoints(unitToSupplierPath);
    plan.add(new PickUpItemTask(supplier, itemType));
    plan.addAll(supplierToRepoTask);
    plan.add(new DeliverItemTask(repository, itemType));

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
    repository.getObj().unReserveDeliverItem(itemType);
    supplier.getObj().unReservePickUpItem(itemType);
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
