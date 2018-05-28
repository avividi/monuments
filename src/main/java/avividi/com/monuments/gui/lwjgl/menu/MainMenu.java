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
    else if (key == GLFW_KEY_B) {
      this.currentSubMenu = buildMenu;
      return  Optional.of(UserAction.toggleBuildMarker);
    };

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
      if (key == GLFW_KEY_P || key == GLFW_KEY_T || key == GLFW_KEY_O) return Optional.of(UserAction.toggleBuildMarker);
      else if (key == GLFW_KEY_ESCAPE) return Optional.of(UserAction.deToggleMarker);
      else if (key == GLFW_KEY_F) return Optional.of(UserAction.fire);
      return Optional.empty();
    };

    @Override
    public Optional<Menu> navigate (int key, boolean secondary, boolean tertiary) {
      if (key == GLFW_KEY_P) return Optional.of(plotMenu);
      else if (key == GLFW_KEY_T) return Optional.of(construction);
      else if (key == GLFW_KEY_O) return Optional.of(scaffolding);
      return Optional.empty();
    };

    @Override
    public void render() {
      build.renderText("(p)lot", 0, 10);
      build.renderText("(f)ireplace", 0, 10);
      build.renderText("s(t)one structures", 0, 10);
      build.renderText("w(o)od structures", 0, 10);
      returnF.renderText("(ESC) back", 0, 10);
    }
  };

  private Menu plotMenu = new AbstractMenu(buildMenu) {
    Font build = new Font(20);
    Font returnF = new Font(16);
    @Override
    public Optional<UserAction> makeAction(int key, boolean secondary, boolean tertiary) {

      if (key == GLFW_KEY_O) return Optional.of(UserAction.plotWood);
      else  if (key == GLFW_KEY_T) return Optional.of(UserAction.plotStone);
      else  if (key == GLFW_KEY_L) return Optional.of(UserAction.plotLeaf);
      return Optional.empty();
    }

    @Override
    public void render() {
      build.renderText("w(o)od", 0, 10);
      build.renderText("s(t)one", 0, 10);
      build.renderText("(l)eaves", 0, 10);
      returnF.renderText("(ESC) back", 0, 10);
    }
  };

  private Menu construction = new AbstractMenu(buildMenu) {
    Font build = new Font(20);
    Font returnF = new Font(16);
    @Override
    public Optional<UserAction> makeAction(int key, boolean secondary, boolean tertiary) {
      if (key == GLFW_KEY_L) return Optional.of(UserAction.roughWall);
      else if (key == GLFW_KEY_F) return Optional.of(UserAction.roughFloor);
      return Optional.empty();
    }

    @Override
    public void render() {

      build.renderText("wa(l)l", 0, 10);
      build.renderText("(f)loor", 0, 10);
      returnF.renderText("(ESC) back", 0, 10);
    }
  };

  private Menu scaffolding = new AbstractMenu(buildMenu) {
    Font build = new Font(20);
    Font returnF = new Font(16);
    @Override
    public Optional<UserAction> makeAction(int key, boolean secondary, boolean tertiary) {
      if (key == GLFW_KEY_L) return Optional.of(UserAction.scaffoldingLadder);
      else  if (key == GLFW_KEY_C) return Optional.of(UserAction.scaffoldingSupport);
      else  if (key == GLFW_KEY_U) return Optional.of(UserAction.quarry);
      return Optional.empty();
    }

    @Override
    public void render() {


      build.renderText("(l)adder", 0, 10);
      build.renderText("s(c)affolding", 0, 10);
      build.renderText("q(u)arry", 0, 10);
      returnF.renderText("(ESC) back", 0, 10);
    }
  };

  private Menu debugMenu = new AbstractMenu(firstMenu) {
    Font build = new Font(20);
    @Override
    public Optional<UserAction> makeAction(int key, boolean secondary, boolean tertiary) {
      if (key == GLFW_KEY_T) return Optional.of(UserAction.debugSectors);
      if (key == GLFW_KEY_P) return Optional.of(UserAction.debugPaths);
      return Optional.empty();
    }
    @Override
    public void render() {
      build.renderText("(t)oggle sectors", 0, 10);
      build.renderText("toggle(p)aths", 0, 10);
    }
  };

  private Menu infoMenu;
  private Menu currentSubMenu = firstMenu;

}
