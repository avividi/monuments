package avividi.com.controller.gameitems.staticitems;

import avividi.com.controller.gameitems.GameItem;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;

public class CustomStaticItem extends GameItem {

  private final List<String> image;
  private final Transform transform;
  private boolean affectedByLight = true;
  private boolean passable = true;

  public CustomStaticItem (ObjectNode json) {
    super(null);
    image = new ArrayList<>();
    json.get("images").forEach(img -> image.add(img.asText()));
    transform = Transform.valueOf(json.get("transform").asText());
  }

  public CustomStaticItem(List<String> image, Transform transform) {
    super(null);
    this.image = image;
    this.transform = transform;
  }

  public CustomStaticItem(List<String> image, Transform transform, boolean affectedByLight, boolean passable) {
    super(null);
    this.image = image;
    this.transform = transform;
    this.affectedByLight = affectedByLight;
    this.passable = passable;
  }

  @Override
  public boolean passable() {
    return passable;
  }

  @Override
  public Transform getTransform() {
    return transform;
  }

  @Override
  public List<String> getImageNames() {
    return image;
  }

  @Override
  public boolean affectedByLight() {
    return affectedByLight;
  }

}
