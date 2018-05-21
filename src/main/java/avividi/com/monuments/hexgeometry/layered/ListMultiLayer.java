package avividi.com.monuments.hexgeometry.layered;

import avividi.com.monuments.hexgeometry.HexLayer;
import avividi.com.monuments.hexgeometry.Hexagon;
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

  public void addLayerAbove(HexLayer<T> layer) {
    layers.add(layer);
  }

  public void addLayerBelow(HexLayer<T> layer) {
    indexOffset++;
    layers.add(0, layer);
  }

  private int layerToIndex(int layer) {
    int index = layer + indexOffset;
    Preconditions.checkState(layers.size() > index);
    return index;
  }

  public boolean hasLayer (int layer) {
    int index = layer + indexOffset;
    return layers.size() > index;
  }

  @Override
  public Stream<Hexagon<T>> getHexagons(int layer) {
    return layers.get(layerToIndex(layer)).getHexagons();
  }

  @Override
  public Optional<Hexagon<T>> getByAxial(LayerPoint point) {
    return layers.get(point.getLayer()).getByAxial(point.getPointAxial());
  }

  @Override
  public T clearHex(LayerPoint point) {
    return layers.get(point.getLayer()).clearHex(point.getPointAxial());
  }

  @Override
  public boolean setHex(T t, LayerPoint point) {
    return layers.get(layerToIndex(point.getLayer())).setHex(t, point.getPointAxial());
  }
}
