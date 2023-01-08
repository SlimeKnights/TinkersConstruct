package slimeknights.tconstruct.library.modifiers.dynamic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ToolAction;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierAttribute;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.modifiers.util.ModifierStatBoost;
import slimeknights.tconstruct.library.modifiers.util.ModifierStatBoost.BoostType;
import slimeknights.tconstruct.library.modifiers.util.ModifierStatBoost.StatBoost;
import slimeknights.tconstruct.library.modifiers.util.ModifierStatBoost.StatUpdate;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Modifier that applies generic stat boosts
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class StatBoostModifier extends IncrementalModifier {
  /** Rarity to display */
  @Nullable
  private final Rarity rarity;
  /** List of boosts to apply */
  private final List<ModifierStatBoost> stats;
  /** List of attribute modifiers to apply */
  private final List<ModifierAttribute> attributes;
  /** List of flags to set */
  private final List<ResourceLocation> flags;
  /** Way to display each level of the modifier */
  private final ModifierLevelDisplay levelDisplay;
  /** Determines if this modifier displays */
  private final ModifierDisplay modifierDisplay;
  /** List of extra tool actions this tool can perform */
  private final Set<ToolAction> actions;

  /** Creates a new builder instance */
  public static Builder builder() {
    return new Builder();
  }

  @Override
  public Component getDisplayName(int level) {
    return levelDisplay.nameForLevel(this, level);
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
    for (ModifierStatBoost boost : stats) {
      boost.apply(context, scaledLevel, builder);
    }
  }

  @Override
  public void addAttributes(IToolStackView tool, int level, EquipmentSlot slot, BiConsumer<Attribute,AttributeModifier> consumer) {
    float scaledLevel = getScaledLevel(tool, level);
    for (ModifierAttribute attribute : attributes) {
      attribute.apply(tool, scaledLevel, slot, consumer);
    }
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, int level, ToolAction toolAction) {
    return actions.contains(toolAction);
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return modifierDisplay.shouldDisplay(advanced);
  }

  /** Determines if the modifier should display */
  @RequiredArgsConstructor
  public enum ModifierDisplay {
    ALWAYS(true, true),
    TABLE(true, false),
    NEVER(false, false);

    private final boolean normal;
    private final boolean advanced;
    @Getter
    private final String name = name().toLowerCase(Locale.ROOT);

    /** Determines if the modifier should display under the condition */
    public boolean shouldDisplay(boolean isAdvanced) {
      return isAdvanced ? advanced : normal;
    }
  }

  /** Loader for this modifier */
  public static final IGenericLoader<StatBoostModifier> LOADER = new IGenericLoader<>() {
    @Override
    public StatBoostModifier deserialize(JsonObject json) {
      Rarity rarity = null;
      if (json.has("rarity")) {
        rarity = JsonHelper.getAsEnum(json, "rarity", Rarity.class);
      }
      List<ModifierStatBoost> stats = Collections.emptyList();
      if (json.has("stats")) {
        stats = JsonHelper.parseList(json, "stats", ModifierStatBoost::fromJson);
      }
      List<ModifierAttribute> attributes = Collections.emptyList();
      if (json.has("attributes")) {
        attributes = JsonHelper.parseList(json, "attributes", ModifierAttribute::fromJson);
      }
      List<ResourceLocation> flags = Collections.emptyList();
      if (json.has("flags")) {
        flags = JsonHelper.parseList(json, "flags", JsonHelper::convertToResourceLocation);
      }
      ModifierLevelDisplay display = ModifierLevelDisplay.LOADER.getAndDeserialize(json, "level_display");
      ModifierDisplay modifierDisplay = ModifierDisplay.ALWAYS;
      if (json.has("modifier_display")) {
        modifierDisplay = JsonHelper.getAsEnum(json, "modifier_display", ModifierDisplay.class);
      }
      Set<ToolAction> actions = Collections.emptySet();
      if (json.has("tool_actions")) {
        actions = ImmutableSet.copyOf(JsonHelper.parseList(json, "tool_actions", (element, name) -> ToolAction.get(GsonHelper.convertToString(element, name))));
      }
      return new StatBoostModifier(rarity, stats, attributes, flags, display, modifierDisplay, actions);
    }

    @Override
    public void serialize(StatBoostModifier object, JsonObject json) {
      json.add("level_display", ModifierLevelDisplay.LOADER.serialize(object.levelDisplay));
      if (object.rarity != null) {
        json.addProperty("rarity", object.rarity.name().toLowerCase(Locale.ROOT));
      }
      if (!object.stats.isEmpty()) {
        JsonArray stats = new JsonArray();
        for (ModifierStatBoost boost : object.stats) {
          stats.add(boost.toJson());
        }
        json.add("stats", stats);
      }
      if (!object.attributes.isEmpty()) {
        JsonArray attributes = new JsonArray();
        for (ModifierAttribute attribute : object.attributes) {
          attributes.add(attribute.toJson());
        }
        json.add("attributes", attributes);
      }
      if (!object.flags.isEmpty()) {
        JsonArray stats = new JsonArray();
        for (ResourceLocation flag : object.flags) {
          stats.add(flag.toString());
        }
        json.add("flags", stats);
      }
      if (object.modifierDisplay != ModifierDisplay.ALWAYS) {
        json.addProperty("modifier_display", object.modifierDisplay.getName());
      }
      if (!object.actions.isEmpty()) {
        JsonArray actions = new JsonArray();
        for (ToolAction action : object.actions) {
          actions.add(action.name());
        }
        json.add("tool_actions", actions);
      }
    }

    @Override
    public StatBoostModifier fromNetwork(FriendlyByteBuf buffer) {
      Rarity rarity = null;
      if (buffer.readBoolean()) {
        rarity = buffer.readEnum(Rarity.class);
      }
      int size = buffer.readVarInt();
      ImmutableList.Builder<ModifierStatBoost> stats = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        stats.add(ModifierStatBoost.fromNetwork(buffer));
      }
      size = buffer.readVarInt();
      ImmutableList.Builder<ModifierAttribute> attributes = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        attributes.add(ModifierAttribute.fromNetwork(buffer));
      }
      size = buffer.readVarInt();
      ImmutableList.Builder<ResourceLocation> flags = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        flags.add(buffer.readResourceLocation());
      }
      ModifierLevelDisplay levelDisplay = ModifierLevelDisplay.LOADER.fromNetwork(buffer);
      ModifierDisplay modifierDisplay = buffer.readEnum(ModifierDisplay.class);
      size = buffer.readVarInt();
      ImmutableSet.Builder<ToolAction> actions = ImmutableSet.builder();
      for (int i = 0; i < size; i++) {
        actions.add(ToolAction.get(buffer.readUtf(Short.MAX_VALUE)));
      }
      return new StatBoostModifier(rarity, stats.build(), attributes.build(), flags.build(), levelDisplay, modifierDisplay, actions.build());
    }

    @Override
    public void toNetwork(StatBoostModifier object, FriendlyByteBuf buffer) {
      if (object.rarity != null) {
        buffer.writeBoolean(true);
        buffer.writeEnum(object.rarity);
      } else {
        buffer.writeBoolean(false);
      }
      buffer.writeVarInt(object.stats.size());
      for (ModifierStatBoost boost : object.stats) {
        boost.toNetwork(buffer);
      }
      buffer.writeVarInt(object.attributes.size());
      for (ModifierAttribute attribute : object.attributes) {
        attribute.toNetwork(buffer);
      }
      buffer.writeVarInt(object.flags.size());
      for (ResourceLocation flag : object.flags) {
        buffer.writeResourceLocation(flag);
      }
      ModifierLevelDisplay.LOADER.toNetwork(object.levelDisplay, buffer);
      buffer.writeEnum(object.modifierDisplay);
      buffer.writeVarInt(object.actions.size());
      for (ToolAction action : object.actions) {
        buffer.writeUtf(action.name());
      }
    }
  };

  /** Builder for a stat boost modifier */
  @Accessors(fluent = true)
  public static class Builder {
    /** Rarity for the tool to show */
    @Setter
    private Rarity rarity = null;
    /** List of all boosts to apply */
    private final ImmutableList.Builder<ModifierStatBoost> boosts = ImmutableList.builder();
    /** List of all attributes to apply */
    private final ImmutableList.Builder<ModifierAttribute> attributes = ImmutableList.builder();
    /** List of flags to set */
    private final ImmutableList.Builder<ResourceLocation> flags = ImmutableList.builder();
    /** List of flags to set */
    private final ImmutableSet.Builder<ToolAction> actions = ImmutableSet.builder();
    /** Display for the level */
    @Setter
    private ModifierLevelDisplay display = ModifierLevelDisplay.DEFAULT;
    /** Display for the level */
    @Setter
    private ModifierDisplay modifierDisplay = ModifierDisplay.ALWAYS;

    /** Updates a stat in the builder */
    @SafeVarargs
    public final <T> Builder update(IToolStat<T> stat, T value, TagKey<Item>... tagRequirements) {
      boosts.add(new StatUpdate<>(stat, value, List.of(tagRequirements)));
      return this;
    }

    /** Adds a general boost */
    private Builder boost(INumericToolStat<?> stat, BoostType type, float amount, TagKey<Item>[] tagRequirements) {
      boosts.add(new StatBoost(stat, type, amount, List.of(tagRequirements)));
      return this;
    }

    /** Adds a numeric boost */
    @SafeVarargs
    public final Builder add(INumericToolStat<?> stat, float amount, TagKey<Item>... tagRequirements) {
      return boost(stat, ModifierStatBoost.BoostType.ADD, amount, tagRequirements);
    }

    /** Multiplies the base value of a stat */
    @SafeVarargs
    public final Builder multiplyBase(INumericToolStat<?> stat, float amount, TagKey<Item>... tagRequirements) {
      return boost(stat, ModifierStatBoost.BoostType.MULTIPLY_BASE, amount, tagRequirements);
    }

    /** Multiplies conditional boosts */
    @SafeVarargs
    public final Builder multiplyConditional(INumericToolStat<?> stat, float amount, TagKey<Item>... tagRequirements) {
      return boost(stat, ModifierStatBoost.BoostType.MULTIPLY_CONDITIONAL, amount, tagRequirements);
    }

    /** Multiplies both base and conditional boosts */
    @SafeVarargs
    public final Builder multiplyAll(INumericToolStat<?> stat, float amount, TagKey<Item>... tagRequirements) {
      return boost(stat, ModifierStatBoost.BoostType.MULTIPLY_ALL, amount, tagRequirements);
    }

    /** Adds an attribute to the builder */
    public Builder attribute(String unique, Attribute attribute, Operation operation, float amount, EquipmentSlot... slots) {
      this.attributes.add(new ModifierAttribute(unique, attribute, operation, amount, slots));
      return this;
    }

    /** Adds the given flag to the builder */
    public Builder addFlag(ResourceLocation flag) {
      this.flags.add(flag);
      return this;
    }

    /** Adds the given flag to the builder */
    public Builder addAction(ToolAction action) {
      this.actions.add(action);
      return this;
    }

    /** Builds the final modifier */
    public StatBoostModifier build() {
      return new StatBoostModifier(rarity, boosts.build(), attributes.build(), flags.build(), display, modifierDisplay, actions.build());
    }
  }
}
