package slimeknights.tconstruct.library.modifiers.modules.fluid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.List;

/** Module letting a tool change tank capacity */
@Getter
@RequiredArgsConstructor
public class TankCapacityModule implements ModifierModule, VolatileDataModifierHook {
  private static final List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.VOLATILE_DATA);
  /** Default key for capacity */
  public static final ResourceLocation DEFAULT_CAPACITY_KEY = TConstruct.getResource("tank_capacity");
  /* Fields */
  protected static final LoadableField<ResourceLocation,TankCapacityModule> CAPACITY_KEY_FIELD = Loadables.RESOURCE_LOCATION.defaultField("capacity_key", DEFAULT_CAPACITY_KEY, TankCapacityModule::getCapacityKey);
  protected static final LoadableField<Integer,TankCapacityModule> CAPACITY_FIELD = IntLoadable.FROM_ZERO.requiredField("capacity", TankCapacityModule::getCapacity);
  protected static final LoadableField<Boolean,TankCapacityModule> SCALE_CAPACITY_FIELD = BooleanLoadable.INSTANCE.requiredField("scale_capacity", TankCapacityModule::isScaleCapacity);
  /** Loader instance */
  public static final RecordLoadable<TankCapacityModule> LOADER = RecordLoadable.create(CAPACITY_KEY_FIELD, CAPACITY_FIELD, SCALE_CAPACITY_FIELD, TankCapacityModule::new);

  /** Volatile NBT integer indicating the tank's max capacity */
  private final ResourceLocation capacityKey;
  /** Max capacity added by this module */
  private final int capacity;
  /** If true, capacity scales with level */
  private final boolean scaleCapacity;

  public TankCapacityModule(int capacity, boolean scaleCapacity) {
    this(DEFAULT_CAPACITY_KEY, capacity, scaleCapacity);
  }

  /** Gets the full capacity of the tank */
  public int getCapacity(IToolStackView tool) {
    return tool.getVolatileData().getInt(getCapacityKey());
  }

  @Override
  public void addVolatileData(ToolRebuildContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    ResourceLocation key = getCapacityKey();
    volatileData.putInt(key, capacity * modifier.getLevel() + volatileData.getInt(key));
  }

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }
}
