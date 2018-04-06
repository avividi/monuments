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

  List<AtomicTask> plan = new ArrayList<>();

  @Override
  public boolean planningAndFeasibility(Board board, PointAxial unitPos, Unit unit) {
    return false;
  }

  @Override
  public void performStep(Board board, PointAxial self, Unit unit) {
    Optional<Hexagon<InteractingItem>> fire = board.getOthers().getByFirstOccurrence(Fire.class);

    if (fire.isPresent() && PointAxial.distance(fire.get().getPosAxial(), self) > 2) {
      if (plan.isEmpty()) {
        plan = new AStar(board).withDestination(fire.get().getPosAxial()).withOrigin(self)
            .get()
            .map(AtomicMoveTask::fromPoints)
            .orElse(new ArrayList<>());
      }
      if (!plan.isEmpty()) {
        if (plan.get(0).perform(board, unit, self)) {
          plan.remove(0);
        }
        else {
          randomMove(board, unit, self);
        }
      }
    }
    else if (random.nextDouble() > 0.90) {
      randomMove(board, unit, self);
      plan.clear();
    }
  }

  private void randomMove (Board board, Unit unit, PointAxial point) {

    PointAxial dir = PointAxial.allDirections.get(random.nextInt(PointAxial.allDirections.size()));
    if (board.hexIsFree(point.add(dir))) makeMove(board, unit, point, point.add(dir));
  }

  private void makeMove (Board board, Unit unit, PointAxial p1, PointAxial dir){
    board.getUnits().clearHex(p1);
    board.getUnits().setHex(unit, dir);
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
