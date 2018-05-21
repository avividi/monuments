package avividi.com.monuments.hexgeometry;

import java.util.Optional;
import java.util.stream.Stream;

public class CroppedGrid<T> extends GridLayer<T> {


  private final GridLayer<T> inner;
  private int cropSize;
  private final int offset;
  private PointAxial center;

  public CroppedGrid (GridLayer<T> other, int cropSize, PointAxial center) {
    super(other);
    this.inner = other;
    this.cropSize = cropSize;
//    offset = cropSize < defaultCropSize ? defaultCropSize - cropSize : 0;
    offset = 0;
    this.center = center;
  }

  @Override
  public Optional<Hexagon<T>> getByAxial(PointAxial pointAxial) {
    return super.getByAxial(pointAxial).map(this::adjustHexPosition);
  }

  @Override
  public Optional<Hexagon<T>> getBy2d(Point2d point2d) {

    PointAxial pointAxial = super.point2dToPointAxial(point2d)
        .map(this::adjustToGrid)
        .orElse(null);
    if (pointAxial == null) return Optional.empty();

    Point2d adjusted = super.pointAxialToPoint2d(pointAxial).orElse(null);
    if (adjusted == null) return Optional.empty();

    return super.getBy2d(adjusted);
  }

  @Override
  public Stream<Hexagon<T>> getHexagons () {
    if (cropSize < 0) return Stream.empty();

    Stream.Builder<Hexagon<T>> builder = Stream.builder();
    getByAxial(center).ifPresent(builder::add);

    for (int i = 1; i < cropSize; ++i) {
      addHexagonsToStream(i, PointAxial.E, builder);
      addHexagonsToStream(i, PointAxial.NE, builder);
      addHexagonsToStream(i, PointAxial.NW, builder);
      addHexagonsToStream(i, PointAxial.W, builder);
      addHexagonsToStream(i, PointAxial.SW, builder);
      addHexagonsToStream(i, PointAxial.SE, builder);
    }

    return builder.build().map(this::adjustHexPosition);
  }

  private void addHexagonsToStream (int i, PointAxial direction, Stream.Builder<Hexagon<T>> builder) {

    PointAxial newPoint = direction.multiply(i).add(center);

    getByAxial(newPoint).ifPresent(builder::add);

    PointAxial r1 = new PointAxial(direction.getX() + direction.getY(), -direction.getX());
    PointAxial rotated = new PointAxial(r1.getX() + r1.getY(), -r1.getX());

    for (int j = 1; j < i; ++j) {
      getByAxial(rotated.multiply(j).add(newPoint)).ifPresent(builder::add);
    }
  }

  private <U> Hexagon<U> adjustHexPosition (Hexagon<U> hex) {

    PointAxial newPos = adjustFromGrid(hex.getPosAxial());
    Point2d newPos2d = pointAxialToPoint2d(newPos).orElse(null);
    return new Hexagon<>(hex.getObj(), hex.getPosAxial(), newPos2d);
  }

  private PointAxial adjustFromGrid (PointAxial pointAxial) {
    return pointAxial.add(
        -center.getX() + (cropSize + offset) / 2 - 1,
        -center.getY() + (cropSize + offset) - 1);
  }

  private PointAxial adjustToGrid (PointAxial pointAxial) {
    return pointAxial.add(
        center.getX() - (cropSize + offset) / 2 + 1 ,
        center.getY() - (cropSize + offset) + 1);
  }

  public void setCenter(PointAxial center) {
    this.center = center;
  }

  public int getCropSize() {
    return cropSize;
  }

  public void setCropSize(int cropSize) {
    this.cropSize = cropSize;
  }

  public PointAxial getCenter() {
    return center;
  }

  public GridLayer<T> getInnner() {
    return inner;
  }
}
