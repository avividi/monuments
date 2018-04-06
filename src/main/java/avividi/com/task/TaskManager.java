package avividi.com.task;

import avividi.com.Board;
import avividi.com.gameitems.Unit;
import avividi.com.hexgeometry.Hexagon;
import avividi.com.hexgeometry.PointAxial;
import avividi.com.task.Task;

import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

public class TaskManager {

  Queue<Task> taskQueue = new PriorityQueue<>();//TODO

  public void manageTasks (Board board, List<Hexagon<Unit>> units) {


    int i = 0;
    while (!taskQueue.isEmpty() || i > units.size()) {
      Unit unit = units.get(i).getObj();
      PointAxial pos = units.get(i).getPosAxial();

      if (unit.getTask() == null || unit.getTask().getPriority() == 0) {

        unit.assignTask(taskQueue.poll());
        boolean feasable = unit.getTask().planningAndFeasibility(board, pos, unit);
        if(!feasable) unit.assignTask(null);
      }
      i++;
    }

    checkForNewTasks(board);
    int x = 0;
  }

  private void checkForNewTasks (Board board) {
    board.getOthers().getHexagons()
        .map(io -> io.getObj().checkForTasks(board.getOthers(), io.getPosAxial()))
        .filter(Optional::isPresent).map(Optional::get)
        .forEach(task ->  taskQueue.add(task));

  }
}
