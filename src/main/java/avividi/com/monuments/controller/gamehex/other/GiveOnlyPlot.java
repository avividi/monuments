package avividi.com.monuments.controller.gamehex.other;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.ItemGiver;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.*;

public class GiveOnlyPlot implements Interactor, ItemGiver {

  private int reservedPickUpStockCount = 0;

  private final Class<? extends Item> itemType;
  private final List<Item> items;

  public GiveOnlyPlot(Class<? extends Item> itemType, List<Item> items) {
    this.itemType = itemType;
    this.items = items;
  }

  @Override
  public void everyTickAction(Board board, PointAxial self) {

  }

  @Override
  public List<String> getImageNames() {
    if (items.isEmpty()) return Collections.emptyList();

    Item item = items.get(0);
    return ImmutableList.of(
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
    if (items.size() == 1) board.getOthers().clearHex(self);
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
}
