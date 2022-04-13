package slimeknights.tconstruct.library.modifiers.dynamic;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.util.IStatBoost;
import slimeknights.tconstruct.library.modifiers.util.IStatBoost.BoostType;
import slimeknights.tconstruct.library.modifiers.util.IStatBoost.StatBoost;
import slimeknights.tconstruct.library.modifiers.util.IStatBoost.StatUpdate;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Modifier that applies generic stat boosts
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class StatBoostModifier extends IncrementalModifier {
  /** List of boosts to apply */
  private final List<IStatBoost> stats;
  /** Rarity to display */
  @Nullable
  private final Rarity rarity;
  /** List of flags to set */
  private final List<ResourceLocation> flags;

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    if (rarity != null) {
      IModifiable.setRarity(volatileData, rarity);
    }
    for (ResourceLocation flag : flags) {
      volatileData.putBoolean(flag, true);
    }
  }

  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    float scaledLevel = getScaledLevel(context, level);
    for (IStatBoost boost : stats) {
      boost.apply(context, scaledLevel, builder);
    }
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  /** Gets a rarity from a string */
  @Nullable
  private static Rarity getRarity(String name) {
    for (Rarity rarity : Rarity.values()) {
      if (rarity.name().toLowerCase(Locale.ROOT).equals(name)) {
        return rarity;
      }
    }
    return null;
  }

  /** Loader for this modifier */
  public static final IGenericLoader<StatBoostModifier> LOADER = new IGenericLoader<>() {
    @Override
    public StatBoostModifier deserialize(JsonObject json) {
      List<IStatBoost> stats = Collections.emptyList();
      if (json.has("stats")) {
        stats = JsonHelper.parseList(json, "stats", IStatBoost::fromJson);
      }
      Rarity rarity = null;
      if (json.has("rarity")) {
        String rarityName = GsonHelper.getAsString(json, "rarity");
        rarity = getRarity(rarityName);
        if (rarity == null) {
          throw new JsonSyntaxException("Unknown rarity '" + rarityName + "'");
        }
      }
      List<ResourceLocation> flags = Collections.emptyList();
      if (json.has("flags")) {
        flags = JsonHelper.parseList(json, "flags", JsonHelper::convertToResourceLocation);
      }
      return new StatBoostModifier(stats, rarity, flags);
    }

    @Override
    public void serialize(StatBoostModifier object, JsonObject json) {
      if (object.rarity != null) {
        json.addProperty("rarity", object.rarity.name().toLowerCase(Locale.ROOT));
      }
      if (!object.stats.isEmpty()) {
        JsonArray stats = new JsonArray();
        for (IStatBoost boost : object.stats) {
          stats.add(boost.toJson());
        }
        json.add("stats", stats);
      }
      if (!object.flags.isEmpty()) {
        JsonArray stats = new JsonArray();
        for (ResourceLocation flag : object.flags) {
          stats.add(flag.toString());
        }
        json.add("flags", stats);
      }
    }

    @Override
    public StatBoostModifier fromNetwork(FriendlyByteBuf buffer) {
      int size = buffer.readVarInt();
      ImmutableList.Builder<IStatBoost> stats = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        stats.add(IStatBoost.fromNetwork(buffer));
      }
      Rarity rarity = null;
      if (buffer.readBoolean()) {
        rarity = buffer.readEnum(Rarity.class);
      }
      size = buffer.readVarInt();
      ImmutableList.Builder<ResourceLocation> flags = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        flags.add(buffer.readResourceLocation());
      }
      return new StatBoostModifier(stats.build(), rarity, flags.build());
    }

    @Override
    public void toNetwork(StatBoostModifier object, FriendlyByteBuf buffer) {
      buffer.writeVarInt(object.stats.size());
      for (IStatBoost boost : object.stats) {
        boost.toNetwork(buffer);
      }
      if (object.rarity != null) {
        buffer.writeBoolean(true);
        buffer.writeEnum(object.rarity);
      } else {
        buffer.writeBoolean(false);
      }
      buffer.writeVarInt(object.flags.size());
      for (ResourceLocation flag : object.flags) {
        buffer.writeResourceLocation(flag);
      }
    }
  };

  /** Builder for a stat boost modifier */
  @Accessors(fluent = true)
  public static class Builder {
    /** List of all boosts to apply */
    private final ImmutableList.Builder<IStatBoost> boosts = ImmutableList.builder();
    /** List of flags to set */
    private final ImmutableList.Builder<ResourceLocation> flags = ImmutableList.builder();
    /** Rarity for the tool to show */
    @Setter
    private Rarity rarity = null;

    /**
     * Updates a stat in the builder
     */
    @SafeVarargs
    public final <T> Builder update(IToolStat<T> stat, T value, TagKey<Item>... tagRequirements) {
      boosts.add(new StatUpdate<>(stat, value, List.of(tagRequirements)));
      return this;
    }

    /**
     * Adds a general boost
     */
    private Builder boost(INumericToolStat<?> stat, BoostType type, float amount, TagKey<Item>[] tagRequirements) {
      boosts.add(new StatBoost(stat, type, amount, List.of(tagRequirements)));
      return this;
    }

    /**
     * Adds a numeric boost
     */
    @SafeVarargs
    public final Builder add(INumericToolStat<?> stat, float amount, TagKey<Item>... tagRequirements) {
      return boost(stat, IStatBoost.BoostType.ADD, amount, tagRequirements);
    }

    /**
     * Multiplies the base value of a stat
     */
    @SafeVarargs
    public final Builder multiplyBase(INumericToolStat<?> stat, float amount, TagKey<Item>... tagRequirements) {
      return boost(stat, IStatBoost.BoostType.MULTIPLY_BASE, amount, tagRequirements);
    }

    /**
     * Multiplies conditional boosts
     */
    @SafeVarargs
    public final Builder multiplyConditional(INumericToolStat<?> stat, float amount, TagKey<Item>... tagRequirements) {
      return boost(stat, IStatBoost.BoostType.MULTIPLY_CONDITIONAL, amount, tagRequirements);
    }

    /**
     * Multiplies both base and conditional boosts
     */
    @SafeVarargs
    public final Builder multiplyAll(INumericToolStat<?> stat, float amount, TagKey<Item>... tagRequirements) {
      return boost(stat, IStatBoost.BoostType.MULTIPLY_ALL, amount, tagRequirements);
    }

    /** Adds the given flag to the builder */
    public Builder addFlag(ResourceLocation flag) {
      this.flags.add(flag);
      return this;
    }

    /** Builds the final modifier */
    public StatBoostModifier build() {
      return new StatBoostModifier(boosts.build(), rarity, flags.build());
    }
  }
}
