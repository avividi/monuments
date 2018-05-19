package avividi.com.monuments.controller.item.food;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.TickConstants;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.gamehex.other.GiveOnlyPlot;
import avividi.com.monuments.controller.gamehex.other.Plot;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.ItemGiver;
import avividi.com.monuments.controller.item.ItemTaker;
import avividi.com.monuments.controller.task.plan.Plan;
import avividi.com.monuments.controller.task.plan.SupplyItemPlan;
import avividi.com.monuments.controller.userinput.UserAction;
import avividi.com.monuments.generic.ReflectBuilder;
import avividi.com.monuments.hexgeometry.Grid;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FoodPlot extends Plot implements  FoodGiver {


  public FoodPlot(Class<? extends Food> itemType) {
    super(itemType);
  }

  public FoodPlot(ObjectNode json) {
    super(json);
  }

  @Override
  public boolean hasAvailableFood() {
    return hasAvailableItem(this.itemType);
  }
}
