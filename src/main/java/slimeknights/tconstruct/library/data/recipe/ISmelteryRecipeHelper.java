package slimeknights.tconstruct.library.data.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.function.Consumer;

import static slimeknights.mantle.Mantle.COMMON;
import static slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe.getTemperature;

/**
 * Recipe helper for methods related to melting and casting
 */
public interface ISmelteryRecipeHelper extends ICastCreationHelper {
  /* Builders for casting and melting from tags */

  /** Creates a smeltery builder for a standard fluid */
  default SmelteryRecipeBuilder fluid(Consumer<FinishedRecipe> consumer, String name, FluidObject<?> fluid) {
    return SmelteryRecipeBuilder.fluid(consumer, location(name), fluid);
  }

  /** Creates a smeltery builder for a molten fluid */
  default SmelteryRecipeBuilder molten(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid) {
    return fluid(consumer, fluid.getId().getPath().substring("molten_".length()), fluid);
  }


  /* Melting */

  /**
   * Creates a melting recipe with a tag input
   * @param consumer    Recipe consumer
   * @param fluid       Fluid to melt into
   * @param temperature Minimum melting temperature
   * @param tagName     Input tag
   * @param factor      Melting factor
   * @param recipePath  Recipe output name
   * @param isOptional  If true, recipe is optional
   */
  default void tagMelting(Consumer<FinishedRecipe> consumer, FluidOutput fluid, int temperature, String tagName, float factor, String recipePath, boolean isOptional) {
    Consumer<FinishedRecipe> wrapped = isOptional ? withCondition(consumer, tagCondition(tagName)) : consumer;
    MeltingRecipeBuilder.melting(Ingredient.of(getItemTag(COMMON, tagName)), fluid, temperature, factor)
                        .save(wrapped, location(recipePath));
  }

  /**
   * Common usage of {@link #tagMelting(Consumer, FluidOutput, int, String, float, String, boolean)}
   * @param consumer    Recipe consumer
   * @param fluid       Fluid to melt into
   * @param amount      Fluid output amount
   * @param tagName     Input tag
   * @param factor      Melting factor
   * @param recipePath  Recipe output name
   * @param isOptional  If true, recipe is optional
   */
  default void tagMelting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, int amount, String tagName, float factor, String recipePath, boolean isOptional) {
    tagMelting(consumer, fluid.result(amount), getTemperature(fluid), tagName, factor, recipePath, isOptional);
  }


  /* Casting */

  /**
   * Adds a recipe for casting using a cast
   * @param consumer  Recipe consumer
   * @param fluid     Recipe fluid
   * @param amount    Fluid amount
   * @param cast      Cast used
   * @param output    Recipe output
   * @param location  Recipe base
   */
  default void castingWithCast(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, int amount, CastItemObject cast, ItemOutput output, String location) {
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluidAndTime(fluid, amount)
                            .setCast(cast.getMultiUseTag(), false)
                            .save(consumer, location(location + "_gold_cast"));
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluidAndTime(fluid, amount)
                            .setCast(cast.getSingleUseTag(), true)
                            .save(consumer, location(location + "_sand_cast"));
  }

  /**
   * Adds a recipe for casting using a cast
   * @param consumer  Recipe consumer
   * @param fluid     Recipe fluid
   * @param amount    Fluid amount
   * @param cast      Cast used
   * @param output    Recipe output
   * @param location  Recipe base
   */
  default void castingWithCast(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, int amount, CastItemObject cast, ItemLike output, String location) {
    castingWithCast(consumer, fluid, amount, cast, ItemOutput.fromItem(output), location);
  }

  /**
   * Adds a recipe for casting an item from a tag
   * @param consumer     Recipe consumer
   * @param fluid        Input fluid
   * @param amount       Recipe amount
   * @param cast         Cast for recipe
   * @param tagName      Tag for output
   * @param recipeName   Name of the recipe for output
   * @param optional     If true, conditions the recipe on the tag
   */
  default void tagCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, int amount, CastItemObject cast, String tagName, String recipeName, boolean optional) {
    if (optional) {
      consumer = withCondition(consumer, tagCondition(tagName));
    }
    castingWithCast(consumer, fluid, amount, cast, ItemOutput.fromTag(getItemTag(COMMON, tagName)), recipeName);
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param amount    Recipe amount
   * @param ingot     Ingot output
   * @param location  Recipe base
   */
  default void ingotCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, int amount, ItemLike ingot, String location) {
    castingWithCast(consumer, fluid, amount, TinkerSmeltery.ingotCast, ingot, location);
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param ingot     Ingot output
   * @param location  Recipe base
   */
  default void ingotCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, ItemLike ingot, String location) {
    ingotCasting(consumer, fluid, FluidValues.INGOT, ingot, location);
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param gem       Gem output
   * @param location  Recipe base
   */
  default void gemCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, ItemLike gem, String location) {
    castingWithCast(consumer, fluid, FluidValues.GEM, TinkerSmeltery.gemCast, gem, location);
  }

  /**
   * Adds a casting recipe using a nugget cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param nugget    Nugget output
   * @param location  Recipe base
   */
  default void nuggetCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, ItemLike nugget, String location) {
    castingWithCast(consumer, fluid, FluidValues.NUGGET, TinkerSmeltery.nuggetCast, nugget, location);
  }
}
