package avividi.com.monuments.controller.userinput;

import static avividi.com.monuments.controller.userinput.UserAction.ActionType.*;

public enum UserAction {



  moveW(move), moveE(move), moveNW(move), moveSW(move), moveNE(move), moveSE(move),

  deToggleMarker(toggle), toggleBuildMarker(toggle), toggleInfoMarker(toggle),

  plotWood(build), plotStone(build), plotLeaf(build), roughWall(build), roughFloor(build), fire(build), ladder(build),
  debugSectors(debug), debugPaths(debug),

  cancel(select), clear(select), activate(select), disable(select);

  public final ActionType type;

  UserAction(ActionType type) {
    this.type = type;
  }

  enum ActionType {
    toggle, move, build, debug, select
  }
}