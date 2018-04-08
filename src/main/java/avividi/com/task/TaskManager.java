package avividi.com.task;

import avividi.com.Board;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;
import avividi.com.task.Task;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskManager {

  Queue<Task> taskQueue = new PriorityQueue<>(100,
      Comparator.comparingInt(Task::getPriority).reversed());

  public void manageTasks (Board board, List<Hexagon<Unit>> units) {

    checkForNewTasks(board);

    if (taskQueue.isEmpty()) return;

    Set<Hexagon<Unit>> availableUnits = getAvailableUnits(units);
    if (availableUnits.isEmpty()) {
      taskQueue.remove();
      return;
    }

    while (!taskQueue.isEmpty()) {
      Task task = taskQueue.poll();

      List<Hexagon<Unit>> chosenUnits = task.chooseFromPool(availableUnits);

      for (Hexagon<Unit> u : chosenUnits) {
        u.getObj().assignTask(task);

        boolean feasible = u.getObj().getTask().planningAndFeasibility(board, u);
        if(!feasible) {
           u.getObj().assignTask(null);
        }
        else {
          availableUnits.remove(u);
          break;
        }
      }
    }
  }

  private Set<Hexagon<Unit>> getAvailableUnits (List<Hexagon<Unit>> allUnits) {
    return allUnits.stream()
        .filter(u -> u.getObj().isFriendly())
        .filter(u -> u.getObj().getTask() == null || u.getObj().getTask().getPriority() == 0)
        .collect(Collectors.toSet());
  }

  private void checkForNewTasks (Board board) {
    board.getOthers().getHexagons()
        .filter(io -> !io.getObj().linkedToTask())
        .map(io -> io.getObj().checkForTasks(board.getOthers(), io.getPosAxial()))
        .filter(Optional::isPresent).map(Optional::get)
         .forEach(task ->  taskQueue.add(task));

  }
}
