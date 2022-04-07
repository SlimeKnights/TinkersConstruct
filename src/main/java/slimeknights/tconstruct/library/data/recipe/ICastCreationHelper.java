package slimeknights.tconstruct.library.data.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.function.Consumer;

/**
 * Shared methods between {@link ISmelteryRecipeHelper} and {@link IToolRecipeHelper}
 */
public interface ICastCreationHelper extends IRecipeHelper {
  /* Cast creation */

  /**
   * Adds recipe to create a cast
   * @param consumer  Recipe consumer
   * @param input     Item consumed to create cast
   * @param cast      Produced cast
   * @param folder    Output folder
   */
  default void castCreation(Consumer<FinishedRecipe> consumer, TagKey<Item> input, CastItemObject cast, String folder) {
    castCreation(consumer, Ingredient.of(input), cast, folder, input.location().getPath());
  }

  /**
   * Adds recipe to create a cast
   * @param consumer  Recipe consumer
   * @param input     Item consumed to create cast
   * @param cast      Produced cast
   * @param folder    Output folder
   * @param name      Cast name
   */
  default void castCreation(Consumer<FinishedRecipe> consumer, Ingredient input, CastItemObject cast, String folder, String name) {
    ItemCastingRecipeBuilder.tableRecipe(cast)
                            .setFluidAndTime(TinkerFluids.moltenGold, true, FluidValues.INGOT)
                            .setCast(input, true)
                            .setSwitchSlots()
                            .save(consumer, modResource(folder + "gold_casts/" + name));
    MoldingRecipeBuilder.moldingTable(cast.getSand())
                        .setMaterial(TinkerSmeltery.blankSandCast)
                        .setPattern(input, false)
                        .save(consumer, modResource(folder + "sand_casts/" + name));
    MoldingRecipeBuilder.moldingTable(cast.getRedSand())
                        .setMaterial(TinkerSmeltery.blankRedSandCast)
                        .setPattern(input, false)
                        .save(consumer, modResource(folder + "red_sand_casts/" + name));
  }
}
