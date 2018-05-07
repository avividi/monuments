package avividi.com.monuments.controller.util;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.controller.Marker;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.Hexagon;

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
    Stream<Hexagon<GameHex>> groundStream = board.getGround().getHexagons();
    Stream<Hexagon<Interactor>> otherStream = board.getOthers().getHexagons();
    Stream<Hexagon<Unit>> unitStream = board.getUnits().getHexagons();

    Hexagon<GameHex> markerHex = marker.asHexagon(board.getGround());
    Stream<Hexagon<GameHex>> mark = marker.toggled() ? Stream.of(marker.asHexagon(board.getGround())) : Stream.empty();
    cropFilter.adjustToMarker(markerHex);


    Stream<Hexagon<? extends HexItem>> stream = cropFilter.crop(Stream.of(groundStream, otherStream, unitStream, mark)
        .flatMap(Function.identity())
        .filter(h -> h.getObj().renderAble()));

    if (debugSectors) {
      return Stream.concat(stream, cropFilter.crop(board.getSectors().displaySectorsDebug(board.getGround())));
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
