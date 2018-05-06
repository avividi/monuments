package avividi.com.monuments.gui.lwjgl.menu;

import avividi.com.monuments.controller.UserAction;

import java.util.Optional;

public abstract class AbstractMenu implements Menu {

  private Menu parentMenu;

  public AbstractMenu(Menu parentMenu) {
    this.parentMenu = parentMenu;
  }
  abstract public void render();


  public Menu parentMenu() {
    return this.parentMenu;
  }
}
