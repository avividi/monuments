package avividi.com.task;

import avividi.com.AStar;
import avividi.com.Board;
import avividi.com.gameitems.Fire;
import avividi.com.gameitems.InteractingItem;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class DefaultLeisureTask implements Task {

  Random random = new Random();

  List<PointAxial> path = new ArrayList<>();

  @Override
  public void performStep(Board board, PointAxial self, Unit unit) {
    Optional<Hexagon<InteractingItem>> fire = board.getOthers().getByFirstOccurrence(Fire.class);

    if (fire.isPresent() && PointAxial.distance(fire.get().getPosAxial(), self) > 2) {
      if (path.isEmpty()) {
        path = new AStar(board).withDestination(fire.get().getPosAxial()).withOrigin(self)
            .get().orElse(new ArrayList<>());
      }
      if (!path.isEmpty()) {
        makeMove(board, unit, self, self.add(path.get(0)));
        path.remove(0);
      }
    }
    else if (random.nextDouble() > 0.95) {
      PointAxial dir = PointAxial.allDirections.get(random.nextInt(PointAxial.allDirections.size()));
      if (board.hexIsFree(self.add(dir))) makeMove(board, unit, self, self.add(dir));
      path.clear();
    }
  }

  private void makeMove (Board board, Unit unit, PointAxial p1, PointAxial p2){
    board.getUnits().clearHex(p1);
    board.getUnits().setHex(unit, p2);
  }

  @Override
  public boolean isComplete() {
    return false;
  }

  @Override
  public int getPriority() {
    return 0;
  }
}
