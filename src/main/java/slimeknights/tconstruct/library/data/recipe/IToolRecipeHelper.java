package slimeknights.tconstruct.library.data.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CompoundIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Interface for tool and part crafting recipes
 */
public interface IToolRecipeHelper extends ICastCreationHelper {

  /**
   * Registers recipe for tool building
   * @param consumer Recipe consumer
   * @param tool     Tool
   * @param folder   Folder for recipe
   */
  default void toolBuilding(Consumer<FinishedRecipe> consumer, IModifiable tool, String folder) {
    ToolBuildingRecipeBuilder.toolBuildingRecipe(tool)
                             .save(consumer, prefix(id(tool), folder));
  }

  /**
   * Registers recipe for tool building
   * @param consumer   Recipe consumer
   * @param tool       Tool
   * @param folder     Folder for recipe
   * @param layoutSlot StationLayoutSlot id
   */
  default void toolBuilding(Consumer<FinishedRecipe> consumer, IModifiable tool, String folder, ResourceLocation layoutSlot) {
    ToolBuildingRecipeBuilder.toolBuildingRecipe(tool)
      .layoutSlot(layoutSlot)
      .save(consumer, prefix(id(tool), folder));
  }

  /**
   * Registers recipe for tool building
   * @param consumer Recipe consumer
   * @param tool     Tool supplier
   * @param folder   Folder for recipe
   */
  default void toolBuilding(Consumer<FinishedRecipe> consumer, Supplier<? extends IModifiable> tool, String folder) {
    toolBuilding(consumer, tool.get(), folder);
  }


  /**
   * Adds cast creation and casting recipes for using a part. Does not add part builder recipes.
   * @param consumer Recipe consumer
   * @param part     Part to be crafted
   * @param cast     Part cast
   * @param cost     Part cost
   * @param partFolder   Folder for recipes
   */
  default void partCasting(Consumer<FinishedRecipe> consumer, IMaterialItem part, CastItemObject cast, int cost, String partFolder, String castFolder) {
    String name = id(part).getPath();
    // Material Casting
    String castingFolder = partFolder + "casting/";
    MaterialCastingRecipeBuilder.tableRecipe(part)
                                .setItemCost(cost)
                                .setCast(cast.getMultiUseTag(), false)
                                .save(consumer, location(castingFolder + name + "_gold_cast"));
    MaterialCastingRecipeBuilder.tableRecipe(part)
                                .setItemCost(cost)
                                .setCast(cast.getSingleUseTag(), true)
                                .save(consumer, location(castingFolder + name + "_sand_cast"));
    CompositeCastingRecipeBuilder.table(part, cost)
                                 .save(consumer, location(castingFolder + name + "_composite"));

    // Cast Casting
    MaterialIngredient ingredient = MaterialIngredient.of(part);
    castCreation(consumer, ingredient, cast, castFolder, name);
  }

  /**
   * Adds recipes for a part with no cast item
   * @param consumer             Recipe consumer
   * @param part                 Part to be crafted
   * @param cost                 Part cost
   * @param castingStatConflict  If nonnull, disallows casting if a material matching this fluid and stat type can be casted. Prevents conflicts with tool casting
   * @param partFolder   Folder for recipes
   */
  default void uncastablePart(Consumer<FinishedRecipe> consumer, IMaterialItem part, int cost, @Nullable MaterialStatsId castingStatConflict, String partFolder) {
    ResourceLocation id = id(part);
    PartRecipeBuilder.partRecipe(part)
                     .setPattern(id)
                     .setPatternItem(Ingredient.of(TinkerTags.Items.DEFAULT_PATTERNS))
                     .setCost(cost)
                     .save(consumer, location(partFolder + "builder/" + id.getPath()));
    CompositeCastingRecipeBuilder.table(part, cost)
                                 .castingStatConflict(castingStatConflict)
                                 .save(consumer, location(partFolder + "casting/" + id.getPath() + "_composite"));
  }

  /**
   * Adds cast creation, casting, and part builder recipes for a material item
   * @param consumer Recipe consumer
   * @param part     Part to be crafted
   * @param cast     Part cast
   * @param cost     Part cost
   * @param partFolder   Folder for recipes
   */
  default void partRecipes(Consumer<FinishedRecipe> consumer, IMaterialItem part, CastItemObject cast, int cost, String partFolder, String castFolder) {
    ResourceLocation id = id(part);
    // Part Builder
    PartRecipeBuilder.partRecipe(part)
                     .setPattern(id)
                     .setPatternItem(CompoundIngredient.of(Ingredient.of(TinkerTags.Items.DEFAULT_PATTERNS), Ingredient.of(cast.get())))
                     .setCost(cost)
                     .save(consumer, location(partFolder + "builder/" + id.getPath()));
    // casting
    partCasting(consumer, part, cast, cost, partFolder, castFolder);
  }

  /**
   * Adds a recipe to craft a material item
   * @param consumer Recipe consumer
   * @param part     Part to be crafted
   * @param cast     Part cast
   * @param cost     Part cost
   * @param partFolder   Folder for recipes
   */
  default void partRecipes(Consumer<FinishedRecipe> consumer, Supplier<? extends IMaterialItem> part, CastItemObject cast, int cost, String partFolder, String castFolder) {
    partRecipes(consumer, part.get(), cast, cost, partFolder, castFolder);
  }
}
