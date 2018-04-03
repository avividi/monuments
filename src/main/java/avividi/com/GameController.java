package avividi.com;

import avividi.com.gameitems.*;
import avividi.com.hexgeometry.Grid;
import avividi.com.hexgeometry.Point2d;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GameController implements Controller {

  private List<ControllerListener> listeners = new ArrayList<>();
  private Board board;
  private Random random = new Random();
  private int actionsLeft = 20;

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


  private Map<Character, Supplier<GameItem>> unitSupplier =
      ImmutableMap.<Character, Supplier<GameItem>>builder()
          .put('S', Unit::new)
          .put( 'O', () -> new CustomStaticItem("boulder"))
          .put( '#', () -> new CustomStaticItem("brick"))
          .put( 'W', Fire::new)
          .put( '§', FirePlant::new)
          .build();

  private String unitMap = String.join("", "",
      "S + § + + + + + + + + + + + + + + + +\n",
      " + § + § + + + + + + § § + + + + + + \n",
      "+ + § + + + + + + + + + + + + § + + +\n",
      " + + § § + + + + + + + + + + + § § § \n",
      "+ + + + § + + # O + + + + + + § + + +\n",
      " + + + § + + + + § + + + § + § + + + \n",
      "§ + + § + + + + § + + + + § § + + + §\n",
      " + + + § § + + § + + + + + + + § + + \n",
      "+ § + + § + + § + + + + + + + § § + W\n",
      " + + + + + + + § § § § + § + § + + + \n",
      "+ + + + + + + + + + + § § § § + + + +\n",
      " + + + + + + + + + + + O § + § + + + \n",
      "+ + + + + + + + + + + + § + + + + + §\n",
      " + + + + + + + + + + + § § + + + + + \n",
      "+ + + + + + + + + + + + + § + § O + +\n",
      " + + + + + + + + + + + + + + + § + + \n");

  public GameController () {
    board = new Board(
        new Grid<>(groundMap, groundSupplier),
        new Grid<>(unitMap, unitSupplier)
    );

    new Timer().scheduleAtFixedRate(triggerEndOfTurn(), 1000, 500);
  }

  @Override
  public Board getBoard() {
    return board;
  }

  @Override
  public void giveInput(Point2d point2d) {
    board.getUnits().getBy2d(point2d)
        .filter(hex -> hex.getObj().clickAble())
        .ifPresent(hex -> {
          hex.getObj().clickAction(board, hex.getPosAxial());
//          endOfTurn();
          notifyListeners();
        });
  }

  private void endOfTurn() {
    board.getUnits().getHexagons()
        .collect(Collectors.toList())
        .forEach(item -> item.getObj().endOfTurnAction(board, item.getPosAxial()));
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

  private TimerTask triggerEndOfTurn () {
    return new TimerTask() {
      @Override
      public void run() {
        notifyListeners();
        endOfTurn();
      }
    };
  }

}
