package slimeknights.tconstruct.library.tools.definition.module.build;

import net.minecraft.world.item.ArmorItem;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.ToolModule;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modules.ArmorModuleBuilder;

import java.util.List;

/** Module to set global multipliers on the tool */
public record MultiplyStatsModule(MultiplierNBT multipliers) implements ToolStatsHook, ToolModule {
  public static final RecordLoadable<MultiplyStatsModule> LOADER = RecordLoadable.create(MultiplierNBT.LOADABLE.requiredField("multipliers", MultiplyStatsModule::multipliers), MultiplyStatsModule::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MultiplyStatsModule>defaultHooks(ToolHooks.TOOL_STATS);

  @Override
  public RecordLoadable<MultiplyStatsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void addToolStats(IToolContext context, ModifierStatsBuilder builder) {
    for (INumericToolStat<?> stat : multipliers.getContainedStats()) {
      stat.multiplyAll(builder, multipliers.get(stat));
    }
  }


  /** Creates a builder instance */
  public static ArmorBuilder armor(List<ArmorItem.Type> slots) {
    return new ArmorBuilder(slots);
  }

  public static class ArmorBuilder implements ArmorModuleBuilder<MultiplyStatsModule> {
    private final List<ArmorItem.Type> slotTypes;
    private final MultiplierNBT.Builder[] builders = new MultiplierNBT.Builder[4];

    private ArmorBuilder(List<ArmorItem.Type> slotTypes) {
      this.slotTypes = slotTypes;
      for (ArmorItem.Type slotType : slotTypes) {
        builders[slotType.ordinal()] = MultiplierNBT.builder();
      }
    }

    /** Gets the builder for the given slot */
    protected MultiplierNBT.Builder getBuilder(ArmorItem.Type slotType) {
      MultiplierNBT.Builder builder = builders[slotType.ordinal()];
      if (builder == null) {
        throw new IllegalArgumentException("Unsupported slot type " + slotType);
      }
      return builder;
    }

    /** Adds a bonus to the builder */
    public ArmorBuilder set(ArmorItem.Type slotType, INumericToolStat<?> stat, float value) {
      getBuilder(slotType).set(stat, value);
      return this;
    }

    /** Sets the same bonus on all pieces */
    public ArmorBuilder setAll(INumericToolStat<?> stat, float value) {
      for (ArmorItem.Type slotType : slotTypes) {
        set(slotType, stat, value);
      }
      return this;
    }

    /**
     * Sets a different bonus on all pieces.
     * order is usually helmet, chestplate, leggings, boot, but depends on the order from {@link SetStatsModule#armor(List)}
     */
    public final ArmorBuilder setInOrder(INumericToolStat<?> stat, float... values) {
      if (values.length != slotTypes.size()) {
        throw new IllegalStateException("Wrong number of stats set");
      }
      for (int i = 0; i < values.length; i++) {
        set(slotTypes.get(i), stat, values[i]);
      }
      return this;
    }

    /** Builds the final module */
    @Override
    public MultiplyStatsModule build(ArmorItem.Type slot) {
      return new MultiplyStatsModule(getBuilder(slot).build());
    }
  }
}
