package avividi.com.monuments.controller.task.atomic;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.unit.Maldar;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.controller.item.food.Food;
import avividi.com.monuments.hexgeometry.Hexagon;
import com.google.common.base.Preconditions;

public class EatTask implements Task {

  private boolean isComplete = false;
  private final static int tick_eatTime = 14;
  private int timeCount;

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {

    if (++timeCount <= tick_eatTime) return true;

    isComplete = true;

    Food food = (Food) unit.getObj().getItem().orElse(null);
    Preconditions.checkNotNull(food);
    unit.getObj().setItem(null);
    ((Maldar) unit.getObj()).eat(food);

    return true;
  }

  @Override
  public boolean shouldAbort() {
    return false;
  }

  @Override
  public boolean isComplete() {
    return isComplete;
  }
}
