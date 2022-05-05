package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class DwarvenModifier extends Modifier {
  private static final Component MINING_SPEED = TConstruct.makeTranslation("modifier", "dwarven.mining_speed");
  /** Distance below sea level to get boost */
  private static final float BOOST_DISTANCE = 64f;
  /** Blocks above 0 when debuff starts, and the range of debuff in the world */
  private static final float DEBUFF_RANGE = 128f;
  /** Boost when at distance, gets larger when lower */
  private static final float BONUS = 6;

  /** Gets the boost for the given level and height, can go negative */
  private static float getBoost(Level world, int y, int level, float baseSpeed, float modifier) {
    // grants 0 bonus at 64, 1x at -BOOST_DISTANCE, 2x at -2*BOOST_DISTANCE
    // prevents the modifier from getting too explosive in tall worlds, clamp between -6 and 12
    if (y < BOOST_DISTANCE) {
      float scale = Mth.clamp((BOOST_DISTANCE - y) / BOOST_DISTANCE, 0, 2);
      return baseSpeed + (level * scale * BONUS * modifier);
    }

    // start the debuff 128 blocks below the top, but for short worlds start it 128 blocks above the full boost (so we have 64 blocks of neutral)
    // in the overworld, debuff is between 320 and 128. In the nether, its between 256 and 128
    // the method to get the world's sea level is not reliable, so just using absolute bounds of the world
    float baselineDebuff = Math.max(world.getMaxBuildHeight() - (DEBUFF_RANGE + BOOST_DISTANCE), DEBUFF_RANGE);
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
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (!isEffective) {
      return;
    }
    event.setNewSpeed(getBoost(event.getPlayer().level, event.getPos().getY(), level, event.getNewSpeed(), miningSpeedModifier * tool.getMultiplier(ToolStats.MINING_SPEED)));
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    if (tool.hasTag(TinkerTags.Items.HARVEST)) {
      double boost;
      if (player != null && key == TooltipKey.SHIFT) {
        // passing in 1 means greater than 1 is a boost, and less than 1 is a percentage
        // the -1 means for percentage, the range is now 0 to -75%, and for flat boost its properly 0 to BOOST
        boost = getBoost(player.level, (int)player.getY(), level, 1, 1f) - 1;
        if (boost < 0) {
          // goes from 0 to -75%, don't show 0%
          if (boost <= -0.01) {
            addPercentTooltip(MINING_SPEED, boost, tooltip);
          }
          return;
        }
      } else {
        boost = BONUS * level;
      }
      if (boost >= 0.01) {
        addFlatBoost(MINING_SPEED, boost * tool.getMultiplier(ToolStats.MINING_SPEED), tooltip);
      }
    }
  }
}
