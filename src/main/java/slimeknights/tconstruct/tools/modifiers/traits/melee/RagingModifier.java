package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

/** Modifier that boosts damage at low health */
public class RagingModifier extends Modifier {
  private static final float LOWEST_HEALTH = 2f;
  private static final float HIGHEST_HEALTH = 10f;
  private static final float DAMAGE_PER_LEVEL = 4f;

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
  public float getEntityDamage(IToolStackView tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    return damage + getBonus(context.getAttacker(), level);
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag flag) {
    float bonus = level * 4;
    if (player != null && key == TooltipKey.SHIFT) {
      bonus = getBonus(player, level);
    }
    addDamageTooltip(tool, bonus, tooltip);
  }
}
