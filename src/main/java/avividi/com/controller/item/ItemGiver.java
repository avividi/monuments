package avividi.com.controller.item;

import java.util.Optional;

public interface ItemGiver<T extends Item> {

  boolean hasItem();

  void reserveGetItem();
  void unReserveGetItem();
  Optional<T> getItem();

  Class<? extends Item> getItemClass();

}
