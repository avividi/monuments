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

      .add("fireplant.png")
      .add("livefireplant.png")
      .add("driedPlantItem/driedPlantItem-striver.png")
      .add("driedPlantItem/driedPlantItem.png")
      .add("driedPlantItem/driedPlantItem-plot-1.png")
      .add("driedPlantItem/driedPlantItem-plot-2.png")
      .add("driedPlantItem/driedPlantItem-plot-3.png")
      .add("driedPlantItem/driedPlantItem-plot-4.png")

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

      .add("wall2/wall2-full.png")
      .add("wall2/wall2-se-ne-dw.png")
      .add("wall2/wall2-sw-e-dnw.png")
      .add("wall2/wall2-sw-ne-dnw.png")
      .add("wall2/wall2-sw-nw-dw.png")
      .add("wall2/wall2-w-e-dn.png")
      .add("wall2/wall2-w-ne-dnw.png")

      .add("wall1/wall1-full.png")
      .add("wall1/wall1-se-ne-dw.png")
      .add("wall1/wall1-sw-e-dnw.png")
      .add("wall1/wall1-sw-ne-dnw.png")
      .add("wall1/wall1-sw-nw-dw.png")
      .add("wall1/wall1-w-e-dn.png")
      .add("wall1/wall1-w-ne-dnw.png")


      .add("marker/marker-yellow.png")
      .add("marker/marker-red.png")
      .add("marker/marker-blue.png")
      .add("marker/marker-green.png")

      .add("plot.png")
      .add("build-marker.png")

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
