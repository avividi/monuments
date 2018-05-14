package avividi.com.monuments.controller.gamehex.staticitems;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class RoughFloor implements GameHex {

  public RoughFloor(ObjectNode json) {

  }

  public RoughFloor() {

  }

  @Override
  public List<String> getImageNames() {
    return ImmutableList.of("rough-floor");
  }

  @Override
  public boolean passable() {
    return true;
  }

  @Override
  public String getId() {
    return "floor";
  }
}
