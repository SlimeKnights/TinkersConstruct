package tconstruct.library.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import tconstruct.library.tools.ToolHelper;

/**
 * Used for simple info buidling in the tools!
 */
public class TooltipBuilder {

  private static final DecimalFormat df = new DecimalFormat("#.##");

  private final List<String> tips = new LinkedList<>();
  private final ItemStack stack;

  public TooltipBuilder(ItemStack stack) {
    this.stack = stack;
  }

  public String[] getTooltip() {
    return tips.toArray(new String[tips.size()]);
  }

  public TooltipBuilder addDurability() {
    if(stack != null)
    tips.add(String.format("%s: %d", StatCollector.translateToLocal("tooltip.tool.durability"),
                           ToolHelper.getDurability(stack)));

    return this;
  }

  public TooltipBuilder addMiningSpeed() {
    if(stack != null)
    tips.add(String.format("%s: %s", StatCollector.translateToLocal("tooltip.tool.miningspeed"),
                           df.format(ToolHelper.getMiningSpeed(stack))));

    return this;
  }

  public TooltipBuilder addHarvestLevel() {
    if(stack != null)
    tips.add(String.format("%s: %d", StatCollector.translateToLocal("tooltip.tool.harvestlevel"),
                           ToolHelper.getHarvestLevel(stack)));

    return this;
  }

  public TooltipBuilder addAttack() {
    if(stack != null)
    tips.add(String.format("%s: %s", StatCollector.translateToLocal("tooltip.tool.attack"),
                           df.format(ToolHelper.getAttack(stack))));

    return this;
  }
}
