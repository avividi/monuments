package avividi.com.monuments.hexgeometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class MappedLayer<T> implements HexLayer<T> {

  private final Map<PointAxial, Hexagon<T>> map = new HashMap<>();
  private final GridLayer<?> baseGrid;

  public MappedLayer(String input, Map<Character, Supplier<T>> charMap, GridLayer<?> baseGrid) {
    this.baseGrid = baseGrid;

    new GridLayer<T>(input, charMap).getHexagons()
        .forEach(hex -> map.put(hex.getPosAxial(), hex));
  }

  public MappedLayer(GridLayer<T> grid, GridLayer<?> baseGrid) {
    this.baseGrid = baseGrid;

    grid.getHexagons()
        .forEach(hex -> map.put(hex.getPosAxial(), hex));
  }

  @Override
  public Optional<Hexagon<T>> getByAxial(PointAxial pointAxial) {

    return Optional.ofNullable(map.get(pointAxial));
  }

  @Override
  public Stream<Hexagon<T>> getHexagons() {
    return  new ArrayList<>(map.values()).stream();
  }

  @SuppressWarnings({"unchecked"})
  public <U extends T> Stream<Hexagon<U>> getHexagons (Class<U> clazz) {
    return getHexagons()
        .filter(hex -> clazz.equals(hex.getObj().getClass()))
        .map(hex -> new Hexagon<>((U) hex.getObj(), hex.getPosAxial(), hex.getPos2d()));
  }

  @Override
  public T clearHex(PointAxial pointAxial) {
    Hexagon<T> hex = map.remove(pointAxial);
    return hex == null ? null : hex.getObj();
  }

  @Override
  public boolean setHex(T t, PointAxial pointAxial) {
    Hexagon<?> base = baseGrid.getByAxial(pointAxial).orElse(null);
    if (base == null) return false;

    map.put(pointAxial, new Hexagon<>(t, pointAxial, base.getPos2d()));

    return true;
  }


}

