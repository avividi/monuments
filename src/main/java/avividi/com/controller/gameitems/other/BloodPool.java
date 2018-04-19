package avividi.com.controller.gameitems.other;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.util.RandomUtil;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class BloodPool implements InteractingItem {

  private final Transform transform;

  public BloodPool() {
    transform = Transform.values()[RandomUtil.get().nextInt(Transform.values().length)];
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

  }

  @Override
  public List<String> getImageName() {
    return ImmutableList.of("bloodpool");
  }

  @Override
  public boolean passable() {
    return true;
  }

  @Override
  public Transform getTransform() {
    return transform;
  }
}
