package slimeknights.tconstruct.library.modifiers.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.dynamic.ComposableModifier.TooltipDisplay;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;

/**
 * Basic modifier, having a collection of hooks and the ability to set common modifier properties.
 * In most cases it's better to use {@link slimeknights.tconstruct.library.modifiers.dynamic.ComposableModifier},
 * however as sometimes its not feasible extract code to JSON this can be a good alternative for static modifiers.
 */
public class BasicModifier extends Modifier {
  protected final ModifierLevelDisplay levelDisplay;
  protected final TooltipDisplay tooltipDisplay;
  @Getter
  protected final int priority;

  public BasicModifier(ModifierHookMap hookMap, ModifierLevelDisplay levelDisplay, TooltipDisplay tooltipDisplay, int priority) {
    super(hookMap);
    this.levelDisplay = levelDisplay;
    this.tooltipDisplay = tooltipDisplay;
    this.priority = priority;
  }

  /**
   * This method is final to prevent overrides as the constructor no longer calls it
   */
  @Override
  protected final void registerHooks(ModifierHookMap.Builder hookBuilder) {}

  @Override
  public Component getDisplayName(int level) {
    return levelDisplay.nameForLevel(this, level);
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return advanced ? tooltipDisplay != TooltipDisplay.NEVER
                    : tooltipDisplay == TooltipDisplay.ALWAYS;
  }

  /**
   * Builder to create simple static modifiers. Similar to {@link slimeknights.tconstruct.library.modifiers.dynamic.ComposableModifier.Builder}, except more efficient as we don't require it be JSON serializable.
   * Generally it's better to just use composable unless there is a good reason.
   */
  @Accessors(fluent = true)
  @Setter
  @RequiredArgsConstructor(staticName = "builder")
  public static class Builder {
    private final ModifierHookMap hookMap;

    /** Method of displaying levels in this modifier */
    private ModifierLevelDisplay levelDisplay = ModifierLevelDisplay.DEFAULT;
    /** Whether to show this modifier in tooltips */
    private TooltipDisplay tooltipDisplay = TooltipDisplay.ALWAYS;
    /** Priority level for this modifier */
    private int priority = DEFAULT_PRIORITY;

    /** Builds the final modifier */
    public BasicModifier build() {
      return new BasicModifier(hookMap, levelDisplay, tooltipDisplay, priority);
    }
  }
}
