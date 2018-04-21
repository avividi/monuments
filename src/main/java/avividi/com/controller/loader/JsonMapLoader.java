package avividi.com.controller.loader;

import avividi.com.controller.Board;
import avividi.com.controller.HexItem;
import avividi.com.controller.gameitems.GameItem;
import avividi.com.controller.gameitems.InteractingItem;
import avividi.com.controller.gameitems.staticitems.CustomStaticItem;
import avividi.com.controller.gameitems.unit.Unit;
import avividi.com.controller.hexgeometry.Grid;
import avividi.com.generic.ReflectBuilder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class JsonMapLoader implements Supplier<Board> {

  private final ObjectMapper objectMapper = new ObjectMapper().enable(JsonParser.Feature.ALLOW_COMMENTS);
  private final String url;

  public JsonMapLoader(String url) {
    this.url = url;
  }

  @Override
  public Board get() {
    ObjectNode objectNode = getJson(url);

    Grid<GameItem> ground = handleMap((ObjectNode) objectNode.get("ground"));
    Grid<InteractingItem> other = handleMap((ObjectNode) objectNode.get("other"));
    Grid<Unit> units = handleMap((ObjectNode) objectNode.get("units"));
    return new Board(ground, other, units);
  }

  private ObjectNode getJson (String url) {
    try {
      return (ObjectNode) objectMapper.readTree(JsonMapLoader.class.getResource(url));
    }
    catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private <T extends GameItem> Grid<T> handleMap(ObjectNode surfaces) {
    ObjectNode groundSymbols = (ObjectNode) surfaces.get("symbols");
    Map<Character, Supplier<T>> charToGround = new HashMap<>();

    groundSymbols.fields().forEachRemaining(symbol -> {
      if (symbol.getValue().has("class")) {
        charToGround.put(symbol.getKey().charAt(0),
            () -> new ReflectBuilder<T>(symbol.getValue().get("class").asText())
                .withConstructorParamClasses(ObjectNode.class)
                .withConstructorParams(symbol.getValue())
                .get());
      }
      else {
        throw new IllegalStateException();
      }
    });
     String groundMap = buildInput((ArrayNode) surfaces.get("map"));
     return new Grid<>(groundMap, charToGround);
  }


  private String buildInput(ArrayNode map) {
    StringBuilder stringBuilder = new StringBuilder();
    map.forEach(line -> {
      stringBuilder.append(line.asText());
      stringBuilder.append("\n");
    });
    return stringBuilder.toString();
  }
}
