package avividi.com.monuments.controller;

import avividi.com.monuments.controller.alert.AlertManager;
import avividi.com.monuments.controller.clock.ClockManager;
import avividi.com.monuments.controller.clock.ClockStage;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.gamehex.other.Fire;
import avividi.com.monuments.controller.gamehex.unit.Maldar;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.controller.item.Item;
import avividi.com.monuments.controller.item.ItemGiver;
import avividi.com.monuments.controller.item.food.FoodGiver;
import avividi.com.monuments.controller.pathing.SectorsManager;
import avividi.com.monuments.controller.spawn.SpawnManager;
import avividi.com.monuments.hexgeometry.GridLayer;
import avividi.com.monuments.hexgeometry.Hexagon;
import avividi.com.monuments.hexgeometry.MappedLayer;
import avividi.com.monuments.hexgeometry.PointAxial;
import avividi.com.monuments.hexgeometry.layered.MultiHexLayer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.*;

public class Board {
  private final List<PointAxial> spawnEdges;

  public int clock = ClockStage.dawn.start;
  private final GridLayer<GameHex> ground;
  private final MultiHexLayer<GameHex> statics;
  private final MultiHexLayer<Interactor> others;
  private final MultiHexLayer<Unit> units;
  private final SectorsManager sectorsManager;
  private final AlertManager alertManager = new AlertManager();
  private final ClockManager clockManager = new ClockManager();
  private boolean shouldCalculateSectors = true;

  private Multimap<Class<? extends Unit>, Hexagon<Unit>> unitMap;
  private Multimap<Class<? extends Interactor>, Hexagon<Interactor>> otherMap;
  private Multimap<Class<? extends Item>, Hexagon<ItemGiver>> itemGiverMap;
  private List<Hexagon<FoodGiver>> foodSources;
  private Collection<Hexagon<Fire>> burningFires;
  private Collection<Hexagon<Unit>> friendlyUnits;


  public Board(GridLayer<GameHex> ground,
               MultiHexLayer<GameHex> statics,
               MultiHexLayer<Interactor> others,
               MultiHexLayer<Unit> units) {
    this.ground = ground;
    this.statics = statics;
    this.others = others;
    this.units = units;
    spawnEdges = SpawnManager.calculateSpawnEdges(this);

    this.ground.getHexagons().forEach(hex -> hex.getObj().postLoadCalculation(this, hex.getPosAxial()));
    this.others.getHexagons().forEach(hex -> hex.getObj().postLoadCalculation(this, hex.getPosAxial()));
    this.units.getHexagons().forEach(hex -> hex.getObj().postLoadCalculation(this, hex.getPosAxial()));

    sectorsManager = new SectorsManager(this::hexIsPathAble);
  }

  public void prepareOneTick() {
    clockManager.clockStep(this);

    if (shouldCalculateSectors) calculateSectors();

    calculateUnitMap();
    calculateOtherMap();

    int x = 0;
  }

  private void calculateSectors () {
    System.out.println("  sector recalc");
    sectorsManager.calculateSectors(this.ground.getHexagons().map(Hexagon::getPosAxial), p -> {});
    shouldCalculateSectors = false;
  }

  public ClockStage getDayStage() {
    return clockManager.getDayStage();
  }

  public boolean isStage(ClockStage stage) {
    return clockManager.isStage(stage);
  }

  public GridLayer<GameHex> getGround() {
    return ground;
  }

  public MultiHexLayer<GameHex> getStatics() {
    return statics;
  }

  public MultiHexLayer<Interactor> getOthers() {
    return others;
  }

  public MultiHexLayer<Unit> getUnits() {
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

  //todo implement
  public boolean hexGivesPassageUp(PointAxial pointAxial) {
    return false;
  }

  //todo fix
  public boolean hexIsBuildAble(PointAxial pointAxial) {
    return getStatics().getByAxial(pointAxial).filter(h -> h.getObj().buildable()).isPresent()
        && getOthers().getByAxial(pointAxial).filter(h -> h.getObj().buildable()).isPresent()
        && getUnits().getByAxial(pointAxial).filter(h -> h.getObj().buildable()).isPresent();
  }

  public boolean hasStaticObstructions (PointAxial pointAxial) {

    Optional<Hexagon<GameHex>> ground = getStatics().getByAxial(pointAxial);
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

  public Collection<Hexagon<FoodGiver>> getFoodSources() {
    return foodSources;
  }

  public Collection<Hexagon<ItemGiver>> getItemGiver (Class<? extends Item> clazz) {
    return itemGiverMap.get(clazz);
  }

  public Set<Integer> getSector(PointAxial point) {
    return sectorsManager.getSector(point);
  }

  public boolean isInSameSector(PointAxial p1, PointAxial p2) {
    return sectorsManager.isInSameSector(p1, p2);
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
    foodSources = new ArrayList<>();

    otherMap = ArrayListMultimap.create();
    this.others.getHexagons().forEach(this::mapOtherHex);
  }

  private void mapOtherHex(Hexagon<Interactor> hex) {
    otherMap.put(hex.getObj().getClass(), hex);
    if (hex.getObj() instanceof Fire && ((Fire) hex.getObj()).burning()) burningFires.add(hex.as());
    if (hex.getObj() instanceof ItemGiver) {
      itemGiverMap.put(((ItemGiver) hex.getObj()).getItemPickupType(), hex.as());
    }
    if (hex.getObj() instanceof FoodGiver && ((FoodGiver) hex.getObj()).hasAvailableFood()) {
      foodSources.add(hex.as());
    }

  }

  public void setShouldCalculateSectors() {
    this.shouldCalculateSectors = true;
  }

  public SectorsManager getSectorsManager() {
    return sectorsManager;
  }

  public void addLayerAbove (int currentLayer) {
    if (statics.hasLayer(currentLayer + 1)) return;
    statics.addLayerAbove(new MappedLayer<>(ground, currentLayer + 1));
    others.addLayerAbove(new MappedLayer<>(ground, currentLayer + 1));
    units.addLayerAbove(new MappedLayer<>(ground, currentLayer + 1));
  }


  public void addLayerBelow (int currentLayer) {
    if (statics.hasLayer(currentLayer - 1)) return;
    statics.addLayerBelow(new MappedLayer<>(ground, currentLayer - 1));
    others.addLayerBelow(new MappedLayer<>(ground, currentLayer - 1));
    units.addLayerBelow(new MappedLayer<>(ground, currentLayer - 1));
  }

  public AlertManager getAlertManager() {
    return alertManager;
  }
}
