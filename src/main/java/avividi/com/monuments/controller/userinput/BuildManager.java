package avividi.com.monuments.controller.userinput;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.gamehex.other.Fire;
import avividi.com.monuments.controller.gamehex.other.Plot;
import avividi.com.monuments.controller.gamehex.other.buildmarker.GameHexBuildMarker;
import avividi.com.monuments.controller.gamehex.other.buildmarker.InteractorBuildMarker;
import avividi.com.monuments.controller.gamehex.other.buildmarker.WallBuildMarker;
import avividi.com.monuments.controller.gamehex.staticitems.AutoWall;
import avividi.com.monuments.controller.gamehex.staticitems.RoughFloor;
import avividi.com.monuments.controller.item.BoulderItem;
import avividi.com.monuments.controller.item.DriedPlantItem;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;

public class BuildManager {


  public void handleAction(Board board, Marker marker, UserAction action, boolean secondary) {

    Preconditions.checkState(action.type == UserAction.ActionType.build);

    if (!marker.isInBuildMode()) return;
    PointAxial pos = marker.getCurrentPosition();
    if (!board.hexIsBuildAble(pos))return;

    Interactor buildHex;

    if (action == UserAction.plotWood)
      buildHex = new Plot(DriedPlantItem.class);
    else if (action == UserAction.plotStone)
      buildHex = new Plot(BoulderItem.class);
    else if (action == UserAction.fire)
      buildHex = new InteractorBuildMarker(DriedPlantItem.class, 1, 100, 5, Fire::new);
    else if (action == UserAction.roughWall)
      buildHex = new WallBuildMarker(BoulderItem.class, 2, 30, 1, () -> new AutoWall(board, pos, "wall3"));
    else if (action == UserAction.roughFloor)
      buildHex = new GameHexBuildMarker(BoulderItem.class, 1, 20, 2, () -> new RoughFloor(board, pos));
    else throw new IllegalStateException();

    board.getOthers().setHex(buildHex, pos);

    marker.checkBuildable(board);


  }
}
