package avividi.com.controller.gameitems.staticitems;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.hexgeometry.Grid;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static avividi.com.controller.HexItem.Transform.*;
import static avividi.com.controller.hexgeometry.PointAxial.*;

public class AutoEdge extends GameItem {

  private final String location;
  private final Class<?> innerCliffClass;
  private final List<String> images = new ArrayList<>();
  private Transform transform;

  public AutoEdge(ObjectNode json) {
    super(json);
    images.add(json.get("background").asText());
    location = json.get("location").asText();
    try {
      innerCliffClass = Class.forName(json.get("innerClass").asText());
    }
    catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void postLoadCalculation(Board board, PointAxial self) {
    super.postLoadCalculation(board, self);

    Function<PointAxial, Boolean> inner = getIsOfClassFunction(board.getGround(), self, innerCliffClass);
    Function<PointAxial, Boolean> edge = getIsOfClassFunction(board.getGround(), self, AutoEdge.class);

    if (is(W, E, NW, inner, edge)) setImage("w-e-dn", none);
    else if (is(W, NE, NW, inner, edge)) setImage("w-ne-dnw", none);
    else if (is(SW, E, NW, inner, edge)) setImage("sw-e-dnw", none);
    else if (is(SW, NW, W, inner, edge)) setImage("sw-nw-dw", none);
    else if (is(SW, NE, NW, inner, edge)) setImage("sw-ne-dnw", none);
    else if (is(SE, NE, W, inner, edge)) setImage("se-ne-dw", none);

    else if (is(NW, E, NE, inner, edge)) setImage("w-ne-dnw", flipped);
    else if (is(NW, SE, NE, inner, edge)) setImage("sw-ne-dnw", flipped);
    else if (is(SE, NE, E, inner, edge)) setImage("sw-nw-dw", flipped);
    else if (is(SW, NW, E, inner, edge)) setImage("se-ne-dw", flipped);
    else if (is(SE, W, NE, inner, edge)) setImage("sw-e-dnw", flipped);

    else if (is(W, E, SW, inner, edge)) setImage("w-e-dn", oneEighty);

    else if (is(SW, E, SE, inner, edge)) setImage("w-ne-dnw", oneEighty);
    else if (is(W, NE, SE, inner, edge)) setImage("sw-e-dnw", oneEighty);
    else if (is(SW, NE, SE, inner, edge)) setImage("sw-ne-dnw", oneEighty);

    else if (is(SE, W, SW, inner, edge)) setImage("w-ne-dnw", oneEightyFlipped);
    else if (is(E, NW, SE, inner, edge)) setImage("sw-e-dnw", oneEightyFlipped);
    else if (is(SE, NW, SW, inner, edge)) setImage("sw-ne-dnw", oneEightyFlipped);


    else  setImage("full", none);
  }

  @Override
  public List<String> getImageNames() {
    return images;
  }

  @Override
  public Transform getTransform() {
    return transform;
  }

  private Function<PointAxial, Boolean> getIsOfClassFunction (Grid<GameItem> ground, PointAxial pos, Class<?> clazz) {
    return (dir) -> {
      Optional<Hexagon<GameItem>> hex = ground.getByAxial(pos.add(dir));
      return !hex.isPresent() || hex.filter(h -> h.getObj().getClass().equals(clazz)).isPresent();
    };
  }

  private void setImage(String positionCode, Transform transform) {
    images.add(location + "/" + location + "-" + positionCode);
    this.transform = transform;
  }
  private boolean is(PointAxial p1, PointAxial p2, PointAxial dir,
                     Function<PointAxial, Boolean> isInner, Function<PointAxial, Boolean> isCliff) {
    return isInner.apply(dir)
        && isCliff.apply(p1)
        && isCliff.apply(p2);
  }
}
