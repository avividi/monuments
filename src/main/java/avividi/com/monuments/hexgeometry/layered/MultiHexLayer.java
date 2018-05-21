package avividi.com.monuments.hexgeometry.layered;

import avividi.com.monuments.hexgeometry.Hexagon;

import java.util.Optional;
import java.util.stream.Stream;

public interface MultiHexLayer<T> {

  Stream<Hexagon<T>> getHexagons(int layer);
  Optional<Hexagon<T>> getByAxial(LayerPoint point);
  T clearHex(LayerPoint point);
  boolean setHex(T t, LayerPoint point);
}
