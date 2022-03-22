package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class TemperateModifier extends Modifier {
  private static final float BASELINE_TEMPERATURE = 0.75f;
  private static final float MAX_BOOST = 7.5f;
  private static final Component MINING_SPEED = TConstruct.makeTranslation("modifier", "temperate.mining_speed");

  /** Gets the bonus for the given position */
  private static float getBonus(Player player, BlockPos pos, int level) {
    // temperature ranges from 0 to 1.25
    return Math.abs(player.level.getBiome(pos).getTemperature(pos) - BASELINE_TEMPERATURE) * level * MAX_BOOST / 1.25f;
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      // temperature ranges from 0 to 1.25. Division makes it 0 to 0.125 per level
      event.setNewSpeed(event.getNewSpeed() + (getBonus(event.getPlayer(), event.getPos(), level) * tool.getMultiplier(ToolStats.MINING_SPEED) * miningSpeedModifier));
    }
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag flag) {
    if (tool.hasTag(TinkerTags.Items.HARVEST)) {
      float bonus;
      if (player != null && key == TooltipKey.SHIFT) {
        bonus = getBonus(player, player.blockPosition(), level);
      } else {
        bonus = level * MAX_BOOST;
      }
      if (bonus > 0.01f) {
        addFlatBoost(MINING_SPEED, bonus * tool.getMultiplier(ToolStats.MINING_SPEED), tooltip);
      }
    }
  }
}
