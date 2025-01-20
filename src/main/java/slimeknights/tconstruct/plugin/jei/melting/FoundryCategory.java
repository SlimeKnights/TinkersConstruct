package slimeknights.tconstruct.plugin.jei.melting;

import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
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

  @Getter
  private final IDrawable icon;

  public FoundryCategory(IGuiHelper helper) {
    super(helper);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(TinkerSmeltery.foundryController));
  }

  @Override
  public RecipeType<MeltingRecipe> getRecipeType() {
    return TConstructJEIConstants.FOUNDRY;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, MeltingRecipe recipe, IFocusGroup focuses) {
    // input
    builder.addSlot(RecipeIngredientRole.INPUT, 24, 18).addIngredients(recipe.getInput());

    // output fluid
    AlloyRecipeCategory.drawVariableFluids(builder, RecipeIngredientRole.OUTPUT, 96, 4, 32, 32, recipe.getOutputWithByproducts(), FluidValues.METAL_BLOCK, Function.identity(), list -> MeltingFluidCallback.INSTANCE);

    // fuel
    builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 4, 4)
           .addTooltipCallback(FUEL_TOOLTIP)
           .setFluidRenderer(1, false, 12, 32)
           .addIngredients(ForgeTypes.FLUID_STACK, MeltingFuelHandler.getUsableFuels(recipe.getTemperature()));
  }
}
