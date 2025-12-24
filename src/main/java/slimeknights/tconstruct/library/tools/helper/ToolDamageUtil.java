package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Handles tool damage and repair, along with a quick broken check
 */
public class ToolDamageUtil {
  /**
   * Raw method to set a tool as broken. Bypasses {@link ToolStack} for the sake of things that may not be a full Tinker Tool
   * @param stack  Tool stack
   */
  public static void breakTool(ItemStack stack) {
    stack.getOrCreateTag().putBoolean(ToolStack.TAG_BROKEN, true);
  }

  /**
   * Checks if the given stack is broken
   * @param stack  Stack to check
   * @return  True if broken
   */
  public static boolean isBroken(ItemStack stack) {
    CompoundTag nbt = stack.getTag();
    return nbt != null && nbt.getBoolean(ToolStack.TAG_BROKEN);
  }

  /**
   * Gets the max damage for use in {@link net.minecraft.world.item.Item#getMaxDamage(ItemStack)}.
   * Intentionally avoids ever letting the tool's max damage exceed its current.
   * For normal tool usages, see {@link ToolStack#getStats()} with {@link ToolStats#DURABILITY}.
   */
  public static int getFakeMaxDamage(ItemStack stack) {
    if (!stack.getItem().canBeDepleted()) {
      return 0;
    }
    ToolStack tool = ToolStack.from(stack);
    int durability = tool.getStats().getInt(ToolStats.DURABILITY);
    // vanilla deletes tools if max damage == getDamage, so tell vanilla our max is one higher when broken
    return tool.isBroken() ? durability + 1 : durability;
  }


  /* Damaging and repairing */

  /**
   * Directly damages the tool, bypassing modifier hooks
   * @param tool    Tool to damage
   * @param amount  Amount to damage
   * @param entity  Entity holding the tool
   * @param stack   Stack being damaged
   * @return  True if the tool is broken now
   */
  public static boolean directDamage(IToolStackView tool, int amount, @Nullable LivingEntity entity, @Nullable ItemStack stack) {
    if (entity instanceof Player player && player.isCreative()) {
      return false;
    }

    int durability = tool.getStats().getInt(ToolStats.DURABILITY);
    int damage = tool.getDamage();
    int current = durability - damage;
    amount = Math.min(amount, current);
    if (amount > 0) {
      // criteria updates
      int newDamage = damage + amount;
      // TODO: needed?
      if (entity instanceof ServerPlayer player) {
        if (stack == null) {
          stack = entity.getMainHandItem();
        }
        CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger(player, stack, newDamage);
      }

      tool.setDamage(newDamage);
      return newDamage >= durability;
    }
    return false;
  }

  /**
   * Damages the tool by the given amount
   * @param amount  Amount to damage
   * @param entity  Entity for criteria updates, if null no updates run
   * @param stack   Stack to use for criteria updates, if null uses main hand stack
   * @return true if the tool broke when damaging
   */
  public static boolean damage(IToolStackView tool, int amount, @Nullable LivingEntity entity, @Nullable ItemStack stack) {
    if (amount <= 0 || tool.isBroken() || tool.isUnbreakable() || !tool.hasTag(TinkerTags.Items.DURABILITY)) {
      return false;
    }

    // try each modifier
    for (ModifierEntry entry : tool.getModifierList()) {
      amount = entry.getHook(ModifierHooks.TOOL_DAMAGE).onDamageTool(tool, entry, amount, entity, stack);
      // if no more damage, done
      if (amount <= 0) {
        return false;
      }
    }
    return directDamage(tool, amount, entity, stack);
  }

  /**
   * Damages the tool and sends the break animation if it broke
   * @param tool    Tool to damage
   * @param amount  Amount of damage
   * @param entity  Entity for animation
   * @param slot    Slot containing the stack
   */
  public static boolean damageAnimated(IToolStackView tool, int amount, LivingEntity entity, EquipmentSlot slot) {
    if (damage(tool, amount, entity, entity.getItemBySlot(slot))) {
      entity.broadcastBreakEvent(slot);
      return true;
    }
    return false;
  }

  /**
   * Damages the tool and sends the break animation if it broke
   * @param tool    Tool to damage
   * @param amount  Amount of damage
   * @param entity  Entity for animation
   * @param hand    Hand containing the stack
   * @return true if the tool broke when damaging
   */
  public static boolean damageAnimated(IToolStackView tool, int amount, LivingEntity entity, InteractionHand hand) {
    if (damage(tool, amount, entity, entity.getItemInHand(hand))) {
      entity.broadcastBreakEvent(hand);
      // TODO: why don't we fire ForgeEventFactory.onPlayerDestroyItem here?
      return true;
    }
    return false;
  }

  /**
   * Damages the tool in the main hand and sends the break animation if it broke
   * @param tool    Tool to damage
   * @param amount  Amount of damage
   * @param entity  Entity for animation
   */
  public static boolean damageAnimated(IToolStackView tool, int amount, LivingEntity entity) {
    return damageAnimated(tool, amount, entity, entity.isUsingItem() ? entity.getUsedItemHand() : InteractionHand.MAIN_HAND);
  }

  /** Implements {@link net.minecraft.world.item.Item#damageItem(ItemStack, int, LivingEntity, Consumer)} for a modifiable item */
  public static <T extends LivingEntity> void handleDamageItem(ItemStack stack, int amount, T damager, Consumer<T> onBroken) {
    // We basically emulate Itemstack.damageItem here. We always return 0 to skip the handling in ItemStack.
    // If we don't tools ignore our damage logic
    if (stack.getItem().canBeDepleted() && ToolDamageUtil.damage(ToolStack.from(stack), amount, damager, stack)) {
      onBroken.accept(damager);
    }
  }

  /**
   * Repairs the given tool stack
   * @param amount  Amount to repair
   */
  public static void repair(IToolStackView tool, int amount) {
    if (amount <= 0) {
      return;
    }

    // if undamaged, nothing to do
    int damage = tool.getDamage();
    if (damage == 0) {
      return;
    }

    // note modifiers are run in the recipe instead

    // ensure we never repair more than max durability
    int newDamage = damage - Math.min(amount, damage);
    tool.setDamage(newDamage);
  }
}
