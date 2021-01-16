package slimeknights.tconstruct.smeltery.data;

import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.data.SingleItemRecipeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ContainerFillingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityIngredient;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.StickySlimeBlock.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class SmelteryRecipeProvider extends BaseRecipeProvider {
  public SmelteryRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.addBaseRecipes(consumer);
    this.addMeltingRecipes(consumer);
    this.addCastingRecipes(consumer);
    this.addAlloyRecipes(consumer);
    this.addEntityMeltingRecipes(consumer);
  }

  private void addBaseRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "smeltery/seared_block/";
    // grout crafting
    ShapelessRecipeBuilder.shapelessRecipe(TinkerSmeltery.grout, 2)
                          .addIngredient(Items.CLAY_BALL)
                          .addIngredient(ItemTags.SAND)
                          .addIngredient(Blocks.GRAVEL)
                          .addCriterion("has_item", hasItem(Items.CLAY_BALL))
                          .build(consumer, prefix(TinkerSmeltery.grout, folder));
    ShapelessRecipeBuilder.shapelessRecipe(TinkerSmeltery.grout, 8)
                          .addIngredient(Blocks.CLAY)
                          .addIngredient(ItemTags.SAND).addIngredient(ItemTags.SAND).addIngredient(ItemTags.SAND).addIngredient(ItemTags.SAND)
                          .addIngredient(Blocks.GRAVEL).addIngredient(Blocks.GRAVEL).addIngredient(Blocks.GRAVEL).addIngredient(Blocks.GRAVEL)
                          .addCriterion("has_item", hasItem(Blocks.CLAY))
                          .build(consumer, wrap(TinkerSmeltery.grout, folder, "_multiple"));

    // seared bricks from grout
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(TinkerSmeltery.grout), TinkerSmeltery.searedBrick, 0.3f, 200)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.grout))
                        .build(consumer, prefix(TinkerSmeltery.searedBricks, folder));

    // block from bricks
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedBricks)
                       .key('b', TinkerSmeltery.searedBrick)
                       .patternLine("bb")
                       .patternLine("bb")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, wrap(TinkerSmeltery.searedBricks, folder, "_from_brick"));
    // ladder from bircks
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedLadder, 2)
                       .key('b', TinkerSmeltery.searedBrick)
                       .patternLine("b b")
                       .patternLine("bbb")
                       .patternLine("b b")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, prefix(TinkerSmeltery.searedLadder, folder));

    // cobble -> stone
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(TinkerSmeltery.searedCobble.get()), TinkerSmeltery.searedStone, 0.1f, 200)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.searedCobble.get()))
                        .build(consumer, wrap(TinkerSmeltery.searedStone, folder, "_smelting"));
    // stone -> paver
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(TinkerSmeltery.searedStone.get()), TinkerSmeltery.searedPaver, 0.1f, 200)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.searedStone.get()))
                        .build(consumer, wrap(TinkerSmeltery.searedPaver, folder, "_smelting"));
    // stone -> bricks
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedBricks, 4)
                       .key('b', TinkerSmeltery.searedStone)
                       .patternLine("bb")
                       .patternLine("bb")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedStone))
                       .build(consumer, wrap(TinkerSmeltery.searedBricks, folder, "_crafting"));
    // bricks -> cracked
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(TinkerSmeltery.searedBricks), TinkerSmeltery.searedCrackedBricks, 0.1f, 200)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.searedBricks))
                        .build(consumer, wrap(TinkerSmeltery.searedCrackedBricks, folder, "_smelting"));
    // brick slabs -> fancy
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedFancyBricks)
                       .key('s', TinkerSmeltery.searedBricks.getSlab())
                       .patternLine("s")
                       .patternLine("s")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBricks.getSlab()))
                       .build(consumer, wrap(TinkerSmeltery.searedFancyBricks, folder, "_crafting"));
    // bricks or stone as input
    this.addSearedStonecutter(consumer, TinkerSmeltery.searedBricks, folder);
    this.addSearedStonecutter(consumer, TinkerSmeltery.searedFancyBricks, folder);
    this.addSearedStonecutter(consumer, TinkerSmeltery.searedTriangleBricks, folder);

    // seared glass
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedGlass)
                       .key('b', TinkerSmeltery.searedBrick)
                       .key('G', Tags.Items.GLASS_COLORLESS)
                       .patternLine(" b ")
                       .patternLine("bGb")
                       .patternLine(" b ")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, prefix(TinkerSmeltery.searedGlass, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedGlassPane, 16)
                       .key('#', TinkerSmeltery.searedGlass)
                       .patternLine("###")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedGlass))
                       .build(consumer, prefix(TinkerSmeltery.searedGlassPane, folder));

    // stairs and slabs
    this.registerSlabStair(consumer, TinkerSmeltery.searedStone, folder, true);
    this.registerSlabStairWall(consumer, TinkerSmeltery.searedCobble, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedPaver, folder, true);
    this.registerSlabStairWall(consumer, TinkerSmeltery.searedBricks, folder, true);

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTank.get(SearedTankBlock.TankType.TANK))
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('B', Tags.Items.GLASS)
                       .patternLine("###")
                       .patternLine("#B#")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location("smeltery/seared/tank"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTank.get(SearedTankBlock.TankType.GAUGE))
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('B', Tags.Items.GLASS)
                       .patternLine("#B#")
                       .patternLine("BBB")
                       .patternLine("#B#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location("smeltery/seared/gauge"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTank.get(SearedTankBlock.TankType.WINDOW))
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('B', Tags.Items.GLASS)
                       .patternLine("#B#")
                       .patternLine("#B#")
                       .patternLine("#B#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location("smeltery/seared/window"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedFaucet.get())
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("# #")
                       .patternLine(" # ")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location("smeltery/faucet"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedChannel.get())
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("# #")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location("smeltery/channel"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.castingBasin.get())
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("# #")
                       .patternLine("# #")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location("smeltery/casting/basin"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.castingTable.get())
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("###")
                       .patternLine("# #")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location("smeltery/casting/table"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedDrain)
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("# #")
                       .patternLine("# #")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location("smeltery/drain"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedChute)
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("###")
                       .patternLine("   ")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location("smeltery/chute"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedDuct)
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('C', TinkerTags.Items.INGOTS_COBALT)
                       .patternLine("# #")
                       .patternLine("C C")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerTags.Items.INGOTS_COBALT))
                       .build(consumer, location("smeltery/duct"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedMelter)
                       .key('G', TinkerSmeltery.searedTank.get(TankType.GAUGE))
                       .key('B', TinkerSmeltery.searedBrick)
                       .patternLine("BGB")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, prefix(TinkerSmeltery.searedMelter, "smeltery/"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.copperCan, 3)
                       .key('c', TinkerTags.Items.INGOTS_COPPER)
                       .patternLine("c c")
                       .patternLine(" c ")
                       .addCriterion("has_item", hasItem(TinkerTags.Items.INGOTS_COPPER))
                       .build(consumer, prefix(TinkerSmeltery.copperCan, "smeltery/"));
  }


  private void addCastingRecipes(Consumer<IFinishedRecipe> consumer) {
    // Pure Fluid Recipes
    String folder = "casting/";
    ContainerFillingRecipeBuilder.tableRecipe(Items.BUCKET, FluidAttributes.BUCKET_VOLUME)
                                 .addCriterion("has_item", hasItem(Items.BUCKET))
                                 .build(consumer, location(folder + "filling/bucket"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.copperCan, MaterialValues.VALUE_Ingot)
                                 .addCriterion("has_item", hasItem(TinkerSmeltery.copperCan))
                                 .build(consumer, location(folder + "filling/copper_can"));
    // Slime
    this.addSlimeCastingRecipe(consumer, TinkerFluids.blood, SlimeType.BLOOD, folder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.greenSlime, SlimeType.GREEN, folder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.blueSlime, SlimeType.BLUE, folder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.purpleSlime, SlimeType.PURPLE, folder);

    // seared blocks
    this.addBlockCastingRecipe(consumer, TinkerFluids.searedStone, MaterialValues.VALUE_BrickBlock, TinkerSmeltery.searedStone, folder);
    this.addIngotCastingRecipe(consumer, TinkerFluids.searedStone, TinkerSmeltery.searedBrick, folder);
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedGlass)
                            .setFluid(new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.VALUE_Ingot * 4))
                            .setCast(Tags.Items.GLASS_COLORLESS, true)
                            .addCriterion("has_item", hasItem(TinkerFluids.searedStone.asItem()))
                            .build(consumer, prefix(TinkerSmeltery.searedGlass, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.searedGlassPane)
                            .setFluid(new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.VALUE_BrickBlock * 6 / 16))
                            .setCast(Tags.Items.GLASS_PANES_COLORLESS, true)
                            .addCriterion("has_item", hasItem(Tags.Items.GLASS_PANES_COLORLESS))
                            .build(consumer, prefix(TinkerSmeltery.searedGlassPane, folder));

    // glass
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenGlass, MaterialValues.VALUE_Glass, TinkerCommons.clearGlass, folder);
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.clearGlassPane)
                            .setFluid(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Pane))
                            .addCriterion("has_item", hasItem(TinkerCommons.clearGlassPane.asItem()))
                            .build(consumer, prefix(TinkerCommons.clearGlassPane, folder));

    // emeralds
    this.addGemCastingRecipe(consumer, TinkerFluids.moltenEmerald, Items.EMERALD, folder);
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenEmerald, MaterialValues.VALUE_GemBlock, Items.EMERALD_BLOCK, folder);

    // ender pearls
    this.addGemCastingRecipe(consumer, TinkerFluids.moltenEnder, Items.ENDER_PEARL, folder);

    // obsidian
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenObsidian, MaterialValues.VALUE_Glass, Items.OBSIDIAN, folder);
    // Molten objects with Bucket, Block, Ingot, and Nugget forms with standard values
    this.addMoltenMineralCastingRecipe(consumer, TinkerFluids.moltenIron, Items.IRON_BLOCK, Items.IRON_INGOT, Items.IRON_NUGGET, folder);
    this.addMoltenMineralCastingRecipe(consumer, TinkerFluids.moltenGold, Items.GOLD_BLOCK, Items.GOLD_INGOT, Items.GOLD_NUGGET, folder);
    this.addMoltenMineralCastingRecipe(consumer, TinkerFluids.moltenCobalt, TinkerMaterials.cobaltBlock, TinkerMaterials.cobaltIngot, TinkerMaterials.cobaltNugget, folder);
    this.addMoltenMineralCastingRecipe(consumer, TinkerFluids.moltenArdite, TinkerMaterials.arditeBlock, TinkerMaterials.arditeIngot,  TinkerMaterials.arditeNugget, folder);
    this.addMoltenMineralCastingRecipe(consumer, TinkerFluids.moltenManyullyn, TinkerMaterials.manyullynBlock, TinkerMaterials.manyullynIngot, TinkerMaterials.manyullynNugget, folder);
    this.addMoltenMineralCastingRecipe(consumer, TinkerFluids.moltenPigIron, TinkerMaterials.pigironBlock, TinkerMaterials.pigironIngot, TinkerMaterials.pigironNugget, folder);
    this.addMoltenMineralCastingRecipe(consumer, TinkerFluids.moltenKnightslime, TinkerMaterials.knightSlimeBlock, TinkerMaterials.knightslimeIngot, TinkerMaterials.knightslimeNugget, folder);
    this.addMoltenMineralCastingRecipe(consumer, TinkerFluids.moltenCopper, TinkerMaterials.copperBlock, TinkerMaterials.copperIngot, TinkerMaterials.copperNugget, folder);
    this.addMoltenMineralCastingRecipe(consumer, TinkerFluids.moltenRoseGold, TinkerMaterials.roseGoldBlock, TinkerMaterials.roseGoldIngot, TinkerMaterials.roseGoldNugget, folder);
    this.addMoltenMineralCastingRecipe(consumer, TinkerFluids.moltenNetherite, Blocks.NETHERITE_BLOCK, Items.NETHERITE_INGOT, TinkerMaterials.netheriteNugget, folder);

    // craft seared stone from glass and stone
    // cobble
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedCobble, new CompoundIngredient(
      Ingredient.fromTag(Tags.Items.COBBLESTONE),
      Ingredient.fromItems(Blocks.GRAVEL)
    ), folder);
    addSearedSlabCastingRecipe(consumer, TinkerSmeltery.searedCobble.getSlab(), Ingredient.fromItems(Blocks.COBBLESTONE_SLAB), folder);
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedCobble.getStairs(), Ingredient.fromItems(Blocks.COBBLESTONE_STAIRS), folder);
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedCobble.getWall(), Ingredient.fromItems(Blocks.COBBLESTONE_WALL), folder);
    // stone
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedStone, Ingredient.fromTag(Tags.Items.STONE), folder);
    addSearedSlabCastingRecipe(consumer, TinkerSmeltery.searedStone.getSlab(), Ingredient.fromItems(Blocks.STONE_SLAB), folder);
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedStone.getStairs(), Ingredient.fromItems(Blocks.STONE_STAIRS), folder);
    // stone bricks
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedBricks, Ingredient.fromItems(Blocks.STONE_BRICKS), folder);
    addSearedSlabCastingRecipe(consumer, TinkerSmeltery.searedBricks.getSlab(), Ingredient.fromItems(Blocks.STONE_BRICK_SLAB), folder);
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedBricks.getStairs(), Ingredient.fromItems(Blocks.STONE_BRICK_STAIRS), folder);
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedBricks.getWall(), Ingredient.fromItems(Blocks.STONE_BRICK_WALL), folder);
    // other seared
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedCrackedBricks, Ingredient.fromItems(Blocks.CRACKED_STONE_BRICKS), folder);
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedFancyBricks, Ingredient.fromItems(Blocks.CHISELED_STONE_BRICKS), folder);
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedPaver, Ingredient.fromItems(Blocks.SMOOTH_STONE), folder);

    // Misc
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.lavawood)
                            .setFluid(new FluidStack(Fluids.LAVA, 250))
                            .setCast(ItemTags.PLANKS, true)
                            .addCriterion("has_item", hasItem(Items.LAVA_BUCKET))
                            .build(consumer, prefix(TinkerCommons.lavawood, folder));

    // Cast recipes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.blankCast)
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot))
                            .setSwitchSlots()
                            .addCriterion("has_item", hasItem(TinkerSmeltery.castingTable))
                            .build(consumer, location(folder + "casts/blank"));

    this.addCastCastingRecipe(consumer, Tags.Items.INGOTS, TinkerSmeltery.ingotCast, folder);
    this.addCastCastingRecipe(consumer, Tags.Items.NUGGETS, TinkerSmeltery.nuggetCast, folder);
    this.addCastCastingRecipe(consumer, Tags.Items.GEMS, TinkerSmeltery.gemCast, folder);
  }

  private void addMeltingRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "melting/";

    // water from ice
    ToIntFunction<Integer> waterTemp = (amount) -> IMeltingRecipe.calcTemperature(20, amount);
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ICE), Fluids.WATER, FluidAttributes.BUCKET_VOLUME, waterTemp)
                        .addCriterion("has_item", hasItem(Items.ICE))
                        .build(consumer, location(folder + "water_from_ice"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.PACKED_ICE), Fluids.WATER, FluidAttributes.BUCKET_VOLUME * 9, waterTemp)
                        .addCriterion("has_item", hasItem(Items.PACKED_ICE))
                        .build(consumer, location(folder + "water_from_packed_ice"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.BLUE_ICE), Fluids.WATER, FluidAttributes.BUCKET_VOLUME * 81, waterTemp)
                        .addCriterion("has_item", hasItem(Items.BLUE_ICE))
                        .build(consumer, location(folder + "water_from_blue_ice"));
    // water from snow
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SNOWBALL), Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 8, waterTemp)
                        .addCriterion("has_item", hasItem(Items.SNOWBALL))
                        .build(consumer, location(folder + "water_from_snowball"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SNOW_BLOCK), Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 2, waterTemp)
                        .addCriterion("has_item", hasItem(Items.SNOW_BLOCK))
                        .build(consumer, location(folder + "water_from_snow_block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SNOW), Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 8, waterTemp)
                        .addCriterion("has_item", hasItem(Items.SNOW))
                        .build(consumer, location(folder + "water_from_snow_layer"));

    // ore metals
    addMetalMelting(consumer, TinkerFluids.moltenIron.get(), "iron", true, folder);
    addMetalMelting(consumer, TinkerFluids.moltenGold.get(), "gold", true, folder);
    addMetalMelting(consumer, TinkerFluids.moltenCopper.get(), "copper", true, folder);
    addMetalMelting(consumer, TinkerFluids.moltenCobalt.get(), "cobalt", true, folder);
    addMetalMelting(consumer, TinkerFluids.moltenArdite.get(), "ardite", true, folder);
    ITag<Item> ore = Tags.Items.ORES_NETHERITE_SCRAP;
    MeltingRecipeBuilder.melting(Ingredient.fromTag(ore), TinkerFluids.moltenDebris.get(), MaterialValues.VALUE_Ingot)
                        .setOre()
                        .addCriterion("hasItem", hasItem(ore))
                        .build(consumer, location(folder + "molten_debris_from_ore"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_SCRAP), TinkerFluids.moltenDebris.get(), MaterialValues.VALUE_Ingot)
                        .addCriterion("hasItem", hasItem(ore))
                        .build(consumer, location(folder + "molten_debris_from_scrap"));
    // metal alloys
    addMetalMelting(consumer, TinkerFluids.moltenRoseGold.get(), "rose_gold", false, folder);
    addMetalMelting(consumer, TinkerFluids.moltenManyullyn.get(), "manyullyn", false, folder);
    addMetalMelting(consumer, TinkerFluids.moltenPigIron.get(), "pig_iron", false, folder);
    addMetalMelting(consumer, TinkerFluids.moltenKnightslime.get(), "knightslime", false, folder);
    addMetalMelting(consumer, TinkerFluids.moltenNetherite.get(), "netherite", false, folder);

    // blood
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ROTTEN_FLESH), TinkerFluids.blood.get(), MaterialValues.VALUE_SlimeBall / 5)
                        .addCriterion("has_item", hasItem(Items.ROTTEN_FLESH))
                        .build(consumer, location("blood_from_flesh"));

    // glass
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.SAND), TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Glass)
                        .addCriterion("has_item", hasItem(Tags.Items.SAND))
                        .build(consumer, location(folder + "glass_from_sand"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GLASS), TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Glass)
                        .addCriterion("has_item", hasItem(Tags.Items.GLASS))
                        .build(consumer, location(folder + "glass_from_block"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GLASS_PANES), TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Pane)
                        .addCriterion("has_item", hasItem(Tags.Items.GLASS_PANES))
                        .build(consumer, location(folder + "glass_from_pane"));

    // seared stone
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.SEARED_BLOCKS), TinkerFluids.searedStone.get(), MaterialValues.VALUE_BrickBlock)
                        .addCriterion("has_item", hasItem(TinkerTags.Items.SEARED_BLOCKS))
                        .build(consumer, location(folder + "seared_stone_from_block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedBrick), TinkerFluids.searedStone.get(), MaterialValues.VALUE_Ingot)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                        .build(consumer, location(folder + "seared_stone_from_brick"));
    // double efficiency when using smeltery for grout
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.grout), TinkerFluids.searedStone.get(), MaterialValues.VALUE_Ingot * 2)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                        .build(consumer, location(folder + "seared_stone_from_grout"));

    // slime
    addSlimeMeltingRecipe(consumer, TinkerFluids.greenSlime,  SlimeType.GREEN,  TinkerTags.Items.GREEN_SLIMEBALL,  folder);
    addSlimeMeltingRecipe(consumer, TinkerFluids.blueSlime,   SlimeType.BLUE,   TinkerTags.Items.BLUE_SLIMEBALL,   folder);
    addSlimeMeltingRecipe(consumer, TinkerFluids.purpleSlime, SlimeType.PURPLE, TinkerTags.Items.PURPLE_SLIMEBALL, folder);

    // obsidian
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.OBSIDIAN), TinkerFluids.moltenObsidian.get(), MaterialValues.VALUE_Glass)
                        .addCriterion("has_item", hasItem(Tags.Items.OBSIDIAN))
                        .build(consumer, location(folder + "obsidian"));

    // emerald
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.ORES_EMERALD), TinkerFluids.moltenEmerald.get(), MaterialValues.VALUE_Gem)
                        .addCriterion("has_item", hasItem(Tags.Items.ORES_EMERALD))
                        .build(consumer, prefix(Items.EMERALD_ORE, folder));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), TinkerFluids.moltenEmerald.get(), MaterialValues.VALUE_Gem)
                        .addCriterion("has_item", hasItem(Tags.Items.GEMS_EMERALD))
                        .build(consumer, prefix(Items.EMERALD, folder));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_EMERALD), TinkerFluids.moltenEmerald.get(), MaterialValues.VALUE_GemBlock)
                        .addCriterion("has_item", hasItem(Tags.Items.STORAGE_BLOCKS_EMERALD))
                        .build(consumer, prefix(Items.EMERALD_BLOCK, folder));

    // special recipes
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_HORSE_ARMOR), TinkerFluids.moltenIron.get(), MaterialValues.VALUE_Ingot * 7)
                        .addCriterion("has_item", hasItem(Items.IRON_HORSE_ARMOR))
                        .build(consumer, prefix(Items.IRON_HORSE_ARMOR, folder));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_HORSE_ARMOR), TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot * 7)
                        .addCriterion("has_item", hasItem(Items.GOLDEN_HORSE_ARMOR))
                        .build(consumer, prefix(Items.GOLDEN_HORSE_ARMOR, folder));
    // cast to gold
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.CASTS), TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot)
                        .addCriterion("has_item", hasItem(TinkerTags.Items.CASTS))
                        .build(consumer, location(folder + "gold_from_cast"));

    // fuels
    MeltingFuelBuilder.fuel(new FluidStack(Fluids.LAVA, 50), 100)
                      .addCriterion("has_item", hasItem(Items.LAVA_BUCKET))
                      .build(consumer, location(folder + "fuel/lava"));
    MeltingFuelBuilder.fuel(new FluidStack(TinkerFluids.moltenBlaze.get(), 50), 150)
                      .addCriterion("has_item", hasItem(Items.LAVA_BUCKET))
                      .build(consumer, location(folder + "fuel/blaze"));
  }

  private void addAlloyRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "smeltery/alloys/";
    // TODO: knightslime

    // alloy recipes are in terms of ingots

    // rose gold
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenRoseGold.get(), MaterialValues.VALUE_Ingot * 4)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.VALUE_Ingot * 3)
                      .addInput(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot)
                      .addCriterion("has_copper", hasItem(TinkerMaterials.copperNugget))
                      .addCriterion("has_gold", hasItem(Items.GOLD_INGOT))
                      .build(consumer, prefixR(TinkerFluids.moltenRoseGold, folder));

    // pig iron
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenPigIron.get(), MaterialValues.VALUE_Ingot * 2)
                      .addInput(TinkerFluids.moltenIron.get(), MaterialValues.VALUE_Ingot)
                      .addInput(TinkerFluids.blood.get(), MaterialValues.VALUE_SlimeBall)
                      .addInput(TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Glass / 4)
                      .addCriterion("has_iron", hasItem(Items.IRON_INGOT))
                      .build(consumer, prefixR(TinkerFluids.moltenPigIron, folder));

    // manyullyn
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenManyullyn.get(), MaterialValues.VALUE_Ingot * 4)
                      .addInput(TinkerFluids.moltenCobalt.get(), MaterialValues.VALUE_Ingot * 3)
                      .addInput(TinkerFluids.moltenDebris.get(), MaterialValues.VALUE_Ingot)
                      .addCriterion("has_iron", hasItem(TinkerMaterials.cobaltIngot))
                      .build(consumer, prefixR(TinkerFluids.moltenManyullyn, folder));

    // netherrite
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenNetherite.get(), MaterialValues.VALUE_Nugget)
                      .addInput(TinkerFluids.moltenDebris.get(), MaterialValues.VALUE_Nugget * 4)
                      .addInput(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Nugget * 4)
                      .addCriterion("has_iron", hasItem(Items.IRON_NUGGET))
                      .build(consumer, prefixR(TinkerFluids.moltenNetherite, folder));
  }


  /* Helpers */

  /**
   * Adds a stonecutting recipe with automatic name and criteria
   * @param consumer  Recipe consumer
   * @param output    Recipe output
   * @param folder    Recipe folder path
   */
  private void addSearedStonecutter(@Nonnull Consumer<IFinishedRecipe> consumer, IItemProvider output, String folder) {
    SingleItemRecipeBuilder.stonecuttingRecipe(new CompoundIngredient(Arrays.asList(
      Ingredient.fromItems(TinkerSmeltery.searedStone),
      Ingredient.fromTag(TinkerTags.Items.SEARED_BRICKS))), output, 1)
                           .addCriterion("has_stone", hasItem(TinkerSmeltery.searedStone))
                           .addCriterion("has_bricks", hasItem(TinkerTags.Items.SEARED_BRICKS))
                           .build(consumer, wrap(output, folder, "_stonecutting"));
  }

  /**
   * Adds a basic ingot, nugget, block, ore melting recipe set
   * @param consumer  Recipe consumer
   * @param fluid     Fluid result
   * @param name      Resource name for tags
   * @param hasOre    If true, adds recipe for melting the ore
   * @param folder    Recipe folder
   */
  private void addMetalMelting(Consumer<IFinishedRecipe> consumer, Fluid fluid, String name, boolean hasOre, String folder) {
    String prefix = folder + name + "_from_";
    // block
    ITag<Item> block = getTag("forge", "storage_blocks/" + name);
    MeltingRecipeBuilder.melting(Ingredient.fromTag(block), fluid, MaterialValues.VALUE_Block)
                        .addCriterion("hasItem", hasItem(block))
                        .build(consumer, location(prefix + "block"));
    // ingot
    ITag<Item> ingot = getTag("forge", "ingots/" + name);
    MeltingRecipeBuilder.melting(Ingredient.fromTag(ingot), fluid, MaterialValues.VALUE_Ingot)
                        .addCriterion("hasItem", hasItem(Tags.Items.INGOTS_IRON))
                        .build(consumer, location(prefix + "ingot"));
    // nugget
    ITag<Item> nugget = getTag("forge", "nuggets/" + name);
    MeltingRecipeBuilder.melting(Ingredient.fromTag(nugget), fluid, MaterialValues.VALUE_Nugget)
                        .addCriterion("hasItem", hasItem(Tags.Items.NUGGETS_IRON))
                        .build(consumer, location(prefix + "nugget"));
    if (hasOre) {
      ITag<Item> ore = getTag("forge", "ores/" + name);
      MeltingRecipeBuilder.melting(Ingredient.fromTag(ore), fluid, MaterialValues.VALUE_Ingot)
                          .setOre()
                          .addCriterion("hasItem", hasItem(ore))
                          .build(consumer, location(prefix + "ore"));
    }
  }

  private void addEntityMeltingRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "smeltery/entity_melting/";

    // zombies give less blood, they lost a lot already
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ZOMBIE, EntityType.ZOMBIFIED_PIGLIN, EntityType.ZOGLIN, EntityType.ZOMBIE_HORSE),
                                       new FluidStack(TinkerFluids.blood.get(), MaterialValues.VALUE_SlimeBall / 10), 2)
                              .build(consumer, prefixR(EntityType.ZOMBIE, folder));

    // creepers are based on explosives, tnt is explosive, tnt is made from sand, sand melts into glass. therefore, creepers melt into glass
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.CREEPER),
                                       new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Glass / 16), 2)
                              .build(consumer, prefixR(EntityType.CREEPER, folder));

    // melt skeletons to get the milk out
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityIngredient.of(EntityTypeTags.SKELETONS), EntityIngredient.of(EntityType.SKELETON_HORSE)),
                                       new FluidStack(TinkerFluids.milk.get(), FluidAttributes.BUCKET_VOLUME / 10))
                              .build(consumer, location(folder + "skeletons"));

    // slimes melt into slime, shocker
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SLIME), new FluidStack(TinkerFluids.greenSlime.get(), MaterialValues.VALUE_SlimeBall / 10))
                              .build(consumer, prefixR(EntityType.SLIME, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.blueSlimeEntity.get()), new FluidStack(TinkerFluids.blueSlime.get(), MaterialValues.VALUE_SlimeBall / 10))
                              .build(consumer, prefixR(TinkerWorld.blueSlimeEntity, folder));

    // iron golems can be healed using an iron ingot 25 health
    // 4 * 9 gives 36, which is larger
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.IRON_GOLEM), new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.VALUE_Nugget), 4)
                              .build(consumer, prefixR(EntityType.IRON_GOLEM, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SNOW_GOLEM), new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 10))
                              .build(consumer, prefixR(EntityType.SNOW_GOLEM, folder));

    // "melt" blazes to get fuel
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.BLAZE), new FluidStack(TinkerFluids.moltenBlaze.get(), FluidAttributes.BUCKET_VOLUME / 50), 2)
                              .build(consumer, prefixR(EntityType.BLAZE, folder));

    // guardians are rock, seared stone is rock, don't think about it too hard
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN), new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.VALUE_Nugget), 4)
                              .build(consumer, prefixR(EntityType.GUARDIAN, folder));
    // silverfish also seem like rock, sorta?
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SILVERFISH), new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.VALUE_Nugget), 2)
                              .build(consumer, prefixR(EntityType.SILVERFISH, folder));

    // villagers melt into emerald, but they die quite quick
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.VILLAGER),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), MaterialValues.VALUE_Gem / 10), 5)
                              .build(consumer, prefixR(EntityType.VILLAGER, folder));
    // illagers are more resistant, they resist the villager culture afterall
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.PILLAGER, EntityType.ILLUSIONER, EntityType.EVOKER),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), MaterialValues.VALUE_Gem / 10), 2)
                              .build(consumer, location(folder + "illager"));
    // zombie villagers faintly recall being a villager once
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ZOMBIE_VILLAGER),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), MaterialValues.VALUE_Gem / 25), 3)
                              .build(consumer, prefixR(EntityType.ZOMBIE_VILLAGER, folder));

    // melt ender for the molten ender
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON),
                                       new FluidStack(TinkerFluids.moltenEnder.get(), MaterialValues.VALUE_Gem / 25), 2)
                              .build(consumer, location(folder + "ender"));
  }


  /* Casting */


  /**
   * Adds a casting recipe for a block
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param amount    Input amount
   * @param block     Output block
   * @param folder    Output folder
   */
  private void addBlockCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, int amount, IItemProvider block, String folder) {
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluid(new FluidStack(fluid.get(), amount))
                            .addCriterion("has_item", hasItem(block))
                            .build(consumer, prefix(block, folder));
  }

  /**
   * Adds a recipe to create the given seared block using molten glass on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param folder    Folder
   */
  private static void addSearedCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, String folder) {
    addSearedCastingRecipe(consumer, block, cast, MaterialValues.VALUE_Glass, folder);
  }

  /**
   * Adds a recipe to create the given seared slab block using molten glass on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param folder    Folder
   */
  private static void addSearedSlabCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, String folder) {
    addSearedCastingRecipe(consumer, block, cast, MaterialValues.VALUE_Glass / 2, folder);
  }

  /**
   * Adds a recipe to create the given seared block using molten glass on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param amount    Amount of fluid needed
   * @param folder    Folder
   */
  private static void addSearedCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, int amount, String folder) {
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluid(new FluidStack(TinkerFluids.moltenGlass.get(), amount))
                            .setCast(cast, true)
                            .addCriterion("has_item", hasItem(TinkerFluids.moltenGlass.asItem()))
                            .build(consumer, prefix(block, folder + "seared/"));
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param ingot     Ingot output
   * @param folder    Output folder
   */
  private void addIngotCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, IItemProvider ingot, String folder) {
    ItemCastingRecipeBuilder.tableRecipe(ingot)
                            .setFluid(new FluidStack(fluid.get(), MaterialValues.VALUE_Ingot))
                            .setCast(TinkerSmeltery.ingotCast, false)
                            .addCriterion("has_item", hasItem(ingot))
                            .build(consumer, prefix(ingot, folder));
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param gem       Gem output
   * @param folder    Output folder
   */
  private void addGemCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, IItemProvider gem, String folder) {
    ItemCastingRecipeBuilder.tableRecipe(gem)
                            .setFluid(new FluidStack(fluid.get(), MaterialValues.VALUE_Gem))
                            .setCast(TinkerSmeltery.gemCast, false)
                            .addCriterion("has_item", hasItem(gem))
                            .build(consumer, prefix(gem, folder));
  }

  /**
   * Adds a casting recipe using a nugget cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param nugget    Nugget output
   * @param folder    Output folder
   */
  private void addNuggetCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, IItemProvider nugget, String folder) {
    ItemCastingRecipeBuilder.tableRecipe(nugget)
                            .setFluid(new FluidStack(fluid.get(), MaterialValues.VALUE_Nugget))
                            .setCast(TinkerSmeltery.nuggetCast, false)
                            .addCriterion("has_item", hasItem(nugget))
                            .build(consumer, prefix(nugget, folder));
  }

  /**
   * Adds melting recipes for slime
   * @param consumer       Consumer
   * @param fluidSupplier  Fluid
   * @param type           Slime type
   * @param tag            Slime ball tag
   * @param folder         Output folder
   */
  private void addSlimeMeltingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluidSupplier, SlimeType type, ITag<Item> tag, String folder) {
    MeltingRecipeBuilder.melting(Ingredient.fromTag(tag), fluidSupplier.get(), MaterialValues.VALUE_SlimeBall)
                        .addCriterion("has_item", hasItem(tag))
                        .build(consumer, wrapR(fluidSupplier, folder, "_from_ball"));
    IItemProvider item = TinkerWorld.congealedSlime.get(type);
    MeltingRecipeBuilder.melting(Ingredient.fromItems(item), fluidSupplier.get(), MaterialValues.VALUE_SlimeBall * 4)
                        .addCriterion("has_item", hasItem(item))
                        .build(consumer, wrapR(fluidSupplier, folder, "_from_congealed"));
    item = TinkerWorld.slime.get(type);
    MeltingRecipeBuilder.melting(Ingredient.fromItems(item), fluidSupplier.get(), MaterialValues.VALUE_SlimeBall * 9)
                        .addCriterion("has_item", hasItem(item))
                        .build(consumer, wrapR(fluidSupplier, folder, "_from_block"));
  }

  /**
   * Adds slime related casting recipes
   * @param consumer    Recipe consumer
   * @param fluid       Fluid matching the slime type
   * @param slimeType   SlimeType for this recipe
   * @param folder      Output folder
   */
  private void addSlimeCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, SlimeType slimeType, String folder) {
    addBlockCastingRecipe(consumer, fluid, MaterialValues.VALUE_SlimeBall * 4, TinkerWorld.congealedSlime.get(slimeType), folder);
    ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.slime.get(slimeType))
                            .setFluid(new FluidStack(fluid.get(), MaterialValues.VALUE_SlimeBall * 5))
                            .setCast(TinkerWorld.congealedSlime.get(slimeType), true)
                            .addCriterion("has_item", hasItem(TinkerCommons.slimeball.get(slimeType)))
                            .build(consumer, location(folder +"slime/" + slimeType.getString()));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.slimeball.get(slimeType))
                            .setFluid(new FluidStack(fluid.get(), MaterialValues.VALUE_SlimeBall))
                            .addCriterion("has_item", hasItem(TinkerCommons.slimeball.get(slimeType)))
                            .build(consumer, location(folder + "slimeball/" + slimeType.getString()));
  }

  /**
   * Add recipes for a standard mineral
   * @param consumer  Recipe consumer
   * @param fluid     Fluid input
   * @param block     Block result
   * @param ingot     Ingot result
   * @param nugget    Nugget result
   * @param folder    Output folder
   */
  private void addMoltenMineralCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, @Nullable IItemProvider block, @Nullable IItemProvider ingot, @Nullable IItemProvider nugget, String folder) {
    if (block != null) {
      addBlockCastingRecipe(consumer, fluid, MaterialValues.VALUE_Block, block, folder);
    }
    if (ingot != null) {
      addIngotCastingRecipe(consumer, fluid, ingot, folder);
    }
    if (nugget != null) {
      addNuggetCastingRecipe(consumer, fluid, nugget, folder);
    }
  }

  /**
   * Adds recipe to create a cast
   * @param consumer  Recipe consumer
   * @param input     Item consumed to create cast
   * @param cast      Produced cast
   * @param folder    Output folder
   */
  private void addCastCastingRecipe(Consumer<IFinishedRecipe> consumer, INamedTag<Item> input, IItemProvider cast, String folder) {
    ItemCastingRecipeBuilder.tableRecipe(cast)
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot))
                            .setCast(input, true)
                            .setSwitchSlots()
                            .addCriterion("has_item", hasItem(input))
                            .build(consumer, location(folder + "casts/" + input.getName().getPath()));
  }
}
