package slimeknights.tconstruct.plugin.jei;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;

public class SmeltingRecipeCategory implements IRecipeCategory {

  public static String CATEGORY_Smeltery = Util.prefix("smeltery");
  public static ResourceLocation background_loc = Util.getResource("textures/gui/jei/smeltery.png");

  private final IDrawable background;

  public SmeltingRecipeCategory(IGuiHelper guiHelper) {
    background = guiHelper.createDrawable(background_loc, 0, 0, 160, 60, 0, 0, 0, 0);
  }

  @Nonnull
  @Override
  public String getUid() {
    return CATEGORY_Smeltery;
  }

  @Nonnull
  @Override
  public String getTitle() {
    return Util.translate("gui.jei.smelting.title");
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

  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull IRecipeWrapper recipeWrapper) {
    IGuiItemStackGroup items = recipeLayout.getItemStacks();
    items.init(0, true, 27, 21);
    items.setFromRecipe(0, recipeWrapper.getInputs());

    if(recipeWrapper instanceof SmelteryRecipeWrapper) {
      SmelteryRecipeWrapper recipe = (SmelteryRecipeWrapper) recipeWrapper;

      IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
      fluids.init(0, true, 115, 6, 18, 32, Material.VALUE_Block, null);
      fluids.set(0, recipe.outputs);
    }
  }
}
