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
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.StickySlimeBlock.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
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
    // metals
    registerMineralRecipes(consumer, "cobalt",      TinkerMaterials.cobaltBlock,      TinkerMaterials.cobaltIngot,      TinkerMaterials.cobaltNugget,      folder);
    registerMineralRecipes(consumer, "ardite",      TinkerMaterials.arditeBlock,      TinkerMaterials.arditeIngot,      TinkerMaterials.arditeNugget,      folder);
    registerMineralRecipes(consumer, "manyullyn",   TinkerMaterials.manyullynBlock,   TinkerMaterials.manyullynIngot,   TinkerMaterials.manyullynNugget,   folder);
    registerMineralRecipes(consumer, "knightslime", TinkerMaterials.knightSlimeBlock, TinkerMaterials.knightslimeIngot, TinkerMaterials.knightslimeNugget, folder);
    registerMineralRecipes(consumer, "pig_iron",    TinkerMaterials.pigironBlock,     TinkerMaterials.pigironIngot,     TinkerMaterials.pigironNugget,     folder);
    registerMineralRecipes(consumer, "copper",      TinkerMaterials.copperBlock,      TinkerMaterials.copperIngot,      TinkerMaterials.copperNugget,      folder);
    registerMineralRecipes(consumer, "rose_gold",   TinkerMaterials.roseGoldBlock,    TinkerMaterials.roseGoldIngot,    TinkerMaterials.roseGoldNugget,    folder);
    registerPackingRecipe(consumer, "ingot", Items.NETHERITE_INGOT, "nugget", TinkerMaterials.netheriteNugget, folder);

    // smelt ore into ingots, must use a blast furnace for nether ores
    CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(TinkerWorld.cobaltOre), TinkerMaterials.cobaltIngot, 1.5f, 200)
                        .addCriterion("has_item", hasItem(TinkerWorld.cobaltOre))
                        .build(consumer, wrap(TinkerMaterials.cobaltIngot, folder, "_smelting"));
    CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(TinkerWorld.arditeOre), TinkerMaterials.arditeIngot, 1.5f, 200)
                        .addCriterion("has_item", hasItem(TinkerWorld.arditeOre))
                        .build(consumer, wrap(TinkerMaterials.arditeIngot, folder, "_smelting"));

    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(TinkerWorld.copperOre), TinkerMaterials.copperIngot, 1.5f, 200)
                        .addCriterion("has_item", hasItem(TinkerWorld.copperOre))
                        .build(consumer, wrap(TinkerMaterials.copperIngot, folder, "_smelting"));
    CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(TinkerWorld.copperOre), TinkerMaterials.copperIngot, 1.5f, 100)
                        .addCriterion("has_item", hasItem(TinkerWorld.copperOre))
                        .build(consumer, wrap(TinkerMaterials.copperIngot, folder, "_blasting"));

    // FIXME: temporary knightslime recipe
    Item purpleSlime = TinkerCommons.slimeball.get(SlimeType.PURPLE);
    ShapelessRecipeBuilder.shapelessRecipe(TinkerMaterials.knightslimeIngot)
                          .addIngredient(purpleSlime)
                          .addIngredient(Items.IRON_INGOT)
                          .addIngredient(TinkerSmeltery.searedBrick)
                          .setGroup(TinkerMaterials.knightslimeIngot.getRegistryName().toString())
                          .addCriterion("has_item", hasItem(purpleSlime))
                          .build(consumer, wrap(TinkerMaterials.knightslimeIngot, folder, "_crafting"));
  }
}
