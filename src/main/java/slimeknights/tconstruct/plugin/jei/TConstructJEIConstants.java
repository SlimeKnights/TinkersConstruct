package slimeknights.tconstruct.plugin.jei;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;

public class TConstructJEIConstants {
  public static final ResourceLocation PLUGIN = TConstruct.getResource("jei_plugin");

  // ingredient types
  @SuppressWarnings("rawtypes")
  public static final IIngredientType<EntityType> ENTITY_TYPE = () -> EntityType.class;
  public static final IIngredientType<ModifierEntry> MODIFIER_TYPE = () -> ModifierEntry.class;
  public static final IIngredientType<Pattern> PATTERN_TYPE = () -> Pattern.class;

  // casting
  public static final RecipeType<IDisplayableCastingRecipe> CASTING_BASIN = type("casting_basin", IDisplayableCastingRecipe.class);
  public static final RecipeType<IDisplayableCastingRecipe> CASTING_TABLE = type("casting_table", IDisplayableCastingRecipe.class);
  public static final RecipeType<MoldingRecipe> MOLDING = type("molding", MoldingRecipe.class);

  // melting
  public static final RecipeType<MeltingRecipe> MELTING = type("melting", MeltingRecipe.class);
  public static final RecipeType<EntityMeltingRecipe> ENTITY_MELTING = type("entity_melting", EntityMeltingRecipe.class);
  public static final RecipeType<AlloyRecipe> ALLOY = type("alloy", AlloyRecipe.class);
  public static final RecipeType<MeltingRecipe> FOUNDRY = type("foundry", MeltingRecipe.class);

  // tinker station
  public static final RecipeType<IDisplayModifierRecipe> MODIFIERS = type("modifiers", IDisplayModifierRecipe.class);
  public static final RecipeType<SeveringRecipe> SEVERING = type("severing", SeveringRecipe.class);

  // part builder
  public static final RecipeType<IDisplayPartBuilderRecipe> PART_BUILDER = type("part_builder", IDisplayPartBuilderRecipe.class);

  private static <T> RecipeType<T> type(String name, Class<T> clazz) {
    return RecipeType.create(TConstruct.MOD_ID, name, clazz);
  }
}
