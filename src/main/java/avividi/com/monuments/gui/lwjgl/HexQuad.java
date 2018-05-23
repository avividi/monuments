package avividi.com.monuments.gui.lwjgl;

import avividi.com.monuments.controller.clock.ClockStage;
import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.hexgeometry.GridLayer;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.Point2;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HexQuad {

  public static final String assetLocation = "graphics/";
  public static final int imgSize = 32;
  public static final int padding = 0;
  public static final int layerOffset = -8;

  private final List<ImageQuad> imageQuads;
  private final Point2 position;
  private final HexItem.Transform transform;

  public HexQuad(Hexagon<? extends HexItem> hex, Map<String, ImageQuad> imageQuadMap, ClockStage stage) {
    this.transform = hex.getObj().getTransform();
    this.imageQuads =  hex.getObj().getImageNames().stream()
        .map(imageQuadMap::get)
        .peek(Preconditions::checkNotNull)
        .collect(Collectors.toList());
    if (hex.getObj().affectedByLight()) imageQuads.forEach(img -> img.setColorFilter(getColorFilter(stage)));
    Preconditions.checkNotNull(imageQuadMap);
    this.position = getPixelPosition(hex).add(0, hex.getLayer() * layerOffset);
  }

  private ImageQuad.ColorFilter getColorFilter (ClockStage stage) {
    if (stage == ClockStage.dusk) return new ImageQuad.ColorFilter(0.82f, 0.78f, 0.93f, 1f);
    else if (stage == ClockStage.dawn) return new ImageQuad.ColorFilter(0.82f, 0.72f, 0.75f, 1f);
    else if (stage == ClockStage.night)return new ImageQuad.ColorFilter( 0.54f, 0.57f, 0.90f, 1f);
    return new ImageQuad.ColorFilter();
  }

  public void draw () {
    if (transform == HexItem.Transform.flipped)
      imageQuads.forEach(img -> img.drawFlippedHorizontally(position.getX(), position.getY()));
    else if (transform == HexItem.Transform.oneEighty)
      imageQuads.forEach(img -> img.drawOneEighty(position.getX(), position.getY()));
    else if (transform == HexItem.Transform.oneEightyFlipped)
      imageQuads.forEach(img -> img.drawOneEightFlipped(position.getX(), position.getY()));
    else imageQuads.forEach(img -> img.draw(position.getX(), position.getY()));

  }

  private Point2 getPixelPosition (Hexagon<?> hex) {
    return GridLayer.getPixelPosition(imgSize, hex.getPos2d(), padding / 2);
  }
}
