package avividi.com.controller.item;

import avividi.com.controller.hexgeometry.PointAxial;

import java.util.Optional;
import java.util.Set;

public interface ItemTaker {

  boolean acceptsItems(Class<? extends Item> itemType);

  void reserveDeliverItem(Class<? extends Item> itemType);
  void unReserveDeliverItem(Class<? extends Item> itemType);
  <T extends Item> boolean deliverItem(T item);

  Set<Class<? extends Item>> getSupportedDeliverItems();

  int deliveryTime();

}
