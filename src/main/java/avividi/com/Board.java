package avividi.com;

import avividi.com.gameitems.GameItem;
import avividi.com.gameitems.Ground;
import avividi.com.gameitems.InteractingItem;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Grid;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;

import java.util.Optional;
import java.util.stream.Stream;

public class Board {

  private final Grid<GameItem> ground;
  private final Grid<InteractingItem> others;
  private final Grid<Unit> units;

  public Board(Grid<GameItem> ground, Grid<InteractingItem> others, Grid<Unit> units) {
    this.ground = ground;
    this.others = others;
    this.units = units;
  }

  public Grid<GameItem> getGround() {
    return ground;
  }

  public Grid<InteractingItem> getOthers() {
    return others;
  }

  public Grid<Unit> getUnits() {
    return units;
  }

  public boolean hexIsFree(PointAxial pointAxial) {

    Optional<Hexagon<GameItem>> ground = getGround().getByAxial(pointAxial);
    if (!ground.filter(g -> g.getObj().passable()).isPresent()) return false;

    Optional<Hexagon<InteractingItem>> other = getOthers().getByAxial(pointAxial);
    if (other.isPresent() && other.filter(u -> !u.getObj().passable()).isPresent()) return false;

    Optional<Hexagon<Unit>> unit = getUnits().getByAxial(pointAxial);
    if (unit.isPresent() && unit.filter(u -> !u.getObj().passable()).isPresent()) return false;

    return true;
  }

  public Stream<Hexagon<? extends HexItem>> getHexagonsByDrawingOrder() {
    return Stream.concat(Stream.concat(ground.getHexagons(), others.getHexagons()), units.getHexagons());

  }

}
