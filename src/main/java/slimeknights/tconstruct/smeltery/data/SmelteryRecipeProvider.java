package slimeknights.tconstruct.smeltery.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ItemExistsCondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipeBuilder;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.mantle.recipe.data.ICommonRecipeHelper;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.recipe.data.ItemNameOutput;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.recipe.ingredient.PotionDisplayIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.common.registration.GeodeItemObject;
import slimeknights.tconstruct.common.registration.GeodeItemObject.BudSize;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.fluids.fluids.PotionFluidType;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.data.recipe.ISmelteryRecipeHelper;
import slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder;
import slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder.CommonRecipe;
import slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder.MetalMelting;
import slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder.ToolItemMelting;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.PotionCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.container.ContainerFillingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.BlockTagIngredient;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.ingredient.NoContainerIngredient;
import slimeknights.tconstruct.library.recipe.melting.IMeltingContainer.OreRateType;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.FoliageType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static slimeknights.mantle.Mantle.COMMON;
import static slimeknights.mantle.Mantle.commonResource;
import static slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder.ARMOR;
import static slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder.ARMOR_PLUS;
import static slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder.AXES;
import static slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder.BOOTS;
import static slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder.CHESTPLATE;
import static slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder.HELMET;
import static slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder.LEGGINGS_PLUS;
import static slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder.SHOVEL_PLUS;
import static slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder.SWORD;
import static slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder.TOOLS;

@SuppressWarnings("removal")
public class SmelteryRecipeProvider extends BaseRecipeProvider implements ISmelteryRecipeHelper, ICommonRecipeHelper {
  public SmelteryRecipeProvider(PackOutput packOutput) {
    super(packOutput);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Smeltery Recipes";
  }

  @Override
  protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
    this.addCraftingRecipes(consumer);
    this.addSmelteryRecipes(consumer);
    this.addFoundryRecipes(consumer);
    this.addTagRecipes(consumer);
    this.addMeltingRecipes(consumer);
    this.addCastingRecipes(consumer);
    this.addAlloyRecipes(consumer);
    this.addEntityMeltingRecipes(consumer);

