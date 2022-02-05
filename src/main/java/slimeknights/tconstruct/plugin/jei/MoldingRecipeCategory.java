package slimeknights.tconstruct.plugin.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.Collections;
import java.util.List;

/** Recipe category for molding casts */
public class MoldingRecipeCategory implements IRecipeCategory<MoldingRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/casting.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "molding.title");
  private static final Component TOOLTIP_PATTERN_CONSUMED = new TranslatableComponent(TConstruct.makeTranslationKey("jei", "molding.pattern_consumed"));

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  private final IDrawable table, basin, downArrow, upArrow;
  public MoldingRecipeCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 55, 70, 57);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(TinkerSmeltery.blankSandCast.get()));
    this.table = helper.createDrawable(BACKGROUND_LOC, 117, 0, 16, 16);
    this.basin = helper.createDrawable(BACKGROUND_LOC, 117, 16, 16, 16);
    this.downArrow = helper.createDrawable(BACKGROUND_LOC, 70, 55, 6, 6);
    this.upArrow = helper.createDrawable(BACKGROUND_LOC, 76, 55, 6, 6);
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
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void draw(MoldingRecipe recipe, PoseStack matrixStack, double mouseX, double mouseY) {
    // draw the main block
    IDrawable block = recipe.getType() == RecipeTypes.MOLDING_BASIN ? basin : table;
    block.draw(matrixStack, 3, 40);

    // if no mold, we "pickup" the item, so draw no table
    if (!recipe.getPattern().isEmpty()) {
      block.draw(matrixStack, 51, 40);
      downArrow.draw(matrixStack, 8, 17);
    } else {
      upArrow.draw(matrixStack, 8, 17);
    }
  }

  @Override
  public List<Component> getTooltipStrings(MoldingRecipe recipe, double mouseX, double mouseY) {
    if (recipe.isPatternConsumed() && !recipe.getPattern().isEmpty() && GuiUtil.isHovered((int)mouseX, (int)mouseY, 50, 7, 18, 18)) {
      return Collections.singletonList(TOOLTIP_PATTERN_CONSUMED);
    }
    return Collections.emptyList();
  }

  @Override
  public void setIngredients(MoldingRecipe recipe, IIngredients ingredients) {
    ingredients.setInputIngredients(recipe.getIngredients());
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
  }

  @Override
  public void setRecipe(IRecipeLayout layout, MoldingRecipe recipe, IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = layout.getItemStacks();
    // main item
    guiItemStacks.init(0, true, 2, 23);
    guiItemStacks.init(1, false, 50, 23);

    // if we have a mold, we are pressing into the table, so draw pressed item on input and output
    if (!recipe.getPattern().isEmpty()) {
      guiItemStacks.init(2, true, 2, 0);
      guiItemStacks.set(ingredients);
      if (!recipe.isPatternConsumed()) {
        guiItemStacks.init(3, true, 50, 7);
        guiItemStacks.set(3, ingredients.getInputs(VanillaTypes.ITEM).get(1));
      }
    } else {
      guiItemStacks.set(ingredients);
    }
  }
}
