package avividi.com.monuments.controller.gamehex.other.buildmarker;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.hexgeometry.PointAxial;

import java.util.function.Supplier;

public class DoubleLayerBuildMarker extends BuildMarker {

  private final Supplier<GameHex> result;

  public DoubleLayerBuildMarker(Class<? extends Item> itemType,
                                int amount,
                                int buildTime,
                                int priority,
                                Supplier<GameHex> result) {
    super(itemType, amount, buildTime, priority);
    this.result = result;
  }

  @Override
  public void everyTickAction(Board board, PointAxial self) {

    if (fullFilled()) {
      GameHex builtThing = result.get();
      board.setShouldCalculateSectors();
      board.getOthers().clearHex(self);
      board.getStatics().setHex(builtThing, self);
    }
  }

}
