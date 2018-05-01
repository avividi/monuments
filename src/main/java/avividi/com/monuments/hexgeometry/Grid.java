package avividi.com.monuments.hexgeometry;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/* this class assumes 'pointy' top hexagons, not 'flat' top hexagons */
public class Grid<T> {

  public static Point2 getPixelPosition(int imgHeight, Point2d point2d, int padding) {
    final double verticalConstant = 0.75;
    final double horizontalConstant = 0.4330127; // Math.sqrt(3) / 4;
    imgHeight = imgHeight + 1;// visual adjust

    int verticalDistance = (int) (imgHeight * verticalConstant) + padding;
    int horizontalDistance = (int) (imgHeight * horizontalConstant) + padding;

    return new Point2(
        point2d.getX() * horizontalDistance,
        point2d.getY() * verticalDistance);
  }

  public Point2d getPosition2d(double imageHeight, double x, double y, double padding) {

    double sqrt3 = 1.732050;

    double pixelHeight = imageHeight + padding;
    double pixelWidth = pixelHeight * (sqrt3 / 2);
    double pixelStartX = (pixelHeight - pixelWidth) / 2;

    double startSquare = (pixelHeight) / 4;
    double squareHeight = pixelHeight / 2;

    double yd = (y) / (squareHeight + startSquare);
    int yi = (int) Math.floor(yd);

    double yAddjuster = ((double) (yi & 1)) / 2 + (((double) odd) / 2);

    double xd = (x - pixelStartX) / pixelWidth + yAddjuster;
    int xi = (int) Math.floor(xd) * 2 - (yi & 1) - odd;

//    System.out.println("xy: " + xi + " " + yi);

    if (yd - yi > (1.0 / 3)) {

    } else {

      //TODO figure out which hex (the y area between hexes is not mapped)
      return new Point2d(-1, -1);
    }

    return new Point2d(xi, yi);
//    double sqrt3 = 1.732050;
  }

  private final T[][] grid;
  private final int padding;
  private final int odd;

  private final int columnMax;
  private final int columnMin;
  private final int rowMax;
  private final int rowMin = 0;

  private Grid(T[][] grid,
               int padding,
               int odd,
               int columnMax,
               int columnMin,
               int rowMax) {
    this.grid = grid;
    this.padding = padding;
    this.odd = odd;
    this.columnMax = columnMax;
    this.columnMin = columnMin;
    this.rowMax = rowMax;
  }

  public <U> Grid<U> getEmptyCopy() {
    U[][] emptyGrid = createArray(grid[0].length, grid.length);

    return new Grid<>(emptyGrid, padding, odd, columnMax, columnMin, rowMax);
  }

  public Grid<T> getShallowCopy() {
    T[][] emptyGrid = createArray(grid[0].length, grid.length);
    copyArray(emptyGrid, grid, u -> u);

    return new Grid<>(emptyGrid, padding, odd, columnMax, columnMin, rowMax);
  }

  public Grid<T> getFullCopy(Function<T, T> copyMethod) {
    T[][] emptyGrid = createArray(grid[0].length, grid.length);
    copyArray(emptyGrid, grid, copyMethod);

    return new Grid<>(emptyGrid, padding, odd, columnMax, columnMin, rowMax);
  }

  public Grid(String input, Map<Character, Supplier<T>> charMap) {
    String[] rows = input.split("\n");

    Preconditions.checkState(rows.length > 0, "Must have at least one row");
    Preconditions.checkState(!rows[0].isEmpty(), "Must have at least one column");
    Preconditions.checkState(Arrays.stream(rows).allMatch(s -> s.length() == rows[0].length()),
        "All rows must be of same length");
    Preconditions.checkState(StringUtils.isNotBlank(rows[0]),
        "First row must not be blank");

    odd = IntStream.range(0, rows[0].length())
        .filter(i -> rows[0].charAt(i) != ' ')
        .findFirst()
        .orElse(0) & 1;

    rowMax = rows.length - 1;

    padding = rowMax / 2 + odd;
    int topLeftPadding = padding;

    columnMax = (rows[0].length() + 1) / 2 - 1;
    columnMin = -padding;

    int oddRow = odd;

    grid = createArray(padding + ((1 + rows[0].length()) >> 1), rows.length);

    for (int j = 0; j <= rowMax; ++j) {
      for (int i = oddRow; i < rows[j].length(); i += 2) {

        char c = rows[j].charAt(i);
        grid[j][topLeftPadding + i / 2] = (c == '+' || c == ' ') ? null : getInstanceFromCharMap(charMap, c);
      }
      topLeftPadding -= oddRow;
      oddRow = oddRow == 1 ? 0 : 1;
    }
  }

  private T getInstanceFromCharMap(Map<Character, Supplier<T>> charMap, char c) {
    return charMap.getOrDefault(c, () -> {
      throw new IllegalStateException("Missing mapping for character '" + c + "'");
    })
        .get();
  }

