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
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.conditions.ConfigEnabledCondition;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.recipe.IngredientIntersection;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.common.registration.MetalItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
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
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.tools.data.MaterialIds;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock.FoliageType;

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
  public String getName() {
    return "Tinkers' Construct Smeltery Recipes";
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
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedLadder, 4)
                       .key('b', TinkerSmeltery.searedBrick)
                       .key('B', TinkerTags.Items.SEARED_BRICKS)
                       .patternLine("b b")
                       .patternLine("b b")
                       .patternLine("BBB")
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
    String folder = "smeltery/casting/";

    // container filling
    ContainerFillingRecipeBuilder.tableRecipe(Items.BUCKET, FluidAttributes.BUCKET_VOLUME)
                                 .build(consumer, location(folder + "filling/bucket"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.copperCan, MaterialValues.INGOT)
                                 .build(consumer, location(folder + "filling/copper_can"));
    // Slime
    String slimeFolder = folder + "slime/";
    this.addSlimeCastingRecipe(consumer, TinkerFluids.blood, SlimeType.BLOOD, slimeFolder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.earthSlime, SlimeType.EARTH, slimeFolder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.skySlime, SlimeType.SKY, slimeFolder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.enderSlime, SlimeType.ENDER, slimeFolder);
    // magma cream
    addBlockCastingRecipe(consumer, TinkerFluids.magmaCream, MaterialValues.SLIME_CONGEALED, Blocks.MAGMA_BLOCK, slimeFolder + "magma_cream/block");
    ItemCastingRecipeBuilder.tableRecipe(Items.MAGMA_CREAM)
                            .setFluid(new FluidStack(TinkerFluids.magmaCream.get(), MaterialValues.SLIMEBALL))
                            .build(consumer, location(slimeFolder + "magma_cream/ball"));

    // glass
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenGlass, MaterialValues.GLASS_BLOCK, TinkerCommons.clearGlass, folder + "glass/block");
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.clearGlassPane)
                            .setFluid(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_PANE))
                            .build(consumer, location(folder + "glass/pane"));
    // soul glass
    this.addBlockCastingRecipe(consumer, TinkerFluids.liquidSoul, MaterialValues.GLASS_BLOCK, TinkerCommons.soulGlass, folder + "soul/glass");
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.soulGlassPane)
                            .setFluid(new FluidStack(TinkerFluids.liquidSoul.get(), MaterialValues.GLASS_PANE))
                            .build(consumer, location(folder + "soul/pane"));

    // clay
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenClay, MaterialValues.SLIME_CONGEALED, Blocks.TERRACOTTA, folder + "clay/block");
    this.addIngotCastingRecipe(consumer, TinkerFluids.moltenClay, MaterialValues.SLIMEBALL, Items.BRICK, folder + "clay/brick");

    // emeralds
    this.addGemCastingRecipe(consumer, TinkerFluids.moltenEmerald, Items.EMERALD, folder + "emerald/gem");
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenEmerald, MaterialValues.GEM_BLOCK, Items.EMERALD_BLOCK, folder + "emerald/block");

    // ender pearls
    this.addGemCastingRecipe(consumer, TinkerFluids.moltenEnder, Items.ENDER_PEARL, folder + "ender_pearl");

    // obsidian
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenObsidian, MaterialValues.GLASS_BLOCK, Items.OBSIDIAN, folder + "obsidian");

    // Molten objects with Bucket, Block, Ingot, and Nugget forms with standard values
    String metalFolder = folder + "metal/";
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenIron,      Items.IRON_BLOCK,       Items.IRON_INGOT,      Items.IRON_NUGGET,               metalFolder + "iron/");
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenGold,      Items.GOLD_BLOCK,       Items.GOLD_INGOT,      Items.GOLD_NUGGET,               metalFolder + "gold/");
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenNetherite, Blocks.NETHERITE_BLOCK, Items.NETHERITE_INGOT, TinkerMaterials.netheriteNugget, metalFolder + "netherite/");
    this.addIngotCastingRecipe(consumer, TinkerFluids.moltenDebris, Items.NETHERITE_SCRAP, metalFolder + "netherite/scrap");
    // ores
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenCopper, TinkerMaterials.copper, metalFolder + "copper/");
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenCobalt, TinkerMaterials.cobalt, metalFolder + "cobalt/");
    // tier 3 alloys
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenSlimesteel,    TinkerMaterials.slimesteel,    metalFolder + "slimesteel/");
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenTinkersBronze, TinkerMaterials.tinkersBronze, metalFolder + "tinkers_bronze/");
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenRoseGold,      TinkerMaterials.roseGold,      metalFolder + "rose_gold/");
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenPigIron,       TinkerMaterials.pigIron,       metalFolder + "pig_iron/");
    // tier 4 alloys
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenManyullyn,   TinkerMaterials.manyullyn,   metalFolder + "manyullyn/");
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenHepatizon,   TinkerMaterials.hepatizon,   metalFolder + "hepatizon/");
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenQueensSlime, TinkerMaterials.queensSlime, metalFolder + "queens_slime/");
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenSoulsteel,   TinkerMaterials.soulsteel,   metalFolder + "soulsteel/");
    // tier 5 alloys
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenKnightslime, TinkerMaterials.knightslime, metalFolder + "knightslime/");

    // compat
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      this.addMetalOptionalCasting(consumer, compat.getFluid(), compat.getName(), metalFolder);
    }

    // seared blocks
    String searedFolder = folder + "seared/";
    this.addBlockCastingRecipe(consumer, TinkerFluids.searedStone, MaterialValues.METAL_BRICK, TinkerSmeltery.searedStone, searedFolder + "stone/block_from_seared");
    this.addIngotCastingRecipe(consumer, TinkerFluids.searedStone, TinkerSmeltery.searedBrick, searedFolder + "brick");
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedGlass)
                            .setFluid(new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.METAL_BRICK))
                            .setCast(Tags.Items.GLASS_COLORLESS, true)
                            .build(consumer, location(searedFolder + "glass"));
    // discount for casting panes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.searedGlassPane)
                            .setFluid(new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.INGOT))
                            .setCast(Tags.Items.GLASS_PANES_COLORLESS, true)
                            .build(consumer, location(searedFolder + "glass_pane"));

    // smeltery controller
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.smelteryController)
                            .setCast(TinkerSmeltery.searedHeater, true)
                            .setFluid(new FluidStack(TinkerFluids.moltenCopper.get(), MaterialValues.INGOT * 4))
                            .build(consumer, prefix(TinkerSmeltery.smelteryController, searedFolder));

    // craft seared stone from clay and stone
    // cobble
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedCobble, new CompoundIngredient(
      Ingredient.fromTag(Tags.Items.COBBLESTONE),
      Ingredient.fromItems(Blocks.GRAVEL)
    ), searedFolder + "cobble/block");
    addSearedSlabCastingRecipe(consumer, TinkerSmeltery.searedCobble.getSlab(), Ingredient.fromItems(Blocks.COBBLESTONE_SLAB), searedFolder + "cobble/slab");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedCobble.getStairs(), Ingredient.fromItems(Blocks.COBBLESTONE_STAIRS), searedFolder + "cobble/stairs");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedCobble.getWall(), Ingredient.fromItems(Blocks.COBBLESTONE_WALL), searedFolder + "cobble/wall");
    // stone
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedStone, Ingredient.fromTag(Tags.Items.STONE), searedFolder + "stone/block_from_clay");
    addSearedSlabCastingRecipe(consumer, TinkerSmeltery.searedStone.getSlab(), Ingredient.fromItems(Blocks.STONE_SLAB), searedFolder + "stone/slab");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedStone.getStairs(), Ingredient.fromItems(Blocks.STONE_STAIRS), searedFolder + "stone/stairs");
    // stone bricks
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedBricks, Ingredient.fromItems(Blocks.STONE_BRICKS), searedFolder + "bricks/block");
    addSearedSlabCastingRecipe(consumer, TinkerSmeltery.searedBricks.getSlab(), Ingredient.fromItems(Blocks.STONE_BRICK_SLAB), searedFolder + "bricks/slab");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedBricks.getStairs(), Ingredient.fromItems(Blocks.STONE_BRICK_STAIRS), searedFolder + "bricks/stairs");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedBricks.getWall(), Ingredient.fromItems(Blocks.STONE_BRICK_WALL), searedFolder + "bricks/wall");
    // other seared
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedCrackedBricks, Ingredient.fromItems(Blocks.CRACKED_STONE_BRICKS), searedFolder + "cracked");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedFancyBricks, Ingredient.fromItems(Blocks.CHISELED_STONE_BRICKS), searedFolder + "chiseled");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedPaver, Ingredient.fromItems(Blocks.SMOOTH_STONE), searedFolder + "paver");

    // Misc
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.lavawood)
                            .setFluid(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10))
                            .setCast(ItemTags.PLANKS, true)
                            .build(consumer, prefix(TinkerCommons.lavawood, folder));
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.blazewood)
                            .setFluid(new FluidStack(TinkerFluids.moltenBlaze.get(), FluidAttributes.BUCKET_VOLUME / 10))
                            .setCast(new IngredientIntersection(Ingredient.fromTag(ItemTags.PLANKS), Ingredient.fromTag(ItemTags.NON_FLAMMABLE_WOOD)), true)
                            .build(consumer, prefix(TinkerCommons.blazewood, folder));
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.mudBricks)
                            .setFluid(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 10))
                            .setCast(Items.DIRT, true)
                            .build(consumer, prefix(TinkerCommons.mudBricks, folder));

    // Cast recipes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.blankCast)
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.INGOT))
                            .setSwitchSlots()
                            .build(consumer, location(folder + "casts/blank"));

    this.addCastCastingRecipe(consumer, Tags.Items.INGOTS, TinkerSmeltery.ingotCast, folder);
    this.addCastCastingRecipe(consumer, Tags.Items.NUGGETS, TinkerSmeltery.nuggetCast, folder);
    this.addCastCastingRecipe(consumer, Tags.Items.GEMS, TinkerSmeltery.gemCast, folder);

    // misc casting - gold
    ItemCastingRecipeBuilder.tableRecipe(Items.GOLDEN_APPLE)
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 8))
                            .setCast(Items.APPLE, true)
                            .build(consumer, prefix(Items.GOLDEN_APPLE, folder));
    ItemCastingRecipeBuilder.tableRecipe(Items.GLISTERING_MELON_SLICE)
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.NUGGET * 8))
                            .setCast(Items.MELON_SLICE, true)
                            .build(consumer, prefix(Items.GLISTERING_MELON_SLICE, folder));
    ItemCastingRecipeBuilder.tableRecipe(Items.GOLDEN_CARROT)
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.NUGGET * 8))
                            .setCast(Items.CARROT, true)
                            .build(consumer, prefix(Items.GOLDEN_CARROT, folder));
    ItemCastingRecipeBuilder.tableRecipe(Items.CLOCK)
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 4))
                            .setCast(Items.REDSTONE, true)
                            .build(consumer, prefix(Items.CLOCK, folder));
    // misc casting - iron
    ItemCastingRecipeBuilder.tableRecipe(Items.LANTERN)
                            .setFluid(new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.NUGGET * 8))
                            .setCast(Blocks.TORCH, true)
                            .build(consumer, prefix(Items.LANTERN, folder));
    ItemCastingRecipeBuilder.tableRecipe(Items.SOUL_LANTERN)
                            .setFluid(new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.NUGGET * 8))
                            .setCast(Blocks.SOUL_TORCH, true)
                            .build(consumer, prefix(Items.SOUL_LANTERN, folder));
    ItemCastingRecipeBuilder.tableRecipe(Items.COMPASS)
                            .setFluid(new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 4))
                            .setCast(Items.REDSTONE, true)
                            .build(consumer, prefix(Items.COMPASS, folder));
    // ender chest
    ItemCastingRecipeBuilder.basinRecipe(Blocks.ENDER_CHEST)
                            .setFluid(new FluidStack(TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK * 8))
                            .setCast(Items.ENDER_EYE, true)
                            .build(consumer, prefix(Items.ENDER_CHEST, folder));

    // composite casting
    String compositeFolder = "tools/parts/composite/";
    // half a clay is 1 seared brick per grout amounts
    CompositeCastingRecipeBuilder.table(MaterialIds.stone, MaterialIds.searedStone)
                                 .setFluid(new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL / 2))
                                 .build(consumer, location(compositeFolder + "seared_stone"));
    CompositeCastingRecipeBuilder.table(MaterialIds.wood, MaterialIds.slimewood)
                                 .setFluid(new FluidStack(TinkerFluids.earthSlime.get(), MaterialValues.SLIMEBALL))
                                 .build(consumer, location(compositeFolder + "slimewood"));
    CompositeCastingRecipeBuilder.table(MaterialIds.wood, MaterialIds.nahuatl)
                                 .setFluid(new FluidStack(TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK))
                                 .build(consumer, location(compositeFolder + "nahuatl"));
  }

  private void addMeltingRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "smeltery/melting/";

    // water from ice
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ICE), Fluids.WATER, FluidAttributes.BUCKET_VOLUME, 1.0f)
                        .build(consumer, location(folder + "water/ice"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.PACKED_ICE), Fluids.WATER, FluidAttributes.BUCKET_VOLUME * 9, 3.0f)
                        .build(consumer, location(folder + "water/packed_ice"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.BLUE_ICE), Fluids.WATER, FluidAttributes.BUCKET_VOLUME * 81, 9.0f)
                        .build(consumer, location(folder + "water/blue_ice"));
    // water from snow
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SNOWBALL), Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 8, 0.5f)
                        .build(consumer, location(folder + "water/snowball"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SNOW_BLOCK), Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 2, 0.75f)
                        .build(consumer, location(folder + "water/snow_block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SNOW), Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 8, 0.5f)
                        .build(consumer, location(folder + "water/snow_layer"));

    // ores
    String metalFolder = folder + "metal/";
    addMetalMelting(consumer, TinkerFluids.moltenIron.get(),   "iron",   true, metalFolder, false);
    addMetalMelting(consumer, TinkerFluids.moltenGold.get(),   "gold",   true, metalFolder, false);
    addMetalMelting(consumer, TinkerFluids.moltenCopper.get(), "copper", true, metalFolder, false);
    addMetalMelting(consumer, TinkerFluids.moltenCobalt.get(), "cobalt", true, metalFolder, false);
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.ORES_NETHERITE_SCRAP), TinkerFluids.moltenDebris.get(), MaterialValues.INGOT, 2.0f)
                        .setOre()
                        .build(consumer, location(metalFolder + "molten_debris/ore"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.INGOTS_NETHERITE_SCRAP), TinkerFluids.moltenDebris.get(), MaterialValues.INGOT, 1.0f)
                        .build(consumer, location(metalFolder + "molten_debris/scrap"));
    
    // tier 3
    addMetalMelting(consumer, TinkerFluids.moltenSlimesteel.get(),    "slimesteel",     false, metalFolder, false);
    addMetalMelting(consumer, TinkerFluids.moltenTinkersBronze.get(), "silicon_bronze", false, metalFolder, false);
    addMetalMelting(consumer, TinkerFluids.moltenRoseGold.get(),      "rose_gold",      false, metalFolder, false);
    addMetalMelting(consumer, TinkerFluids.moltenPigIron.get(),       "pig_iron",       false, metalFolder, false);
    // tier 4
    addMetalMelting(consumer, TinkerFluids.moltenManyullyn.get(),   "manyullyn",    false, metalFolder, false);
    addMetalMelting(consumer, TinkerFluids.moltenHepatizon.get(),   "hepatizon",    false, metalFolder, false);
    addMetalMelting(consumer, TinkerFluids.moltenQueensSlime.get(), "queens_slime", false, metalFolder, false);
    addMetalMelting(consumer, TinkerFluids.moltenSoulsteel.get(),   "soulsteel",    false, metalFolder, false);
    addMetalMelting(consumer, TinkerFluids.moltenNetherite.get(),   "netherite",    false, metalFolder, false);
    // tier 5
    addMetalMelting(consumer, TinkerFluids.moltenKnightslime.get(), "knightslime", false, metalFolder, false);

    // compat
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      this.addMetalMelting(consumer, compat.getFluid(), compat.getName(), compat.isOre(), metalFolder, true);
    }

    // blood
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ROTTEN_FLESH), TinkerFluids.blood.get(), MaterialValues.SLIMEBALL / 5, 1.0f)
                        .build(consumer, location("blood_from_flesh"));

    // glass
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.SAND), TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_BLOCK, 1.5f)
                        .build(consumer, location(folder + "glass/sand"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GLASS), TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_BLOCK, 1.0f)
                        .build(consumer, location(folder + "glass/block"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GLASS_PANES), TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_PANE, 0.5f)
                        .build(consumer, location(folder + "glass/pane"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GLASS_BOTTLE), TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_BLOCK, 1.25f)
                        .build(consumer, location(folder + "glass/bottle"));
    // melt extra sand casts back
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.blankCast.getSand(), TinkerSmeltery.blankCast.getRedSand()),
                                 TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_PANE, 0.75f)
                        .build(consumer, location(folder + "glass/sand_cast"));

    // liquid soul
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.SOUL_SAND, Blocks.SOUL_SOIL), TinkerFluids.liquidSoul.get(), MaterialValues.GLASS_BLOCK, 1.5f)
                        .build(consumer, location(folder + "soul/sand"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerCommons.soulGlass), TinkerFluids.liquidSoul.get(), MaterialValues.GLASS_BLOCK, 1.0f)
                        .build(consumer, location(folder + "soul/glass"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerCommons.soulGlassPane), TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_PANE, 0.5f)
                        .build(consumer, location(folder + "soul/pane"));

    // clay
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.CLAY), TinkerFluids.moltenClay.get(), MaterialValues.SLIME_CONGEALED, 1.0f)
                        .build(consumer, location(folder + "clay/block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.CLAY_BALL), TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL, 0.5f)
                        .build(consumer, location(folder + "clay/ball"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.FLOWER_POT), TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 3, 2.0f)
                        .build(consumer, location(folder + "clay/pot"));
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
      Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA);
    MeltingRecipeBuilder.melting(terracottaBlock, TinkerFluids.moltenClay.get(), MaterialValues.SLIME_CONGEALED, 2.0f)
                        .build(consumer, location(folder + "clay/terracotta"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.BRICK), TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL, 1.0f)
                        .build(consumer, location(folder + "clay/brick"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.BRICK_SLAB),
                                 TinkerFluids.moltenClay.get(), MaterialValues.SLIME_CONGEALED / 2, 1.5f)
                        .build(consumer, location(folder + "clay/brick_slab"));

    // seared stone
    // stairs are here since the cheapest stair recipe is stone cutter, 1 to 1
    MeltingRecipeBuilder.melting(new CompoundIngredient(Ingredient.fromTag(TinkerTags.Items.SEARED_BLOCKS),
                                                        Ingredient.fromItems(TinkerSmeltery.searedLadder, TinkerSmeltery.searedCobble.getWall(), TinkerSmeltery.searedBricks.getWall(),
                                                                             TinkerSmeltery.searedCobble.getStairs(), TinkerSmeltery.searedStone.getStairs(), TinkerSmeltery.searedBricks.getStairs(), TinkerSmeltery.searedPaver.getStairs())),
                                 TinkerFluids.searedStone.get(), MaterialValues.METAL_BRICK, 2.0f)
                        .build(consumer, location(folder + "seared_stone/block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedCobble.getSlab(), TinkerSmeltery.searedStone.getSlab(), TinkerSmeltery.searedBricks.getSlab(), TinkerSmeltery.searedPaver.getSlab()),
                                 TinkerFluids.searedStone.get(), MaterialValues.METAL_BRICK / 2, 1.5f)
                        .build(consumer, location(folder + "seared_stone/slab"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedBrick), TinkerFluids.searedStone.get(), MaterialValues.INGOT, 1.0f)
                        .build(consumer, location(folder + "seared_stone/brick"));

    // melt down smeltery components
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedFaucet), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 3 / 2, 1.5f)
                        .build(consumer, location(folder + "seared_stone/faucet"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedChannel), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 5 / 3, 1.5f)
                        .build(consumer, location(folder + "seared_stone/channel"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.castingBasin, TinkerSmeltery.castingTable), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 7, 2.5f)
                        .build(consumer, location(folder + "seared_stone/casting"));
    // glass and tanks
    // TODO: output glass as well
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedTank.get(TankType.TANK)), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 8, 3f)
                        .build(consumer, location(folder + "seared_stone/tank"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedTank.get(TankType.WINDOW)), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 6, 2.5f)
                        .build(consumer, location(folder + "seared_stone/window"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedTank.get(TankType.GAUGE)), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 4, 2f)
                        .build(consumer, location(folder + "seared_stone/gauge"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedGlass), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 4, 2f)
                        .build(consumer, location(folder + "seared_stone/glass"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedGlassPane), TinkerFluids.searedStone.get(), MaterialValues.INGOT, 1.0f)
                        .build(consumer, location(folder + "seared_stone/pane"));
    // controllers
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedMelter), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 9, 3.5f)
                        .build(consumer, location(folder + "seared_stone/melter"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedHeater), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 8, 3f)
                        .build(consumer, location(folder + "seared_stone/heater"));
    // TODO: smeltery controller (requires copper)
    // TODO: IO (requires metal)


    // double efficiency when using smeltery for grout
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.grout), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 2, 1.5f)
                        .build(consumer, location(folder + "seared_stone/grout"));

    // slime
    String slimeFolder = folder + "slime/";
    addSlimeMeltingRecipe(consumer, TinkerFluids.earthSlime, SlimeType.EARTH, TinkerTags.Items.EARTH_SLIMEBALL, slimeFolder);
    addSlimeMeltingRecipe(consumer, TinkerFluids.skySlime, SlimeType.SKY, TinkerTags.Items.SKY_SLIMEBALL, slimeFolder);
    addSlimeMeltingRecipe(consumer, TinkerFluids.enderSlime, SlimeType.ENDER, TinkerTags.Items.ENDER_SLIMEBALL, slimeFolder);
    // magma cream
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.MAGMA_CREAM), TinkerFluids.magmaCream.get(), MaterialValues.SLIMEBALL, 1.0f)
                        .build(consumer, location(slimeFolder + "magma_cream/ball"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.MAGMA_BLOCK), TinkerFluids.magmaCream.get(), MaterialValues.SLIME_CONGEALED, 3.0f)
                        .build(consumer, location(slimeFolder + "magma_cream/block"));

    // obsidian
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.OBSIDIAN), TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK, 2.0f)
                        .build(consumer, location(folder + "obsidian"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.ENDER_CHEST), TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK * 8, 5.0f)
                        .build(consumer, location(folder + "obsidian_from_chest"));

    // emerald
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.ORES_EMERALD), TinkerFluids.moltenEmerald.get(), MaterialValues.GEM, 1.5f)
                        .build(consumer, location(folder + "emerald/ore"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), TinkerFluids.moltenEmerald.get(), MaterialValues.GEM, 1.0f)
                        .build(consumer, location(folder + "emerald/gem"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_EMERALD), TinkerFluids.moltenEmerald.get(), MaterialValues.GEM_BLOCK, 3.0f)
                        .build(consumer, location(folder + "emerald/block"));

    // iron melting - standard values
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ACTIVATOR_RAIL, Items.DETECTOR_RAIL, Blocks.STONECUTTER, Blocks.PISTON, Blocks.STICKY_PISTON), TinkerFluids.moltenIron.get(), MaterialValues.INGOT)
                        .build(consumer, location(metalFolder + "iron/ingot_1"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_DOOR, Blocks.SMITHING_TABLE), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 2)
                        .build(consumer, location(metalFolder + "iron/ingot_2"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.BUCKET), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 3)
                        .build(consumer, location(metalFolder + "iron/bucket"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.COMPASS, Blocks.IRON_TRAPDOOR), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 4)
                        .build(consumer, location(metalFolder + "iron/ingot_4"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.BLAST_FURNACE, Blocks.HOPPER, Items.MINECART), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 5)
                        .build(consumer, location(metalFolder + "iron/ingot_5"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.CAULDRON), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 7)
                        .build(consumer, location(metalFolder + "iron/cauldron"));
    // non-standard
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.CHAIN), TinkerFluids.moltenIron.get(), MaterialValues.INGOT + MaterialValues.NUGGET * 2)
                        .build(consumer, location(metalFolder + "iron/chain"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.ANVIL), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 4 + MaterialValues.METAL_BLOCK * 3)
                        .build(consumer, location(metalFolder + "iron/anvil"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.IRON_BARS, Blocks.RAIL), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 6 / 16)
                        .build(consumer, location(metalFolder + "iron/ingot_6_16"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.TRIPWIRE_HOOK), TinkerFluids.moltenIron.get(), MaterialValues.INGOT / 2)
                        .build(consumer, location(metalFolder + "iron/tripwire"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.LANTERN, Blocks.SOUL_LANTERN), TinkerFluids.moltenIron.get(), MaterialValues.NUGGET * 8)
                        .build(consumer, location(metalFolder + "iron/lantern"));
    // armor
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_HELMET), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 5)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "iron/helmet"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_CHESTPLATE), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 8)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "iron/chestplate"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_LEGGINGS), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 7)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "iron/leggings"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_BOOTS), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 4)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "iron/boots"));
    // tools
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_AXE, Items.IRON_PICKAXE), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 3)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "iron/axes"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_SWORD, Items.IRON_HOE, Items.SHEARS), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 2)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "iron/weapon"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_SHOVEL, Items.FLINT_AND_STEEL, Items.SHIELD), TinkerFluids.moltenIron.get(), MaterialValues.INGOT)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "iron/small"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.CROSSBOW), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 3 / 2) // tripwire hook is .5, ingot is 1
                        .setDamagable()
                        .build(consumer, location(metalFolder + "iron/crossbow"));
    // unique melting
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_HORSE_ARMOR), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 7)
                        .build(consumer, location(metalFolder + "iron/horse_armor"));

    // gold melting
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.GOLD_CASTS), TinkerFluids.moltenGold.get(), MaterialValues.INGOT)
                        .build(consumer, location(metalFolder + "gold/cast"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.POWERED_RAIL), TinkerFluids.moltenGold.get(), MaterialValues.INGOT)
                        .build(consumer, location(metalFolder + "gold/powered_rail"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE), TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 2)
                        .build(consumer, location(metalFolder + "gold/plate"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.CLOCK), TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 4)
                        .build(consumer, location(metalFolder + "gold/clock"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_APPLE), TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 8)
                        .build(consumer, location(metalFolder + "gold/apple"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GLISTERING_MELON_SLICE, Items.GOLDEN_CARROT), TinkerFluids.moltenGold.get(), MaterialValues.NUGGET * 8)
                        .build(consumer, location(metalFolder + "gold/produce"));
    // armor
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_HELMET), TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 5)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "gold/helmet"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_CHESTPLATE), TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 8)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "gold/chestplate"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_LEGGINGS), TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 7)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "gold/leggings"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_BOOTS), TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 4)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "gold/boots"));
    // tools
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_AXE, Items.GOLDEN_PICKAXE), TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 3)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "gold/axes"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_SWORD, Items.GOLDEN_HOE), TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 2)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "gold/weapon"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_SHOVEL), TinkerFluids.moltenGold.get(), MaterialValues.INGOT)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "gold/shovel"));
    // unique melting
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_HORSE_ARMOR), TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 7)
                        .build(consumer, location(metalFolder + "gold/horse_armor"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ENCHANTED_GOLDEN_APPLE), TinkerFluids.moltenGold.get(), MaterialValues.METAL_BLOCK * 8)
                        .build(consumer, location(metalFolder + "gold/enchanted_apple"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.GILDED_BLACKSTONE), TinkerFluids.moltenGold.get(), MaterialValues.NUGGET * 6) // bit better than mining before ore bonus
                        .setOre()
                        .build(consumer, location(metalFolder + "gold/gilded_blackstone"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.BELL), TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 4) // bit arbitrary, I am happy to change the value if someone has a better one
                        .build(consumer, location(metalFolder + "gold/bell"));

    // netherite
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.LODESTONE), TinkerFluids.moltenNetherite.get(), MaterialValues.INGOT)
                        .build(consumer, location(metalFolder + "netherite/lodestone"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(
      Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS,
      Items.NETHERITE_HOE, Items.NETHERITE_AXE, Items.NETHERITE_PICKAXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_SWORD
                                                     ), TinkerFluids.moltenNetherite.get(), MaterialValues.INGOT, 1.0f)
                        .setDamagable()
                        .build(consumer, location(metalFolder + "netherite/tools_and_armor"));

    // slime
    TinkerGadgets.slimeBoots.forEach((type, boots) -> {
      if (type != SlimeType.ICHOR) { // no ichor fluid
        MeltingRecipeBuilder.melting(Ingredient.fromItems(boots), TinkerFluids.slime.get(type).get(), MaterialValues.SLIMEBALL * 2 + MaterialValues.SLIME_CONGEALED * 2)
                            .build(consumer, location(slimeFolder + type.getString() + "/boots"));
      }
    });
    TinkerGadgets.slimeSling.forEach((type, sling) -> {
      if (type != SlimeType.ICHOR) { // no ichor fluid
        MeltingRecipeBuilder.melting(Ingredient.fromItems(sling), TinkerFluids.slime.get(type).get(), MaterialValues.SLIMEBALL * 3 + MaterialValues.SLIME_CONGEALED)
                            .setDamagable()
                            .build(consumer, location(slimeFolder + type.getString() + "/sling"));
      }
    });
    // recycle saplings
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.slimeSapling.get(FoliageType.SKY)), TinkerFluids.skySlime.get(), MaterialValues.SLIMEBALL)
                        .build(consumer, location(slimeFolder + "sky/sapling"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.slimeSapling.get(FoliageType.ENDER)), TinkerFluids.enderSlime.get(), MaterialValues.SLIMEBALL)
                        .build(consumer, location(slimeFolder + "ender/sapling"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.slimeSapling.get(FoliageType.BLOOD)), TinkerFluids.blood.get(), MaterialValues.SLIMEBALL)
                        .build(consumer, location(slimeFolder + "blood/sapling"));

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

    // slimesteel: 1 iron + 1 skyslime + 1 seared brick = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenSlimesteel.get(), MaterialValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenIron.get(), MaterialValues.INGOT)
                      .addInput(TinkerFluids.skySlime.get(), MaterialValues.SLIMEBALL)
                      .addInput(TinkerFluids.searedStone.get(), MaterialValues.INGOT)
                      .build(consumer, prefixR(TinkerFluids.moltenSlimesteel, folder));

    // tinker's bronze: 3 copper + 1 silicon (1/4 glass) = 4
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenTinkersBronze.get(), MaterialValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_BLOCK)
                      .build(consumer, prefixR(TinkerFluids.moltenTinkersBronze, folder));

    // rose gold: 3 copper + 1 gold = 4
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenRoseGold.get(), MaterialValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenGold.get(), MaterialValues.INGOT)
                      .build(consumer, prefixR(TinkerFluids.moltenRoseGold, folder));
    // pig iron: 1 iron + 1 blood + 1 clay = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenPigIron.get(), MaterialValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenIron.get(), MaterialValues.INGOT)
                      .addInput(TinkerFluids.blood.get(), MaterialValues.SLIMEBALL)
                      .addInput(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL)
                      .build(consumer, prefixR(TinkerFluids.moltenPigIron, folder));
    // obsidian: 1 water + 1 lava = 2
    // note this is not a progression break, as the same tier lets you combine glass and copper for same mining level
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK / 10)
                      .addInput(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 10)
                      .addInput(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10)
                      .build(consumer, prefixR(TinkerFluids.moltenObsidian, folder));

    // tier 4

    // queens slime: 1 cobalt + 1 gold + 1 magma cream = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenQueensSlime.get(), MaterialValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCobalt.get(), MaterialValues.INGOT)
                      .addInput(TinkerFluids.moltenGold.get(), MaterialValues.INGOT)
                      .addInput(TinkerFluids.magmaCream.get(), MaterialValues.SLIMEBALL)
                      .build(consumer, prefixR(TinkerFluids.moltenQueensSlime, folder));

    // manyullyn: 3 cobalt + 1 debris = 3
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenManyullyn.get(), MaterialValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCobalt.get(), MaterialValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenDebris.get(), MaterialValues.INGOT)
                      .build(consumer, prefixR(TinkerFluids.moltenManyullyn, folder));

    // heptazion: 2 copper + 1 cobalt + 1/4 obsidian = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenHepatizon.get(), MaterialValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCobalt.get(), MaterialValues.INGOT)
                      .addInput(TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK)
                      .build(consumer, prefixR(TinkerFluids.moltenHepatizon, folder));

    // netherrite: 4 debris + 4 gold = 1 (why is this so dense vanilla?)
    ConditionalRecipe.builder()
                     .addCondition(ConfigEnabledCondition.CHEAPER_NETHERITE_ALLOY)
                     .addRecipe(
                       AlloyRecipeBuilder.alloy(TinkerFluids.moltenNetherite.get(), MaterialValues.NUGGET)
                                         .addInput(TinkerFluids.moltenDebris.get(), MaterialValues.NUGGET * 4)
                                         .addInput(TinkerFluids.moltenGold.get(), MaterialValues.NUGGET * 2)::build)
                     .addCondition(TrueCondition.INSTANCE) // fallback
                     .addRecipe(
                       AlloyRecipeBuilder.alloy(TinkerFluids.moltenNetherite.get(), MaterialValues.NUGGET)
                                         .addInput(TinkerFluids.moltenDebris.get(), MaterialValues.NUGGET * 4)
                                         .addInput(TinkerFluids.moltenGold.get(), MaterialValues.NUGGET * 4)::build)
                     .build(consumer, prefixR(TinkerFluids.moltenNetherite, folder));


    // tier 3 compat
    Consumer<IFinishedRecipe> wrapped;

    // bronze
    wrapped = withCondition(consumer, tagCondition("ingots/bronze"), tagCondition("ingots/tin"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenBronze.get(), MaterialValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenTin.get(), MaterialValues.INGOT)
                      .build(wrapped, prefixR(TinkerFluids.moltenBronze, folder));

    // brass
    wrapped = withCondition(consumer, tagCondition("ingots/brass"), tagCondition("ingots/zinc"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenBrass.get(), MaterialValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenZinc.get(), MaterialValues.INGOT)
                      .build(wrapped, prefixR(TinkerFluids.moltenBrass, folder));

    // electrum
    wrapped = withCondition(consumer, tagCondition("ingots/electrum"), tagCondition("ingots/silver"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenElectrum.get(), MaterialValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenGold.get(), MaterialValues.INGOT)
                      .addInput(TinkerFluids.moltenSilver.get(), MaterialValues.INGOT)
                      .build(wrapped, prefixR(TinkerFluids.moltenElectrum, folder));

    // invar
    wrapped = withCondition(consumer, tagCondition("ingots/invar"), tagCondition("ingots/nickel"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenInvar.get(), MaterialValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenNickel.get(), MaterialValues.INGOT)
                      .build(wrapped, prefixR(TinkerFluids.moltenInvar, folder));

    // constantan
    wrapped = withCondition(consumer, tagCondition("ingots/constantan"), tagCondition("ingots/nickel"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenConstantan.get(), MaterialValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.INGOT)
                      .addInput(TinkerFluids.moltenNickel.get(), MaterialValues.INGOT)
                      .build(wrapped, prefixR(TinkerFluids.moltenConstantan, folder));
  }

  private void addEntityMeltingRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "smeltery/entity_melting/";

    // zombies give less blood, they lost a lot already
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED, EntityType.ZOMBIFIED_PIGLIN, EntityType.ZOGLIN, EntityType.ZOMBIE_HORSE),
                                       new FluidStack(TinkerFluids.blood.get(), MaterialValues.SLIMEBALL / 10), 2)
                              .build(consumer, prefixR(EntityType.ZOMBIE, folder));

    // creepers are based on explosives, tnt is explosive, tnt is made from sand, sand melts into glass. therefore, creepers melt into glass
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.CREEPER),
                                       new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_BLOCK / 20), 2)
                              .build(consumer, prefixR(EntityType.CREEPER, folder));

    // melt skeletons to get the milk out
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityIngredient.of(EntityTypeTags.SKELETONS), EntityIngredient.of(EntityType.SKELETON_HORSE)),
                                       new FluidStack(ForgeMod.MILK.get(), FluidAttributes.BUCKET_VOLUME / 10))
                              .build(consumer, location(folder + "skeletons"));

    // slimes melt into slime, shocker
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SLIME), new FluidStack(TinkerFluids.earthSlime.get(), MaterialValues.SLIMEBALL / 10))
                              .build(consumer, prefixR(EntityType.SLIME, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.skySlimeEntity.get()), new FluidStack(TinkerFluids.skySlime.get(), MaterialValues.SLIMEBALL / 10))
                              .build(consumer, prefixR(TinkerWorld.skySlimeEntity, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.MAGMA_CUBE), new FluidStack(TinkerFluids.magmaCream.get(), MaterialValues.SLIMEBALL / 10))
                              .build(consumer, prefixR(EntityType.MAGMA_CUBE, folder));

    // iron golems can be healed using an iron ingot 25 health
    // 4 * 9 gives 36, which is larger
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.IRON_GOLEM), new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.NUGGET), 4)
                              .build(consumer, prefixR(EntityType.IRON_GOLEM, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SNOW_GOLEM), new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 10))
                              .build(consumer, prefixR(EntityType.SNOW_GOLEM, folder));

    // "melt" blazes to get fuel
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.BLAZE), new FluidStack(TinkerFluids.moltenBlaze.get(), FluidAttributes.BUCKET_VOLUME / 50), 2)
                              .build(consumer, prefixR(EntityType.BLAZE, folder));

    // guardians are rock, seared stone is rock, don't think about it too hard
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN), new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.NUGGET), 4)
                              .build(consumer, prefixR(EntityType.GUARDIAN, folder));
    // silverfish also seem like rock, sorta?
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SILVERFISH), new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.NUGGET), 2)
                              .build(consumer, prefixR(EntityType.SILVERFISH, folder));

    // villagers melt into emerald, but they die quite quick
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.VILLAGER, EntityType.WANDERING_TRADER),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), MaterialValues.GEM / 10), 5)
                              .build(consumer, prefixR(EntityType.VILLAGER, folder));
    // illagers are more resistant, they resist the villager culture afterall
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.EVOKER, EntityType.ILLUSIONER, EntityType.PILLAGER, EntityType.VINDICATOR),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), MaterialValues.GEM / 10), 2)
                              .build(consumer, location(folder + "illager"));
    // zombie villagers and witches faintly recall being a villager once
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ZOMBIE_VILLAGER, EntityType.WITCH),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), MaterialValues.GEM / 25), 3)
                              .build(consumer, prefixR(EntityType.ZOMBIE_VILLAGER, folder));

    // melt ender for the molten ender
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON),
                                       new FluidStack(TinkerFluids.moltenEnder.get(), MaterialValues.GEM / 25), 2)
                              .build(consumer, location(folder + "ender"));

    // if you can get him to stay, wither is a source of free liquid soul
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.WITHER),
                                       new FluidStack(TinkerFluids.liquidSoul.get(), MaterialValues.GLASS_BLOCK / 20), 2)
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
    String prefix = folder + "/" + name + "/";
    addMetalBase(consumer, fluid, MaterialValues.METAL_BLOCK, false, "storage_blocks/" + name, 3.0f, prefix + "block", isOptional);
    addMetalBase(consumer, fluid, MaterialValues.INGOT, false, "ingots/" + name, 1.0f, prefix + "ingot", isOptional);
    addMetalBase(consumer, fluid, MaterialValues.NUGGET, false, "nuggets/" + name, 1 / 3f, prefix + "nugget", isOptional);
    if (hasOre) {
      addMetalBase(consumer, fluid, MaterialValues.INGOT, true, "ores/" + name, 1.5f, prefix + "ore", isOptional);
    }
    // dust is always optional, as we don't do dust
    addMetalBase(consumer, fluid, MaterialValues.INGOT, false, "dusts/" + name, 0.75f, prefix + "dust", true);
  }


  /* Casting */


  /**
   * Adds a casting recipe for a block
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param amount    Input amount
   * @param block     Output block
   * @param location  Output name
   */
  private void addBlockCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, int amount, IItemProvider block, String location) {
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluid(new FluidStack(fluid.get(), amount))
                            .build(consumer, location(location));
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param location  Recipe location
   */
  private static void addSearedCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, String location) {
    addSearedCastingRecipe(consumer, block, cast, MaterialValues.SLIMEBALL * 2, location);
  }

  /**
   * Adds a recipe to create the given seared slab block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param location  Recipe location
   */
  private static void addSearedSlabCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, String location) {
    addSearedCastingRecipe(consumer, block, cast, MaterialValues.SLIMEBALL, location);
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param amount    Amount of fluid needed
   * @param location  Recipe location
   */
  private static void addSearedCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, int amount, String location) {
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluid(new FluidStack(TinkerFluids.moltenClay.get(), amount))
                            .setCast(cast, true)
                            .build(consumer, location(location));
  }

  /**
   * Adds a recipe for casting using a cast
   * @param consumer  Recipe consumer
   * @param fluid     Recipe fluid
   * @param amount    Fluid amount
   * @param cast      Cast used
   * @param output    Recipe output
   * @param location  Recipe base
   */
  private void addCastingWithCastRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, int amount, CastItemObject cast, IItemProvider output, String location) {
    FluidStack fluidStack = new FluidStack(fluid.get(), amount);
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluid(fluidStack)
                            .setCast(cast, false)
                            .build(consumer, location(location + "_gold_cast"));
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluid(fluidStack)
                            .setCast(cast.getSingleUseTag(), true)
                            .build(consumer, location(location + "_sand_cast"));
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param amount    Recipe amount
   * @param ingot     Ingot output
   * @param location  Recipe base
   */
  private void addIngotCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, int amount, IItemProvider ingot, String location) {
    addCastingWithCastRecipe(consumer, fluid, amount, TinkerSmeltery.ingotCast, ingot, location);
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param ingot     Ingot output
   * @param location  Recipe base
   */
  private void addIngotCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, IItemProvider ingot, String location) {
    addIngotCastingRecipe(consumer, fluid, MaterialValues.INGOT, ingot, location);
  }

  /**
   * Adds a casting recipe using an ingot cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param gem       Gem output
   * @param location  Recipe base
   */
  private void addGemCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, IItemProvider gem, String location) {
    addCastingWithCastRecipe(consumer, fluid, MaterialValues.GEM, TinkerSmeltery.gemCast, gem, location);
  }

  /**
   * Adds a casting recipe using a nugget cast
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param nugget    Nugget output
   * @param location  Recipe base
   */
  private void addNuggetCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, IItemProvider nugget, String location) {
    addCastingWithCastRecipe(consumer, fluid, MaterialValues.NUGGET, TinkerSmeltery.nuggetCast, nugget, location);
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
    String slimeFolder = folder + type.getString() + "/";
    MeltingRecipeBuilder.melting(Ingredient.fromTag(tag), fluidSupplier.get(), MaterialValues.SLIMEBALL, 1.0f)
                        .build(consumer, location(slimeFolder + "ball"));
    IItemProvider item = TinkerWorld.congealedSlime.get(type);
    MeltingRecipeBuilder.melting(Ingredient.fromItems(item), fluidSupplier.get(), MaterialValues.SLIME_CONGEALED, 2.0f)
                        .build(consumer, location(slimeFolder + "congealed"));
    item = TinkerWorld.slime.get(type);
    MeltingRecipeBuilder.melting(Ingredient.fromItems(item), fluidSupplier.get(), MaterialValues.SLIMEBLOCK, 3.0f)
                        .build(consumer, location(slimeFolder + "block"));
  }

  /**
   * Adds slime related casting recipes
   * @param consumer    Recipe consumer
   * @param fluid       Fluid matching the slime type
   * @param slimeType   SlimeType for this recipe
   * @param folder      Output folder
   */
  private void addSlimeCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, SlimeType slimeType, String folder) {
    String colorFolder = folder + slimeType.getString() + "/";
    addBlockCastingRecipe(consumer, fluid, MaterialValues.SLIME_CONGEALED, TinkerWorld.congealedSlime.get(slimeType), colorFolder + "congealed");
    ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.slime.get(slimeType))
                            .setFluid(new FluidStack(fluid.get(), MaterialValues.SLIMEBLOCK - MaterialValues.SLIME_CONGEALED))
                            .setCast(TinkerWorld.congealedSlime.get(slimeType), true)
                            .build(consumer, location(colorFolder + "block"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.slimeball.get(slimeType))
                            .setFluid(new FluidStack(fluid.get(), MaterialValues.SLIMEBALL))
                            .build(consumer, location(colorFolder + "slimeball"));
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
      addBlockCastingRecipe(consumer, fluid, MaterialValues.METAL_BLOCK, block, folder + "block");
    }
    if (ingot != null) {
      addIngotCastingRecipe(consumer, fluid, ingot, folder + "ingot");
    }
    if (nugget != null) {
      addNuggetCastingRecipe(consumer, fluid, nugget, folder + "nugget");
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
    addOptionalCastingWithCast(consumer, fluid, MaterialValues.NUGGET, TinkerSmeltery.nuggetCast, "nuggets", "nugget", name, folder);
    addOptionalCastingWithCast(consumer, fluid, MaterialValues.INGOT, TinkerSmeltery.ingotCast, "ingots", "ingot", name, folder);
    // block
    ITag<Item> block = getTag("forge", "storage_blocks/" + name);
    Consumer<IFinishedRecipe> wrapped = withCondition(consumer, tagCondition("storage_blocks/" + name));
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluid(new FluidStack(fluid, MaterialValues.METAL_BLOCK))
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
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.INGOT))
                            .setCast(input, true)
                            .setSwitchSlots()
                            .build(consumer, location(folder + "casts/" + path));
    MoldingRecipeBuilder.moldingTable(cast.getSand())
                        .setMaterial(TinkerSmeltery.blankCast.getSand())
                        .setPattern(input, false)
                        .build(consumer, location(folder + "sand_casts/" + path));
    MoldingRecipeBuilder.moldingTable(cast.getRedSand())
                        .setMaterial(TinkerSmeltery.blankCast.getRedSand())
                        .setPattern(input, false)
                        .build(consumer, location(folder + "red_sand_casts/" + path));
  }
}
