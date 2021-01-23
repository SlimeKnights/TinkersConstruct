package slimeknights.tconstruct.plugin.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.Collections;
import java.util.List;

/** Recipe category for molding casts */
public class MoldingRecipeCategory implements IRecipeCategory<MoldingRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/casting.png");
  private static final String KEY_TITLE = Util.makeTranslationKey("jei", "molding.title");
  private static final ITextComponent TOOLTIP_MOLD_CONSUMED = new TranslationTextComponent(Util.makeTranslationKey("jei", "molding.mold_consumed"));

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  @Getter
  private final String title;
  private final IDrawable table, downArrow, upArrow;
  public MoldingRecipeCategory(IGuiHelper helper) {
    this.title = ForgeI18n.getPattern(KEY_TITLE);
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 55, 70, 57);
    this.icon = helper.createDrawableIngredient(new ItemStack(TinkerSmeltery.blankCast.getSand()));
    this.table = helper.createDrawable(BACKGROUND_LOC, 117, 0, 16, 16);
    this.downArrow = helper.createDrawable(BACKGROUND_LOC, 70, 55, 6, 6);
    this.upArrow = helper.createDrawable(BACKGROUND_LOC, 76, 55, 6, 6);
    this.moldConsumed = helper.createDrawable(BACKGROUND_LOC, 141, 32, 13, 11);
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.molding;
  }

  @Override
  public Class<? extends MoldingRecipe> getRecipeClass() {
    return MoldingRecipe.class;
  }

  @Override
  public void draw(MoldingRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
    // if no mold, we "pickup" the item, so draw no table
    if (!recipe.getMold().hasNoMatchingItems()) {
      table.draw(matrixStack, 51, 40);
      downArrow.draw(matrixStack, 8, 17);
    } else {
      upArrow.draw(matrixStack, 8, 17);
    }
  }

  @Override
  public List<ITextComponent> getTooltipStrings(MoldingRecipe recipe, double mouseX, double mouseY) {
    if (recipe.isMoldConsumed() && !recipe.getMold().hasNoMatchingItems() && GuiUtil.isHovered((int)mouseX, (int)mouseY, 50, 7, 18, 18)) {
      return Collections.singletonList(TOOLTIP_MOLD_CONSUMED);
    }
    return Collections.emptyList();
  }

  @Override
  public void setIngredients(MoldingRecipe recipe, IIngredients ingredients) {
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
  }

  @Override
  public void setRecipe(IRecipeLayout layout, MoldingRecipe recipe, IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = layout.getItemStacks();
    // main item
    guiItemStacks.init(0, true, 2, 23);
    guiItemStacks.init(1, false, 50, 23);

    // if we have a mold, we are pressing into the table, so draw pressed item on input and output
    if (!recipe.getMold().hasNoMatchingItems()) {
      guiItemStacks.init(2, true, 2, 0);
      guiItemStacks.set(ingredients);
      if (!recipe.isMoldConsumed()) {
        guiItemStacks.init(3, true, 50, 7);
        guiItemStacks.set(3, ingredients.getInputs(VanillaTypes.ITEM).get(1));
      }
    } else {
      guiItemStacks.set(ingredients);
    }
  }
}
