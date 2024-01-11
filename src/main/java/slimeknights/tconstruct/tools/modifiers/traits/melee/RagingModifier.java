package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @deprecated use {@link slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule}
 * and {@link slimeknights.tconstruct.library.modifiers.modules.behavior.ConditionalStatModule}
 */
@Deprecated
public class RagingModifier extends Modifier implements ConditionalStatModifierHook {
  private static final float LOWEST_HEALTH = 2f;
  private static final float HIGHEST_HEALTH = 10f;
  private static final float DAMAGE_PER_LEVEL = 4f;
  private static final float DRAWSPEED_PER_LEVEL = 0.25f;

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, TinkerHooks.CONDITIONAL_STAT);
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
      return level;
      // if below highest health, scale boost
    } else if (health < HIGHEST_HEALTH) {
      return level * (HIGHEST_HEALTH - health)  / (HIGHEST_HEALTH - LOWEST_HEALTH);
    }
    return 0;
  }

  @Override
  public float getEntityDamage(IToolStackView tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    return damage + getBonus(context.getAttacker(), level) * DAMAGE_PER_LEVEL * tool.getMultiplier(ToolStats.ATTACK_DAMAGE);
  }

  @Override
  public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
    if (stat == ToolStats.DRAW_SPEED) {
      return baseValue + getBonus(living, modifier.getLevel()) * DRAWSPEED_PER_LEVEL * multiplier;
    }
    return  baseValue;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag flag) {
    float bonus = level;
    if (player != null && key == TooltipKey.SHIFT) {
      bonus = getBonus(player, level);
    }
    if (bonus > 0) {
      addDamageTooltip(tool, bonus * DAMAGE_PER_LEVEL, tooltip);
      addStatTooltip(tool, ToolStats.DRAW_SPEED, TinkerTags.Items.RANGED, bonus * DRAWSPEED_PER_LEVEL, tooltip);
    }
  }
}
