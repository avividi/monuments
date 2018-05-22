package avividi.com.monuments.controller.clock;

import avividi.com.monuments.controller.Board;

public class ClockManager {


  public int clock = ClockStage.dawn.start;

  public void clockStep (Board board) {
    clock++;
    if (clock == ClockStage.day.end) board.getAlertManager().setAlertText("Night time is falling");
    else if (clock == ClockStage.night.end) board.getAlertManager().setAlertText("The sun is rising!");

    if (clock > ClockStage.dawn.end) clock = 0;
  }

  public ClockStage getDayStage() {
    if (clock >= ClockStage.dawn.start) return ClockStage.dawn;
    if (clock >= ClockStage.night.start) return ClockStage.night;
    if (clock >= ClockStage.dusk.start) return ClockStage.dusk;
    return ClockStage.day;
  }

  public boolean isStage(ClockStage stage) {
    return clock >= stage.start && clock < stage.end;
  }
}
