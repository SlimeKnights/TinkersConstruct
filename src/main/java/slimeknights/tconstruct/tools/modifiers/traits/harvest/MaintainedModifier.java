package slimeknights.tconstruct.tools.modifiers.traits.harvest;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

/** Well maintained for Tinkers Bronze */
public class MaintainedModifier extends Modifier {
  private static final Component MINING_SPEED = TConstruct.makeTranslation("modifier", "maintained.mining_speed");
  private static final ResourceLocation KEY_ORIGINAL_DURABILITY = TConstruct.getResource("durability");

  @Override
  public void addVolatileData(ToolRebuildContext context, int level, ModDataNBT volatileData) {
    volatileData.putInt(KEY_ORIGINAL_DURABILITY, (int)(context.getBaseStats().get(ToolStats.DURABILITY) * context.getDefinition().getData().getMultiplier(ToolStats.DURABILITY)));
  }

  /**
   * Applies the boost for the given range. Basically an inverse lerp
   * @param durability  Current durability
   * @param boost       Amount to boost
   * @param min         Minimum value to apply this
   * @param max         Value at which this boost maxes
   * @return  Boost amount
   */
  public static float boost(int durability, float boost, int min, int max) {
    if (durability > min) {
      if (durability > max) {
        return boost;
      }
      return boost * (durability - min) / (max - min);
    }
    return 0;
  }

  /**
   * Gets the total bonus for this tool at the given durabiity
   * @param tool   Tool instance
   * @param level  Tool levle
   * @return  Total boost
   */
  protected float getTotalBoost(IToolStackView tool, int level) {
    int durability = tool.getCurrentDurability();
    int baseMax = tool.getVolatileData().getInt(KEY_ORIGINAL_DURABILITY);

    // from 50% to 100%: 10% boost
    float boost = boost(durability, 0.1f, baseMax / 2, baseMax);
    // grant an extra 5% boost for getting up to 200% durability using modifiers
    // compared to WellMaintained2, this will grant slightly higher at top and maintain that higher speed though a bit more durability
    int fullMax = tool.getStats().getInt(ToolStats.DURABILITY);
    if (fullMax > baseMax) {
      // from 100% to 200% or full, whichever is larger
      boost += boost(durability, 0.05f, baseMax, Math.max(baseMax * 2, fullMax));
    }
    // max is 15% boost at 400% durability or above, with 3 levels (pickaxe), that gives up to 90%
    return boost * level;
  }

  @Override
  public void addInformation(IToolStackView tool, int level, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (tool.hasTag(TinkerTags.Items.HARVEST)) {
      double boost = getTotalBoost(tool, level);
      if (boost != 0) {
        addPercentTooltip(MINING_SPEED, boost, tooltip);
      }
    }
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      event.setNewSpeed(event.getNewSpeed() * (1 + getTotalBoost(tool, level)));
    }
  }
}
