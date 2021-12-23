package slimeknights.tconstruct.shared.data;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.data.recipe.ICommonRecipeHelper;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class CommonRecipeProvider extends BaseRecipeProvider implements ICommonRecipeHelper {
  public CommonRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Common Recipes";
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.addCommonRecipes(consumer);
    this.addMaterialRecipes(consumer);
  }

  private void addCommonRecipes(Consumer<IFinishedRecipe> consumer) {
    // firewood and lavawood
    String folder = "common/firewood/";
    slabStairsCrafting(consumer, TinkerCommons.blazewood, folder, false);
    slabStairsCrafting(consumer, TinkerCommons.lavawood, folder, false);

    // nahuatl
    slabStairsCrafting(consumer, TinkerMaterials.nahuatl, folder, false);
    ShapedRecipeBuilder.shapedRecipe(TinkerMaterials.nahuatl.getFence(), 6)
                       .patternLine("WWW").patternLine("WWW")
                       .key('W', TinkerMaterials.nahuatl)
                       .addCriterion("has_planks", hasItem(TinkerMaterials.nahuatl))
                       .build(consumer, modResource(folder + "nahuatl_fence"));

    // mud bricks
    slabStairsCrafting(consumer, TinkerCommons.mudBricks, "common/", false);

    // book
    ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.materialsAndYou)
                          .addIngredient(Items.BOOK)
                          .addIngredient(TinkerTables.pattern)
                          .addCriterion("has_item", hasItem(TinkerTables.pattern))
                          .build(consumer, prefix(TinkerCommons.materialsAndYou, "common/"));
    ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.tinkersGadgetry)
                          .addIngredient(Items.BOOK)
                          .addIngredient(SlimeType.SKY.getSlimeballTag())
                          .addCriterion("has_item", hasItem(SlimeType.SKY.getSlimeballTag()))
                          .build(consumer, prefix(TinkerCommons.tinkersGadgetry, "common/"));
    ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.punySmelting)
                          .addIngredient(Items.BOOK)
                          .addIngredient(TinkerSmeltery.grout)
                          .addCriterion("has_item", hasItem(TinkerSmeltery.grout))
                          .build(consumer, prefix(TinkerCommons.punySmelting, "common/"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.mightySmelting)
                            .setFluidAndTime(TinkerFluids.searedStone, false, FluidValues.INGOT)
                            .setCast(Items.BOOK, true)
                            .build(consumer, prefix(TinkerCommons.mightySmelting, "common/"));
    ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.fantasticFoundry)
                          .addIngredient(Items.BOOK)
                          .addIngredient(TinkerSmeltery.netherGrout)
                          .addCriterion("has_item", hasItem(TinkerSmeltery.netherGrout))
                          .build(consumer, prefix(TinkerCommons.fantasticFoundry, "common/"));
    ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.encyclopedia)
                          .addIngredient(TinkerCommons.materialsAndYou)
                          .addIngredient(TinkerCommons.punySmelting)
                          .addIngredient(TinkerCommons.mightySmelting)
                          .addIngredient(TinkerCommons.fantasticFoundry)
                          .addCriterion("has_item", hasItem(TinkerCommons.fantasticFoundry))
                          .build(consumer, prefix(TinkerCommons.encyclopedia, "common/"));

    // glass
    folder = "common/glass/";
    ShapedRecipeBuilder.shapedRecipe(TinkerCommons.clearGlassPane, 16)
                       .key('#', TinkerCommons.clearGlass)
                       .patternLine("###")
                       .patternLine("###")
                       .addCriterion("has_block", hasItem(TinkerCommons.clearGlass))
                       .build(consumer, prefix(TinkerCommons.clearGlassPane, folder));
    for (GlassColor color : GlassColor.values()) {
      Block block = TinkerCommons.clearStainedGlass.get(color);
      ShapedRecipeBuilder.shapedRecipe(block, 8)
                         .key('#', TinkerCommons.clearGlass)
                         .key('X', color.getDye().getTag())
                         .patternLine("###")
                         .patternLine("#X#")
                         .patternLine("###")
                         .setGroup(modPrefix("stained_clear_glass"))
                         .addCriterion("has_clear_glass", hasItem(TinkerCommons.clearGlass))
                         .build(consumer, prefix(block, folder));
      Block pane = TinkerCommons.clearStainedGlassPane.get(color);
      ShapedRecipeBuilder.shapedRecipe(pane, 16)
                         .key('#', block)
                         .patternLine("###")
                         .patternLine("###")
                         .setGroup(modPrefix("stained_clear_glass_pane"))
                         .addCriterion("has_block", hasItem(block))
                         .build(consumer, prefix(pane, folder));
      ShapedRecipeBuilder.shapedRecipe(pane, 8)
                         .key('#', TinkerCommons.clearGlassPane)
                         .key('X', color.getDye().getTag())
                         .patternLine("###")
                         .patternLine("#X#")
                         .patternLine("###")
                         .setGroup(modPrefix("stained_clear_glass_pane"))
                         .addCriterion("has_clear_glass", hasItem(TinkerCommons.clearGlassPane))
                         .build(consumer, wrap(pane, folder, "_from_panes"));
    }
    // fix vanilla recipes not using tinkers glass
    String glassVanillaFolder = folder + "vanilla/";
    Consumer<IFinishedRecipe> vanillaGlassConsumer = withCondition(consumer, ConfigEnabledCondition.GLASS_RECIPE_FIX);
    ShapedRecipeBuilder.shapedRecipe(Blocks.BEACON)
                       .key('S', Items.NETHER_STAR)
                       .key('G', Tags.Items.GLASS_COLORLESS)
                       .key('O', Blocks.OBSIDIAN)
                       .patternLine("GGG")
                       .patternLine("GSG")
                       .patternLine("OOO")
                       .addCriterion("has_nether_star", hasItem(Items.NETHER_STAR))
                       .build(vanillaGlassConsumer, prefix(Blocks.BEACON, glassVanillaFolder));
    ShapedRecipeBuilder.shapedRecipe(Blocks.DAYLIGHT_DETECTOR)
                       .key('Q', Items.QUARTZ)
                       .key('G', Tags.Items.GLASS_COLORLESS)
                       .key('W', ItemTags.WOODEN_SLABS)
                       .patternLine("GGG")
                       .patternLine("QQQ")
                       .patternLine("WWW")
                       .addCriterion("has_quartz", hasItem(Items.QUARTZ))
                       .build(vanillaGlassConsumer, prefix(Blocks.DAYLIGHT_DETECTOR, glassVanillaFolder));
    ShapedRecipeBuilder.shapedRecipe(Items.END_CRYSTAL)
                       .key('T', Items.GHAST_TEAR)
                       .key('E', Items.ENDER_EYE)
                       .key('G', Tags.Items.GLASS_COLORLESS)
                       .patternLine("GGG")
                       .patternLine("GEG")
                       .patternLine("GTG")
                       .addCriterion("has_ender_eye", hasItem(Items.ENDER_EYE))
                       .build(vanillaGlassConsumer, prefix(Items.END_CRYSTAL, glassVanillaFolder));
    ShapedRecipeBuilder.shapedRecipe(Items.GLASS_BOTTLE, 3)
                       .key('#', Tags.Items.GLASS_COLORLESS)
                       .patternLine("# #")
                       .patternLine(" # ")
                       .addCriterion("has_glass", hasItem(Tags.Items.GLASS_COLORLESS))
                       .build(vanillaGlassConsumer, prefix(Items.GLASS_BOTTLE, glassVanillaFolder));


    // vanilla recipes
    ShapelessRecipeBuilder.shapelessRecipe(Items.FLINT)
                          .addIngredient(Blocks.GRAVEL)
                          .addIngredient(Blocks.GRAVEL)
                          .addIngredient(Blocks.GRAVEL)
                          .addCriterion("has_item", hasItem(Blocks.GRAVEL))
                          .build(
                            ConsumerWrapperBuilder.wrap()
                                                  .addCondition(ConfigEnabledCondition.GRAVEL_TO_FLINT)
                                                  .build(consumer),
                            modResource("common/flint"));
  }

  private void addMaterialRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "common/materials/";

    // ores
    metalCrafting(consumer, TinkerMaterials.copper, folder);
    metalCrafting(consumer, TinkerMaterials.cobalt, folder);
    // tier 3
    metalCrafting(consumer, TinkerMaterials.slimesteel, folder);
    metalCrafting(consumer, TinkerMaterials.tinkersBronze, folder);
    metalCrafting(consumer, TinkerMaterials.roseGold, folder);
    metalCrafting(consumer, TinkerMaterials.pigIron, folder);
    // tier 4
    metalCrafting(consumer, TinkerMaterials.queensSlime, folder);
    metalCrafting(consumer, TinkerMaterials.manyullyn, folder);
    metalCrafting(consumer, TinkerMaterials.hepatizon, folder);
    //registerMineralRecipes(consumer, TinkerMaterials.soulsteel,   folder);
    packingRecipe(consumer, "ingot", Items.NETHERITE_INGOT, "nugget", TinkerMaterials.netheriteNugget, folder);
    // tier 5
    //registerMineralRecipes(consumer, TinkerMaterials.knightslime, folder);

    // smelt ore into ingots, must use a blast furnace for nether ores
    Item cobaltIngot = TinkerMaterials.cobalt.getIngot();
    CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(TinkerWorld.cobaltOre), cobaltIngot, 1.5f, 200)
                        .addCriterion("has_item", hasItem(TinkerWorld.cobaltOre))
                        .build(consumer, wrap(cobaltIngot, folder, "_smelting"));
    Item copperIngot = TinkerMaterials.copper.getIngot();
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(TinkerWorld.copperOre), copperIngot, 1.5f, 200)
                        .addCriterion("has_item", hasItem(TinkerWorld.copperOre))
                        .build(consumer, wrap(copperIngot, folder, "_smelting"));
    CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(TinkerWorld.copperOre), copperIngot, 1.5f, 100)
                        .addCriterion("has_item", hasItem(TinkerWorld.copperOre))
                        .build(consumer, wrap(copperIngot, folder, "_blasting"));
  }
}
