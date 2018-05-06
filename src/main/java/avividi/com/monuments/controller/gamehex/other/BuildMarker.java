package avividi.com.monuments.controller.gamehex.other;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.ItemTaker;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

public class BuildMarker implements ItemTaker, Interactor {
  @Override
  public void endOfTurnAction(Board board, PointAxial self) {

  }

  @Override
  public boolean passable() {
    return true;
  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of("marker/marker-blue");
  }

  @Override
  public void reserveDeliverItem(Class<? extends Item> itemType) {

  }

  @Override
  public void unReserveDeliverItem(Class<? extends Item> itemType) {

  }

  @Override
  public <T extends Item> boolean deliverItem(T item) {
    return false;
  }

  @Override
  public Set<Class<? extends Item>> getSupportedDeliverItems() {
    return ImmutableSet.of();
  }

  @Override
  public int deliveryTime() {
    return 0;
  }
}
