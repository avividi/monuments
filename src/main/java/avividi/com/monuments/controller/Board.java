package avividi.com.monuments.controller;

import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.gamehex.other.Fire;
import avividi.com.monuments.controller.gamehex.unit.Maldar;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.Grid;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.ItemGiver;
import avividi.com.monuments.controller.pathing.Sectors;
import avividi.com.monuments.controller.spawn.SpawnManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.*;

public class Board {
  private final List<PointAxial> spawnEdges;

  private int clock = 1000;
  private final Grid<GameHex> ground;
  private final Grid<Interactor> others;
  private final Grid<Unit> units;
  private final Sectors sectors;
  private boolean shouldCalculateSectors = true;

  private Multimap<Class<? extends Unit>, Hexagon<Unit>> unitMap;
  private Multimap<Class<? extends Interactor>, Hexagon<Interactor>> otherMap;
  private Multimap<Class<? extends Item>, Hexagon<ItemGiver>> itemGiverMap;
  private Collection<Hexagon<Fire>> burningFires;
  private Collection<Hexagon<Unit>> friendlyUnits;


  public Board(Grid<GameHex> ground, Grid<Interactor> others, Grid<Unit> units) {
    this.ground = ground;
    this.others = others;
    this.units = units;
    spawnEdges = SpawnManager.calculateSpawnEdges(this);

    this.ground.getHexagons().forEach(hex -> hex.getObj().postLoadCalculation(this, hex.getPosAxial()));
    this.others.getHexagons().forEach(hex -> hex.getObj().postLoadCalculation(this, hex.getPosAxial()));
    this.units.getHexagons().forEach(hex -> hex.getObj().postLoadCalculation(this, hex.getPosAxial()));

    sectors = new Sectors(this::hexIsPathAble);
  }

  public void step() {
    clockStep();

    if (shouldCalculateSectors) calculateSectors();

    calculateUnitMap();
    calculateOtherMap();
  }

  private void clockStep () {

    clock++;
    if (clock > 3000) clock = 0;
  }

  private void calculateSectors () {
    System.out.println("Sector recalc");
    sectors.calculateSectors(this.ground.getHexagons().map(Hexagon::getPosAxial), p -> {});
    shouldCalculateSectors = false;
  }

  public DayStage getDayStage() {
    if (clock > 2850) return DayStage.dawn;
    if (clock > 2150) return DayStage.night;
    if (clock > 2000) return DayStage.dusk;
    return DayStage.day;
  }

  public Grid<GameHex> getGround() {
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

  public boolean hexIsBuildAble(PointAxial pointAxial) {
    if (hasStaticObstructions(pointAxial)) return false;
    return !getOthers().getByAxial(pointAxial).isPresent();
  }

  public boolean hasStaticObstructions (PointAxial pointAxial) {

    Optional<Hexagon<GameHex>> ground = getGround().getByAxial(pointAxial);
    if (!ground.filter(g -> g.getObj().passable()).isPresent()) return true;

    Optional<Hexagon<Interactor>> other = getOthers().getByAxial(pointAxial);
    if (other.isPresent() && other.filter(u -> !u.getObj().passable()).isPresent()) return true;
    return false;
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

  public Collection<Hexagon<ItemGiver>> getItemGiver (Class<? extends Item> clazz) {
    return itemGiverMap.get(clazz);
  }

  public Set<Integer> getSector(PointAxial point) {
    return sectors.getSector(point);
  }

  public boolean isInSameSector(PointAxial p1, PointAxial p2) {
    return sectors.isInSameSector(p1, p2);

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


  public void setShouldCalculateSectors() {
    this.shouldCalculateSectors = true;
  }
}
