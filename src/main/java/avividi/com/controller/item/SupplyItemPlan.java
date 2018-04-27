package avividi.com.controller.item;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.pathing.AStar;
import avividi.com.controller.task.atomic.Task;
import avividi.com.controller.task.plan.Plan;

import java.util.List;
import java.util.Optional;

public class SupplyItemPlan<T extends Item> implements Plan {

  private final Class<T> itemClass;
  private final Hexagon<? extends ItemTaker<T>> repository;
  private List<Task> plan;
  private boolean isComplete = false;

  private ItemGiver<T> supplier;

  public SupplyItemPlan (Hexagon<? extends ItemTaker<T>> repository) {
    this.repository = repository;
    this.itemClass = repository.getObj().getItemClass();
  }

  @Override
  public boolean planningAndFeasibility(Board board, Hexagon<Unit> unit) {
    //starts by finding a path from the units current position to the fire.
    //all though this path is not used, it saves looping through all plants paths in case the unit is blocked in.
    if (!findPath(board, unit.getPosAxial(), repository.getPosAxial()).isPresent()) return false;

    Optional<List<PointAxial>> unitToItemPath = board.getItemGiver(itemClass).stream()
        .filter(p -> ((ItemGiver<?>) p.getObj()).hasItem())
        .sorted(Hexagon.compareDistance(repository.getPosAxial()))
        .map(hex -> findPath(board, unit.getPosAxial(), hex.getPosAxial()))
        .filter(Optional::isPresent).map(Optional::get)
        .findFirst();
    if (!unitToItemPath.isPresent()) return false;

    List<PointAxial> unitToPlantPath = unitToItemPath.get();
    Hexagon<InteractingItem> supplierHex = board.getOthers().getByAxial(unitToPlantPath.get(unitToPlantPath.size() -1))
        .orElseThrow(IllegalStateException::new);
    supplier = (ItemGiver<T>) supplierHex.getObj();

    Optional<List<PointAxial>> supplierToRepositoryPathOpt = findPath(board, unit.getPosAxial(), supplierHex.getPosAxial());
    if (!supplierToRepositoryPathOpt.isPresent()) return false;

    List<PointAxial> supplierToRepositoryPath = supplierToRepositoryPathOpt.get();
    supplierToRepositoryPath.remove(supplierToRepositoryPath.size() - 1);//remove last so he doesn't step on the repository
    PointAxial toRepositoryPath = supplierToRepositoryPath.get(supplierToRepositoryPath.size()-1);
//
//    Optional<List<PointAxial>> toFireOpt = findPath(board, toRepositoryPath, this.fire.getPosAxial());
//    if (!toFireOpt.isPresent()) return false;
//
//    List<Task> toFire = MaldarMoveTask.fromPoints(toFireOpt.get());
//    toFire.remove(toFire.size() - 1);
//
//    plan = MaldarMoveTask.fromPoints(repositoryPath);
//    plan.add(new CutFirePlantTask( firePlantHex));
//    plan.addAll(toFire);
//    plan.add(new ReplenishFireTask(this.fire));
//
//    this.fire.getObj().setLinkedToTask(true);
//    firePlantHex.getObj().setLinkedToTask(true);
//
//    return true;

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
