package slimeknights.tconstruct.plugin.jei.casting;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;

import java.awt.Color;

public abstract class AbstractCastingCategory<T extends AbstractCastingRecipe> implements IRecipeCategory<T> {
  protected static final int inputSlot = 0;
  protected static final int outputSlot = 1;
  public static final ResourceLocation backgroundLoc = Util.getResource("textures/gui/jei/casting.png");
  private final IDrawable background;
  private final IDrawable icon;
  private final String localizedName;
  protected final IDrawableAnimated arrow;

  AbstractCastingCategory(IGuiHelper guiHelper, Block icon, String translationKey, int regularCoolingTime) {
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
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setInputLists(VanillaTypes.FLUID, ImmutableList.of(recipe.getFluids()));
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
  }

  @Override
  public void draw(T recipe, double mouseX, double mouseY) {
    arrow.draw(80, 25);

    int coolingTime = recipe.getCoolingTime() / 20;
    // TODO: Localizable 's'
    String coolingString;// = Util.translate("gui.jei.casting.cooling_time", coolingTime);
    coolingString = String.format("%d s", coolingTime);
    FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    int x = 92;
    x -= fontRenderer.getStringWidth(coolingString) / 2;
    fontRenderer.drawString(coolingString, x, 16, Color.GRAY.getRGB());
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
    fluidStacks.init(0, true, 22, 10, 18, 32, MaterialValues.VALUE_Block, false, null);
    fluidStacks.set(ingredients);
    int h = 11;
    if (recipe.getCast() == Ingredient.EMPTY) {
      h += 16;
    }
    fluidStacks.init(1, true, 64, 15, 6, h, 1, false, null);
    fluidStacks.set(1, recipe.getFluids());
  }
}
