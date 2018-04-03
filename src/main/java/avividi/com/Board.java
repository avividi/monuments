package avividi.com;

import avividi.com.hexgeometry.Grid;

public class Board {

  private final Grid<GameItem> ground;
  private final Grid<GameItem> units;

  public Board(Grid<GameItem> ground, Grid<GameItem> units) {
    this.ground = ground;
    this.units = units;
  }

  public Grid<GameItem> getGround() {
    return ground;
  }

  public Grid<GameItem> getUnits() {
    return units;
  }
}
