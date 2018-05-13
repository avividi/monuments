package avividi.com.monuments.controller;
import avividi.com.monuments.controller.gamehex.unit.Maldar;
import avividi.com.monuments.controller.userinput.UserAction;
import avividi.com.monuments.controller.userinput.UserActionManager;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.Point2d;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.loader.JsonMapLoader;
import avividi.com.monuments.controller.spawn.SpawnManager;
import avividi.com.monuments.controller.task.PlanManager;
import avividi.com.monuments.controller.util.HexagonDrawingOrderStreamer;
import com.google.common.base.Preconditions;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameController implements Controller {

  private final Board board;
  private final PlanManager planManager = new PlanManager();;
  private final SpawnManager spawnManager = new SpawnManager();;
  private final UserActionManager userActionManager;
  private final HexagonDrawingOrderStreamer hexagonDrawingOrderStreamer;

  public GameController(String mapUrl) {
    board =  new JsonMapLoader(mapUrl).get();
    this.hexagonDrawingOrderStreamer = new HexagonDrawingOrderStreamer(board);

    userActionManager = new UserActionManager(new PointAxial(0, 0));
  }

  public Board getBoard() {
    return board;
  }

  @Override
  public Stream<Hexagon<? extends HexItem>> getHexagons() {

    return hexagonDrawingOrderStreamer.getHexagons(userActionManager.getMarker());
  }

  @Override
  public Point2d getPosition2d(double imageHeight, double x, double y, double padding) {
    return board.getGround().getPosition2d(imageHeight, x, y, padding);
  }

  @Override
  public void makeAction(UserAction action, boolean secondary) {

    userActionManager.handleAction(board, action, secondary);

    if (action == UserAction.debugSectors) hexagonDrawingOrderStreamer.toggleDebugSectors();
    else if (action == UserAction.debugPaths) hexagonDrawingOrderStreamer.toggleDebugPaths();
  }

  @Override
  public List<UserAction> getSelectUserActions() {
    return userActionManager.getSelectUserActions(board);
  }


  @Override
  public void oneStep() {
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
  public DayStage getDayStage() {
    return board.getDayStage();
  }

  public void setDisableSpawns(boolean disableSpawns) {
    this.spawnManager.setDisabled(disableSpawns);
  }
}
