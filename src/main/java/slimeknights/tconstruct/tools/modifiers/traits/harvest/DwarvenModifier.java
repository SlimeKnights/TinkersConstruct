package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.stats.ToolType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

// TODO: convert into a module
public class DwarvenModifier extends Modifier implements ConditionalStatModifierHook, BreakSpeedModifierHook, TooltipModifierHook {
  private static final Component MINING_SPEED = TConstruct.makeTranslation("modifier", "dwarven.mining_speed");
  private static final Component VELOCITY = TConstruct.makeTranslation("modifier", "dwarven.velocity");
  /** Distance below sea level to get boost */
  private static final float BOOST_DISTANCE = 64f;
  /** Blocks above 0 when debuff starts, and the range of debuff in the world */
  private static final float DEBUFF_RANGE = 128f;
  /** Mining speed boost when at distance, gets larger when lower */
  private static final float MINING_BONUS = 6;
  /** Velocity boost when at distance, gets larger when lower */
  private static final float VELOCITY_BONUS = 0.05f;

  private static final ToolType[] TYPES = { ToolType.RANGED, ToolType.MELEE };

  @Override
  public int getPriority() {
    return 85; // after flat boosts, before multipliers
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.CONDITIONAL_STAT, ModifierHooks.BREAK_SPEED, ModifierHooks.TOOLTIP);
  }

  /** Gets the boost for the given level and height, can go negative */
  private static float getBoost(Level world, float y, ModifierEntry entry, float baseSpeed, float bonus) {
    // grants 0 bonus at 64, 1x at -BOOST_DISTANCE, 2x at -2*BOOST_DISTANCE
    // prevents the modifier from getting too explosive in tall worlds, clamp between -6 and 12
    if (y < BOOST_DISTANCE) {
      float scale = Mth.clamp((BOOST_DISTANCE - y) / BOOST_DISTANCE, 0, 2);
      return baseSpeed + (entry.getEffectiveLevel() * scale * bonus);
    }

    // start the debuff 128 blocks below the top, but for short worlds start it 128 blocks above the full boost (so we have 64 blocks of neutral)
    // in the overworld, debuff is between 320 and 128. In the nether, its between 256 and 96
    // the method to get the world's sea level is not reliable, so just using absolute bounds of the world
    float baselineDebuff = Math.max(world.getMaxBuildHeight() - (DEBUFF_RANGE + BOOST_DISTANCE), 96);
    if (y > baselineDebuff) {
      // range of 64 blocks for the regular debuff, anything above is full debuff
      if (y >= baselineDebuff + DEBUFF_RANGE) {
        return baseSpeed * 0.25f;
      }
      // formula goes from 100% at baseline to 25% at baseline+128
      return baseSpeed * (1 - ((y - baselineDebuff) / DEBUFF_RANGE * 0.75f));
    }

    // no boost, no debuff
    return baseSpeed;
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    Optional<BlockPos> pos = event.getPosition();
    if (!isEffective || pos.isEmpty()) {
      return;
    }
    event.setNewSpeed(getBoost(event.getEntity().level(), pos.get().getY(), modifier, event.getNewSpeed(), miningSpeedModifier * tool.getMultiplier(ToolStats.MINING_SPEED) * MINING_BONUS));
  }

  @Override
  public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
    if (stat == ToolStats.VELOCITY) {
      return getBoost(living.level(), (float)living.getY(), modifier, baseValue, multiplier * VELOCITY_BONUS);
    }
    return baseValue;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    ToolType type = ToolType.from(tool.getItem(), TYPES);
    if (type != null) {
      Component prefix = type == ToolType.RANGED ? VELOCITY : MINING_SPEED;
      float baseBoost = type == ToolType.RANGED ? VELOCITY_BONUS : MINING_BONUS;
      double boost;
      if (player != null && key == TooltipKey.SHIFT) {
        // passing in 1 means greater than 1 is a boost, and less than 1 is a percentage
        // the -1 means for percentage, the range is now 0 to -75%, and for flat boost its properly 0 to baseBoost
        boost = getBoost(player.level(), (float)player.getY(), modifier, 1, baseBoost) - 1;
        if (boost < 0) {
          // goes from 0 to -75%, don't show 0%
          if (boost <= -0.01) {
            TooltipModifierHook.addPercentBoost(this, prefix, boost, tooltip);
          }
          return;
        }
      } else {
        boost = baseBoost * modifier.getLevel();
      }
      if (boost >= 0.01) {
        TooltipModifierHook.addFlatBoost(this, prefix, boost * tool.getMultiplier(type == ToolType.RANGED ? ToolStats.VELOCITY : ToolStats.MINING_SPEED), tooltip);
      }
    }
  }
}
