package slimeknights.tconstruct.plugin.jei.casting;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

public abstract class AbstractCastingCategory implements IRecipeCategory<IDisplayableCastingRecipe>, ITooltipCallback<FluidStack> {
  private static final int INPUT_SLOT = 0;
  private static final int OUTPUT_SLOT = 1;
  private static final String KEY_COOLING_TIME = Util.makeTranslationKey("jei", "time");
  private static final String KEY_CAST_KEPT = Util.makeTranslationKey("jei", "casting.cast_kept");
  private static final String KEY_CAST_CONSUMED = Util.makeTranslationKey("jei", "casting.cast_consumed");
  protected static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/casting.png");

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  @Getter
  private final String title;
  private final IDrawable tankOverlay;
  private final IDrawable castConsumed;
  private final IDrawable castKept;
  private final IDrawable block;
  private final LoadingCache<Integer,IDrawableAnimated> cachedArrows;

  protected AbstractCastingCategory(IGuiHelper guiHelper, Block icon, String translationKey, IDrawable block) {
    this.background = guiHelper.createDrawable(BACKGROUND_LOC, 0, 0, 117, 54);
    this.icon = guiHelper.createDrawableIngredient(new ItemStack(icon));
    this.title = ForgeI18n.getPattern(translationKey);
    this.tankOverlay = guiHelper.createDrawable(BACKGROUND_LOC, 133, 0, 32, 32);
    this.castConsumed = guiHelper.createDrawable(BACKGROUND_LOC, 141, 32, 13, 11);
    this.castKept = guiHelper.createDrawable(BACKGROUND_LOC, 141, 43, 13, 11);
    this.block = block;
    this.cachedArrows = CacheBuilder.newBuilder().maximumSize(25L).build(new CacheLoader<Integer,IDrawableAnimated>() {
      @Override
      public IDrawableAnimated load(Integer coolingTime) {
        return guiHelper.drawableBuilder(BACKGROUND_LOC, 117, 32, 24, 17).buildAnimated(coolingTime, IDrawableAnimated.StartDirection.LEFT, false);
      }
    });
  }

  @Override
  public abstract boolean isHandled(IDisplayableCastingRecipe recipe);

  @Override
  public Class<? extends IDisplayableCastingRecipe> getRecipeClass() {
    return IDisplayableCastingRecipe.class;
  }

  @Override
  public void setIngredients(IDisplayableCastingRecipe recipe, IIngredients ingredients) {
    ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(recipe.getCastItems()));
    ingredients.setInputLists(VanillaTypes.FLUID, Collections.singletonList(recipe.getFluids()));
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
  }

  @Override
  public void draw(IDisplayableCastingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
    cachedArrows.getUnchecked(recipe.getCoolingTime()).draw(matrixStack, 58, 18);
    block.draw(matrixStack, 38, 35);
    if (recipe.hasCast()) {
      (recipe.isConsumed() ? castConsumed : castKept).draw(matrixStack, 63, 39);
    }

    int coolingTime = recipe.getCoolingTime() / 20;
    String coolingString = I18n.format(KEY_COOLING_TIME, coolingTime);
    FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    int x = 72 - fontRenderer.getStringWidth(coolingString) / 2;
    fontRenderer.drawString(matrixStack, coolingString, x, 2, Color.GRAY.getRGB());
  }

  @Override
  public List<ITextComponent> getTooltipStrings(IDisplayableCastingRecipe recipe, double mouseX, double mouseY) {
    if (recipe.hasCast() && GuiUtil.isHovered((int)mouseX, (int)mouseY, 63, 39, 13, 11)) {
      return Collections.singletonList(new TranslationTextComponent(recipe.isConsumed() ? KEY_CAST_CONSUMED : KEY_CAST_KEPT));
    }
    return Collections.emptyList();
  }

  @Override
  public void setRecipe(IRecipeLayout recipeLayout, IDisplayableCastingRecipe recipe, IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    guiItemStacks.init(INPUT_SLOT, true, 37, 18);
    guiItemStacks.init(OUTPUT_SLOT, false, 92, 17);
    guiItemStacks.set(ingredients);

    IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
    fluidStacks.addTooltipCallback(this);
    int capacity = MaterialValues.VALUE_Block;
    fluidStacks.init(0, true, 3, 3, 32, 32, capacity, false, tankOverlay);
    fluidStacks.set(ingredients);
    int h = 11;
    if (!recipe.hasCast()) {
      h += 16;
    }
    fluidStacks.init(1, true, 43, 8, 6, h, 1, false, null);
    fluidStacks.set(1, recipe.getFluids());
  }

  @Override
  public void onTooltip(int index, boolean input, FluidStack stack, List<ITextComponent> list) {
    ITextComponent name = list.get(0);
    ITextComponent modId = list.get(list.size() - 1);
    list.clear();
    list.add(name);
    FluidTooltipHandler.appendMaterial(stack, list);
    list.add(modId);
  }
}
