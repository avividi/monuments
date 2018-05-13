package avividi.com.monuments.gui.lwjgl.menu;

import avividi.com.monuments.controller.userinput.UserAction;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SelectMenu extends AbstractMenu {

  public SelectMenu(Menu parentMenu, Supplier<List<UserAction>> availableUserActions) {
    super(parentMenu);
  }

  @Override
  public Optional<UserAction> makeAction(int key, boolean secondary, boolean tertiary) {
    return Optional.empty();
  }

  @Override
  public Optional<Menu> navigate(int key, boolean secondary, boolean tertiary) {
    return Optional.empty();
  }

  @Override
  public void render() {

  }
}
