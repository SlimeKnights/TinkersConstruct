package slimeknights.tconstruct.library.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.BeheadingRecipe;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;

/**
 * Class containing all of Tinkers Construct recipe types
 */
public interface RecipeTypes {
  RecipeType<PartRecipe> PART_BUILDER = register("part_builder");
  RecipeType<MaterialRecipe> MATERIAL = register("material");
  RecipeType<ITinkerStationRecipe> TINKER_STATION = register("tinker_station");

  // casting
  RecipeType<ICastingRecipe> CASTING_BASIN = register("casting_basin");
  RecipeType<ICastingRecipe> CASTING_TABLE = register("casting_table");
  RecipeType<MoldingRecipe> MOLDING_TABLE = register("molding_table");
  RecipeType<MoldingRecipe> MOLDING_BASIN = register("molding_basin");

  // smeltery
  RecipeType<IMeltingRecipe> MELTING = register("melting");
  RecipeType<EntityMeltingRecipe> ENTITY_MELTING = register("entity_melting");
  RecipeType<MeltingFuel> FUEL = register("fuel");
  RecipeType<AlloyRecipe> ALLOYING = register("alloying");

  // modifiers
  RecipeType<BeheadingRecipe> BEHEADING = register("beheading");

  /**
   * Registers a new recipe type, prefixing with the mod ID
   * @param name  Recipe type name
   * @param <T>   Recipe type
   * @return  Registered recipe type
   */
  static <T extends Recipe<?>> RecipeType<T> register(String name) {
    return RecipeType.register(TConstruct.modID + ":" + name);
  }
}
