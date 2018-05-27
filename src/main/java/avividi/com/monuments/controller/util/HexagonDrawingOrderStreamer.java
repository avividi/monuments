package avividi.com.monuments.controller.util;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.controller.userinput.Marker;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.Point2;

import java.util.function.Function;
import java.util.stream.Stream;

public class HexagonDrawingOrderStreamer {


  private final CropFilter cropFilter;
  private final Board board;

  private boolean debugSectors;
  private boolean debugPaths;

  public HexagonDrawingOrderStreamer(Board board) {
    this.cropFilter = new CropFilter(board.getGround());
    this.board = board;
  }

  public Stream<Hexagon<? extends HexItem>> getHexagons(Marker marker) {

    Point2 layerRange = board.getLayerDisplayRange();
    Stream.Builder<Stream<?>> builder = Stream.builder();
    for (int i = layerRange.getX(); i < layerRange.getY(); ++i) {
      builder.add(board.getStatics().getHexagons(i));
      builder.add(board.getOthers().getHexagons(i));
      builder.add(board.getUnits().getHexagons(i));
    }

    Hexagon<? extends HexItem> markerHex = marker.asHexagon(board.getGround());
    Stream<Hexagon<? extends HexItem>> mark = marker.toggled() ? marker.asStream(board.getGround()) : Stream.empty();
    cropFilter.adjustToMarker(markerHex);

    builder.add(mark);

    Stream<Hexagon<? extends GameHex>> uncroppedStream = builder.build()
        .flatMap(Function.identity())
        .filter(h -> ((GameHex) ((Hexagon<?>)h).getObj()).renderAble())
        .map(h -> (Hexagon<? extends GameHex>) h);

    Stream<Hexagon<? extends HexItem>> stream = cropFilter.crop(uncroppedStream);

    if (debugSectors) {
      return Stream.concat(stream, cropFilter.crop(board.getSectorsManager().displaySectorsDebug(board.getGround())));
    }

    return stream;
  }

  public void toggleDebugSectors() {
    debugSectors = !debugSectors;
  }

  public void toggleDebugPaths() {
    debugPaths = !debugPaths;
  }


}
