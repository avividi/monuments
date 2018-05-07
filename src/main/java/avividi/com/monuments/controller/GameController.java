package avividi.com.monuments.controller;
import avividi.com.monuments.controller.gamehex.other.BuildMarker;
import avividi.com.monuments.controller.gamehex.other.Fire;
import avividi.com.monuments.controller.gamehex.other.Plot;
import avividi.com.monuments.controller.gamehex.other.RoughWall;
import avividi.com.monuments.controller.gamehex.unit.Maldar;
import avividi.com.monuments.controller.item.BoulderItem;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.Point2d;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.item.DriedPlantItem;
import avividi.com.monuments.loader.JsonMapLoader;
import avividi.com.monuments.controller.spawn.SpawnManager;
import avividi.com.monuments.controller.task.PlanManager;
import avividi.com.monuments.controller.util.HexagonDrawingOrderStreamer;
import com.google.common.base.Preconditions;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameController implements Controller {

  private List<ControllerListener> listeners = new ArrayList<>();
  private final Board board;
  private final PlanManager planManager;
  private final SpawnManager spawnManager;
  private final Marker marker;
  private final HexagonDrawingOrderStreamer hexagonDrawingOrderStreamer;

  public GameController(String mapUrl) {

    board =  new JsonMapLoader(mapUrl).get();
    this.hexagonDrawingOrderStreamer = new HexagonDrawingOrderStreamer(board);

    planManager = new PlanManager();
    spawnManager = new SpawnManager();
    marker = new Marker(new PointAxial(0, 0));
  }

  public Board getBoard() {
    return board;
  }

  @Override
  public Stream<Hexagon<? extends HexItem>> getHexagons() {

    return hexagonDrawingOrderStreamer.getHexagons(marker);
  }

  @Override
  public Point2d getPosition2d(double imageHeight, double x, double y, double padding) {
    return board.getGround().getPosition2d(imageHeight, x, y, padding);
  }

  @Override
  public void makeAction(UserAction action, boolean secondary) {

    Consumer<PointAxial> markerMove = (dir) -> marker.move(board, dir, secondary ? 5 : 1);


    if (action == UserAction.deToggleMarker) marker.toggle(false,false);
    if (action == UserAction.toggleBuildMarker) marker.toggle(true,true);
    if (action == UserAction.toggleInfoMarker) marker.toggle(true, false);
    else if (action == UserAction.moveNE) markerMove.accept(PointAxial.NE);
    else if (action == UserAction.moveNW)  markerMove.accept(PointAxial.NW);
    else if (action == UserAction.moveE)  markerMove.accept(PointAxial.E);
    else if (action == UserAction.moveW) markerMove.accept(PointAxial.W);
    else if (action == UserAction.moveSE) markerMove.accept(PointAxial.SE);
    else if (action == UserAction.moveSW)  markerMove.accept(PointAxial.SW);
    else if (action == UserAction.buildPlotWood && marker.toggled()) {
      PointAxial pos = marker.getCurrentPosition();
      if (board.hexIsBuildAble(pos)) board.getOthers().setHex(new Plot(DriedPlantItem.class), pos);
    }
    else if (action == UserAction.buildPlotStone && marker.toggled()) {
      PointAxial pos = marker.getCurrentPosition();
      if (board.hexIsBuildAble(pos)) board.getOthers().setHex(new Plot(BoulderItem.class), pos);
    }
    else if (action == UserAction.buildFire && marker.toggled()) {
      PointAxial pos = marker.getCurrentPosition();
      if (board.hexIsBuildAble(pos)) board.getOthers().setHex(new BuildMarker(DriedPlantItem.class, 1, 20, Fire::new), pos);
    }
    else if (action == UserAction.buildRoughWall && marker.toggled()) {
      PointAxial pos = marker.getCurrentPosition();
      if (board.hexIsBuildAble(pos)) board.getOthers().setHex(new BuildMarker(BoulderItem.class, 3, 30, RoughWall::new), pos);
    }

    else if (action == UserAction.debugSectors) hexagonDrawingOrderStreamer.toggleDebugSectors();
    else if (action == UserAction.debugPaths) hexagonDrawingOrderStreamer.toggleDebugPaths();
  }


  @Override
  public void oneStep() {
    notifyListeners();
    endOfTurn();
  }

  private void endOfTurn() {
    board.step();

    spawnManager.spawn(board);

    planManager.manageTasks(board);

    board.getOthers().getHexagons()
        .collect(Collectors.toList())
        .forEach(item -> item.getObj().endOfTurnAction(board, item.getPosAxial()));


    long units1 = (long) board.getUnits(Maldar.class).size();
    board.getUnits().getHexagons()
        .collect(Collectors.toList())
        .forEach(item -> item.getObj().endOfTurnAction(board, item.getPosAxial()));

    long units2 =  board.getUnits(Maldar.class).size();

    Preconditions.checkState(units1 == units2);

  }

  @Override
  public void addListener(ControllerListener listener) {
    Preconditions.checkState(!this.listeners.contains(listener));
    this.listeners.add(listener);
  }

  private void notifyListeners () {
    this.listeners.forEach(ControllerListener::changesOccurred);
  }

  @Override
  public DayStage getDayStage() {
    return board.getDayStage();
  }

  public void setDisableSpawns(boolean disableSpawns) {
    this.spawnManager.setDisabled(disableSpawns);
  }
}
