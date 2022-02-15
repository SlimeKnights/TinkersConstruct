package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;
import net.minecraftforge.fluids.FluidAttributes;
import slimeknights.mantle.recipe.data.CompoundIngredient;
import slimeknights.mantle.recipe.data.FluidNameIngredient;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.recipe.data.ItemNameOutput;
import slimeknights.mantle.recipe.helper.FluidTagEmptyCondition;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.recipe.ingredient.IngredientDifference;
import slimeknights.mantle.recipe.ingredient.IngredientIntersection;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.json.ConfigEnabledCondition;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.RandomItem;
import slimeknights.tconstruct.library.recipe.TagPredicate;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierMatch;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.SwappableModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.CureEffectsSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.DamageSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.DamageSpillingEffect.DamageType;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.DamageSpillingEffect.LivingEntityPredicate;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.EffectSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.ExtinguishSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.PotionFluidEffect;
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
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.tools.modifiers.traits.skull.StrongBonesModifier;
import slimeknights.tconstruct.tools.recipe.ArmorDyeingRecipe;
import slimeknights.tconstruct.tools.recipe.ModifierRemovalRecipe;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class ModifierRecipeProvider extends BaseRecipeProvider {
  public ModifierRecipeProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Modifier Recipes";
  }

  @Override
  protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
    addItemRecipes(consumer);
    addModifierRecipes(consumer);
    addTextureRecipes(consumer);
    addHeadRecipes(consumer);
    addSpillingRecipes(consumer);
  }

  private void addItemRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "tools/modifiers/";

    // reinforcements
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.ironReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenIron, true, FluidValues.NUGGET * 3)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .save(consumer, prefix(TinkerModifiers.ironReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.slimesteelReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenSlimesteel, false, FluidValues.NUGGET * 3)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .save(consumer, prefix(TinkerModifiers.slimesteelReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.searedReinforcement)
                            .setFluidAndTime(TinkerFluids.searedStone, false, FluidValues.BRICK)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .save(consumer, prefix(TinkerModifiers.searedReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.goldReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenGold, true, FluidValues.NUGGET * 3)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .save(consumer, prefix(TinkerModifiers.goldReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.emeraldReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenEmerald, false, FluidValues.GEM_SHARD)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .save(consumer, prefix(TinkerModifiers.emeraldReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.bronzeReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenTinkersBronze, true, FluidValues.NUGGET * 3)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .save(consumer, prefix(TinkerModifiers.bronzeReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.cobaltReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenCobalt, true, FluidValues.NUGGET * 3)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .save(consumer, prefix(TinkerModifiers.cobaltReinforcement, folder));

    // jeweled apple
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.jeweledApple)
                            .setFluidAndTime(TinkerFluids.moltenDiamond, false, FluidValues.GEM * 4)
                            .setCast(Items.APPLE, true)
                            .save(consumer, prefix(TinkerCommons.jeweledApple, folder));

    // silky cloth
    ShapedRecipeBuilder.shaped(TinkerModifiers.silkyCloth)
                       .define('s', Tags.Items.STRING)
                       .define('g', TinkerMaterials.roseGold.getIngotTag())
                       .pattern("sss")
                       .pattern("sgs")
                       .pattern("sss")
                       .unlockedBy("has_item", has(Tags.Items.INGOTS_GOLD))
                       .save(consumer, prefix(TinkerModifiers.silkyCloth, folder));

    // wither bone purifying
    ShapelessRecipeBuilder.shapeless(Items.BONE)
                          .requires(TinkerTags.Items.WITHER_BONES)
                          .unlockedBy("has_bone", has(TinkerTags.Items.WITHER_BONES))
                          .save(withCondition(consumer, ConfigEnabledCondition.WITHER_BONE_CONVERSION), modResource(folder + "wither_bone_conversion"));

    // modifier repair
    String repairFolder = folder + "repair/";
    // stringy - from string
    ModifierRepairRecipeBuilder.repair(TinkerModifiers.stringy.get(), Ingredient.of(Tags.Items.STRING), 25)
                               .buildCraftingTable(consumer, wrap(TinkerModifiers.stringy, folder, "_crafting_table"))
                               .save(consumer, wrap(TinkerModifiers.stringy, folder, "_tinker_station"));
    // pig iron - from bacon, only in the tinker station
    ModifierRepairRecipeBuilder.repair(TinkerModifiers.tasty.get(), Ingredient.of(TinkerCommons.bacon), 25)
                               .save(consumer, prefix(TinkerModifiers.tasty, folder));
  }

  private void addModifierRecipes(Consumer<FinishedRecipe> consumer) {
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
                         .saveSalvage(consumer, prefix(TinkerModifiers.reinforced, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.reinforced, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.overforced.get())
                                    .setInputSalvage(TinkerModifiers.slimesteelReinforcement, 1, 24, false)
                                    .setMaxLevel(5) // +250 capacity
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setTools(TinkerTags.Items.DURABILITY)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.overforced, upgradeSalvage))
                                    .save(consumer, prefix(TinkerModifiers.overforced, upgradeFolder));
    // gems are special, I'd like them to be useful on all types of tools
    ModifierRecipeBuilder.modifier(TinkerModifiers.emerald.get())
                         .addInput(Tags.Items.GEMS_EMERALD)
                         .addSalvage(Items.EMERALD, 0.5f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.emerald, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.emerald, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.diamond.get())
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .addSalvage(Items.DIAMOND, 0.65f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.diamond, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.diamond, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.worldbound.get())
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.worldbound, slotlessSalvage))
                         .save(consumer, prefix(TinkerModifiers.worldbound, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.soulbound.get())
                         .addInputSalvage(Items.TOTEM_OF_UNDYING, 0.7f)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.soulbound, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.soulbound, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.netherite.get())
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .addSalvage(Items.NETHERITE_INGOT, 0.6f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setRequirements(ModifierMatch.list(1, ModifierMatch.entry(TinkerModifiers.diamond.get()), ModifierMatch.entry(TinkerModifiers.emerald.get())))
                         .setRequirementsError(makeRequirementsError("netherite_requirements"))
                         .saveSalvage(consumer, prefix(TinkerModifiers.netherite, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.netherite, upgradeFolder));

    // overslime - earth
    OverslimeModifierRecipeBuilder.modifier(TinkerCommons.slimeball.get(SlimeType.EARTH), 10)
                                  .save(consumer, modResource(slotlessFolder + "overslime/earth_ball"));
    OverslimeModifierRecipeBuilder.modifier(TinkerWorld.congealedSlime.get(SlimeType.EARTH), 45)
                                  .save(consumer, modResource(slotlessFolder + "overslime/earth_congealed"));
    OverslimeModifierRecipeBuilder.modifier(TinkerWorld.slime.get(SlimeType.EARTH), 108)
                                  .save(consumer, modResource(slotlessFolder + "overslime/earth_block"));
    // sky
    OverslimeModifierRecipeBuilder.modifier(TinkerCommons.slimeball.get(SlimeType.SKY), 40)
                                  .save(consumer, modResource(slotlessFolder + "overslime/sky_ball"));
    OverslimeModifierRecipeBuilder.modifier(TinkerWorld.congealedSlime.get(SlimeType.SKY), 180)
                                  .save(consumer, modResource(slotlessFolder + "overslime/sky_congealed"));
    OverslimeModifierRecipeBuilder.modifier(TinkerWorld.slime.get(SlimeType.SKY), 432)
                                  .save(consumer, modResource(slotlessFolder + "overslime/sky_block"));
    // ichor
    OverslimeModifierRecipeBuilder.modifier(TinkerCommons.slimeball.get(SlimeType.ICHOR), 100)
                                  .save(consumer, modResource(slotlessFolder + "overslime/ichor_ball"));
    OverslimeModifierRecipeBuilder.modifier(TinkerWorld.congealedSlime.get(SlimeType.ICHOR), 450)
                                  .save(consumer, modResource(slotlessFolder + "overslime/ichor_congealed"));
    OverslimeModifierRecipeBuilder.modifier(TinkerWorld.slime.get(SlimeType.ICHOR), 1080)
                                  .save(consumer, modResource(slotlessFolder + "overslime/ichor_block"));

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
                         .saveSalvage(consumer, prefix(TinkerModifiers.experienced, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.experienced, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.magnetic.get())
                         .addInputSalvage(Items.COMPASS, 0.5f)
                         .setMaxLevel(5)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE_OR_HARVEST)
                         .save(consumer, prefix(TinkerModifiers.magnetic, upgradeFolder));
    // armor has a max level of 1 per piece, so 4 total
    ModifierRecipeBuilder.modifier(TinkerModifiers.magnetic.get())
                         .addInputSalvage(Items.COMPASS, 0.5f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.ARMOR)
                         .save(consumer, wrap(TinkerModifiers.magnetic, upgradeFolder, "_armor"));
    // salvage supports either
    ModifierRecipeBuilder.modifier(TinkerModifiers.magnetic.get())
                         .addSalvage(Items.COMPASS, 0.5f)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE_OR_HARVEST, TinkerTags.Items.ARMOR))
                         .saveSalvage(consumer, prefix(TinkerModifiers.magnetic, upgradeSalvage));
    // no salvage so we can potentially grant shiny in another way without being an apple farm, and no recipe as that leaves nothing to salvage
    ModifierRecipeBuilder.modifier(TinkerModifiers.shiny.get())
                         .addInput(Items.ENCHANTED_GOLDEN_APPLE)
                         .setMaxLevel(1)
                         .save(consumer, prefix(TinkerModifiers.shiny, slotlessFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.farsighted.get())
                                    .setTools(TinkerTags.Items.MODIFIABLE)
                                    .setInput(Tags.Items.CROPS_CARROT, 1, 45)
                                    .save(consumer, prefix(TinkerModifiers.farsighted, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.nearsighted.get())
                                    .setTools(TinkerTags.Items.MODIFIABLE)
                                    .setInput(Items.INK_SAC, 1, 45)
                                    .save(consumer, prefix(TinkerModifiers.nearsighted, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.offhanded.get())
                         .setTools(TinkerTags.Items.HELD)
                         .addInputSalvage(Items.LEATHER, 0.7f)
                         .addInputSalvage(Items.PHANTOM_MEMBRANE, 0.3f)
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .setMaxLevel(1)
                         .setSalvageLevelRange(1, 1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(consumer, wrap(TinkerModifiers.offhanded, upgradeSalvage, "_level_1"))
                         .save(consumer, wrap(TinkerModifiers.offhanded, upgradeFolder, "_level_1"));
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
                         .saveSalvage(consumer, wrap(TinkerModifiers.offhanded, upgradeSalvage, "_level_2"))
                         .save(consumer, wrap(TinkerModifiers.offhanded, upgradeFolder, "_level_2"));

    /*
     * Speed
     */

    // haste can use redstone or blocks
    Ingredient chestplateHarvest = ingredientFromTags(TinkerTags.Items.HARVEST, TinkerTags.Items.CHESTPLATES);
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.haste.get())
                                    .setTools(chestplateHarvest)
                                    .setInput(Tags.Items.DUSTS_REDSTONE, 1, 45)
                                    .setSalvage(Items.REDSTONE, false)
                                    .setMaxLevel(5) // +25 mining speed, vanilla +26, +50% mining speed on chestplates
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.haste, upgradeSalvage))
                                    .save(consumer, wrap(TinkerModifiers.haste, upgradeFolder, "_from_dust"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.haste.get())
                                    .setTools(chestplateHarvest)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_REDSTONE, 9, 45)
                                    .setLeftover(new ItemStack(Items.REDSTONE))
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(consumer, wrap(TinkerModifiers.haste, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.blasting.get())
                                    .setTools(TinkerTags.Items.STONE_HARVEST)
                                    .setInput(Tags.Items.GUNPOWDER, 1, 20)
                                    .setSalvage(Items.GUNPOWDER, false)
                                    .setMaxLevel(5) // +50 mining speed at max, conditionally
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.blasting, upgradeSalvage))
                                    .save(consumer, prefix(TinkerModifiers.blasting, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.hydraulic.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Tags.Items.DUSTS_PRISMARINE, 1, 36) // stupid forge name
                                    .setSalvage(Items.PRISMARINE_SHARD, false)
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.hydraulic, upgradeSalvage))
                                    .save(consumer, wrap(TinkerModifiers.hydraulic, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.hydraulic.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Blocks.PRISMARINE, 4, 36)
                                    .setLeftover(new ItemStack(Items.PRISMARINE_SHARD))
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(consumer, wrap(TinkerModifiers.hydraulic, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.hydraulic.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Blocks.PRISMARINE_BRICKS, 9, 36)
                                    .setLeftover(new ItemStack(Items.PRISMARINE_SHARD))
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(consumer, wrap(TinkerModifiers.hydraulic, upgradeFolder, "_from_bricks"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.lightspeed.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Tags.Items.DUSTS_GLOWSTONE, 1, 64)
                                    .setSalvage(Items.GLOWSTONE_DUST, false)
                                    .setMaxLevel(5) // +45 mining speed at max, conditionally
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.lightspeed, upgradeSalvage))
                                    .save(consumer, wrap(TinkerModifiers.lightspeed, upgradeFolder, "_from_dust"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.lightspeed.get())
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Blocks.GLOWSTONE, 4, 64)
                                    .setLeftover(new ItemStack(Items.GLOWSTONE_DUST))
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(consumer, wrap(TinkerModifiers.lightspeed, upgradeFolder, "_from_block"));

    /*
     * weapon
     */
    ModifierRecipeBuilder.modifier(TinkerModifiers.knockback.get())
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInput(TinkerTags.Items.SLIME_BLOCK)
                         .setMaxLevel(5) // max +2.5 knockback points (knockback 5) (whatever that number means in vanilla)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE)
                         .saveSalvage(consumer, prefix(TinkerModifiers.knockback, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.knockback, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.padded.get())
                         .addInput(Items.LEATHER)
                         .addInput(ItemTags.WOOL)
                         .addInput(Items.LEATHER)
                         .addSalvage(Items.LEATHER, 2)
                         .setMaxLevel(3) // max 12.5% knockback, or 6.25% on the dagger
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE)
                         .includeUnarmed()
                         .saveSalvage(consumer, prefix(TinkerModifiers.padded, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.padded, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.severing.get())
                         .addInput(TinkerTags.Items.WITHER_BONES)
                         .addInputSalvage(Items.LIGHTNING_ROD, 0.75f)
                         .addInput(TinkerTags.Items.WITHER_BONES)
                         .addInput(Items.TNT)
                         .addSalvage(TinkerMaterials.necroticBone, 2)
                         .setMaxLevel(5) // max +25% head drop chance, combine with +15% chance from luck
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE)
                         .includeUnarmed()
                         .saveSalvage(consumer, prefix(TinkerModifiers.severing, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.severing, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.fiery.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.BLAZE_POWDER, 1, 25, false)
                                    .setMaxLevel(5) // +25 seconds fire damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .includeUnarmed()
                                    .saveSalvage(consumer, prefix(TinkerModifiers.fiery, upgradeSalvage))
                                    .save(consumer, prefix(TinkerModifiers.fiery, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.necrotic.get())
                         .addInputSalvage(Items.WITHER_ROSE, 0.1f)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.BLOOD))
                         .addInput(Items.GHAST_TEAR)
                         .setMaxLevel(5) // +50% chance of heal, combine with +40% from traits for +90% total
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE)
                         .includeUnarmed()
                         .saveSalvage(consumer, prefix(TinkerModifiers.necrotic, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.necrotic, upgradeFolder));

    /*
     * damage boost
     */
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.piercing.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Blocks.CACTUS, 1, 25, false)
                                    .setMaxLevel(5) // +2.5 pierce damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.piercing, upgradeSalvage))
                                    .save(consumer, prefix(TinkerModifiers.piercing, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.smite.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.GLISTERING_MELON_SLICE, 1, 5, false)
                                    .setMaxLevel(5) // +12.5 undead damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .includeUnarmed()
                                    .saveSalvage(consumer, prefix(TinkerModifiers.smite, upgradeSalvage))
                                    .save(consumer, prefix(TinkerModifiers.smite, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.baneOfSssss.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.FERMENTED_SPIDER_EYE, 1, 15, false)
                                    .setMaxLevel(5) // +12.5 spider damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .includeUnarmed()
                                    .saveSalvage(consumer, prefix(TinkerModifiers.baneOfSssss, upgradeSalvage))
                                    .save(consumer, prefix(TinkerModifiers.baneOfSssss, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.antiaquatic.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.PUFFERFISH, 1, 20, false)
                                    .setMaxLevel(5) // +12.5 fish damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .includeUnarmed()
                                    .saveSalvage(consumer, prefix(TinkerModifiers.antiaquatic, upgradeSalvage))
                                    .save(consumer, prefix(TinkerModifiers.antiaquatic, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.cooling.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInputSalvage(Items.PRISMARINE_CRYSTALS, 1, 25, false)
                                    .setMaxLevel(5) // +10 fire mob damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .includeUnarmed()
                                    .saveSalvage(consumer, prefix(TinkerModifiers.cooling, upgradeSalvage))
                                    .save(consumer, prefix(TinkerModifiers.cooling, upgradeFolder));
    // sharpness can use shards or blocks
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sharpness.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.GEMS_QUARTZ, 1, 36)
                                    .setSalvage(Items.QUARTZ, false)
                                    .setMaxLevel(5) // +5 damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .includeUnarmed()
                                    .saveSalvage(consumer, prefix(TinkerModifiers.sharpness, upgradeSalvage))
                                    .save(consumer, wrap(TinkerModifiers.sharpness, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sharpness.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_QUARTZ, 4, 36)
                                    .setLeftover(new ItemStack(Items.QUARTZ))
                                    .setMaxLevel(5)
                                    .includeUnarmed()
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(consumer, wrap(TinkerModifiers.sharpness, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sweeping.get())
                                    .setTools(TinkerTags.Items.SWORD)
                                    .setInputSalvage(Blocks.CHAIN, 1, 18, true) // every 9 is 11 ingots, so this is 22 ingots
                                    .setMaxLevel(4) // goes 25%, 50%, 75%, 100%
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.sweeping, upgradeSalvage))
                                    .save(consumer, prefix(TinkerModifiers.sweeping, upgradeFolder));
    // swiftstrike works on blocks too, we are nice
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.swiftstrike.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.AMETHYST_SHARD, 1, 72)
                                    .setSalvage(Items.AMETHYST_SHARD, false)
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.swiftstrike, upgradeSalvage))
                                    .save(consumer, wrap(TinkerModifiers.swiftstrike, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.swiftstrike.get())
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Blocks.AMETHYST_BLOCK, 4, 72)
                                    .setLeftover(new ItemStack(Items.AMETHYST_SHARD))
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(consumer, wrap(TinkerModifiers.swiftstrike, upgradeFolder, "_from_block"));
    /*
     * armor
     */
    // protection
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.meleeProtection.get())
                                    .setInputSalvage(TinkerModifiers.cobaltReinforcement, 1, 24, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.meleeProtection, defenseSalvage))
                                    .save(consumer, prefix(TinkerModifiers.meleeProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.projectileProtection.get())
                                    .setInputSalvage(TinkerModifiers.bronzeReinforcement, 1, 24, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.projectileProtection, defenseSalvage))
                                    .save(consumer, prefix(TinkerModifiers.projectileProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.blastProtection.get())
                                    .setInputSalvage(TinkerModifiers.emeraldReinforcement, 1, 24, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.blastProtection, defenseSalvage))
                                    .save(consumer, prefix(TinkerModifiers.blastProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.magicProtection.get())
                                    .setInputSalvage(TinkerModifiers.goldReinforcement, 1, 24, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.magicProtection, defenseSalvage))
                                    .save(consumer, prefix(TinkerModifiers.magicProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.fireProtection.get())
                                    .setInputSalvage(TinkerModifiers.searedReinforcement, 1, 24, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.fireProtection, defenseSalvage))
                                    .save(consumer, prefix(TinkerModifiers.fireProtection, defenseFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.protection.get())
                         .addInputSalvage(TinkerModifiers.goldReinforcement,    1, 4)
                         .addInputSalvage(TinkerModifiers.searedReinforcement,  1, 4)
                         .addInputSalvage(TinkerModifiers.bronzeReinforcement,  1, 4)
                         .addInputSalvage(TinkerModifiers.emeraldReinforcement, 1, 4)
                         .addInputSalvage(TinkerModifiers.cobaltReinforcement,  1, 4)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.ARMOR)
                         .saveSalvage(consumer, prefix(TinkerModifiers.protection, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.protection, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.knockbackResistance.get())
                         .setTools(TinkerTags.Items.ARMOR)
                         .addInput(SizedIngredient.fromItems(Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL))
                         .addSalvage(Blocks.DAMAGED_ANVIL, 0.5f)
                         .setSlots(SlotType.DEFENSE, 1)
                         .setMaxLevel(1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.knockbackResistance, defenseSalvage))
                         .save(consumer, prefix(TinkerModifiers.knockbackResistance, defenseFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.golden.get())
                         .setTools(TinkerTags.Items.ARMOR)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .setSlots(SlotType.DEFENSE, 1)
                         .setMaxLevel(1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.golden, defenseSalvage))
                         .save(consumer, prefix(TinkerModifiers.golden, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.turtleShell.get())
                                    .setInputSalvage(Items.SCUTE, 1, 5, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.turtleShell, defenseSalvage))
                                    .save(consumer, prefix(TinkerModifiers.turtleShell, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.dragonborn.get())
                                    .setInputSalvage(TinkerModifiers.dragonScale, 1, 10, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.dragonScale, defenseSalvage))
                                    .save(consumer, prefix(TinkerModifiers.dragonScale, defenseFolder));
    // 3 each for chest and legs, 2 each for boots and helmet, leads to 10 total
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.revitalizing.get())
                                    .setTools(ingredientFromTags(TinkerTags.Items.CHESTPLATES, TinkerTags.Items.LEGGINGS))
                                    .setInputSalvage(TinkerCommons.jeweledApple, 1, 2, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setMaxLevel(3)
                                    .save(consumer, wrap(TinkerModifiers.revitalizing, defenseFolder, "_large"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.revitalizing.get())
                                    .setTools(ingredientFromTags(TinkerTags.Items.HELMETS, TinkerTags.Items.BOOTS))
                                    .setInputSalvage(TinkerCommons.jeweledApple, 1, 2, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setMaxLevel(2)
                                    .save(consumer, wrap(TinkerModifiers.revitalizing, defenseFolder, "_small"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.revitalizing.get())
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .setInputSalvage(Items.GHAST_TEAR, 1, 5, false)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setMaxLevel(3)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.revitalizing, defenseSalvage));

    // upgrade - counterattack
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.thorns.get())
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .setInputSalvage(Blocks.CACTUS, 1, 25, false)
                                    .setMaxLevel(3)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.thorns, upgradeSalvage))
                                    .save(consumer, prefix(TinkerModifiers.thorns, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sticky.get())
                                    .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.ARMOR))
                                    .setInputSalvage(Blocks.COBWEB, 1, 5, false)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setMaxLevel(3)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.sticky, upgradeSalvage))
                                    .save(consumer, prefix(TinkerModifiers.sticky, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.springy.get())
                         .setTools(TinkerTags.Items.ARMOR)
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInput(TinkerWorld.slime.get(SlimeType.ICHOR))
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(3)
                         .saveSalvage(consumer, prefix(TinkerModifiers.springy, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.springy, upgradeFolder));
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
                         .saveSalvage(consumer, prefix(TinkerModifiers.respiration, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.respiration, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.itemFrame.get())
                         .setTools(TinkerTags.Items.HELMETS)
                         .addInput(Ingredient.of(Arrays.stream(FrameType.values())
                                                               .filter(type -> type != FrameType.CLEAR)
                                                               .map(type -> new ItemStack(TinkerGadgets.itemFrame.get(type)))))
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.itemFrame, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.itemFrame, upgradeFolder));
    // upgrade - chestplate
    ModifierRecipeBuilder.modifier(TinkerModifiers.armorKnockback.get())
                         .setTools(TinkerTags.Items.CHESTPLATES)
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInput(TinkerWorld.slime.get(SlimeType.EARTH))
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(3)
                         .saveSalvage(consumer, prefix(TinkerModifiers.armorKnockback, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.armorKnockback, upgradeFolder));
    // upgrade - leggings
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.speedy.get())
                                    .setTools(TinkerTags.Items.LEGGINGS)
                                    .setInput(Tags.Items.DUSTS_REDSTONE, 1, 45)
                                    .setSalvage(Items.REDSTONE, false)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setMaxLevel(3)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.speedy, upgradeSalvage))
                                    .save(consumer, wrap(TinkerModifiers.speedy, upgradeFolder, "_from_dust"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.speedy.get())
                                    .setTools(TinkerTags.Items.LEGGINGS)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_REDSTONE, 9, 45)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setMaxLevel(3)
                                    .save(consumer, wrap(TinkerModifiers.speedy, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.leaping.get())
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .setInputSalvage(Items.RABBIT_FOOT, 1, 5, false)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(2)
                         .saveSalvage(consumer, prefix(TinkerModifiers.leaping, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.leaping, upgradeFolder));
    // half of pockets as a slotless
    ModifierRecipeBuilder.modifier(TinkerModifiers.pocketChain.get())
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .addInputSalvage(TinkerMaterials.cobalt.getIngotTag(), 0.3f)
                         .addInputSalvage(Items.CHAIN, 0.7f)
                         .addInputSalvage(Items.CHAIN, 0.4f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setRequirementsError(makeRequirementsError("pocket_chain"))
                         .setRequirements(ModifierMatch.list(1, ModifierMatch.entry(TinkerModifiers.shieldStrap.get()), ModifierMatch.entry(TinkerModifiers.toolBelt.get())))
                         .saveSalvage(consumer, prefix(TinkerModifiers.pocketChain, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.pocketChain, upgradeFolder));
    // upgrade - boots
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.featherFalling.get())
                                    .setTools(TinkerTags.Items.BOOTS)
                                    .setInputSalvage(Items.FEATHER, 1, 40, false)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setMaxLevel(4)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.featherFalling, upgradeSalvage))
                                    .save(consumer, prefix(TinkerModifiers.featherFalling, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.soulspeed.get())
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInputSalvage(Items.MAGMA_BLOCK, 0.2f)
                         .addInputSalvage(Items.CRYING_OBSIDIAN, 0.4f)
                         .addInputSalvage(Items.MAGMA_BLOCK, 0.2f)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(3)
                         .saveSalvage(consumer, prefix(TinkerModifiers.soulspeed, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.soulspeed, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.lightspeedArmor.get())
                                    .setTools(TinkerTags.Items.BOOTS)
                                    .setInput(Tags.Items.DUSTS_GLOWSTONE, 1, 64)
                                    .setSalvage(Items.GLOWSTONE_DUST, false)
                                    .setMaxLevel(3) // 45% running speed at max, conditionally
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.lightspeedArmor, upgradeSalvage))
                                    .save(consumer, wrap(TinkerModifiers.lightspeedArmor, upgradeFolder, "_from_dust"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.lightspeedArmor.get())
                                    .setTools(TinkerTags.Items.BOOTS)
                                    .setInput(Blocks.GLOWSTONE, 4, 64)
                                    .setLeftover(new ItemStack(Items.GLOWSTONE_DUST))
                                    .setMaxLevel(3)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(consumer, wrap(TinkerModifiers.lightspeedArmor, upgradeFolder, "_from_block"));
    // upgrade - all
    ModifierRecipeBuilder.modifier(TinkerModifiers.ricochet.get())
                         .setTools(TinkerTags.Items.ARMOR)
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInput(TinkerWorld.slime.get(SlimeType.SKY))
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(2) // 2 per piece gives +160% total
                         .saveSalvage(consumer, prefix(TinkerModifiers.ricochet, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.ricochet, upgradeFolder));

    // armor ability
    // helmet
    ModifierRecipeBuilder.modifier(TinkerModifiers.zoom.get())
                         .setTools(TinkerTags.Items.HELMETS)
                         .addInput(Tags.Items.GLASS_PANES)
                         .addInput(Tags.Items.STORAGE_BLOCKS_REDSTONE)
                         .addInput(Tags.Items.GLASS_PANES)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addSalvage(Items.REDSTONE, 0, 9)
                         .addSalvage(Items.GLASS_PANE, 0, 2)
                         .addSalvage(Items.COPPER_INGOT, 1, 2)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.zoom, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.zoom, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.slurping.get())
                         .addInputSalvage(Items.GLASS_BOTTLE, 0.5f)
                         .addInput(TinkerTags.Items.TANKS)
                         .addInputSalvage(Items.GLASS_BOTTLE, 0.5f)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addSalvage(Items.COPPER_INGOT, 1, 2)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HELMETS)
                         .saveSalvage(consumer, prefix(TinkerModifiers.slurping, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.slurping, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.aquaAffinity.get())
                         .addInput(Blocks.PRISMARINE_BRICKS)
                         .addInputSalvage(Items.HEART_OF_THE_SEA, 0.65f)
                         .addInput(Blocks.PRISMARINE_BRICKS)
                         .addInput(Blocks.DARK_PRISMARINE)
                         .addInput(Blocks.DARK_PRISMARINE)
                         .addSalvage(Items.PRISMARINE_SHARD, 8, 34)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HELMETS)
                         .saveSalvage(consumer, prefix(TinkerModifiers.aquaAffinity, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.aquaAffinity, abilityFolder));
    // chestplate
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
                         .saveSalvage(consumer, prefix(TinkerModifiers.unarmed, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.unarmed, abilityFolder));
    // if ichor geodes are disabled (sadface), use ichor dirt instead for the recipe
    // if you are disabling both, you have a ton of recipes to fix anyways
    IncrementalModifierRecipeBuilder geodeBuilder =
        IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.strength.get())
                                      .setTools(TinkerTags.Items.CHESTPLATES)
                                      .setInputSalvage(TinkerWorld.ichorGeode.asItem(), 1, 72, false)
                                      .setSlots(SlotType.ABILITY, 1);
    IncrementalModifierRecipeBuilder noGeodeBuilder =
      IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.strength.get())
                                      .setTools(TinkerTags.Items.CHESTPLATES)
                                      .setInputSalvage(TinkerWorld.slimeDirt.get(SlimeType.ICHOR), 1, 36, false)
                                      .setSlots(SlotType.ABILITY, 1);
    ConditionalRecipe.builder()
                     .addCondition(ConfigEnabledCondition.ICHOR_GEODES)
                     .addRecipe(geodeBuilder::save)
                     .addCondition(TrueCondition.INSTANCE)
                     .addRecipe(noGeodeBuilder::save)
                     .build(consumer, prefix(TinkerModifiers.strength, abilityFolder));
    // salvage needs to be a second recipe
    ConditionalRecipe.builder()
                     .addCondition(ConfigEnabledCondition.ICHOR_GEODES)
                     .addRecipe(c -> geodeBuilder.saveSalvage(c, TinkerModifiers.strength.getId()))
                     .addCondition(TrueCondition.INSTANCE)
                     .addRecipe(c -> noGeodeBuilder.saveSalvage(c, TinkerModifiers.strength.getId()))
                     .build(consumer, prefix(TinkerModifiers.strength, abilitySalvage));

    // leggings
    ModifierRecipeBuilder.modifier(TinkerModifiers.pockets.get())
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .addInputSalvage(Items.SHULKER_SHELL, 0.9f)
                         .addInputSalvage(Tags.Items.INGOTS_IRON, 0.7f)
                         .addInputSalvage(Items.SHULKER_SHELL, 0.9f)
                         .addInputSalvage(Items.LEATHER, 0.4f)
                         .addInputSalvage(Items.LEATHER, 0.4f)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.pockets, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.pockets, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.shieldStrap.get())
                         .addInputSalvage(TinkerWorld.skySlimeVine, 0.25f)
                         .addInputSalvage(TinkerMaterials.slimesteel.getIngotTag(), 0.7f)
                         .addInputSalvage(TinkerWorld.skySlimeVine, 0.25f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .saveSalvage(consumer, prefix(TinkerModifiers.shieldStrap, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.shieldStrap, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.toolBelt.get())
                         .addInputSalvage(Items.LEATHER, 0.7f)
                         .addInputSalvage(TinkerMaterials.hepatizon.getIngotTag(), 0.7f)
                         .addInputSalvage(Items.LEATHER, 0.7f)
                         .addInputSalvage(Items.LEATHER, 0.4f)
                         .addInputSalvage(Items.LEATHER, 0.4f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .saveSalvage(consumer, prefix(TinkerModifiers.toolBelt, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.toolBelt, abilityFolder));
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
                         .saveSalvage(consumer, prefix(TinkerModifiers.doubleJump, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.doubleJump, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.bouncy.get())
                         .setTools(IngredientDifference.difference(Ingredient.of(TinkerTags.Items.BOOTS), Ingredient.of(TinkerTools.slimesuit.get(ArmorSlotType.BOOTS))))
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY),   4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ICHOR), 4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY),   4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH), 4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH), 4)
                         .addSalvage(TinkerCommons.slimeball.get(SlimeType.EARTH), 4, 32)
                         .addSalvage(TinkerCommons.slimeball.get(SlimeType.SKY), 4, 32)
                         .addSalvage(TinkerCommons.slimeball.get(SlimeType.ICHOR), 4, 16)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.bouncy, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.bouncy, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.frostWalker.get())
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(Items.BLUE_ICE)
                         .addInputSalvage(TinkerWorld.heads.get(TinkerHeadType.STRAY), 0.5f)
                         .addInput(Items.BLUE_ICE)
                         .addInput(Items.BLUE_ICE)
                         .addInput(Items.BLUE_ICE)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.frostWalker, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.frostWalker, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.snowdrift.get())
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(Items.SNOW_BLOCK)
                         .addInput(Items.CARVED_PUMPKIN)
                         .addInput(Items.SNOW_BLOCK)
                         .addInput(Items.SNOW_BLOCK)
                         .addInput(Items.SNOW_BLOCK)
                         .addSalvage(Items.SNOWBALL, 0, 16)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.snowdrift, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.snowdrift, abilityFolder));

    // transform ingredients
    Ingredient bootsWithDuraibility = IngredientIntersection.intersection(Ingredient.of(TinkerTags.Items.BOOTS), Ingredient.of(TinkerTags.Items.DURABILITY));
    SizedIngredient roundPlate = SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.roundPlate.get()));
    SizedIngredient smallBlade = SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.smallBlade.get()));
    SizedIngredient toolBinding = SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.toolBinding.get()));
    ModifierRecipeBuilder.modifier(TinkerModifiers.pathMaker.get())
                         .setTools(bootsWithDuraibility)
                         .addInput(roundPlate)
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(toolBinding)
                         .addInput(roundPlate)
                         .addInput(toolBinding)
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.pathMaker, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.pathMaker, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.plowing.get())
                         .setTools(bootsWithDuraibility)
                         .addInput(smallBlade)
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(toolBinding)
                         .addInput(smallBlade)
                         .addInput(toolBinding)
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.plowing, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.plowing, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.flamewake.get())
                         .setTools(bootsWithDuraibility)
                         .addInputSalvage(Items.FLINT, 0.2f)
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInputSalvage(Items.FLINT, 0.2f)
                         .addInputSalvage(Items.FLINT, 0.2f)
                         .addInputSalvage(Items.FLINT, 0.2f)
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.flamewake, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.flamewake, abilityFolder));

    /*
     * ability
     */
    ModifierRecipeBuilder.modifier(TinkerModifiers.gilded.get())
                         .addInput(Items.GOLDEN_APPLE)
                         .addSalvage(Items.APPLE, 0.75f)
                         .addSalvage(Items.GOLD_INGOT, 1, 8)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.gilded, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.gilded, abilityFolder));
    // luck is 3 recipes, similar for both so pulled into a function
    luckRecipes(consumer, TinkerModifiers.luck.get(), TinkerTags.Items.MELEE_OR_HARVEST, SlotType.ABILITY, false, abilityFolder, abilitySalvage);
    luckRecipes(consumer, TinkerModifiers.looting.get(), TinkerTags.Items.CHESTPLATES, SlotType.UPGRADE, true, upgradeFolder, upgradeSalvage);
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
                         .saveSalvage(consumer, wrap(TinkerModifiers.luck, abilitySalvage, "_pants"))
                         .save(consumer, wrap(TinkerModifiers.luck, abilityFolder, "_pants"));
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
                         .saveSalvage(consumer, prefix(TinkerModifiers.silky, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.silky, abilityFolder));
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
                         .setTools(TinkerTags.Items.HARVEST)
                         .saveSalvage(consumer, prefix(TinkerModifiers.exchanging, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.exchanging, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.autosmelt.get())
                         .addInput(Items.FIRE_CHARGE)
                         .addInputSalvage(Blocks.MAGMA_BLOCK, 0.4f)
                         .addInput(Items.FIRE_CHARGE)
                         .addInput(TinkerMaterials.blazingBone)
                         .addInput(TinkerMaterials.blazingBone)
                         .addSalvage(TinkerMaterials.blazingBone, 1, 2)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HARVEST)
                         .saveSalvage(consumer, prefix(TinkerModifiers.autosmelt, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.autosmelt, abilityFolder));
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
                         .saveSalvage(consumer, prefix(TinkerModifiers.melting, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.melting, abilityFolder));
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
                         .saveSalvage(consumer, prefix(TinkerModifiers.bucketing, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.bucketing, abilityFolder));
    SizedIngredient channels = SizedIngredient.fromItems(TinkerSmeltery.searedChannel, TinkerSmeltery.scorchedChannel);
    Ingredient meleeChestplate = ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.CHESTPLATES);
    ModifierRecipeBuilder.modifier(TinkerModifiers.spilling.get())
                         .addInput(channels)
                         .addInput(TinkerTags.Items.TANKS)
                         .addInput(channels)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addSalvage(Items.COPPER_INGOT, 1, 2)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(meleeChestplate)
                         .saveSalvage(consumer, prefix(TinkerModifiers.spilling, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.spilling, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.tank.get()) // TODO: armor does not interact with chestplates for tanks, is that bad?
                         .addInput(TinkerTags.Items.TANKS) // no salvage as don't want conversion between seared and scorched
                         .setMaxLevel(5)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.INTERACTABLE, TinkerTags.Items.HELMETS))
                         .saveSalvage(consumer, prefix(TinkerModifiers.tank, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.tank, upgradeFolder));
    // expanders
    ModifierRecipeBuilder.modifier(TinkerModifiers.expanded.get())
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInputSalvage(TinkerMaterials.tinkersBronze.getIngotTag(), 1.0f)
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.AOE)
                         .saveSalvage(consumer, prefix(TinkerModifiers.expanded, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.expanded, abilityFolder));
    // reach expander
    ModifierRecipeBuilder.modifier(TinkerModifiers.reach.get())
                         .setTools(ingredientFromTags(TinkerTags.Items.HARVEST, TinkerTags.Items.CHESTPLATES))
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInputSalvage(TinkerMaterials.queensSlime.getIngotTag(), 1.0f)
                         .addInputSalvage(Items.PISTON, 0.9f)
                         .addInput(SlimeType.ENDER.getSlimeballTag())
                         .addInput(SlimeType.ENDER.getSlimeballTag())
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.reach, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.reach, abilityFolder));
    // block transformers
    Ingredient heldWithDurability = IngredientIntersection.intersection(Ingredient.of(TinkerTags.Items.DURABILITY), Ingredient.of(TinkerTags.Items.INTERACTABLE));
    ModifierRecipeBuilder.modifier(TinkerModifiers.pathing.get())
                         .setTools(IngredientDifference.difference(heldWithDurability, Ingredient.of(TinkerTools.mattock, TinkerTools.excavator)))
                         .addInput(roundPlate)
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(toolBinding)
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.pathing, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.pathing, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.stripping.get())
                         .setTools(IngredientDifference.difference(heldWithDurability, Ingredient.of(TinkerTools.handAxe, TinkerTools.broadAxe)))
                         .addInput(SizedIngredient.of(MaterialIngredient.fromItem(TinkerToolParts.smallAxeHead.get())))
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(toolBinding)
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.stripping, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.stripping, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.tilling.get())
                         .setTools(IngredientDifference.difference(heldWithDurability, Ingredient.of(TinkerTools.kama, TinkerTools.scythe)))
                         .addInput(smallBlade)
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(toolBinding)
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.tilling, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.tilling, abilityFolder));
    // glowing
    ModifierRecipeBuilder.modifier(TinkerModifiers.glowing.get())
                         .setTools(heldWithDurability)
                         .addInput(Items.GLOWSTONE)
                         .addInputSalvage(Items.DAYLIGHT_DETECTOR, 0.9f)
                         .addInputSalvage(Items.SHROOMLIGHT, 0.4f)
                         .addSalvage(Items.GLOWSTONE_DUST, 1, 4)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.glowing, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.glowing, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.firestarter.get())
                         .setTools(IngredientDifference.difference(heldWithDurability, Ingredient.of(TinkerTools.flintAndBronze)))
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInputSalvage(Items.FLINT, 0.2f)
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.firestarter, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.firestarter, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.fireprimer.get())
                         .setTools(Ingredient.of(TinkerTools.flintAndBronze))
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInputSalvage(Items.FLINT, 0.2f)
                         .addSalvage(Items.NETHERITE_SCRAP, 0.35f)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.fireprimer, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.fireprimer, upgradeFolder));

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
                         .saveSalvage(consumer, prefix(TinkerModifiers.unbreakable, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.unbreakable, abilityFolder));
    // weapon
    ModifierRecipeBuilder.modifier(TinkerModifiers.dualWielding.get())
                         .addInput(TinkerMaterials.manyullyn.getIngotTag())
                         .addInput(Items.NAUTILUS_SHELL)
                         .addInput(TinkerMaterials.manyullyn.getIngotTag())
                         .addInput(SlimeType.SKY.getSlimeballTag())
                         .addInput(SlimeType.SKY.getSlimeballTag())
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(IngredientDifference.difference(IngredientIntersection.intersection(Ingredient.of(TinkerTags.Items.MELEE), Ingredient.of(TinkerTags.Items.ONE_HANDED)), Ingredient.of(TinkerTools.dagger)))
                         .save(consumer, wrap(TinkerModifiers.dualWielding, abilityFolder, "_one_handed"));
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
                         .setTools(IngredientIntersection.intersection(Ingredient.of(TinkerTags.Items.MELEE), Ingredient.of(TinkerTags.Items.TWO_HANDED)))
                         .save(consumer, wrap(TinkerModifiers.dualWielding, abilityFolder, "_two_handed"));
    // using a single salvage recipe for the two of them, as we don't care about requirements when removing
    ModifierRecipeBuilder.modifier(TinkerModifiers.dualWielding.get())
                         .addSalvage(Items.NAUTILUS_SHELL, 0.95f)
                         .addSalvage(TinkerMaterials.manyullyn.getIngotTag(), 2, 2)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(IngredientDifference.difference(meleeChestplate, Ingredient.of(TinkerTools.dagger)))
                         .saveSalvage(consumer, prefix(TinkerModifiers.dualWielding, abilitySalvage));
    /*
     * extra modifiers
     */
    ModifierRecipeBuilder.modifier(TinkerModifiers.writable.get())
                         .addInputSalvage(Items.WRITABLE_BOOK, 1.0f)
                         .setMaxLevel(1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.writable, slotlessSalvage))
                         .save(consumer, prefix(TinkerModifiers.writable, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.harmonious.get())
                         .addInput(ItemTags.MUSIC_DISCS)
                         .addSalvage(Items.MUSIC_DISC_11, 1.0f) // your disc broke, now it sounds scary
                         .setMaxLevel(1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.harmonious, slotlessSalvage))
                         .save(consumer, prefix(TinkerModifiers.harmonious, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.recapitated.get()) // you want your head back? that is gross!
                         .addInput(SizedIngredient.of(IngredientDifference.difference(Ingredient.of(Tags.Items.HEADS), Ingredient.of(Items.DRAGON_HEAD))))
                         .setMaxLevel(1)
                         .save(consumer, prefix(TinkerModifiers.recapitated, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.resurrected.get())
                         .addInput(Items.END_CRYSTAL)
                         .addSalvage(Items.ENDER_EYE, 1.0f) // ironic, it could save others, but it could not save itself
                         .setMaxLevel(1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.resurrected, slotlessSalvage))
                         .save(consumer, prefix(TinkerModifiers.resurrected, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.draconic.get())
                         .addInput(Items.DRAGON_HEAD)
                         .setMaxLevel(1)
                         .save(consumer, wrap(TinkerModifiers.draconic, slotlessFolder, "_from_head"));
    ModifierRecipeBuilder.modifier(TinkerModifiers.draconic.get())
                         .addInput(Items.SHULKER_SHELL)
                         .addInputSalvage(TinkerModifiers.dragonScale, 1.0f) // you can apply the modifier in two ways, but scales are cheap so give them
                         .addInput(Blocks.WITHER_ROSE)
                         .addInputSalvage(TinkerModifiers.dragonScale, 0.5f)
                         .addInputSalvage(TinkerModifiers.dragonScale, 0.25f)
                         .setMaxLevel(1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.draconic, slotlessSalvage))
                         .save(consumer, wrap(TinkerModifiers.draconic, slotlessFolder, "_from_scales"));
    // creative
    // no salvage. I am not adding a recipe for creative modifiers, nope. don't want the gift from the server admin to be abused as a upgrade source
    SpecialRecipeBuilder.special(TinkerModifiers.creativeSlotSerializer.get()).save(consumer, modPrefix(slotlessFolder + "creative_slot"));

    // removal
    // temporary removal recipe until a proper table is added
    ModifierRemovalRecipe.Builder.removal(Ingredient.of(Blocks.WET_SPONGE), new ItemStack(Blocks.SPONGE))
                                 .save(consumer, modResource(slotlessFolder + "remove_modifier"));

    // compatability
    String theOneProbe = "theoneprobe";
    ResourceLocation probe = new ResourceLocation(theOneProbe, "probe");
    Consumer<FinishedRecipe> topConsumer = withCondition(consumer, modLoaded(theOneProbe));
    ModifierRecipeBuilder.modifier(TinkerModifiers.theOneProbe.get())
                         .setTools(ingredientFromTags(TinkerTags.Items.HELMETS, TinkerTags.Items.HELD))
                         .addInput(ItemNameIngredient.from(probe))
                         .addSalvage(RandomItem.chance(ItemNameOutput.fromName(probe), 0.9f))
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(1)
                         .saveSalvage(topConsumer, prefix(TinkerModifiers.theOneProbe, compatSalvage))
                         .save(topConsumer, prefix(TinkerModifiers.theOneProbe, compatFolder));

  }

  private void addTextureRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "tools/modifiers/slotless/";

    // travelers gear //
    consumer.accept(new ArmorDyeingRecipe.Finished(modResource(folder + "travelers_dyeing"), Ingredient.of(TinkerTools.travelersGear.values().stream().map(ItemStack::new))));

    // plate //
    Ingredient plate = Ingredient.of(TinkerTools.plateArmor.values().stream().map(ItemStack::new));
    // tier 2
    plateTexture(consumer, plate, MaterialIds.iron,   false, folder);
    plateTexture(consumer, plate, MaterialIds.copper, false, folder);
    // tier 3
    plateTexture(consumer, plate, MaterialIds.slimesteel,    false, folder);
    plateTexture(consumer, plate, MaterialIds.siliconBronze, "ingots/silicon_bronze", false, folder);
    plateTexture(consumer, plate, MaterialIds.roseGold,      false, folder);
    plateTexture(consumer, plate, MaterialIds.pigIron,       false, folder);
    // tier 4
    plateTexture(consumer, plate, MaterialIds.manyullyn, false, folder);
    plateTexture(consumer, plate, MaterialIds.hepatizon, false, folder);
    plateTexture(consumer, plate, MaterialIds.netherite, "nuggets/netherite", false, folder);
    // tier 2 compat
    plateTexture(consumer, plate, MaterialIds.osmium,   true, folder);
    plateTexture(consumer, plate, MaterialIds.tungsten, true, folder);
    plateTexture(consumer, plate, MaterialIds.platinum, true, folder);
    plateTexture(consumer, plate, MaterialIds.silver,   true, folder);
    plateTexture(consumer, plate, MaterialIds.lead,     true, folder);
    plateTexture(consumer, plate, MaterialIds.aluminum, true, folder);
    plateTexture(consumer, plate, MaterialIds.nickel,   true, folder);
    plateTexture(consumer, plate, MaterialIds.tin,      true, folder);
    plateTexture(consumer, plate, MaterialIds.zinc,     true, folder);
    plateTexture(consumer, plate, MaterialIds.uranium,  true, folder);
    // tier 3 compat
    plateTexture(consumer, plate, MaterialIds.steel,      true, folder);
    plateTexture(consumer, plate, MaterialIds.tinBronze,  "ingots/bronze", true, folder);
    plateTexture(consumer, plate, MaterialIds.constantan, true, folder);
    plateTexture(consumer, plate, MaterialIds.invar,      true, folder);
    plateTexture(consumer, plate, MaterialIds.electrum,   true, folder);
    plateTexture(consumer, plate, MaterialIds.brass,      true, folder);

    // slimesuit //
    Ingredient slimesuit = Ingredient.of(TinkerTools.slimesuit.values().stream().map(ItemStack::new));
    slimeTexture(consumer, slimesuit, MaterialIds.earthslime, SlimeType.EARTH, folder);
    slimeTexture(consumer, slimesuit, MaterialIds.skyslime,   SlimeType.SKY, folder);
    slimeTexture(consumer, slimesuit, MaterialIds.blood,      SlimeType.BLOOD, folder);
    slimeTexture(consumer, slimesuit, MaterialIds.ichor,      SlimeType.ICHOR, folder);
  }

  private void addHeadRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "tools/severing/";
    // first, beheading
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.ZOMBIE), Items.ZOMBIE_HEAD)
												 .save(consumer, modResource(folder + "zombie_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SKELETON), Items.SKELETON_SKULL)
												 .save(consumer, modResource(folder + "skeleton_skull"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.WITHER_SKELETON, EntityType.WITHER), Items.WITHER_SKELETON_SKULL)
												 .save(consumer, modResource(folder + "wither_skeleton_skull"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CREEPER), Items.CREEPER_HEAD)
												 .save(consumer, modResource(folder + "creeper_head"));
    SpecialRecipeBuilder.special(TinkerModifiers.playerBeheadingSerializer.get()).save(consumer, modPrefix(folder + "player_head"));
    SpecialRecipeBuilder.special(TinkerModifiers.snowGolemBeheadingSerializer.get()).save(consumer, modPrefix(folder + "snow_golem_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.IRON_GOLEM), Blocks.CARVED_PUMPKIN)
                         .save(consumer, modResource(folder + "iron_golem_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.ENDER_DRAGON), Items.DRAGON_HEAD)
                         .save(consumer, modResource(folder + "ender_dragon_head"));
    TinkerWorld.headItems.forEach((type, head) ->
      SeveringRecipeBuilder.severing(EntityIngredient.of(type.getType()), head)
                           .save(consumer, modResource(folder + type.getSerializedName() + "_head")));

    // other body parts
    // hostile
    // beeyeing
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SPIDER, EntityType.CAVE_SPIDER), Items.SPIDER_EYE)
                         .save(consumer, modResource(folder + "spider_eye"));
    // be-internal-combustion-device
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CREEPER), Blocks.TNT)
                         .save(consumer, modResource(folder + "creeper_tnt"));
    // bemembraning?
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.PHANTOM), Items.PHANTOM_MEMBRANE)
                         .save(consumer, modResource(folder + "phantom_membrane"));
    // beshelling
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SHULKER), Items.SHULKER_SHELL)
                         .save(consumer, modResource(folder + "shulker_shell"));
    // deboning
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.STRAY), ItemOutput.fromStack(new ItemStack(Items.BONE, 2)))
                         .save(consumer, modResource(folder + "skeleton_bone"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.WITHER_SKELETON), ItemOutput.fromStack(new ItemStack(TinkerMaterials.necroticBone, 2)))
                         .save(consumer, modResource(folder + "wither_skeleton_bone"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.BLAZE), ItemOutput.fromStack(new ItemStack(Items.BLAZE_ROD, 2)))
                         .save(consumer, modResource(folder + "blaze_rod"));
    // desliming (you cut off a chunk of slime)
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SLIME, TinkerWorld.earthSlimeEntity.get()), Items.SLIME_BALL)
                         .save(consumer, modResource(folder + "earthslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.skySlimeEntity.get()), TinkerCommons.slimeball.get(SlimeType.SKY))
                         .save(consumer, modResource(folder + "skyslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.enderSlimeEntity.get()), TinkerCommons.slimeball.get(SlimeType.ENDER))
                         .save(consumer, modResource(folder + "enderslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.terracubeEntity.get()), Items.CLAY_BALL)
                         .save(consumer, modResource(folder + "terracube_clay"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.MAGMA_CUBE), Items.MAGMA_CREAM)
                         .save(consumer, modResource(folder + "magma_cream"));
    // descaling? I don't know what to call those
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN), ItemOutput.fromStack(new ItemStack(Items.PRISMARINE_SHARD, 2)))
                         .save(consumer, modResource(folder + "guardian_shard"));

    // passive
    // befeating
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.RABBIT), Items.RABBIT_FOOT)
                         .setChildOutput(null) // only adults
												 .save(consumer, modResource(folder + "rabbit_foot"));
    // befeathering
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CHICKEN), ItemOutput.fromStack(new ItemStack(Items.FEATHER, 2)))
                         .setChildOutput(null) // only adults
                         .save(consumer, modResource(folder + "chicken_feather"));
    // beshrooming
    SpecialRecipeBuilder.special(TinkerModifiers.mooshroomDemushroomingSerializer.get()).save(consumer, modPrefix(folder + "mooshroom_shroom"));
    // beshelling
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.TURTLE), Items.TURTLE_HELMET)
                         .setChildOutput(ItemOutput.fromItem(Items.SCUTE))
                         .save(consumer, modResource(folder + "turtle_shell"));
    // befleecing
    SpecialRecipeBuilder.special(TinkerModifiers.sheepShearing.get()).save(consumer, modPrefix(folder + "sheep_wool"));
  }

  private void addSpillingRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "tools/spilling/";

    // vanilla
    SpillingRecipeBuilder.forFluid(Fluids.WATER, FluidAttributes.BUCKET_VOLUME / 20)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.WATER_SENSITIVE, DamageType.PIERCING, 2f))
                         .addEffect(ExtinguishSpillingEffect.INSTANCE)
                         .save(consumer, modResource(folder + "water"));
    SpillingRecipeBuilder.forFluid(Fluids.LAVA, FluidAttributes.BUCKET_VOLUME / 20)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 2f))
                         .addEffect(new SetFireSpillingEffect(10))
                         .save(consumer, modResource(folder + "lava"));
    SpillingRecipeBuilder.forFluid(Tags.Fluids.MILK, FluidAttributes.BUCKET_VOLUME / 10)
                         .addEffect(new CureEffectsSpillingEffect(new ItemStack(Items.MILK_BUCKET)))
                         .addEffect(StrongBonesModifier.SPILLING_EFFECT)
                         .save(consumer, modResource(folder + "milk"));
    // blaze - more damage, less fire
    SpillingRecipeBuilder.forFluid(TinkerFluids.blazingBlood.getLocalTag(), FluidAttributes.BUCKET_VOLUME / 20)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 3f))
                         .addEffect(new SetFireSpillingEffect(5))
                         .save(consumer, prefix(TinkerFluids.blazingBlood, folder));
    // slime
    int slimeballPiece = FluidValues.SLIMEBALL / 5;
    // earth - lucky
    SpillingRecipeBuilder.forFluid(TinkerFluids.earthSlime.getForgeTag(), slimeballPiece)
                         .addEffect(new EffectSpillingEffect(MobEffects.LUCK, 15, 1))
                         .addEffect(new EffectSpillingEffect(MobEffects.MOVEMENT_SLOWDOWN, 15, 1))
                         .save(consumer, prefix(TinkerFluids.earthSlime, folder));
    // sky - jump boost
    SpillingRecipeBuilder.forFluid(TinkerFluids.skySlime.getLocalTag(), slimeballPiece)
                         .addEffect(new EffectSpillingEffect(MobEffects.JUMP, 20, 1))
                         .addEffect(new EffectSpillingEffect(MobEffects.MOVEMENT_SLOWDOWN, 15, 1))
                         .save(consumer, prefix(TinkerFluids.skySlime, folder));
    // ender - levitation
    SpillingRecipeBuilder.forFluid(TinkerFluids.enderSlime.getLocalTag(), slimeballPiece)
                         .addEffect(new EffectSpillingEffect(MobEffects.LEVITATION, 5, 1))
                         .addEffect(new EffectSpillingEffect(MobEffects.MOVEMENT_SLOWDOWN, 15, 1))
                         .save(consumer, prefix(TinkerFluids.enderSlime, folder));
    // slimelike
    // blood - food
    SpillingRecipeBuilder.forFluid(TinkerFluids.blood.getLocalTag(), slimeballPiece)
                         .addEffect(new RestoreHungerSpillingEffect(1, 0.2f))
                         .addEffect(new EffectSpillingEffect(MobEffects.DIG_SLOWDOWN, 10, 1))
                         .save(consumer, prefix(TinkerFluids.blood, folder));
    // venom - poison
    SpillingRecipeBuilder.forFluid(TinkerFluids.venom.getLocalTag(), slimeballPiece)
                         .addEffect(new EffectSpillingEffect(MobEffects.POISON, 25, 1))
                         .save(consumer, prefix(TinkerFluids.venom, folder));
    // magma - fire resistance
    SpillingRecipeBuilder.forFluid(TinkerFluids.magma.getForgeTag(), slimeballPiece)
                         .addEffect(new EffectSpillingEffect(MobEffects.FIRE_RESISTANCE, 25, 1))
                         .save(consumer, prefix(TinkerFluids.magma, folder));
    // soul - slowness and blindness
    SpillingRecipeBuilder.forFluid(TinkerFluids.liquidSoul.getLocalTag(), slimeballPiece)
                         .addEffect(new EffectSpillingEffect(MobEffects.MOVEMENT_SLOWDOWN, 25, 2))
                         .addEffect(new EffectSpillingEffect(MobEffects.BLINDNESS, 5, 1))
                         .save(consumer, prefix(TinkerFluids.liquidSoul, folder));
    // ender - teleporting
    SpillingRecipeBuilder.forFluid(TinkerFluids.moltenEnder.getForgeTag(), FluidAttributes.BUCKET_VOLUME / 20)
                         .addEffect(new DamageSpillingEffect(DamageType.MAGIC, 1f))
                         .addEffect(TeleportSpillingEffect.INSTANCE)
                         .save(consumer, prefix(TinkerFluids.moltenEnder, folder));

    // multi-recipes
    SpillingRecipeBuilder.forFluid(TinkerTags.Fluids.GLASS_SPILLING, FluidAttributes.BUCKET_VOLUME / 10)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 1f))
                         .addEffect(new SetFireSpillingEffect(3))
                         .save(consumer, modResource(folder + "glass"));
    SpillingRecipeBuilder.forFluid(TinkerTags.Fluids.CLAY_SPILLING, FluidAttributes.BUCKET_VOLUME / 20)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 1.5f))
                         .addEffect(new SetFireSpillingEffect(3))
                         .save(consumer, modResource(folder + "clay"));

    SpillingRecipeBuilder.forFluid(TinkerTags.Fluids.CHEAP_METAL_SPILLING, FluidValues.NUGGET)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 1.5f))
                         .addEffect(new SetFireSpillingEffect(7))
                         .save(consumer, modResource(folder + "metal_cheap"));
    SpillingRecipeBuilder.forFluid(TinkerTags.Fluids.AVERAGE_METAL_SPILLING, FluidValues.NUGGET)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 2f))
                         .addEffect(new SetFireSpillingEffect(7))
                         .save(consumer, modResource(folder + "metal_average"));
    SpillingRecipeBuilder.forFluid(TinkerTags.Fluids.EXPENSIVE_METAL_SPILLING, FluidValues.NUGGET)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 2.5f))
                         .addEffect(new SetFireSpillingEffect(7))
                         .save(consumer, modResource(folder + "metal_expensive"));
    // gold applies magic
    SpillingRecipeBuilder.forFluid(TinkerFluids.moltenGold.getForgeTag(), FluidValues.NUGGET)
                         .addEffect(new DamageSpillingEffect(DamageType.MAGIC, 2f))
                         .addEffect(new SetFireSpillingEffect(3))
                         .save(consumer, prefix(TinkerFluids.moltenGold, folder));
    // pig iron fills you up magic
    SpillingRecipeBuilder.forFluid(TinkerFluids.moltenPigIron.getLocalTag(), FluidValues.NUGGET)
                         .addEffect(new RestoreHungerSpillingEffect(2, 0.3f))
                         .addEffect(new SetFireSpillingEffect(2))
                         .save(consumer, prefix(TinkerFluids.moltenPigIron, folder));
    // uranium also does poison
    SpillingRecipeBuilder.forFluid(TinkerFluids.moltenUranium.getLocalTag(), FluidValues.NUGGET)
                         .addEffect(new DamageSpillingEffect(LivingEntityPredicate.NOT_FIRE_IMMUNE, DamageType.FIRE, 1.5f))
                         .addEffect(new EffectSpillingEffect(MobEffects.POISON, 10, 1))
                         .addEffect(new SetFireSpillingEffect(3))
                         .save(consumer, prefix(TinkerFluids.moltenUranium, folder));

    // potion fluid compat
    ResourceLocation potionTag = new ResourceLocation("forge", "potion");
    // standard potion is 250 mb, but we want a smaller number. For the effects, we really want to divide into 4 pieces
    SpillingRecipeBuilder.forFluid(FluidIngredient.of(FluidTags.createOptional(potionTag), FluidAttributes.BUCKET_VOLUME / 8))
                         .addEffect(new PotionFluidEffect(0.5f, TagPredicate.ANY))
                         .save(withCondition(consumer, new NotCondition(new FluidTagEmptyCondition(potionTag))), modResource(folder + "potion_fluid"));

    // create has three types of bottles stored on their fluid, react to it to boost
    Function<String,TagPredicate> createBottle = value -> {
      CompoundTag compound = new CompoundTag();
      compound.putString("Bottle", value);
      return new TagPredicate(compound);
    };
    String create = "create";
    SpillingRecipeBuilder.forFluid(FluidNameIngredient.of(new ResourceLocation(create, "potion"), FluidAttributes.BUCKET_VOLUME / 8))
                         .addEffect(new PotionFluidEffect(0.25f, createBottle.apply("REGULAR")))
                         .addEffect(new PotionFluidEffect(0.5f, createBottle.apply("SPLASH")))
                         .addEffect(new PotionFluidEffect(1f, createBottle.apply("LINGERING")))
                         .save(withCondition(consumer, modLoaded(create)), modResource(folder + "create_potion_fluid"));

  }

  /** Adds recipes for a plate armor texture */
  private void plateTexture(Consumer<FinishedRecipe> consumer, Ingredient tool, MaterialId material, boolean optional, String folder) {
    plateTexture(consumer, tool, material, "ingots/" + material.getPath(), optional, folder);
  }

  /** Adds recipes for a plate armor texture with a custom tag */
  private void plateTexture(Consumer<FinishedRecipe> consumer, Ingredient tool, MaterialVariantId material, String tag, boolean optional, String folder) {
    Ingredient ingot = Ingredient.of(ItemTags.createOptional(new ResourceLocation("forge", tag)));
    if (optional) {
      consumer = withCondition(consumer, tagCondition(tag));
    }
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment.get(), material.toString())
                                  .setTools(tool)
                                  .addInput(ingot).addInput(ingot).addInput(ingot)
                                  .save(consumer, wrap(TinkerModifiers.embellishment, folder, "_" + material.getLocation('_').getPath()));
  }

  /** Adds recipes for a slime armor texture */
  private void slimeTexture(Consumer<FinishedRecipe> consumer, Ingredient tool, MaterialId material, SlimeType slime, String folder) {
    ItemLike congealed = TinkerWorld.congealedSlime.get(slime);
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment.get(), material.toString())
                                  .setTools(tool)
                                  .addInput(congealed).addInput(TinkerWorld.slime.get(slime)).addInput(congealed)
                                  .save(consumer, wrap(TinkerModifiers.embellishment, folder, "_" + slime.getSerializedName()));
  }

  private void burningSpilling(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, float damage, int time) {
    burningSpilling(consumer, fluid, damage, time, FluidValues.NUGGET);
  }

  private void burningSpilling(Consumer<FinishedRecipe> consumer, FluidObject<?> fluid, float damage, int time, int amount) {
    SpillingRecipeBuilder.forFluid(fluid.getLocalTag(), amount)
                         .addEffect(new DamageSpillingEffect(DamageType.FIRE, damage))
                         .addEffect(new SetFireSpillingEffect(time))
                         .save(consumer, prefix(fluid, "tools/spilling/"));
  }

  /** Common logic to add recipes for luck and unarmed looting */
  private void luckRecipes(Consumer<FinishedRecipe> consumer, Modifier modifier, Tag<Item> tools, SlotType slotType, boolean unarmed, String folder, String salvage) {
    String key = modifier.getId().getPath();
    // level 1 always requires a slot
    ModifierRecipeBuilder builder1 = ModifierRecipeBuilder.modifier(modifier)
                         .setTools(tools)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(SizedIngredient.fromItems(Items.CORNFLOWER, Items.BLUE_ORCHID))
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.STORAGE_BLOCKS_LAPIS)
                         .addInput(Tags.Items.STORAGE_BLOCKS_LAPIS)
                         .addSalvage(Items.COPPER_INGOT, 2, 2)
                         .addSalvage(Items.LAPIS_LAZULI, 3, 18)
                         .setSalvageLevelRange(1, 1)
                         .setMaxLevel(1)
                         .setSlots(slotType, 1);
    if (unarmed) {
      builder1.setRequirements(ModifierMatch.entry(TinkerModifiers.unarmed.get()));
      builder1.setRequirementsError(TConstruct.makeTranslationKey("recipe", "modifier.unarmed"));
    }
    builder1.saveSalvage(consumer, wrap(modifier, salvage, "_level_1")).save(consumer, wrap(modifier, folder, "_level_1"));
    // level 2 and 3 only charge if not ability
    ModifierRecipeBuilder builder2 = ModifierRecipeBuilder.modifier(modifier)
                         .setTools(tools)
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
                         .setMaxLevel(2);
    ModifierRecipeBuilder builder3 = ModifierRecipeBuilder.modifier(modifier)
                         .setTools(tools)
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
                         .setMaxLevel(3);
    if (slotType == SlotType.UPGRADE) {
      builder2.setSlots(slotType, 1);
      builder3.setSlots(slotType, 1);
    }
    builder2.saveSalvage(consumer, wrap(modifier, salvage, "_level_2")).save(consumer, wrap(modifier, folder, "_level_2"));
    builder3.saveSalvage(consumer, wrap(modifier, salvage, "_level_3")).save(consumer, wrap(modifier, folder, "_level_3"));
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
  private static Ingredient ingredientFromTags(Tag<Item>... tags) {
    Ingredient[] tagIngredients = new Ingredient[tags.length];
    for (int i = 0; i < tags.length; i++) {
      tagIngredients[i] = Ingredient.of(tags[i]);
    }
    return CompoundIngredient.from(tagIngredients);
  }
}
