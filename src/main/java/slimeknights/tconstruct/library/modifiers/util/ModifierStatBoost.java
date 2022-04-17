package slimeknights.tconstruct.library.modifiers.util;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Stat boost to apply
 */
public interface ModifierStatBoost {
  /**
   * Checks if all tags match
   */
  default boolean matchesTags(ToolRebuildContext context) {
    for (TagKey<Item> key : tagRequirements()) {
      if (!context.hasTag(key)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Gets all tag requirements
   * TODO: general tag compound logic? to allow AND, OR, NOT, and alike
   */
  List<TagKey<Item>> tagRequirements();

  /**
   * Applies the given boost
   */
  void apply(ToolRebuildContext context, float level, ModifierStatsBuilder builder);

  /**
   * Converts this to JSON
   */
  JsonObject toJson();

  /**
   * Writes this to the network
   */
  void toNetwork(FriendlyByteBuf buffer);

  /**
   * Parses the object from JSON
   */
  static ModifierStatBoost fromJson(JsonObject json) {
    IToolStat<?> stat = ToolStats.fromJson(GsonHelper.getAsString(json, "stat"));
    List<TagKey<Item>> tagRequirements = Collections.emptyList();
    if (json.has("tags")) {
      tagRequirements = JsonHelper.parseList(json, "tags", (element, name) -> TagKey.create(Registry.ITEM_REGISTRY, JsonHelper.convertToResourceLocation(element, name)));
    }
    if (stat instanceof INumericToolStat<?> numeric) {
      return StatBoost.fromJson(json, numeric, tagRequirements);
    }
    return StatUpdate.fromJson(json, stat, tagRequirements);
  }

  /**
   * Reads this to the network
   */
  static ModifierStatBoost fromNetwork(FriendlyByteBuf buffer) {
    IToolStat<?> stat = ToolStats.fromNetwork(buffer);
    ImmutableList.Builder<TagKey<Item>> tagRequirements = ImmutableList.builder();
    int size = buffer.readVarInt();
    for (int i = 0; i < size; i++) {
      tagRequirements.add(TagKey.create(Registry.ITEM_REGISTRY, buffer.readResourceLocation()));
    }
    if (stat instanceof INumericToolStat<?> numeric) {
      return StatBoost.fromNetwork(buffer, numeric, tagRequirements.build());
    }
    return StatUpdate.fromNetwork(buffer, stat, tagRequirements.build());
  }

  /** Record representing a single stat boost */
  enum BoostType {
    ADD {
      @Override
      public void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value, float level) {
        stat.add(builder, value * level);
      }
    },
    MULTIPLY_BASE {
      @Override
      public void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value, float level) {
        stat.multiply(builder, 1 + (value * level));
      }
    },
    MULTIPLY_CONDITIONAL {
      @Override
      public void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value, float level) {
        builder.multiplier(stat, 1 + (value * level));
      }
    },
    MULTIPLY_ALL {
      @Override
      public void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value, float level) {
        stat.multiplyAll(builder, 1 + (value * level));
      }
    };

    @Getter
    private final String name = name().toLowerCase(Locale.ROOT);

    /** Applies this boost type for the given values */
    public abstract void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value, float level);

    /** Gets the boost type for the given name */
    @Nullable
    public static BoostType byName(String name) {
      for (BoostType type : BoostType.values()) {
        if (type.getName().equals(name)) {
          return type;
        }
      }
      return null;
    }
  }

  /** Writes the tag keys to JSON */
  private static void serializeTags(JsonObject json, List<TagKey<Item>> tagRequirements) {
    if (!tagRequirements.isEmpty()) {
      JsonArray array = new JsonArray();
      for (TagKey<Item> tag : tagRequirements) {
        array.add(tag.location().toString());
      }
      json.add("tags", array);
    }
  }

  /** Record representing a single stat boost */
  record StatBoost(INumericToolStat<?> stat, BoostType type, float amount, List<TagKey<Item>> tagRequirements) implements ModifierStatBoost {
    /** Applies the given boost */
    @Override
    public void apply(ToolRebuildContext context, float level, ModifierStatsBuilder builder) {
      if (matchesTags(context)) {
        type.apply(builder, stat, amount, level);
      }
    }

    /** Converts the object to JSON */
    @Override
    public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("stat", stat.getName().toString());
      json.addProperty("type", type.getName());
      json.addProperty("value", amount);
      serializeTags(json, tagRequirements);
      return json;
    }

    /** Parses this from JSON */
    public static StatBoost fromJson(JsonObject json, INumericToolStat<?> stat, List<TagKey<Item>> tagRequirements) {
      String typeName = GsonHelper.getAsString(json, "type", "add");
      BoostType boostType = BoostType.byName(typeName);
      if (boostType == null) {
        throw new JsonSyntaxException("Unknown stat type '" + typeName + "'");
      }
      float amount = GsonHelper.getAsFloat(json, "value");
      return new StatBoost(stat, boostType, amount, tagRequirements);
    }

    /** Writes this to the network */
    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
      buffer.writeUtf(stat.getName().toString());
      buffer.writeVarInt(tagRequirements.size());
      for (TagKey<Item> key : tagRequirements) {
        buffer.writeResourceLocation(key.location());
      }
      buffer.writeEnum(type);
      buffer.writeFloat(amount);
    }

    /** Reads the stat boost from the network */
    public static StatBoost fromNetwork(FriendlyByteBuf buffer, INumericToolStat<?> stat, List<TagKey<Item>> tagRequirement) {
      BoostType type = buffer.readEnum(BoostType.class);
      float amount = buffer.readFloat();
      return new StatBoost(stat, type, amount, tagRequirement);
    }
  }

  /** Performs a generic stat update */
  record StatUpdate<T>(IToolStat<T> stat, T value, List<TagKey<Item>> tagRequirements) implements ModifierStatBoost {
    @Override
    public void apply(ToolRebuildContext context, float level, ModifierStatsBuilder builder) {
      stat.update(builder, value);
    }

    @Override
    public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("stat", stat.getName().toString());
      json.add("value", stat.serialize(value));
      serializeTags(json, tagRequirements);
      return json;
    }

    /** Parses the stat update from JSON */
    public static <T> StatUpdate<T> fromJson(JsonObject json, IToolStat<T> stat, List<TagKey<Item>> tagRequirements) {
      T value = stat.deserialize(JsonHelper.getElement(json, "value"));
      return new StatUpdate<>(stat, value, tagRequirements);
    }

    /** Writes this to the network */
    @Override
    public void toNetwork(FriendlyByteBuf buffer) {
      buffer.writeUtf(stat.getName().toString());
      buffer.writeVarInt(tagRequirements.size());
      for (TagKey<Item> tag : tagRequirements) {
        buffer.writeResourceLocation(tag.location());
      }
      stat.toNetwork(buffer, value);
    }

    /** Reads this from the network */
    public static <T> StatUpdate<T> fromNetwork(FriendlyByteBuf buffer, IToolStat<T> stat, List<TagKey<Item>> tagRequirements) {
      T value = stat.fromNetwork(buffer);
      return new StatUpdate<>(stat, value, tagRequirements);
    }
  }
}
