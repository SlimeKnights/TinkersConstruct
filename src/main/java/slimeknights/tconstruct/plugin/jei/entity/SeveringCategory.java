package slimeknights.tconstruct.plugin.jei.entity;

import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.plugin.jei.MantleJEIConstants;
import slimeknights.mantle.plugin.jei.entity.EntityIngredientRenderer;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tools.TinkerTools;

public class SeveringCategory implements IRecipeCategory<SeveringRecipe> {
  public static final ResourceLocation BACKGROUND_LOC = TConstruct.getResource("textures/gui/jei/tinker_station.png");
  private static final Component TITLE = TConstruct.makeTranslation("jei", "severing.title");

  /** Renderer instance to use in this category */
  private final EntityIngredientRenderer entityRenderer = new EntityIngredientRenderer(32);

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  public SeveringCategory(IGuiHelper helper) {
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 78, 100, 38);
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, TinkerTools.cleaver.get().getRenderTool());
  }

  @Override
  public RecipeType<SeveringRecipe> getRecipeType() {
    return TConstructJEIConstants.SEVERING;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void setRecipe(IRecipeLayoutBuilder builder, SeveringRecipe recipe, IFocusGroup focuses) {
    EntityIngredient input = recipe.getIngredient();
    IIngredientAcceptor<?> entities = builder.addSlot(RecipeIngredientRole.INPUT, 3, 3)
           .setCustomRenderer(MantleJEIConstants.ENTITY_TYPE, entityRenderer)
           .addIngredients(MantleJEIConstants.ENTITY_TYPE, input.getDisplay());
    IIngredientAcceptor<?> eggs = builder.addInvisibleIngredients(RecipeIngredientRole.INPUT).addItemStacks(input.getEggs());
    builder.createFocusLink(entities, eggs);

    // output
    builder.addSlot(RecipeIngredientRole.OUTPUT, 76, 11).addItemStack(recipe.getOutput());
  }

  @Override
  public ResourceLocation getRegistryName(SeveringRecipe recipe) {
    return recipe.getId();
  }
}
