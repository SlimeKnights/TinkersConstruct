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
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.recipe.ModifierRemovalRecipe;
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
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenIron.get(), FluidValues.NUGGET * 3))
                            .setCast(TinkerCommons.obsidianPane, true)
                            .build(consumer, prefix(TinkerModifiers.ironReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.slimesteelReinforcement)
                            .setFluidAndTime(new FluidStack(TinkerFluids.moltenSlimesteel.get(), FluidValues.NUGGET * 3))
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
                          .build(withCondition(consumer, ConfigEnabledCondition.WITHER_BONE_CONVERSION), modResource(folder + "wither_bone_conversion"));
  }

  private void addModifierRecipes(Consumer<IFinishedRecipe> consumer) {
    // upgrades
    String upgradeFolder = "tools/modifiers/upgrade/";
    String abilityFolder = "tools/modifiers/ability/";
    String slotlessFolder = "tools/modifiers/slotless/";
    String upgradeSalvage = "tools/modifiers/salvage/upgrade/";
    String abilitySalvage = "tools/modifiers/salvage/ability/";
    String slotlessSalvage = "tools/modifiers/salvage/slotless/";

    /*
     * durability
     */
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.reinforced.get())
                         .setInputSalvage(TinkerModifiers.ironReinforcement, 1, 24)
                         .setMaxLevel(5) // max 75% resistant to damage
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.DURABILITY)
                         .buildSalvage(consumer, prefix(TinkerModifiers.reinforced, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.reinforced, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.overforced.get())
                                    .setInputSalvage(TinkerModifiers.slimesteelReinforcement, 1, 24)
                                    .setMaxLevel(5) // +250 capacity
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setTools(TinkerTags.Items.DURABILITY)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.overforced, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.overforced, upgradeFolder));
    // gems are special, I'd like them to be useful on all types of tools
    ModifierRecipeBuilder.modifier(TinkerModifiers.emerald.get())
                         .addInput(Tags.Items.GEMS_EMERALD)
                         .addSalvage(Items.EMERALD, 0.5f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.emerald, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.emerald, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.diamond.get())
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .addSalvage(Items.DIAMOND, 0.65f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.diamond, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.diamond, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.worldbound.get())
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.worldbound, slotlessSalvage))
                         .build(consumer, prefix(TinkerModifiers.worldbound, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.soulbound.get())
                         .addInputSalvage(Items.TOTEM_OF_UNDYING, 0.7f)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.soulbound, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.soulbound, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.netherite.get())
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .addSalvage(Items.NETHERITE_INGOT, 0.6f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setRequirements(ModifierMatch.list(1, ModifierMatch.entry(TinkerModifiers.diamond.get()), ModifierMatch.entry(TinkerModifiers.emerald.get())))
                         .setRequirementsError(makeRequirementsError("netherite_requirements"))
                         .buildSalvage(consumer, prefix(TinkerModifiers.netherite, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.netherite, upgradeFolder));

    // overslime
    OverslimeModifierRecipeBuilder.modifier(TinkerModifiers.slimeCrystal.get(SlimeType.EARTH), 10)
                                  .build(consumer, modResource(slotlessFolder + "overslime/earth"));
    OverslimeModifierRecipeBuilder.modifier(TinkerModifiers.slimeCrystal.get(SlimeType.SKY), 40)
                                  .build(consumer, modResource(slotlessFolder + "overslime/sky"));
    OverslimeModifierRecipeBuilder.modifier(TinkerModifiers.slimeCrystal.get(SlimeType.ICHOR), 100)
                                  .build(consumer, modResource(slotlessFolder + "overslime/ichor"));
    OverslimeModifierRecipeBuilder.modifier(TinkerModifiers.slimeCrystal.get(SlimeType.ENDER), 200)
                                  .build(consumer, modResource(slotlessFolder + "overslime/ender"));

    /*
     * general effects
     */
    ModifierRecipeBuilder.modifier(TinkerModifiers.experienced.get())
                         .addInput(Items.EXPERIENCE_BOTTLE)
                         .addInput(Items.EXPERIENCE_BOTTLE)
                         .addInput(Items.EXPERIENCE_BOTTLE)
                         .addSalvage(Items.EXPERIENCE_BOTTLE, 3)
                         .setMaxLevel(5) // max +250%
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .buildSalvage(consumer, prefix(TinkerModifiers.experienced, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.experienced, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.magnetic.get())
                         .addInputSalvage(Items.COMPASS, 0.5f)
                         .setMaxLevel(5)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .buildSalvage(consumer, prefix(TinkerModifiers.magnetic, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.magnetic, upgradeFolder));
    // no salvage so we can potentially grant shiny in another way without being an apple farm, and no recipe as that leaves nothing to salvage
    ModifierRecipeBuilder.modifier(TinkerModifiers.shiny.get())
                         .addInput(Items.ENCHANTED_GOLDEN_APPLE)
                         .setMaxLevel(1)
                         .build(consumer, prefix(TinkerModifiers.shiny, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.offhanded.get())
                         .addInputSalvage(Items.LEATHER, 0.7f)
                         .addInputSalvage(Items.PHANTOM_MEMBRANE, 0.3f)
                         .addInput(TinkerTags.Items.ICHOR_SLIMEBALL)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.offhanded, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.offhanded, upgradeFolder));

    /*
     * Speed
     */

    // haste can use redstone or blocks
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.haste.get())
                                    .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                                    .setInput(Tags.Items.DUSTS_REDSTONE, 1, 45)
                                    .setSalvage(Items.REDSTONE)
                                    .setMaxLevel(5) // +25 mining speed, vanilla +26
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.haste, upgradeSalvage))
                                    .build(consumer, wrap(TinkerModifiers.haste, upgradeFolder, "_from_dust"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.haste.get())
                                    .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_REDSTONE, 9, 45)
                                    .setLeftover(new ItemStack(Items.REDSTONE))
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .build(consumer, wrap(TinkerModifiers.haste, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.blasting.get())
                                    .setTools(TinkerTags.Items.STONE_HARVEST)
                                    .setInput(Tags.Items.GUNPOWDER, 1, 20)
                                    .setSalvage(Items.GUNPOWDER)
                                    .setMaxLevel(5) // +50 mining speed at max, conditionally
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.blasting, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.blasting, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.hydraulic.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Tags.Items.DUSTS_PRISMARINE, 1, 36) // stupid forge name
                                    .setSalvage(Items.PRISMARINE_SHARD)
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.hydraulic, upgradeSalvage))
                                    .build(consumer, wrap(TinkerModifiers.hydraulic, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.hydraulic.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Blocks.PRISMARINE, 4, 36)
                                    .setLeftover(new ItemStack(Items.PRISMARINE_SHARD))
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .build(consumer, wrap(TinkerModifiers.hydraulic, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.hydraulic.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Blocks.PRISMARINE_BRICKS, 9, 36)
                                    .setLeftover(new ItemStack(Items.PRISMARINE_SHARD))
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .build(consumer, wrap(TinkerModifiers.hydraulic, upgradeFolder, "_from_bricks"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.lightspeed.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Tags.Items.DUSTS_GLOWSTONE, 1, 64)
                                    .setSalvage(Items.GLOWSTONE_DUST)
                                    .setMaxLevel(5) // +45 mining speed at max, conditionally
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.lightspeed, upgradeSalvage))
                                    .build(consumer, wrap(TinkerModifiers.lightspeed, upgradeFolder, "_from_dust"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.lightspeed.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Blocks.GLOWSTONE, 4, 64)
                                    .setLeftover(new ItemStack(Items.GLOWSTONE_DUST))
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .build(consumer, wrap(TinkerModifiers.lightspeed, upgradeFolder, "_from_block"));

    /*
     * weapon
     */
    ModifierRecipeBuilder.modifier(TinkerModifiers.knockback.get())
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInputSalvage(TinkerTags.Items.SLIME_BLOCK, 0.3f)
                         .setMaxLevel(5) // max +2.5 knockback points (knockback 5) (whatever that number means in vanilla)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE)
                         .buildSalvage(consumer, prefix(TinkerModifiers.knockback, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.knockback, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.padded.get())
                         .addInput(Items.LEATHER)
                         .addInput(ItemTags.WOOL)
                         .addInput(Items.LEATHER)
                         .addSalvage(Items.LEATHER, 2)
                         .setMaxLevel(3) // max 12.5% knockback, or 6.25% on the dagger
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE)
                         .buildSalvage(consumer, prefix(TinkerModifiers.padded, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.padded, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.severing.get())
                         .addInput(TinkerTags.Items.WITHER_BONES)
                         .addInputSalvage(TinkerMaterials.copper.getIngotTag(), 1.0f)
                         .addInput(TinkerTags.Items.WITHER_BONES)
                         .addInput(Items.TNT)
                         .addSalvage(TinkerMaterials.necroticBone, 2)
                         .setMaxLevel(5) // max +25% head drop chance, combine with +15% chance from luck
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE)
                         .buildSalvage(consumer, prefix(TinkerModifiers.severing, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.severing, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.fiery.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.BLAZE_POWDER, 1, 25)
                                    .setMaxLevel(5) // +25 seconds fire damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.fiery, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.fiery, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.necrotic.get())
                         .addInputSalvage(Items.WITHER_ROSE, 0.1f)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.BLOOD))
                         .addInput(Items.GHAST_TEAR)
                         .setMaxLevel(5) // +50% chance of heal, combine with +40% from traits for +90% total
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE)
                         .buildSalvage(consumer, prefix(TinkerModifiers.necrotic, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.necrotic, upgradeFolder));

    /*
     * damage boost
     */
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.piercing.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Blocks.CACTUS, 1, 25)
                                    .setMaxLevel(5) // +2.5 pierce damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.piercing, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.piercing, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.smite.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.GLISTERING_MELON_SLICE, 1, 5)
                                    .setMaxLevel(5) // +12.5 undead damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.smite, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.smite, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.baneOfSssss.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.FERMENTED_SPIDER_EYE, 1, 15)
                                    .setMaxLevel(5) // +12.5 spider damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.baneOfSssss, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.baneOfSssss, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.antiaquatic.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.PUFFERFISH, 1, 20)
                                    .setMaxLevel(5) // +12.5 fish damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.antiaquatic, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.antiaquatic, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.cooling.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.PRISMARINE_CRYSTALS, 1, 25)
                                    .setMaxLevel(5) // +10 fire mob damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.cooling, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.cooling, upgradeFolder));
    // sharpness can use shards or blocks
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sharpness.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.GEMS_QUARTZ, 1, 36)
                                    .setSalvage(Items.QUARTZ)
                                    .setMaxLevel(5) // +5 damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.sharpness, upgradeSalvage))
                                    .build(consumer, wrap(TinkerModifiers.sharpness, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sharpness.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_QUARTZ, 4, 36)
                                    .setLeftover(new ItemStack(Items.QUARTZ))
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .build(consumer, wrap(TinkerModifiers.sharpness, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sweeping.get())
                                    .setTools(TinkerTags.Items.SWORD)
                                    .setInputSalvage(Blocks.CHAIN, 1, 18) // every 9 is 11 ingots, so this is 22 ingots
                                    .setMaxLevel(4) // goes 25%, 50%, 75%, 100%
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.sweeping, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.sweeping, upgradeFolder));

    /*
     * ability
     */
    ModifierRecipeBuilder.modifier(TinkerModifiers.gilded.get())
                         .addInput(Items.GOLDEN_APPLE)
                         .addSalvage(Items.APPLE, 0.75f)
                         .addSalvage(Items.GOLD_INGOT, 1, 8)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.gilded, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.gilded, abilityFolder));
    // luck is 3 recipes, first uses slots, second 2 do not
    ModifierRecipeBuilder.modifier(TinkerModifiers.luck.get())
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .addInput(TinkerMaterials.copper.getIngotTag())
                         .addInputSalvage(Blocks.CORNFLOWER, 0.1f)
                         .addInput(TinkerMaterials.copper.getIngotTag())
                         .addInput(Tags.Items.STORAGE_BLOCKS_LAPIS)
                         .addInput(Tags.Items.STORAGE_BLOCKS_LAPIS)
                         .addSalvage(TinkerMaterials.copper.getIngotTag(), 2, 2)
                         .addSalvage(Items.LAPIS_LAZULI, 3, 18)
                         .setSalvageLevelRange(1, 1)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, wrap(TinkerModifiers.luck, abilitySalvage, "_level_1"))
                         .build(consumer, wrap(TinkerModifiers.luck, abilityFolder, "_level_1"));
    ModifierRecipeBuilder.modifier(TinkerModifiers.luck.get())
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Items.GOLDEN_CARROT)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .addSalvage(Items.GOLD_INGOT, 2, 3)
                         .addSalvage(Items.GOLD_NUGGET, 1, 8)
                         .addSalvage(Items.CARROT, 0.75f) // all the magic is gone, its just a carrot now
                         .addSalvage(Items.ENDER_PEARL, 2)
                         .setRequirements(ModifierMatch.entry(TinkerModifiers.luck.get(), 1))
                         .setRequirementsError(makeRequirementsError("luck.level_2"))
                         .setSalvageLevelRange(2, 2)
                         .setMaxLevel(2)
                         .buildSalvage(consumer, wrap(TinkerModifiers.luck, abilitySalvage, "_level_2"))
                         .build(consumer, wrap(TinkerModifiers.luck, abilityFolder, "_level_2"));
    ModifierRecipeBuilder.modifier(TinkerModifiers.luck.get())
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .addInput(TinkerMaterials.roseGold.getIngotTag())
                         .addInputSalvage(Items.RABBIT_FOOT, 0.15f)
                         .addInput(TinkerMaterials.roseGold.getIngotTag())
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .addInputSalvage(Items.NAME_TAG, 0.1f)
                         .addSalvage(Items.DIAMOND, 0.65f)
                         .addSalvage(TinkerMaterials.roseGold.getIngotTag(), 2, 2)
                         .setRequirements(ModifierMatch.entry(TinkerModifiers.luck.get(), 2))
                         .setRequirementsError(makeRequirementsError("luck.level_3"))
                         .setSalvageLevelRange(3, 3)
                         .setMaxLevel(3)
                         .buildSalvage(consumer, wrap(TinkerModifiers.luck, abilitySalvage, "_level_3"))
                         .build(consumer, wrap(TinkerModifiers.luck, abilityFolder, "_level_3"));
    // silky: all the cloth
    ModifierRecipeBuilder.modifier(TinkerModifiers.silky.get())
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addSalvage(TinkerModifiers.silkyCloth, 3, 5)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HARVEST)
                         .buildSalvage(consumer, prefix(TinkerModifiers.silky, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.silky, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.exchanging.get())
                         .addInput(Items.STICKY_PISTON)
                         .addInputSalvage(TinkerMaterials.hepatizon.getIngotTag(), 1.0f)
                         .addInput(Items.STICKY_PISTON)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .addSalvage(Items.PISTON, 0.9f) // piston lost its sticky from all that swapping
                         .addSalvage(Items.PISTON, 0.9f)
                         .addSalvage(Items.ENDER_PEARL, 2)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HARVEST_PRIMARY)
                         .buildSalvage(consumer, prefix(TinkerModifiers.exchanging, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.exchanging, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.autosmelt.get())
                         .addInput(Items.FIRE_CHARGE)
                         .addInputSalvage(Blocks.MAGMA_BLOCK, 0.4f)
                         .addInput(Items.FIRE_CHARGE)
                         .addInput(TinkerCommons.blazewood)
                         .addInput(TinkerCommons.blazewood)
                         .addSalvage(TinkerCommons.blazewood, 1, 2)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HARVEST_PRIMARY)
                         .buildSalvage(consumer, prefix(TinkerModifiers.autosmelt, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.autosmelt, abilityFolder));
    // fluid stuff
    ModifierRecipeBuilder.modifier(TinkerModifiers.melting.get())
                         .addInput(Items.BLAZE_ROD)
                         .addInput(TinkerSmeltery.searedMelter)
                         .addInput(Items.BLAZE_ROD)
                         .addInput(Items.LAVA_BUCKET)
                         .addInput(Items.LAVA_BUCKET)
                         .addSalvage(TinkerSmeltery.searedBrick, 4, 9)
                         .addSalvage(Items.BLAZE_ROD, 2)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .buildSalvage(consumer, prefix(TinkerModifiers.melting, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.melting, abilityFolder));
    SizedIngredient faucets = SizedIngredient.fromItems(TinkerSmeltery.searedFaucet, TinkerSmeltery.scorchedFaucet); // no salvage as don't want conversion between seared and scorched
    ModifierRecipeBuilder.modifier(TinkerModifiers.bucketing.get())
                         .addInput(faucets)
                         .addInputSalvage(Items.BUCKET, 1.0f)
                         .addInput(faucets)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .addSalvage(Items.ENDER_PEARL, 2)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .buildSalvage(consumer, prefix(TinkerModifiers.bucketing, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.bucketing, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.tank.get())
                         .addInput(TinkerTags.Items.TANKS) // no salvage as don't want conversion between seared and scorched
                         .setMaxLevel(5)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .buildSalvage(consumer, prefix(TinkerModifiers.tank, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.tank, upgradeFolder));
    // expanders
    ModifierRecipeBuilder.modifier(TinkerModifiers.expanded.get())
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInputSalvage(TinkerMaterials.tinkersBronze.getIngotTag(), 1.0f)
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInput(TinkerTags.Items.ICHOR_SLIMEBALL)
                         .addInput(TinkerTags.Items.ICHOR_SLIMEBALL)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.AOE)
                         .buildSalvage(consumer, prefix(TinkerModifiers.expanded, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.expanded, abilityFolder));
    // reach expander
    ModifierRecipeBuilder.modifier(TinkerModifiers.reach.get())
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInputSalvage(TinkerMaterials.queensSlime.getIngotTag(), 1.0f)
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInput(TinkerTags.Items.ENDER_SLIMEBALL)
                         .addInput(TinkerTags.Items.ENDER_SLIMEBALL)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.reach, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.reach, abilityFolder));
    // block transformers
    ModifierRecipeBuilder.modifier(TinkerModifiers.pathing.get())
                         .setTools(new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.HARVEST_PRIMARY), Ingredient.fromItems(TinkerTools.mattock, TinkerTools.excavator)))
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.pickaxeHead.get())))
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.smallAxeHead.get())))
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.pathing, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.pathing, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.stripping.get())
                         .setTools(new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.HARVEST_PRIMARY), Ingredient.fromItems(TinkerTools.handAxe, TinkerTools.broadAxe)))
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.smallAxeHead.get())))
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.toolBinding.get())))
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.stripping, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.stripping, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.tilling.get())
                         .setTools(new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.HARVEST_PRIMARY), Ingredient.fromItems(TinkerTools.kama, TinkerTools.scythe)))
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.smallBlade.get())))
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.toolBinding.get())))
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.tilling, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.tilling, abilityFolder));
    // glowing
    ModifierRecipeBuilder.modifier(TinkerModifiers.glowing.get())
                         .addInput(Items.GLOWSTONE)
                         .addInputSalvage(Items.DAYLIGHT_DETECTOR, 0.9f)
                         .addInputSalvage(Items.SHROOMLIGHT, 0.4f)
                         .addSalvage(Items.GLOWSTONE_DUST, 1, 4)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.glowing, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.glowing, abilityFolder));
    // unbreakable
    ModifierRecipeBuilder.modifier(TinkerModifiers.unbreakable.get())
                         .addInput(Items.SHULKER_SHELL)
                         .addInput(Items.DRAGON_BREATH)
                         .addInput(Items.SHULKER_SHELL)
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .addSalvage(Items.NETHERITE_INGOT, 2, 2)
                         .addSalvage(Items.SHULKER_SHELL, 0, 2)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setRequirements(ModifierMatch.list(2, ModifierMatch.entry(TinkerModifiers.netherite.get(), 1), ModifierMatch.entry(TinkerModifiers.reinforced.get(), 5)))
                         .setRequirementsError(makeRequirementsError("unbreakable_requirements"))
                         .buildSalvage(consumer, prefix(TinkerModifiers.unbreakable, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.unbreakable, abilityFolder));
    // weapon
    ModifierRecipeBuilder.modifier(TinkerModifiers.dualWielding.get())
                         .addInput(TinkerMaterials.manyullyn.getIngotTag())
                         .addInput(Items.NAUTILUS_SHELL)
                         .addInput(TinkerMaterials.manyullyn.getIngotTag())
                         .addInput(TinkerTags.Items.SKY_SLIMEBALL)
                         .addInput(TinkerTags.Items.SKY_SLIMEBALL)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(new IngredientWithout(new IngredientIntersection(Ingredient.fromTag(TinkerTags.Items.MELEE), Ingredient.fromTag(TinkerTags.Items.ONE_HANDED)), Ingredient.fromItems(TinkerTools.dagger)))
                         .build(consumer, wrap(TinkerModifiers.dualWielding, abilityFolder, "_one_handed"));
    ModifierRecipeBuilder.modifier(TinkerModifiers.dualWielding.get())
                         .addInput(TinkerMaterials.manyullyn.getIngotTag())
                         .addInput(Items.NAUTILUS_SHELL)
                         .addInput(TinkerMaterials.manyullyn.getIngotTag())
                         .addInput(TinkerTags.Items.SKY_SLIMEBALL)
                         .addInput(TinkerTags.Items.SKY_SLIMEBALL)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setRequirements(ModifierMatch.entry(TinkerModifiers.offhanded.get()))
                         .setRequirementsError(makeRequirementsError("two_handed_dual_wielding"))
                         .setTools(new IngredientIntersection(Ingredient.fromTag(TinkerTags.Items.MELEE), Ingredient.fromTag(TinkerTags.Items.TWO_HANDED)))
                         .build(consumer, wrap(TinkerModifiers.dualWielding, abilityFolder, "_two_handed"));
    // using a single salvage recipe for the two of them, as we don't care about requirements when removing
    ModifierRecipeBuilder.modifier(TinkerModifiers.dualWielding.get())
                         .addSalvage(Items.NAUTILUS_SHELL, 0.95f)
                         .addSalvage(TinkerMaterials.manyullyn.getIngotTag(), 2, 2)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.MELEE), Ingredient.fromItems(TinkerTools.dagger)))
                         .buildSalvage(consumer, prefix(TinkerModifiers.dualWielding, abilitySalvage));
    /*
     * extra modifiers
     */
    ModifierRecipeBuilder.modifier(TinkerModifiers.writable.get())
                         .addInputSalvage(Items.WRITABLE_BOOK, 1.0f)
                         .setMaxLevel(1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.writable, slotlessSalvage))
                         .build(consumer, prefix(TinkerModifiers.writable, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.harmonious.get())
                         .addInput(ItemTags.MUSIC_DISCS)
                         .addSalvage(Items.MUSIC_DISC_11, 1.0f) // your disc broke, now it sounds scary
                         .setMaxLevel(1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.harmonious, slotlessSalvage))
                         .build(consumer, prefix(TinkerModifiers.harmonious, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.recapitated.get()) // you want your head back? that is gross!
                         .addInput(SizedIngredient.of(new IngredientWithout(Ingredient.fromTag(Tags.Items.HEADS), Ingredient.fromItems(Items.DRAGON_HEAD))))
                         .setMaxLevel(1)
                         .build(consumer, prefix(TinkerModifiers.recapitated, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.resurrected.get())
                         .addInput(Items.END_CRYSTAL)
                         .addSalvage(Items.ENDER_EYE, 1.0f) // ironic, it could save others, but it could not save itself
                         .setMaxLevel(1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.resurrected, slotlessSalvage))
                         .build(consumer, prefix(TinkerModifiers.resurrected, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.draconic.get())
                         .addInput(Items.DRAGON_HEAD)
                         .setMaxLevel(1)
                         .build(consumer, wrap(TinkerModifiers.draconic, slotlessFolder, "_from_head"));
    ModifierRecipeBuilder.modifier(TinkerModifiers.draconic.get())
                         .addInput(Items.SHULKER_SHELL)
                         .addInputSalvage(TinkerModifiers.dragonScale, 1.0f) // you can apply the modifier in two ways, but scales are cheap so give them
                         .addInput(Blocks.WITHER_ROSE)
                         .addInputSalvage(TinkerModifiers.dragonScale, 0.5f)
                         .addInputSalvage(TinkerModifiers.dragonScale, 0.25f)
                         .setMaxLevel(1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.draconic, slotlessSalvage))
                         .build(consumer, wrap(TinkerModifiers.draconic, slotlessFolder, "_from_scales"));
    // creative
    // no salvage. I am not adding a recipe for creative modifiers, nope. don't want the gift from the server admin to be abused as a upgrade source
    CustomRecipeBuilder.customRecipe(TinkerModifiers.creativeSlotSerializer.get()).build(consumer, slotlessFolder + "creative_slot");

    // removal
    // temporary removal recipe until a proper table is added
    ModifierRemovalRecipe.Builder.removal(Ingredient.fromItems(Blocks.WET_SPONGE), new ItemStack(Blocks.SPONGE))
                                 .build(consumer, modResource(slotlessFolder + "remove_modifier"));
  }

  private void addHeadRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/severing/";
    // first, beheading
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.ZOMBIE, EntityType.HUSK, EntityType.DROWNED), Items.ZOMBIE_HEAD)
												 .build(consumer, modResource(folder + "zombie_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SKELETON, EntityType.STRAY), Items.SKELETON_SKULL)
												 .build(consumer, modResource(folder + "skeleton_skull"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.WITHER_SKELETON, EntityType.WITHER), Items.WITHER_SKELETON_SKULL)
												 .build(consumer, modResource(folder + "wither_skeleton_skull"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CREEPER), Items.CREEPER_HEAD)
												 .build(consumer, modResource(folder + "creeper_head"));
    CustomRecipeBuilder.customRecipe(TinkerModifiers.playerBeheadingSerializer.get()).build(consumer, modPrefix(folder + "player_head"));
    CustomRecipeBuilder.customRecipe(TinkerModifiers.snowGolemBeheadingSerializer.get()).build(consumer, modPrefix(folder + "snow_golem_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.IRON_GOLEM), Blocks.CARVED_PUMPKIN)
                         .build(consumer, modResource(folder + "iron_golem_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.ENDER_DRAGON), Items.DRAGON_HEAD)
                         .build(consumer, modResource(folder + "ender_dragon_head"));

    // other body parts
    // hostile
    // beeyeing
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SPIDER, EntityType.CAVE_SPIDER), Items.SPIDER_EYE)
                         .build(consumer, modResource(folder + "spider_eye"));
    // be-internal-combustion-device
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CREEPER), Blocks.TNT)
                         .build(consumer, modResource(folder + "creeper_tnt"));
    // bemembraning?
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.PHANTOM), Items.PHANTOM_MEMBRANE)
                         .build(consumer, modResource(folder + "phantom_membrane"));
    // beshelling
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SHULKER), Items.SHULKER_SHELL)
                         .build(consumer, modResource(folder + "shulker_shell"));
    // deboning
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.STRAY), ItemOutput.fromStack(new ItemStack(Items.BONE, 2)))
                         .build(consumer, modResource(folder + "skeleton_bone"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.WITHER_SKELETON), ItemOutput.fromStack(new ItemStack(TinkerMaterials.necroticBone, 2)))
                         .build(consumer, modResource(folder + "wither_skeleton_bone"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.BLAZE), ItemOutput.fromStack(new ItemStack(Items.BLAZE_ROD, 2)))
                         .build(consumer, modResource(folder + "blaze_rod"));
    // desliming (you cut off a chunk of slime)
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SLIME, TinkerWorld.earthSlimeEntity.get()), Items.SLIME_BALL)
                         .build(consumer, modResource(folder + "earthslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.skySlimeEntity.get()), TinkerCommons.slimeball.get(SlimeType.SKY))
                         .build(consumer, modResource(folder + "skyslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.enderSlimeEntity.get()), TinkerCommons.slimeball.get(SlimeType.ENDER))
                         .build(consumer, modResource(folder + "enderslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.MAGMA_CUBE), Items.MAGMA_CREAM)
                         .build(consumer, modResource(folder + "magma_cream"));
    // descaling? I don't know what to call those
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN), ItemOutput.fromStack(new ItemStack(Items.PRISMARINE_SHARD, 2)))
                         .build(consumer, modResource(folder + "guardian_shard"));

    // passive
    // befeating
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.RABBIT), Items.RABBIT_FOOT)
                         .setChildOutput(null) // only adults
												 .build(consumer, modResource(folder + "rabbit_foot"));
    // befeathering
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CHICKEN), ItemOutput.fromStack(new ItemStack(Items.FEATHER, 2)))
                         .setChildOutput(null) // only adults
                         .build(consumer, modResource(folder + "chicken_feather"));
    // beshrooming
    CustomRecipeBuilder.customRecipe(TinkerModifiers.mooshroomDemushroomingSerializer.get()).build(consumer, modPrefix(folder + "mooshroom_shroom"));
    // beshelling
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.TURTLE), Items.TURTLE_HELMET)
                         .setChildOutput(ItemOutput.fromItem(Items.SCUTE))
                         .build(consumer, modResource(folder + "turtle_shell"));
    // befleecing
    CustomRecipeBuilder.customRecipe(TinkerModifiers.sheepShearing.get()).build(consumer, modPrefix(folder + "sheep_wool"));
  }

  /** Just a helper for consistency of requirements errors */
  private static String makeRequirementsError(String recipe) {
    return TConstruct.makeTranslationKey("recipe", "modifier." + recipe);
  }
}
