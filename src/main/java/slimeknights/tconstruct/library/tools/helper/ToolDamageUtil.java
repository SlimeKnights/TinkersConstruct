package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Because, really, there's way too much stuff that handles breaking/unbreaking and broken tools.
 */
public class ToolDamageUtil {
  /**
   * Marks a tool as broken
   * @param stack  Tool stack
   */
  public static void breakTool(ItemStack stack) {
    ToolData toolData = ToolData.from(stack);
    if(!toolData.getStats().broken) {
      toolData.createNewDataWithBroken(true).updateStack(stack);
    }
  }

  /**
   * Gets the current tool durability. Essentially the reverse of {@link #getCurrentDamage(ItemStack)}
   *
   * @param stack the tool stack to use
   * @return the currently durability of the tool stack
   */
  public static int getCurrentDurability(ItemStack stack) {
    return getCurrentDurability(stack, ToolData.from(stack));
  }

  /**
   * Gets the current tool durability. Same as {@link #getCurrentDurability(ItemStack)}, but use when a tool data instance exists
   *
   * @param stack the tool stack to use
   * @param data  current tool data to use
   * @return the currently durability of the tool stack
   */
  public static int getCurrentDurability(ItemStack stack, ToolData data) {
    StatsNBT stats = data.getStats();
    if (stats.broken) {
      return 0;
    }
    return stats.durability - stack.getDamage();
  }

  /**
   * Gets the current damge the tool has taken. Essentially the reverse of {@link #getCurrentDurability(ItemStack)}
   *
   * @param stack the tool stack to use
   * @return the currently durability of the tool stack
   */
  public static int getCurrentDamage(ItemStack stack) {
    StatsNBT stats = ToolData.from(stack).getStats();
    if (stats.broken) {
      return stats.durability;
    }
    return stack.getDamage();
  }

  /**
   * Checks if vanilla marked this tool unbreakable
   * @param stack  Tool stack
   * @return True if vanilla unbreakable
   */
  private static boolean isVanillaUnbreakable(ItemStack stack) {
    CompoundNBT tag = stack.getTag();
    return tag != null && tag.getBoolean("Unbreakable");
  }

  /**
   * Damages the tool.  Should not be called directly, just use {@link ItemStack#damageItem(int, LivingEntity, Consumer)}
   * @param stack   Tool stack
   * @param amount  Amount to damage
   * @param entity  Entity damaging the tool
   * @return true if the tool broke when damaging
   */
  public static boolean damageTool(ItemStack stack, int amount, @Nullable LivingEntity entity) {
    ToolData toolData = ToolData.from(stack);
    StatsNBT stats = toolData.getStats();
    if (amount <= 0 || stats.broken || isVanillaUnbreakable(stack)) {
      return false;
    }

    // todo: modifiers

    // ensure we never deal more damage than current durability
    int damage = stack.getDamage();
    int currentDurability = stats.durability - damage;
    amount = Math.min(amount, currentDurability);
    if (amount > 0) {
      // criteria updates
      int newDamage = damage + amount;
      if (entity instanceof ServerPlayerEntity) {
        CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger((ServerPlayerEntity) entity, stack, newDamage);
      }

      // actual damage
      stack.setDamage(newDamage);
      return newDamage >= stats.durability;
    }
    return false;
  }

  /**
   * Checks if the given stack needs to be repaired
   * @param stack  Stack to check
   * @return  True if it needs repair
   */
  public static boolean needsRepair(ItemStack stack) {
    return stack.getDamage() > 0 || ToolData.isBroken(stack);
  }

  /**
   * Repairs the given tool stack
   * @param stack   Stack to repair
   * @param amount  Amount to repair
   * @param entity  Entity repairing the tool
   */
  public static void repairTool(ItemStack stack, int amount, @Nullable LivingEntity entity) {
    if (amount <= 0) {
      return;
    }

    // if broken, treat damage as max
    ToolData data = ToolData.from(stack);
    StatsNBT stats = data.getStats();
    int damage = stats.broken ? stats.durability : stack.getDamage();
    if (damage == 0) {
      return;
    }

    // todo: modifiers

    // remove broken tag
    if (stats.broken) {
      data.createNewDataWithBroken(false).updateStack(stack);
    }

    // ensure we never repair more than max durability
    int newDamage = damage - Math.min(amount, damage);
    stack.setDamage(newDamage);

    // trigger criteria updates
    if (entity instanceof ServerPlayerEntity) {
      CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger((ServerPlayerEntity) entity, stack, newDamage);
    }
  }
}
