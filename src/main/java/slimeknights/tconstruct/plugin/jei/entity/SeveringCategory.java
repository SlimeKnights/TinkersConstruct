package slimeknights.tconstruct.plugin.jei.entity;

import lombok.Getter;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
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
    this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM, TinkerTools.cleaver.get().getRenderTool());
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.severing;
  }

  @Override
  public Class<? extends SeveringRecipe> getRecipeClass() {
    return SeveringRecipe.class;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }

  @Override
  public void setIngredients(SeveringRecipe recipe, IIngredients ingredients) {
    ingredients.setInputLists(JEIPlugin.ENTITY_TYPE, recipe.getDisplayInputs());
    ingredients.setInputLists(VanillaTypes.ITEM, EntityMeltingRecipeCategory.getSpawnEggs(recipe.getInputs().stream()));
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void setRecipe(IRecipeLayout layout, SeveringRecipe recipe, IIngredients ingredients) {
    IGuiIngredientGroup<EntityType> entityTypes = layout.getIngredientsGroup(JEIPlugin.ENTITY_TYPE);
    entityTypes.init(0, true, entityRenderer, 3, 3, 32, 32, 0, 0);
    entityTypes.set(ingredients);
    EntityIngredientHelper.setFocus(layout, entityTypes, recipe.getInputs(), 0);

    // output
    IGuiItemStackGroup items = layout.getItemStacks();
    items.init(1, false, 75, 10);
    items.set(ingredients);
  }
}
