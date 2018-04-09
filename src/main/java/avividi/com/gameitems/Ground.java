package avividi.com.gameitems;

import avividi.com.Board;
import avividi.com.DayStage;
import avividi.com.hexgeometry.PointAxial;

import java.util.Random;

public class Ground implements GameItem {

  private final String image;

  public Ground(Random random) {
    image = random.nextBoolean() ? "grounddirt" : "grounddirt2";
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self, DayStage stage) {
  }

  @Override
  public String getImageName() {
    return image;
  }

  @Override
  public boolean passable() {
    return true;
  }
}
