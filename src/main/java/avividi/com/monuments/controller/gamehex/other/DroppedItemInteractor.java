package avividi.com.monuments.controller.gamehex.other;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.SingleItemGiver;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.generic.ReflectBuilder;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.Supplier;

public class DroppedItemInteractor extends SingleItemGiver implements Interactor {

  private final String imageName;
  private final boolean passable;
  private final Class<? extends Item> itemType;
  private final Supplier<Item> itemSupplier;

  public DroppedItemInteractor(Class<? extends Item> itemType, Supplier<Item> itemSupplier, String imageName, boolean passable) {
    this.itemType = itemType;
    this.itemSupplier = itemSupplier;
    this.imageName = imageName;
    this.passable = passable;
  }

  public DroppedItemInteractor(Class<? extends Item> itemType, String imageName, boolean passable) {
    this.itemType = itemType;
    this.itemSupplier = () -> (Item) new ReflectBuilder<>(itemType).get();
    this.imageName = imageName;
    this.passable = passable;
  }


  public DroppedItemInteractor(ObjectNode json) {
    this.itemType = ReflectBuilder.getClassByName((json.get("itemType").asText())).asSubclass(Item.class);
    this.itemSupplier = () -> (Item) new ReflectBuilder<>(itemType).get();
    this.imageName = json.get("imageName").asText();
    this.passable = json.get("passable").asBoolean();
  }

  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

  }

  @Override
  public boolean passable() {
    return passable;
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of(imageName);
  }

  @Override
  protected Class<? extends Item> getItemType() {
    return itemType;
  }

  @Override
  protected Item getItem() {
    return itemSupplier.get();
  }
}
