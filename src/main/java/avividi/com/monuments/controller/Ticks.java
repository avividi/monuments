package avividi.com.monuments.controller;

public class Ticks {
  public class TOthers {
    public class TFire {
      public static final int startLife = 1200;
      public static final int indicateLifeLow = 500;
      public static final int flickerPause = 6;
      public static final int waitForReTask = 4; //* PlanManager.waitForRePlan
      public static final int deliverTime = 4;
    }
    public class TFirePlant {
      public static final int pickUpTime = 8;
    }
    public class TSingleItemGiver {
      public static final int defaultPickUpTime = 5;
    }
  }
  public class TUnits {
    public class TRivskin {
      public static final int waitForRePlan = 30;
      public static final int normalSpeed = 12;
      public static final int escapeSpeed = 5;
      public static final int chaseSpeed = 3;
    }
  }
  public class TSpawnManager { public static final int spawnCycle = 100;}
  public class TPlanManager { public static final int waitForRePlan = 10;}
  public class TTask {
    public class TMaldarMoveTask { public static final int time = 5;}
    public class TSelfDestroyTask { public static final int time = 5;}
    public class TSimpleMoveTask { public static final int defaultTime = TMaldarMoveTask.time;}
  }
}
