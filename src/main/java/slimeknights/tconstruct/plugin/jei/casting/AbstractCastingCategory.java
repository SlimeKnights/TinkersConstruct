package slimeknights.tconstruct.plugin.jei.casting;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;

import java.awt.Color;
import java.util.Collections;
import java.util.List;


public abstract class AbstractCastingCategory<T extends ItemCastingRecipe> implements IRecipeCategory<T>, ITooltipCallback<FluidStack> {
  private static final int INPUT_SLOT = 0;
  private static final int OUTPUT_SLOT = 1;
  private static final String KEY_COOLING_TIME = "jei.tconstruct.casting.cooling_time";
  private static final String KEY_CAST_KEPT = "jei.tconstruct.casting.cast_kept";
  private static final String KEY_CAST_CONSUMED = "jei.tconstruct.casting.cast_consumed";
  protected static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/casting.png");

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  @Getter
  private final String title;
  private final IDrawableAnimated arrow;
  private final IDrawable tankOverlay;
  private final IDrawable castConsumed;
  private final IDrawable castKept;
  private final IDrawable block;

  protected AbstractCastingCategory(IGuiHelper guiHelper, Block icon, String translationKey, IDrawable block) {
    this.background = guiHelper.createDrawable(BACKGROUND_LOC, 0, 0, 117, 54);
    this.icon = guiHelper.createDrawableIngredient(new ItemStack(icon));
    this.title = ForgeI18n.getPattern(translationKey);
    this.arrow = guiHelper.drawableBuilder(BACKGROUND_LOC, 117, 32, 24, 17).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    this.tankOverlay = guiHelper.createDrawable(BACKGROUND_LOC, 133, 0, 32, 32);
    this.castConsumed = guiHelper.createDrawable(BACKGROUND_LOC, 141, 32, 13, 11);
    this.castKept = guiHelper.createDrawable(BACKGROUND_LOC, 141, 43, 13, 11);
    this.block = block;
  }

  @Override
  public void setIngredients(T recipe, IIngredients ingredients) {
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setInputLists(VanillaTypes.FLUID, ImmutableList.of(recipe.getFluids()));
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
  }

  @Override
  public void draw(T recipe, double mouseX, double mouseY) {
    arrow.draw(58, 18);
    block.draw(38, 35);
    if (recipe.getCast() != Ingredient.EMPTY) {
      (recipe.isConsumed() ? castConsumed : castKept).draw(63, 39);
    }

    int coolingTime = recipe.getCoolingTime() / 20;
    String coolingString = ForgeI18n.parseMessage(KEY_COOLING_TIME, coolingTime);
    FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    int x = 72 - fontRenderer.getStringWidth(coolingString) / 2;
    fontRenderer.drawString(coolingString, x, 2, Color.GRAY.getRGB());
  }

  @Override
  public List<String> getTooltipStrings(T recipe, double mouseX, double mouseY) {
    if (mouseX >= 63 && mouseY >= 39 && mouseX < 76 && mouseY < 50 && recipe.getCast() != Ingredient.EMPTY) {
      return Collections.singletonList(ForgeI18n.getPattern(recipe.isConsumed() ? KEY_CAST_CONSUMED : KEY_CAST_KEPT));
    }
    return Collections.emptyList();
  }

  @Override
  public void setRecipe(IRecipeLayout recipeLayout, T recipe, IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    guiItemStacks.init(INPUT_SLOT, true, 37, 18);
    guiItemStacks.init(OUTPUT_SLOT, false, 92, 17);
    guiItemStacks.set(ingredients);

    IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
    fluidStacks.addTooltipCallback(this);
    int capacity = MaterialValues.VALUE_Block;
    if (recipe.getType() == RecipeTypes.CASTING_TABLE) {
      capacity /= 2;
    }
    fluidStacks.init(0, true, 3, 3, 32, 32, capacity, false, tankOverlay);
    fluidStacks.set(ingredients);
    int h = 11;
    if (recipe.getCast() == Ingredient.EMPTY) {
      h += 16;
    }
    fluidStacks.init(1, true, 43, 8, 6, h, 1, false, null);
    fluidStacks.set(1, recipe.getFluids());
  }

  @Override
  public void onTooltip(int index, boolean input, FluidStack stack, List<String> list) {
    String name = list.get(0);
    String modId = list.get(list.size() - 1);
    list.clear();
    list.add(name);
    FluidTooltipHandler.appendMaterial(stack, list);
    list.add(modId);
  }
}
