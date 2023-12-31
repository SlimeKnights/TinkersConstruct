package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.hook.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.library.modifiers.modules.behavior.ReduceToolDamageModule.reduceDamage;
import static slimeknights.tconstruct.tools.modifiers.upgrades.general.ReinforcedModifier.diminishingPercent;

public class TemperateModifier extends Modifier implements ConditionalStatModifierHook {
  private static final float BASELINE_TEMPERATURE = 0.75f;
  private static final float MAX_MINING_BOOST = 7.5f;
  private static final float MAX_DRAWSPEED_BOOST = 0.15f;
  private static final Component SPEED = TConstruct.makeTranslation("modifier", "temperate.speed");
  private static final Component REINFORCED = TConstruct.makeTranslation("modifier", "temperate.reinforced");

  /** Gets the bonus for the given position */
  private static float getBonus(LivingEntity living, BlockPos pos, int level) {
    // temperature ranges from -1.25 to 1.25, so make it go -1 to 1
    // negative is cold, positive is hot
    return (living.level.getBiome(pos).value().getTemperature(pos) - BASELINE_TEMPERATURE) * level / 1.25f;
  }

  @Override
  public int onDamageTool(IToolStackView tool, int level, int amount, @Nullable LivingEntity holder) {
    // less damage in the heat
    if (holder != null) {
      float bonus = getBonus(holder, holder.blockPosition(), level);
      if (bonus > 0) {
        return reduceDamage(amount, diminishingPercent(bonus * 2));
      }
    }
    return amount;
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    // break faster in the cold
    if (isEffective) {
      float bonus = getBonus(event.getPlayer(), event.getPos(), level);
      if (bonus < 0) {
        // temperature ranges from 0 to 1.25. Division makes it 0 to 0.125 per level
        event.setNewSpeed(event.getNewSpeed() - (bonus * MAX_MINING_BOOST * tool.getMultiplier(ToolStats.MINING_SPEED) * miningSpeedModifier));
      }
    }
  }

  @Override
  public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
    // draw faster in the cold
    if (stat == ToolStats.DRAW_SPEED) {
      float bonus = getBonus(living, living.blockPosition(), modifier.getLevel());
      if (bonus < 0) {
        baseValue -= bonus * MAX_DRAWSPEED_BOOST * multiplier;
      }
    }
    return baseValue;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag flag) {
    boolean harvest = tool.hasTag(TinkerTags.Items.HARVEST);
    if (harvest || tool.hasTag(TinkerTags.Items.RANGED)) {
      float bonus;
      if (player != null && key == TooltipKey.SHIFT) {
        bonus = getBonus(player, player.blockPosition(), level);
      } else {
        bonus = -level;
      }
      if (bonus < -0.01f) {
        if (harvest) {
          addFlatBoost(SPEED, -bonus * tool.getMultiplier(ToolStats.MINING_SPEED) * MAX_MINING_BOOST, tooltip);
        } else {
          addFlatBoost(SPEED, -bonus * tool.getMultiplier(ToolStats.DRAW_SPEED) * MAX_DRAWSPEED_BOOST, tooltip);
        }
      }
      if (bonus > 0.01f) {
        tooltip.add(applyStyle(new TextComponent(Util.PERCENT_FORMAT.format(diminishingPercent(bonus * 2)) + " ").append(REINFORCED)));
      }
    }
  }
}
