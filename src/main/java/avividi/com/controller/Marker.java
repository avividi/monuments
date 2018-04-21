package avividi.com.controller;

import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.gameitems.staticitems.CustomStaticItem;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Grid;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.Point2d;
import avividi.com.controller.hexgeometry.PointAxial;
import com.google.common.collect.ImmutableList;

public class Marker {

  private boolean toggled = true;
  private PointAxial currentPosition;
  private final GameItem item;

  public Marker(PointAxial currentPosition) {
    item = new CustomStaticItem(ImmutableList.of("marker1"), HexItem.Transform.none, false);
    this.currentPosition = currentPosition;
  }

  public void toggle() {
    this.toggled = !this.toggled;
  }

  public boolean toggled() {
    return toggled;
  }

  public void move (Grid<GameItem> ground, PointAxial dir) {
    PointAxial newPos = this.currentPosition.add(dir);
    ground.getByAxial(newPos).ifPresent($ -> this.currentPosition = newPos);
  }

  public Hexagon<GameItem> asHexagon(Grid<GameItem> ground) {
    Point2d pos2d = ground.pointAxialToPoint2d(currentPosition).orElseThrow(IllegalStateException::new);
    return new Hexagon<>(item, currentPosition, pos2d);
  }
}
