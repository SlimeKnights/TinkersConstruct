package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/** Helper for working with fluid tooltips */
@FunctionalInterface
public interface FluidTooltipCallback extends IRecipeSlotTooltipCallback {
  String AMOUNT_KEY = "jei.tooltip.liquid.amount";

  /** Default instance, simply replaces mb units with our unit handler. */
  FluidTooltipCallback UNITS = (fluid, recipeSlotView, tooltip) -> FluidTooltipHandler.appendMaterial(fluid, tooltip);

  /** Default instance, simply replaces mb units with our unit handler. */
  FluidTooltipCallback NO_AMOUNT = (fluid, recipeSlotView, tooltip) -> {};

  @Override
  default void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
    ListIterator<Component> listIterator = tooltip.listIterator();
    while (listIterator.hasNext()) {
      Component component = listIterator.next();
      if (component.getContents() instanceof TranslatableContents translatable && AMOUNT_KEY.equals(translatable.getKey())) {
        listIterator.remove();
        FluidStack fluid = recipeSlotView.getDisplayedIngredient(ForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY);
        List<Component> newTooltip = new ArrayList<>();
        onFluidTooltip(fluid, recipeSlotView, newTooltip);
        tooltip.addAll(listIterator.nextIndex(), newTooltip);
        return;
      }
    }
    // failed to find the tooltip to replace, so just append our stuff at the end
    FluidStack fluid = recipeSlotView.getDisplayedIngredient(ForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY);
    onFluidTooltip(fluid, recipeSlotView, tooltip);
  }

  /** Adds information about the fluid to the tooltip */
  void onFluidTooltip(FluidStack fluid, IRecipeSlotView recipeSlotView, List<Component> tooltip);
}
