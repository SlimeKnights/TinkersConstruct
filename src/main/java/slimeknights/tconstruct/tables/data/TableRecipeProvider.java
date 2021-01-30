package slimeknights.tconstruct.tables.data;

import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipeBuilder;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.function.Consumer;

public class TableRecipeProvider extends BaseRecipeProvider {

  public TableRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Table Recipes";
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tables/";
    // pattern
    ShapedRecipeBuilder.shapedRecipe(TinkerTables.pattern, 4)
      .key('s', Tags.Items.RODS_WOODEN)
      .key('p', ItemTags.PLANKS)
      .patternLine("ps")
      .patternLine("sp")
      .addCriterion("has_item", hasItem(Tags.Items.RODS_WOODEN))
      .build(consumer, prefix(TinkerTables.pattern, folder));

    // book from patterns and slime
    ShapelessRecipeBuilder.shapelessRecipe(Items.BOOK)
                          .addIngredient(Items.PAPER)
                          .addIngredient(Items.PAPER)
                          .addIngredient(Items.PAPER)
                          .addIngredient(Tags.Items.SLIMEBALLS)
                          .addIngredient(TinkerTables.pattern)
                          .addIngredient(TinkerTables.pattern)
                          .addCriterion("has_item", hasItem(TinkerTables.pattern))
                          .build(consumer, location(folder + "book_substitute"));

    // crafting station -> crafting table upgrade
    ShapedRecipeBuilder.shapedRecipe(TinkerTables.craftingStation)
      .key('p', TinkerTables.pattern)
      .key('w', Items.CRAFTING_TABLE)
      .patternLine("p")
      .patternLine("w")
      .addCriterion("has_item", hasItem(TinkerTables.pattern))
      .build(consumer, prefix(TinkerTables.craftingStation, folder));

    // part builder
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shapedRecipe(TinkerTables.partBuilder)
        .key('p', TinkerTables.pattern)
        .key('w', ItemTags.PLANKS)
        .patternLine("pp")
        .patternLine("ww")
        .addCriterion("has_item", hasItem(TinkerTables.pattern)))
      .setSource(ItemTags.PLANKS)
      .setMatchAll()
      .build(consumer, prefix(TinkerTables.partBuilder, folder));

    // tinker station
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shapedRecipe(TinkerTables.tinkerStation)
        .key('p', TinkerTables.pattern)
        .key('w', ItemTags.PLANKS)
        .patternLine("ppp")
        .patternLine("w w")
        .patternLine("w w")
        .addCriterion("has_item", hasItem(TinkerTables.pattern)))
      .setSource(ItemTags.PLANKS)
      .setMatchAll()
      .build(consumer, prefix(TinkerTables.tinkerStation, folder));

    // tool repair recipe
    CustomRecipeBuilder.customRecipe(TinkerTables.tinkerStationRepairSerializer.get())
                       .build(consumer, locationString(folder + "tinker_station_repair"));
    CustomRecipeBuilder.customRecipe(TinkerTables.tinkerStationPartSwappingSerializer.get())
                       .build(consumer, locationString(folder + "tinker_station_part_swapping"));
  }
}
