package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.stats.ToolType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static slimeknights.tconstruct.library.modifiers.modules.behavior.ReduceToolDamageModule.reduceDamage;

public class TemperateModifier extends Modifier implements ConditionalStatModifierHook, ToolDamageModifierHook, BreakSpeedModifierHook, TooltipModifierHook, ProtectionModifierHook {
  private static final float MAX_TEMPERATURE = 1.25f;
  private static final float BASELINE_TEMPERATURE = 0.75f;
  private static final float MAX_MINING_BOOST = 7.5f / MAX_TEMPERATURE;
  private static final float MAX_DRAWSPEED_BOOST = 0.15f / MAX_TEMPERATURE;
  private static final Component SPEED = TConstruct.makeTranslation("modifier", "temperate.speed");
  private static final Component REINFORCED = TConstruct.makeTranslation("modifier", "temperate.reinforced");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.CONDITIONAL_STAT, ModifierHooks.TOOL_DAMAGE, ModifierHooks.BREAK_SPEED, ModifierHooks.TOOLTIP, ModifierHooks.PROTECTION);
  }

  /** Default logic that starting at 25% gives a bonus of 5% less per level */
  private static float diminishingPercent(float level) {
    // formula gives 25%, 45%, 60%, 70%, 75% for first 5 levels
    if (level < 5) {
      return 0.025f * level * (11 - level);
    }
    // after level 5.5 the above formula breaks, so just do +5% per level
    // means for levels 6 to 10, you get 80%, 85%, 90%, 95%, 100%
    // in default config we never go past level 5, but nice for datapacks to allow
    return 0.75f + (level - 5) * 0.05f;
  }
  /** Gets the bonus for the given position */
  private static float getBonus(LivingEntity living, BlockPos pos) {
    // temperature ranges from -1.25 to 1.25, so make it go -1 to 1
    // negative is cold, positive is hot
    return (living.level().getBiome(pos).value().getTemperature(pos) - BASELINE_TEMPERATURE);
  }

  @Override
  public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
    // less damage in the heat
    if (holder != null) {
      float bonus = getBonus(holder, holder.blockPosition());
      if (bonus > 0) {
        return reduceDamage(amount, diminishingPercent(bonus * 2 * modifier.getEffectiveLevel() / MAX_TEMPERATURE));
      }
    }
    return amount;
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    // break faster in the cold
    Optional<BlockPos> pos = event.getPosition();
    if (isEffective && pos.isPresent()) {
      float bonus = getBonus(event.getEntity(), pos.get());
      if (bonus < 0) {
        // temperature ranges from 0 to 1.25. Division makes it 0 to 0.125 per level
        event.setNewSpeed(event.getNewSpeed() - (bonus * MAX_MINING_BOOST * tool.getMultiplier(ToolStats.MINING_SPEED) * miningSpeedModifier * modifier.getEffectiveLevel()));
      }
    }
  }

  @Override
  public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
    // draw faster in the cold
    if (stat == ToolStats.DRAW_SPEED) {
      float bonus = getBonus(living, living.blockPosition());
      if (bonus < 0) {
        baseValue -= bonus * MAX_DRAWSPEED_BOOST * multiplier * modifier.getEffectiveLevel();
      }
    }
    return baseValue;
  }

  @Override
  public float getProtectionModifier(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float modifierValue) {
    if (DamageSourcePredicate.CAN_PROTECT.matches(source)) {
      LivingEntity target = context.getEntity();
      float bonus = getBonus(target, target.blockPosition());
      if (bonus < 0) {
        modifierValue -= bonus * modifier.getEffectiveLevel();
      }
    }
    return modifierValue;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    ToolType type = ToolType.from(tool.getItem(), ToolType.NO_MELEE);
    if (type != null) {
      float bonus = entry.getEffectiveLevel();
      if (player != null && key == TooltipKey.SHIFT) {
        bonus *= getBonus(player, player.blockPosition());
      } else {
        bonus *= -1;
      }
      if (bonus < -0.01f) {
        if (type == ToolType.ARMOR) {
          ProtectionModule.addResistanceTooltip(tool, entry.getModifier(), -bonus, player, tooltip);
        } else {
          float value;
          if (type == ToolType.HARVEST) {
            value = -bonus * tool.getMultiplier(ToolStats.MINING_SPEED) * MAX_MINING_BOOST;
          } else {
            value = -bonus * tool.getMultiplier(ToolStats.DRAW_SPEED) * MAX_DRAWSPEED_BOOST;
          }
          TooltipModifierHook.addFlatBoost(entry.getModifier(), SPEED, value, tooltip);
        }
      }
      if (bonus > 0.01f) {
        tooltip.add(applyStyle(Component.literal(Util.PERCENT_FORMAT.format(diminishingPercent(bonus * 2 / MAX_TEMPERATURE)) + " ").append(REINFORCED)));
      }
    }
  }
}
