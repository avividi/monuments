package avividi.com.controller.gameitems.staticitems;

import avividi.com.controller.HexItem;
import avividi.com.controller.gameitems.GameItem;

public class Cliff implements GameItem {

  private String image;
  private Transform transform;

  public Cliff(String image, Transform transform) {
    this.image = image;
    this.transform = transform;
  }

  @Override
  public boolean passable() {
    return false;
  }

  @Override
  public Transform getTransform() {
    return transform;
  }

  @Override
  public String getImageName() {
    return image;
  }
}
