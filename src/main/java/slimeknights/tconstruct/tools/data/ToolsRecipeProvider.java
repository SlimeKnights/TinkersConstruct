package slimeknights.tconstruct.tools.data;

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
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.StickySlimeBlock.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.SlimeGrassBlock;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ToolsRecipeProvider extends BaseRecipeProvider {
  public ToolsRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    this.addModifierRecipes(consumer);
    this.addMaterialsRecipes(consumer);
    this.addPartRecipes(consumer);
    this.addTinkerStationRecipes(consumer);
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
    ShapedRecipeBuilder.shapedRecipe(TinkerModifiers.reinforcement)
                       .key('O', Items.OBSIDIAN)
                       .key('G', TinkerSmeltery.blankCast)
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
    registerPackingRecipe(consumer, "block", TinkerModifiers.silkyJewelBlock, "gem", TinkerModifiers.silkyJewel, folder);


    // slimy mud and slime crystals
    registerMudRecipe(consumer, SlimeType.GREEN, null, TinkerModifiers.slimyMudGreen, TinkerModifiers.greenSlimeCrystal, folder);
    registerMudRecipe(consumer, SlimeType.BLUE, null, TinkerModifiers.slimyMudBlue, TinkerModifiers.blueSlimeCrystal, folder);
    registerMudRecipe(consumer, SlimeType.MAGMA, Items.MAGMA_CREAM, TinkerModifiers.slimyMudMagma, TinkerModifiers.magmaSlimeCrystal, folder);
  }

  private void addPartRecipes(Consumer<IFinishedRecipe> consumer) {
    addPartRecipe(consumer, TinkerToolParts.pickaxeHead, 2, TinkerSmeltery.pickaxeHeadCast);
    addPartRecipe(consumer, TinkerToolParts.hammerHead, 8, TinkerSmeltery.hammerHeadCast);
    addPartRecipe(consumer, TinkerToolParts.shovelHead, 2, TinkerSmeltery.shovelHeadCast);
    addPartRecipe(consumer, TinkerToolParts.axeHead, 2, TinkerSmeltery.axeHeadCast);
    addPartRecipe(consumer, TinkerToolParts.excavatorHead, 8, TinkerSmeltery.excavatorHeadCast);
    addPartRecipe(consumer, TinkerToolParts.kamaHead, 2, TinkerSmeltery.kamaHeadCast);
    addPartRecipe(consumer, TinkerToolParts.swordBlade, 2, TinkerSmeltery.swordBladeCast);
    addPartRecipe(consumer, TinkerToolParts.smallBinding, 1, TinkerSmeltery.smallBindingCast);
    addPartRecipe(consumer, TinkerToolParts.toughBinding, 3, TinkerSmeltery.toughBindingCast);
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

    registerMaterial(consumer, MaterialIds.slimevine_blue, Ingredient.fromItems(TinkerWorld.blueSlimeVine), 1, 1, "slimevine_blue");
    registerMaterial(consumer, MaterialIds.slimevine_purple, Ingredient.fromItems(TinkerWorld.purpleSlimeVine), 1, 1, "slimevine_purple");

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

  private void addTinkerStationRecipes(Consumer<IFinishedRecipe> consumer) {
    registerBuildingRecipe(consumer, TinkerTools.pickaxe);
    registerBuildingRecipe(consumer, TinkerTools.hammer);

    registerBuildingRecipe(consumer, TinkerTools.shovel);
    registerBuildingRecipe(consumer, TinkerTools.excavator);

    registerBuildingRecipe(consumer, TinkerTools.axe);

    registerBuildingRecipe(consumer, TinkerTools.kama);

    registerBuildingRecipe(consumer, TinkerTools.broadSword);
  }

  private void registerBuildingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends ToolCore> sup) {
    // Base data
    ToolCore toolCore = sup.get();
    String name = Objects.requireNonNull(toolCore.getRegistryName()).getPath();

    ToolBuildingRecipeBuilder.toolBuildingRecipe(toolCore)
      .addCriterion("has_item", hasItem(TinkerTables.tinkerStation))
      .build(consumer, location("tinker_station/building/" + name));
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
    ItemCastingRecipeBuilder.tableRecipe(cast)
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot))
                            .setCast(MaterialIngredient.fromItem(part), true)
                            .setSwitchSlots()
                            .addCriterion("has_item", hasItem(part))
                            .build(consumer, location("casting/casts/" + Objects.requireNonNull(part.asItem().getRegistryName()).getPath()));

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
}
