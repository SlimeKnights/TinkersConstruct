package slimeknights.tconstruct.library.tools;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.IMaterialRegistry;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.definition.IToolStatProvider;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionLoader;
import slimeknights.tconstruct.library.tools.definition.ToolStatProviders;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class serves primarily as a container where the datapack tool data will be injected on datapack load
 */
public class ToolDefinition {
  /** Empty tool definition instance to prevent the need for null for a fallback */
  public static final ToolDefinition EMPTY = new ToolDefinition(TConstruct.getResource("empty"), new IToolStatProvider() {
    @Override
    public StatsNBT buildStats(ToolDefinition definition, List<IMaterial> materials) {
      return StatsNBT.EMPTY;
    }

    @Override
    public boolean isMultipart() {
      return false;
    }
  });

  @Getter
  private final ResourceLocation id;
  /** Function to convert from tool definition and materials into tool stats */
  @Getter
  private final IToolStatProvider statProvider;

  /** Base data loaded from JSON, contains stats, traits, and starting slots */
  @Getter
  protected ToolDefinitionData data;

  protected ToolDefinition(ResourceLocation id, IToolStatProvider statProvider) {
    this.id = id;
    this.statProvider = statProvider;
    data = statProvider.getDefaultData();
  }

  /**
   * Creates an tool definition builder
   * @param id  Tool definition ID
   * @return Definition builder
   */
  public static ToolDefinition.Builder builder(ResourceLocation id) {
    return new Builder(id);
  }

  /**
   * Creates an tool definition builder
   * @param item  Tool item
   * @return Definition builder
   */
  public static ToolDefinition.Builder builder(RegistryObject<? extends IItemProvider> item) {
    return builder(item.getId());
  }

  /**
   * Creates an tool definition builder
   * @param item  Tool item
   * @return Definition builder
   */
  public static ToolDefinition.Builder builder(ItemObject<? extends IItemProvider> item) {
    return builder(item.getRegistryName());
  }

  /** Checks if the tool uses multipart stats, may not match {@link #getData()} if the JSON file was invalid*/
  public boolean isMultipart() {
    return statProvider.isMultipart();
  }

  /**
   * Builds the stats for this tool definition
   * @param materials  Materials list
   * @return  Stats NBT
   */
  public StatsNBT buildStats(List<IMaterial> materials) {
    return statProvider.buildStats(this, materials);
  }


  /* Repairing */

  /** Cached indices that can be used to repair this tool */
  private int[] repairIndices;

  /** Largest weight of all repair parts */
  private Integer maxRepairWeight;

  /** Returns a list of part material requirements for repair materials */
  public int[] getRepairParts() {
    if (repairIndices == null) {
      // get indices of all head parts
      List<PartRequirement> components = getData().getParts();
      if (components.isEmpty()) {
        repairIndices = new int[0];
      } else {
        IMaterialRegistry registry = MaterialRegistry.getInstance();
        repairIndices = IntStream.range(0, components.size())
                                 .filter(i -> registry.getDefaultStats(components.get(i).getPart().getStatType()) instanceof IRepairableMaterialStats)
                                 .toArray();
      }
    }
    return repairIndices;
  }

  /** Gets the largest weight for all repair parts */
  public int getMaxRepairWeight() {
    if (maxRepairWeight == null) {
      int max = 1;
      List<PartRequirement> parts = getData().getParts();
      for (int i : getRepairParts()) {
        int cmp = parts.get(i).getWeight();
        if (cmp > max) {
          max = cmp;
        }
      }
      maxRepairWeight = max;
    }
    return maxRepairWeight;
  }


  /* Loader methods */

  /** Validates the given tool data works with this tool definition. Throws if invalid */
  public void validate(ToolDefinitionData data) {
    statProvider.validate(data);
  }

  /** Updates the data in this tool definition from the JSON loader, should not be called directly other than by the loader */
  public void setData(ToolDefinitionData data) {
    this.data = data;
    // clear caches
    repairIndices = null;
    maxRepairWeight = null;
    baseStatDefinition = null;
  }

  /** Sets the tool data to the default, for the sake of erroring */
  public void setDefaultData() {
    setData(statProvider.getDefaultData());
  }


  /* Deprecated methods from before datapack transfer */

  /** Cache of base stats definition, for deprecated hooks */
  @Nullable @Deprecated
  private ToolBaseStatDefinition baseStatDefinition;

  /** @deprecated Use {@link #getData()} */
  @Deprecated
  public ToolBaseStatDefinition getBaseStatDefinition() {
    if (baseStatDefinition == null) {
      baseStatDefinition = new ToolBaseStatDefinition(getData());
    }
    return baseStatDefinition;
  }

  /** @deprecated Use {@link ToolDefinitionData#getParts()} */
  @Deprecated
  public List<IToolPart> getRequiredComponents() {
    return getData().getParts().stream().map(PartRequirement::getPart).collect(Collectors.toList());
  }

  /** @deprecated use {@link ToolDefinitionData#getTraits()} */
  @Deprecated
  public List<ModifierEntry> getModifiers() {
    return getData().getTraits();
  }


  /** Builder to easily create a tool definition */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private final ResourceLocation id;
    @Setter @Accessors(chain = true)
    private IToolStatProvider statsProvider;
    private boolean register = true;

    /** Sets the tool to use a melee harvest tool stat provider, which requires at least 1 head part and uses any number of handle or bindings */
    public Builder meleeHarvest() {
      setStatsProvider(ToolStatProviders.MELEE_HARVEST);
      return this;
    }

    /** Sets the tool to not use parts */
    public Builder noParts() {
      setStatsProvider(ToolStatProviders.NO_PARTS);
      return this;
    }

    /** Tells the definition to not be registered with the loader, used internally for testing. In general mods wont need this */
    public Builder skipRegister() {
      register = false;
      return this;
    }

    /**
     * Builds the final tool definition
     * @return  Tool definition
     */
    public ToolDefinition build() {
      if (statsProvider == null) {
        throw new IllegalArgumentException("Stats provider is required for tools");
      }
      ToolDefinition definition = new ToolDefinition(id, statsProvider);
      if (register) {
        ToolDefinitionLoader.getInstance().registerToolDefinition(definition);
      }
      return definition;
    }
  }
}
