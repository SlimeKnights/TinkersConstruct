package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipFlag;

import java.util.List;

public class LustrousModifier extends Modifier {
  public LustrousModifier() {
    super(0xA3E7FE);
  }

  @Override
  public int getPriority() {
    return 125; // run before trait boosts such as dwarven
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective && event.getState().isIn(Tags.Blocks.ORES)) {
      // grants +8 mining speed per level against ores
      event.setNewSpeed(event.getNewSpeed() + (level * 8 * tool.getModifier(ToolStats.MINING_SPEED)));
    }
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, TooltipFlag flag) {
    addStatTooltip(tool, ToolStats.MINING_SPEED, TinkerTags.Items.HARVEST, 8 * level, tooltip);
  }
}
