package slimeknights.tconstruct.shared.data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraftforge.common.Tags;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.mantle.recipe.data.ICommonRecipeHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Locale;
import java.util.function.Consumer;

public class CommonRecipeProvider extends BaseRecipeProvider implements ICommonRecipeHelper {
  public CommonRecipeProvider(PackOutput output) {
    super(output);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Common Recipes";
  }

  @Override
  protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
    this.addCommonRecipes(consumer);
    this.addMaterialRecipes(consumer);
  }

  private void addCommonRecipes(Consumer<FinishedRecipe> consumer) {
    // firewood and lavawood
    String folder = "common/firewood/";
    slabStairsCrafting(consumer, TinkerMaterials.blazewood, folder, false);
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerMaterials.blazewood.getFence(), 6)
                       .pattern("WWW").pattern("WWW")
                       .define('W', TinkerMaterials.blazewood)
                       .unlockedBy("has_planks", has(TinkerMaterials.blazewood))
                       .save(consumer, location(folder + "blazewood_fence"));

    // nahuatl
    slabStairsCrafting(consumer, TinkerMaterials.nahuatl, folder, false);
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerMaterials.nahuatl.getFence(), 6)
                       .pattern("WWW").pattern("WWW")
                       .define('W', TinkerMaterials.nahuatl)
                       .unlockedBy("has_planks", has(TinkerMaterials.nahuatl))
                       .save(consumer, location(folder + "nahuatl_fence"));

    // gold
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerCommons.goldBars, 16)
                       .define('#', Tags.Items.INGOTS_GOLD)
                       .pattern("###")
                       .pattern("###")
                       .unlockedBy("has_ingot", has(Tags.Items.INGOTS_GOLD))
                       .save(consumer, location("common/gold_bars"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerCommons.goldPlatform, 4)
                       .define('#', Tags.Items.INGOTS_GOLD)
                       .define('.', Tags.Items.NUGGETS_GOLD)
                       .pattern("#.#")
                       .pattern(". .")
                       .pattern("#.#")
                       .unlockedBy("has_gold", has(Tags.Items.INGOTS_GOLD))
                       .save(consumer, location("common/gold_platform"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerCommons.ironPlatform, 4)
                       .define('#', Tags.Items.INGOTS_IRON)
                       .define('.', Tags.Items.NUGGETS_IRON)
                       .pattern("#.#")
                       .pattern(". .")
                       .pattern("#.#")
                       .unlockedBy("has_bars", has(Tags.Items.INGOTS_IRON))
                       .save(consumer, location("common/iron_platform"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerCommons.copperPlatform.get(WeatherState.UNAFFECTED), 4)
                       .define('#', Tags.Items.INGOTS_COPPER)
                       .define('.', TinkerTags.Items.NUGGETS_COPPER)
                       .pattern("#.#")
                       .pattern(". .")
                       .pattern("#.#")
                       .unlockedBy("has_bars", has(Tags.Items.INGOTS_COPPER))
                       .save(consumer, location("common/copper_platform"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerCommons.cobaltPlatform, 4)
                       .define('#', TinkerMaterials.cobalt.getIngotTag())
                       .define('.', TinkerMaterials.cobalt.getNuggetTag())
                       .pattern("#.#")
                       .pattern(". .")
                       .pattern("#.#")
                       .unlockedBy("has_bars", has(TinkerMaterials.cobalt.getIngotTag()))
                       .save(consumer, location("common/cobalt_platform"));
    TinkerCommons.waxedCopperPlatform.forEach((age, block) -> {
      Block unwaxed = TinkerCommons.copperPlatform.get(age);
      ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, block)
                            .requires(unwaxed)
                            .requires(Items.HONEYCOMB)
                            .group("tconstruct:wax_copper_platform")
                            .unlockedBy("has_block", has(unwaxed))
                            .save(consumer, location("common/copper_platform_waxing_" + age.toString().toLowerCase(Locale.ROOT)));
    });



    // book
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TinkerCommons.materialsAndYou)
                          .requires(Items.BOOK)
                          .requires(TinkerTables.pattern)
                          .unlockedBy("has_item", has(TinkerTables.pattern))
                          .save(consumer, prefix(TinkerCommons.materialsAndYou, "common/"));
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TinkerCommons.tinkersGadgetry)
                          .requires(Items.BOOK)
                          .requires(SlimeType.SKY.getSlimeballTag())
                          .unlockedBy("has_item", has(SlimeType.SKY.getSlimeballTag()))
                          .save(consumer, prefix(TinkerCommons.tinkersGadgetry, "common/"));
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TinkerCommons.punySmelting)
                          .requires(Items.BOOK)
                          .requires(TinkerSmeltery.grout)
                          .unlockedBy("has_item", has(TinkerSmeltery.grout))
                          .save(consumer, prefix(TinkerCommons.punySmelting, "common/"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.mightySmelting)
                            .setFluidAndTime(TinkerFluids.searedStone, false, FluidValues.BRICK)
                            .setCast(Items.BOOK, true)
                            .save(consumer, prefix(TinkerCommons.mightySmelting, "common/"));
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TinkerCommons.fantasticFoundry)
                          .requires(Items.BOOK)
                          .requires(TinkerSmeltery.netherGrout)
                          .unlockedBy("has_item", has(TinkerSmeltery.netherGrout))
                          .save(consumer, prefix(TinkerCommons.fantasticFoundry, "common/"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.encyclopedia)
                            .setFluidAndTime(TinkerFluids.moltenGold, true, FluidValues.INGOT)
                            .setCast(Items.BOOK, true)
                            .save(consumer, prefix(TinkerCommons.encyclopedia, "common/"));

    // glass
    folder = "common/glass/";
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerCommons.clearGlassPane, 16)
                       .define('#', TinkerCommons.clearGlass)
                       .pattern("###")
                       .pattern("###")
                       .unlockedBy("has_block", has(TinkerCommons.clearGlass))
                       .save(consumer, prefix(TinkerCommons.clearGlassPane, folder));
    for (GlassColor color : GlassColor.values()) {
      Block block = TinkerCommons.clearStainedGlass.get(color);
      ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block, 8)
                         .define('#', TinkerCommons.clearGlass)
                         .define('X', color.getDye().getTag())
                         .pattern("###")
                         .pattern("#X#")
                         .pattern("###")
                         .group(prefix("stained_clear_glass"))
                         .unlockedBy("has_clear_glass", has(TinkerCommons.clearGlass))
                         .save(consumer, prefix(id(block), folder));
      Block pane = TinkerCommons.clearStainedGlassPane.get(color);
      ResourceLocation paneId = id(pane);
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, pane, 16)
                         .define('#', block)
                         .pattern("###")
                         .pattern("###")
                         .group(prefix("stained_clear_glass_pane"))
                         .unlockedBy("has_block", has(block))
                         .save(consumer, prefix(paneId, folder));
      ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, pane, 8)
                         .define('#', TinkerCommons.clearGlassPane)
                         .define('X', color.getDye().getTag())
                         .pattern("###")
                         .pattern("#X#")
                         .pattern("###")
                         .group(prefix("stained_clear_glass_pane"))
                         .unlockedBy("has_clear_glass", has(TinkerCommons.clearGlassPane))
                         .save(consumer, wrap(paneId, folder, "_from_panes"));
    }
    // fix vanilla recipes not using tinkers glass
    String glassVanillaFolder = folder + "vanilla/";
    Consumer<FinishedRecipe> vanillaGlassConsumer = withCondition(consumer, ConfigEnabledCondition.GLASS_RECIPE_FIX);
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Blocks.BEACON)
                       .define('S', Items.NETHER_STAR)
                       .define('G', Tags.Items.GLASS_COLORLESS)
                       .define('O', Blocks.OBSIDIAN)
                       .pattern("GGG")
                       .pattern("GSG")
                       .pattern("OOO")
                       .unlockedBy("has_nether_star", has(Items.NETHER_STAR))
                       .save(vanillaGlassConsumer, prefix(id(Blocks.BEACON), glassVanillaFolder));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, Blocks.DAYLIGHT_DETECTOR)
                       .define('Q', Items.QUARTZ)
                       .define('G', Tags.Items.GLASS_COLORLESS)
                       .define('W', ItemTags.WOODEN_SLABS)
                       .pattern("GGG")
                       .pattern("QQQ")
                       .pattern("WWW")
                       .unlockedBy("has_quartz", has(Items.QUARTZ))
                       .save(vanillaGlassConsumer, prefix(id(Blocks.DAYLIGHT_DETECTOR), glassVanillaFolder));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.END_CRYSTAL)
                       .define('T', Items.GHAST_TEAR)
                       .define('E', Items.ENDER_EYE)
                       .define('G', Tags.Items.GLASS_COLORLESS)
                       .pattern("GGG")
                       .pattern("GEG")
                       .pattern("GTG")
                       .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
                       .save(vanillaGlassConsumer, prefix(id(Items.END_CRYSTAL), glassVanillaFolder));
    ShapedRecipeBuilder.shaped(RecipeCategory.BREWING, Items.GLASS_BOTTLE, 3)
                       .define('#', Tags.Items.GLASS_COLORLESS)
                       .pattern("# #")
                       .pattern(" # ")
                       .unlockedBy("has_glass", has(Tags.Items.GLASS_COLORLESS))
                       .save(vanillaGlassConsumer, prefix(id(Items.GLASS_BOTTLE), glassVanillaFolder));


    // vanilla recipes
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.FLINT)
                          .requires(Blocks.GRAVEL)
                          .requires(Blocks.GRAVEL)
                          .requires(Blocks.GRAVEL)
                          .unlockedBy("has_item", has(Blocks.GRAVEL))
                          .save(
                            ConsumerWrapperBuilder.wrap()
                                                  .addCondition(ConfigEnabledCondition.GRAVEL_TO_FLINT)
                                                  .build(consumer),
                            location("common/flint"));

    // allow crafting the blast furnace in the nether
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.BLAST_FURNACE)
                       .define('#', Blocks.SMOOTH_BASALT)
                       .define('X', Blocks.FURNACE)
                       .define('I', Items.IRON_INGOT)
                       .pattern("III")
                       .pattern("IXI")
                       .pattern("###")
                       .unlockedBy("has_smooth_stone", has(Blocks.SMOOTH_BASALT))
                       .save(consumer, location("common/basalt_blast_furnace"));

    // cheese
    ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, TinkerCommons.cheeseBlock)
                       .define('#', TinkerCommons.cheeseIngot)
                       .pattern("##").pattern("##")
                       .unlockedBy("has_cheese", has(TinkerCommons.cheeseIngot))
                       .save(consumer, location("common/cheese_block_from_ingot"));
    ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, TinkerCommons.cheeseIngot, 4)
                          .requires(TinkerCommons.cheeseBlock)
                          .unlockedBy("has_cheese", has(TinkerCommons.cheeseBlock))
                          .save(consumer, location("common/cheese_ingot_from_block"));
  }

  private void addMaterialRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "common/materials/";

    // ores
    metalCrafting(consumer, TinkerMaterials.cobalt, folder);
    // tier 3
    metalCrafting(consumer, TinkerMaterials.slimesteel, folder);
    metalCrafting(consumer, TinkerMaterials.amethystBronze, folder);
    metalCrafting(consumer, TinkerMaterials.roseGold, folder);
    metalCrafting(consumer, TinkerMaterials.pigIron, folder);
    // tier 4
    metalCrafting(consumer, TinkerMaterials.queensSlime, folder);
    metalCrafting(consumer, TinkerMaterials.manyullyn, folder);
    metalCrafting(consumer, TinkerMaterials.hepatizon, folder);
    //registerMineralRecipes(consumer, TinkerMaterials.soulsteel,   folder);
    packingRecipe(consumer, RecipeCategory.MISC, "ingot", Items.COPPER_INGOT,    "nugget", TinkerMaterials.copperNugget,    TinkerTags.Items.NUGGETS_COPPER,    folder);
    packingRecipe(consumer, RecipeCategory.MISC, "ingot", Items.NETHERITE_INGOT, "nugget", TinkerMaterials.netheriteNugget, TinkerTags.Items.NUGGETS_NETHERITE, folder);
    // tier 5
    //registerMineralRecipes(consumer, TinkerMaterials.knightslime, folder);

    // smelt ore into ingots, must use a blast furnace for nether ores
    Item cobaltIngot = TinkerMaterials.cobalt.getIngot();
    SimpleCookingRecipeBuilder.blasting(Ingredient.of(TinkerWorld.rawCobalt, TinkerWorld.cobaltOre), RecipeCategory.MISC, cobaltIngot, 1.5f, 200)
                              .unlockedBy("has_item", has(TinkerWorld.rawCobalt))
                              .save(consumer, wrap(id(cobaltIngot), folder, "_smelting"));
    // pack raw cobalt
    packingRecipe(consumer, RecipeCategory.MISC, "raw_block", TinkerWorld.rawCobaltBlock, "raw", TinkerWorld.rawCobalt, TinkerTags.Items.RAW_COBALT, folder);
  }
}
