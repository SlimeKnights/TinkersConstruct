package slimeknights.tconstruct.library.modifiers.hook;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.function.Predicate;

/**
 * Hook to find ammo on a bow
 */
public interface BowAmmoModifierHook {
  /** Default instance */
  BowAmmoModifierHook EMPTY = (tool, modifier, shooter, standardAmmo, ammoPredicate) -> ItemStack.EMPTY;

  /**
   * Finds the ammo. Does *not* modify the tool, this method may be called without loosing an arrow
   * @param tool           Tool instance
   * @param modifier       Modifier being called
   * @param shooter        Entity using the bow
   * @param standardAmmo   Arrows found in the player inventory. Will be empty if not found
   * @param ammoPredicate  Predicate from the bow of types of ammo it accepts
   * @return  Item stack of ammo found. If empty, will continue searching for ammo elsewhere until falling back to standard ammo
   */
  ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack standardAmmo, Predicate<ItemStack> ammoPredicate);

  /**
   * Callback to shrink the ammo returned by {@link #findAmmo(IToolStackView, ModifierEntry, LivingEntity, ItemStack, Predicate)}.
   * Will only be called on the modifier that returned non-empty in the previous method
   * @param tool      Tool instance
   * @param modifier  Modifier instance
   * @param shooter   Entity shooting the ammo
   * @param ammo      Ammo that was found
   */
  default void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo) {
    ammo.shrink(1);
  }
}
