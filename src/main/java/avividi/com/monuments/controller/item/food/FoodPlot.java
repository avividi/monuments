package avividi.com.monuments.controller.item.food;

import avividi.com.monuments.controller.gamehex.other.Plot;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FoodPlot extends Plot implements  FoodGiver {


  public FoodPlot(Class<? extends Food> itemType) {
    super(itemType);
  }

  public FoodPlot(ObjectNode json) {
    super(json);
  }

  @Override
  public boolean hasAvailableFood() {
    return hasAvailableItem(this.itemType);
  }
}
