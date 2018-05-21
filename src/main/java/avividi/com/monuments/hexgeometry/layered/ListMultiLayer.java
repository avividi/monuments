package avividi.com.monuments.hexgeometry.layered;

import avividi.com.monuments.hexgeometry.HexLayer;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.Point2;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ListMultiLayer<T> implements MultiHexLayer<T> {

  private List<HexLayer<T>> layers;
  private int indexOffset;

  public ListMultiLayer(List<HexLayer<T>> initialLayers, int startingIndexOffset) {
    layers = new ArrayList<>(initialLayers);
    indexOffset = startingIndexOffset;
  }

  @Override
  public void addLayerAbove(HexLayer<T> layer) {
    layers.add(layer);
  }

  @Override
  public void addLayerBelow(HexLayer<T> layer) {
    indexOffset++;
    layers.add(0, layer);
  }

  private int layerToIndex(int layer) {
    int index = layer + indexOffset;
//    Preconditions.checkState( index >= 0 && layers.size() > index);
    return index;
  }

  @Override
  public boolean hasLayer (int layer) {
    int index = layer + indexOffset;
    return index >= 0 && layers.size() > index;
  }

  @Override
  public Stream<Hexagon<T>> getHexagons() {
    return layers.stream().flatMap(HexLayer::getHexagons);
  }

  @Override
  public Optional<Hexagon<T>> getByAxial(PointAxial point) {

    if (!hasLayer(point.getLayer())) return Optional.empty();
    return layers.get(point.getLayer() + indexOffset).getByAxial(point);

//    return getLayer(point.getLayer()).flatMap(l -> l.getByAxial(point));
  }

  private Optional<HexLayer<T>> getLayer(int layer) {
//    return hasLayer(layer) ? Optional.of(layers.get(layerToIndex(layer))) : Optional.empty();

    return hasLayer(layer) ? Optional.of(layers.get(layer + indexOffset)) : Optional.empty();
  }

  @Override
  public T clearHex(PointAxial point) {
    return layers.get(point.getLayer()).clearHex(point);
  }

  @Override
  public boolean setHex(T t, PointAxial point) {
    return layers.get(point.getLayer() + indexOffset).setHex(t, point);
  }

  @Override
  public Stream<Hexagon<T>> getHexagons(int layer) {
    return layers.get(layer + indexOffset).getHexagons();
  }

  @Override
  public Point2 getLayerRange() {
    return new Point2(-indexOffset, layers.size() - indexOffset);
  }
}
