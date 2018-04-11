package avividi.com.controller.task.atomic;

import avividi.com.controller.Board;
import avividi.com.controller.DirectionTransformUtil;
import avividi.com.controller.gameitems.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.task.DefaultLeisureTask;
import avividi.com.controller.task.Task;
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

//    Preconditions.checkState(board.getUnits().getByAxial(unit.getPosAxial()).filter(u -> u.getObj() == unit.getObj()).isPresent());
    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
//    Preconditions.checkState(board.getUnits().clearHex(unit.getPosAxial().add(dir)) == null);
    unit.getObj().setTransform(DirectionTransformUtil.getTransform(dir));
    Preconditions.checkState(board.getUnits().setHex(unit.getObj(), unit.getPosAxial().add(dir)));

    return true;
  }

  @Override
  public boolean abortSuggested() {
    return false;
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
    Task t = otherUnit.getObj().getTask();
    if (t == null) return false;
    AtomicTask ot = t.getNextAtomicTask();
    if (t instanceof DefaultLeisureTask && !(unit.getObj().getTask() instanceof DefaultLeisureTask)) {
      brutishSwap(board, unit, otherUnit);
      return true;
    }

    if (ot == null) {return false;
    }
    else if (!(ot instanceof AtomicMoveTask)) return false;
    AtomicMoveTask otherTask = (AtomicMoveTask) ot;

    PointAxial itsNewPos = otherUnit.getPosAxial().add(otherTask.getDir());

    if (itsNewPos.equals(unit.getPosAxial())) {
      swap(board, unit, otherUnit);
      return true;
    };

    return false;
  }

  private void swap (Board board, Hexagon<Unit> unit, Hexagon<Unit> otherUnit) {
//    Preconditions.checkState(board.getUnits().getByAxial(otherUnit.getPosAxial()).filter(u -> u.getObj() == otherUnit.getObj()).isPresent());
//    Preconditions.checkState(board.getUnits().getByAxial(unit.getPosAxial()).filter(u -> u.getObj() == unit.getObj()).isPresent());
//    Preconditions.checkState(!unit.getPosAxial().equals(otherUnit.getPosAxial()));
    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
    unit.getObj().setTransform(DirectionTransformUtil.getTransform(dir));
    otherUnit.getObj().getTask().performStep(board, otherUnit);
    otherUnit.getObj().getTask().addNoOp();
    board.getUnits().setHex(unit.getObj(), otherUnit.getPosAxial());
  }

  private void brutishSwap (Board board, Hexagon<Unit> unit, Hexagon<Unit> otherUnit) {
//    Preconditions.checkState(board.getUnits().getByAxial(otherUnit.getPosAxial()).filter(u -> u.getObj() == otherUnit.getObj()).isPresent());
//    Preconditions.checkState(board.getUnits().getByAxial(unit.getPosAxial()).filter(u -> u.getObj() == unit.getObj()).isPresent());
//    Preconditions.checkState(!unit.getPosAxial().equals(otherUnit.getPosAxial()));
    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
    unit.getObj().setTransform(DirectionTransformUtil.getTransform(dir));
    board.getUnits().setHex(unit.getObj(), otherUnit.getPosAxial());
    otherUnit.getObj().getTask().addNoOp();
    board.getUnits().setHex(otherUnit.getObj(), unit.getPosAxial());
  }
}
