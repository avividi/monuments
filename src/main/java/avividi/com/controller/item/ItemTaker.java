package avividi.com.controller.item;

public interface ItemTaker<T extends Item> {

  boolean wantsItems();

  boolean acceptsItems();

  void reserveTakeItem();
  void unReserveTakeItem();
  boolean giveItem(T item);
}
