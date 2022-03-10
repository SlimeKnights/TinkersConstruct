package slimeknights.tconstruct.tools.modifiers.traits.harvest;

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

/** Well maintained for Tinkers Bronze */
public class MaintainedModifier extends Modifier {
  private static final Component MINING_SPEED = TConstruct.makeTranslation("modifier", "maintained.mining_speed");
  /** Total boost when at full durability */
  private static final float BOOST_AT_FULL = 6;
  /** Min durability to get boost */
  private static final float MIN_BOOST_PERCENT = 0.5f;

  /**
   * Gets the total bonus for this tool at the given durabiity
   * @param tool   Tool instance
   * @param level  Tool levle
   * @return  Total boost
   */
  private static float getTotalBoost(IToolStackView tool, int level) {
    int durability = tool.getCurrentDurability();
    int max = tool.getStats().getInt(ToolStats.DURABILITY);
    float min = max * MIN_BOOST_PERCENT;
    if (durability > min) {
      return BOOST_AT_FULL * level * (durability - min) / (max - min);
    }
    return 0f;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (tool.hasTag(TinkerTags.Items.HARVEST)) {
      double boost;
      if (tooltipKey == TooltipKey.SHIFT) {
        boost = getTotalBoost(tool, level);
      } else {
        boost = BOOST_AT_FULL * level;
      }
      if (boost > 0.01f) {
        addFlatBoost(MINING_SPEED, boost * tool.getMultiplier(ToolStats.MINING_SPEED), tooltip);
      }
    }
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      event.setNewSpeed(event.getNewSpeed() + (getTotalBoost(tool, level) * miningSpeedModifier * tool.getMultiplier(ToolStats.MINING_SPEED)));
    }
  }
}
