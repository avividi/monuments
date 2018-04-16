package avividi.com.gui.lwjgl;

import avividi.com.controller.DayStage;
import avividi.com.controller.HexItem;
import avividi.com.controller.hexgeometry.Grid;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.Point2;
import com.google.common.base.Preconditions;

import java.util.Map;

public class HexQuad {

  public static String assetLocation = "graphics/";
  public static int imgSize = 32;
  public static int padding = 0;

  private final ImageQuad imageQuad;
  private final Point2 position;
  private final HexItem.Transform transform;

  public HexQuad(Hexagon<? extends HexItem> hex, Map<String, ImageQuad> imageQuadMap, DayStage stage) {
    this.transform = hex.getObj().getTransform();
    this.imageQuad = imageQuadMap.get(hex.getObj().getImageName());
    if (hex.getObj().affectedByLight()) imageQuad.setColorFilter(getColorFilter(stage));
    Preconditions.checkNotNull(imageQuadMap);
    this.position = getPixelPosition(hex);
  }

  private ImageQuad.ColorFilter getColorFilter (DayStage stage) {
    if (stage == DayStage.dusk) return new ImageQuad.ColorFilter(0.82f, 0.78f, 0.93f, 1f);
    else if (stage == DayStage.dawn) return new ImageQuad.ColorFilter(0.82f, 0.72f, 0.75f, 1f);
    else if (stage == DayStage.night)return new ImageQuad.ColorFilter( 0.54f, 0.57f, 0.90f, 1f);
    return new ImageQuad.ColorFilter();
  }

  public void draw () {
    if (transform == HexItem.Transform.flipped)
      imageQuad.drawFlippedHorizontally(position.getX(), position.getY());
    else if (transform == HexItem.Transform.oneEighty)
      imageQuad.drawOneEighty(position.getX(), position.getY());
    else if (transform == HexItem.Transform.oneEightyFlipped)
      imageQuad.drawOneEightFlipped(position.getX(), position.getY());
    else imageQuad.draw(position.getX(), position.getY());

  }

  private Point2 getPixelPosition (Hexagon<?> hex) {
    return Grid.getPixelPosition(imgSize, hex.getPos2d(), padding / 2);
  }
}
