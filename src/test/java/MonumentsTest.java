import avividi.com.controller.GameController;
import avividi.com.controller.gameitems.other.Fire;
import avividi.com.controller.gameitems.other.FirePlant;
import avividi.com.controller.gameitems.unit.Maldar;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.IntStream;

public class MonumentsTest {

  @Test
  public void crowded() {

    GameController controller = new GameController("/maps/crowded.json");
    controller.setDisableSpawns(true);

    Assert.assertEquals(controller.getHexagons().filter(h -> h.getObj() instanceof Maldar).count(), 19);

    while (controller.getHexagons().anyMatch(h -> h.getObj() instanceof FirePlant)) {
      System.out.println("step 200");
      IntStream.range(0, 200).forEach($ -> controller.oneStep());
      Assert.assertEquals(controller.getHexagons().filter(h -> h.getObj() instanceof Maldar).count(), 19);
    }
  }


  @Test
  public void multiFires() {


    GameController controller = new GameController("/maps/multifires.json");
    controller.setDisableSpawns(true);

    Assert.assertEquals(controller.getHexagons().filter(h -> h.getObj() instanceof Maldar).count(), 18);
    Assert.assertEquals(controller.getHexagons()
        .filter(h -> h.getObj() instanceof Fire)
        .filter(h -> ((Fire) h.getObj()).burning()).count(), 10);

    IntStream.range(0, 20).forEach($ -> {
      System.out.println("step 200");
      IntStream.range(0, 200).forEach($$ -> controller.oneStep());
      Assert.assertEquals(controller.getHexagons().filter(h -> h.getObj() instanceof Maldar).count(), 18);
      Assert.assertEquals(controller.getHexagons()
          .filter(h -> h.getObj() instanceof Fire)
          .filter(h -> ((Fire) h.getObj()).burning()).count(), 10);
    });
    while (controller.getHexagons().anyMatch(h -> h.getObj() instanceof FirePlant)) {
      System.out.println("step 200");
      IntStream.range(0, 200).forEach($ -> controller.oneStep());
      Assert.assertEquals(controller.getHexagons().filter(h -> h.getObj() instanceof Maldar).count(), 18);
    }
  }
}
