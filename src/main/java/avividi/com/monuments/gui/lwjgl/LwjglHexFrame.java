package avividi.com.monuments.gui.lwjgl;

import avividi.com.monuments.controller.Controller;
import avividi.com.monuments.controller.userinput.UserAction;
import avividi.com.monuments.gui.lwjgl.menu.MainMenu;
import avividi.com.monuments.gui.lwjgl.menu.Menu;
import avividi.com.monuments.gui.lwjgl.text.Font;
import avividi.com.monuments.gui.util.AssetUtil;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static avividi.com.monuments.gui.lwjgl.util.GLFWUtil.glfwInvoke;
import static java.lang.Math.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.system.MemoryUtil.*;

public final class LwjglHexFrame {
  private int w = 32;
  private int h = 32;

  private long window;
  private int  ww;
  private int  wh;
  private boolean ctrlDown;
  private boolean shiftDown;
  private int scale;

  private int targetFps = 20;
  private int gameStepsPerFrame = 1;
  private String fpsText = "fps: ?";
  private Font fpsFont;
  private Menu menu;
  private Font alertTextFont;

  private int currentDisplayLayer = 0;

  private Map<String, ImageQuad> images;
  private List<HexQuad> hexQuads;
  private Controller game;

  public LwjglHexFrame(Controller game) {
    this.game = game;
    currentDisplayLayer = this.game.setUpperDisplayLayer(currentDisplayLayer);
  }

  public void run() {
    init();
    loop();
    destroy();
  }

