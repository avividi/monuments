package avividi.com.monuments.controller.item;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;

public abstract class SingleItemGiver implements ItemGiver {

  private static final int tick_defaultPickUpTime = 5;

  protected boolean reserved = false;
  protected boolean alive = true;

  protected abstract Item getItem();


  @Override
  public boolean hasAvailableItem(Class<? extends Item> itemType) {
    return !reserved && itemType.equals(getItemPickupType());
  }

  @Override
  public void reservePickUpItem(Class<? extends Item> itemType) {
    Preconditions.checkState(!reserved);
    Preconditions.checkState(itemType.equals(getItemPickupType()));
    reserved = true;
  }

  @Override
  public void unReservePickUpItem(Class<? extends Item> itemType) {
    Preconditions.checkState(reserved);
    Preconditions.checkState(itemType.equals(getItemPickupType()));
    reserved = false;
  }

  @Override
  public Optional<? extends Item> pickUpItem(Board board, PointAxial self, Class<? extends Item> itemType) {
    Preconditions.checkState(itemType.equals(getItemPickupType()));
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
  public abstract Class<? extends Item> getItemPickupType();
  @Override
  public int pickUpTime() {
    return tick_defaultPickUpTime;
  }




}
