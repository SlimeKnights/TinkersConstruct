package slimeknights.tconstruct.tools.modules.ranged;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.item.CrystalshotItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/** Module implementing crystalshot and its configuration */
public record CrystalshotModule(int durabilityUsage, boolean checkStandardArrows) implements ModifierModule, BowAmmoModifierHook, ModifierRemovalHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<CrystalshotModule>defaultHooks(ModifierHooks.BOW_AMMO, ModifierHooks.REMOVE);
  public static final RecordLoadable<CrystalshotModule> LOADER = RecordLoadable.create(
    IntLoadable.FROM_ZERO.requiredField("durability_usage", CrystalshotModule::durabilityUsage),
    BooleanLoadable.INSTANCE.defaultField("check_standard_arrows", true, CrystalshotModule::checkStandardArrows),
    CrystalshotModule::new
  );

  @Override
  public RecordLoadable<? extends IHaveLoader> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack standardAmmo, Predicate<ItemStack> ammoPredicate) {
    if (checkStandardArrows && !standardAmmo.isEmpty()) {
      return ItemStack.EMPTY;
    }
    // our available count is based on how many arrows we can create from the remaining durability, though round up to be nice
    int count = durabilityUsage <= 0 ? 64 : Math.min(64, (tool.getCurrentDurability() + durabilityUsage - 1) / durabilityUsage);
    return CrystalshotItem.withVariant(tool.getPersistentData().getString(modifier.getId()), count);
  }

  @Override
  public void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, int needed) {
    if (durabilityUsage > 0) {
      ToolDamageUtil.damageAnimated(tool, durabilityUsage * needed, shooter, shooter.getUsedItemHand());
    }
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(modifier.getId());
    return null;
  }
}
