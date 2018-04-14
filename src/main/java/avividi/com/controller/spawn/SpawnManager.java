package avividi.com.controller.spawn;

import avividi.com.controller.Board;
import avividi.com.controller.DayStage;
import avividi.com.controller.gameitems.unit.Rivskin;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.util.RandomUtil;

import java.util.List;
import java.util.stream.Collectors;

public class SpawnManager {

  public void spawn (Board board) {
    if (board.getDayStage() != DayStage.dusk) return;

    List<PointAxial> availableEdges = board.getSpawnEdges().stream()
        .filter(board::hexIsFree)
        .collect(Collectors.toList());

    if (availableEdges.isEmpty()) return;

    int index = RandomUtil.get().nextInt(availableEdges.size());
    PointAxial pos = availableEdges.get(index);

    if (board.hexIsFree(pos) && RandomUtil.get().nextDouble() > 0.95) {
      board.getUnits().setHex(new Rivskin(), pos);
    }
  }
}
