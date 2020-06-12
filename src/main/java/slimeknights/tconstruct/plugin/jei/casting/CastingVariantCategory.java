package slimeknights.tconstruct.plugin.jei.casting;

import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;

public abstract class CastingVariantCategory<T> implements IRecipeCategory<T> {
  protected static final int inputSlot = 0;
  protected static final int outputSlot = 1;

//  protected final IDrawableStatic staticFluid;
//  protected final IDrawableAnimated animatedFluid;

  public CastingVariantCategory(IGuiHelper guiHelper) {
//    staticFluid = guiHelper.createDrawable()
//    animatedFluid = guiHelper.createAnimatedDrawable(staticFluid,);
  }
}
