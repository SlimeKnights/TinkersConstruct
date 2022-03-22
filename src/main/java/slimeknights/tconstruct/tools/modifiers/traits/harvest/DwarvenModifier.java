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
  /** Boost when at distance, gets larger when lower */
  private static final float BONUS = 6;

  /** Gets the boost for the given level and height, can go negative */
  private static float getBoost(Level world, int y, int level) {
    // prevent the modifier from getting too explosive in tall worlds, clamp between -6 and 12
    float bonus = Mth.clamp((world.getSeaLevel() - y) / BOOST_DISTANCE, -1, 2);
    // grants 0 bonus at sea level, -1x at BOOST_DISTANCE, 1x at -BOOST_DISTANCE, 2x at -2*BOOST_DISTANCE
    return bonus * level * BONUS;
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (!isEffective) {
      return;
    }
    float boost = getBoost(event.getPlayer().level, event.getPos().getY(), level);
    event.setNewSpeed(event.getNewSpeed() + (boost * miningSpeedModifier * tool.getMultiplier(ToolStats.MINING_SPEED)));
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    if (tool.hasTag(TinkerTags.Items.HARVEST)) {
      double boost;
      if (player != null && key == TooltipKey.SHIFT) {
        boost = getBoost(player.level, (int)player.getY(), level);
      } else {
        boost = BONUS * level;
      }
      if (boost != 0) {
        addFlatBoost(MINING_SPEED, boost * tool.getMultiplier(ToolStats.MINING_SPEED), tooltip);
      }
    }
  }
}
