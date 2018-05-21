package avividi.com.monuments.hexgeometry;

import java.util.Optional;
import java.util.stream.Stream;

public interface HexLayer<T> {

  Stream<Hexagon<T>> getHexagons();
  Optional<Hexagon<T>> getByAxial(PointAxial pointAxial);
  T clearHex(PointAxial pointAxial);
  boolean setHex(T t, PointAxial pointAxial);
}
