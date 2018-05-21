package avividi.com.monuments.hexgeometry.layered;

import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.hexgeometry.*;

import java.util.Optional;
import java.util.stream.Stream;

public interface MultiHexLayer<T> extends HexLayer<T> {

  void addLayerBelow(HexLayer<T> layer);
  void addLayerAbove(HexLayer<T> layer);
  boolean hasLayer (int layer);
  Stream<Hexagon<T>> getHexagons(int layer);


  Point2 getLayerRange();
//  GridLayer<?> getBaseGrid();
}
