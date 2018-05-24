package avividi.com.monuments.controller.userinput;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.staticitems.CustomStaticItem;
import avividi.com.monuments.hexgeometry.GridLayer;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.Point2;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Marker {

  private boolean toggled = false;
  private PointAxial currentPosition;
  private final CustomStaticItem item;
  private boolean inBuildMode = false;
  private boolean buildAble = true;


  public Marker(PointAxial currentPosition) {
    item = new CustomStaticItem(ImmutableList.of("marker/marker-yellow"), HexItem.Transform.none, false, false, false);
    this.currentPosition = currentPosition;
  }

  public void toggle(boolean toggled, boolean inBuildMode) {
    this.inBuildMode = inBuildMode;
    setImages(inBuildMode);
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
    setBuildAble();
  }


  //todo bug moving out of board
  public void move (Board board, PointAxial dir, int steps) {
    if (!toggled) return;
//    currentPosition = getHighestOccupiedHex(board, this.currentPosition.add(dir.multiply(steps)));
    PointAxial newPos =  this.currentPosition.add(dir.multiply(steps));
    board.getGround().getByAxial(newPos).ifPresent($ -> this.currentPosition = newPos);
    if (inBuildMode) {
      buildAble = board.hexIsBuildAble(this.currentPosition);
      setBuildAble();
    }
    System.out.println("ax: " + asHexagon(board.getGround()).getPosAxial() + " 2d: " + asHexagon(board.getGround()).getPos2d());
  }

  public void setLayer(int layer) {
    this.currentPosition = new PointAxial(this.currentPosition.getX(), this.currentPosition.getY(), layer);
  }

  public Hexagon<GameHex> asHexagon(GridLayer<GameHex> ground) {
    return ground.getByAxial(currentPosition)
        .map(hex ->  new Hexagon<>((GameHex) item, hex.getPosAxial(), hex.getPos2d()))
        .orElseThrow(IllegalStateException::new);
  }

  public PointAxial getCurrentPosition() {
    return currentPosition;
  }

  private void setImages(boolean inBuildMode) {
    if (!inBuildMode) this.item.setImages(ImmutableList.of("marker/marker-yellow"));
    else setBuildAble();
  }

  private void setBuildAble () {
    List<String> images = buildAble
        ? ImmutableList.of("marker/marker-green")
        : ImmutableList.of("marker/marker-red");
    this.item.setImages(images);
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
}
