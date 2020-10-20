package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.RegistryManager;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.List;

@RequiredArgsConstructor
public class TooltipBuilder {

  public final static String FREE_MODIFIERS_LOCALIZATION = "tooltip.tool.modifiers";
  public final static String AMMO_LOCALIZATION = "stat.projectile.ammo.name";

  public static final String BROKEN_LOCALIZATION = "tooltip.tool.broken";
  public static final String EMPTY_LOCALIZATION = "tooltip.tool.empty";

  private final List<ITextComponent> tips = Lists.newLinkedList();
  private final ItemStack tool;
  private final ToolData data;

  public TooltipBuilder(ItemStack tool) {
    this(tool, ToolData.from(tool));
  }

  /**
   * Gets the tooltips from the builder
   *
   * @return the list of tooltips
   */
  public List<ITextComponent> getTooltips() {
    return this.tips;
  }

  /**
   * Adds the given text to the tooltip
   *
   * @param text the text to add
   * @return the tooltip builder
   */
  public TooltipBuilder add(String text) {
    this.tips.add(new StringTextComponent(text));

    return this;
  }

  /**
   * Adds the given text to the tooltip
   *
   * @param textComponent the text component to add
   * @return the tooltip builder
   */
  public TooltipBuilder add(ITextComponent textComponent) {
    this.tips.add(textComponent);

    return this;
  }

  /**
   * Adds the mining speed to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addMiningSpeed() {
    float speed = data.getStats().miningSpeed;

    if (!this.tool.isEmpty() && this.tool.getItem() instanceof ToolCore) {
      speed *= ((ToolCore) this.tool.getItem()).getToolDefinition().getBaseStatDefinition().getMiningSpeedModifier();
    }

    this.tips.add(HeadMaterialStats.formatMiningSpeed(speed));

    return this;
  }

  /**
   * Adds the harvest level to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addHarvestLevel() {
    this.tips.add(HeadMaterialStats.formatHarvestLevel(data.getStats().harvestLevel));

    return this;
  }

  /**
   * Adds the durability to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addDurability(boolean textIfBroken) {
    StatsNBT stats = data.getStats();
    if (stats.broken && textIfBroken) {
      this.tips.add(new TranslationTextComponent(HeadMaterialStats.DURABILITY_LOCALIZATION)
        .append(new StringTextComponent(": "))
        .append(new TranslationTextComponent("tooltip.tool.broken").mergeStyle(TextFormatting.BOLD, TextFormatting.DARK_RED)));
    }
    else {
      this.tips.add(HeadMaterialStats.formatDurability(ToolDamageUtil.getCurrentDurability(this.tool, data), stats.durability));
    }

    return this;
  }

  /**
   * Adds the attack damage to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addAttack() {
    float attack = ToolAttackUtil.getActualDamage(this.tool, Minecraft.getInstance().player);

    this.tips.add(HeadMaterialStats.formatAttack(attack));

    return this;
  }

  /**
   * Adds the current free modifiers to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addFreeModifiers() {
    this.tips.add(new TranslationTextComponent(FREE_MODIFIERS_LOCALIZATION)
      .appendString(": ")
      .appendString(String.valueOf(data.getStats().freeModifiers)));

    return this;
  }

  /**
   * Adds the modifier information to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addModifierInfo() {
    List<ModifierNBT> modifiers = data.getModifiers().getCurrentModifiers();

    for (ModifierNBT modifierNBT : modifiers) {
      IModifier modifier = RegistryManager.ACTIVE.getRegistry(IModifier.class).getValue(modifierNBT.identifier);
      if (modifier == null || modifier.isHidden()) {
        continue;
      }

      for (ITextComponent textComponent : modifier.getExtraInfo(this.tool, modifierNBT)) {
        if (!textComponent.getString().isEmpty()) {
          this.tips.add(textComponent);
        }
      }
    }

    return this;
  }

  /**
   * Adds the modifier information to the tooltip
   */
  public static void addModifierTooltips(ItemStack stack, List<ITextComponent> tooltips) {
    List<ModifierNBT> modifiers = ToolData.from(stack).getModifiers().getCurrentModifiers();

    for (ModifierNBT modifierNBT : modifiers) {
      IModifier modifier = RegistryManager.ACTIVE.getRegistry(IModifier.class).getValue(modifierNBT.identifier);
      if (modifier == null || modifier.isHidden()) {
        continue;
      }

      tooltips.add(modifier.getTooltip(modifierNBT, false));
    }
  }


  //todo: are these still needed?
  /**
   * Adds the draw speed to the tooltip
   *
   * @return the tooltip builder
   */
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

  /**
   * Adds the range information to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addRange() {
    this.tips.add(new StringTextComponent("TODO: implement getting range"));

    //todo implement code below and remove line above.
    //this.tips.add(BowMaterialStats.formatRange(ProjectileLauncherNBT.from(stack).range));

    return this;
  }

  /**
   * Adds the projective damage bonus to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addProjectileBonusDamage() {
    this.tips.add(new StringTextComponent("TODO: Implement getting projectile bonus damage"));

    //todo implement code below and remove line above.
    //this.tips.add(BowMaterialStats.formatDamage(ProjectileLauncherNBT.from(stack).bonusDamage));

    return this;
  }
}
