package avividi.com.controller.gameitems.unit;

import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.item.Item;
import avividi.com.controller.task.plan.Plan;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Optional;

public abstract class Unit extends InteractingItem {

  public Unit(ObjectNode json) {
    super(json);
  }

  abstract public void setItem(Item item);
  abstract public Optional<Item> getItem();

  abstract public void assignTask(Plan task);
  abstract public void kill();

  abstract public Plan getPlan();

  public abstract boolean isFriendly();
}
