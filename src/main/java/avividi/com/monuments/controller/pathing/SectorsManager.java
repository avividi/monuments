package avividi.com.monuments.controller.pathing;

import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.staticitems.CustomStaticItem;
import avividi.com.monuments.hexgeometry.AxialDirection;
import avividi.com.monuments.hexgeometry.GridLayer;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SectorsManager {

  private final Multimap<PointAxial, Integer> sectors = HashMultimap.create();
  private final BiPredicate<PointAxial, AxialDirection> isPathable;
  private final Predicate<PointAxial> isFree;

  public SectorsManager(BiPredicate<PointAxial, AxialDirection> isPathable, Predicate<PointAxial> isFree) {

    this.isPathable = isPathable;
    this.isFree = isFree;
  }

  public void calculateSectors(Stream<PointAxial> set, Consumer<PointAxial> otherMapping) {
    this.sectors.clear();

    int currentSector = 0;

    Set<PointAxial> initialSet = set.filter(isFree).collect(Collectors.toSet());

    while (!initialSet.isEmpty()) {
      currentSector++;
      PointAxial next = initialSet.stream().findAny().orElseThrow(IllegalStateException::new);
      initialSet.remove(next);
      exploreSector(currentSector, initialSet, next);
    }
    int x = 0;
  }

  private void exploreSector(int sectorName, Set<PointAxial> initialSet, PointAxial start) {
    Queue<PointAxial> queue = new LinkedList<>();
    queue.add(start);

    //todo optimize
    while (!queue.isEmpty()) {
      PointAxial orig = queue.poll();
      getDirections().forEach(dir -> {
        if (isPathable.test(orig, dir) && initialSet.contains(orig.add(dir.dir))) {
          queue.add(orig.add(dir.dir));
          initialSet.remove(orig.add(dir.dir));
        }
        sectors.put(orig.add(dir.dir), sectorName);
      });
    }
  }

  private Stream<AxialDirection> getDirections() {
    return Arrays.stream(PointAxial.allDirections);
  }

  public Set<Integer> getSector (PointAxial point) {
    return (Set<Integer>) sectors.get(point);
  }


  public boolean isInSameSector(PointAxial p1, PointAxial p2) {
    return !Sets.intersection(
        (Set<Integer>) sectors.get(p1),
        (Set<Integer>) sectors.get(p2))
        .isEmpty();
  }

  public Stream<Hexagon<? extends GameHex>> displaySectorsDebug(GridLayer<GameHex> ground) {
    List<String> images = ImmutableList.of( "marker/marker-green",
        "marker/marker-blue", "marker/marker-yellow");
    Stream.Builder<Hexagon<? extends GameHex>> builder = Stream.builder();

    BiConsumer<String, PointAxial> function = (image, pos) -> {
      ground.pointAxialToPoint2d(pos).ifPresent(point2d -> {
        builder.accept(new Hexagon<>(new CustomStaticItem(
            ImmutableList.of(image),
            HexItem.Transform.none, false, false, false), pos,
            point2d));
      });
    };

    Map<Integer, String> assignedSectors = new HashMap<>();
    AtomicInteger currentImage = new AtomicInteger(0);
    sectors.asMap().forEach((pos, sectors) -> {
      if (sectors.size() > 1) {
        function.accept( "marker/marker-red", pos); return;
      }
      Integer sector = sectors.stream().findAny().orElseThrow(IllegalStateException::new);
      String image = assignedSectors.computeIfAbsent(sector, key -> images.get(currentImage.getAndIncrement()));
      function.accept( image, pos);
      if (currentImage.get() > 2) currentImage.set(0);
    });
    return builder.build();
  };

}
