package avividi.com.controller.gameitems;

import avividi.com.controller.item.Item;

import java.util.Optional;

public interface ItemSupplier<T extends Item> {

  Optional<T> getItem();
  void reserveItem();

}
