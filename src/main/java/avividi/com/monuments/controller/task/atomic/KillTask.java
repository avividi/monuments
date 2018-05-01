package avividi.com.monuments.controller.task.atomic;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.other.BloodPool;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;

public class KillTask implements Task {
  private final PointAxial target;
  private final Class<? extends Unit> clazz;
  private boolean shouldAbort = true;
  private boolean isComplete = false;

  public KillTask(PointAxial target, Class<? extends Unit> clazz) {
    this.target = target;
    this.clazz = clazz;
  }

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {

    Preconditions.checkState( PointAxial.distance(target, unit.getPosAxial()) == 1);
    if (!board.getUnits().getByAxial(target).filter(h -> h.getObj().getClass().equals(clazz)).isPresent()) {
      shouldAbort = true;
      return false;
    }
    isComplete = true;
    Unit targetUnit = board.getUnits().clearHex(target);
    Preconditions.checkNotNull(targetUnit);
    targetUnit.kill(board, target);
    if (!board.getOthers().getByAxial(target).isPresent()) board.getOthers().setHex(new BloodPool(), target);
    System.out.println("Yummy!");

    return true;
  }

  @Override
  public boolean shouldAbort() {
    return shouldAbort;
  }

  @Override
  public boolean isComplete() {
    return isComplete;
  }
}
