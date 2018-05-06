package avividi.com.monuments;

import avividi.com.monuments.controller.GameController;
import avividi.com.monuments.gui.lwjgl.LwjglHexFrame;

public class MonumentsApp {

  public static void main(String[] args) {


    new LwjglHexFrame(new GameController("/maps/map4.json")).run();
//    new SwingHexFrame(new GameController());
  }


}
