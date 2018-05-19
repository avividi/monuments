package avividi.com.monuments.controller;
import avividi.com.monuments.controller.userinput.UserAction;
import avividi.com.monuments.controller.userinput.UserActionManager;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.Point2d;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.loader.JsonMapLoader;
import avividi.com.monuments.controller.spawn.SpawnManager;
import avividi.com.monuments.controller.task.PlanManager;
import avividi.com.monuments.controller.util.HexagonDrawingOrderStreamer;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameController implements Controller {

  private final Board board;
  private final PlanManager planManager = new PlanManager();;
  private final SpawnManager spawnManager = new SpawnManager();;
  private final UserActionManager userActionManager;
  private final HexagonDrawingOrderStreamer hexagonDrawingOrderStreamer;

  private int every10counter;
  private int every100counter;

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
  public void oneTick() {

    board.prepareOneTick();

    if (board.clock == 0) {
      everyDay();
    }
    if (every100counter++ == 100) {
      every100Tick();
      every100counter = 0;
    }
    if (every10counter++ == 10) {
      every10Tick();
      every10counter = 0;
    }
    everyTick();

  }

  private void everyTick() {
    board.getOthers().getHexagons()
        .forEach(item -> item.getObj().everyTickAction(board, item.getPosAxial()));
    board.getUnits().getHexagons()
        .forEach(item -> item.getObj().everyTickAction(board, item.getPosAxial()));
  }

  private void every10Tick() {
//    System.out.println(String.join("","every10 clock=" + String.valueOf(board.clock)));
    planManager.manageTasks(board);
    board.getOthers().getHexagons()
        .forEach(item -> item.getObj().every10TickAction(board, item.getPosAxial()));
    board.getUnits().getHexagons()
        .forEach(item -> item.getObj().every10TickAction(board, item.getPosAxial()));
  }

  private void every100Tick() {
    spawnManager.spawn(board);
    board.getOthers().getHexagons()
        .forEach(item -> item.getObj().every100TickAction(board, item.getPosAxial()));
    board.getUnits().getHexagons()
        .forEach(item -> item.getObj().every100TickAction(board, item.getPosAxial()));
  }

  private void everyDay() {
    board.getOthers().getHexagons()
        .forEach(item -> item.getObj().everyDayTickAction(board, item.getPosAxial()));
    board.getUnits().getHexagons()
        .forEach(item -> item.getObj().everyDayTickAction(board, item.getPosAxial()));
  }

  @Override
  public DayStage getDayStage() {
    return board.getDayStage();
  }

  public void setDisableSpawns(boolean disableSpawns) {
    this.spawnManager.setDisabled(disableSpawns);
  }
}
