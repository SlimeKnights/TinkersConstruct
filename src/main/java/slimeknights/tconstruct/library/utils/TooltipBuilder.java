package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.List;

public class TooltipBuilder {

  public final static String FREE_MODIFIERS_LOCALIZATION = "tooltip.tool.modifiers";
  public final static String AMMO_LOCALIZATION = "stat.projectile.ammo.name";

  public static final String BROKEN_LOCALIZATION = "tooltip.tool.broken";
  public static final String EMPTY_LOCALIZATION = "tooltip.tool.empty";

  private final List<ITextComponent> tips = Lists.newLinkedList();
  private final ItemStack tool;

  public TooltipBuilder(ItemStack tool) {
    this.tool = tool;
  }

  public List<ITextComponent> getTooltip() {
    return this.tips;
  }

  public TooltipBuilder add(String text) {
    this.tips.add(new StringTextComponent(text));

    return this;
  }

  public void addMiningSpeed() {
    float speed = ToolData.from(this.tool).getStats().miningSpeed;

    if (!this.tool.isEmpty() && this.tool.getItem() instanceof ToolCore) {
      speed *= ((ToolCore) this.tool.getItem()).getToolDefinition().getBaseStatDefinition().getMiningSpeedModifier();
    }

    tips.add(HeadMaterialStats.formatMiningSpeed(speed));

  }

  public void addHarvestLevel() {
    tips.add(HeadMaterialStats.formatHarvestLevel(ToolData.from(this.tool).getStats().harvestLevel));

  }

  public void addDurability(boolean textIfBroken) {
    if (ToolData.isBroken(this.tool) && textIfBroken) {
      this.tips.add(new TranslationTextComponent(HeadMaterialStats.DURABILITY_LOCALIZATION)
        .append(new StringTextComponent(": "))
        .append(new TranslationTextComponent("tooltip.tool.broken").mergeStyle(TextFormatting.BOLD, TextFormatting.DARK_RED)));
    }
    else if (ToolData.isBroken(this.tool)) {
      this.tips.add(HeadMaterialStats.formatDurability(ToolCore.getCurrentDurability(this.tool), ToolData.from(this.tool).getStats().durability));
    }
    else {
      this.tips.add(HeadMaterialStats.formatDurability(ToolCore.getCurrentDurability(this.tool), this.tool.getMaxDamage()));
    }

  }

  public void addAttack() {
    float attack = ToolAttackUtil.getActualDamage(this.tool, Minecraft.getInstance().player);

    tips.add(HeadMaterialStats.formatAttack(attack));

  }

  public void addFreeModifiers() {
    this.tips.add(new TranslationTextComponent(FREE_MODIFIERS_LOCALIZATION)
      .appendString(": ")
      .appendString(String.valueOf(ToolData.from(this.tool).getStats().freeModifiers)));

  }

  public void addModifierInfo() {
    this.tips.add(new StringTextComponent("todo modifier information"));

    //todo implement code below and remove line above.
    /*NBTTagList tagList = TagUtil.getModifiersTagList(stack);
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
    }*/

  }

  //todo: are these still needed?
  public void addDrawSpeed() {
    this.tips.add(new StringTextComponent("TODO: implement getting draw speed"));

    //todo implement code below and remove line above.
    /*float speed = ProjectileLauncherNBT.from(stack).drawSpeed;
    // convert speed per tick to seconds drawtime
    if(stack.getItem() instanceof BowCore) {
      speed = (float)((BowCore) stack.getItem()).getDrawTime()/(20f * speed);
    }
    this.tips.add(BowMaterialStats.formatDrawspeed(speed));*/

  }

  public void addRange() {
    this.tips.add(new StringTextComponent("TODO: implement getting range"));

    //todo implement code below and remove line above.
    //this.tips.add(BowMaterialStats.formatRange(ProjectileLauncherNBT.from(stack).range));
  }

  public void addProjectileBonusDamage() {
    this.tips.add(new StringTextComponent("TODO: Implement getting projectile bonus damage"));

    //todo implement code below and remove line above.
    //this.tips.add(BowMaterialStats.formatDamage(ProjectileLauncherNBT.from(stack).bonusDamage));

  }
}
