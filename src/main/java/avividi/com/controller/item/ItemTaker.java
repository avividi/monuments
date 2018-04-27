package avividi.com.controller.item;

import avividi.com.controller.hexgeometry.PointAxial;

import java.util.Optional;

public interface ItemTaker<T extends Item> {

  Optional<SupplyItemPlan<T>> checkForPlan(PointAxial self);

  boolean acceptsItems();

  void reserveTakeItem();
  void unReserveTakeItem();
  boolean giveItem(T item);

  Class<T> getItemClass();
}
