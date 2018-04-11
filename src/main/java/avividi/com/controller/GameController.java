package avividi.com.controller;

import avividi.com.controller.gameitems.*;
import avividi.com.controller.hexgeometry.Grid;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.Point2d;
import avividi.com.controller.task.TaskManager;
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
  private final Random random = new Random();


  private Map<Character, Supplier<GameItem>> groundSupplier =
      ImmutableMap.of(
          '=', () -> new Ground(random)
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
      "§ § § + + + + + + + + + + + § + + + +\n",
      " § § + + + + + + + + + + + § § + + + \n",
      "§ § § + + + + + + + + + + + + + + + +\n",
      " § § + + + + + + + + + + + + § + + + \n",
      "§ § + § + § + + + + + + + + + + + + +\n",
      " § § + + + + + + + + + + + + + + + + \n",
      "§ § + + + + § + O O + + + § + + + + +\n",
      " § + + + + + + O W + + + + + + + + + \n",
      "§ + + + + + + + O O + + + + + + + + +\n",
      " + § O + + + + + + + + + + § + + § § \n",
      "+ + O + + + + + + + + + + § § + + + §\n",
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
      "+ + + + S + + + + + + + + + + + + + +\n",
      " + + + + S + + + + + + + + + + + + + \n",
      "+ + + + S + + + + S + + + + + + + + +\n",
      " + + + + S + + + + + + + + + + + + + \n",
      "+ + + + S + + + + + + + + + + + + + +\n",
      " + + + + S + + + + + + + + + + + + + \n",
      "+ + + + + + + + + + + + + + + + + + +\n",
      " + + + + + + + + + + + + + + + + + + \n",
      "+ + + + + + + + + + S + + + + + S S +\n",
      " + + + + + + + + + + + + + + + + + S \n",
      "+ U + + + + + + + + + + + + + S S S S\n",
      " + + + + + + + + + + + + + + + + S S \n");

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


    long units1 = board.getUnits().getHexagons(Maldar.class).count();
    board.getUnits().getHexagons()
        .collect(Collectors.toList())
        .forEach(item -> item.getObj().endOfTurnAction(board, item.getPosAxial(), getDayStage()));

    long units2 = board.getUnits().getHexagons(Maldar.class).count();

    Preconditions.checkState(units1 == units2);

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
    if (clock > 500) clock = 0;
  }

  @Override
  public DayStage getDayStage() {
    if (clock > 470) return DayStage.dawn;
    if (clock > 380) return DayStage.night;
    if (clock > 350) return DayStage.dusk;
    return DayStage.day;
  }

//  private void handleClockStep() {
//    clock++;
//    if (clock > 100) clock = 0;
//  }
//
//  @Override
//  public DayStage getDayStage() {
//    if (clock > 80) return DayStage.dawn;
//    if (clock > 50) return DayStage.night;
//    if (clock > 30) return DayStage.dusk;
//    return DayStage.day;
//  }
}
