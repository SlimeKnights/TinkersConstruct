package slimeknights.tconstruct.library.utils;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
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
  /** Key for free modifiers localization */
  private final static String KEY_FREE_UPGRADES = Util.makeTranslationKey("tooltip", "tool.upgrades");
  private final static String KEY_FREE_ABILITIES = Util.makeTranslationKey("tooltip", "tool.abilities");
  private final static String KEY_ATTACK_SPEED = Util.makeTranslationKey("stat", "attack_speed");

  private final static TextColor UPGRADE_COLOR = TextColor.fromRgb(0xFFCCBA47);
  private final static TextColor ABILITY_COLOR = TextColor.fromRgb(0xFFB8A0FF);
  private final static TextColor ATTACK_SPEED_COLOR = TextColor.fromRgb(0xFF8547CC);

  /** Final list of tooltips */
  private final List<Text> tips = Lists.newLinkedList();
  private final ToolStack tool;


  /**
   * Gets the tooltips from the builder
   *
   * @return the list of tooltips
   */
  public List<Text> getTooltips() {
    return this.tips;
  }

  /**
   * Adds the given text to the tooltip
   *
   * @param textComponent the text component to add
   * @return the tooltip builder
   */
  public TooltipBuilder add(Text textComponent) {
    this.tips.add(textComponent);

    return this;
  }

  /**
   * Adds the mining speed to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addMiningSpeed() {
    this.tips.add(HeadMaterialStats.formatMiningSpeed(tool.getStats().getMiningSpeed()));
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
  public TooltipBuilder addDurability() {
    // never show broken text in this context
    this.tips.add(HeadMaterialStats.formatDurability(tool.getCurrentDurability(), tool.getStats().getDurability(), false));
    return this;
  }

  /**
   * Adds the attack damage to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addAttackDamage() {
    float attack = ToolAttackUtil.getActualDamage(tool, MinecraftClient.getInstance().player);
    this.tips.add(HeadMaterialStats.formatAttack(attack));
    return this;
  }

  /**
   * Adds the attack speed to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addAttackSpeed() {
    this.tips.add(BaseMaterialStats.formatNumber(KEY_ATTACK_SPEED, ATTACK_SPEED_COLOR, tool.getStats().getAttackSpeed()));
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
  public TooltipBuilder addModifierInfo(boolean advanced) {
    for (ModifierEntry entry : tool.getModifierList()) {
      if (entry.getModifier().shouldDisplay(advanced)) {
        this.tips.add(entry.getModifier().getDisplayName(tool, entry.getLevel()));
      }
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
    this.tips.add(new LiteralText("TODO: implement getting draw speed"));

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
    this.tips.add(new LiteralText("TODO: implement getting range"));

    //todo implement code below and remove line above.
    //this.tips.add(BowMaterialStats.formatRange(ProjectileLauncherNBT.from(stack).range));

    return this;
  }
}
