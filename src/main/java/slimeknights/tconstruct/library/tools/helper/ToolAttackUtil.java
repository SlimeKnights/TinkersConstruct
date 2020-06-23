package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolData;

public class ToolAttackUtil {

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
      damage = calcCutoffDamage(damage, ((ToolCore) stack.getItem()).getToolDefinition().getBaseStatDefinition().getDamageCutoff());
    }

    return damage;
  }

  public static float calcCutoffDamage(float damage, float cutoff) {
    float p = 1f;
    float d = damage;
    damage = 0f;
    while (d > cutoff) {
      damage += p * cutoff;
      // safety for ridiculous values
      if (p > 0.001f) {
        p *= 0.9f;
      }
      else {
        damage += p * cutoff * ((d / cutoff) - 1f);
        return damage;
      }
      d -= cutoff;
    }

    damage += p * d;

    return damage;
  }
}
