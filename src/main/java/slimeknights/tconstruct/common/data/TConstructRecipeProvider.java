package slimeknights.tconstruct.common.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;
import slimeknights.tconstruct.shared.block.SlimeBlock;
import slimeknights.tconstruct.shared.block.SlimeBlock.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.SearedTankBlock;
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
    this.addPartBuilderRecipes(consumer);
    this.addMaterialsRecipes(consumer);
  }

  private void addCommonRecipes(Consumer<IFinishedRecipe> consumer) {
    // firewood and lavawood
    String folder = "common/firewood/";
    ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.firewood)
      .addIngredient(Items.BLAZE_POWDER)
      .addIngredient(TinkerCommons.lavawood)
      .addIngredient(Items.BLAZE_POWDER)
      .addCriterion("has_lavawood", this.hasItem(TinkerCommons.lavawood))
      .build(consumer, prefix(TinkerCommons.firewood, folder));
    registerSlabStair(consumer, TinkerCommons.firewood, folder, false);
    registerSlabStair(consumer, TinkerCommons.lavawood, folder, false);
    // FIXME: temporary lavawood recipe
    ShapedRecipeBuilder.shapedRecipe(TinkerCommons.lavawood)
                       .key('p', ItemTags.PLANKS)
                       .key('l', Items.LAVA_BUCKET)
                       .patternLine(" p ")
                       .patternLine("plp")
                       .patternLine(" p ")
                       .addCriterion("has_lava", hasItem(Items.LAVA_BUCKET))
                       .build(consumer, prefix(TinkerCommons.lavawood, folder));

    // graveyard soil
    folder = "common/soil/";
    ShapelessRecipeBuilder.shapelessRecipe(TinkerModifiers.graveyardSoil)
      .addIngredient(Blocks.DIRT)
      .addIngredient(Items.ROTTEN_FLESH)
      .addIngredient(Items.BONE_MEAL)
      .addCriterion("has_dirt", this.hasItem(Blocks.DIRT))
      .addCriterion("has_rotten_flesh", this.hasItem(Items.ROTTEN_FLESH))
      .addCriterion("has_bone_meal", this.hasItem(Items.BONE_MEAL))
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
      .addCriterion("has_mud_brick", this.hasItem(TinkerCommons.mudBrick))
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
      .addCriterion("has_item", this.hasItem(TinkerTables.pattern))
      .build(consumer, prefix(TinkerCommons.book, "common/"));

    // glass
    ShapedRecipeBuilder.shapedRecipe(TinkerCommons.clearGlassPane, 16)
                       .key('#', TinkerCommons.clearGlass)
                       .patternLine("###")
                       .patternLine("###")
                       .addCriterion("has_block", this.hasItem(TinkerCommons.clearGlass))
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
                         .addCriterion("has_clear_glass", this.hasItem(TinkerCommons.clearGlass))
                         .build(consumer, prefix(block, "common/glass/"));
      Block pane = TinkerCommons.clearStainedGlassPane.get(color);
      ShapedRecipeBuilder.shapedRecipe(pane, 16)
                         .key('#', block)
                         .patternLine("###")
                         .patternLine("###")
                         .setGroup(locationString("stained_clear_glass_pane"))
                         .addCriterion("has_block", this.hasItem(block))
                         .build(consumer, prefix(pane, "common/glass/"));
      ShapedRecipeBuilder.shapedRecipe(pane, 8)
                         .key('#', TinkerCommons.clearGlassPane)
                         .key('X', color.getDye().getTag())
                         .patternLine("###")
                         .patternLine("#X#")
                         .patternLine("###")
                         .setGroup(locationString("stained_clear_glass_pane"))
                         .addCriterion("has_clear_glass", this.hasItem(TinkerCommons.clearGlassPane))
                         .build(consumer, wrap(pane, "common/glass/", "_from_panes"));
    }

    // FIXME: temporary clear glass recipe
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromTag(Tags.Items.GLASS_COLORLESS), TinkerCommons.clearGlass, 0.1F, 200)
                        .addCriterion("has_item", this.hasItem(Tags.Items.GLASS_COLORLESS))
                        .build(consumer, wrap(TinkerCommons.clearGlass, "common/glass/", "_from_smelting"));
    CookingRecipeBuilder.smeltingRecipe(Ingredient.fromTag(Tags.Items.GLASS_PANES_COLORLESS), TinkerCommons.clearGlassPane, 0.1F, 200)
                        .addCriterion("has_item", this.hasItem(Tags.Items.GLASS_PANES_COLORLESS))
                        .build(consumer, wrap(TinkerCommons.clearGlassPane, "common/glass/", "_from_smelting"));

    // vanilla recipes
    ResourceLocation flintId = location("common/flint");
    ConditionalRecipe.builder()
      .addCondition(new ConfigOptionEnabledCondition("addGravelToFlintRecipe"))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(Items.FLINT)
        .addIngredient(Blocks.GRAVEL)
        .addIngredient(Blocks.GRAVEL)
        .addIngredient(Blocks.GRAVEL)
        .addCriterion("has_item", this.hasItem(Blocks.GRAVEL))::build)
      .setAdvancement(location("recipes/tinkers_general/common/flint"), ConditionalAdvancement.builder()
        .addCondition(new ConfigOptionEnabledCondition("addGravelToFlintRecipe"))
        .addAdvancement(Advancement.Builder.builder()
          .withParentId(new ResourceLocation("recipes/root"))
          .withRewards(AdvancementRewards.Builder.recipe(flintId))
          .withCriterion("has_item", hasItem(Blocks.GRAVEL))
          .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(flintId))
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
    // FIXME: texture recipe
    ShapedRecipeBuilder.shapedRecipe(TinkerTables.partBuilder)
                       .key('p', TinkerTables.pattern)
                       .key('w', ItemTags.PLANKS)
                       .patternLine("p")
                       .patternLine("w")
                       .addCriterion("has_item", hasItem(TinkerTables.pattern))
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
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeBoots.get(slime))
                         .setGroup("tconstruct:slime_boots")
                         .key('#', TinkerWorld.congealedSlime.get(slime))
                         .key('X', slime.getSlimeBallTag())
                         .patternLine("X X")
                         .patternLine("# #")
                         .addCriterion("has_item", this.hasItem(Items.SLIME_BALL))
                         .build(consumer, location(folder + slime.getName()));
    }
    folder = "gadgets/slimesling/";
    for (SlimeBlock.SlimeType slime : SlimeBlock.SlimeType.values()) {
      ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.slimeSling.get(slime))
                         .setGroup("tconstruct:slimesling")
                         .key('#', Items.STRING)
                         .key('X', TinkerWorld.congealedSlime.get(slime))
                         .key('L', slime.getSlimeBallTag())
                         .patternLine("#X#")
                         .patternLine("L L")
                         .patternLine(" L ")
                         .addCriterion("has_item", this.hasItem(Items.STRING))
                         .build(consumer, location(folder + slime.getName()));
    }

    // rails
    folder = "gadgets/rail/";
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.woodenRail, 4)
                       .key('#', ItemTags.PLANKS)
                       .key('X', Tags.Items.RODS_WOODEN)
                       .patternLine("# #")
                       .patternLine("#X#")
                       .patternLine("# #")
                       .addCriterion("has_item", this.hasItem(ItemTags.PLANKS))
                       .build(consumer, prefix(TinkerGadgets.woodenRail, folder));

    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.woodenDropperRail, 4)
                       .key('#', ItemTags.PLANKS)
                       .key('X', ItemTags.WOODEN_TRAPDOORS)
                       .patternLine("# #")
                       .patternLine("#X#")
                       .patternLine("# #")
                       .addCriterion("has_item", this.hasItem(ItemTags.PLANKS))
                       .build(consumer, prefix(TinkerGadgets.woodenDropperRail, folder));

    // stone
    folder = "gadgets/stone/";
    ShapedRecipeBuilder.shapedRecipe(Blocks.JACK_O_LANTERN)
                       .key('#', Blocks.CARVED_PUMPKIN)
                       .key('X', TinkerGadgets.stoneTorch.get())
                       .patternLine("#")
                       .patternLine("X")
                       .addCriterion("has_item", this.hasItem(Blocks.CARVED_PUMPKIN))
                       .build(consumer, location(folder + "jack_o_lantern"));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneLadder.get(), 3)
                       .key('#', TinkerTags.Items.RODS_STONE)
                       .patternLine("# #")
                       .patternLine("###")
                       .patternLine("# #")
                       .addCriterion("has_item", this.hasItem(TinkerTags.Items.RODS_STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneLadder, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneStick.get(), 4)
                       .key('#', Ingredient.fromItemListStream(Stream.of(
                         new Ingredient.TagList(Tags.Items.STONE),
                         new Ingredient.TagList(Tags.Items.COBBLESTONE))
                       ))
                       .patternLine("#")
                       .patternLine("#")
                       .addCriterion("has_item", this.hasItem(Tags.Items.STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneStick, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.stoneTorch.get(), 4)
                       .key('#', Ingredient.fromItemListStream(Stream.of(
                         new Ingredient.SingleItemList(new ItemStack(Items.COAL)),
                         new Ingredient.SingleItemList(new ItemStack(Items.CHARCOAL))
                       )))
                       .key('X', TinkerTags.Items.RODS_STONE)
                       .patternLine("#")
                       .patternLine("X")
                       .addCriterion("has_item", this.hasItem(TinkerTags.Items.RODS_STONE))
                       .build(consumer, prefix(TinkerGadgets.stoneTorch, folder));

    // throw balls
    ResourceLocation eflnBallId = new ResourceLocation(TConstruct.modID, "gadgets/throwball/efln");
    ConditionalRecipe.builder()
      .addCondition(new TagEmptyCondition("forge", "dusts/sulfur"))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(TinkerGadgets.efln.get())
        .addIngredient(Items.FLINT)
        .addIngredient(Items.GUNPOWDER)
        .addCriterion("has_item", this.hasItem(Tags.Items.DUSTS_GLOWSTONE))::build)
      .addCondition(not(new TagEmptyCondition("forge", "dusts/sulfur")))
      .addRecipe(ShapelessRecipeBuilder.shapelessRecipe(TinkerGadgets.efln.get())
        .addIngredient(TinkerTags.Items.DUSTS_SULFUR)
        .addIngredient(Ingredient.fromItemListStream(Stream.of(
          new Ingredient.TagList(TinkerTags.Items.DUSTS_SULFUR),
          new Ingredient.SingleItemList(new ItemStack(Items.GUNPOWDER)))
        ))
        .addCriterion("has_item", this.hasItem(Items.GUNPOWDER))::build)
      .build(consumer, eflnBallId);
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.glowBall.get(), 8)
      .key('#', Items.SNOWBALL)
      .key('X', Tags.Items.DUSTS_GLOWSTONE)
      .patternLine("###")
      .patternLine("#X#")
      .patternLine("###")
      .addCriterion("has_item", this.hasItem(Tags.Items.DUSTS_GLOWSTONE))
      .build(consumer, wrap(TinkerGadgets.glowBall, "gadgets/throwball/", ""));

    // piggybackpack
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.piggyBackpack.get())
      .key('#', Tags.Items.RODS_WOODEN)
      .key('X', Tags.Items.LEATHER)
      .patternLine(" X ")
      .patternLine("# #")
      .patternLine(" X ")
      .addCriterion("has_item", this.hasItem(Tags.Items.RODS_WOODEN))
      .build(consumer, prefix(TinkerGadgets.piggyBackpack, "gadgets/"));
    ShapedRecipeBuilder.shapedRecipe(TinkerGadgets.punji.get(), 3)
      .key('#', Items.SUGAR_CANE)
      .patternLine("# #")
      .patternLine(" # ")
      .patternLine("# #")
      .addCriterion("has_item", this.hasItem(Items.SUGAR_CANE))
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
    for (SlimeType slime : SlimeType.VISIBLE_COLORS) {
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
    // FIXME: temporary seared cobble recipe
    CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(TinkerSmeltery.searedCrackedBricks), TinkerSmeltery.searedCobble, 0.3f, 300)
                        .addCriterion("has_item", hasItem(TinkerSmeltery.searedCrackedBricks))
                        .build(consumer, wrap(TinkerSmeltery.searedCrackedBricks, folder, "_blasting"));
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
      .addCriterion("has_item", this.hasItem(TinkerSmeltery.searedBrick))
      .build(consumer, location("smeltery/seared/tank"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTank.get(SearedTankBlock.TankType.GAUGE))
      .key('#', TinkerSmeltery.searedBrick)
      .key('B', Tags.Items.GLASS)
      .patternLine("#B#")
      .patternLine("BBB")
      .patternLine("#B#")
      .addCriterion("has_item", this.hasItem(TinkerSmeltery.searedBrick))
      .build(consumer, location("smeltery/seared/gauge"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedTank.get(SearedTankBlock.TankType.WINDOW))
      .key('#', TinkerSmeltery.searedBrick)
      .key('B', Tags.Items.GLASS)
      .patternLine("#B#")
      .patternLine("#B#")
      .patternLine("#B#")
      .addCriterion("has_item", this.hasItem(TinkerSmeltery.searedBrick))
      .build(consumer, location("smeltery/seared/window"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.searedFaucet.get())
      .key('#', TinkerSmeltery.searedBrick)
      .patternLine("# #")
      .patternLine(" # ")
      .addCriterion("has_item", this.hasItem(TinkerSmeltery.searedBrick))
      .build(consumer, location("smeltery/faucet"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.castingBasin.get())
      .key('#', TinkerSmeltery.searedBrick)
      .patternLine("# #")
      .patternLine("# #")
      .patternLine("###")
      .addCriterion("has_item", this.hasItem(TinkerSmeltery.searedBrick))
      .build(consumer, location("smeltery/casting/basin"));

    ShapedRecipeBuilder.shapedRecipe(TinkerSmeltery.castingTable.get())
      .key('#', TinkerSmeltery.searedBrick)
      .patternLine("###")
      .patternLine("# #")
      .patternLine("# #")
      .addCriterion("has_item", this.hasItem(TinkerSmeltery.searedBrick))
      .build(consumer, location("smeltery/casting/table"));
  }

  private void addSlimeRecipes(Consumer<IFinishedRecipe> consumer) {
    // Add recipe for all slimeball<->congealed
    for (SlimeBlock.SlimeType slimeType : SlimeBlock.SlimeType.values()) {
      ShapedRecipeBuilder.shapedRecipe(TinkerWorld.congealedSlime.get(slimeType))
        .key('#', slimeType.getSlimeBallTag())
        .patternLine("##")
        .patternLine("##")
        .addCriterion("has_item", this.hasItem(slimeType.getSlimeBallTag()))
        .setGroup("tconstruct:congealed_slime")
        .build(consumer, "tconstruct:common/slime/" + slimeType.getName() + "/congealed");

      ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.slimeball.get(slimeType), 4)
        .addIngredient(TinkerWorld.congealedSlime.get(slimeType))
        .addCriterion("has_item", this.hasItem(TinkerWorld.congealedSlime.get(slimeType)))
        .setGroup("tconstruct:slime_balls")
        .build(consumer, "tconstruct:common/slime/" + slimeType.getName() + "/slimeball_from_congealed");
    }

    // Don't re add recipe for vanilla slime_block and slime_ball
    for (SlimeBlock.SlimeType slimeType : SlimeBlock.SlimeType.TINKER) {
      ShapedRecipeBuilder.shapedRecipe(TinkerWorld.slime.get(slimeType))
        .key('#', slimeType.getSlimeBallTag())
        .patternLine("###")
        .patternLine("###")
        .patternLine("###")
        .addCriterion("has_item", this.hasItem(slimeType.getSlimeBallTag()))
        .setGroup("tconstruct:slime_blocks")
        .build(consumer, "tconstruct:common/slime/" + slimeType.getName() + "/slimeblock");

      ShapelessRecipeBuilder.shapelessRecipe(TinkerCommons.slimeball.get(slimeType), 9)
        .addIngredient(TinkerWorld.slime.get(slimeType))
        .addCriterion("has_item", this.hasItem(TinkerWorld.slime.get(slimeType)))
        .setGroup("tconstruct:slime_balls")
        .build(consumer, "tconstruct:common/slime/" + slimeType.getName() + "/slimeball_from_block");
    }
  }

  private void addPartBuilderRecipes(Consumer<IFinishedRecipe> consumer) {
    addPartRecipe(consumer, TinkerToolParts.pickaxeHead, 2, "pickaxe_head");
    addPartRecipe(consumer, TinkerToolParts.hammerHead, 8, "hammer_head");
    addPartRecipe(consumer, TinkerToolParts.shovelHead, 2, "shovel_head");
    addPartRecipe(consumer, TinkerToolParts.swordBlade, 2, "sword_blade");
    addPartRecipe(consumer, TinkerToolParts.smallBinding, 1, "small_binding");
    addPartRecipe(consumer, TinkerToolParts.wideGuard, 1, "wide_guard");
    addPartRecipe(consumer, TinkerToolParts.largePlate, 8, "large_plate");
    addPartRecipe(consumer, TinkerToolParts.toolRod, 1, "tool_rod");
    addPartRecipe(consumer, TinkerToolParts.toughToolRod, 3, "tough_tool_rod");
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

    registerMaterial(consumer, MaterialIds.pigiron, Ingredient.fromTag(TinkerTags.Items.INGOTS_PIGIRON), 1, 1, "pigiron_from_ingot");
    registerMaterial(consumer, MaterialIds.pigiron, Ingredient.fromTag(TinkerTags.Items.NUGGETS_PIGIRON), 1, 9, "pigiron_from_nugget");
    registerMaterial(consumer, MaterialIds.pigiron, Ingredient.fromTag(TinkerTags.Items.STORAGE_BLOCKS_PIGIRON), 9, 1, "pigiron_from_block");

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
    return new ResourceLocation(loc.getNamespace(), prefix + loc.getPath() + suffix);
  }

  /**
   * Prefixes the resource location path with the given value
   * @param item    Item registry name to use
   * @param prefix  Prefix value
   * @return  Resource location path
   */
  private static ResourceLocation prefix(IItemProvider item, String prefix) {
    ResourceLocation loc = Objects.requireNonNull(item.asItem().getRegistryName());
    return new ResourceLocation(loc.getNamespace(), prefix + loc.getPath());
  }


  /* Helpers */

  private void addPartRecipe(@Nonnull Consumer<IFinishedRecipe> consumer, IItemProvider part, int cost, String saveName) {
    PartRecipeBuilder.partRecipe(new ItemStack(part))
      .setPattern(location(saveName))
      .setCost(cost)
      .addCriterion("has_item", this.hasItem(TinkerTables.pattern.get()))
      .build(consumer, location("parts/" + saveName));
  }

  private void registerMaterial(@Nonnull Consumer<IFinishedRecipe> consumer, MaterialId material, Ingredient input, int value, int needed, String saveName) {
    MaterialRecipeBuilder.materialRecipe(material)
      .setIngredient(input)
      .setValue(value)
      .setNeeded(needed)
      .addCriterion("has_item", this.hasItem(TinkerTables.pattern.get()))
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
  private void addCampfireCooking(@Nonnull Consumer<IFinishedRecipe> consumer, IItemProvider input, IItemProvider output, float experience, String folder) {
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
  private void registerSlabStair(@Nonnull Consumer<IFinishedRecipe> consumer, BuildingBlockObject building, String folder, boolean addStonecutter) {
    Item item = building.asItem();
    ICriterionInstance hasBlock = hasItem(item);
    Ingredient ingredient = Ingredient.fromItems(item);
    // slab
    Item slab = building.getSlabItem();
    ShapedRecipeBuilder.shapedRecipe(slab, 6)
                       .key('B', item)
                       .patternLine("BBB")
                       .addCriterion("has_item", hasBlock)
                       .setGroup(Objects.requireNonNull(slab.getRegistryName()).toString())
                       .build(consumer, wrap(item, folder, "_slab"));
    // stairs
    Item stairs = building.getStairsItem();
    ShapedRecipeBuilder.shapedRecipe(stairs, 4)
                       .key('B', item)
                       .patternLine("B  ")
                       .patternLine("BB ")
                       .patternLine("BBB")
                       .addCriterion("has_item", hasBlock)
                       .setGroup(Objects.requireNonNull(stairs.getRegistryName()).toString())
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

  // Forge constructor is private, not sure if there is a public place for this
  private static class CompoundIngredient extends net.minecraftforge.common.crafting.CompoundIngredient {
    private CompoundIngredient(List<Ingredient> children) {
      super(children);
    }
  }
}
