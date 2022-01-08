package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

/** Modifier that boosts damage at low health */
public class RagingModifier extends Modifier {
  private static final float LOWEST_HEALTH = 2f;
  private static final float HIGHEST_HEALTH = 10f;
  private static final float DAMAGE_PER_LEVEL = 4f;

  public RagingModifier() {
    super(0xB30000);
  }

  /** Gets the bonus for the given health */
  private static float getBonus(LivingEntity attacker, int level) {
    float health = attacker.getHealth();
    // if the max health is less than our range of boost, decrease the max possible boost
    float max = attacker.getMaxHealth();
    if (max < HIGHEST_HEALTH) {
      health += HIGHEST_HEALTH - max;
    }

    // if we are below the point of lowest health, apply full boost
    if (health <= LOWEST_HEALTH) {
      return level * DAMAGE_PER_LEVEL;
      // if below highest health, scale boost
    } else if (health < HIGHEST_HEALTH) {
      return level * DAMAGE_PER_LEVEL * (HIGHEST_HEALTH - health)  / (HIGHEST_HEALTH - LOWEST_HEALTH);
    }
    return 0;
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    return damage + getBonus(context.getAttacker(), level);
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, @Nullable PlayerEntity player, List<ITextComponent> tooltip, TooltipKey key, TooltipFlag flag) {
    float bonus = level * 4;
    if (player != null && key == TooltipKey.SHIFT) {
      bonus = getBonus(player, level);
    }
    addDamageTooltip(tool, bonus, tooltip);
  }
}
