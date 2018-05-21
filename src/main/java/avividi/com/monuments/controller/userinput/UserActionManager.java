package avividi.com.monuments.controller.userinput;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.hexgeometry.PointAxial;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UserActionManager {

  private final Marker marker;
  private final BuildManager buildManager = new BuildManager();

  public UserActionManager(PointAxial pointAxial) {
    marker = new Marker(pointAxial);
  }

  public Marker getMarker() {
    return marker;
  }

  public void handleAction(Board board, UserAction action, boolean secondary) {

    PointAxial pos = marker.getCurrentPosition();
    Consumer<PointAxial> markerMove = (dir) -> marker.move(board, dir, secondary ? 5 : 1);

    if (action.type == UserAction.ActionType.build)
      buildManager.handleAction(board, marker, action, secondary);
    else if (action.type == UserAction.ActionType.select) {
      board.getOthers().getByAxial(pos).ifPresent(hex -> hex.getObj().doUserAction(action, board, pos));
      board.getStatics().getByAxial(pos).ifPresent(hex -> hex.getObj().doUserAction(action, board, pos));
      board.getUnits().getByAxial(pos).ifPresent(hex -> hex.getObj().doUserAction(action, board, pos));
    }
    else if (action == UserAction.deToggleMarker) marker.toggle(false,false);
    else if (action == UserAction.toggleBuildMarker) marker.toggle(true,true);
    else if (action == UserAction.toggleInfoMarker) marker.toggle(true, false);
    else if (action == UserAction.moveNE) markerMove.accept(PointAxial.NE);
    else if (action == UserAction.moveNW)  markerMove.accept(PointAxial.NW);
    else if (action == UserAction.moveE)  markerMove.accept(PointAxial.E);
    else if (action == UserAction.moveW) markerMove.accept(PointAxial.W);
    else if (action == UserAction.moveSE) markerMove.accept(PointAxial.SE);
    else if (action == UserAction.moveSW)  markerMove.accept(PointAxial.SW);
  }

  public List<UserAction> getSelectUserActions(Board board) {
    List<UserAction> list = new ArrayList<>();

    PointAxial pos = marker.getCurrentPosition();

    board.getOthers().getByAxial(pos).ifPresent(hex -> list.addAll(hex.getObj().getUserActions()));
    board.getStatics().getByAxial(pos).ifPresent(hex -> list.addAll(hex.getObj().getUserActions()));
    board.getUnits().getByAxial(pos).ifPresent(hex -> list.addAll(hex.getObj().getUserActions()));

    return list;
  }
}
