package avividi.com.monuments.loader;

import avividi.com.monuments.controller.Board;
import avividi.com.monuments.controller.gamehex.GameHex;
import avividi.com.monuments.controller.gamehex.Interactor;
import avividi.com.monuments.controller.gamehex.unit.Unit;
import avividi.com.monuments.hexgeometry.Grid;
import avividi.com.monuments.generic.ReflectBuilder;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class JsonMapLoader implements Supplier<Board> {

  private String packageName =  "avividi.com.monuments.controller";

  private final ObjectMapper objectMapper = new ObjectMapper().enable(JsonParser.Feature.ALLOW_COMMENTS);
  private final String url;

  public JsonMapLoader(String url) {
    this.url = url;
  }

  @Override
  public Board get() {
    ObjectNode objectNode = getJson(url);

    Grid<GameHex> ground = handleMap((ObjectNode) objectNode.get("ground"));
    Grid<Interactor> other = handleMap((ObjectNode) objectNode.get("other"));
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

  private <T extends GameHex> Grid<T> handleMap(ObjectNode surfaces) {
    ObjectNode groundSymbols = (ObjectNode) surfaces.get("symbols");
    Map<Character, Supplier<T>> charToGround = new HashMap<>();

    groundSymbols.fields().forEachRemaining(symbol -> {
      if (symbol.getValue().has("class")) {
        charToGround.put(symbol.getKey().charAt(0),
            () -> new ReflectBuilder<T>(getClassName(symbol.getValue()))
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

  private String getClassName(JsonNode textNode) {
    String name = textNode.get("class").asText();
    if (name.startsWith("${default}")) {
      name = packageName + name.substring(10);
    }
    return name;
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