package avividi.com.controller.item;

import avividi.com.controller.Board;
import avividi.com.controller.hexgeometry.PointAxial;

public class FireplantItem implements Item {


  @Override
  public void dropItem(Board board, PointAxial position) {
    if (!board.hexIsPathAble(position)) return;

    board.getOthers().setHex(getDropped(), position);
  }

  private DroppedItemInteractor getDropped() {
    return new DroppedItemInteractor(
        FireplantItem.class,
        FireplantItem::new,
        "fireplant/firePlantItem",
        true);
  }
}
