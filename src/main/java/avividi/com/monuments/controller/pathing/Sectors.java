package avividi.com.monuments.controller.pathing;

import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Sectors {

  private final Multimap<PointAxial, Integer> sectors = HashMultimap.create();
  private final Predicate<PointAxial> isPathable;

  public Sectors(Predicate<PointAxial> isPathable) {

    this.isPathable = isPathable;
  }

  public void calculateSectors(Stream<PointAxial> set, Consumer<PointAxial> otherMapping) {
    int currentSector = 0;

    Set<PointAxial> initialSet = set.filter(isPathable).collect(Collectors.toSet());

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

    while (!queue.isEmpty()) {
      getNeighbors(queue.poll()).forEach(neighbor -> {
        if (isPathable.test(neighbor) && initialSet.contains(neighbor)) {
          queue.add(neighbor);
          initialSet.remove(neighbor);
        }
        sectors.put(neighbor, sectorName);
      });
    }

  }

  private Stream<PointAxial> getNeighbors (PointAxial point) {
    return PointAxial.allDirections.stream()
        .map(point::add);
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

  public Stream<Hexagon<GameHex>> displaySectorsDebug() {
    Stream.Builder<Hexagon<GameHex>> builder = Stream.builder();
    sectors.asMap().forEach((k, v) -> {

    });
    return builder.build();
  };

}
