package avividi.com.monuments.controller.gamehex.other;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.ItemTaker;
import avividi.com.monuments.controller.task.plan.Plan;
import avividi.com.monuments.controller.task.plan.SupplyItemPlan;
import avividi.com.monuments.hexgeometry.Grid;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import static avividi.com.monuments.controller.Ticks.TOthers.TFire.waitForReTask;

public class BuildMarker implements ItemTaker, Interactor {

  private final Class<? extends Item> itemType;
  private final int desiredAmount;
  private int currentAmount;
  private int reservedAmount;
  private final int buildTime;
  private final Supplier<Interactor> result;
  private int waitForReTaskCount;

  public BuildMarker(Class<? extends Item> itemType, int amount, int buildTime, Supplier<Interactor> result) {
    this.itemType = itemType;
    this.desiredAmount = amount;
    this.buildTime = buildTime;
    this.result = result;
  }

  @Override
  public Optional<Plan> checkForPlan(Grid<? extends GameHex> grid, PointAxial self) {

    waitForReTaskCount--;
    if (waitingFullCapacity()) return Optional.empty();

    if (waitForReTaskCount > 0) return Optional.empty();
    waitForReTaskCount = waitForReTask;

    return Optional.of(new SupplyItemPlan<>(new Hexagon<>(this, self, null), itemType, 1));
  };

  private boolean waitingFullCapacity () {
    return currentAmount + reservedAmount >= desiredAmount;
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {
    if (desiredAmount == currentAmount) {
      Interactor builtThing = result.get();
      if (!builtThing.passable()) board.setShouldCalculateSectors();
      board.getOthers().setHex(builtThing, self);
    }
  }

  @Override
  public boolean passable() {
    return true;
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of("build-marker");
  }

  @Override
  public void reserveDeliverItem(Class<? extends Item> itemType) {
    reservedAmount++;
    checkReservedAmount();
  }

  @Override
  public void unReserveDeliverItem(Class<? extends Item> itemType) {
    reservedAmount--;
    checkReservedAmount();
  }

  private void  checkReservedAmount() {
    Preconditions.checkState(reservedAmount + currentAmount <= desiredAmount);
    Preconditions.checkState(reservedAmount >= 0);
  }


  @Override
  public <T extends Item> boolean deliverItem(T item) {
    if (desiredAmount == currentAmount) return false;
    reservedAmount --;
    currentAmount++;
    return true;
  }

  @Override
  public Set<Class<? extends Item>> getSupportedDeliverItems() {
    return ImmutableSet.of(itemType);
  }

  @Override
  public int deliveryTime() {
    return buildTime;
  }
}
