package avividi.com.monuments.controller.gamehex.staticitems;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.hexgeometry.HexLayer;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static avividi.com.monuments.hexgeometry.PointAxial.*;

public class AutoWall implements GameHex {

  private String location;
  private final List<String> images = new ArrayList<>();
  private GameHex background;
  private boolean passable;
  private boolean buildable;

  public AutoWall(Board board,
                  PointAxial self) {
    board.getStatics().getByAxial(self).ifPresent(hex -> background = hex.getObj());
  }


  public void calculateWallImage(Board board, PointAxial self) {

    passable = !board.getStatics().getByAxial(self.add(PointAxial.UP))
        .filter(hex -> hex.getObj() instanceof AutoWall).isPresent();

    buildable = passable;

    location = passable  ? "wall3" : "wall3-sublevel";

    images.clear();
    Optional.ofNullable(background).ifPresent(b -> images.addAll(b.getImageNames()));

    Function<PointAxial, Boolean> isWall = getIsOfClassFunction(board.getStatics(), self, AutoWall.class);

    if (is(W, E, isWall)) setImage("w-e");
    else if (is(W, NE, isWall)) setImage("w-ne");
    else if (is(W, SE, isWall)) setImage("w-se");
    else if (is(NW, SW, isWall)) setImage("nw-sw");
    else if (is(NW, E, isWall)) setImage("nw-e");
    else if (is(NW, SE, isWall)) setImage("nw-se");
    else if (is(SW, NE, isWall)) setImage("sw-ne");
    else if (is(SW, E, isWall)) setImage("sw-e");
    else if (is(NE, SE, isWall)) setImage("ne-se");
    else setImage("full");
  }

  @Override
  public boolean passable() {
    return passable;
  }

  @Override
  public boolean buildable() {
    return buildable;
  }

  @Override
  public List<String> getImageNames() {
    return images;
  }


  private Function<PointAxial, Boolean> getIsOfClassFunction (HexLayer<GameHex> ground, PointAxial pos, Class<?> clazz) {
    return (dir) -> ground.getByAxial(pos.add(dir)).filter(h -> h.getObj().getClass().equals(clazz)).isPresent();
  }

  private void setImage(String positionCode) {
    images.add(location + "/" + location + "-" + positionCode);
  }


  private boolean is(PointAxial p1, PointAxial p2, Function<PointAxial, Boolean> isWall) {
    return isWall.apply(p1) && isWall.apply(p2);
  }

  public void recalculateBelowWall (Board board, PointAxial self) {
    board.getStatics().getByAxial(self.add(PointAxial.DOWN))
        .filter(hex -> hex.getObj() instanceof AutoWall).ifPresent(h -> ((AutoWall) h.getObj()).calculateWallImage(board, self.add(PointAxial.DOWN)));
  }

  public void recalculateWallGraph(Board board, PointAxial self) {
    recalculateBelowWall(board, self);

    Set<PointAxial> visited = new HashSet<>();
    List<Hexagon<AutoWall>> neighbors = new ArrayList<>();
    neighbors.add(new Hexagon<>(this, self, null));

    while (!neighbors.isEmpty()) {
      Hexagon<AutoWall> next = neighbors.remove(0);
      next.getObj().calculateWallImage(board, next.getPosAxial());
      visited.add(next.getPosAxial());
      findWallNeighbors(board, self, next.getPosAxial(), visited)
          .forEach(neighbors::add);
    }
  }

  private Stream<Hexagon<AutoWall>> findWallNeighbors(Board board,
                                                      PointAxial orig,
                                                      PointAxial pos,
                                                      Set<PointAxial> visited) {
    return PointAxial.cardinalDirectionList.stream()
        .map(dir -> dir.add(pos))
        .filter(n -> PointAxial.distance(orig, n) <= 2)
        .filter(n -> !visited.contains(n))
        .map(n -> board.getStatics().getByAxial(n))
        .filter(opt -> opt.filter(h -> h.getObj() instanceof AutoWall).isPresent())
        .map(Optional::get)
        .map(Hexagon::as);
  }
}
