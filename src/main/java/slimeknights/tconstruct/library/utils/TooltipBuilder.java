package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.List;

@SuppressWarnings("UnusedReturnValue")
@RequiredArgsConstructor
public class TooltipBuilder {
  /** Formateed broken string */
  public static final ITextComponent TOOLTIP_BROKEN = Util.makeTranslation("tooltip", "tool.broken").mergeStyle(TextFormatting.BOLD, TextFormatting.DARK_RED);
  /** Prefixed broken string */
  private static final ITextComponent TOOLTIP_BROKEN_PREFIXED = new TranslationTextComponent(HeadMaterialStats.DURABILITY_PREFIX).append(TOOLTIP_BROKEN);
  /** Key for free modifiers localization */
  private final static String KEY_FREE_UPGRADES = Util.makeTranslationKey("tooltip", "tool.upgrades");
  private final static String KEY_FREE_ABILITIES = Util.makeTranslationKey("tooltip", "tool.abilities");
  private final static String KEY_ATTACK_SPEED = Util.makeTranslationKey("stat", "attack_speed");

  private final static Color UPGRADE_COLOR = Color.fromInt(0xFFCCBA47);
  private final static Color ABILITY_COLOR = Color.fromInt(0xFFB8A0FF);
  private final static Color ATTACK_SPEED_COLOR = Color.fromInt(0xFF8547CC);

  /** Final list of tooltips */
  private final List<ITextComponent> tips = Lists.newLinkedList();
  private final ToolStack tool;


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
    float speed = tool.getStats().getMiningSpeed();
    speed *= tool.getDefinition().getBaseStatDefinition().getMiningSpeedModifier();
    this.tips.add(HeadMaterialStats.formatMiningSpeed(speed));
    return this;
  }

  /**
   * Adds the harvest level to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addHarvestLevel() {
    this.tips.add(HeadMaterialStats.formatHarvestLevel(tool.getStats().getHarvestLevel()));
    return this;
  }

  /**
   * Adds the durability to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addDurability(boolean textIfBroken) {
    if (tool.isBroken() && textIfBroken) {
      this.tips.add(TOOLTIP_BROKEN_PREFIXED);
    } else {
      this.tips.add(HeadMaterialStats.formatDurability(tool.getCurrentDurability(), tool.getStats().getDurability()));
    }

    return this;
  }

  /**
   * Adds the attack damage to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addAttackDamage() {
    float attack = ToolAttackUtil.getActualDamage(tool, Minecraft.getInstance().player);
    this.tips.add(HeadMaterialStats.formatAttack(attack));
    return this;
  }

  /**
   * Adds the attack speed to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addAttackSpeed() {
    double attackSpeed = tool.getStats().getAttackSpeed() * tool.getDefinition().getBaseStatDefinition().getAttackSpeed();
    this.tips.add(BaseMaterialStats.formatNumber(KEY_ATTACK_SPEED, ATTACK_SPEED_COLOR, (float)attackSpeed));
    return this;
  }

  /**
   * Adds the current free modifiers to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addFreeUpgrades() {
    int modifiers = tool.getFreeUpgrades();
    if (modifiers > 0) {
      this.tips.add(BaseMaterialStats.formatNumber(KEY_FREE_UPGRADES, UPGRADE_COLOR, modifiers));
    }

    return this;
  }

  /**
   * Adds the current free modifiers to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addFreeAbilities() {
    int abilities = tool.getFreeAbilities();
    if (abilities > 0) {
      this.tips.add(BaseMaterialStats.formatNumber(KEY_FREE_ABILITIES, ABILITY_COLOR, abilities));
    }

    return this;
  }

  /**
   * Adds the modifier information to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addModifierInfo() {
    for (ModifierEntry entry : tool.getModifierList()) {
      this.tips.add(entry.getModifier().getDisplayName(entry.getLevel()));
    }
    return this;
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
}
