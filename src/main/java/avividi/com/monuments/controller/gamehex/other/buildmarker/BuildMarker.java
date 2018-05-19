package avividi.com.monuments.controller.gamehex.other.buildmarker;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.ItemTaker;
import avividi.com.monuments.controller.task.plan.Plan;
import avividi.com.monuments.controller.task.plan.SupplyItemPlan;
import avividi.com.monuments.controller.userinput.UserAction;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

import static avividi.com.monuments.controller.TickConstants.tick_waitForReTask;

public abstract class BuildMarker implements ItemTaker, Interactor {

  private final Class<? extends Item> itemType;
  private final int desiredAmount;
  private int currentAmount;
  private int reservedAmount;
  private final int buildTime;
  private final int priority;
  private int waitForReTaskCount;

  public BuildMarker(Class<? extends Item> itemType, int amount, int buildTime, int priority) {
    this.itemType = itemType;
    this.desiredAmount = amount;
    this.buildTime = buildTime;
    this.priority = priority;
  }

  @Override
  public Optional<Plan> checkForPlan(Board board, PointAxial self) {

    waitForReTaskCount--;
    if (assigned()) return Optional.empty();

    if (waitForReTaskCount > 0) return Optional.empty();
    waitForReTaskCount = tick_waitForReTask;

    return Optional.of(new SupplyItemPlan<>(new Hexagon<>(this, self, null), itemType, priority));
  };

  private boolean assigned() {
    return reservedAmount > 0 || currentAmount >= desiredAmount;
  }

  @Override
  abstract public void everyTickAction(Board board, PointAxial self);

  private void cancel (Board board, PointAxial self) {
    board.getOthers().clearHex(self);
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
  public Class<? extends Item> getDeliverItemType() {
    return itemType;
  }

  @Override
  public int deliveryTime() {
    return buildTime;
  }

  protected boolean fullFilled() {
    return desiredAmount == currentAmount;
  }

  @Override
  public void doUserAction(UserAction action, Board board, PointAxial self) {
    if (action == UserAction.cancel) this.cancel(board, self);
  }

  @Override
  public List<UserAction> getUserActions() {
    return ImmutableList.of(UserAction.cancel);
  }
}
