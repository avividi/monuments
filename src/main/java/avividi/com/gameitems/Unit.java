package avividi.com.gameitems;

import avividi.com.*;
import avividi.com.hexgeometry.Grid;
import avividi.com.hexgeometry.PointAxial;

import java.util.Random;

public class Unit implements GameItem {


  Random random = new Random();
  Task currentTask;

  @Override
  public void clickAction(Board board, PointAxial self) {


    board.getUnits().getHexagons()
        .filter(hex -> hex.getObj() instanceof FirePlant)
        .findFirst()
        .ifPresent(plant -> {
          new AStar(board)
              .withOrigin(self)
              .withDestination(plant.getPosAxial())
              .get();
        });
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

    if (currentTask == null) {
      currentTask = new DefaultLeisureTask();
    }

    currentTask.performStep(board, self, this);

  }

  private boolean moveTo (Grid<GameItem> grid, PointAxial thisPos, PointAxial to) {
    boolean possible = grid.getByAxial(thisPos.add(to))
        .filter(hex -> hex.getObj().isGround())
        .isPresent();

    if (!possible) {
      boolean nothing = !grid.getByAxial(thisPos.add(to)).isPresent();
      if (nothing) {
        grid.setHex(this, thisPos.add(to));
        grid.setHex(new Ground(), thisPos);
        return true;
      }
      else return false;
    }

    grid.setHex(this, thisPos.add(to));
    grid.setHex(new Ground(), thisPos);
    return true;
  }

  @Override
  public String getImageName() {
    return "slavefireplant";
  }

  @Override
  public boolean isGround () {
    return false;
  }

  @Override
  public boolean clickAble() {
    return true;
  }
}
