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
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ForgeI18n;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.recipe.modifiers.BeheadingRecipe;
import slimeknights.tconstruct.plugin.jei.JEIPlugin;
import slimeknights.tconstruct.plugin.jei.TConstructRecipeCategoryUid;
import slimeknights.tconstruct.tools.TinkerTools;

public class BeheadingCategory implements IRecipeCategory<BeheadingRecipe> {
  public static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/tinker_station.png");
  private static final String KEY_TITLE = Util.makeTranslationKey("jei", "beheading.title");

  /** Renderer instance to use in this category */
  private final EntityIngredientRenderer entityRenderer = new EntityIngredientRenderer(32);

  @Getter
  private final IDrawable background;
  @Getter
  private final IDrawable icon;
  @Getter
  private final String title;
  public BeheadingCategory(IGuiHelper helper) {
    this.title = ForgeI18n.getPattern(KEY_TITLE);
    this.background = helper.createDrawable(BACKGROUND_LOC, 0, 78, 100, 38);
    this.icon = helper.createDrawableIngredient(TinkerTools.axe.get().buildToolForRendering()); // TODO: cleaver
  }

  @Override
  public ResourceLocation getUid() {
    return TConstructRecipeCategoryUid.beheading;
  }

  @Override
  public Class<? extends BeheadingRecipe> getRecipeClass() {
    return BeheadingRecipe.class;
  }

  @Override
  public void setIngredients(BeheadingRecipe recipe, IIngredients ingredients) {
    ingredients.setInputLists(JEIPlugin.ENTITY_TYPE, recipe.getDisplayInputs());
    ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutput());
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void setRecipe(IRecipeLayout layout, BeheadingRecipe recipe, IIngredients ingredients) {
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
