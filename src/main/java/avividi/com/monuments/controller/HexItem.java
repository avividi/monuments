package avividi.com.monuments.controller;

import java.util.List;

public interface HexItem {

  List<String> getImageNames();

  default boolean renderAble() {
    return true;
  }

  default boolean affectedByLight() {
    return true;
  }

  default Transform getTransform() {
    return Transform.none;
  }

  default void setTransform(Transform transform) {
    throw new UnsupportedOperationException();
  };

  public enum Transform {
    none,
    flipped,
    oneEighty,
    oneEightyFlipped
  }
}
