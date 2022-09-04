package slimeknights.tconstruct.library.data.tinkering;

import com.google.common.collect.ImmutableList;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionDataBuilder;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.item.ArmorSlotType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Base datagenerator to generate tool definition data */
public abstract class AbstractToolDefinitionDataProvider extends GenericDataProvider {
  /** copy of the vanilla array for the builder */
  private static final int[] MAX_DAMAGE_ARRAY = {13, 15, 16, 11};

  private final Map<ResourceLocation,ToolDefinitionDataBuilder> allTools = new HashMap<>();
  /** Mod ID to filter definitions we care about */
  private final String modId;

  public AbstractToolDefinitionDataProvider(DataGenerator generator, String modId) {
    super(generator, PackType.SERVER_DATA, ToolDefinitionLoader.FOLDER, ToolDefinitionLoader.GSON);
    this.modId = modId;
  }

  /**
   * Function to add all relevant tool definitions
   */
  protected abstract void addToolDefinitions();

  /** Defines the given ID as a tool definition */
  protected ToolDefinitionDataBuilder define(ResourceLocation id) {
    return allTools.computeIfAbsent(id, i -> ToolDefinitionDataBuilder.builder());
  }

  /** Defines the given ID as a tool definition */
  protected ToolDefinitionDataBuilder define(ItemLike item) {
    return define(Objects.requireNonNull(item.asItem().getRegistryName()));
  }

  /** Defines the given ID as a tool definition */
  protected ToolDefinitionDataBuilder define(ToolDefinition definition) {
    return define(definition.getId());
  }

  /** Defines an armor data builder */
  protected ArmorDataBuilder defineArmor(ModifiableArmorMaterial armorMaterial) {
    return new ArmorDataBuilder(armorMaterial);
  }

  @Override
  public void run(HashCache cache) throws IOException {
    addToolDefinitions();
    Map<ResourceLocation,ToolDefinition> relevantDefinitions = ToolDefinitionLoader.getInstance().getRegisteredToolDefinitions().stream()
                                                                                   .filter(def -> def.getId().getNamespace().equals(modId))
                                                                                   .collect(Collectors.toMap(ToolDefinition::getId, Function.identity()));
    // ensure all required definitions are included
    for (ToolDefinition definition : relevantDefinitions.values()) {
      ResourceLocation name = definition.getId();
      if (!allTools.containsKey(name)) {
        throw new IllegalStateException(String.format("Missing tool definition for '%s'", name));
      }
    }
    // ensure all included ones are required, and the built ones are valid
    for (Entry<ResourceLocation,ToolDefinitionDataBuilder> entry : allTools.entrySet()) {
      ResourceLocation id = entry.getKey();
      ToolDefinition definition = relevantDefinitions.get(id);
      if (definition == null) {
        throw new IllegalStateException("Unknown tool definition with ID " + id);
      }
      ToolDefinitionData data = entry.getValue().build();
      definition.validate(data);
      saveThing(cache, id, data);
    }
  }

  /** Builder for an armor material to batch certain hooks */
  @SuppressWarnings("UnusedReturnValue")
  protected class ArmorDataBuilder {
    private final ResourceLocation name;
    private final ToolDefinitionDataBuilder[] builders;
    private final List<ArmorSlotType> slotTypes;
    private ArmorDataBuilder(ModifiableArmorMaterial armorMaterial) {
      this.name = new ResourceLocation(armorMaterial.getName());
      this.builders = new ToolDefinitionDataBuilder[4];
      ImmutableList.Builder<ArmorSlotType> slotTypes = ImmutableList.builder();
      for (ArmorSlotType slotType : ArmorSlotType.values()) {
        ToolDefinition definition = armorMaterial.getArmorDefinition(slotType);
        if (definition != null) {
          this.builders[slotType.getIndex()] = define(definition);
          slotTypes.add(slotType);
        }
      }
      this.slotTypes = slotTypes.build();
    }

    /** Gets the builder for the given slot */
    protected ToolDefinitionDataBuilder getBuilder(ArmorSlotType slotType) {
      ToolDefinitionDataBuilder builder = builders[slotType.getIndex()];
      if (builder == null) {
        throw new IllegalArgumentException("Unsupported slot type " + slotType + " for material " + name);
      }
      return builder;
    }


    /* Parts */

    /** Adds a part to the builder */
    public ArmorDataBuilder part(ArmorSlotType slotType, IToolPart part, int weight) {
      getBuilder(slotType).part(part, weight);
      return this;
    }

    /** Adds a part to the builder */
    public ArmorDataBuilder part(ArmorSlotType slotType, MaterialStatsId statsId, int weight) {
      getBuilder(slotType).part(statsId, weight);
      return this;
    }


    /* Stats */

    /** Adds a bonus to the builder */
    public <T> ArmorDataBuilder stat(ArmorSlotType slotType, IToolStat<T> stat, T value) {
      getBuilder(slotType).stat(stat, value);
      return this;
    }

