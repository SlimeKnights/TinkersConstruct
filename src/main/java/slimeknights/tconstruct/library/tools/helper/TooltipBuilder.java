package slimeknights.tconstruct.library.tools.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import static java.awt.Color.HSBtoRGB;

/** Builder for tool stats */
@SuppressWarnings("UnusedReturnValue")
@RequiredArgsConstructor
public class TooltipBuilder {
  private static final Color MAX = valueToColor(1, 1);
  private static final UnaryOperator<Style> APPLY_MAX = style -> style.setColor(MAX);

  /** Formatted broken string */
  private static final ITextComponent TOOLTIP_BROKEN = TConstruct.makeTranslation("tooltip", "tool.broken").mergeStyle(TextFormatting.BOLD, TextFormatting.DARK_RED);
  /** Prefixed broken string */
  private static final ITextComponent TOOLTIP_BROKEN_PREFIXED = ToolStats.DURABILITY.getPrefix().appendSibling(TOOLTIP_BROKEN);

  private final IModifierToolStack tool;
  @Getter
  private final List<ITextComponent> tooltips;

  public TooltipBuilder(ToolStack tool) {
    this.tool = tool;
    this.tooltips = new ArrayList<>();
  }

  /**
   * Adds the given text to the tooltip
   *
   * @param textComponent the text component to add
   * @return the tooltip builder
   */
  public TooltipBuilder add(ITextComponent textComponent) {
    this.tooltips.add(textComponent);

    return this;
  }

  /**
   * Adds the given stat to the tooltip
   *
   * @param stat  Stat to add
   * @return the tooltip builder
   */
  public TooltipBuilder add(IToolStat<?> stat) {
    this.tooltips.add(stat.formatValue(tool.getStats().getFloat(stat)));
    return this;
  }

  /**
   * Adds the given stat to the tooltip if above 0
   *
   * @param stat  Stat to add
   * @return the tooltip builder
   */
  public TooltipBuilder addOptional(IToolStat<?> stat) {
    float value = tool.getStats().getFloat(stat);
    if (value > 0) {
      this.tooltips.add(stat.formatValue(value));
    }
    return this;
  }

  /** Applies formatting for durability with a reference durability */
  public static ITextComponent formatDurability(int durability, int ref, boolean textIfBroken) {
    if (textIfBroken && durability == 0) {
      return TOOLTIP_BROKEN_PREFIXED;
    }
    return ToolStats.DURABILITY.getPrefix().appendSibling(formatPartialAmount(durability, ref));
  }

  /**
   * Takes a value between 0.0 and 1.0.
   * Returns a color between red and green, depending on the value. 1.0 is green.
   * If the value goes above 1.0 it continues along the color spectrum.
   */
  public static Color valueToColor(float value, float max) {
    // 0.0 -> 0 = red
    // 1.0 -> 1/3 = green
    // 1.5 -> 1/2 = aqua
    float hue = MathHelper.clamp(((value / max) / 3), 0.01f, 0.5f);
    return Color.fromInt(HSBtoRGB(hue, 0.65f, 0.8f));
  }

  /**
   * Formats a fraction with color based on the percent
   * @param value  Value
   * @param max    Max value
   * @return  Formatted amount
   */
  public static ITextComponent formatPartialAmount(int value, int max) {
    return new StringTextComponent(Util.COMMA_FORMAT.format(value))
      .modifyStyle(style -> style.setColor(valueToColor(value, max)))
      .appendSibling(new StringTextComponent(" / ").mergeStyle(TextFormatting.GRAY))
      .appendSibling(new StringTextComponent(Util.COMMA_FORMAT.format(max)).modifyStyle(APPLY_MAX));
  }

  /**
   * Adds the durability to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addDurability() {
    // never show broken text in this context
    this.tooltips.add(formatDurability(tool.getCurrentDurability(), tool.getStats().getInt(ToolStats.DURABILITY), false));
    return this;
  }

  /**
   * Adds the given stat to the tooltip, summing in the attribute value
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addWithAttribute(IToolStat<?> stat, Attribute attribute) {
    float damage = (float) attribute.getDefaultValue();
    PlayerEntity player = Minecraft.getInstance().player;
    if (player != null) {
      ModifiableAttributeInstance instance = player.getAttribute(attribute);
      if (instance != null) {
        damage = (float) instance.getBaseValue();
      }
    }
    this.tooltips.add(ToolStats.ATTACK_DAMAGE.formatValue(damage + tool.getStats().getFloat(stat)));
    return this;
  }

  /**
   * Adds the specific free slot to the tooltip
   * @param slotType  Type of slot to add
   * @return the tooltip builder
   */
  public TooltipBuilder addFreeSlots(SlotType slotType) {
    int slots = tool.getFreeSlots(slotType);
    if (slots > 0) {
      this.tooltips.add(IToolStat.formatNumber(slotType.getPrefix(), slotType.getColor(), slots));
    }
    return this;
  }

  /**
   * Adds the current free modifiers to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addAllFreeSlots() {
    for (SlotType slotType : SlotType.getAllSlotTypes()) {
      addFreeSlots(slotType);
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
        this.tooltips.add(entry.getModifier().getDisplayName(tool, entry.getLevel()));
      }
    }
    return this;
  }


  /* Deprecated */

  /** @deprecated use {@link #addFreeSlots(SlotType)} or {@link #addAllFreeSlots()} */
  @Deprecated
  public TooltipBuilder addFreeUpgrades() {
    return addFreeSlots(SlotType.UPGRADE);
  }

  /** @deprecated use {@link #addFreeSlots(SlotType)} or {@link #addAllFreeSlots()} */
  @Deprecated
  public TooltipBuilder addFreeAbilities() {
    return addFreeSlots(SlotType.ABILITY);
  }
}
