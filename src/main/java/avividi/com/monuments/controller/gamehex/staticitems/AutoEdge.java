package avividi.com.monuments.controller.gamehex.staticitems;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.hexgeometry.GridLayer;
import avividi.com.monuments.hexgeometry.HexLayer;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static avividi.com.monuments.controller.HexItem.Transform.*;
import static avividi.com.monuments.hexgeometry.PointAxial.*;

public class AutoEdge implements GameHex {

  private final String location;
  private final String innerTypeId;
  private final String innerBackgroundLocation;
  private final List<String> images = new ArrayList<>();
  private Transform transform;

  public AutoEdge(ObjectNode json) {
    images.add(json.get("background").asText());
    location = json.get("location").asText();
    innerTypeId =  json.get("innerTypeId").asText();

    innerBackgroundLocation = json.get("innerBackgroundLocation") == null
        ? null : json.get("innerBackgroundLocation").asText();
  }

  @Override
  public void postLoadCalculation(Board board, PointAxial self) {

    if (innerBackgroundLocation != null) {
      calculateEdges(board, self, innerTypeId, setImageFunction(innerBackgroundLocation));
    }
    calculateEdges(board, self, innerTypeId, setImageFunction(location));

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

  private BiConsumer<String, Transform> setImageFunction (String location) {
    return (positionCode, transform) -> {
      images.add(location + "/" + location + "-" + positionCode);
      this.transform = transform;
    };
  }

  private static void calculateEdges(Board board,
                              PointAxial self,
                              String innerTypeId,
                              BiConsumer<String, Transform> setImage) {

    Function<PointAxial, Boolean> inner = geHasIdFunction(board.getStatics(), self, innerTypeId);
    Function<PointAxial, Boolean> edge = getIsOfClassFunction(board.getStatics(), self, AutoEdge.class);

    if (is(W, E, NW, inner, edge)) setImage.accept("w-e-dn", none);
    else if (is(W, NE, NW, inner, edge)) setImage.accept("w-ne-dnw", none);
    else if (is(SW, E, NW, inner, edge)) setImage.accept("sw-e-dnw", none);
    else if (is(SW, NW, W, inner, edge)) setImage.accept("sw-nw-dw", none);
    else if (is(SW, NE, NW, inner, edge)) setImage.accept("sw-ne-dnw", none);
    else if (is(SE, NE, W, inner, edge)) setImage.accept("se-ne-dw", none);

    else if (is(NW, E, NE, inner, edge)) setImage.accept("w-ne-dnw", flipped);
    else if (is(NW, SE, NE, inner, edge)) setImage.accept("sw-ne-dnw", flipped);
    else if (is(SE, NE, E, inner, edge)) setImage.accept("sw-nw-dw", flipped);
    else if (is(SW, NW, E, inner, edge)) setImage.accept("se-ne-dw", flipped);
    else if (is(SE, W, NE, inner, edge)) setImage.accept("sw-e-dnw", flipped);

    else if (is(W, E, SW, inner, edge)) setImage.accept("w-e-dn", oneEighty);

    else if (is(SW, E, SE, inner, edge)) setImage.accept("w-ne-dnw", oneEighty);
    else if (is(W, NE, SE, inner, edge)) setImage.accept("sw-e-dnw", oneEighty);
    else if (is(SW, NE, SE, inner, edge)) setImage.accept("sw-ne-dnw", oneEighty);

    else if (is(SE, W, SW, inner, edge)) setImage.accept("w-ne-dnw", oneEightyFlipped);
    else if (is(E, NW, SE, inner, edge)) setImage.accept("sw-e-dnw", oneEightyFlipped);
    else if (is(SE, NW, SW, inner, edge)) setImage.accept("sw-ne-dnw", oneEightyFlipped);


    else setImage.accept("full", none);
  }

  private static Function<PointAxial, Boolean> getIsOfClassFunction (HexLayer<GameHex> ground, PointAxial pos, Class<?> clazz) {
    return (dir) -> {
      Optional<Hexagon<GameHex>> hex = ground.getByAxial(pos.add(dir));
      return !hex.isPresent() || hex.filter(h -> h.getObj().getClass().equals(clazz)).isPresent();
    };
  }

  private static Function<PointAxial, Boolean> geHasIdFunction(HexLayer<GameHex> ground, PointAxial pos, String id) {
    return (dir) -> ground.getByAxial(pos.add(dir)).filter(h -> id.equals(h.getObj().getId())).isPresent();
  }


  private static boolean is(PointAxial p1, PointAxial p2, PointAxial dir,
                     Function<PointAxial, Boolean> isInner, Function<PointAxial, Boolean> isCliff) {
    return isInner.apply(dir)
        && isCliff.apply(p1)
        && isCliff.apply(p2);
  }
}
