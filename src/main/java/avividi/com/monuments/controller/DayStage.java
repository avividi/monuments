package avividi.com.monuments.controller;

public enum DayStage {
  day(0, 2000),
  dusk(day.end, 2150),
  night(dusk.end, 2850),
  dawn(night.end, 3000);

  final int start;
  final int end;

  DayStage(int start, int end) {
    this.start = start;
    this.end = end;
  }

  public static int cycleSize = dawn.end;
}
