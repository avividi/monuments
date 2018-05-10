package avividi.com.monuments.controller.gamehex.staticitems;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.hexgeometry.Grid;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static avividi.com.monuments.controller.HexItem.Transform.*;
import static avividi.com.monuments.hexgeometry.PointAxial.*;

public class AutoWall implements GameHex {

  private final String location;
  private final String innerEdgeFirstImage;
  private final List<String> images = new ArrayList<>();
  private Transform transform;

  public AutoWall(ObjectNode json) {
    images.add(json.get("background").asText());
    location = json.get("location").asText();
    innerEdgeFirstImage =  json.get("innerEdgeFirstImage").asText();
  }

  @Override
  public void postLoadCalculation(Board board, PointAxial self) {

    Function<PointAxial, Boolean> isWall = getIsOfClassFunction(board.getGround(), self, AutoWall.class);

    if (is(W, E, isWall)) setImage("w-e", none);
    else if (is(W, NE, isWall)) setImage("w-ne", none);
    else if (is(W, SE, isWall)) setImage("w-se", none);
    else if (is(NW, SW, isWall)) setImage("nw-sw", none);
    else if (is(NW, E, isWall)) setImage("nw-e", none);
    else if (is(NW, SE, isWall)) setImage("nw-se", none);
    else if (is(SW, NE, isWall)) setImage("sw-ne", none);
    else if (is(SW, E, isWall)) setImage("sw-e", none);
    else if (is(NE, SE, isWall)) setImage("ne-se", none);
    else  setImage("full", none);
  }

  @Override
  public boolean passable() {
    return false;
  }

  @Override
  public List<String> getImageNames() {
    return images;
  }

  @Override
  public Transform getTransform() {
    return transform;
  }

  private Function<PointAxial, Boolean> getIsOfClassFunction (Grid<GameHex> ground, PointAxial pos, Class<?> clazz) {
    return (dir) -> {
      Optional<Hexagon<GameHex>> hex = ground.getByAxial(pos.add(dir));
      return !hex.isPresent() || hex.filter(h -> h.getObj().getClass().equals(clazz)).isPresent();
    };
  }

  private void setImage(String positionCode, Transform transform) {
    images.add(location + "/" + location + "-" + positionCode);
    this.transform = transform;
  }



  private boolean is(PointAxial p1, PointAxial p2, Function<PointAxial, Boolean> isWall) {
    return isWall.apply(p1) && isWall.apply(p2);
  }
}
