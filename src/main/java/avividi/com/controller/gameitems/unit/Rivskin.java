package avividi.com.controller.gameitems.unit;

import avividi.com.controller.Board;
import avividi.com.controller.DayStage;
import avividi.com.controller.HexItem;
import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.item.Item;
import avividi.com.controller.pathing.AStar;
import avividi.com.controller.task.atomic.*;
import avividi.com.controller.task.plan.Plan;
import avividi.com.controller.util.RandomUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static avividi.com.controller.Ticks.TUnits.TRivskin.*;

public class Rivskin implements Unit {

  private HexItem.Transform transform = HexItem.Transform.none;

  private boolean hasEaten = false;
  private int waitForRePlanCount;
  private boolean leaveMode = false;
  List<Task> plan = new ArrayList<>();

  public Rivskin(ObjectNode json) {
  }

  public Rivskin() {
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

    if (--waitForRePlanCount <= 0 || plan.isEmpty()) {
      replan(board, self);
    }

    Task next = plan.get(0);
    Hexagon<Unit> unit = new Hexagon<>(this, self, null);

    if (next.perform(board, unit) && next.isComplete()) {
      if (next instanceof KillTask) hasEaten = true;
      plan.remove(0);
    }
    else if (next.shouldAbort()) {
      plan.clear();
    }

  }

  private void replan(Board board, PointAxial self) {
    waitForRePlanCount = waitForRePlan;

    if (leaveMode || board.getDayStage() == DayStage.dawn || board.getDayStage() == DayStage.day) {
      if (!leaveMode) plan.clear();
      leaveMode = true;
      if (plan.isEmpty()) {
        planNearestExit(board, self);
        return;
      }
    }
    planKill(board, self);


  }

  private void planNearestExit (Board board, PointAxial self) {
    Optional<List<PointAxial>> nearestExit =  findNearestExit(board, self);
    if (nearestExit.isPresent()) {

      plan.addAll(SimpleMoveTask.fromPoints(nearestExit.get(), escapeSpeed));
      plan.add(new SelfDestroyTask());
    }
    else plan.add(new RandomMoveTask());
  }

  private Optional<List<PointAxial>> findNearestExit(Board board, PointAxial self) {
    return board.getSpawnEdges().stream()
        .filter(p -> board.isInSameSector(self, p))
        .min(PointAxial.comparingPoint(self))
        .flatMap(edge -> AStar.builder()
            .withDestination(edge)
            .withOrigin(self)
            .withIsPathable(point -> isPathable(board, point))
            .get());
  }

  private void planKill(Board board, PointAxial self) {
    if (!hasEaten) {
      Optional<List<PointAxial>> pathToPrey =  findPrey(board, self);
      if (pathToPrey.isPresent()) {

        plan.clear();
        plan.addAll(SimpleMoveTask.fromPoints(pathToPrey.get(), chaseSpeed));
        Task lastly = plan.remove(plan.size() -1);
        plan.add(new KillTask(pathToPrey.get().get(pathToPrey.get().size() - 1), Maldar.class));
        plan.add(lastly);
        return;
      }
    }
    planRandomExit(board, self);
  }

  private Optional<List<PointAxial>> findPrey(Board board, PointAxial self) {
    return board.getUnits(Maldar.class).stream()
        .filter(p -> board.isInSameSector(self, p.getPosAxial()))
        .filter(maldar -> board.getBurningFires().stream()
            .noneMatch(fire -> PointAxial.distance(fire.getPosAxial(), maldar.getPosAxial()) < 4))
        .min(Hexagon.compareDistance(self))
        .flatMap(prey -> AStar.builder()
            .withOrigin(self)
            .withDestination(prey.getPosAxial())
            .withIsPathable(point -> isPathable(board, point))
            .get());

  }

  private void  planRandomExit (Board board, PointAxial self) {
    if (!plan.isEmpty()) return;
    Optional<List<PointAxial>> findRandomExit =  findRandomExit(board, self);
    if (findRandomExit.isPresent()) {
      plan.addAll(SimpleMoveTask.fromPoints(findRandomExit.get(), normalSpeed));
      plan.add(new SelfDestroyTask());
    }
    else plan.add(new RandomMoveTask(normalSpeed));
  }

  private Optional<List<PointAxial>> findRandomExit(Board board, PointAxial self) {

    PointAxial destionation = board.getSpawnEdges()
        .stream().filter(p -> board.isInSameSector(self, p)).collect(Collectors.toList())
        .get(RandomUtil.get().nextInt(board.getSpawnEdges().size()));

    return AStar.builder()
        .withDestination(destionation)
        .withOrigin(self)
        .withIsPathable(point -> isPathable(board, point))
        .get();
  }

  private boolean isPathable (Board board, PointAxial point) {
    return board.hexIsFree(point) && board.getBurningFires().stream()
        .noneMatch(fire -> PointAxial.distance(fire.getPosAxial(), point) < 5);
  }

  @Override
  public boolean isFriendly() {
    return false;
  }

  @Override
  public void setTransform(HexItem.Transform transform) {
    this.transform = transform;
  }

  @Override
  public Transform getTransform() {
    return transform;
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of(hasEaten ? "rivskinmett" : "rivskin");
  }

  @Override
  public boolean affectedByLight() {
    return true;
  }

  @Override
  public void setItem(Item item) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void dropItem(Board board, PointAxial self) {

  }

  @Override
  public Optional<Item> getItem() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void assignTask(Plan task) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void kill(Board board, PointAxial self) {

  }

  @Override
  public Plan getPlan() {
    return null;
  }

  @Override
  public boolean passable() {
    return false;
  }
}
