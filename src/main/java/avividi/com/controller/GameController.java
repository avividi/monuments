package avividi.com.controller;
import avividi.com.controller.gameitems.unit.Maldar;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.Point2d;
import avividi.com.controller.loader.JsonMapLoader;
import avividi.com.controller.spawn.SpawnManager;
import avividi.com.controller.task.TaskManager;
import com.google.common.base.Preconditions;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameController implements Controller {

  private List<ControllerListener> listeners = new ArrayList<>();
  private Board board;
  private TaskManager taskManager;
  private SpawnManager spawnManager;

  public GameController () {

    board =  new JsonMapLoader("/maps/map1.json").get();

    taskManager = new TaskManager();
    spawnManager = new SpawnManager();
  }

  public Board getBoard() {
    return board;
  }

  @Override
  public Stream<Hexagon<? extends HexItem>> getHexagons() {
    return board.getHexagonsByDrawingOrder();
  }

  @Override
  public Point2d getPosition2d(double imageHeight, double x, double y, double padding) {
    return board.getGround().getPosition2d(imageHeight, x, y, padding);
  }


  @Override
  public void oneStep() {
    notifyListeners();
    endOfTurn();
  }

  @Override
  public void giveInput(Point2d point2d) {
  }

  private void endOfTurn() {
    board.step();

    spawnManager.spawn(board);

    taskManager.manageTasks(board);

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
}
