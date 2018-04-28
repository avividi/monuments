package avividi.com.controller.item;

import avividi.com.controller.Board;
import avividi.com.controller.gameitems.Interactor;
import avividi.com.controller.hexgeometry.PointAxial;

public interface Item {

  void dropItem(Board board, PointAxial position);
}
