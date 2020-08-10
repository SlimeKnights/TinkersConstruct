package slimeknights.tconstruct.common.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.EntityPredicate.AndPredicate;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.data.SingleItemRecipeBuilder;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.crafting.ShapedFallbackRecipeBuilder;
import slimeknights.mantle.recipe.crafting.ShapedRetexturedRecipeBuilder;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.casting.ContainerFillingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.StickySlimeBlock.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.SearedTankBlock;
import slimeknights.tconstruct.smeltery.block.SearedTankBlock.TankType;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.data.MaterialIds;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

public class TConstructRecipeProvider extends RecipeProvider implements IConditionBuilder {

  public TConstructRecipeProvider(DataGenerator generatorIn) {
    super(generatorIn);
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.addCommonRecipes(consumer);
    this.addSlimeRecipes(consumer);
    this.addTableRecipes(consumer);
    this.addModifierRecipes(consumer);
    this.addMaterialRecipes(consumer);
    this.addSmelteryRecipes(consumer);
    this.addGadgetRecipes(consumer);
    this.addPartRecipes(consumer);
    this.addMaterialsRecipes(consumer);
    this.addCastingRecipes(consumer);
    this.addMeltingRecipes(consumer);
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
    ResourceLocation flintId = location("common/flint");
    ConditionalRecipe.builder()
      .addCondition(new ConfigOptionEnabledCondition("addGravelToFlintRecipe"))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(Items.FLINT)
        .addIngredient(Blocks.GRAVEL)
        .addIngredient(Blocks.GRAVEL)
        .addIngredient(Blocks.GRAVEL)
        .addCriterion("has_item", hasItem(Blocks.GRAVEL))::build)
      .setAdvancement(location("recipes/tinkers_general/common/flint"), ConditionalAdvancement.builder()
        .addCondition(new ConfigOptionEnabledCondition("addGravelToFlintRecipe"))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(flintId))
          .withCriterion("has_item", hasItem(Blocks.GRAVEL))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(AndPredicate.ANY_AND, flintId))
          .withRequirementsStrategy(IRequirementsStrategy.OR))
       ).build(consumer, flintId);
  }

  private void addTableRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/";
    // pattern
    ShapedRecipeBuilder.shapedRecipe(TinkerTables.pattern)
                       .key('s', Tags.Items.RODS_WOODEN)
                       .key('p', ItemTags.PLANKS)
                       .patternLine("ps")
                       .patternLine("sp")
                       .addCriterion("has_item", hasItem(Tags.Items.RODS_WOODEN))
                       .build(consumer, prefix(TinkerTables.pattern, folder));

    // crafting station -> crafting table upgrade
    ShapedRecipeBuilder.shapedRecipe(TinkerTables.craftingStation)
                       .key('p', TinkerTables.pattern)
                       .key('w', Items.CRAFTING_TABLE)
                       .patternLine("p")
                       .patternLine("w")
                       .addCriterion("has_item", hasItem(TinkerTables.pattern))
                       .build(consumer, prefix(TinkerTables.craftingStation, folder));

    // part builder
    ShapedRetexturedRecipeBuilder.fromShaped(
      ShapedRecipeBuilder.shapedRecipe(TinkerTables.partBuilder)
                         .key('p', TinkerTables.pattern)
                         .key('w', ItemTags.PLANKS)
                         .patternLine("p")
                         .patternLine("w")
                         .addCriterion("has_item", hasItem(TinkerTables.pattern)))
      .setSource(ItemTags.PLANKS)
      .build(consumer, prefix(TinkerTables.partBuilder, folder));
  }

  private void addModifierRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/modifiers/";

    // ball of moss
    ShapedRecipeBuilder.shapedRecipe(TinkerModifiers.moss)
                       .key('m', Ingredient.fromItems(Blocks.MOSSY_COBBLESTONE, Blocks.MOSSY_STONE_BRICKS))
                       .patternLine("mmm")
                       .patternLine("mmm")
                       .patternLine("mmm")
                       .addCriterion("has_cobble", hasItem(Blocks.MOSSY_COBBLESTONE))
                       .addCriterion("has_bricks", hasItem(Blocks.MOSSY_STONE_BRICKS))
                       .build(consumer, prefix(TinkerModifiers.moss, folder));

    // reinforcement
    // FIXME: switch recipe to use a golden cast
    ShapedRecipeBuilder.shapedRecipe(TinkerModifiers.reinforcement)
                       .key('O', Items.OBSIDIAN)
                       .key('G', Tags.Items.INGOTS_GOLD)
                       .patternLine("OOO")
                       .patternLine("OGO")
                       .patternLine("OOO")
                       .addCriterion("has_center", hasItem(Tags.Items.INGOTS_GOLD))
                       .build(consumer, prefix(TinkerModifiers.reinforcement, folder));

    // expanders
    ShapedRecipeBuilder.shapedRecipe(TinkerModifiers.heightExpander)
                       .key('P', Items.PISTON)
                       .key('L', Tags.Items.GEMS_LAPIS)
                       .key('S', TinkerTags.Items.PURPLE_SLIMEBALL)
                       .patternLine(" P ")
                       .patternLine("LSL")
                       .patternLine(" P ")
                       .addCriterion("has_item", hasItem(TinkerTags.Items.PURPLE_SLIMEBALL))
                       .build(consumer, prefix(TinkerModifiers.heightExpander, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerModifiers.widthExpander)
                       .key('P', Items.PISTON)
                       .key('L', Tags.Items.GEMS_LAPIS)
                       .key('S', TinkerTags.Items.PURPLE_SLIMEBALL)
                       .patternLine(" L ")
                       .patternLine("PSP")
                       .patternLine(" L ")
                       .addCriterion("has_item", hasItem(TinkerTags.Items.PURPLE_SLIMEBALL))
                       .build(consumer, prefix(TinkerModifiers.widthExpander, folder));

    // silky cloth
    ShapedRecipeBuilder.shapedRecipe(TinkerModifiers.silkyCloth)
                       .key('s', Tags.Items.STRING)
                       .key('g', Tags.Items.INGOTS_GOLD)
                       .patternLine("sss")
                       .patternLine("sgs")
                       .patternLine("sss")
                       .addCriterion("has_item", hasItem(Tags.Items.INGOTS_GOLD))
                       .build(consumer, prefix(TinkerModifiers.silkyCloth, folder));
    // silky jewel
    ShapedRecipeBuilder.shapedRecipe(TinkerModifiers.silkyJewel)
                       .key('c', TinkerModifiers.silkyCloth)
                       .key('E', Items.EMERALD)
                       .patternLine(" c ")
                       .patternLine("cEc")
                       .patternLine(" c ")
                       .addCriterion("has_item", hasItem(TinkerModifiers.silkyCloth))
                       .setGroup(TinkerModifiers.silkyJewel.getRegistryName().toString())
                       .build(consumer, prefix(TinkerModifiers.silkyJewel, folder));
    registerMineralRecipes(consumer, TinkerModifiers.silkyJewelBlock, TinkerModifiers.silkyJewel, null, folder);


    // slimy mud and slime crystals
    registerMudRecipe(consumer, SlimeType.GREEN, null, TinkerModifiers.slimyMudGreen, TinkerModifiers.greenSlimeCrystal, folder);
    registerMudRecipe(consumer, SlimeType.BLUE, null, TinkerModifiers.slimyMudBlue, TinkerModifiers.blueSlimeCrystal, folder);
    registerMudRecipe(consumer, SlimeType.MAGMA, Items.MAGMA_CREAM, TinkerModifiers.slimyMudMagma, TinkerModifiers.magmaSlimeCrystal, folder);
  }

  private void addMaterialRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/materials/";
    // metals
    registerMineralRecipes(consumer, TinkerMaterials.cobaltBlock, TinkerMaterials.cobaltIngot, TinkerMaterials.cobaltNugget, folder);
    registerMineralRecipes(consumer, TinkerMaterials.arditeBlock, TinkerMaterials.arditeIngot, TinkerMaterials.arditeNugget, folder);
    registerMineralRecipes(consumer, TinkerMaterials.manyullynBlock, TinkerMaterials.manyullynIngot, TinkerMaterials.manyullynNugget, folder);
    registerMineralRecipes(consumer, TinkerMaterials.knightSlimeBlock, TinkerMaterials.knightslimeIngot, TinkerMaterials.knightslimeNugget, folder);
    registerMineralRecipes(consumer, TinkerMaterials.pigironBlock, TinkerMaterials.pigironIngot, TinkerMaterials.pigironNugget, folder);
    registerMineralRecipes(consumer, TinkerMaterials.copperBlock, TinkerMaterials.copperIngot, TinkerMaterials.copperNugget, folder);
    registerMineralRecipes(consumer, TinkerMaterials.roseGoldBlock, TinkerMaterials.roseGoldIngot, TinkerMaterials.roseGoldNugget, folder);

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

    // FIXME: temporary manyullyn recipe
    ShapelessRecipeBuilder.shapelessRecipe(TinkerMaterials.manyullynNugget)
                          .addIngredient(TinkerMaterials.cobaltNugget)
                          .addIngredient(TinkerMaterials.arditeNugget)
                          .addIngredient(Items.COAL)
                          .setGroup(TinkerMaterials.manyullynNugget.getRegistryName().toString())
                          .addCriterion("has_item", hasItem(TinkerMaterials.cobaltNugget))
                          .build(consumer, wrap(TinkerMaterials.manyullynNugget, folder, "_crafting"));
    ShapelessRecipeBuilder.shapelessRecipe(TinkerMaterials.manyullynIngot)
                          .addIngredient(TinkerMaterials.cobaltIngot)
                          .addIngredient(TinkerMaterials.arditeIngot)
                          .addIngredient(Blocks.COAL_BLOCK)
                          .setGroup(TinkerMaterials.manyullynIngot.getRegistryName().toString())
                          .addCriterion("has_item", hasItem(TinkerMaterials.cobaltIngot))
                          .build(consumer, wrap(TinkerMaterials.manyullynIngot, folder, "_crafting"));

    // FIXME: temporary rose gold recipe
    ShapelessRecipeBuilder.shapelessRecipe(TinkerMaterials.roseGoldNugget)
                          .addIngredient(TinkerMaterials.copperNugget)
                          .addIngredient(Items.GOLD_NUGGET)
                          .addIngredient(Items.COAL)
                          .setGroup(TinkerMaterials.roseGoldNugget.getRegistryName().toString())
                          .addCriterion("has_item", hasItem(Items.GOLD_NUGGET))
                          .build(consumer, wrap(TinkerMaterials.roseGoldNugget, folder, "_crafting"));
    ShapelessRecipeBuilder.shapelessRecipe(TinkerMaterials.roseGoldIngot)
                          .addIngredient(TinkerMaterials.copperIngot)
                          .addIngredient(Items.GOLD_INGOT)
                          .addIngredient(Blocks.COAL_BLOCK)
                          .setGroup(TinkerMaterials.roseGoldIngot.getRegistryName().toString())
                          .addCriterion("has_item", hasItem(Items.GOLD_INGOT))
                          .build(consumer, wrap(TinkerMaterials.roseGoldIngot, folder, "_crafting"));

    // FIXME: temporary knightslime recipe
    Item purpleSlime = TinkerCommons.slimeball.get(SlimeType.PURPLE);
    ShapelessRecipeBuilder.shapelessRecipe(TinkerMaterials.knightslimeIngot)
                          .addIngredient(purpleSlime)
                          .addIngredient(Items.IRON_INGOT)
                          .addIngredient(TinkerSmeltery.searedBrick)
                          .setGroup(TinkerMaterials.knightslimeIngot.getRegistryName().toString())
                          .addCriterion("has_item", hasItem(purpleSlime))
                          .build(consumer, wrap(TinkerMaterials.knightslimeIngot, folder, "_crafting"));

    // FIXME: temporary pigiron recipe
    Item blood = TinkerCommons.slimeball.get(SlimeType.BLOOD);
    ShapelessRecipeBuilder.shapelessRecipe(TinkerMaterials.pigironIngot, 4)
                          .addIngredient(blood)
                          .addIngredient(Items.IRON_INGOT).addIngredient(Items.IRON_INGOT).addIngredient(Items.IRON_INGOT).addIngredient(Items.IRON_INGOT)
                          .addIngredient(Items.BRICK).addIngredient(Items.BRICK).addIngredient(Items.BRICK).addIngredient(Items.BRICK)
                          .setGroup(TinkerMaterials.pigironIngot.getRegistryName().toString())
                          .addCriterion("has_item", hasItem(purpleSlime))
                          .build(consumer, wrap(TinkerMaterials.pigironIngot, folder, "_crafting"));
  }

  private void addGadgetRecipes(Consumer<IFinishedRecipe> consumer) {
    // slime
    String folder = "gadgets/slimeboots/";
    ShapedFallbackRecipeBuilder slimeBoots = ShapedFallbackRecipeBuilder.fallback(
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeBoots.get(SlimeType.GREEN))
                         .setGroup("tconstruct:slime_boots")
                         .key('#', TinkerTags.Items.CONGEALED_SLIME)
                         .key('X', Tags.Items.SLIMEBALLS)
                         .patternLine("X X")
                         .patternLine("# #")
                         .addCriterion("has_item", hasItem(Tags.Items.SLIMEBALLS)));
    for (SlimeType slime : SlimeType.TINKER) {
      ResourceLocation name = location(folder + slime.getString());
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeBoots.get(slime))
                         .setGroup("tconstruct:slime_boots")
                         .key('#', TinkerWorld.congealedSlime.get(slime))
                         .key('X', slime.getSlimeBallTag())
                         .patternLine("X X")
                         .patternLine("# #")
                         .addCriterion("has_item", hasItem(slime.getSlimeBallTag()))
                         .build(consumer, name);
      slimeBoots.addAlternative(name);
    }
    slimeBoots.build(consumer, location(folder + "green"));

    folder = "gadgets/slimesling/";
    ShapedFallbackRecipeBuilder slimeSling = ShapedFallbackRecipeBuilder.fallback(
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeSling.get(SlimeType.GREEN))
                         .setGroup("tconstruct:slimesling")
                         .key('#', Tags.Items.STRING)
                         .key('X', TinkerTags.Items.CONGEALED_SLIME)
                         .key('L', Tags.Items.SLIMEBALLS)
                         .patternLine("#X#")
                         .patternLine("L L")
                         .patternLine(" L ")
                         .addCriterion("has_item", hasItem(Tags.Items.SLIMEBALLS)));
    for (SlimeType slime : SlimeType.TINKER) {
      ResourceLocation name = location(folder + slime.getString());
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeSling.get(slime))
                         .setGroup("tconstruct:slimesling")
                         .key('#', Items.STRING)
                         .key('X', TinkerWorld.congealedSlime.get(slime))
                         .key('L', slime.getSlimeBallTag())
                         .patternLine("#X#")
                         .patternLine("L L")
                         .patternLine(" L ")
                         .addCriterion("has_item", hasItem(slime.getSlimeBallTag()))
                         .build(consumer, name);
      slimeSling.addAlternative(name);
    }
    slimeSling.build(consumer, location(folder + "green"));

    // rails
    folder = "gadgets/rail/";
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.woodenRail, 4)
                       .key('#', ItemTags.PLANKS)
                       .key('X', Tags.Items.RODS_WOODEN)
                       .patternLine("# #")
                       .patternLine("#X#")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(ItemTags.PLANKS))
                       .build(consumer, prefix(TinkerGadgets.woodenRail, folder));

    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.woodenDropperRail, 4)
                       .key('#', ItemTags.PLANKS)
                       .key('X', ItemTags.WOODEN_TRAPDOORS)
                       .patternLine("# #")
                       .patternLine("#X#")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(ItemTags.PLANKS))
                       .build(consumer, prefix(TinkerGadgets.woodenDropperRail, folder));

    // stone
    folder = "gadgets/stone/";
    ShapedRecipeBuilder.shapedRecipe(Blocks.JACK_O_LANTERN)
                       .key('#', Blocks.CARVED_PUMPKIN)
                       .key('X', TinkerGadgets.stoneTorch.get())
                       .patternLine("#")
                       .patternLine("X")
                       .addCriterion("has_item", hasItem(Blocks.CARVED_PUMPKIN))
                       .build(consumer, location(folder + "jack_o_lantern"));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneLadder.get(), 3)
                       .key('#', TinkerTags.Items.RODS_STONE)
                       .patternLine("# #")
                       .patternLine("###")
                       .patternLine("# #")
                       .addCriterion("has_item", hasItem(TinkerTags.Items.RODS_STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneLadder, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneStick.get(), 4)
                       .key('#', Ingredient.fromItemListStream(Stream.of(
                         new Ingredient.TagList(Tags.Items.STONE),
                         new Ingredient.TagList(Tags.Items.COBBLESTONE))
                       ))
                       .patternLine("#")
                       .patternLine("#")
                       .addCriterion("has_item", hasItem(Tags.Items.STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneStick, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneTorch.get(), 4)
                       .key('#', Ingredient.fromItemListStream(Stream.of(
                         new Ingredient.SingleItemList(new ItemStack(Items.COAL)),
                         new Ingredient.SingleItemList(new ItemStack(Items.CHARCOAL))
                       )))
                       .key('X', TinkerTags.Items.RODS_STONE)
                       .patternLine("#")
                       .patternLine("X")
                       .addCriterion("has_item", hasItem(TinkerTags.Items.RODS_STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneTorch, folder));

    // throw balls
    ShapelessRecipeBuilder.shapelessRecipe(TinkerGadgets.efln.get())
      .addIngredient(Items.FLINT)
      .addIngredient(Items.GUNPOWDER)
      .addCriterion("has_item", hasItem(Tags.Items.DUSTS_GLOWSTONE)).build(consumer, prefix(TinkerGadgets.efln, "gadgets/throwball/"));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.glowBall.get(), 8)
      .key('#', Items.SNOWBALL)
      .key('X', Tags.Items.DUSTS_GLOWSTONE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .addCriterion("has_item", hasItem(Tags.Items.DUSTS_GLOWSTONE))
      .build(consumer, prefix(TinkerGadgets.glowBall, "gadgets/throwball/"));

    // piggybackpack
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.piggyBackpack.get())
      .key('#', Tags.Items.RODS_WOODEN)
      .key('X', Tags.Items.LEATHER)
      .patternLine(" X ")
      .patternLine("# #")
      .patternLine(" X ")
      .addCriterion("has_item", hasItem(Tags.Items.RODS_WOODEN))
      .build(consumer, prefix(TinkerGadgets.piggyBackpack, "gadgets/"));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.punji.get(), 3)
      .key('#', Items.SUGAR_CANE)
      .patternLine("# #")
      .patternLine(" # ")
      .patternLine("# #")
      .addCriterion("has_item", hasItem(Items.SUGAR_CANE))
      .build(consumer, prefix(TinkerGadgets.punji, "gadgets/"));

    // frames
    registerFrameRecipes(consumer, TinkerModifiers.silkyCloth, FrameType.JEWEL);
    registerFrameRecipes(consumer, TinkerMaterials.cobaltNugget, FrameType.COBALT);
    registerFrameRecipes(consumer, TinkerMaterials.arditeNugget, FrameType.ARDITE);
    registerFrameRecipes(consumer, TinkerMaterials.manyullynNugget, FrameType.MANYULLYN);
    registerFrameRecipes(consumer, Items.GOLD_NUGGET, FrameType.GOLD);
    Item clearFrame = TinkerGadgets.itemFrame.get(FrameType.CLEAR);
    ShapedRecipeBuilder.shapedRecipe(clearFrame)
                       .key('e', Tags.Items.GLASS_PANES_COLORLESS)
                       .key('M', Tags.Items.GLASS_COLORLESS)
                       .patternLine(" e ")
                       .patternLine("eMe")
                       .patternLine(" e ")
                       .addCriterion("has_item", hasItem(Tags.Items.GLASS_PANES_COLORLESS))
                       .setGroup(locationString("fancy_item_frame"))
                       .build(consumer, prefix(clearFrame, "gadgets/fancy_frame/"));

    // dried clay
    folder = "building/";
    ShapedRecipeBuilder.shapedRecipe(TinkerCommons.driedClayBricks)
                       .key('b', TinkerCommons.driedBrick)
                       .patternLine("bb")
                       .patternLine("bb")
                       .addCriterion("has_item", hasItem(TinkerCommons.driedClay))
                       .build(consumer, prefix(TinkerCommons.driedClayBricks, folder));
    registerSlabStair(consumer, TinkerCommons.driedClay, folder, true);
    registerSlabStair(consumer, TinkerCommons.driedClayBricks, folder, true);

    // FIXME: temporary dried clay recipes
    addCampfireCooking(consumer, Blocks.CLAY, TinkerCommons.driedClay, 0.3f, folder);
    addCampfireCooking(consumer, Items.CLAY_BALL, TinkerCommons.driedBrick, 0.3f, folder);

    // FIXME: temporary jerky recipes
    folder = "foods/";
    addFoodCooking(consumer, Items.COOKED_BEEF, TinkerGadgets.beefJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.COOKED_CHICKEN, TinkerGadgets.chickenJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.COOKED_PORKCHOP, TinkerGadgets.porkJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.COOKED_MUTTON, TinkerGadgets.muttonJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.COOKED_RABBIT, TinkerGadgets.rabbitJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.COOKED_COD, TinkerGadgets.fishJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.COOKED_SALMON, TinkerGadgets.salmonJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.ROTTEN_FLESH, TinkerGadgets.monsterJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.TROPICAL_FISH, TinkerGadgets.clownfishJerky, 0.35f, folder);
    addFoodCooking(consumer, Items.PUFFERFISH, TinkerGadgets.pufferfishJerky, 0.35f, folder);
    // slime drops
    for (SlimeType slime : SlimeType.values()) {
      addFoodCooking(consumer, TinkerCommons.slimeball.get(slime), TinkerGadgets.slimeDrop.get(slime), 0.35f, folder);
    }
  }

  private void addSmelteryRecipes(Consumer<IFinishedRecipe> consumer) {
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
                       .patternLine("bb").patternLine("bb")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, wrap(TinkerSmeltery.searedBricks, folder, "_from_brick"));

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
    // brick slabs -> chiseled
    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedSquareBricks)
                       .key('s', TinkerSmeltery.searedBricks.getSlab())
                       .patternLine("s")
                       .patternLine("s")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBricks.getSlab()))
                       .build(consumer, wrap(TinkerSmeltery.searedSquareBricks, folder, "_crafting"));
    // bricks or stone as input
    this.addSearedStonecutter(consumer, TinkerSmeltery.searedBricks, folder);
    this.addSearedStonecutter(consumer, TinkerSmeltery.searedFancyBricks, folder);
    this.addSearedStonecutter(consumer, TinkerSmeltery.searedSquareBricks, folder);
    this.addSearedStonecutter(consumer, TinkerSmeltery.searedSmallBricks, folder);
    this.addSearedStonecutter(consumer, TinkerSmeltery.searedTriangleBricks, folder);
    this.addSearedStonecutter(consumer, TinkerSmeltery.searedRoad, folder);
    // transform smooth
    ICriterionInstance hasSmoothSeared = hasItem(TinkerTags.Items.SMOOTH_SEARED_BLOCKS);
    SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromTag(TinkerTags.Items.SMOOTH_SEARED_BLOCKS), TinkerSmeltery.searedPaver)
                           .addCriterion("has_item", hasSmoothSeared)
                           .build(consumer, wrap(TinkerSmeltery.searedPaver, folder, "_stonecutting"));
    SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromTag(TinkerTags.Items.SMOOTH_SEARED_BLOCKS), TinkerSmeltery.searedCreeper)
                           .addCriterion("has_item", hasSmoothSeared)
                           .build(consumer, wrap(TinkerSmeltery.searedCreeper, folder, "_stonecutting"));
    SingleItemRecipeBuilder.stonecuttingRecipe(Ingredient.fromTag(TinkerTags.Items.SMOOTH_SEARED_BLOCKS), TinkerSmeltery.searedTile)
                           .addCriterion("has_item", hasSmoothSeared)
                           .build(consumer, wrap(TinkerSmeltery.searedTile, folder, "_stonecutting"));

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
    this.registerSlabStair(consumer, TinkerSmeltery.searedCobble, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedPaver, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedBricks, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedCrackedBricks, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedFancyBricks, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedSquareBricks, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedSmallBricks, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedTriangleBricks, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedCreeper, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedRoad, folder, true);
    this.registerSlabStair(consumer, TinkerSmeltery.searedTile, folder, true);

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

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedMelter)
                       .key('G', TinkerSmeltery.searedTank.get(TankType.GAUGE))
                       .key('B', TinkerSmeltery.searedBrick)
                       .patternLine("BGB")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                       .build(consumer, prefix(TinkerSmeltery.searedMelter, "smeltery/"));
  }

  private void addCastingRecipes(Consumer<IFinishedRecipe> consumer) {
    // Pure Fluid Recipes
    String folder = "casting/";
    ContainerFillingRecipeBuilder.tableRecipe(Items.BUCKET, FluidAttributes.BUCKET_VOLUME)
      .addCriterion("has_item", hasItem(Items.BUCKET))
      .build(consumer, location(folder + "filling/bucket"));
    // Slime
    this.addSlimeCastingRecipe(consumer, TinkerFluids.blood, SlimeType.BLOOD, folder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.blueSlime, SlimeType.BLUE, folder);
    this.addSlimeCastingRecipe(consumer, TinkerFluids.purpleSlime, SlimeType.PURPLE, folder);
    this.addBlockCastingRecipe(consumer, TinkerFluids.searedStone, MaterialValues.VALUE_BrickBlock, TinkerSmeltery.searedStone, folder);
    this.addIngotCastingRecipe(consumer, TinkerFluids.searedStone, TinkerSmeltery.searedBrick, folder);
    this.addBlockCastingRecipe(consumer, TinkerFluids.moltenGlass, MaterialValues.VALUE_Glass, TinkerCommons.clearGlass, folder);
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.clearGlassPane)
      .setFluid(new FluidStack(TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Pane))
      .addCriterion("has_item", hasItem(TinkerCommons.clearGlassPane.asItem()))
      .build(consumer, prefix(TinkerCommons.clearGlassPane, folder));
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

    // Smeltery Misc
    ItemCastingRecipeBuilder.basinRecipe(TinkerSmeltery.searedCobble)
      .setFluid(new FluidStack(TinkerFluids.searedStone.get(), MaterialValues.VALUE_Ingot * 3))
      .setCast(Tags.Items.COBBLESTONE, true)
      .addCriterion("has_item", hasItem(TinkerFluids.searedStone.asItem()))
      .build(consumer, prefix(TinkerSmeltery.searedCobble, folder));

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
    // metal alloys
    addMetalMelting(consumer, TinkerFluids.moltenRoseGold.get(), "rose_gold", false, folder);
    addMetalMelting(consumer, TinkerFluids.moltenManyullyn.get(), "manyullyn", false, folder);
    addMetalMelting(consumer, TinkerFluids.moltenPigIron.get(), "pig_iron", false, folder);
    addMetalMelting(consumer, TinkerFluids.moltenKnightslime.get(), "knightslime", false, folder);

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
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.GLASS_PANES), TinkerFluids.moltenGlass.get(), MaterialValues.VALUE_Glass * 6 / 16)
                        .addCriterion("has_item", hasItem(Tags.Items.GLASS_PANES))
                        .build(consumer, location(folder + "glass_from_pane"));

    // seared stone
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.SEARED_BLOCKS), TinkerFluids.searedStone.get(), MaterialValues.VALUE_SearedBlock)
                        .addCriterion("has_item", hasItem(TinkerTags.Items.SEARED_BLOCKS))
                        .build(consumer, location(folder + "seared_stone_from_block"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.searedBrick), TinkerFluids.searedStone.get(), MaterialValues.VALUE_SearedMaterial)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                        .build(consumer, location(folder + "seared_stone_from_brick"));
    MeltingRecipeBuilder.melting(Ingredient.fromItems(TinkerSmeltery.grout), TinkerFluids.searedStone.get(), MaterialValues.VALUE_SearedMaterial)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.searedBrick))
                        .build(consumer, location(folder + "seared_stone_from_grout"));

    // blue slime
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.BLUE_SLIMEBALL), TinkerFluids.blueSlime.get(), MaterialValues.VALUE_SlimeBall)
                        .addCriterion("has_item", hasItem(TinkerTags.Items.BLUE_SLIMEBALL))
                        .build(consumer, location(folder + "blue_slime_from_ball"));
    IItemProvider item = TinkerWorld.congealedSlime.get(SlimeType.BLUE);
    MeltingRecipeBuilder.melting(Ingredient.fromItems(item), TinkerFluids.blueSlime.get(), MaterialValues.VALUE_SlimeBall * 4)
                        .addCriterion("has_item", hasItem(item))
                        .build(consumer, location(folder + "blue_slime_from_congealed"));
    item = TinkerWorld.slime.get(SlimeType.BLUE);
    MeltingRecipeBuilder.melting(Ingredient.fromItems(item), TinkerFluids.blueSlime.get(), MaterialValues.VALUE_SlimeBall * 9)
                        .addCriterion("has_item", hasItem(item))
                        .build(consumer, location(folder + "blue_slime_from_block"));

    // purple slime
    MeltingRecipeBuilder.melting(Ingredient.fromTag(TinkerTags.Items.PURPLE_SLIMEBALL), TinkerFluids.purpleSlime.get(), MaterialValues.VALUE_SlimeBall)
                        .addCriterion("has_item", hasItem(TinkerTags.Items.PURPLE_SLIMEBALL))
                        .build(consumer, location(folder + "purple_slime_from_ball"));
    item = TinkerWorld.congealedSlime.get(SlimeType.PURPLE);
    MeltingRecipeBuilder.melting(Ingredient.fromItems(item), TinkerFluids.purpleSlime.get(), MaterialValues.VALUE_SlimeBall * 4)
                        .addCriterion("has_item", hasItem(item))
                        .build(consumer, location(folder + "purple_slime_from_congealed"));
    item = TinkerWorld.slime.get(SlimeType.PURPLE);
    MeltingRecipeBuilder.melting(Ingredient.fromItems(item), TinkerFluids.purpleSlime.get(), MaterialValues.VALUE_SlimeBall * 9)
                        .addCriterion("has_item", hasItem(item))
                        .build(consumer, location(folder + "purple_slime_from_block"));

    // obsidian
    MeltingRecipeBuilder.melting(Ingredient.fromTag(Tags.Items.OBSIDIAN), TinkerFluids.moltenObsidian.get(), MaterialValues.VALUE_Glass)
                        .addCriterion("has_item", hasItem(Tags.Items.OBSIDIAN))
                        .build(consumer, location(folder + "obsidian"));

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
  }

  private void addSlimeRecipes(Consumer<IFinishedRecipe> consumer) {

    // Add recipe for all slimeball <-> congealed and slimeblock <-> slimeball
    // fallback: green slime
    ShapedFallbackRecipeBuilder congealed = ShapedFallbackRecipeBuilder.fallback(
      ShapedRecipeBuilder.shapedRecipe(TinkerWorld.congealedSlime.get(SlimeType.GREEN))
        .key('#', Tags.Items.SLIMEBALLS)
        .patternLine("##")
        .patternLine("##")
        .addCriterion("has_item", hasItem(Tags.Items.SLIMEBALLS))
        .setGroup("tconstruct:congealed_slime"));
    // replace vanilla recipe to prevent it from conflicting with our slime blocks
    ShapedFallbackRecipeBuilder slimeBlock = ShapedFallbackRecipeBuilder.fallback(
      ShapedRecipeBuilder.shapedRecipe(Blocks.SLIME_BLOCK)
                         .key('#', Tags.Items.SLIMEBALLS)
                         .patternLine("###")
                         .patternLine("###")
                         .patternLine("###")
                         .addCriterion("has_item", hasItem(Tags.Items.SLIMEBALLS))
                         .setGroup("slime_blocks"));
    // does not need green as its the fallback
    for (SlimeType slimeType : SlimeType.TINKER) {
      ResourceLocation name = location("common/slime/" + slimeType.getString() + "/congealed");
      ShapedRecipeBuilder.shapedRecipe(TinkerWorld.congealedSlime.get(slimeType))
                         .key('#', slimeType.getSlimeBallTag())
                         .patternLine("##")
                         .patternLine("##")
                         .addCriterion("has_item", hasItem(slimeType.getSlimeBallTag()))
                         .setGroup("tconstruct:congealed_slime")
                         .build(consumer, name);
      congealed.addAlternative(name);
      ResourceLocation blockName = location("common/slime/" + slimeType.getString() + "/slimeblock");
      ShapedRecipeBuilder.shapedRecipe(TinkerWorld.slime.get(slimeType))
                         .key('#', slimeType.getSlimeBallTag())
                         .patternLine("###")
                         .patternLine("###")
                         .patternLine("###")
                         .addCriterion("has_item", hasItem(slimeType.getSlimeBallTag()))
                         .setGroup("slime_blocks")
                         .build(consumer, blockName);
      slimeBlock.addAlternative(blockName);
      // green already can craft into slime balls
      ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.slimeball.get(slimeType), 9)
                            .addIngredient(TinkerWorld.slime.get(slimeType))
                            .addCriterion("has_item", hasItem(TinkerWorld.slime.get(slimeType)))
                            .setGroup("tconstruct:slime_balls")
                            .build(consumer, "tconstruct:common/slime/" + slimeType.getString() + "/slimeball_from_block");
    }
    // all types of congealed need a recipe to a block
    for (SlimeType slimeType : SlimeType.values()) {
      ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.slimeball.get(slimeType), 4)
                            .addIngredient(TinkerWorld.congealedSlime.get(slimeType))
                            .addCriterion("has_item", hasItem(TinkerWorld.congealedSlime.get(slimeType)))
                            .setGroup("tconstruct:slime_balls")
                            .build(consumer, "tconstruct:common/slime/" + slimeType.getString() + "/slimeball_from_congealed");
    }

    // build fallback recipes
    congealed.build(consumer, location("common/slime/green/congealed"));
    // block fallback replaces the vanilla recipe
    slimeBlock.build(consumer);
  }

  private void addPartRecipes(Consumer<IFinishedRecipe> consumer) {
    addPartRecipe(consumer, TinkerToolParts.pickaxeHead, 2, TinkerSmeltery.pickaxeHeadCast);
    addPartRecipe(consumer, TinkerToolParts.hammerHead, 8, TinkerSmeltery.hammerHeadCast);
    addPartRecipe(consumer, TinkerToolParts.shovelHead, 2, TinkerSmeltery.shovelHeadCast);
    addPartRecipe(consumer, TinkerToolParts.swordBlade, 2, TinkerSmeltery.swordBladeCast);
    addPartRecipe(consumer, TinkerToolParts.smallBinding, 1, TinkerSmeltery.smallBindingCast);
    addPartRecipe(consumer, TinkerToolParts.wideGuard, 1, TinkerSmeltery.wideGuardCast);
    addPartRecipe(consumer, TinkerToolParts.largePlate, 8, TinkerSmeltery.largePlateCast);
    addPartRecipe(consumer, TinkerToolParts.toolRod, 1, TinkerSmeltery.toolRodCast);
    addPartRecipe(consumer, TinkerToolParts.toughToolRod, 3, TinkerSmeltery.toughToolRodCast);
  }

  private void addMaterialsRecipes(Consumer<IFinishedRecipe> consumer) {
    registerMaterial(consumer, MaterialIds.wood, Ingredient.fromTag(ItemTags.PLANKS), 1, 1, "wood_from_planks");
    registerMaterial(consumer, MaterialIds.wood, Ingredient.fromTag(ItemTags.LOGS), 4, 1, "wood_from_logs");

    registerMaterial(consumer, MaterialIds.stone, Ingredient.fromTag(Tags.Items.COBBLESTONE), 1, 1, "stone_from_cobblestone");
    registerMaterial(consumer, MaterialIds.stone, Ingredient.fromTag(Tags.Items.STONE), 1, 1, "stone_from_stone");

    registerMaterial(consumer, MaterialIds.flint, Ingredient.fromItems(Items.FLINT), 1, 1, "flint");

    registerMaterial(consumer, MaterialIds.cactus, Ingredient.fromItems(Items.CACTUS), 1, 1, "cactus");

    registerMaterial(consumer, MaterialIds.obsidian, Ingredient.fromItems(Items.OBSIDIAN), 1, 1, "obsidian");

    registerMaterial(consumer, MaterialIds.prismarine, Ingredient.fromItems(Items.PRISMARINE), 1, 1, "prismarine_from_block");
    registerMaterial(consumer, MaterialIds.prismarine, Ingredient.fromItems(Items.PRISMARINE_BRICKS), 9, 4, "prismarine_from_bricks");
    registerMaterial(consumer, MaterialIds.prismarine, Ingredient.fromItems(Items.DARK_PRISMARINE), 2, 1, "prismarine_from_dark");
    registerMaterial(consumer, MaterialIds.prismarine, Ingredient.fromItems(Items.PRISMARINE_SHARD), 1, 4, "prismarine_from_shard");

    registerMaterial(consumer, MaterialIds.netherrack, Ingredient.fromItems(Items.NETHERRACK), 1, 1, "netherrack");

    registerMaterial(consumer, MaterialIds.bone, Ingredient.fromTag(Tags.Items.BONES), 1, 1, "bone_from_bones");
    registerMaterial(consumer, MaterialIds.bone, Ingredient.fromItems(Items.BONE_MEAL), 1, 4, "bone_from_bonemeal");

    registerMaterial(consumer, MaterialIds.paper, Ingredient.fromItems(Items.PAPER), 1, 4, "paper");

    registerMaterial(consumer, MaterialIds.sponge, Ingredient.fromItems(Items.SPONGE), 1, 1, "sponge");

    registerMaterial(consumer, MaterialIds.sponge, Ingredient.fromItems(TinkerCommons.firewood), 1, 1, "firewood");

    registerMaterial(consumer, MaterialIds.slime, Ingredient.fromItems(TinkerModifiers.greenSlimeCrystal), 1, 1, "slime");

    registerMaterial(consumer, MaterialIds.blueslime, Ingredient.fromItems(TinkerModifiers.blueSlimeCrystal), 1, 1, "blue_slime");

    registerMaterial(consumer, MaterialIds.knightslime, Ingredient.fromTag(TinkerTags.Items.INGOTS_KNIGHTSLIME), 1, 1, "knightslime_from_ingot");
    registerMaterial(consumer, MaterialIds.knightslime, Ingredient.fromTag(TinkerTags.Items.NUGGETS_KNIGHTSLIME), 1, 9, "knightslime_from_nugget");
    registerMaterial(consumer, MaterialIds.knightslime, Ingredient.fromTag(TinkerTags.Items.STORAGE_BLOCKS_KNIGHTSLIME), 9, 1, "knightslime_from_block");

    registerMaterial(consumer, MaterialIds.magmaslime, Ingredient.fromItems(TinkerModifiers.magmaSlimeCrystal), 1, 1, "magma_slime");

    registerMaterial(consumer, MaterialIds.iron, Ingredient.fromTag(Tags.Items.INGOTS_IRON), 1, 1, "iron_from_ingot");
    registerMaterial(consumer, MaterialIds.iron, Ingredient.fromTag(Tags.Items.NUGGETS_IRON), 1, 9, "iron_from_nugget");
    registerMaterial(consumer, MaterialIds.iron, Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_IRON), 9, 1, "iron_from_block");

    registerMaterial(consumer, MaterialIds.pigiron, Ingredient.fromTag(TinkerTags.Items.INGOTS_PIG_IRON), 1, 1, "pigiron_from_ingot");
    registerMaterial(consumer, MaterialIds.pigiron, Ingredient.fromTag(TinkerTags.Items.NUGGETS_PIG_IRON), 1, 9, "pigiron_from_nugget");
    registerMaterial(consumer, MaterialIds.pigiron, Ingredient.fromTag(TinkerTags.Items.STORAGE_BLOCKS_PIG_IRON), 9, 1, "pigiron_from_block");

    registerMaterial(consumer, MaterialIds.cobalt, Ingredient.fromTag(TinkerTags.Items.INGOTS_COBALT), 1, 1, "cobalt_from_ingot");
    registerMaterial(consumer, MaterialIds.cobalt, Ingredient.fromTag(TinkerTags.Items.NUGGETS_COBALT), 1, 9, "cobalt_from_nugget");
    registerMaterial(consumer, MaterialIds.cobalt, Ingredient.fromTag(TinkerTags.Items.STORAGE_BLOCKS_COBALT), 9, 1, "cobalt_from_block");

    registerMaterial(consumer, MaterialIds.ardite, Ingredient.fromTag(TinkerTags.Items.INGOTS_ARDITE), 1, 1, "ardite_from_ingot");
    registerMaterial(consumer, MaterialIds.ardite, Ingredient.fromTag(TinkerTags.Items.NUGGETS_ARDITE), 1, 9, "ardite_from_nugget");
    registerMaterial(consumer, MaterialIds.ardite, Ingredient.fromTag(TinkerTags.Items.STORAGE_BLOCKS_ARDITE), 9, 1, "ardite_from_block");

    registerMaterial(consumer, MaterialIds.manyullyn, Ingredient.fromTag(TinkerTags.Items.INGOTS_MANYULLYN), 1, 1, "manyullyn_from_ingot");
    registerMaterial(consumer, MaterialIds.manyullyn, Ingredient.fromTag(TinkerTags.Items.NUGGETS_MANYULLYN), 1, 9, "manyullyn_from_nugget");
    registerMaterial(consumer, MaterialIds.manyullyn, Ingredient.fromTag(TinkerTags.Items.STORAGE_BLOCKS_MANYULLYN), 9, 1, "manyullyn_from_block");

    registerMaterial(consumer, MaterialIds.copper, Ingredient.fromTag(TinkerTags.Items.INGOTS_COPPER), 1, 1, "copper_from_ingot");
    registerMaterial(consumer, MaterialIds.copper, Ingredient.fromTag(TinkerTags.Items.NUGGETS_COPPER), 1, 9, "copper_from_nugget");
    registerMaterial(consumer, MaterialIds.copper, Ingredient.fromTag(TinkerTags.Items.STORAGE_BLOCKS_COPPER), 9, 1, "copper_from_block");

    registerMaterial(consumer, MaterialIds.string, Ingredient.fromTag(Tags.Items.STRING), 1, 1, "string");

    registerMaterial(consumer, MaterialIds.slimevine_blue, Ingredient.fromItems(TinkerWorld.blueSlimeVine, TinkerWorld.blueSlimeVineMiddle, TinkerWorld.blueSlimeVineEnd), 1, 1, "slimevine_blue");

    registerMaterial(consumer, MaterialIds.slimevine_purple, Ingredient.fromItems(TinkerWorld.purpleSlimeVine, TinkerWorld.purpleSlimeVineMiddle, TinkerWorld.purpleSlimeVineEnd), 1, 1, "slimevine_purple");

    registerMaterial(consumer, MaterialIds.blaze, Ingredient.fromItems(Items.BLAZE_ROD), 1, 1, "blaze");

    registerMaterial(consumer, MaterialIds.reed, Ingredient.fromItems(Items.SUGAR_CANE), 1, 1, "reed");

    registerMaterial(consumer, MaterialIds.ice, Ingredient.fromItems(Items.PACKED_ICE), 1, 1, "ice");

    registerMaterial(consumer, MaterialIds.endrod, Ingredient.fromItems(Items.END_ROD), 1, 1, "endrod");

    registerMaterial(consumer, MaterialIds.feather, Ingredient.fromItems(Items.FEATHER), 1, 1, "feather");

    registerMaterial(consumer, MaterialIds.leaf, Ingredient.fromTag(ItemTags.LEAVES), 1, 2, "leaf");

    registerMaterial(consumer, MaterialIds.slimeleaf_blue, Ingredient.fromItems(TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.BLUE)), 1, 2, "slimeleaf_blue");
    registerMaterial(consumer, MaterialIds.slimeleaf_orange, Ingredient.fromItems(TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.ORANGE)), 1, 2, "slimeleaf_orange");
    registerMaterial(consumer, MaterialIds.slimeleaf_purple, Ingredient.fromItems(TinkerWorld.slimeLeaves.get(SlimeGrassBlock.FoliageType.PURPLE)), 1, 2, "slimeleaf_purple");
  }

  /* String helpers */
  /**
   * Gets a resource location for Tinkers
   * @param id  Location path
   * @return  Location for Tinkers
   */
  private static ResourceLocation location(String id) {
    return new ResourceLocation(TConstruct.modID, id);
  }
  /**
   * Gets a resource location string for Tinkers
   * @param id  Location path
   * @return  Location for Tinkers
   */
  private static String locationString(String id) {
    return TConstruct.modID + ":" + id;
  }

  /**
   * Prefixes the resource location path with the given value
   * @param item    Item registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  private static ResourceLocation wrap(IItemProvider item, String prefix, String suffix) {
    ResourceLocation loc = Objects.requireNonNull(item.asItem().getRegistryName());
    return location(prefix + loc.getPath() + suffix);
  }

  /**
   * Prefixes the resource location path with the given value
   * @param item    Item registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  private static ResourceLocation prefix(IItemProvider item, String prefix) {
    ResourceLocation loc = Objects.requireNonNull(item.asItem().getRegistryName());
    return location(prefix + loc.getPath());
  }


  /* Helpers */

  /**
   * Adds a recipe to craft a part
   * @param consumer  Recipe consumer
   * @param sup       Part to be crafted
   * @param cost      Part cost
   * @param cast      Part cast
   */
  private void addPartRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends IMaterialItem> sup, int cost, IItemProvider cast) {
    // Base data
    IMaterialItem part = sup.get();
    String name = Objects.requireNonNull(part.asItem().getRegistryName()).getPath();

    // Part Builder
    PartRecipeBuilder.partRecipe(part)
      .setPattern(location(name))
      .setCost(cost)
      .addCriterion("has_item", hasItem(TinkerTables.pattern))
      .build(consumer, location("parts/" + name));

    // Material Casting
    MaterialCastingRecipeBuilder.tableRecipe(part)
      .setFluidAmount(cost * MaterialValues.VALUE_Ingot)
      .setCast(cast, false)
      .addCriterion("has_item", hasItem(cast))
      .build(consumer, location("casting/parts/" + name));

    // Cast Casting
    addCastCastingRecipe(consumer, part, cast, "casting/");

    // Part melting
    MaterialMeltingRecipeBuilder.melting(part, cost * MaterialValues.VALUE_Ingot)
                                .addCriterion("has_item", hasItem(part))
                                .build(consumer, location("melting/parts/" + part));
  }

  /**
   * Registers a material recipe
   * @param consumer  Recipe consumer
   * @param material  Material ID
   * @param input     Recipe input
   * @param value     Material value
   * @param needed    Number of items needed
   * @param saveName  Material save name
   */
  private void registerMaterial(Consumer<IFinishedRecipe> consumer, MaterialId material, Ingredient input, int value, int needed, String saveName) {
    MaterialRecipeBuilder.materialRecipe(material)
      .setIngredient(input)
      .setValue(value)
      .setNeeded(needed)
      .addCriterion("has_item", hasItem(TinkerTables.pattern.get()))
      .build(consumer, location("materials/" + saveName));
  }

  /**
   * Adds a campfire cooking recipe
   * @param consumer    Recipe consumer
   * @param input       Recipe input
   * @param output      Recipe output
   * @param experience  Experience for the recipe
   * @param folder      Folder to store the recipe
   */
  private void addCampfireCooking(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider output, float experience, String folder) {
    CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(input), output, experience, 600, IRecipeSerializer.CAMPFIRE_COOKING)
                        .addCriterion("has_item", hasItem(input))
                        .build(consumer, wrap(output, folder, "_campfire"));
  }

  /**
   * Adds a recipe to the campfire, furnace, and smoker
   * @param consumer    Recipe consumer
   * @param input       Recipe input
   * @param output      Recipe output
   * @param experience  Experience for the recipe
   * @param folder      Folder to store the recipe
   */
  private void addFoodCooking(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider output, float experience, String folder) {
    addCampfireCooking(consumer, input, output, experience, folder);
    // furnace is 200 ticks
    ICriterionInstance criteria = hasItem(input);
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(input), output, experience, 200)
                        .addCriterion("has_item", criteria)
                        .build(consumer, wrap(output, folder, "_furnace"));
    // smoker 100 ticks
    CookingRecipeBuilder.cookingRecipe(Ingredient.fromItems(input), output, experience, 100, IRecipeSerializer.SMOKING)
                        .addCriterion("has_item", criteria)
                        .build(consumer, wrap(output, folder, "_smoker"));
  }

  /**
   * Registers generic building block recipes
   * @param consumer  Recipe consumer
   * @param building  Building object instance
   */
  private void registerSlabStair(Consumer<IFinishedRecipe> consumer, BuildingBlockObject building, String folder, boolean addStonecutter) {
    Item item = building.asItem();
    ICriterionInstance hasBlock = hasItem(item);
    Ingredient ingredient = Ingredient.fromItems(item);
    // slab
    IItemProvider slab = building.getSlab();
    ShapedRecipeBuilder.shapedRecipe(slab, 6)
                       .key('B', item)
                       .patternLine("BBB")
                       .addCriterion("has_item", hasBlock)
                       .setGroup(Objects.requireNonNull(slab.asItem().getRegistryName()).toString())
                       .build(consumer, wrap(item, folder, "_slab"));
    // stairs
    IItemProvider stairs = building.getStairs();
    ShapedRecipeBuilder.shapedRecipe(stairs, 4)
                       .key('B', item)
                       .patternLine("B  ")
                       .patternLine("BB ")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasBlock)
                       .setGroup(Objects.requireNonNull(stairs.asItem().getRegistryName()).toString())
                       .build(consumer, wrap(item, folder, "_stairs"));

    // only add stonecutter if relevant
    if (addStonecutter) {
      SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, slab, 2)
                             .addCriterion("has_item", hasBlock)
                             .build(consumer, wrap(item, folder, "_slab_stonecutter"));
      SingleItemRecipeBuilder.stonecuttingRecipe(ingredient, stairs)
                             .addCriterion("has_item", hasBlock)
                             .build(consumer, wrap(item, folder, "_stairs_stonecutter"));
    }
  }

  /**
   * Adds recipes to convert a block to ingot, ingot to block, and for nuggets
   * @param consumer  Recipe consumer
   * @param block     Block item
   * @param ingot     Ingot item
   * @param nugget    Nugget item
   * @param folder    Folder for recipes
   */
  private void registerMineralRecipes(Consumer<IFinishedRecipe> consumer, IItemProvider block, IItemProvider ingot, @Nullable IItemProvider nugget, String folder) {
    // ingot to block
    ShapedRecipeBuilder.shapedRecipe(block)
                       .key('i', ingot)
                       .patternLine("iii")
                       .patternLine("iii")
                       .patternLine("iii")
                       .addCriterion("has_item", hasItem(ingot))
                       .setGroup(Objects.requireNonNull(block.asItem().getRegistryName()).toString())
                       .build(consumer, wrap(block, folder, "_from_ingots"));
    // block to ingot
    ShapelessRecipeBuilder.shapelessRecipe(ingot, 9)
                          .addIngredient(block)
                          .addCriterion("has_item", hasItem(block))
                          .setGroup(Objects.requireNonNull(ingot.asItem().getRegistryName()).toString())
                          .build(consumer, wrap(ingot, folder, "_from_block"));
    // nugget recipes
    if (nugget != null) {
      // nugget to ingot
      ShapedRecipeBuilder.shapedRecipe(ingot)
                         .key('n', nugget)
                         .patternLine("nnn")
                         .patternLine("nnn")
                         .patternLine("nnn")
                         .addCriterion("has_item", hasItem(nugget))
                         .setGroup(Objects.requireNonNull(ingot.asItem().getRegistryName()).toString())
                         .build(consumer, wrap(ingot, folder, "_from_ingots"));
      // ingot to nugget
      ShapelessRecipeBuilder.shapelessRecipe(nugget, 9)
                            .addIngredient(ingot)
                            .addCriterion("has_item", hasItem(ingot))
                            .setGroup(Objects.requireNonNull(nugget.asItem().getRegistryName()).toString())
                            .build(consumer, wrap(nugget, folder, "_from_ingot"));
    }
  }


  /* Specialized helpers */

  /**
   * Adds a recipe for an item frame type
   * @param consumer  Recipe consumer
   * @param edges     Edge item
   * @param type      Frame type
   */
  private void registerFrameRecipes(Consumer<IFinishedRecipe> consumer, IItemProvider edges, FrameType type) {
    Item frame = TinkerGadgets.itemFrame.get(type);
    ShapedRecipeBuilder.shapedRecipe(frame)
                       .key('e', edges)
                       .key('M', Items.OBSIDIAN)
                       .patternLine(" e ")
                       .patternLine("eMe")
                       .patternLine(" e ")
                       .addCriterion("has_item", hasItem(edges))
                       .setGroup(locationString("fancy_item_frame"))
                       .build(consumer, prefix(frame, "gadgets/fancy_frame/"));

  }

  /**
   * Registers recipes to craft slimy mud
   * @param consumer   Recipe consumer
   * @param slime      Slime type
   * @param extraItem  Extra item to mix with slime
   * @param mud        Mud output
   * @param crystal    Crystal output
   * @param folder     Output folder
   */
  private void registerMudRecipe(Consumer<IFinishedRecipe> consumer, SlimeType slime, @Nullable IItemProvider extraItem, IItemProvider mud, IItemProvider crystal, String folder) {
    Item slimeball = TinkerCommons.slimeball.get(slime);

    // null means use slime for both, so we can add congealed recipe
    if (extraItem == null) {
      Block congealed = TinkerWorld.congealedSlime.get(slime);
      ShapelessRecipeBuilder.shapelessRecipe(mud)
                            .addIngredient(congealed)
                            .addIngredient(Tags.Items.SAND)
                            .addIngredient(Blocks.DIRT)
                            .addCriterion("has_item", hasItem(congealed))
                            .setGroup(locationString("slimy_mud"))
                            .build(consumer, wrap(mud, folder, "_congealed"));
      extraItem = slimeball;
    }
    // base recipe
    ShapelessRecipeBuilder.shapelessRecipe(mud)
                          .addIngredient(slimeball)
                          .addIngredient(slimeball)
                          .addIngredient(extraItem)
                          .addIngredient(extraItem)
                          .addIngredient(Tags.Items.SAND)
                          .addIngredient(Blocks.DIRT)
                          .addCriterion("has_item", hasItem(slimeball))
                          .setGroup(locationString("slimy_mud"))
                          .build(consumer, wrap(mud, folder, "_slimeballs"));
    // crystal smelting
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(mud), crystal, 0.5f, 200)
                        .addCriterion("has_item", hasItem(mud))
                        .build(consumer, wrap(crystal, folder, "_smelting"));
  }


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

  /**
   * Adds a recipe to create a cast
   * @param consumer  Recipe consumer
   * @param input     Item consumed to create cast
   * @param cast      Produced cast
   * @param folder    Output folder
   */
  private void addCastCastingRecipe(Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider cast, String folder) {
    ItemCastingRecipeBuilder.tableRecipe(cast)
      .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot))
      .setCast(input, true)
      .setSwitchSlots()
      .addCriterion("has_item", hasItem(input))
      .build(consumer, location(folder + "casts/" + Objects.requireNonNull(input.asItem().getRegistryName()).getPath()));
  }

  /* Melting */

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
      // TODO: mark as an ore recipe for ore doubling
      ITag<Item> ore = getTag("forge", "ores/" + name);
      MeltingRecipeBuilder.melting(Ingredient.fromTag(ore), fluid, MaterialValues.VALUE_Ingot)
                          .addCriterion("hasItem", hasItem(ore))
                          .build(consumer, location(prefix + "ore"));
    }
  }

  /**
   * Gets a tag by name
   * @param modId  Mod ID for tag
   * @param name   Tag name
   * @return  Tag instance
   */
  private static ITag<Item> getTag(String modId, String name) {
    return ItemTags.makeWrapperTag(modId + ":" + name);
  }

  // Forge constructor is private, not sure if there is a public place for this
  private static class CompoundIngredient extends net.minecraftforge.common.crafting.CompoundIngredient {
    private CompoundIngredient(List<Ingredient> children) {
      super(children);
    }
  }
}
