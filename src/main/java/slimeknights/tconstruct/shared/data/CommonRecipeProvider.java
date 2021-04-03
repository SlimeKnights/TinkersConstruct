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
import slimeknights.tconstruct.common.conditions.ConfigEnabledCondition;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.registration.MetalItemObject;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class CommonRecipeProvider extends BaseRecipeProvider {
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
    registerSlabStair(consumer, TinkerCommons.blazewood, folder, false);
    registerSlabStair(consumer, TinkerCommons.lavawood, folder, false);

    // mud bricks
    registerSlabStair(consumer, TinkerCommons.mudBricks, "common/", false);

    // book
    ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.book)
                          .addIngredient(Items.BOOK)
                          .addIngredient(TinkerTables.pattern)
                          .addCriterion("has_item", hasItem(TinkerTables.pattern))
                          .build(consumer, prefix(TinkerCommons.book, "common/"));

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
                         .setGroup(locationString("stained_clear_glass"))
                         .addCriterion("has_clear_glass", hasItem(TinkerCommons.clearGlass))
                         .build(consumer, prefix(block, folder));
      Block pane = TinkerCommons.clearStainedGlassPane.get(color);
      ShapedRecipeBuilder.shapedRecipe(pane, 16)
                         .key('#', block)
                         .patternLine("###")
                         .patternLine("###")
                         .setGroup(locationString("stained_clear_glass_pane"))
                         .addCriterion("has_block", hasItem(block))
                         .build(consumer, prefix(pane, folder));
      ShapedRecipeBuilder.shapedRecipe(pane, 8)
                         .key('#', TinkerCommons.clearGlassPane)
                         .key('X', color.getDye().getTag())
                         .patternLine("###")
                         .patternLine("#X#")
                         .patternLine("###")
                         .setGroup(locationString("stained_clear_glass_pane"))
                         .addCriterion("has_clear_glass", hasItem(TinkerCommons.clearGlassPane))
                         .build(consumer, wrap(pane, folder, "_from_panes"));
    }

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
                            location("common/flint"));
  }

  private void addMaterialRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "common/materials/";

    // ores
    registerMineralRecipes(consumer, TinkerMaterials.copper, folder);
    registerMineralRecipes(consumer, TinkerMaterials.cobalt, folder);
    // tier 3
    registerMineralRecipes(consumer, TinkerMaterials.slimesteel,    folder);
    registerMineralRecipes(consumer, TinkerMaterials.tinkersBronze, folder);
    registerMineralRecipes(consumer, TinkerMaterials.roseGold,      folder);
    registerMineralRecipes(consumer, TinkerMaterials.pigIron,       folder);
    // tier 4
    registerMineralRecipes(consumer, TinkerMaterials.queensSlime, folder);
    registerMineralRecipes(consumer, TinkerMaterials.manyullyn,   folder);
    registerMineralRecipes(consumer, TinkerMaterials.hepatizon,   folder);
    //registerMineralRecipes(consumer, TinkerMaterials.soulsteel,   folder);
    registerPackingRecipe(consumer, "ingot", Items.NETHERITE_INGOT, "nugget", TinkerMaterials.netheriteNugget, folder);
    // tier 5
    //registerMineralRecipes(consumer, TinkerMaterials.knightslime, folder);

    // smelt ore into ingots, must use a blast furnace for nether ores
    IItemProvider cobaltIngot = TinkerMaterials.cobalt.getIngot();
    CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(TinkerWorld.cobaltOre), cobaltIngot, 1.5f, 200)
                        .addCriterion("has_item", hasItem(TinkerWorld.cobaltOre))
                        .build(consumer, wrap(cobaltIngot, folder, "_smelting"));
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
    IItemProvider ingot = metal.getIngot();
    registerPackingRecipe(consumer, "block", metal.get(), "ingot", ingot, metal.getIngotTag(), folder);
    registerPackingRecipe(consumer, "ingot", ingot, "nugget", metal.getNugget(), metal.getNuggetTag(), folder);
  }
}
