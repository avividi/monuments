package avividi.com.monuments.controller.gamehex.other.buildmarker;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.staticitems.AutoWall;
import avividi.com.monuments.controller.item.BoulderItem;
import avividi.com.monuments.hexgeometry.PointAxial;


public class WallBuildMarker extends BuildMarker {

  public WallBuildMarker() {
    super(BoulderItem.class, 2, 30, 1);
  }

  @Override
  public void everyTickAction(Board board, PointAxial self) {
    if (fullFilled()) {


      int layer = self.getLayer();
      if (!board.getStatics().getByAxial(self).filter(h -> h.getObj() instanceof AutoWall).isPresent()) createWall(board, self);
      else board.getOthers().clearHex(self);

      board.addLayerAbove(layer);
      createWall(board, new PointAxial(self.getX(), self.getY(), layer + 1));

    }
  }

  private void createWall(Board board, PointAxial self) {

    AutoWall builtWall = new AutoWall(board, self);
    board.setShouldCalculateSectors();
    board.getOthers().clearHex(self);
    board.getStatics().setHex(builtWall, self);
    builtWall.recalculateWallGraph(board, self);
  }

}
