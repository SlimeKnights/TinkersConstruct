package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import java.text.DecimalFormat;
import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;

/**
 * Used for simple info buidling in the tools!
 */
public class TooltipBuilder {

  public final static String LOC_FreeModifiers = "tooltip.tool.modifiers";

  private static final DecimalFormat df = new DecimalFormat("#.##");

  private final List<String> tips = Lists.newLinkedList();
  private final ItemStack stack;

  public TooltipBuilder(ItemStack stack) {
    this.stack = stack;
  }

  public String[] getTooltip() {
    return tips.toArray(new String[tips.size()]);
  }

  // also includes traits
  public TooltipBuilder addModifiers() {
    if(stack != null) {
      NBTTagList tagList = TagUtil.getModifiersTagList(stack);

      for(int i = 0; i < tagList.tagCount(); i++) {
        NBTTagCompound tag = tagList.getCompoundTagAt(i);
        ModifierNBT data = ModifierNBT.readTag(tag);

        // get matching modifier
        IModifier modifier = TinkerRegistry.getModifier(data.identifier);
        if(modifier == null) {
          continue;
        }

        tips.add(data.getColorString() + modifier.getTooltip(tag, false));
      }
    }

    return this;
  }

  public TooltipBuilder addDurability() {
    if(stack != null) {
      if(ToolHelper.isBroken(stack)) {
        tips.add(String.format("%s: %s%s%s", Util.translate(ToolMaterialStats.LOC_Durability), EnumChatFormatting.DARK_RED, EnumChatFormatting.BOLD, Util.translate("tooltip.tool.broken")));
      }
      else {
        tips.add(ToolMaterialStats.formatDurability(ToolHelper.getCurrentDurability(stack), ToolHelper.getDurability(stack)));
      }
    }

    return this;
  }

  public TooltipBuilder addMiningSpeed() {
    if(stack != null) {
      tips.add(ToolMaterialStats.formatMiningSpeed(ToolHelper.getMiningSpeed(stack)));
    }

    return this;
  }

  public TooltipBuilder addHarvestLevel() {
    if(stack != null) {
      tips.add(ToolMaterialStats.formatHarvestLevel(ToolHelper.getHarvestLevel(stack)));
    }

    return this;
  }

  public TooltipBuilder addAttack() {
    if(stack != null) {
      float attack = ToolHelper.getActualDamage(stack, Minecraft.getMinecraft().thePlayer);
      tips.add(ToolMaterialStats.formatAttack(attack));
    }

    return this;
  }

  public TooltipBuilder addFreeModifiers() {
    if(stack != null) {
      tips.add(String.format("%s: %d", StatCollector.translateToLocal(LOC_FreeModifiers),
                             ToolHelper.getFreeModifiers(stack)));
    }

    return this;
  }

  public TooltipBuilder addCustom(String custom) {
    if(stack != null) {
      tips.add(custom);
    }

    return this;
  }
}
