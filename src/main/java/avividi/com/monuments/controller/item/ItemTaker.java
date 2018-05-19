package avividi.com.monuments.controller.item;

public interface ItemTaker {

  void reserveDeliverItem(Class<? extends Item> itemType);
  void unReserveDeliverItem(Class<? extends Item> itemType);
  <T extends Item> boolean deliverItem(T item);

  Class<? extends Item> getDeliverItemType();

  int deliveryTime();

}
