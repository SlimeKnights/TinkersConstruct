package slimeknights.tconstruct.plugin.jei.smelting;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.List;

import javax.annotation.Nonnull;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.materials.Material;

public class SmeltingRecipeCategory implements IRecipeCategory<SmeltingRecipeWrapper> {

  public static String CATEGORY = Util.prefix("smeltery");
  public static ResourceLocation background_loc = Util.getResource("textures/gui/jei/smeltery.png");

  private final IDrawable background;
  private final IDrawable tankOverlay;

  public SmeltingRecipeCategory(IGuiHelper guiHelper) {
    background = guiHelper.createDrawable(background_loc, 0, 0, 160, 60, 0, 0, 0, 0);
    tankOverlay = guiHelper.createDrawable(background_loc, 160, 0, 18, 18);
  }

  @Nonnull
  @Override
  public String getUid() {
    return CATEGORY;
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
  public void drawExtras(@Nonnull Minecraft minecraft) {

  }

  @Override
  public void setRecipe(IRecipeLayout recipeLayout, SmeltingRecipeWrapper recipe, IIngredients ingredients) {
    IGuiItemStackGroup items = recipeLayout.getItemStacks();
    items.init(0, true, 27, 20);
    items.set(ingredients);

    IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
    fluids.addTooltipCallback(GuiUtil::onFluidTooltip);

    fluids.init(0, false, 115, 6, 18, 32, Material.VALUE_Block, false, null);
    fluids.set(ingredients);

    fluids.init(1, false, 72, 38, 16, 16, 1000, false, tankOverlay);
    fluids.set(1, recipe.fuels);
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
