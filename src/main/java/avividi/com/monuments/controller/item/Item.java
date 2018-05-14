package avividi.com.monuments.controller.item;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.hexgeometry.PointAxial;

public interface Item {

  void dropItem(Board board, PointAxial position);

  String getItemImageNameSpace();
}
