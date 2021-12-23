package slimeknights.tconstruct.smeltery.data;

import net.minecraft.block.Block;
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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.recipe.data.CompoundIngredient;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.recipe.data.ItemNameOutput;
import slimeknights.mantle.recipe.data.NBTIngredient;
import slimeknights.mantle.recipe.data.NBTNameIngredient;
import slimeknights.mantle.recipe.ingredient.IngredientIntersection;
import slimeknights.mantle.recipe.ingredient.IngredientWithout;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.data.FluidTagEmptyCondition;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.data.recipe.ICommonRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.ISmelteryRecipeHelper;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.container.ContainerFillingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SmelteryRecipeProvider extends BaseRecipeProvider implements ISmelteryRecipeHelper, ICommonRecipeHelper {
  public SmelteryRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Smeltery Recipes";
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.addCraftingRecipes(consumer);
    this.addSmelteryRecipes(consumer);
    this.addFoundryRecipes(consumer);
    this.addMeltingRecipes(consumer);
    this.addCastingRecipes(consumer);
    this.addAlloyRecipes(consumer);
    this.addEntityMeltingRecipes(consumer);

    this.addCompatRecipes(consumer);
  }

  private void addCraftingRecipes(Consumer<IFinishedRecipe> consumer) {
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.copperCan, 3)
                       .key('c', TinkerMaterials.copper.getIngotTag())
                       .patternLine("c c")
                       .patternLine(" c ")
                       .addCriterion("has_item", hasItem(TinkerMaterials.copper.getIngotTag()))
                       .build(consumer, prefix(TinkerSmeltery.copperCan, "smeltery/"));

    // sand casts
    ShapelessRecipeBuilder.shapelessRecipe(TinkerSmeltery.blankCast.getSand(), 4)
                          .addIngredient(Tags.Items.SAND_COLORLESS)
                          .addCriterion("has_casting", hasItem(TinkerSmeltery.searedTable))
                          .build(consumer, modResource("smeltery/sand_cast"));
    ShapelessRecipeBuilder.shapelessRecipe(TinkerSmeltery.blankCast.getRedSand(), 4)
                          .addIngredient(Tags.Items.SAND_RED)
                          .addCriterion("has_casting", hasItem(TinkerSmeltery.searedTable))
                          .build(consumer, modResource("smeltery/red_sand_cast"));

    // pick up sand casts from the table
    MoldingRecipeBuilder.moldingTable(TinkerSmeltery.blankCast.getSand())
                        .setMaterial(TinkerTags.Items.SAND_CASTS)
                        .build(consumer, modResource("smeltery/sand_cast_pickup"));
    MoldingRecipeBuilder.moldingTable(TinkerSmeltery.blankCast.getRedSand())
                        .setMaterial(TinkerTags.Items.RED_SAND_CASTS)
                        .build(consumer, modResource("smeltery/red_sand_cast_pickup"));
  }

  private void addSmelteryRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "smeltery/seared/";
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
                        .build(consumer, prefix(TinkerSmeltery.searedBrick, folder));
    Consumer<Consumer<IFinishedRecipe>> fastGrout = c ->
      CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(TinkerSmeltery.grout), TinkerSmeltery.searedBrick, 0.3f, 100)
                          .addCriterion("has_item", hasItem(TinkerSmeltery.grout)).build(c);
    ConditionalRecipe.builder()
                     .addCondition(new ModLoadedCondition("ceramics"))
                     .addRecipe(c -> fastGrout.accept(ConsumerWrapperBuilder.wrap(new ResourceLocation("ceramics", "kiln")).build(c)))
                     .addCondition(TrueCondition.INSTANCE)
                     .addRecipe(fastGrout)
                     .generateAdvancement()
                     .build(consumer, wrap(TinkerSmeltery.searedBrick, folder, "_kiln"));


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
    this.searedStonecutter(consumer, TinkerSmeltery.searedBricks, folder);
    this.searedStonecutter(consumer, TinkerSmeltery.searedFancyBricks, folder);
    this.searedStonecutter(consumer, TinkerSmeltery.searedTriangleBricks, folder);

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
    this.slabStairsCrafting(consumer, TinkerSmeltery.searedStone, folder, true);
    this.stairSlabWallCrafting(consumer, TinkerSmeltery.searedCobble, folder, true);
    this.slabStairsCrafting(consumer, TinkerSmeltery.searedPaver, folder, true);
    this.stairSlabWallCrafting(consumer, TinkerSmeltery.searedBricks, folder, true);

    // tanks
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTank.get(TankType.FUEL_TANK))
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('B', Tags.Items.GLASS)
                       .patternLine("###")
                       .patternLine("#B#")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, modResource(folder + "fuel_tank"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE))
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('B', Tags.Items.GLASS)
                       .patternLine("#B#")
                       .patternLine("BBB")
                       .patternLine("#B#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, modResource(folder + "fuel_gauge"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTank.get(TankType.INGOT_TANK))
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('B', Tags.Items.GLASS)
                       .patternLine("#B#")
                       .patternLine("#B#")
                       .patternLine("#B#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, modResource(folder + "ingot_tank"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE))
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('B', Tags.Items.GLASS)
                       .patternLine("B#B")
                       .patternLine("#B#")
                       .patternLine("B#B")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, modResource(folder + "ingot_gauge"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedLantern.get(), 3)
                       .key('C', Tags.Items.INGOTS_IRON)
                       .key('B', TinkerSmeltery.searedBrick)
                       .key('P', TinkerSmeltery.searedGlassPane)
                       .patternLine(" C ")
                       .patternLine("PPP")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, modResource(folder + "lantern"));

    // fluid transfer
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedFaucet.get(), 2)
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("# #")
                       .patternLine(" # ")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, modResource(folder + "faucet"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedChannel.get(), 3)
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("# #")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, modResource(folder + "channel"));

    // casting
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedBasin.get())
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("# #")
                       .patternLine("# #")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, modResource(folder + "basin"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTable.get())
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("###")
                       .patternLine("# #")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, modResource(folder + "table"));

    // peripherals
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedDrain)
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('C', TinkerMaterials.copper.getIngotTag())
                       .patternLine("# #")
                       .patternLine("C C")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, modResource(folder + "drain"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedChute)
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('C', TinkerMaterials.copper.getIngotTag())
                       .patternLine("#C#")
                       .patternLine("   ")
                       .patternLine("#C#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, modResource(folder + "chute"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedDuct)
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('C', TinkerMaterials.cobalt.getIngotTag())
                       .patternLine("# #")
                       .patternLine("C C")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerMaterials.cobalt.getIngotTag()))
                       .build(consumer, modResource(folder + "duct"));

    // controllers
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedMelter)
                       .key('G', Ingredient.fromItems(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE), TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE)))
                       .key('B', TinkerSmeltery.searedBrick)
                       .patternLine("BGB")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, modResource(folder + "melter"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedHeater)
                       .key('B', TinkerSmeltery.searedBrick)
                       .patternLine("BBB")
                       .patternLine("B B")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, modResource(folder + "heater"));

    // casting
    String castingFolder = "smeltery/casting/seared/";

    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedStone)
                            .setFluidAndTime(TinkerFluids.searedStone, false, FluidValues.METAL_BRICK)
                            .build(consumer, modResource(castingFolder + "stone/block_from_seared"));
    this.ingotCasting(consumer, TinkerFluids.searedStone, TinkerSmeltery.searedBrick, castingFolder + "brick");
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedGlass)
                            .setFluidAndTime(TinkerFluids.searedStone, false, FluidValues.METAL_BRICK)
                            .setCast(Tags.Items.GLASS_COLORLESS, true)
                            .build(consumer, modResource(castingFolder + "glass"));
    // discount for casting panes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.searedGlassPane)
                            .setFluidAndTime(TinkerFluids.searedStone, false, FluidValues.INGOT)
                            .setCast(Tags.Items.GLASS_PANES_COLORLESS, true)
                            .build(consumer, modResource(castingFolder + "glass_pane"));

    // smeltery controller
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.smelteryController)
                            .setCast(TinkerSmeltery.searedHeater, true)
                            .setFluidAndTime(TinkerFluids.moltenCopper, true, FluidValues.INGOT * 4)
                            .build(consumer, prefix(TinkerSmeltery.smelteryController, castingFolder));

    // craft seared stone from clay and stone
    // button is the closest we have to a single stone brick, just go with it, better than not having the recipe
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.searedBrick)
                            .setFluidAndTime(TinkerFluids.moltenClay, false, FluidValues.SLIMEBALL / 2)
                            .setCast(Items.STONE_BUTTON, true)
                            .build(consumer, modResource(castingFolder + "brick_composite"));
    // cobble
    searedCasting(consumer, TinkerSmeltery.searedCobble, CompoundIngredient.from(Ingredient.fromTag(Tags.Items.COBBLESTONE), Ingredient.fromItems(Blocks.GRAVEL)), castingFolder + "cobble/block");
    searedSlabCasting(consumer, TinkerSmeltery.searedCobble.getSlab(), Ingredient.fromItems(Blocks.COBBLESTONE_SLAB), castingFolder + "cobble/slab");
    searedCasting(consumer, TinkerSmeltery.searedCobble.getStairs(), Ingredient.fromItems(Blocks.COBBLESTONE_STAIRS), castingFolder + "cobble/stairs");
    searedCasting(consumer, TinkerSmeltery.searedCobble.getWall(), Ingredient.fromItems(Blocks.COBBLESTONE_WALL), castingFolder + "cobble/wall");
    // stone
    searedCasting(consumer, TinkerSmeltery.searedStone, Ingredient.fromTag(Tags.Items.STONE), castingFolder + "stone/block_from_clay");
    searedSlabCasting(consumer, TinkerSmeltery.searedStone.getSlab(), Ingredient.fromItems(Blocks.STONE_SLAB), castingFolder + "stone/slab");
    searedCasting(consumer, TinkerSmeltery.searedStone.getStairs(), Ingredient.fromItems(Blocks.STONE_STAIRS), castingFolder + "stone/stairs");
    // stone bricks
    searedCasting(consumer, TinkerSmeltery.searedBricks, Ingredient.fromItems(Blocks.STONE_BRICKS), castingFolder + "bricks/block");
    searedSlabCasting(consumer, TinkerSmeltery.searedBricks.getSlab(), Ingredient.fromItems(Blocks.STONE_BRICK_SLAB), castingFolder + "bricks/slab");
    searedCasting(consumer, TinkerSmeltery.searedBricks.getStairs(), Ingredient.fromItems(Blocks.STONE_BRICK_STAIRS), castingFolder + "bricks/stairs");
    searedCasting(consumer, TinkerSmeltery.searedBricks.getWall(), Ingredient.fromItems(Blocks.STONE_BRICK_WALL), castingFolder + "bricks/wall");
    // other seared
    searedCasting(consumer, TinkerSmeltery.searedCrackedBricks, Ingredient.fromItems(Blocks.CRACKED_STONE_BRICKS), castingFolder + "cracked");
    searedCasting(consumer, TinkerSmeltery.searedFancyBricks, Ingredient.fromItems(Blocks.CHISELED_STONE_BRICKS), castingFolder + "chiseled");
    searedCasting(consumer, TinkerSmeltery.searedPaver, Ingredient.fromItems(Blocks.SMOOTH_STONE), castingFolder + "paver");

    // seared blocks
    String meltingFolder = "smeltery/melting/seared/";

    // double efficiency when using smeltery for grout
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.grout), TinkerFluids.searedStone.get(), FluidValues.INGOT * 2, 1.5f)
                        .build(consumer, modResource(meltingFolder + "grout"));
    // seared stone
    // stairs are here since the cheapest stair recipe is stone cutter, 1 to 1
    MeltingRecipeBuilder.melting(CompoundIngredient.from(Ingredient.fromTag(TinkerTags.Items.SEARED_BLOCKS),
                                                         Ingredient.fromItems(TinkerSmeltery.searedLadder, TinkerSmeltery.searedCobble.getWall(), TinkerSmeltery.searedBricks.getWall(),
                                                                              TinkerSmeltery.searedCobble.getStairs(), TinkerSmeltery.searedStone.getStairs(), TinkerSmeltery.searedBricks.getStairs(), TinkerSmeltery.searedPaver.getStairs())),
                                 TinkerFluids.searedStone.get(), FluidValues.METAL_BRICK, 2.0f)
                        .build(consumer, modResource(meltingFolder + "block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedCobble.getSlab(), TinkerSmeltery.searedStone.getSlab(), TinkerSmeltery.searedBricks.getSlab(), TinkerSmeltery.searedPaver.getSlab()),
                                 TinkerFluids.searedStone.get(), FluidValues.METAL_BRICK / 2, 1.5f)
                        .build(consumer, modResource(meltingFolder + "slab"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedBrick), TinkerFluids.searedStone.get(), FluidValues.INGOT, 1.0f)
                        .build(consumer, modResource(meltingFolder + "brick"));

    // melt down smeltery components
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedFaucet), TinkerFluids.searedStone.get(), FluidValues.INGOT * 3 / 2, 1.5f)
                        .build(consumer, modResource(meltingFolder + "faucet"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedChannel), TinkerFluids.searedStone.get(), FluidValues.INGOT * 5 / 3, 1.5f)
                        .build(consumer, modResource(meltingFolder + "channel"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedBasin, TinkerSmeltery.searedTable), TinkerFluids.searedStone.get(), FluidValues.INGOT * 7, 2.5f)
                        .build(consumer, modResource(meltingFolder + "casting"));
    // tanks
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.searedTank.get(TankType.FUEL_TANK))), TinkerFluids.searedStone.get(), FluidValues.INGOT * 8, 3f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), FluidValues.GLASS_BLOCK))
                        .build(consumer, modResource(meltingFolder + "fuel_tank"));
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.searedTank.get(TankType.INGOT_TANK))), TinkerFluids.searedStone.get(), FluidValues.INGOT * 6, 2.5f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), FluidValues.GLASS_BLOCK * 3))
                        .build(consumer, modResource(meltingFolder + "ingot_tank"));
    MeltingRecipeBuilder.melting(CompoundIngredient.from(NBTIngredient.from(new ItemStack(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE))),
                                                         NBTIngredient.from(new ItemStack(TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE)))),
                                 TinkerFluids.searedStone.get(), FluidValues.INGOT * 4, 2f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), FluidValues.GLASS_BLOCK * 5))
                        .build(consumer, modResource(meltingFolder + "gauge"));
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.searedLantern)), TinkerFluids.searedStone.get(), FluidValues.INGOT * 2, 1.0f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), FluidValues.GLASS_PANE))
                        .addByproduct(new FluidStack(TinkerFluids.moltenIron.get(), FluidValues.INGOT / 3))
                        .build(consumer, modResource(meltingFolder + "lantern"));
    // glass
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedGlass), TinkerFluids.searedStone.get(), FluidValues.INGOT * 4, 2f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), FluidValues.GLASS_BLOCK))
                        .build(consumer, modResource(meltingFolder + "glass"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedGlassPane), TinkerFluids.searedStone.get(), FluidValues.INGOT, 1.0f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), FluidValues.GLASS_PANE))
                        .build(consumer, modResource(meltingFolder + "pane"));
    // controllers
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedMelter), TinkerFluids.searedStone.get(), FluidValues.INGOT * 9, 3.5f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), FluidValues.GLASS_PANE * 5))
                        .build(consumer, modResource(meltingFolder + "melter"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedHeater), TinkerFluids.searedStone.get(), FluidValues.INGOT * 8, 3f)
                        .build(consumer, modResource(meltingFolder + "heater"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.smelteryController), TinkerFluids.moltenCopper.get(), FluidValues.INGOT * 4, 3.5f)
                        .addByproduct(new FluidStack(TinkerFluids.searedStone.get(), FluidValues.INGOT * 8))
                        .build(consumer, modResource("smeltery/melting/copper/smeltery_controller"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedDrain, TinkerSmeltery.searedChute), TinkerFluids.moltenCopper.get(), FluidValues.INGOT * 2, 2.5f)
                        .addByproduct(new FluidStack(TinkerFluids.searedStone.get(), FluidValues.INGOT * 4))
                        .build(consumer, modResource("smeltery/melting/copper/smeltery_io"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedDuct), TinkerFluids.moltenCobalt.get(), FluidValues.INGOT * 2, 2.5f)
                        .addByproduct(new FluidStack(TinkerFluids.searedStone.get(), FluidValues.INGOT * 4))
                        .build(consumer, modResource("smeltery/melting/cobalt/seared_duct"));
    // misc
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerModifiers.searedReinforcement), TinkerFluids.searedStone.get(), FluidValues.INGOT)
                        .addByproduct(new FluidStack(TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_PANE))
                        .build(consumer, modResource(meltingFolder + "reinforcement"));
  }

  private void addFoundryRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "smeltery/scorched/";
    // grout crafting
    ShapelessRecipeBuilder.shapelessRecipe(TinkerSmeltery.netherGrout, 2)
                          .addIngredient(Items.MAGMA_CREAM)
                          .addIngredient(Ingredient.fromItems(Blocks.SOUL_SAND, Blocks.SOUL_SOIL))
                          .addIngredient(Blocks.GRAVEL)
                          .addCriterion("has_item", hasItem(Items.MAGMA_CREAM))
                          .build(consumer, prefix(TinkerSmeltery.netherGrout, folder));

    // scorched bricks from grout
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(TinkerSmeltery.netherGrout), TinkerSmeltery.scorchedBrick, 0.3f, 200)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.netherGrout))
                        .build(consumer, prefix(TinkerSmeltery.scorchedBrick, folder));
    Consumer<Consumer<IFinishedRecipe>> fastGrout = c ->
      CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(TinkerSmeltery.netherGrout), TinkerSmeltery.scorchedBrick, 0.3f, 100)
                          .addCriterion("has_item", hasItem(TinkerSmeltery.netherGrout)).build(c);
    ConditionalRecipe.builder()
                     .addCondition(new ModLoadedCondition("ceramics"))
                     .addRecipe(c -> fastGrout.accept(ConsumerWrapperBuilder.wrap(new ResourceLocation("ceramics", "kiln")).build(c)))
                     .addCondition(TrueCondition.INSTANCE)
                     .addRecipe(fastGrout)
                     .generateAdvancement()
                     .build(consumer, wrap(TinkerSmeltery.scorchedBrick, folder, "_kiln"));

    // block from bricks
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedBricks)
                       .key('b', TinkerSmeltery.scorchedBrick)
                       .patternLine("bb")
                       .patternLine("bb")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, wrap(TinkerSmeltery.scorchedBricks, folder, "_from_brick"));
    // ladder from bricks
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedLadder, 4)
                       .key('b', TinkerSmeltery.scorchedBrick)
                       .key('B', TinkerTags.Items.SCORCHED_BLOCKS)
                       .patternLine("b b")
                       .patternLine("b b")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, prefix(TinkerSmeltery.scorchedLadder, folder));

    // stone -> polished
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.polishedScorchedStone, 4)
                       .key('b', TinkerSmeltery.scorchedStone)
                       .patternLine("bb")
                       .patternLine("bb")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedStone))
                       .build(consumer, wrap(TinkerSmeltery.polishedScorchedStone, folder, "_crafting"));
    // polished -> bricks
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedBricks, 4)
                       .key('b', TinkerSmeltery.polishedScorchedStone)
                       .patternLine("bb")
                       .patternLine("bb")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.polishedScorchedStone))
                       .build(consumer, wrap(TinkerSmeltery.scorchedBricks, folder, "_crafting"));
    // stone -> road
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(TinkerSmeltery.scorchedStone), TinkerSmeltery.scorchedRoad, 0.1f, 200)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedStone))
                        .build(consumer, wrap(TinkerSmeltery.scorchedRoad, folder, "_smelting"));
    // brick slabs -> chiseled
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.chiseledScorchedBricks)
                       .key('s', TinkerSmeltery.scorchedBricks.getSlab())
                       .patternLine("s")
                       .patternLine("s")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBricks.getSlab()))
                       .build(consumer, wrap(TinkerSmeltery.chiseledScorchedBricks, folder, "_crafting"));
    // stonecutting
    this.scorchedStonecutter(consumer, TinkerSmeltery.polishedScorchedStone, folder);
    this.scorchedStonecutter(consumer, TinkerSmeltery.scorchedBricks, folder);
    this.scorchedStonecutter(consumer, TinkerSmeltery.chiseledScorchedBricks, folder);

    // scorched glass
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedGlass)
                       .key('b', TinkerSmeltery.scorchedBrick)
                       .key('G', Tags.Items.GEMS_QUARTZ)
                       .patternLine(" b ")
                       .patternLine("bGb")
                       .patternLine(" b ")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, prefix(TinkerSmeltery.scorchedGlass, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedGlassPane, 16)
                       .key('#', TinkerSmeltery.scorchedGlass)
                       .patternLine("###")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedGlass))
                       .build(consumer, prefix(TinkerSmeltery.scorchedGlassPane, folder));

    // stairs, slabs, and fences
    this.slabStairsCrafting(consumer, TinkerSmeltery.scorchedBricks, folder, true);
    this.slabStairsCrafting(consumer, TinkerSmeltery.scorchedRoad, folder, true);
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedBricks.getFence(), 6)
                       .key('B', TinkerSmeltery.scorchedBricks)
                       .key('b', TinkerSmeltery.scorchedBrick)
                       .patternLine("BbB")
                       .patternLine("BbB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBricks))
                       .build(consumer, prefix(TinkerSmeltery.scorchedBricks.getFence(), folder));

    // tanks
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedTank.get(TankType.FUEL_TANK))
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .key('B', Tags.Items.GEMS_QUARTZ)
                       .patternLine("###")
                       .patternLine("#B#")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, modResource(folder + "fuel_tank"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE))
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .key('B', Tags.Items.GEMS_QUARTZ)
                       .patternLine("#B#")
                       .patternLine("BBB")
                       .patternLine("#B#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, modResource(folder + "fuel_gauge"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedTank.get(TankType.INGOT_TANK))
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .key('B', Tags.Items.GEMS_QUARTZ)
                       .patternLine("#B#")
                       .patternLine("#B#")
                       .patternLine("#B#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, modResource(folder + "ingot_tank"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE))
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .key('B', Tags.Items.GEMS_QUARTZ)
                       .patternLine("B#B")
                       .patternLine("#B#")
                       .patternLine("B#B")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, modResource(folder + "ingot_gauge"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedLantern.get(), 3)
                       .key('C', Tags.Items.INGOTS_IRON)
                       .key('B', TinkerSmeltery.scorchedBrick)
                       .key('P', TinkerSmeltery.scorchedGlassPane)
                       .patternLine(" C ")
                       .patternLine("PPP")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, modResource(folder + "lantern"));

    // fluid transfer
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedFaucet.get(), 2)
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .patternLine("# #")
                       .patternLine(" # ")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, modResource(folder + "faucet"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedChannel.get(), 3)
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .patternLine("# #")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, modResource(folder + "channel"));

    // casting
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedBasin.get())
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .patternLine("# #")
                       .patternLine("# #")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, modResource(folder + "basin"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedTable.get())
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .patternLine("###")
                       .patternLine("# #")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, modResource(folder + "table"));


    // peripherals
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedDrain)
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .key('C', TinkerCommons.obsidianPane)
                       .patternLine("# #")
                       .patternLine("C C")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, modResource(folder + "drain"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedChute)
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .key('C', TinkerCommons.obsidianPane)
                       .patternLine("#C#")
                       .patternLine("   ")
                       .patternLine("#C#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, modResource(folder + "chute"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedDuct)
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .key('C', TinkerMaterials.cobalt.getIngotTag())
                       .patternLine("# #")
                       .patternLine("C C")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerMaterials.cobalt.getIngotTag()))
                       .build(consumer, modResource(folder + "duct"));

    // controllers
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedAlloyer)
                       .key('G', Ingredient.fromItems(TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE), TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE)))
                       .key('B', TinkerSmeltery.scorchedBrick)
                       .patternLine("BGB")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, modResource(folder + "alloyer"));

    // casting
    String castingFolder = "smeltery/casting/scorched/";
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedStone)
                            .setFluidAndTime(TinkerFluids.scorchedStone, false, FluidValues.METAL_BRICK)
                            .build(consumer, modResource(castingFolder + "stone_from_scorched"));
    this.ingotCasting(consumer, TinkerFluids.scorchedStone, TinkerSmeltery.scorchedBrick, castingFolder + "brick");
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedGlass)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, false, FluidValues.GEM)
                            .setCast(TinkerSmeltery.scorchedBricks, true)
                            .build(consumer, modResource(castingFolder + "glass"));
    // discount for casting panes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.scorchedGlassPane)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, false, FluidValues.GEM / 4)
                            .setCast(TinkerSmeltery.scorchedBrick, true)
                            .build(consumer, modResource(castingFolder + "glass_pane"));
    // craft scorched stone from magma and basalt
    // flint is almost a brick
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.scorchedBrick)
                            .setFluidAndTime(TinkerFluids.magma, true, FluidValues.SLIMEBALL / 2)
                            .setCast(Items.FLINT, true)
                            .build(consumer, modResource(castingFolder + "brick_composite"));
    scorchedCasting(consumer, TinkerSmeltery.scorchedStone, Ingredient.fromItems(Blocks.BASALT , Blocks.GRAVEL), castingFolder + "stone_from_magma");
    scorchedCasting(consumer, TinkerSmeltery.polishedScorchedStone, Ingredient.fromItems(Blocks.POLISHED_BASALT), castingFolder + "polished_from_magma");
    // foundry controller
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.foundryController)
                            .setCast(TinkerSmeltery.scorchedBricks, true) // TODO: can I find a "heater" for the nether?
                            .setFluidAndTime(TinkerFluids.moltenObsidian, false, FluidValues.GLASS_BLOCK)
                            .build(consumer, prefix(TinkerSmeltery.foundryController, castingFolder));


    // melting
    String meltingFolder = "smeltery/melting/scorched/";

    // double efficiency when using smeltery for grout
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.netherGrout), TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 2, 1.5f)
                        .build(consumer, modResource(meltingFolder + "grout"));

    // scorched stone
    // stairs are here since the cheapest stair recipe is stone cutter, 1 to 1
    MeltingRecipeBuilder.melting(CompoundIngredient.from(Ingredient.fromTag(TinkerTags.Items.SCORCHED_BLOCKS),
                                                         Ingredient.fromItems(TinkerSmeltery.scorchedLadder, TinkerSmeltery.scorchedBricks.getStairs(), TinkerSmeltery.scorchedRoad.getStairs())),
                                 TinkerFluids.scorchedStone.get(), FluidValues.METAL_BRICK, 2.0f)
                        .build(consumer, modResource(meltingFolder + "block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedBricks.getSlab(), TinkerSmeltery.scorchedBricks.getSlab(), TinkerSmeltery.scorchedRoad.getSlab()),
                                 TinkerFluids.scorchedStone.get(), FluidValues.METAL_BRICK / 2, 1.5f)
                        .build(consumer, modResource(meltingFolder + "slab"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedBrick), TinkerFluids.scorchedStone.get(), FluidValues.INGOT, 1.0f)
                        .build(consumer, modResource(meltingFolder + "brick"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedBricks.getFence()), TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 3, 1.0f)
                        .build(consumer, modResource(meltingFolder + "fence"));

    // melt down foundry components
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedFaucet), TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 3 / 2, 1.5f)
                        .build(consumer, modResource(meltingFolder + "faucet"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedChannel), TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 5 / 3, 1.5f)
                        .build(consumer, modResource(meltingFolder + "channel"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedBasin, TinkerSmeltery.scorchedTable), TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 7, 2.5f)
                        .build(consumer, modResource(meltingFolder + "casting"));
    // tanks
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.scorchedTank.get(TankType.FUEL_TANK))), TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 8, 3f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), FluidValues.GEM))
                        .build(consumer, modResource(meltingFolder + "fuel_tank"));
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.scorchedTank.get(TankType.INGOT_TANK))), TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 6, 2.5f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), FluidValues.GEM * 3))
                        .build(consumer, modResource(meltingFolder + "ingot_tank"));
    MeltingRecipeBuilder.melting(CompoundIngredient.from(NBTIngredient.from(new ItemStack(TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE))),
                                                         NBTIngredient.from(new ItemStack(TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE)))),
                                 TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 4, 2f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), FluidValues.GEM * 5))
                        .build(consumer, modResource(meltingFolder + "gauge"));
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.scorchedLantern)), TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 2, 1.0f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), FluidValues.GEM / 4))
                        .addByproduct(new FluidStack(TinkerFluids.moltenIron.get(), FluidValues.INGOT / 3))
                        .build(consumer, modResource(meltingFolder + "lantern"));
    // glass
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedGlass), TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 4, 2f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), FluidValues.GEM))
                        .build(consumer, modResource(meltingFolder + "glass"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedGlassPane), TinkerFluids.scorchedStone.get(), FluidValues.INGOT, 1.0f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), FluidValues.GEM / 4))
                        .build(consumer, modResource(meltingFolder + "pane"));
    // controllers
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedAlloyer), TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 9, 3.5f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), FluidValues.GEM * 5))
                        .build(consumer, modResource(meltingFolder + "melter"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.foundryController), TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_BLOCK, 3.5f)
                        .addByproduct(new FluidStack(TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 4))
                        .build(consumer, modResource("smeltery/melting/obsidian/foundry_controller"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedDrain, TinkerSmeltery.scorchedChute), TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_PANE * 2, 2.5f)
                        .addByproduct(new FluidStack(TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 4))
                        .build(consumer, modResource("smeltery/melting/obsidian/foundry_io"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedDuct), TinkerFluids.moltenCobalt.get(), FluidValues.INGOT * 2, 2.5f)
                        .addByproduct(new FluidStack(TinkerFluids.scorchedStone.get(), FluidValues.INGOT * 4))
                        .build(consumer, modResource("smeltery/melting/cobalt/scorched_duct"));
  }

  private void addCastingRecipes(Consumer<IFinishedRecipe> consumer) {
    // Pure Fluid Recipes
    String folder = "smeltery/casting/";

    // container filling
    ContainerFillingRecipeBuilder.tableRecipe(Items.BUCKET, FluidAttributes.BUCKET_VOLUME)
                                 .build(consumer, modResource(folder + "filling/bucket"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.copperCan, FluidValues.INGOT)
                                 .build(consumer, modResource(folder + "filling/copper_can"));
    // tank filling - seared
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.INGOT_TANK), FluidValues.INGOT)
                                 .build(consumer, modResource(folder + "filling/seared_ingot_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE), FluidValues.INGOT)
                                 .build(consumer, modResource(folder + "filling/seared_ingot_gauge"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.FUEL_TANK), FluidAttributes.BUCKET_VOLUME / 4)
                                 .build(consumer, modResource(folder + "filling/seared_fuel_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE), FluidAttributes.BUCKET_VOLUME / 4)
                                 .build(consumer, modResource(folder + "filling/seared_fuel_gauge"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.searedLantern, FluidValues.NUGGET)
                                 .build(consumer, modResource(folder + "filling/seared_lantern_pixel"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedLantern, FluidAttributes.BUCKET_VOLUME / 10)
                                 .build(consumer, modResource(folder + "filling/seared_lantern_full"));
    // tank filling - scorched
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.INGOT_TANK), FluidValues.INGOT)
                                 .build(consumer, modResource(folder + "filling/scorched_ingot_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE), FluidValues.INGOT)
                                 .build(consumer, modResource(folder + "filling/scorched_ingot_gauge"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.FUEL_TANK), FluidAttributes.BUCKET_VOLUME / 4)
                                 .build(consumer, modResource(folder + "filling/scorched_fuel_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE), FluidAttributes.BUCKET_VOLUME / 4)
                                 .build(consumer, modResource(folder + "filling/scorched_fuel_gauge"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.scorchedLantern, FluidValues.NUGGET)
                                 .build(consumer, modResource(folder + "filling/scorched_lantern_pixel"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedLantern, FluidAttributes.BUCKET_VOLUME / 10)
                                 .build(consumer, modResource(folder + "filling/scorched_lantern_full"));

    // Slime
    String slimeFolder = folder + "slime/";
    this.slimeCasting(consumer, TinkerFluids.blood, false, SlimeType.BLOOD, slimeFolder);
    ItemCastingRecipeBuilder.tableRecipe(TinkerMaterials.bloodbone)
                            .setFluidAndTime(TinkerFluids.blood, false, FluidValues.SLIMEBALL)
                            .setCast(Tags.Items.BONES, true)
                            .build(consumer, modResource(slimeFolder + "blood/bone"));
    this.slimeCasting(consumer, TinkerFluids.earthSlime, true, SlimeType.EARTH, slimeFolder);
    this.slimeCasting(consumer, TinkerFluids.skySlime, false, SlimeType.SKY, slimeFolder);
    this.slimeCasting(consumer, TinkerFluids.enderSlime, false, SlimeType.ENDER, slimeFolder);
    // magma cream
    ItemCastingRecipeBuilder.basinRecipe(Blocks.MAGMA_BLOCK)
                            .setFluidAndTime(TinkerFluids.magma, true, FluidValues.SLIME_CONGEALED)
                            .build(consumer, modResource(slimeFolder + "magma_block"));

    // glass
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.clearGlass)
                            .setFluidAndTime(TinkerFluids.moltenGlass, false, FluidValues.GLASS_BLOCK)
                            .build(consumer, modResource(folder + "glass/block"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.clearGlassPane)
                            .setFluidAndTime(TinkerFluids.moltenGlass, false, FluidValues.GLASS_PANE)
                            .build(consumer, modResource(folder + "glass/pane"));
    // soul glass
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.soulGlass)
                            .setFluidAndTime(TinkerFluids.liquidSoul, false, FluidValues.GLASS_BLOCK)
                            .build(consumer, modResource(folder + "soul/glass"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.soulGlassPane)
                            .setFluidAndTime(TinkerFluids.liquidSoul, false, FluidValues.GLASS_PANE)
                            .build(consumer, modResource(folder + "soul/pane"));

    // clay
    ItemCastingRecipeBuilder.basinRecipe(Blocks.TERRACOTTA)
                            .setFluidAndTime(TinkerFluids.moltenClay, false, FluidValues.SLIME_CONGEALED)
                            .build(consumer, modResource(folder + "clay/block"));
    this.ingotCasting(consumer, TinkerFluids.moltenClay, false, FluidValues.SLIMEBALL, Items.BRICK, folder + "clay/brick");
    this.tagCasting(consumer, TinkerFluids.moltenClay, false, FluidValues.SLIMEBALL, TinkerSmeltery.plateCast, "plates/brick", folder + "clay/plate", true);

    // emeralds
    this.gemCasting(consumer, TinkerFluids.moltenEmerald, Items.EMERALD, folder + "emerald/gem");
    ItemCastingRecipeBuilder.basinRecipe(Blocks.EMERALD_BLOCK)
                            .setFluidAndTime(TinkerFluids.moltenEmerald, false, FluidValues.GEM_BLOCK)
                            .build(consumer, modResource(folder + "emerald/block"));

    // quartz
    this.gemCasting(consumer, TinkerFluids.moltenQuartz, Items.QUARTZ, folder + "quartz/gem");
    ItemCastingRecipeBuilder.basinRecipe(Blocks.QUARTZ_BLOCK)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, false, FluidValues.GEM * 4)
                            .build(consumer, modResource(folder + "quartz/block"));

    // diamond
    this.gemCasting(consumer, TinkerFluids.moltenDiamond, Items.DIAMOND, folder + "diamond/gem");
    ItemCastingRecipeBuilder.basinRecipe(Blocks.DIAMOND_BLOCK)
                            .setFluidAndTime(TinkerFluids.moltenDiamond, false, FluidValues.GEM_BLOCK)
                            .build(consumer, modResource(folder + "diamond/block"));

    // ender pearls
    ItemCastingRecipeBuilder.tableRecipe(Items.ENDER_PEARL)
                            .setFluidAndTime(TinkerFluids.moltenEnder, true, FluidValues.SLIMEBALL)
                            .build(consumer, modResource(folder + "ender/pearl"));
    ItemCastingRecipeBuilder.tableRecipe(Items.ENDER_EYE)
                            .setFluidAndTime(TinkerFluids.moltenEnder, true, FluidValues.SLIMEBALL)
                            .setCast(Items.BLAZE_POWDER, true)
                            .build(consumer, modResource(folder + "ender/eye"));

    // obsidian
    ItemCastingRecipeBuilder.basinRecipe(Blocks.OBSIDIAN)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, false, FluidValues.GLASS_BLOCK)
                            .build(consumer, modResource(folder + "obsidian/block"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.obsidianPane)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, false, FluidValues.GLASS_PANE)
                            .build(consumer, modResource(folder + "obsidian/pane"));
    // Molten objects with Bucket, Block, Ingot, and Nugget forms with standard values
    String metalFolder = folder + "metal/";
    this.metalCasting(consumer, TinkerFluids.moltenIron,      true, Items.IRON_BLOCK,       Items.IRON_INGOT,      Items.IRON_NUGGET,               metalFolder, "iron");
    this.metalCasting(consumer, TinkerFluids.moltenGold,      true, Items.GOLD_BLOCK,       Items.GOLD_INGOT,      Items.GOLD_NUGGET,               metalFolder, "gold");
    this.metalCasting(consumer, TinkerFluids.moltenNetherite, true, Blocks.NETHERITE_BLOCK, Items.NETHERITE_INGOT, TinkerMaterials.netheriteNugget, metalFolder, "netherite");
    this.ingotCasting(consumer, TinkerFluids.moltenDebris, false, Items.NETHERITE_SCRAP, metalFolder + "netherite/scrap");
    this.tagCasting(consumer, TinkerFluids.moltenDebris, false, FluidValues.NUGGET, TinkerSmeltery.nuggetCast,
                    TinkerTags.Items.NUGGETS_NETHERITE_SCRAP.getName().getPath(), metalFolder + "netherite/debris_nugget", false);

    // anything common uses tag output, if its unique to us (slime metals mostly), use direct output
    // ores
    this.metalTagCasting(consumer, TinkerFluids.moltenCopper, "copper", metalFolder, true);
    this.metalTagCasting(consumer, TinkerFluids.moltenCobalt, "cobalt", metalFolder, true);
    // tier 3 alloys
    this.metalTagCasting(consumer, TinkerFluids.moltenTinkersBronze, "silicon_bronze", metalFolder, true);
    this.metalTagCasting(consumer, TinkerFluids.moltenRoseGold, "rose_gold", metalFolder, true);
    this.metalCasting(consumer, TinkerFluids.moltenSlimesteel, TinkerMaterials.slimesteel, metalFolder, "slimesteel");
    this.metalCasting(consumer, TinkerFluids.moltenPigIron, TinkerMaterials.pigIron, metalFolder, "pig_iron");
    // tier 4 alloys
    this.metalTagCasting(consumer, TinkerFluids.moltenManyullyn, "manyullyn", metalFolder, true);
    this.metalTagCasting(consumer, TinkerFluids.moltenHepatizon, "hepatizon", metalFolder, true);
    this.metalCasting(consumer, TinkerFluids.moltenQueensSlime, TinkerMaterials.queensSlime, metalFolder, "queens_slime");
    this.metalCasting(consumer, TinkerFluids.moltenSoulsteel, TinkerMaterials.soulsteel, metalFolder, "soulsteel");
    // tier 5 alloys
    this.metalCasting(consumer, TinkerFluids.moltenKnightslime, TinkerMaterials.knightslime, metalFolder, "knightslime");

    // compat
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      this.metalTagCasting(consumer, compat.getFluid(), compat.getName(), metalFolder, false);
    }

    // water
    String waterFolder = folder + "water/";
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.mudBricks)
                            .setFluidAndTime(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 10))
                            .setCast(Items.DIRT, true)
                            .build(consumer, prefix(TinkerCommons.mudBricks, waterFolder));
    // casting concrete
    BiConsumer<Block,Block> concreteCasting = (powder, block) ->
      ItemCastingRecipeBuilder.basinRecipe(block)
                              .setFluidAndTime(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 10))
                              .setCast(powder, true)
                              .build(consumer, prefix(block, waterFolder));
    concreteCasting.accept(Blocks.WHITE_CONCRETE_POWDER,      Blocks.WHITE_CONCRETE);
    concreteCasting.accept(Blocks.ORANGE_CONCRETE_POWDER,     Blocks.ORANGE_CONCRETE);
    concreteCasting.accept(Blocks.MAGENTA_CONCRETE_POWDER,    Blocks.MAGENTA_CONCRETE);
    concreteCasting.accept(Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE);
    concreteCasting.accept(Blocks.YELLOW_CONCRETE_POWDER,     Blocks.YELLOW_CONCRETE);
    concreteCasting.accept(Blocks.LIME_CONCRETE_POWDER,       Blocks.LIME_CONCRETE);
    concreteCasting.accept(Blocks.PINK_CONCRETE_POWDER,       Blocks.PINK_CONCRETE);
    concreteCasting.accept(Blocks.GRAY_CONCRETE_POWDER,       Blocks.GRAY_CONCRETE);
    concreteCasting.accept(Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE);
    concreteCasting.accept(Blocks.CYAN_CONCRETE_POWDER,       Blocks.CYAN_CONCRETE);
    concreteCasting.accept(Blocks.PURPLE_CONCRETE_POWDER,     Blocks.PURPLE_CONCRETE);
    concreteCasting.accept(Blocks.BLUE_CONCRETE_POWDER,       Blocks.BLUE_CONCRETE);
    concreteCasting.accept(Blocks.BROWN_CONCRETE_POWDER,      Blocks.BROWN_CONCRETE);
    concreteCasting.accept(Blocks.GREEN_CONCRETE_POWDER,      Blocks.GREEN_CONCRETE);
    concreteCasting.accept(Blocks.RED_CONCRETE_POWDER,        Blocks.RED_CONCRETE);
    concreteCasting.accept(Blocks.BLACK_CONCRETE_POWDER,      Blocks.BLACK_CONCRETE);

    // misc
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.lavawood)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10))
                            .setCast(ItemTags.PLANKS, true)
                            .build(consumer, prefix(TinkerCommons.lavawood, folder));
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.blazewood)
                            .setFluidAndTime(TinkerFluids.blazingBlood, false, FluidAttributes.BUCKET_VOLUME / 10)
                            .setCast(new IngredientIntersection(Ingredient.fromTag(ItemTags.PLANKS), Ingredient.fromTag(ItemTags.NON_FLAMMABLE_WOOD)), true)
                            .build(consumer, prefix(TinkerCommons.blazewood, folder));

    // cast molten blaze into blaze rods
    castingWithCast(consumer, TinkerFluids.blazingBlood, false, FluidAttributes.BUCKET_VOLUME / 10, TinkerSmeltery.rodCast, Items.BLAZE_ROD, folder + "blaze/rod");
    ItemCastingRecipeBuilder.tableRecipe(TinkerMaterials.blazingBone)
                            .setFluidAndTime(TinkerFluids.blazingBlood, false, FluidAttributes.BUCKET_VOLUME / 5)
                            .setCast(TinkerTags.Items.WITHER_BONES, true)
                            .build(consumer, modResource(folder + "blaze/bone"));

    // Cast recipes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.blankCast)
                            .setFluidAndTime(TinkerFluids.moltenGold, true, FluidValues.INGOT)
                            .setSwitchSlots()
                            .build(consumer, modResource(folder + "casts/blank"));

    String castFolder = "smeltery/casts/";
    this.castCreation(consumer, Tags.Items.INGOTS, TinkerSmeltery.ingotCast, castFolder);
    this.castCreation(consumer, Tags.Items.NUGGETS, TinkerSmeltery.nuggetCast, castFolder);
    this.castCreation(consumer, Tags.Items.GEMS, TinkerSmeltery.gemCast, castFolder);
    this.castCreation(consumer, Tags.Items.RODS, TinkerSmeltery.rodCast, castFolder);
    // other casts are added if needed
    this.castCreation(withCondition(consumer, tagCondition("plates")), getTag("forge", "plates"), TinkerSmeltery.plateCast, castFolder);
    this.castCreation(withCondition(consumer, tagCondition("gears")), getTag("forge", "gears"), TinkerSmeltery.gearCast, castFolder);
    this.castCreation(withCondition(consumer, tagCondition("coins")), getTag("forge", "coins"), TinkerSmeltery.coinCast, castFolder);
    this.castCreation(withCondition(consumer, tagCondition("wires")), getTag("forge", "wires"), TinkerSmeltery.wireCast, castFolder);

    // misc casting - gold
    ItemCastingRecipeBuilder.tableRecipe(Items.GOLDEN_APPLE)
                            .setFluidAndTime(TinkerFluids.moltenGold, true, FluidValues.INGOT * 8)
                            .setCast(Items.APPLE, true)
                            .build(consumer, modResource(metalFolder + "gold/apple"));
    ItemCastingRecipeBuilder.tableRecipe(Items.GLISTERING_MELON_SLICE)
                            .setFluidAndTime(TinkerFluids.moltenGold, true, FluidValues.NUGGET * 8)
                            .setCast(Items.MELON_SLICE, true)
                            .build(consumer, modResource(metalFolder + "gold/melon"));
    ItemCastingRecipeBuilder.tableRecipe(Items.GOLDEN_CARROT)
                            .setFluidAndTime(TinkerFluids.moltenGold, true, FluidValues.NUGGET * 8)
                            .setCast(Items.CARROT, true)
                            .build(consumer, modResource(metalFolder + "gold/carrot"));
    ItemCastingRecipeBuilder.tableRecipe(Items.CLOCK)
                            .setFluidAndTime(TinkerFluids.moltenGold, true, FluidValues.INGOT * 4)
                            .setCast(Items.REDSTONE, true)
                            .build(consumer, modResource(metalFolder + "gold/clock"));
    // misc casting - iron
    ItemCastingRecipeBuilder.tableRecipe(Blocks.IRON_BARS)  // cheaper by 6mb, not a duplication as the melting recipe was adjusted too (like panes)
                            .setFluidAndTime(TinkerFluids.moltenIron, true, FluidValues.NUGGET * 3)
                            .build(consumer, modResource(metalFolder + "iron/bars"));
    ItemCastingRecipeBuilder.tableRecipe(Items.LANTERN)
                            .setFluidAndTime(TinkerFluids.moltenIron, true, FluidValues.NUGGET * 8)
                            .setCast(Blocks.TORCH, true)
                            .build(consumer, modResource(metalFolder + "iron/lantern"));
    ItemCastingRecipeBuilder.tableRecipe(Items.SOUL_LANTERN)
                            .setFluidAndTime(TinkerFluids.moltenIron, true, FluidValues.NUGGET * 8)
                            .setCast(Blocks.SOUL_TORCH, true)
                            .build(consumer, modResource(metalFolder + "iron/soul_lantern"));
    ItemCastingRecipeBuilder.tableRecipe(Items.COMPASS)
                            .setFluidAndTime(TinkerFluids.moltenIron, true, FluidValues.INGOT * 4)
                            .setCast(Items.REDSTONE, true)
                            .build(consumer, modResource(metalFolder + "iron/compass"));
    // ender chest
    ItemCastingRecipeBuilder.basinRecipe(Blocks.ENDER_CHEST)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, false, FluidValues.GLASS_BLOCK * 8)
                            .setCast(Items.ENDER_EYE, true)
                            .build(consumer, modResource(folder + "obsidian/chest"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.nahuatl)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, false, FluidAttributes.BUCKET_VOLUME)
                            .setCast(ItemTags.PLANKS, true)
                            .build(consumer, modResource(folder + "obsidian/nahuatl"));
    // overworld stones from quartz
    ItemCastingRecipeBuilder.basinRecipe(Blocks.ANDESITE)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, false, FluidValues.GEM / 2)
                            .setCast(Tags.Items.COBBLESTONE, true)
                            .build(consumer, prefix(Blocks.ANDESITE, folder + "quartz/"));
    ItemCastingRecipeBuilder.basinRecipe(Blocks.DIORITE)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, false, FluidValues.GEM / 2)
                            .setCast(Blocks.ANDESITE, true)
                            .build(consumer, prefix(Blocks.DIORITE, folder + "quartz/"));
    ItemCastingRecipeBuilder.basinRecipe(Blocks.GRANITE)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, false, FluidValues.GEM)
                            .setCast(Blocks.DIORITE, true)
                            .build(consumer, prefix(Blocks.GRANITE, folder + "quartz/"));
  }

  private void addMeltingRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "smeltery/melting/";

    // water from ice
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ICE), Fluids.WATER, FluidAttributes.BUCKET_VOLUME, 1.0f)
                        .build(consumer, modResource(folder + "water/ice"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.PACKED_ICE), Fluids.WATER, FluidAttributes.BUCKET_VOLUME * 9, 3.0f)
                        .build(consumer, modResource(folder + "water/packed_ice"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.BLUE_ICE), Fluids.WATER, FluidAttributes.BUCKET_VOLUME * 81, 9.0f)
                        .build(consumer, modResource(folder + "water/blue_ice"));
    // water from snow
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SNOWBALL), Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 8, 0.5f)
                        .build(consumer, modResource(folder + "water/snowball"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SNOW_BLOCK), Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 2, 0.75f)
                        .build(consumer, modResource(folder + "water/snow_block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SNOW), Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 8, 0.5f)
                        .build(consumer, modResource(folder + "water/snow_layer"));

    // ores
    String metalFolder = folder + "metal/";
    metalMelting(consumer, TinkerFluids.moltenIron.get(), "iron", true, metalFolder, false, Byproduct.NICKEL, Byproduct.COPPER);
    metalMelting(consumer, TinkerFluids.moltenGold.get(), "gold", true, metalFolder, false, Byproduct.COPPER);
    metalMelting(consumer, TinkerFluids.moltenCopper.get(), "copper", true, metalFolder, false, Byproduct.SMALL_GOLD);
    metalMelting(consumer, TinkerFluids.moltenCobalt.get(), "cobalt", true, metalFolder, false, Byproduct.IRON);

    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.ORES_NETHERITE_SCRAP), TinkerFluids.moltenDebris.get(), FluidValues.INGOT, 2.0f)
                        .setOre()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), FluidValues.GEM / 3))
                        .addByproduct(new FluidStack(TinkerFluids.moltenGold.get(), FluidValues.INGOT))
                        .build(consumer, modResource(metalFolder + "molten_debris/ore"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.INGOTS_NETHERITE_SCRAP), TinkerFluids.moltenDebris.get(), FluidValues.INGOT, 1.0f)
                        .build(consumer, modResource(metalFolder + "molten_debris/scrap"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.NUGGETS_NETHERITE_SCRAP), TinkerFluids.moltenDebris.get(), FluidValues.NUGGET, 1 / 3f)
                        .build(consumer, modResource(metalFolder + "molten_debris/debris_nugget"));
    
    // tier 3
    metalMelting(consumer, TinkerFluids.moltenSlimesteel.get(), "slimesteel", false, metalFolder, false);
    metalMelting(consumer, TinkerFluids.moltenTinkersBronze.get(), "silicon_bronze", false, metalFolder, false);
    metalMelting(consumer, TinkerFluids.moltenRoseGold.get(), "rose_gold", false, metalFolder, false);
    metalMelting(consumer, TinkerFluids.moltenPigIron.get(), "pig_iron", false, metalFolder, false);
    // tier 4
    metalMelting(consumer, TinkerFluids.moltenManyullyn.get(), "manyullyn", false, metalFolder, false);
    metalMelting(consumer, TinkerFluids.moltenHepatizon.get(), "hepatizon", false, metalFolder, false);
    metalMelting(consumer, TinkerFluids.moltenQueensSlime.get(), "queens_slime", false, metalFolder, false);
    metalMelting(consumer, TinkerFluids.moltenSoulsteel.get(), "soulsteel", false, metalFolder, false);
    metalMelting(consumer, TinkerFluids.moltenNetherite.get(), "netherite", false, metalFolder, false);
    // tier 5
    metalMelting(consumer, TinkerFluids.moltenKnightslime.get(), "knightslime", false, metalFolder, false);

    // compat
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      this.metalMelting(consumer, compat.getFluid().get(), compat.getName(), compat.isOre(), compat.hasDust(), metalFolder, true, compat.getByproducts());
    }

    // blood
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ROTTEN_FLESH), TinkerFluids.blood.get(), FluidValues.SLIMEBALL / 5, 1.0f)
                        .build(consumer, modResource(folder + "slime/blood/flesh"));
    // venom
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SPIDER_EYE), TinkerFluids.venom.get(), FluidValues.SLIMEBALL / 5, 1.0f)
                        .build(consumer, modResource(folder + "venom/eye"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.FERMENTED_SPIDER_EYE), TinkerFluids.venom.get(), FluidValues.SLIMEBALL * 2 / 5, 1.0f)
                        .build(consumer, modResource(folder + "venom/fermented_eye"));

    // glass
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.SAND), TinkerFluids.moltenGlass.get(), FluidValues.GLASS_BLOCK, 1.5f)
                        .build(consumer, modResource(folder + "glass/sand"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GLASS), TinkerFluids.moltenGlass.get(), FluidValues.GLASS_BLOCK, 1.0f)
                        .build(consumer, modResource(folder + "glass/block"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GLASS_PANES), TinkerFluids.moltenGlass.get(), FluidValues.GLASS_PANE, 0.5f)
                        .build(consumer, modResource(folder + "glass/pane"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GLASS_BOTTLE), TinkerFluids.moltenGlass.get(), FluidValues.GLASS_BLOCK, 1.25f)
                        .build(consumer, modResource(folder + "glass/bottle"));
    // melt extra sand casts back
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.blankCast.getSand(), TinkerSmeltery.blankCast.getRedSand()),
                                 TinkerFluids.moltenGlass.get(), FluidValues.GLASS_PANE, 0.75f)
                        .build(consumer, modResource(folder + "glass/sand_cast"));

    // liquid soul
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.SOUL_SAND, Blocks.SOUL_SOIL), TinkerFluids.liquidSoul.get(), FluidValues.GLASS_BLOCK, 1.5f)
                        .build(consumer, modResource(folder + "soul/sand"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerCommons.soulGlass), TinkerFluids.liquidSoul.get(), FluidValues.GLASS_BLOCK, 1.0f)
                        .build(consumer, modResource(folder + "soul/glass"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerCommons.soulGlassPane), TinkerFluids.liquidSoul.get(), FluidValues.GLASS_PANE, 0.5f)
                        .build(consumer, modResource(folder + "soul/pane"));

    // clay
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.CLAY), TinkerFluids.moltenClay.get(), FluidValues.SLIME_CONGEALED, 1.0f)
                        .build(consumer, modResource(folder + "clay/block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.CLAY_BALL), TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL, 0.5f)
                        .build(consumer, modResource(folder + "clay/ball"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.FLOWER_POT), TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 3, 2.0f)
                        .build(consumer, modResource(folder + "clay/pot"));
    metalMeltingBase(consumer, TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL, "plates/brick", 1.0f, folder + "clay/plate", true);
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
    MeltingRecipeBuilder.melting(terracottaBlock, TinkerFluids.moltenClay.get(), FluidValues.SLIME_CONGEALED, 2.0f)
                        .build(consumer, modResource(folder + "clay/terracotta"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.BRICK), TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL, 1.0f)
                        .build(consumer, modResource(folder + "clay/brick"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.BRICK_SLAB),
                                 TinkerFluids.moltenClay.get(), FluidValues.SLIME_CONGEALED / 2, 1.5f)
                        .build(consumer, modResource(folder + "clay/brick_slab"));

    // slime
    String slimeFolder = folder + "slime/";
    slimeMelting(consumer, TinkerFluids.earthSlime, SlimeType.EARTH, slimeFolder);
    slimeMelting(consumer, TinkerFluids.skySlime, SlimeType.SKY, slimeFolder);
    slimeMelting(consumer, TinkerFluids.enderSlime, SlimeType.ENDER, slimeFolder);
    slimeMelting(consumer, TinkerFluids.blood, SlimeType.BLOOD, slimeFolder);
    // magma cream
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.MAGMA_CREAM), TinkerFluids.magma.get(), FluidValues.SLIMEBALL, 1.0f)
                        .build(consumer, modResource(slimeFolder + "magma/ball"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.MAGMA_BLOCK), TinkerFluids.magma.get(), FluidValues.SLIME_CONGEALED, 3.0f)
                        .build(consumer, modResource(slimeFolder + "magma/block"));

    // copper cans if empty
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.copperCan)), TinkerFluids.moltenCopper.get(), FluidValues.INGOT, 1.0f)
                        .build(consumer, modResource(metalFolder + "copper/can"));
    // ender
    MeltingRecipeBuilder.melting(
      CompoundIngredient.from(Ingredient.fromTag(Tags.Items.ENDER_PEARLS), Ingredient.fromItems(Items.ENDER_EYE)),
      TinkerFluids.moltenEnder.get(), FluidValues.SLIMEBALL, 1.0f)
                        .build(consumer, modResource(folder + "ender/pearl"));

    // obsidian
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.OBSIDIAN), TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_BLOCK, 2.0f)
                        .build(consumer, modResource(folder + "obsidian/block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerCommons.obsidianPane), TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_PANE, 1.5f)
                        .build(consumer, modResource(folder + "obsidian/pane"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.ENDER_CHEST), TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_BLOCK * 8, 5.0f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenEnder.get(), FluidValues.SLIMEBALL))
                        .build(consumer, modResource(folder + "obsidian/chest"));
    metalMeltingBase(consumer, TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_PANE, "dusts/obsidian", 1.0f, folder + "obsidian/dust", true);

    // emerald
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.ORES_EMERALD), TinkerFluids.moltenEmerald.get(), FluidValues.GEM, 1.5f)
                        .setOre()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), FluidValues.GEM / 3))
                        .build(consumer, modResource(folder + "emerald/ore"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), TinkerFluids.moltenEmerald.get(), FluidValues.GEM, 1.0f)
                        .build(consumer, modResource(folder + "emerald/gem"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_EMERALD), TinkerFluids.moltenEmerald.get(), FluidValues.GEM_BLOCK, 3.0f)
                        .build(consumer, modResource(folder + "emerald/block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerModifiers.emeraldReinforcement), TinkerFluids.moltenEmerald.get(), FluidValues.GEM / 3)
                        .addByproduct(new FluidStack(TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_PANE))
                        .build(consumer, modResource(metalFolder + "emerald/reinforcement"));

    // quartz
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.ORES_QUARTZ), TinkerFluids.moltenQuartz.get(), FluidValues.GEM, 1.5f)
                        .setOre()
                        .addByproduct(new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL))
                        .build(consumer, modResource(folder + "quartz/ore"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GEMS_QUARTZ), TinkerFluids.moltenQuartz.get(), FluidValues.GEM, 1.0f)
                        .build(consumer, modResource(folder + "quartz/gem"));
    MeltingRecipeBuilder.melting(
      CompoundIngredient.from(Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_QUARTZ), Ingredient.fromItems(Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_BRICKS, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ_STAIRS)),
      TinkerFluids.moltenQuartz.get(), FluidValues.GEM * 4, 2.0f)
                        .build(consumer, modResource(folder + "quartz/block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ_SLAB), TinkerFluids.moltenQuartz.get(), FluidValues.GEM * 2, 1.5f)
                        .build(consumer, modResource(folder + "quartz/slab"));

    // diamond
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.ORES_DIAMOND), TinkerFluids.moltenDiamond.get(), FluidValues.GEM, 1.5f)
                        .setOre()
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), FluidValues.GEM / 3))
                        .build(consumer, modResource(folder + "diamond/ore"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GEMS_DIAMOND), TinkerFluids.moltenDiamond.get(), FluidValues.GEM, 1.0f)
                        .build(consumer, modResource(folder + "diamond/gem"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_DIAMOND), TinkerFluids.moltenDiamond.get(), FluidValues.GEM_BLOCK, 3.0f)
                        .build(consumer, modResource(folder + "diamond/block"));

    // iron melting - standard values
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ACTIVATOR_RAIL, Items.DETECTOR_RAIL, Blocks.STONECUTTER, Blocks.PISTON, Blocks.STICKY_PISTON), TinkerFluids.moltenIron.get(), FluidValues.INGOT)
                        .build(consumer, modResource(metalFolder + "iron/ingot_1"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_DOOR, Blocks.SMITHING_TABLE), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 2)
                        .build(consumer, modResource(metalFolder + "iron/ingot_2"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.BUCKET), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 3)
                        .build(consumer, modResource(metalFolder + "iron/bucket"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.COMPASS, Blocks.IRON_TRAPDOOR), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 4)
                        .build(consumer, modResource(metalFolder + "iron/ingot_4"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.BLAST_FURNACE, Blocks.HOPPER, Items.MINECART), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 5)
                        .build(consumer, modResource(metalFolder + "iron/ingot_5"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.CAULDRON), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 7)
                        .build(consumer, modResource(metalFolder + "iron/cauldron"));
    // non-standard
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.CHAIN), TinkerFluids.moltenIron.get(), FluidValues.INGOT + FluidValues.NUGGET * 2)
                        .build(consumer, modResource(metalFolder + "iron/chain"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.ANVIL), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 4 + FluidValues.METAL_BLOCK * 3)
                        .build(consumer, modResource(metalFolder + "iron/anvil"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.RAIL), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 6 / 16)
                        .build(consumer, modResource(metalFolder + "iron/ingot_6_16"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.IRON_BARS), TinkerFluids.moltenIron.get(), FluidValues.NUGGET * 3)
                        .build(consumer, modResource(metalFolder + "iron/nugget_3"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.TRIPWIRE_HOOK), TinkerFluids.moltenIron.get(), FluidValues.INGOT / 2)
                        .build(consumer, modResource(metalFolder + "iron/tripwire"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.LANTERN, Blocks.SOUL_LANTERN), TinkerFluids.moltenIron.get(), FluidValues.NUGGET * 8)
                        .build(consumer, modResource(metalFolder + "iron/lantern"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerModifiers.ironReinforcement), TinkerFluids.moltenIron.get(), FluidValues.NUGGET * 3)
                        .addByproduct(new FluidStack(TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_PANE))
                        .build(consumer, modResource(metalFolder + "iron/reinforcement"));
    // armor
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_HELMET), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 5)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "iron/helmet"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_CHESTPLATE), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 8)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "iron/chestplate"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_LEGGINGS), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 7)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "iron/leggings"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_BOOTS), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 4)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "iron/boots"));
    // tools
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_AXE, Items.IRON_PICKAXE), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 3)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "iron/axes"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_SWORD, Items.IRON_HOE, Items.SHEARS), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 2)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "iron/weapon"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_SHOVEL, Items.FLINT_AND_STEEL, Items.SHIELD), TinkerFluids.moltenIron.get(), FluidValues.INGOT)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "iron/small"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.CROSSBOW), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 3 / 2) // tripwire hook is .5, ingot is 1
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "iron/crossbow"));
    // unique melting
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.IRON_HORSE_ARMOR), TinkerFluids.moltenIron.get(), FluidValues.INGOT * 7)
                        .build(consumer, modResource(metalFolder + "iron/horse_armor"));

    // gold melting
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.GOLD_CASTS), TinkerFluids.moltenGold.get(), FluidValues.INGOT)
                        .build(consumer, modResource(metalFolder + "gold/cast"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.POWERED_RAIL), TinkerFluids.moltenGold.get(), FluidValues.INGOT)
                        .build(consumer, modResource(metalFolder + "gold/powered_rail"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE), TinkerFluids.moltenGold.get(), FluidValues.INGOT * 2)
                        .build(consumer, modResource(metalFolder + "gold/plate"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.CLOCK), TinkerFluids.moltenGold.get(), FluidValues.INGOT * 4)
                        .build(consumer, modResource(metalFolder + "gold/clock"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_APPLE), TinkerFluids.moltenGold.get(), FluidValues.INGOT * 8)
                        .build(consumer, modResource(metalFolder + "gold/apple"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GLISTERING_MELON_SLICE, Items.GOLDEN_CARROT), TinkerFluids.moltenGold.get(), FluidValues.NUGGET * 8)
                        .build(consumer, modResource(metalFolder + "gold/produce"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerModifiers.goldReinforcement), TinkerFluids.moltenGold.get(), FluidValues.NUGGET * 3)
                        .addByproduct(new FluidStack(TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_PANE))
                        .build(consumer, modResource(metalFolder + "gold/reinforcement"));
    // armor
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_HELMET), TinkerFluids.moltenGold.get(), FluidValues.INGOT * 5)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "gold/helmet"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_CHESTPLATE), TinkerFluids.moltenGold.get(), FluidValues.INGOT * 8)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "gold/chestplate"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_LEGGINGS), TinkerFluids.moltenGold.get(), FluidValues.INGOT * 7)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "gold/leggings"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_BOOTS), TinkerFluids.moltenGold.get(), FluidValues.INGOT * 4)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "gold/boots"));
    // tools
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_AXE, Items.GOLDEN_PICKAXE), TinkerFluids.moltenGold.get(), FluidValues.INGOT * 3)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "gold/axes"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_SWORD, Items.GOLDEN_HOE), TinkerFluids.moltenGold.get(), FluidValues.INGOT * 2)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "gold/weapon"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_SHOVEL), TinkerFluids.moltenGold.get(), FluidValues.INGOT)
                        .setDamagable()
                        .build(consumer, modResource(metalFolder + "gold/shovel"));
    // unique melting
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.GOLDEN_HORSE_ARMOR), TinkerFluids.moltenGold.get(), FluidValues.INGOT * 7)
                        .build(consumer, modResource(metalFolder + "gold/horse_armor"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ENCHANTED_GOLDEN_APPLE), TinkerFluids.moltenGold.get(), FluidValues.METAL_BLOCK * 8)
                        .build(consumer, modResource(metalFolder + "gold/enchanted_apple"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.GILDED_BLACKSTONE), TinkerFluids.moltenGold.get(), FluidValues.NUGGET * 6) // bit better than mining before ore bonus
                        .setOre()
                        .build(consumer, modResource(metalFolder + "gold/gilded_blackstone"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.BELL), TinkerFluids.moltenGold.get(), FluidValues.INGOT * 4) // bit arbitrary, I am happy to change the value if someone has a better one
                        .build(consumer, modResource(metalFolder + "gold/bell"));

    // diamond melting
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.JUKEBOX), TinkerFluids.moltenDiamond.get(), FluidValues.GEM)
                        .build(consumer, modResource(folder + "diamond/jukebox"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.ENCHANTING_TABLE), TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 2)
                        .addByproduct(new FluidStack(TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_BLOCK * 4))
                        .build(consumer, modResource(folder + "diamond/enchanting_table"));
    // armor
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_HELMET), TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 5)
                        .setDamagable()
                        .build(consumer, modResource(folder + "diamond/helmet"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_CHESTPLATE), TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 8)
                        .setDamagable()
                        .build(consumer, modResource(folder + "diamond/chestplate"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_LEGGINGS), TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 7)
                        .setDamagable()
                        .build(consumer, modResource(folder + "diamond/leggings"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_BOOTS), TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 4)
                        .setDamagable()
                        .build(consumer, modResource(folder + "diamond/boots"));
    // tools
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_AXE, Items.DIAMOND_PICKAXE), TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 3)
                        .setDamagable()
                        .build(consumer, modResource(folder + "diamond/axes"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_SWORD, Items.DIAMOND_HOE), TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 2)
                        .setDamagable()
                        .build(consumer, modResource(folder + "diamond/weapon"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_SHOVEL), TinkerFluids.moltenDiamond.get(), FluidValues.GEM)
                        .setDamagable()
                        .build(consumer, modResource(folder + "diamond/shovel"));
    // unique melting
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_HORSE_ARMOR), TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 7)
                        .build(consumer, modResource(folder + "diamond/horse_armor"));

    // netherite
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.LODESTONE), TinkerFluids.moltenNetherite.get(), FluidValues.INGOT)
                        .build(consumer, modResource(metalFolder + "netherite/lodestone"));
    // armor
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_HELMET), TinkerFluids.moltenNetherite.get(), FluidValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 5))
                        .build(consumer, modResource(folder + "netherite/helmet"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_CHESTPLATE), TinkerFluids.moltenNetherite.get(), FluidValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 8))
                        .build(consumer, modResource(folder + "netherite/chestplate"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_LEGGINGS), TinkerFluids.moltenNetherite.get(), FluidValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 7))
                        .build(consumer, modResource(folder + "netherite/leggings"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_BOOTS), TinkerFluids.moltenNetherite.get(), FluidValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 4))
                        .build(consumer, modResource(folder + "netherite/boots"));
    // tools
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_AXE, Items.NETHERITE_PICKAXE), TinkerFluids.moltenNetherite.get(), FluidValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 3))
                        .build(consumer, modResource(folder + "netherite/axes"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_SWORD, Items.NETHERITE_HOE), TinkerFluids.moltenNetherite.get(), FluidValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), FluidValues.GEM * 2))
                        .build(consumer, modResource(folder + "netherite/weapon"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_SHOVEL), TinkerFluids.moltenNetherite.get(), FluidValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), FluidValues.GEM))
                        .build(consumer, modResource(folder + "netherite/shovel"));

    // quartz
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.OBSERVER, Blocks.COMPARATOR, TinkerGadgets.quartzShuriken), TinkerFluids.moltenQuartz.get(), FluidValues.GEM)
                        .build(consumer, modResource(folder + "quartz/gem_1"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.DAYLIGHT_DETECTOR), TinkerFluids.moltenQuartz.get(), FluidValues.GEM * 3)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), FluidValues.GLASS_BLOCK * 3))
                        .build(consumer, modResource(folder + "quartz/daylight_detector"));

    // obsidian, if you are crazy i guess
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.BEACON), TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_BLOCK * 3)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), FluidValues.GLASS_BLOCK * 5))
                        .build(consumer, modResource(folder + "obsidian/beacon"));

    // ender
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.END_CRYSTAL), TinkerFluids.moltenEnder.get(), FluidValues.SLIMEBALL)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), FluidValues.GLASS_BLOCK * 7))
                        .build(consumer, modResource(folder + "ender/end_crystal"));
    // it may be silky, but its still rose gold
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerModifiers.silkyCloth), TinkerFluids.moltenRoseGold.get(), FluidValues.INGOT)
                        .build(consumer, modResource(metalFolder + "rose_gold/silky_cloth"));

    // misc reinforcements
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerModifiers.slimesteelReinforcement), TinkerFluids.moltenSlimesteel.get(), FluidValues.NUGGET * 3)
                        .addByproduct(new FluidStack(TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_PANE))
                        .build(consumer, modResource(metalFolder + "slimesteel/reinforcement"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerModifiers.bronzeReinforcement), TinkerFluids.moltenTinkersBronze.get(), FluidValues.NUGGET * 3)
                        .addByproduct(new FluidStack(TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_PANE))
                        .build(consumer, modResource(metalFolder + "tinkers_bronze/reinforcement"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerModifiers.cobaltReinforcement), TinkerFluids.moltenCobalt.get(), FluidValues.NUGGET * 3)
                        .addByproduct(new FluidStack(TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_PANE))
                        .build(consumer, modResource(metalFolder + "cobalt/reinforcement"));

    // slime
    TinkerGadgets.slimeSling.forEach((type, sling) -> {
      if (type != SlimeType.ICHOR) { // no ichor fluid
        MeltingRecipeBuilder.melting(Ingredient.fromItems(sling), TinkerFluids.slime.get(type).get(), FluidValues.SLIMEBALL * 3 + FluidValues.SLIME_CONGEALED)
                            .setDamagable()
                            .build(consumer, modResource(slimeFolder + type.getString() + "/sling"));
      }
    });
    TinkerModifiers.slimeCrystal.forEach((type, crystal) -> {
      if (type != SlimeType.ICHOR) { // no ichor fluid
        MeltingRecipeBuilder.melting(Ingredient.fromItems(crystal), TinkerFluids.slime.get(type).get(), FluidValues.SLIMEBALL)
                            .setDamagable()
                            .build(consumer, modResource(slimeFolder + type.getString() + "/crystal"));
      }
    });
    // recycle saplings
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.slimeSapling.get(SlimeType.EARTH)), TinkerFluids.earthSlime.get(), FluidValues.SLIMEBALL)
                        .build(consumer, modResource(slimeFolder + "earth/sapling"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.slimeSapling.get(SlimeType.SKY)), TinkerFluids.skySlime.get(), FluidValues.SLIMEBALL)
                        .build(consumer, modResource(slimeFolder + "sky/sapling"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.slimeSapling.get(SlimeType.ENDER)), TinkerFluids.enderSlime.get(), FluidValues.SLIMEBALL)
                        .build(consumer, modResource(slimeFolder + "ender/sapling"));

    // fuels
    MeltingFuelBuilder.fuel(new FluidStack(Fluids.LAVA, 50), 100)
                      .build(consumer, modResource(folder + "fuel/lava"));
    MeltingFuelBuilder.fuel(new FluidStack(TinkerFluids.blazingBlood.get(), 50), 150)
                      .build(consumer, modResource(folder + "fuel/blaze"));
  }


  private void addAlloyRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "smeltery/alloys/";

    // alloy recipes are in terms of ingots

    // tier 3

    // slimesteel: 1 iron + 1 skyslime + 1 seared brick = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenSlimesteel.get(), FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenIron.getForgeTag(), FluidValues.INGOT)
                      .addInput(TinkerFluids.skySlime.getLocalTag(), FluidValues.SLIMEBALL)
                      .addInput(TinkerFluids.searedStone.getLocalTag(), FluidValues.INGOT)
                      .build(consumer, prefix(TinkerFluids.moltenSlimesteel, folder));

    // tinker's bronze: 3 copper + 1 silicon (1/4 glass) = 4
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenTinkersBronze.get(), FluidValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenCopper.getForgeTag(), FluidValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenGlass.getLocalTag(), FluidValues.GLASS_BLOCK)
                      .build(consumer, prefix(TinkerFluids.moltenTinkersBronze, folder));

    // rose gold: 3 copper + 1 gold = 4
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenRoseGold.get(), FluidValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCopper.getForgeTag(), FluidValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenGold.getForgeTag(), FluidValues.INGOT)
                      .build(consumer, prefix(TinkerFluids.moltenRoseGold, folder));
    // pig iron: 1 iron + 1 blood + 1 clay = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenPigIron.get(), FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenIron.getForgeTag(), FluidValues.INGOT)
                      .addInput(TinkerFluids.blood.getLocalTag(), FluidValues.SLIMEBALL)
                      .addInput(TinkerFluids.moltenClay.getLocalTag(), FluidValues.SLIMEBALL)
                      .build(consumer, prefix(TinkerFluids.moltenPigIron, folder));
    // obsidian: 1 water + 1 lava = 2
    // note this is not a progression break, as the same tier lets you combine glass and copper for same mining level
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenObsidian.get(), FluidValues.GLASS_BLOCK / 10)
                      .addInput(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 20)
                      .addInput(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10)
                      .build(consumer, prefix(TinkerFluids.moltenObsidian, folder));

    // tier 4

    // queens slime: 1 cobalt + 1 gold + 1 magma cream = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenQueensSlime.get(), FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCobalt.getForgeTag(), FluidValues.INGOT)
                      .addInput(TinkerFluids.moltenGold.getForgeTag(), FluidValues.INGOT)
                      .addInput(TinkerFluids.magma.getForgeTag(), FluidValues.SLIMEBALL)
                      .build(consumer, prefix(TinkerFluids.moltenQueensSlime, folder));

    // manyullyn: 3 cobalt + 1 debris = 3
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenManyullyn.get(), FluidValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCobalt.getForgeTag(), FluidValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenDebris.getLocalTag(), FluidValues.INGOT)
                      .build(consumer, prefix(TinkerFluids.moltenManyullyn, folder));

    // heptazion: 2 copper + 1 cobalt + 1/4 obsidian = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenHepatizon.get(), FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.getForgeTag(), FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCobalt.getForgeTag(), FluidValues.INGOT)
                      .addInput(TinkerFluids.moltenObsidian.getLocalTag(), FluidValues.GLASS_BLOCK)
                      .build(consumer, prefix(TinkerFluids.moltenHepatizon, folder));

    // netherrite: 4 debris + 4 gold = 1 (why is this so dense vanilla?)
    ConditionalRecipe.builder()
                     .addCondition(ConfigEnabledCondition.CHEAPER_NETHERITE_ALLOY)
                     .addRecipe(
                       AlloyRecipeBuilder.alloy(TinkerFluids.moltenNetherite.get(), FluidValues.NUGGET)
                                         .addInput(TinkerFluids.moltenDebris.getLocalTag(), FluidValues.NUGGET * 4)
                                         .addInput(TinkerFluids.moltenGold.getForgeTag(), FluidValues.NUGGET * 2)::build)
                     .addCondition(TrueCondition.INSTANCE) // fallback
                     .addRecipe(
                       AlloyRecipeBuilder.alloy(TinkerFluids.moltenNetherite.get(), FluidValues.NUGGET)
                                         .addInput(TinkerFluids.moltenDebris.getLocalTag(), FluidValues.NUGGET * 4)
                                         .addInput(TinkerFluids.moltenGold.getForgeTag(), FluidValues.NUGGET * 4)::build)
                     .build(consumer, prefix(TinkerFluids.moltenNetherite, folder));


    // tier 3 compat
    Consumer<IFinishedRecipe> wrapped;

    // bronze
    wrapped = withCondition(consumer, tagCondition("ingots/bronze"), tagCondition("ingots/tin"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenBronze.get(), FluidValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCopper.getForgeTag(), FluidValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenTin.getForgeTag(), FluidValues.INGOT)
                      .build(wrapped, prefix(TinkerFluids.moltenBronze, folder));

    // brass
    wrapped = withCondition(consumer, tagCondition("ingots/brass"), tagCondition("ingots/zinc"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenBrass.get(), FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.getForgeTag(), FluidValues.INGOT)
                      .addInput(TinkerFluids.moltenZinc.getForgeTag(), FluidValues.INGOT)
                      .build(wrapped, prefix(TinkerFluids.moltenBrass, folder));

    // electrum
    wrapped = withCondition(consumer, tagCondition("ingots/electrum"), tagCondition("ingots/silver"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenElectrum.get(), FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenGold.getForgeTag(), FluidValues.INGOT)
                      .addInput(TinkerFluids.moltenSilver.getForgeTag(), FluidValues.INGOT)
                      .build(wrapped, prefix(TinkerFluids.moltenElectrum, folder));

    // invar
    wrapped = withCondition(consumer, tagCondition("ingots/invar"), tagCondition("ingots/nickel"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenInvar.get(), FluidValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenIron.getForgeTag(), FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenNickel.getForgeTag(), FluidValues.INGOT)
                      .build(wrapped, prefix(TinkerFluids.moltenInvar, folder));

    // constantan
    wrapped = withCondition(consumer, tagCondition("ingots/constantan"), tagCondition("ingots/nickel"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenConstantan.get(), FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.getForgeTag(), FluidValues.INGOT)
                      .addInput(TinkerFluids.moltenNickel.getForgeTag(), FluidValues.INGOT)
                      .build(wrapped, prefix(TinkerFluids.moltenConstantan, folder));

    // pewter
    wrapped = withCondition(consumer, tagCondition("ingots/pewter"), tagCondition("ingots/lead"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenPewter.get(), FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenIron.getForgeTag(), FluidValues.INGOT)
                      .addInput(TinkerFluids.moltenLead.getForgeTag(), FluidValues.INGOT)
                      .build(wrapped, prefix(TinkerFluids.moltenPewter, folder));

    // thermal alloys
    Function<String,ICondition> fluidTagLoaded = name -> new NotCondition(new FluidTagEmptyCondition("forge", name));
    // enderium
    wrapped = withCondition(consumer, tagCondition("ingots/enderium"), tagCondition("ingots/lead"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenEnderium.get(), FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenLead.getForgeTag(), FluidValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenDiamond.getLocalTag(), FluidValues.GEM)
                      .addInput(TinkerFluids.moltenEnder.getForgeTag(), FluidValues.SLIMEBALL * 2)
                      .build(wrapped, prefix(TinkerFluids.moltenEnderium, folder));
    // lumium
    wrapped = withCondition(consumer, tagCondition("ingots/lumium"), tagCondition("ingots/tin"), tagCondition("ingots/silver"), fluidTagLoaded.apply("glowstone"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenLumium.get(), FluidValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenTin.getForgeTag(), FluidValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenSilver.getForgeTag(), FluidValues.INGOT)
                      .addInput(FluidIngredient.of(FluidTags.makeWrapperTag("forge:glowstone"), FluidValues.SLIMEBALL * 2))
                      .build(wrapped, prefix(TinkerFluids.moltenLumium, folder));
    // signalum
    wrapped = withCondition(consumer, tagCondition("ingots/signalum"), tagCondition("ingots/copper"), tagCondition("ingots/silver"), fluidTagLoaded.apply("redstone"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenSignalum.get(), FluidValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCopper.getForgeTag(), FluidValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenSilver.getForgeTag(), FluidValues.INGOT)
                      .addInput(FluidIngredient.of(FluidTags.makeWrapperTag("forge:redstone"), 400))
                      .build(wrapped, prefix(TinkerFluids.moltenSignalum, folder));

    // refined obsidian, note glowstone is done as a composite
    wrapped = withCondition(consumer, tagCondition("ingots/refined_obsidian"), tagCondition("ingots/osmium"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenRefinedObsidian.get(), FluidValues.INGOT)
                      .addInput(TinkerFluids.moltenObsidian.getLocalTag(), FluidValues.GLASS_PANE)
                      .addInput(TinkerFluids.moltenDiamond.getLocalTag(), FluidValues.GEM)
                      .addInput(TinkerFluids.moltenOsmium.getForgeTag(), FluidValues.INGOT)
                      .build(wrapped, prefix(TinkerFluids.moltenRefinedObsidian, folder));
  }

  private void addEntityMeltingRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "smeltery/entity_melting/";
    String headFolder = "smeltery/entity_melting/heads/";

    // zombies give less blood, they lost a lot already
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ZOMBIE, EntityType.HUSK, EntityType.ZOMBIFIED_PIGLIN, EntityType.ZOGLIN, EntityType.ZOMBIE_HORSE),
                                       new FluidStack(TinkerFluids.blood.get(), FluidValues.SLIMEBALL / 10), 2)
                              .build(consumer, prefix(EntityType.ZOMBIE, folder));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ZOMBIE_HEAD, TinkerWorld.heads.get(TinkerHeadType.HUSK), TinkerWorld.heads.get(TinkerHeadType.PIGLIN), TinkerWorld.heads.get(TinkerHeadType.PIGLIN_BRUTE), TinkerWorld.heads.get(TinkerHeadType.ZOMBIFIED_PIGLIN)), TinkerFluids.blood.get(), FluidValues.SLIMEBALL * 2)
                        .build(consumer, prefix(EntityType.ZOMBIE, headFolder));
    // drowned are weird, there is water flowing through their veins
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.DROWNED),
                                       new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 50), 2)
                              .build(consumer, prefix(EntityType.DROWNED, folder));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.heads.get(TinkerHeadType.DROWNED)), Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 4)
                        .build(consumer, prefix(EntityType.DROWNED, headFolder));
    // melt spiders into venom
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SPIDER, EntityType.CAVE_SPIDER),
                                       new FluidStack(TinkerFluids.venom.get(), FluidValues.SLIMEBALL / 10), 2)
                              .build(consumer, prefix(EntityType.SPIDER, folder));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.heads.get(TinkerHeadType.SPIDER), TinkerWorld.heads.get(TinkerHeadType.CAVE_SPIDER)), TinkerFluids.venom.get(), FluidValues.SLIMEBALL * 2)
                        .build(consumer, prefix(EntityType.SPIDER, headFolder));

    // creepers are based on explosives, tnt is explosive, tnt is made from sand, sand melts into glass. therefore, creepers melt into glass
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.CREEPER),
                                       new FluidStack(TinkerFluids.moltenGlass.get(), FluidValues.GLASS_BLOCK / 20), 2)
                              .build(consumer, prefix(EntityType.CREEPER, folder));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.CREEPER_HEAD), TinkerFluids.moltenGlass.get(), FluidAttributes.BUCKET_VOLUME / 4)
                        .build(consumer, prefix(EntityType.CREEPER, headFolder));

    // melt skeletons to get the milk out
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityIngredient.of(EntityTypeTags.SKELETONS), EntityIngredient.of(EntityType.SKELETON_HORSE)),
                                       new FluidStack(ForgeMod.MILK.get(), FluidAttributes.BUCKET_VOLUME / 10))
                              .build(consumer, modResource(folder + "skeletons"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, TinkerWorld.heads.get(TinkerHeadType.STRAY)), ForgeMod.MILK.get(), FluidAttributes.BUCKET_VOLUME / 4)
                        .build(consumer, prefix(EntityType.SKELETON, headFolder));

    // slimes melt into slime, shocker
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SLIME, TinkerWorld.earthSlimeEntity.get()), new FluidStack(TinkerFluids.earthSlime.get(), FluidValues.SLIMEBALL / 10))
                              .build(consumer, prefix(EntityType.SLIME, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.skySlimeEntity.get()), new FluidStack(TinkerFluids.skySlime.get(), FluidValues.SLIMEBALL / 10))
                              .build(consumer, prefix(TinkerWorld.skySlimeEntity, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.enderSlimeEntity.get()), new FluidStack(TinkerFluids.enderSlime.get(), FluidValues.SLIMEBALL / 10))
                              .build(consumer, prefix(TinkerWorld.enderSlimeEntity, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.terracubeEntity.get()), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL / 10))
                              .build(consumer, prefix(TinkerWorld.terracubeEntity, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.MAGMA_CUBE), new FluidStack(TinkerFluids.magma.get(), FluidValues.SLIMEBALL / 10))
                              .build(consumer, prefix(EntityType.MAGMA_CUBE, folder));

    // iron golems can be healed using an iron ingot 25 health
    // 4 * 9 gives 36, which is larger
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.IRON_GOLEM), new FluidStack(TinkerFluids.moltenIron.get(), FluidValues.NUGGET), 4)
                              .build(consumer, prefix(EntityType.IRON_GOLEM, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SNOW_GOLEM), new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 10))
                              .build(consumer, prefix(EntityType.SNOW_GOLEM, folder));

    // "melt" blazes to get fuel
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.BLAZE), new FluidStack(TinkerFluids.blazingBlood.get(), FluidAttributes.BUCKET_VOLUME / 50), 2)
                              .build(consumer, prefix(EntityType.BLAZE, folder));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.heads.get(TinkerHeadType.BLAZE)), new FluidStack(TinkerFluids.blazingBlood.get(), FluidAttributes.BUCKET_VOLUME / 10), 1000, IMeltingRecipe.calcTime(1500, 1.0f))
                        .build(consumer, prefix(EntityType.BLAZE, headFolder));

    // guardians are rock, seared stone is rock, don't think about it too hard
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN), new FluidStack(TinkerFluids.searedStone.get(), FluidValues.NUGGET), 4)
                              .build(consumer, prefix(EntityType.GUARDIAN, folder));
    // silverfish also seem like rock, sorta?
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SILVERFISH), new FluidStack(TinkerFluids.searedStone.get(), FluidValues.NUGGET), 2)
                              .build(consumer, prefix(EntityType.SILVERFISH, folder));

    // villagers melt into emerald, but they die quite quick
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.VILLAGER, EntityType.WANDERING_TRADER),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), FluidValues.GEM / 9), 5)
                              .build(consumer, prefix(EntityType.VILLAGER, folder));
    // illagers are more resistant, they resist the villager culture afterall
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.EVOKER, EntityType.ILLUSIONER, EntityType.PILLAGER, EntityType.VINDICATOR),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), FluidValues.GEM / 9), 2)
                              .build(consumer, modResource(folder + "illager"));
    // zombie villagers and witches faintly recall being a villager once
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ZOMBIE_VILLAGER, EntityType.WITCH),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), FluidValues.GEM / 18), 3)
                              .build(consumer, prefix(EntityType.ZOMBIE_VILLAGER, folder));

    // melt ender for the molten ender
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON),
                                       new FluidStack(TinkerFluids.moltenEnder.get(), FluidValues.SLIMEBALL / 10), 2)
                              .build(consumer, modResource(folder + "ender"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.heads.get(TinkerHeadType.ENDERMAN)), TinkerFluids.moltenEnder.get(), FluidValues.SLIMEBALL * 2)
                        .build(consumer, prefix(EntityType.ENDERMAN, headFolder));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DRAGON_HEAD), TinkerFluids.moltenEnder.get(), FluidValues.SLIMEBALL * 4)
                        .build(consumer, prefix(EntityType.ENDER_DRAGON, headFolder));

    // if you can get him to stay, wither is a source of free liquid soul
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.WITHER),
                                       new FluidStack(TinkerFluids.liquidSoul.get(), FluidValues.GLASS_BLOCK / 20), 2)
                              .build(consumer, prefix(EntityType.WITHER, folder));
  }

  private void addCompatRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "compat/";
    // create - cast andesite alloy
    ItemOutput andesiteAlloy = ItemNameOutput.fromName(new ResourceLocation("create", "andesite_alloy"));
    Consumer<IFinishedRecipe> createConsumer = withCondition(consumer, new ModLoadedCondition("create"));
    ItemCastingRecipeBuilder.basinRecipe(andesiteAlloy)
                            .setCast(Blocks.ANDESITE, true)
                            .setFluidAndTime(TinkerFluids.moltenIron, true, FluidValues.NUGGET)
                            .build(createConsumer, modResource(folder + "create/andesite_alloy_iron"));
    ItemCastingRecipeBuilder.basinRecipe(andesiteAlloy)
                            .setCast(Blocks.ANDESITE, true)
                            .setFluidAndTime(TinkerFluids.moltenZinc, true, FluidValues.NUGGET)
                            .build(createConsumer, modResource(folder + "create/andesite_alloy_zinc"));

    // immersive engineering - casting treated wood
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(new ResourceLocation("immersiveengineering", "treated_wood_horizontal")))
                            .setCast(ItemTags.PLANKS, true)
                            .setFluid(FluidTags.makeWrapperTag("forge:creosote"), 125)
                            .setCoolingTime(100)
                            .build(withCondition(consumer, new ModLoadedCondition("immersiveengineering")), modResource(folder + "immersiveengineering/treated_wood"));

    // ceramics compat: a lot of melting and some casting
    String ceramics = "ceramics";
    String ceramicsFolder = folder + ceramics + "/";
    Function<String,ResourceLocation> ceramicsId = name -> new ResourceLocation(ceramics, name);
    Consumer<IFinishedRecipe> ceramicsConsumer = withCondition(consumer, new ModLoadedCondition(ceramics));

    // fill clay and cracked clay buckets
    ContainerFillingRecipeBuilder.tableRecipe(ceramicsId.apply("clay_bucket"), FluidAttributes.BUCKET_VOLUME)
                                 .build(ceramicsConsumer, modResource(ceramicsFolder + "filling_clay_bucket"));
    ContainerFillingRecipeBuilder.tableRecipe(ceramicsId.apply("cracked_clay_bucket"), FluidAttributes.BUCKET_VOLUME)
                                 .build(ceramicsConsumer, modResource(ceramicsFolder + "filling_cracked_clay_bucket"));

    // porcelain for ceramics
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenPorcelain.get(), FluidValues.SLIMEBALL * 4)
                      .addInput(TinkerFluids.moltenClay.getLocalTag(), FluidValues.SLIMEBALL * 3)
                      .addInput(TinkerFluids.moltenQuartz.getLocalTag(), FluidValues.INGOT)
                      .build(ceramicsConsumer, modResource(ceramicsFolder + "alloy_porcelain"));

    // melting clay
    String clayFolder = ceramicsFolder + "clay/";

    // unfired clay
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_clay_plate")), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL), 0.5f)
                        .build(ceramicsConsumer, modResource(clayFolder + "clay_1"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_faucet"), ceramicsId.apply("clay_channel")), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 2), 0.65f)
                        .build(ceramicsConsumer, modResource(clayFolder + "clay_2"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_clay_bucket"), ceramicsId.apply("clay_cistern")), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 3), 0.9f)
                        .build(ceramicsConsumer, modResource(clayFolder + "clay_3"));

    // 2 bricks
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("dark_bricks_slab"), ceramicsId.apply("dragon_bricks_slab"),
      ceramicsId.apply("terracotta_faucet"), ceramicsId.apply("terracotta_channel")), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 2), 1.33f)
                        .build(ceramicsConsumer, modResource(clayFolder + "bricks_2"));
    // 3 bricks
    MeltingRecipeBuilder.melting(CompoundIngredient.from(
      Ingredient.fromTag(ItemTags.createOptional(ceramicsId.apply("terracotta_cisterns"))),
      NBTNameIngredient.from(ceramicsId.apply("clay_bucket")),
      NBTNameIngredient.from(ceramicsId.apply("cracked_clay_bucket"))),
                                 new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 3), 1.67f)
                        .build(ceramicsConsumer, modResource(clayFolder + "bricks_3"));
    // 4 bricks
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("dark_bricks"), ceramicsId.apply("dark_bricks_stairs"), ceramicsId.apply("dark_bricks_wall"),
      ceramicsId.apply("dragon_bricks"), ceramicsId.apply("dragon_bricks_stairs"), ceramicsId.apply("dragon_bricks_wall")
    ), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 4), 2.0f)
                        .build(ceramicsConsumer, modResource(clayFolder + "block"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("kiln")), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIME_CONGEALED * 3 + FluidValues.SLIMEBALL * 5), 4.0f)
                        .build(ceramicsConsumer, modResource(clayFolder + "kiln"));
    // lava bricks, lava byproduct
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("lava_bricks_slab")), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 2), 1.33f)
                        .addByproduct(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 20))
                        .build(ceramicsConsumer, modResource(clayFolder + "lava_bricks_slab"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("lava_bricks"), ceramicsId.apply("lava_bricks_stairs"), ceramicsId.apply("lava_bricks_wall")
    ), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 4), 2f)
                        .addByproduct(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10))
                        .build(ceramicsConsumer, modResource(clayFolder + "lava_bricks_block"));
    // gauge, partially glass
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("terracotta_gauge")), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL), 1f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), FluidValues.GLASS_PANE / 4))
                        .build(ceramicsConsumer, modResource(clayFolder + "gauge"));
    // clay armor
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_helmet")), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 5), 2.25f)
                        .setDamagable()
                        .build(ceramicsConsumer, modResource(clayFolder + "clay_helmet"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_chestplate")), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 8), 3f)
                        .setDamagable()
                        .build(ceramicsConsumer, modResource(clayFolder + "clay_chestplate"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_leggings")), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 7), 2.75f)
                        .setDamagable()
                        .build(ceramicsConsumer, modResource(clayFolder + "clay_leggings"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_boots")), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 4), 2f)
                        .setDamagable()
                        .build(ceramicsConsumer, modResource(clayFolder + "clay_boots"));

    // melting porcelain
    String porcelainFolder = ceramicsFolder + "porcelain/";
    // unfired
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_porcelain")), new FluidStack(TinkerFluids.moltenPorcelain.get(), FluidValues.SLIMEBALL), 0.5f)
                        .build(ceramicsConsumer, modResource(porcelainFolder + "unfired_1"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_faucet"), ceramicsId.apply("unfired_channel")), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 2), 0.65f)
                        .build(ceramicsConsumer, modResource(porcelainFolder + "unfired_2"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_cistern")), new FluidStack(TinkerFluids.moltenClay.get(), FluidValues.SLIMEBALL * 3), 0.9f)
                        .build(ceramicsConsumer, modResource(porcelainFolder + "unfired_3"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_porcelain_block")), new FluidStack(TinkerFluids.moltenPorcelain.get(), FluidValues.SLIME_CONGEALED), 1f)
                        .build(ceramicsConsumer, modResource(porcelainFolder + "unfired_4"));

    // 1 brick
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("porcelain_brick")), new FluidStack(TinkerFluids.moltenPorcelain.get(), FluidValues.SLIMEBALL), 1f)
                        .build(ceramicsConsumer, modResource(porcelainFolder + "bricks_1"));
    // 2 bricks
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("porcelain_bricks_slab"), ceramicsId.apply("monochrome_bricks_slab"), ceramicsId.apply("marine_bricks_slab"), ceramicsId.apply("rainbow_bricks_slab"),
      ceramicsId.apply("porcelain_faucet"), ceramicsId.apply("porcelain_channel")
    ), new FluidStack(TinkerFluids.moltenPorcelain.get(), FluidValues.SLIMEBALL * 2), 1.33f)
                        .build(ceramicsConsumer, modResource(porcelainFolder + "bricks_2"));
    // 3 bricks
    MeltingRecipeBuilder.melting(Ingredient.fromTag(ItemTags.makeWrapperTag(ceramics + ":porcelain_cisterns")), new FluidStack(TinkerFluids.moltenPorcelain.get(), FluidValues.SLIMEBALL * 3), 1.67f)
                        .build(ceramicsConsumer, modResource(porcelainFolder + "bricks_3"));
    // 4 bricks
    MeltingRecipeBuilder.melting(CompoundIngredient.from(
      Ingredient.fromTag(ItemTags.makeWrapperTag(ceramics + ":porcelain_block")),
      Ingredient.fromTag(ItemTags.makeWrapperTag(ceramics + ":rainbow_porcelain")),
      ItemNameIngredient.from(
        ceramicsId.apply("porcelain_bricks"), ceramicsId.apply("porcelain_bricks_stairs"), ceramicsId.apply("porcelain_bricks_wall"),
        ceramicsId.apply("monochrome_bricks"), ceramicsId.apply("monochrome_bricks_stairs"), ceramicsId.apply("monochrome_bricks_wall"),
        ceramicsId.apply("marine_bricks"), ceramicsId.apply("marine_bricks_stairs"), ceramicsId.apply("marine_bricks_wall"),
        ceramicsId.apply("rainbow_bricks"), ceramicsId.apply("rainbow_bricks_stairs"), ceramicsId.apply("rainbow_bricks_wall")
      )), new FluidStack(TinkerFluids.moltenPorcelain.get(), FluidValues.SLIMEBALL * 4), 2.0f)
                        .build(ceramicsConsumer, modResource(porcelainFolder + "blocks"));
    // gold bricks, gold byproduct
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("golden_bricks_slab")), new FluidStack(TinkerFluids.moltenPorcelain.get(), FluidValues.SLIMEBALL * 2), 1.33f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGold.get(), FluidValues.NUGGET / 16)) // yep, exactly 1mb, such recycling
                        .build(ceramicsConsumer, modResource(porcelainFolder + "golden_bricks_slab"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("golden_bricks"), ceramicsId.apply("golden_bricks_stairs"), ceramicsId.apply("golden_bricks_wall")
    ), new FluidStack(TinkerFluids.moltenPorcelain.get(), FluidValues.SLIMEBALL * 4), 2f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGold.get(), FluidValues.NUGGET / 8)) // 2mb is slightly better, but still not great
                        .build(ceramicsConsumer, modResource(porcelainFolder + "golden_bricks_block"));
    // gauge, partially glass
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("porcelain_gauge")), new FluidStack(TinkerFluids.moltenPorcelain.get(), FluidValues.SLIMEBALL), 1f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), FluidValues.GLASS_PANE / 4))
                        .build(ceramicsConsumer, modResource(porcelainFolder + "gauge"));

    // casting bricks
    String castingFolder = ceramicsFolder + "casting/";
    castingWithCast(ceramicsConsumer, TinkerFluids.moltenPorcelain, FluidValues.SLIMEBALL, TinkerSmeltery.ingotCast, ItemNameOutput.fromName(ceramicsId.apply("porcelain_brick")), castingFolder + "porcelain_brick");
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("white_porcelain")))
                            .setFluidAndTime(TinkerFluids.moltenPorcelain, false, FluidValues.SLIME_CONGEALED)
                            .build(ceramicsConsumer, modResource(castingFolder + "porcelain"));
    // lava bricks
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("lava_bricks")))
                            .setCast(Blocks.BRICKS, true)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10))
                            .build(ceramicsConsumer, modResource(castingFolder + "lava_bricks"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("lava_bricks_slab")))
                            .setCast(Blocks.BRICK_SLAB, true)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 20))
                            .build(ceramicsConsumer, modResource(castingFolder + "lava_bricks_slab"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("lava_bricks_stairs")))
                            .setCast(Blocks.BRICK_STAIRS, true)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10))
                            .build(ceramicsConsumer, modResource(castingFolder + "lava_bricks_stairs"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("lava_bricks_wall")))
                            .setCast(Blocks.BRICK_WALL, true)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10))
                            .build(ceramicsConsumer, modResource(castingFolder + "lava_bricks_wall"));

    // golden bricks
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("golden_bricks")))
                            .setCast(ItemNameIngredient.from(ceramicsId.apply("porcelain_bricks")), true)
                            .setFluidAndTime(TinkerFluids.moltenGold, true, FluidValues.NUGGET / 8)
                            .build(ceramicsConsumer, modResource(castingFolder + "golden_bricks"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("golden_bricks_slab")))
                            .setCast(ItemNameIngredient.from(ceramicsId.apply("porcelain_bricks_slab")), true)
                            .setFluidAndTime(TinkerFluids.moltenGold, true, FluidValues.NUGGET / 16)
                            .build(ceramicsConsumer, modResource(castingFolder + "golden_bricks_slab"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("golden_bricks_stairs")))
                            .setCast(ItemNameIngredient.from(ceramicsId.apply("porcelain_bricks_stairs")), true)
                            .setFluidAndTime(TinkerFluids.moltenGold, true, FluidValues.NUGGET / 8)
                            .build(ceramicsConsumer, modResource(castingFolder + "golden_bricks_stairs"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("golden_bricks_wall")))
                            .setCast(ItemNameIngredient.from(ceramicsId.apply("porcelain_bricks_wall")), true)
                            .setFluidAndTime(TinkerFluids.moltenGold, true, FluidValues.NUGGET / 8)
                            .build(ceramicsConsumer, modResource(castingFolder + "golden_bricks_wall"));

    // refined glowstone composite
    Consumer<IFinishedRecipe> wrapped = withCondition(consumer, tagCondition("ingots/refined_glowstone"), tagCondition("ingots/osmium"));
    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromTag(ItemTags.makeWrapperTag("forge:ingots/refined_glowstone"), 1))
                            .setCast(Tags.Items.DUSTS_GLOWSTONE, true)
                            .setFluidAndTime(TinkerFluids.moltenOsmium, FluidValues.INGOT)
                            .build(wrapped, modResource(folder + "refined_glowstone_ingot"));
    wrapped = withCondition(consumer, tagCondition("ingots/refined_obsidian"), tagCondition("ingots/osmium"));
    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromTag(ItemTags.makeWrapperTag("forge:ingots/refined_obsidian"), 1))
                            .setCast(ItemTags.makeWrapperTag("forge:dusts/refined_obsidian"), true)
                            .setFluidAndTime(TinkerFluids.moltenOsmium, FluidValues.INGOT)
                            .build(wrapped, modResource(folder + "refined_obsidian_ingot"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerMaterials.necroniumBone)
                            .setFluidAndTime(TinkerFluids.moltenUranium, true, FluidValues.INGOT)
                            .setCast(TinkerTags.Items.WITHER_BONES, true)
                            .build(withCondition(consumer, tagCondition("ingots/uranium")), modResource(folder + "necronium_bone"));
  }


  /* Seared casting */

  /**
   * Adds a stonecutting recipe with automatic name and criteria
   * @param consumer  Recipe consumer
   * @param output    Recipe output
   * @param folder    Recipe folder path
   */
  private void searedStonecutter(Consumer<IFinishedRecipe> consumer, IItemProvider output, String folder) {
    SingleItemRecipeBuilder.stonecuttingRecipe(CompoundIngredient.from(
      Ingredient.fromItems(TinkerSmeltery.searedStone),
      new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.SEARED_BRICKS), Ingredient.fromItems(output))), output, 1)
                           .addCriterion("has_stone", hasItem(TinkerSmeltery.searedStone))
                           .addCriterion("has_bricks", hasItem(TinkerTags.Items.SEARED_BRICKS))
                           .build(consumer, wrap(output.asItem(), folder, "_stonecutting"));
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param location  Recipe location
   */
  private void searedCasting(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, String location) {
    searedCasting(consumer, block, cast, FluidValues.SLIMEBALL * 2, location);
  }

  /**
   * Adds a recipe to create the given seared slab block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param location  Recipe location
   */
  private void searedSlabCasting(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, String location) {
    searedCasting(consumer, block, cast, FluidValues.SLIMEBALL, location);
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param amount    Amount of fluid needed
   * @param location  Recipe location
   */
  private void searedCasting(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, int amount, String location) {
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluidAndTime(TinkerFluids.moltenClay, false, amount)
                            .setCast(cast, true)
                            .build(consumer, modResource(location));
  }


  /* Scorched casting */

  /**
   * Adds a stonecutting recipe with automatic name and criteria
   * @param consumer  Recipe consumer
   * @param output    Recipe output
   * @param folder    Recipe folder path
   */
  private void scorchedStonecutter(Consumer<IFinishedRecipe> consumer, IItemProvider output, String folder) {
    SingleItemRecipeBuilder.stonecuttingRecipe(new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.SCORCHED_BLOCKS), Ingredient.fromItems(output)), output, 1)
                           .addCriterion("has_block", hasItem(TinkerTags.Items.SCORCHED_BLOCKS))
                           .build(consumer, wrap(output.asItem(), folder, "_stonecutting"));
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param location  Recipe location
   */
  private void scorchedCasting(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, String location) {
    scorchedCasting(consumer, block, cast, FluidValues.SLIMEBALL * 2, location);
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param amount    Amount of fluid needed
   * @param location  Recipe location
   */
  private void scorchedCasting(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, int amount, String location) {
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluidAndTime(TinkerFluids.magma, true, amount)
                            .setCast(cast, true)
                            .build(consumer, modResource(location));
  }


  /* Casting */

  /**
   * Adds melting recipes for slime
   * @param consumer       Consumer
   * @param fluidSupplier  Fluid
   * @param type           Slime type
   * @param folder         Output folder
   */
  private void slimeMelting(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluidSupplier, SlimeType type, String folder) {
    String slimeFolder = folder + type.getString() + "/";
    MeltingRecipeBuilder.melting(Ingredient.fromTag(type.getSlimeballTag()), fluidSupplier.get(), FluidValues.SLIMEBALL, 1.0f)
                        .build(consumer, modResource(slimeFolder + "ball"));
    IItemProvider item = TinkerWorld.congealedSlime.get(type);
    MeltingRecipeBuilder.melting(Ingredient.fromItems(item), fluidSupplier.get(), FluidValues.SLIME_CONGEALED, 2.0f)
                        .build(consumer, modResource(slimeFolder + "congealed"));
    item = TinkerWorld.slime.get(type);
    MeltingRecipeBuilder.melting(Ingredient.fromItems(item), fluidSupplier.get(), FluidValues.SLIMEBLOCK, 3.0f)
                        .build(consumer, modResource(slimeFolder + "block"));
  }

  /**
   * Adds slime related casting recipes
   * @param consumer    Recipe consumer
   * @param fluid       Fluid matching the slime type
   * @param slimeType   SlimeType for this recipe
   * @param folder      Output folder
   */
  private void slimeCasting(Consumer<IFinishedRecipe> consumer, FluidObject<?> fluid, boolean forgeTag, SlimeType slimeType, String folder) {
    String colorFolder = folder + slimeType.getString() + "/";
    ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.congealedSlime.get(slimeType))
                            .setFluidAndTime(fluid, forgeTag, FluidValues.SLIME_CONGEALED)
                            .build(consumer, modResource(colorFolder + "congealed"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.slime.get(slimeType))
                            .setFluidAndTime(fluid, forgeTag, FluidValues.SLIMEBLOCK - FluidValues.SLIME_CONGEALED)
                            .setCast(TinkerWorld.congealedSlime.get(slimeType), true)
                            .build(consumer, modResource(colorFolder + "block"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.slimeball.get(slimeType))
                            .setFluidAndTime(fluid, forgeTag, FluidValues.SLIMEBALL)
                            .build(consumer, modResource(colorFolder + "slimeball"));
  }
}
