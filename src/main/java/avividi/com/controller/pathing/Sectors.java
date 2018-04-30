package avividi.com.controller.pathing;

import avividi.com.controller.hexgeometry.PointAxial;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Sectors {

  private final Map<PointAxial, Integer> sectors = new HashMap<>();
  private final Predicate<PointAxial> isPathable;

  public Sectors(Predicate<PointAxial> isPathable) {

    this.isPathable = isPathable;
  }

  public void calculateSectors(Set<PointAxial> set) {
    int currentSector = 0;

    while (!set.isEmpty()) {
      currentSector++;
      PointAxial fromSet = set.stream().findAny().orElseThrow(IllegalStateException::new);
      List<PointAxial> sectorList = new ArrayList<>();
      sectorList.add(fromSet);

      while (!sectorList.isEmpty()) {

        PointAxial next = sectorList.remove(0);
        set.remove(next);
        if (isPathable.test(fromSet)) {

          sectors.put(next, currentSector);
          getNeighbors(next).forEach(sectorList::add);
        }
      }
    }
  }

  private boolean isInSameSector(PointAxial p1, PointAxial p2) {
    return Objects.equals(sectors.get(p1), sectors.get(p2));
  }

  private Stream<PointAxial> getNeighbors (PointAxial point) {
    return PointAxial.allDirections.stream()
        .map(point::add)
        .filter(p -> !sectors.containsKey(p))
        .filter(isPathable);
  }
}
