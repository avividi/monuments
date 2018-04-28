package avividi.com.controller.item;

import java.util.Optional;
import java.util.Set;

public interface ItemGiver {


  boolean hasAvailableItem(Class<? extends Item> itemType);

  void reservePickUpItem(Class<? extends Item> itemType);
  void unReservePickUpItem(Class<? extends Item> itemType);
  Optional<? extends Item> pickUpItem(Class<? extends Item> item);

  Set<Class<? extends Item>> getSupportedPickUpItems();

  int pickUpTime();
}
