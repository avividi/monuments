package avividi.com.monuments.gui.lwjgl.menu;

import avividi.com.monuments.controller.userinput.UserAction;

import java.util.Optional;

public interface Menu {

  default Optional<UserAction> makeAction (int key, boolean secondary, boolean tertiary) { return Optional.empty();};
  default Optional<Menu> navigate (int key, boolean secondary, boolean tertiary) { return Optional.empty();};
  void render();

  Menu parentMenu();
}
