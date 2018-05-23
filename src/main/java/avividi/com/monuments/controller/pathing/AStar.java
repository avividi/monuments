package avividi.com.monuments.controller.pathing;

import avividi.com.monuments.hexgeometry.AxialDirection;
import avividi.com.monuments.hexgeometry.PointAxial;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class AStar implements Supplier<Optional<List<PointAxial>>> {

  private final PointAxial origin, destination;
  private final BiPredicate<PointAxial, AxialDirection> isPathable;
  private final BiPredicate<PointAxial, AxialDirection> isReachable;
  private final boolean cardinalDirectionsOnly;

  public static Builder builder() {
    return new Builder();
  }

  public AStar(PointAxial origin,
               PointAxial destination,
               BiPredicate<PointAxial, AxialDirection>  isPathable,
               BiPredicate<PointAxial, AxialDirection> isReachable,
               boolean cardinalDirectionsOnly) {
    this.origin = origin;
    this.destination = destination;
    this.isPathable = isPathable;
    this.isReachable = isReachable;
    this.cardinalDirectionsOnly = cardinalDirectionsOnly;
  }

  @Override
  public Optional<List<PointAxial>> get() {

    PriorityQueue<Node> frontier = new PriorityQueue<>(256, Node.comparator());
    frontier.add(new Node(origin, 0));

    Map<PointAxial, PointAxial> cameFrom = new HashMap<>();
    cameFrom.put(origin, null);

    Map<PointAxial, Integer> costs = new HashMap<>();
    costs.put(origin, 0);

    while (!frontier.isEmpty()) {
      Node current = frontier.poll();
      if (current.point.equals(destination)) break;

      for(PointAxial next : getNeighbors2(current.point)) {
        int newCost = costs.get(current.point) + 1;
        if (costs.containsKey(next) && newCost >= costs.get(next));
        else {
          costs.put(next, newCost);
          int score = newCost + heuristic(next);
          frontier.add(new Node(next, score));
          cameFrom.put(next, current.point);
        }
      }
    }

    if (cameFrom.get(destination) == null) return Optional.empty();
    return Optional.of(pathMapToPathList(cameFrom));
  }

  private int heuristic(PointAxial point) {
    return PointAxial.distance(origin, point);
  }

  private List<PointAxial> pathMapToPathList (Map<PointAxial, PointAxial> pathMap) {
    List<PointAxial> path = new ArrayList<>();
    PointAxial backTrace = destination;
    path.add(backTrace);
    while (!backTrace.equals(origin)) {
      backTrace = pathMap.get(backTrace);
      path.add(0, backTrace);
    }
    return path;
  }

  private List<PointAxial> getNeighbors2 (PointAxial point) {

    List<PointAxial> list = new ArrayList<>();
//    {
//      PointAxial p = point.add(PointAxial.NW);
//      if (isPathable.test(p) || p.equals(destination)) list.add(p);
//    }
//    {
//      PointAxial p = point.add(PointAxial.NE);
//      if (isPathable.test(p) || p.equals(destination)) list.add(p);
//    }
//    {
//      PointAxial p = point.add(PointAxial.E);
//      if (isPathable.test(p) || p.equals(destination)) list.add(p);
//    }
//    {
//      PointAxial p = point.add(PointAxial.SE);
//      if (isPathable.test(p) || p.equals(destination)) list.add(p);
//    }
//    {
//      PointAxial p = point.add(PointAxial.SW);
//      if (isPathable.test(p) || p.equals(destination)) list.add(p);
//    }
//    {
//      PointAxial p = point.add(PointAxial.W);
//      if (isPathable.test(p) || p.equals(destination)) list.add(p);
//    }
//    if (!cardinalDirectionsOnly) {
//      {
//        PointAxial p = point.add(PointAxial.UP);
//        if (isPathable.test(p) || p.equals(destination)) list.add(p);
//      }
//      {
//        PointAxial p = point.add(PointAxial.DOWN);
//        if (isPathable.test(p) || p.equals(destination)) list.add(p);
//      }
//    }
//    return list;

    if (cardinalDirectionsOnly) {
      AxialDirection[] directions = PointAxial.cardinalDirections;
      for (int i = 0; i < 6; ++i) {
        AxialDirection dir = directions[i];
        PointAxial p = point.add(dir.dir);
        if (isPathable.test(point, dir) || p.equals(destination)) list.add(p);
      }
      return list;
    }
    AxialDirection[] directions = PointAxial.allDirections;
    for (int i = 0; i < 8; ++i) {
      AxialDirection dir = directions[i];
      PointAxial p = point.add(dir.dir);
      if (isPathable.test(point, dir) || p.equals(destination) && isReachable.test(destination, dir)) list.add(p);
    }
    return list;
  }

  private static class Node {
    final PointAxial point;
    final int score;

    public Node(PointAxial point, int score) {
      this.point = point;
      this.score = score;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Node node = (Node) o;
      return Objects.equals(point, node.point);
    }

    @Override
    public int hashCode() {

      return Objects.hash(point);
    }

    static Comparator<Node> comparator() {
      return Comparator.comparing(n -> n.score);
    }

  }

  public static class Builder implements Supplier<Optional<List<PointAxial>>> {

    private PointAxial origin, destination;
    private BiPredicate<PointAxial, AxialDirection> isPathable;
    private BiPredicate<PointAxial, AxialDirection> isReachable;
    private boolean cardinalDirectionsOnly;


    public Builder withOrigin(PointAxial origin) {
      this.origin = origin;
      return this;
    }

    public Builder withDestination(PointAxial destination) {
      this.destination = destination;
      return this;
    }

    public Builder withIsPathable(BiPredicate<PointAxial, AxialDirection> isPathable) {
      this.isPathable = isPathable;
      return this;
    }

    public Builder withIsReachable (BiPredicate<PointAxial, AxialDirection> isReachable) {
      this.isReachable = isReachable;
      return this;
    }

    public Builder withCardinalDirectionsOnly() {
      this.cardinalDirectionsOnly = true;
      return this;
    }

    @Override
    public Optional<List<PointAxial>> get() {
      return new AStar(origin, destination, isPathable, isReachable, cardinalDirectionsOnly).get();
    }

  }
}
