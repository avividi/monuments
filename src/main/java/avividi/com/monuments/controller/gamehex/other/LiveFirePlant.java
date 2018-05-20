package avividi.com.monuments.controller.gamehex.other;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.DayStage;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.item.*;
import avividi.com.monuments.controller.item.food.FireplantLeaf;
import avividi.com.monuments.controller.item.food.FoodGiver;
import avividi.com.monuments.controller.item.food.SingleFoodGiver;
import avividi.com.monuments.controller.util.RandomUtil;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static avividi.com.monuments.controller.gamehex.other.LiveFirePlant.LifeStage.*;

public class LiveFirePlant extends SingleFoodGiver implements Interactor {

  private int lifeStage;
  private boolean passable = true;
  private boolean hasLeaves = false;
  static final int cycleAtom = (DayStage.day.end - DayStage.day.start)  / 10;
  private static int tick_pickUpTime = 20;

  enum LifeStage {
    ROOT(Integer.MIN_VALUE, 2 * cycleAtom),
    SPROUT(ROOT.end, 3 * cycleAtom),
    BLOOM(SPROUT.end, 8 * cycleAtom),
    DEAD(BLOOM.end, Integer.MAX_VALUE);

    final int start;
    final int end;
    LifeStage(int start, int end) {
      this.start = start;
      this.end = end;
    }
  }

  public LiveFirePlant(ObjectNode json) {
    lifeStage = json.get("lifeStage") == null
        ? RandomUtil.get().nextInt(12 * cycleAtom)
        : json.get("lifeStage").asInt();
    if (inStage(BLOOM)) hasLeaves = true;
    passable = inStage(ROOT) || inStage(SPROUT);
  }

  public LiveFirePlant(int lifeStage) {
    this.lifeStage = lifeStage;
  }


  @Override
  public void everyTickAction(Board board, PointAxial self) {}

  @Override
  public void every10TickAction(Board board, PointAxial self) {

    if (!board.isStage(DayStage.day)) return;
    boolean shouldGrowLeaves = inStage(SPROUT);
    lifeStage++;

    boolean newPassableFlag = false;

    if (inStage(DEAD)) {
      hasLeaves = false;
      board.getOthers().setHex(new DeadFirePlant(), self);
      return;
    }
    else if (inStage(ROOT) || inStage(SPROUT)) newPassableFlag = true;
    else if (inStage(BLOOM)){
      newPassableFlag = false;
      if (shouldGrowLeaves) this.hasLeaves = true;
    }

    if (newPassableFlag != passable) {
      passable = newPassableFlag;
      board.setShouldCalculateSectors();
    }
  }

  @Override
  public List<String> getImageNames() {
    if (inStage(SPROUT)) return ImmutableList.of("livefireplant-young");
    else if (inStage(BLOOM)) return  ImmutableList.of(hasLeaves ? "livefireplant" : "livefireplant-stripped");
    else if (inStage(DEAD)) return ImmutableList.of("fireplant");
    else return Collections.emptyList();
  }

  public boolean inStage(LifeStage stage) {
    return lifeStage >= stage.start && lifeStage < stage.end;
  }

  @Override
  protected Item getItem() {
    return new FireplantLeaf();
  }

  @Override
  public int pickUpTime() {
    return tick_pickUpTime;
  }


  @Override
  public Optional<? extends Item> pickUpItem(Board board, PointAxial self, Class<? extends Item> itemType) {
    Preconditions.checkState(itemType.equals(getItemPickupType()));
    if (hasLeaves) {
      hasLeaves = false;
      return Optional.of(getItem());
    }
    hasLeaves = false;
    return Optional.empty();
  }

  @Override
  public Class<? extends Item> getItemPickupType() {
    return FireplantLeaf.class;
  }


  @Override
  public boolean hasAvailableItem(Class<? extends Item> itemType) {
    return hasLeaves && super.hasAvailableItem(itemType);
  }

  @Override
  public boolean hasAvailableFood() {
    return hasLeaves && !reserved;
  }

  @Override
  public boolean passable() {
    return passable;
  }

  public boolean buildable() {
    return inStage(ROOT) || inStage(SPROUT);
  }
}
