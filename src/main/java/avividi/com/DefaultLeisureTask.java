package avividi.com;

import avividi.com.gameitems.Fire;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;

import java.util.ArrayList;
import java.util.List;

public class DefaultLeisureTask implements Task {

  List<PointAxial> path = new ArrayList<>();

  @Override
  public void performStep(Board board, PointAxial self, Unit unit) {
    Hexagon<GameItem> fire = board.getUnits().getByFirstOccurrence(Fire.class).get();

    if (PointAxial.distance(fire.getPosAxial(), self) > 2) {
      if (path.isEmpty()) {
        path = new AStar(board).withDestination(fire.getPosAxial()).withOrigin(self)
            .get().get();
      }
      board.getUnits().clearHex(self);
      board.getUnits().setHex(unit, self.add(path.get(0)));
      path.remove(0);
    }
  }

  @Override
  public boolean isComplete() {
    return false;
  }
}
