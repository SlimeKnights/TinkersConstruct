package slimeknights.tconstruct.library.json.predicate.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Predicate matching a block with the given properties
 * @param block       Block to match
 * @param properties  Properties to match
 */
public record BlockPropertiesPredicate(Block block, List<Matcher> properties) implements BlockPredicate {
  private static final Function<String,RuntimeException> JSON_EXCEPTION = JsonSyntaxException::new;
  private static final Function<String,RuntimeException> DECODER_EXCEPTION = DecoderException::new;

  @Override
  public boolean matches(BlockState input) {
    if (input.getBlock() != block) {
      return false;
    }
    for (Matcher matcher : properties) {
      if (!matcher.matches(input)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<BlockState>> getLoader() {
    return LOADER;
  }

  /** Parses a property from the given state definition */
  private static Property<?> parseProperty(Block block, String name, Function<String, RuntimeException> exception) {
    Property<?> property = block.getStateDefinition().getProperty(name);
    if (property == null) {
      throw exception.apply("Property " + name + " does not exist in block " + block.getRegistryName());
    }
    return property;
  }

  public static final IGenericLoader<BlockPropertiesPredicate> LOADER = new IGenericLoader<>() {
    @Override
    public BlockPropertiesPredicate deserialize(JsonObject json) {
      Block block = JsonHelper.getAsEntry(ForgeRegistries.BLOCKS, json, "block");
      ImmutableList.Builder<Matcher> builder = ImmutableList.builder();
      for (Entry<String, JsonElement> entry : GsonHelper.getAsJsonObject(json, "properties").entrySet()) {
        Property<?> property = parseProperty(block, entry.getKey(), JSON_EXCEPTION);
        builder.add(Matcher.deserialize(property, entry.getValue()));
      }
      return new BlockPropertiesPredicate(block, builder.build());
    }

    @Override
    public void serialize(BlockPropertiesPredicate object, JsonObject json) {
      json.addProperty("block", Objects.requireNonNull(object.block.getRegistryName()).toString());
      JsonObject properties = new JsonObject();
      for (Matcher matcher : object.properties) {
        properties.add(matcher.property().getName(), matcher.serialize());
      }
      json.add("properties", properties);
    }

    @Override
    public BlockPropertiesPredicate fromNetwork(FriendlyByteBuf buffer) {
      Block block = buffer.readRegistryIdUnsafe(ForgeRegistries.BLOCKS);
      int size = buffer.readVarInt();
      ImmutableList.Builder<Matcher> builder = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        builder.add(Matcher.fromNetwork(block, buffer));
      }
      return new BlockPropertiesPredicate(block, builder.build());
    }

    @Override
    public void toNetwork(BlockPropertiesPredicate object, FriendlyByteBuf buffer) {
      buffer.writeRegistryIdUnsafe(ForgeRegistries.BLOCKS, object.block);
      buffer.writeVarInt(object.properties.size());
      for (Matcher matcher : object.properties) {
        matcher.toNetwork(buffer);
      }
    }
  };

  /** Interface of nested matcher classes */
  public sealed interface Matcher {
    /** Returns true if the given block matches the given property */
    boolean matches(BlockState state);

    /** Gets the property for this matcher */
    Property<?> property();

    /** Serializes the match to a json element */
    JsonElement serialize();

    /** Writes this to the network */
    void toNetwork(FriendlyByteBuf buffer);

    /** Deserializes the value from JSON */
    private static <T extends Comparable<T>> T parseValue(Property<T> property, String name, Function<String, RuntimeException> exception) {
      Optional<T> value = property.getValue(name);
      if (value.isPresent()) {
        return value.get();
      }
      throw exception.apply("Unknown property value " + name);
    }

    /**
     * Deserializes the property from JSON
     * @param element   Json to deserialize
     * @param property  Property to use
     * @param <T>  Property type
     * @return  Matcher instance
     */
    static <T extends Comparable<T>> Matcher deserialize(Property<T> property, JsonElement element) {
      // if a value type, exact match. To reduce code, just use the set matcher with size 1
      if (element.isJsonPrimitive()) {
        return new SetMatcher<>(property, parseValue(property, GsonHelper.convertToString(element, property.getName()), JSON_EXCEPTION));
      }
      // if an array, set match
      if (element.isJsonArray()) {
        return new SetMatcher<>(property, ImmutableSet.copyOf(JsonHelper.parseList(
          element.getAsJsonArray(), property.getName(),(e, key) -> parseValue(property, GsonHelper.convertToString(e, key), JSON_EXCEPTION)))
        );
      }
      // object means range match
      if (element.isJsonObject()) {
        JsonObject json = element.getAsJsonObject();
        T min = null;
        T max = null;
        if (json.has("min")) {
          min = parseValue(property, GsonHelper.getAsString(json, "min"), JSON_EXCEPTION);
        }
        if (json.has("max")) {
          max = parseValue(property, GsonHelper.getAsString(json, "max"), JSON_EXCEPTION);
        }
        if (min == null) {
          if (max == null) {
            throw new JsonSyntaxException("Either min or max must be set for a range matcher");
          }
        } else if (min.equals(max)) {
          // if equal, might as well use set matcher to save effort
          return new SetMatcher<>(property, min);
        }
        return new RangeMatcher<>(property, min, max);
      }
      throw new JsonSyntaxException("Invalid matcher type " + GsonHelper.getType(element));
    }

    /**
     * Parses a matcher from the buffer
     * @param block   Block to search for the property
     * @param buffer  Buffer instance before reading property name
     * @return  Matcher instance
     */
    static Matcher fromNetwork(Block block, FriendlyByteBuf buffer) {
      Property<?> property = parseProperty(block, buffer.readUtf(Short.MAX_VALUE), DECODER_EXCEPTION);
      return fromNetwork(property, buffer);
    }

    /**
     * Parses a matcher from the buffer
     * @param property   Matcher property
     * @param buffer     Buffer instance after reading property name
     * @return  Matcher instance
     */
    static <T extends Comparable<T>> Matcher fromNetwork(Property<T> property, FriendlyByteBuf buffer) {
      int size = buffer.readVarInt();
      // 0 means range match
      if (size == 0) {
        T min = null;
        T max = null;
        RangeType rangeType = buffer.readEnum(RangeType.class);
        if (rangeType != RangeType.MAX) {
          min = parseValue(property, buffer.readUtf(Short.MAX_VALUE), DECODER_EXCEPTION);
        }
        if (rangeType != RangeType.MIN) {
          max = parseValue(property, buffer.readUtf(Short.MAX_VALUE), DECODER_EXCEPTION);
        }
        return new RangeMatcher<>(property, min, max);
      } else {
        ImmutableSet.Builder<T> builder = ImmutableSet.builder();
        for (int i = 0; i < size; i++) {
          builder.add(parseValue(property, buffer.readUtf(Short.MAX_VALUE), DECODER_EXCEPTION));
        }
        return new SetMatcher<>(property, builder.build());
      }
    }
  }

  /**
   * Matches on a value being in a set.
   * @param property  Property to match
   * @param values    Set of values, must not be empty
   */
  public record SetMatcher<T extends Comparable<T>>(Property<T> property, Set<T> values) implements Matcher {
    public SetMatcher {
      if (values.isEmpty()) {
        throw new IllegalArgumentException("Values must not be empty");
      }
    }

    public SetMatcher(Property<T> property, T value) {
      this(property, Set.of(value));
    }

    @Override
    public boolean matches(BlockState state) {
      return values.contains(state.getValue(property));
    }

    @Override
    public JsonElement serialize() {
      // if only a single element, simplify serialization
      if (values.size() == 1) {
        return new JsonPrimitive(property.getName(values.iterator().next()));
      }
      // if more than 1 element, store in an array
      JsonArray array = new JsonArray();
      for (T value : values) {
        array.add(property.getName(value));
      }
      return array;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
      buffer.writeUtf(property.getName());
      // size of 0 represents range matcher, size above 0 means set matcher
      buffer.writeVarInt(values.size());
      // only way we know to sync the property and values is as strings, inefficient but mojang never made properties buffer friendly
      for (T value : values) {
        buffer.writeUtf(property.getName(value));
      }
    }
  }

  /** Helper to deal with the range matcher network serializing */
  private enum RangeType {
    FULL, MIN, MAX;

    /** Creates a ranged type from the given values, doubles as a validator for the arguments */
    public static RangeType fromValues(@Nullable Object min, @Nullable Object max) {
      if (max == null) {
        if (min == null) {
          throw new IllegalArgumentException("Cannot have both min and max null");
        }
        return MIN;
      } else if (min == null) {
        return MAX;
      } else {
        return FULL;
      }
    }
  }

  /** Matches on a range of values */
  public record RangeMatcher<T extends Comparable<T>>(Property<T> property, @Nullable T min, @Nullable T max) implements Matcher {
    public RangeMatcher {
      RangeType.fromValues(min, max);
    }

    @Override
    public boolean matches(BlockState state) {
      T value = state.getValue(property);
      // null means ignore that part of the range
      return (min == null || value.compareTo(min) >= 0) && (max == null || value.compareTo(max) <= 0);
    }

    @Override
    public JsonElement serialize() {
      JsonObject json = new JsonObject();
      if (min != null) {
        json.addProperty("min", property.getName(min));
      }
      if (max != null) {
        json.addProperty("max", property.getName(max));
      }
      return json;
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
      buffer.writeUtf(property.getName());
      // 0 means it' a range match, anything above 0 is the set size
      buffer.writeVarInt(0);
      buffer.writeEnum(RangeType.fromValues(min, max));
      if (min != null) {
        buffer.writeUtf(property.getName(min));
      }
      if (max != null) {
        buffer.writeUtf(property.getName(max));
      }
    }
  }

  /** Creates a builder instance */
  public static Builder block(Block block) {
    return new Builder(block);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private final Block block;
    private final Map<Property<?>, Matcher> matchers = new LinkedHashMap<>();

    /** Adds a matcher to the builder */
    private Builder matches(Matcher matcher) {
      Property<?> property = matcher.property();
      // validate the property is part of the block
      if (!block.getStateDefinition().getProperties().contains(property)) {
        throw new IllegalArgumentException("Property " + property + " does not exist in block " + block);
      }
      // validate we don't have the same property twice, messes with JSON serialization
      Matcher original = this.matchers.put(property, matcher);
      if (original != null) {
        throw new IllegalArgumentException("Matcher for property already exists: previous matcher " + original);
      }
      return this;
    }


    /* Set match */

    /** Matches on the given set values */
    public <T extends Comparable<T>> Builder matches(Property<T> property, Set<T> values) {
      return matches(new SetMatcher<>(property, values));
    }

    /** Matches on the given set values */
    @SafeVarargs
    public final <T extends Comparable<T>> Builder matches(Property<T> property, T... values) {
      return matches(property, Set.of(values));
    }


    /* Range match */

    /** Matches values between min and max (inclusive) */
    public <T extends Comparable<T>> Builder range(Property<T> property, T min, T max) {
      if (Objects.equals(min, max)) {
        return matches(property, min);
      }
      return matches(new RangeMatcher<>(property, min, max));
    }

    /** Matches values greater than or equal to min */
    public <T extends Comparable<T>> Builder min(Property<T> property, T min) {
      return matches(new RangeMatcher<>(property, min, null));
    }

    /** Matches values less than or equal to max */
    public <T extends Comparable<T>> Builder max(Property<T> property, T max) {
      return matches(new RangeMatcher<>(property, null, max));
    }

    /** Builds the final instance */
    public BlockPropertiesPredicate build() {
      if (matchers.isEmpty()) {
        throw new IllegalArgumentException("Must have at least one property");
      }
      return new BlockPropertiesPredicate(block, ImmutableList.copyOf(matchers.values()));
    }
  }
}
