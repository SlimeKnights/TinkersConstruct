package slimeknights.tconstruct.tools.modules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.CapacityBarHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module implementing the overgrowth module
 * TODO 1.21: consider merging into {@link slimeknights.tconstruct.library.modifiers.modules.capacity.TimeToCapacityModule}..
 * TODO 1.21: otherwise move to {@link slimeknights.tconstruct.tools.modules.durability}
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class OvergrowthModule implements ModifierModule, InventoryTickModifierHook, ConditionalModule<IToolStackView> {
  public static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<OvergrowthModule>defaultHooks(ModifierHooks.INVENTORY_TICK);
  protected static final LoadableField<LevelingValue,OvergrowthModule> CHANCE_FIELD = LevelingValue.LOADABLE.requiredField("chance", OvergrowthModule::chance);
  public static final RecordLoadable<OvergrowthModule> LOADER = RecordLoadable.create(CHANCE_FIELD, ModifierCondition.TOOL_FIELD, OvergrowthModule::new);

  private final LevelingValue chance;
  private final ModifierCondition<IToolStackView> condition;

  public OvergrowthModule(LevelingValue chance) {
    this(chance, ModifierCondition.ANY_TOOL);
  }

  @Override
  public RecordLoadable<? extends OvergrowthModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Gets the bar to use for this modifier */
  protected CapacityBarHook getBar(ModifierEntry modifier) {
    return OverslimeModule.INSTANCE;
  }

  @Override
  public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
    // update 1 times a second, but skip when active (messes with pulling bow back)
    if (!world.isClientSide && holder.tickCount % 20 == 0 && holder.getUseItem() != stack && condition.matches(tool, modifier)) {
      // has a chance of restoring each second per level
      CapacityBarHook bar = getBar(modifier);
      if (bar.getAmount(tool) < bar.getCapacity(tool, modifier) && Modifier.RANDOM.nextFloat() < chance.compute(modifier.getEffectiveLevel())) {
        bar.addAmount(tool, modifier, 1);
      }
    }
  }
}
