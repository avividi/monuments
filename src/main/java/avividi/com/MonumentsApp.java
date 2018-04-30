package avividi.com;

import avividi.com.controller.GameController;
import avividi.com.gui.lwjgl.LwjglHexFrame;

public class MonumentsApp {

  public static void main(String[] args) {


    new LwjglHexFrame(new GameController("/maps/map2.json")).run();
//    new SwingHexFrame(new GameController());
  }


}
