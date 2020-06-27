package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

public class ToolAttackUtil {

  /**
   * Gets the actual damage a tool does
   *
   * @param stack the ItemStack to check
   * @param player the current player
   * @return the actual damage of the tool
   */
  public static float getActualDamage(ItemStack stack, LivingEntity player) {
    float damage = (float) SharedMonsterAttributes.ATTACK_DAMAGE.getDefaultValue();

    if (player != null) {
      damage = (float) player.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
    }

    float toolDamage = ToolData.from(stack).getStats().attack;

    if (!stack.isEmpty() && stack.getItem() instanceof ToolCore) {
      toolDamage *= ((ToolCore) stack.getItem()).getToolDefinition().getBaseStatDefinition().getDamageModifier();
    }

    damage += toolDamage;

    if (stack.getItem() instanceof ToolCore) {
      damage = calculateCutoffDamage(damage, ((ToolCore) stack.getItem()).getToolDefinition().getBaseStatDefinition().getDamageCutoff());
    }

    return damage;
  }

  /**
   * Used to calculate the damage to start doing diminishing returns
   *
   * @param damageIn the current damage the tool does
   * @param cutoffDamage the fixed damage value for the diminishing effects to kick in
   * @return the damage to use from the cutoff
   */
  public static float calculateCutoffDamage(float damageIn, float cutoffDamage) {
    float percent = 1f;
    float oldDamage = damageIn;

    damageIn = 0f;
    while (oldDamage > cutoffDamage) {
      damageIn += percent * cutoffDamage;
      // safety for ridiculous values
      if (percent > 0.001f) {
        percent *= 0.9f;
      }
      else {
        damageIn += percent * cutoffDamage * ((oldDamage / cutoffDamage) - 1f);
        return damageIn;
      }

      oldDamage -= cutoffDamage;
    }

    damageIn += percent * oldDamage;

    return damageIn;
  }
}
