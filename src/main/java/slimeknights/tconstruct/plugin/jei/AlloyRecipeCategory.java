package slimeknights.tconstruct.plugin.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.List;

/**
 * Alloy recipe category for JEI display
 */
public class AlloyRecipeCategory implements IRecipeCategory<AlloyRecipe>, ITooltipCallback<FluidStack> {
  private static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/alloy.png");
  private static final String KEY_TITLE = Util.makeTranslationKey("jei", "alloy.title");

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  @Getter
  private final String title;
  private final IDrawable arrow;
  private final IDrawable tank;

  public AlloyRecipeCategory(IGuiHelper helper) {
    this.title = ForgeI18n.getPattern(KEY_TITLE);
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 0, 166, 62);
    this.icon = helper.createDrawableIngredient(new ItemStack(TinkerSmeltery.smelteryController));
    this.arrow = helper.drawableBuilder(BACKGROUND_LOC, 166, 0, 24, 17).buildAnimated(200, StartDirection.LEFT, false);
    this.tank = helper.createDrawable(BACKGROUND_LOC, 166, 17, 16, 16);
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.alloy;
  }

  @Override
  public Class<? extends AlloyRecipe> getRecipeClass() {
    return AlloyRecipe.class;
  }

  @Override
  public void setIngredients(AlloyRecipe recipe, IIngredients ingredients) {
    ingredients.setInputLists(VanillaTypes.FLUID, recipe.getDisplayInputs());
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutput());
  }

  @Override
  public void draw(AlloyRecipe recipe, MatrixStack matrices, double mouseX, double mouseY) {
    arrow.draw(matrices, 87, 21);
  }

  @Override
  public void setRecipe(IRecipeLayout layout, AlloyRecipe recipe, IIngredients ingredients) {
    // find maximum used amount in the recipe so relations are correct
    List<List<FluidStack>> inputs = recipe.getDisplayInputs();
    int maxAmount = recipe.getOutput().getAmount();
    for(List<FluidStack> inputList : inputs) {
      for(FluidStack input : inputList) {
        if (input.getAmount() > maxAmount) {
          maxAmount = input.getAmount();
        }
      }
    }

    // inputs
    IGuiFluidStackGroup fluids = layout.getFluidStacks();
    fluids.addTooltipCallback(this);
    float width = 48f / inputs.size();
    for (int i = 0; i < inputs.size(); i++) {
      int x = 19 + (int) (i * width);
      int w = (int) ((i + 1) * width - i * width);
      fluids.init(i + 2, true, x, 11, w, 32, maxAmount, false, null);
    }

    // output
    fluids.init(0, false, 131, 11, 16, 32, maxAmount, false, null);
    fluids.set(ingredients);

    // show fuels that are valid for this recipe
    fluids.init(1, true, 91, 43, 16, 16, 1, false, tank);
    fluids.set(1, MeltingFuelHandler.getUsableFuels(1));
  }

  @Override
  public void onTooltip(int index, boolean input, FluidStack stack, List<ITextComponent> list) {
    Fluid fluid = stack.getFluid();
    if (fluid != null) {
      ITextComponent name = list.get(0);
      ITextComponent modId = list.get(list.size() - 1);
      list.clear();
      list.add(name);

      // multiply amount by 5 to put in terms of seconds
      if (index != 1) {
        FluidTooltipHandler.appendMaterial(stack.getFluid(), stack.getAmount() * 5, true, list);
      }
      list.add(modId);
    }
  }
}
