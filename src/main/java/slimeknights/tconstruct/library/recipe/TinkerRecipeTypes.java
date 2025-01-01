package slimeknights.tconstruct.library.recipe;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.worktable.IModifierWorktableRecipe;

/**
 * Class containing all of Tinkers Construct recipe types
 */
public class TinkerRecipeTypes {
  /** Deferred instance */
  private static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, TConstruct.MOD_ID);

  public static final RegistryObject<RecipeType<IPartBuilderRecipe>> PART_BUILDER = register("part_builder");
  public static final RegistryObject<RecipeType<MaterialRecipe>> MATERIAL = register("material");
  public static final RegistryObject<RecipeType<ITinkerStationRecipe>> TINKER_STATION = register("tinker_station");
  public static final RegistryObject<RecipeType<IModifierWorktableRecipe>> MODIFIER_WORKTABLE = register("modifier_worktable");

  // casting
  public static final RegistryObject<RecipeType<ICastingRecipe>> CASTING_BASIN = register("casting_basin");
  public static final RegistryObject<RecipeType<ICastingRecipe>> CASTING_TABLE = register("casting_table");
  public static final RegistryObject<RecipeType<MoldingRecipe>> MOLDING_TABLE = register("molding_table");
  public static final RegistryObject<RecipeType<MoldingRecipe>> MOLDING_BASIN = register("molding_basin");

  // smeltery
  public static final RegistryObject<RecipeType<IMeltingRecipe>> MELTING = register("melting");
  public static final RegistryObject<RecipeType<EntityMeltingRecipe>> ENTITY_MELTING = register("entity_melting");
  public static final RegistryObject<RecipeType<MeltingFuel>> FUEL = register("fuel");
  public static final RegistryObject<RecipeType<AlloyRecipe>> ALLOYING = register("alloying");

  // modifiers
  public static final RegistryObject<RecipeType<SeveringRecipe>> SEVERING = register("severing");

  /** Internal recipe type for recipes that are not pulled by any specific crafting block */
  public static final RegistryObject<RecipeType<Recipe<?>>> DATA = register("data");

  /** Initializes the deferred register */
  public static void init(IEventBus bus) {
    TYPES.register(bus);
  }

  /**
   * Registers a new recipe type, prefixing with the mod ID
   * @param name  Recipe type name
   * @param <T>   Recipe type
   * @return  Registered recipe type
   */
  static <T extends Recipe<?>> RegistryObject<RecipeType<T>> register(String name) {
    return TYPES.register(name, () -> new RecipeType<>() {
      @Override
      public String toString() {
        return TConstruct.MOD_ID + ":" + name;
      }
    });
  }
}
