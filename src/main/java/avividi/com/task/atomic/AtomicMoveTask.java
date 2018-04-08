package avividi.com.task.atomic;

import avividi.com.Board;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AtomicMoveTask implements AtomicTask {

  private final PointAxial dir;

  public AtomicMoveTask(PointAxial dir) {
    this.dir = dir;
  }

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {

    PointAxial newPos = unit.getPosAxial().add(dir);

    if (!board.hexIsFree(newPos)) {
      return checkForAndMakeSwapPossibility(board, unit);
    }

    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
    Preconditions.checkState(board.getUnits().setHex(unit.getObj(), unit.getPosAxial().add(dir)));

    return true;
  }

  public static List<AtomicTask> fromPoints(List<PointAxial> pointAxials) {
    List<AtomicTask> tasks = new ArrayList<>();
    PointAxial prev = null;
    for (PointAxial p : pointAxials) {
       if (prev != null) {
         tasks.add(new AtomicMoveTask(PointAxial.getDirection(prev, p)));
       }
       prev = p;
    }
    return tasks;
  }

  public PointAxial getDir() {
    return dir;
  }

  public boolean checkForAndMakeSwapPossibility(Board board, Hexagon<Unit> unit) {
    Optional<Hexagon<Unit>> other = board.getUnits().getByAxial(unit.getPosAxial().add(dir));
    if (!other.isPresent()) return false;
    Hexagon<Unit> otherUnit = other.get();

    return hasThisAsDestination(board, unit, otherUnit);

  }

  private boolean hasThisAsDestination (Board board, Hexagon<Unit> unit, Hexagon<Unit> otherUnit) {
    AtomicTask ot = otherUnit.getObj().getTask().getNextAtomicTask();
    if (ot == null || !(ot instanceof AtomicMoveTask)) return false;
    AtomicMoveTask otherTask = (AtomicMoveTask) ot;

    PointAxial thisNewPos = otherUnit.getPosAxial();
    PointAxial itsNewPos = otherUnit.getPosAxial().add(otherTask.getDir());

    if (itsNewPos.equals(unit.getPosAxial())) {
      Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
      otherUnit.getObj().getTask().performStep(board, otherUnit);
      otherUnit.getObj().getTask().addNoOp();
      board.getUnits().setHex(unit.getObj(), thisNewPos);
      return true;
    };

    return false;
  }
}
