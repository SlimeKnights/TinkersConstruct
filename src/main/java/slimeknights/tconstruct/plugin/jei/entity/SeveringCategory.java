package slimeknights.tconstruct.plugin.jei.entity;

import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.plugin.jei.MantleJEIConstants;
import slimeknights.mantle.plugin.jei.entity.EntityIngredientRenderer;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tools.TinkerTools;

public class SeveringCategory extends AbstractRecipeCategory<SeveringRecipe> {
  public static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "severing.title");

  /** Renderer instance to use in this category */
  private final EntityIngredientRenderer entityRenderer = new EntityIngredientRenderer(32);

  public SeveringCategory(IGuiHelper helper) {
    super(TConstructJEIConstants.SEVERING, TITLE, helper.createDrawableItemStack(TinkerTools.cleaver.get().getRenderTool()), 100, 38);
  }

  @Override
  public void createRecipeExtras(IRecipeExtrasBuilder builder, SeveringRecipe recipe, IFocusGroup focuses) {
    builder.addRecipeArrow().setPosition(42, 10);
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, SeveringRecipe recipe, IFocusGroup focuses) {
    EntityIngredient input = recipe.getIngredient();
    IIngredientAcceptor<?> entities = builder.addInputSlot(3, 3)
           .setCustomRenderer(MantleJEIConstants.ENTITY_TYPE, entityRenderer)
           .addIngredients(MantleJEIConstants.ENTITY_TYPE, input.getDisplay());
    IIngredientAcceptor<?> eggs = builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(input.getEggs());
    builder.createFocusLink(entities, eggs);

    // output
    builder.addOutputSlot(76, 11).addItemStack(recipe.getOutput()).setOutputSlotBackground();
  }

  @Override
  public ResourceLocation getRegistryName(SeveringRecipe recipe) {
    return recipe.getId();
  }
}
