package avividi.com.monuments.controller.gamehex.other;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.DayStage;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.util.RandomUtil;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.item.DriedPlantItem;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.SingleItemGiver;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Optional;

import static avividi.com.monuments.controller.gamehex.other.LiveFirePlant.cycleAtom;


public class DeadFirePlant extends SingleItemGiver implements Interactor {

  private int stage = 0;
  boolean reviveable = false;
  private int tick_pickUp = 8;

  public DeadFirePlant(ObjectNode json) {
    reviveable = false;

  }


  public DeadFirePlant() {
    reviveable = true;
    stage = 0;
  }

  @Override
  public Optional<? extends Item> pickUpItem(Board board, PointAxial self, Class<? extends Item> itemType) {
    Preconditions.checkState(itemType.equals(getItemType()));
    if (alive) {
      Interactor thisInteractor = board.getOthers().clearHex(self);
      Preconditions.checkNotNull(thisInteractor);
      board.setShouldCalculateSectors();
      if (reviveable) board.getOthers().setHex(new LiveFirePlant(- (cycleAtom * 5) + stage), self);
      alive = false;
      return Optional.of(getItem());
    }
    return Optional.empty();
  }

  @Override
  public void everyTickAction(Board board, PointAxial self) {}
  @Override

  public void every10TickAction(Board board, PointAxial self) {
    if (!reviveable || !board.isStage(DayStage.day)) return;


    if (stage++ > cycleAtom * 5) {
      board.getOthers().setHex(new LiveFirePlant(0), self);
    }
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of("fireplant");
  }

  @Override
  protected Class<? extends Item> getItemType() {
    return DriedPlantItem.class;
  }

  @Override
  protected Item getItem() {
    return new DriedPlantItem();
  }

  @Override
  public int pickUpTime() {
    return tick_pickUp;
  }


  @Override
  public boolean passable() {
    return false;
  }
}
