package avividi.com.monuments.controller.task;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.DayStage;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.controller.task.plan.Plan;

import java.util.*;
import java.util.stream.Collectors;



public class PlanManager {

  private Queue<Plan> planQueue = new PriorityQueue<>(64,
      Comparator.comparingInt(Plan::getPriority).reversed());

  public void manageTasks (Board board) {

    checkForNewPlans(board);

    if (planQueue.isEmpty()) return;

    Set<Hexagon<Unit>> availableUnits = getAvailableUnits(board.getFriendlyUnits());
    if (availableUnits.isEmpty()) {
      planQueue.clear();
      return;
    }

    while (!planQueue.isEmpty()) {
      Plan plan = planQueue.poll();

      List<Hexagon<Unit>> chosenUnits = plan.chooseFromPool(board, availableUnits);

      for (Hexagon<Unit> u : chosenUnits) {
        u.getObj().assignPlan(plan);

        boolean feasible = u.getObj().getPlan().planningAndFeasibility(board, u);

        if(!feasible) u.getObj().assignPlan(null);
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
        .filter(p -> nightFilter(board, p))
         .forEach(task ->  planQueue.add(task));
  }

  //no unimportant tasks at night... should be configurable
  private boolean nightFilter (Board board, Plan plan) {
    return plan.getPriority() > 4 || board.getDayStage() == DayStage.day;
  }
}
