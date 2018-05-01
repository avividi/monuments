package avividi.com.monuments.controller.util;

import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.hexgeometry.Grid;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.Point2d;

import java.util.Comparator;
import java.util.stream.Stream;

public class CropFilter {

  private final int scrollZone = 8;
  private final Point2d frameSize = new Point2d(61, 26);
  private final int maxWidth;
  private final int maxHeight;

  private Point2d currentFrameMin = new Point2d(0, 0);

  public CropFilter(Grid<GameHex> ground) {
    maxWidth = ground.getHexagons().max(Comparator.comparingInt(h -> h.getPos2d().getX()))
        .orElseThrow(IllegalStateException::new).getPos2d().getX() - frameSize.getX();
    maxHeight = ground.getHexagons().max(Comparator.comparingInt(h -> h.getPos2d().getY()))
        .orElseThrow(IllegalStateException::new).getPos2d().getY() - frameSize.getY();

  }

  public void adjustToMarker (Hexagon<? extends GameHex> marker) {
    Point2d pos =  marker.getPos2d();
    Point2d max = frameSize.add(currentFrameMin);

    int distMinX = currentFrameMin.getX() + scrollZone * 2 - pos.getX();
    if (distMinX > 1 && currentFrameMin.getX() > 0) {
      currentFrameMin = currentFrameMin.add(-distMinX, 0);
    }
    int distMinY = currentFrameMin.getY() + scrollZone - pos.getY();
    if (distMinY > 1 && currentFrameMin.getY() > 0) {
      currentFrameMin = currentFrameMin.add(0, -distMinY);
    }
    int distMaxX = max.getX() - scrollZone * 2 - pos.getX();
    if (distMaxX < 0 && currentFrameMin.getX() <= maxWidth) {
      currentFrameMin = currentFrameMin.add(-distMaxX, 0);
    }
    int distMaxY = max.getY() - scrollZone - pos.getY();
    if (distMaxY < 0 && currentFrameMin.getY() <= maxHeight) {
      currentFrameMin = currentFrameMin.add(0, -distMaxY);
    }
  }

  public Stream<Hexagon<? extends HexItem>> crop(Stream<Hexagon<? extends GameHex>> stream) {
    return stream.filter(this::fitsFrame)
        .map(this::transformFrame);
  }

  private boolean fitsFrame (Hexagon<?> hex) {
    Point2d pos = hex.getPos2d();
    Point2d max = frameSize.add(currentFrameMin);

    return pos.getX() >= currentFrameMin.getX() && pos.getY() >= currentFrameMin.getY()
        && pos.getX() < max.getX() && pos.getY() < max.getY();
  }

  private <T> Hexagon<T> transformFrame (Hexagon<T> hex) {
    return new Hexagon<>(hex.getObj(), hex.getPosAxial(), hex.getPos2d().subtract(currentFrameMin)) ;
  }


}
