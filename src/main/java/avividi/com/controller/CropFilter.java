package avividi.com.controller;

import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.Point2d;

import java.util.stream.Stream;

public class CropFilter {

  private int scrollZone = 5;
  private Point2d currentFrameMin = new Point2d(0, 0);
  private Point2d frameSize = new Point2d(31, 16);

  public void adjustToMarker (Hexagon<? extends GameItem> marker) {
    Point2d pos =  marker.getPos2d();
    Point2d max = frameSize.add(currentFrameMin);

    int distMinX = currentFrameMin.getX() + scrollZone - pos.getX();
    if (distMinX > 0 && currentFrameMin.getX() > 0) {
      currentFrameMin = currentFrameMin.add(-distMinX, 0);
    }
    int distMinY = currentFrameMin.getY() + scrollZone - pos.getY();
    if (distMinY > 0 && currentFrameMin.getY() > 0) {
      currentFrameMin = currentFrameMin.add(0, -distMinY);
    }
    int distMaxX = max.getX() - scrollZone - pos.getX();
    if (distMaxX < 0) {
      currentFrameMin = currentFrameMin.add(-distMaxX, 0);
    }
    int distMaxY = max.getY() - scrollZone - pos.getY();
    if (distMaxY < 0) {
      currentFrameMin = currentFrameMin.add(0, -distMaxY);
    }
  }

  Stream<Hexagon<? extends GameItem>> crop(Stream<Hexagon<? extends GameItem>> stream) {
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
