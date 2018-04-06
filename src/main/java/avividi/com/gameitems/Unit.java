package avividi.com.gameitems;

import avividi.com.item.Item;
import avividi.com.task.Task;

import java.util.Optional;

public interface Unit extends InteractingItem {

  void setItem(Item item);
  Optional<Item> getItem();

  void assignTask(Task task);

  Task getTask ();
}
