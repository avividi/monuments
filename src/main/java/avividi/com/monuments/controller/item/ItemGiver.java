package avividi.com.monuments.controller.item;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.hexgeometry.PointAxial;

import java.util.Optional;
import java.util.Set;

public interface ItemGiver {


  boolean hasAvailableItem(Class<? extends Item> itemType);

  void reservePickUpItem(Class<? extends Item> itemType);
  void unReservePickUpItem(Class<? extends Item> itemType);
  Optional<? extends Item> pickUpItem(Board board, PointAxial self, Class<? extends Item> item);

  Set<Class<? extends Item>> getSupportedPickUpItems();

  int pickUpTime();
}