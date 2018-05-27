package avividi.com.monuments.gui.util;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AssetUtil {

  public static String location = "graphics/";

  public static List<String> assets = ImmutableList.<String>builder()
      .add("grounddirt.png")
      .add("grounddirt2.png")
      .add("reddirt.png")
      .add("alienground.png")
      .add("sandground.png")

      .add("fireplant.png")
      .add("fireplant-dead.png")
      .add("livefireplant.png")
      .add("livefireplant-young.png")
      .add("livefireplant-stripped.png")
      .add("driedPlantItem/driedPlantItem-striver.png")
      .add("driedPlantItem/driedPlantItem.png")
      .add("driedPlantItem/driedPlantItem-plot-1.png")
      .add("driedPlantItem/driedPlantItem-plot-2.png")
      .add("driedPlantItem/driedPlantItem-plot-3.png")
      .add("driedPlantItem/driedPlantItem-plot-4.png")

      .add("leafFireplant/leafFireplant-striver.png")
      .add("leafFireplant/leafFireplant.png")
      .add("leafFireplant/leafFireplant-plot-1.png")
      .add("leafFireplant/leafFireplant-plot-2.png")
      .add("leafFireplant/leafFireplant-plot-3.png")
      .add("leafFireplant/leafFireplant-plot-4.png")

      .add("boulder/boulder.png")
      .add("boulder/boulder-striver.png")
      .add("boulder/boulder-plot-1.png")
      .add("boulder/boulder-plot-2.png")
      .add("boulder/boulder-plot-3.png")
      .add("boulder/boulder-plot-4.png")

      .add("fire-no.png")
      .add("firelow1.png")
      .add("firelow2.png")
      .add("fire1.png")
      .add("fire2.png")
      .add("brick.png")
      .add("striver.png")
      .add("striverhammer.png")
      .add("striver-dead1.png")
      .add("striver-dead2.png")
      .add("sentinel.png")
      .add("rivskin.png")
      .add("rivskinmett.png")
      .add("bloodpool.png")

      .add("cliff1/cliff1-full.png")
      .add("cliff1/cliff1-se-ne-dw.png")
      .add("cliff1/cliff1-sw-e-dnw.png")
      .add("cliff1/cliff1-sw-ne-dnw.png")
      .add("cliff1/cliff1-sw-nw-dw.png")
      .add("cliff1/cliff1-w-e-dn.png")
      .add("cliff1/cliff1-w-ne-dnw.png")

      .add("cliff2/cliff2-full.png")
      .add("cliff2/cliff2-se-ne-dw.png")
      .add("cliff2/cliff2-sw-e-dnw.png")
      .add("cliff2/cliff2-sw-ne-dnw.png")
      .add("cliff2/cliff2-sw-nw-dw.png")
      .add("cliff2/cliff2-w-e-dn.png")
      .add("cliff2/cliff2-w-ne-dnw.png")

      .add("cliff-background-1/cliff-background-1-full.png")
      .add("cliff-background-1/cliff-background-1-se-ne-dw.png")
      .add("cliff-background-1/cliff-background-1-sw-e-dnw.png")
      .add("cliff-background-1/cliff-background-1-sw-ne-dnw.png")
      .add("cliff-background-1/cliff-background-1-sw-nw-dw.png")
      .add("cliff-background-1/cliff-background-1-w-e-dn.png")
      .add("cliff-background-1/cliff-background-1-w-ne-dnw.png")

      .add("wall3/wall3-full.png")
      .add("wall3/wall3-ne-se.png")
      .add("wall3/wall3-nw-e.png")
      .add("wall3/wall3-nw-se.png")
      .add("wall3/wall3-nw-sw.png")
      .add("wall3/wall3-sw-e.png")
      .add("wall3/wall3-sw-ne.png")
      .add("wall3/wall3-w-e.png")
      .add("wall3/wall3-w-ne.png")
      .add("wall3/wall3-w-se.png")

      .add("wall3-sublevel/wall3-sublevel-full.png")
      .add("wall3-sublevel/wall3-sublevel-ne-se.png")
      .add("wall3-sublevel/wall3-sublevel-nw-e.png")
      .add("wall3-sublevel/wall3-sublevel-nw-se.png")
      .add("wall3-sublevel/wall3-sublevel-nw-sw.png")
      .add("wall3-sublevel/wall3-sublevel-sw-e.png")
      .add("wall3-sublevel/wall3-sublevel-sw-ne.png")
      .add("wall3-sublevel/wall3-sublevel-w-e.png")
      .add("wall3-sublevel/wall3-sublevel-w-ne.png")
      .add("wall3-sublevel/wall3-sublevel-w-se.png")

      .add("marker/marker-yellow.png")
      .add("marker/marker-yellow-lower.png")
      .add("marker/marker-red.png")
      .add("marker/marker-red-lower.png")
      .add("marker/marker-blue.png")
      .add("marker/marker-blue-lower.png")
      .add("marker/marker-green.png")
      .add("marker/marker-green-lower.png")

      .add("plot.png")
      .add("build-marker.png")

      .add("scaffolding/scaffolding-full.png")
      .add("scaffolding/scaffolding-bottom.png")
      .add("scaffolding/scaffolding-ladder.png")


      .add("rough-wall.png")
      .add("rough-floor.png")
      .add("outliner.png")
      .build();


  public static Map<String, String> getAll() {
    return assets.stream()
        .collect(Collectors.toMap(a -> StringUtils.substringBeforeLast(a, "."),
            a -> location + a));
  }

  public static ByteBuffer loadAsset(String path) {
    try {
      return IOUtil.ioResourceToByteBuffer(path, 1024);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
