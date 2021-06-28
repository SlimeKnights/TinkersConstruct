package slimeknights.tconstruct.tools.data;

import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.mantle.recipe.ingredient.IngredientIntersection;
import slimeknights.mantle.recipe.ingredient.IngredientWithout;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.modifiers.SeveringRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierMatch;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.OverslimeModifierRecipeBuilder;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.function.Consumer;

public class ModifierRecipeProvider extends BaseRecipeProvider {
  public ModifierRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Modifier Recipes";
  }

  @Override
  protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
    addItemRecipes(consumer);
    addModifierRecipes(consumer);
    addHeadRecipes(consumer);
  }

  private void addItemRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/modifiers/";

    // reinforcements
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.ironReinforcement)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenIron.get(), MaterialValues.NUGGET * 3))
                            .setCast(TinkerCommons.obsidianPane, true)
                            .build(consumer, prefix(TinkerModifiers.ironReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.slimesteelReinforcement)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenSlimesteel.get(), MaterialValues.NUGGET * 3))
                            .setCast(TinkerCommons.obsidianPane, true)
                            .build(consumer, prefix(TinkerModifiers.slimesteelReinforcement, folder));

    // silky cloth
    ShapedRecipeBuilder.shapedRecipe(TinkerModifiers.silkyCloth)
                       .key('s', Tags.Items.STRING)
                       .key('g', TinkerMaterials.roseGold.getIngotTag())
                       .patternLine("sss")
                       .patternLine("sgs")
                       .patternLine("sss")
                       .addCriterion("has_item", hasItem(Tags.Items.INGOTS_GOLD))
                       .build(consumer, prefix(TinkerModifiers.silkyCloth, folder));

    // slime crystals
    TinkerModifiers.slimeCrystal.forEach((type, crystal) -> {
      IItemProvider slimeball = TinkerCommons.slimeball.get(type);
      CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(slimeball), crystal, 1.0f, 400)
                          .addCriterion("has_item", hasItem(slimeball))
                          .build(consumer, folder + "slime_crystal/" + type.getString());
    });

    // wither bone purifying
    ShapelessRecipeBuilder.shapelessRecipe(Items.BONE)
                          .addIngredient(TinkerTags.Items.WITHER_BONES)
                          .addCriterion("has_bone", hasItem(TinkerTags.Items.WITHER_BONES))
                          .build(withCondition(consumer, ConfigEnabledCondition.WITHER_BONE_CONVERSION), location(folder + "wither_bone_conversion"));
  }

  private void addModifierRecipes(Consumer<IFinishedRecipe> consumer) {
    // upgrades
    String upgradeFolder = "tools/modifiers/upgrade/";
    String abilityFolder = "tools/modifiers/ability/";
    String slotlessFolder = "tools/modifiers/slotless/";

    /*
     * durability
     */
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.reinforced.get())
                         .setInput(TinkerModifiers.ironReinforcement, 1, 24)
                         .setMaxLevel(5) // max 83% resistant to damage
                         .setUpgradeSlots(1)
                         .build(consumer, prefixR(TinkerModifiers.reinforced, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.overforced.get())
                                    .setInput(TinkerModifiers.slimesteelReinforcement, 1, 24)
                                    .setMaxLevel(5) // +250 capacity
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.overforced, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.emerald.get())
                         .addInput(Tags.Items.GEMS_EMERALD)
                         .setMaxLevel(1)
                         .setUpgradeSlots(1)
                         .build(consumer, prefixR(TinkerModifiers.emerald, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.diamond.get())
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .setMaxLevel(1)
                         .setUpgradeSlots(1)
                         .build(consumer, prefixR(TinkerModifiers.diamond, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.worldbound.get())
                         .addInput(Items.NETHERITE_SCRAP)
                         .setMaxLevel(1)
                         .build(consumer, prefixR(TinkerModifiers.worldbound, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.soulbound.get())
                         .addInput(Items.TOTEM_OF_UNDYING)
                         .setUpgradeSlots(1)
                         .setMaxLevel(1)
                         .build(consumer, prefixR(TinkerModifiers.soulbound, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.netherite.get())
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .setMaxLevel(1)
                         .setUpgradeSlots(1)
                         .setRequirements(ModifierMatch.list(1, ModifierMatch.entry(TinkerModifiers.diamond.get()), ModifierMatch.entry(TinkerModifiers.emerald.get())))
                         .setRequirementsError(Util.makeTranslationKey("recipe", "modifier.netherite_requirements"))
                         .build(consumer, prefixR(TinkerModifiers.netherite, upgradeFolder));

    // overslime
    OverslimeModifierRecipeBuilder.modifier(TinkerModifiers.slimeCrystal.get(SlimeType.EARTH), 10)
                                  .build(consumer, location(slotlessFolder + "overslime/earth"));
    OverslimeModifierRecipeBuilder.modifier(TinkerModifiers.slimeCrystal.get(SlimeType.SKY), 40)
                                  .build(consumer, location(slotlessFolder + "overslime/sky"));
    OverslimeModifierRecipeBuilder.modifier(TinkerModifiers.slimeCrystal.get(SlimeType.ICHOR), 100)
                                  .build(consumer, location(slotlessFolder + "overslime/ichor"));
    OverslimeModifierRecipeBuilder.modifier(TinkerModifiers.slimeCrystal.get(SlimeType.ENDER), 200)
                                  .build(consumer, location(slotlessFolder + "overslime/ender"));

    /*
     * general effects
     */
    ModifierRecipeBuilder.modifier(TinkerModifiers.experienced.get())
                         .addInput(Items.EXPERIENCE_BOTTLE)
                         .addInput(Items.EXPERIENCE_BOTTLE)
                         .addInput(Items.EXPERIENCE_BOTTLE)
                         .setMaxLevel(5) // max +250%
                         .setUpgradeSlots(1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .build(consumer, prefixR(TinkerModifiers.experienced, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.magnetic.get())
                         .addInput(Items.COMPASS)
                         .setMaxLevel(5)
                         .setUpgradeSlots(1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .build(consumer, prefixR(TinkerModifiers.magnetic, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.shiny.get())
                         .addInput(Items.ENCHANTED_GOLDEN_APPLE)
                         .setMaxLevel(1)
                         .build(consumer, prefixR(TinkerModifiers.shiny, slotlessFolder));

    /*
     * Speed
     */

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
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.blasting.get())
                                    .setTools(TinkerTags.Items.STONE_HARVEST)
                                    .setInput(Tags.Items.GUNPOWDER, 1, 20)
                                    .setMaxLevel(5) // +50 mining speed at max, conditionally
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.blasting, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.hydraulic.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Tags.Items.DUSTS_PRISMARINE, 1, 36) // stupid forge name
                                    .setMaxLevel(5)
                                    .setUpgradeSlots(1)
                                    .build(consumer, wrapR(TinkerModifiers.hydraulic, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.hydraulic.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Blocks.PRISMARINE, 4, 36)
                                    .setLeftover(new ItemStack(Items.PRISMARINE_SHARD))
                                    .setMaxLevel(5)
                                    .setUpgradeSlots(1)
                                    .build(consumer, wrapR(TinkerModifiers.hydraulic, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.hydraulic.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Blocks.PRISMARINE_BRICKS, 9, 36)
                                    .setLeftover(new ItemStack(Items.PRISMARINE_SHARD))
                                    .setMaxLevel(5)
                                    .setUpgradeSlots(1)
                                    .build(consumer, wrapR(TinkerModifiers.hydraulic, upgradeFolder, "_from_bricks"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.lightspeed.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Tags.Items.DUSTS_GLOWSTONE, 1, 64)
                                    .setMaxLevel(5) // +45 mining speed at max, conditionally
                                    .setUpgradeSlots(1)
                                    .build(consumer, wrapR(TinkerModifiers.lightspeed, upgradeFolder, "_from_dust"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.lightspeed.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Blocks.GLOWSTONE, 4, 64)
                                    .setLeftover(new ItemStack(Items.GLOWSTONE_DUST))
                                    .setMaxLevel(5)
                                    .setUpgradeSlots(1)
                                    .build(consumer, wrapR(TinkerModifiers.lightspeed, upgradeFolder, "_from_block"));

    /*
     * weapon
     */
    ModifierRecipeBuilder.modifier(TinkerModifiers.knockback.get())
                         .addInput(Items.PISTON)
                         .addInput(TinkerTags.Items.SLIME_BLOCK)
                         .setMaxLevel(5) // max +2.5 knockback points (knockback 5) (whatever that number means in vanilla)
                         .setUpgradeSlots(1)
                         .setTools(TinkerTags.Items.MELEE)
                         .build(consumer, prefixR(TinkerModifiers.knockback, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.padded.get())
                         .addInput(Items.LEATHER)
                         .addInput(ItemTags.WOOL)
                         .addInput(Items.LEATHER)
                         .setMaxLevel(3) // max 25% knockback, or 20% on the dagger
                         .setUpgradeSlots(1)
                         .setTools(TinkerTags.Items.MELEE)
                         .build(consumer, prefixR(TinkerModifiers.padded, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.severing.get())
                         .addInput(TinkerTags.Items.WITHER_BONES)
                         .addInput(TinkerMaterials.copper.getIngotTag())
                         .addInput(TinkerTags.Items.WITHER_BONES)
                         .addInput(Items.TNT)
                         .setMaxLevel(5) // max +25% head drop chance, combine with +15% chance from luck
                         .setUpgradeSlots(1)
                         .setTools(TinkerTags.Items.MELEE)
                         .build(consumer, prefixR(TinkerModifiers.severing, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.fiery.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.BLAZE_POWDER, 1, 25)
                                    .setMaxLevel(5) // +25 seconds fire damage
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.fiery, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.necrotic.get())
                         .addInput(Items.WITHER_ROSE)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.BLOOD))
                         .addInput(Items.GHAST_TEAR)
                         .setMaxLevel(5) // +50% chance of heald, combine with +40% from traits for +90% total
                         .setUpgradeSlots(1)
                         .setTools(TinkerTags.Items.MELEE)
                         .build(consumer, prefixR(TinkerModifiers.necrotic, upgradeFolder));

    /*
     * damage boost
     */
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.piercing.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Blocks.CACTUS, 1, 25)
                                    .setMaxLevel(5) // +2.5 pierce damage
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.piercing, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.smite.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.GLISTERING_MELON_SLICE, 1, 5)
                                    .setMaxLevel(5) // +12.5 undead damage
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.smite, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.baneOfSssss.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.FERMENTED_SPIDER_EYE, 1, 15)
                                    .setMaxLevel(5) // +12.5 spider damage
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.baneOfSssss, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.antiaquatic.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.PUFFERFISH, 1, 20)
                                    .setMaxLevel(5) // +12.5 fish damage
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.antiaquatic, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.cooling.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.PRISMARINE_CRYSTALS, 1, 25)
                                    .setMaxLevel(5) // +10 fire mob damage
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.cooling, upgradeFolder));
    // sharpness can use shards or blocks
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sharpness.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.GEMS_QUARTZ, 1, 36)
                                    .setMaxLevel(5) // +5 damage
                                    .setUpgradeSlots(1)
                                    .build(consumer, wrapR(TinkerModifiers.sharpness, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sharpness.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_QUARTZ, 4, 36)
                                    .setLeftover(new ItemStack(Items.QUARTZ))
                                    .setMaxLevel(5)
                                    .setUpgradeSlots(1)
                                    .build(consumer, wrapR(TinkerModifiers.sharpness, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sweeping.get())
                                    .setTools(TinkerTags.Items.SWORD)
                                    .setInput(Blocks.CHAIN, 1, 18) // every 9 is 11 ingots, so this is 22 ingots
                                    .setMaxLevel(4) // goes 50%, 67%, 75%, 80%, level 5 at 83% is not really worthwhile
                                    .setUpgradeSlots(1)
                                    .build(consumer, prefixR(TinkerModifiers.sweeping, upgradeFolder));

    /*
     * ability
     */
    ModifierRecipeBuilder.modifier(TinkerModifiers.gilded.get())
                         .addInput(Items.GOLDEN_APPLE)
                         .setAbilitySlots(1)
                         .build(consumer, prefixR(TinkerModifiers.gilded, abilityFolder));
    // luck is 3 recipes, first uses slots, second 2 do not
    ModifierRecipeBuilder.modifier(TinkerModifiers.luck.get())
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .addInput(TinkerMaterials.copper.getIngotTag())
                         .addInput(Blocks.CORNFLOWER)
                         .addInput(TinkerMaterials.copper.getIngotTag())
                         .addInput(Tags.Items.STORAGE_BLOCKS_LAPIS)
                         .addInput(Tags.Items.STORAGE_BLOCKS_LAPIS)
                         .setMaxLevel(1)
                         .setAbilitySlots(1)
                         .build(consumer, wrapR(TinkerModifiers.luck, abilityFolder, "_level_1"));
    ModifierRecipeBuilder.modifier(TinkerModifiers.luck.get())
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Items.GOLDEN_CARROT)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .setRequirements(ModifierMatch.entry(TinkerModifiers.luck.get(), 1))
                         .setRequirementsError(Util.makeTranslationKey("recipe", "modifier.luck.level_2"))
                         .setMaxLevel(2)
                         .build(consumer, wrapR(TinkerModifiers.luck, abilityFolder, "_level_2"));
    ModifierRecipeBuilder.modifier(TinkerModifiers.luck.get())
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .addInput(TinkerMaterials.roseGold.getIngotTag())
                         .addInput(Items.RABBIT_FOOT)
                         .addInput(TinkerMaterials.roseGold.getIngotTag())
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .addInput(Items.NAME_TAG)
                         .setRequirements(ModifierMatch.entry(TinkerModifiers.luck.get(), 2))
                         .setRequirementsError(Util.makeTranslationKey("recipe", "modifier.luck.level_3"))
                         .setMaxLevel(3)
                         .build(consumer, wrapR(TinkerModifiers.luck, abilityFolder, "_level_3"));
    // silky: all the cloth
    ModifierRecipeBuilder.modifier(TinkerModifiers.silky.get())
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .setMaxLevel(1)
                         .setAbilitySlots(1)
                         .setTools(TinkerTags.Items.HARVEST)
                         .build(consumer, prefixR(TinkerModifiers.silky, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.exchanging.get())
                         .addInput(Items.STICKY_PISTON)
                         .addInput(TinkerMaterials.hepatizon.getIngotTag())
                         .addInput(Items.STICKY_PISTON)
                         .addInput(Items.ENDER_PEARL)
                         .addInput(Items.ENDER_PEARL)
                         .setMaxLevel(1)
                         .setAbilitySlots(1)
                         .setTools(TinkerTags.Items.HARVEST_PRIMARY)
                         .build(consumer, prefixR(TinkerModifiers.exchanging, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.autosmelt.get())
                         .addInput(Items.FIRE_CHARGE)
                         .addInput(Blocks.MAGMA_BLOCK)
                         .addInput(Items.FIRE_CHARGE)
                         .addInput(TinkerCommons.blazewood)
                         .addInput(TinkerCommons.blazewood)
                         .setMaxLevel(1)
                         .setAbilitySlots(1)
                         .setTools(TinkerTags.Items.HARVEST_PRIMARY)
                         .build(consumer, prefixR(TinkerModifiers.autosmelt, abilityFolder));
    // fluid stuff
    ModifierRecipeBuilder.modifier(TinkerModifiers.melting.get())
                         .addInput(Items.BLAZE_ROD)
                         .addInput(TinkerSmeltery.searedMelter)
                         .addInput(Items.BLAZE_ROD)
                         .addInput(Items.LAVA_BUCKET)
                         .addInput(Items.LAVA_BUCKET)
                         .setMaxLevel(1)
                         .setAbilitySlots(1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .build(consumer, prefixR(TinkerModifiers.melting, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.bucketing.get())
                         .addInput(SizedIngredient.fromItems(TinkerSmeltery.searedFaucet, TinkerSmeltery.scorchedFaucet))
                         .addInput(Items.BUCKET)
                         .addInput(SizedIngredient.fromItems(TinkerSmeltery.searedFaucet, TinkerSmeltery.scorchedFaucet))
                         .addInput(Items.ENDER_PEARL)
                         .addInput(Items.ENDER_PEARL)
                         .setMaxLevel(1)
                         .setAbilitySlots(1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .build(consumer, prefixR(TinkerModifiers.bucketing, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.tank.get())
                         .addInput(TinkerTags.Items.TANKS)
                         .setMaxLevel(5)
                         .setUpgradeSlots(1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .build(consumer, prefixR(TinkerModifiers.tank, upgradeFolder));
    // expanders
    ModifierRecipeBuilder.modifier(TinkerModifiers.expanded.get())
                         .addInput(Items.PISTON)
                         .addInput(TinkerMaterials.tinkersBronze.getIngotTag())
                         .addInput(Items.PISTON)
                         .addInput(TinkerTags.Items.ICHOR_SLIMEBALL)
                         .addInput(TinkerTags.Items.ICHOR_SLIMEBALL)
                         .setAbilitySlots(1)
                         .setTools(TinkerTags.Items.AOE)
                         .build(consumer, prefixR(TinkerModifiers.expanded, abilityFolder));
    // reach expander
    ModifierRecipeBuilder.modifier(TinkerModifiers.reach.get())
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .addInput(Items.PISTON)
                         .addInput(TinkerMaterials.queensSlime.getIngotTag())
                         .addInput(Items.PISTON)
                         .addInput(TinkerTags.Items.ENDER_SLIMEBALL)
                         .addInput(TinkerTags.Items.ENDER_SLIMEBALL)
                         .setAbilitySlots(1)
                         .build(consumer, prefixR(TinkerModifiers.reach, abilityFolder));
    // block transformers
    ModifierRecipeBuilder.modifier(TinkerModifiers.pathing.get())
                         .setTools(new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.HARVEST_PRIMARY), Ingredient.fromItems(TinkerTools.mattock, TinkerTools.excavator)))
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.pickaxeHead.get())))
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.smallAxeHead.get())))
                         .setMaxLevel(1)
                         .setAbilitySlots(1)
                         .build(consumer, prefixR(TinkerModifiers.pathing, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.stripping.get())
                         .setTools(new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.HARVEST_PRIMARY), Ingredient.fromItems(TinkerTools.handAxe, TinkerTools.broadAxe)))
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.smallAxeHead.get())))
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.toolBinding.get())))
                         .setMaxLevel(1)
                         .setAbilitySlots(1)
                         .build(consumer, prefixR(TinkerModifiers.stripping, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.tilling.get())
                         .setTools(new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.HARVEST_PRIMARY), Ingredient.fromItems(TinkerTools.kama, TinkerTools.scythe)))
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.smallBlade.get())))
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.toolBinding.get())))
                         .setMaxLevel(1)
                         .setAbilitySlots(1)
                         .build(consumer, prefixR(TinkerModifiers.tilling, abilityFolder));
    // glowing
    ModifierRecipeBuilder.modifier(TinkerModifiers.glowing.get())
                         .addInput(Items.GLOWSTONE)
                         .addInput(Items.DAYLIGHT_DETECTOR)
                         .addInput(Items.SHROOMLIGHT)
                         .setMaxLevel(1)
                         .setAbilitySlots(1)
                         .build(consumer, prefixR(TinkerModifiers.glowing, abilityFolder));
    // unbreakable
    ModifierRecipeBuilder.modifier(TinkerModifiers.unbreakable.get())
                         .addInput(Items.SHULKER_SHELL)
                         .addInput(Items.DRAGON_BREATH)
                         .addInput(Items.SHULKER_SHELL)
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .setMaxLevel(1)
                         .setAbilitySlots(1)
                         .setRequirements(ModifierMatch.list(2, ModifierMatch.entry(TinkerModifiers.netherite.get(), 1), ModifierMatch.entry(TinkerModifiers.reinforced.get(), 5)))
                         .setRequirementsError(Util.makeTranslationKey("recipe", "modifier.unbreakable_requirements"))
                         .build(consumer, prefixR(TinkerModifiers.unbreakable, abilityFolder));
    // weapon
    ModifierRecipeBuilder.modifier(TinkerModifiers.duelWielding.get())
                         .addInput(TinkerMaterials.manyullyn.getIngotTag())
                         .addInput(Items.NAUTILUS_SHELL)
                         .addInput(TinkerMaterials.manyullyn.getIngotTag())
                         .addInput(TinkerTags.Items.SKY_SLIMEBALL)
                         .addInput(TinkerTags.Items.SKY_SLIMEBALL)
                         .setMaxLevel(1)
                         .setAbilitySlots(1)
                         .setTools(new IngredientWithout(new IngredientIntersection(Ingredient.fromTag(TinkerTags.Items.MELEE), Ingredient.fromTag(TinkerTags.Items.ONE_HANDED)), Ingredient.fromItems(TinkerTools.dagger)))
                         .build(consumer, prefixR(TinkerModifiers.duelWielding, abilityFolder));
    
    /*
     * extra modifiers
     */
    ModifierRecipeBuilder.modifier(TinkerModifiers.writable.get())
                         .addInput(Items.WRITABLE_BOOK)
                         .setMaxLevel(1)
                         .build(consumer, prefixR(TinkerModifiers.writable, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.harmonious.get())
                         .addInput(ItemTags.MUSIC_DISCS)
                         .setMaxLevel(1)
                         .build(consumer, prefixR(TinkerModifiers.harmonious, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.recapitated.get())
                         .addInput(SizedIngredient.of(new IngredientWithout(Ingredient.fromTag(Tags.Items.HEADS), Ingredient.fromItems(Items.DRAGON_HEAD))))
                         .setMaxLevel(1)
                         .build(consumer, prefixR(TinkerModifiers.recapitated, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.resurrected.get())
                         .addInput(Items.END_CRYSTAL)
                         .setMaxLevel(1)
                         .build(consumer, prefixR(TinkerModifiers.resurrected, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.draconic.get())
                         .addInput(Items.DRAGON_HEAD)
                         .setMaxLevel(1)
                         .build(consumer, wrapR(TinkerModifiers.draconic, slotlessFolder, "_from_head"));
    ModifierRecipeBuilder.modifier(TinkerModifiers.draconic.get())
                         .addInput(TinkerModifiers.dragonScale)
                         .addInput(TinkerModifiers.dragonScale)
                         .addInput(TinkerModifiers.dragonScale)
                         .addInput(TinkerModifiers.dragonScale)
                         .addInput(TinkerModifiers.dragonScale)
                         .setMaxLevel(1)
                         .build(consumer, wrapR(TinkerModifiers.draconic, slotlessFolder, "_from_scales"));
    // creative
    ModifierRecipeBuilder.modifier(TinkerModifiers.creativeUpgrade.get())
                         .addInput(TinkerModifiers.creativeUpgradeItem)
                         .build(consumer, prefixR(TinkerModifiers.creativeUpgrade, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.creativeAbility.get())
                         .addInput(TinkerModifiers.creativeAbilityItem)
                         .build(consumer, prefixR(TinkerModifiers.creativeAbility, slotlessFolder));

    // removal
    // TODO: migrate to a newer system
//    ModifierRemovalRecipe.Builder.removal(Ingredient.fromItems(Blocks.WET_SPONGE), new ItemStack(Blocks.SPONGE))
//                                 .build(consumer, location(slotlessFolder + "remove_modifier"));
  }

  private void addHeadRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/severing/";
    // first, beheading
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED), Items.ZOMBIE_HEAD)
												 .build(consumer, location(folder + "zombie_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SKELETON, EntityType.STRAY), Items.SKELETON_SKULL)
												 .build(consumer, location(folder + "skeleton_skull"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.WITHER_SKELETON, EntityType.WITHER), Items.WITHER_SKELETON_SKULL)
												 .build(consumer, location(folder + "wither_skeleton_skull"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CREEPER), Items.CREEPER_HEAD)
												 .build(consumer, location(folder + "creeper_head"));
    CustomRecipeBuilder.customRecipe(TinkerModifiers.playerBeheadingSerializer.get()).build(consumer, locationString(folder + "player_head"));
    CustomRecipeBuilder.customRecipe(TinkerModifiers.snowGolemBeheadingSerializer.get()).build(consumer, locationString(folder + "snow_golem_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.IRON_GOLEM), Blocks.CARVED_PUMPKIN)
                         .build(consumer, location(folder + "iron_golem_head"));

    // other body parts
    // hostile
    // beeyeing
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SPIDER, EntityType.CAVE_SPIDER), Items.SPIDER_EYE)
                         .build(consumer, location(folder + "spider_eye"));
    // be-internal-combustion-device
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CREEPER), Blocks.TNT)
                         .build(consumer, location(folder + "creeper_tnt"));
    // bemembraning?
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.PHANTOM), Items.PHANTOM_MEMBRANE)
                         .build(consumer, location(folder + "phantom_membrane"));
    // beshelling
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SHULKER), Items.SHULKER_SHELL)
                         .build(consumer, location(folder + "shulker_shell"));
    // deboning
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.STRAY), ItemOutput.fromStack(new ItemStack(Items.BONE, 2)))
                         .build(consumer, location(folder + "skeleton_bone"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.WITHER_SKELETON), ItemOutput.fromStack(new ItemStack(TinkerModifiers.necroticBone, 2)))
                         .build(consumer, location(folder + "wither_skeleton_bone"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.BLAZE), ItemOutput.fromStack(new ItemStack(Items.BLAZE_ROD, 2)))
                         .build(consumer, location(folder + "blaze_rod"));
    // desliming (you cut off a chunk of slime)
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SLIME, TinkerWorld.earthSlimeEntity.get()), Items.SLIME_BALL)
                         .build(consumer, location(folder + "earthslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.skySlimeEntity.get()), TinkerCommons.slimeball.get(SlimeType.SKY))
                         .build(consumer, location(folder + "skyslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.enderSlimeEntity.get()), TinkerCommons.slimeball.get(SlimeType.ENDER))
                         .build(consumer, location(folder + "enderslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.MAGMA_CUBE), Items.MAGMA_CREAM)
                         .build(consumer, location(folder + "magma_cream"));
    // descaling? I don't know what to call those
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN), ItemOutput.fromStack(new ItemStack(Items.PRISMARINE_SHARD, 2)))
                         .build(consumer, location(folder + "guardian_shard"));

    // passive
    // befeating
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.RABBIT), Items.RABBIT_FOOT)
                         .setChildOutput(null) // only adults
												 .build(consumer, location(folder + "rabbit_foot"));
    // befeathering
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CHICKEN), ItemOutput.fromStack(new ItemStack(Items.FEATHER, 2)))
                         .setChildOutput(null) // only adults
                         .build(consumer, location(folder + "chicken_feather"));
    // beshrooming
    CustomRecipeBuilder.customRecipe(TinkerModifiers.mooshroomDemushroomingSerializer.get()).build(consumer, locationString(folder + "mooshroom_shroom"));
    // beshelling
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.TURTLE), Items.TURTLE_HELMET)
                         .setChildOutput(ItemOutput.fromItem(Items.SCUTE))
                         .build(consumer, location(folder + "turtle_shell"));
    // befleecing
    CustomRecipeBuilder.customRecipe(TinkerModifiers.sheepShearing.get()).build(consumer, locationString(folder + "sheep_wool"));
  }
}
