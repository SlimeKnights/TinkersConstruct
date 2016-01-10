package slimeknights.tconstruct.plugin.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;

public class CastingRecipeCategory implements IRecipeCategory {

  public static String CATEGORY = Util.prefix("casting_table");
  public static ResourceLocation background_loc = Util.getResource("textures/gui/jei/casting.png");

  protected final IDrawable background;
  protected final IDrawableAnimated arrow;

  public final IDrawable castingTable;
  public final IDrawable castingBasin;

  protected CastingRecipeCategory(IGuiHelper guiHelper) {
    this.background = guiHelper.createDrawable(background_loc, 0, 0, 141, 61);

    IDrawableStatic arrowDrawable = guiHelper.createDrawable(background_loc, 141, 32, 24, 17);
    this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);

    this.castingTable = guiHelper.createDrawable(background_loc, 141, 0, 16, 16);
    this.castingBasin = guiHelper.createDrawable(background_loc, 141, 16, 16, 16);
  }

  @Nonnull
  @Override
  public String getUid() {
    return CATEGORY;
  }

  @Nonnull
  @Override
  public String getTitle() {
    return Util.translate("gui.jei.casting.title");
  }

  @Nonnull
  @Override
  public IDrawable getBackground() {
    return this.background;
  }

  @Override
  public void drawExtras(Minecraft minecraft) {

  }

  @Override
  public void drawAnimations(Minecraft minecraft) {
    arrow.draw(minecraft, 79, 25);
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
    if(recipeWrapper instanceof CastingRecipeWrapper) {
      CastingRecipeWrapper recipe = (CastingRecipeWrapper) recipeWrapper;
      IGuiItemStackGroup items = recipeLayout.getItemStacks();
      IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();

      int cap = recipe.getFluidInputs().get(0).amount;

      items.init(0, true, 58, 25);
      items.setFromRecipe(0, recipe.getInputs());

      items.init(1, false, 113, 24);
      items.setFromRecipe(1, recipe.getOutputs());

      fluids.init(0, true, 22, 10, 18, 32, Material.VALUE_Block, false, null);
      fluids.set(0, recipe.getFluidInputs());

      // no cast, bigger fluid
      int h = 11;
      if(recipe.getInputs().isEmpty()) {
        h += 15;
      }
      fluids.init(1, true, 64, 15, 6, h, cap, false, null);
      fluids.set(1, recipe.getFluidInputs());
    }
  }
}
