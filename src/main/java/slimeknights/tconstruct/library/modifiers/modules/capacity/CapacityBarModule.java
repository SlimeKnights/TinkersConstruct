package slimeknights.tconstruct.library.modifiers.modules.capacity;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.hook.special.CapacityBarHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;

/** Standard capacity bar implementation which scales through modifier level */
public class CapacityBarModule extends CapacityBarHook.PersistentDataCapacityBar implements ModifierModule {
  public static final RecordLoadable<CapacityBarModule> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    LevelingInt.LOADABLE.directField(m -> m.capacity),
    ToolStats.NUMERIC_LOADER.nullableField("multiplier", m -> m.multiplier),
    CapacityBarModule::new);

  private final LevelingInt capacity;
  @Nullable
  private final INumericToolStat<?> multiplier;
  public CapacityBarModule(ResourceLocation key, LevelingInt capacity, @Nullable INumericToolStat<?> multiplier) {
    super(key);
    this.capacity = capacity;
    this.multiplier = multiplier;
  }

  /** Constructor for datagen */
  public CapacityBarModule(LevelingInt capacity, @Nullable INumericToolStat<?> multiplier) {
    this(ModifierManager.EMPTY, capacity, multiplier);
  }

  @Override
  public RecordLoadable<CapacityBarModule> getLoader() {
    return LOADER;
  }

  @Override
  public int getCapacity(IToolStackView tool, ModifierEntry entry) {
    int capacity = this.capacity.compute(entry.getEffectiveLevel());
    if (multiplier != null) {
      capacity *= tool.getMultiplier(multiplier);
    }
    return capacity;
  }
}
