package avividi.com.monuments.gui.lwjgl.menu;

import avividi.com.monuments.controller.userinput.UserAction;
import avividi.com.monuments.gui.lwjgl.text.Font;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;

public class SelectMenu extends AbstractMenu {

 private Font text = new Font(16);
 private Supplier<List<UserAction>> availableUserActions;

  public SelectMenu(Menu parentMenu, Supplier<List<UserAction>> availableUserActions) {
    super(parentMenu);
    this.availableUserActions = availableUserActions;
  }

  @Override
  public Optional<UserAction> makeAction(int key, boolean secondary, boolean tertiary) {
    if (key == GLFW_KEY_ESCAPE) return Optional.of(UserAction.deToggleMarker);
    else if (key == GLFW_KEY_C) return Optional.of(UserAction.cancel);
    else if (key == GLFW_KEY_A) return Optional.of(UserAction.activate);
    else if (key == GLFW_KEY_D) return Optional.of(UserAction.disable);
    return Optional.empty();
  }


  @Override
  public Optional<Menu> navigate(int key, boolean secondary, boolean tertiary) {
    return Optional.empty();
  }
  public void render() {

    List<UserAction> userActions = availableUserActions.get();
    if (userActions.isEmpty()) {
      text.renderText("Nothing of interest", 0, 10);
    }
    else {
      userActions.forEach(a -> text.renderText(userActionToDisplay.get(a), 0, 10));

    }
    text.renderText("(ESC) back", 0, 10);
  }

  Map<UserAction, String> userActionToDisplay = ImmutableMap.<UserAction, String>builder()
      .put(UserAction.cancel, "(C)ancel")
      .put(UserAction.clear, "(C)clear")
      .put(UserAction.activate, "(A)ctivate")
      .put(UserAction.disable, "(D)isable")
      .build();
}
