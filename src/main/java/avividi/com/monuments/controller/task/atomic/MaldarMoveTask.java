package avividi.com.monuments.controller.task.atomic;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.util.DirectionTransformUtil;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.AxialDirection;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.task.plan.DefaultLeisurePlan;
import avividi.com.monuments.controller.task.plan.Plan;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaldarMoveTask implements Task {

  private static final int tick_moveTime = 5;

  private final AxialDirection dir;
  private boolean isComplete = false;
  private int timeCount = tick_moveTime;
  private boolean shouldAbort = false;
  private int swapTrialCounts = 0;

  public MaldarMoveTask(AxialDirection dir) {
    this.dir = dir;
  }

  @Override
  public boolean perform(Board board, Hexagon<Unit> unit) {
    if (--timeCount > 0) return true;
    isComplete = true;

    PointAxial newPos = unit.getPosAxial().add(dir.dir);


    if(!board.hexIsPathAblePlanning(newPos, dir)) {
      shouldAbort = true;
      System.out.println("  abort hex not pathable");
      return false;
    }
    else if (!board.hexIsFreeForUnit(newPos)) {
      return checkForAndMakeSwapPossibility(board, unit);
    }

    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
    unit.getObj().setTransform(DirectionTransformUtil.getTransform(dir.dir));
    Preconditions.checkState(board.getUnits().setHex(unit.getObj(), unit.getPosAxial().add(dir.dir)));

    return true;
  }

  @Override
  public boolean shouldAbort() {
    return shouldAbort;
  }

  @Override
  public boolean isComplete() {
    return isComplete;
  }

  public static List<Task> fromPoints(List<PointAxial> pointAxials) {
    List<Task> tasks = new ArrayList<>();
    PointAxial prev = null;
    for (PointAxial p : pointAxials) {
      if (prev != null) {
        tasks.add(new MaldarMoveTask(AxialDirection.fromPoints(prev, p)));
      }
      prev = p;
    }
    return tasks;
  }

  public PointAxial getDir() {
    return dir.dir;
  }

  public boolean checkForAndMakeSwapPossibility(Board board, Hexagon<Unit> unit) {


    Optional<Hexagon<Unit>> other = board.getUnits().getByAxial(unit.getPosAxial().add(dir.dir));
    if (!other.isPresent() || swapTrialCounts++ > 10) {
      System.out.println("  aborting... swapTrialCounts=" + swapTrialCounts);
      this.shouldAbort = true;
      return false;
    }

    Hexagon<Unit> otherUnit = other.get();



    return hasThisAsDestination(board, unit, otherUnit);

  }

  private boolean hasThisAsDestination (Board board, Hexagon<Unit> unit, Hexagon<Unit> otherUnit) {
    Plan uOtherPlan = otherUnit.getObj().getPlan();
    if (uOtherPlan == null) return false;
    Plan uPlan = unit.getObj().getPlan();
    if (uPlan instanceof DefaultLeisurePlan) return false;
    Task uTask = uPlan.getNextAtomicTask();
    if(!(uTask instanceof MaldarMoveTask)) return false;

    MaldarMoveTask task = (MaldarMoveTask) uTask;
    if (uOtherPlan instanceof DefaultLeisurePlan) {
      return swapAgainstLeisure(board, unit, otherUnit, task, (DefaultLeisurePlan) uOtherPlan);
    }

    Task uOtherTask = uOtherPlan.getNextAtomicTask();
    if (uOtherTask == null) {return false; }
    else if (!(uOtherTask instanceof MaldarMoveTask)) return false;

    MaldarMoveTask otherTask = (MaldarMoveTask) uOtherTask;
    PointAxial itsNewPos = otherUnit.getPosAxial().add(otherTask.getDir());

    if (itsNewPos.equals(unit.getPosAxial())) {
      return swapAgainstAtomicMoveTask(board, unit, otherUnit, task, otherTask);
    };

    return false;
  }

  private boolean swapAgainstAtomicMoveTask(Board board,
                                            Hexagon<Unit> unit,
                                            Hexagon<Unit> otherUnit,
                                            MaldarMoveTask moveTask,
                                            MaldarMoveTask otherMoveTask) {
//    Preconditions.checkState(board.getUnits().getByAxial(otherUnit.getPosAxial()).filter(u -> u.getObj() == otherUnit.getObj()).isPresent());
//    Preconditions.checkState(board.getUnits().getByAxial(unit.getPosAxial()).filter(u -> u.getObj() == unit.getObj()).isPresent());
//    Preconditions.checkState(!unit.getPosAxial().equals(otherUnit.getPosAxial()));

    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
    unit.getObj().setTransform(DirectionTransformUtil.getTransform(dir.dir));
    otherMoveTask.timeCount = 1;
    otherUnit.getObj().getPlan().performStep(board, otherUnit);
    otherUnit.getObj().getPlan().addNoOp();
    board.getUnits().setHex(unit.getObj(), otherUnit.getPosAxial());


    System.out.println("  flip against move task");

    return true;
  }

  private boolean swapAgainstLeisure(Board board,
                                    Hexagon<Unit> unit,
                                    Hexagon<Unit> otherUnit,
                                    MaldarMoveTask moveTask,
                                    DefaultLeisurePlan leisurePlan) {
//    Preconditions.checkState(board.getUnits().getByAxial(otherUnit.getPosAxial()).filter(u -> u.getObj() == otherUnit.getObj()).isPresent());
//    Preconditions.checkState(board.getUnits().getByAxial(unit.getPosAxial()).filter(u -> u.getObj() == unit.getObj()).isPresent());
//    Preconditions.checkState(!unit.getPosAxial().equals(otherUnit.getPosAxial()));
    Preconditions.checkNotNull(board.getUnits().clearHex(unit.getPosAxial()));
    unit.getObj().setTransform(DirectionTransformUtil.getTransform(dir.dir));
    board.getUnits().setHex(unit.getObj(), otherUnit.getPosAxial());
    otherUnit.getObj().getPlan().addNoOp();
    board.getUnits().setHex(otherUnit.getObj(), unit.getPosAxial());


    System.out.println("  flip against leisure task");

    return true;
  }
}
