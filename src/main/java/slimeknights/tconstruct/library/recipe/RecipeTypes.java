package slimeknights.tconstruct.library.recipe;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;

/**
 * Class containing all of Tinkers Construct recipe types
 */
public interface RecipeTypes {
  IRecipeType<IPartBuilderRecipe> PART_BUILDER = register("part_builder");
  IRecipeType<MaterialRecipe> MATERIAL = register("material");
  IRecipeType<ITinkerStationRecipe> TINKER_STATION = register("tinker_station");

  // casting
  IRecipeType<ICastingRecipe> CASTING_BASIN = register("casting_basin");
  IRecipeType<ICastingRecipe> CASTING_TABLE = register("casting_table");
  IRecipeType<MoldingRecipe> MOLDING_TABLE = register("molding_table");
  IRecipeType<MoldingRecipe> MOLDING_BASIN = register("molding_basin");

  // smeltery
  IRecipeType<IMeltingRecipe> MELTING = register("melting");
  IRecipeType<EntityMeltingRecipe> ENTITY_MELTING = register("entity_melting");
  IRecipeType<MeltingFuel> FUEL = register("fuel");
  IRecipeType<AlloyRecipe> ALLOYING = register("alloying");

  // modifiers
  IRecipeType<SeveringRecipe> SEVERING = register("severing");

  /** Internal recipe type for recipes that are not pulled by any specific crafting block */
  IRecipeType<IRecipe<?>> DATA = register("data");

  /**
   * Registers a new recipe type, prefixing with the mod ID
   * @param name  Recipe type name
   * @param <T>   Recipe type
   * @return  Registered recipe type
   */
  static <T extends IRecipe<?>> IRecipeType<T> register(String name) {
    return IRecipeType.register(TConstruct.modID + ":" + name);
  }
}
