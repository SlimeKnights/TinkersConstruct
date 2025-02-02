package slimeknights.tconstruct.tools.modules;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

import java.util.List;

/** Module implementing the overgrowth module */
public record OvergrowthModule(LevelingValue chance) implements ModifierModule, InventoryTickModifierHook {
  public static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<OvergrowthModule>defaultHooks(ModifierHooks.INVENTORY_TICK);
  public static final RecordLoadable<OvergrowthModule> LOADER = RecordLoadable.create(
    LevelingValue.LOADABLE.requiredField("chance", OvergrowthModule::chance),
    OvergrowthModule::new);

  @Override
  public RecordLoadable<OvergrowthModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
    // update 1 times a second, but skip when active (messes with pulling bow back)
    if (!world.isClientSide && holder.tickCount % 20 == 0 && holder.getUseItem() != stack) {
      OverslimeModifier overslime = TinkerModifiers.overslime.get();
      ModifierEntry entry = tool.getModifier(TinkerModifiers.overslime.getId());
      // has a 5% chance of restoring each second per level
      if (entry.getLevel() > 0 && overslime.getShield(tool) < overslime.getShieldCapacity(tool, entry) && Modifier.RANDOM.nextFloat() < chance.compute(modifier.getEffectiveLevel())) {
        overslime.addOverslime(tool, entry, 1);
      }
    }
  }
}
