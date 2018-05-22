package avividi.com.monuments.controller.clock;

public enum ClockStage {
  day(0, 2000),
  dusk(day.end, 2150),
  night(dusk.end, 2850),
  dawn(night.end, 3000);

  public final int start;
  public final int end;

  ClockStage(int start, int end) {
    this.start = start;
    this.end = end;
  }

  public int getSize() {
    return end - start;
  }

  public static int cycleSize = dawn.end;
}
