package avividi.com.monuments.gui.lwjgl.menu;

import avividi.com.monuments.controller.userinput.UserAction;
import avividi.com.monuments.gui.lwjgl.text.Font;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.lwjgl.glfw.GLFW.*;

public class MainMenu implements Menu {

  public MainMenu(Supplier<List<UserAction>> selectActionsSupplier) {
    infoMenu = new SelectMenu(firstMenu, selectActionsSupplier);
  }

  @Override
  public Optional<UserAction> makeAction(int key, boolean secondary, boolean tertiary) {

    Optional<UserAction> subMenuAction = currentSubMenu.makeAction(key, secondary, tertiary);

    currentSubMenu.navigate(key, secondary, tertiary)
        .ifPresent(menu -> this.currentSubMenu = menu);

    if (key == GLFW_KEY_ESCAPE) {
      currentSubMenu = currentSubMenu.parentMenu() == this ? firstMenu : currentSubMenu.parentMenu();
    }
    else if (key == GLFW_KEY_S) {
      this.currentSubMenu = infoMenu;
      return Optional.of(UserAction.toggleInfoMarker);
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

  private Menu firstMenu = new AbstractMenu(this) {
    Font font = new Font(20);

    @Override
    public Optional<UserAction> makeAction (int key, boolean secondary, boolean tertiary) {
      if (key == GLFW_KEY_B) return Optional.of(UserAction.toggleBuildMarker);
      return Optional.empty();
    };


    @Override
    public Optional<Menu> navigate (int key, boolean secondary, boolean tertiary) {
      if (key == GLFW_KEY_B) return Optional.of(buildMenu);
      else if (key == GLFW_KEY_D) return Optional.of(debugMenu);
      return Optional.empty();
    };

    @Override
    public void render() {
      font.renderText("(s)elect", 0, 10);
      font.renderText("(b)uild", 0, 10);
      font.renderText("(d)ebug", 0, 10);
    }
  };

  private Menu buildMenu = new AbstractMenu(firstMenu) {
    Font build = new Font(20);
    Font returnF = new Font(16);

    @Override
    public Optional<UserAction> makeAction (int key, boolean secondary, boolean tertiary) {
      if (key == GLFW_KEY_P) return Optional.of(UserAction.toggleBuildMarker);
      else if (key == GLFW_KEY_ESCAPE) return Optional.of(UserAction.deToggleMarker);
      else if (key == GLFW_KEY_W) return Optional.of(UserAction.roughWall);
      else if (key == GLFW_KEY_F) return Optional.of(UserAction.roughFloor);
      else if (key == GLFW_KEY_B) return Optional.of(UserAction.fire);
      else if (key == GLFW_KEY_L) return Optional.of(UserAction.ladder);
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
      build.renderText("(b)onfire", 0, 10);
      build.renderText("(l)adder", 0, 10);
      build.renderText("rough (w)all", 0, 10);
      build.renderText("rough (f)loor", 0, 10);
      returnF.renderText("(ESC) back", 0, 10);
    }
  };

  private Menu plotMenu = new AbstractMenu(buildMenu) {
    Font build = new Font(20);
    Font returnF = new Font(16);
    @Override
    public Optional<UserAction> makeAction(int key, boolean secondary, boolean tertiary) {

      if (key == GLFW_KEY_W) return Optional.of(UserAction.plotWood);
      else  if (key == GLFW_KEY_T) return Optional.of(UserAction.plotStone);
      else  if (key == GLFW_KEY_F) return Optional.of(UserAction.plotLeaf);
      return Optional.empty();
    }

    @Override
    public void render() {
      build.renderText("(w)ood", 0, 10);
      build.renderText("s(t)one", 0, 10);
      build.renderText("(f)ireplant leaves", 0, 10);
      returnF.renderText("(ESC) back", 0, 10);
    }
  };

  private Menu debugMenu = new AbstractMenu(firstMenu) {
    Font build = new Font(20);
    @Override
    public Optional<UserAction> makeAction(int key, boolean secondary, boolean tertiary) {
      if (key == GLFW_KEY_E) return Optional.of(UserAction.debugSectors);
      if (key == GLFW_KEY_P) return Optional.of(UserAction.debugPaths);
      return Optional.empty();
    }
    @Override
    public void render() {
      build.renderText("toggle s(e)ctors", 0, 10);
      build.renderText("toggle(p)aths", 0, 10);
    }
  };

  private Menu infoMenu;
  private Menu currentSubMenu = firstMenu;

}
