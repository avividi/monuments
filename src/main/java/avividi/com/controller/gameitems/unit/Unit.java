package avividi.com.controller.gameitems.unit;

import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.item.Item;
import avividi.com.controller.task.plan.Plan;

import java.util.Optional;

public interface Unit extends InteractingItem {

  void setItem(Item item);
  Optional<Item> getItem();

  void assignTask(Plan task);
  void kill();

  Plan getPlan();

  boolean isFriendly ();
}
