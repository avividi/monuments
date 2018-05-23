package avividi.com.monuments;

import avividi.com.monuments.controller.GameController;
import avividi.com.monuments.gui.lwjgl.LwjglHexFrame;

public class MonumentsApp {

  public static void main(String[] args) {

    GameController controller = new GameController("/maps/map1.json");
    controller.setDisableSpawns(true);

    new LwjglHexFrame(controller).run();
//    new SwingHexFrame(new GameController());
  }


}
