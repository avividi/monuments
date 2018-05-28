package avividi.com.monuments.controller.gamehex.other;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.item.BoulderItem;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.ItemGiver;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

public class InfiniteQuarry implements Interactor, ItemGiver {
  @Override
  public void everyTickAction(Board board, PointAxial self) {

  }

  @Override
  public boolean passable() {
    return true;
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of("quarry");
  }

  @Override
  public boolean hasAvailableItem(Class<? extends Item> itemType) {
    return true;
  }

  @Override
  public void reservePickUpItem(Class<? extends Item> itemType) {

  }

  @Override
  public void unReservePickUpItem(Class<? extends Item> itemType) {

  }

  @Override
  public Optional<? extends Item> pickUpItem(Board board, PointAxial self, Class<? extends Item> item) {
    return Optional.of(new BoulderItem());
  }

  @Override
  public Class<? extends Item> getItemPickupType() {
    return BoulderItem.class;
  }

  @Override
  public int pickUpTime() {
    return 25;
  }
}
