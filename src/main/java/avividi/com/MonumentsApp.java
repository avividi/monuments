package avividi.com;

import avividi.com.controller.GameController;
import avividi.com.gui.lwjgl.LwjglHexFrame;
import avividi.com.gui.swing.SwingHexFrame;

public class MonumentsApp {

  public static void main(String[] args) {


    new LwjglHexFrame(new GameController()).run();
//    new SwingHexFrame(new GameController());
  }


}
