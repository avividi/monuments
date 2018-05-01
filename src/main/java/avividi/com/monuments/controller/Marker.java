package avividi.com.monuments.controller;

import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.staticitems.CustomStaticItem;
import avividi.com.monuments.hexgeometry.Grid;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.collect.ImmutableList;

public class Marker {

  private boolean toggled = false;
  private PointAxial currentPosition;
  private final GameHex item;

  public Marker(PointAxial currentPosition) {
    item = new CustomStaticItem(ImmutableList.of("marker/marker-green"), HexItem.Transform.none, false, true);
    this.currentPosition = currentPosition;
  }

  public void toggle() {
    this.toggled = !this.toggled;
  }

  public boolean toggled() {
    return toggled;
  }

  public void move (Grid<GameHex> ground, PointAxial dir, int steps) {
    if (!toggled) return;
    PointAxial newPos = this.currentPosition.add(dir.multiply(steps));
    ground.getByAxial(newPos).ifPresent($ -> this.currentPosition = newPos);
    System.out.println("ax: " + asHexagon(ground).getPosAxial() + " 2d: " + asHexagon(ground).getPos2d());
  }

  public Hexagon<GameHex> asHexagon(Grid<GameHex> ground) {
    return ground.getByAxial(currentPosition)
        .map(hex ->  new Hexagon<>(item, hex.getPosAxial(), hex.getPos2d()))
        .orElseThrow(IllegalStateException::new);
  }

  public PointAxial getCurrentPosition() {
    return currentPosition;
  }
}
