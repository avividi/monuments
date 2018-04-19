package avividi.com.controller.gameitems.staticitems;

import avividi.com.controller.gameitems.GameItem;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class Cliff implements GameItem {

  private final List<String> image;
  private final Transform transform;

  public Cliff(List<String> image, Transform transform) {
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
  public List<String> getImageName() {
    return image;
  }
}
