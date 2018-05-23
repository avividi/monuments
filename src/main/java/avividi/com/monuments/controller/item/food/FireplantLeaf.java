package avividi.com.monuments.controller.item.food;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.clock.ClockStage;
import avividi.com.monuments.controller.gamehex.other.DroppedItemInteractor;
import avividi.com.monuments.hexgeometry.PointAxial;

public class FireplantLeaf implements Food {


  @Override
  public void dropItem(Board board, PointAxial position) {
    if (!board.hexIsFreeForOther(position)) return;

    board.getOthers().setHex(getDropped(), position);
  }

  @Override
  public String getItemImageNameSpace() {
    return "leafFireplant";
  }

  private DroppedItemInteractor getDropped() {
    return new DroppedItemInteractor(
        FireplantLeaf.class,
        FireplantLeaf::new,
        String.join("/", getItemImageNameSpace(), getItemImageNameSpace()),
        true);
  }

  @Override
  public int nutritionValue() {
    return ClockStage.cycleSize / (15);
  }
}
