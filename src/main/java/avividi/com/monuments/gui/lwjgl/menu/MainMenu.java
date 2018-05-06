package avividi.com.monuments.gui.lwjgl.menu;

import avividi.com.monuments.controller.UserAction;
import avividi.com.monuments.gui.lwjgl.text.Font;

import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;

public class MainMenu implements Menu {

  private Menu firstMenu = new AbstractMenu(this) {
    Font font = new Font(20);

    @Override
    public Optional<Menu> navigate (int key, boolean secondary, boolean tertiary) {
      if (key == GLFW_KEY_B) return Optional.of(buildMenu);
      return Optional.empty();
    };

    @Override
    public void render() {
      font.renderText("(s)elect", 0, 10);
      font.renderText("(b)uild", 0, 10);

    }
  };

  private Menu buildMenu = new AbstractMenu(firstMenu) {
    Font build = new Font(20);
    Font returnF = new Font(16);

    @Override
    public Optional<UserAction> makeAction (int key, boolean secondary, boolean tertiary) {
      if (key == GLFW_KEY_P) return Optional.of(UserAction.toggleMarker);
      return Optional.empty();
    };

    @Override
    public Optional<Menu> navigate (int key, boolean secondary, boolean tertiary) {
      if (key == GLFW_KEY_P) return Optional.of(plotMenu);
      return Optional.empty();
    };

    @Override
    public void render() {
      build.renderText("(p)lot", 0, 10);
      build.renderText("(f)ireplace", 0, 10);
      build.renderText("(q)uarry", 0, 10);
      build.renderText("(w)all", 0, 10);
      returnF.renderText("(ESC) back", 0, 10);
    }
  };

  private Menu plotMenu = new AbstractMenu(buildMenu) {
    Font build = new Font(20);
    Font returnF = new Font(16);
    @Override
    public Optional<UserAction> makeAction(int key, boolean secondary, boolean tertiary) {

      if (key == GLFW_KEY_W) return Optional.of(UserAction.build);
      if (key == GLFW_KEY_ESCAPE) return Optional.of(UserAction.toggleMarker);
      return Optional.empty();
    }

    @Override
    public void render() {
      build.renderText("(w)ood", 0, 10);
      build.renderText("s(t)one", 0, 10);
      returnF.renderText("(ESC) back", 0, 10);
    }
  };

  private Menu currentSubMenu = firstMenu;

  @Override
  public Optional<UserAction> makeAction(int key, boolean secondary, boolean tertiary) {

    Optional<UserAction> subMenuAction = currentSubMenu.makeAction(key, secondary, tertiary);

    currentSubMenu.navigate(key, secondary, tertiary)
        .ifPresent(menu -> this.currentSubMenu = menu);

    if (key == GLFW_KEY_ESCAPE) {
      currentSubMenu = currentSubMenu.parentMenu() == this ? firstMenu : currentSubMenu.parentMenu();
    }


    return subMenuAction;
  }

  @Override
  public void render() {
    currentSubMenu.render();
  }

  @Override
  public Menu parentMenu() {
    return null;
  }
}
