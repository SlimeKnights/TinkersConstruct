package slimeknights.tconstruct.tools.modules.ranged;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.SingletonRecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.StackMatch;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.function.Predicate;

/** Module implementing bulk quiver, which pulls arrows from the inventory to fire */
public enum BulkQuiverModule implements ModifierModule, BowAmmoModifierHook {
  INSTANCE;

  public static final SingletonRecordLoadable<BulkQuiverModule> LOADER = new SingletonRecordLoadable<>(INSTANCE);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<BulkQuiverModule>defaultHooks(ModifierHooks.BOW_AMMO);
  private static final ResourceLocation LAST_SLOT = TConstruct.getResource("quiver_last_selected");

  @Override
  public RecordLoadable<BulkQuiverModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack standardAmmo, Predicate<ItemStack> ammoPredicate) {
    // skip if we have standard ammo, this quiver holds backup arrows
    if (!standardAmmo.isEmpty()) {
      return ItemStack.EMPTY;
    }
    StackMatch match = modifier.getHook(ToolInventoryCapability.HOOK).findStack(tool, modifier, ammoPredicate);
    if (!match.isEmpty()) {
      tool.getPersistentData().putInt(LAST_SLOT, match.slot());
      return match.stack();
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, int needed) {
    // we assume no one else touched the quiver inventory, this is a good assumption, do not make it a bad assumption by modifying the quiver in other modifiers
    ammo.shrink(needed);
    modifier.getHook(ToolInventoryCapability.HOOK).setStack(tool, modifier, tool.getPersistentData().getInt((LAST_SLOT)), ammo);
  }
}
