package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

/**
 * Helper methods that are used when the tool interacts with the world or other things
 */
public class ToolInteractionUtil {

  /**
   * Damages the tool. Entity is only needed in case the tool breaks for rendering the break effect.
   */
  public static void damageTool(ItemStack stack, int amount, LivingEntity entity) {
    ToolData toolData = ToolData.from(stack);
    StatsNBT stats = toolData.getStats();
    if (amount == 0 || stats.broken) {
      return;
    }

    int actualAmount = amount;

    // todo: trait nbt
    /*for (ITrait trait : TinkerUtil.getTraitsOrdered(stack)) {
      if (amount > 0) {
        actualAmount = trait.onToolDamage(stack, amount, actualAmount, entity);
      } else {
        actualAmount = trait.onToolHeal(stack, amount, actualAmount, entity);
      }
    }*/

    // extra compatibility for unbreaking.. because things just love to mess it up.. like 3rd party stuff
    // TiC unbreaking is handled by the modifier/trait inself
    if (actualAmount > 0 && isVanillaUnbreakable(stack)) {
      actualAmount = 0;
    }

    // ensure we never deal more damage than durability
    int currentDurability = ToolCore.getCurrentDurability(stack);
    actualAmount = Math.min(actualAmount, currentDurability);
    stack.setDamage(stack.getDamage() + actualAmount);

    if (entity instanceof ServerPlayerEntity) {
      if (actualAmount != 0) {
        CriteriaTriggers.ITEM_DURABILITY_CHANGED.trigger((ServerPlayerEntity) entity, stack, stack.getDamage() + actualAmount);
      }

      if (currentDurability <= 0) {
        ToolBreakUtil.breakTool(stack);
        // todo: move this to proxy
        ToolBreakUtil.triggerToolBreakAnimation(stack, (ServerPlayerEntity) entity);
      }
    }
  }

  /**
   * See e.g. {@link ItemStack#isDamageable()}
   */
  private static boolean isVanillaUnbreakable(ItemStack stack) {
    CompoundNBT compoundnbt = stack.getTag();
    return compoundnbt != null && compoundnbt.getBoolean("Unbreakable");
  }
}
