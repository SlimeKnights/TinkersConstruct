package slimeknights.tconstruct.plugin.jei.melting;

import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.melting.IMeltingInventory;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.plugin.jei.AlloyRecipeCategory;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.List;

/** Extension of melting for byproducts, but ditchs solid fuels */
public class FoundryCategory extends AbstractMeltingCategory {
  private static final String KEY_TITLE = Util.makeTranslationKey("jei", "foundry.title");

  /** Tooltip callback for fluids */
  private static final ITooltipCallback<FluidStack> FLUID_TOOLTIP = new MeltingFluidCallback(false);
  private static final ITooltipCallback<FluidStack> ORE_FLUID_TOOLTIP = new MeltingFluidCallback(true);

  @Getter
  private final String title;
  @Getter
  private final IDrawable icon;

  public FoundryCategory(IGuiHelper helper) {
    super(helper);
    this.icon = helper.createDrawableIngredient(new ItemStack(TinkerSmeltery.foundryController));
    this.title = ForgeI18n.getPattern(KEY_TITLE);
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.foundry;
  }

  @Override
  public void setIngredients(MeltingRecipe recipe, IIngredients ingredients) {
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setOutputLists(VanillaTypes.FLUID, recipe.getOutputWithByproducts());
  }

  @Override
  public void setRecipe(IRecipeLayout layout, MeltingRecipe recipe, IIngredients ingredients) {
    // input
    IGuiItemStackGroup items = layout.getItemStacks();
    items.init(0, true, 23, 17);
    items.set(ingredients);

    // outputs
    IGuiFluidStackGroup fluids = layout.getFluidStacks();
    AlloyRecipeCategory.drawVariableFluids(fluids, 0, false, 96, 4, 32, 32, recipe.getOutputWithByproducts(), MaterialValues.METAL_BLOCK);
    fluids.set(ingredients);

    // liquid fuel
    fluids.init(-1, true, 4, 4, 12, 32, 1, false, null);
    fluids.set(-1, MeltingFuelHandler.getUsableFuels(recipe.getTemperature()));
    fluids.addTooltipCallback(recipe.isOre() ? ORE_FLUID_TOOLTIP : FLUID_TOOLTIP);
  }

  /** Adds amounts to outputs and temperatures to fuels */
  private static class MeltingFluidCallback extends AbstractMeltingCategory.MeltingFluidCallback {
    public MeltingFluidCallback(boolean isOre) {
      super(isOre);
    }

    @Override
    protected boolean addOreTooltip(FluidStack stack, List<ITextComponent> list) {
      return FluidTooltipHandler.appendMaterialNoShift(stack.getFluid(), IMeltingInventory.applyOreBoost(stack.getAmount(), Config.COMMON.foundryNuggetsPerOre.get()), list);
    }
  }
}
