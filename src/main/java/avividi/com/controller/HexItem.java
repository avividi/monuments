package avividi.com.controller;

public interface HexItem {

  String getImageName();

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
