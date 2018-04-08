package avividi.com.gameitems;

import avividi.com.*;
import avividi.com.hexgeometry.PointAxial;
import avividi.com.item.BoulderItem;
import avividi.com.item.DriedFireplantItem;
import avividi.com.item.Item;
import avividi.com.task.DefaultLeisureTask;
import avividi.com.task.Task;

import java.util.Optional;

public class Maldar implements Unit {
  private Task currentTask;
  private Item heldItem;

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

    if (currentTask == null) {
      currentTask = new DefaultLeisureTask();
    }
    currentTask.performStep(board, self, this);
    if (currentTask.isComplete()) this.currentTask = null;

  }
  @Override
  public String getImageName() {
    return getItem().map(this::itemToImage).orElse("slave");
  }


  private String itemToImage(Item item) {
    if (item instanceof DriedFireplantItem) return "slavefireplant" ;
    if (item instanceof BoulderItem) return "slaveboulder";

    return "slave";
  }

  @Override
  public void setItem(Item item) {
    this.heldItem = item;
  }

  @Override
  public Optional<Item> getItem() {
    return Optional.ofNullable(heldItem);
  }

  @Override
  public void assignTask(Task task) {
    this.currentTask = task;
  }

  @Override
  public Task getTask() {
    return currentTask;
  }
}
