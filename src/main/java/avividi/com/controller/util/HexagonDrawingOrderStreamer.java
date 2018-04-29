package avividi.com.controller.util;

import avividi.com.controller.Board;
import avividi.com.controller.HexItem;
import avividi.com.controller.Marker;
import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.gameitems.Interactor;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;

import java.util.function.Function;
import java.util.stream.Stream;

public class HexagonDrawingOrderStreamer {

  private final CropFilter cropFilter;
  private final Board board;


  public HexagonDrawingOrderStreamer(Board board) {
    this.cropFilter = new CropFilter(board.getGround());
    this.board = board;
  }

  public Stream<Hexagon<? extends HexItem>> getHexagons(Marker marker) {
    Stream<Hexagon<GameItem>> groundStream = board.getGround().getHexagons();
    Stream<Hexagon<Interactor>> otherStream = board.getOthers().getHexagons();
    Stream<Hexagon<Unit>> unitStream = board.getUnits().getHexagons();

    Hexagon<GameItem> markerHex = marker.asHexagon(board.getGround());
    Stream<Hexagon<GameItem>> mark = marker.toggled() ? Stream.of(marker.asHexagon(board.getGround())) : Stream.empty();
    cropFilter.adjustToMarker(markerHex);

    return cropFilter.crop(Stream.of(groundStream, otherStream, unitStream, mark)
        .flatMap(Function.identity())
        .filter(h -> h.getObj().renderAble()));
  }

}
