package avividi.com.monuments.controller.item;

import java.util.Set;

public interface ItemTaker {

  void reserveDeliverItem(Class<? extends Item> itemType);
  void unReserveDeliverItem(Class<? extends Item> itemType);
  <T extends Item> boolean deliverItem(T item);

  Set<Class<? extends Item>> getSupportedDeliverItems();

  int deliveryTime();

}
