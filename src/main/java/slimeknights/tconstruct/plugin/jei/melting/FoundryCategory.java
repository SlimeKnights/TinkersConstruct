package slimeknights.tconstruct.plugin.jei.melting;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.melting.IDisplayableMeltingRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.plugin.jei.util.CategoryUtil;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Extension of melting for byproducts, but ditchs solid fuels */
public class FoundryCategory extends AbstractMeltingCategory {
  private static final Component TITLE = TConstruct.makeTranslation("jei", "foundry.title");

  public FoundryCategory(IGuiHelper helper) {
    super(helper, TConstructJEIConstants.FOUNDRY, TITLE, helper.createDrawableItemLike(TinkerSmeltery.foundryController));
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, IDisplayableMeltingRecipe recipe, IFocusGroup focuses) {
    // input
    List<ItemStack> inputs = recipe.getInputs();
    IRecipeSlotBuilder inputSlot = builder.addInputSlot(24, 18).addItemStacks(inputs);

    // output fluid
    List<List<FluidStack>> fluids = recipe.getOutputWithByproducts();
    List<IRecipeSlotBuilder> slots = new ArrayList<>(fluids.size() + 1);
    CategoryUtil.drawMultipleFluids(builder, i -> RecipeIngredientRole.OUTPUT, 96, 4, 32, 32, recipe.getOutputWithByproducts(), FluidValues.METAL_BLOCK, Function.identity(), list -> MeltingFluidCallback.INSTANCE, slots::add);

    // apply focus links to anything matching the first output size
    int size = fluids.get(0).size();
    if (fluids.get(0).size() > 1) {
      // remove any byproducts that have the wrong size
      for (int i = fluids.size() - 1; i >= 1; i--) {
        if (fluids.get(i).size() != size) {
          slots.remove(i);
        }
      }
      // add input if its size matches
      if (inputs.size() == size) {
        slots.add(inputSlot);
      }
      // link slots
      if (slots.size() > 1) {
        builder.createFocusLink(slots.toArray(IRecipeSlotBuilder[]::new));
      }
    }

    // fuel
    builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 4, 4)
           .addRichTooltipCallback(FUEL_TOOLTIP)
           .setFluidRenderer(1, false, 12, 32)
           .addIngredients(ForgeTypes.FLUID_STACK, MeltingFuelHandler.getUsableFuels(recipe.getTemperature()));
  }
}
