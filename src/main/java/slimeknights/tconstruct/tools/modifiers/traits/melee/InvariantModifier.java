package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

public class InvariantModifier extends Modifier implements ConditionalStatModifierHook, MeleeDamageModifierHook, TooltipModifierHook {
  private static final float BASELINE_TEMPERATURE = 0.75f;
  private static final float MAX_TEMPERATURE = 1.25f;
  private static final float DAMAGE = 2.5f / MAX_TEMPERATURE;
  private static final float ACCURACY = 0.15f / MAX_TEMPERATURE;

  /** Gets the bonus for this modifier */
  private static float getBonus(LivingEntity living, int level) {
    // temperature ranges from 0 to 1.25. multiplication makes it go from 0 to 2.5
    BlockPos pos = living.blockPosition();
    return ((MAX_TEMPERATURE - Math.abs(BASELINE_TEMPERATURE - living.level.getBiome(pos).value().getTemperature(pos))) * level);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.CONDITIONAL_STAT, TinkerHooks.MELEE_DAMAGE, TinkerHooks.TOOLTIP);
  }

  @Override
  public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
    return damage + getBonus(context.getAttacker(), modifier.getLevel()) * DAMAGE * tool.getMultiplier(ToolStats.ATTACK_DAMAGE);
  }

  @Override
  public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
    if (stat == ToolStats.ACCURACY) {
      return baseValue + getBonus(living, modifier.getLevel()) * ACCURACY * multiplier;
    }
    return baseValue;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    int level = modifier.getLevel();
    float bonus;
    if (player != null && key == TooltipKey.SHIFT) {
      bonus = getBonus(player, level);
    } else {
      bonus = level * MAX_TEMPERATURE;
    }
    if (bonus > 0.01f) {
      if (tool.hasTag(TinkerTags.Items.RANGED)) {
        TooltipModifierHook.addStatBoost(tool, this, ToolStats.ACCURACY, TinkerTags.Items.RANGED, bonus * ACCURACY, tooltip);
      } else {
        TooltipModifierHook.addDamageBoost(tool, this, bonus * DAMAGE, tooltip);
      }
    }
  }
}
