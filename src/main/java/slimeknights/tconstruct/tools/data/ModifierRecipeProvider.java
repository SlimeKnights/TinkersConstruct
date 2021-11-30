package slimeknights.tconstruct.tools.data;

import net.minecraft.block.Blocks;
import net.minecraft.data.CookingRecipeBuilder;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.Effects;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidAttributes;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.mantle.recipe.data.CompoundIngredient;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.recipe.data.ItemNameOutput;
import slimeknights.mantle.recipe.ingredient.IngredientIntersection;
import slimeknights.mantle.recipe.ingredient.IngredientWithout;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.RandomItem;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.CureEffectsSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.DamageSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.DamageSpillingEffect.DamageType;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.DamageSpillingEffect.LivingEntityPredicate;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.EffectSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.ExtinguishSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.RestoreHungerSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.SetFireSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.TeleportSpillingEffect;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairRecipeBuilder;
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

import java.util.function.BiConsumer;
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
    addSpillingRecipes(consumer);
  }

  private void addItemRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/modifiers/";

    // reinforcements
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.ironReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenIron, true, FluidValues.NUGGET * 3)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .build(consumer, prefix(TinkerModifiers.ironReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.slimesteelReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenSlimesteel, false, FluidValues.NUGGET * 3)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .build(consumer, prefix(TinkerModifiers.slimesteelReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.searedReinforcement)
                            .setFluidAndTime(TinkerFluids.searedStone, false, FluidValues.INGOT)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .build(consumer, prefix(TinkerModifiers.searedReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.goldReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenGold, true, FluidValues.NUGGET * 3)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .build(consumer, prefix(TinkerModifiers.goldReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.emeraldReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenEmerald, false, FluidValues.INGOT / 3)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .build(consumer, prefix(TinkerModifiers.emeraldReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.bronzeReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenTinkersBronze, true, FluidValues.NUGGET * 3)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .build(consumer, prefix(TinkerModifiers.bronzeReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.cobaltReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenCobalt, true, FluidValues.NUGGET * 3)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .build(consumer, prefix(TinkerModifiers.cobaltReinforcement, folder));

    // jeweled apple
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.jeweledApple)
                            .setFluidAndTime(TinkerFluids.moltenDiamond, false, FluidValues.INGOT * 4)
                            .setCast(Items.APPLE, true)
                            .build(consumer, prefix(TinkerCommons.jeweledApple, folder));

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

    // modifier repair
    String repairFolder = folder + "repair/";
    // stringy - from string
    ModifierRepairRecipeBuilder.repair(TinkerModifiers.stringy.get(), Ingredient.fromTag(Tags.Items.STRING), 25)
                               .buildCraftingTable(consumer, wrap(TinkerModifiers.stringy, folder, "_crafting_table"))
                               .build(consumer, wrap(TinkerModifiers.stringy, folder, "_tinker_station"));
    // pig iron - from bacon, only in the tinker station
    ModifierRepairRecipeBuilder.repair(TinkerModifiers.tasty.get(), Ingredient.fromItems(TinkerCommons.bacon), 25)
                               .build(consumer, prefix(TinkerModifiers.tasty, folder));
  }

  private void addModifierRecipes(Consumer<IFinishedRecipe> consumer) {
    // upgrades
    String upgradeFolder = "tools/modifiers/upgrade/";
    String abilityFolder = "tools/modifiers/ability/";
    String slotlessFolder = "tools/modifiers/slotless/";
    String upgradeSalvage = "tools/modifiers/salvage/upgrade/";
    String abilitySalvage = "tools/modifiers/salvage/ability/";
    String slotlessSalvage = "tools/modifiers/salvage/slotless/";
    String defenseFolder = "tools/modifiers/defense/";
    String defenseSalvage = "tools/modifiers/salvage/defense/";
    String compatFolder = "tools/modifiers/compat/";
    String compatSalvage = "tools/modifiers/salvage/compat/";

    /*
     * durability
     */
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.reinforced.get())
                         .setInputSalvage(TinkerModifiers.ironReinforcement, 1, 24, false)
                         .setMaxLevel(5) // max 75% resistant to damage
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.DURABILITY)
                         .buildSalvage(consumer, prefix(TinkerModifiers.reinforced, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.reinforced, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.overforced.get())
                                    .setInputSalvage(TinkerModifiers.slimesteelReinforcement, 1, 24, false)
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
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE_OR_HARVEST, TinkerTags.Items.LEGGINGS))
                         .includeUnarmed()
                         .buildSalvage(consumer, prefix(TinkerModifiers.experienced, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.experienced, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.magnetic.get())
                         .addInputSalvage(Items.COMPASS, 0.5f)
                         .setMaxLevel(5)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .build(consumer, prefix(TinkerModifiers.magnetic, upgradeFolder));
    // armor has a max level of 1 per piece, so 4 total
    ModifierRecipeBuilder.modifier(TinkerModifiers.magnetic.get())
                         .addInputSalvage(Items.COMPASS, 0.5f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.ARMOR)
                         .build(consumer, wrap(TinkerModifiers.magnetic, upgradeFolder, "_armor"));
    // salvage supports either
    ModifierRecipeBuilder.modifier(TinkerModifiers.magnetic.get())
                         .addSalvage(Items.COMPASS, 0.5f)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE_OR_HARVEST, TinkerTags.Items.ARMOR))
                         .buildSalvage(consumer, prefix(TinkerModifiers.magnetic, upgradeSalvage));
    // no salvage so we can potentially grant shiny in another way without being an apple farm, and no recipe as that leaves nothing to salvage
    ModifierRecipeBuilder.modifier(TinkerModifiers.shiny.get())
                         .addInput(Items.ENCHANTED_GOLDEN_APPLE)
                         .setMaxLevel(1)
                         .build(consumer, prefix(TinkerModifiers.shiny, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.offhanded.get())
                         .setTools(TinkerTags.Items.HELD)
                         .addInputSalvage(Items.LEATHER, 0.7f)
                         .addInputSalvage(Items.PHANTOM_MEMBRANE, 0.3f)
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .setMaxLevel(1)
                         .setSalvageLevelRange(1, 1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .buildSalvage(consumer, wrap(TinkerModifiers.offhanded, upgradeSalvage, "_level_1"))
                         .build(consumer, wrap(TinkerModifiers.offhanded, upgradeFolder, "_level_1"));
    ModifierRecipeBuilder.modifier(TinkerModifiers.offhanded.get())
                         .setTools(TinkerTags.Items.HELD)
                         .addInputSalvage(Items.LEATHER, 0.7f)
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(2)
                         .setMinSalvageLevel(2)
                         .setRequirements(ModifierMatch.entry(TinkerModifiers.offhanded.get(), 1))
                         .setRequirementsError(makeRequirementsError("offhanded.level_2"))
                         .buildSalvage(consumer, wrap(TinkerModifiers.offhanded, upgradeSalvage, "_level_2"))
                         .build(consumer, wrap(TinkerModifiers.offhanded, upgradeFolder, "_level_2"));

    /*
     * Speed
     */

    // haste can use redstone or blocks
    Ingredient chestplateMeleeHarvest = ingredientFromTags(TinkerTags.Items.MELEE_OR_HARVEST, TinkerTags.Items.CHESTPLATES);
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.haste.get())
                                    .setTools(chestplateMeleeHarvest)
                                    .setInput(Tags.Items.DUSTS_REDSTONE, 1, 45)
                                    .setSalvage(Items.REDSTONE, false)
                                    .setMaxLevel(5) // +25 mining speed, vanilla +26, +50% mining speed on chestplates
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.haste, upgradeSalvage))
                                    .build(consumer, wrap(TinkerModifiers.haste, upgradeFolder, "_from_dust"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.haste.get())
                                    .setTools(chestplateMeleeHarvest)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_REDSTONE, 9, 45)
                                    .setLeftover(new ItemStack(Items.REDSTONE))
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .build(consumer, wrap(TinkerModifiers.haste, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.blasting.get())
                                    .setTools(TinkerTags.Items.STONE_HARVEST)
                                    .setInput(Tags.Items.GUNPOWDER, 1, 20)
                                    .setSalvage(Items.GUNPOWDER, false)
                                    .setMaxLevel(5) // +50 mining speed at max, conditionally
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.blasting, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.blasting, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.hydraulic.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Tags.Items.DUSTS_PRISMARINE, 1, 36) // stupid forge name
                                    .setSalvage(Items.PRISMARINE_SHARD, false)
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
                                    .setSalvage(Items.GLOWSTONE_DUST, false)
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
                         .addInput(TinkerTags.Items.SLIME_BLOCK)
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
                         .includeUnarmed()
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
                         .includeUnarmed()
                         .buildSalvage(consumer, prefix(TinkerModifiers.severing, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.severing, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.fiery.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.BLAZE_POWDER, 1, 25, false)
                                    .setMaxLevel(5) // +25 seconds fire damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .includeUnarmed()
                                    .buildSalvage(consumer, prefix(TinkerModifiers.fiery, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.fiery, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.necrotic.get())
                         .addInputSalvage(Items.WITHER_ROSE, 0.1f)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.BLOOD))
                         .addInput(Items.GHAST_TEAR)
                         .setMaxLevel(5) // +50% chance of heal, combine with +40% from traits for +90% total
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE)
                         .includeUnarmed()
                         .buildSalvage(consumer, prefix(TinkerModifiers.necrotic, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.necrotic, upgradeFolder));

    /*
     * damage boost
     */
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.piercing.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Blocks.CACTUS, 1, 25, false)
                                    .setMaxLevel(5) // +2.5 pierce damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.piercing, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.piercing, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.smite.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.GLISTERING_MELON_SLICE, 1, 5, false)
                                    .setMaxLevel(5) // +12.5 undead damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .includeUnarmed()
                                    .buildSalvage(consumer, prefix(TinkerModifiers.smite, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.smite, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.baneOfSssss.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.FERMENTED_SPIDER_EYE, 1, 15, false)
                                    .setMaxLevel(5) // +12.5 spider damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .includeUnarmed()
                                    .buildSalvage(consumer, prefix(TinkerModifiers.baneOfSssss, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.baneOfSssss, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.antiaquatic.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.PUFFERFISH, 1, 20, false)
                                    .setMaxLevel(5) // +12.5 fish damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .includeUnarmed()
                                    .buildSalvage(consumer, prefix(TinkerModifiers.antiaquatic, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.antiaquatic, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.cooling.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.PRISMARINE_CRYSTALS, 1, 25, false)
                                    .setMaxLevel(5) // +10 fire mob damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .includeUnarmed()
                                    .buildSalvage(consumer, prefix(TinkerModifiers.cooling, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.cooling, upgradeFolder));
    // sharpness can use shards or blocks
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sharpness.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.GEMS_QUARTZ, 1, 36)
                                    .setSalvage(Items.QUARTZ, false)
                                    .setMaxLevel(5) // +5 damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .includeUnarmed()
                                    .buildSalvage(consumer, prefix(TinkerModifiers.sharpness, upgradeSalvage))
                                    .build(consumer, wrap(TinkerModifiers.sharpness, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sharpness.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_QUARTZ, 4, 36)
                                    .setLeftover(new ItemStack(Items.QUARTZ))
                                    .setMaxLevel(5)
                                    .includeUnarmed()
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .build(consumer, wrap(TinkerModifiers.sharpness, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sweeping.get())
                                    .setTools(TinkerTags.Items.SWORD)
                                    .setInputSalvage(Blocks.CHAIN, 1, 18, true) // every 9 is 11 ingots, so this is 22 ingots
                                    .setMaxLevel(4) // goes 25%, 50%, 75%, 100%
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.sweeping, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.sweeping, upgradeFolder));
    /*
     * armor
     */
    // protection
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.meleeProtection.get())
                                    .setInputSalvage(TinkerModifiers.cobaltReinforcement, 1, 24, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.meleeProtection, defenseSalvage))
                                    .build(consumer, prefix(TinkerModifiers.meleeProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.projectileProtection.get())
                                    .setInputSalvage(TinkerModifiers.bronzeReinforcement, 1, 24, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.projectileProtection, defenseSalvage))
                                    .build(consumer, prefix(TinkerModifiers.projectileProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.blastProtection.get())
                                    .setInputSalvage(TinkerModifiers.emeraldReinforcement, 1, 24, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.blastProtection, defenseSalvage))
                                    .build(consumer, prefix(TinkerModifiers.blastProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.magicProtection.get())
                                    .setInputSalvage(TinkerModifiers.goldReinforcement, 1, 24, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.magicProtection, defenseSalvage))
                                    .build(consumer, prefix(TinkerModifiers.magicProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.fireProtection.get())
                                    .setInputSalvage(TinkerModifiers.searedReinforcement, 1, 24, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.fireProtection, defenseSalvage))
                                    .build(consumer, prefix(TinkerModifiers.fireProtection, defenseFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.knockbackResistance.get())
                         .setTools(TinkerTags.Items.ARMOR)
                         .addInput(SizedIngredient.fromItems(Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL))
                         .addSalvage(Blocks.DAMAGED_ANVIL, 0.5f)
                         .setSlots(SlotType.DEFENSE, 1)
                         .setMaxLevel(1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.knockbackResistance, defenseSalvage))
                         .build(consumer, prefix(TinkerModifiers.knockbackResistance, defenseFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.golden.get())
                         .setTools(TinkerTags.Items.ARMOR)
                         .addInputSalvage(TinkerSmeltery.blankCast.get(), 0.4f)
                         .addInputSalvage(TinkerSmeltery.blankCast.get(), 0.4f)
                         .addInputSalvage(TinkerSmeltery.blankCast.get(), 0.4f)
                         .addInputSalvage(TinkerSmeltery.blankCast.get(), 0.4f)
                         .addInputSalvage(TinkerSmeltery.blankCast.get(), 0.4f)
                         .setSlots(SlotType.DEFENSE, 1)
                         .setMaxLevel(1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.golden, defenseSalvage))
                         .build(consumer, prefix(TinkerModifiers.golden, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.turtleShell.get())
                                    .setInputSalvage(Items.SCUTE, 1, 5, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.turtleShell, defenseSalvage))
                                    .build(consumer, prefix(TinkerModifiers.turtleShell, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.dragonborn.get())
                                    .setInputSalvage(TinkerModifiers.dragonScale, 1, 10, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.dragonScale, defenseSalvage))
                                    .build(consumer, prefix(TinkerModifiers.dragonScale, defenseFolder));
    // 3 each for chest and legs, 2 each for boots and helmet, leads to 10 total
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.revitalizing.get())
                                    .setTools(ingredientFromTags(TinkerTags.Items.CHESTPLATES, TinkerTags.Items.LEGGINGS))
                                    .setInputSalvage(TinkerCommons.jeweledApple, 1, 2, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setMaxLevel(3)
                                    .build(consumer, wrap(TinkerModifiers.revitalizing, defenseFolder, "_large"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.revitalizing.get())
                                    .setTools(ingredientFromTags(TinkerTags.Items.HELMETS, TinkerTags.Items.BOOTS))
                                    .setInputSalvage(TinkerCommons.jeweledApple, 1, 2, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setMaxLevel(2)
                                    .build(consumer, wrap(TinkerModifiers.revitalizing, defenseFolder, "_small"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.revitalizing.get())
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .setInputSalvage(Items.GHAST_TEAR, 1, 5, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setMaxLevel(3)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.revitalizing, defenseSalvage));

    // upgrade - counterattack
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.thorns.get())
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .setInputSalvage(Blocks.CACTUS, 1, 25, false)
                                    .setMaxLevel(3)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .includeUnarmed()
                                    .buildSalvage(consumer, prefix(TinkerModifiers.thorns, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.thorns, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sticky.get())
                                    .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.ARMOR))
                                    .setInputSalvage(Blocks.COBWEB, 1, 5, false)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setMaxLevel(3)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.sticky, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.sticky, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.springy.get())
                         .setTools(TinkerTags.Items.ARMOR)
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInput(TinkerWorld.slime.get(SlimeType.ICHOR))
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(3)
                         .buildSalvage(consumer, prefix(TinkerModifiers.springy, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.springy, upgradeFolder));
    // upgrade - helmet
    ModifierRecipeBuilder.modifier(TinkerModifiers.respiration.get())
                         .setTools(TinkerTags.Items.HELMETS)
                         .addInput(ItemTags.FISHES)
                         .addInput(Tags.Items.GLASS_COLORLESS)
                         .addInput(ItemTags.FISHES)
                         .addInputSalvage(Items.KELP, 0.5f)
                         .addInputSalvage(Items.KELP, 0.5f)
                         .setMaxLevel(3)
                         .setSlots(SlotType.UPGRADE, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.respiration, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.respiration, upgradeFolder));
    // upgrade - chestplate
    ModifierRecipeBuilder.modifier(TinkerModifiers.unarmed.get())
                         .setTools(TinkerTags.Items.CHESTPLATES)
                         .addInputSalvage(Items.LEATHER, 0.5f)
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .addInputSalvage(Items.LEATHER, 0.5f)
                         .addInput(Tags.Items.STRING)
                         .addInput(Tags.Items.STRING)
                         .addSalvage(Items.DIAMOND, 0.25f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.unarmed, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.unarmed, abilityFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.armorPower.get())
                                    .setTools(TinkerTags.Items.CHESTPLATES)
                                    .setInputSalvage(TinkerCommons.slimeball.get(SlimeType.ICHOR), 1, 36, false)
                                    .setMaxLevel(5) // +25% damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.armorPower, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.armorPower, upgradeFolder));
    // upgrade - leggings
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.speedy.get())
                                    .setTools(TinkerTags.Items.LEGGINGS)
                                    .setInput(Tags.Items.DUSTS_REDSTONE, 1, 45)
                                    .setSalvage(Items.REDSTONE, false)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setMaxLevel(3)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.speedy, upgradeSalvage))
                                    .build(consumer, wrap(TinkerModifiers.speedy, upgradeFolder, "_from_dust"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.speedy.get())
                                    .setTools(TinkerTags.Items.LEGGINGS)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_REDSTONE, 9, 45)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setMaxLevel(3)
                                    .build(consumer, wrap(TinkerModifiers.speedy, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.leaping.get())
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .setInputSalvage(Items.RABBIT_FOOT, 1, 5, false)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(2)
                         .buildSalvage(consumer, prefix(TinkerModifiers.leaping, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.leaping, upgradeFolder));
    // upgrade - boots
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.featherFalling.get())
                                    .setTools(TinkerTags.Items.BOOTS)
                                    .setInputSalvage(Items.FEATHER, 1, 40, false)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setMaxLevel(4)
                                    .buildSalvage(consumer, prefix(TinkerModifiers.featherFalling, upgradeSalvage))
                                    .build(consumer, prefix(TinkerModifiers.featherFalling, upgradeFolder));
    // upgrade - all
    ModifierRecipeBuilder.modifier(TinkerModifiers.ricochet.get())
                         .setTools(TinkerTags.Items.ARMOR)
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInput(TinkerWorld.slime.get(SlimeType.SKY))
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(2) // 2 per piece gives +160% total
                         .buildSalvage(consumer, prefix(TinkerModifiers.ricochet, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.ricochet, upgradeFolder));

    // armor ability
    // helmet
    ModifierRecipeBuilder.modifier(TinkerModifiers.zoom.get())
                         .setTools(TinkerTags.Items.HELMETS)
                         .addInput(Tags.Items.GLASS_PANES)
                         .addInput(Tags.Items.STORAGE_BLOCKS_REDSTONE)
                         .addInput(Tags.Items.GLASS_PANES)
                         .addInput(TinkerMaterials.copper.getIngotTag())
                         .addInput(TinkerMaterials.copper.getIngotTag())
                         .addSalvage(Items.REDSTONE, 0, 9)
                         .addSalvage(Items.GLASS_PANE, 0, 2)
                         .addSalvage(TinkerMaterials.copper.getIngotTag(), 1, 2)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.zoom, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.zoom, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.slurping.get())
                         .addInputSalvage(Items.GLASS_BOTTLE, 0.5f)
                         .addInput(TinkerTags.Items.TANKS)
                         .addInputSalvage(Items.GLASS_BOTTLE, 0.5f)
                         .addInput(TinkerMaterials.copper.getIngotTag())
                         .addInput(TinkerMaterials.copper.getIngotTag())
                         .addSalvage(TinkerMaterials.copper.getIngot(), 1, 2)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HELMETS)
                         .buildSalvage(consumer, prefix(TinkerModifiers.slurping, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.slurping, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.aquaAffinity.get())
                         .addInput(Blocks.PRISMARINE_BRICKS)
                         .addInputSalvage(Items.HEART_OF_THE_SEA, 0.65f)
                         .addInput(Blocks.PRISMARINE_BRICKS)
                         .addInput(Blocks.DARK_PRISMARINE)
                         .addInput(Blocks.DARK_PRISMARINE)
                         .addSalvage(Items.PRISMARINE_SHARD, 8, 34)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HELMETS)
                         .buildSalvage(consumer, prefix(TinkerModifiers.aquaAffinity, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.aquaAffinity, abilityFolder));
    // boots
    ModifierRecipeBuilder.modifier(TinkerModifiers.doubleJump.get())
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInput(TinkerWorld.slime.get(SlimeType.SKY))
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInputSalvage(Items.PHANTOM_MEMBRANE, 0.3f)
                         .addInputSalvage(Items.PHANTOM_MEMBRANE, 0.3f)
                         .addSalvage(TinkerCommons.slimeball.get(SlimeType.SKY), 2, 9)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.doubleJump, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.doubleJump, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.bouncy.get())
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY))
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ENDER))
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY))
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ICHOR))
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ICHOR))
                         .addSalvage(TinkerMaterials.slimesteel.getIngot(), 0, 2)
                         .addSalvage(TinkerCommons.slimeball.get(SlimeType.SKY), 4, 18)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.bouncy, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.bouncy, abilityFolder));

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
    BiConsumer<Modifier,INamedTag<Item>> luckRecipes = (modifier, tag) -> {
      String key = modifier.getId().getPath();
      ModifierRecipeBuilder.modifier(modifier)
                           .setTools(tag)
                           .addInput(TinkerMaterials.copper.getIngotTag())
                           .addInput(SizedIngredient.fromItems(Items.CORNFLOWER, Items.BLUE_ORCHID))
                           .addInput(TinkerMaterials.copper.getIngotTag())
                           .addInput(Tags.Items.STORAGE_BLOCKS_LAPIS)
                           .addInput(Tags.Items.STORAGE_BLOCKS_LAPIS)
                           .addSalvage(TinkerMaterials.copper.getIngotTag(), 2, 2)
                           .addSalvage(Items.LAPIS_LAZULI, 3, 18)
                           .setSalvageLevelRange(1, 1)
                           .setMaxLevel(1)
                           .setSlots(SlotType.ABILITY, 1)
                           .buildSalvage(consumer, wrap(modifier, abilitySalvage, "_level_1"))
                           .build(consumer, wrap(modifier, abilityFolder, "_level_1"));
      ModifierRecipeBuilder.modifier(modifier)
                           .setTools(tag)
                           .addInput(Tags.Items.INGOTS_GOLD)
                           .addInput(Items.GOLDEN_CARROT)
                           .addInput(Tags.Items.INGOTS_GOLD)
                           .addInput(Tags.Items.ENDER_PEARLS)
                           .addInput(Tags.Items.ENDER_PEARLS)
                           .addSalvage(Items.GOLD_INGOT, 2, 3)
                           .addSalvage(Items.GOLD_NUGGET, 1, 8)
                           .addSalvage(Items.CARROT, 0.75f) // all the magic is gone, its just a carrot now
                           .addSalvage(Items.ENDER_PEARL, 2)
                           .setRequirements(ModifierMatch.entry(modifier, 1))
                           .setRequirementsError(makeRequirementsError(key + ".level_2"))
                           .setSalvageLevelRange(2, 2)
                           .setMaxLevel(2)
                           .buildSalvage(consumer, wrap(modifier, abilitySalvage, "_level_2"))
                           .build(consumer, wrap(modifier, abilityFolder, "_level_2"));
      ModifierRecipeBuilder.modifier(modifier)
                           .setTools(tag)
                           .addInput(TinkerMaterials.roseGold.getIngotTag())
                           .addInputSalvage(Items.RABBIT_FOOT, 0.15f)
                           .addInput(TinkerMaterials.roseGold.getIngotTag())
                           .addInput(Tags.Items.GEMS_DIAMOND)
                           .addInputSalvage(Items.NAME_TAG, 0.1f)
                           .addSalvage(Items.DIAMOND, 0.65f)
                           .addSalvage(TinkerMaterials.roseGold.getIngotTag(), 2, 2)
                           .setRequirements(ModifierMatch.entry(modifier, 2))
                           .setRequirementsError(makeRequirementsError(key + ".level_3"))
                           .setSalvageLevelRange(3, 3)
                           .setMaxLevel(3)
                           .buildSalvage(consumer, wrap(modifier, abilitySalvage, "_level_3"))
                           .build(consumer, wrap(modifier, abilityFolder, "_level_3"));
    };
    luckRecipes.accept(TinkerModifiers.luck.get(), TinkerTags.Items.MELEE_OR_HARVEST);
    luckRecipes.accept(TinkerModifiers.looting.get(), TinkerTags.Items.CHESTPLATES);
    ModifierRecipeBuilder.modifier(TinkerModifiers.luck.get())
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .addInput(SizedIngredient.fromItems(Items.CORNFLOWER, Items.BLUE_ORCHID))
                         .addInputSalvage(Items.RABBIT_FOOT, 0.15f)
                         .addInput(Items.GOLDEN_CARROT)
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .addInputSalvage(Items.NAME_TAG, 0.1f)
                         .addSalvage(Items.DIAMOND, 0.65f)
                         .addSalvage(Items.CARROT, 0.75f) // all the magic is gone, its just a carrot now
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, wrap(TinkerModifiers.luck, abilitySalvage, "_pants"))
                         .build(consumer, wrap(TinkerModifiers.luck, abilityFolder, "_pants"));
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
                         .includeUnarmed()
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
                         .setTools(TinkerTags.Items.INTERACTABLE)
                         .buildSalvage(consumer, prefix(TinkerModifiers.bucketing, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.bucketing, abilityFolder));
    SizedIngredient channels = SizedIngredient.fromItems(TinkerSmeltery.searedChannel, TinkerSmeltery.scorchedChannel);
    Ingredient meleeChestplate = ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.CHESTPLATES);
    ModifierRecipeBuilder.modifier(TinkerModifiers.spilling.get())
                         .addInput(channels)
                         .addInput(TinkerTags.Items.TANKS)
                         .addInput(channels)
                         .addInput(TinkerMaterials.copper.getIngotTag())
                         .addInput(TinkerMaterials.copper.getIngotTag())
                         .addSalvage(TinkerMaterials.copper.getIngotTag(), 1, 2)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(meleeChestplate)
                         .buildSalvage(consumer, prefix(TinkerModifiers.spilling, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.spilling, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.tank.get()) // TODO: armor does not interact with chestplates for tanks, is that bad?
                         .addInput(TinkerTags.Items.TANKS) // no salvage as don't want conversion between seared and scorched
                         .setMaxLevel(5)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.INTERACTABLE, TinkerTags.Items.HELMETS))
                         .buildSalvage(consumer, prefix(TinkerModifiers.tank, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.tank, upgradeFolder));
    // expanders
    ModifierRecipeBuilder.modifier(TinkerModifiers.expanded.get())
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInputSalvage(TinkerMaterials.tinkersBronze.getIngotTag(), 1.0f)
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.AOE)
                         .buildSalvage(consumer, prefix(TinkerModifiers.expanded, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.expanded, abilityFolder));
    // reach expander
    ModifierRecipeBuilder.modifier(TinkerModifiers.reach.get())
                         .setTools(ingredientFromTags(TinkerTags.Items.HARVEST, TinkerTags.Items.CHESTPLATES))
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInputSalvage(TinkerMaterials.queensSlime.getIngotTag(), 1.0f)
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInput(SlimeType.ENDER.getSlimeballTag())
                         .addInput(SlimeType.ENDER.getSlimeballTag())
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.reach, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.reach, abilityFolder));
    // block transformers
    Ingredient heldWithDurability = new IngredientIntersection(Ingredient.fromTag(TinkerTags.Items.DURABILITY), Ingredient.fromTag(TinkerTags.Items.INTERACTABLE));
    ModifierRecipeBuilder.modifier(TinkerModifiers.pathing.get())
                         .setTools(new IngredientWithout(heldWithDurability, Ingredient.fromItems(TinkerTools.mattock, TinkerTools.excavator)))
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.pickaxeHead.get())))
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.smallAxeHead.get())))
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.pathing, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.pathing, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.stripping.get())
                         .setTools(new IngredientWithout(heldWithDurability, Ingredient.fromItems(TinkerTools.handAxe, TinkerTools.broadAxe)))
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.smallAxeHead.get())))
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.toolBinding.get())))
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.stripping, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.stripping, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.tilling.get())
                         .setTools(new IngredientWithout(heldWithDurability, Ingredient.fromItems(TinkerTools.kama, TinkerTools.scythe)))
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
                         .setTools(heldWithDurability)
                         .addInput(Items.GLOWSTONE)
                         .addInputSalvage(Items.DAYLIGHT_DETECTOR, 0.9f)
                         .addInputSalvage(Items.SHROOMLIGHT, 0.4f)
                         .addSalvage(Items.GLOWSTONE_DUST, 1, 4)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.glowing, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.glowing, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.firestarter.get())
                         .setTools(new IngredientWithout(heldWithDurability, Ingredient.fromItems(TinkerTools.flintAndBronze)))
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInputSalvage(Items.FLINT, 0.2f)
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.firestarter, abilitySalvage))
                         .build(consumer, prefix(TinkerModifiers.firestarter, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.fireprimer.get())
                         .setTools(Ingredient.fromItems(TinkerTools.flintAndBronze))
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInputSalvage(Items.FLINT, 0.2f)
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.fireprimer, upgradeSalvage))
                         .build(consumer, prefix(TinkerModifiers.fireprimer, upgradeFolder));

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
                         .addInput(SlimeType.SKY.getSlimeballTag())
                         .addInput(SlimeType.SKY.getSlimeballTag())
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(new IngredientWithout(new IngredientIntersection(Ingredient.fromTag(TinkerTags.Items.MELEE), Ingredient.fromTag(TinkerTags.Items.ONE_HANDED)), Ingredient.fromItems(TinkerTools.dagger)))
                         .build(consumer, wrap(TinkerModifiers.dualWielding, abilityFolder, "_one_handed"));
    ModifierRecipeBuilder.modifier(TinkerModifiers.dualWielding.get())
                         .addInput(TinkerMaterials.manyullyn.getIngotTag())
                         .addInput(Items.NAUTILUS_SHELL)
                         .addInput(TinkerMaterials.manyullyn.getIngotTag())
                         .addInput(SlimeType.SKY.getSlimeballTag())
                         .addInput(SlimeType.SKY.getSlimeballTag())
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
                         .setTools(new IngredientWithout(meleeChestplate, Ingredient.fromItems(TinkerTools.dagger)))
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

    // compatability
    String theOneProbe = "theoneprobe";
    ResourceLocation probe = new ResourceLocation(theOneProbe, "probe");
    Consumer<IFinishedRecipe> topConsumer = withCondition(consumer, modLoaded(theOneProbe));
    ModifierRecipeBuilder.modifier(TinkerModifiers.theOneProbe.get())
                         .setTools(ingredientFromTags(TinkerTags.Items.HELMETS, TinkerTags.Items.HELD))
                         .addInput(ItemNameIngredient.from(probe))
                         .addSalvage(RandomItem.chance(ItemNameOutput.fromName(probe), 0.9f))
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(1)
                         .buildSalvage(consumer, prefix(TinkerModifiers.theOneProbe, compatSalvage))
                         .build(topConsumer, prefix(TinkerModifiers.theOneProbe, compatFolder));

  }

  private void addHeadRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/severing/";
    // first, beheading
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.ZOMBIE), Items.ZOMBIE_HEAD)
												 .build(consumer, modResource(folder + "zombie_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SKELETON), Items.SKELETON_SKULL)
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
    TinkerWorld.headItems.forEach((type, head) ->
      SeveringRecipeBuilder.severing(EntityIngredient.of(type.getType()), head)
                           .build(consumer, modResource(folder + type.getString() + "_head"))
																 );

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
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.terracubeEntity.get()), Items.CLAY_BALL)
                         .build(consumer, modResource(folder + "terracube_clay"));
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


  private void addSpillingRecipes(Consumer<IFinishedRecipe> consumer) {
    String folder = "tools/spilling/";

    // vanilla
    SpillingRecipeBuilder.forFluid(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 20)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.WATER_SENSITIVE, DamageType.PIERCING, 2f))
                         .addEffect(ExtinguishSpillingEffect.INSTANCE)
                         .build(consumer, modResource(folder + "water"));
    SpillingRecipeBuilder.forFluid(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 20)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 2f))
                         .addEffect(new SetFireSpillingEffect(10))
                         .build(consumer, modResource(folder + "lava"));
    SpillingRecipeBuilder.forFluid(Tags.Fluids.MILK, FluidAttributes.BUCKET_VOLUME / 10)
                         .addEffect(new CureEffectsSpillingEffect(new ItemStack(Items.MILK_BUCKET)))
                         .build(consumer, modResource(folder + "milk"));
    // blaze - more damage, less fire
    SpillingRecipeBuilder.forFluid(TinkerFluids.blazingBlood.getLocalTag(), FluidAttributes.BUCKET_VOLUME / 20)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 3f))
                         .addEffect(new SetFireSpillingEffect(5))
                         .build(consumer, prefix(TinkerFluids.blazingBlood, folder));
    // slime
    int slimeballPiece = FluidValues.SLIMEBALL / 5;
    // earth - lucky
    SpillingRecipeBuilder.forFluid(TinkerFluids.earthSlime.getForgeTag(), slimeballPiece)
                         .addEffect(new EffectSpillingEffect(Effects.LUCK, 15, 1))
                         .addEffect(new EffectSpillingEffect(Effects.SLOWNESS, 15, 1))
                         .build(consumer, prefix(TinkerFluids.earthSlime, folder));
    // sky - jump boost
    SpillingRecipeBuilder.forFluid(TinkerFluids.skySlime.getLocalTag(), slimeballPiece)
                         .addEffect(new EffectSpillingEffect(Effects.JUMP_BOOST, 20, 1))
                         .addEffect(new EffectSpillingEffect(Effects.SLOWNESS, 15, 1))
                         .build(consumer, prefix(TinkerFluids.skySlime, folder));
    // ender - levitation
    SpillingRecipeBuilder.forFluid(TinkerFluids.enderSlime.getLocalTag(), slimeballPiece)
                         .addEffect(new EffectSpillingEffect(Effects.LEVITATION, 5, 1))
                         .addEffect(new EffectSpillingEffect(Effects.SLOWNESS, 15, 1))
                         .build(consumer, prefix(TinkerFluids.enderSlime, folder));
    // slimelike
    // blood - food
    SpillingRecipeBuilder.forFluid(TinkerFluids.blood.getLocalTag(), slimeballPiece)
                         .addEffect(new RestoreHungerSpillingEffect(1, 0.2f))
                         .addEffect(new EffectSpillingEffect(Effects.MINING_FATIGUE, 10, 1))
                         .build(consumer, prefix(TinkerFluids.blood, folder));
    // venom - poison
    SpillingRecipeBuilder.forFluid(TinkerFluids.venom.getLocalTag(), slimeballPiece)
                         .addEffect(new EffectSpillingEffect(Effects.POISON, 25, 1))
                         .build(consumer, prefix(TinkerFluids.venom, folder));
    // magma - fire resistance
    SpillingRecipeBuilder.forFluid(TinkerFluids.magma.getForgeTag(), slimeballPiece)
                         .addEffect(new EffectSpillingEffect(Effects.FIRE_RESISTANCE, 25, 1))
                         .build(consumer, prefix(TinkerFluids.magma, folder));
    // soul - slowness and blindness
    SpillingRecipeBuilder.forFluid(TinkerFluids.liquidSoul.getLocalTag(), slimeballPiece)
                         .addEffect(new EffectSpillingEffect(Effects.SLOWNESS, 25, 2))
                         .addEffect(new EffectSpillingEffect(Effects.BLINDNESS, 5, 1))
                         .build(consumer, prefix(TinkerFluids.liquidSoul, folder));
    // ender - teleporting
    SpillingRecipeBuilder.forFluid(TinkerFluids.moltenEnder.getForgeTag(), FluidAttributes.BUCKET_VOLUME / 20)
                         .addEffect(new DamageSpillingEffect(DamageType.MAGIC, 1f))
                         .addEffect(TeleportSpillingEffect.INSTANCE)
                         .build(consumer, prefix(TinkerFluids.moltenEnder, folder));

    // multi-recipes
    SpillingRecipeBuilder.forFluid(TinkerTags.Fluids.GLASS_SPILLING, FluidAttributes.BUCKET_VOLUME / 10)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 1f))
                         .addEffect(new SetFireSpillingEffect(3))
                         .build(consumer, modResource(folder + "glass"));
    SpillingRecipeBuilder.forFluid(TinkerTags.Fluids.CLAY_SPILLING, FluidAttributes.BUCKET_VOLUME / 20)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 1.5f))
                         .addEffect(new SetFireSpillingEffect(3))
                         .build(consumer, modResource(folder + "clay"));

    SpillingRecipeBuilder.forFluid(TinkerTags.Fluids.CHEAP_METAL_SPILLING, FluidValues.NUGGET)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 1.5f))
                         .addEffect(new SetFireSpillingEffect(7))
                         .build(consumer, modResource(folder + "metal_cheap"));
    SpillingRecipeBuilder.forFluid(TinkerTags.Fluids.AVERAGE_METAL_SPILLING, FluidValues.NUGGET)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 2f))
                         .addEffect(new SetFireSpillingEffect(7))
                         .build(consumer, modResource(folder + "metal_average"));
    SpillingRecipeBuilder.forFluid(TinkerTags.Fluids.EXPENSIVE_METAL_SPILLING, FluidValues.NUGGET)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 2.5f))
                         .addEffect(new SetFireSpillingEffect(7))
                         .build(consumer, modResource(folder + "metal_expensive"));
    // gold applies magic
    SpillingRecipeBuilder.forFluid(TinkerFluids.moltenGold.getForgeTag(), FluidValues.NUGGET)
                         .addEffect(new DamageSpillingEffect(DamageType.MAGIC, 2f))
                         .addEffect(new SetFireSpillingEffect(3))
                         .build(consumer, prefix(TinkerFluids.moltenGold, folder));
    // pig iron fills you up magic
    SpillingRecipeBuilder.forFluid(TinkerFluids.moltenPigIron.getLocalTag(), FluidValues.NUGGET)
                         .addEffect(new RestoreHungerSpillingEffect(2, 0.3f))
                         .addEffect(new SetFireSpillingEffect(2))
                         .build(consumer, prefix(TinkerFluids.moltenPigIron, folder));
    // uranium also does poison
    SpillingRecipeBuilder.forFluid(TinkerFluids.moltenUranium.getLocalTag(), FluidValues.NUGGET)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 1.5f))
                         .addEffect(new EffectSpillingEffect(Effects.POISON, 10, 1))
                         .addEffect(new SetFireSpillingEffect(3))
                         .build(consumer, prefix(TinkerFluids.moltenUranium, folder));
  }

  private void burningSpilling(Consumer<IFinishedRecipe> consumer, FluidObject<?> fluid, float damage, int time) {
    burningSpilling(consumer, fluid, damage, time, FluidValues.NUGGET);
  }

  private void burningSpilling(Consumer<IFinishedRecipe> consumer, FluidObject<?> fluid, float damage, int time, int amount) {
    SpillingRecipeBuilder.forFluid(fluid.getLocalTag(), amount)
                         .addEffect(new DamageSpillingEffect(DamageType.FIRE, damage))
                         .addEffect(new SetFireSpillingEffect(time))
                         .build(consumer, prefix(fluid, "tools/spilling/"));
  }

  /** Just a helper for consistency of requirements errors */
  private static String makeRequirementsError(String recipe) {
    return TConstruct.makeTranslationKey("recipe", "modifier." + recipe);
  }

  /**
   * Creates a compound ingredient from multiple tags
   * @param tags  Tags to use
   * @return  Compound ingredient
   */
  @SafeVarargs
  private static Ingredient ingredientFromTags(ITag<Item>... tags) {
    Ingredient[] tagIngredients = new Ingredient[tags.length];
    for (int i = 0; i < tags.length; i++) {
      tagIngredients[i] = Ingredient.fromTag(tags[i]);
    }
    return CompoundIngredient.from(tagIngredients);
  }
}
