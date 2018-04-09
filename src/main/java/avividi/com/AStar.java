package avividi.com;

import avividi.com.hexgeometry.PointAxial;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AStar implements Supplier<Optional<List<PointAxial>>> {

  private final Board board;
  private PointAxial origin, destination;

  public AStar (Board board) {
    this.board = board;
  }

  public AStar withOrigin(PointAxial origin) {
    this.origin = origin;
    return this;
  }

  public AStar withDestination(PointAxial destination) {
    this.destination = destination;
    return this;
  }

  @Override
  public Optional<List<PointAxial>> get() {

    PriorityQueue<Node> frontier = new PriorityQueue<>(100, Node.comparator());
    frontier.add(new Node(origin, 0));

    Map<PointAxial, PointAxial> cameFrom = new HashMap<>();
    cameFrom.put(origin, null);

    Map<PointAxial, Integer> costs = new HashMap<>();
    costs.put(origin, 0);

    while (!frontier.isEmpty()) {
      Node current = frontier.poll();

      if (current.point.equals(destination)){
        break;
      }
      for (PointAxial next : getNeighbors(current.point)) {
        int newCost = costs.get(current.point) + 1;
        if (!costs.containsKey(next) || newCost < costs.get(next)) {
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

  private List<PointAxial> getNeighbors (PointAxial point) {
    return PointAxial.allDirections.stream()
        .map(point::add)
        .filter(p -> board.hexIsPathAble(p) || p.equals(destination))
        .collect(Collectors.toList());
  }

  private static class Node {
    final PointAxial point;
    final int score;

    public Node(PointAxial point, int score) {
      this.point = point;
      this.score = score;
    }

    public PointAxial getPoint() {
      return point;
    }

    public int getScore() {
      return score;
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

    public static Comparator<Node> comparator() {
      return Comparator.comparing(n -> n.score);
    }

  }
}
