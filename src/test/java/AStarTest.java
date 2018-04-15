import avividi.com.controller.Board;
import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.gameitems.staticitems.Ground;
import avividi.com.controller.hexgeometry.Grid;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.pathing.AStar;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AStarTest {

  @Test
  public void testPath() {

    String map = String.join("\n",
        "A - - A - - - - - - - - - - - - - - - - - - - - - - - - - - -",
        " A - - A A A A A A A - - - - - - - - - - - - - - - - - - - - ",
        "- A - - - - - - - - A - - - - - - - - - - - - - - - - - - - -",
        " - A A A A A A A - - A A - - - - - - - - - - - - - - - - - - ",
        "- - A - - - - - A - - - A A - - - - - - - - - - - - - - - - -",
        " - A A - - - - - A A - - - A A A A A A A A A A A A A A A A - ",
        "- - A - - - - - - - A - - - - - - - - - - - - - - A - - - A -",
        " - A - - - - - - - - A A - - - - - - - - - - - - A - - - - A ",
        "- A - - - - - - - - - - A A A A A A A A A A - - A - - - - - A",
        " - A - - - - - - - - - - - A - - - - - - A - - A - - - A - A ",
        "- A - - - - - - - - - - - - A - - - - - - A - - A - - A - A -",
        " - A - - - - - - - - - - - - A - - - - - - A - - - - A - A - ",
        "- A A - - - - - - - - - - - - A A - - - - - A - - - A - A - -",
        " - - A A A A A - - - - - - - - - A - - - - - A - - A - A - - ",
        "- - - - - - - A - - A A - - - - - A A A A - - - - A - A - - -",
        " - - - - - - - A A A - A - - - - - - - - A - - - A - - A - - ",
        "- - A A A A - - - A - - A A A A A A A - - A A A A A - - A - -",
        " - A - - - A A A - - - A - - - - - - A A - A - - - A - - A - ",
        "- - A A - - - - A A A A - - - - - - - - A - - - - - A - - - -",
        " - A - A - - - - - - - - A A A A A A - - A A A A A - A - - A ",
        "- - A - - - A A A A A - A - - - - - A - - - - - - - - - - A -",
        " - A - - - A - A - - - A - - - - - - A A A A A A - - - - A - ",
        "- - A - - A - - A - - A - - - A A A A A - - - - - - - - A - -",
        " - - A - A - - - A - A - - - - - - - - A - - - - - - A A A - ",
        "- - - A A - - - - A A - - - - - - - - - A A A A A A A - - A -",
        " - - - - - - - - - - - - - - - - - - - - - - - - - - - - - A "
    );

    Map<Character, Supplier<GameItem>> supplier = ImmutableMap.of
        (
            '-', () -> new GameItem() {
              public String getImageName() { return null;}
              public void endOfTurnAction(Board board, PointAxial self) { }
              public boolean passable() {return false; }
            },
            'A', Ground::new
        );

    Grid<GameItem> grid = new Grid<>(map, supplier);

    List<Hexagon<GameItem>> list = grid.getHexagons().collect(Collectors.toList());
    Hexagon<GameItem> first = list.get(0);
    Hexagon<GameItem> last = list.get(list.size() - 1);

    Optional<List<PointAxial>> path = AStar.builder()
        .withOrigin(first.getPosAxial())
        .withDestination(last.getPosAxial())
        .withIsPathable(p -> grid.getByAxial(p).filter(h -> h.getObj().passable()).isPresent())
        .get();

    Assert.assertTrue(path.isPresent());
    Assert.assertEquals(path.get().size(), 83);

    Optional<List<PointAxial>> noPath = AStar.builder()
        .withOrigin(list.get(3).getPosAxial())
        .withDestination(last.getPosAxial())
        .withIsPathable(p -> grid.getByAxial(p).filter(h -> h.getObj().passable()).isPresent())
        .get();

    Assert.assertFalse(noPath.isPresent());
  }
}
