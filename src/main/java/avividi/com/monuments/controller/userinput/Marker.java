package avividi.com.monuments.controller.userinput;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.staticitems.CustomStaticItem;
import avividi.com.monuments.hexgeometry.*;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Marker {

  private boolean toggled = false;
  private PointAxial currentPosition;
  private boolean inBuildMode = false;
  private boolean buildAble = true;


  public Marker(PointAxial currentPosition) {
  this.currentPosition = currentPosition;
  }

  public void toggle(boolean toggled, boolean inBuildMode) {
    this.inBuildMode = inBuildMode;
    this.toggled = toggled;
  }

  public boolean toggled() {
    return toggled;
  }

  public boolean isInBuildMode() {
    return toggled && inBuildMode;
  }

  public boolean isInSelectMode() {
    return toggled && !inBuildMode;
  }

  public void checkBuildable (Board board) {
    buildAble = board.hexIsBuildAble(this.currentPosition);
  }

  public void move (Board board, PointAxial dir, int steps) {
    if (!toggled) return;
//    currentPosition = getHighestOccupiedHex(board, this.currentPosition.add(dir.multiply(steps)));
    PointAxial newPos =  this.currentPosition.add(dir.multiply(steps));
    board.getGround().getByAxial(newPos).ifPresent($ -> this.currentPosition = newPos);
      buildAble = board.hexIsBuildAble(this.currentPosition);
    System.out.println("ax: " + asHexagon(board.getGround()).getPosAxial() + " 2d: " + asHexagon(board.getGround()).getPos2d());
  }


  public Stream<Hexagon<? extends HexItem>> asStream(GridLayer<GameHex> ground) {

    String color = inBuildMode ? (buildAble ? "green" : "red") : "yellow";
    String markerImg = String.join("-","marker/marker", color);
    String markerHeightImg = String.join("-","marker/marker", color, "lower");

    Hexagon<?> hex = ground.getByAxial(currentPosition).orElseThrow(IllegalStateException::new);
    PointAxial markerPos = hex.getPosAxial();


    List<Hexagon<? extends HexItem>> hexes =  new ArrayList<>();
    for (int i = 0; i < markerPos.getLayer(); i++) {
      PointAxial lowerPos = new PointAxial(markerPos.getX(), markerPos.getY(), i);
      hexes.add(create(markerHeightImg, lowerPos, hex.getPos2d()));
    }

    hexes.add(create(markerImg, markerPos, hex.getPos2d()));

    return hexes.stream();
  }

  public Hexagon<? extends HexItem> asHexagon (GridLayer<GameHex> ground) {
        return ground.getByAxial(currentPosition)
        .orElseThrow(IllegalStateException::new);
  }

  public PointAxial getCurrentPosition() {
    return currentPosition;
  }


  private PointAxial getHighestOccupiedHex(Board board, PointAxial pointAxial) {
    Point2 dispRange = board.getLayerDisplayRange();

    for (int i = dispRange.getY(); i >= dispRange.getX(); --i) {
      PointAxial p = new PointAxial(pointAxial.getX(), pointAxial.getY(), i);
      if (occupied(board, p)) return p;
    }

    return pointAxial;
  }

  private boolean occupied(Board board, PointAxial pointAxial) {
    return board.getStatics().getByAxial(pointAxial).isPresent()
        || board.getOthers().getByAxial(pointAxial).isPresent()
        || board.getUnits().getByAxial(pointAxial).isPresent();
  }

  public void moveLayer(int dir, Board board) {

    int min = board.getLayerDisplayRange().getX();
    int max = board.getLayerDisplayRange().getY();

    int layer = this.currentPosition.getLayer() + dir;

    if (max <= layer) return;
    else if (min > layer) return;

    this.currentPosition = new PointAxial(this.currentPosition.getX(), this.currentPosition.getY(), layer);
    this.checkBuildable(board);
  }

  private Hexagon<? extends HexItem> create(String imageName, PointAxial p, Point2d p2d) {
    return new Hexagon<>( new CustomStaticItem(ImmutableList.of(imageName), HexItem.Transform.none, false, false, false)
        , p, p2d);
  }
}
