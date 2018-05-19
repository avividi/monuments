package avividi.com.monuments.controller.gamehex.unit;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.DayStage;
import avividi.com.monuments.controller.HexItem;
import avividi.com.monuments.controller.item.food.FireplantLeaf;
import avividi.com.monuments.controller.item.food.Food;
import avividi.com.monuments.controller.task.plan.EatPlan;
import avividi.com.monuments.controller.util.RandomUtil;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.item.BoulderItem;
import avividi.com.monuments.controller.item.DriedPlantItem;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.task.plan.DefaultLeisurePlan;
import avividi.com.monuments.controller.task.plan.Plan;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

public class Maldar implements Unit {  //Striver

  private Plan currentPlan;
  private Item heldItem;
  private HexItem.Transform transform = HexItem.Transform.none;
  private boolean alive = true;

  private final static int foodLevelFull = DayStage.cycleSize / 10 * 6;
  private final static int foodLevelCritical = DayStage.cycleSize / 10 * 2;
  private int foodLevel = RandomUtil.get().nextInt(DayStage.cycleSize / 10 * 4) + foodLevelFull;
  private final static int tick_foodPlanRetry = 10;
  private int foodPlanRetryCount = 0;

  public Maldar(ObjectNode json) {
  }

  @Override
  public void everyTickAction(Board board, PointAxial self) {
    if (!alive) return;

    if (currentPlan == null || currentPlan.isComplete()) {
      currentPlan = new DefaultLeisurePlan();
    }
    currentPlan.performStep(board, new Hexagon<>(this, self, null));
    if (currentPlan.isComplete()) this.currentPlan = null;

    checkForFood(board, self);
  }

  @Override
  public void every10TickAction(Board board, PointAxial self) {
    if (foodLevel > 0) foodLevel--;
    else if (foodLevel == 0) {
      Preconditions.checkNotNull(board.getUnits().clearHex(self));
      Preconditions.checkState(board.getUnits().setHex(new DeadMaldar(null), self));
    }
  }

  private void checkForFood(Board board, PointAxial self) {
    if (currentPlan instanceof EatPlan || foodLevel > foodLevelCritical) return;
    if (currentPlan != null && foodPlanRetryCount == 0) currentPlan.abort(board, self);
  }

  @Override
  public Optional<Plan> checkForPlan(Board board, PointAxial self) {
    if (foodPlanRetryCount-- >= 0) return Optional.empty();
    if (foodLevel >= foodLevelFull || (foodLevel >= foodLevelCritical && board.isStage(DayStage.night))) return Optional.empty();
    foodPlanRetryCount = tick_foodPlanRetry;
    return Optional.of(new EatPlan(new Hexagon<>(this, self, null)));
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of(
        getItem().map(this::itemToImage).orElse("striver"))
        ;
  }


  private String itemToImage(Item item) {
    if (item instanceof DriedPlantItem) return "driedPlantItem/driedPlantItem-striver" ;
    else if (item instanceof BoulderItem) return "boulder/boulder-striver";

    else if (item instanceof FireplantLeaf) return "leafFireplant/leafFireplant-striver";
    return "striver";
  }

  @Override
  public void setItem(Item item) {
    this.heldItem = item;
  }

  @Override
  public void dropItem(Board board, PointAxial self) {
    if (heldItem != null) {
      heldItem.dropItem(board, self);
      heldItem = null;
    }
  }

  @Override
  public Optional<Item> getItem() {
    return Optional.ofNullable(heldItem);
  }

  @Override
  public void assignPlan(Plan plan) {
    this.currentPlan = plan;
  }

  @Override
  public void kill(Board board, PointAxial self) {
    alive = false;
    if (this.currentPlan == null) return;
    this.currentPlan.abort(board, self);
  }

  @Override
  public Plan getPlan() {
    return currentPlan;
  }

  @Override
  public boolean isFriendly() {
    return true;
  }

  @Override
  public void setTransform(HexItem.Transform transform) {
    this.transform = transform;
  }

  @Override
  public HexItem.Transform getTransform() {
    return transform;
  }

  @Override
  public boolean passable() {
    return false;
  }

  public void eat(Food food) {
    this.foodLevel += food.nutritionValue();
  }
}
