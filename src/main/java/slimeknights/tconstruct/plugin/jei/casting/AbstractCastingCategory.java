package slimeknights.tconstruct.plugin.jei.casting;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.recipe.AbstractCastingRecipe;

public abstract class AbstractCastingCategory<T extends AbstractCastingRecipe> extends CastingVariantCategory<T> {
  public static final ResourceLocation backgroundLoc = Util.getResource("textures/gui/jei/casting.png");
  private final IDrawable background;
  private final IDrawable icon;
  private final String localizedName;
  protected final IDrawableAnimated arrow;

  AbstractCastingCategory(IGuiHelper guiHelper, Block icon, String translationKey, int regularCoolingTime) {
    super(guiHelper);
    background = guiHelper.createDrawable(backgroundLoc, 0, 0, 141, 61);
    this.icon = guiHelper.createDrawableIngredient(new ItemStack(icon));
    localizedName = Util.translate(translationKey);
    this.arrow = guiHelper.drawableBuilder(backgroundLoc, 141,32,24, 17)
      .buildAnimated(regularCoolingTime, IDrawableAnimated.StartDirection.LEFT, false);
  }

  public ResourceLocation getBackgroundLoc() {
    return backgroundLoc;
  }
  @Override
  public IDrawable getBackground() {
    return background;
  }

  @Override
  public IDrawable getIcon() {
    return icon;
  }

  @Override
  public void setIngredients(T recipe, IIngredients ingredients) {
//    ingredients.setAInputIngredients(recipe.getCast());
//    ingredients.setInput(VanillaTypes.ITEM, recipe.getCast());
    ingredients.setInput(VanillaTypes.FLUID, recipe.getFluidStack());
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
  }

  @Override
  public void draw(T recipe, double mouseX, double mouseY) {
    arrow.draw(80, 25);

    //int coolingTime = recipe.getCoolingTime();

  }

  @Override
  public String getTitle() {
    return localizedName;
  }

  @Override
  public void setRecipe(IRecipeLayout recipeLayout, T recipe, IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    guiItemStacks.init(inputSlot, true, 58, 25);
    guiItemStacks.init(outputSlot, false, 113, 24);
    guiItemStacks.set(ingredients);

    IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
    FluidStack fluid = recipe.getFluidStack();
    fluidStacks.init(0, true, 22, 10, 18, 32, 1296, false, null);
    fluidStacks.set(ingredients);
    fluidStacks.init(1, true, 64, 15, 6, 27, recipe.getFluidAmount(), false, null);
    fluidStacks.set(1, fluid);
    // TODO:
  }
}
