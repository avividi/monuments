package avividi.com.monuments.controller.gamehex.staticitems;

import avividi.com.monuments.controller.gamehex.GameHex;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomStaticItem implements GameHex {

  private List<String> images;
  private final Transform transform;
  private boolean affectedByLight = true;
  private final boolean passable;
  private final boolean buildable;
  private final String id;


  public CustomStaticItem (ObjectNode json) {
    images = new ArrayList<>();
    json.get("images").forEach(img -> images.add(img.asText()));
    transform = Transform.valueOf(json.get("transform").asText());
    passable = Optional.ofNullable(json.get("passable")).map(JsonNode::asBoolean).orElse(true);
    id = json.get("id") == null ? null : json.get("id").asText();
    buildable = json.get("buildable") != null && json.get("buildable").asBoolean();
  }

  public CustomStaticItem(List<String> images,
                          Transform transform,
                          boolean affectedByLight,
                          boolean passable,
                          boolean buildable) {
    this.images = images;
    this.transform = transform;
    this.affectedByLight = affectedByLight;
    this.passable = passable;
    this.buildable = buildable;
    id = null;
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
    return images;
  }

  @Override
  public boolean affectedByLight() {
    return affectedByLight;
  }

  public void setImages(List<String> images) {
    this.images = images;
  }

  @Override
  public String getId() {
    return id;
  }
}
