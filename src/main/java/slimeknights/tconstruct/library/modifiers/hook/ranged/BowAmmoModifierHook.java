package slimeknights.tconstruct.library.modifiers.hook.ranged;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/** Hook to find ammo on a bow.*/
public interface BowAmmoModifierHook {
  /** Volatile data key telling the tool to not fetch ammo from the inventory. */
  ResourceLocation SKIP_INVENTORY_AMMO = TConstruct.getResource("skip_inventory_ammo");

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

  /** @deprecated use {@link #getAmmo(IToolStackView, ItemStack, LivingEntity, Predicate)} */
  @Deprecated(forRemoval = true)
  static boolean hasAmmo(IToolStackView tool, ItemStack bowStack, Player player, Predicate<ItemStack> predicate) {
    return !getAmmo(tool, bowStack, player, predicate).isEmpty();
  }

  /** @deprecated use {@link #getAmmo(IToolStackView, ItemStack, LivingEntity, Predicate)} */
  @Deprecated(forRemoval = true)
  static boolean hasAmmo(IToolStackView tool, ItemStack bowStack, LivingEntity living, Predicate<ItemStack> predicate) {
    return !getAmmo(tool, bowStack, living, predicate).isEmpty();
  }

  /**
   * Gets ammo for the given tool without consuming it.
   * @param tool       Tool instance, for running modifier hooks
   * @param bow   Bow stack instance, for standard ammo lookup
   * @param living     Player instance, for standard ammo lookup
   * @param predicate  Predicate for finding ammo in modifiers
   * @return  True if there is ammo either on the player or on the modifiers
   */
  static ItemStack getAmmo(IToolStackView tool, ItemStack bow, LivingEntity living, @Nullable Predicate<ItemStack> predicate) {
    // if no predicate, means we want the event result, used for ballista
    if (predicate == null) {
      return ForgeHooks.getProjectile(living, bow, ItemStack.EMPTY);
    }
    ItemStack standardAmmo = tool.getVolatileData().getBoolean(SKIP_INVENTORY_AMMO) ? ItemStack.EMPTY : living.getProjectile(bow);
    for (ModifierEntry entry : tool.getModifierList()) {
      ItemStack ammo = entry.getHook(ModifierHooks.BOW_AMMO).findAmmo(tool, entry, living, standardAmmo, predicate);
      if (!ammo.isEmpty()) {
        return ammo;
      }
    }
    return standardAmmo;
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

  /** @deprecated use {@link #consumeAmmo(IToolStackView, ItemStack, LivingEntity, Player, Predicate)} */
  @Deprecated(forRemoval = true)
  static ItemStack findAmmo(IToolStackView tool, ItemStack bow, Player player, Predicate<ItemStack> predicate) {
    return consumeAmmo(tool, bow, player, player, predicate);
  }

  /** Gets the number of projectiles desired for the given tool */
  static int getDesiredProjectiles(IToolStackView tool) {
    return 1 + (2 * tool.getModifierLevel(TinkerModifiers.multishot.getId()));
  }

  /**
   * Finds ammo in the inventory, and consume it if not creative
   * @param tool       Tool instance
   * @param bow        Bow stack instance
   * @param predicate  Predicate for valid ammo
   * @param living     Living entity to search.
   * @param player     Player firing bow. If null, will not remove the fired stack from the player inventory.
   * @return  Found ammo
   */
  static ItemStack consumeAmmo(IToolStackView tool, ItemStack bow, LivingEntity living, @Nullable Player player, @Nullable Predicate<ItemStack> predicate) {
    return consumeAmmo(tool, bow, living, player, predicate, getDesiredProjectiles(tool));
  }

  /**
   * Finds ammo in the inventory, and consume it if not creative
   * @param tool       Tool instance
   * @param bow        Bow stack instance
   * @param predicate  Predicate for valid ammo
   * @param living     Living entity to search.
   * @param player     Player firing bow. If null, will not remove the fired stack from the player inventory.
   * @param projectilesDesired  Number of projectiles to locate at maximum.
   * @return  Found ammo
   */
  static ItemStack consumeAmmo(IToolStackView tool, ItemStack bow, LivingEntity living, @Nullable Player player, @Nullable Predicate<ItemStack> predicate, int projectilesDesired) {
    // treat client side as creative, no need to shrink the stacks clientside
    Level level = living.level();
    boolean creative = (player != null && player.getAbilities().instabuild) || level.isClientSide;

    // first search, find what ammo type we want
    boolean skipInventoryAmmo = tool.getVolatileData().getBoolean(SKIP_INVENTORY_AMMO);
    ItemStack standardAmmo;
    if (skipInventoryAmmo) {
      standardAmmo = ItemStack.EMPTY;
    } else if (predicate == null) {
      // no predicate means we just want the event result to start, used for ballista
      standardAmmo = ForgeHooks.getProjectile(living, bow, ItemStack.EMPTY);
    } else {
      standardAmmo = living.getProjectile(bow);
    }
    ItemStack resultStack = ItemStack.EMPTY;
    if (predicate != null) {
      for (ModifierEntry entry : tool.getModifierList()) {
        BowAmmoModifierHook hook = entry.getHook(ModifierHooks.BOW_AMMO);
        ItemStack ammo = hook.findAmmo(tool, entry, living, standardAmmo, predicate);
        if (!ammo.isEmpty()) {
          // if creative, we are done, just return the ammo with the given size
          if (creative) {
            return ItemHandlerHelper.copyStackWithSize(ammo, projectilesDesired);
          }

          // not creative, split out the desired amount. We may have to do more work if it is too small
          resultStack = ItemHandlerHelper.copyStackWithSize(ammo, Math.min(projectilesDesired, ammo.getCount()));
          hook.shrinkAmmo(tool, entry, living, ammo, resultStack.getCount());
          break;
        }
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
      if (standardAmmo.isEmpty() && player != null) {
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
      if (!skipInventoryAmmo && standardAmmo.isEmpty()) {
        standardAmmo = findMatchingAmmo(bow, living, predicate);
      }
      // next, try asking modifiers if they have anything new again
      int needed = projectilesDesired - resultStack.getCount();
      for (ModifierEntry entry : tool.getModifierList()) {
        BowAmmoModifierHook hook = entry.getHook(ModifierHooks.BOW_AMMO);
        ItemStack ammo = hook.findAmmo(tool, entry, living, standardAmmo, predicate);
        if (!ammo.isEmpty()) {
          // consume as much of the stack as we need then continue, loop condition will stop if we are now done
          int gained = Math.min(needed, ammo.getCount());
          hook.shrinkAmmo(tool, entry, living, ammo, gained);
          resultStack.grow(gained);
          continue hasEnough;
        }
      }
      // no standard and no modifier found means we give up
      if (standardAmmo.isEmpty()) {
        break;
      }

      // if we have standard ammo, take what we can then loop again
      if (needed > standardAmmo.getCount()) {
        // consume the whole stack
        resultStack.grow(standardAmmo.getCount());
        if (player != null) {
          player.getInventory().removeItem(standardAmmo);
        }
        standardAmmo = ItemStack.EMPTY;
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
