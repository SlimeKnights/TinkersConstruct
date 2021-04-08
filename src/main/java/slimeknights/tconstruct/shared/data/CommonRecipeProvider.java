package slimeknights.tconstruct.shared.data;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.server.recipe.CookingRecipeJsonFactory;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonFactory;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
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
  protected void generate(Consumer<RecipeJsonProvider> consumer) {
    this.addCommonRecipes(consumer);
    this.addMaterialRecipes(consumer);
  }

  private void addCommonRecipes(Consumer<RecipeJsonProvider> consumer) {
    // firewood and lavawood
    String folder = "common/firewood/";
    registerSlabStair(consumer, TinkerCommons.blazewood, folder, false);
    registerSlabStair(consumer, TinkerCommons.lavawood, folder, false);

    // mud bricks
    registerSlabStair(consumer, TinkerCommons.mudBricks, "common/", false);

    // book
    ShapelessRecipeJsonFactory.create(TinkerCommons.book)
                          .input(Items.BOOK)
                          .input(TinkerTables.pattern)
                          .criterion("has_item", conditionsFromItem(TinkerTables.pattern))
                          .offerTo(consumer, prefix(TinkerCommons.book, "common/"));

    // glass
    folder = "common/glass/";
    ShapedRecipeJsonFactory.create(TinkerCommons.clearGlassPane, 16)
                       .input('#', TinkerCommons.clearGlass)
                       .pattern("###")
                       .pattern("###")
                       .criterion("has_block", conditionsFromItem(TinkerCommons.clearGlass))
                       .offerTo(consumer, prefix(TinkerCommons.clearGlassPane, folder));
    for (GlassColor color : GlassColor.values()) {
      Block block = TinkerCommons.clearStainedGlass.get(color);
      ShapedRecipeJsonFactory.create(block, 8)
                         .input('#', TinkerCommons.clearGlass)
                         .input('X', color.getDye().getTag())
                         .pattern("###")
                         .pattern("#X#")
                         .pattern("###")
                         .group(locationString("stained_clear_glass"))
                         .criterion("has_clear_glass", conditionsFromItem(TinkerCommons.clearGlass))
                         .offerTo(consumer, prefix(block, folder));
      Block pane = TinkerCommons.clearStainedGlassPane.get(color);
      ShapedRecipeJsonFactory.create(pane, 16)
                         .input('#', block)
                         .pattern("###")
                         .pattern("###")
                         .group(locationString("stained_clear_glass_pane"))
                         .criterion("has_block", conditionsFromItem(block))
                         .offerTo(consumer, prefix(pane, folder));
      ShapedRecipeJsonFactory.create(pane, 8)
                         .input('#', TinkerCommons.clearGlassPane)
                         .input('X', color.getDye().getTag())
                         .pattern("###")
                         .pattern("#X#")
                         .pattern("###")
                         .group(locationString("stained_clear_glass_pane"))
                         .criterion("has_clear_glass", conditionsFromItem(TinkerCommons.clearGlassPane))
                         .offerTo(consumer, wrap(pane, folder, "_from_panes"));
    }

    // vanilla recipes
    ShapelessRecipeJsonFactory.create(Items.FLINT)
                          .input(Blocks.GRAVEL)
                          .input(Blocks.GRAVEL)
                          .input(Blocks.GRAVEL)
                          .criterion("has_item", conditionsFromItem(Blocks.GRAVEL))
                          .offerTo(
                            ConsumerWrapperBuilder.wrap()
                                                  .addCondition(ConfigEnabledCondition.GRAVEL_TO_FLINT)
                                                  .build(consumer),
                            location("common/flint"));
  }

  private void addMaterialRecipes(Consumer<RecipeJsonProvider> consumer) {
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
    ItemConvertible cobaltIngot = TinkerMaterials.cobalt.getIngot();
    CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(TinkerWorld.cobaltOre), cobaltIngot, 1.5f, 200)
                        .criterion("has_item", conditionsFromItem(TinkerWorld.cobaltOre))
                        .offerTo(consumer, wrap(cobaltIngot, folder, "_smelting"));
    ItemConvertible copperIngot = TinkerMaterials.copper.getIngot();
    CookingRecipeJsonFactory.createSmelting(Ingredient.ofItems(TinkerWorld.copperOre), copperIngot, 1.5f, 200)
                        .criterion("has_item", conditionsFromItem(TinkerWorld.copperOre))
                        .offerTo(consumer, wrap(copperIngot, folder, "_smelting"));
    CookingRecipeJsonFactory.createBlasting(Ingredient.ofItems(TinkerWorld.copperOre), copperIngot, 1.5f, 100)
                        .criterion("has_item", conditionsFromItem(TinkerWorld.copperOre))
                        .offerTo(consumer, wrap(copperIngot, folder, "_blasting"));
  }

  /**
   * Adds recipes to convert a block to ingot, ingot to block, and for nuggets
   * @param consumer  Recipe consumer
   * @param metal     Metal object
   * @param folder    Folder for recipes
   */
  protected void registerMineralRecipes(Consumer<RecipeJsonProvider> consumer, MetalItemObject metal, String folder) {
    ItemConvertible ingot = metal.getIngot();
    registerPackingRecipe(consumer, "block", metal.get(), "ingot", ingot, metal.getIngotTag(), folder);
    registerPackingRecipe(consumer, "ingot", ingot, "nugget", metal.getNugget(), metal.getNuggetTag(), folder);
  }
}
