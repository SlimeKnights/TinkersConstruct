package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

/** Deals a percentage boost in damage when on fire */
public class ConductingModifier extends Modifier implements ConditionalStatModifierHook, TooltipModifierHook, MeleeDamageModifierHook {
  private static final Component BOOST = TConstruct.makeTranslation("modifier", "conducting.boost");
  private static final int MAX_BONUS_TICKS = 10 * 20; // 2/3 time from lava
  private static final float PERCENT_PER_LEVEL = 0.2f; // 20% bonus when in lava essentially

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.CONDITIONAL_STAT, ModifierHooks.TOOLTIP, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MONSTER_MELEE_DAMAGE);
  }

  @Override
  public int getPriority() {
    return 90;
  }

  /** Gets the bonus damage for the given entity and level */
  public static float bonusScale(LivingEntity living) {
    int fire = living.getRemainingFireTicks();
    if (fire > 0) {
      float bonus = 1;
      // if less than 15 seconds of fire, smaller boost
      if (fire < MAX_BONUS_TICKS) {
        bonus *= (float)(fire) / MAX_BONUS_TICKS;
      }
      // half boost if not on fire
      if (living.hasEffect(MobEffects.FIRE_RESISTANCE)) {
        bonus /= 2;
      }
      return bonus;
    }
    return 0;
  }

  @Override
  public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
    float bonus = bonusScale(context.getAttacker()) * modifier.getEffectiveLevel() * PERCENT_PER_LEVEL;
    if (bonus > 0) {
      damage *= 1 + bonus;
    }
    return damage;
  }

  @Override
  public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
    if (stat == ToolStats.PROJECTILE_DAMAGE) {
      float bonus = bonusScale(living) * modifier.getEffectiveLevel() * PERCENT_PER_LEVEL;
      if (bonus > 0) {
        baseValue *= 1 + bonus;
      }
    }
    return baseValue;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    if (tool.hasTag(TinkerTags.Items.RANGED) || tool.hasTag(TinkerTags.Items.MELEE)) {
      float bonus = (PERCENT_PER_LEVEL) * modifier.getLevel();
      // client only knows if the player is on fire or not, not the amount of fire, so just show full if on fire
      if (player != null && key == TooltipKey.SHIFT && player.getRemainingFireTicks() == 0) {
        bonus = 0;
      }
      tooltip.add(applyStyle(Component.literal(Util.PERCENT_BOOST_FORMAT.format(bonus) + " ").append(BOOST)));
    }
  }
}
