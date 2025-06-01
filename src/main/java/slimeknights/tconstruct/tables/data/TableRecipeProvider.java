package slimeknights.tconstruct.tables.data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipeBuilder;
import slimeknights.mantle.recipe.helper.SimpleFinishedRecipe;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.CraftingNBTWrapper;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.recipe.TinkerStationDamagingRecipeBuilder;

import java.util.function.Consumer;

public class TableRecipeProvider extends BaseRecipeProvider {

  public TableRecipeProvider(PackOutput packOutput) {
    super(packOutput);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Table Recipes";
  }

  @Override
  protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "tables/";
    // pattern
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerTables.pattern, 3)
      .define('s', Tags.Items.RODS_WOODEN)
      .define('p', ItemTags.PLANKS)
      .pattern("ps")
      .pattern("sp")
      .unlockedBy("has_item", has(Tags.Items.RODS_WOODEN))
      .save(consumer, prefix(TinkerTables.pattern, folder));

    // book from patterns and slime
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.BOOK)
                          .requires(Items.PAPER)
                          .requires(Items.PAPER)
                          .requires(Items.PAPER)
                          .requires(Tags.Items.SLIMEBALLS)
                          .requires(TinkerTables.pattern)
                          .requires(TinkerTables.pattern)
                          .unlockedBy("has_item", has(TinkerTables.pattern))
                          .save(consumer, location(folder + "book_substitute"));

    // crafting station -> crafting table upgrade
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerTables.craftingStation)
      .define('p', TinkerTables.pattern)
      .define('w', DifferenceIngredient.of(CompoundIngredient.of(Ingredient.of(TinkerTags.Items.WORKBENCHES), Ingredient.of(TinkerTags.Items.TABLES)),
                                           Ingredient.of(TinkerTables.craftingStation.get())))
      .pattern("p")
      .pattern("w")
      .unlockedBy("has_item", has(TinkerTables.pattern))
      .save(consumer, prefix(TinkerTables.craftingStation, folder));
    // station with log texture
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerTables.craftingStation)
                         .define('p', TinkerTables.pattern)
                         .define('w', ItemTags.LOGS)
                         .pattern("p")
                         .pattern("w")
                         .unlockedBy("has_item", has(TinkerTables.pattern)))
      .setSource(ItemTags.LOGS)
      .build(consumer, wrap(TinkerTables.craftingStation, folder, "_from_logs"));

    // part builder
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerTables.partBuilder)
        .define('p', TinkerTables.pattern)
        .define('w', TinkerTags.Items.PLANKLIKE)
        .pattern("pp")
        .pattern("ww")
        .unlockedBy("has_item", has(TinkerTables.pattern)))
      .setSource(TinkerTags.Items.PLANKLIKE)
      .setMatchAll()
      .build(consumer, prefix(TinkerTables.partBuilder, folder));

    // tinker station
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerTables.tinkerStation)
        .define('p', TinkerTables.pattern)
        .define('w', TinkerTags.Items.PLANKLIKE)
        .pattern("ppp")
        .pattern("w w")
        .pattern("w w")
        .unlockedBy("has_item", has(TinkerTables.pattern)))
      .setSource(TinkerTags.Items.PLANKLIKE)
      .setMatchAll()
      .build(consumer, prefix(TinkerTables.tinkerStation, folder));

    // part chest
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerTables.partChest)
                       .define('p', TinkerTables.pattern)
                       .define('w', ItemTags.PLANKS)
                       .define('s', Tags.Items.RODS_WOODEN)
                       .define('C', Tags.Items.CHESTS_WOODEN)
                       .pattern(" p ")
                       .pattern("sCs")
                       .pattern("sws")
                       .unlockedBy("has_item", has(TinkerTables.pattern))
                       .save(consumer, prefix(TinkerTables.partChest, folder));
    // modifier chest
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerTables.tinkersChest)
                       .define('p', TinkerTables.pattern)
                       .define('w', ItemTags.PLANKS)
                       .define('l', Tags.Items.GEMS_LAPIS)
                       .define('C', Tags.Items.CHESTS_WOODEN)
                       .pattern(" p " )
                       .pattern("lCl")
                       .pattern("lwl")
                       .unlockedBy("has_item", has(TinkerTables.pattern))
                       .save(consumer, prefix(TinkerTables.tinkersChest, folder));
    // cast chest
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerTables.castChest)
                       .define('c', TinkerTags.Items.GOLD_CASTS)
                       .define('b', TinkerSmeltery.searedBrick)
                       .define('B', TinkerSmeltery.searedBricks)
                       .define('C', Tags.Items.CHESTS_WOODEN)
                       .pattern(" c ")
                       .pattern("bCb")
                       .pattern("bBb")
                       .unlockedBy("has_item", has(TinkerTags.Items.GOLD_CASTS))
                       .save(consumer, prefix(TinkerTables.castChest, folder));

    // modifier worktable
    ShapedRetexturedRecipeBuilder.fromShaped(
                                   ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerTables.modifierWorktable)
                                                      .define('r', TinkerTags.Items.WORKSTATION_ROCK)
                                                      .define('s', TinkerTags.Items.SEARED_BLOCKS)
                                                      .pattern("sss")
                                                      .pattern("r r")
                                                      .pattern("r r")
                                                      .unlockedBy("has_item", has(TinkerTags.Items.SEARED_BLOCKS)))
                                 .setSource(TinkerTags.Items.WORKSTATION_ROCK)
                                 .setMatchAll()
                                 .build(consumer, prefix(TinkerTables.modifierWorktable, folder));

    // tinker anvil
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerTables.tinkersAnvil)
                         .define('m', TinkerTags.Items.ANVIL_METAL)
                         .define('s', TinkerTags.Items.SEARED_BLOCKS)
                         .pattern("mmm")
                         .pattern(" s ")
                         .pattern("sss")
                         .unlockedBy("has_item", has(TinkerTags.Items.ANVIL_METAL)))
                                 .setSource(TinkerTags.Items.ANVIL_METAL)
                                 .setMatchAll()
                                 .build(consumer, prefix(TinkerTables.tinkersAnvil, folder));
    Consumer<FinishedRecipe> toolForge;
    {
      CompoundTag nbt = new CompoundTag();
      CompoundTag display = new CompoundTag();
      display.putString("Name", Serializer.toJson(Component.translatable("block.tconstruct.tool_forge")));
      nbt.put("display", display);
      toolForge = CraftingNBTWrapper.wrap(consumer, nbt);
    }
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerTables.tinkersAnvil)
                         .define('m', TinkerTags.Items.ANVIL_METAL)
                         .define('s', TinkerTags.Items.SEARED_BLOCKS)
                         .define('t', TinkerTables.tinkerStation)
                         .pattern("mmm")
                         .pattern("sts")
                         .pattern("s s")
                         .unlockedBy("has_item", has(TinkerTags.Items.ANVIL_METAL)))
      .setSource(TinkerTags.Items.ANVIL_METAL)
      .setMatchAll()
      .build(toolForge, location(folder + "tinkers_forge"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerTables.scorchedAnvil)
                         .define('m', TinkerTags.Items.ANVIL_METAL)
                         .define('s', TinkerTags.Items.SCORCHED_BLOCKS)
                         .pattern("mmm")
                         .pattern(" s ")
                         .pattern("sss")
                         .unlockedBy("has_item", has(TinkerTags.Items.ANVIL_METAL)))
      .setSource(TinkerTags.Items.ANVIL_METAL)
      .setMatchAll()
      .build(toolForge, prefix(TinkerTables.scorchedAnvil, folder));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerTables.scorchedAnvil)
                         .define('m', TinkerTags.Items.ANVIL_METAL)
                         .define('s', TinkerTags.Items.SCORCHED_BLOCKS)
                         .define('t', TinkerTables.tinkerStation)
                         .pattern("mmm")
                         .pattern("sts")
                         .pattern("s s")
                         .unlockedBy("has_item", has(TinkerTags.Items.ANVIL_METAL)))
                                 .setSource(TinkerTags.Items.ANVIL_METAL)
                                 .setMatchAll()
                                 .build(consumer, location(folder + "scorched_forge"));

    // tool repair recipe
    consumer.accept(new SimpleFinishedRecipe(location(folder + "tinker_station_repair"), TinkerTables.tinkerStationRepairSerializer.get()));
    consumer.accept(new SimpleFinishedRecipe(location(folder + "tinker_station_part_swapping"), TinkerTables.tinkerStationPartSwappingSerializer.get()));
    consumer.accept(new SimpleFinishedRecipe(location(folder + "crafting_table_repair"), TinkerTables.craftingTableRepairSerializer.get()));

    // tool damaging
    String damageFolder = folder + "tinker_station_damaging/";
    TinkerStationDamagingRecipeBuilder.damage(Ingredient.of(TinkerFluids.magmaBottle), 20)
      .save(consumer, location(damageFolder + "magma_bottle"));
    TinkerStationDamagingRecipeBuilder.damage(Ingredient.of(TinkerFluids.magma), 100)
      .save(consumer, location(damageFolder + "magma_bucket"));
    TinkerStationDamagingRecipeBuilder.damage(Ingredient.of(TinkerFluids.venomBottle), 200)
      .save(consumer, location(damageFolder + "venom_bottle"));
    TinkerStationDamagingRecipeBuilder.damage(Ingredient.of(TinkerFluids.venom), 1000)
      .save(consumer, location(damageFolder + "venom_bucket"));
    TinkerStationDamagingRecipeBuilder.damage(Ingredient.of(Items.LAVA_BUCKET), 500)
      .save(consumer, location(damageFolder + "lava_bucket"));
    TinkerStationDamagingRecipeBuilder.damage(Ingredient.of(TinkerFluids.blazingBlood), 2500)
      .save(consumer, location(damageFolder + "blazing_bucket"));
  }
}
