package avividi.com.monuments.controller.item;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;
import java.util.Set;

public abstract class SingleItemGiver implements ItemGiver {

  private static final int tick_defaultPickUpTime = 5;

  private boolean reserved = false;
  protected boolean alive = true;

  protected abstract Class<? extends Item> getItemType();
  protected abstract Item getItem();


  @Override
  public boolean hasAvailableItem(Class<? extends Item> itemType) {
    return !reserved && itemType.equals(getItemType());
  }

  @Override
  public void reservePickUpItem(Class<? extends Item> itemType) {
    Preconditions.checkState(itemType.equals(getItemType()));
    reserved = true;
  }

  @Override
  public void unReservePickUpItem(Class<? extends Item> itemType) {
    Preconditions.checkState(itemType.equals(getItemType()));
    reserved = false;
  }

  @Override
  public Optional<? extends Item> pickUpItem(Board board, PointAxial self, Class<? extends Item> itemType) {
    Preconditions.checkState(itemType.equals(getItemType()));
    if (alive) {
      Interactor thisInteractor = board.getOthers().clearHex(self);
      Preconditions.checkNotNull(thisInteractor);
      if (!thisInteractor.passable()) board.setShouldCalculateSectors();
      return Optional.of(getItem());
    }
    alive = false;
    return Optional.empty();
  }

  @Override
  public Set<Class<? extends Item>> getSupportedPickUpItems() {
    return ImmutableSet.of(getItemType());
  }

  @Override
  public int pickUpTime() {
    return tick_defaultPickUpTime;
  }




}
