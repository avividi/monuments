package avividi.com.controller.item;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import java.util.Optional;
import java.util.Set;

import static avividi.com.controller.Ticks.TOthers.TSingleItemGiver.defaultPickUpTime;

public abstract class SingleItemGiver implements ItemGiver {

  private boolean reserved = false;
  private boolean alive = true;

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
  public Optional<? extends Item> pickUpItem(Class<? extends Item> itemType) {
    Preconditions.checkState(itemType.equals(getItemType()));
    if (alive) return Optional.of(getItem());
    alive = false;
    return Optional.empty();
  }

  @Override
  public Set<Class<? extends Item>> getSupportedPickUpItems() {
    return ImmutableSet.of(getItemType());
  }

  @Override
  public int pickUpTime() {
    return defaultPickUpTime;
  }


}
