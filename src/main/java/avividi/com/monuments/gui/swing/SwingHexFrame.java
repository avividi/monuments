//package avividi.com.gui.swing;
//
//import avividi.com.monuments.controller.Controller;
//import avividi.com.monuments.controller.ControllerListener;
//import avividi.com.monuments.controller.ClockStage;
//import avividi.com.monuments.controller.HexItem;
//import avividi.com.monuments.controller.hexgeometry.Grid;
//import avividi.com.monuments.controller.hexgeometry.Hexagon;
//import avividi.com.monuments.controller.hexgeometry.Point2;
//import avividi.com.monuments.controller.hexgeometry.Point2d;
//import org.imgscalr.Scalr;
//
//import javax.imageio.ImageIO;
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.awt.geom.AffineTransform;
//import java.awt.image.AffineTransformOp;
//import java.awt.image.BufferedImage;
//import java.awt.image.RescaleOp;
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//public class SwingHexFrame extends JFrame {
//
//  private int startingSpeed = 400;
//  private double scale = 2;
//  final private int originalImageSize = 32;
//  private int imgSize = (int) (originalImageSize * scale);
//  Map<String, BufferedImage> loaded = new HashMap<>();
//
//
//  public SwingHexFrame(Controller game) {
//
//
//    setSize(700, 620);
//
////    GraphicsDevice gd =
////        GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
////    if (gd.isFullScreenSupported()) {
////      setUndecorated(true);
////      gd.setFullScreenWindow(this);
////    }
//
//
//    if ("Mac OS X".equals(System.getProperty("os.name"))) {
//      try {
//        Class.forName("com.apple.eawt.FullScreenUtilities")
//            .getMethod ("setWindowCanFullScreen", Window.class, boolean.class)
//            .invoke(null,this, true);
//      } catch (IllegalAccessException |
//          ClassNotFoundException |
//          NoSuchMethodException |
//          InvocationTargetException e) {
//        throw new IllegalStateException(e);
//      }
//    }
//
//
//    setTitle("HexBattles");
//
//    View view = new View(game);
//    add(view);
//
//
//    Loop loop = new Loop(game);
//
//    addKeyListener(new ExploreKeyboardListener2(game, loop));
//
//    setVisible(true);
//  }
//
//  class View extends JPanel implements MouseListener, ControllerListener {
//
//    private int padding = 0;
//    final private String baseImageUrl = "/graphics/";
//    final private JLabel storyText;
//    private final Controller game;
//
//    public View(Controller game) {
//      addMouseListener(this);
//      this.game = game;
//      this.game.addListener(this);
//
//      setBackground(Color.black);
//
//      setLayout(null);
//
//      storyText = new JLabel();
//      storyText.setFont(new Font("Georgia", Font.PLAIN, 35));
//      storyText.setForeground(Color.lightGray);
//      storyText.setLocation(650, 500);
//      storyText.setSize(50, 50);
//      add(storyText);
//    }
//
//    public void paint(Graphics g) {
//      super.paint(g);
//
//      Graphics2D g2 = (Graphics2D) g;
//
////      padding = game.getGround() instanceof BattleGround ? 2 : 0;
//
//
//      game.getHexagons().forEach(h -> drawImage(g2, h));
//
//
////      storyText.setText("<html>" +  game.getGround().getStoryText() + "</html>");
//
////      storyText.setText(String.valueOf(game.getActionsLeft()));
//      setVisible(true);
//    }
//
//
//
//    @Override
//    public void mouseClicked(MouseEvent e) {
//      Point2d point2d = game.getPosition2d(imgSize, e.getX(), e.getY(), padding);
//       makeClick(point2d);
//    }
//
//    private void makeClick (Point2d point2d) {
//      game.giveInput(point2d);
//    }
//
//    @Override
//    public void mousePressed(MouseEvent e) {
//
//    }
//
//    @Override
//    public void mouseReleased(MouseEvent e) {
//
//    }
//
//    @Override
//    public void mouseEntered(MouseEvent e) {
//
//    }
//
//    @Override
//    public void mouseExited(MouseEvent e) {
//
//    }
//
//    @Override
//    public void changesOccurred() {
//      repaint();
//    }
//
//    private void drawImage (Graphics2D g2, Hexagon<? extends HexItem> hex) {
//      HexItem item = hex.getObj();
//      Point2 position = getPixelPosition(hex);
//
//      String imageName = item.getImageNames();
//
//      BufferedImage img = loaded.get(imageName);
//
//      if (img == null) {
//        img = loadImage(baseImageUrl + item.getImageNames() + ".png");
//        loaded.put(imageName, img);
//      }
//      img = transform(item.getTransform()).apply(img);
//      img = scale(img);
//      g2.drawImage(img, getDarknessFactor(game.getDayStage(), hex.getObj()), position.getX(), position.getY());
//    }
//
//    private Point2 getPixelPosition (Hexagon<?> hex) {
//      return Grid.getPixelPosition(imgSize, hex.getPos2d(), padding / 2);
//    }
//  }
//
//  private RescaleOp getDarknessFactor(ClockStage stage, HexItem hexItem) {
//    if (stage ==  ClockStage.day || !hexItem.affectedByLight()) return new RescaleOp(1, 1, null);
//    if (stage == ClockStage.dusk) return new RescaleOp(
//        new float[]{0.82f, 0.78f, 0.93f, 1f}, // scale factors for red, green, blue, alpha
//        new float[]{0f, 0f, 0f, 0f}, // offsets for red, green, blue, alpha
//        null);
//    if (stage == ClockStage.dawn) return new RescaleOp(
//        new float[]{0.82f, 0.72f, 0.75f, 1f}, // scale factors for red, green, blue, alpha
//        new float[]{0f, 0f, 0f, 0f}, // offsets for red, green, blue, alpha
//        null);
//    else return new RescaleOp(
//        new float[]{0.54f, 0.57f, 0.90f, 1f}, // scale factors for red, green, blue, alpha
//        new float[]{0f, 0f, 0f, 0f}, // offsets for red, green, blue, alpha
//        null);
//
//  }
//
//
//  private BufferedImage loadImage (String url) {
//    try {
//      return ImageIO.read(SwingHexFrame.class.getResourceAsStream(url));
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  private Function<BufferedImage, BufferedImage> transform (HexItem.Transform transform) {
//    if (transform == HexItem.Transform.none) return i -> i;
//    else if (transform == HexItem.Transform.flipped) return this::flipImage;
//    else if (transform == HexItem.Transform.oneEighty) return this::oneEightyImage;
//    else if (transform == HexItem.Transform.oneEightyFlipped) return this::oneEightyFlipped;
//    throw new IllegalStateException();
//  }
//
//  private BufferedImage scale (BufferedImage img) {
////    AffineTransform at = new AffineTransform();
////    at.scale(scale, scale);
////    AffineTransformOp scaleOp =
////        new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
////    return scaleOp.filter(img, null);
////
////    BufferedImage bi = new BufferedImage(
////        ((int) scale * img.getWidth(null)),
////        ((int) scale * img.getHeight(null)),
////        BufferedImage.TYPE_INT_ARGB);
////
////    Graphics2D grph = (Graphics2D) bi.getGraphics();
////    grph.scale(scale, scale);
////    grph.drawImage(img, 0, 0, null);
////    grph.dispose();
////    return bi;
//
//    return
//        Scalr.resize(
//            img,
//            Scalr.Method.SPEED,
//            imgSize,
//            imgSize);
//  }
//
//  private BufferedImage scale (BufferedImage img, int dimX, int dimY) {
//    return
//        Scalr.resize(
//            img,
//            Scalr.Method.SPEED,
//            (int) (dimX * scale),
//            (int) (dimY * scale));
//  }
//
//  private BufferedImage flipImage (BufferedImage img) {
//    AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
//    tx.translate(-img.getWidth(null), 0);
//    AffineTransformOp op = new AffineTransformOp(tx,
//        AffineTransformOp.TYPE_BILINEAR);
//    return op.filter(img, null);
//  }
//
//  private BufferedImage oneEightyImage(BufferedImage img) {
//    AffineTransform tx = new AffineTransform();
//    tx.rotate(Math.PI, img.getWidth() / 2, img.getHeight() / 2);
//    AffineTransformOp op = new AffineTransformOp(tx,
//        AffineTransformOp.TYPE_BILINEAR);
//    return op.filter(img, null);
//  }
//
//  private BufferedImage oneEightyFlipped (BufferedImage img) {
//    AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
//    tx.translate(-img.getWidth(null), 0);
//    tx.rotate(Math.PI, img.getWidth() / 2, img.getHeight() / 2);
//    AffineTransformOp op = new AffineTransformOp(tx,
//        AffineTransformOp.TYPE_BILINEAR);
//    return op.filter(img, null);
//  }
//
//  private class ExploreKeyboardListener2 implements KeyListener {
//
//    Controller game;
//    private Loop loop;
//
//    ExploreKeyboardListener2(Controller game, Loop loop) {
//      this.game = game;
//      this.loop = loop;
//    }
//
//    @Override
//    public void keyTyped(KeyEvent keyEvent) {
//
//    }
//
//    @Override
//    public void keyPressed(KeyEvent keyEvent) {
//
//    }
//
//    @Override
//    public void keyReleased(KeyEvent keyEvent) {
//
//
//      if (keyEvent.getKeyChar() == '+') {
//        scale = scale == 2 ? 1 : 2;
//        imgSize = (int) (originalImageSize * scale);
//        repaint();
//      }
//      if (keyEvent.getKeyChar() == 'p') {
//        loop.setPaused(!loop.getPaused());
//      }
//      if (keyEvent.getKeyChar() == 's') {
//        if (loop.getPaused()) game.oneTick();
//      }
////
////      if (keyEvent.getKeyChar() == 'd') input.setInput(Point2d.E);
////      else if (keyEvent.getKeyChar() == 'a')  input.setInput(Point2d.W);
////      else if  (keyEvent.getKeyChar() == 'q')  input.setInput(Point2d.NW);
////      else if  (keyEvent.getKeyChar() == 'e')  input.setInput(Point2d.NE);
////      else if  (keyEvent.getKeyChar() == 'z')  input.setInput(Point2d.SW);
////      else if  (keyEvent.getKeyChar() == 'x') input.setInput(Point2d.SE);
//    }
//  }
//
//  public class Loop implements ActionListener {
//
//
//    private boolean paused = false;
//    private Controller game;
//
//    public Loop(Controller game) {
//      this.game = game;
//      Timer timer = new Timer(startingSpeed, this);
//      timer.setInitialDelay(1000);
//       timer.start();
//    }
//
//
//    public void setPaused(boolean paused) {
//      this.paused = paused;
//    }
//
//    public boolean getPaused() {
//      return paused;
//    }
//
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//      if (paused) return;
//      this.game.oneTick();
//    }
//  }
//
//}
//
