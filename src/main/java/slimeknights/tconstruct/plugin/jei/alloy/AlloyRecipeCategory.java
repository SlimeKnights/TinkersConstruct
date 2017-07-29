package slimeknights.tconstruct.plugin.jei.alloy;

import java.util.List;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;

public class AlloyRecipeCategory implements IRecipeCategory<AlloyRecipeWrapper> {

  public static String CATEGORY = Util.prefix("alloy");
  public static ResourceLocation background_loc = Util.getResource("textures/gui/jei/smeltery.png");

  protected final IDrawable background;
  protected final IDrawableAnimated arrow;

  public AlloyRecipeCategory(IGuiHelper guiHelper) {
    background = guiHelper.createDrawable(background_loc, 0, 60, 160, 60);

    IDrawableStatic arrowDrawable = guiHelper.createDrawable(background_loc, 160, 60, 24, 17);
    this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
  }

  @Nonnull
  @Override
  public String getUid() {
    return CATEGORY;
  }

  @Nonnull
  @Override
  public String getTitle() {
    return Util.translate("gui.jei.alloy.title");
  }

  @Nonnull
  @Override
  public IDrawable getBackground() {
    return background;
  }

  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {
    arrow.draw(minecraft, 76, 22);
  }

  @Override
  public void setRecipe(IRecipeLayout recipeLayout, AlloyRecipeWrapper recipe, IIngredients ingredients) {
    IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();

    List<FluidStack> inputs = recipe.inputs;
    List<FluidStack> outputs = ingredients.getOutputs(FluidStack.class).get(0);

    float w = 36f / inputs.size();

    // find maximum used amount in the recipe so relations are correct
    int max_amount = 0;
    for(FluidStack fs : inputs) {
      if(fs.amount > max_amount) {
        max_amount = fs.amount;
      }
    }
    for(FluidStack fs : outputs) {
      if(fs.amount > max_amount) {
        max_amount = fs.amount;
      }
    }

    // inputs
    for(int i = 0; i < inputs.size(); i++) {
      int x = 21 + (int) (i * w);
      int _w = (int) ((i + 1) * w - i * w);
      fluids.init(i + 1, true, x, 11, _w, 32, max_amount, false, null);
    }

    // output
    fluids.init(0, false, 118, 11, 18, 32, max_amount, false, null);
    fluids.set(ingredients);
  }

  @Override
  public List<String> getTooltipStrings(int mouseX, int mouseY) {
    return ImmutableList.of();
  }

  @Override
  public IDrawable getIcon() {
    // use the default icon
    return null;
  }

  @Override
  public String getModName() {
    return TConstruct.modName;
  }
}
