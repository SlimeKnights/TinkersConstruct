package slimeknights.tconstruct.plugin.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

/** Recipe category for molding casts */
public class MoldingRecipeCategory extends AbstractRecipeCategory<MoldingRecipe> {
  private static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/casting.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "molding.title");
  private static final Component TOOLTIP_PATTERN_CONSUMED = Component.translatable(TConstruct.makeTranslationKey("jei", "molding.pattern_consumed"));
  private static final IRecipeSlotRichTooltipCallback PATTERN_CONSUMED_TOOLTIP = (slot, tooltip) -> tooltip.add(TOOLTIP_PATTERN_CONSUMED);

  private final IDrawable table, basin, downArrow, upArrow;
  public MoldingRecipeCategory(IGuiHelper helper) {
    super(TConstructJEIConstants.MOLDING, TITLE, helper.createDrawableItemLike(TinkerSmeltery.blankSandCast), 70, 57);
    this.table = helper.createDrawable(BACKGROUND_LOC, 117, 0, 16, 16);
    this.basin = helper.createDrawable(BACKGROUND_LOC, 117, 16, 16, 16);
    this.downArrow = helper.createDrawable(BACKGROUND_LOC, 70, 55, 6, 6);
    this.upArrow = helper.createDrawable(BACKGROUND_LOC, 76, 55, 6, 6);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, MoldingRecipe recipe, IFocusGroup focuses) {
    builder.addRecipeArrow().setPosition(24, 23);
    builder.addDrawable(recipe.getPattern().isEmpty() ? upArrow : downArrow, 8, 17);
  }

  @Override
  public void draw(MoldingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
    // draw the main block
    IDrawable block = recipe.getType() == TinkerRecipeTypes.MOLDING_BASIN.get() ? basin : table;
    block.draw(graphics, 3, 40);

    // if no mold, we "pickup" the item, so draw no table
    if (!recipe.getPattern().isEmpty()) {
      block.draw(graphics, 51, 40);
    }
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, MoldingRecipe recipe, IFocusGroup focuses) {
    // basic input output
    builder.addInputSlot(3, 24).addIngredients(recipe.getMaterial());
    RegistryAccess access = SafeClientAccess.getRegistryAccess();
    if (access != null) {
      builder.addOutputSlot(51, 24).addItemStack(recipe.getResultItem(access));
    }

    // if we have a mold, we are pressing into the table, so draw pressed item on input and output
    Ingredient pattern = recipe.getPattern();
    if (!pattern.isEmpty()) {
      boolean patternConsumed = recipe.isPatternConsumed();
      IRecipeSlotBuilder inputSlot = builder.addSlot(patternConsumed ? RecipeIngredientRole.INPUT : RecipeIngredientRole.CATALYST, 3, 1)
        .addIngredients(pattern);
      if (patternConsumed) {
        inputSlot.addRichTooltipCallback(PATTERN_CONSUMED_TOOLTIP);
      } else {
        IRecipeSlotBuilder preservedSlot = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 51, 8).addIngredients(pattern);
        builder.createFocusLink(inputSlot, preservedSlot);
      }
    }
  }

  @Override
  public ResourceLocation getRegistryName(MoldingRecipe recipe) {
    return recipe.getId();
  }
}
