package avividi.com.controller;

import avividi.com.controller.gameitems.*;
import avividi.com.controller.gameitems.other.Fire;
import avividi.com.controller.gameitems.unit.Maldar;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {

  private int clock = 1650;
  private final Grid<GameItem> ground;
  private final Grid<InteractingItem> others;
  private final Grid<Unit> units;

  private final CropFilter cropFilter = new CropFilter();


  private final List<PointAxial> spawnEdges;
  private Multimap<Class<? extends Unit>, Hexagon<Unit>> unitMap;
  private Multimap<Class<? extends InteractingItem>, Hexagon<InteractingItem>> otherMap;
  private Collection<Hexagon<Fire>> burningFires;
  private Collection<Hexagon<Unit>> friendlyUnits;


  public Board(Grid<GameItem> ground, Grid<InteractingItem> others, Grid<Unit> units) {
    this.ground = ground;
    this.others = others;
    this.units = units;
    spawnEdges = calculateSpawnEdges();

    this.ground.getHexagons().forEach(hex -> hex.getObj().postLoadCalculation(this, hex.getPosAxial()));
    this.others.getHexagons().forEach(hex -> hex.getObj().postLoadCalculation(this, hex.getPosAxial()));
    this.units.getHexagons().forEach(hex -> hex.getObj().postLoadCalculation(this, hex.getPosAxial()));
  }

  public void step() {
    clockStep();

    calculateFriendlyUnits();
    calculateUnitMap();
    calculateBurningFires();
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

  public Grid<InteractingItem> getOthers() {
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

    Optional<Hexagon<InteractingItem>> other = getOthers().getByAxial(pointAxial);
    if (other.isPresent() && other.filter(u -> !u.getObj().passable()).isPresent()) return true;
    return false;
  }

  public Stream<Hexagon<? extends GameItem>> getHexagonsByDrawingOrder(Marker marker) {
    Stream<Hexagon<GameItem>> groundStream = ground.getHexagons();
    Stream<Hexagon<InteractingItem>> otherStream = others.getHexagons();
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


  public Collection<Hexagon<Unit>> getUnits(Class<? extends Unit> clazz) {
    return unitMap.get(clazz);
  }

  private void calculateUnitMap() {
    unitMap = ArrayListMultimap.create();
    this.units.getHexagons().forEach(unit -> unitMap.put(unit.getObj().getClass(), unit));
  }

  public Collection<Hexagon<InteractingItem>> getOther(Class<? extends InteractingItem> clazz) {
    return otherMap.get(clazz);
  }

  private void calculateOtherMap() {
    otherMap = ArrayListMultimap.create();
    this.others.getHexagons().forEach(other -> otherMap.put(other.getObj().getClass(), other));
  }

  private void calculateBurningFires() {
    burningFires = others.getHexagons(Fire.class).filter(f -> f.getObj().burning()).collect(Collectors.toList());
  }

  public Collection<Hexagon<Fire>>  getBurningFires() {
    return burningFires;
  }

  private void calculateFriendlyUnits() {
    friendlyUnits = units.getHexagons()
        .filter(u -> u.getObj().isFriendly())
        .collect(Collectors.toList());
  }

  public Collection<Hexagon<Unit>> getFriendlyUnits() {
    return friendlyUnits;
  }

}
