package avividi.com.controller.item;

import avividi.com.controller.item.Item;

import java.util.Optional;

public interface ItemGiver<T extends Item> {

  boolean hasItem();

  void reserveGetItem();
  void unReserveGetItem();
  Optional<T> getItem();

}
