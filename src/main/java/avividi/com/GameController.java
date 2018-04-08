package avividi.com;

import avividi.com.gameitems.*;
import avividi.com.hexgeometry.Grid;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.Point2d;
import avividi.com.task.TaskManager;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameController implements Controller {

  private List<ControllerListener> listeners = new ArrayList<>();
  private Board board;
  private int actionsLeft = 20;
  private TaskManager taskManager;
  private int clock = 0;


  private Map<Character, Supplier<GameItem>> groundSupplier =
      ImmutableMap.of(
          '=', Ground::new
      );
  private String groundMap = String.join("", "",
      "= = = = = = = = = = = = = = = = = = =\n",
      " = = = = = = = = = = = = = = = = = = \n",
      "= = = = = = = = = = = = = = = = = = =\n",
      " = = = = = = = = = = = = = = = = = = \n",
      "= = = = = = = = = = = = = = = = = = =\n",
      " = = = = = = = = = = = = = = = = = = \n",
      "= = = = = = = = = = = = = = = = = = =\n",
      " = = = = = = = = = = = = = = = = = = \n",
      "= = = = = = = = = = = = = = = = = = =\n",
      " = = = = = = = = = = = = = = = = = = \n",
      "= = = = = = = = = = = = = = = = = = =\n",
      " = = = = = = = = = = = = = = = = = = \n",
      "= = = = = = = = = = = = = = = = = = =\n",
      " = = = = = = = = = = = = = = = = = = \n",
      "= = = = = = = = = = = = = = = = = = =\n",
      " = = = = = = = = = = = = = = = = = = \n");

  private Map<Character, Supplier<InteractingItem>> interSupplier =
      ImmutableMap.<Character, Supplier<InteractingItem>>builder()
          .put( 'O', () -> new CustomStaticItem("boulder"))
          .put( '#', () -> new CustomStaticItem("brick"))
          .put( 'W', Fire::new)
          .put( '§', FirePlant::new)
          .build();

  private String interactingMap = String.join("", "",
      "+ + + + + + + + + + + + + + § + + + +\n",
      " § + + + + + + + + + + + + § § + + + \n",
      "+ + + + + + + + + + + + + + + + + + +\n",
      " + + + + + + + + + + + + + + § + + + \n",
      "+ + + § + § + + + + + + + + + + + + +\n",
      " + + + + + + + + + + + + + + + + + + \n",
      "+ + + + + + + + + + + + + + + + + + +\n",
      " + + + + + + + O W + + + + + + + + + \n",
      "+ + + + + + + + + + + + + + + + + + +\n",
      " + § O + + + + + + + + + + + + + § § \n",
      "+ + O + + + + + + + + + + + + + + + §\n",
      " + + + + + + + + + + + + + + + § + + \n",
      "+ + + + + + + + + + + + + + + + + + +\n",
      " + + + + O + + + + + + § + + + + § + \n",
      "+ + + § + + + + + § + + + + + + + + +\n",
      " + + § § + + + + + + + + + + + § + + \n");


  private Map<Character, Supplier<Unit>> unitSupplier =
      ImmutableMap.<Character, Supplier<Unit>>builder()
          .put('S', Maldar::new)
          .put('U', Rivskin::new)
          .build();

  private String unitMap = String.join("", "",
      "+ + + + + + + + + + + + + + + + + + +\n",
      " + + + + + + + + + + + + + + + + + + \n",
      "+ + + + + + + + + + + + + + + + + + +\n",
      " + + + + + + + + + + + + + + + + + + \n",
      "+ + + + + + + + + + + + + + + + + + +\n",
      " + + + + + + + + + + + + + + + + + + \n",
      "+ + + + + + + + + S + + + + + + + + +\n",
      " + + + + + + + + + + + + + + + + + + \n",
      "+ + + + + + + + + + + + + + + + + + +\n",
      " + + + + + + + + + + + + + + + + + + \n",
      "+ + + + + + + + + + + + + + + + + + +\n",
      " + + + + + + + + + + + + + + + + + + \n",
      "+ + + + + + + + + + + + + + + + + S +\n",
      " + + + + + + + + + + + + + + + + + + \n",
      "+ U + + + + + + + + + + + + + + + + +\n",
      " + + + + + + + + + + + + + + + + + + \n");

  public GameController () {
    board = new Board(
        new Grid<>(groundMap, groundSupplier),
        new Grid<>(interactingMap, interSupplier),
        new Grid<>(unitMap, unitSupplier)
    );

    taskManager = new TaskManager();
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
    handleClockStep();
  }

  @Override
  public void giveInput(Point2d point2d) {
  }

  private void endOfTurn() {

    taskManager.manageTasks(board, board.getUnits()
        .getHexagons()
        .collect(Collectors.toList()));

    board.getOthers().getHexagons()
        .collect(Collectors.toList())
        .forEach(item -> item.getObj().endOfTurnAction(board, item.getPosAxial(), getDayStage()));


    board.getUnits().getHexagons()
        .collect(Collectors.toList())
        .forEach(item -> item.getObj().endOfTurnAction(board, item.getPosAxial(), getDayStage()));

  }

  @Override
  public void addListener(ControllerListener listener) {
    Preconditions.checkState(!this.listeners.contains(listener));
    this.listeners.add(listener);
  }

  @Override
  public int getActionsLeft() {
    return actionsLeft;
  }

  private void notifyListeners () {
    this.listeners.forEach(ControllerListener::changesOccurred);
  }


  private void handleClockStep() {
    clock++;
    if (clock > 60) clock = 0;
  }

  @Override
  public DayStage getDayStage() {
    if (clock > 50) return DayStage.duskdawn;
    if (clock > 30) return DayStage.night;
    if (clock > 20) return DayStage.duskdawn;
    return DayStage.day;
  }
}
