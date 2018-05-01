import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.staticitems.CustomStaticItem;
import avividi.com.monuments.controller.gamehex.staticitems.Ground;
import avividi.com.monuments.hexgeometry.Grid;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.pathing.AStar;
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

    Map<Character, Supplier<GameHex>> supplier = ImmutableMap.of
        (
            '-', () -> new CustomStaticItem(null, HexItem.Transform.none, false, false),
            'A', () -> new Ground(null)
        );

    Grid<GameHex> grid = new Grid<>(map, supplier);

    List<Hexagon<GameHex>> list = grid.getHexagons().collect(Collectors.toList());
    Hexagon<GameHex> first = list.get(0);
    Hexagon<GameHex> last = list.get(list.size() - 1);

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
