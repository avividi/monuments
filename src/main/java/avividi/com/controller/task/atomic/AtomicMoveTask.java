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
  private boolean isComplete = false;
  private int steps = 5;

  public AtomicMoveTask(PointAxial dir) {
    this.dir = dir;
  }

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    if (--steps > 0) return true;
    isComplete = true;

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
  public boolean shouldAbort() {
    return false;
  }

  @Override
  public boolean isComplete() {
    return isComplete;
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
    Task uOtherTask = otherUnit.getObj().getTask();
    if (uOtherTask == null) return false;
    Task uTask = unit.getObj().getTask();
    if (uTask instanceof DefaultLeisureTask) return false;
    AtomicTask uAtomicTask = uTask.getNextAtomicTask();
    if(!(uAtomicTask instanceof AtomicMoveTask)) return false;

    AtomicMoveTask atomicTask = (AtomicMoveTask) uAtomicTask;
    if (uOtherTask instanceof DefaultLeisureTask) {
      return swapAgainstLeisure(board, unit, otherUnit, atomicTask, (DefaultLeisureTask) uOtherTask);
    }

    AtomicTask uOtherAtomicTask = uOtherTask.getNextAtomicTask();
    if (uOtherAtomicTask == null) {return false; }
    else if (!(uOtherAtomicTask instanceof AtomicMoveTask)) return false;

    AtomicMoveTask otherAtomicTask = (AtomicMoveTask) uOtherAtomicTask;
    PointAxial itsNewPos = otherUnit.getPosAxial().add(otherAtomicTask.getDir());

    if (itsNewPos.equals(unit.getPosAxial())) {
      return swapAgainstAtomicMoveTask(board, unit, otherUnit, atomicTask, otherAtomicTask);
    };

    return false;
  }

  private boolean swapAgainstAtomicMoveTask(Board board,
                                            Hexagon<Unit> unit,
                                            Hexagon<Unit> otherUnit,
                                            AtomicMoveTask moveTask,
                                            AtomicMoveTask otherMoveTask) {
//    Preconditions.checkState(board.getUnits().getByAxial(otherUnit.getPosAxial()).filter(u -> u.getObj() == otherUnit.getObj()).isPresent());
//    Preconditions.checkState(board.getUnits().getByAxial(unit.getPosAxial()).filter(u -> u.getObj() == unit.getObj()).isPresent());
//    Preconditions.checkState(!unit.getPosAxial().equals(otherUnit.getPosAxial()));

    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
    unit.getObj().setTransform(DirectionTransformUtil.getTransform(dir));
    otherMoveTask.steps = 1;
    otherUnit.getObj().getTask().performStep(board, otherUnit);
    otherUnit.getObj().getTask().addNoOp();
    board.getUnits().setHex(unit.getObj(), otherUnit.getPosAxial());

    return true;
  }

  private boolean swapAgainstLeisure(Board board,
                                    Hexagon<Unit> unit,
                                    Hexagon<Unit> otherUnit,
                                    AtomicMoveTask atomicMoveTask,
                                    DefaultLeisureTask leisureTask) {
//    Preconditions.checkState(board.getUnits().getByAxial(otherUnit.getPosAxial()).filter(u -> u.getObj() == otherUnit.getObj()).isPresent());
//    Preconditions.checkState(board.getUnits().getByAxial(unit.getPosAxial()).filter(u -> u.getObj() == unit.getObj()).isPresent());
//    Preconditions.checkState(!unit.getPosAxial().equals(otherUnit.getPosAxial()));
    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
    unit.getObj().setTransform(DirectionTransformUtil.getTransform(dir));
    board.getUnits().setHex(unit.getObj(), otherUnit.getPosAxial());
    otherUnit.getObj().getTask().addNoOp();
    board.getUnits().setHex(otherUnit.getObj(), unit.getPosAxial());

    return true;
  }
}
