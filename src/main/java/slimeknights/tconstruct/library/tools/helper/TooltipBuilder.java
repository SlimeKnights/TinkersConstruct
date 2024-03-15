package slimeknights.tconstruct.library.tools.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tier;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
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
  private static final TextColor MAX = valueToColor(1, 1);
  private static final UnaryOperator<Style> APPLY_MAX = style -> style.withColor(MAX);

  /** Formatted broken string */
  private static final Component TOOLTIP_BROKEN = TConstruct.makeTranslation("tooltip", "tool.broken").withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_RED);
  /** Prefixed broken string */
  private static final Component TOOLTIP_BROKEN_PREFIXED = ToolStats.DURABILITY.getPrefix().append(TOOLTIP_BROKEN);

  private final IToolStackView tool;
  @Getter
  private final List<Component> tooltips;

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
  public TooltipBuilder add(Component textComponent) {
    this.tooltips.add(textComponent);

    return this;
  }

  /** Formats the stat value */
  private <T> Component formatValue(IToolStat<T> stat) {
    return stat.formatValue(tool.getStats().get(stat));
  }

  /**
   * Adds the given stat to the tooltip
   *
   * @param stat  Stat to add
   * @return the tooltip builder
   */
  public TooltipBuilder add(IToolStat<?> stat) {
    this.tooltips.add(formatValue(stat));
    return this;
  }

  /**
   * Adds harvest tier to the tooltip
   *
   * @return the tooltip builder
   */
  public TooltipBuilder addTier() {
    Tier tier = tool.getDefinition().getData().getHarvestLogic().getTier(tool);
    this.tooltips.add(ToolStats.HARVEST_TIER.formatValue(tier));
    return this;
  }

  /**
   * Adds the given stat to the tooltip if above 0
   *
   * @param stat  Stat to add
   * @return the tooltip builder
   */
  public TooltipBuilder addOptional(INumericToolStat<?> stat) {
    return addOptional(stat, 1.0f);
  }

  /**
   * Adds the given stat to the tooltip if above 0, scaling by the given value
   *
   * @param stat  Stat to add
   * @param scale Amount to scale this value by
   * @return the tooltip builder
   */
  public TooltipBuilder addOptional(INumericToolStat<?> stat, float scale) {
    float value = tool.getStats().get(stat).floatValue();
    if (value > 0) {
      this.tooltips.add(stat.formatValue(value * scale));
    }
    return this;
  }

  /** Applies formatting for durability with a reference durability */
  public static Component formatDurability(int durability, int ref, boolean textIfBroken) {
    if (textIfBroken && durability == 0) {
      return TOOLTIP_BROKEN_PREFIXED;
    }
    return ToolStats.DURABILITY.getPrefix().append(formatPartialAmount(durability, ref));
  }

  /**
   * Takes a value between 0.0 and 1.0.
   * Returns a color between red and green, depending on the value. 1.0 is green.
   * If the value goes above 1.0 it continues along the color spectrum.
   */
  public static TextColor valueToColor(float value, float max) {
    // 0.0 -> 0 = red
    // 1.0 -> 1/3 = green
    // 1.5 -> 1/2 = aqua
    float hue = Mth.clamp(((value / max) / 3), 0.01f, 0.5f);
    return TextColor.fromRgb(HSBtoRGB(hue, 0.65f, 0.8f));
  }

  /**
   * Formats a fraction with color based on the percent
   * @param value  Value
   * @param max    Max value
   * @return  Formatted amount
   */
  public static Component formatPartialAmount(int value, int max) {
    return Component.literal(Util.COMMA_FORMAT.format(value))
      .withStyle(style -> style.withColor(valueToColor(value, max)))
      .append(Component.literal(" / ").withStyle(ChatFormatting.GRAY))
      .append(Component.literal(Util.COMMA_FORMAT.format(max)).withStyle(APPLY_MAX));
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
  public TooltipBuilder addWithAttribute(INumericToolStat<?> stat, Attribute attribute) {
    float damage = (float) attribute.getDefaultValue();
    Player player = Minecraft.getInstance().player;
    if (player != null) {
      AttributeInstance instance = player.getAttribute(attribute);
      if (instance != null) {
        damage = (float) instance.getBaseValue();
      }
    }
    this.tooltips.add(stat.formatValue(damage + tool.getStats().get(stat).floatValue()));
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
}
