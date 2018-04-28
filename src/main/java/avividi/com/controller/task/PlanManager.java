package avividi.com.controller.task;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.task.plan.Plan;

import java.util.*;
import java.util.stream.Collectors;

import static avividi.com.controller.Ticks.TPlanManager.waitForRePlan;

public class PlanManager {

  private int waitForRePlanCount = 0;

  private Queue<Plan> planQueue = new PriorityQueue<>(64,
      Comparator.comparingInt(Plan::getPriority).reversed());

  public void manageTasks (Board board) {

    if (--waitForRePlanCount > 0) return;
    waitForRePlanCount = waitForRePlan;

    checkForNewPlans(board);

    if (planQueue.isEmpty()) return;

    Set<Hexagon<Unit>> availableUnits = getAvailableUnits(board.getFriendlyUnits());
    if (availableUnits.isEmpty()) {
      planQueue.clear();
      return;
    }

    while (!planQueue.isEmpty()) {
      Plan task = planQueue.poll();

      List<Hexagon<Unit>> chosenUnits = task.chooseFromPool(availableUnits);

      for (Hexagon<Unit> u : chosenUnits) {
        u.getObj().assignTask(task);

        boolean feasible = u.getObj().getPlan().planningAndFeasibility(board, u);

        if(!feasible) u.getObj().assignTask(null);
        else {
          availableUnits.remove(u);
          break;
        }
      }
    }
  }

  private Set<Hexagon<Unit>> getAvailableUnits (Collection<Hexagon<Unit>> friendlies) {
    return friendlies.stream()
        .filter(u -> u.getObj().getPlan() == null || u.getObj().getPlan().getPriority() == 0)
        .collect(Collectors.toSet());
  }

  private void checkForNewPlans(Board board) {
    board.getOthers().getHexagons()
        .map(io -> io.getObj().checkForPlan(board.getOthers(), io.getPosAxial()))
        .filter(Optional::isPresent).map(Optional::get)
         .forEach(task ->  planQueue.add(task));


  }
}
