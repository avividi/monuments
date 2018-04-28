package avividi.com.controller.gameitems.other;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.gameitems.Interactor;
import avividi.com.controller.hexgeometry.Grid;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.item.Item;
import avividi.com.controller.item.ItemTaker;
import avividi.com.controller.item.SupplyItemPlan;
import avividi.com.controller.task.plan.Plan;
import avividi.com.generic.ReflectBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static avividi.com.controller.Ticks.TOthers.TFire.waitForReTask;

public class Plot implements Interactor, ItemTaker {

  private boolean passable = true;
  private final int capacity = 4;
  private int reservedStockCount = 0;
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
  public Optional<Plan> checkForPlan(Grid<? extends GameItem> grid, PointAxial self) {

    waitForReTaskCount--;
    if (waitingFullCapacity()) return Optional.empty();

    if (waitForReTaskCount-- > 0) return Optional.empty();
    waitForReTaskCount = waitForReTask;

    return Optional.of(new SupplyItemPlan<>(new Hexagon<>(this, self, null), itemType, 1));
  };

  private boolean waitingFullCapacity () {
    return reservedStockCount >= capacity;
  }


  @Override
  public List<String> getImageNames() {
    if (items.isEmpty()) return ImmutableList.of("plot");

    Item item = items.get(0);
    return ImmutableList.of("plot",
        String.join("-",
        String.join("/", item.getItemNameSpace(), item.getItemNameSpace()),
    "plot", String.valueOf(items.size()))
    );

  }

  @Override
  public boolean passable() {
    return passable;
  }

  @Override
  public void reserveDeliverItem(Class<? extends Item> itemType) {
    reservedStockCount++;
  }

  @Override
  public void unReserveDeliverItem(Class<? extends Item> itemType) {
    reservedStockCount--;
  }

  @Override
  public <T extends Item> boolean deliverItem(T item) {

    if (items.size() == capacity) return false;
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
}
