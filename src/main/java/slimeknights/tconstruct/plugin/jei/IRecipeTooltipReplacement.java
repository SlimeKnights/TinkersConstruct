package slimeknights.tconstruct.plugin.jei;

import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.network.chat.Component;

import java.util.List;

/** Implementation of tooltips that preserves the name and mod ID, but replaces the contents between, which basically all of TiC's tooltips use */
@FunctionalInterface
public interface IRecipeTooltipReplacement extends IRecipeSlotTooltipCallback {
  /** Tooltip replacement that keeps just the name and mod ID */
  IRecipeTooltipReplacement EMPTY = (slot, tooltip) -> {};

  @Override
  default void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
    Component name = tooltip.get(0);
    Component modId = tooltip.get(tooltip.size() - 1);
    tooltip.clear();
    tooltip.add(name);
    addMiddleLines(recipeSlotView, tooltip);
    // only add last line if we actually have mod IDs
    if (JEIPlugin.modIdHelper != null && JEIPlugin.modIdHelper.isDisplayingModNameEnabled()) {
      tooltip.add(modId);
    }
  }

  /** Adds the lines between the name and mod ID */
  void addMiddleLines(IRecipeSlotView recipeSlotView, List<Component> tooltip);
}
