package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class LustrousModifier extends Modifier {
  @Override
  public int getPriority() {
    return 125; // run before trait boosts such as dwarven
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    Block block = event.getState().getBlock();
    if (isEffective && (Tags.Blocks.ORES.contains(block) || Tags.Blocks.STORAGE_BLOCKS.contains(block))) {
      // grants +8 mining speed per level against ores
      event.setNewSpeed(event.getNewSpeed() + (level * 8 * tool.getMultiplier(ToolStats.MINING_SPEED)));
    }
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    addStatTooltip(tool, ToolStats.MINING_SPEED, TinkerTags.Items.HARVEST, 8 * level, tooltip);
  }
}
