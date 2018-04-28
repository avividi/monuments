package avividi.com.controller;

import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.gameitems.Interactor;
import avividi.com.controller.gameitems.other.Fire;
import avividi.com.controller.gameitems.unit.Maldar;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Grid;
import avividi.com.controller.hexgeometry.Hexagon;
import avividi.com.controller.hexgeometry.PointAxial;
import avividi.com.controller.item.Item;
import avividi.com.controller.item.ItemGiver;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class Board {

  private int clock = 1650;
  private final Grid<GameItem> ground;
  private final Grid<Interactor> others;
  private final Grid<Unit> units;

  private final CropFilter cropFilter;


  private final List<PointAxial> spawnEdges;

  private Multimap<Class<? extends Unit>, Hexagon<Unit>> unitMap;
  private Multimap<Class<? extends Interactor>, Hexagon<Interactor>> otherMap;
  private Multimap<Class<? extends Item>, Hexagon<ItemGiver>> itemGiverMap;
  private Collection<Hexagon<Fire>> burningFires;
  private Collection<Hexagon<Unit>> friendlyUnits;


  public Board(Grid<GameItem> ground, Grid<Interactor> others, Grid<Unit> units) {
    this.ground = ground;
    this.others = others;
    this.units = units;
    spawnEdges = calculateSpawnEdges();

    this.ground.getHexagons().forEach(hex -> hex.getObj().postLoadCalculation(this, hex.getPosAxial()));
    this.others.getHexagons().forEach(hex -> hex.getObj().postLoadCalculation(this, hex.getPosAxial()));
    this.units.getHexagons().forEach(hex -> hex.getObj().postLoadCalculation(this, hex.getPosAxial()));

    cropFilter = new CropFilter(this.ground);
  }

  public void step() {
    clockStep();

    calculateUnitMap();
    calculateOtherMap();
  }

  private void clockStep () {

    clock++;
    if (clock > 3000) clock = 0;
  }

  public DayStage getDayStage() {
    if (clock > 2800) return DayStage.dawn;
    if (clock > 2000) return DayStage.night;
    if (clock > 1800) return DayStage.dusk;
    return DayStage.day;
  }

  public Grid<GameItem> getGround() {
    return ground;
  }

  public Grid<Interactor> getOthers() {
    return others;
  }

  public Grid<Unit> getUnits() {
    return units;
  }

  public boolean hexIsFree(PointAxial pointAxial) {
    if (hasStaticObstructions(pointAxial)) return false;

    Optional<Hexagon<Unit>> unit = getUnits().getByAxial(pointAxial);
    if (unit.isPresent() && unit.filter(u -> !u.getObj().passable()).isPresent()) return false;

    return true;
  }

  public boolean hexIsPathAble (PointAxial pointAxial) {
    if (hasStaticObstructions(pointAxial)) return false;

    Optional<Hexagon<Unit>> unit = getUnits().getByAxial(pointAxial);
    if (unit.isPresent() && unit.filter(u -> !(u.getObj() instanceof Maldar)).isPresent()) return false;

    return true;
  }

  public boolean hasStaticObstructions (PointAxial pointAxial) {

    Optional<Hexagon<GameItem>> ground = getGround().getByAxial(pointAxial);
    if (!ground.filter(g -> g.getObj().passable()).isPresent()) return true;

    Optional<Hexagon<Interactor>> other = getOthers().getByAxial(pointAxial);
    if (other.isPresent() && other.filter(u -> !u.getObj().passable()).isPresent()) return true;
    return false;
  }

  public Stream<Hexagon<? extends HexItem>> getHexagonsByDrawingOrder(Marker marker) {
    Stream<Hexagon<GameItem>> groundStream = ground.getHexagons();
    Stream<Hexagon<Interactor>> otherStream = others.getHexagons();
    Stream<Hexagon<Unit>> unitStream = units.getHexagons();

    Hexagon<GameItem> markerHex = marker.asHexagon(ground);
    Stream<Hexagon<GameItem>> mark = marker.toggled() ? Stream.of(marker.asHexagon(ground)) : Stream.empty();
    cropFilter.adjustToMarker(markerHex);

    return cropFilter.crop(Stream.of(groundStream, otherStream, unitStream, mark)
        .flatMap(Function.identity())
        .filter(h -> h.getObj().renderAble()));
  }

  public List<PointAxial> getSpawnEdges() {
    return spawnEdges;
  }


  public Collection<Hexagon<Unit>> getUnits(Class<? extends Unit> clazz) {
    return unitMap.get(clazz);
  }

  public Collection<Hexagon<Interactor>> getOther(Class<? extends Interactor> clazz) {
    return otherMap.get(clazz);
  }

  public Collection<Hexagon<Fire>>  getBurningFires() {
    return burningFires;
  }


  public Collection<Hexagon<Unit>> getFriendlyUnits() {
    return friendlyUnits;
  }

  public Collection<Hexagon<ItemGiver>>  getItemGiver (Class<? extends Item> clazz) {
    return itemGiverMap.get(clazz);
  }


  private List<PointAxial> calculateSpawnEdges() {
    Iterator<Hexagon<GameItem>> iterator = ground.getHexagons().iterator();
    Set<PointAxial> edges = new HashSet<>() ;

    while (iterator.hasNext()) {
      Hexagon<GameItem> next = iterator.next();
      if (!edges.contains(next.getPosAxial())
          && !hasStaticObstructions(next.getPosAxial())
          && PointAxial.allDirections.stream()
          .anyMatch(dir -> !ground.getByAxial(next.getPosAxial().add(dir)).isPresent())) {

        edges.add(next.getPosAxial());
      }
    }
    return new ArrayList<>(edges);
  }

  private void calculateUnitMap() {
    this.friendlyUnits = new ArrayList<>();

    unitMap = ArrayListMultimap.create();
    this.units.getHexagons().forEach(this::mapUnitHex);
  }

  private void mapUnitHex(Hexagon<Unit> hex) {
    unitMap.put(hex.getObj().getClass(), hex);
    if (hex.getObj().isFriendly()) friendlyUnits.add(hex);
  }

  private void calculateOtherMap() {
    this.burningFires = new ArrayList<>();
    this.itemGiverMap = ArrayListMultimap.create();

    otherMap = ArrayListMultimap.create();
    this.others.getHexagons().forEach(this::mapOtherHex);
  }

  private void mapOtherHex(Hexagon<Interactor> hex) {
    otherMap.put(hex.getObj().getClass(), hex);
    if (hex.getObj() instanceof Fire && ((Fire) hex.getObj()).burning()) burningFires.add(hex.as());
    if (hex.getObj() instanceof ItemGiver)
      ((ItemGiver) hex.getObj()).getSupportedPickUpItems().forEach(item -> itemGiverMap.put(item, hex.as()));
  }


}
