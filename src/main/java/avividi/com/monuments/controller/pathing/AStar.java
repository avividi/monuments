package avividi.com.monuments.controller.pathing;

import avividi.com.monuments.hexgeometry.PointAxial;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class AStar implements Supplier<Optional<List<PointAxial>>> {

  private final PointAxial origin, destination;
  private final Predicate<PointAxial> isPathable;

  public static Builder builder() {
    return new Builder();
  }

  public AStar(PointAxial origin, PointAxial destination, Predicate<PointAxial> isPathable) {
    this.origin = origin;
    this.destination = destination;
    this.isPathable = isPathable;
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

    PointAxial[] directions = PointAxial.allDirectionArray;
    for (int i = 0; i < 8; ++i) {
      PointAxial p = point.add(directions[i]);
      if (isPathable.test(p) || p.equals(destination)) list.add(p);
    }
    return list;
  }


  private Stream<PointAxial> getNeighbors (PointAxial point) {

    return PointAxial.allDirections.stream()
        .map(point::add)
        .filter(p -> isPathable.test(p) || p.equals(destination));
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
    private Predicate<PointAxial> isPathable;


    public Builder withOrigin(PointAxial origin) {
      this.origin = origin;
      return this;
    }

    public Builder withDestination(PointAxial destination) {
      this.destination = destination;
      return this;
    }

    public Builder withIsPathable (Predicate<PointAxial> isPathable) {
      this.isPathable = isPathable;
      return this;
    }

    @Override
    public Optional<List<PointAxial>> get() {
      return new AStar(origin, destination, isPathable).get();
    }

  }
}
