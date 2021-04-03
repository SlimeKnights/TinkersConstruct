package slimeknights.tconstruct.tables.data;

import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipeBuilder;
import slimeknights.tconstruct.common.recipe.IngredientWithout;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
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
    ShapedRecipeBuilder.shapedRecipe(TinkerTables.pattern, 3)
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
      .key('w', new IngredientWithout(new CompoundIngredient(Ingredient.fromTag(TinkerTags.Items.WORKBENCHES), Ingredient.fromTag(TinkerTags.Items.TABLES)),
                                      Ingredient.fromItems(TinkerTables.craftingStation.get())))
      .patternLine("p")
      .patternLine("w")
      .addCriterion("has_item", hasItem(TinkerTables.pattern))
      .build(consumer, prefix(TinkerTables.craftingStation, folder));
    // station with log texture
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shapedRecipe(TinkerTables.craftingStation)
                         .key('p', TinkerTables.pattern)
                         .key('w', ItemTags.LOGS)
                         .patternLine("p")
                         .patternLine("w")
                         .addCriterion("has_item", hasItem(TinkerTables.pattern)))
      .setSource(ItemTags.LOGS)
      .build(consumer, wrap(TinkerTables.craftingStation, folder, "_from_logs"));

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

    // part chest
    ShapedRecipeBuilder.shapedRecipe(TinkerTables.partChest)
                       .key('p', TinkerTables.pattern)
                       .key('w', ItemTags.PLANKS)
                       .key('s', Tags.Items.RODS_WOODEN)
                       .key('C', Tags.Items.CHESTS_WOODEN)
                       .patternLine(" p ")
                       .patternLine("sCs")
                       .patternLine("sws")
                       .addCriterion("has_item", hasItem(TinkerTables.pattern))
                       .build(consumer, prefix(TinkerTables.partChest, folder));
    // modifier chest
    ShapedRecipeBuilder.shapedRecipe(TinkerTables.modifierChest)
                       .key('p', TinkerTables.pattern)
                       .key('w', ItemTags.PLANKS)
                       .key('l', Tags.Items.GEMS_LAPIS)
                       .key('C', Tags.Items.CHESTS_WOODEN)
                       .patternLine(" p " )
                       .patternLine("lCl")
                       .patternLine("lwl")
                       .addCriterion("has_item", hasItem(TinkerTables.pattern))
                       .build(consumer, prefix(TinkerTables.modifierChest, folder));
    // cast chest
    ShapedRecipeBuilder.shapedRecipe(TinkerTables.castChest)
                       .key('c', TinkerSmeltery.blankCast)
                       .key('b', TinkerSmeltery.searedBrick)
                       .key('B', TinkerSmeltery.searedBricks)
                       .key('C', Tags.Items.CHESTS_WOODEN)
                       .patternLine(" c ")
                       .patternLine("bCb")
                       .patternLine("bBb")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.blankCast))
                       .build(consumer, prefix(TinkerTables.castChest, folder));

    // tinker anvil
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shapedRecipe(TinkerTables.tinkersAnvil)
                         .key('m', TinkerTags.Items.ANVIL_METAL)
                         .key('s', TinkerTags.Items.SEARED_BLOCKS)
                         .patternLine("mmm")
                         .patternLine(" s ")
                         .patternLine("sss")
                         .addCriterion("has_item", hasItem(TinkerTags.Items.ANVIL_METAL)))
                                 .setSource(TinkerTags.Items.ANVIL_METAL)
                                 .build(consumer, prefix(TinkerTables.tinkersAnvil, folder));

    // tool repair recipe
    CustomRecipeBuilder.customRecipe(TinkerTables.tinkerStationRepairSerializer.get())
                       .build(consumer, locationString(folder + "tinker_station_repair"));
    CustomRecipeBuilder.customRecipe(TinkerTables.tinkerStationPartSwappingSerializer.get())
                       .build(consumer, locationString(folder + "tinker_station_part_swapping"));
  }
}
