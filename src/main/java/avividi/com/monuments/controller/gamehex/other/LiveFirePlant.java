package avividi.com.monuments.controller.gamehex.other;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.DayStage;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.util.RandomUtil;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;

import static avividi.com.monuments.controller.gamehex.other.LiveFirePlant.LifeStage.*;

public class LiveFirePlant implements Interactor {

  int lifeStage = 0;
  private boolean passable = true;

  enum LifeStage {
    ROOT(0, 160), SPROUT(ROOT.end, 320), BLOOM(SPROUT.end, 480), DEAD(BLOOM.end, Integer.MAX_VALUE);

    final int start;
    final int end;
    LifeStage(int start, int end) {
      this.start = start;
      this.end = end;
    }
  }

  public LiveFirePlant(ObjectNode json) {
    lifeStage = RandomUtil.get().nextInt(480);
  }

  public LiveFirePlant(int lifeStage) {
    this.lifeStage = lifeStage;
  }


  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

    if (!board.isStage(DayStage.day)) return;
    lifeStage++;

    if (lifeStage % 160 != 0) return;

    boolean newPassableFlag;

    if (inStage(DEAD)) {
      board.getOthers().setHex(new DeadFirePlant(), self);
      return;
    }
    else if (inStage(ROOT) || inStage(SPROUT)) newPassableFlag = true;
    else newPassableFlag = false;
    if (newPassableFlag != passable) {
      passable = newPassableFlag;
      board.setShouldCalculateSectors();
    }
  }

  @Override
  public List<String> getImageNames() {
    if (inStage(SPROUT)) return ImmutableList.of("livefireplant-young");
    else if (inStage(BLOOM)) return ImmutableList.of("livefireplant");
    return Collections.emptyList();
  }

  public boolean inStage(LifeStage stage) {
    return lifeStage >= stage.start && lifeStage < stage.end;
  }

//  @Override
//  protected Class<? extends Item> getItemType() {
//    return DriedPlantItem.class;
//  }
//
//  @Override
//  protected Item getItem() {
//    return new DriedPlantItem();
//  }
//
//  @Override
//  public int pickUpTime() {
//    return pickUpTime;
//  }


  @Override
  public boolean passable() {
    return passable;
  }
}
