package slimeknights.tconstruct.library.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipe;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;

/**
 * Class containing all of Tinkers Construct recipe types
 */
public interface RecipeTypes {
  RecipeType<IPartBuilderRecipe> PART_BUILDER = register("part_builder");
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
  RecipeType<SeveringRecipe> SEVERING = register("severing");
  RecipeType<SpillingRecipe> SPILLING = register("spilling");

  /** Internal recipe type for recipes that are not pulled by any specific crafting block */
  RecipeType<Recipe<?>> DATA = register("data");

  /**
   * Registers a new recipe type, prefixing with the mod ID
   * @param name  Recipe type name
   * @param <T>   Recipe type
   * @return  Registered recipe type
   */
  static <T extends Recipe<?>> RecipeType<T> register(String name) {
    return RecipeType.register(TConstruct.MOD_ID + ":" + name);
  }
}
