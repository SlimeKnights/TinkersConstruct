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
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.StickySlimeBlock.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerWorld;

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
    addPartRecipe(consumer, TinkerToolParts.largePlate, 8, TinkerSmeltery.largePlateCast);
    addPartRecipe(consumer, TinkerToolParts.toolRod, 1, TinkerSmeltery.toolRodCast);
    addPartRecipe(consumer, TinkerToolParts.toughToolRod, 3, TinkerSmeltery.toughToolRodCast);
  }

  private void addMaterialsRecipes(Consumer<IFinishedRecipe> consumer) {
    // tier 1
    registerMaterial(consumer, MaterialIds.wood, Ingredient.fromTag(Tags.Items.RODS_WOODEN), 1, 2, "wood_from_sticks");
    registerMaterial(consumer, MaterialIds.wood, Ingredient.fromTag(ItemTags.PLANKS), 1, 1, "wood_from_planks");
    registerMaterial(consumer, MaterialIds.wood, Ingredient.fromTag(ItemTags.LOGS), 4, 1, "wood_from_logs");
    registerMaterial(consumer, MaterialIds.stone, new CompoundIngredient(
      Ingredient.fromTag(Tags.Items.STONE), Ingredient.fromTag(Tags.Items.COBBLESTONE), Ingredient.fromItems(Blocks.BASALT, Blocks.POLISHED_BASALT, Blocks.POLISHED_BLACKSTONE)
    ), 1, 1, "stone_from_stone");
    registerMaterial(consumer, MaterialIds.flint, Ingredient.fromItems(Items.FLINT), 1, 1, "flint");
    registerMaterial(consumer, MaterialIds.bone, Ingredient.fromTag(Tags.Items.BONES), 1, 1, "bone_from_bones");
    // tier 2
    registerMetalMaterial(consumer, MaterialIds.iron, "iron", false);
    registerMaterial(consumer, MaterialIds.searedStone, Ingredient.fromTag(TinkerTags.Items.SEARED_BLOCKS), 4, 1, "seared_stone_from_block");
    registerMetalMaterial(consumer, MaterialIds.copper, "copper", false);
    registerMaterial(consumer, MaterialIds.slimewood, Ingredient.fromTag(TinkerTags.Items.GREEN_SLIMEBALL), 1, 1, "slime_from_ball");
    registerMaterial(consumer, MaterialIds.slimewood, Ingredient.fromItems(TinkerWorld.congealedSlime.get(SlimeType.GREEN)), 4, 1, "slime_from_congealed");
    registerMaterial(consumer, MaterialIds.slimewood, Ingredient.fromItems(TinkerWorld.slime.get(SlimeType.GREEN)), 5, 1, "slime_from_block");
    // tier 3
    registerMetalMaterial(consumer, MaterialIds.slimesteel, "slimesteel", false);
    registerMaterial(consumer, MaterialIds.nahuatl, Ingredient.fromItems(Items.OBSIDIAN), 1, 1, "nahuatl");
    registerMetalMaterial(consumer, MaterialIds.tinkersBronze, "tinkers_bronze", false);
    registerMetalMaterial(consumer, MaterialIds.roseGold, "rose_gold", false);
    registerMetalMaterial(consumer, MaterialIds.pigIron, "pigiron", false);

    // tier 2 (nether)
    // tier 3 (nether)
    registerMetalMaterial(consumer, MaterialIds.cobalt, "cobalt", false);
    // tier 4
    registerMetalMaterial(consumer, MaterialIds.manyullyn, "manyullyn", false);
    registerMetalMaterial(consumer, MaterialIds.hepatizon, "hepatizon", false);
    registerMetalMaterial(consumer, MaterialIds.slimeBronze, "slime_bronze", false);
    registerMetalMaterial(consumer, MaterialIds.soulsteel, "soulsteel", false);

    // tier 2 (end)
    registerMaterial(consumer, MaterialIds.endstone, Ingredient.fromItems(Blocks.END_STONE), 1, 1, "endstone");

    // tier 2 (mod compat)
    registerMetalMaterial(consumer, MaterialIds.silver, "silver", true);
    registerMetalMaterial(consumer, MaterialIds.lead, "lead", true);
    // tier 3 (mod integration)
    registerMetalMaterial(consumer, MaterialIds.electrum, "electrum", true);
    registerMetalMaterial(consumer, MaterialIds.bronze, "bronze", true);
    registerMetalMaterial(consumer, MaterialIds.steel, "steel", true);

    //registerMaterial(consumer, MaterialIds.string, Ingredient.fromTag(Tags.Items.STRING), 1, 1, "string");
    //registerMaterial(consumer, MaterialIds.slimevine_blue, Ingredient.fromItems(TinkerWorld.blueSlimeVine), 1, 1, "slimevine_blue");
    //registerMaterial(consumer, MaterialIds.slimevine_purple, Ingredient.fromItems(TinkerWorld.purpleSlimeVine), 1, 1, "slimevine_purple");
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
  private void addPartRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends IMaterialItem> sup, int cost, CastItemObject cast) {
    // Base data
    IMaterialItem part = sup.get();
    String name = Objects.requireNonNull(part.asItem().getRegistryName()).getPath();

    // Part Builder
    PartRecipeBuilder.partRecipe(part)
                     .setPattern(location(name))
                     .setCost(cost)
                     .build(consumer, location("parts/" + name));

    // Material Casting
    MaterialCastingRecipeBuilder.tableRecipe(part)
                                .setFluidAmount(cost * MaterialValues.VALUE_Ingot)
                                .setCast(cast, false)
                                .build(consumer, location("casting/parts/" + name));
    MaterialCastingRecipeBuilder.tableRecipe(part)
                                .setFluidAmount(cost * MaterialValues.VALUE_Ingot)
                                .setCast(cast.getSingleUseTag(), true)
                                .build(consumer, location("casting/parts/" + name + "_sand"));

    // Cast Casting
    MaterialIngredient ingredient = MaterialIngredient.fromItem(part);
    String partName = Objects.requireNonNull(part.asItem().getRegistryName()).getPath();
    ItemCastingRecipeBuilder.tableRecipe(cast)
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.VALUE_Ingot))
                            .setCast(ingredient, true)
                            .setSwitchSlots()
                            .build(consumer, location("casting/casts/" + partName));

    // sand cast molding
    MoldingRecipeBuilder.moldingTable(cast.getSand())
                        .setMaterial(TinkerSmeltery.blankCast.getSand())
                        .setMold(ingredient, false)
                        .build(consumer, location("casting/sand_casts/" + partName));
    MoldingRecipeBuilder.moldingTable(cast.getRedSand())
                        .setMaterial(TinkerSmeltery.blankCast.getRedSand())
                        .setMold(ingredient, false)
                        .build(consumer, location("casting/red_sand_casts/" + partName));

    // Part melting
    MaterialMeltingRecipeBuilder.melting(part, cost * MaterialValues.VALUE_Ingot).build(consumer, location("melting/parts/" + part));
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
                         .build(consumer, location("materials/" + saveName));
  }

  /**
   * Register ingots, nuggets, and blocks for a metal material
   * @param consumer  Consumer instance
   * @param material  Material
   * @param name      Material name
   */
  private void registerMetalMaterial(Consumer<IFinishedRecipe> consumer, MaterialId material, String name, boolean optional) {
    Consumer<IFinishedRecipe> wrapped = optional ? withCondition(consumer, tagCondition("ingots/" + name)) : consumer;
    registerMaterial(wrapped, material, Ingredient.fromTag(getTag("forge", "ingots/" + name)), 1, 1, name + "_from_ingot");
    wrapped = optional ? withCondition(consumer, tagCondition("nuggets/" + name)) : consumer;
    registerMaterial(wrapped, material, Ingredient.fromTag(getTag("forge", "nuggets/" + name)), 1, 9, name + "_from_nugget");
    wrapped = optional ? withCondition(consumer, tagCondition("storage_blocks/" + name)) : consumer;
    registerMaterial(wrapped, material, Ingredient.fromTag(getTag("forge", "storage_blocks/" + name)), 9, 1, name + "_from_block");
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
