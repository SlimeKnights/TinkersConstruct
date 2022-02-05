package slimeknights.tconstruct.plugin.jei.melting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.fluid.FluidTooltipHandler;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.plugin.jei.AlloyRecipeCategory;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.List;

/** Extension of melting for byproducts, but ditchs solid fuels */
public class FoundryCategory extends AbstractMeltingCategory {
  private static final Component TITLE = TConstruct.makeTranslation("jei", "foundry.title");

  /** Tooltip callback for fluids */
  private static final ITooltipCallback<FluidStack> METAL_ORE_TOOLTIP = new MeltingFluidCallback(OreRateType.METAL);
  private static final ITooltipCallback<FluidStack> GEM_ORE_TOOLTIP = new MeltingFluidCallback(OreRateType.GEM);
  @Getter
  private final IDrawable icon;

  public FoundryCategory(IGuiHelper helper) {
    super(helper);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(TinkerSmeltery.foundryController));
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.foundry;
  }

  @Override
  public Component getTitle() {
    return TITLE;
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
    AlloyRecipeCategory.drawVariableFluids(fluids, 0, false, 96, 4, 32, 32, recipe.getOutputWithByproducts(), FluidValues.METAL_BLOCK);
    fluids.set(ingredients);

    // liquid fuel
    fluids.init(-1, true, 4, 4, 12, 32, 1, false, null);
    fluids.set(-1, MeltingFuelHandler.getUsableFuels(recipe.getTemperature()));

    // change tooltip for ore boosted recipes
    OreRateType oreType = recipe.getOreType();
    if (oreType == OreRateType.METAL) {
      fluids.addTooltipCallback(METAL_ORE_TOOLTIP);
    } else if (oreType == OreRateType.GEM) {
      fluids.addTooltipCallback(GEM_ORE_TOOLTIP);
    } else {
      fluids.addTooltipCallback(MeltingFluidCallback.INSTANCE);
    }
  }

  /** Adds amounts to outputs and temperatures to fuels */
  @RequiredArgsConstructor
  private static class MeltingFluidCallback extends AbstractMeltingCategory.MeltingFluidCallback {
    @Getter
    private final OreRateType oreRate;

    @Override
    protected boolean appendMaterial(FluidStack stack, List<Component> list) {
      return FluidTooltipHandler.appendMaterialNoShift(stack.getFluid(), Config.COMMON.foundryOreRate.applyOreBoost(oreRate, stack.getAmount()), list);
    }
  }
}
