package avividi.com.monuments;

import avividi.com.monuments.controller.GameController;
import avividi.com.monuments.gui.lwjgl.LwjglHexFrame;

public class MonumentsApp {

  public static void main(String[] args) {

    GameController controller = new GameController("/maps/map2.json");
    controller.setDisableSpawns(false);

    new LwjglHexFrame(controller).run();
//    new SwingHexFrame(new GameController());
  }


}
