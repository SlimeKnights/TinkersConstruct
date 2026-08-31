package slimeknights.tconstruct.plugin.jei.util;

import com.mojang.datafixers.util.Either;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/** Helper for working with fluid tooltips */
@SuppressWarnings("removal")
@FunctionalInterface
public interface FluidTooltipCallback extends mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback, IRecipeSlotRichTooltipCallback {
  String AMOUNT_KEY = "jei.tooltip.liquid.amount";

  /** Default instance, simply replaces mb units with our unit handler. */
  FluidTooltipCallback UNITS = (fluid, recipeSlotView, tooltip) -> FluidTooltipHandler.appendMaterial(fluid, tooltip);

  /** Instance that removes the amount from fluid tooltips. */
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

  @Override
  default void onRichTooltip(IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
    ListIterator<Either<FormattedText,TooltipComponent>> listIterator = tooltip.getLines().listIterator();
    while (listIterator.hasNext()) {
      FormattedText line = listIterator.next().left().orElse(null);
      if (line instanceof Component component && component.getContents() instanceof TranslatableContents translatable && AMOUNT_KEY.equals(translatable.getKey())) {
        listIterator.remove();
        FluidStack fluid = recipeSlotView.getDisplayedIngredient(ForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY);
        onFluidTooltip(fluid, recipeSlotView, tooltip);
        return;
      }
    }
    // failed to find the tooltip to replace, so just append our stuff at the end
    recipeSlotView.getDisplayedIngredient(ForgeTypes.FLUID_STACK)
      .ifPresent(fluid -> onFluidTooltip(fluid, recipeSlotView, tooltip));
  }

  /** Adds rich information about the fluid to the tooltip. */
  default void onFluidTooltip(FluidStack fluid, IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
    List<Component> newTooltip = new ArrayList<>();
    onFluidTooltip(fluid, recipeSlotView, newTooltip);
    tooltip.addAll(newTooltip);
  }

  /** Adds information about the fluid to the tooltip */
  void onFluidTooltip(FluidStack fluid, IRecipeSlotView recipeSlotView, List<Component> tooltip);
}
