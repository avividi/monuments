package avividi.com.monuments.controller.item;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.hexgeometry.PointAxial;

import java.util.Optional;

public interface ItemGiver {


  boolean hasAvailableItem(Class<? extends Item> itemType);

  void reservePickUpItem(Class<? extends Item> itemType);
  void unReservePickUpItem(Class<? extends Item> itemType);
  Optional<? extends Item> pickUpItem(Board board, PointAxial self, Class<? extends Item> item);

  Class<? extends Item> getItemPickupType();

  int pickUpTime();
}
