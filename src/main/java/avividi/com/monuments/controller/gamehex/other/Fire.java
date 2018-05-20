package avividi.com.monuments.controller.gamehex.other;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.DayStage;
import avividi.com.monuments.controller.TickConstants;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.task.plan.ReviveFirePlan;
import avividi.com.monuments.controller.userinput.UserAction;
import avividi.com.monuments.hexgeometry.Grid;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.item.*;
import avividi.com.monuments.controller.task.plan.Plan;
import avividi.com.monuments.controller.task.plan.SupplyItemPlan;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Fire implements Interactor, ItemTaker {

  private static final int tick_startLife = DayStage.cycleSize / 2;
  private static final int tick_indicateLifeLow = 500;
  private static final int tick_flickerPause = 6;
  private static final int tick_waitForReTask = TickConstants.tick_waitForReTask;
  private static final int tick_deliverTime = 4;

  private int flickerPauseCount = tick_flickerPause;

  private int life = tick_startLife;
  private String image = "fire1";
  private boolean linkedToTask;
  private int waitForReTaskCount = 0;
  private boolean readyForRevival = false;
  private boolean disabled = false;

  public Fire(ObjectNode json) {
  }

  public Fire() {
    this.life = 0;
    this.image = "fire-no";
    this.readyForRevival = true;
  }

  @Override
  public void everyTickAction(Board board, PointAxial self) {
    if (life > 0) life--;
    else if (life - 1 == 0) {
      board.setShouldCalculateSectors();
      disabled = true;
    }
    image = calculateImage();
  }

  private String calculateImage () {

    if (--flickerPauseCount != 0) return image;
    flickerPauseCount = tick_flickerPause;
    if (life > tick_indicateLifeLow) return "fire1".equals(image) ? "fire2" : "fire1";
    if (life > 0) return  "firelow1".equals(image) ? "firelow2" : "firelow1";
    else return "fire-no";
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of(image);
  }

  @Override
  public boolean affectedByLight() {
    return false;
  }

  @Override
  public boolean passable() {
    return life <= 0;
  }

  public boolean burning() {
    return life > 0;
  }

  @Override
  public Optional<Plan> checkForPlan(Board board, PointAxial self) {

    waitForReTaskCount--;
    if (linkedToTask || life > tick_indicateLifeLow || disabled) return Optional.empty();

    if (life <= 0) return checkForRevivalPlan(board.getOthers(), self);

    if (waitForReTaskCount > 0) return Optional.empty();
    waitForReTaskCount = tick_waitForReTask;

    return Optional.of(new SupplyItemPlan<>(new Hexagon<>(this, self, null), DriedPlantItem.class, 5));
  };

  private Optional<Plan> checkForRevivalPlan(Grid<? extends GameHex> grid, PointAxial self) {
    if (!readyForRevival) return Optional.empty();
    if (waitForReTaskCount-- > 0) return Optional.empty();
    waitForReTaskCount = tick_waitForReTask;

    return Optional.of(new ReviveFirePlan(new Hexagon<>(this, self, null)));
  }


  @Override
  public void reserveDeliverItem(Class<? extends Item> itemType) {
    Preconditions.checkState(itemType.equals(DriedPlantItem.class));
    linkedToTask = true;

  }

  @Override
  public void unReserveDeliverItem(Class<? extends Item> itemType) {
    Preconditions.checkState(itemType.equals(DriedPlantItem.class));
    linkedToTask = false;
  }

  @Override
  public <T extends Item> boolean deliverItem(T item) {
    Preconditions.checkNotNull(item);
    this.linkedToTask = false;
    if (life <= 0) {
      return false;
    }
    life += tick_startLife;
    return true;
  }

  @Override
  public Class<? extends Item> getDeliverItemType() {
    return DriedPlantItem.class;
  }

  @Override
  public int deliveryTime() {
    return tick_deliverTime;
  }

  public void setReviving(boolean reviving) {
    this.linkedToTask = reviving;
  }

  public void revive(Board board) {
    this.life = tick_indicateLifeLow;
    this.linkedToTask = false;
    this.readyForRevival = false;
    waitForReTaskCount = 0;
    board.setShouldCalculateSectors();
  }

  @Override
  public void doUserAction(UserAction action, Board board, PointAxial self) {
    if (action == UserAction.activate) {
      if (life <= 0) {
        this.readyForRevival = true;
      }
      this.disabled = false;
    }
    else if (action == UserAction.disable) {
      this.disabled = true;
      this.readyForRevival = false;
    }

  }

  @Override
  public List<UserAction> getUserActions() {
    List<UserAction> list = new ArrayList<>();
    if (this.disabled || life <= 0 && !this.readyForRevival) list.add(UserAction.activate);
    if (this.disabled && life <= 0) list.add(UserAction.clear);
    if (!this.disabled) list.add(UserAction.disable);
    return list;
  }
}
