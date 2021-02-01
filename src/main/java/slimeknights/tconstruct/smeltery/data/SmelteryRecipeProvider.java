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
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.common.registration.MetalItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.container.ContainerFillingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.StickySlimeBlock.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.tools.data.MaterialIds;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
    // ladder from bricks
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

    // tanks
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

    // fluid transfer
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedFaucet.get(), 2)
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("# #")
                       .patternLine(" # ")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location("smeltery/faucet"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedChannel.get(), 3)
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("# #")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location("smeltery/channel"));

    // casting
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

    // peripherals
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedDrain)
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('C', TinkerMaterials.copper.getIngotTag())
                       .patternLine("# #")
                       .patternLine("C C")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location("smeltery/drain"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedChute)
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('C', TinkerMaterials.copper.getIngotTag())
                       .patternLine("#C#")
                       .patternLine("   ")
                       .patternLine("#C#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location("smeltery/chute"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedDuct)
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('C', TinkerMaterials.cobalt.getIngotTag())
                       .patternLine("# #")
                       .patternLine("C C")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerMaterials.cobalt.getIngotTag()))
                       .build(consumer, location("smeltery/duct"));

    // controllers
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedMelter)
                       .key('G', TinkerSmeltery.searedTank.get(TankType.GAUGE))
                       .key('B', TinkerSmeltery.searedBrick)
                       .patternLine("BGB")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, prefix(TinkerSmeltery.searedMelter, "smeltery/"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedHeater)
                       .key('B', TinkerSmeltery.searedBrick)
                       .patternLine("BBB")
                       .patternLine("B B")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, prefix(TinkerSmeltery.searedHeater, "smeltery/"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.copperCan, 3)
                       .key('c', TinkerMaterials.copper.getIngotTag())
                       .patternLine("c c")
                       .patternLine(" c ")
                       .addCriterion("has_item", hasItem(TinkerMaterials.copper.getIngotTag()))
                       .build(consumer, prefix(TinkerSmeltery.copperCan, "smeltery/"));

    // sand casts
    ShapelessRecipeBuilder.shapelessRecipe(TinkerSmeltery.blankCast.getSand(), 4)
                          .addIngredient(Tags.Items.SAND_COLORLESS)
                          .addCriterion("has_casting", hasItem(TinkerSmeltery.castingTable))
                          .build(consumer, location("smeltery/sand_cast"));
    ShapelessRecipeBuilder.shapelessRecipe(TinkerSmeltery.blankCast.getRedSand(), 4)
                          .addIngredient(Tags.Items.SAND_RED)
                          .addCriterion("has_casting", hasItem(TinkerSmeltery.castingTable))
                          .build(consumer, location("smeltery/red_sand_cast"));

    // pick up sand casts from the table
    MoldingRecipeBuilder.moldingTable(TinkerSmeltery.blankCast.getSand())
                        .setMaterial(TinkerTags.Items.SAND_CASTS)
                        .build(consumer, location("smeltery/sand_cast_pickup"));
    MoldingRecipeBuilder.moldingTable(TinkerSmeltery.blankCast.getRedSand())
                        .setMaterial(TinkerTags.Items.RED_SAND_CASTS)
                        .build(consumer, location("smeltery/red_sand_cast_pickup"));
  }


  private void addCastingRecipes(Consumer<IFinishedRecipe> consumer) {
    // Pure Fluid Recipes
    String folder = "casting/";

    // smeltery controller
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.smelteryController)
                            .setCast(TinkerSmeltery.searedHeater, true)
                            .setFluid(new FluidStack(TinkerFluids.moltenCopper.get(), MaterialValues.VALUE_Ingot * 4))
                            .build(consumer, prefix(TinkerSmeltery.smelteryController, folder));

    // container filling
    ContainerFillingRecipeBuilder.tableRecipe(Items.BUCKET, FluidAttributes.BUCKET_VOLUME)
                                 .build(consumer, location(folder + "filling/bucket"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.copperCan, MaterialValues.VALUE_Ingot)
                                 .build(consumer, location(folder + "filling/copper_can"));
    // Slime
    this.addSlimeCastingRecipe(consumer, TinkerFluids.blood, SlimeType.BLOOD, folder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.greenSlime, SlimeType.GREEN, folder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.blueSlime, SlimeType.BLUE, folder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.purpleSlime, SlimeType.PURPLE, folder);
    // magma cream
    addBlockCastingRecipe(consumer, TinkerFluids.magmaCream, MaterialValues.VALUE_SlimeBall * 4, Blocks.MAGMA_BLOCK, folder);
    ItemCastingRecipeBuilder.tableRecipe(Items.MAGMA_CREAM)
                            .setFluid(new FluidStack(TinkerFluids.magmaCream.get(), MaterialValues.VALUE_SlimeBall))
                            .build(consumer, location(folder + "slimeball/magma_cream"));

    // seared blocks
    this.addBlockCastingRecipe(consumer, TinkerFluids.searedStone, MaterialValues.VALUE_BrickBlock, TinkerSmeltery.searedStone, folder);
    this.addIngotCastingRecipe(consumer, TinkerFluids.searedStone, TinkerSmeltery.searedBrick, folder);
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedGlass)
                            .setFluid(new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.VALUE_Ingot * 4))
                            .setCast(Tags.Items.GLASS_COLORLESS, true)
                            .build(consumer, prefix(TinkerSmeltery.searedGlass, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.searedGlassPane)
                            .setFluid(new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.VALUE_BrickBlock * 6 / 16))
                            .setCast(Tags.Items.GLASS_PANES_COLORLESS, true)
                            .build(consumer, prefix(TinkerSmeltery.searedGlassPane, folder));

    // glass
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenGlass, MaterialValues.VALUE_Glass, TinkerCommons.clearGlass, folder);
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.clearGlassPane)
                            .setFluid(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Pane))
                            .build(consumer, prefix(TinkerCommons.clearGlassPane, folder));
    // soul glass
    this.addBlockCastingRecipe(consumer, TinkerFluids.liquidSoul, MaterialValues.VALUE_Glass, TinkerCommons.soulGlass, folder);
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.soulGlassPane)
                            .setFluid(new FluidStack(TinkerFluids.liquidSoul.get(), MaterialValues.VALUE_Pane))
                            .build(consumer, prefix(TinkerCommons.soulGlassPane, folder));

    // clay
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenClay, MaterialValues.VALUE_SlimeBall * 4, Blocks.TERRACOTTA, folder);
    this.addIngotCastingRecipe(consumer, TinkerFluids.moltenClay, MaterialValues.VALUE_SlimeBall, Items.BRICK, folder);

    // emeralds
    this.addGemCastingRecipe(consumer, TinkerFluids.moltenEmerald, Items.EMERALD, folder);
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenEmerald, MaterialValues.VALUE_GemBlock, Items.EMERALD_BLOCK, folder);

    // ender pearls
    this.addGemCastingRecipe(consumer, TinkerFluids.moltenEnder, Items.ENDER_PEARL, folder);

    // obsidian
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenObsidian, MaterialValues.VALUE_Glass, Items.OBSIDIAN, folder);

    // Molten objects with Bucket, Block, Ingot, and Nugget forms with standard values
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenIron,      Items.IRON_BLOCK,       Items.IRON_INGOT,      Items.IRON_NUGGET,               folder);
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenGold,      Items.GOLD_BLOCK,       Items.GOLD_INGOT,      Items.GOLD_NUGGET,               folder);
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenNetherite, Blocks.NETHERITE_BLOCK, Items.NETHERITE_INGOT, TinkerMaterials.netheriteNugget, folder);
    // ores
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenCopper, TinkerMaterials.copper, folder);
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenCobalt, TinkerMaterials.cobalt, folder);
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenArdite, TinkerMaterials.ardite, folder);
    // tier 3 alloys
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenSlimesteel,    TinkerMaterials.slimesteel,    folder);
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenTinkersBronze, TinkerMaterials.tinkersBronze, folder);
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenRoseGold,      TinkerMaterials.roseGold,      folder);
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenPigIron,       TinkerMaterials.pigiron,       folder);
    // tier 4 alloys
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenManyullyn,   TinkerMaterials.manyullyn,   folder);
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenHepatizon,   TinkerMaterials.hepatizon,   folder);
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenQueensSlime, TinkerMaterials.queensSlime, folder);
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenSoulsteel,   TinkerMaterials.soulsteel,   folder);
    // tier 5 alloys
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenKnightslime, TinkerMaterials.knightslime, folder);

    // compat
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      this.addMetalOptionalCasting(consumer, compat.getFluid(), compat.getName(), folder);
    }

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
                            .build(consumer, prefix(TinkerCommons.lavawood, folder));

    // Cast recipes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.blankCast)
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot))
                            .setSwitchSlots()
                            .build(consumer, location(folder + "casts/blank"));

    this.addCastCastingRecipe(consumer, Tags.Items.INGOTS, TinkerSmeltery.ingotCast, folder);
    this.addCastCastingRecipe(consumer, Tags.Items.NUGGETS, TinkerSmeltery.nuggetCast, folder);
    this.addCastCastingRecipe(consumer, Tags.Items.GEMS, TinkerSmeltery.gemCast, folder);

    // composite casting
    folder += "composite/";
    CompositeCastingRecipeBuilder.table(MaterialIds.stone, MaterialIds.searedStone)
                                 .setFluid(new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.VALUE_SlimeBall / 2))
                                 .build(consumer, location(folder + "seared_stone"));
    CompositeCastingRecipeBuilder.table(MaterialIds.wood, MaterialIds.slimewood)
                                 .setFluid(new FluidStack(TinkerFluids.greenSlime.get(), MaterialValues.VALUE_SlimeBall))
                                 .build(consumer, location(folder + "slimewood"));
    CompositeCastingRecipeBuilder.table(MaterialIds.wood, MaterialIds.nahuatl)
                                 .setFluid(new FluidStack(TinkerFluids.moltenObsidian.get(), MaterialValues.VALUE_Pane))
                                 .build(consumer, location(folder + "nahuatl"));
  }

  private void addMeltingRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "melting/";

    // water from ice
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ICE), Fluids.WATER, FluidAttributes.BUCKET_VOLUME, 1.0f)
                        .build(consumer, location(folder + "water_from_ice"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.PACKED_ICE), Fluids.WATER, FluidAttributes.BUCKET_VOLUME * 9, 3.0f)
                        .build(consumer, location(folder + "water_from_packed_ice"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.BLUE_ICE), Fluids.WATER, FluidAttributes.BUCKET_VOLUME * 81, 9.0f)
                        .build(consumer, location(folder + "water_from_blue_ice"));
    // water from snow
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SNOWBALL), Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 8, 0.5f)
                        .build(consumer, location(folder + "water_from_snowball"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SNOW_BLOCK), Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 2, 0.75f)
                        .build(consumer, location(folder + "water_from_snow_block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SNOW), Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 8, 0.5f)
                        .build(consumer, location(folder + "water_from_snow_layer"));

    // ores
    addMetalMelting(consumer, TinkerFluids.moltenIron.get(),   "iron",   true, folder, false);
    addMetalMelting(consumer, TinkerFluids.moltenGold.get(),   "gold",   true, folder, false);
    addMetalMelting(consumer, TinkerFluids.moltenCopper.get(), "copper", true, folder, false);
    addMetalMelting(consumer, TinkerFluids.moltenCobalt.get(), "cobalt", true, folder, false);
    addMetalMelting(consumer, TinkerFluids.moltenArdite.get(), "ardite", true, folder, false);
    ITag<Item> ore = Tags.Items.ORES_NETHERITE_SCRAP;
    MeltingRecipeBuilder.melting(Ingredient.fromTag(ore), TinkerFluids.moltenDebris.get(), MaterialValues.VALUE_Ingot, 2.0f)
                        .setOre()
                        .build(consumer, location(folder + "molten_debris_from_ore"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_SCRAP), TinkerFluids.moltenDebris.get(), MaterialValues.VALUE_Ingot, 1.0f)
                        .build(consumer, location(folder + "molten_debris_from_scrap"));
    // tier 3
    addMetalMelting(consumer, TinkerFluids.moltenSlimesteel.get(),    "slimesteel",     false, folder, false);
    addMetalMelting(consumer, TinkerFluids.moltenTinkersBronze.get(), "tinkers_bronze", false, folder, false);
    addMetalMelting(consumer, TinkerFluids.moltenRoseGold.get(),      "rose_gold",      false, folder, false);
    addMetalMelting(consumer, TinkerFluids.moltenPigIron.get(),       "pigiron",       false, folder, false);
    // tier 4
    addMetalMelting(consumer, TinkerFluids.moltenManyullyn.get(),   "manyullyn",    false, folder, false);
    addMetalMelting(consumer, TinkerFluids.moltenHepatizon.get(),   "hepatizon",    false, folder, false);
    addMetalMelting(consumer, TinkerFluids.moltenQueensSlime.get(), "queens_slime", false, folder, false);
    addMetalMelting(consumer, TinkerFluids.moltenSoulsteel.get(),   "soulsteel",    false, folder, false);
    addMetalMelting(consumer, TinkerFluids.moltenNetherite.get(),   "netherite",    false, folder, false);
    // tier 5
    addMetalMelting(consumer, TinkerFluids.moltenKnightslime.get(), "knightslime", false, folder, false);

    // compat
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      this.addMetalMelting(consumer, compat.getFluid(), compat.getName(), compat.isOre(), folder, true);
    }

    // blood
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ROTTEN_FLESH), TinkerFluids.blood.get(), MaterialValues.VALUE_SlimeBall / 5, 1.0f)
                        .build(consumer, location("blood_from_flesh"));

    // glass
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.SAND), TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Glass, 1.5f)
                        .build(consumer, location(folder + "glass_from_sand"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GLASS), TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Glass, 1.0f)
                        .build(consumer, location(folder + "glass_from_block"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GLASS_PANES), TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Pane, 0.5f)
                        .build(consumer, location(folder + "glass_from_pane"));

    // liquid soul
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.SOUL_SAND, Blocks.SOUL_SOIL), TinkerFluids.liquidSoul.get(), MaterialValues.VALUE_Glass, 1.5f)
                        .build(consumer, location(folder + "soul_from_sand"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerCommons.soulGlass), TinkerFluids.liquidSoul.get(), MaterialValues.VALUE_Glass, 1.0f)
                        .build(consumer, location(folder + "soul_from_glass"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerCommons.soulGlassPane), TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Pane, 0.5f)
                        .build(consumer, location(folder + "soul_from_pane"));

    // melt extra sand casts back
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.blankCast.getSand(), TinkerSmeltery.blankCast.getRedSand()),
      TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Pane, 0.75f)
                        .build(consumer, location(folder + "glass_from_sand_cast"));

    // clay
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.CLAY), TinkerFluids.moltenClay.get(), MaterialValues.VALUE_SlimeBall * 4, 1.0f)
                        .build(consumer, location(folder + "clay_from_block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.CLAY_BALL), TinkerFluids.moltenClay.get(), MaterialValues.VALUE_SlimeBall, 0.5f)
                        .build(consumer, location(folder + "clay_from_ball"));
    // terracotta
    Ingredient terracottaBlock = Ingredient.fromItems(
      Blocks.TERRACOTTA, Blocks.BRICKS, Blocks.BRICK_WALL, Blocks.BRICK_STAIRS,
      Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA,
      Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA,
      Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA,
      Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA,
      Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
      Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA,
      Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA,
      Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA,
      TinkerCommons.driedClay, TinkerCommons.driedClayBricks);
    MeltingRecipeBuilder.melting(terracottaBlock, TinkerFluids.moltenClay.get(), MaterialValues.VALUE_SlimeBall * 4, 2.0f)
                        .build(consumer, location(folder + "clay_from_terracotta"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerCommons.driedClay.getStairs(), TinkerCommons.driedClayBricks.getStairs()), TinkerFluids.moltenClay.get(), MaterialValues.VALUE_SlimeBall, 2.0f)
                        .build(consumer, location(folder + "clay_from_stairs"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.BRICK, TinkerCommons.driedBrick), TinkerFluids.moltenClay.get(), MaterialValues.VALUE_SlimeBall, 1.0f)
                        .build(consumer, location(folder + "clay_from_brick"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.BRICK_SLAB, TinkerCommons.driedClay.getSlab(), TinkerCommons.driedClayBricks.getSlab()),
                                 TinkerFluids.moltenClay.get(), MaterialValues.VALUE_SlimeBall * 2, 1.5f)
                        .build(consumer, location(folder + "clay_from_brick_slab"));

    // seared stone
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.SEARED_BLOCKS), TinkerFluids.searedStone.get(), MaterialValues.VALUE_BrickBlock, 2.0f)
                        .build(consumer, location(folder + "seared_stone_from_block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedBrick), TinkerFluids.searedStone.get(), MaterialValues.VALUE_Ingot, 1.0f)
                        .build(consumer, location(folder + "seared_stone_from_brick"));
    // double efficiency when using smeltery for grout
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.grout), TinkerFluids.searedStone.get(), MaterialValues.VALUE_Ingot * 2, 1.5f)
                        .build(consumer, location(folder + "seared_stone_from_grout"));

    // slime
    addSlimeMeltingRecipe(consumer, TinkerFluids.greenSlime,  SlimeType.GREEN,  TinkerTags.Items.GREEN_SLIMEBALL,  folder);
    addSlimeMeltingRecipe(consumer, TinkerFluids.blueSlime,   SlimeType.BLUE,   TinkerTags.Items.BLUE_SLIMEBALL,   folder);
    addSlimeMeltingRecipe(consumer, TinkerFluids.purpleSlime, SlimeType.PURPLE, TinkerTags.Items.PURPLE_SLIMEBALL, folder);
    // magma cream
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.MAGMA_CREAM), TinkerFluids.magmaCream.get(), MaterialValues.VALUE_SlimeBall, 1.0f)
                        .build(consumer, wrapR(TinkerFluids.magmaCream, folder, "_from_ball"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.MAGMA_BLOCK), TinkerFluids.magmaCream.get(), MaterialValues.VALUE_SlimeBall * 4, 3.0f)
                        .build(consumer, wrapR(TinkerFluids.magmaCream, folder, "_from_block"));

    // obsidian
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.OBSIDIAN), TinkerFluids.moltenObsidian.get(), MaterialValues.VALUE_Glass, 2.0f)
                        .build(consumer, location(folder + "obsidian"));

    // emerald
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.ORES_EMERALD), TinkerFluids.moltenEmerald.get(), MaterialValues.VALUE_Gem, 1.5f)
                        .build(consumer, prefix(Items.EMERALD_ORE, folder));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), TinkerFluids.moltenEmerald.get(), MaterialValues.VALUE_Gem, 1.0f)
                        .build(consumer, prefix(Items.EMERALD, folder));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_EMERALD), TinkerFluids.moltenEmerald.get(), MaterialValues.VALUE_GemBlock, 3.0f)
                        .build(consumer, prefix(Items.EMERALD_BLOCK, folder));

    // special recipes
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_HORSE_ARMOR), TinkerFluids.moltenIron.get(), MaterialValues.VALUE_Ingot * 7)
                        .build(consumer, prefix(Items.IRON_HORSE_ARMOR, folder));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_HORSE_ARMOR), TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot * 7)
                        .build(consumer, prefix(Items.GOLDEN_HORSE_ARMOR, folder));
    // cast to gold
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.GOLD_CASTS), TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot)
                        .build(consumer, location(folder + "gold_from_cast"));

    // fuels
    MeltingFuelBuilder.fuel(new FluidStack(Fluids.LAVA, 50), 100)
                      .build(consumer, location(folder + "fuel/lava"));
    MeltingFuelBuilder.fuel(new FluidStack(TinkerFluids.moltenBlaze.get(), 50), 150)
                      .build(consumer, location(folder + "fuel/blaze"));
  }

  private void addAlloyRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "smeltery/alloys/";

    // alloy recipes are in terms of ingots

    // tier 3

    // slimesteel: 1 iron + 1 blueslime + 1 seared brick = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenSlimesteel.get(), MaterialValues.VALUE_Ingot * 2)
                      .addInput(TinkerFluids.moltenIron.get(), MaterialValues.VALUE_Ingot)
                      .addInput(TinkerFluids.blueSlime.get(), MaterialValues.VALUE_SlimeBall)
                      .addInput(TinkerFluids.searedStone.get(), MaterialValues.VALUE_Ingot)
                      .build(consumer, prefixR(TinkerFluids.moltenSlimesteel, folder));

    // tinker's bronze: 3 copper + 1 silicon (1/4 glass) = 4
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenTinkersBronze.get(), MaterialValues.VALUE_Ingot * 4)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.VALUE_Ingot * 3)
                      .addInput(TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Pane)
                      .build(consumer, prefixR(TinkerFluids.moltenTinkersBronze, folder));

    // rose gold: 3 copper + 1 gold = 4
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenRoseGold.get(), MaterialValues.VALUE_Ingot * 4)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.VALUE_Ingot * 3)
                      .addInput(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot)
                      .build(consumer, prefixR(TinkerFluids.moltenRoseGold, folder));
    // pig iron: 1 iron + 1 blood + 1 clay = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenPigIron.get(), MaterialValues.VALUE_Ingot * 2)
                      .addInput(TinkerFluids.moltenIron.get(), MaterialValues.VALUE_Ingot)
                      .addInput(TinkerFluids.blood.get(), MaterialValues.VALUE_SlimeBall)
                      .addInput(TinkerFluids.moltenClay.get(), MaterialValues.VALUE_SlimeBall)
                      .build(consumer, prefixR(TinkerFluids.moltenPigIron, folder));

    // tier 4

    // queens slime: 1 cobalt + 1 gold + 1 magma cream
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenQueensSlime.get(), MaterialValues.VALUE_Ingot * 2)
                      .addInput(TinkerFluids.moltenCobalt.get(), MaterialValues.VALUE_Ingot)
                      .addInput(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot)
                      .addInput(TinkerFluids.magmaCream.get(), MaterialValues.VALUE_SlimeBall)
                      .build(consumer, prefixR(TinkerFluids.moltenQueensSlime, folder));

    // manyullyn: 3 cobalt + 1 debris = 4
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenManyullyn.get(), MaterialValues.VALUE_Ingot * 4)
                      .addInput(TinkerFluids.moltenCobalt.get(), MaterialValues.VALUE_Ingot * 3)
                      .addInput(TinkerFluids.moltenDebris.get(), MaterialValues.VALUE_Ingot)
                      .build(consumer, prefixR(TinkerFluids.moltenManyullyn, folder));

    // heptazion: 2 copper + 1 cobalt + 1/4 obsidian = 4
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenHepatizon.get(), MaterialValues.VALUE_Ingot * 4)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.VALUE_Ingot * 2)
                      .addInput(TinkerFluids.moltenCobalt.get(), MaterialValues.VALUE_Ingot)
                      .addInput(TinkerFluids.moltenObsidian.get(), MaterialValues.VALUE_Pane)
                      .build(consumer, prefixR(TinkerFluids.moltenHepatizon, folder));

    // soulsteel: 2 iron + 1 cobalt + 1 soul = 4
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenSoulsteel.get(), MaterialValues.VALUE_Ingot * 4)
                      .addInput(TinkerFluids.moltenIron.get(), MaterialValues.VALUE_Ingot * 2)
                      .addInput(TinkerFluids.moltenCobalt.get(), MaterialValues.VALUE_Ingot)
                      .addInput(TinkerFluids.liquidSoul.get(), MaterialValues.VALUE_Pane)
                      .build(consumer, prefixR(TinkerFluids.moltenSoulsteel, folder));

    // netherrite: 4 debris + 4 gold = 1 (why is this so dense vanilla?)
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenNetherite.get(), MaterialValues.VALUE_Nugget)
                      .addInput(TinkerFluids.moltenDebris.get(), MaterialValues.VALUE_Nugget * 4)
                      .addInput(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Nugget * 4)
                      .build(consumer, prefixR(TinkerFluids.moltenNetherite, folder));


    // tier 3 compat
    Consumer<IFinishedRecipe> wrapped;

    // bronze
    wrapped = withCondition(consumer, tagCondition("ingots/bronze"), tagCondition("ingots/tin"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenBronze.get(), MaterialValues.VALUE_Ingot * 4)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.VALUE_Ingot * 3)
                      .addInput(TinkerFluids.moltenTin.get(), MaterialValues.VALUE_Ingot)
                      .build(wrapped, prefixR(TinkerFluids.moltenBronze, folder));

    // brass
    wrapped = withCondition(consumer, tagCondition("ingots/brass"), tagCondition("ingots/zinc"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenBrass.get(), MaterialValues.VALUE_Ingot * 4)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.VALUE_Ingot * 3)
                      .addInput(TinkerFluids.moltenZinc.get(), MaterialValues.VALUE_Ingot)
                      .build(wrapped, prefixR(TinkerFluids.moltenBrass, folder));

    // electrum
    wrapped = withCondition(consumer, tagCondition("ingots/electrum"), tagCondition("ingots/silver"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenElectrum.get(), MaterialValues.VALUE_Ingot * 2)
                      .addInput(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot)
                      .addInput(TinkerFluids.moltenSilver.get(), MaterialValues.VALUE_Ingot)
                      .build(wrapped, prefixR(TinkerFluids.moltenElectrum, folder));

    // invar
    wrapped = withCondition(consumer, tagCondition("ingots/invar"), tagCondition("ingots/nickel"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenInvar.get(), MaterialValues.VALUE_Ingot * 3)
                      .addInput(TinkerFluids.moltenIron.get(), MaterialValues.VALUE_Ingot * 2)
                      .addInput(TinkerFluids.moltenNickel.get(), MaterialValues.VALUE_Ingot)
                      .build(wrapped, prefixR(TinkerFluids.moltenInvar, folder));

    // constantan
    wrapped = withCondition(consumer, tagCondition("ingots/constantan"), tagCondition("ingots/nickel"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenConstantan.get(), MaterialValues.VALUE_Ingot * 2)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.VALUE_Ingot)
                      .addInput(TinkerFluids.moltenNickel.get(), MaterialValues.VALUE_Ingot)
                      .build(wrapped, prefixR(TinkerFluids.moltenConstantan, folder));
  }

  private void addEntityMeltingRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "smeltery/entity_melting/";

    // zombies give less blood, they lost a lot already
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIFIED_PIGLIN, EntityType.ZOGLIN, EntityType.ZOMBIE_HORSE),
                                       new FluidStack(TinkerFluids.blood.get(), MaterialValues.VALUE_SlimeBall / 10), 2)
                              .build(consumer, prefixR(EntityType.ZOMBIE, folder));

    // creepers are based on explosives, tnt is explosive, tnt is made from sand, sand melts into glass. therefore, creepers melt into glass
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.CREEPER),
                                       new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Glass / 16), 2)
                              .build(consumer, prefixR(EntityType.CREEPER, folder));

    // melt skeletons to get the milk out
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityIngredient.of(EntityTypeTags.SKELETONS), EntityIngredient.of(EntityType.SKELETON_HORSE)),
                                       new FluidStack(ForgeMod.MILK.get(), FluidAttributes.BUCKET_VOLUME / 10))
                              .build(consumer, location(folder + "skeletons"));

    // slimes melt into slime, shocker
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SLIME), new FluidStack(TinkerFluids.greenSlime.get(), MaterialValues.VALUE_SlimeBall / 10))
                              .build(consumer, prefixR(EntityType.SLIME, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.blueSlimeEntity.get()), new FluidStack(TinkerFluids.blueSlime.get(), MaterialValues.VALUE_SlimeBall / 10))
                              .build(consumer, prefixR(TinkerWorld.blueSlimeEntity, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.MAGMA_CUBE), new FluidStack(TinkerFluids.magmaCream.get(), MaterialValues.VALUE_SlimeBall / 10))
                              .build(consumer, prefixR(EntityType.MAGMA_CUBE, folder));

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
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.VILLAGER, EntityType.WANDERING_TRADER),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), MaterialValues.VALUE_Gem / 10), 5)
                              .build(consumer, prefixR(EntityType.VILLAGER, folder));
    // illagers are more resistant, they resist the villager culture afterall
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.EVOKER, EntityType.ILLUSIONER, EntityType.PILLAGER, EntityType.VINDICATOR),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), MaterialValues.VALUE_Gem / 10), 2)
                              .build(consumer, location(folder + "illager"));
    // zombie villagers and witches faintly recall being a villager once
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ZOMBIE_VILLAGER, EntityType.WITCH),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), MaterialValues.VALUE_Gem / 25), 3)
                              .build(consumer, prefixR(EntityType.ZOMBIE_VILLAGER, folder));

    // melt ender for the molten ender
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON),
                                       new FluidStack(TinkerFluids.moltenEnder.get(), MaterialValues.VALUE_Gem / 25), 2)
                              .build(consumer, location(folder + "ender"));

    // if you can get him to stay, wither is a source of free liquid soul
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.WITHER),
                                       new FluidStack(TinkerFluids.liquidSoul.get(), MaterialValues.VALUE_Glass / 16), 2)
                              .build(consumer, prefixR(EntityType.WITHER, folder));
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
   * Base logic for {@link #addMetalMelting(Consumer, Fluid, String, boolean, String, boolean)}
   * @param consumer    Recipe consumer
   * @param fluid       Fluid to melt into
   * @param amount      Amount to melt into
   * @param isOre       If true, this is an ore recipe
   * @param tagName     Input tag
   * @param factor      Melting factor
   * @param recipePath  Recipe output name
   * @param isOptional  If true, recipe is optional
   */
  private static void addMetalBase(Consumer<IFinishedRecipe> consumer, Fluid fluid, int amount, boolean isOre, String tagName, float factor, String recipePath, boolean isOptional) {
    Consumer<IFinishedRecipe> wrapped = isOptional ? withCondition(consumer, tagCondition(tagName)) : consumer;
    MeltingRecipeBuilder builder = MeltingRecipeBuilder.melting(Ingredient.fromTag(getTag("forge", tagName)), fluid, amount, factor);
    if (isOre) {
      builder.setOre();
    }
    builder.build(wrapped, location(recipePath));
  }

  /**
   * Adds a basic ingot, nugget, block, ore melting recipe set
   * @param consumer  Recipe consumer
   * @param fluid     Fluid result
   * @param name      Resource name for tags
   * @param hasOre    If true, adds recipe for melting the ore
   * @param folder    Recipe folder
   */
  private void addMetalMelting(Consumer<IFinishedRecipe> consumer, Fluid fluid, String name, boolean hasOre, String folder, boolean isOptional) {
    String prefix = folder + name + "_from_";
    addMetalBase(consumer, fluid, MaterialValues.VALUE_Block,  false, "storage_blocks/" + name, 3.0f, prefix + "block",  isOptional);
    addMetalBase(consumer, fluid, MaterialValues.VALUE_Ingot,  false, "ingots/" + name,         1.0f, prefix + "ingot",  isOptional);
    addMetalBase(consumer, fluid, MaterialValues.VALUE_Nugget, false, "nuggets/" + name,        1/3f, prefix + "nugget", isOptional);
    if (hasOre) {
      addMetalBase(consumer, fluid, MaterialValues.VALUE_Ingot, true, "ores/" + name, 1.5f, prefix + "ore", isOptional);
    }
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
                            .build(consumer, prefix(block, folder));
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param folder    Folder
   */
  private static void addSearedCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, String folder) {
    addSearedCastingRecipe(consumer, block, cast, MaterialValues.VALUE_SlimeBall * 2, folder);
  }

  /**
   * Adds a recipe to create the given seared slab block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param folder    Folder
   */
  private static void addSearedSlabCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, String folder) {
    addSearedCastingRecipe(consumer, block, cast, MaterialValues.VALUE_SlimeBall, folder);
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param amount    Amount of fluid needed
   * @param folder    Folder
   */
  private static void addSearedCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, int amount, String folder) {
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluid(new FluidStack(TinkerFluids.moltenClay.get(), amount))
                            .setCast(cast, true)
                            .build(consumer, prefix(block, folder + "seared/"));
  }

  /**
   * Adds a recipe for casting using a cast
   * @param consumer  Recipe consumer
   * @param fluid     Recipe fluid
   * @param amount    Fluid amount
   * @param cast      Cast used
   * @param output    Recipe output
   * @param folder    Recipe folder
   */
  private void addCastingWithCastRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, int amount, CastItemObject cast, IItemProvider output, String folder) {
    FluidStack fluidStack = new FluidStack(fluid.get(), amount);
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluid(fluidStack)
                            .setCast(cast, false)
                            .build(consumer, wrap(output, folder, "_gold_cast"));
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluid(fluidStack)
                            .setCast(cast.getSingleUseTag(), true)
                            .build(consumer, wrap(output, folder, "_sand_cast"));
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param amount    Recipe amount
   * @param ingot     Ingot output
   * @param folder    Output folder
   */
  private void addIngotCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, int amount, IItemProvider ingot, String folder) {
    addCastingWithCastRecipe(consumer, fluid, amount, TinkerSmeltery.ingotCast, ingot, folder);
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param ingot     Ingot output
   * @param folder    Output folder
   */
  private void addIngotCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, IItemProvider ingot, String folder) {
    addIngotCastingRecipe(consumer, fluid, MaterialValues.VALUE_Ingot, ingot, folder);
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param gem       Gem output
   * @param folder    Output folder
   */
  private void addGemCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, IItemProvider gem, String folder) {
    addCastingWithCastRecipe(consumer, fluid, MaterialValues.VALUE_Gem, TinkerSmeltery.gemCast, gem, folder);
  }

  /**
   * Adds a casting recipe using a nugget cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param nugget    Nugget output
   * @param folder    Output folder
   */
  private void addNuggetCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, IItemProvider nugget, String folder) {
    addCastingWithCastRecipe(consumer, fluid, MaterialValues.VALUE_Nugget, TinkerSmeltery.nuggetCast, nugget, folder);
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
    MeltingRecipeBuilder.melting(Ingredient.fromTag(tag), fluidSupplier.get(), MaterialValues.VALUE_SlimeBall, 1.0f)
                        .build(consumer, wrapR(fluidSupplier, folder, "_from_ball"));
    IItemProvider item = TinkerWorld.congealedSlime.get(type);
    MeltingRecipeBuilder.melting(Ingredient.fromItems(item), fluidSupplier.get(), MaterialValues.VALUE_SlimeBall * 4, 2.0f)
                        .build(consumer, wrapR(fluidSupplier, folder, "_from_congealed"));
    item = TinkerWorld.slime.get(type);
    MeltingRecipeBuilder.melting(Ingredient.fromItems(item), fluidSupplier.get(), MaterialValues.VALUE_SlimeBall * 9, 3.0f)
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
                            .build(consumer, location(folder +"slime/" + slimeType.getString()));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.slimeball.get(slimeType))
                            .setFluid(new FluidStack(fluid.get(), MaterialValues.VALUE_SlimeBall))
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
  private void addMetalCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, @Nullable IItemProvider block, @Nullable IItemProvider ingot, @Nullable IItemProvider nugget, String folder) {
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
   * Add recipes for a standard mineral
   * @param consumer  Recipe consumer
   * @param fluid     Fluid input
   * @param metal     Metal object
   * @param folder    Output folder
   */
  private void addMetalCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, MetalItemObject metal, String folder) {
    addMetalCastingRecipe(consumer, fluid, metal.get(), metal.getIngot(), metal.getNugget(), folder);
  }


  /** Adds a recipe for casting using a cast */
  private void addOptionalCastingWithCast(Consumer<IFinishedRecipe> consumer, Fluid fluid, int amount, CastItemObject cast, String tagPrefix, String recipeName, String name, String folder) {
    String tagName = tagPrefix + "/" + name;
    ITag<Item> tag = getTag("forge", tagName);
    Consumer<IFinishedRecipe> wrapped = withCondition(consumer, tagCondition(tagName));
    ItemCastingRecipeBuilder.tableRecipe(tag)
                            .setFluid(new FluidStack(fluid, amount))
                            .setCast(cast, false)
                            .build(wrapped, location(folder + name + "/" + recipeName + "_gold_cast"));
    ItemCastingRecipeBuilder.tableRecipe(tag)
                            .setFluid(new FluidStack(fluid, amount))
                            .setCast(cast.getSingleUseTag(), true)
                            .build(wrapped, location(folder + name + "/" + recipeName + "_sand_cast"));
  }

  /**
   * Add recipes for a standard mineral
   * @param consumer  Recipe consumer
   * @param fluid     Fluid input
   * @param name      Name of ore
   * @param folder    Output folder
   */
  private void addMetalOptionalCasting(Consumer<IFinishedRecipe> consumer, Fluid fluid, String name, String folder) {
    // nugget and ingot
    addOptionalCastingWithCast(consumer, fluid, MaterialValues.VALUE_Nugget, TinkerSmeltery.nuggetCast, "nuggets", "nugget", name, folder);
    addOptionalCastingWithCast(consumer, fluid, MaterialValues.VALUE_Ingot, TinkerSmeltery.ingotCast, "ingots", "ingot", name, folder);
    // block
    ITag<Item> block = getTag("forge", "storage_blocks/" + name);
    Consumer<IFinishedRecipe> wrapped = withCondition(consumer, tagCondition("storage_blocks/" + name));
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluid(new FluidStack(fluid, MaterialValues.VALUE_Block))
                            .build(wrapped, location(folder + name + "/block"));
  }

  /**
   * Adds recipe to create a cast
   * @param consumer  Recipe consumer
   * @param input     Item consumed to create cast
   * @param cast      Produced cast
   * @param folder    Output folder
   */
  private void addCastCastingRecipe(Consumer<IFinishedRecipe> consumer, INamedTag<Item> input, CastItemObject cast, String folder) {
    String path = input.getName().getPath();
    ItemCastingRecipeBuilder.tableRecipe(cast)
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot))
                            .setCast(input, true)
                            .setSwitchSlots()
                            .build(consumer, location(folder + "casts/" + path));
    MoldingRecipeBuilder.moldingTable(cast.getSand())
                        .setMaterial(TinkerSmeltery.blankCast.getSand())
                        .setMold(input, false)
                        .build(consumer, location(folder + "sand_casts/" + path));
    MoldingRecipeBuilder.moldingTable(cast.getRedSand())
                        .setMaterial(TinkerSmeltery.blankCast.getRedSand())
                        .setMold(input, false)
                        .build(consumer, location(folder + "red_sand_casts/" + path));
  }
}
