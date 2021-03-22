package slimeknights.tconstruct.tools.data;

import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.ItemStack;
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
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierMatch;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.OverslimeModifierRecipeBuilder;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ToolsRecipeProvider extends BaseRecipeProvider {
  public ToolsRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Tool Recipes";
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
    ShapedRecipeBuilder.shapedRecipe(TinkerModifiers.ichorExpander)
                       .key('P', Items.PISTON)
                       .key('L', TinkerMaterials.tinkersBronze.getIngotTag())
                       .key('S', TinkerTags.Items.ICHOR_SLIMEBALL)
                       .patternLine(" P ")
                       .patternLine("SLS")
                       .patternLine(" P ")
                       .addCriterion("has_item", hasItem(TinkerTags.Items.ICHOR_SLIMEBALL))
                       .build(consumer, prefix(TinkerModifiers.ichorExpander, folder));
    ShapedRecipeBuilder.shapedRecipe(TinkerModifiers.enderExpander)
                       .key('P', Items.PISTON)
                       .key('L', TinkerMaterials.manyullyn.getIngotTag())
                       .key('S', TinkerTags.Items.ENDER_SLIMEBALL)
                       .patternLine(" P ")
                       .patternLine("SLS")
                       .patternLine(" P ")
                       .addCriterion("has_item", hasItem(TinkerTags.Items.ENDER_SLIMEBALL))
                       .build(consumer, prefix(TinkerModifiers.enderExpander, folder));

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

    // slime crystals
    TinkerModifiers.slimeCrystal.forEach((type, crystal) -> {
      IItemProvider slimeball = TinkerCommons.slimeball.get(type);
      CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(slimeball), crystal, 1.0f, 400)
                          .addCriterion("has_item", hasItem(slimeball))
                          .build(consumer, folder + "slime_crystal/" + type.getString());
    });

    // upgrades
    String upgradeFolder = folder + "upgrade/";

    // tier 2
    ModifierRecipeBuilder.modifier(TinkerModifiers.reinforced.get())
                         .addInput(TinkerModifiers.reinforcement)
                         .setMaxLevel(5) // max 83% resistant to damage
                         .setUpgradeSlots(1)
                         .build(consumer, prefixR(TinkerModifiers.reinforced, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.experienced.get())
                         .addInput(Items.EXPERIENCE_BOTTLE, 5)
                         .setMaxLevel(5) // max +250%
                         .setUpgradeSlots(1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .build(consumer, prefixR(TinkerModifiers.experienced, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.knockback.get())
                         .addInput(Items.PISTON)
                         .setMaxLevel(5) // max +2.5 knockback points (knockback 5) (whatever that number means in vanilla)
                         .setUpgradeSlots(1)
                         .setTools(TinkerTags.Items.MELEE)
                         .build(consumer, prefixR(TinkerModifiers.knockback, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.magnetic.get())
                         .addInput(Items.COMPASS)
                         .setMaxLevel(5)
                         .setUpgradeSlots(1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .build(consumer, prefixR(TinkerModifiers.magnetic, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.emerald.get())
                         .addInput(Tags.Items.GEMS_EMERALD)
                         .setMaxLevel(1)
                         .setUpgradeSlots(1)
                         .build(consumer, prefixR(TinkerModifiers.emerald, upgradeFolder));

    // haste can use redstone or blocks
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.haste.get())
                                    .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                                    .setInput(Tags.Items.DUSTS_REDSTONE, 1, 45)
                                    .setMaxLevel(5) // +25 mining speed, vanilla +26
                                    .setUpgradeSlots(1)
                                    .build(consumer, wrapR(TinkerModifiers.haste, upgradeFolder, "_from_dust"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.haste.get())
                                    .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_REDSTONE, 9, 45)
                                    .setLeftover(new ItemStack(Items.REDSTONE))
                                    .setMaxLevel(5)
                                    .setUpgradeSlots(1)
                                    .build(consumer, wrapR(TinkerModifiers.haste, upgradeFolder, "_from_block"));

    // luck can use lapis or blocks
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.luck.get())
                                    .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                                    .setInput(Tags.Items.GEMS_LAPIS, 1, 108) // 36 per effective level
                                    .setMaxLevel(2)
                                    .setAbilitySlots(1)
                                    .build(consumer, wrapR(TinkerModifiers.luck, upgradeFolder, "_from_dust"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.luck.get())
                                    .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_LAPIS, 9, 108)
                                    .setLeftover(new ItemStack(Items.LAPIS_LAZULI))
                                    .setMaxLevel(2)
                                    .setAbilitySlots(1)
                                    .build(consumer, wrapR(TinkerModifiers.luck, upgradeFolder, "_from_block"));
    // mob damage
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.smite.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.GLISTERING_MELON_SLICE, 1, 5)
                                    .setMaxLevel(5) // +12.5 undead damage
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.smite, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.baneOfArthropods.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.FERMENTED_SPIDER_EYE, 1, 15)
                                    .setMaxLevel(5) // +12.5 spider damage
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.baneOfArthropods, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.antiaquatic.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Blocks.CACTUS, 1, 40)
                                    .setMaxLevel(5) // +12.5 fish damage
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.antiaquatic, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.cooling.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.PRISMARINE_CRYSTALS, 1, 25)
                                    .setMaxLevel(5) // +5.5 fire mob damage
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.cooling, upgradeFolder));

    // tier 3
    ModifierRecipeBuilder.modifier(TinkerModifiers.silky.get())
                         .addInput(TinkerModifiers.silkyJewel)
                         .setMaxLevel(1)
                         .setAbilitySlots(1)
                         .setTools(TinkerTags.Items.HARVEST)
                         .build(consumer, prefixR(TinkerModifiers.silky, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.diamond.get())
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .setMaxLevel(1)
                         .setUpgradeSlots(1)
                         .build(consumer, prefixR(TinkerModifiers.diamond, upgradeFolder));

    // tier 4
    ModifierRecipeBuilder.modifier(TinkerModifiers.worldbound.get())
                         .addInput(Items.NETHERITE_SCRAP)
                         .setMaxLevel(1)
                         .build(consumer, prefixR(TinkerModifiers.worldbound, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.netherite.get())
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .setMaxLevel(1)
                         .setUpgradeSlots(1)
                         .setRequirements(ModifierMatch.list(1, ModifierMatch.entry(TinkerModifiers.diamond.get()), ModifierMatch.entry(TinkerModifiers.emerald.get())))
                         .setRequirementsError(Util.makeTranslationKey("recipe", "modifier.netherite_requirements"))
                         .build(consumer, prefixR(TinkerModifiers.netherite, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.expanded.get())
                         .addInput(TinkerModifiers.ichorExpander)
                         .setAbilitySlots(1)
                         .setMaxLevel(1)
                         .setTools(TinkerTags.Items.AOE)
                         .build(consumer, wrapR(TinkerModifiers.expanded, upgradeFolder, "_ichor"));
    // fiery
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.fiery.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.BLAZE_POWDER, 1, 25)
                                    .setMaxLevel(5) // +25 seconds fire damage
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.fiery, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.necrotic.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(TinkerTags.Items.WITHER_BONES, 1, 10)
                                    .setMaxLevel(5) // +50% chance for 10% life steel
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.necrotic, upgradeFolder));

    // sharpness can use shards or blocks
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sharpness.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.GEMS_QUARTZ, 1, 36)
                                    .setMaxLevel(5) // +3 damage
                                    .setUpgradeSlots(1)
                                    .build(consumer, wrapR(TinkerModifiers.sharpness, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sharpness.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_QUARTZ, 4, 36)
                                    .setLeftover(new ItemStack(Items.QUARTZ))
                                    .setMaxLevel(5)
                                    .setUpgradeSlots(1)
                                    .build(consumer, wrapR(TinkerModifiers.sharpness, upgradeFolder, "_from_block"));

    // tier 5
    ModifierRecipeBuilder.modifier(TinkerModifiers.expanded.get())
                         .addInput(TinkerModifiers.enderExpander)
                         .setRequirements(ModifierMatch.entry(TinkerModifiers.expanded.get(), 1))
                         .setRequirementsError(Util.makeTranslationKey("recipe", "modifier.ender_expander_requirements"))
                         .setAbilitySlots(1)
                         .setMaxLevel(2)
                         .setTools(TinkerTags.Items.AOE)
                         .build(consumer, wrapR(TinkerModifiers.expanded, upgradeFolder, "_ender"));

    // extra modifiers
    ModifierRecipeBuilder.modifier(TinkerModifiers.writable.get())
                         .addInput(Items.WRITABLE_BOOK)
                         .setMaxLevel(1)
                         .build(consumer, prefixR(TinkerModifiers.writable, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.harmonious.get())
                         .addInput(ItemTags.MUSIC_DISCS)
                         .setMaxLevel(1)
                         .build(consumer, prefixR(TinkerModifiers.harmonious, upgradeFolder));
    // TODO: dragon head is currently included in this, do we want a different item for dragon kill?
    ModifierRecipeBuilder.modifier(TinkerModifiers.recapitated.get())
                         .addInput(Tags.Items.HEADS)
                         .setMaxLevel(1)
                         .build(consumer, prefixR(TinkerModifiers.recapitated, upgradeFolder));

    // creative
    ModifierRecipeBuilder.modifier(TinkerModifiers.creativeUpgrade.get())
                         .addInput(TinkerModifiers.creativeUpgradeItem)
                         .build(consumer, prefixR(TinkerModifiers.creativeUpgrade, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.creativeAbility.get())
                         .addInput(TinkerModifiers.creativeAbilityItem)
                         .build(consumer, prefixR(TinkerModifiers.creativeAbility, upgradeFolder));

    // overslime
    OverslimeModifierRecipeBuilder.modifier(TinkerModifiers.slimeCrystal.get(SlimeType.EARTH), 10)
                                  .build(consumer, location(upgradeFolder + "overslime/earth"));
    OverslimeModifierRecipeBuilder.modifier(TinkerModifiers.slimeCrystal.get(SlimeType.SKY), 40)
                                  .build(consumer, location(upgradeFolder + "overslime/sky"));
    OverslimeModifierRecipeBuilder.modifier(TinkerModifiers.slimeCrystal.get(SlimeType.ICHOR), 100)
                                  .build(consumer, location(upgradeFolder + "overslime/ichor"));
    OverslimeModifierRecipeBuilder.modifier(TinkerModifiers.slimeCrystal.get(SlimeType.ENDER), 200)
                                  .build(consumer, location(upgradeFolder + "overslime/ender"));
  }

  private void addPartRecipes(Consumer<IFinishedRecipe> consumer) {
    addPartRecipe(consumer, TinkerToolParts.pickaxeHead, 2, TinkerSmeltery.pickaxeHeadCast);
    addPartRecipe(consumer, TinkerToolParts.hammerHead, 8, TinkerSmeltery.hammerHeadCast);
    addPartRecipe(consumer, TinkerToolParts.axeHead, 2, TinkerSmeltery.axeHeadCast);
    addPartRecipe(consumer, TinkerToolParts.kamaHead, 2, TinkerSmeltery.kamaHeadCast);
    addPartRecipe(consumer, TinkerToolParts.swordBlade, 2, TinkerSmeltery.swordBladeCast);
    addPartRecipe(consumer, TinkerToolParts.toolBinding, 1, TinkerSmeltery.toolBindingCast);
    addPartRecipe(consumer, TinkerToolParts.largePlate, 4, TinkerSmeltery.largePlateCast);
    addPartRecipe(consumer, TinkerToolParts.toolRod, 1, TinkerSmeltery.toolRodCast);
    addPartRecipe(consumer, TinkerToolParts.toughToolRod, 3, TinkerSmeltery.toughToolRodCast);
  }

  private void addMaterialsRecipes(Consumer<IFinishedRecipe> consumer) {
    // tier 1
    registerMaterial(consumer, MaterialIds.wood, Ingredient.fromTag(Tags.Items.RODS_WOODEN), 1, 2, "wood/sticks");
    registerMaterial(consumer, MaterialIds.wood, Ingredient.fromTag(ItemTags.PLANKS), 1, 1, "wood/planks");
    registerMaterial(consumer, MaterialIds.wood, Ingredient.fromTag(ItemTags.LOGS), 4, 1, "wood/logs");
    registerMaterial(consumer, MaterialIds.stone, new CompoundIngredient(
      Ingredient.fromTag(Tags.Items.STONE), Ingredient.fromTag(Tags.Items.COBBLESTONE), Ingredient.fromItems(Blocks.BASALT, Blocks.POLISHED_BASALT, Blocks.POLISHED_BLACKSTONE)
    ), 1, 1, "stone");
    registerMaterial(consumer, MaterialIds.flint, Ingredient.fromItems(Items.FLINT), 1, 1, "flint");
    registerMaterial(consumer, MaterialIds.bone, Ingredient.fromTag(Tags.Items.BONES), 1, 1, "bone");
    // tier 2
    registerMetalMaterial(consumer, MaterialIds.iron, "iron", false);
    registerMaterial(consumer, MaterialIds.searedStone, Ingredient.fromItems(TinkerSmeltery.searedBrick), 1, 1, "seared_stone/brick");
    registerMaterial(consumer, MaterialIds.searedStone, Ingredient.fromTag(TinkerTags.Items.SEARED_BLOCKS), 4, 1, "seared_stone/block");
    registerMetalMaterial(consumer, MaterialIds.copper, "copper", false);
    registerMaterial(consumer, MaterialIds.slimewood, Ingredient.fromTag(TinkerTags.Items.EARTH_SLIMEBALL), 1, 1, "slimewood/ball");
    registerMaterial(consumer, MaterialIds.slimewood, Ingredient.fromItems(TinkerWorld.congealedSlime.get(SlimeType.EARTH)), 4, 1, "slimewood/congealed");
    registerMaterial(consumer, MaterialIds.slimewood, Ingredient.fromItems(TinkerWorld.slime.get(SlimeType.EARTH)), 5, 1, "slimewood/block");
    registerMetalMaterial(consumer, MaterialIds.roseGold, "rose_gold", false);
    // tier 3
    registerMetalMaterial(consumer, MaterialIds.slimesteel, "slimesteel", false);
    registerMaterial(consumer, MaterialIds.nahuatl, Ingredient.fromItems(Items.OBSIDIAN), 1, 1, "nahuatl");
    registerMetalMaterial(consumer, MaterialIds.tinkersBronze, "tinkers_bronze", false);
    registerMetalMaterial(consumer, MaterialIds.pigIron, "pigiron", false);

    // tier 2 (nether)
    // tier 3 (nether)
    registerMetalMaterial(consumer, MaterialIds.cobalt, "cobalt", false);
    // tier 4
    registerMetalMaterial(consumer, MaterialIds.queensSlime, "queens_slime", false);
    registerMetalMaterial(consumer, MaterialIds.manyullyn,   "manyullyn",    false);
    registerMetalMaterial(consumer, MaterialIds.hepatizon,   "hepatizon",    false);
    //registerMetalMaterial(consumer, MaterialIds.soulsteel,   "soulsteel",    false);

    // tier 2 (end)
    //registerMaterial(consumer, MaterialIds.endstone, Ingredient.fromItems(Blocks.END_STONE), 1, 1, "endstone");

    // tier 2 (mod compat)
    registerMetalMaterial(consumer, MaterialIds.silver,   "silver",  true);
    registerMetalMaterial(consumer, MaterialIds.lead,     "lead",     true);
    registerMetalMaterial(consumer, MaterialIds.electrum, "electrum", true);
    // tier 3 (mod integration)
    registerMetalMaterial(consumer, MaterialIds.bronze,     "bronze",     true);
    registerMetalMaterial(consumer, MaterialIds.steel,      "steel",      true);
    registerMetalMaterial(consumer, MaterialIds.constantan, "constantan", true);

    //registerMaterial(consumer, MaterialIds.string, Ingredient.fromTag(Tags.Items.STRING), 1, 1, "string");
    //registerMaterial(consumer, MaterialIds.slimevine_sky, Ingredient.fromItems(TinkerWorld.skySlimeVine), 1, 1, "slimevine_sky");
    //registerMaterial(consumer, MaterialIds.slimevine_ender, Ingredient.fromItems(TinkerWorld.enderSlimeVine), 1, 1, "slimevine_ender");
  }

  private void addTinkerStationRecipes(Consumer<IFinishedRecipe> consumer) {
    registerBuildingRecipe(consumer, TinkerTools.pickaxe);
    registerBuildingRecipe(consumer, TinkerTools.sledgeHammer);

    registerBuildingRecipe(consumer, TinkerTools.mattock);
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
      .build(consumer, location("tools/building/" + name));
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
    String folder = "tools/parts/";
    // Base data
    IMaterialItem part = sup.get();
    String name = Objects.requireNonNull(part.asItem().getRegistryName()).getPath();

    // Part Builder
    PartRecipeBuilder.partRecipe(part)
                     .setPattern(location(name))
                     .setCost(cost)
                     .build(consumer, location(folder + "builder/" + name));

    // Material Casting
    String castingFolder = folder + "casting/";
    MaterialCastingRecipeBuilder.tableRecipe(part)
                                .setItemCost(cost)
                                .setCast(cast, false)
                                .build(consumer, location(castingFolder + name + "_gold_cast"));
    MaterialCastingRecipeBuilder.tableRecipe(part)
                                .setItemCost(cost)
                                .setCast(cast.getSingleUseTag(), true)
                                .build(consumer, location(castingFolder + name + "_sand_cast"));

    // Cast Casting
    MaterialIngredient ingredient = MaterialIngredient.fromItem(part);
    String partName = Objects.requireNonNull(part.asItem().getRegistryName()).getPath();
    ItemCastingRecipeBuilder.tableRecipe(cast)
                            .setFluid(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.INGOT))
                            .setCast(ingredient, true)
                            .setSwitchSlots()
                            .build(consumer, location("smeltery/casting/casts/" + partName));

    // sand cast molding
    MoldingRecipeBuilder.moldingTable(cast.getSand())
                        .setMaterial(TinkerSmeltery.blankCast.getSand())
                        .setPattern(ingredient, false)
                        .build(consumer, location("smeltery/casting/sand_casts/" + partName));
    MoldingRecipeBuilder.moldingTable(cast.getRedSand())
                        .setMaterial(TinkerSmeltery.blankCast.getRedSand())
                        .setPattern(ingredient, false)
                        .build(consumer, location("smeltery/casting/red_sand_casts/" + partName));

    // Part melting
    MaterialMeltingRecipeBuilder.melting(part, cost).build(consumer, location(folder + "melting/" + part));
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
                         .build(consumer, location("tools/materials/" + saveName));
  }

  /**
   * Register ingots, nuggets, and blocks for a metal material
   * @param consumer  Consumer instance
   * @param material  Material
   * @param name      Material name
   */
  private void registerMetalMaterial(Consumer<IFinishedRecipe> consumer, MaterialId material, String name, boolean optional) {
    Consumer<IFinishedRecipe> wrapped = optional ? withCondition(consumer, tagCondition("ingots/" + name)) : consumer;
    registerMaterial(wrapped, material, Ingredient.fromTag(getTag("forge", "ingots/" + name)), 1, 1, name + "/ingot");
    wrapped = optional ? withCondition(consumer, tagCondition("nuggets/" + name)) : consumer;
    registerMaterial(wrapped, material, Ingredient.fromTag(getTag("forge", "nuggets/" + name)), 1, 9, name + "/nugget");
    wrapped = optional ? withCondition(consumer, tagCondition("storage_blocks/" + name)) : consumer;
    registerMaterial(wrapped, material, Ingredient.fromTag(getTag("forge", "storage_blocks/" + name)), 9, 1, name + "/block");
  }
}
