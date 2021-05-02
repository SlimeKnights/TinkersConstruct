package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;

import java.util.List;

/** Well maintained for Tinkers Bronze */
public class MaintainedModifier extends Modifier {
  private static final ITextComponent MINING_SPEED = Util.makeTranslation("modifier", "fake_attribute.mining_speed");
  private static final ResourceLocation KEY_ORIGINAL_DURABILITY = Util.getResource("durability");
  public MaintainedModifier() {
    super(0xe3bd68);
  }

  protected MaintainedModifier(int color) {
    super(color);
  }

  @Override
  public void addVolatileData(ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, int level, ModDataNBT volatileData) {
    volatileData.putInt(KEY_ORIGINAL_DURABILITY, (int)(baseStats.getDurability() * toolDefinition.getBaseStatDefinition().getDurabilityModifier()));
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
  protected float getTotalBoost(IModifierToolStack tool, int level) {
    int durability = tool.getCurrentDurability();
    int baseMax = tool.getVolatileData().getInt(KEY_ORIGINAL_DURABILITY);

    // from 50% to 100%: 20% boost
    float boost = boost(durability, 0.2f, baseMax / 2, baseMax);
    // grant an extra 10% boost for getting up to 200% durability using modifiers
    // compared to WellMaintained2, this will grant slightly higher at top and maintain that higher speed though a bit more durability
    int fullMax = tool.getStats().getDurability();
    if (fullMax > baseMax) {
      // from 100% to 200% or full, whichever is larger
      boost += boost(durability, 0.1f, baseMax, Math.max(baseMax * 2, fullMax));
    }
    // max is 30% boost at 400% durability or above, with 3 levels (pickaxe), that gives up to 90%
    return boost * level;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, ITooltipFlag flag, boolean detailed) {
    double boost = getTotalBoost(tool, level);
    if (boost != 0) {
      tooltip.add(applyStyle(new StringTextComponent(Util.dfPercentBoost.format(boost)).appendString(" ").append(MINING_SPEED)));
    }
  }

  @Override
  public void onBreakSpeed(IModifierToolStack tool, int level, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      event.setNewSpeed(event.getNewSpeed() * (1 + getTotalBoost(tool, level)));
    }
  }
}
