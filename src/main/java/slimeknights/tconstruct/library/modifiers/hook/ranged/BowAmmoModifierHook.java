package slimeknights.tconstruct.library.modifiers.hook.ranged;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.function.Predicate;

/** Hook to find ammo on a bow.*/
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
   * @param ammo      Ammo that was found by {@link #findAmmo(IToolStackView, ModifierEntry, LivingEntity, ItemStack, Predicate)}
   * @param needed    Desired size, should always be less than the size of {@code ammo}
   */
  default void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, int needed) {
    ammo.shrink(needed);
  }

  /**
   * Checks if the player has ammo for the given tool
   * @param tool       Tool instance, for running modifier hooks
   * @param bowStack   Bow stack instance, for standard ammo lookup
   * @param player     Player instance, for standard ammo lookup
   * @param predicate  Predicate for finding ammo in modifiers
   * @return  True if there is ammo either on the player or on the modifiers
   */
  static boolean hasAmmo(IToolStackView tool, ItemStack bowStack, Player player, Predicate<ItemStack> predicate) {
    // no need to ask the modifiers for ammo if we have it in the inventory, as there is no way for a modifier to say not to use ammo if its present
    // inventory search is probably a bit faster on average than modifier search as its already parsed
    if (!player.getProjectile(bowStack).isEmpty()) {
      return true;
    }
    for (ModifierEntry entry : tool.getModifierList()) {
      if (!entry.getHook(ModifierHooks.BOW_AMMO).findAmmo(tool, entry, player, ItemStack.EMPTY, predicate).isEmpty()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Looks for a matching item stack in the player inventory
   * @param bow        Bow stack
   * @param living     Entity to search
   * @param predicate  Predicate for finding ammo in modifiers
   * @return  Matching stack in the player inventory
   */
  private static ItemStack findMatchingAmmo(ItemStack bow, LivingEntity living, Predicate<ItemStack> predicate) {
    // start with hands, find one that matches but is not the bow
    for (InteractionHand hand : InteractionHand.values()) {
      ItemStack stack = living.getItemInHand(hand);
      if (stack != bow && predicate.test(stack)) {
        return ForgeHooks.getProjectile(living, bow, stack);
      }
    }

    // was not in hand, search the rest of the inventory
    if (living instanceof Player player) {
      Inventory inventory = player.getInventory();
      for (int i = 0; i < inventory.getContainerSize(); i++) {
        ItemStack stack = inventory.getItem(i);
        if (!stack.isEmpty() && predicate.test(stack)) {
          return ForgeHooks.getProjectile(player, bow, stack);
        }
      }
    }
    return ItemStack.EMPTY;
  }

  /**
   * Finds ammo in the inventory, and consume it if not creative
   * @param tool       Tool instance
   * @param bow        Bow stack instance
   * @param predicate  Predicate for valid ammo
   * @param player     Player to search
   * @return  Found ammo
   */
  static ItemStack findAmmo(IToolStackView tool, ItemStack bow, Player player, Predicate<ItemStack> predicate) {
    int projectilesDesired = 1 + (2 * tool.getModifierLevel(TinkerModifiers.multishot.getId()));
    // treat client side as creative, no need to shrink the stacks clientside
    Level level = player.level();
    boolean creative = player.getAbilities().instabuild || level.isClientSide;

    // first search, find what ammo type we want
    ItemStack standardAmmo = player.getProjectile(bow);
    ItemStack resultStack = ItemStack.EMPTY;
    for (ModifierEntry entry : tool.getModifierList()) {
      BowAmmoModifierHook hook = entry.getHook(ModifierHooks.BOW_AMMO);
      ItemStack ammo = hook.findAmmo(tool, entry, player, standardAmmo, predicate);
      if (!ammo.isEmpty()) {
        // if creative, we are done, just return the ammo with the given size
        if (creative) {
          return ItemHandlerHelper.copyStackWithSize(ammo, projectilesDesired);
        }

        // not creative, split out the desired amount. We may have to do more work if it is too small
        resultStack = ItemHandlerHelper.copyStackWithSize(ammo, Math.min(projectilesDesired, ammo.getCount()));
        hook.shrinkAmmo(tool, entry, player, ammo, resultStack.getCount());
        break;
      }
    }

    // result stack being empty means no modifier found it, so we use standard ammo
    if (resultStack.isEmpty()) {
      // if standard ammo is empty as well, nothing else to do but give up
      if (standardAmmo.isEmpty()) {
        return ItemStack.EMPTY;
      }
      // with standard ammo, in creative we can just return that
      if (creative) {
        return ItemHandlerHelper.copyStackWithSize(standardAmmo, projectilesDesired);
      }
      // make a copy of the result, up to the desired size
      resultStack = standardAmmo.split(projectilesDesired);
      if (standardAmmo.isEmpty()) {
        player.getInventory().removeItem(standardAmmo);
      }
    }

    // if we made it this far, we found ammo and are not in creative
    // we may be done already, saves making a predicate
    // can also return if on client side, they don't need the full stack
    if (resultStack.getCount() >= projectilesDesired || level.isClientSide) {
      return resultStack;
    }

    // not enough? keep searching until we fill the stack
    ItemStack match = resultStack;
    predicate = stack -> ItemStack.isSameItemSameTags(stack, match);
    hasEnough:
    do {
      // if standard ammo is empty, try finding a matching stack again
      if (standardAmmo.isEmpty()) {
        standardAmmo = findMatchingAmmo(bow, player, predicate);
      }
      // next, try asking modifiers if they have anything new again
      for (ModifierEntry entry : tool.getModifierList()) {
        BowAmmoModifierHook hook = entry.getHook(ModifierHooks.BOW_AMMO);
        ItemStack ammo = hook.findAmmo(tool, entry, player, standardAmmo, predicate);
        if (!ammo.isEmpty()) {
          // consume as much of the stack as we need then restart the loop
          hook.shrinkAmmo(tool, entry, player, ammo, Math.min(projectilesDesired - resultStack.getCount(), ammo.getCount()));
          continue hasEnough;
        }
      }
      // no standard and no modifier found means we give up
      if (standardAmmo.isEmpty()) {
        break;
      }

      // if we have standard ammo, take what we can then loop again
      int needed = projectilesDesired - resultStack.getCount();
      if (needed <= standardAmmo.getCount()) {
        // consume the whole stack
        resultStack.grow(standardAmmo.getCount());
        player.getInventory().removeItem(standardAmmo);
      } else {
        // found what we need, we are done
        standardAmmo.shrink(needed);
        resultStack.grow(needed);
        break;
      }
    } while (resultStack.getCount() < projectilesDesired);

    // TODO: diyo would prefer enforcing an odd number, so if we do not find more we may want to grow the ammo stack back a bit
    return resultStack;
  }
}
