package avividi.com.monuments.controller.spawn;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.DayStage;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.unit.Rivskin;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.util.RandomUtil;

import java.util.*;
import java.util.stream.Collectors;

import static avividi.com.monuments.controller.Ticks.TSpawnManager.spawnCycle;

public class SpawnManager {

  private int spawnCycleCount = 0;
  private boolean disabled = false;

  public void spawn (Board board) {
    if (disabled) return;
    if (--spawnCycleCount > 0) return;

    spawnCycleCount = spawnCycle;
    if (!board.isStage(DayStage.night)) return;
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

  public static List<PointAxial> calculateSpawnEdges(Board board) {
    Iterator<Hexagon<GameHex>> iterator = board.getGround().getHexagons().iterator();
    Set<PointAxial> edges = new HashSet<>() ;

    while (iterator.hasNext()) {
      Hexagon<GameHex> next = iterator.next();
      if (!edges.contains(next.getPosAxial())
          && !board.hasStaticObstructions(next.getPosAxial())
          && PointAxial.allDirections.stream()
          .anyMatch(dir -> !board.getGround().getByAxial(next.getPosAxial().add(dir)).isPresent())) {

        edges.add(next.getPosAxial());
      }
    }
    return new ArrayList<>(edges);
  }
}
