package avividi.com;

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

  public enum Transform {
    none,
    flipped,
    oneEighty,
    oneEightyFlipped
  }
}
