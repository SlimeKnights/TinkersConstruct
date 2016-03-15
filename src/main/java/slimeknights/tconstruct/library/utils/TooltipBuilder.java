package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;

/**
 * Used for simple info buidling in the tools!
 */
public class TooltipBuilder {

  public final static String LOC_FreeModifiers = "tooltip.tool.modifiers";

  private final List<String> tips = Lists.newLinkedList();
  private final ItemStack stack;

  public TooltipBuilder(ItemStack stack) {
    this.stack = stack;
  }

  public List<String> getTooltip() {
    return tips;
  }

  public TooltipBuilder add(String text) {
    tips.add(text);

    return this;
  }

  public TooltipBuilder addDurability(boolean textIfBroken) {
    if(ToolHelper.isBroken(stack) && textIfBroken) {
      tips.add(String.format("%s: %s%s%s", Util.translate(HeadMaterialStats.LOC_Durability), EnumChatFormatting.DARK_RED, EnumChatFormatting.BOLD, Util.translate("tooltip.tool.broken")));
    }
    else {
      tips.add(HeadMaterialStats
                   .formatDurability(ToolHelper.getCurrentDurability(stack), ToolHelper.getDurabilityStat(stack)));
    }

    return this;
  }

  public TooltipBuilder addMiningSpeed() {
    tips.add(HeadMaterialStats.formatMiningSpeed(ToolHelper.getActualMiningSpeed(stack)));

    return this;
  }

  public TooltipBuilder addHarvestLevel() {
    tips.add(HeadMaterialStats.formatHarvestLevel(ToolHelper.getHarvestLevelStat(stack)));

    return this;
  }

  public TooltipBuilder addAttack() {
    float attack = ToolHelper.getActualDamage(stack, Minecraft.getMinecraft().thePlayer);
    tips.add(HeadMaterialStats.formatAttack(attack));

    return this;
  }

  public TooltipBuilder addFreeModifiers() {
    tips.add(String.format("%s: %d", StatCollector.translateToLocal(LOC_FreeModifiers),
                           ToolHelper.getFreeModifiers(stack)));

    return this;
  }

  public TooltipBuilder addModifierInfo() {
    NBTTagList tagList = TagUtil.getModifiersTagList(stack);
    for(int i = 0; i < tagList.tagCount(); i++) {
      NBTTagCompound tag = tagList.getCompoundTagAt(i);
      ModifierNBT data = ModifierNBT.readTag(tag);

      // get matching modifier
      IModifier modifier = TinkerRegistry.getModifier(data.identifier);
      if(modifier == null || modifier.isHidden()) {
        continue;
      }

      for(String string : modifier.getExtraInfo(stack, tag)) {
        if(!string.isEmpty()) {
          tips.add(data.getColorString() + string);
        }
      }
    }

    return this;
  }
}
