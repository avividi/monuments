package avividi.com.controller.gameitems.unit;

import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.item.Item;
import avividi.com.controller.task.Task;

import java.util.Optional;

public interface Unit extends InteractingItem {

  void setItem(Item item);
  Optional<Item> getItem();

  void assignTask(Task task);

  Task getTask ();

  boolean isFriendly ();

  void setTransform(Transform transform);
}