    this.addCompatRecipes(consumer);
  }

  private void addCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, TinkerSmeltery.copperCan, 3)
                       .define('c', Tags.Items.INGOTS_COPPER)
                       .pattern("c c")
                       .pattern(" c ")
                       .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                       .save(consumer, prefix(TinkerSmeltery.copperCan, "smeltery/"));

    // sand casts
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TinkerSmeltery.blankSandCast, 4)
                          .requires(Tags.Items.SAND_COLORLESS)
                          .unlockedBy("has_casting", has(TinkerSmeltery.searedTable))
                          .save(consumer, location("smeltery/sand_cast"));
    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TinkerSmeltery.blankRedSandCast, 4)
                          .requires(Tags.Items.SAND_RED)
                          .unlockedBy("has_casting", has(TinkerSmeltery.searedTable))
                          .save(consumer, location("smeltery/red_sand_cast"));

    // pick up sand casts from the table
    MoldingRecipeBuilder.moldingTable(TinkerSmeltery.blankSandCast)
                        .setMaterial(TinkerTags.Items.SAND_CASTS)
                        .save(consumer, location("smeltery/sand_cast_pickup"));
    MoldingRecipeBuilder.moldingTable(TinkerSmeltery.blankRedSandCast)
                        .setMaterial(TinkerTags.Items.RED_SAND_CASTS)
                        .save(consumer, location("smeltery/red_sand_cast_pickup"));
  }

  private void addSmelteryRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "smeltery/seared/";
    // grout crafting
    ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.grout, 2)
                          .requires(Items.CLAY_BALL)
                          .requires(ItemTags.SAND)
                          .requires(Blocks.GRAVEL)
                          .unlockedBy("has_item", has(Items.CLAY_BALL))
                          .save(consumer, prefix(id(TinkerSmeltery.grout), folder));
    ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.grout, 8)
                          .requires(Blocks.CLAY)
                          .requires(ItemTags.SAND).requires(ItemTags.SAND).requires(ItemTags.SAND).requires(ItemTags.SAND)
                          .requires(Blocks.GRAVEL).requires(Blocks.GRAVEL).requires(Blocks.GRAVEL).requires(Blocks.GRAVEL)
                          .unlockedBy("has_item", has(Blocks.CLAY))
                          .save(consumer, wrap(TinkerSmeltery.grout, folder, "_multiple"));

    // seared bricks from grout
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(TinkerSmeltery.grout), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedBrick, 0.3f, 200)
                        .unlockedBy("has_item", has(TinkerSmeltery.grout))
                        .save(consumer, prefix(TinkerSmeltery.searedBrick, folder));
    Consumer<Consumer<FinishedRecipe>> fastGrout = c ->
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(TinkerSmeltery.grout), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedBrick, 0.3f, 100)
                          .unlockedBy("has_item", has(TinkerSmeltery.grout)).save(c);
    ConditionalRecipe.builder()
                     .addCondition(new ModLoadedCondition("ceramics"))
                     .addRecipe(c -> fastGrout.accept(ConsumerWrapperBuilder.wrap(new ResourceLocation("ceramics", "kiln")).build(c)))
                     .addCondition(TrueCondition.INSTANCE)
                     .addRecipe(fastGrout)
                     .generateAdvancement()
                     .build(consumer, wrap(TinkerSmeltery.searedBrick, folder, "_kiln"));


    // block from bricks
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedBricks)
                       .define('b', TinkerSmeltery.searedBrick)
                       .pattern("bb")
                       .pattern("bb")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, wrap(TinkerSmeltery.searedBricks, folder, "_from_brick"));
    // ladder from bricks
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedLadder, 4)
                       .define('b', TinkerSmeltery.searedBrick)
                       .define('B', TinkerTags.Items.SEARED_BRICKS)
                       .pattern("b b")
                       .pattern("b b")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, prefix(TinkerSmeltery.searedLadder, folder));

    // cobble -> stone
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(TinkerSmeltery.searedCobble.get()), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedStone, 0.1f, 200)
                        .unlockedBy("has_item", has(TinkerSmeltery.searedCobble.get()))
                        .save(consumer, wrap(TinkerSmeltery.searedStone, folder, "_smelting"));
    // stone -> paver
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(TinkerSmeltery.searedStone.get()), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedPaver, 0.1f, 200)
                        .unlockedBy("has_item", has(TinkerSmeltery.searedStone.get()))
                        .save(consumer, wrap(TinkerSmeltery.searedPaver, folder, "_smelting"));
    // stone -> bricks
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedBricks, 4)
                       .define('b', TinkerSmeltery.searedStone)
                       .pattern("bb")
                       .pattern("bb")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedStone))
                       .save(consumer, wrap(TinkerSmeltery.searedBricks, folder, "_crafting"));
    // bricks -> cracked
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(TinkerSmeltery.searedBricks), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedCrackedBricks, 0.1f, 200)
                        .unlockedBy("has_item", has(TinkerSmeltery.searedBricks))
                        .save(consumer, wrap(TinkerSmeltery.searedCrackedBricks, folder, "_smelting"));
    // brick slabs -> fancy
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedFancyBricks)
                       .define('s', TinkerSmeltery.searedBricks.getSlab())
                       .pattern("s")
                       .pattern("s")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBricks.getSlab()))
                       .save(consumer, wrap(TinkerSmeltery.searedFancyBricks, folder, "_crafting"));
    // bricks or stone as input
    this.searedStonecutter(consumer, TinkerSmeltery.searedBricks, folder);
    this.searedStonecutter(consumer, TinkerSmeltery.searedFancyBricks, folder);
    this.searedStonecutter(consumer, TinkerSmeltery.searedTriangleBricks, folder);

    // seared glass
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedGlass)
                       .define('b', TinkerSmeltery.searedBrick)
                       .define('G', Tags.Items.GLASS_COLORLESS)
                       .pattern(" b ")
                       .pattern("bGb")
                       .pattern(" b ")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, prefix(TinkerSmeltery.searedGlass, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedLamp)
      .define('b', TinkerSmeltery.searedBrick)
      .define('G', Blocks.GLOWSTONE)
      .pattern(" b ")
      .pattern("bGb")
      .pattern(" b ")
      .unlockedBy("has_item", has(Blocks.GLOWSTONE))
      .save(consumer, prefix(TinkerSmeltery.searedLamp, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedGlassPane, 16)
                       .define('#', TinkerSmeltery.searedGlass)
                       .pattern("###")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedGlass))
                       .save(consumer, prefix(TinkerSmeltery.searedGlassPane, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedSoulGlass)
                       .define('b', TinkerSmeltery.searedBrick)
                       .define('G', TinkerCommons.soulGlass)
                       .pattern(" b ")
                       .pattern("bGb")
                       .pattern(" b ")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, prefix(TinkerSmeltery.searedSoulGlass, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedSoulGlassPane, 16)
                       .define('#', TinkerSmeltery.searedSoulGlass)
                       .pattern("###")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedSoulGlass))
                       .save(consumer, prefix(TinkerSmeltery.searedSoulGlassPane, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.searedTintedGlass)
                       .define('b', TinkerSmeltery.searedBrick)
                       .define('G', Tags.Items.GLASS_TINTED)
                       .pattern(" b ")
                       .pattern("bGb")
                       .pattern(" b ")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, prefix(TinkerSmeltery.searedTintedGlass, folder));

    // stairs and slabs
    this.slabStairsCrafting(consumer, TinkerSmeltery.searedStone, folder, true);
    this.stairSlabWallCrafting(consumer, TinkerSmeltery.searedCobble, folder, true);
    this.slabStairsCrafting(consumer, TinkerSmeltery.searedPaver, folder, true);
    this.stairSlabWallCrafting(consumer, TinkerSmeltery.searedBricks, folder, true);

    // tanks
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedTank.get(TankType.FUEL_TANK))
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('B', Tags.Items.GLASS)
                       .pattern("###")
                       .pattern("#B#")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "fuel_tank"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE))
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('B', Tags.Items.GLASS)
                       .pattern("#B#")
                       .pattern("BBB")
                       .pattern("#B#")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "fuel_gauge"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedTank.get(TankType.INGOT_TANK))
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('B', Tags.Items.GLASS)
                       .pattern("#B#")
                       .pattern("#B#")
                       .pattern("#B#")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "ingot_tank"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE))
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('B', Tags.Items.GLASS)
                       .pattern("B#B")
                       .pattern("#B#")
                       .pattern("B#B")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "ingot_gauge"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedLantern.get(), 3)
                       .define('C', Tags.Items.INGOTS_IRON)
                       .define('B', TinkerSmeltery.searedBrick)
                       .define('P', TinkerSmeltery.searedGlassPane)
                       .pattern(" C ")
                       .pattern("PPP")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "lantern"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedCastingTank.get())
                       .define('B', TinkerSmeltery.searedBrick)
                       .define('G', Tags.Items.GLASS)
                       .define('C', Tags.Items.INGOTS_COPPER)
                       .pattern("BGB")
                       .pattern("CGC")
                       .pattern("BGB")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "seared_casting_tank"));

    // fluid transfer
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedFaucet.get(), 3)
                       .define('#', TinkerSmeltery.searedBrick)
                       .pattern("# #")
                       .pattern(" # ")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "faucet"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedChannel.get(), 5)
                       .define('#', TinkerSmeltery.searedBrick)
                       .pattern("# #")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "channel"));

    // casting
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedBasin.get())
                       .define('#', TinkerSmeltery.searedBrick)
                       .pattern("# #")
                       .pattern("# #")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "basin"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedTable.get())
                       .define('#', TinkerSmeltery.searedBrick)
                       .pattern("###")
                       .pattern("# #")
                       .pattern("# #")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "table"));

    // peripherals
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.copperGauge, 4)
      .define('G', Tags.Items.GLASS_PANES_COLORLESS)
      .define('C', Tags.Items.INGOTS_COPPER)
      .pattern(" C ")
      .pattern("CGC")
      .pattern(" C ")
      .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
      .save(consumer, location(folder + "gauge"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedDrain)
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('C', Tags.Items.INGOTS_COPPER)
                       .pattern("# #")
                       .pattern("C C")
                       .pattern("# #")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "drain"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedDrain)
                         .define('#', TinkerTags.Items.SMELTERY_BRICKS)
                         .define('C', Tags.Items.INGOTS_COPPER)
                         .pattern("C#C")
                         .unlockedBy("has_item", has(TinkerTags.Items.SMELTERY_BRICKS)))
                                 .setSource(TinkerTags.Items.SMELTERY_BRICKS)
                                 .build(consumer, location(folder + "drain_retextured"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedChute)
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('C', Tags.Items.INGOTS_COPPER)
                       .pattern("#C#")
                       .pattern("   ")
                       .pattern("#C#")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "chute"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedChute)
                         .define('#', TinkerTags.Items.SMELTERY_BRICKS)
                         .define('C', Tags.Items.INGOTS_COPPER)
                         .pattern("C")
                         .pattern("#")
                         .pattern("C")
                         .unlockedBy("has_item", has(TinkerTags.Items.SMELTERY_BRICKS)))
                                 .setSource(TinkerTags.Items.SMELTERY_BRICKS)
                                 .build(consumer, location(folder + "chute_retextured"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedDuct)
                       .define('#', TinkerSmeltery.searedBrick)
                       .define('C', Tags.Items.INGOTS_GOLD)
                       .pattern("# #")
                       .pattern("C C")
                       .pattern("# #")
                       .unlockedBy("has_item", has(Tags.Items.INGOTS_GOLD))
                       .save(consumer, location(folder + "duct"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedDuct)
                         .define('#', TinkerTags.Items.SMELTERY_BRICKS)
                         .define('C', Tags.Items.INGOTS_GOLD)
                         .pattern("C#C")
                         .unlockedBy("has_item", has(TinkerTags.Items.SMELTERY_BRICKS)))
                                 .setSource(TinkerTags.Items.SMELTERY_BRICKS)
                                 .build(consumer, location(folder + "duct_retextured"));

    // controllers
    Ingredient similarTanks = NoContainerIngredient.of(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE), TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedMelter)
                       .define('G', similarTanks)
                       .define('B', TinkerSmeltery.searedBrick)
                       .pattern("BGB")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "melter"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.searedHeater)
                       .define('B', TinkerSmeltery.searedBrick)
                       .pattern("BBB")
                       .pattern("B B")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.searedBrick))
                       .save(consumer, location(folder + "heater"));
    // fluid cannon
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.searedFluidCannon)
                       .define('T', similarTanks)
                       .define('C', Tags.Items.INGOTS_COPPER)
                       .pattern("CTC")
                       .pattern("CCC")
                       .unlockedBy("has_item", has(Tags.Items.INGOTS_COPPER))
                       .save(consumer, location(folder + "fluid_cannon"));

    // casting
    String castingFolder = "smeltery/casting/seared/";

    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedStone)
                            .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK_BLOCK)
                            .save(consumer, location(castingFolder + "stone/block_from_seared"));
    this.ingotCasting(consumer, TinkerFluids.searedStone, FluidValues.BRICK, TinkerSmeltery.searedBrick, castingFolder + "brick");
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedGlass)
                            .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK_BLOCK)
                            .setCast(Tags.Items.GLASS_COLORLESS, true)
                            .save(consumer, location(castingFolder + "glass"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedLamp)
      .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK_BLOCK)
      .setCast(Blocks.GLOWSTONE, true)
      .save(consumer, location(castingFolder + "lamp"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedSoulGlass)
                            .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK_BLOCK)
                            .setCast(TinkerCommons.soulGlass, true)
                            .save(consumer, location(castingFolder + "glass_soul"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTintedGlass)
                            .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK_BLOCK)
                            .setCast(Tags.Items.GLASS_TINTED, true)
                            .save(consumer, location(castingFolder + "glass_tinted"));
    // discount for casting panes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.searedGlassPane)
                            .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK)
                            .setCast(Tags.Items.GLASS_PANES_COLORLESS, true)
                            .save(consumer, location(castingFolder + "glass_pane"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.searedSoulGlassPane)
                            .setFluidAndTime(TinkerFluids.searedStone, FluidValues.BRICK)
                            .setCast(TinkerCommons.soulGlassPane, true)
                            .save(consumer, location(castingFolder + "glass_pane_soul"));

    // smeltery controller
    ItemCastingRecipeBuilder.retexturedBasinRecipe(ItemOutput.fromItem(TinkerSmeltery.smelteryController))
                            .setCast(TinkerTags.Items.SMELTERY_BRICKS, true)
                            .setFluidAndTime(TinkerFluids.moltenCopper, FluidValues.INGOT * 4)
                            .save(consumer, prefix(TinkerSmeltery.smelteryController, castingFolder));

    // craft seared stone from clay and stone
    // button is the closest we have to a single stone brick, just go with it, better than not having the recipe
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.searedBrick)
                            .setFluidAndTime(TinkerFluids.moltenClay, FluidValues.BRICK / 2)
                            .setCast(Items.FLINT, true) // if gravel works, flint makes sense to use
                            .save(consumer, location(castingFolder + "brick_composite"));
    // cobble
    searedCasting(consumer, TinkerSmeltery.searedCobble, CompoundIngredient.of(Ingredient.of(Tags.Items.COBBLESTONE), Ingredient.of(Blocks.GRAVEL)), castingFolder + "cobble/block");
    searedSlabCasting(consumer, TinkerSmeltery.searedCobble.getSlab(), Ingredient.of(Blocks.COBBLESTONE_SLAB), castingFolder + "cobble/slab");
    searedCasting(consumer, TinkerSmeltery.searedCobble.getStairs(), Ingredient.of(Blocks.COBBLESTONE_STAIRS), castingFolder + "cobble/stairs");
    searedCasting(consumer, TinkerSmeltery.searedCobble.getWall(), Ingredient.of(Blocks.COBBLESTONE_WALL), castingFolder + "cobble/wall");
    // stone
    searedCasting(consumer, TinkerSmeltery.searedStone, Ingredient.of(Tags.Items.STONE), castingFolder + "stone/block_from_clay");
    searedSlabCasting(consumer, TinkerSmeltery.searedStone.getSlab(), Ingredient.of(Blocks.STONE_SLAB), castingFolder + "stone/slab");
    searedCasting(consumer, TinkerSmeltery.searedStone.getStairs(), Ingredient.of(Blocks.STONE_STAIRS), castingFolder + "stone/stairs");
    // stone bricks
    searedCasting(consumer, TinkerSmeltery.searedBricks, Ingredient.of(Blocks.STONE_BRICKS), castingFolder + "bricks/block");
    searedSlabCasting(consumer, TinkerSmeltery.searedBricks.getSlab(), Ingredient.of(Blocks.STONE_BRICK_SLAB), castingFolder + "bricks/slab");
    searedCasting(consumer, TinkerSmeltery.searedBricks.getStairs(), Ingredient.of(Blocks.STONE_BRICK_STAIRS), castingFolder + "bricks/stairs");
    searedCasting(consumer, TinkerSmeltery.searedBricks.getWall(), Ingredient.of(Blocks.STONE_BRICK_WALL), castingFolder + "bricks/wall");
    // other seared
    searedCasting(consumer, TinkerSmeltery.searedCrackedBricks, Ingredient.of(Blocks.CRACKED_STONE_BRICKS), castingFolder + "cracked");
    searedCasting(consumer, TinkerSmeltery.searedFancyBricks, Ingredient.of(Blocks.CHISELED_STONE_BRICKS), castingFolder + "chiseled");
    searedCasting(consumer, TinkerSmeltery.searedPaver, Ingredient.of(Blocks.SMOOTH_STONE), castingFolder + "paver");

    // seared blocks
    String meltingFolder = "smeltery/melting/seared/";

    // double efficiency when using smeltery for grout
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.grout), TinkerFluids.searedStone, FluidValues.BRICK * 2, 1.5f)
                        .save(consumer, location(meltingFolder + "grout"));
    // seared stone
    // stairs are here since the cheapest stair recipe is stone cutter, 1 to 1
    MeltingRecipeBuilder.melting(CompoundIngredient.of(Ingredient.of(TinkerTags.Items.SEARED_BLOCKS),
                                                       Ingredient.of(TinkerSmeltery.searedLadder, TinkerSmeltery.searedCobble.getWall(), TinkerSmeltery.searedBricks.getWall(),
                                                                     TinkerSmeltery.searedCobble.getStairs(), TinkerSmeltery.searedStone.getStairs(), TinkerSmeltery.searedBricks.getStairs(), TinkerSmeltery.searedPaver.getStairs())),
																 TinkerFluids.searedStone, FluidValues.BRICK_BLOCK, 2.0f)
                        .save(consumer, location(meltingFolder + "block"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedCobble.getSlab(), TinkerSmeltery.searedStone.getSlab(), TinkerSmeltery.searedBricks.getSlab(), TinkerSmeltery.searedPaver.getSlab()),
																 TinkerFluids.searedStone, FluidValues.BRICK_BLOCK / 2, 1.5f)
                        .save(consumer, location(meltingFolder + "slab"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedBrick), TinkerFluids.searedStone, FluidValues.BRICK, 1.0f)
                        .save(consumer, location(meltingFolder + "brick"));

    // melt down smeltery components
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedFaucet, TinkerSmeltery.searedChannel), TinkerFluids.searedStone, FluidValues.BRICK, 1.5f)
                        .save(consumer, location(meltingFolder + "faucet"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedBasin, TinkerSmeltery.searedTable), TinkerFluids.searedStone, FluidValues.BRICK * 7, 2.5f)
                        .save(consumer, location(meltingFolder + "casting"));
    // tanks
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.searedTank.get(TankType.FUEL_TANK)), TinkerFluids.searedStone, FluidValues.BRICK * 8, 3f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK))
                        .save(consumer, location(meltingFolder + "fuel_tank"));
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.searedTank.get(TankType.INGOT_TANK)), TinkerFluids.searedStone, FluidValues.BRICK * 6, 2.5f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK * 3))
                        .save(consumer, location(meltingFolder + "ingot_tank"));
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE), TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE)), TinkerFluids.searedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK * 5))
                        .save(consumer, location(meltingFolder + "gauge"));
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.searedLantern), TinkerFluids.searedStone, FluidValues.BRICK * 2, 1.0f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_PANE))
                        .addByproduct(TinkerFluids.moltenIron.result(FluidValues.INGOT / 3))
                        .save(consumer, location(meltingFolder + "lantern"));
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.searedCastingTank), TinkerFluids.moltenCopper, FluidValues.INGOT * 2, 2.5f)
                        .addByproduct(TinkerFluids.searedStone.result(FluidValues.BRICK * 4))
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK * 3))
                        .save(consumer, location(meltingFolder + "seared_casting_tank"));
    // glass
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedGlass), TinkerFluids.searedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.moltenGlass.result( FluidValues.GLASS_BLOCK))
                        .save(consumer, location(meltingFolder + "glass"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedSoulGlass), TinkerFluids.searedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.liquidSoul.result( FluidValues.GLASS_BLOCK))
                        .save(consumer, location(meltingFolder + "glass_soul"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedTintedGlass), TinkerFluids.searedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK))
                        .addByproduct(TinkerFluids.moltenAmethyst.result(FluidValues.GEM * 2))
                        .save(consumer, location(meltingFolder + "glass_tinted"));
    // panes
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedGlassPane), TinkerFluids.searedStone, FluidValues.BRICK, 1.0f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_PANE))
                        .save(consumer, location(meltingFolder + "pane"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedSoulGlassPane), TinkerFluids.searedStone, FluidValues.BRICK, 1.0f)
                        .addByproduct(TinkerFluids.liquidSoul.result(FluidValues.GLASS_PANE))
                        .save(consumer, location(meltingFolder + "pane_soul"));
    // controllers
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedMelter), TinkerFluids.searedStone, FluidValues.BRICK * 9, 3.5f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_PANE * 5))
                        .save(consumer, location(meltingFolder + "melter"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedHeater), TinkerFluids.searedStone, FluidValues.BRICK * 8, 3f)
                        .save(consumer, location(meltingFolder + "heater"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedFluidCannon), TinkerFluids.moltenCopper, FluidValues.INGOT * 5, 2.5f)
                        .addByproduct(TinkerFluids.searedStone.result(FluidValues.BRICK * 4))
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_PANE * 5))
                        .save(consumer, location(meltingFolder + "fluid_cannon"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.smelteryController), TinkerFluids.moltenCopper, FluidValues.INGOT * 4, 3.5f)
                        .addByproduct(TinkerFluids.searedStone.result(FluidValues.BRICK * 4))
                        .save(consumer, location("smeltery/melting/metal/copper/smeltery_controller"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.copperGauge), TinkerFluids.moltenCopper, FluidValues.INGOT, 1f)
      .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_PANE / 5))
      .save(consumer, location("smeltery/melting/metal/copper/gauge"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedDrain, TinkerSmeltery.searedChute), TinkerFluids.moltenCopper, FluidValues.INGOT * 2, 2.5f)
                        .addByproduct(TinkerFluids.searedStone.result(FluidValues.BRICK * 4))
                        .save(consumer, location("smeltery/melting/metal/copper/smeltery_io"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.searedDuct), TinkerFluids.moltenGold, FluidValues.INGOT * 2, 2.5f)
                        .addByproduct(TinkerFluids.searedStone.result(FluidValues.BRICK * 4))
                        .save(consumer, location("smeltery/melting/metal/cobalt/seared_duct"));
  }

  private void addFoundryRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "smeltery/scorched/";
    // grout crafting
    Ingredient soulSand = Ingredient.of(Blocks.SOUL_SAND, Blocks.SOUL_SOIL);
    ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.netherGrout, 2)
                          .requires(Items.MAGMA_CREAM)
                          .requires(soulSand)
                          .requires(Blocks.GRAVEL)
                          .unlockedBy("has_item", has(Items.MAGMA_CREAM))
                          .save(consumer, prefix(TinkerSmeltery.netherGrout, folder));
    ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.netherGrout, 8)
                          .requires(Blocks.MAGMA_BLOCK)
                          .requires(soulSand).requires(soulSand).requires(soulSand).requires(soulSand)
                          .requires(Blocks.GRAVEL).requires(Blocks.GRAVEL).requires(Blocks.GRAVEL).requires(Blocks.GRAVEL)
                          .unlockedBy("has_item", has(Items.MAGMA_CREAM))
                          .save(consumer, wrap(TinkerSmeltery.netherGrout, folder, "_multiple"));

    // scorched bricks from grout
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(TinkerSmeltery.netherGrout), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedBrick, 0.3f, 200)
                              .unlockedBy("has_item", has(TinkerSmeltery.netherGrout))
                              .save(consumer, prefix(TinkerSmeltery.scorchedBrick, folder));
    Consumer<Consumer<FinishedRecipe>> fastGrout = c ->
      SimpleCookingRecipeBuilder.blasting(Ingredient.of(TinkerSmeltery.netherGrout), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedBrick, 0.3f, 100)
                                .unlockedBy("has_item", has(TinkerSmeltery.netherGrout)).save(c);
    ConditionalRecipe.builder()
                     .addCondition(new ModLoadedCondition("ceramics"))
                     .addRecipe(c -> fastGrout.accept(ConsumerWrapperBuilder.wrap(new ResourceLocation("ceramics", "kiln")).build(c)))
                     .addCondition(TrueCondition.INSTANCE)
                     .addRecipe(fastGrout)
                     .generateAdvancement()
                     .build(consumer, wrap(TinkerSmeltery.scorchedBrick, folder, "_kiln"));

    // block from bricks
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedBricks)
                       .define('b', TinkerSmeltery.scorchedBrick)
                       .pattern("bb")
                       .pattern("bb")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, wrap(TinkerSmeltery.scorchedBricks, folder, "_from_brick"));
    // ladder from bricks
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedLadder, 4)
                       .define('b', TinkerSmeltery.scorchedBrick)
                       .define('B', TinkerTags.Items.SCORCHED_BLOCKS)
                       .pattern("b b")
                       .pattern("b b")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, prefix(TinkerSmeltery.scorchedLadder, folder));

    // stone -> polished
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.polishedScorchedStone, 4)
                       .define('b', TinkerSmeltery.scorchedStone)
                       .pattern("bb")
                       .pattern("bb")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedStone))
                       .save(consumer, wrap(TinkerSmeltery.polishedScorchedStone, folder, "_crafting"));
    // polished -> bricks
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedBricks, 4)
                       .define('b', TinkerSmeltery.polishedScorchedStone)
                       .pattern("bb")
                       .pattern("bb")
                       .unlockedBy("has_item", has(TinkerSmeltery.polishedScorchedStone))
                       .save(consumer, wrap(TinkerSmeltery.scorchedBricks, folder, "_crafting"));
    // stone -> road
    SimpleCookingRecipeBuilder.smelting(Ingredient.of(TinkerSmeltery.scorchedStone), RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedRoad, 0.1f, 200)
                        .unlockedBy("has_item", has(TinkerSmeltery.scorchedStone))
                        .save(consumer, wrap(TinkerSmeltery.scorchedRoad, folder, "_smelting"));
    // brick slabs -> chiseled
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.chiseledScorchedBricks)
                       .define('s', TinkerSmeltery.scorchedBricks.getSlab())
                       .pattern("s")
                       .pattern("s")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBricks.getSlab()))
                       .save(consumer, wrap(TinkerSmeltery.chiseledScorchedBricks, folder, "_crafting"));
    // stonecutting
    this.scorchedStonecutter(consumer, TinkerSmeltery.polishedScorchedStone, folder);
    this.scorchedStonecutter(consumer, TinkerSmeltery.scorchedBricks, folder);
    this.scorchedStonecutter(consumer, TinkerSmeltery.chiseledScorchedBricks, folder);

    // scorched glass
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedGlass)
                       .define('b', TinkerSmeltery.scorchedBrick)
                       .define('G', Tags.Items.GEMS_QUARTZ)
                       .pattern(" b ")
                       .pattern("bGb")
                       .pattern(" b ")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, prefix(TinkerSmeltery.scorchedGlass, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedLamp)
      .define('b', TinkerSmeltery.scorchedBrick)
      .define('G', Blocks.GLOWSTONE)
      .pattern(" b ")
      .pattern("bGb")
      .pattern(" b ")
      .unlockedBy("has_item", has(Blocks.GLOWSTONE))
      .save(consumer, prefix(TinkerSmeltery.scorchedLamp, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedSoulGlass)
                       .define('b', TinkerSmeltery.scorchedBrick)
                       .define('G', TinkerCommons.soulGlass)
                       .pattern(" b ")
                       .pattern("bGb")
                       .pattern(" b ")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, prefix(TinkerSmeltery.scorchedSoulGlass, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedTintedGlass)
                       .define('b', TinkerSmeltery.scorchedBrick)
                       .define('G', Tags.Items.GLASS_TINTED)
                       .pattern(" b ")
                       .pattern("bGb")
                       .pattern(" b ")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, prefix(TinkerSmeltery.scorchedTintedGlass, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.scorchedGlassPane, 16)
                       .define('#', TinkerSmeltery.scorchedGlass)
                       .pattern("###")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedGlass))
                       .save(consumer, prefix(TinkerSmeltery.scorchedGlassPane, folder));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.scorchedSoulGlassPane, 16)
                       .define('#', TinkerSmeltery.scorchedSoulGlass)
                       .pattern("###")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedSoulGlass))
                       .save(consumer, prefix(TinkerSmeltery.scorchedSoulGlassPane, folder));

    // stairs, slabs, and fences
    this.slabStairsCrafting(consumer, TinkerSmeltery.scorchedBricks, folder, true);
    this.slabStairsCrafting(consumer, TinkerSmeltery.scorchedRoad, folder, true);
    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, TinkerSmeltery.scorchedBricks.getFence(), 6)
                       .define('B', TinkerSmeltery.scorchedBricks)
                       .define('b', TinkerSmeltery.scorchedBrick)
                       .pattern("BbB")
                       .pattern("BbB")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBricks))
                       .save(consumer, prefix(id(TinkerSmeltery.scorchedBricks.getFence()), folder));

    // tanks
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedTank.get(TankType.FUEL_TANK))
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('B', Tags.Items.GEMS_QUARTZ)
                       .pattern("###")
                       .pattern("#B#")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "fuel_tank"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE))
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('B', Tags.Items.GEMS_QUARTZ)
                       .pattern("#B#")
                       .pattern("BBB")
                       .pattern("#B#")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "fuel_gauge"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedTank.get(TankType.INGOT_TANK))
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('B', Tags.Items.GEMS_QUARTZ)
                       .pattern("#B#")
                       .pattern("#B#")
                       .pattern("#B#")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "ingot_tank"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE))
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('B', Tags.Items.GEMS_QUARTZ)
                       .pattern("B#B")
                       .pattern("#B#")
                       .pattern("B#B")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "ingot_gauge"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.scorchedLantern.get(), 3)
                       .define('C', Tags.Items.INGOTS_IRON)
                       .define('B', TinkerSmeltery.scorchedBrick)
                       .define('P', TinkerSmeltery.scorchedGlassPane)
                       .pattern(" C ")
                       .pattern("PPP")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "lantern"));

    // fluid transfer
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedFaucet.get(), 3)
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .pattern("# #")
                       .pattern(" # ")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "faucet"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedChannel.get(), 5)
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .pattern("# #")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "channel"));

    // casting
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.scorchedBasin.get())
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .pattern("# #")
                       .pattern("# #")
                       .pattern("###")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "basin"));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.scorchedTable.get())
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .pattern("###")
                       .pattern("# #")
                       .pattern("# #")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "table"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedProxyTank.get())
      .define('#', TinkerSmeltery.scorchedBrick)
      .define('G', Tags.Items.GEMS_QUARTZ)
      .define('C', TinkerCommons.obsidianPane)
      .pattern("#G#")
      .pattern("CGC")
      .pattern("#G#")
      .unlockedBy("has_item", has(TinkerCommons.obsidianPane))
      .save(consumer, location(folder + "proxy_tank"));


    // peripherals
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.obsidianGauge, 4)
      .define('G', Tags.Items.GLASS_PANES_COLORLESS)
      .define('C', TinkerCommons.obsidianPane)
      .pattern(" C ")
      .pattern("CGC")
      .pattern(" C ")
      .unlockedBy("has_item", has(TinkerCommons.obsidianPane))
      .save(consumer, location(folder + "gauge"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedDrain)
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('C', TinkerCommons.obsidianPane)
                       .pattern("# #")
                       .pattern("C C")
                       .pattern("# #")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "drain"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedDrain)
                         .define('#', TinkerTags.Items.FOUNDRY_BRICKS)
                         .define('C', TinkerCommons.obsidianPane)
                         .pattern("C#C")
                         .unlockedBy("has_item", has(TinkerTags.Items.FOUNDRY_BRICKS)))
                                 .setSource(TinkerTags.Items.FOUNDRY_BRICKS)
                                 .build(consumer, location(folder + "drain_retextured"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedChute)
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('C', TinkerCommons.obsidianPane)
                       .pattern("#C#")
                       .pattern("   ")
                       .pattern("#C#")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "chute"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedChute)
                         .define('#', TinkerTags.Items.FOUNDRY_BRICKS)
                         .define('C', TinkerCommons.obsidianPane)
                         .pattern("C")
                         .pattern("#")
                         .pattern("C")
                         .unlockedBy("has_item", has(TinkerTags.Items.FOUNDRY_BRICKS)))
                                 .setSource(TinkerTags.Items.FOUNDRY_BRICKS)
                                 .build(consumer, location(folder + "chute_retextured"));
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedDuct)
                       .define('#', TinkerSmeltery.scorchedBrick)
                       .define('C', Tags.Items.INGOTS_GOLD)
                       .pattern("# #")
                       .pattern("C C")
                       .pattern("# #")
                       .unlockedBy("has_item", has(Tags.Items.INGOTS_GOLD))
                       .save(consumer, location(folder + "duct"));
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedDuct)
                         .define('#', TinkerTags.Items.FOUNDRY_BRICKS)
                         .define('C', Tags.Items.INGOTS_GOLD)
                         .pattern("C#C")
                         .unlockedBy("has_item", has(TinkerTags.Items.FOUNDRY_BRICKS)))
                                 .setSource(TinkerTags.Items.FOUNDRY_BRICKS)
                                 .build(consumer, location(folder + "duct_retextured"));

    // controllers
    Ingredient similarTanks = NoContainerIngredient.of(TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE), TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE));
    ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, TinkerSmeltery.scorchedAlloyer)
                       .define('G', similarTanks)
                       .define('B', TinkerSmeltery.scorchedBrick)
                       .pattern("BGB")
                       .pattern("BBB")
                       .unlockedBy("has_item", has(TinkerSmeltery.scorchedBrick))
                       .save(consumer, location(folder + "alloyer"));
    // fluid cannon
    ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, TinkerSmeltery.scorchedFluidCannon)
                       .define('T', similarTanks)
                       .define('C', TinkerMaterials.cobalt.getIngotTag())
                       .pattern("CTC")
                       .pattern("CCC")
                       .unlockedBy("has_item", has(TinkerMaterials.cobalt.getIngotTag()))
                       .save(consumer, location(folder + "fluid_cannon"));

    // casting
    String castingFolder = "smeltery/casting/scorched/";
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedStone)
                            .setFluidAndTime(TinkerFluids.scorchedStone, FluidValues.BRICK_BLOCK)
                            .save(consumer, location(castingFolder + "stone_from_scorched"));
    this.ingotCasting(consumer, TinkerFluids.scorchedStone, FluidValues.BRICK, TinkerSmeltery.scorchedBrick, castingFolder + "brick");
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedGlass)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, FluidValues.GEM)
                            .setCast(TinkerSmeltery.scorchedBricks, true)
                            .save(consumer, location(castingFolder + "glass"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedLamp)
      .setFluidAndTime(TinkerFluids.scorchedStone, FluidValues.BRICK_BLOCK)
      .setCast(Blocks.GLOWSTONE, true)
      .save(consumer, location(castingFolder + "lamp"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedSoulGlass)
                            .setFluidAndTime(TinkerFluids.scorchedStone, FluidValues.BRICK_BLOCK)
                            .setCast(TinkerCommons.soulGlass, true)
                            .save(consumer, location(castingFolder + "glass_soul"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTintedGlass)
                            .setFluidAndTime(TinkerFluids.scorchedStone, FluidValues.BRICK_BLOCK)
                            .setCast(Tags.Items.GLASS_TINTED, true)
                            .save(consumer, location(castingFolder + "glass_tinted"));
    // discount for casting panes
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.scorchedGlassPane)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, FluidValues.GEM_SHARD)
                            .setCast(TinkerSmeltery.scorchedBrick, true)
                            .save(consumer, location(castingFolder + "glass_pane"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.scorchedSoulGlassPane)
                            .setFluidAndTime(TinkerFluids.scorchedStone, FluidValues.BRICK)
                            .setCast(TinkerCommons.soulGlassPane, true)
                            .save(consumer, location(castingFolder + "glass_pane_soul"));
    // craft scorched stone from magma and basalt
    // flint is almost a brick
    ItemCastingRecipeBuilder.tableRecipe(TinkerSmeltery.scorchedBrick)
                            .setFluidAndTime(TinkerFluids.magma, FluidValues.SLIMEBALL / 2)
                            .setCast(Items.FLINT, true)
                            .save(consumer, location(castingFolder + "brick_composite"));
    scorchedCasting(consumer, TinkerSmeltery.scorchedStone, Ingredient.of(Blocks.BASALT , Blocks.GRAVEL), castingFolder + "stone_from_magma");
    scorchedCasting(consumer, TinkerSmeltery.polishedScorchedStone, Ingredient.of(Blocks.POLISHED_BASALT), castingFolder + "polished_from_magma");
    // foundry controller
    ItemCastingRecipeBuilder.retexturedBasinRecipe(ItemOutput.fromItem(TinkerSmeltery.foundryController))
                            .setCast(TinkerTags.Items.FOUNDRY_BRICKS, true)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK)
                            .save(consumer, prefix(TinkerSmeltery.foundryController, castingFolder));


    // melting
    String meltingFolder = "smeltery/melting/scorched/";

    // double efficiency when using smeltery for grout
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.netherGrout), TinkerFluids.scorchedStone, FluidValues.BRICK * 2, 1.5f)
                        .save(consumer, location(meltingFolder + "grout"));

    // scorched stone
    // stairs are here since the cheapest stair recipe is stone cutter, 1 to 1
    MeltingRecipeBuilder.melting(CompoundIngredient.of(Ingredient.of(TinkerTags.Items.SCORCHED_BLOCKS),
                                                       Ingredient.of(TinkerSmeltery.scorchedLadder, TinkerSmeltery.scorchedBricks.getStairs(), TinkerSmeltery.scorchedRoad.getStairs())),
																 TinkerFluids.scorchedStone, FluidValues.BRICK_BLOCK, 2.0f)
                        .save(consumer, location(meltingFolder + "block"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedBricks.getSlab(), TinkerSmeltery.scorchedBricks.getSlab(), TinkerSmeltery.scorchedRoad.getSlab()),
																 TinkerFluids.scorchedStone, FluidValues.BRICK_BLOCK / 2, 1.5f)
                        .save(consumer, location(meltingFolder + "slab"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedBrick), TinkerFluids.scorchedStone, FluidValues.BRICK, 1.0f)
                        .save(consumer, location(meltingFolder + "brick"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedBricks.getFence()), TinkerFluids.scorchedStone, FluidValues.BRICK * 3, 1.0f)
                        .save(consumer, location(meltingFolder + "fence"));

    // melt down foundry components
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedFaucet, TinkerSmeltery.scorchedChannel), TinkerFluids.scorchedStone, FluidValues.BRICK, 1.5f)
                        .save(consumer, location(meltingFolder + "faucet"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedBasin, TinkerSmeltery.scorchedTable), TinkerFluids.scorchedStone, FluidValues.BRICK * 7, 2.5f)
                        .save(consumer, location(meltingFolder + "casting"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedProxyTank), TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE * 2, 2.5f)
      .addByproduct(TinkerFluids.scorchedStone.result(FluidValues.BRICK * 4))
      .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 3))
      .save(consumer, location(meltingFolder + "proxy_tank"));

    // tanks
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.scorchedTank.get(TankType.FUEL_TANK)), TinkerFluids.scorchedStone, FluidValues.BRICK * 8, 3f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM))
                        .save(consumer, location(meltingFolder + "fuel_tank"));
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.scorchedTank.get(TankType.INGOT_TANK)), TinkerFluids.scorchedStone, FluidValues.BRICK * 6, 2.5f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 3))
                        .save(consumer, location(meltingFolder + "ingot_tank"));
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE), TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE)), TinkerFluids.scorchedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 5))
                        .save(consumer, location(meltingFolder + "gauge"));
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.scorchedLantern), TinkerFluids.scorchedStone, FluidValues.BRICK * 2, 1.0f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM_SHARD))
                        .addByproduct(TinkerFluids.moltenIron.result(FluidValues.NUGGET * 3))
                        .save(consumer, location(meltingFolder + "lantern"));
    // glass
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedGlass), TinkerFluids.scorchedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM))
                        .save(consumer, location(meltingFolder + "glass"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedSoulGlass), TinkerFluids.scorchedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.liquidSoul.result(FluidValues.GLASS_BLOCK))
                        .save(consumer, location(meltingFolder + "glass_soul"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedTintedGlass), TinkerFluids.scorchedStone, FluidValues.BRICK * 4, 2f)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK))
                        .addByproduct(TinkerFluids.moltenAmethyst.result(FluidValues.GEM * 2))
                        .save(consumer, location(meltingFolder + "glass_tinted"));
    // panes
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedGlassPane), TinkerFluids.scorchedStone, FluidValues.BRICK, 1.0f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM_SHARD))
                        .save(consumer, location(meltingFolder + "pane"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedSoulGlassPane), TinkerFluids.scorchedStone, FluidValues.BRICK, 1.0f)
                        .addByproduct(TinkerFluids.liquidSoul.result(FluidValues.GLASS_PANE))
                        .save(consumer, location(meltingFolder + "pane_soul"));
    // controllers
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedAlloyer), TinkerFluids.scorchedStone, FluidValues.BRICK * 9, 3.5f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 5))
                        .save(consumer, location(meltingFolder + "melter"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedFluidCannon), TinkerFluids.moltenCobalt, FluidValues.INGOT * 5, 3.5f)
                        .addByproduct(TinkerFluids.scorchedStone.result(FluidValues.BRICK * 4))
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 5))
                        .save(consumer, location(meltingFolder + "fluid_cannon"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.foundryController), TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK, 3.5f)
                        .addByproduct(TinkerFluids.scorchedStone.result(FluidValues.BRICK * 4))
                        .save(consumer, location("smeltery/melting/obsidian/foundry_controller"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedDrain, TinkerSmeltery.scorchedChute), TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE * 2, 2.5f)
                        .addByproduct(TinkerFluids.scorchedStone.result(FluidValues.BRICK * 4))
                        .save(consumer, location("smeltery/melting/obsidian/foundry_io"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.obsidianGauge), TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE, 2.5f)
      .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_PANE / 5))
      .save(consumer, location("smeltery/melting/obsidian/gauge"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.scorchedDuct), TinkerFluids.moltenGold, FluidValues.INGOT * 2, 2.5f)
                        .addByproduct(TinkerFluids.scorchedStone.result(FluidValues.BRICK * 4))
                        .save(consumer, location("smeltery/melting/metal/cobalt/scorched_duct"));
  }

  private void addCastingRecipes(Consumer<FinishedRecipe> consumer) {
    // Pure Fluid Recipes
    String folder = "smeltery/casting/";

    // container filling
    ContainerFillingRecipeBuilder.tableRecipe(Items.BUCKET, FluidType.BUCKET_VOLUME)
                                 .save(consumer, location(folder + "filling/bucket"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.copperCan, FluidValues.INGOT)
                                 .save(consumer, location(folder + "filling/copper_can"));
    // potion filling
    FluidIngredient potionBottle = TinkerFluids.potion.ingredient(FluidValues.BOTTLE);
    PotionCastingRecipeBuilder.tableRecipe(Items.POTION)
                              .setBottle(Items.GLASS_BOTTLE)
                              .setFluid(potionBottle)
                              .save(consumer, location(folder + "filling/bottle"));
    PotionCastingRecipeBuilder.tableRecipe(Items.SPLASH_POTION)
                              .setBottle(TinkerTags.Items.SPLASH_BOTTLE)
                              .setFluid(potionBottle)
                              .save(consumer, location(folder + "filling/lingering_bottle"));
    PotionCastingRecipeBuilder.tableRecipe(Items.LINGERING_POTION)
                              .setBottle(TinkerTags.Items.LINGERING_BOTTLE)
                              .setFluid(potionBottle)
                              .save(consumer, location(folder + "filling/splash_bottle"));
    PotionCastingRecipeBuilder.tableRecipe(Items.TIPPED_ARROW)
                              .setBottle(Items.ARROW)
                              .setFluid(TinkerFluids.potion.ingredient(FluidValues.BOTTLE / 5))
                              .setCoolingTime(20)
                              .save(consumer, location(folder + "filling/tipped_arrow"));
    ItemCastingRecipeBuilder.tableRecipe(Items.ARROW)
      .setCast(PotionDisplayIngredient.of(Items.TIPPED_ARROW), true)
      .setFluid(MantleTags.Fluids.WATER, FluidValues.BOTTLE / 5)
      .setCoolingTime(1)
      .save(consumer, location(folder + "filling/tipped_arrow_clean"));
    // tank filling - seared
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.INGOT_TANK), FluidValues.INGOT)
                                 .save(consumer, location(folder + "filling/seared_ingot_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.INGOT_GAUGE), FluidValues.INGOT)
                                 .save(consumer, location(folder + "filling/seared_ingot_gauge"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.FUEL_TANK), FluidType.BUCKET_VOLUME / 4)
                                 .save(consumer, location(folder + "filling/seared_fuel_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedTank.get(TankType.FUEL_GAUGE), FluidType.BUCKET_VOLUME / 4)
                                 .save(consumer, location(folder + "filling/seared_fuel_gauge"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.searedLantern, FluidValues.NUGGET)
                                 .save(consumer, location(folder + "filling/seared_lantern_pixel"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.searedLantern, FluidValues.LANTERN_CAPACITY)
                                 .save(consumer, location(folder + "filling/seared_lantern_full"));
    // tank filling - scorched
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.INGOT_TANK), FluidValues.INGOT)
                                 .save(consumer, location(folder + "filling/scorched_ingot_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.INGOT_GAUGE), FluidValues.INGOT)
                                 .save(consumer, location(folder + "filling/scorched_ingot_gauge"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.FUEL_TANK), FluidType.BUCKET_VOLUME / 4)
                                 .save(consumer, location(folder + "filling/scorched_fuel_tank"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedTank.get(TankType.FUEL_GAUGE), FluidType.BUCKET_VOLUME / 4)
                                 .save(consumer, location(folder + "filling/scorched_fuel_gauge"));
    ContainerFillingRecipeBuilder.tableRecipe(TinkerSmeltery.scorchedLantern, FluidValues.NUGGET)
                                 .save(consumer, location(folder + "filling/scorched_lantern_pixel"));
    ContainerFillingRecipeBuilder.basinRecipe(TinkerSmeltery.scorchedLantern, FluidValues.LANTERN_CAPACITY)
                                 .save(consumer, location(folder + "filling/scorched_lantern_full"));

    // Slime
    String slimeFolder = folder + "slime/";
    this.slimeCasting(consumer, TinkerFluids.earthSlime, SlimeType.EARTH, slimeFolder);
    this.slimeCasting(consumer, TinkerFluids.skySlime,   SlimeType.SKY,   slimeFolder);
    this.slimeCasting(consumer, TinkerFluids.enderSlime, SlimeType.ENDER, slimeFolder);
    this.slimeCasting(consumer, TinkerFluids.ichor,      SlimeType.ICHOR, slimeFolder);
    // magma cream
    ItemCastingRecipeBuilder.basinRecipe(Blocks.MAGMA_BLOCK)
                            .setFluidAndTime(TinkerFluids.magma, FluidValues.SLIME_CONGEALED)
                            .save(consumer, location(slimeFolder + "magma_block"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerFluids.magmaBottle)
                            .setFluid(TinkerFluids.magma.getTag(), FluidValues.SLIMEBALL)
                            .setCoolingTime(1)
                            .setCast(Items.GLASS_BOTTLE, true)
                            .save(consumer, location(slimeFolder + "magma_bottle"));

    // glass
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.clearGlass)
                            .setFluidAndTime(TinkerFluids.moltenGlass, FluidValues.GLASS_BLOCK)
                            .save(consumer, location(folder + "glass/block"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.clearGlassPane)
                            .setFluidAndTime(TinkerFluids.moltenGlass, FluidValues.GLASS_PANE)
                            .save(consumer, location(folder + "glass/pane"));
    // soul glass
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.soulGlass)
                            .setFluidAndTime(TinkerFluids.liquidSoul, FluidValues.GLASS_BLOCK)
                            .save(consumer, location(folder + "soul/glass"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.soulGlassPane)
                            .setFluidAndTime(TinkerFluids.liquidSoul, FluidValues.GLASS_PANE)
                            .save(consumer, location(folder + "soul/pane"));

    // clay
    ItemCastingRecipeBuilder.basinRecipe(Blocks.TERRACOTTA)
                            .setFluidAndTime(TinkerFluids.moltenClay, FluidValues.SLIME_CONGEALED)
                            .save(consumer, location(folder + "clay/block"));
    this.ingotCasting(consumer, TinkerFluids.moltenClay, FluidValues.SLIMEBALL, Items.BRICK, folder + "clay/brick");
    this.tagCasting(consumer, TinkerFluids.moltenClay, FluidValues.SLIMEBALL, TinkerSmeltery.plateCast, "plates/brick", folder + "clay/plate", true);

    // amethyst
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.clearTintedGlass)
                            .setCast(Tags.Items.GLASS_COLORLESS, true)
                            .setFluidAndTime(TinkerFluids.moltenAmethyst, FluidValues.GEM * 2)
                            .save(consumer, location(folder + "amethyst/glass"));

    // diamond
    ItemCastingRecipeBuilder.tableDuplication()
                            .setCast(CompoundIngredient.of(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ItemTags.TRIM_TEMPLATES)), false)
                            .setFluidAndTime(TinkerFluids.moltenDiamond, FluidValues.GEM * 5)
                            .save(consumer, location(folder + "diamond/smithing_template"));

    // ender pearls
    ItemCastingRecipeBuilder.tableRecipe(Items.ENDER_PEARL)
                            .setFluidAndTime(TinkerFluids.moltenEnder, FluidValues.SLIMEBALL)
                            .save(consumer, location(folder + "ender/pearl"));
    ItemCastingRecipeBuilder.tableRecipe(Items.ENDER_EYE)
                            .setFluidAndTime(TinkerFluids.moltenEnder, FluidValues.SLIMEBALL)
                            .setCast(Items.BLAZE_POWDER, true)
                            .save(consumer, location(folder + "ender/eye"));

    // obsidian
    ItemCastingRecipeBuilder.basinRecipe(Blocks.OBSIDIAN)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK)
                            .save(consumer, location(folder + "obsidian/block"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.obsidianPane)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE)
                            .save(consumer, location(folder + "obsidian/pane"));
    // Molten objects with Bucket, Block, Ingot, and Nugget forms with standard values
    String metalFolder = folder + "metal/";
    this.ingotCasting(consumer, TinkerFluids.moltenDebris, Items.NETHERITE_SCRAP, metalFolder + "netherite/scrap");
    this.tagCasting(consumer, TinkerFluids.moltenDebris, FluidValues.NUGGET, TinkerSmeltery.nuggetCast, TinkerTags.Items.NUGGETS_NETHERITE_SCRAP.location().getPath(), metalFolder + "netherite/debris_nugget", false);
    // ancient is not castable, outside of repair kits
    this.castingWithCast(consumer, TinkerFluids.moltenDebris, FluidValues.INGOT * 2, TinkerSmeltery.repairKitCast, ItemOutput.fromStack(TinkerToolParts.repairKit.get().withMaterialForDisplay(MaterialIds.ancient)), metalFolder + "netherite/ancient_repair_kit");

    // water
    String waterFolder = folder + "water/";
    ItemCastingRecipeBuilder.basinRecipe(Blocks.MUD)
                            .setFluidAndTime(new FluidStack(Fluids.WATER, FluidValues.BOTTLE))
                            .setCast(new BlockTagIngredient(BlockTags.CONVERTABLE_TO_MUD), true)
                            .save(consumer, location(waterFolder + "mud"));
    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromStack(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)))
                            .setFluid(MantleTags.Fluids.WATER, FluidValues.BOTTLE * 2)
                            .setCoolingTime(1)
                            .setCast(Items.GLASS_BOTTLE, true)
                            .save(consumer, location(waterFolder + "bottle"));
    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromStack(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.WATER)))
                            .setFluid(MantleTags.Fluids.WATER, FluidValues.BOTTLE * 2)
                            .setCoolingTime(1)
                            .setCast(TinkerTags.Items.SPLASH_BOTTLE, true)
                            .save(consumer, location(waterFolder + "splash"));
    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromStack(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), Potions.WATER)))
                            .setFluid(MantleTags.Fluids.WATER, FluidValues.BOTTLE * 2)
                            .setCoolingTime(1)
                            .setCast(TinkerTags.Items.LINGERING_BOTTLE, true)
                            .save(consumer, location(waterFolder + "lingering"));
    ItemCastingRecipeBuilder.basinRecipe(Blocks.WET_SPONGE)
      .setFluid(Fluids.WATER, FluidValues.BOTTLE)
      .setCoolingTime(1)
      .setCast(Blocks.SPONGE, true)
      .save(consumer, location(waterFolder + "wet_sponge"));

    // casting concrete
    BiConsumer<Block,Block> concreteCasting = (powder, block) ->
      ItemCastingRecipeBuilder.basinRecipe(block)
                              .setFluidAndTime(new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME / 10))
                              .setCast(powder, true)
                              .save(consumer, prefix(id(block), waterFolder));
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

    // blazewood
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.blazewood)
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 5)
                            .setCast(TinkerWorld.bloodshroom, true)
                            .save(consumer, prefix(TinkerMaterials.blazewood, folder));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.blazewood.getSlab())
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 10)
                            .setCast(TinkerWorld.bloodshroom.getSlab(), true)
                            .save(consumer, wrap(TinkerMaterials.blazewood, folder, "_slab"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.blazewood.getStairs())
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 5)
                            .setCast(TinkerWorld.bloodshroom.getStairs(), true)
                            .save(consumer, wrap(TinkerMaterials.blazewood, folder, "_stairs"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.blazewood.getFence())
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 5)
                            .setCast(TinkerWorld.bloodshroom.getFence(), true)
                            .save(consumer, wrap(TinkerMaterials.blazewood, folder, "_fence"));

    // cast molten blaze into blazing stuff
    castingWithCast(consumer, TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 10, TinkerSmeltery.rodCast, Items.BLAZE_ROD, folder + "blaze/rod");
    ItemCastingRecipeBuilder.tableRecipe(Items.MAGMA_CREAM)
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 20)
                            .setCast(Tags.Items.SLIMEBALLS, true)
                            .save(consumer, location(folder + "blaze/cream"));
    ItemCastingRecipeBuilder.basinRecipe(Blocks.MAGMA_BLOCK)
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 5)
                            .setCast(TinkerTags.Items.CONGEALED_SLIME, true)
                            .save(consumer, location(folder + "blaze/congealed"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerMaterials.blazingBone)
                            .setFluidAndTime(TinkerFluids.blazingBlood, FluidType.BUCKET_VOLUME / 5)
                            .setCast(TinkerTags.Items.WITHER_BONES, true)
                            .save(consumer, location(folder + "blaze/bone"));

    // honey
    ItemCastingRecipeBuilder.tableRecipe(Items.HONEY_BOTTLE)
                            .setFluid(TinkerFluids.honey.getTag(), FluidValues.BOTTLE)
                            .setCoolingTime(1)
                            .setCast(Items.GLASS_BOTTLE, true)
                            .save(consumer, location(folder + "honey/bottle"));
    ItemCastingRecipeBuilder.basinRecipe(Items.HONEY_BLOCK)
                            .setFluidAndTime(TinkerFluids.honey, FluidValues.BOTTLE * 4)
                            .save(consumer, location(folder + "honey/block"));
    // soup
    ItemCastingRecipeBuilder.tableRecipe(Items.BEETROOT_SOUP)
                            .setFluid(TinkerFluids.beetrootSoup.getTag(), FluidValues.BOWL)
                            .setCast(Items.BOWL, true)
                            .setCoolingTime(1)
                            .save(consumer, location(folder + "soup/beetroot"));
    ItemCastingRecipeBuilder.tableRecipe(Items.MUSHROOM_STEW)
                            .setFluid(TinkerFluids.mushroomStew.getTag(), FluidValues.BOWL)
                            .setCast(Items.BOWL, true)
                            .setCoolingTime(1)
                            .save(consumer, location(folder + "soup/mushroom"));
    ItemCastingRecipeBuilder.tableRecipe(Items.RABBIT_STEW)
                            .setFluid(TinkerFluids.rabbitStew.getTag(), FluidValues.BOWL)
                            .setCast(Items.BOWL, true)
                            .setCoolingTime(1)
                            .save(consumer, location(folder + "soup/rabbit"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerFluids.meatSoupBowl)
                            .setFluid(TinkerFluids.meatSoup.getTag(), FluidValues.BOWL)
                            .setCast(Items.BOWL, true)
                            .setCoolingTime(1)
                            .save(consumer, location(folder + "soup/meat"));
    // venom
    ItemCastingRecipeBuilder.tableRecipe(TinkerFluids.venomBottle)
                            .setFluid(TinkerFluids.venom.getTag(), FluidValues.BOTTLE)
                            .setCoolingTime(1)
                            .setCast(Items.GLASS_BOTTLE, true)
                            .save(consumer, location(folder + "venom_bottle"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerMaterials.venombone)
                            .setFluidAndTime(TinkerFluids.venom, FluidValues.SLIMEBALL)
                            .setCast(Tags.Items.BONES, true)
                            .save(consumer, location(slimeFolder + "venombone"));

    // cheese
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.cheeseIngot)
                            .setFluid(Tags.Fluids.MILK, FluidValues.BOTTLE)
                            .setCast(TinkerSmeltery.ingotCast.getMultiUseTag(), false)
                            .setCoolingTime(20*60*2)
                            .save(consumer, location(folder + "cheese_ingot_gold_cast"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.cheeseIngot)
                            .setFluid(Tags.Fluids.MILK, FluidValues.BOTTLE)
                            .setCast(TinkerSmeltery.ingotCast.getSingleUseTag(), true)
                            .setCoolingTime(20*60*2)
                            .save(consumer, location(folder + "cheese_ingot_sand_cast"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerCommons.cheeseBlock)
                            .setFluid(Tags.Fluids.MILK, FluidType.BUCKET_VOLUME)
                            .setCoolingTime(20*60*5)
                            .save(consumer, location(folder + "cheese_block"));
    ItemCastingRecipeBuilder.tableRecipe(Items.BONE)
      .setFluid(Tags.Fluids.MILK, FluidType.BUCKET_VOLUME / 5)
      .setCoolingTime(50)
      .setCast(TinkerTags.Items.WITHER_BONES, true)
      .save(consumer, location(folder + "bone_purifying"));


    String castFolder = "smeltery/casts/";
    this.castCreation(consumer, CompoundIngredient.of(
      // fake ingots are in the ingot tag, but you get the default "missing" ingot from that
      // so subtract it out and replace with the material version for nicer display
      DifferenceIngredient.of(Ingredient.of(Tags.Items.INGOTS), Ingredient.of(TinkerToolParts.fakeIngot)),
      MaterialIngredient.of(TinkerToolParts.fakeIngot)
    ), TinkerSmeltery.ingotCast, castFolder, "ingots");
    this.castCreation(consumer, Tags.Items.NUGGETS, TinkerSmeltery.nuggetCast, castFolder);
    this.castCreation(consumer, Tags.Items.GEMS, TinkerSmeltery.gemCast, castFolder);
    this.castCreation(consumer, Tags.Items.RODS, TinkerSmeltery.rodCast, castFolder);
    // other casts are added if needed
    this.castCreation(withCondition(consumer, tagCondition("plates")), getItemTag(COMMON, "plates"), TinkerSmeltery.plateCast, castFolder);
    this.castCreation(withCondition(consumer, tagCondition("gears")),  getItemTag(COMMON, "gears"), TinkerSmeltery.gearCast, castFolder);
    this.castCreation(withCondition(consumer, tagCondition("coins")),  getItemTag(COMMON, "coins"), TinkerSmeltery.coinCast, castFolder);
    this.castCreation(withCondition(consumer, tagCondition("wires")),  getItemTag(COMMON, "wires"), TinkerSmeltery.wireCast, castFolder);

    // misc casting - gold
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.goldBars)
                            .setFluidAndTime(TinkerFluids.moltenGold, FluidValues.NUGGET * 3)
                            .save(consumer, location(metalFolder + "gold/bars"));
    ItemCastingRecipeBuilder.tableRecipe(Items.GOLDEN_APPLE)
                            .setFluidAndTime(TinkerFluids.moltenGold, FluidValues.INGOT * 8)
                            .setCast(Items.APPLE, true)
                            .save(consumer, location(metalFolder + "gold/apple"));
    ItemCastingRecipeBuilder.tableRecipe(Items.GLISTERING_MELON_SLICE)
                            .setFluidAndTime(TinkerFluids.moltenGold, FluidValues.NUGGET * 8)
                            .setCast(Items.MELON_SLICE, true)
                            .save(consumer, location(metalFolder + "gold/melon"));
    ItemCastingRecipeBuilder.tableRecipe(Items.GOLDEN_CARROT)
                            .setFluidAndTime(TinkerFluids.moltenGold, FluidValues.NUGGET * 8)
                            .setCast(Items.CARROT, true)
                            .save(consumer, location(metalFolder + "gold/carrot"));
    ItemCastingRecipeBuilder.tableRecipe(Items.CLOCK)
                            .setFluidAndTime(TinkerFluids.moltenGold, FluidValues.INGOT * 4)
                            .setCast(Items.REDSTONE, true)
                            .save(consumer, location(metalFolder + "gold/clock"));
    // misc casting - iron
    ItemCastingRecipeBuilder.tableRecipe(Blocks.IRON_BARS)  // cheaper by 6mb, not a duplication as the melting recipe was adjusted too (like panes)
                            .setFluidAndTime(TinkerFluids.moltenIron, FluidValues.NUGGET * 3)
                            .save(consumer, location(metalFolder + "iron/bars"));
    ItemCastingRecipeBuilder.tableRecipe(Items.LANTERN)
                            .setFluidAndTime(TinkerFluids.moltenIron, FluidValues.NUGGET * 8)
                            .setCast(Blocks.TORCH, true)
                            .save(consumer, location(metalFolder + "iron/lantern"));
    ItemCastingRecipeBuilder.tableRecipe(Items.SOUL_LANTERN)
                            .setFluidAndTime(TinkerFluids.moltenIron, FluidValues.NUGGET * 8)
                            .setCast(Blocks.SOUL_TORCH, true)
                            .save(consumer, location(metalFolder + "iron/soul_lantern"));
    ItemCastingRecipeBuilder.tableRecipe(Items.COMPASS)
                            .setFluidAndTime(TinkerFluids.moltenIron, FluidValues.INGOT * 4)
                            .setCast(Items.REDSTONE, true)
                            .save(consumer, location(metalFolder + "iron/compass"));
    // ender chest
    ItemCastingRecipeBuilder.basinRecipe(Blocks.ENDER_CHEST)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK * 8)
                            .setCast(Items.ENDER_EYE, true)
                            .save(consumer, location(folder + "obsidian/chest"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.nahuatl)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE)
                            .setCast(ItemTags.PLANKS, true)
                            .save(consumer, location(folder + "obsidian/nahuatl"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.nahuatl.getSlab())
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE / 2)
                            .setCast(ItemTags.WOODEN_SLABS, true)
                            .save(consumer, location(folder + "obsidian/nahuatl_slab"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.nahuatl.getStairs())
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE)
                            .setCast(ItemTags.WOODEN_STAIRS, true)
                            .save(consumer, location(folder + "obsidian/nahuatl_stairs"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerMaterials.nahuatl.getFence())
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE)
                            .setCast(ItemTags.WOODEN_FENCES, true)
                            .save(consumer, location(folder + "obsidian/nahuatl_fence"));
    // overworld stones from quartz
    ItemCastingRecipeBuilder.basinRecipe(Blocks.ANDESITE)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, FluidValues.GEM / 2)
                            .setCast(Tags.Items.COBBLESTONE, true)
                            .save(consumer, prefix(id(Blocks.ANDESITE), folder + "quartz/"));
    ItemCastingRecipeBuilder.basinRecipe(Blocks.DIORITE)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, FluidValues.GEM / 2)
                            .setCast(Blocks.ANDESITE, true)
                            .save(consumer, prefix(id(Blocks.DIORITE), folder + "quartz/"));
    ItemCastingRecipeBuilder.basinRecipe(Blocks.GRANITE)
                            .setFluidAndTime(TinkerFluids.moltenQuartz, FluidValues.GEM)
                            .setCast(Blocks.DIORITE, true)
                            .save(consumer, prefix(id(Blocks.GRANITE), folder + "quartz/"));
  }

  private void addMeltingRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "smeltery/melting/";

    // water from ice
    MeltingRecipeBuilder.melting(Ingredient.of(Items.ICE), Fluids.WATER, FluidType.BUCKET_VOLUME, 1.0f)
                        .save(consumer, location(folder + "water/ice"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.PACKED_ICE), Fluids.WATER, FluidType.BUCKET_VOLUME * 9, 3.0f)
                        .save(consumer, location(folder + "water/packed_ice"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.BLUE_ICE), Fluids.WATER, FluidType.BUCKET_VOLUME * 81, 9.0f)
                        .save(consumer, location(folder + "water/blue_ice"));
    // water from snow
    MeltingRecipeBuilder.melting(Ingredient.of(Items.SNOWBALL), Fluids.WATER, FluidType.BUCKET_VOLUME / 8, 0.5f)
                        .save(consumer, location(folder + "water/snowball"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.SNOW_BLOCK), Fluids.WATER, FluidType.BUCKET_VOLUME / 2, 0.75f)
                        .save(consumer, location(folder + "water/snow_block"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.SNOW), Fluids.WATER, FluidType.BUCKET_VOLUME / 8, 0.5f)
                        .save(consumer, location(folder + "water/snow_layer"));

    // ores
    String metalFolder = folder + "metal/";
    MeltingRecipeBuilder.melting(Ingredient.of(Tags.Items.ORES_NETHERITE_SCRAP), TinkerFluids.moltenDebris, FluidValues.INGOT, 2.0f)
                        .setOre(OreRateType.METAL)
                        .addByproduct(TinkerFluids.moltenNetherite.result(FluidValues.NUGGET * 3))
                        .save(consumer, location(metalFolder + "molten_debris/ore"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerTags.Items.INGOTS_NETHERITE_SCRAP), TinkerFluids.moltenDebris, FluidValues.INGOT, 1.0f)
                        .save(consumer, location(metalFolder + "molten_debris/scrap"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerTags.Items.NUGGETS_NETHERITE_SCRAP), TinkerFluids.moltenDebris, FluidValues.NUGGET, 1 / 3f)
                        .save(consumer, location(metalFolder + "molten_debris/debris_nugget"));
    
    // venom
    MeltingRecipeBuilder.melting(Ingredient.of(Items.SPIDER_EYE), TinkerFluids.venom, FluidValues.BOTTLE, 1.0f)
                        .save(consumer, location(folder + "venom/eye"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.FERMENTED_SPIDER_EYE), TinkerFluids.venom, FluidValues.BOTTLE * 2, 1.0f)
                        .save(consumer, location(folder + "venom/fermented_eye"));

    // glass
    MeltingRecipeBuilder.melting(Ingredient.of(ItemTags.SMELTS_TO_GLASS), TinkerFluids.moltenGlass, FluidValues.GLASS_BLOCK, 1.5f)
                        .save(consumer, location(folder + "glass/sand"));
    MeltingRecipeBuilder.melting(Ingredient.of(Tags.Items.GLASS_SILICA), TinkerFluids.moltenGlass, FluidValues.GLASS_BLOCK, 1.0f)
                        .save(consumer, location(folder + "glass/block"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerTags.Items.GLASS_PANES_SILICA), TinkerFluids.moltenGlass, FluidValues.GLASS_PANE, 0.5f)
                        .save(consumer, location(folder + "glass/pane"));
    MeltingRecipeBuilder.melting(CompoundIngredient.of(Ingredient.of(Items.GLASS_BOTTLE), Ingredient.of(TinkerTags.Items.SPLASH_BOTTLE), Ingredient.of(TinkerTags.Items.LINGERING_BOTTLE)),
      TinkerFluids.moltenGlass, FluidValues.GLASS_BLOCK, 1.25f).save(consumer, location(folder + "glass/bottle"));
    // melt extra sand casts back
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerSmeltery.blankSandCast, TinkerSmeltery.blankRedSandCast),
                                 TinkerFluids.moltenGlass, FluidValues.GLASS_PANE, 0.75f)
                        .save(consumer, location(folder + "glass/sand_cast"));

    // liquid soul
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.SOUL_SAND, Blocks.SOUL_SOIL), TinkerFluids.liquidSoul, FluidValues.GLASS_BLOCK, 1.5f)
                        .save(consumer, location(folder + "soul/sand"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.soulGlass), TinkerFluids.liquidSoul, FluidValues.GLASS_BLOCK, 1.0f)
                        .save(consumer, location(folder + "soul/glass"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.soulGlassPane), TinkerFluids.liquidSoul, FluidValues.GLASS_PANE, 0.5f)
                        .save(consumer, location(folder + "soul/pane"));

    // clay
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.CLAY), TinkerFluids.moltenClay, FluidValues.BRICK_BLOCK, 1.0f)
                        .save(consumer, location(folder + "clay/block"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.CLAY_BALL), TinkerFluids.moltenClay, FluidValues.BRICK, 0.5f)
                        .save(consumer, location(folder + "clay/ball"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.FLOWER_POT), TinkerFluids.moltenClay, FluidValues.BRICK * 3, 2.0f)
                        .save(consumer, location(folder + "clay/pot"));
    tagMelting(consumer, TinkerFluids.moltenClay, FluidValues.BRICK, "plates/brick", 1.0f, folder + "clay/plate", true);
    // terracotta
    Ingredient terracottaBlock = Ingredient.of(
      Blocks.TERRACOTTA, Blocks.BRICKS, Blocks.BRICK_WALL, Blocks.BRICK_STAIRS,
      Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA,
      Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA,
      Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA,
      Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA,
      Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA,
      Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA,
      Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA,
      Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA, Blocks.DECORATED_POT);
    MeltingRecipeBuilder.melting(terracottaBlock, TinkerFluids.moltenClay, FluidValues.BRICK_BLOCK, 2.0f)
                        .save(consumer, location(folder + "clay/terracotta"));
    MeltingRecipeBuilder.melting(CompoundIngredient.of(Ingredient.of(Items.BRICK), Ingredient.of(ItemTags.DECORATED_POT_SHERDS)), TinkerFluids.moltenClay, FluidValues.BRICK, 1.0f)
                        .save(consumer, location(folder + "clay/brick"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.BRICK_SLAB),
                                 TinkerFluids.moltenClay, FluidValues.BRICK_BLOCK / 2, 1.5f)
                        .save(consumer, location(folder + "clay/brick_slab"));

    // slime
    String slimeFolder = folder + "slime/";
    slimeMelting(consumer, TinkerFluids.earthSlime, SlimeType.EARTH, slimeFolder);
    slimeMelting(consumer, TinkerFluids.skySlime,   SlimeType.SKY, slimeFolder);
    slimeMelting(consumer, TinkerFluids.enderSlime, SlimeType.ENDER, slimeFolder);
    // ichor is special, it requires a byproduct to melt
    String ichorFolder = slimeFolder + SlimeType.ICHOR.getSerializedName() + "/";
    MeltingRecipeBuilder.melting(Ingredient.of(SlimeType.ICHOR.getSlimeballTag()), TinkerFluids.blazingBlood, FluidValues.ICHOR_BLAZING_BLOOD, 1.0f)
                        .addByproduct(TinkerFluids.ichor.result(FluidValues.ICHOR_BYPRODUCT))
                        .save(consumer, location(ichorFolder + "ball"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.congealedSlime.get(SlimeType.ICHOR)), TinkerFluids.blazingBlood, FluidValues.ICHOR_BLAZING_BLOOD * 4, 2.0f)
                        .addByproduct(TinkerFluids.ichor.result(FluidValues.ICHOR_BYPRODUCT * 4))
                        .save(consumer, location(ichorFolder + "congealed"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.slime.get(SlimeType.ICHOR)), TinkerFluids.blazingBlood, FluidValues.ICHOR_BLAZING_BLOOD * 9, 3.0f)
                        .addByproduct(TinkerFluids.ichor.result(FluidValues.ICHOR_BYPRODUCT * 9))
                        .save(consumer, location(ichorFolder + "block"));
    // magma cream
    MeltingRecipeBuilder.melting(Ingredient.of(Items.MAGMA_CREAM), TinkerFluids.magma, FluidValues.SLIMEBALL, 1.0f)
                        .save(consumer, location(slimeFolder + "magma/ball"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.MAGMA_BLOCK), TinkerFluids.magma, FluidValues.SLIME_CONGEALED, 3.0f)
                        .save(consumer, location(slimeFolder + "magma/block"));

    // copper cans if empty
    MeltingRecipeBuilder.melting(NoContainerIngredient.of(TinkerSmeltery.copperCan), TinkerFluids.moltenCopper, FluidValues.INGOT, 1.0f)
                        .save(consumer, location(metalFolder + "copper/can"));
    // ender
    MeltingRecipeBuilder.melting(
      CompoundIngredient.of(Ingredient.of(Tags.Items.ENDER_PEARLS), Ingredient.of(Items.ENDER_EYE)),
      TinkerFluids.moltenEnder, FluidValues.SLIMEBALL, 1.0f)
                        .save(consumer, location(folder + "ender/pearl"));

    // obsidian
    MeltingRecipeBuilder.melting(Ingredient.of(Tags.Items.OBSIDIAN), TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK, 2.0f)
                        .save(consumer, location(folder + "obsidian/block"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.obsidianPane), TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE, 1.5f)
                        .save(consumer, location(folder + "obsidian/pane"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.ENDER_CHEST), TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK * 8, 5.0f)
                        .addByproduct(TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL))
                        .save(consumer, location(folder + "obsidian/chest"));
    tagMelting(consumer, TinkerFluids.moltenObsidian, FluidValues.GLASS_PANE, "dusts/obsidian", 1.0f, folder + "obsidian/dust", true);

    // emerald
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.emeraldReinforcement), TinkerFluids.moltenEmerald, FluidValues.GEM_SHARD)
                        .addByproduct(TinkerFluids.moltenObsidian.result(FluidValues.GLASS_PANE))
                        .save(consumer, location(metalFolder + "emerald/reinforcement"));

    // quartz
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.SMOOTH_QUARTZ, Blocks.QUARTZ_PILLAR, Blocks.QUARTZ_BRICKS, Blocks.CHISELED_QUARTZ_BLOCK, Blocks.QUARTZ_STAIRS, Blocks.SMOOTH_QUARTZ_STAIRS),
      TinkerFluids.moltenQuartz, FluidValues.SMALL_GEM_BLOCK, 2.0f)
                        .save(consumer, location(folder + "quartz/decorative_block"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.QUARTZ_SLAB, Blocks.SMOOTH_QUARTZ_SLAB), TinkerFluids.moltenQuartz, FluidValues.GEM * 2, 1.5f)
                        .save(consumer, location(folder + "quartz/slab"));

    // amethyst
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.AMETHYST_CLUSTER), TinkerFluids.moltenAmethyst, FluidValues.GEM * 4, 4.0f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 4))
                        .setOre(OreRateType.GEM)
                        .save(consumer, location(folder + "amethyst/cluster"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.SMALL_AMETHYST_BUD), TinkerFluids.moltenAmethyst, FluidValues.GEM, 1.0f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM))
                        .setOre(OreRateType.GEM)
                        .save(consumer, location(folder + "amethyst/bud_small"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.MEDIUM_AMETHYST_BUD), TinkerFluids.moltenAmethyst, FluidValues.GEM * 2, 2.0f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 2))
                        .setOre(OreRateType.GEM)
                        .save(consumer, location(folder + "amethyst/bud_medium"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.LARGE_AMETHYST_BUD), TinkerFluids.moltenAmethyst, FluidValues.GEM * 3, 3.0f)
                        .addByproduct(TinkerFluids.moltenQuartz.result(FluidValues.GEM * 3))
                        .setOre(OreRateType.GEM)
                        .save(consumer, location(folder + "amethyst/bud_large"));

    // iron melting - standard values
    MeltingRecipeBuilder.melting(Ingredient.of(Items.ACTIVATOR_RAIL, Items.DETECTOR_RAIL, Blocks.STONECUTTER, Blocks.PISTON, Blocks.STICKY_PISTON), TinkerFluids.moltenIron, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "iron/ingot_1"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.HEAVY_WEIGHTED_PRESSURE_PLATE, Items.IRON_DOOR, Blocks.SMITHING_TABLE), TinkerFluids.moltenIron, FluidValues.INGOT * 2)
                        .save(consumer, location(metalFolder + "iron/ingot_2"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.BUCKET), TinkerFluids.moltenIron, FluidValues.INGOT * 3)
                        .save(consumer, location(metalFolder + "iron/bucket"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.COMPASS, Blocks.IRON_TRAPDOOR), TinkerFluids.moltenIron, FluidValues.INGOT * 4)
                        .save(consumer, location(metalFolder + "iron/ingot_4"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.BLAST_FURNACE, Blocks.HOPPER, Items.MINECART), TinkerFluids.moltenIron, FluidValues.INGOT * 5)
                        .save(consumer, location(metalFolder + "iron/ingot_5"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.CAULDRON), TinkerFluids.moltenIron, FluidValues.INGOT * 7)
                        .save(consumer, location(metalFolder + "iron/cauldron"));
    // non-standard
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.CHAIN), TinkerFluids.moltenIron, FluidValues.INGOT + FluidValues.NUGGET * 2)
                        .save(consumer, location(metalFolder + "iron/chain"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL), TinkerFluids.moltenIron, FluidValues.INGOT * 4 + FluidValues.METAL_BLOCK * 3)
                        .save(consumer, location(metalFolder + "iron/anvil"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.IRON_BARS, Blocks.RAIL), TinkerFluids.moltenIron, FluidValues.NUGGET * 3)
                        .save(consumer, location(metalFolder + "iron/nugget_3"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.ironPlatform), TinkerFluids.moltenIron, FluidValues.NUGGET * 10)
                        .save(consumer, location(metalFolder + "iron/platform"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.TRIPWIRE_HOOK), TinkerFluids.moltenIron, FluidValues.NUGGET * 4)
                        .save(consumer, location(metalFolder + "iron/tripwire"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.LANTERN, Blocks.SOUL_LANTERN), TinkerFluids.moltenIron, FluidValues.NUGGET * 8)
                        .save(consumer, location(metalFolder + "iron/lantern"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.ironReinforcement), TinkerFluids.moltenIron, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "iron/reinforcement"));
    // tools
    MeltingRecipeBuilder.melting(Ingredient.of(Items.CROSSBOW), TinkerFluids.moltenIron, FluidValues.NUGGET * 13) // tripwire hook is 4 nuggets, ingot is 9 nuggets
                        .setDamagable(FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "iron/crossbow"));
    // unique melting
    MeltingRecipeBuilder.melting(Ingredient.of(Items.IRON_HORSE_ARMOR), TinkerFluids.moltenIron, FluidValues.INGOT * 7)
                        .save(consumer, location(metalFolder + "iron/horse_armor"));
    // chainmail armor to steel
    // working off the assumption that some mods out there decided to craft chainmail for an ingots worth of material at minimum, possibly a bit more if they used chains (which is nonsensical)
    final int chainIron = FluidValues.NUGGET * 6;
    final int chainSteel = FluidValues.NUGGET * 3;
    MeltingRecipeBuilder.melting(Ingredient.of(Items.CHAINMAIL_HELMET), TinkerFluids.moltenIron, chainIron * 5)
                        .addByproduct(TinkerFluids.moltenSteel.result(chainSteel * 5))
                        .setDamagable(FluidValues.NUGGET, FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "iron/chain_helmet"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.CHAINMAIL_CHESTPLATE), TinkerFluids.moltenIron, chainIron * 8)
                        .addByproduct(TinkerFluids.moltenSteel.result(chainSteel * 8))
                        .setDamagable(FluidValues.NUGGET, FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "iron/chain_chestplate"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.CHAINMAIL_LEGGINGS), TinkerFluids.moltenIron, chainIron * 7)
                        .addByproduct(TinkerFluids.moltenSteel.result(chainSteel * 7))
                        .setDamagable(FluidValues.NUGGET, FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "iron/chain_leggings"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.CHAINMAIL_BOOTS), TinkerFluids.moltenIron, chainIron * 4)
                        .addByproduct(TinkerFluids.moltenSteel.result(chainSteel * 4))
                        .setDamagable(FluidValues.NUGGET, FluidValues.NUGGET)
                        .save(consumer, location(metalFolder + "iron/chain_boots"));



    // gold melting
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerTags.Items.GOLD_CASTS), TinkerFluids.moltenGold, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "gold/cast"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.POWERED_RAIL), TinkerFluids.moltenGold, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "gold/powered_rail"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE), TinkerFluids.moltenGold, FluidValues.INGOT * 2)
                        .save(consumer, location(metalFolder + "gold/pressure_plate"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.CLOCK), TinkerFluids.moltenGold, FluidValues.INGOT * 4)
                        .save(consumer, location(metalFolder + "gold/clock"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GOLDEN_APPLE), TinkerFluids.moltenGold, FluidValues.INGOT * 8)
                        .save(consumer, location(metalFolder + "gold/apple"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GLISTERING_MELON_SLICE, Items.GOLDEN_CARROT), TinkerFluids.moltenGold, FluidValues.NUGGET * 8)
                        .save(consumer, location(metalFolder + "gold/produce"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.goldReinforcement), TinkerFluids.moltenGold, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "gold/reinforcement"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.goldBars), TinkerFluids.moltenGold, FluidValues.NUGGET * 3)
                        .save(consumer, location(metalFolder + "gold/nugget_3"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.goldPlatform), TinkerFluids.moltenGold, FluidValues.NUGGET * 10)
                        .save(consumer, location(metalFolder + "gold/platform"));
    // unique melting
    MeltingRecipeBuilder.melting(Ingredient.of(Items.GOLDEN_HORSE_ARMOR), TinkerFluids.moltenGold, FluidValues.INGOT * 7)
                        .save(consumer, location(metalFolder + "gold/horse_armor"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.ENCHANTED_GOLDEN_APPLE), TinkerFluids.moltenGold, FluidValues.METAL_BLOCK * 8)
                        .save(consumer, location(metalFolder + "gold/enchanted_apple"));
    // we directly add the recipe for nether gold ore instead of doing a sparse gold ore as we want to change the byproduct
    // if you add a sparse non-nether gold ore and need it meltable, let us know and we can add support
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.NETHER_GOLD_ORE), TinkerFluids.moltenGold, FluidValues.INGOT)
                        .addByproduct(TinkerFluids.moltenCopper.result(FluidValues.INGOT))
                        .setOre(OreRateType.METAL)
                        .save(consumer, location(metalFolder + "gold/nether_gold_ore"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.GILDED_BLACKSTONE), TinkerFluids.moltenGold, FluidValues.NUGGET * 3) // bit below average, ore rate will bring you bit above average
                        .addByproduct(TinkerFluids.moltenCopper.result(FluidValues.INGOT))
                        .setOre(OreRateType.METAL)
                        .save(consumer, location(metalFolder + "gold/gilded_blackstone"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.BELL), TinkerFluids.moltenGold, FluidValues.INGOT * 4) // bit arbitrary, I am happy to change the value if someone has a better one
                        .save(consumer, location(metalFolder + "gold/bell"));


    // copper melting
    MeltingRecipeBuilder.melting(Ingredient.of(
      Blocks.EXPOSED_COPPER, Blocks.WEATHERED_COPPER, Blocks.OXIDIZED_COPPER,
      Blocks.WAXED_COPPER_BLOCK, Blocks.WAXED_EXPOSED_COPPER, Blocks.WAXED_WEATHERED_COPPER, Blocks.WAXED_OXIDIZED_COPPER),
                                 TinkerFluids.moltenCopper, FluidValues.METAL_BLOCK)
                        .save(consumer, location(metalFolder + "copper/decorative_block"));
    MeltingRecipeBuilder.melting(Ingredient.of(
                                   Blocks.CUT_COPPER, Blocks.EXPOSED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER,
                                   Blocks.CUT_COPPER_STAIRS, Blocks.EXPOSED_CUT_COPPER_STAIRS, Blocks.WEATHERED_CUT_COPPER_STAIRS, Blocks.OXIDIZED_CUT_COPPER_STAIRS,
                                   Blocks.WAXED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER,
                                   Blocks.WAXED_CUT_COPPER_STAIRS, Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS),
                                 TinkerFluids.moltenCopper, FluidValues.NUGGET * 20)
                        .save(consumer, location(metalFolder + "copper/cut_block"));
    MeltingRecipeBuilder.melting(Ingredient.of(
                                   Blocks.CUT_COPPER_SLAB, Blocks.EXPOSED_CUT_COPPER_SLAB, Blocks.WEATHERED_CUT_COPPER_SLAB, Blocks.OXIDIZED_CUT_COPPER_SLAB,
                                   Blocks.WAXED_CUT_COPPER_SLAB, Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB),
                                 TinkerFluids.moltenCopper, FluidValues.NUGGET * 10)
                        .save(consumer, location(metalFolder + "copper/cut_slab"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.LIGHTNING_ROD), TinkerFluids.moltenCopper, FluidValues.INGOT * 3)
                        .save(consumer, location(metalFolder + "copper/lightning_rod"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerTags.Items.COPPER_PLATFORMS), TinkerFluids.moltenCopper, FluidValues.NUGGET * 10)
                        .save(consumer, location(metalFolder + "copper/platform"));

    // amethyst melting
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.TINTED_GLASS, TinkerCommons.clearTintedGlass), TinkerFluids.moltenAmethyst, FluidValues.GEM * 2)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK / 2))
                        .save(consumer, location(folder + "amethyst/tinted_glass"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.SPYGLASS), TinkerFluids.moltenAmethyst, FluidValues.GEM)
                        .addByproduct(TinkerFluids.moltenCopper.result(FluidValues.INGOT * 2))
                        .save(consumer, location(folder + "amethyst/spyglass"));

    // diamond melting
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.JUKEBOX), TinkerFluids.moltenDiamond, FluidValues.GEM)
                        .save(consumer, location(folder + "diamond/jukebox"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.ENCHANTING_TABLE), TinkerFluids.moltenDiamond, FluidValues.GEM * 2)
                        .addByproduct(TinkerFluids.moltenObsidian.result(FluidValues.GLASS_BLOCK * 4))
                        .save(consumer, location(folder + "diamond/enchanting_table"));
    // not the full copy cost as we have a discount recipe
    MeltingRecipeBuilder.melting(CompoundIngredient.of(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ItemTags.TRIM_TEMPLATES)), TinkerFluids.moltenDiamond, FluidValues.GEM * 5)
                        .save(consumer, location(folder + "diamond/smithing_template"));
    // unique melting
    MeltingRecipeBuilder.melting(Ingredient.of(Items.DIAMOND_HORSE_ARMOR), TinkerFluids.moltenDiamond, FluidValues.GEM * 7)
                        .save(consumer, location(folder + "diamond/horse_armor"));

    // netherite
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.LODESTONE), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "netherite/lodestone"));
    // armor
    int[] netheriteSizes = {FluidValues.NUGGET, FluidValues.GEM_SHARD};
    MeltingRecipeBuilder.melting(Ingredient.of(Items.NETHERITE_HELMET), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 5))
                        .save(consumer, location(metalFolder + "netherite/helmet"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.NETHERITE_CHESTPLATE), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 8))
                        .save(consumer, location(metalFolder + "netherite/chestplate"));
    // leggings use tag for the sake of paxels from mekanism
    MeltingRecipeBuilder.melting(Ingredient.of(getItemTag(TConstruct.MOD_ID, "melting/netherite/tools_costing_" + 7)), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 7))
                        .save(consumer, location(metalFolder + "netherite/leggings"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.NETHERITE_BOOTS), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 4))
                        .save(consumer, location(metalFolder + "netherite/boots"));
    // tools
    // axes uses tag for tools complement sickle
    MeltingRecipeBuilder.melting(Ingredient.of(getItemTag(TConstruct.MOD_ID, "melting/netherite/tools_costing_" + 3)), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 3))
                        .save(consumer, location(metalFolder + "netherite/axes"));
    // tools costing 2 only has vanilla sword and hoe, but its easier to use the tag then not for other metals so we use it here
    MeltingRecipeBuilder.melting(Ingredient.of(getItemTag(TConstruct.MOD_ID, "melting/netherite/tools_costing_" + 2)), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 2))
                        .save(consumer, location(metalFolder + "netherite/sword"));
    // shovels use tags for tools complement knife
    MeltingRecipeBuilder.melting(Ingredient.of(getItemTag(TConstruct.MOD_ID, "melting/netherite/tools_costing_" + 1)), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM))
                        .save(consumer, location(metalFolder + "netherite/shovel"));
    // tools complement compat - excavators and hammers
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation("tools_complement", "netherite_excavator")), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 11))
                        .save(withCondition(consumer, new ItemExistsCondition("tools_complement", "netherite_excavator")), location(metalFolder + "netherite/excavator"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(new ResourceLocation("tools_complement", "netherite_hammer")), TinkerFluids.moltenNetherite, FluidValues.INGOT)
                        .setDamagable(netheriteSizes)
                        .addByproduct(TinkerFluids.moltenDiamond.result(FluidValues.GEM * 13))
                        .save(withCondition(consumer, new ItemExistsCondition("tools_complement", "netherite_hammer")), location(metalFolder + "netherite/hammer"));

    // quartz
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.OBSERVER, Blocks.COMPARATOR, TinkerGadgets.quartzShuriken), TinkerFluids.moltenQuartz, FluidValues.GEM)
                        .save(consumer, location(folder + "quartz/gem_1"));
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.DAYLIGHT_DETECTOR), TinkerFluids.moltenQuartz, FluidValues.GEM * 3)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK * 3))
                        .save(consumer, location(folder + "quartz/daylight_detector"));

    // obsidian, if you are crazy i guess
    MeltingRecipeBuilder.melting(Ingredient.of(Blocks.BEACON), TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK * 3)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK * 5))
                        .save(consumer, location(folder + "obsidian/beacon"));

    // ender
    MeltingRecipeBuilder.melting(Ingredient.of(Items.END_CRYSTAL), TinkerFluids.moltenEnder, FluidValues.SLIMEBALL)
                        .addByproduct(TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK * 7))
                        .save(consumer, location(folder + "ender/end_crystal"));
    // it may be silky, but its still rose gold
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.silkyCloth), TinkerFluids.moltenRoseGold, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "rose_gold/silky_cloth"));

    // durability reinforcements
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.slimesteelReinforcement), TinkerFluids.moltenSlimesteel, FluidValues.NUGGET * 3)
                        .addByproduct(TinkerFluids.moltenObsidian.result(FluidValues.GLASS_PANE))
                        .save(consumer, location(metalFolder + "slimesteel/reinforcement"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.obsidianReinforcement), TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK)
                        .save(consumer, location(metalFolder + "obsidian/reinforcement"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerModifiers.cobaltReinforcement), TinkerFluids.moltenCobalt, FluidValues.INGOT)
                        .save(consumer, location(metalFolder + "cobalt/reinforcement"));

    MeltingRecipeBuilder.melting(Ingredient.of(TinkerCommons.cobaltPlatform), TinkerFluids.moltenCobalt, FluidValues.NUGGET * 10)
                        .save(consumer, location(metalFolder + "cobalt/platform"));

    // geode stuff
    crystalMelting(consumer, TinkerWorld.earthGeode, TinkerFluids.earthSlime, slimeFolder + "earth/");
    crystalMelting(consumer, TinkerWorld.skyGeode,   TinkerFluids.skySlime,   slimeFolder + "sky/");
    crystalMelting(consumer, TinkerWorld.enderGeode, TinkerFluids.enderSlime, slimeFolder + "ender/");
    // ichor is again special, melting via byproduct
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.ichorGeode), TinkerFluids.blazingBlood, FluidValues.ICHOR_BLAZING_BLOOD, 1.0f)
                        .addByproduct(TinkerFluids.ichor.result(FluidValues.ICHOR_BYPRODUCT))
                        .save(consumer, location(slimeFolder + "ichor/crystal"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.ichorGeode.getBlock()), TinkerFluids.blazingBlood, FluidValues.ICHOR_BLAZING_BLOOD * 4, 2.0f)
                        .addByproduct(TinkerFluids.ichor.result(FluidValues.ICHOR_BYPRODUCT * 4))
                        .save(consumer, location(slimeFolder + "ichor/crystal_block"));
    for (BudSize bud : BudSize.values()) {
      int size = bud.getSize();
      MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.ichorGeode.getBud(bud)), TinkerFluids.blazingBlood, FluidValues.ICHOR_BLAZING_BLOOD * size, (size + 1) / 2f)
                          .addByproduct(TinkerFluids.ichor.result(FluidValues.ICHOR_BYPRODUCT * size))
                          .setOre(OreRateType.GEM)
                          .save(consumer, location(slimeFolder + "ichor/bud_" + bud.getName()));
    }
    // clusters
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.steelCluster), TinkerFluids.moltenSteel, FluidValues.NUGGET * 4, 5/2f)
      .save(consumer, location(folder + "metal/steel/cluster"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.knightmetalCluster), TinkerFluids.moltenKnightmetal, FluidValues.NUGGET * 4, 5/2f)
      .save(consumer, location(folder + "metal/knightmetal/cluster"));

    // recycle saplings
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.slimeSapling.get(FoliageType.EARTH)), TinkerFluids.earthSlime, FluidValues.SLIMEBALL)
                        .save(consumer, location(slimeFolder + "earth/sapling"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.slimeSapling.get(FoliageType.SKY)), TinkerFluids.skySlime, FluidValues.SLIMEBALL)
                        .save(consumer, location(slimeFolder + "sky/sapling"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.slimeSapling.get(FoliageType.ENDER)), TinkerFluids.enderSlime, FluidValues.SLIMEBALL)
                        .save(consumer, location(slimeFolder + "ender/sapling"));

    // honey
    MeltingRecipeBuilder.melting(Ingredient.of(Items.HONEY_BLOCK), TinkerFluids.honey, FluidValues.BOTTLE * 4)
                        .save(consumer, location(slimeFolder + "honey_block"));
    // soup
    MeltingRecipeBuilder.melting(Ingredient.of(Items.BEETROOT), TinkerFluids.beetrootSoup, FluidValues.BOTTLE / 5, 1)
                        .save(consumer, location(slimeFolder + "beetroot_soup"));
    MeltingRecipeBuilder.melting(Ingredient.of(Tags.Items.MUSHROOMS), TinkerFluids.mushroomStew, FluidValues.BOTTLE / 2, 1)
                        .save(consumer, location(slimeFolder + "mushroom_stew"));

    // fuels
    MeltingFuelBuilder.solid(800)
                      .save(consumer, location(folder + "fuel/solid"));
    MeltingFuelBuilder.fuel(new FluidStack(Fluids.LAVA, 50), 100)
                      .save(consumer, location(folder + "fuel/lava"));
    MeltingFuelBuilder.fuel(new FluidStack(TinkerFluids.blazingBlood.get(), 50), 150)
                      .save(consumer, location(folder + "fuel/blaze"));
  }


  private void addAlloyRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "smeltery/alloys/";

    // alloy recipes are in terms of ingots

    // tier 3

    // slimesteel: 1 iron + 1 skyslime + 1 seared brick = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenSlimesteel, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenIron.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.skySlime.ingredient(FluidValues.SLIMEBALL))
                      .addInput(TinkerFluids.searedStone.ingredient(FluidValues.BRICK))
                      .save(consumer, prefix(TinkerFluids.moltenSlimesteel, folder));

    // amethyst bronze: 1 copper + 1 amethyst = 1
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenAmethystBronze, FluidValues.INGOT)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenAmethyst.ingredient(FluidValues.GEM))
                      .save(consumer, prefix(TinkerFluids.moltenAmethystBronze, folder));

    // rose gold: 1 copper + 1 gold = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenRoseGold, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenGold.ingredient(FluidValues.INGOT))
                      .save(consumer, prefix(TinkerFluids.moltenRoseGold, folder));
    // pig iron: 1 iron + 2 blood + 1 honey = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenPigIron, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenIron.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.meatSoup.ingredient(FluidValues.SLIMEBALL * 2))
                      .addInput(TinkerFluids.honey.ingredient(FluidValues.BOTTLE))
                      .save(consumer, prefix(TinkerFluids.moltenPigIron, folder));
    // obsidian: 1 lava = 1 obsidian, but require some water is present
    // water being a catalyst makes this friendlier in the nether as we just need 1 bottle instead of collecting a bunch
    // note this is not a progression break, as the same tier lets you make other alloys like amethyst bronze
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK / 10)
                      .addCatalyst(FluidIngredient.of(Fluids.WATER, FluidValues.BOTTLE))
                      .addInput(Fluids.LAVA, FluidType.BUCKET_VOLUME / 10)
                      .save(consumer, prefix(TinkerFluids.moltenObsidian, folder));

    // tier 4

    // cinderslime: 1 gold + 1 ichor + 1 scorched stone = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenCinderslime, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenGold.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.ichor.ingredient(FluidValues.SLIMEBALL))
                      .addInput(TinkerFluids.scorchedStone.ingredient(FluidValues.BRICK))
                      .save(consumer, prefix(TinkerFluids.moltenCinderslime, folder));

    // queens slime: 1 cobalt + 1 gold + 1 magma cream = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenQueensSlime, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCobalt.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenGold.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.magma.ingredient(FluidValues.SLIMEBALL))
                      .save(consumer, prefix(TinkerFluids.moltenQueensSlime, folder));

    // manyullyn: 3 cobalt + 1 debris = 3
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenManyullyn, FluidValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCobalt.ingredient(FluidValues.INGOT * 3))
                      .addInput(TinkerFluids.moltenDebris.ingredient(FluidValues.INGOT))
                      .save(consumer, prefix(TinkerFluids.moltenManyullyn, folder));

    // heptazion: 2 copper + 1 cobalt + 1 quartz = 2
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenHepatizon, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT * 2))
                      .addInput(TinkerFluids.moltenCobalt.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenQuartz.ingredient(FluidValues.GEM))
                      .save(consumer, prefix(TinkerFluids.moltenHepatizon, folder));

    // netherrite: 4 debris + 4 gold = 1 (why is this so dense vanilla?)
    ConditionalRecipe.builder()
                     .addCondition(ConfigEnabledCondition.CHEAPER_NETHERITE_ALLOY)
                     .addRecipe(
                       AlloyRecipeBuilder.alloy(TinkerFluids.moltenNetherite, FluidValues.NUGGET)
                                         .addInput(TinkerFluids.moltenDebris.ingredient(FluidValues.NUGGET * 4))
                                         .addInput(TinkerFluids.moltenGold.ingredient(FluidValues.NUGGET * 2))::save)
                     .addCondition(TrueCondition.INSTANCE) // fallback
                     .addRecipe(
                       AlloyRecipeBuilder.alloy(TinkerFluids.moltenNetherite, FluidValues.NUGGET)
                                         .addInput(TinkerFluids.moltenDebris.ingredient(FluidValues.NUGGET * 4))
                                         .addInput(TinkerFluids.moltenGold.ingredient(FluidValues.NUGGET * 4))::save)
                     .build(consumer, prefix(TinkerFluids.moltenNetherite, folder));


    // tier 3 compat
    Consumer<FinishedRecipe> wrapped;

    // bronze
    wrapped = withCondition(consumer, tagCondition("ingots/tin"), new OrCondition(ConfigEnabledCondition.ALLOW_INGOTLESS_ALLOYS, tagCondition("ingots/bronze")));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenBronze, FluidValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT * 3))
                      .addInput(TinkerFluids.moltenTin.ingredient(FluidValues.INGOT))
                      .save(wrapped, prefix(TinkerFluids.moltenBronze, folder));

    // brass
    wrapped = withCondition(consumer, tagCondition("ingots/zinc"), new OrCondition(ConfigEnabledCondition.ALLOW_INGOTLESS_ALLOYS, tagCondition("ingots/brass")));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenBrass, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenZinc.ingredient(FluidValues.INGOT))
                      .save(wrapped, prefix(TinkerFluids.moltenBrass, folder));

    // electrum
    wrapped = withCondition(consumer, tagCondition("ingots/silver"), new OrCondition(ConfigEnabledCondition.ALLOW_INGOTLESS_ALLOYS, tagCondition("ingots/electrum")));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenElectrum, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenGold.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenSilver.ingredient(FluidValues.INGOT))
                      .save(wrapped, prefix(TinkerFluids.moltenElectrum, folder));

    // invar
    wrapped = withCondition(consumer, tagCondition("ingots/nickel"), new OrCondition(ConfigEnabledCondition.ALLOW_INGOTLESS_ALLOYS, tagCondition("ingots/invar")));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenInvar, FluidValues.INGOT * 3)
                      .addInput(TinkerFluids.moltenIron.ingredient(FluidValues.INGOT * 2))
                      .addInput(TinkerFluids.moltenNickel.ingredient(FluidValues.INGOT))
                      .save(wrapped, prefix(TinkerFluids.moltenInvar, folder));

    // constantan
    wrapped = withCondition(consumer, tagCondition("ingots/nickel"), new OrCondition(ConfigEnabledCondition.ALLOW_INGOTLESS_ALLOYS, tagCondition("ingots/constantan")));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenConstantan, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT))
                      .addInput(TinkerFluids.moltenNickel.ingredient(FluidValues.INGOT))
                      .save(wrapped, prefix(TinkerFluids.moltenConstantan, folder));

    // pewter
    ICondition lead = tagCondition("ingots/lead");
    ICondition tin = tagCondition("ingots/tin");
    ConditionalRecipe.builder()
      // if we have both tin and lead, do the combined recipe. Ratio is from Metalborn/Allomancy
      .addCondition(new AndCondition(lead, tin))
      // ratio from Metalborn/Allomancy
      .addRecipe(AlloyRecipeBuilder.alloy(TinkerFluids.moltenPewter, FluidValues.INGOT * 4)
        .addInput(TinkerFluids.moltenTin.ingredient(FluidValues.INGOT * 3))
        .addInput(TinkerFluids.moltenLead.ingredient(FluidValues.INGOT))::save)

      // otherwise, substitute iron for the missing part
      // metalborn does pewter without lead
      .addCondition(tin)
      // ratio from Metalborn
      .addRecipe(AlloyRecipeBuilder.alloy(TinkerFluids.moltenPewter, FluidValues.INGOT * 4)
        .addInput(TinkerFluids.moltenTin.ingredient(FluidValues.INGOT * 3))
        .addInput(TinkerFluids.moltenIron.ingredient(FluidValues.INGOT))::save)

      // Edilon does pewter without tin
      .addCondition(lead)
      // ratio from Edilon mod
      .addRecipe(AlloyRecipeBuilder.alloy(TinkerFluids.moltenPewter, FluidValues.INGOT * 2)
        .addInput(TinkerFluids.moltenIron.ingredient(FluidValues.INGOT))
        .addInput(TinkerFluids.moltenLead.ingredient(FluidValues.INGOT))::save)

      .build(withCondition(consumer, new OrCondition(ConfigEnabledCondition.ALLOW_INGOTLESS_ALLOYS, tagCondition("ingots/pewter"))), prefix(TinkerFluids.moltenPewter, folder));

    // thermal alloys
    Function<String,ICondition> fluidTagLoaded = name -> new TagFilledCondition<>(Registries.FLUID, commonResource(name));
    Function<String,TagKey<Fluid>> fluidTag = name -> FluidTags.create(commonResource(name));
    // enderium
    wrapped = withCondition(consumer, tagCondition("ingots/enderium"), tagCondition("ingots/lead"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenEnderium, FluidValues.INGOT * 2)
                      .addInput(TinkerFluids.moltenLead.ingredient(FluidValues.INGOT * 3))
                      .addInput(TinkerFluids.moltenDiamond.ingredient(FluidValues.GEM))
                      .addInput(TinkerFluids.moltenEnder.ingredient(FluidValues.SLIMEBALL * 2))
                      .save(wrapped, prefix(TinkerFluids.moltenEnderium, folder));
    // lumium
    wrapped = withCondition(consumer, tagCondition("ingots/lumium"), tagCondition("ingots/tin"), tagCondition("ingots/silver"), fluidTagLoaded.apply("glowstone"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenLumium, FluidValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenTin.ingredient(FluidValues.INGOT * 3))
                      .addInput(TinkerFluids.moltenSilver.ingredient(FluidValues.INGOT))
                      .addInput(FluidIngredient.of(fluidTag.apply("glowstone"), FluidValues.SLIMEBALL * 2))
                      .save(wrapped, prefix(TinkerFluids.moltenLumium, folder));
    // signalum
    wrapped = withCondition(consumer, tagCondition("ingots/signalum"), tagCondition("ingots/copper"), tagCondition("ingots/silver"), fluidTagLoaded.apply("redstone"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenSignalum, FluidValues.INGOT * 4)
                      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT * 3))
                      .addInput(TinkerFluids.moltenSilver.ingredient(FluidValues.INGOT))
                      .addInput(FluidIngredient.of(fluidTag.apply("redstone"), 400))
                      .save(wrapped, prefix(TinkerFluids.moltenSignalum, folder));

    // refined obsidian, note glowstone is done as a composite
    wrapped = withCondition(consumer, tagCondition("ingots/refined_obsidian"), tagCondition("ingots/osmium"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenRefinedObsidian, FluidValues.INGOT)
                      .addInput(TinkerFluids.moltenObsidian.ingredient(FluidValues.GLASS_PANE))
                      .addInput(TinkerFluids.moltenDiamond.ingredient(FluidValues.GEM))
                      .addInput(TinkerFluids.moltenOsmium.ingredient(FluidValues.INGOT))
                      .save(wrapped, prefix(TinkerFluids.moltenRefinedObsidian, folder));

    // nicrosil
    wrapped = withCondition(consumer, tagCondition("ingots/nicrosil"));
    ConditionalRecipe.builder()
      // if we have both chromium and nickel, can do the proper recipe
      .addCondition(new AndCondition(tagCondition("ingots/chromium"), tagCondition("ingots/nickel")))
      .addRecipe(AlloyRecipeBuilder.alloy(TinkerFluids.moltenNicrosil, FluidValues.INGOT * 4)
        .addInput(TinkerFluids.moltenNickel.ingredient(FluidValues.INGOT * 2))
        .addInput(TinkerFluids.moltenChromium.ingredient(FluidValues.INGOT))
        .addInput(TinkerFluids.moltenQuartz.ingredient(FluidValues.GEM))::save)

      // if chromium is missing, sub in tin per metalborn, can do the proper recipe
      .addCondition(new AndCondition(tagCondition("ingots/tin"), tagCondition("ingots/nickel")))
      .addRecipe(AlloyRecipeBuilder.alloy(TinkerFluids.moltenNicrosil, FluidValues.INGOT * 4)
        .addInput(TinkerFluids.moltenNickel.ingredient(FluidValues.INGOT * 2))
        .addInput(TinkerFluids.moltenTin.ingredient(FluidValues.INGOT))
        .addInput(TinkerFluids.moltenQuartz.ingredient(FluidValues.GEM))::save)

      // nickel missing? use more chromium and sub in a bit of iron
      .addCondition(tagCondition("ingots/chromium"))
      .addRecipe(AlloyRecipeBuilder.alloy(TinkerFluids.moltenNicrosil, FluidValues.INGOT * 4)
        .addInput(TinkerFluids.moltenChromium.ingredient(FluidValues.INGOT * 2))
        .addInput(TinkerFluids.moltenIron.ingredient(FluidValues.INGOT))
        .addInput(TinkerFluids.moltenQuartz.ingredient(FluidValues.GEM))::save)

      // no nickel or chromium? just use tin and iron per metalborn
      .addCondition(tagCondition("ingots/tin"))
      .addRecipe(AlloyRecipeBuilder.alloy(TinkerFluids.moltenNicrosil, FluidValues.INGOT * 4)
        .addInput(TinkerFluids.moltenTin.ingredient(FluidValues.INGOT * 2))
        .addInput(TinkerFluids.moltenIron.ingredient(FluidValues.INGOT))
        .addInput(TinkerFluids.moltenQuartz.ingredient(FluidValues.GEM))::save)

      .build(wrapped, prefix(TinkerFluids.moltenNicrosil, folder));

    // duralumin
    wrapped = withCondition(consumer, tagCondition("ingots/duralumin"), tagCondition("ingots/aluminum"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenDuralumin, FluidValues.INGOT * 4)
      .addInput(TinkerFluids.moltenAluminum.ingredient(FluidValues.INGOT * 3))
      .addInput(TinkerFluids.moltenCopper.ingredient(FluidValues.INGOT))
      .save(wrapped, prefix(TinkerFluids.moltenDuralumin, folder));

    // bendalloy
    wrapped = withCondition(consumer, tagCondition("ingots/bendalloy"), tagCondition("ingots/tin"), tagCondition("ingots/lead"), tagCondition("ingots/cadmium"));
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenBendalloy, FluidValues.INGOT * 4)
      .addInput(TinkerFluids.moltenTin.ingredient(FluidValues.INGOT * 2))
      .addInput(TinkerFluids.moltenLead.ingredient(FluidValues.INGOT))
      .addInput(TinkerFluids.moltenCadmium.ingredient(FluidValues.INGOT))
      .save(wrapped, prefix(TinkerFluids.moltenBendalloy, folder));
  }

  private void addEntityMeltingRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "smeltery/entity_melting/";
    String headFolder = "smeltery/entity_melting/heads/";

    // meat soup just comes from edible creatures
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.CHICKEN, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.RABBIT, EntityType.SHEEP, EntityType.GOAT, EntityType.COD, EntityType.HOGLIN, EntityType.SALMON, EntityType.TROPICAL_FISH),
                                       TinkerFluids.meatSoup.result(FluidValues.BOWL / 5)).save(consumer, location(folder + "meat_soup"));

    // zombies give iron, they drop it sometimes
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ZOMBIE, EntityType.HUSK, EntityType.ZOMBIE_HORSE), TinkerFluids.moltenIron.result(FluidValues.NUGGET), 4)
                              .save(consumer, location(folder + "zombie"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.ZOMBIE_HEAD, TinkerWorld.heads.get(TinkerHeadType.HUSK)), TinkerFluids.moltenIron, FluidValues.INGOT)
                        .save(consumer, location(headFolder + "zombie"));
    // drowned drop copper instead
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.DROWNED), TinkerFluids.moltenCopper.result(FluidValues.NUGGET), 4)
                              .save(consumer, location(folder + "drowned"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.heads.get(TinkerHeadType.DROWNED)), TinkerFluids.moltenCopper, FluidValues.INGOT)
                        .save(consumer, location(headFolder + "drowned"));
    // and piglins gold
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.ZOMBIFIED_PIGLIN), TinkerFluids.moltenGold.result(FluidValues.NUGGET), 4)
                              .save(consumer, location(folder + "piglin"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.PIGLIN_HEAD, TinkerWorld.heads.get(TinkerHeadType.PIGLIN_BRUTE), TinkerWorld.heads.get(TinkerHeadType.ZOMBIFIED_PIGLIN)), TinkerFluids.moltenGold, FluidValues.INGOT)
                        .save(consumer, location(headFolder + "piglin"));

    // melt spiders into venom
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SPIDER, EntityType.CAVE_SPIDER), TinkerFluids.venom.result(FluidValues.BOTTLE / 10), 2)
                              .save(consumer, location(folder + "spider"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.heads.get(TinkerHeadType.SPIDER), TinkerWorld.heads.get(TinkerHeadType.CAVE_SPIDER)), TinkerFluids.venom, FluidValues.SLIMEBALL * 2)
                        .save(consumer, location(headFolder + "spider"));

    // creepers are based on explosives, tnt is explosive, tnt is made from sand, sand melts into glass. therefore, creepers melt into glass
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.CREEPER), TinkerFluids.moltenGlass.result(FluidValues.GLASS_BLOCK / 20), 2)
                              .save(consumer, location(folder + "creeper"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.CREEPER_HEAD), TinkerFluids.moltenGlass, FluidType.BUCKET_VOLUME / 4)
                        .save(consumer, location(headFolder + "creeper"));

    // ghasts melt into potions, because ghast tears or something, idk
    // axolotls like regen too, you monster!
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.GHAST, EntityType.AXOLOTL), PotionFluidType.potionResult(Potions.REGENERATION, FluidValues.BOTTLE / 5), 2)
                              .save(consumer, location(folder + "regeneration"));
    // likewise, phantoms give slow falling
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.PHANTOM), PotionFluidType.potionResult(Potions.SLOW_FALLING, FluidValues.BOTTLE / 5), 4)
                              .save(consumer, location(folder + "phantom"));
    // its not quite levitation, but close enough
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SHULKER), PotionFluidType.potionResult(Potions.LEAPING, FluidValues.BOTTLE / 10), 3)
                              .save(consumer, location(folder + "shulker"));
    // frogs leap too
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.FROG), PotionFluidType.potionResult(Potions.LEAPING, FluidValues.BOTTLE / 5), 2)
                              .save(consumer, location(folder + "frog"));
    // just making brewing recipes now
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SQUID, EntityType.PUFFERFISH), PotionFluidType.potionResult(Potions.WATER_BREATHING, FluidValues.BOTTLE / 5), 2)
                              .save(consumer, location(folder + "water_breathing"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.BAT, EntityType.GLOW_SQUID), PotionFluidType.potionResult(Potions.NIGHT_VISION, FluidValues.BOTTLE / 5), 2)
                              .save(consumer, location(folder + "night_vision"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.TURTLE), PotionFluidType.potionResult(Potions.TURTLE_MASTER, FluidValues.BOTTLE / 10), 3)
                              .save(consumer, location(folder + "turtle"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.DOLPHIN, EntityType.FOX, EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.LLAMA, EntityType.TRADER_LLAMA, EntityType.OCELOT),
                                       PotionFluidType.potionResult(Potions.SWIFTNESS, FluidValues.BOTTLE / 5), 2)
                              .save(consumer, location(folder + "swiftness"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.STRIDER), PotionFluidType.potionResult(Potions.FIRE_RESISTANCE, FluidValues.BOTTLE / 5), 4)
                              .save(consumer, location(folder + "strider"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.POLAR_BEAR, EntityType.PANDA, EntityType.RAVAGER, EntityType.ZOGLIN), PotionFluidType.potionResult(Potions.STRENGTH, FluidValues.BOTTLE / 5), 4)
                              .save(consumer, location(folder + "strength"));

    // melt skeletons to get the milk out
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityIngredient.of(EntityTypeTags.SKELETONS), EntityIngredient.of(EntityType.SKELETON_HORSE)),
                                       new FluidStack(ForgeMod.MILK.get(), FluidType.BUCKET_VOLUME / 10))
                              .save(consumer, location(folder + "skeletons"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, TinkerWorld.heads.get(TinkerHeadType.STRAY)), ForgeMod.MILK.get(), FluidType.BUCKET_VOLUME / 4)
                        .save(consumer, location(headFolder + "skeleton"));

    // slimes melt into slime, shocker
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SLIME), TinkerFluids.earthSlime.result(FluidValues.SLIMEBALL / 10))
                              .save(consumer, location(folder + "slime"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.skySlimeEntity.get()), TinkerFluids.skySlime.result(FluidValues.SLIMEBALL / 10))
                              .save(consumer, prefix(TinkerWorld.skySlimeEntity, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.enderSlimeEntity.get()), TinkerFluids.enderSlime.result(FluidValues.SLIMEBALL / 10))
                              .save(consumer, prefix(TinkerWorld.enderSlimeEntity, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerWorld.terracubeEntity.get()), TinkerFluids.moltenClay.result(FluidValues.SLIMEBALL / 10))
                              .save(consumer, prefix(TinkerWorld.terracubeEntity, folder));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.MAGMA_CUBE), TinkerFluids.magma.result(FluidValues.SLIMEBALL / 10))
                              .save(consumer, location(folder + "magma_cube"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.BEE), TinkerFluids.honey.result(FluidValues.BOTTLE / 10))
                              .save(consumer, location(folder + "bee"));

    // iron golems can be healed using an iron ingot 25 health
    // 4 * 9 gives 36, which is larger
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.IRON_GOLEM), TinkerFluids.moltenIron.result(FluidValues.NUGGET), 4)
                              .save(consumer, location(folder + "iron_golem"));
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SNOW_GOLEM), new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME / 10))
                              .save(consumer, location(folder + "snow_golem"));

    // "melt" blazes to get fuel
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.BLAZE), TinkerFluids.blazingBlood.result(FluidType.BUCKET_VOLUME / 50), 2)
                              .save(consumer, location(folder + "blaze"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.heads.get(TinkerHeadType.BLAZE)), TinkerFluids.blazingBlood.result(FluidType.BUCKET_VOLUME / 10), 1000, IMeltingRecipe.calcTime(1500, 1.0f))
                        .save(consumer, location(headFolder + "blaze"));

    // guardians are rock, seared stone is rock, don't think about it too hard
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN), TinkerFluids.searedStone.result(FluidValues.BRICK / 5), 4)
                              .save(consumer, location(folder + "guardian"));
    // silverfish also seem like rock, sorta?
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.SILVERFISH), TinkerFluids.searedStone.result(FluidValues.BRICK / 5), 2)
                              .save(consumer, location(folder + "silverfish"));

    // villagers melt into emerald, but they die quite quick
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerTags.EntityTypes.VILLAGERS), TinkerFluids.moltenEmerald.result(FluidValues.GEM_SHARD), 5)
                              .save(consumer, location(folder + "villager"));
    // illagers are more resistant, they resist the villager culture afterall
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(TinkerTags.EntityTypes.ILLAGERS), TinkerFluids.moltenEmerald.result(FluidValues.GEM_SHARD), 2)
                              .save(consumer, location(folder + "illager"));

    // melt ender for the molten ender
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_DRAGON),
                                       TinkerFluids.moltenEnder.result(FluidValues.SLIMEBALL / 10), 2)
                              .save(consumer, location(folder + "ender"));
    MeltingRecipeBuilder.melting(Ingredient.of(TinkerWorld.heads.get(TinkerHeadType.ENDERMAN)), TinkerFluids.moltenEnder, FluidValues.SLIMEBALL * 2)
                        .save(consumer, location(headFolder + "enderman"));
    MeltingRecipeBuilder.melting(Ingredient.of(Items.DRAGON_HEAD), TinkerFluids.moltenEnder, FluidValues.SLIMEBALL * 4)
                        .save(consumer, location(headFolder + "ender_dragon"));

    // if you can get him to stay, wither is a source of free liquid soul
    EntityMeltingRecipeBuilder.melting(EntityIngredient.of(EntityType.WITHER), TinkerFluids.liquidSoul.result(FluidValues.GLASS_BLOCK / 20), 2)
                              .save(consumer, location(folder + "wither"));
  }

  @Override
  public SmelteryRecipeBuilder fluid(Consumer<FinishedRecipe> consumer, String name, FluidObject<?> fluid) {
    return ISmelteryRecipeHelper.super.fluid(consumer, name, fluid).castingFolder("smeltery/casting").meltingFolder("smeltery/melting");
  }

  /** Creates a metal from a tag */
  public SmelteryRecipeBuilder metal(Consumer<FinishedRecipe> consumer, String name, TagKey<Fluid> fluid) {
    return SmelteryRecipeBuilder.fluid(consumer, location(name), fluid).castingFolder("smeltery/casting/metal").meltingFolder("smeltery/melting/metal");
  }

  /** Creates a smeltery builder for a metal fluid */
  public SmelteryRecipeBuilder metal(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid) {
    return molten(consumer, fluid).castingFolder("smeltery/casting/metal").meltingFolder("smeltery/melting/metal");
  }

  /** Handles tag based melting and casting recipes using the builder */
  private void addTagRecipes(Consumer<FinishedRecipe> consumer) {
    // tools complement support - they want us to use the cost 1 tag for shovels, and an excavator and hammer
    ToolItemMelting EXCAVATOR = new ToolItemMelting(11, "tools_complement", "excavator");
    ToolItemMelting HAMMER = new ToolItemMelting(13, "tools_complement", "hammer");
    CommonRecipe[] TOOLS_COMPLEMENT = { SHOVEL_PLUS, SWORD, AXES, EXCAVATOR, HAMMER };
    // mekanism support - they want us to use the cost 7 tag for leggings, and a shield
    ToolItemMelting MEKANISM_SHIELD = new ToolItemMelting(6, "mekanism", "shield");
    CommonRecipe[] MEKANISM_ARMOR = {HELMET, CHESTPLATE, LEGGINGS_PLUS, BOOTS, MEKANISM_SHIELD};
    CommonRecipe FLAKES = new MetalMelting(1/3f, "allomancy", "flakes");

    // metal ores
    // copper has the brush for cost 1, so always keep that one around
    metal(consumer, TinkerFluids.moltenCopper).ore(Byproduct.SMALL_GOLD   ).metal().dust().plate().gear().coin().sheetmetal().geore().oreberry().wire().common(SWORD, AXES, EXCAVATOR, HAMMER, FLAKES).common(ARMOR).toolCostMelting(1, "shovel", false);
    // iron has both railcraft spikemaul and tools complement excavator at cost 11
    metal(consumer, TinkerFluids.moltenIron  ).ore(Byproduct.STEEL        ).metal().dust().plate().gear().coin().sheetmetal().geore().oreberry().minecraftTools().toolCostMelting(11, "tools_costing_11").common(HAMMER, FLAKES).rod();
    metal(consumer, TinkerFluids.moltenCobalt).ore(Byproduct.SMALL_DIAMOND).metal().dust();
    metal(consumer, TinkerFluids.moltenSteel ).metal().dust().plate().gear().coin().sheetmetal().common(SHOVEL_PLUS, SWORD, AXES, MEKANISM_SHIELD, FLAKES).common(ARMOR_PLUS).wire().rod().rawOre(Byproduct.IRON)
      .toolItemMelting(11, "railcraft", "spike_maul")
      .melting(1/9f, "raw_nugget", 1/2f, false);
    // gold ore does non-standard byproduct handling, as it wants sparse gold ore to have a different byproduct, hence moving byproducts so we don't have ores for the metal call
    metal(consumer, TinkerFluids.moltenGold).metal().ore(Byproduct.COBALT).dust().plate().gear().coin().sheetmetal().geore().oreberry().minecraftTools("golden", true).common(EXCAVATOR, HAMMER, FLAKES).rawOre().singularOre(2).denseOre(6);
    // gem ores
    // diamond has both railcraft spikemaul and tools complement excavator at cost 11
    molten(consumer, TinkerFluids.moltenDiamond).ore(Byproduct.DEBRIS ).largeGem().dust().gear().geore().minecraftTools("diamond", true).toolCostMelting(11, "tools_costing_11").common(HAMMER);
    molten(consumer, TinkerFluids.moltenEmerald).ore(Byproduct.DIAMOND).largeGem().dust().gear().geore();
    molten(consumer, TinkerFluids.moltenQuartz ).ore(Byproduct.IRON   ).smallGem().dust().gear().geore();
    molten(consumer, TinkerFluids.moltenAmethyst).smallGem();

    // standard alloys
    metal(consumer, TinkerFluids.moltenNetherite).metal().dust().plate().gear().coin(); // handles tools elsewhere due to byproducts
    // tier 3
    metal(consumer, TinkerFluids.moltenSlimesteel    ).metal();
    metal(consumer, TinkerFluids.moltenAmethystBronze).metal().dust();
    metal(consumer, TinkerFluids.moltenRoseGold      ).metal().dust().plate().coin().gear();
    metal(consumer, TinkerFluids.moltenPigIron       ).metal();
    // tier 4
    metal(consumer, TinkerFluids.moltenManyullyn  ).metal();
    metal(consumer, TinkerFluids.moltenHepatizon  ).metal();
    metal(consumer, TinkerFluids.moltenCinderslime).metal();
    metal(consumer, TinkerFluids.moltenQueensSlime).metal();
    String tf = "twilightforest";
    CommonRecipe tfHelmet     = new ToolItemMelting(5, tf, "helmet");
    CommonRecipe tfChestplate = new ToolItemMelting(8, tf, "chestplate");
    CommonRecipe tfBoots      = new ToolItemMelting(4, tf, "boots");
    CommonRecipe tfSword      = new ToolItemMelting(2, tf, "sword");
    metal(consumer, TinkerFluids.moltenKnightmetal).metal().common(AXES, tfHelmet, tfChestplate, LEGGINGS_PLUS, tfBoots, tfSword)
      .metalMelting(4, tf, "ring", false)
      .itemMelting(16, tf, "block_and_chain", true)
      // not using a traditional ore recipe as there isn't a reasonable byproduct, plus crafting a 3x3 doesn't feel like enough for a 3 nugget bonus
      .melting(1, "raw", "raw_materials", false, true)
      .melting(1/9f, "raw_nugget", 1/2f, false);

    // compat ores
    metal(consumer, TinkerFluids.moltenTin     ).ore(Byproduct.NICKEL, Byproduct.COPPER).optional().metal().dust().oreberry().plate().gear().coin().common(TOOLS_COMPLEMENT).common(ARMOR).common(FLAKES);
    metal(consumer, TinkerFluids.moltenAluminum).ore(Byproduct.IRON                    ).optional().metal().dust().oreberry().plate().gear().coin().sheetmetal().wire().rod().common(FLAKES);
    metal(consumer, TinkerFluids.moltenLead    ).ore(Byproduct.SILVER, Byproduct.GOLD  ).optional().metal().dust().oreberry().plate().gear().coin().common(TOOLS_COMPLEMENT).common(ARMOR).common(FLAKES).sheetmetal().wire();
    metal(consumer, TinkerFluids.moltenSilver  ).ore(Byproduct.LEAD, Byproduct.GOLD    ).optional().metal().dust().oreberry().plate().gear().coin().common(TOOLS_COMPLEMENT).common(ARMOR).common(FLAKES).sheetmetal();
    metal(consumer, TinkerFluids.moltenNickel  ).ore(Byproduct.PLATINUM, Byproduct.IRON).optional().metal().dust().oreberry().plate().gear().coin().common(TOOLS_COMPLEMENT).common(ARMOR).sheetmetal();
    metal(consumer, TinkerFluids.moltenZinc    ).ore(Byproduct.TIN, Byproduct.COPPER   ).optional().metal().dust().oreberry().plate().gear().geore().common(FLAKES);
    metal(consumer, TinkerFluids.moltenPlatinum).ore(Byproduct.GOLD                    ).optional().metal().dust();
    metal(consumer, TinkerFluids.moltenTungsten).ore(Byproduct.PLATINUM, Byproduct.GOLD).optional().metal().dust();
    metal(consumer, TinkerFluids.moltenChromium).ore(Byproduct.ALUMINUM, Byproduct.IRON).optional().metal().dust().common(FLAKES);
    metal(consumer, TinkerFluids.moltenCadmium ).ore(Byproduct.LEAD, Byproduct.COPPER  ).optional().metal().dust().common(FLAKES);
    metal(consumer, TinkerFluids.moltenOsmium  ).ore(Byproduct.IRON                    ).optional().metal().dust().oreberry().common(TOOLS).common(MEKANISM_ARMOR);
    metal(consumer, TinkerFluids.moltenUranium ).ore(Byproduct.LEAD, Byproduct.COPPER  ).optional().metal().dust().oreberry().plate().gear().coin().sheetmetal();
    // compat alloys
    metal(consumer, TinkerFluids.moltenBronze    ).optional().metal().dust().plate().gear().coin().common(TOOLS_COMPLEMENT).common(MEKANISM_ARMOR).common(FLAKES).rawOre(Byproduct.COPPER);
    metal(consumer, TinkerFluids.moltenBrass     ).optional().metal().dust().plate().gear().common(FLAKES).rawOre(Byproduct.ZINC, Byproduct.COPPER);
    metal(consumer, TinkerFluids.moltenElectrum  ).optional().metal().dust().plate().gear().rawOre(Byproduct.SILVER, Byproduct.GOLD).coin().common(TOOLS_COMPLEMENT).common(ARMOR).sheetmetal().wire().common(FLAKES);
    metal(consumer, TinkerFluids.moltenInvar     ).optional().metal().dust().plate().gear().coin().common(TOOLS_COMPLEMENT).common(ARMOR);
    metal(consumer, TinkerFluids.moltenConstantan).optional().metal().dust().plate().gear().coin().common(TOOLS_COMPLEMENT).common(ARMOR).sheetmetal();
    metal(consumer, TinkerFluids.moltenPewter    ).optional().metal().dust().common(FLAKES).rawOre(Byproduct.TIN, Byproduct.LEAD, Byproduct.IRON);
    metal(consumer, TinkerFluids.moltenNicrosil  ).optional().metal().dust().common(FLAKES).rawOre(Byproduct.CHROMIUM);
    metal(consumer, TinkerFluids.moltenDuralumin ).optional().metal().dust().common(FLAKES).rawOre(Byproduct.ALUMINUM, Byproduct.COPPER);
    metal(consumer, TinkerFluids.moltenBendalloy ).optional().metal().dust().common(FLAKES).rawOre(Byproduct.CADMIUM);
    // specialty alloys
    metal(consumer, TinkerFluids.moltenEnderium).optional().metal().dust().plate().gear().coin();
    metal(consumer, TinkerFluids.moltenLumium  ).optional().metal().dust().plate().gear().coin();
    metal(consumer, TinkerFluids.moltenSignalum).optional().metal().dust().plate().gear().coin();
    metal(consumer, TinkerFluids.moltenRefinedObsidian ).optional().metal().common(TOOLS).common(MEKANISM_ARMOR);
    metal(consumer, TinkerFluids.moltenRefinedGlowstone).optional().metal().common(TOOLS).common(MEKANISM_ARMOR);
    // embers provides their own fluid. so we just have to add the recipes
    TagKey<Fluid> dawnstone = getFluidTag(COMMON, "molten_dawnstone");
    metal(withCondition(consumer, new TagFilledCondition<>(dawnstone)), "dawnstone", dawnstone).temperature(900).optional().metal().plate();
    // twilight forest
    CommonRecipe tfLeggings = new ToolItemMelting(7, tf, "leggings");
    CommonRecipe tfShovel = new ToolItemMelting(1, tf, "shovel");
    metal(consumer, TinkerFluids.moltenSteeleaf).optional().metal()
      .common(AXES, SWORD, tfShovel, tfHelmet, tfChestplate, tfLeggings, tfBoots);
    // fiery doesn't have a molten form, rather its composite the whole way
    fluid(consumer, "fiery", TinkerFluids.fieryLiquid).optional()
      .baseUnit(FluidValues.BOTTLE).damageUnit(FluidValues.SIP).unitByproducts(Byproduct.IRON)
      // block and ingot
      .melting(9, "block", "storage_blocks", 3.0f, false, false)
      .blockCasting(9, Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON), false)
      .meltingCasting(1, "ingot", "iron", 1, false)
      // armor and tools
      .common(tfSword, tfHelmet, tfChestplate, tfLeggings, tfBoots)
      .metalMelting(3, tf, "pickaxe", true);
    fluid(consumer, "ironwood", TinkerFluids.moltenIron).optional()
      .baseUnit(FluidValues.INGOT).damageUnit(FluidValues.NUGGET).unitByproducts(Byproduct.TINY_GOLD)
      // block and ingot melting
      .melting(9, "block", "storage_blocks", 3.0f, false, false)
      .melting(1, "ingot", 1f, false)
      .melting(1, "raw", "raw_materials", false, false)
      // armor and tools
      .common(AXES, SWORD, tfShovel, tfHelmet, tfChestplate, tfLeggings, tfBoots);
  }

  private void addCompatRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "compat/";
    // create - cast andesite alloy
    ItemOutput andesiteAlloy = ItemNameOutput.fromName(new ResourceLocation("create", "andesite_alloy"));
    Consumer<FinishedRecipe> createConsumer = withCondition(consumer, new ModLoadedCondition("create"));
    ItemCastingRecipeBuilder.basinRecipe(andesiteAlloy)
                            .setCast(Blocks.ANDESITE, true)
                            .setFluidAndTime(TinkerFluids.moltenIron, FluidValues.NUGGET)
                            .save(createConsumer, location(folder + "create/andesite_alloy_iron"));
    ItemCastingRecipeBuilder.basinRecipe(andesiteAlloy)
                            .setCast(Blocks.ANDESITE, true)
                            .setFluidAndTime(TinkerFluids.moltenZinc, FluidValues.NUGGET)
                            .save(createConsumer, location(folder + "create/andesite_alloy_zinc"));

    // immersive engineering - casting treated wood
    String treatedWood = "treated_wood";
    TagKey<Fluid> creosote = getFluidTag(COMMON, "creosote");
    ItemCastingRecipeBuilder.basinRecipe(ItemOutput.fromTag(getItemTag(COMMON, treatedWood)))
                            .setCast(ItemTags.PLANKS, true)
                            .setFluid(creosote, 125)
                            .setCoolingTime(100)
                            .save(withCondition(consumer, tagCondition(treatedWood), new TagFilledCondition<>(creosote)), location(folder + "treated_wood"));

    // farmers delight - cast dough with a small discount to make numbers work out nicer
    ResourceLocation dough = new ResourceLocation("farmersdelight", "wheat_dough");
    ItemCastingRecipeBuilder.tableRecipe(ItemNameOutput.fromName(dough))
      .setCast(Items.WHEAT, true)
      .setFluid(MantleTags.Fluids.WATER, 250)
      .setCoolingTime(50)
      .save(withCondition(consumer, new ItemExistsCondition(dough)), location(folder + "wheat_dough"));

    // ceramics compat: a lot of melting and some casting

    // ceramics constants //
    // normally its 1/8 of a bucket per lava (125mb), but we give a small discount on casting to make the slab math work out nicer (100 is divisible by 2)
    int lavaPerBlock = FluidType.BUCKET_VOLUME / 10;
    // normally a quarter of a glass pane, but thats 62.5, so round down to 50 for a nice number
    int gaugeGlass = FluidValues.GLASS_PANE / 5;
    // normally its 1 ingot per 8, we do 1 nugget giving a small discount
    int goldPerBlock = FluidValues.NUGGET;

    // ID helpers
    String ceramics = "ceramics";
    String ceramicsFolder = folder + ceramics + "/";
    Function<String,ResourceLocation> ceramicsId = name -> new ResourceLocation(ceramics, name);
    Function<String,Ingredient> ceramicsItem = name -> ItemNameIngredient.from(new ResourceLocation(ceramics, name));
    Function<String,Ingredient> ceramicsTag = name -> Ingredient.of(ItemTags.create(new ResourceLocation(ceramics, name)));
    Function<String,ItemOutput> ceramicsOutput = name -> ItemNameOutput.fromName(new ResourceLocation(ceramics, name));
    Consumer<FinishedRecipe> ceramicsConsumer = withCondition(consumer, new ModLoadedCondition(ceramics));

    // fill clay and cracked clay buckets
    ContainerFillingRecipeBuilder.tableRecipe(ceramicsId.apply("empty_clay_bucket"), FluidType.BUCKET_VOLUME)
                                 .save(ceramicsConsumer, location(ceramicsFolder + "filling_clay_bucket"));
    ContainerFillingRecipeBuilder.tableRecipe(ceramicsId.apply("cracked_empty_clay_bucket"), FluidType.BUCKET_VOLUME)
                                 .save(ceramicsConsumer, location(ceramicsFolder + "filling_cracked_clay_bucket"));

    // porcelain for ceramics
    AlloyRecipeBuilder.alloy(TinkerFluids.moltenPorcelain, FluidValues.BRICK * 4)
                      .addInput(TinkerFluids.moltenClay.getTag(), FluidValues.BRICK * 3)
                      .addInput(TinkerFluids.moltenQuartz.getTag(), FluidValues.GEM)
                      .save(ceramicsConsumer, location(ceramicsFolder + "alloy_porcelain"));

    // melting clay
    String clayFolder = ceramicsFolder + "clay/";

    // unfired clay
    MeltingRecipeBuilder.melting(ceramicsItem.apply("unfired_clay_plate"), TinkerFluids.moltenClay, FluidValues.BRICK, 0.5f)
      .save(ceramicsConsumer, location(clayFolder + "clay_1"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("clay_faucet"), ceramicsId.apply("clay_channel")), TinkerFluids.moltenClay, FluidValues.BRICK * 2, 0.65f)
      .save(ceramicsConsumer, location(clayFolder + "clay_2"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_clay_bucket"), ceramicsId.apply("clay_cistern")), TinkerFluids.moltenClay, FluidValues.BRICK * 3, 0.9f)
      .save(ceramicsConsumer, location(clayFolder + "clay_3"));

    // 2 bricks
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
        ceramicsId.apply("dark_bricks_slab"), ceramicsId.apply("dragon_bricks_slab"),
        ceramicsId.apply("terracotta_faucet"), ceramicsId.apply("terracotta_channel")
      ), TinkerFluids.moltenClay, FluidValues.BRICK * 2, 1.33f)
      .save(ceramicsConsumer, location(clayFolder + "bricks_2"));
    // 3 bricks
    MeltingRecipeBuilder.melting(CompoundIngredient.of(
      ceramicsTag.apply("terracotta_cisterns"),
      ceramicsItem.apply("empty_clay_bucket"),
      // can't ue no container for cracked bucket as the bucket breaks on emptying
      ceramicsItem.apply("cracked_empty_clay_bucket")
    ), TinkerFluids.moltenClay, FluidValues.BRICK * 3, 1.67f)
      .save(ceramicsConsumer, location(clayFolder + "bricks_3"));
    // 4 bricks
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("dark_bricks"), ceramicsId.apply("dark_bricks_stairs"), ceramicsId.apply("dark_bricks_wall"),
      ceramicsId.apply("dragon_bricks"), ceramicsId.apply("dragon_bricks_stairs"), ceramicsId.apply("dragon_bricks_wall")
    ), TinkerFluids.moltenClay, FluidValues.BRICK * 4, 2.0f)
      .save(ceramicsConsumer, location(clayFolder + "block"));
    MeltingRecipeBuilder.melting(ceramicsItem.apply("kiln"), TinkerFluids.moltenClay, FluidValues.BRICK_BLOCK * 3 + FluidValues.BRICK * 5, 4.0f)
      .save(ceramicsConsumer, location(clayFolder + "kiln"));

    // lava bricks, lava byproduct
    MeltingRecipeBuilder.melting(ceramicsItem.apply("lava_bricks_slab"), TinkerFluids.moltenClay, FluidValues.BRICK * 2, 1.33f)
      .addByproduct(new FluidStack(Fluids.LAVA, lavaPerBlock / 2))
      .save(ceramicsConsumer, location(clayFolder + "lava_bricks_slab"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("lava_bricks"), ceramicsId.apply("lava_bricks_stairs"), ceramicsId.apply("lava_bricks_wall")
    ), TinkerFluids.moltenClay, FluidValues.BRICK_BLOCK, 2f)
      .addByproduct(new FluidStack(Fluids.LAVA, lavaPerBlock))
      .save(ceramicsConsumer, location(clayFolder + "lava_bricks_block"));

    // gauge, partially glass
    MeltingRecipeBuilder.melting(ceramicsItem.apply("terracotta_gauge"), TinkerFluids.moltenClay, FluidValues.BRICK, 1f)
      .addByproduct(TinkerFluids.moltenGlass.result(gaugeGlass))
      .save(ceramicsConsumer, location(clayFolder + "gauge"));

    // clay armor
    int brickPart = FluidValues.BRICK / 5;
    MeltingRecipeBuilder.melting(ceramicsItem.apply("clay_helmet"), TinkerFluids.moltenClay, FluidValues.BRICK * 5, 2.25f)
      .setDamagable(brickPart)
      .save(ceramicsConsumer, location(clayFolder + "clay_helmet"));
    MeltingRecipeBuilder.melting(ceramicsItem.apply("clay_chestplate"), TinkerFluids.moltenClay, FluidValues.BRICK * 8, 3f)
      .setDamagable(brickPart)
      .save(ceramicsConsumer, location(clayFolder + "clay_chestplate"));
    MeltingRecipeBuilder.melting(ceramicsItem.apply("clay_leggings"), TinkerFluids.moltenClay, FluidValues.BRICK * 7, 2.75f)
      .setDamagable(brickPart)
      .save(ceramicsConsumer, location(clayFolder + "clay_leggings"));
    MeltingRecipeBuilder.melting(ceramicsItem.apply("clay_boots"), TinkerFluids.moltenClay, FluidValues.BRICK * 4, 2f)
      .setDamagable(brickPart)
      .save(ceramicsConsumer, location(clayFolder + "clay_boots"));

    // melting porcelain
    String porcelainFolder = ceramicsFolder + "porcelain/";
    // unfired
    MeltingRecipeBuilder.melting(ceramicsItem.apply("unfired_porcelain"), TinkerFluids.moltenPorcelain, FluidValues.BRICK, 0.5f)
      .save(ceramicsConsumer, location(porcelainFolder + "unfired_1"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(ceramicsId.apply("unfired_faucet"), ceramicsId.apply("unfired_channel")), TinkerFluids.moltenPorcelain, FluidValues.BRICK * 2, 0.65f)
      .save(ceramicsConsumer, location(porcelainFolder + "unfired_2"));
    MeltingRecipeBuilder.melting(ceramicsItem.apply("unfired_cistern"), TinkerFluids.moltenPorcelain, FluidValues.BRICK * 3, 0.9f)
      .save(ceramicsConsumer, location(porcelainFolder + "unfired_3"));
    MeltingRecipeBuilder.melting(ceramicsItem.apply("unfired_porcelain_block"), TinkerFluids.moltenPorcelain, FluidValues.BRICK_BLOCK, 1f)
      .save(ceramicsConsumer, location(porcelainFolder + "unfired_4"));

    // 1 brick
    MeltingRecipeBuilder.melting(ceramicsItem.apply("porcelain_brick"), TinkerFluids.moltenPorcelain, FluidValues.BRICK, 1f)
      .save(ceramicsConsumer, location(porcelainFolder + "bricks_1"));
    // 2 bricks
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("porcelain_bricks_slab"), ceramicsId.apply("monochrome_bricks_slab"), ceramicsId.apply("marine_bricks_slab"), ceramicsId.apply("rainbow_bricks_slab"),
      ceramicsId.apply("porcelain_faucet"), ceramicsId.apply("porcelain_channel")
    ), TinkerFluids.moltenPorcelain, FluidValues.BRICK * 2, 1.33f)
      .save(ceramicsConsumer, location(porcelainFolder + "bricks_2"));
    // 3 bricks
    MeltingRecipeBuilder.melting(ceramicsTag.apply("porcelain_cisterns"), TinkerFluids.moltenPorcelain, FluidValues.BRICK * 3, 1.67f)
      .save(ceramicsConsumer, location(porcelainFolder + "bricks_3"));
    // 4 bricks
    MeltingRecipeBuilder.melting(CompoundIngredient.of(
      ceramicsTag.apply("porcelain_block"),
      ceramicsTag.apply("rainbow_porcelain"),
      ItemNameIngredient.from(
        ceramicsId.apply("porcelain_bricks"), ceramicsId.apply("porcelain_bricks_stairs"), ceramicsId.apply("porcelain_bricks_wall"),
        ceramicsId.apply("monochrome_bricks"), ceramicsId.apply("monochrome_bricks_stairs"), ceramicsId.apply("monochrome_bricks_wall"),
        ceramicsId.apply("marine_bricks"), ceramicsId.apply("marine_bricks_stairs"), ceramicsId.apply("marine_bricks_wall"),
        ceramicsId.apply("rainbow_bricks"), ceramicsId.apply("rainbow_bricks_stairs"), ceramicsId.apply("rainbow_bricks_wall")
      )), TinkerFluids.moltenPorcelain, FluidValues.BRICK * 4, 2.0f)
      .save(ceramicsConsumer, location(porcelainFolder + "blocks"));

    // gold bricks
    MeltingRecipeBuilder.melting(ceramicsItem.apply("golden_bricks_slab"), TinkerFluids.moltenPorcelain, FluidValues.BRICK * 2, 1.33f)
      .addByproduct(TinkerFluids.moltenGold.result(goldPerBlock / 2))
      .save(ceramicsConsumer, location(porcelainFolder + "golden_bricks_slab"));
    MeltingRecipeBuilder.melting(ItemNameIngredient.from(
      ceramicsId.apply("golden_bricks"), ceramicsId.apply("golden_bricks_stairs"), ceramicsId.apply("golden_bricks_wall")
    ), TinkerFluids.moltenPorcelain, FluidValues.BRICK * 4, 2f)
      .addByproduct(TinkerFluids.moltenGold.result(goldPerBlock))
      .save(ceramicsConsumer, location(porcelainFolder + "golden_bricks_block"));

    // gauge, partially glass
    MeltingRecipeBuilder.melting(ceramicsItem.apply("porcelain_gauge"), TinkerFluids.moltenPorcelain, FluidValues.BRICK, 1f)
      .addByproduct(TinkerFluids.moltenGlass.result(gaugeGlass))
      .save(ceramicsConsumer, location(porcelainFolder + "gauge"));

    // casting bricks
    String castingFolder = ceramicsFolder + "casting/";
    castingWithCast(ceramicsConsumer, TinkerFluids.moltenPorcelain, FluidValues.BRICK, TinkerSmeltery.ingotCast, ceramicsOutput.apply("porcelain_brick"), castingFolder + "porcelain_brick");
    ItemCastingRecipeBuilder.basinRecipe(ItemNameOutput.fromName(ceramicsId.apply("white_porcelain")))
      .setFluidAndTime(TinkerFluids.moltenPorcelain, FluidValues.SLIME_CONGEALED)
      .save(ceramicsConsumer, location(castingFolder + "porcelain"));

    // lava bricks
    ItemCastingRecipeBuilder.basinRecipe(ceramicsOutput.apply("lava_bricks"))
      .setCast(Blocks.BRICKS, true)
      .setFluidAndTime(new FluidStack(Fluids.LAVA, lavaPerBlock))
      .save(ceramicsConsumer, location(castingFolder + "lava_bricks"));
    ItemCastingRecipeBuilder.basinRecipe(ceramicsOutput.apply("lava_bricks_slab"))
      .setCast(Blocks.BRICK_SLAB, true)
      .setFluidAndTime(new FluidStack(Fluids.LAVA, lavaPerBlock / 2))
      .save(ceramicsConsumer, location(castingFolder + "lava_bricks_slab"));
    ItemCastingRecipeBuilder.basinRecipe(ceramicsOutput.apply("lava_bricks_stairs"))
      .setCast(Blocks.BRICK_STAIRS, true)
      .setFluidAndTime(new FluidStack(Fluids.LAVA, lavaPerBlock))
      .save(ceramicsConsumer, location(castingFolder + "lava_bricks_stairs"));
    ItemCastingRecipeBuilder.basinRecipe(ceramicsOutput.apply("lava_bricks_wall"))
      .setCast(Blocks.BRICK_WALL, true)
      .setFluidAndTime(new FluidStack(Fluids.LAVA, lavaPerBlock))
      .save(ceramicsConsumer, location(castingFolder + "lava_bricks_wall"));

    // golden bricks
    ItemCastingRecipeBuilder.basinRecipe(ceramicsOutput.apply("golden_bricks"))
      .setCast(ceramicsItem.apply("porcelain_bricks"), true)
      .setFluidAndTime(TinkerFluids.moltenGold, goldPerBlock)
      .save(ceramicsConsumer, location(castingFolder + "golden_bricks"));
    ItemCastingRecipeBuilder.basinRecipe(ceramicsOutput.apply("golden_bricks_slab"))
      .setCast(ceramicsItem.apply("porcelain_bricks_slab"), true)
      .setFluidAndTime(TinkerFluids.moltenGold, goldPerBlock / 2)
      .save(ceramicsConsumer, location(castingFolder + "golden_bricks_slab"));
    ItemCastingRecipeBuilder.basinRecipe(ceramicsOutput.apply("golden_bricks_stairs"))
      .setCast(ceramicsItem.apply("porcelain_bricks_stairs"), true)
      .setFluidAndTime(TinkerFluids.moltenGold, goldPerBlock)
      .save(ceramicsConsumer, location(castingFolder + "golden_bricks_stairs"));
    ItemCastingRecipeBuilder.basinRecipe(ceramicsOutput.apply("golden_bricks_wall"))
      .setCast(ceramicsItem.apply("porcelain_bricks_wall"), true)
      .setFluidAndTime(TinkerFluids.moltenGold, goldPerBlock)
      .save(ceramicsConsumer, location(castingFolder + "golden_bricks_wall"));

    // refined glowstone composite
    Consumer<FinishedRecipe> wrapped = withCondition(consumer, tagCondition("ingots/refined_glowstone"), tagCondition("ingots/osmium"));
    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromTag(getItemTag(COMMON, "ingots/refined_glowstone")))
                            .setCast(Tags.Items.DUSTS_GLOWSTONE, true)
                            .setFluidAndTime(TinkerFluids.moltenOsmium, FluidValues.INGOT)
                            .save(wrapped, location(folder + "refined_glowstone_ingot"));
    wrapped = withCondition(consumer, tagCondition("ingots/refined_obsidian"), tagCondition("ingots/osmium"));
    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromTag(getItemTag(COMMON, "ingots/refined_obsidian")))
                            .setCast(getItemTag(COMMON, "dusts/refined_obsidian"), true)
                            .setFluidAndTime(TinkerFluids.moltenOsmium, FluidValues.INGOT)
                            .save(wrapped, location(folder + "refined_obsidian_ingot"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerMaterials.necroniumBone)
                            .setFluidAndTime(TinkerFluids.moltenUranium, FluidValues.INGOT)
                            .setCast(TinkerTags.Items.WITHER_BONES, true)
                            .save(withCondition(consumer, tagCondition("ingots/uranium")), location(folder + "necronium_bone"));
  }


  /* Seared casting */

  /**
   * Adds a stonecutting recipe with automatic name and criteria
   * @param consumer  Recipe consumer
   * @param output    Recipe output
   * @param folder    Recipe folder path
   */
  private void searedStonecutter(Consumer<FinishedRecipe> consumer, ItemLike output, String folder) {
    SingleItemRecipeBuilder.stonecutting(
      CompoundIngredient.of(
        Ingredient.of(TinkerSmeltery.searedStone),
        DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.SEARED_BRICKS), Ingredient.of(output))), RecipeCategory.BUILDING_BLOCKS, output, 1)
                           .unlockedBy("has_stone", has(TinkerSmeltery.searedStone))
                           .unlockedBy("has_bricks", has(TinkerTags.Items.SEARED_BRICKS))
                           .save(consumer, wrap(id(output), folder, "_stonecutting"));
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param location  Recipe location
   */
  private void searedCasting(Consumer<FinishedRecipe> consumer, ItemLike block, Ingredient cast, String location) {
    searedCasting(consumer, block, cast, FluidValues.SLIMEBALL * 2, location);
  }

  /**
   * Adds a recipe to create the given seared slab block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param location  Recipe location
   */
  private void searedSlabCasting(Consumer<FinishedRecipe> consumer, ItemLike block, Ingredient cast, String location) {
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
  private void searedCasting(Consumer<FinishedRecipe> consumer, ItemLike block, Ingredient cast, int amount, String location) {
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluidAndTime(TinkerFluids.moltenClay, amount)
                            .setCast(cast, true)
                            .save(consumer, location(location));
  }


  /* Scorched casting */

  /**
   * Adds a stonecutting recipe with automatic name and criteria
   * @param consumer  Recipe consumer
   * @param output    Recipe output
   * @param folder    Recipe folder path
   */
  private void scorchedStonecutter(Consumer<FinishedRecipe> consumer, ItemLike output, String folder) {
    SingleItemRecipeBuilder.stonecutting(DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.SCORCHED_BLOCKS), Ingredient.of(output)), RecipeCategory.BUILDING_BLOCKS, output, 1)
                           .unlockedBy("has_block", has(TinkerTags.Items.SCORCHED_BLOCKS))
                           .save(consumer, wrap(id(output), folder, "_stonecutting"));
  }

  /**
   * Adds a recipe to create the given seared block using molten clay on stone
   * @param consumer  Recipe consumer
   * @param block     Output block
   * @param cast      Cast item
   * @param location  Recipe location
   */
  private void scorchedCasting(Consumer<FinishedRecipe> consumer, ItemLike block, Ingredient cast, String location) {
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
  private void scorchedCasting(Consumer<FinishedRecipe> consumer, ItemLike block, Ingredient cast, int amount, String location) {
    ItemCastingRecipeBuilder.basinRecipe(block)
                            .setFluidAndTime(TinkerFluids.magma, amount)
                            .setCast(cast, true)
                            .save(consumer, location(location));
  }


  /* Casting */

  /**
   * Adds melting recipes for slime
   * @param consumer  Consumer
   * @param fluid     Fluid
   * @param type      Slime type
   * @param folder    Output folder
   */
  private void slimeMelting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, SlimeType type, String folder) {
    String slimeFolder = folder + type.getSerializedName() + "/";
    MeltingRecipeBuilder.melting(Ingredient.of(type.getSlimeballTag()), fluid, FluidValues.SLIMEBALL, 1.0f)
                        .save(consumer, location(slimeFolder + "ball"));
    ItemLike item = TinkerWorld.congealedSlime.get(type);
    MeltingRecipeBuilder.melting(Ingredient.of(item), fluid, FluidValues.SLIME_CONGEALED, 2.0f)
                        .save(consumer, location(slimeFolder + "congealed"));
    item = TinkerWorld.slime.get(type);
    MeltingRecipeBuilder.melting(Ingredient.of(item), fluid, FluidValues.SLIME_BLOCK, 3.0f)
                        .save(consumer, location(slimeFolder + "block"));
  }

  /**
   * Adds slime related casting recipes
   * @param consumer    Recipe consumer
   * @param fluid       Fluid matching the slime type
   * @param slimeType   SlimeType for this recipe
   * @param folder      Output folder
   */
  private void slimeCasting(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, SlimeType slimeType, String folder) {
    String colorFolder = folder + slimeType.getSerializedName() + "/";
    ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.congealedSlime.get(slimeType))
                            .setFluidAndTime(fluid, FluidValues.SLIME_CONGEALED)
                            .save(consumer, location(colorFolder + "congealed"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.slimyEnderbarkRoots.get(slimeType))
                            .setFluidAndTime(fluid, FluidValues.SLIME_CONGEALED)
                            .setCast(TinkerWorld.enderbarkRoots, true)
                            .save(consumer, location(colorFolder + "roots"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.slime.get(slimeType))
                            .setFluidAndTime(fluid, FluidValues.SLIME_BLOCK - FluidValues.SLIME_CONGEALED)
                            .setCast(TinkerWorld.congealedSlime.get(slimeType), true)
                            .save(consumer, location(colorFolder + "block"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.slimeball.get(slimeType))
                            .setFluidAndTime(fluid, FluidValues.SLIMEBALL)
                            .save(consumer, location(colorFolder + "slimeball"));
    ItemCastingRecipeBuilder.tableRecipe(TinkerFluids.slimeBottle.get(slimeType))
                            .setFluid(fluid.ingredient(FluidValues.SLIMEBALL))
                            .setCoolingTime(1)
                            .setCast(Items.GLASS_BOTTLE, true)
                            .save(consumer, location(colorFolder + "bottle"));
    ItemCastingRecipeBuilder.basinRecipe(TinkerWorld.slimeDirt.get(slimeType.asDirt()))
                            .setFluidAndTime(fluid, FluidValues.SLIMEBALL * 2)
                            .setCast(Blocks.DIRT, true)
                            .save(consumer, location(colorFolder + "dirt"));
  }

  /** Adds recipes for melting slime crystals */
  private void crystalMelting(Consumer<FinishedRecipe> consumer, GeodeItemObject geode, FluidObject<?> fluid, String folder) {
    MeltingRecipeBuilder.melting(Ingredient.of(geode), fluid, FluidValues.SLIMEBALL, 1.0f).save(consumer, location(folder + "crystal"));
    MeltingRecipeBuilder.melting(Ingredient.of(geode.getBlock()), fluid, FluidValues.SLIMEBALL * 4, 2.0f).save(consumer, location(folder + "crystal_block"));
    for (BudSize bud : BudSize.values()) {
      int size = bud.getSize();
      MeltingRecipeBuilder.melting(Ingredient.of(geode.getBud(bud)), fluid, FluidValues.SLIMEBALL * size, (size + 1) / 2f)
                          .setOre(OreRateType.GEM)
                          .save(consumer, location(folder + "bud_" + bud.getName()));
    }
  }
}
