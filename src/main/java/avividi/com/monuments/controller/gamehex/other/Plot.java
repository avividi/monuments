package avividi.com.monuments.controller.gamehex.other;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.userinput.UserAction;
import avividi.com.monuments.hexgeometry.Grid;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.ItemGiver;
import avividi.com.monuments.controller.item.ItemTaker;
import avividi.com.monuments.controller.task.plan.SupplyItemPlan;
import avividi.com.monuments.controller.task.plan.Plan;
import avividi.com.monuments.generic.ReflectBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static avividi.com.monuments.controller.Ticks.TOthers.TFire.waitForReTask;

public class Plot implements Interactor, ItemTaker, ItemGiver {

  private final int capacity = 4;
  private int reservedDeliverStockCount = 0;
  private int reservedPickUpStockCount = 0;

  private Class<? extends Item> itemType;
  private List<Item> items = new ArrayList<>();
  private int waitForReTaskCount;

  public Plot(Class<? extends Item> itemType) {
    this.itemType = itemType;
  }

  public Plot(ObjectNode json) {
    itemType = ReflectBuilder.getClassByName((json.get("itemType").asText())).asSubclass(Item.class);
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

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
    return items.size() + reservedDeliverStockCount >= capacity;
  }


  @Override
  public List<String> getImageNames() {
    if (items.isEmpty()) return ImmutableList.of("plot");

    Item item = items.get(0);
    return ImmutableList.of("plot",
        String.join("-",
        String.join("/", item.getItemImageNameSpace(), item.getItemImageNameSpace()),
    "plot", String.valueOf(items.size()))
    );

  }

  @Override
  public boolean passable() {
    return true;
  }

  @Override
  public void reserveDeliverItem(Class<? extends Item> itemType) {
    reservedDeliverStockCount++;
    checkReservedDeliverStockCount();
  }

  @Override
  public void unReserveDeliverItem(Class<? extends Item> itemType) {
    reservedDeliverStockCount--;
    checkReservedDeliverStockCount();

  }

  private void  checkReservedDeliverStockCount() {
    Preconditions.checkState(reservedDeliverStockCount + items.size() <= capacity);
    Preconditions.checkState(reservedDeliverStockCount >= 0);
  }

  @Override
  public <T extends Item> boolean deliverItem(T item) {

    if (items.size() == capacity) return false;
    reservedDeliverStockCount --;
    items.add(item);
    return true;
  }

  @Override
  public Set<Class<? extends Item>> getSupportedDeliverItems() {
    return ImmutableSet.of(itemType);
  }

  @Override
  public int deliveryTime() {
    return 4;
  }

  @Override
  public boolean hasAvailableItem(Class<? extends Item> itemType) {
    return items.size() - reservedPickUpStockCount > 0;
  }

  @Override
  public void reservePickUpItem(Class<? extends Item> itemType) {
    reservedPickUpStockCount++;
    checkReservedPickUpStockCount();
  }

  @Override
  public void unReservePickUpItem(Class<? extends Item> itemType) {
    reservedPickUpStockCount--;
    checkReservedPickUpStockCount();
  }


  private void  checkReservedPickUpStockCount() {
    Preconditions.checkState(reservedPickUpStockCount <= items.size());
    Preconditions.checkState(reservedPickUpStockCount >= 0);
  }

  @Override
  public Optional<? extends Item> pickUpItem(Board board, PointAxial self, Class<? extends Item> item) {
    if (items.isEmpty()) return Optional.empty();

    reservedPickUpStockCount--;
    checkReservedPickUpStockCount();
    return Optional.of(items.remove(0));
  }

  @Override
  public Set<Class<? extends Item>> getSupportedPickUpItems() {
    return ImmutableSet.of(itemType);
  }

  @Override
  public int pickUpTime() {
    return 5;
  }

  @Override
  public void doUserAction(UserAction action, Board board, PointAxial self) {
    if (action == UserAction.cancel) {
      board.getOthers().setHex(new GiveOnlyPlot(this.itemType, this.items), self);
    }
  }

  @Override
  public List<UserAction> getUserActions() {
    return ImmutableList.of(UserAction.cancel);
  }
}
