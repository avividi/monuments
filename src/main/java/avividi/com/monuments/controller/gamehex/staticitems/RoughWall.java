package avividi.com.monuments.controller.gamehex.staticitems;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.hexgeometry.PointAxial;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class RoughWall implements GameHex {

  public RoughWall(ObjectNode json) {

  }

  public RoughWall() {

  }


  @Override
  public List<String> getImageNames() {
    return ImmutableList.of("rough-wall");
  }

  @Override
  public boolean passable() {
    return false;
  }
}
