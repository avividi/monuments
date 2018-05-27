package avividi.com.monuments.controller.gamehex.staticitems;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.userinput.UserAction;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class GroundFloor implements GameHex {

  private GameHex background;

  public GroundFloor(ObjectNode json) {

  }

  public GroundFloor(Board board, PointAxial self) {
    board.getStatics().getByAxial(self).ifPresent(h -> background = h.getObj());
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of("rough-floor");
  }

  @Override
  public boolean passable() {
    return true;
  }

  @Override
  public String getId() {
    return "floor";
  }

  @Override
  public void doUserAction(UserAction action, Board board, PointAxial self) {
    if (action == UserAction.clear) {
      throw new UnsupportedOperationException("TODO");
    }
  }

  @Override
  public boolean buildable() {
    return true;
  }

  @Override
  public List<UserAction> getUserActions() {
    return ImmutableList.of(UserAction.clear);
  }
}
