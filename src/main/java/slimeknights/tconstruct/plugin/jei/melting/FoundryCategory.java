package slimeknights.tconstruct.plugin.jei.melting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.plugin.jei.AlloyRecipeCategory;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.List;

/** Extension of melting for byproducts, but ditchs solid fuels */
public class FoundryCategory extends AbstractMeltingCategory {
  private static final Component TITLE = TConstruct.makeTranslation("jei", "foundry.title");

  /** Tooltip callback for fluids */
  private static final IRecipeSlotTooltipCallback METAL_ORE_TOOLTIP = new MeltingFluidCallback(OreRateType.METAL);
  private static final IRecipeSlotTooltipCallback GEM_ORE_TOOLTIP = new MeltingFluidCallback(OreRateType.GEM);
  @Getter
  private final IDrawable icon;

  public FoundryCategory(IGuiHelper helper) {
    super(helper);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(TinkerSmeltery.foundryController));
  }

  @SuppressWarnings("removal")
  @Override
  public ResourceLocation getUid() {
    return TConstructJEIConstants.FOUNDRY.getUid();
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
    OreRateType oreType = recipe.getOreType();
    IRecipeSlotTooltipCallback tooltip;
    if (oreType == OreRateType.METAL) {
      tooltip = METAL_ORE_TOOLTIP;
    } else if (oreType == OreRateType.GEM) {
      tooltip = GEM_ORE_TOOLTIP;
    } else {
      tooltip = MeltingFluidCallback.INSTANCE;
    }
    AlloyRecipeCategory.drawVariableFluids(builder, RecipeIngredientRole.OUTPUT, 96, 4, 32, 32, recipe.getOutputWithByproducts(), FluidValues.METAL_BLOCK, tooltip);

    // fuel
    builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 4, 4)
           .addTooltipCallback(FUEL_TOOLTIP)
           .setFluidRenderer(1, false, 12, 32)
           .addIngredients(VanillaTypes.FLUID, MeltingFuelHandler.getUsableFuels(recipe.getTemperature()));
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
