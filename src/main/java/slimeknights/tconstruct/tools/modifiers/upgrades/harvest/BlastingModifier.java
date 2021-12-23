package slimeknights.tconstruct.tools.modifiers.upgrades.harvest;

import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

public class BlastingModifier extends IncrementalModifier {
  public BlastingModifier() {
    super(0x8A8A8A);
  }

  @Override
  public int getPriority() {
    return 125; // run before trait boosts such as dwarven
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      float blastResistance = event.getState().getBlock().getExplosionResistance();

      // formula makes a boost of 9 at a hardness of 3 (most ores), boost of 3 at a hardness of 4.5, and a boost of 1 at hardness of 6 (stone)
      double boost = level * (Math.min(10f, Math.pow(3f, (6f - blastResistance)/1.5f))) * miningSpeedModifier;
      // factor in tool definition to prevent this being too strong on hammers
      boost *= tool.getModifier(ToolStats.MINING_SPEED);
      event.setNewSpeed(event.getNewSpeed() + (float)boost);
    }
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    addStatTooltip(tool, ToolStats.MINING_SPEED, TinkerTags.Items.HARVEST, 10 * getScaledLevel(tool, level), tooltip);
  }
}
