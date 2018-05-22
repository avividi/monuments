package avividi.com.monuments.controller.gamehex;
import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.controller.userinput.UserAction;
import avividi.com.monuments.hexgeometry.PointAxial;

import java.util.Collections;
import java.util.List;

public interface GameHex extends HexItem {

  default void postLoadCalculation (Board board, PointAxial self) {
  }

  default void doUserAction(UserAction action, Board board, PointAxial self) {
  }

  default List<UserAction> getUserActions() {
    return Collections.emptyList();
  }

  boolean passable();

  default boolean buildable() {
    return false;
  }

  default String getId() {
    return null;
  }
}
