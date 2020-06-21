package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.helper.ToolInteractionUtil;
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

  public TooltipBuilder addMiningSpeed() {
    float speed = ToolData.from(this.tool).getStats().miningSpeed;

    if (!this.tool.isEmpty() && this.tool.getItem() instanceof ToolCore) {
      speed *= ((ToolCore) this.tool.getItem()).getToolDefinition().getBaseStatDefinition().getMiningSpeedModifier();
    }

    tips.add(new StringTextComponent(HeadMaterialStats.formatMiningSpeed(speed)));

    return this;
  }

  public TooltipBuilder addHarvestLevel() {
    tips.add(new StringTextComponent(HeadMaterialStats.formatHarvestLevel(ToolData.from(this.tool).getStats().harvestLevel)));

    return this;
  }

  public TooltipBuilder addDurability(boolean textIfBroken) {
    if (ToolData.isBroken(this.tool) && textIfBroken) {
      this.tips.add(new StringTextComponent(String.format("%s: %s%s%s", new TranslationTextComponent(HeadMaterialStats.DURABILITY_LOCALIZATION).getFormattedText(), TextFormatting.DARK_RED, TextFormatting.BOLD, new TranslationTextComponent("tooltip.tool.broken").getFormattedText())));
    } else if (ToolData.isBroken(this.tool)) {
      this.tips.add(new StringTextComponent(HeadMaterialStats.formatDurability(ToolCore.getCurrentDurability(this.tool), ToolData.from(this.tool).getStats().durability)));
    } else {
      this.tips.add(new StringTextComponent(HeadMaterialStats.formatDurability(ToolCore.getCurrentDurability(this.tool), this.tool.getMaxDamage())));
    }

    return this;
  }

  public TooltipBuilder addAttack() {
    float attack = ToolInteractionUtil.getActualDamage(this.tool, Minecraft.getInstance().player);

    tips.add(new StringTextComponent(HeadMaterialStats.formatAttack(attack)));

    return this;
  }

  public TooltipBuilder addFreeModifiers() {
    this.tips.add(new StringTextComponent(String.format("%s: %d", new TranslationTextComponent(FREE_MODIFIERS_LOCALIZATION).getFormattedText(), ToolData.from(this.tool).getStats().freeModifiers)));

    return this;
  }

  public TooltipBuilder addModifierInfo() {
    this.tips.add(new StringTextComponent("TODO: GET MODIFIER INFORMATION"));

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

    return this;
  }

  public TooltipBuilder addDrawSpeed() {
    this.tips.add(new StringTextComponent("TODO: implement getting draw speed"));

    //todo implement code below and remove line above.
    /*float speed = ProjectileLauncherNBT.from(stack).drawSpeed;
    // convert speed per tick to seconds drawtime
    if(stack.getItem() instanceof BowCore) {
      speed = (float)((BowCore) stack.getItem()).getDrawTime()/(20f * speed);
    }
    this.tips.add(BowMaterialStats.formatDrawspeed(speed));*/

    return this;
  }

  public TooltipBuilder addRange() {
    this.tips.add(new StringTextComponent("TODO: implement getting range"));

    //todo implement code below and remove line above.
    //this.tips.add(BowMaterialStats.formatRange(ProjectileLauncherNBT.from(stack).range));

    return this;
  }

  public TooltipBuilder addProjectileBonusDamage() {
    this.tips.add(new StringTextComponent("TODO: Implement getting projectile bonus damage"));

    //todo implement code below and remove line above.
    //this.tips.add(BowMaterialStats.formatDamage(ProjectileLauncherNBT.from(stack).bonusDamage));

    return this;
  }
}
