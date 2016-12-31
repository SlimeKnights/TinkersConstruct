package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import java.util.List;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomFontColor;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.FletchingMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.tools.ProjectileNBT;
import slimeknights.tconstruct.library.tools.ranged.BowCore;
import slimeknights.tconstruct.library.tools.ranged.IAmmo;

import static slimeknights.tconstruct.library.Util.df;
import static slimeknights.tconstruct.library.materials.HeadMaterialStats.COLOR_Durability;

/**
 * Used for simple info buidling in the tools!
 */
public class TooltipBuilder {

  public final static String LOC_FreeModifiers = "tooltip.tool.modifiers";
  public final static String LOC_Ammo = "stat.projectile.ammo.name";

  public static final String LOC_Broken = "tooltip.tool.broken";
  public static final String LOC_Empty = "tooltip.tool.empty";

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

  public static String formatAmmo(int durability, int ref) {
    return String.format("%s: %s%s%s/%s%s",
                         Util.translate(LOC_Ammo),
                         CustomFontColor.valueToColorCode((float) durability / (float) ref),
                         df.format(durability),
                         TextFormatting.GRAY.toString(),
                         COLOR_Durability,
                         df.format(ref))
           + TextFormatting.RESET;
  }

  public TooltipBuilder addDurability(boolean textIfBroken) {
    if(ToolHelper.isBroken(stack) && textIfBroken) {
      tips.add(String.format("%s: %s%s%s", Util.translate(HeadMaterialStats.LOC_Durability), TextFormatting.DARK_RED, TextFormatting.BOLD, Util.translate("tooltip.tool.broken")));
    }
    else {
      tips.add(HeadMaterialStats.formatDurability(ToolHelper.getCurrentDurability(stack), ToolHelper.getMaxDurability(stack)));
    }

    return this;
  }

  public TooltipBuilder addAmmo(boolean textIfEmpty) {
    if(stack.getItem() instanceof IAmmo) {
      if(ToolHelper.isBroken(stack) && textIfEmpty) {
        tips.add(String.format("%s: %s%s%s", Util.translate(LOC_Ammo), TextFormatting.DARK_RED, TextFormatting.BOLD, Util.translate(LOC_Empty)));
      }
      else {
        IAmmo ammoItem = (IAmmo) stack.getItem();
        tips.add(formatAmmo(ammoItem.getCurrentAmmo(stack), ammoItem.getMaxAmmo(stack)));
      }
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
    float attack = ToolHelper.getActualDamage(stack, Minecraft.getMinecraft().player);
    tips.add(HeadMaterialStats.formatAttack(attack));

    return this;
  }

  public TooltipBuilder addFreeModifiers() {
    tips.add(String.format("%s: %d", I18n.translateToLocal(LOC_FreeModifiers),
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

  public TooltipBuilder addDrawSpeed() {
    float speed = ProjectileLauncherNBT.from(stack).drawSpeed;
    // convert speed per tick to seconds drawtime
    if(stack.getItem() instanceof BowCore) {
      speed = (float)((BowCore) stack.getItem()).getDrawTime()/(20f * speed);
    }
    tips.add(BowMaterialStats.formatDrawspeed(speed));
    return this;
  }

  public TooltipBuilder addRange() {
    tips.add(BowMaterialStats.formatRange(ProjectileLauncherNBT.from(stack).range));
    return this;
  }

  public TooltipBuilder addProjectileBonusDamage() {
    tips.add(BowMaterialStats.formatDamage(ProjectileLauncherNBT.from(stack).bonusDamage));
    return this;
  }

  public TooltipBuilder addAccuracy() {
    this.add(FletchingMaterialStats.formatAccuracy(ProjectileNBT.from(stack).accuracy));
    return this;
  }

  public static void addModifierTooltips(ItemStack stack, List<String> tooltips) {
    NBTTagList tagList = TagUtil.getModifiersTagList(stack);
    for(int i = 0; i < tagList.tagCount(); i++) {
      NBTTagCompound tag = tagList.getCompoundTagAt(i);
      ModifierNBT data = ModifierNBT.readTag(tag);

      // get matching modifier
      IModifier modifier = TinkerRegistry.getModifier(data.identifier);
      if(modifier == null || modifier.isHidden()) {
        continue;
      }

      tooltips.add(data.getColorString() + modifier.getTooltip(tag, false));
    }
  }
}
