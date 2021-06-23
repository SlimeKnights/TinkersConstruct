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
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.EntityIngredient;
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
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.common.registration.MetalItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.container.ContainerFillingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipeBuilder;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.MaterialIds;
import slimeknights.tconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
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
    this.addCraftingRecipes(consumer);
    this.addSmelteryRecipes(consumer);
    this.addFoundryRecipes(consumer);
    this.addMeltingRecipes(consumer);
    this.addCastingRecipes(consumer);
    this.addMaterialRecipes(consumer);
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
                          .build(consumer, location("smeltery/sand_cast"));
    ShapelessRecipeBuilder.shapelessRecipe(TinkerSmeltery.blankCast.getRedSand(), 4)
                          .addIngredient(Tags.Items.SAND_RED)
                          .addCriterion("has_casting", hasItem(TinkerSmeltery.searedTable))
                          .build(consumer, location("smeltery/red_sand_cast"));

    // pick up sand casts from the table
    MoldingRecipeBuilder.moldingTable(TinkerSmeltery.blankCast.getSand())
                        .setMaterial(TinkerTags.Items.SAND_CASTS)
                        .build(consumer, location("smeltery/sand_cast_pickup"));
    MoldingRecipeBuilder.moldingTable(TinkerSmeltery.blankCast.getRedSand())
                        .setMaterial(TinkerTags.Items.RED_SAND_CASTS)
                        .build(consumer, location("smeltery/red_sand_cast_pickup"));
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
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTank.get(TankType.FUEL_TANK))
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('B', Tags.Items.GLASS)
                       .patternLine("###")
                       .patternLine("#B#")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location(folder + "fuel_tank"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE))
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('B', Tags.Items.GLASS)
                       .patternLine("#B#")
                       .patternLine("BBB")
                       .patternLine("#B#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location(folder + "fuel_gauge"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTank.get(TankType.INGOT_TANK))
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('B', Tags.Items.GLASS)
                       .patternLine("#B#")
                       .patternLine("#B#")
                       .patternLine("#B#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location(folder + "ingot_tank"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE))
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('B', Tags.Items.GLASS)
                       .patternLine("B#B")
                       .patternLine("#B#")
                       .patternLine("B#B")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location(folder + "ingot_gauge"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedLantern.get(), 3)
                       .key('C', Tags.Items.INGOTS_IRON)
                       .key('B', TinkerSmeltery.searedBrick)
                       .key('P', TinkerSmeltery.searedGlassPane)
                       .patternLine(" C ")
                       .patternLine("PPP")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location(folder + "lantern"));

    // fluid transfer
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedFaucet.get(), 2)
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("# #")
                       .patternLine(" # ")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location(folder + "faucet"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedChannel.get(), 3)
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("# #")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location(folder + "channel"));

    // casting
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedBasin.get())
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("# #")
                       .patternLine("# #")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location(folder + "basin"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTable.get())
                       .key('#', TinkerSmeltery.searedBrick)
                       .patternLine("###")
                       .patternLine("# #")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location(folder + "table"));

    // peripherals
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedDrain)
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('C', TinkerMaterials.copper.getIngotTag())
                       .patternLine("# #")
                       .patternLine("C C")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location(folder + "drain"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedChute)
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('C', TinkerMaterials.copper.getIngotTag())
                       .patternLine("#C#")
                       .patternLine("   ")
                       .patternLine("#C#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location(folder + "chute"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedDuct)
                       .key('#', TinkerSmeltery.searedBrick)
                       .key('C', TinkerMaterials.cobalt.getIngotTag())
                       .patternLine("# #")
                       .patternLine("C C")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerMaterials.cobalt.getIngotTag()))
                       .build(consumer, location(folder + "duct"));

    // controllers
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedMelter)
                       .key('G', Ingredient.fromItems(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE), TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE)))
                       .key('B', TinkerSmeltery.searedBrick)
                       .patternLine("BGB")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location(folder + "melter"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedHeater)
                       .key('B', TinkerSmeltery.searedBrick)
                       .patternLine("BBB")
                       .patternLine("B B")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, location(folder + "heater"));

    // casting
    String castingFolder = "smeltery/casting/seared/";
    this.addBlockCastingRecipe(consumer, TinkerFluids.searedStone, MaterialValues.METAL_BRICK, TinkerSmeltery.searedStone, castingFolder + "stone/block_from_seared");
    this.addIngotCastingRecipe(consumer, TinkerFluids.searedStone, TinkerSmeltery.searedBrick, castingFolder + "brick");
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedGlass)
                            .setFluidAndTime(new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.METAL_BRICK))
                            .setCast(Tags.Items.GLASS_COLORLESS, true)
                            .build(consumer, location(castingFolder + "glass"));
    // discount for casting panes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.searedGlassPane)
                            .setFluidAndTime(new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.INGOT))
                            .setCast(Tags.Items.GLASS_PANES_COLORLESS, true)
                            .build(consumer, location(castingFolder + "glass_pane"));

    // smeltery controller
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.smelteryController)
                            .setCast(TinkerSmeltery.searedHeater, true)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenCopper.get(), MaterialValues.INGOT * 4))
                            .build(consumer, prefix(TinkerSmeltery.smelteryController, castingFolder));

    // craft seared stone from clay and stone
    // cobble
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedCobble, CompoundIngredient.from(Ingredient.fromTag(Tags.Items.COBBLESTONE), Ingredient.fromItems(Blocks.GRAVEL)), castingFolder + "cobble/block");
    addSearedSlabCastingRecipe(consumer, TinkerSmeltery.searedCobble.getSlab(), Ingredient.fromItems(Blocks.COBBLESTONE_SLAB), castingFolder + "cobble/slab");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedCobble.getStairs(), Ingredient.fromItems(Blocks.COBBLESTONE_STAIRS), castingFolder + "cobble/stairs");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedCobble.getWall(), Ingredient.fromItems(Blocks.COBBLESTONE_WALL), castingFolder + "cobble/wall");
    // stone
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedStone, Ingredient.fromTag(Tags.Items.STONE), castingFolder + "stone/block_from_clay");
    addSearedSlabCastingRecipe(consumer, TinkerSmeltery.searedStone.getSlab(), Ingredient.fromItems(Blocks.STONE_SLAB), castingFolder + "stone/slab");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedStone.getStairs(), Ingredient.fromItems(Blocks.STONE_STAIRS), castingFolder + "stone/stairs");
    // stone bricks
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedBricks, Ingredient.fromItems(Blocks.STONE_BRICKS), castingFolder + "bricks/block");
    addSearedSlabCastingRecipe(consumer, TinkerSmeltery.searedBricks.getSlab(), Ingredient.fromItems(Blocks.STONE_BRICK_SLAB), castingFolder + "bricks/slab");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedBricks.getStairs(), Ingredient.fromItems(Blocks.STONE_BRICK_STAIRS), castingFolder + "bricks/stairs");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedBricks.getWall(), Ingredient.fromItems(Blocks.STONE_BRICK_WALL), castingFolder + "bricks/wall");
    // other seared
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedCrackedBricks, Ingredient.fromItems(Blocks.CRACKED_STONE_BRICKS), castingFolder + "cracked");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedFancyBricks, Ingredient.fromItems(Blocks.CHISELED_STONE_BRICKS), castingFolder + "chiseled");
    addSearedCastingRecipe(consumer, TinkerSmeltery.searedPaver, Ingredient.fromItems(Blocks.SMOOTH_STONE), castingFolder + "paver");

    // seared blocks
    String meltingFolder = "smeltery/melting/seared/";

    // double efficiency when using smeltery for grout
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.grout), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 2, 1.5f)
                        .build(consumer, location(meltingFolder + "grout"));
    // seared stone
    // stairs are here since the cheapest stair recipe is stone cutter, 1 to 1
    MeltingRecipeBuilder.melting(CompoundIngredient.from(Ingredient.fromTag(TinkerTags.Items.SEARED_BLOCKS),
                                                         Ingredient.fromItems(TinkerSmeltery.searedLadder, TinkerSmeltery.searedCobble.getWall(), TinkerSmeltery.searedBricks.getWall(),
                                                                              TinkerSmeltery.searedCobble.getStairs(), TinkerSmeltery.searedStone.getStairs(), TinkerSmeltery.searedBricks.getStairs(), TinkerSmeltery.searedPaver.getStairs())),
                                 TinkerFluids.searedStone.get(), MaterialValues.METAL_BRICK, 2.0f)
                        .build(consumer, location(meltingFolder + "block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedCobble.getSlab(), TinkerSmeltery.searedStone.getSlab(), TinkerSmeltery.searedBricks.getSlab(), TinkerSmeltery.searedPaver.getSlab()),
                                 TinkerFluids.searedStone.get(), MaterialValues.METAL_BRICK / 2, 1.5f)
                        .build(consumer, location(meltingFolder + "slab"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedBrick), TinkerFluids.searedStone.get(), MaterialValues.INGOT, 1.0f)
                        .build(consumer, location(meltingFolder + "brick"));

    // melt down smeltery components
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedFaucet), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 3 / 2, 1.5f)
                        .build(consumer, location(meltingFolder + "faucet"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedChannel), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 5 / 3, 1.5f)
                        .build(consumer, location(meltingFolder + "channel"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedBasin, TinkerSmeltery.searedTable), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 7, 2.5f)
                        .build(consumer, location(meltingFolder + "casting"));
    // tanks
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.searedTank.get(TankType.FUEL_TANK))), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 8, 3f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_BLOCK))
                        .build(consumer, location(meltingFolder + "fuel_tank"));
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.searedTank.get(TankType.INGOT_TANK))), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 6, 2.5f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_BLOCK * 3))
                        .build(consumer, location(meltingFolder + "ingot_tank"));
    MeltingRecipeBuilder.melting(CompoundIngredient.from(NBTIngredient.from(new ItemStack(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE))),
                                                         NBTIngredient.from(new ItemStack(TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE)))),
                                 TinkerFluids.searedStone.get(), MaterialValues.INGOT * 4, 2f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_BLOCK * 5))
                        .build(consumer, location(meltingFolder + "gauge"));
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.searedLantern)), TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 2, 1.0f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_PANE))
                        .addByproduct(new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.INGOT / 3))
                        .build(consumer, location(meltingFolder + "lantern"));
    // glass
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedGlass), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 4, 2f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_BLOCK))
                        .build(consumer, location(meltingFolder + "glass"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedGlassPane), TinkerFluids.searedStone.get(), MaterialValues.INGOT, 1.0f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_PANE))
                        .build(consumer, location(meltingFolder + "pane"));
    // controllers
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedMelter), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 9, 3.5f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_PANE * 5))
                        .build(consumer, location(meltingFolder + "melter"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedHeater), TinkerFluids.searedStone.get(), MaterialValues.INGOT * 8, 3f)
                        .build(consumer, location(meltingFolder + "heater"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.smelteryController), TinkerFluids.moltenCopper.get(), MaterialValues.INGOT * 4, 3.5f)
                        .addByproduct(new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.INGOT * 8))
                        .build(consumer, location("smeltery/melting/copper/smeltery_controller"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedDrain, TinkerSmeltery.searedChute), TinkerFluids.moltenCopper.get(), MaterialValues.INGOT * 2, 2.5f)
                        .addByproduct(new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.INGOT * 4))
                        .build(consumer, location("smeltery/melting/copper/smeltery_io"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedDuct), TinkerFluids.moltenCobalt.get(), MaterialValues.INGOT * 2, 2.5f)
                        .addByproduct(new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.INGOT * 4))
                        .build(consumer, location("smeltery/melting/cobalt/seared_duct"));
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
    this.addScorchedStonecutter(consumer, TinkerSmeltery.polishedScorchedStone, folder);
    this.addScorchedStonecutter(consumer, TinkerSmeltery.scorchedBricks, folder);
    this.addScorchedStonecutter(consumer, TinkerSmeltery.chiseledScorchedBricks, folder);

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
    this.registerSlabStair(consumer, TinkerSmeltery.scorchedBricks, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.scorchedRoad, folder, true);
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
                       .build(consumer, location(folder + "fuel_tank"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE))
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .key('B', Tags.Items.GEMS_QUARTZ)
                       .patternLine("#B#")
                       .patternLine("BBB")
                       .patternLine("#B#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, location(folder + "fuel_gauge"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedTank.get(TankType.INGOT_TANK))
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .key('B', Tags.Items.GEMS_QUARTZ)
                       .patternLine("#B#")
                       .patternLine("#B#")
                       .patternLine("#B#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, location(folder + "ingot_tank"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE))
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .key('B', Tags.Items.GEMS_QUARTZ)
                       .patternLine("B#B")
                       .patternLine("#B#")
                       .patternLine("B#B")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, location(folder + "ingot_gauge"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedLantern.get(), 3)
                       .key('C', Tags.Items.INGOTS_IRON)
                       .key('B', TinkerSmeltery.scorchedBrick)
                       .key('P', TinkerSmeltery.scorchedGlassPane)
                       .patternLine(" C ")
                       .patternLine("PPP")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, location(folder + "lantern"));

    // fluid transfer
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedFaucet.get(), 2)
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .patternLine("# #")
                       .patternLine(" # ")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, location(folder + "faucet"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedChannel.get(), 3)
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .patternLine("# #")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, location(folder + "channel"));

    // casting
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedBasin.get())
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .patternLine("# #")
                       .patternLine("# #")
                       .patternLine("###")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, location(folder + "basin"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedTable.get())
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .patternLine("###")
                       .patternLine("# #")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, location(folder + "table"));


    // peripherals
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedDrain)
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .key('C', TinkerCommons.obsidianPane)
                       .patternLine("# #")
                       .patternLine("C C")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, location(folder + "drain"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedChute)
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .key('C', TinkerCommons.obsidianPane)
                       .patternLine("#C#")
                       .patternLine("   ")
                       .patternLine("#C#")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, location(folder + "chute"));
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedDuct)
                       .key('#', TinkerSmeltery.scorchedBrick)
                       .key('C', TinkerMaterials.cobalt.getIngotTag())
                       .patternLine("# #")
                       .patternLine("C C")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerMaterials.cobalt.getIngotTag()))
                       .build(consumer, location(folder + "duct"));

    // controllers
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.scorchedAlloyer)
                       .key('G', Ingredient.fromItems(TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE), TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE)))
                       .key('B', TinkerSmeltery.scorchedBrick)
                       .patternLine("BGB")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.scorchedBrick))
                       .build(consumer, location(folder + "alloyer"));

    // casting
    String castingFolder = "smeltery/casting/scorched/";
    this.addBlockCastingRecipe(consumer, TinkerFluids.scorchedStone, MaterialValues.METAL_BRICK, TinkerSmeltery.scorchedStone, castingFolder + "stone_from_scorched");
    this.addIngotCastingRecipe(consumer, TinkerFluids.scorchedStone, TinkerSmeltery.scorchedBrick, castingFolder + "brick");
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedGlass)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenQuartz.get(), MaterialValues.GEM))
                            .setCast(TinkerSmeltery.scorchedBricks, true)
                            .build(consumer, location(castingFolder + "glass"));
    // discount for casting panes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.scorchedGlassPane)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenQuartz.get(), MaterialValues.GEM / 4))
                            .setCast(TinkerSmeltery.scorchedBrick, true)
                            .build(consumer, location(castingFolder + "glass_pane"));
    // craft scorched stone from magma and basalt
    addScorchedCastingRecipe(consumer, TinkerSmeltery.scorchedStone, Ingredient.fromItems(Blocks.BASALT ,Blocks.GRAVEL), castingFolder + "stone_from_magma");
    addScorchedCastingRecipe(consumer, TinkerSmeltery.polishedScorchedStone, Ingredient.fromItems(Blocks.POLISHED_BASALT), castingFolder + "polished_from_magma");
    // foundry controller
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.foundryController)
                            .setCast(TinkerSmeltery.scorchedBricks, true) // TODO: can I find a "heater" for the nether?
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK))
                            .build(consumer, prefix(TinkerSmeltery.foundryController, castingFolder));


    // melting
    String meltingFolder = "smeltery/melting/scorched/";

    // double efficiency when using smeltery for grout
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.netherGrout), TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 2, 1.5f)
                        .build(consumer, location(meltingFolder + "grout"));

    // scorched stone
    // stairs are here since the cheapest stair recipe is stone cutter, 1 to 1
    MeltingRecipeBuilder.melting(CompoundIngredient.from(Ingredient.fromTag(TinkerTags.Items.SCORCHED_BLOCKS),
                                                         Ingredient.fromItems(TinkerSmeltery.scorchedLadder, TinkerSmeltery.scorchedBricks.getStairs(), TinkerSmeltery.scorchedRoad.getStairs())),
                                 TinkerFluids.scorchedStone.get(), MaterialValues.METAL_BRICK, 2.0f)
                        .build(consumer, location(meltingFolder + "block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedBricks.getSlab(), TinkerSmeltery.scorchedBricks.getSlab(), TinkerSmeltery.scorchedRoad.getSlab()),
                                 TinkerFluids.scorchedStone.get(), MaterialValues.METAL_BRICK / 2, 1.5f)
                        .build(consumer, location(meltingFolder + "slab"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedBrick), TinkerFluids.scorchedStone.get(), MaterialValues.INGOT, 1.0f)
                        .build(consumer, location(meltingFolder + "brick"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedBricks.getFence()), TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 3, 1.0f)
                        .build(consumer, location(meltingFolder + "fence"));

    // melt down foundry components
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedFaucet), TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 3 / 2, 1.5f)
                        .build(consumer, location(meltingFolder + "faucet"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedChannel), TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 5 / 3, 1.5f)
                        .build(consumer, location(meltingFolder + "channel"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedBasin, TinkerSmeltery.scorchedTable), TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 7, 2.5f)
                        .build(consumer, location(meltingFolder + "casting"));
    // tanks
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.scorchedTank.get(TankType.FUEL_TANK))), TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 8, 3f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), MaterialValues.GEM))
                        .build(consumer, location(meltingFolder + "fuel_tank"));
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.scorchedTank.get(TankType.INGOT_TANK))), TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 6, 2.5f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), MaterialValues.GEM * 3))
                        .build(consumer, location(meltingFolder + "ingot_tank"));
    MeltingRecipeBuilder.melting(CompoundIngredient.from(NBTIngredient.from(new ItemStack(TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE))),
                                                         NBTIngredient.from(new ItemStack(TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE)))),
                                 TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 4, 2f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), MaterialValues.GEM * 5))
                        .build(consumer, location(meltingFolder + "gauge"));
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.scorchedLantern)), TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 2, 1.0f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), MaterialValues.GEM / 4))
                        .addByproduct(new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.INGOT / 3))
                        .build(consumer, location(meltingFolder + "lantern"));
    // glass
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedGlass), TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 4, 2f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), MaterialValues.GEM))
                        .build(consumer, location(meltingFolder + "glass"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedGlassPane), TinkerFluids.scorchedStone.get(), MaterialValues.INGOT, 1.0f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), MaterialValues.GEM / 4))
                        .build(consumer, location(meltingFolder + "pane"));
    // controllers
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedAlloyer), TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 9, 3.5f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), MaterialValues.GEM * 5))
                        .build(consumer, location(meltingFolder + "melter"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.foundryController), TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK, 3.5f)
                        .addByproduct(new FluidStack(TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 4))
                        .build(consumer, location("smeltery/melting/obsidian/foundry_controller"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedDrain, TinkerSmeltery.scorchedChute), TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_PANE * 2, 2.5f)
                        .addByproduct(new FluidStack(TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 4))
                        .build(consumer, location("smeltery/melting/obsidian/foundry_io"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.scorchedDuct), TinkerFluids.moltenCobalt.get(), MaterialValues.INGOT * 2, 2.5f)
                        .addByproduct(new FluidStack(TinkerFluids.scorchedStone.get(), MaterialValues.INGOT * 4))
                        .build(consumer, location("smeltery/melting/cobalt/scorched_duct"));
  }

  private void addCastingRecipes(Consumer<IFinishedRecipe> consumer) {
    // Pure Fluid Recipes
    String folder = "smeltery/casting/";

    // container filling
    ContainerFillingRecipeBuilder.tableRecipe(Items.BUCKET, FluidAttributes.BUCKET_VOLUME)
                                 .build(consumer, location(folder + "filling/bucket"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.copperCan, MaterialValues.INGOT)
                                 .build(consumer, location(folder + "filling/copper_can"));
    // tank filling - seared
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.INGOT_TANK), MaterialValues.INGOT)
                                 .build(consumer, location(folder + "filling/seared_ingot_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE), MaterialValues.INGOT)
                                 .build(consumer, location(folder + "filling/seared_ingot_gauge"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.FUEL_TANK), FluidAttributes.BUCKET_VOLUME / 4)
                                 .build(consumer, location(folder + "filling/seared_fuel_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE), FluidAttributes.BUCKET_VOLUME / 4)
                                 .build(consumer, location(folder + "filling/seared_fuel_gauge"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.searedLantern, MaterialValues.NUGGET)
                                 .build(consumer, location(folder + "filling/seared_lantern_pixel"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedLantern, FluidAttributes.BUCKET_VOLUME / 10)
                                 .build(consumer, location(folder + "filling/seared_lantern_full"));
    // tank filling - scorched
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.INGOT_TANK), MaterialValues.INGOT)
                                 .build(consumer, location(folder + "filling/scorched_ingot_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE), MaterialValues.INGOT)
                                 .build(consumer, location(folder + "filling/scorched_ingot_gauge"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.FUEL_TANK), FluidAttributes.BUCKET_VOLUME / 4)
                                 .build(consumer, location(folder + "filling/scorched_fuel_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE), FluidAttributes.BUCKET_VOLUME / 4)
                                 .build(consumer, location(folder + "filling/scorched_fuel_gauge"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.scorchedLantern, MaterialValues.NUGGET)
                                 .build(consumer, location(folder + "filling/scorched_lantern_pixel"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedLantern, FluidAttributes.BUCKET_VOLUME / 10)
                                 .build(consumer, location(folder + "filling/scorched_lantern_full"));

    // Slime
    String slimeFolder = folder + "slime/";
    this.addSlimeCastingRecipe(consumer, TinkerFluids.blood.getLocalTag(),      getTemperature(TinkerFluids.blood),      SlimeType.BLOOD, slimeFolder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.earthSlime.getForgeTag(), getTemperature(TinkerFluids.earthSlime), SlimeType.EARTH, slimeFolder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.skySlime.getLocalTag(),   getTemperature(TinkerFluids.skySlime),   SlimeType.SKY, slimeFolder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.enderSlime.getLocalTag(), getTemperature(TinkerFluids.enderSlime), SlimeType.ENDER, slimeFolder);
    // magma cream
    addBlockCastingRecipe(consumer, TinkerFluids.magma.getForgeTag(), getTemperature(TinkerFluids.magma), MaterialValues.SLIME_CONGEALED, Blocks.MAGMA_BLOCK, slimeFolder + "magma_block");

    // glass
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenGlass, MaterialValues.GLASS_BLOCK, TinkerCommons.clearGlass, folder + "glass/block");
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.clearGlassPane)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_PANE))
                            .build(consumer, location(folder + "glass/pane"));
    // soul glass
    this.addBlockCastingRecipe(consumer, TinkerFluids.liquidSoul, MaterialValues.GLASS_BLOCK, TinkerCommons.soulGlass, folder + "soul/glass");
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.soulGlassPane)
                            .setFluidAndTime(new FluidStack(TinkerFluids.liquidSoul.get(), MaterialValues.GLASS_PANE))
                            .build(consumer, location(folder + "soul/pane"));

    // clay
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenClay, MaterialValues.SLIME_CONGEALED, Blocks.TERRACOTTA, folder + "clay/block");
    this.addIngotCastingRecipe(consumer, TinkerFluids.moltenClay, MaterialValues.SLIMEBALL, Items.BRICK, folder + "clay/brick");
    this.addTagCastingWithCast(consumer, TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL, TinkerSmeltery.plateCast, "plates", "brick", folder + "clay/plate", true);

    // emeralds
    this.addGemCastingRecipe(consumer, TinkerFluids.moltenEmerald, Items.EMERALD, folder + "emerald/gem");
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenEmerald, MaterialValues.GEM_BLOCK, Items.EMERALD_BLOCK, folder + "emerald/block");

    // quartz
    this.addGemCastingRecipe(consumer, TinkerFluids.moltenQuartz, Items.QUARTZ, folder + "quartz/gem");
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenQuartz, MaterialValues.GEM * 4, Items.QUARTZ_BLOCK, folder + "quartz/block");

    // diamond
    this.addGemCastingRecipe(consumer, TinkerFluids.moltenDiamond, Items.DIAMOND, folder + "diamond/gem");
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenDiamond, MaterialValues.GEM_BLOCK, Items.DIAMOND_BLOCK, folder + "diamond/block");

    // ender pearls
    ItemCastingRecipeBuilder.tableRecipe(Items.ENDER_PEARL)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenEnder.get(), MaterialValues.SLIMEBALL))
                            .build(consumer, location(folder + "ender/pearl"));
    ItemCastingRecipeBuilder.tableRecipe(Items.ENDER_EYE)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenEnder.get(), MaterialValues.SLIMEBALL))
                            .setCast(Items.BLAZE_POWDER, true)
                            .build(consumer, location(folder + "ender/eye"));

    // obsidian
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenObsidian, MaterialValues.GLASS_BLOCK, Items.OBSIDIAN, folder + "obsidian/block");
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.obsidianPane)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_PANE))
                            .build(consumer, location(folder + "obsidian/pane"));
    // Molten objects with Bucket, Block, Ingot, and Nugget forms with standard values
    String metalFolder = folder + "metal/";
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenIron,      Items.IRON_BLOCK,       Items.IRON_INGOT,      Items.IRON_NUGGET,               metalFolder, "iron");
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenGold,      Items.GOLD_BLOCK,       Items.GOLD_INGOT,      Items.GOLD_NUGGET,               metalFolder, "gold");
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenNetherite, Blocks.NETHERITE_BLOCK, Items.NETHERITE_INGOT, TinkerMaterials.netheriteNugget, metalFolder, "netherite");
    this.addIngotCastingRecipe(consumer, TinkerFluids.moltenDebris, Items.NETHERITE_SCRAP, metalFolder + "netherite/scrap");
    this.addNuggetCastingRecipe(consumer, TinkerFluids.moltenDebris, TinkerMaterials.debrisNugget, metalFolder + "netherite/debris_nugget");

    // anything common uses tag output, if its unique to us (slime metals mostly), use direct output
    // ores
    this.addMetalTagCasting(consumer, TinkerFluids.moltenCopper.get(), "copper", metalFolder, true);
    this.addMetalTagCasting(consumer, TinkerFluids.moltenCobalt.get(), "cobalt", metalFolder, true);
    // tier 3 alloys
    this.addMetalTagCasting(consumer, TinkerFluids.moltenTinkersBronze.get(), "silicon_bronze", metalFolder, true);
    this.addMetalTagCasting(consumer, TinkerFluids.moltenRoseGold.get(),      "rose_gold",      metalFolder, true);
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenSlimesteel,    TinkerMaterials.slimesteel,    metalFolder, "slimesteel");
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenPigIron,       TinkerMaterials.pigIron,       metalFolder, "pig_iron");
    // tier 4 alloys
    this.addMetalTagCasting(consumer, TinkerFluids.moltenManyullyn.get(), "manyullyn", metalFolder, true);
    this.addMetalTagCasting(consumer, TinkerFluids.moltenHepatizon.get(), "hepatizon", metalFolder, true);
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenQueensSlime, TinkerMaterials.queensSlime, metalFolder, "queens_slime");
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenSoulsteel,   TinkerMaterials.soulsteel,   metalFolder, "soulsteel");
    // tier 5 alloys
    this.addMetalCastingRecipe(consumer, TinkerFluids.moltenKnightslime, TinkerMaterials.knightslime, metalFolder, "knightslime");

    // compat
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      this.addMetalTagCasting(consumer, compat.getFluid(), compat.getName(), metalFolder, false);
    }

    // misc
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.lavawood)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10))
                            .setCast(ItemTags.PLANKS, true)
                            .build(consumer, prefix(TinkerCommons.lavawood, folder));
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.blazewood)
                            .setFluidAndTime(new FluidStack(TinkerFluids.blazingBlood.get(), FluidAttributes.BUCKET_VOLUME / 10))
                            .setCast(new IngredientIntersection(Ingredient.fromTag(ItemTags.PLANKS), Ingredient.fromTag(ItemTags.NON_FLAMMABLE_WOOD)), true)
                            .build(consumer, prefix(TinkerCommons.blazewood, folder));
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.mudBricks)
                            .setFluidAndTime(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 10))
                            .setCast(Items.DIRT, true)
                            .build(consumer, prefix(TinkerCommons.mudBricks, folder));

    // cast molten blaze into blaze rods
    addCastingWithCastRecipe(consumer, TinkerFluids.blazingBlood, FluidAttributes.BUCKET_VOLUME / 10, TinkerSmeltery.rodCast, Items.BLAZE_ROD, folder + "blaze/rod");

    // Cast recipes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.blankCast)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.INGOT))
                            .setSwitchSlots()
                            .build(consumer, location(folder + "casts/blank"));

    this.addCastCastingRecipe(consumer, Tags.Items.INGOTS, TinkerSmeltery.ingotCast, folder);
    this.addCastCastingRecipe(consumer, Tags.Items.NUGGETS, TinkerSmeltery.nuggetCast, folder);
    this.addCastCastingRecipe(consumer, Tags.Items.GEMS, TinkerSmeltery.gemCast, folder);
    this.addCastCastingRecipe(consumer, Tags.Items.RODS, TinkerSmeltery.rodCast, folder);
    // other casts are added if needed
    this.addCastCastingRecipe(withCondition(consumer, tagCondition("plates")), getTag("forge", "plates"), TinkerSmeltery.plateCast, folder);
    this.addCastCastingRecipe(withCondition(consumer, tagCondition("gears")),  getTag("forge", "gears"),  TinkerSmeltery.gearCast,  folder);
    this.addCastCastingRecipe(withCondition(consumer, tagCondition("coins")),  getTag("forge", "coins"),  TinkerSmeltery.coinCast,  folder);

    // misc casting - gold
    ItemCastingRecipeBuilder.tableRecipe(Items.GOLDEN_APPLE)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 8))
                            .setCast(Items.APPLE, true)
                            .build(consumer, location(metalFolder + "gold/apple"));
    ItemCastingRecipeBuilder.tableRecipe(Items.GLISTERING_MELON_SLICE)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.NUGGET * 8))
                            .setCast(Items.MELON_SLICE, true)
                            .build(consumer, location(metalFolder + "gold/melon"));
    ItemCastingRecipeBuilder.tableRecipe(Items.GOLDEN_CARROT)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.NUGGET * 8))
                            .setCast(Items.CARROT, true)
                            .build(consumer, location(metalFolder + "gold/carrot"));
    ItemCastingRecipeBuilder.tableRecipe(Items.CLOCK)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.INGOT * 4))
                            .setCast(Items.REDSTONE, true)
                            .build(consumer, location(metalFolder + "gold/clock"));
    // misc casting - iron
    ItemCastingRecipeBuilder.tableRecipe(Blocks.IRON_BARS)  // cheaper by 6mb, not a duplication as the melting recipe was adjusted too (like panes)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.NUGGET * 3))
                            .build(consumer, location(metalFolder + "iron/bars"));
    ItemCastingRecipeBuilder.tableRecipe(Items.LANTERN)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.NUGGET * 8))
                            .setCast(Blocks.TORCH, true)
                            .build(consumer, location(metalFolder + "iron/lantern"));
    ItemCastingRecipeBuilder.tableRecipe(Items.SOUL_LANTERN)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.NUGGET * 8))
                            .setCast(Blocks.SOUL_TORCH, true)
                            .build(consumer, location(metalFolder + "iron/soul_lantern"));
    ItemCastingRecipeBuilder.tableRecipe(Items.COMPASS)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 4))
                            .setCast(Items.REDSTONE, true)
                            .build(consumer, location(metalFolder + "iron/compass"));
    // ender chest
    ItemCastingRecipeBuilder.basinRecipe(Blocks.ENDER_CHEST)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK * 8))
                            .setCast(Items.ENDER_EYE, true)
                            .build(consumer, location(folder + "obsidian/chest"));
    // overworld stones from quartz
    ItemCastingRecipeBuilder.basinRecipe(Blocks.DIORITE)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenQuartz.get(), MaterialValues.GEM))
                            .setCast(Tags.Items.COBBLESTONE, true)
                            .build(consumer, prefix(Blocks.DIORITE, folder + "quartz/"));
    ItemCastingRecipeBuilder.basinRecipe(Blocks.GRANITE)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenQuartz.get(), MaterialValues.GEM))
                            .setCast(Blocks.DIORITE, true)
                            .build(consumer, prefix(Blocks.GRANITE, folder + "quartz/"));
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
    addMetalMelting(consumer, TinkerFluids.moltenIron.get(),   "iron",   true, metalFolder, false, Byproduct.NICKEL, Byproduct.COPPER);
    addMetalMelting(consumer, TinkerFluids.moltenGold.get(),   "gold",   true, metalFolder, false, Byproduct.SILVER, Byproduct.COPPER);
    addMetalMelting(consumer, TinkerFluids.moltenCopper.get(), "copper", true, metalFolder, false, Byproduct.SMALL_GOLD);
    addMetalMelting(consumer, TinkerFluids.moltenCobalt.get(), "cobalt", true, metalFolder, false, Byproduct.IRON);

    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.ORES_NETHERITE_SCRAP), TinkerFluids.moltenDebris.get(), MaterialValues.INGOT, 2.0f)
                        .setOre()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), MaterialValues.GEM / 3))
                        .addByproduct(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.INGOT))
                        .build(consumer, location(metalFolder + "molten_debris/ore"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.INGOTS_NETHERITE_SCRAP), TinkerFluids.moltenDebris.get(), MaterialValues.INGOT, 1.0f)
                        .build(consumer, location(metalFolder + "molten_debris/scrap"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.NUGGETS_NETHERITE_SCRAP), TinkerFluids.moltenDebris.get(), MaterialValues.NUGGET, 1/3f)
                        .build(consumer, location(metalFolder + "molten_debris/debris_nugget"));
    
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
      this.addMetalMelting(consumer, compat.getFluid(), compat.getName(), compat.isOre(), metalFolder, true, compat.getByproducts());
    }

    // blood
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.ROTTEN_FLESH), TinkerFluids.blood.get(), MaterialValues.SLIMEBALL / 5, 1.0f)
                        .build(consumer, location(folder + "slime/blood/flesh"));

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
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerCommons.soulGlassPane), TinkerFluids.liquidSoul.get(), MaterialValues.GLASS_PANE, 0.5f)
                        .build(consumer, location(folder + "soul/pane"));

    // clay
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.CLAY), TinkerFluids.moltenClay.get(), MaterialValues.SLIME_CONGEALED, 1.0f)
                        .build(consumer, location(folder + "clay/block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.CLAY_BALL), TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL, 0.5f)
                        .build(consumer, location(folder + "clay/ball"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.FLOWER_POT), TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 3, 2.0f)
                        .build(consumer, location(folder + "clay/pot"));
    addMetalBase(consumer, TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL, "plates/brick", 1.0f, folder + "clay/plate", true);
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

    // slime
    String slimeFolder = folder + "slime/";
    addSlimeMeltingRecipe(consumer, TinkerFluids.earthSlime, SlimeType.EARTH, TinkerTags.Items.EARTH_SLIMEBALL, slimeFolder);
    addSlimeMeltingRecipe(consumer, TinkerFluids.skySlime, SlimeType.SKY, TinkerTags.Items.SKY_SLIMEBALL, slimeFolder);
    addSlimeMeltingRecipe(consumer, TinkerFluids.enderSlime, SlimeType.ENDER, TinkerTags.Items.ENDER_SLIMEBALL, slimeFolder);
    addSlimeMeltingRecipe(consumer, TinkerFluids.blood, SlimeType.BLOOD, TinkerTags.Items.BLOOD_SLIMEBALL, slimeFolder);
    // magma cream
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.MAGMA_CREAM), TinkerFluids.magma.get(), MaterialValues.SLIMEBALL, 1.0f)
                        .build(consumer, location(slimeFolder + "magma/ball"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.MAGMA_BLOCK), TinkerFluids.magma.get(), MaterialValues.SLIME_CONGEALED, 3.0f)
                        .build(consumer, location(slimeFolder + "magma/block"));

    // copper cans if empty
    MeltingRecipeBuilder.melting(NBTIngredient.from(new ItemStack(TinkerSmeltery.copperCan)), TinkerFluids.moltenCopper.get(), MaterialValues.INGOT, 1.0f)
                        .build(consumer, location(metalFolder + "copper/can"));
    // ender
    MeltingRecipeBuilder.melting(
      CompoundIngredient.from(Ingredient.fromTag(Tags.Items.ENDER_PEARLS), Ingredient.fromItems(Items.ENDER_EYE)),
      TinkerFluids.moltenEnder.get(), MaterialValues.SLIMEBALL, 1.0f)
                        .build(consumer, location(folder + "ender/pearl"));

    // obsidian
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.OBSIDIAN), TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK, 2.0f)
                        .build(consumer, location(folder + "obsidian/block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerCommons.obsidianPane), TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_PANE, 1.5f)
                        .build(consumer, location(folder + "obsidian/pane"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.ENDER_CHEST), TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK * 8, 5.0f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenEnder.get(), MaterialValues.SLIMEBALL))
                        .build(consumer, location(folder + "obsidian/chest"));
    addMetalBase(consumer, TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_PANE, "dusts/obsidian", 1.0f, folder + "obsidian/dust", true);

    // emerald
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.ORES_EMERALD), TinkerFluids.moltenEmerald.get(), MaterialValues.GEM, 1.5f)
                        .setOre()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), MaterialValues.GEM / 3))
                        .build(consumer, location(folder + "emerald/ore"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GEMS_EMERALD), TinkerFluids.moltenEmerald.get(), MaterialValues.GEM, 1.0f)
                        .build(consumer, location(folder + "emerald/gem"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_EMERALD), TinkerFluids.moltenEmerald.get(), MaterialValues.GEM_BLOCK, 3.0f)
                        .build(consumer, location(folder + "emerald/block"));

    // quartz
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.ORES_QUARTZ), TinkerFluids.moltenQuartz.get(), MaterialValues.GEM, 1.5f)
                        .setOre()
                        .addByproduct(new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL))
                        .build(consumer, location(folder + "quartz/ore"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GEMS_QUARTZ), TinkerFluids.moltenQuartz.get(), MaterialValues.GEM, 1.0f)
                        .build(consumer, location(folder + "quartz/gem"));
    MeltingRecipeBuilder.melting(
      CompoundIngredient.from(Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_QUARTZ), Ingredient.fromItems(Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_BRICKS, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ_STAIRS)),
      TinkerFluids.moltenQuartz.get(), MaterialValues.GEM * 4, 2.0f)
                        .build(consumer, location(folder + "quartz/block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ_SLAB), TinkerFluids.moltenQuartz.get(), MaterialValues.GEM * 2, 1.5f)
                        .build(consumer, location(folder + "quartz/slab"));

    // diamond
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.ORES_DIAMOND), TinkerFluids.moltenDiamond.get(), MaterialValues.GEM, 1.5f)
                        .setOre()
                        .addByproduct(new FluidStack(TinkerFluids.moltenQuartz.get(), MaterialValues.GEM / 3))
                        .build(consumer, location(folder + "diamond/ore"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GEMS_DIAMOND), TinkerFluids.moltenDiamond.get(), MaterialValues.GEM, 1.0f)
                        .build(consumer, location(folder + "diamond/gem"));
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_DIAMOND), TinkerFluids.moltenDiamond.get(), MaterialValues.GEM_BLOCK, 3.0f)
                        .build(consumer, location(folder + "diamond/block"));

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
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.RAIL), TinkerFluids.moltenIron.get(), MaterialValues.INGOT * 6 / 16)
                        .build(consumer, location(metalFolder + "iron/ingot_6_16"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.IRON_BARS), TinkerFluids.moltenIron.get(), MaterialValues.NUGGET * 3)
                        .build(consumer, location(metalFolder + "iron/nugget_3"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.TRIPWIRE_HOOK), TinkerFluids.moltenIron.get(), MaterialValues.INGOT / 2)
                        .build(consumer, location(metalFolder + "iron/tripwire"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.LANTERN, Blocks.SOUL_LANTERN), TinkerFluids.moltenIron.get(), MaterialValues.NUGGET * 8)
                        .build(consumer, location(metalFolder + "iron/lantern"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerModifiers.ironReinforcement), TinkerFluids.moltenIron.get(), MaterialValues.NUGGET * 3)
                        .addByproduct(new FluidStack(TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_PANE))
                        .build(consumer, location(metalFolder + "iron/reinforcement"));
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

    // diamond melting
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.JUKEBOX), TinkerFluids.moltenDiamond.get(), MaterialValues.GEM)
                        .build(consumer, location(folder + "diamond/jukebox"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.ENCHANTING_TABLE), TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 2)
                        .addByproduct(new FluidStack(TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK * 4))
                        .build(consumer, location(folder + "diamond/enchanting_table"));
    // armor
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_HELMET), TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 5)
                        .setDamagable()
                        .build(consumer, location(folder + "diamond/helmet"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_CHESTPLATE), TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 8)
                        .setDamagable()
                        .build(consumer, location(folder + "diamond/chestplate"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_LEGGINGS), TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 7)
                        .setDamagable()
                        .build(consumer, location(folder + "diamond/leggings"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_BOOTS), TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 4)
                        .setDamagable()
                        .build(consumer, location(folder + "diamond/boots"));
    // tools
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_AXE, Items.DIAMOND_PICKAXE), TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 3)
                        .setDamagable()
                        .build(consumer, location(folder + "diamond/axes"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_SWORD, Items.DIAMOND_HOE), TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 2)
                        .setDamagable()
                        .build(consumer, location(folder + "diamond/weapon"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_SHOVEL), TinkerFluids.moltenDiamond.get(), MaterialValues.GEM)
                        .setDamagable()
                        .build(consumer, location(folder + "diamond/shovel"));
    // unique melting
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.DIAMOND_HORSE_ARMOR), TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 7)
                        .build(consumer, location(folder + "diamond/horse_armor"));

    // netherite
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.LODESTONE), TinkerFluids.moltenNetherite.get(), MaterialValues.INGOT)
                        .build(consumer, location(metalFolder + "netherite/lodestone"));
    // armor
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_HELMET), TinkerFluids.moltenNetherite.get(), MaterialValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 5))
                        .build(consumer, location(folder + "netherite/helmet"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_CHESTPLATE), TinkerFluids.moltenNetherite.get(), MaterialValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 8))
                        .build(consumer, location(folder + "netherite/chestplate"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_LEGGINGS), TinkerFluids.moltenNetherite.get(), MaterialValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 7))
                        .build(consumer, location(folder + "netherite/leggings"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_BOOTS), TinkerFluids.moltenNetherite.get(), MaterialValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 4))
                        .build(consumer, location(folder + "netherite/boots"));
    // tools
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_AXE, Items.NETHERITE_PICKAXE), TinkerFluids.moltenNetherite.get(), MaterialValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 3))
                        .build(consumer, location(folder + "netherite/axes"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_SWORD, Items.NETHERITE_HOE), TinkerFluids.moltenNetherite.get(), MaterialValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), MaterialValues.GEM * 2))
                        .build(consumer, location(folder + "netherite/weapon"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.NETHERITE_SHOVEL), TinkerFluids.moltenNetherite.get(), MaterialValues.INGOT)
                        .setDamagable()
                        .addByproduct(new FluidStack(TinkerFluids.moltenDiamond.get(), MaterialValues.GEM))
                        .build(consumer, location(folder + "netherite/shovel"));

    // quartz
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.OBSERVER, Blocks.COMPARATOR, TinkerGadgets.quartzShuriken), TinkerFluids.moltenQuartz.get(), MaterialValues.GEM * 3)
                        .build(consumer, location(folder + "quartz/gem_1"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.DAYLIGHT_DETECTOR), TinkerFluids.moltenQuartz.get(), MaterialValues.GEM * 3)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_BLOCK * 3))
                        .build(consumer, location(folder + "quartz/daylight_detector"));

    // obsidian, if you are crazy i guess
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Blocks.BEACON), TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK * 3)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_BLOCK * 5))
                        .build(consumer, location(folder + "obsidian/beacon"));

    // ender
    MeltingRecipeBuilder.melting(Ingredient.fromItems(Items.END_CRYSTAL), TinkerFluids.moltenEnder.get(), MaterialValues.SLIMEBALL)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_BLOCK * 7))
                        .build(consumer, location(folder + "ender/end_crystal"));
    // it may be silky, but its still rose gold
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerModifiers.silkyCloth), TinkerFluids.moltenRoseGold.get(), MaterialValues.INGOT)
                        .build(consumer, location(metalFolder + "rose_gold/silky_cloth"));
    // slimesteel? Just doing it all at this point
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerModifiers.slimesteelReinforcement), TinkerFluids.moltenSlimesteel.get(), MaterialValues.NUGGET * 3)
                        .addByproduct(new FluidStack(TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_PANE))
                        .build(consumer, location(metalFolder + "slimesteel/reinforcement"));

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
    TinkerModifiers.slimeCrystal.forEach((type, crystal) -> {
      if (type != SlimeType.ICHOR) { // no ichor fluid
        MeltingRecipeBuilder.melting(Ingredient.fromItems(crystal), TinkerFluids.slime.get(type).get(), MaterialValues.SLIMEBALL)
                            .setDamagable()
                            .build(consumer, location(slimeFolder + type.getString() + "/crystal"));
      }
    });
    // recycle saplings
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.slimeSapling.get(SlimeType.EARTH)), TinkerFluids.earthSlime.get(), MaterialValues.SLIMEBALL)
                        .build(consumer, location(slimeFolder + "earth/sapling"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.slimeSapling.get(SlimeType.SKY)), TinkerFluids.skySlime.get(), MaterialValues.SLIMEBALL)
                        .build(consumer, location(slimeFolder + "sky/sapling"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.slimeSapling.get(SlimeType.ENDER)), TinkerFluids.enderSlime.get(), MaterialValues.SLIMEBALL)
                        .build(consumer, location(slimeFolder + "ender/sapling"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerWorld.slimeSapling.get(SlimeType.BLOOD)), TinkerFluids.blood.get(), MaterialValues.SLIMEBALL)
                        .build(consumer, location(slimeFolder + "blood/sapling"));

    // fuels
    MeltingFuelBuilder.fuel(new FluidStack(Fluids.LAVA, 50), 100)
                      .build(consumer, location(folder + "fuel/lava"));
    MeltingFuelBuilder.fuel(new FluidStack(TinkerFluids.blazingBlood.get(), 50), 150)
                      .build(consumer, location(folder + "fuel/blaze"));
  }

  private void addMaterialRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/materials/";

    // melting and casting
    // tier 2
    addMaterialMeltingCasting(consumer, MaterialIds.iron,          TinkerFluids.moltenIron,   folder);
    addMaterialMeltingCasting(consumer, MaterialIds.copper,        TinkerFluids.moltenCopper, folder);
    addMaterialMeltingCasting(consumer, MaterialIds.searedStone,   TinkerFluids.searedStone,   MaterialValues.INGOT * 2, folder);
    addMaterialMeltingCasting(consumer, MaterialIds.scorchedStone, TinkerFluids.scorchedStone, MaterialValues.INGOT * 2, folder);
    // half a clay is 1 seared brick per grout amounts
    addCompositeMaterialRecipe(consumer, MaterialIds.stone, MaterialIds.searedStone,   TinkerFluids.moltenClay, MaterialValues.SLIMEBALL, false, folder);
    addCompositeMaterialRecipe(consumer, MaterialIds.wood,  MaterialIds.slimewood,     TinkerFluids.earthSlime, MaterialValues.SLIMEBALL, true,  folder);
    addCompositeMaterialRecipe(consumer, MaterialIds.flint, MaterialIds.scorchedStone, TinkerFluids.magma,      MaterialValues.SLIMEBALL, true,  folder);

    // tier 3
    addMaterialMeltingCasting(consumer, MaterialIds.slimesteel,    TinkerFluids.moltenSlimesteel,    folder);
    addMaterialMeltingCasting(consumer, MaterialIds.tinkersBronze, TinkerFluids.moltenTinkersBronze, folder);
    addMaterialMeltingCasting(consumer, MaterialIds.roseGold,      TinkerFluids.moltenRoseGold,      folder);
    addMaterialMeltingCasting(consumer, MaterialIds.pigIron,       TinkerFluids.moltenPigIron,       folder);
    addMaterialMeltingCasting(consumer, MaterialIds.cobalt,        TinkerFluids.moltenCobalt,        folder);
    addCompositeMaterialRecipe(consumer, MaterialIds.wood, MaterialIds.nahuatl, TinkerFluids.moltenObsidian, MaterialValues.GLASS_BLOCK, false, folder);
    MaterialMeltingRecipeBuilder.material(MaterialIds.nahuatl, new FluidStack(TinkerFluids.moltenObsidian.get(), MaterialValues.GLASS_BLOCK))
                                .build(consumer, location(folder + "melting/nahuatl"));

    // tier 4
    addMaterialMeltingCasting(consumer, MaterialIds.queensSlime, TinkerFluids.moltenQueensSlime, folder);
    addMaterialMeltingCasting(consumer, MaterialIds.hepatizon,   TinkerFluids.moltenHepatizon,   folder);
    addMaterialMeltingCasting(consumer, MaterialIds.manyullyn,   TinkerFluids.moltenManyullyn,   folder);

    // tier 2 compat
    addMaterialMeltingCasting(consumer, MaterialIds.lead,   TinkerFluids.moltenLead,   folder);
    addMaterialMeltingCasting(consumer, MaterialIds.silver, TinkerFluids.moltenSilver, folder);
    // tier 3 compat
    addMaterialMeltingCasting(consumer, MaterialIds.electrum,   TinkerFluids.moltenElectrum,   folder);
    addMaterialMeltingCasting(consumer, MaterialIds.bronze,     TinkerFluids.moltenBronze,     folder);
    addMaterialMeltingCasting(consumer, MaterialIds.steel,      TinkerFluids.moltenSteel,      folder);
    addMaterialMeltingCasting(consumer, MaterialIds.constantan, TinkerFluids.moltenConstantan, folder);
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
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenTinkersBronze.get(), MaterialValues.INGOT * 3)
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
                      .addInput(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 20)
                      .addInput(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10)
                      .build(consumer, prefixR(TinkerFluids.moltenObsidian, folder));

    // tier 4

    // queens slime: 1 cobalt + 1 gold + 1 magma cream = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenQueensSlime.get(), MaterialValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCobalt.get(), MaterialValues.INGOT)
                      .addInput(TinkerFluids.moltenGold.get(), MaterialValues.INGOT)
                      .addInput(TinkerFluids.magma.getForgeTag(), MaterialValues.SLIMEBALL)
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
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenBrass.get(), MaterialValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.get(), MaterialValues.INGOT)
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

    // pewter
    wrapped = withCondition(consumer, tagCondition("ingots/pewter"), tagCondition("ingots/lead"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenPewter.get(), MaterialValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenIron.get(), MaterialValues.INGOT)
                      .addInput(TinkerFluids.moltenLead.get(), MaterialValues.INGOT)
                      .build(wrapped, prefixR(TinkerFluids.moltenPewter, folder));
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
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SLIME, TinkerWorld.earthSlimeEntity.get()), new FluidStack(TinkerFluids.earthSlime.get(), MaterialValues.SLIMEBALL / 10))
                              .build(consumer, prefixR(EntityType.SLIME, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.skySlimeEntity.get()), new FluidStack(TinkerFluids.skySlime.get(), MaterialValues.SLIMEBALL / 10))
                              .build(consumer, prefixR(TinkerWorld.skySlimeEntity, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.enderSlimeEntity.get()), new FluidStack(TinkerFluids.enderSlime.get(), MaterialValues.SLIMEBALL / 10))
                              .build(consumer, prefixR(TinkerWorld.enderSlimeEntity, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.MAGMA_CUBE), new FluidStack(TinkerFluids.magma.get(), MaterialValues.SLIMEBALL / 10))
                              .build(consumer, prefixR(EntityType.MAGMA_CUBE, folder));

    // iron golems can be healed using an iron ingot 25 health
    // 4 * 9 gives 36, which is larger
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.IRON_GOLEM), new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.NUGGET), 4)
                              .build(consumer, prefixR(EntityType.IRON_GOLEM, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SNOW_GOLEM), new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 10))
                              .build(consumer, prefixR(EntityType.SNOW_GOLEM, folder));

    // "melt" blazes to get fuel
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.BLAZE), new FluidStack(TinkerFluids.blazingBlood.get(), FluidAttributes.BUCKET_VOLUME / 50), 2)
                              .build(consumer, prefixR(EntityType.BLAZE, folder));

    // guardians are rock, seared stone is rock, don't think about it too hard
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN), new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.NUGGET), 4)
                              .build(consumer, prefixR(EntityType.GUARDIAN, folder));
    // silverfish also seem like rock, sorta?
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SILVERFISH), new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.NUGGET), 2)
                              .build(consumer, prefixR(EntityType.SILVERFISH, folder));

    // villagers melt into emerald, but they die quite quick
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.VILLAGER, EntityType.WANDERING_TRADER),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), MaterialValues.GEM / 9), 5)
                              .build(consumer, prefixR(EntityType.VILLAGER, folder));
    // illagers are more resistant, they resist the villager culture afterall
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.EVOKER, EntityType.ILLUSIONER, EntityType.PILLAGER, EntityType.VINDICATOR),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), MaterialValues.GEM / 9), 2)
                              .build(consumer, location(folder + "illager"));
    // zombie villagers and witches faintly recall being a villager once
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ZOMBIE_VILLAGER, EntityType.WITCH),
                                       new FluidStack(TinkerFluids.moltenEmerald.get(), MaterialValues.GEM / 18), 3)
                              .build(consumer, prefixR(EntityType.ZOMBIE_VILLAGER, folder));

    // melt ender for the molten ender
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON),
                                       new FluidStack(TinkerFluids.moltenEnder.get(), MaterialValues.GEM / 18), 2)
                              .build(consumer, location(folder + "ender"));

    // if you can get him to stay, wither is a source of free liquid soul
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.WITHER),
                                       new FluidStack(TinkerFluids.liquidSoul.get(), MaterialValues.GLASS_BLOCK / 20), 2)
                              .build(consumer, prefixR(EntityType.WITHER, folder));
  }

  private void addCompatRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "compat/";
    // create - cast andesite alloy
    ItemOutput andesiteAlloy = ItemNameOutput.fromName(new ResourceLocation("create", "andesite_alloy"));
    Consumer<IFinishedRecipe> createConsumer = withCondition(consumer, new ModLoadedCondition("create"));
    ItemCastingRecipeBuilder.basinRecipe(andesiteAlloy)
                            .setCast(Blocks.ANDESITE, true)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.NUGGET))
                            .build(createConsumer, location(folder + "create/andesite_alloy_iron"));
    ItemCastingRecipeBuilder.basinRecipe(andesiteAlloy)
                            .setCast(Blocks.ANDESITE, true)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenZinc.get(), MaterialValues.NUGGET))
                            .build(createConsumer, location(folder + "create/andesite_alloy_zinc"));

    // immersive engineering - casting treated wood
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(new ResourceLocation("immersiveengineering", "treated_wood_horizontal")))
                            .setCast(ItemTags.PLANKS, true)
                            .setFluid(FluidTags.makeWrapperTag("forge:creosote"), 125)
                            .setCoolingTime(100)
                            .build(withCondition(consumer, new ModLoadedCondition("immersiveengineering")), location(folder + "immersiveengineering/treated_wood"));

    // ceramics compat: a lot of melting and some casting
    String ceramics = "ceramics";
    String ceramicsFolder = folder + ceramics + "/";
    Function<String,ResourceLocation> ceramicsId = name -> new ResourceLocation(ceramics, name);
    Consumer<IFinishedRecipe> ceramicsConsumer = withCondition(consumer, new ModLoadedCondition(ceramics));

    // fill clay and cracked clay buckets
    ContainerFillingRecipeBuilder.tableRecipe(ceramicsId.apply("clay_bucket"), FluidAttributes.BUCKET_VOLUME)
                                 .build(ceramicsConsumer, location(ceramicsFolder + "filling_clay_bucket"));
    ContainerFillingRecipeBuilder.tableRecipe(ceramicsId.apply("cracked_clay_bucket"), FluidAttributes.BUCKET_VOLUME)
                                 .build(ceramicsConsumer, location(ceramicsFolder + "filling_cracked_clay_bucket"));

    // porcelain for ceramics
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenPorcelain.get(), MaterialValues.SLIMEBALL * 4)
                      .addInput(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 3)
                      .addInput(TinkerFluids.moltenQuartz.get(), MaterialValues.INGOT)
                      .build(ceramicsConsumer, location(ceramicsFolder + "alloy_porcelain"));

    // melting clay
    String clayFolder = ceramicsFolder + "clay/";

    // unfired clay
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_clay_plate")), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL), 0.5f)
                        .build(ceramicsConsumer, location(clayFolder + "clay_1"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_faucet"), ceramicsId.apply("clay_channel")), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 2), 0.65f)
                        .build(ceramicsConsumer, location(clayFolder + "clay_2"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_clay_bucket"), ceramicsId.apply("clay_cistern")), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 3), 0.9f)
                        .build(ceramicsConsumer, location(clayFolder + "clay_3"));

    // 2 bricks
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("dark_bricks_slab"), ceramicsId.apply("dragon_bricks_slab"),
      ceramicsId.apply("terracotta_faucet"), ceramicsId.apply("terracotta_channel")), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 2), 1.33f)
                        .build(ceramicsConsumer, location(clayFolder + "bricks_2"));
    // 3 bricks
    MeltingRecipeBuilder.melting(CompoundIngredient.from(
      Ingredient.fromTag(ItemTags.createOptional(ceramicsId.apply("terracotta_cisterns"))),
      NBTNameIngredient.from(ceramicsId.apply("clay_bucket")),
      NBTNameIngredient.from(ceramicsId.apply("cracked_clay_bucket"))),
                                 new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 3), 1.67f)
                        .build(ceramicsConsumer, location(clayFolder + "bricks_3"));
    // 4 bricks
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("dark_bricks"), ceramicsId.apply("dark_bricks_stairs"), ceramicsId.apply("dark_bricks_wall"),
      ceramicsId.apply("dragon_bricks"), ceramicsId.apply("dragon_bricks_stairs"), ceramicsId.apply("dragon_bricks_wall")
    ), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 4), 2.0f)
                        .build(ceramicsConsumer, location(clayFolder + "block"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("kiln")), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIME_CONGEALED * 3 + MaterialValues.SLIMEBALL * 5), 4.0f)
                        .build(ceramicsConsumer, location(clayFolder + "kiln"));
    // lava bricks, lava byproduct
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("lava_bricks_slab")), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 2), 1.33f)
                        .addByproduct(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 20))
                        .build(ceramicsConsumer, location(clayFolder + "lava_bricks_slab"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("lava_bricks"), ceramicsId.apply("lava_bricks_stairs"), ceramicsId.apply("lava_bricks_wall")
    ), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 4), 2f)
                        .addByproduct(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10))
                        .build(ceramicsConsumer, location(clayFolder + "lava_bricks_block"));
    // gauge, partially glass
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("terracotta_gauge")), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL), 1f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_PANE / 4))
                        .build(ceramicsConsumer, location(clayFolder + "gauge"));
    // clay armor
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_helmet")), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 5), 2.25f)
                        .setDamagable()
                        .build(ceramicsConsumer, location(clayFolder + "clay_helmet"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_chestplate")), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 8), 3f)
                        .setDamagable()
                        .build(ceramicsConsumer, location(clayFolder + "clay_chestplate"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_leggings")), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 7), 2.75f)
                        .setDamagable()
                        .build(ceramicsConsumer, location(clayFolder + "clay_leggings"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_boots")), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 4), 2f)
                        .setDamagable()
                        .build(ceramicsConsumer, location(clayFolder + "clay_boots"));

    // melting porcelain
    String porcelainFolder = ceramicsFolder + "porcelain/";
    // unfired
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_porcelain")), new FluidStack(TinkerFluids.moltenPorcelain.get(), MaterialValues.SLIMEBALL), 0.5f)
                        .build(ceramicsConsumer, location(porcelainFolder + "unfired_1"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_faucet"), ceramicsId.apply("unfired_channel")), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 2), 0.65f)
                        .build(ceramicsConsumer, location(porcelainFolder + "unfired_2"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_cistern")), new FluidStack(TinkerFluids.moltenClay.get(), MaterialValues.SLIMEBALL * 3), 0.9f)
                        .build(ceramicsConsumer, location(porcelainFolder + "unfired_3"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_porcelain_block")), new FluidStack(TinkerFluids.moltenPorcelain.get(), MaterialValues.SLIME_CONGEALED), 1f)
                        .build(ceramicsConsumer, location(porcelainFolder + "unfired_4"));

    // 1 brick
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("porcelain_brick")), new FluidStack(TinkerFluids.moltenPorcelain.get(), MaterialValues.SLIMEBALL), 1f)
                        .build(ceramicsConsumer, location(porcelainFolder + "bricks_1"));
    // 2 bricks
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("porcelain_bricks_slab"), ceramicsId.apply("monochrome_bricks_slab"), ceramicsId.apply("marine_bricks_slab"), ceramicsId.apply("rainbow_bricks_slab"),
      ceramicsId.apply("porcelain_faucet"), ceramicsId.apply("porcelain_channel")
    ), new FluidStack(TinkerFluids.moltenPorcelain.get(), MaterialValues.SLIMEBALL * 2), 1.33f)
                        .build(ceramicsConsumer, location(porcelainFolder + "bricks_2"));
    // 3 bricks
    MeltingRecipeBuilder.melting(Ingredient.fromTag(ItemTags.makeWrapperTag(ceramics + ":porcelain_cisterns")), new FluidStack(TinkerFluids.moltenPorcelain.get(), MaterialValues.SLIMEBALL * 3), 1.67f)
                        .build(ceramicsConsumer, location(porcelainFolder + "bricks_3"));
    // 4 bricks
    MeltingRecipeBuilder.melting(CompoundIngredient.from(
      Ingredient.fromTag(ItemTags.makeWrapperTag(ceramics + ":porcelain_block")),
      Ingredient.fromTag(ItemTags.makeWrapperTag(ceramics + ":rainbow_porcelain")),
      ItemNameIngredient.from(
        ceramicsId.apply("porcelain_bricks"), ceramicsId.apply("porcelain_bricks_stairs"), ceramicsId.apply("porcelain_bricks_wall"),
        ceramicsId.apply("monochrome_bricks"), ceramicsId.apply("monochrome_bricks_stairs"), ceramicsId.apply("monochrome_bricks_wall"),
        ceramicsId.apply("marine_bricks"), ceramicsId.apply("marine_bricks_stairs"), ceramicsId.apply("marine_bricks_wall"),
        ceramicsId.apply("rainbow_bricks"), ceramicsId.apply("rainbow_bricks_stairs"), ceramicsId.apply("rainbow_bricks_wall")
      )), new FluidStack(TinkerFluids.moltenPorcelain.get(), MaterialValues.SLIMEBALL * 4), 2.0f)
                        .build(ceramicsConsumer, location(porcelainFolder + "blocks"));
    // gold bricks, gold byproduct
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("golden_bricks_slab")), new FluidStack(TinkerFluids.moltenPorcelain.get(), MaterialValues.SLIMEBALL * 2), 1.33f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.NUGGET / 16)) // yep, exactly 1mb, such recycling
                        .build(ceramicsConsumer, location(porcelainFolder + "golden_bricks_slab"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("golden_bricks"), ceramicsId.apply("golden_bricks_stairs"), ceramicsId.apply("golden_bricks_wall")
    ), new FluidStack(TinkerFluids.moltenPorcelain.get(), MaterialValues.SLIMEBALL * 4), 2f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.NUGGET / 8)) // 2mb is slightly better, but still not great
                        .build(ceramicsConsumer, location(porcelainFolder + "golden_bricks_block"));
    // gauge, partially glass
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("porcelain_gauge")), new FluidStack(TinkerFluids.moltenPorcelain.get(), MaterialValues.SLIMEBALL), 1f)
                        .addByproduct(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.GLASS_PANE / 4))
                        .build(ceramicsConsumer, location(porcelainFolder + "gauge"));

    // casting bricks
    String castingFolder = ceramicsFolder + "casting/";
    addCastingWithCastRecipe(ceramicsConsumer, TinkerFluids.moltenPorcelain, MaterialValues.SLIMEBALL, TinkerSmeltery.ingotCast, ItemNameOutput.fromName(ceramicsId.apply("porcelain_brick")), castingFolder + "porcelain_brick");
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("white_porcelain")))
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenPorcelain.get(), MaterialValues.SLIME_CONGEALED))
                            .build(ceramicsConsumer, location(castingFolder + "porcelain"));
    // lava bricks
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("lava_bricks")))
                            .setCast(Blocks.BRICKS, true)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10))
                            .build(ceramicsConsumer, location(castingFolder + "lava_bricks"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("lava_bricks_slab")))
                            .setCast(Blocks.BRICK_SLAB, true)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 20))
                            .build(ceramicsConsumer, location(castingFolder + "lava_bricks_slab"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("lava_bricks_stairs")))
                            .setCast(Blocks.BRICK_STAIRS, true)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10))
                            .build(ceramicsConsumer, location(castingFolder + "lava_bricks_stairs"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("lava_bricks_wall")))
                            .setCast(Blocks.BRICK_WALL, true)
                            .setFluidAndTime(new FluidStack(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 10))
                            .build(ceramicsConsumer, location(castingFolder + "lava_bricks_wall"));

    // golden bricks
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("golden_bricks")))
                            .setCast(ItemNameIngredient.from(ceramicsId.apply("porcelain_bricks")), true)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.NUGGET / 8))
                            .build(ceramicsConsumer, location(castingFolder + "golden_bricks"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("golden_bricks_slab")))
                            .setCast(ItemNameIngredient.from(ceramicsId.apply("porcelain_bricks_slab")), true)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.NUGGET / 16))
                            .build(ceramicsConsumer, location(castingFolder + "golden_bricks_slab"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("golden_bricks_stairs")))
                            .setCast(ItemNameIngredient.from(ceramicsId.apply("porcelain_bricks_stairs")), true)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.NUGGET / 8))
                            .build(ceramicsConsumer, location(castingFolder + "golden_bricks_stairs"));
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("golden_bricks_wall")))
                            .setCast(ItemNameIngredient.from(ceramicsId.apply("porcelain_bricks_wall")), true)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.NUGGET / 8))
                            .build(ceramicsConsumer, location(castingFolder + "golden_bricks_wall"));
  }


  /* Helpers */

  /**
   * Adds a stonecutting recipe with automatic name and criteria
   * @param consumer  Recipe consumer
   * @param output    Recipe output
   * @param folder    Recipe folder path
   */
  private void addSearedStonecutter(Consumer<IFinishedRecipe> consumer, IItemProvider output, String folder) {
    SingleItemRecipeBuilder.stonecuttingRecipe(CompoundIngredient.from(
      Ingredient.fromItems(TinkerSmeltery.searedStone),
      new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.SEARED_BRICKS), Ingredient.fromItems(output))), output, 1)
                           .addCriterion("has_stone", hasItem(TinkerSmeltery.searedStone))
                           .addCriterion("has_bricks", hasItem(TinkerTags.Items.SEARED_BRICKS))
                           .build(consumer, wrap(output, folder, "_stonecutting"));
  }

  /**
   * Adds a stonecutting recipe with automatic name and criteria
   * @param consumer  Recipe consumer
   * @param output    Recipe output
   * @param folder    Recipe folder path
   */
  private void addScorchedStonecutter(Consumer<IFinishedRecipe> consumer, IItemProvider output, String folder) {
    SingleItemRecipeBuilder.stonecuttingRecipe(new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.SCORCHED_BLOCKS), Ingredient.fromItems(output)), output, 1)
                           .addCriterion("has_block", hasItem(TinkerTags.Items.SCORCHED_BLOCKS))
                           .build(consumer, wrap(output, folder, "_stonecutting"));
  }

  /**
   * Base logic for {@link  #addMetalMelting(Consumer, Fluid, String, boolean, String, boolean, Byproduct...)}
   * @param consumer    Recipe consumer
   * @param fluid       Fluid to melt into
   * @param amount      Amount to melt into
   * @param tagName     Input tag
   * @param factor      Melting factor
   * @param recipePath  Recipe output name
   * @param isOptional  If true, recipe is optional
   */
  private static void addMetalBase(Consumer<IFinishedRecipe> consumer, Fluid fluid, int amount, String tagName, float factor, String recipePath, boolean isOptional) {
    Consumer<IFinishedRecipe> wrapped = isOptional ? withCondition(consumer, tagCondition(tagName)) : consumer;
    MeltingRecipeBuilder.melting(Ingredient.fromTag(getTag("forge", tagName)), fluid, amount, factor)
                        .build(wrapped, location(recipePath));
  }

  /**
   * Base logic for {@link  #addMetalMelting(Consumer, Fluid, String, boolean, String, boolean, Byproduct...)}
   * @param consumer    Recipe consumer
   * @param fluid       Fluid to melt into
   * @param amount      Amount to melt into
   * @param tagName     Input tag
   * @param factor      Melting factor
   * @param recipePath  Recipe output name
   * @param isOptional  If true, recipe is optional
   * @param byproducts  List of byproduct options for this metal, first one that is present will be used
   */
  private void addOreMelting(Consumer<IFinishedRecipe> consumer, Fluid fluid, int amount, String tagName, float factor, String recipePath, boolean isOptional, Byproduct... byproducts) {
    Consumer<IFinishedRecipe> wrapped = isOptional ? withCondition(consumer, tagCondition(tagName)) : consumer;
    Supplier<MeltingRecipeBuilder> supplier = () -> MeltingRecipeBuilder.melting(Ingredient.fromTag(getTag("forge", tagName)), fluid, amount, factor).setOre();
    ResourceLocation location = location(recipePath);

    // if no byproducts, just build directly
    if (byproducts.length == 0) {
      supplier.get().build(wrapped, location);
      // if first option is always present, only need that one
    } else if (byproducts[0].isAlwaysPresent()) {
      supplier.get()
              .addByproduct(new FluidStack(byproducts[0].getFluid(), byproducts[0].getNuggets()))
              .build(wrapped, location);
    } else {
      // multiple options, will need a conditonal recipe
      ConditionalRecipe.Builder builder = ConditionalRecipe.builder();
      boolean alwaysPresent = false;
      for (Byproduct byproduct : byproducts) {
        builder.addCondition(tagCondition("ingots/" + byproduct.getName()));
        builder.addRecipe(supplier.get().addByproduct(new FluidStack(byproduct.getFluid(), byproduct.getNuggets()))::build);
        // found an always present byproduct? we are done
        alwaysPresent = byproduct.isAlwaysPresent();
        if (alwaysPresent) {
          break;
        }
      }
      // not always present? add a recipe with no byproducts as a final fallback
      if (!alwaysPresent) {
        builder.addCondition(TrueCondition.INSTANCE);
        builder.addRecipe(supplier.get()::build);
      }
      builder.build(wrapped, location);
    }
  }

  /**
   * Adds a basic ingot, nugget, block, ore melting recipe set
   * @param consumer    Recipe consumer
   * @param fluid       Fluid result
   * @param name        Resource name for tags
   * @param hasOre      If true, adds recipe for melting the ore
   * @param folder      Recipe folder
   * @param isOptional  If true, this recipe is entirely optional
   * @param byproducts  List of byproduct options for this metal, first one that is present will be used
   */
  private void addMetalMelting(Consumer<IFinishedRecipe> consumer, Fluid fluid, String name, boolean hasOre, String folder, boolean isOptional, Byproduct... byproducts) {
    String prefix = folder + "/" + name + "/";
    addMetalBase(consumer, fluid, MaterialValues.METAL_BLOCK, "storage_blocks/" + name, 3.0f, prefix + "block", isOptional);
    addMetalBase(consumer, fluid, MaterialValues.INGOT, "ingots/" + name, 1.0f, prefix + "ingot", isOptional);
    addMetalBase(consumer, fluid, MaterialValues.NUGGET, "nuggets/" + name, 1 / 3f, prefix + "nugget", isOptional);
    if (hasOre) {
      addOreMelting(consumer, fluid, MaterialValues.INGOT, "ores/" + name, 1.5f, prefix + "ore", isOptional, byproducts);
    }
    // dust is always optional, as we don't do dust
    addMetalBase(consumer, fluid, MaterialValues.INGOT,      "dusts/" + name,       0.75f, prefix + "dust",       true);
    addMetalBase(consumer, fluid, MaterialValues.INGOT,      "plates/" + name,      1.0f,  prefix + "plates",     true);
    addMetalBase(consumer, fluid, MaterialValues.INGOT * 4,  "gears/" + name,       2.0f,  prefix + "gear",       true);
    addMetalBase(consumer, fluid, MaterialValues.NUGGET * 3, "coins/" + name,       2/3f,  prefix + "coin",       true);
    addMetalBase(consumer, fluid, MaterialValues.INGOT / 2,  "rods/" + name,        1/5f,  prefix + "rod",        true);
    addMetalBase(consumer, fluid, MaterialValues.INGOT,      "sheetmetals/" + name, 1.0f,  prefix + "sheetmetal", true);
  }


  /* Seared casting */

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
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenClay.get(), amount))
                            .setCast(cast, true)
                            .build(consumer, location(location));
  }


  /* Scorched casting */

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param location  Recipe location
   */
  private static void addScorchedCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, String location) {
    addScorchedCastingRecipe(consumer, block, cast, MaterialValues.SLIMEBALL * 2, location);
  }

  /**
   * Adds a recipe to create the given seared slab block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param location  Recipe location
   */
  private static void addScorchedSlabCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, String location) {
    addScorchedCastingRecipe(consumer, block, cast, MaterialValues.SLIMEBALL, location);
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param amount    Amount of fluid needed
   * @param location  Recipe location
   */
  private static void addScorchedCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider block, Ingredient cast, int amount, String location) {
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluidAndTime(new FluidStack(TinkerFluids.magma.get(), amount))
                            .setCast(cast, true)
                            .build(consumer, location(location));
  }


  /* Casting */

  /** Gets the temperature for a fluid in celsius */
  private int getTemperature(Supplier<? extends Fluid> supplier) {
    return supplier.get().getAttributes().getTemperature() - 300;
  }

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
                            .setFluidAndTime(new FluidStack(fluid.get(), amount))
                            .build(consumer, location(location));
  }

  /**
   * Adds a casting recipe for a block
   * @param consumer  Recipe consumer
   * @param fluid     Input fluid
   * @param amount    Input amount
   * @param block     Output block
   * @param location  Output name
   */
  private void addBlockCastingRecipe(Consumer<IFinishedRecipe> consumer, ITag<Fluid> fluid, int temperature, int amount, IItemProvider block, String location) {
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluidAndTime(temperature, fluid, amount)
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
  private void addCastingWithCastRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, int amount, CastItemObject cast, ItemOutput output, String location) {
    FluidStack fluidStack = new FluidStack(fluid.get(), amount);
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluidAndTime(fluidStack)
                            .setCast(cast.getMultiUseTag(), false)
                            .build(consumer, location(location + "_gold_cast"));
    ItemCastingRecipeBuilder.tableRecipe(output)
                            .setFluidAndTime(fluidStack)
                            .setCast(cast.getSingleUseTag(), true)
                            .build(consumer, location(location + "_sand_cast"));
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
    addCastingWithCastRecipe(consumer, fluid, amount, cast, ItemOutput.fromItem(output), location);
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
  private void addSlimeCastingRecipe(Consumer<IFinishedRecipe> consumer, ITag<Fluid> fluid, int temperature, SlimeType slimeType, String folder) {
    String colorFolder = folder + slimeType.getString() + "/";
    addBlockCastingRecipe(consumer, fluid, temperature, MaterialValues.SLIME_CONGEALED, TinkerWorld.congealedSlime.get(slimeType), colorFolder + "congealed");
    ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.slime.get(slimeType))
                            .setFluidAndTime(temperature, fluid, MaterialValues.SLIMEBLOCK - MaterialValues.SLIME_CONGEALED)
                            .setCast(TinkerWorld.congealedSlime.get(slimeType), true)
                            .build(consumer, location(colorFolder + "block"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.slimeball.get(slimeType))
                            .setFluidAndTime(temperature, fluid, MaterialValues.SLIMEBALL)
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
  private void addMetalCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, @Nullable IItemProvider block, @Nullable IItemProvider ingot, @Nullable IItemProvider nugget, String folder, String metal) {
    String metalFolder = folder + metal + "/";
    if (block != null) {
      addBlockCastingRecipe(consumer, fluid, MaterialValues.METAL_BLOCK, block, metalFolder + "block");
    }
    if (ingot != null) {
      addIngotCastingRecipe(consumer, fluid, ingot, metalFolder + "ingot");
    }
    if (nugget != null) {
      addNuggetCastingRecipe(consumer, fluid, nugget, metalFolder + "nugget");
    }
    // plates are always optional, we don't ship them
    addTagCastingWithCast(consumer, fluid.get(), MaterialValues.INGOT,      TinkerSmeltery.plateCast, "plates", metal, folder + metal + "/plate", true);
    addTagCastingWithCast(consumer, fluid.get(), MaterialValues.INGOT * 4,  TinkerSmeltery.gearCast,  "gears",  metal, folder + metal + "/gear", true);
    addTagCastingWithCast(consumer, fluid.get(), MaterialValues.NUGGET * 3, TinkerSmeltery.coinCast,  "coins",  metal, folder + metal + "/coin", true);
    addTagCastingWithCast(consumer, fluid.get(), MaterialValues.INGOT / 2,  TinkerSmeltery.rodCast,   "rods",   metal, folder + metal + "/rod", true);
  }

  /**
   * Add recipes for a standard mineral
   * @param consumer  Recipe consumer
   * @param fluid     Fluid input
   * @param metal     Metal object
   * @param folder    Output folder
   */
  private void addMetalCastingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends Fluid> fluid, MetalItemObject metal, String folder, String name) {
    addMetalCastingRecipe(consumer, fluid, metal.get(), metal.getIngot(), metal.getNugget(), folder, name);
  }


  /** Adds a recipe for casting using a cast */
  private void addTagCastingWithCast(Consumer<IFinishedRecipe> consumer, Fluid fluid, int amount, CastItemObject cast, String tagPrefix, String name, String recipeName, boolean optional) {
    String tagName = tagPrefix + "/" + name;
    if (optional) {
      consumer = withCondition(consumer, tagCondition(tagName));
    }
    ITag<Item> tag = getTag("forge", tagName);
    ItemCastingRecipeBuilder.tableRecipe(tag)
                            .setFluidAndTime(new FluidStack(fluid, amount))
                            .setCast(cast.getMultiUseTag(), false)
                            .build(consumer, location(recipeName + "_gold_cast"));
    ItemCastingRecipeBuilder.tableRecipe(tag)
                            .setFluidAndTime(new FluidStack(fluid, amount))
                            .setCast(cast.getSingleUseTag(), true)
                            .build(consumer, location(recipeName + "_sand_cast"));
  }

  /**
   * Add recipes for a standard mineral
   * @param consumer       Recipe consumer
   * @param fluid          Fluid input
   * @param name           Name of ore
   * @param folder         Output folder
   * @param forceStandard  If true, all default materials will always get a recipe, used for common materials provided by the mod (e.g. copper)
   */
  private void addMetalTagCasting(Consumer<IFinishedRecipe> consumer, Fluid fluid, String name, String folder, boolean forceStandard) {
    // nugget and ingot
    addTagCastingWithCast(consumer, fluid, MaterialValues.NUGGET,     TinkerSmeltery.nuggetCast, "nuggets", name, folder + name + "/nugget", !forceStandard);
    addTagCastingWithCast(consumer, fluid, MaterialValues.INGOT,      TinkerSmeltery.ingotCast,  "ingots",  name, folder + name + "/ingot", !forceStandard);
    addTagCastingWithCast(consumer, fluid, MaterialValues.INGOT,      TinkerSmeltery.plateCast,  "plates",  name, folder + name + "/plate", true);
    addTagCastingWithCast(consumer, fluid, MaterialValues.INGOT * 4,  TinkerSmeltery.gearCast,   "gears",   name, folder + name + "/gear", true);
    addTagCastingWithCast(consumer, fluid, MaterialValues.NUGGET * 3, TinkerSmeltery.coinCast,   "coins",   name, folder + name + "/coin", true);
    addTagCastingWithCast(consumer, fluid, MaterialValues.INGOT / 2,  TinkerSmeltery.rodCast,    "rods",    name, folder + name + "/rod", true);
    // block
    ITag<Item> block = getTag("forge", "storage_blocks/" + name);
    Consumer<IFinishedRecipe> wrapped = forceStandard ? consumer : withCondition(consumer, tagCondition("storage_blocks/" + name));
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluidAndTime(new FluidStack(fluid, MaterialValues.METAL_BLOCK))
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
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.INGOT))
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

  /** Adds recipes to melt and cast a material */
  private void addMaterialMeltingCasting(Consumer<IFinishedRecipe> consumer, MaterialId material, FluidObject<?> fluid, int fluidAmount, String folder) {
    MaterialFluidRecipeBuilder.material(material)
                              .setFluid(fluid.getLocalTag(), fluidAmount)
                              .setTemperature(getTemperature(fluid))
                              .build(consumer, location(folder + "casting/" + material.getPath()));
    MaterialMeltingRecipeBuilder.material(material, new FluidStack(fluid.get(), fluidAmount))
                                .build(consumer, location(folder + "melting/" + material.getPath()));
  }

  /** Adds recipes to melt and cast a material of ingot size */
  private void addMaterialMeltingCasting(Consumer<IFinishedRecipe> consumer, MaterialId material, FluidObject<?> fluid, String folder) {
    addMaterialMeltingCasting(consumer, material, fluid, MaterialValues.INGOT, folder);
  }

  /** Adds recipes to melt and cast a material of ingot size */
  private void addCompositeMaterialRecipe(Consumer<IFinishedRecipe> consumer, MaterialId input, MaterialId output, FluidObject<?> fluid, int amount, boolean forgeTag, String folder) {
    MaterialFluidRecipeBuilder.material(output)
                              .setInputId(input)
                              .setFluid(forgeTag ? fluid.getForgeTag() : fluid.getLocalTag(), amount)
                              .setTemperature(getTemperature(fluid))
                              .build(consumer, location(folder + "composite/" + output.getPath()));
  }
}