  public Grid(Grid<T> other) {
    grid = other.grid;
    padding = other.padding;
    odd = other.odd;
    columnMax = other.columnMax;
    columnMin = other.columnMin;
    rowMax = other.rowMax;
  }

  private static <U> U[][] createArray(int x, int y) {
    @SuppressWarnings({"unchecked"})
    U[][] grid = (U[][]) new Object[y][x];
    return grid;
  }

  private static <U> void copyArray(U[][] neww, U[][] old, Function<U, U> copyMethod) {
    for (int j = 0; j < old.length; j++) {
      for (int i = 0; i < old[j].length; i++) {
        neww[j][i] = copyMethod.apply(old[j][i]);
      }
    }
  }

  public Optional<Hexagon<T>> getByAxial(PointAxial pointAxial) {
    Optional<Point2> index = indexByAxial(pointAxial);
    if (!index.isPresent()) return Optional.empty();

    T c = grid[index.get().getY()][index.get().getX()];
    return c == null
        ? Optional.empty()
        : Optional.of(new Hexagon<>(c, pointAxial, getPos2d(index.get().getX(), index.get().getY())));
  }

  private Optional<Point2> indexByAxial(PointAxial pointAxial) {
    Preconditions.checkNotNull(pointAxial);
    if (pointAxial.getX() < columnMin || pointAxial.getX() > columnMax
        || pointAxial.getY() < rowMin || pointAxial.getY() > rowMax) return Optional.empty();

    int j = pointAxial.getY();
    int i = pointAxial.getX() + padding;
    return (j >= grid.length || i >= grid[j].length)
        ? Optional.empty()
        : Optional.of(new Point2(i, j));
  }

  private PointAxial getAxial(int i, int j) {
    return new PointAxial(i - padding, j);
  }

  private Optional<Point2> indexBy2d(Point2d point2d) {
    if (point2d.getX() < 0 || point2d.getY() < 0) return Optional.empty();
    int j = point2d.getY();
    int i = point2d.getX() / 2 + (padding - ((j + odd) / 2));
    return (j >= grid.length || i >= grid[j].length)
        ? Optional.empty()
        : Optional.of(new Point2(i, j));
  }

  public Optional<Hexagon<T>> getBy2d(Point2d point2d) {

    Optional<Point2> index = indexBy2d(point2d);
    if (!index.isPresent()) return Optional.empty();

    T c = grid[index.get().getY()][index.get().getX()];
    return c == null
        ? Optional.empty()
        : Optional.of(new Hexagon<>(c, getAxial(index.get().getX(), index.get().getY()), point2d));
  }

  private Point2d getPos2d(int i, int j) {
    int y = j;
    int x = i * 2 - (padding - ((j + odd))) - padding;

    return new Point2d(x, y);
  }

  public Stream<Hexagon<T>> getHexagons() {

    Stream.Builder<Hexagon<T>> s = Stream.builder();

    for (int j = 0; j < grid.length; j++) {
      for (int i = 0; i < grid[0].length; i++) {
        T c = grid[j][i];
        if (c != null) s.accept(new Hexagon<>(c, getAxial(i, j), getPos2d(i, j)));
      }
    }

    return s.build();
  }

  @SuppressWarnings({"unchecked"})
  public <U extends T> Stream<Hexagon<U>> getHexagons (Class<U> clazz) {
    return getHexagons()
        .filter(hex -> clazz.equals(hex.getObj().getClass()))
        .map(hex -> new Hexagon<>((U) hex.getObj(), hex.getPosAxial(), hex.getPos2d()));
  }

  public T clearHex(PointAxial pointAxial) {
    Optional<Point2> opt = indexByAxial(pointAxial);
    if (!opt.isPresent()) return null;

    Point2 i = opt.get();
    T t = grid[i.getY()][i.getX()];

    grid[i.getY()][i.getX()] = null;
    return t;
  }

  public boolean setHex(T t, PointAxial pointAxial) {
    return indexByAxial(pointAxial)
        .map(point2 -> setHex(t, point2))
        .isPresent();
  }

  public boolean setHex(T t, Point2d point2d) {
    return indexBy2d(point2d)
        .map(point2 -> setHex(t, point2))
        .isPresent();
  }

  private boolean setHex(T t, Point2 index) {
    T prev = grid[index.getY()][index.getX()];
    if (prev == t) return false;

    grid[index.getY()][index.getX()] = t;
    return true;
  }

  public <U extends T> Optional<Hexagon<T>> getByFirstOccurrence(Class<U> clazz) {
    return getHexagons().filter(hex -> clazz.equals(hex.getObj().getClass())).findFirst();
  }

  public String toString() {
    return Arrays.stream(this.grid).map(Arrays::toString).collect(Collectors.joining("\n"));
  }

  public Optional<Point2d> pointAxialToPoint2d(PointAxial pointAxial) {
    return indexByAxial(pointAxial).map(index -> getPos2d(index.getX(), index.getY()));
  }

  public Optional<PointAxial> point2dToPointAxial(Point2d point2d) {
    return indexBy2d(point2d).map(index -> getAxial(index.getX(), index.getY()));
  }
}

