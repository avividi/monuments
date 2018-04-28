package avividi.com.controller.gameitems.unit;

import avividi.com.controller.gameitems.Interactor;
import avividi.com.controller.item.Item;
import avividi.com.controller.task.plan.Plan;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;

public interface Unit extends Interactor {


  void setItem(Item item);
  Optional<Item> getItem();

  void assignTask(Plan task);
  void kill();

  Plan getPlan();

  boolean isFriendly();
}
