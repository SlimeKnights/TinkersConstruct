package slimeknights.tconstruct.plugin.jei;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.Util;

public class DryingRecipeCategory implements IRecipeCategory {

  public static String CATEGORY = Util.prefix("dryingrack");
  public static ResourceLocation background_loc = Util.getResource("textures/gui/jei/dryingrack.png");

  private final IDrawable background;
  private final IDrawableAnimated arrow;

  public DryingRecipeCategory(IGuiHelper guiHelper) {
    background = guiHelper.createDrawable(background_loc, 0, 0, 160, 60, 0, 0, 0, 0);
    
    IDrawableStatic arrowDrawable = guiHelper.createDrawable(background_loc, 160, 0, 24, 17);
    arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
  }
  
  @Nonnull
  @Override
  public String getUid() {
    return CATEGORY;
  }

  @Nonnull
  @Override
  public String getTitle() {
      return Util.translate("gui.jei.drying.title");
  }

  @Nonnull
  @Override
  public IDrawable getBackground() {
      return background;
  }

  @Override
  public void drawExtras(Minecraft minecraft) {
  }

  @Override
  public void drawAnimations(Minecraft minecraft) {
      arrow.draw(minecraft, 67, 18);
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
    if(recipeWrapper instanceof DryingRecipeWrapper) {
      DryingRecipeWrapper recipe = (DryingRecipeWrapper) recipeWrapper;
      IGuiItemStackGroup items = recipeLayout.getItemStacks();
      
      items.init(0, true, 43, 17);
      items.setFromRecipe(0, recipe.getInputs());
      
      items.init(1, false, 97, 17);
      items.setFromRecipe(1, recipe.getOutputs());
    }
  }

}
