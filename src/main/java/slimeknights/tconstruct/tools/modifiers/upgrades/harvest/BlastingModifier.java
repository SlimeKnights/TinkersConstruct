package slimeknights.tconstruct.tools.modifiers.upgrades.harvest;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.impl.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class BlastingModifier extends IncrementalModifier {
  @Override
  public int getPriority() {
    return 125; // run before trait boosts such as dwarven
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      float blastResistance = event.getState().getBlock().getExplosionResistance();

      // formula makes a boost of 9 at a hardness of 3 (most ores), boost of 3 at a hardness of 4.5, and a boost of 1 at hardness of 6 (stone)
      double boost = level * (Math.min(10f, Math.pow(3f, (6f - blastResistance)/1.5f))) * miningSpeedModifier;
      // factor in tool definition to prevent this being too strong on hammers
      boost *= tool.getMultiplier(ToolStats.MINING_SPEED);
      event.setNewSpeed(event.getNewSpeed() + (float)boost);
    }
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    addStatTooltip(tool, ToolStats.MINING_SPEED, TinkerTags.Items.HARVEST, 10 * getScaledLevel(tool, level), tooltip);
  }
}
