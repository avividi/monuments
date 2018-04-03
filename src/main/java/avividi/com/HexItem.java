package avividi.com;

public interface HexItem {

  String getImageName();

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
