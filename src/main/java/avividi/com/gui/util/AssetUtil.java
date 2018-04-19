package avividi.com.gui.util;

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
      .add("fireplant.png")
      .add("boulder.png")
      .add("fire-no.png")
      .add("firelow1.png")
      .add("firelow2.png")
      .add("fire1.png")
      .add("fire2.png")
      .add("brick.png")
      .add("striver.png")
      .add("striverfireplant.png")
      .add("striverboulder.png")
      .add("striverchisler.png")
      .add("striverhammer.png")
      .add("rivskin.png")
      .add("rivskinmett.png")
      .add("bloodpool.png")
      .add("cliff-full.png")
      .add("cliff-up.png")
      .add("cliff-ne.png")
      .add("cliff-corner.png")
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
