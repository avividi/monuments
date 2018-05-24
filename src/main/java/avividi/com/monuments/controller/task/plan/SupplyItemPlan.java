package avividi.com.monuments.controller.task.plan;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.item.*;
import avividi.com.monuments.controller.pathing.AStar;
import avividi.com.monuments.controller.task.atomic.*;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SupplyItemPlan<T extends Item> implements Plan {

  private final Class<T> itemType;
  private final Hexagon<ItemTaker> repository;
  private List<Task> plan;
  private boolean isComplete = false;
  private final int priority;
  private Hexagon<ItemGiver> supplier;
  private Unit unit;
  private boolean stage2Reached = false;

  public SupplyItemPlan (Hexagon<ItemTaker> repository, Class<T> itemType, int priority) {
    this.repository = repository;
    this.itemType = itemType;
    this.priority = priority;
  }


  @Override
  public List<Hexagon<Unit>> chooseFromPool( Board board, Set<Hexagon<Unit>> pool) {

    return pool.stream()
        .filter(u -> board.isInSameSector(repository.getPosAxial(), u.getPosAxial()))
        .sorted(Hexagon.compareDistance(repository.getPosAxial()))
        .collect(Collectors.toList());
  }

  @Override
  public boolean planningAndFeasibility(Board board, Hexagon<Unit> unit) {
    List<Hexagon<ItemGiver>> givers = board.getItemGiver(itemType).stream()
        .filter(g -> board.isInSameSector(repository.getPosAxial(), g.getPosAxial()))
        .filter(p -> !p.getObj().getClass().equals(repository.getObj().getClass())) //don't deliver to same type
        .filter(p -> p.getObj().hasAvailableItem(itemType)).collect(Collectors.toList());
    if (givers.isEmpty()) return false;

    //find a path from the units current position to the fire.
    //all though this path is not used, it saves looping through all plants paths in case the unit is blocked in.
    if (!AStar.findPath(board, unit.getPosAxial(), repository.getPosAxial()).isPresent()) return false;

    Optional<List<PointAxial>> unitToItemPathOpt = givers.stream()
        .sorted(Hexagon.compareDistance(repository.getPosAxial()))
        .map(hex -> {
          supplier = hex;
          return AStar.findPath(board, unit.getPosAxial(), hex.getPosAxial());
        })
        .filter(Optional::isPresent).map(Optional::get)
        .findFirst();
    if (!unitToItemPathOpt.isPresent()) return false;

    List<PointAxial> unitToSupplierPath = unitToItemPathOpt.get();
    Preconditions.checkState(unitToItemPathOpt.get().get(unitToItemPathOpt.get().size() - 1).equals(supplier.getPosAxial()));
    if (!board.hexHasNoStaticObstructions(supplier.getPosAxial())) {
      unitToSupplierPath.remove(unitToSupplierPath.size() - 1);//remove last so he doesn't prepareOneTick on the supplier
    }
    PointAxial toRepoStart = unitToSupplierPath.get(unitToSupplierPath.size()-1);
    Optional<List<PointAxial>> supplierToRepoPathOpt = AStar.findPath(board, toRepoStart, repository.getPosAxial());
    if (!supplierToRepoPathOpt.isPresent()) return false;

    List<Task> supplierToRepoTask = MaldarMoveTask.fromPoints(supplierToRepoPathOpt.get());
    Preconditions.checkState(supplierToRepoPathOpt.get().get(supplierToRepoPathOpt.get().size() - 1).equals(repository.getPosAxial()));


    if (!board.hexHasNoStaticObstructions(repository.getPosAxial())) {
      supplierToRepoTask.remove(supplierToRepoTask.size() - 1);//remove last so he doesn't prepareOneTick on the repository
    }

    supplier.getObj().reservePickUpItem(itemType);
    repository.getObj().reserveDeliverItem(itemType);

    plan = MaldarMoveTask.fromPoints(unitToSupplierPath);
    plan.add(new PickUpItemTask(supplier, itemType));
    plan.addAll(supplierToRepoTask);
    plan.add(new DeliverItemTask(repository, itemType));

    this.unit = unit.getObj();
    return true;
  }

  @Override
  public void performStep(Board board, Hexagon<Unit> unit) {
    Preconditions.checkState(!plan.isEmpty());

    Task next = plan.get(0);

    stage2Reached = stage2Reached || next instanceof PickUpItemTask;

    if (next.perform(board, unit) && next.isComplete()) {
      plan.remove(0);
    }
    else if (next.shouldAbort()) abort(board, unit.getPosAxial());
    isComplete = plan.isEmpty();
  }

  @Override
  public void abort(Board board, PointAxial position) {
    unit.dropItem(board, position);
    repository.getObj().unReserveDeliverItem(itemType);
    if (!stage2Reached) supplier.getObj().unReservePickUpItem(itemType);
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
    return priority;
  }
}
