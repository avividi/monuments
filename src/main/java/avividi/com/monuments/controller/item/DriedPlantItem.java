package avividi.com.monuments.controller.item;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.other.DroppedItemInteractor;
import avividi.com.monuments.hexgeometry.PointAxial;

public class DriedPlantItem implements Item {


  @Override
  public void dropItem(Board board, PointAxial position) {
    if (!board.hexIsFreeForOther(position)) return;

    board.getOthers().setHex(getDropped(), position);
  }

  @Override
  public String getItemImageNameSpace() {
    return "driedPlantItem";
  }

  private DroppedItemInteractor getDropped() {
    return new DroppedItemInteractor(
        DriedPlantItem.class,
        DriedPlantItem::new,
        String.join("/", getItemImageNameSpace(), getItemImageNameSpace()),
        true);
  }
}
