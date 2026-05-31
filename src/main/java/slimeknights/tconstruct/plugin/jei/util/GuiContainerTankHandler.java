package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import slimeknights.tconstruct.smeltery.client.screen.IScreenWithFluidTank;
import slimeknights.tconstruct.smeltery.client.screen.IScreenWithFluidTank.FluidLocation;

import java.util.Optional;

/**
 * Class to pass {@link IScreenWithFluidTank} into JEI
 */
public class GuiContainerTankHandler<C extends AbstractContainerMenu, T extends AbstractContainerScreen<C> & IScreenWithFluidTank> implements IGuiContainerHandler<T> {
  @Override
  public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(T containerScreen, double mouseX, double mouseY) {
    FluidLocation fluid = containerScreen.getFluidUnderMouse((int)mouseX, (int)mouseY);
    if (fluid != null) {
      return Optional.of(new ClickableIngredient<>(NeoForgeTypes.FLUID_STACK, fluid.fluid(), fluid.location()));
    }
    return Optional.empty();
  }
}
