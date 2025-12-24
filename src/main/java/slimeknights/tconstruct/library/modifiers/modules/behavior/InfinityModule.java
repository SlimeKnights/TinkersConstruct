package slimeknights.tconstruct.library.modifiers.modules.behavior;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.common.ItemStackLoadable;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Module making the bow fire infinite ammo of the given type.
 * @param ammo                Item stack setting the ammo.
 * @param variantTag           If not empty, copies the modifier variant into this tag on the arrow item.
 * @param durabilityUsage      Amount of extra durability consumed when using this module.
 * @param checkStandardArrows  If true, won't fire infinite arrows if there are standard arrows.
 */
public record InfinityModule(ItemStack ammo, String variantTag, int durabilityUsage, boolean checkStandardArrows) implements ModifierModule, BowAmmoModifierHook, ModifierRemovalHook, ProjectileLaunchModifierHook.NoShooter {
  /** NBT marking the stack as infinity to set arrow pickup */
  private static final String INFINITY = "tic_infinity";
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<InfinityModule>defaultHooks(ModifierHooks.BOW_AMMO, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_SHOT, ModifierHooks.REMOVE);
  public static final RecordLoadable<InfinityModule> LOADER = RecordLoadable.create(
    ItemStackLoadable.REQUIRED_ITEM_NBT.requiredField("ammo", InfinityModule::ammo),
    StringLoadable.DEFAULT.defaultField("variant_tag", "", InfinityModule::variantTag),
    IntLoadable.FROM_ZERO.requiredField("durability_usage", InfinityModule::durabilityUsage),
    BooleanLoadable.INSTANCE.defaultField("check_standard_arrows", true, InfinityModule::checkStandardArrows),
    InfinityModule::new
  );

  @Override
  public RecordLoadable<InfinityModule> getLoader() {
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
    ItemStack ammo = this.ammo.copyWithCount(count);
    CompoundTag tag = ammo.getOrCreateTag();
    // mark the arrow as infinity for the projectile launch hook
    tag.putBoolean(INFINITY, true);
    // if a variant is requested, set that on the stack
    if (!variantTag.isEmpty()) {
      String variant = tool.getPersistentData().getString(modifier.getId());
      if (!variant.isEmpty()) {
        tag.putString(variantTag, variant);
      }
    }
    return ammo;
  }

  @Override
  public void onProjectileShoot(IToolStackView tool, ModifierEntry modifier, @Nullable LivingEntity shooter, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, boolean primary) {
    // for arrows fired by this module, set them to creative only pickup
    // not an issue if you have multiple types of infinity, they all agree on the goal here
    if (arrow != null && arrow.pickup != Pickup.CREATIVE_ONLY) {
      CompoundTag tag = ammo.getTag();
      if (tag != null && tag.getBoolean(INFINITY)) {
        arrow.pickup = Pickup.CREATIVE_ONLY;
      }
    }
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
    if (!variantTag.isEmpty()) {
      tool.getPersistentData().remove(modifier.getId());
    }
    return null;
  }
}
