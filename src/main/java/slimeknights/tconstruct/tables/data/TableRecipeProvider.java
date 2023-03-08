package slimeknights.tconstruct.tables.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.NBTIngredient;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipeBuilder;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.recipe.PartBuilderToolRecycle;
import slimeknights.tconstruct.tables.recipe.TinkerStationDamagingRecipe;
import slimeknights.tconstruct.tools.TinkerTools;

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
  protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "tables/";
    // pattern
    ShapedRecipeBuilder.shaped(TinkerTables.pattern, 3)
      .define('s', Tags.Items.RODS_WOODEN)
      .define('p', ItemTags.PLANKS)
      .pattern("ps")
      .pattern("sp")
      .unlockedBy("has_item", has(Tags.Items.RODS_WOODEN))
      .save(consumer, prefix(TinkerTables.pattern, folder));

    // book from patterns and slime
    ShapelessRecipeBuilder.shapeless(Items.BOOK)
                          .requires(Items.PAPER)
                          .requires(Items.PAPER)
                          .requires(Items.PAPER)
                          .requires(Tags.Items.SLIMEBALLS)
                          .requires(TinkerTables.pattern)
                          .requires(TinkerTables.pattern)
                          .unlockedBy("has_item", has(TinkerTables.pattern))
                          .save(consumer, modResource(folder + "book_substitute"));

    // crafting station -> crafting table upgrade
    ShapedRecipeBuilder.shaped(TinkerTables.craftingStation)
      .define('p', TinkerTables.pattern)
      .define('w', DifferenceIngredient.of(CompoundIngredient.of(Ingredient.of(TinkerTags.Items.WORKBENCHES), Ingredient.of(TinkerTags.Items.TABLES)),
                                           Ingredient.of(TinkerTables.craftingStation.get())))
      .pattern("p")
      .pattern("w")
      .unlockedBy("has_item", has(TinkerTables.pattern))
      .save(consumer, prefix(TinkerTables.craftingStation, folder));
    // station with log texture
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(TinkerTables.craftingStation)
                         .define('p', TinkerTables.pattern)
                         .define('w', ItemTags.LOGS)
                         .pattern("p")
                         .pattern("w")
                         .unlockedBy("has_item", has(TinkerTables.pattern)))
      .setSource(ItemTags.LOGS)
      .build(consumer, wrap(TinkerTables.craftingStation, folder, "_from_logs"));

    // part builder
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(TinkerTables.partBuilder)
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
      ShapedRecipeBuilder.shaped(TinkerTables.tinkerStation)
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
    ShapedRecipeBuilder.shaped(TinkerTables.partChest)
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
    ShapedRecipeBuilder.shaped(TinkerTables.tinkersChest)
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
    ShapedRecipeBuilder.shaped(TinkerTables.castChest)
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
                                   ShapedRecipeBuilder.shaped(TinkerTables.modifierWorktable)
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
      ShapedRecipeBuilder.shaped(TinkerTables.tinkersAnvil)
                         .define('m', TinkerTags.Items.ANVIL_METAL)
                         .define('s', TinkerTags.Items.SEARED_BLOCKS)
                         .pattern("mmm")
                         .pattern(" s ")
                         .pattern("sss")
                         .unlockedBy("has_item", has(TinkerTags.Items.ANVIL_METAL)))
                                 .setSource(TinkerTags.Items.ANVIL_METAL)
                                 .setMatchAll()
                                 .build(consumer, prefix(TinkerTables.tinkersAnvil, folder));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(TinkerTables.tinkersAnvil)
                         .define('m', TinkerTags.Items.ANVIL_METAL)
                         .define('s', TinkerTags.Items.SEARED_BLOCKS)
                         .define('t', TinkerTables.tinkerStation)
                         .pattern("mmm")
                         .pattern("sts")
                         .pattern("s s")
                         .unlockedBy("has_item", has(TinkerTags.Items.ANVIL_METAL)))
                                 .setSource(TinkerTags.Items.ANVIL_METAL)
                                 .setMatchAll()
                                 .build(consumer, modResource(folder + "tinkers_forge"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(TinkerTables.scorchedAnvil)
                         .define('m', TinkerTags.Items.ANVIL_METAL)
                         .define('s', TinkerTags.Items.SCORCHED_BLOCKS)
                         .pattern("mmm")
                         .pattern(" s ")
                         .pattern("sss")
                         .unlockedBy("has_item", has(TinkerTags.Items.ANVIL_METAL)))
                                 .setSource(TinkerTags.Items.ANVIL_METAL)
                                 .setMatchAll()
                                 .build(consumer, prefix(TinkerTables.scorchedAnvil, folder));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(TinkerTables.scorchedAnvil)
                         .define('m', TinkerTags.Items.ANVIL_METAL)
                         .define('s', TinkerTags.Items.SCORCHED_BLOCKS)
                         .define('t', TinkerTables.tinkerStation)
                         .pattern("mmm")
                         .pattern("sts")
                         .pattern("s s")
                         .unlockedBy("has_item", has(TinkerTags.Items.ANVIL_METAL)))
                                 .setSource(TinkerTags.Items.ANVIL_METAL)
                                 .setMatchAll()
                                 .build(consumer, modResource(folder + "scorched_forge"));

    // recycling singleton
    consumer.accept(new PartBuilderToolRecycle.Finished(
      modResource(folder + "tool_recycling"),
      SizedIngredient.of(DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.MULTIPART_TOOL), Ingredient.of(TinkerTags.Items.UNSALVAGABLE))),
      Ingredient.of(TinkerTags.Items.PATTERNS)
    ));
    consumer.accept(new PartBuilderToolRecycle.Finished(
      modResource(folder + "dagger_recycling"),
      SizedIngredient.fromItems(2, TinkerTools.dagger),
      Ingredient.of(TinkerTags.Items.PATTERNS)
    ));

    // tool repair recipe
    SpecialRecipeBuilder.special(TinkerTables.tinkerStationRepairSerializer.get())
                       .save(consumer, modPrefix(folder + "tinker_station_repair"));
    SpecialRecipeBuilder.special(TinkerTables.tinkerStationPartSwappingSerializer.get())
                       .save(consumer, modPrefix(folder + "tinker_station_part_swapping"));
    SpecialRecipeBuilder.special(TinkerTables.craftingTableRepairSerializer.get())
                       .save(consumer, modPrefix(folder + "crafting_table_repair"));
    // tool damaging
    String damageFolder = folder + "tinker_station_damaging/";
    TinkerStationDamagingRecipe.Builder.damage(NBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.MUNDANE)), 1)
                                       .save(consumer, modResource(damageFolder + "base_one"));
    TinkerStationDamagingRecipe.Builder.damage(NBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.THICK)), 5)
                                       .save(consumer, modResource(damageFolder + "base_two"));
    TinkerStationDamagingRecipe.Builder.damage(NBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.HARMING)), 25)
                                       .save(consumer, modResource(damageFolder + "potion_one"));
    TinkerStationDamagingRecipe.Builder.damage(NBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.STRONG_HARMING)), 75)
                                       .save(consumer, modResource(damageFolder + "potion_two"));
    TinkerStationDamagingRecipe.Builder.damage(NBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.HARMING)), 150)
                                       .save(consumer, modResource(damageFolder + "splash_one"));
    TinkerStationDamagingRecipe.Builder.damage(NBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.STRONG_HARMING)), 400)
                                       .save(consumer, modResource(damageFolder + "splash_two"));
    TinkerStationDamagingRecipe.Builder.damage(NBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), Potions.HARMING)), 1000)
                                       .save(consumer, modResource(damageFolder + "lingering_one"));
    TinkerStationDamagingRecipe.Builder.damage(NBTIngredient.of(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), Potions.STRONG_HARMING)), 2500)
                                       .save(consumer, modResource(damageFolder + "lingering_two"));
  }
}
