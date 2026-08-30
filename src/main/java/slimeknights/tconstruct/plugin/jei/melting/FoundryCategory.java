package slimeknights.tconstruct.plugin.jei.melting;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.plugin.jei.AlloyRecipeCategory;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.function.Function;

/** Extension of melting for byproducts, but ditchs solid fuels */
public class FoundryCategory extends AbstractMeltingCategory {
  private static final Component TITLE = TConstruct.makeTranslation("jei", "foundry.title");

  public FoundryCategory(IGuiHelper helper) {
    super(helper, TConstructJEIConstants.FOUNDRY, TITLE, helper.createDrawableItemLike(TinkerSmeltery.foundryController));
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, MeltingRecipe recipe, IFocusGroup focuses) {
    // input
    builder.addInputSlot(24, 18).addIngredients(recipe.getInput());

    // output fluid
    AlloyRecipeCategory.drawVariableFluidsWithRichTooltip(builder, i -> RecipeIngredientRole.OUTPUT, 96, 4, 32, 32, recipe.getOutputWithByproducts(), FluidValues.METAL_BLOCK, Function.identity(), list -> MeltingFluidCallback.INSTANCE);

    // fuel
    builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 4, 4)
           .addRichTooltipCallback(FUEL_TOOLTIP)
           .setFluidRenderer(1, false, 12, 32)
           .addIngredients(ForgeTypes.FLUID_STACK, MeltingFuelHandler.getUsableFuels(recipe.getTemperature()));
  }
}
