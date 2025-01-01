package slimeknights.tconstruct.library.tools.definition.module.material;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.item.ArmorItem;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.field.OptionallyNestedLoadable;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tools.modules.ArmorModuleBuilder;

import java.util.List;
import java.util.function.Supplier;

/** Tool using tool parts for its material stats, allows part swapping and tool building */
public class PartStatsModule extends MaterialStatsModule implements ToolPartsHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<PartStatsModule>defaultHooks(ToolHooks.TOOL_STATS, ToolHooks.TOOL_TRAITS, ToolHooks.TOOL_MATERIALS, ToolHooks.TOOL_PARTS, ToolHooks.MATERIAL_REPAIR);
  public static final RecordLoadable<PartStatsModule> LOADER = RecordLoadable.create(
    new OptionallyNestedLoadable<>(TinkerLoadables.TOOL_PART_ITEM, "item").list().requiredField("parts", m -> m.parts),
    new StatScaleField("item", "parts"),
    PRIMARY_PART_FIELD,
    PartStatsModule::new);

  private final List<IToolPart> parts;

  protected PartStatsModule(List<IToolPart> parts, float[] scales, int primaryPart) {
    super(parts.stream().map(IToolPart::getStatType).toList(), scales, primaryPart);
    this.parts = parts;
  }

  @Override
  public RecordLoadable<PartStatsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public List<IToolPart> getParts(ToolDefinition definition) {
    return parts;
  }


  /* Builder */

  public static Builder parts() {
    return new Builder();
  }

  /** Starts a builder for armor stats */
  public static ArmorBuilder armor(List<ArmorItem.Type> slots) {
    return new ArmorBuilder(slots);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private final ImmutableList.Builder<IToolPart> parts = ImmutableList.builder();
    private final ImmutableList.Builder<Float> scales = ImmutableList.builder();
    @Setter @Accessors(fluent = true)
    private int primaryPart = 0;

    /** Adds a part to the builder */
    public Builder part(IToolPart part, float scale) {
      parts.add(part);
      scales.add(scale);
      return this;
    }

    /** Adds a part to the builder */
    public Builder part(Supplier<? extends IToolPart> part, float scale) {
      return part(part.get(), scale);
    }

    /** Adds a part to the builder */
    public Builder part(IToolPart part) {
      return part(part, 1);
    }

    /** Adds a part to the builder */
    public Builder part(Supplier<? extends IToolPart> part) {
      return part(part, 1);
    }

    /** Builds the module */
    public PartStatsModule build() {
      List<IToolPart> parts = this.parts.build();
      if (primaryPart >= parts.size() || primaryPart < -1) {
        throw new IllegalStateException("Primary part must be within parts list, maximum " + parts.size() + ", got " + primaryPart);
      }
      return new PartStatsModule(parts, MaterialStatsModule.Builder.buildScales(scales.build()), primaryPart);
    }
  }

  /** Builder for armor */
  public static class ArmorBuilder implements ArmorModuleBuilder<PartStatsModule> {
    private final List<ArmorItem.Type> slotTypes;
    private final Builder[] builders = new Builder[4];

    private ArmorBuilder(List<ArmorItem.Type> slotTypes) {
      this.slotTypes = slotTypes;
      for (ArmorItem.Type slotType : slotTypes) {
        builders[slotType.ordinal()] = new Builder();
      }
    }

    /** Gets the builder for the given slot */
    protected Builder getBuilder(ArmorItem.Type slotType) {
      Builder builder = builders[slotType.ordinal()];
      if (builder == null) {
        throw new IllegalArgumentException("Unsupported slot type " + slotType);
      }
      return builder;
    }

    /** Adds a part to the given slot */
    public ArmorBuilder part(ArmorItem.Type slotType, IToolPart part, float scale) {
      getBuilder(slotType).part(part, scale);
      return this;
    }

    /** Adds a part to all slots */
    public ArmorBuilder part(IToolPart part, float scale) {
      for (ArmorItem.Type slotType : slotTypes) {
        getBuilder(slotType).part(part, scale);
      }
      return this;
    }

    /** Adds a part to all slots */
    public ArmorBuilder part(Supplier<? extends IToolPart> part, float scale) {
      return part(part.get(), scale);
    }

    /** Adds parts to the builder from the passed object */
    public ArmorBuilder part(EnumObject<ArmorItem.Type, ? extends IToolPart> parts, float scale) {
      for (ArmorItem.Type slotType : slotTypes) {
        getBuilder(slotType).part(parts.get(slotType), scale);
      }
      return this;
    }

    /** Sets the primary part for all slots, assuming its the same index as you defined the parts using this builder. */
    public ArmorBuilder primaryPart(int index) {
      for (ArmorItem.Type slotType : slotTypes) {
        getBuilder(slotType).primaryPart(index);
      }
      return this;
    }

    @Override
    public PartStatsModule build(ArmorItem.Type slot) {
      return getBuilder(slot).build();
    }
  }
}
