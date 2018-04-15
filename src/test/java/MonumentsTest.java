import avividi.com.controller.Controller;
import avividi.com.controller.GameController;
import avividi.com.controller.gameitems.other.FirePlant;
import avividi.com.controller.gameitems.unit.Maldar;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.IntStream;

public class MonumentsTest {

  @Test
  public void crowded() {

    Controller controller = new GameController("/maps/crowded.json");

    Assert.assertEquals(controller.getHexagons().filter(h -> h.getObj() instanceof Maldar).count(), 19);

    while (controller.getHexagons().anyMatch(h -> h.getObj() instanceof FirePlant)) {
      System.out.println("step 200");
      IntStream.range(0, 200).forEach($ -> controller.oneStep());
      Assert.assertEquals(controller.getHexagons().filter(h -> h.getObj() instanceof Maldar).count(), 19);
    }
  }
}
