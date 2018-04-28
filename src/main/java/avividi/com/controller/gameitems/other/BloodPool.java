package avividi.com.controller.gameitems.other;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.Interactor;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.util.RandomUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class BloodPool implements Interactor {

  private final Transform transform;

  public BloodPool(ObjectNode json) {
    transform = Transform.values()[RandomUtil.get().nextInt(Transform.values().length)];
  }

  public BloodPool() {
    transform = Transform.values()[RandomUtil.get().nextInt(Transform.values().length)];
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

  }

  @Override
  public List<String> getImageNames() {
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