    /** Adds a bonus to the builder */
    public ArmorDataBuilder stat(ArmorSlotType slotType, IToolStat<Float> stat, float value) {
      return stat(slotType, stat, (Float) value);
    }

    /** Sets the same bonus on all pieces */
    public <T> ArmorDataBuilder statAll(IToolStat<T> stat, T value) {
      for (ArmorSlotType slotType : slotTypes) {
        stat(slotType, stat, value);
      }
      return this;
    }

    /** Sets the same bonus on all pieces */
    public ArmorDataBuilder statAll(IToolStat<Float> stat, float value) {
      return statAll(stat, (Float) value);
    }

    /** Sets a different bonus on all pieces */
    @SafeVarargs
    public final <T> ArmorDataBuilder statEach(IToolStat<T> stat, T... values) {
      if (values.length != slotTypes.size()) {
        throw new IllegalStateException("Wrong number of stats set");
      }
      for (int i = 0; i < values.length; i++) {
        stat(slotTypes.get(i), stat, values[i]);
      }
      return this;
    }

    /** Sets a different bonus on all pieces, float overload as it comes up commonly */
    public final ArmorDataBuilder statEach(IToolStat<Float> stat, float... values) {
      if (values.length != slotTypes.size()) {
        throw new IllegalStateException("Wrong number of stats set");
      }
      for (int i = 0; i < values.length; i++) {
        stat(slotTypes.get(i), stat, values[i]);
      }
      return this;
    }

    /**
     * Sets the durability for all parts like vanilla armor materials
     * @param maxDamageFactor  Durability modifier applied to the base value for each slot
     * @return  Builder
     */
    public ArmorDataBuilder durabilityFactor(float maxDamageFactor) {
      for (ArmorSlotType slotType : slotTypes) {
        stat(slotType, ToolStats.DURABILITY, MAX_DAMAGE_ARRAY[slotType.getIndex()] * maxDamageFactor);
      }
      return this;
    }

    /** Applies a global multiplier to a single slot */
    public ArmorDataBuilder multiplier(ArmorSlotType slotType, FloatToolStat stat, float value) {
      getBuilder(slotType).multiplier(stat, value);
      return this;
    }

    /** Applies a global multiplier to all slots */
    public ArmorDataBuilder multiplier(FloatToolStat stat, float value) {
      for (ArmorSlotType slotType : slotTypes) {
        multiplier(slotType, stat, value);
      }
      return this;
    }

    /** Sets the starting slots for the given type, unspecified defaults to 0 */
    public ArmorDataBuilder startingSlots(ArmorSlotType armorSlot, SlotType slotType, int value) {
      getBuilder(armorSlot).startingSlots(slotType, value);
      return this;
    }

    /** Sets the starting slots for all types */
    public ArmorDataBuilder startingSlots(SlotType slotType, int value) {
      for (ArmorSlotType armorSlot : slotTypes) {
        startingSlots(armorSlot, slotType, value);
      }
      return this;
    }

    /** Sets the starting slots for multiple slots */
    public ArmorDataBuilder startingSlots(SlotType slotType, int... values) {
      if (values.length != slotTypes.size()) {
        throw new IllegalStateException("Wrong number of stats set");
      }
      for (int i = 0; i < values.length; i++) {
        startingSlots(slotTypes.get(i), slotType, values[i]);
      }
      return this;
    }


    /* Traits */

    /** Adds a base trait to all variants of the tool */
    public ArmorDataBuilder trait(ModifierId modifier, int level) {
      for (ArmorSlotType armorSlot : slotTypes) {
        trait(armorSlot, modifier, level);
      }
      return this;
    }

    /** Adds a base trait to the tool */
    public ArmorDataBuilder trait(LazyModifier modifier, int level) {
      return trait(modifier.getId(), level);
    }

    /** Adds a base trait to the tool */
    public ArmorDataBuilder trait(ModifierId modifier) {
      return trait(modifier, 1);
    }

    /** Adds a base trait to the tool */
    public ArmorDataBuilder trait(LazyModifier modifier) {
      return trait(modifier, 1);
    }

    /** Adds a base trait to the tool */
    public ArmorDataBuilder trait(ArmorSlotType slotType, ModifierId modifier, int level) {
      getBuilder(slotType).trait(modifier, level);
      return this;
    }

    /** Adds a base trait to the tool */
    public ArmorDataBuilder trait(ArmorSlotType slotType, LazyModifier modifier, int level) {
      return trait(slotType, modifier.getId(), level);
    }

    /** Adds a base trait to the tool */
    public ArmorDataBuilder trait(ArmorSlotType slotType, ModifierId modifier) {
      return trait(slotType, modifier, 1);
    }

    /** Adds a base trait to the tool */
    public ArmorDataBuilder trait(ArmorSlotType slotType, LazyModifier modifier) {
      return trait(slotType, modifier, 1);
    }
  }
}
