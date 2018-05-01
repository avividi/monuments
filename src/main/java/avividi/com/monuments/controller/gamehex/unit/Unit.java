package avividi.com.monuments.controller.gamehex.unit;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.task.plan.Plan;

import java.util.Optional;

public interface Unit extends Interactor {


  void setItem(Item item);
  void dropItem(Board board, PointAxial self);
  Optional<Item> getItem();

  void assignPlan(Plan task);
  void kill(Board board, PointAxial self);

  Plan getPlan();

  boolean isFriendly();
}