  private void loop() {



    images = AssetUtil.getAll().entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> new ImageQuad(e.getValue())));

    hexQuads = game.getHexagons()
        .filter(h -> h.getObj().renderAble())
        .map(h -> new HexQuad(h, images, game.getDayStage())).collect(Collectors.toList());

    glfwSetWindowRefreshCallback(window, window -> render());

    glEnable(GL_TEXTURE_2D);
    glClearColor(0 / 255f, 0 / 255f, 0 / 255f, 0f);

    long lastTime = System.nanoTime();
    BiConsumer<Long, Long> fpsCounter = fpsCounter(targetFps);

    glTranslatef(150.0f, 20.0f, 0f); //space for menu on left hand side

    menu = new MainMenu(() -> game.getSelectUserActions());
    fpsFont = new Font(12);
    alertTextFont = new Font(14);

    while (!glfwWindowShouldClose(window)) {

      syncFrameRate(targetFps, lastTime);

      long thisTime = System.nanoTime();
      fpsCounter.accept(thisTime, lastTime);
      lastTime = thisTime;

      IntStream.range(0, gameStepsPerFrame).forEach($ -> game.oneTick());
      hexQuads = game.getHexagons()
          .filter(h -> h.getObj().renderAble())
          .map(h -> new HexQuad(h, images, game.getDayStage())).collect(Collectors.toList());

      glfwPollEvents();


      render();

    }

    glDisable(GL_TEXTURE_2D);
  }

  private BiConsumer<Long, Long> fpsCounter(int everyXFrame) {
    AtomicInteger x = new AtomicInteger(0);
    DecimalFormat format = new DecimalFormat("0.00");
    return (now, last) -> {
      if (x.incrementAndGet() >= everyXFrame) {
        x.set(0);
        fpsText = String.format("fps: %s", format.format(1E9f / (now - last)));
      }
    };

  }

  private void syncFrameRate(float fps, long lastNanos) {
    long targetNanos = lastNanos + (long) (1_000_000_000.0f / fps) - 1_000_000L;
    try {
      while (System.nanoTime() < targetNanos) {
        Thread.sleep(1);
      }
    }
    catch (InterruptedException ignore) {}
  }


  private void render() {
    glClear(GL_COLOR_BUFFER_BIT);

    float scaleFactor = 1.0f + scale * 0.1f;

    glPushMatrix();
    glScalef(scaleFactor, scaleFactor, 1f);
    hexQuads.forEach(HexQuad::draw);

    fpsFont.renderText(fpsText, -140, -13);
    alertTextFont.renderText(game.alertText(), 50, -7);

    glTranslatef(-50f, 0, 0f);
    menu.render();

    glPopMatrix();
    glfwSwapBuffers(window);
  }


  private void destroy() {
    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);
    glfwTerminate();
    Objects.requireNonNull(glfwSetErrorCallback(null)).free();
  }

  private void init() {
    GLFWErrorCallback.createPrint().set();
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialize GLFW");
    }

    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

    GLFWVidMode vidmode = Objects.requireNonNull(glfwGetVideoMode(glfwGetPrimaryMonitor()));

    ww = max(1050, min(w, vidmode.width() - 160));
    wh = max(650, min(h, vidmode.height() - 120));

    this.window = glfwCreateWindow(ww, wh, "Monuments", NULL, NULL);
    if (window == NULL) {
      throw new RuntimeException("Failed to create the GLFW window");
    }

    // Center window
    glfwSetWindowPos(
        window,
        (vidmode.width() - ww) / 2,
        (vidmode.height() - wh) / 2
    );
    glfwSetWindowSizeCallback(window, this::windowSizeChanged);
    glfwSetFramebufferSizeCallback(window, LwjglHexFrame::framebufferSizeChanged);

    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
      ctrlDown = (mods & GLFW_MOD_CONTROL) != 0;
      shiftDown =  (mods & GLFW_MOD_SHIFT) != 0;
      if (action == GLFW_RELEASE) {
        return;
      }

       menu.makeAction(key, shiftDown, false)
       .ifPresent(a -> game.makeAction(a, shiftDown));

      switch (key) {
//        case GLFW_KEY_ESCAPE:
//          glfwSetWindowShouldClose(window, true);
//          break;
        case GLFW_KEY_KP_ADD:
        case GLFW_KEY_EQUAL:
          setScale(scale + 1);
          break;
        case GLFW_KEY_KP_SUBTRACT:
        case GLFW_KEY_MINUS:
          setScale(scale - 1);
          break;
        case GLFW_KEY_0:
        case GLFW_KEY_KP_0:
          if (ctrlDown) {
            setScale(0);
          }
          break;
        case GLFW_KEY_SPACE:
          togglePause();
          System.out.println("gameStepsPerFrame = " + gameStepsPerFrame);
          break;
        case GLFW_KEY_1:
          if (gameStepsPerFrame > 0) gameStepsPerFrame--;
          System.out.println("gameStepsPerFrame = " + gameStepsPerFrame);
          break;
        case GLFW_KEY_2:
          if (gameStepsPerFrame < 500) gameStepsPerFrame++;
          System.out.println("gameStepsPerFrame = " + gameStepsPerFrame);
          break;
//        case GLFW_KEY_RIGHT:
//          game.makeAction(UserAction.moveE, shiftDown);
//          break;
//        case GLFW_KEY_LEFT:
//          game.makeAction(UserAction.moveW, shiftDown);
//          break;
        case GLFW_KEY_UP:
          currentDisplayLayer = game.setUpperDisplayLayer(++currentDisplayLayer);
          break;
        case GLFW_KEY_DOWN:
          currentDisplayLayer = game.setUpperDisplayLayer(--currentDisplayLayer);
          break;
        case GLFW_KEY_U:
          game.makeAction(UserAction.moveNW, shiftDown);
          break;
        case GLFW_KEY_I:
          game.makeAction(UserAction.moveNE, shiftDown);
          break;
        case GLFW_KEY_K:
          game.makeAction(UserAction.moveE, shiftDown);
          break;
        case GLFW_KEY_H:
          game.makeAction(UserAction.moveW, shiftDown);
          break;
        case GLFW_KEY_N:
          game.makeAction(UserAction.moveSW, shiftDown);
          break;
        case GLFW_KEY_M:
          game.makeAction(UserAction.moveSE, shiftDown);
          break;
        case GLFW_KEY_PERIOD:
          game.oneTick();
          glfwPollEvents();
          render();
          break;
      }
    });

    glfwSetScrollCallback(window, (window, xoffset, yoffset) -> {
      if (ctrlDown) {
        setScale(scale + (int)yoffset);
      }
    });

    // Create context
    glfwMakeContextCurrent(window);
    GL.createCapabilities();

    glfwSwapInterval(1);
    glfwShowWindow(window);

    glfwInvoke(window, this::windowSizeChanged, LwjglHexFrame::framebufferSizeChanged);

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
  }

  private void windowSizeChanged(long window, int width, int height) {
    this.ww = width;
    this.wh = height;

    glMatrixMode(GL_PROJECTION);
    glLoadIdentity();
    glOrtho(0.0, width, height, 0.0, -1.0, 1.0);
    glMatrixMode(GL_MODELVIEW);
  }

  private static void framebufferSizeChanged(long window, int width, int height) {
    glViewport(0, 0, width, height);
  }

  private void setScale(int scale) {
    this.scale = max(-9, scale);
    System.out.println("scale = " + (1.0f + this.scale * 0.1f));
  }

  private void togglePause () {
    gameStepsPerFrame = gameStepsPerFrame == 0 ? 1 : 0;
  }

}