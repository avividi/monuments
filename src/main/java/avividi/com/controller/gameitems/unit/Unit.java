package avividi.com.controller.gameitems.unit;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.Interactor;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.item.Item;
import avividi.com.controller.task.plan.Plan;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;

public interface Unit extends Interactor {


  void setItem(Item item);
  void dropItem(Board board, PointAxial self);
  Optional<Item> getItem();

  void assignTask(Plan task);
  void kill(Board board, PointAxial self);

  Plan getPlan();

  boolean isFriendly();
}
