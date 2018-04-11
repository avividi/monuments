package avividi.com.gui.lwjgl;

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

  public HexQuad(Hexagon<? extends HexItem> hex, Map<String, ImageQuad> imageQuadMap) {
    this.imageQuad = imageQuadMap.get(hex.getObj().getImageName());
    Preconditions.checkNotNull(imageQuadMap);
    this.position = getPixelPosition(hex);
  }

  public void draw () {
    imageQuad.draw(position.getX(), position.getY());
  }

  private Point2 getPixelPosition (Hexagon<?> hex) {
    return Grid.getPixelPosition(imgSize, hex.getPos2d(), padding / 2);
  }
}
