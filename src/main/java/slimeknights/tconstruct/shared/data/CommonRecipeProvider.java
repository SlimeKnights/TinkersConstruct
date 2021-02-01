package slimeknights.tconstruct.shared.data;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.registration.MetalItemObject;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class CommonRecipeProvider extends BaseRecipeProvider {
  public CommonRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.addCommonRecipes(consumer);
    this.addMaterialRecipes(consumer);
  }

  private void addCommonRecipes(Consumer<IFinishedRecipe> consumer) {
    // firewood and lavawood
    String folder = "common/firewood/";
    ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.firewood)
                          .addIngredient(Items.BLAZE_POWDER)
                          .addIngredient(TinkerCommons.lavawood)
                          .addIngredient(Items.BLAZE_POWDER)
                          .addCriterion("has_lavawood", hasItem(TinkerCommons.lavawood))
                          .build(consumer, prefix(TinkerCommons.firewood, folder));
    registerSlabStair(consumer, TinkerCommons.firewood, folder, false);
    registerSlabStair(consumer, TinkerCommons.lavawood, folder, false);

    // graveyard soil
    folder = "common/soil/";
    ShapelessRecipeBuilder.shapelessRecipe(TinkerModifiers.graveyardSoil)
                          .addIngredient(Blocks.DIRT)
                          .addIngredient(Items.ROTTEN_FLESH)
                          .addIngredient(Items.BONE_MEAL)
                          .addCriterion("has_dirt", hasItem(Blocks.DIRT))
                          .addCriterion("has_rotten_flesh", hasItem(Items.ROTTEN_FLESH))
                          .addCriterion("has_bone_meal", hasItem(Items.BONE_MEAL))
                          .build(consumer, prefix(TinkerModifiers.graveyardSoil, folder));
    // consecrated soil
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(TinkerModifiers.graveyardSoil), TinkerModifiers.consecratedSoil, 0.1f, 200)
                        .addCriterion("has_item", hasItem(TinkerModifiers.graveyardSoil))
                        .build(consumer, prefix(TinkerModifiers.consecratedSoil, folder));

    // mud bricks
    ShapedRecipeBuilder.shapedRecipe(TinkerCommons.mudBricks)
                       .key('#', TinkerCommons.mudBrick.get())
                       .patternLine("##")
                       .patternLine("##")
                       .addCriterion("has_mud_brick", hasItem(TinkerCommons.mudBrick))
                       .build(consumer, prefix(TinkerCommons.mudBricks, folder));
    registerSlabStair(consumer, TinkerCommons.mudBricks, folder, false);
    // FIXME: temporary mud brick item recipe
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(Blocks.DIRT), TinkerCommons.mudBrick, 0.3f, 200)
                        .addCriterion("has_item", hasItem(Blocks.DIRT))
                        .build(consumer, prefix(TinkerCommons.mudBrick, folder));

    // book
    ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.book)
                          .addIngredient(Items.BOOK)
                          .addIngredient(TinkerTables.pattern)
                          .addCriterion("has_item", hasItem(TinkerTables.pattern))
                          .build(consumer, prefix(TinkerCommons.book, "common/"));

    // glass
    ShapedRecipeBuilder.shapedRecipe(TinkerCommons.clearGlassPane, 16)
                       .key('#', TinkerCommons.clearGlass)
                       .patternLine("###")
                       .patternLine("###")
                       .addCriterion("has_block", hasItem(TinkerCommons.clearGlass))
                       .build(consumer, prefix(TinkerCommons.clearGlassPane, "common/glass/"));
    for (GlassColor color : GlassColor.values()) {
      Block block = TinkerCommons.clearStainedGlass.get(color);
      ShapedRecipeBuilder.shapedRecipe(block, 8)
                         .key('#', TinkerCommons.clearGlass)
                         .key('X', color.getDye().getTag())
                         .patternLine("###")
                         .patternLine("#X#")
                         .patternLine("###")
                         .setGroup(locationString("stained_clear_glass"))
                         .addCriterion("has_clear_glass", hasItem(TinkerCommons.clearGlass))
                         .build(consumer, prefix(block, "common/glass/"));
      Block pane = TinkerCommons.clearStainedGlassPane.get(color);
      ShapedRecipeBuilder.shapedRecipe(pane, 16)
                         .key('#', block)
                         .patternLine("###")
                         .patternLine("###")
                         .setGroup(locationString("stained_clear_glass_pane"))
                         .addCriterion("has_block", hasItem(block))
                         .build(consumer, prefix(pane, "common/glass/"));
      ShapedRecipeBuilder.shapedRecipe(pane, 8)
                         .key('#', TinkerCommons.clearGlassPane)
                         .key('X', color.getDye().getTag())
                         .patternLine("###")
                         .patternLine("#X#")
                         .patternLine("###")
                         .setGroup(locationString("stained_clear_glass_pane"))
                         .addCriterion("has_clear_glass", hasItem(TinkerCommons.clearGlassPane))
                         .build(consumer, wrap(pane, "common/glass/", "_from_panes"));
    }

    // vanilla recipes
    ShapelessRecipeBuilder.shapelessRecipe(Items.FLINT)
                          .addIngredient(Blocks.GRAVEL)
                          .addIngredient(Blocks.GRAVEL)
                          .addIngredient(Blocks.GRAVEL)
                          .addCriterion("has_item", hasItem(Blocks.GRAVEL))
                          .build(
                            ConsumerWrapperBuilder.wrap()
                                                  .addCondition(new ConfigOptionEnabledCondition("addGravelToFlintRecipe"))
                                                  .build(consumer),
                            location("common/flint"));
  }

  private void addMaterialRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "common/materials/";

    // ores
    registerMineralRecipes(consumer, TinkerMaterials.copper, folder);
    registerMineralRecipes(consumer, TinkerMaterials.cobalt, folder);
    registerMineralRecipes(consumer, TinkerMaterials.ardite, folder);
    // tier 3
    registerMineralRecipes(consumer, TinkerMaterials.slimesteel,    folder);
    registerMineralRecipes(consumer, TinkerMaterials.tinkersBronze, folder);
    registerMineralRecipes(consumer, TinkerMaterials.roseGold,      folder);
    registerMineralRecipes(consumer, TinkerMaterials.pigiron,       folder);
    // tier 4
    registerMineralRecipes(consumer, TinkerMaterials.manyullyn,   folder);
    registerMineralRecipes(consumer, TinkerMaterials.hepatizon, folder);
    registerMineralRecipes(consumer, TinkerMaterials.slimeBronze, folder);
    registerMineralRecipes(consumer, TinkerMaterials.soulsteel,   folder);
    registerPackingRecipe(consumer, "ingot", Items.NETHERITE_INGOT, "nugget", TinkerMaterials.netheriteNugget, folder);
    // tier 5
    registerMineralRecipes(consumer, TinkerMaterials.knightslime, folder);

    // smelt ore into ingots, must use a blast furnace for nether ores
    IItemProvider cobaltIngot = TinkerMaterials.cobalt.getIngot();
    CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(TinkerWorld.cobaltOre), cobaltIngot, 1.5f, 200)
                        .addCriterion("has_item", hasItem(TinkerWorld.cobaltOre))
                        .build(consumer, wrap(cobaltIngot, folder, "_smelting"));
    IItemProvider arditeIngot = TinkerMaterials.ardite.getIngot();
    CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(TinkerWorld.arditeOre), arditeIngot, 1.5f, 200)
                        .addCriterion("has_item", hasItem(TinkerWorld.arditeOre))
                        .build(consumer, wrap(arditeIngot, folder, "_smelting"));
    IItemProvider copperIngot = TinkerMaterials.copper.getIngot();
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(TinkerWorld.copperOre), copperIngot, 1.5f, 200)
                        .addCriterion("has_item", hasItem(TinkerWorld.copperOre))
                        .build(consumer, wrap(copperIngot, folder, "_smelting"));
    CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(TinkerWorld.copperOre), copperIngot, 1.5f, 100)
                        .addCriterion("has_item", hasItem(TinkerWorld.copperOre))
                        .build(consumer, wrap(copperIngot, folder, "_blasting"));
  }

  /**
   * Adds recipes to convert a block to ingot, ingot to block, and for nuggets
   * @param consumer  Recipe consumer
   * @param metal     Metal object
   * @param folder    Folder for recipes
   */
  protected void registerMineralRecipes(Consumer<IFinishedRecipe> consumer, MetalItemObject metal, String folder) {
    String name = metal.getName();
    IItemProvider ingot = metal.getIngot();
    registerPackingRecipe(consumer, "block", metal.get(), "ingot", ingot, getTag("forge", "ingots/" + name), folder);
    registerPackingRecipe(consumer, "ingot", ingot, "nugget", metal.getNugget(), getTag("forge", "nuggets/" + name), folder);
  }
}
