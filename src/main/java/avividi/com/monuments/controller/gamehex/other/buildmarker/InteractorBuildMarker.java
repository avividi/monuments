package avividi.com.monuments.controller.gamehex.other.buildmarker;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.item.Item;

import avividi.com.monuments.hexgeometry.PointAxial;

import java.util.function.Supplier;

public class InteractorBuildMarker extends BuildMarker {

  private final Supplier<Interactor> result;

  public InteractorBuildMarker(Class<? extends Item> itemType,
                               int amount,
                               int buildTime,
                               int priority,
                               Supplier<Interactor> result) {
    super(itemType, amount, buildTime, priority);
    this.result = result;
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {
    if (fullFilled()) {
      Interactor builtThing = result.get();
      if (!builtThing.passable()) board.setShouldCalculateSectors();
      board.getOthers().setHex(builtThing, self);
    }
  }


  @Override
  protected void cancel (Board board, PointAxial self) {
    board.getOthers().clearHex(self);
  }
}
