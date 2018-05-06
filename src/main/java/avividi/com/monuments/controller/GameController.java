package avividi.com.monuments.controller;
import avividi.com.monuments.controller.gamehex.other.Plot;
import avividi.com.monuments.controller.gamehex.unit.Maldar;
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
    if (action == UserAction.toggleMarker) marker.toggle();
    else if (action == UserAction.moveNE) marker.move(board.getGround(), PointAxial.NE, secondary ? 5 : 1);
    else if (action == UserAction.moveNW) marker.move(board.getGround(), PointAxial.NW, secondary ? 5 : 1);
    else if (action == UserAction.moveE) marker.move(board.getGround(), PointAxial.E, secondary ? 5 : 1);
    else if (action == UserAction.moveW) marker.move(board.getGround(), PointAxial.W, secondary ? 5 : 1);
    else if (action == UserAction.moveSE) marker.move(board.getGround(), PointAxial.SE, secondary ? 5 : 1);
    else if (action == UserAction.moveSW) marker.move(board.getGround(), PointAxial.SW, secondary ? 5 : 1);
    else if (action == UserAction.build && marker.toggled()) {
      PointAxial pos = marker.getCurrentPosition();
      if (!board.getOthers().getByAxial(pos).isPresent()) board.getOthers().setHex(new Plot(DriedPlantItem.class), pos);
    }
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
