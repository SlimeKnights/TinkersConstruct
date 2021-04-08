package slimeknights.tconstruct.tables.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.recipe.ComplexRecipeJsonFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.ItemTags;
import net.minecraftforge.common.Tags;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipeBuilder;
import slimeknights.mantle.recipe.ingredient.IngredientWithout;
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
  protected void generate(Consumer<RecipeJsonProvider> consumer) {
    String folder = "tables/";
    // pattern
    ShapedRecipeJsonFactory.create(TinkerTables.pattern, 3)
      .input('s', Tags.Items.RODS_WOODEN)
      .input('p', ItemTags.PLANKS)
      .pattern("ps")
      .pattern("sp")
      .criterion("has_item", conditionsFromTag(Tags.Items.RODS_WOODEN))
      .offerTo(consumer, prefix(TinkerTables.pattern, folder));

    // book from patterns and slime
    ShapelessRecipeJsonFactory.create(Items.BOOK)
                          .input(Items.PAPER)
                          .input(Items.PAPER)
                          .input(Items.PAPER)
                          .input(Tags.Items.SLIMEBALLS)
                          .input(TinkerTables.pattern)
                          .input(TinkerTables.pattern)
                          .criterion("has_item", conditionsFromItem(TinkerTables.pattern))
                          .offerTo(consumer, location(folder + "book_substitute"));

    // crafting station -> crafting table upgrade
    ShapedRecipeJsonFactory.create(TinkerTables.craftingStation)
      .input('p', TinkerTables.pattern)
      .input('w', new IngredientWithout(new CompoundIngredient(Ingredient.fromTag(TinkerTags.Items.WORKBENCHES), Ingredient.fromTag(TinkerTags.Items.TABLES)),
                                      Ingredient.ofItems(TinkerTables.craftingStation.get())))
      .pattern("p")
      .pattern("w")
      .criterion("has_item", conditionsFromItem(TinkerTables.pattern))
      .offerTo(consumer, prefix(TinkerTables.craftingStation, folder));
    // station with log texture
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeJsonFactory.create(TinkerTables.craftingStation)
                         .input('p', TinkerTables.pattern)
                         .input('w', ItemTags.LOGS)
                         .pattern("p")
                         .pattern("w")
                         .criterion("has_item", conditionsFromItem(TinkerTables.pattern)))
      .setSource(ItemTags.LOGS)
      .build(consumer, wrap(TinkerTables.craftingStation, folder, "_from_logs"));

    // part builder
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeJsonFactory.create(TinkerTables.partBuilder)
        .input('p', TinkerTables.pattern)
        .input('w', ItemTags.PLANKS)
        .pattern("pp")
        .pattern("ww")
        .criterion("has_item", conditionsFromItem(TinkerTables.pattern)))
      .setSource(ItemTags.PLANKS)
      .setMatchAll()
      .build(consumer, prefix(TinkerTables.partBuilder, folder));

    // tinker station
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeJsonFactory.create(TinkerTables.tinkerStation)
        .input('p', TinkerTables.pattern)
        .input('w', ItemTags.PLANKS)
        .pattern("ppp")
        .pattern("w w")
        .pattern("w w")
        .criterion("has_item", conditionsFromItem(TinkerTables.pattern)))
      .setSource(ItemTags.PLANKS)
      .setMatchAll()
      .build(consumer, prefix(TinkerTables.tinkerStation, folder));

    // part chest
    ShapedRecipeJsonFactory.create(TinkerTables.partChest)
                       .input('p', TinkerTables.pattern)
                       .input('w', ItemTags.PLANKS)
                       .input('s', Tags.Items.RODS_WOODEN)
                       .input('C', Tags.Items.CHESTS_WOODEN)
                       .pattern(" p ")
                       .pattern("sCs")
                       .pattern("sws")
                       .criterion("has_item", conditionsFromItem(TinkerTables.pattern))
                       .offerTo(consumer, prefix(TinkerTables.partChest, folder));
    // modifier chest
    ShapedRecipeJsonFactory.create(TinkerTables.modifierChest)
                       .input('p', TinkerTables.pattern)
                       .input('w', ItemTags.PLANKS)
                       .input('l', Tags.Items.GEMS_LAPIS)
                       .input('C', Tags.Items.CHESTS_WOODEN)
                       .pattern(" p " )
                       .pattern("lCl")
                       .pattern("lwl")
                       .criterion("has_item", conditionsFromItem(TinkerTables.pattern))
                       .offerTo(consumer, prefix(TinkerTables.modifierChest, folder));
    // cast chest
    ShapedRecipeJsonFactory.create(TinkerTables.castChest)
                       .input('c', TinkerSmeltery.blankCast)
                       .input('b', TinkerSmeltery.searedBrick)
                       .input('B', TinkerSmeltery.searedBricks)
                       .input('C', Tags.Items.CHESTS_WOODEN)
                       .pattern(" c ")
                       .pattern("bCb")
                       .pattern("bBb")
                       .criterion("has_item", conditionsFromItem(TinkerSmeltery.blankCast))
                       .offerTo(consumer, prefix(TinkerTables.castChest, folder));

    // tinker anvil
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeJsonFactory.create(TinkerTables.tinkersAnvil)
                         .input('m', TinkerTags.Items.ANVIL_METAL)
                         .input('s', TinkerTags.Items.SEARED_BLOCKS)
                         .pattern("mmm")
                         .pattern(" s ")
                         .pattern("sss")
                         .criterion("has_item", conditionsFromTag(TinkerTags.Items.ANVIL_METAL)))
                                 .setSource(TinkerTags.Items.ANVIL_METAL)
                                 .build(consumer, prefix(TinkerTables.tinkersAnvil, folder));

    // tool repair recipe
    ComplexRecipeJsonFactory.create(TinkerTables.tinkerStationRepairSerializer.get())
                       .offerTo(consumer, locationString(folder + "tinker_station_repair"));
    ComplexRecipeJsonFactory.create(TinkerTables.tinkerStationPartSwappingSerializer.get())
                       .offerTo(consumer, locationString(folder + "tinker_station_part_swapping"));
  }
}
