package avividi.com.controller.spawn;

import avividi.com.controller.Board;
import avividi.com.controller.DayStage;
import avividi.com.controller.gameitems.unit.Rivskin;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.util.RandomUtil;

import java.util.List;
import java.util.stream.Collectors;

public class SpawnManager {

  private int spawnCycle = 0;
  private boolean disabled = false;

  public void spawn (Board board) {
    if (disabled) return;
    if (--spawnCycle > 0) return;

    spawnCycle = 100;
    if (board.getDayStage() != DayStage.night) return;
    if (board.getUnits(Rivskin.class).size() >= 4) return;

    List<PointAxial> availableEdges = board.getSpawnEdges().stream()
        .filter(board::hexIsFree)
        .collect(Collectors.toList());

    if (availableEdges.isEmpty()) return;

    int index = RandomUtil.get().nextInt(availableEdges.size());
    PointAxial pos = availableEdges.get(index);

    if (board.hexIsFree(pos) && RandomUtil.get().nextDouble() > 0.5) {
      System.out.println("Spawning RIVSKIN");
      board.getUnits().setHex(new Rivskin(), pos);
    }
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }
}
