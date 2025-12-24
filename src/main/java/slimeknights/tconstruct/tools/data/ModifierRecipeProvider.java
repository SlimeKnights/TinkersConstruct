package slimeknights.tconstruct.tools.data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.Tags.Fluids;
import net.minecraftforge.common.crafting.CompoundIngredient;
import net.minecraftforge.common.crafting.DifferenceIngredient;
import net.minecraftforge.common.crafting.IntersectionIngredient;
import net.minecraftforge.fluids.FluidType;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.datagen.MantleTags;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.data.ItemNameIngredient;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.helper.SimpleFinishedRecipe;
import slimeknights.mantle.recipe.ingredient.EntityIngredient;
import slimeknights.mantle.recipe.ingredient.FluidContainerIngredient;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.registration.object.WoodBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.data.BaseRecipeProvider;
import slimeknights.tconstruct.common.registration.GeodeItemObject.BudSize;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.gadgets.entity.FrameType;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.SlotTypeModifierPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.LazyModifier;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.PotionCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.ingredient.NoContainerIngredient;
import slimeknights.tconstruct.library.recipe.ingredient.ToolHookIngredient;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.MultilevelModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.adding.SwappableModifierRecipe.VariantFormatter;
import slimeknights.tconstruct.library.recipe.modifiers.adding.SwappableModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairRecipeBuilder;
import slimeknights.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.recipe.EnchantmentConvertingRecipeBuilder;
import slimeknights.tconstruct.tools.recipe.ModifierRemovalRecipeBuilder;
import slimeknights.tconstruct.tools.recipe.ModifierSortingRecipeBuilder;
import slimeknights.tconstruct.tools.recipe.ToggleInteractionWorktableRecipeBuilder;
import slimeknights.tconstruct.world.TinkerHeadType;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.FoliageType;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static slimeknights.mantle.Mantle.COMMON;
import static slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe.getTemperature;

public class ModifierRecipeProvider extends BaseRecipeProvider {
  public ModifierRecipeProvider(PackOutput packOutput) {
    super(packOutput);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Modifier Recipes";
  }

  @Override
  protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
    addItemRecipes(consumer);
    addModifierRecipes(consumer);
    addTextureRecipes(consumer);
    addHeadRecipes(consumer);
  }

  private void addItemRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "tools/modifiers/";

    // durability reinforcements, use obsidian
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.emeraldReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenEmerald, FluidValues.GEM_SHARD)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .save(consumer, prefix(TinkerModifiers.emeraldReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.slimesteelReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenSlimesteel, FluidValues.NUGGET * 3)
                            .setCast(TinkerCommons.obsidianPane, true)
                            .save(consumer, prefix(TinkerModifiers.slimesteelReinforcement, folder));
    // protection reinforcements, use patterns
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.ironReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenIron, FluidValues.INGOT)
                            .setCast(TinkerTables.pattern, true)
                            .save(consumer, prefix(TinkerModifiers.ironReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.searedReinforcement)
                            .setFluid(FluidIngredient.of(TinkerFluids.searedStone.ingredient(FluidValues.BRICK), TinkerFluids.scorchedStone.ingredient(FluidValues.BRICK)))
                            .setCoolingTime(getTemperature(TinkerFluids.searedStone), FluidValues.BRICK)
                            .setCast(TinkerTables.pattern, true)
                            .save(consumer, prefix(TinkerModifiers.searedReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.goldReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenGold, FluidValues.INGOT)
                            .setCast(TinkerTables.pattern, true)
                            .save(consumer, prefix(TinkerModifiers.goldReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.obsidianReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenObsidian, FluidValues.GLASS_BLOCK)
                            .setCast(TinkerTables.pattern, true)
                            .save(consumer, prefix(TinkerModifiers.obsidianReinforcement, folder));
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.cobaltReinforcement)
                            .setFluidAndTime(TinkerFluids.moltenCobalt, FluidValues.INGOT)
                            .setCast(TinkerTables.pattern, true)
                            .save(consumer, prefix(TinkerModifiers.cobaltReinforcement, folder));

    // jeweled apple
    ItemCastingRecipeBuilder.tableRecipe(TinkerCommons.jeweledApple)
                            .setFluidAndTime(TinkerFluids.moltenDiamond, FluidValues.GEM * 2)
                            .setCast(Items.APPLE, true)
                            .save(consumer, prefix(TinkerCommons.jeweledApple, folder));

    // silky cloth
    ItemCastingRecipeBuilder.tableRecipe(TinkerModifiers.silkyCloth)
                            .setCast(Items.COBWEB, true)
                            .setFluidAndTime(TinkerFluids.moltenRoseGold, FluidValues.INGOT)
                            .save(consumer, prefix(TinkerModifiers.silkyCloth, folder));

    // modifier repair
    // pig iron - from bacon, only in the tinker station
    ModifierRepairRecipeBuilder.repair(TinkerModifiers.tasty, Ingredient.of(TinkerCommons.bacon), 25)
                               .save(consumer, prefix(TinkerModifiers.tasty, folder));
  }

  @SuppressWarnings("removal")
  private void addModifierRecipes(Consumer<FinishedRecipe> consumer) {
    // modifiers
    String upgradeFolder = "tools/modifiers/upgrade/";
    String abilityFolder = "tools/modifiers/ability/";
    String slotlessFolder = "tools/modifiers/slotless/";
    String defenseFolder = "tools/modifiers/defense/";
    String compatFolder = "tools/modifiers/compat/";
    String worktableFolder = "tools/modifiers/worktable/";
    // salvage
    String salvageFolder = "tools/modifiers/salvage/";
    String upgradeSalvage = salvageFolder + "upgrade/";
    String abilitySalvage = salvageFolder + "ability/";
    String defenseSalvage = salvageFolder + "defense/";
    String compatSalvage = salvageFolder + "compat/";

    /*
     * durability
     */
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.reinforced)
                         .setInput(TinkerModifiers.emeraldReinforcement, 1, 4)
                         .setMaxLevel(5) // max 75% resistant to damage
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.DURABILITY)
                         .saveSalvage(consumer, prefix(ModifierIds.reinforced, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.reinforced, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.overforced)
                                    .setInput(TinkerModifiers.slimesteelReinforcement, 1, 4)
                                    .setMaxLevel(5) // +250 capacity
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setTools(TinkerTags.Items.DURABILITY)
                                    .saveSalvage(consumer, prefix(ModifierIds.overforced, upgradeSalvage))
                                    .save(consumer, prefix(ModifierIds.overforced, upgradeFolder));
    // gems are special, I'd like them to be useful on all types of tools
    ModifierRecipeBuilder.modifier(ModifierIds.emerald)
                         .setTools(TinkerTags.Items.DURABILITY)
                         .addInput(Tags.Items.GEMS_EMERALD)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(consumer, prefix(ModifierIds.emerald, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.emerald, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.diamond)
                         .setTools(TinkerTags.Items.DURABILITY)
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(consumer, prefix(ModifierIds.diamond, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.diamond, upgradeFolder));
    Ingredient multiuse = DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.MODIFIABLE), Ingredient.of(TinkerTags.Items.SINGLE_USE));
    ModifierRecipeBuilder.modifier(ModifierIds.worldbound)
      .setTools(multiuse)
      .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
      .setMaxLevel(1)
      .save(consumer, prefix(ModifierIds.worldbound, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.soulbound)
      .setTools(multiuse)
      .addInput(Items.ECHO_SHARD)
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(1)
      .saveSalvage(consumer, prefix(ModifierIds.soulbound, upgradeSalvage))
      .save(consumer, prefix(ModifierIds.soulbound, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.soulbound)
      .setTools(TinkerTags.Items.SINGLE_USE)
      .addInput(Items.SCULK_VEIN)
      .setMaxLevel(1)
      .save(consumer, wrap(ModifierIds.soulbound, slotlessFolder, "_ammo"));
    ModifierRecipeBuilder.modifier(ModifierIds.netherite)
                         .setTools(TinkerTags.Items.DURABILITY)
                         .addInput(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(consumer, prefix(ModifierIds.netherite, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.netherite, upgradeFolder));

    // overslime
    Ingredient overslimeTools = Ingredient.of(TinkerTags.Items.DURABILITY);
    for (SlimeType type : SlimeType.values()) {
      int amount;
      switch (type) {
        // earth is common and easy to get
        case EARTH -> amount = 20;
        // sky is tinkers specialty
        case SKY -> amount = 50;
        // ichor is hard to farm
          case ICHOR -> amount = 100;
        // ender is late game, but easier to farm than ichor
        case ENDER -> {
          amount = 80;
        }
        // unhandled -> update
        default -> {
          continue;
        }
      };
      String name = type.getSerializedName();
      // ball and bottle - base amount
      OverslimeModifierRecipeBuilder.modifier(TinkerCommons.slimeball.get(type), amount)
        .setTools(overslimeTools)
        .save(consumer, location(slotlessFolder + "overslime/" + name + "_ball"));
      OverslimeModifierRecipeBuilder.modifier(TinkerFluids.slimeBottle.get(type), amount)
        .saveCrafting(consumer, location(slotlessFolder + "overslime/" + name + "_bottle_crafting_table"))
        .save(consumer, location(slotlessFolder + "overslime/" + name + "_bottle"));
      // congealed: 4x
      OverslimeModifierRecipeBuilder.modifier(TinkerWorld.congealedSlime.get(type), amount * 4)
        .setTools(overslimeTools)
        .save(consumer, location(slotlessFolder + "overslime/" + name + "_congealed"));
      // block: 9x
      OverslimeModifierRecipeBuilder.modifier(TinkerWorld.slime.get(type), amount * 9)
        .setTools(overslimeTools)
        .save(consumer, location(slotlessFolder + "overslime/" + name + "_block"));
    }

    /*
     * general effects
     */
    ModifierRecipeBuilder.modifier(ModifierIds.experienced)
                         .addInput(Items.EXPERIENCE_BOTTLE)
                         .addInput(Items.EXPERIENCE_BOTTLE)
                         .addInput(Items.EXPERIENCE_BOTTLE)
                         .setMaxLevel(5) // max +250%
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.LAUNCHERS, TinkerTags.Items.LEGGINGS))
                         .saveSalvage(consumer, prefix(ModifierIds.experienced, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.experienced, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.magnetic)
                         .addInput(Items.COMPASS)
                         .setMaxLevel(5)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.HARVEST))
                         .save(consumer, prefix(TinkerModifiers.magnetic, upgradeFolder));
    // armor has a max level of 1 per piece, so 4 total
    ModifierRecipeBuilder.modifier(TinkerModifiers.magnetic)
                         .addInput(Items.COMPASS)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.WORN_ARMOR) // TODO: reconsider for shields
                         .save(consumer, wrap(TinkerModifiers.magnetic, upgradeFolder, "_armor"));
    // salvage supports either
    ModifierRecipeBuilder.modifier(TinkerModifiers.magnetic)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.HARVEST, TinkerTags.Items.WORN_ARMOR))
                         .saveSalvage(consumer, prefix(TinkerModifiers.magnetic, upgradeSalvage));
    ModifierRecipeBuilder.modifier(ModifierIds.shiny)
                         .addInput(Ingredient.of(Items.ENCHANTED_GOLDEN_APPLE, Items.NETHER_STAR))
                         .setMaxLevel(1)
                         .save(consumer, prefix(ModifierIds.shiny, slotlessFolder));
    Ingredient sighted = ingredientFromTags(TinkerTags.Items.HELD, TinkerTags.Items.ARMOR);
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.farsighted)
                                    .setTools(sighted)
                                    .setInput(Tags.Items.CROPS_CARROT, 1, 45)
                                    .save(consumer, prefix(TinkerModifiers.farsighted, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.nearsighted)
                                    .setTools(sighted)
                                    .setInput(Items.INK_SAC, 1, 45)
                                    .save(consumer, prefix(TinkerModifiers.nearsighted, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.offhanded)
                         .setTools(TinkerTags.Items.INTERACTABLE_RIGHT)
                         .addInput(Items.LEATHER)
                         .addInput(Items.FIRE_CHARGE)
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .setMaxLevel(2)
                         .save(consumer, prefix(ModifierIds.offhanded, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.smelting)
                         .setTools(ingredientFromTags(TinkerTags.Items.INTERACTABLE, TinkerTags.Items.WORN_ARMOR))
                         .addInput(Blocks.CAMPFIRE)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(consumer, prefix(ModifierIds.smelting, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.smelting, upgradeFolder));

    /*
     * Speed
     */

    // haste can use redstone or blocks
    hasteRecipes(consumer, ModifierIds.haste, ingredientFromTags(TinkerTags.Items.HARVEST, TinkerTags.Items.CHESTPLATES), 5, upgradeFolder, upgradeSalvage);
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.blasting)
                                    .setTools(TinkerTags.Items.STONE_HARVEST)
                                    .setInput(Tags.Items.GUNPOWDER, 1, 20)
                                    .setMaxLevel(5) // +50 mining speed at max, conditionally
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.blasting, upgradeSalvage))
                                    .save(consumer, prefix(ModifierIds.blasting, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.hydraulic)
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Tags.Items.DUSTS_PRISMARINE, 1, 36) // stupid forge name
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.hydraulic, upgradeSalvage))
                                    .save(consumer, wrap(ModifierIds.hydraulic, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.hydraulic)
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Blocks.PRISMARINE, 4, 36)
                                    .setLeftover(Items.PRISMARINE_SHARD)
                                    .setMaxLevel(5)
                                    .disallowCrystal()
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(consumer, wrap(ModifierIds.hydraulic, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.hydraulic)
                                    .setTools(TinkerTags.Items.HARVEST)
                                    .setInput(Blocks.PRISMARINE_BRICKS, 9, 36)
                                    .setLeftover(Items.PRISMARINE_SHARD)
                                    .setMaxLevel(5)
                                    .disallowCrystal()
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(consumer, wrap(ModifierIds.hydraulic, upgradeFolder, "_from_bricks"));
    IncrementalModifierRecipeBuilder lightspeed = IncrementalModifierRecipeBuilder.modifier(ModifierIds.lightspeed)
      .setInput(Tags.Items.DUSTS_GLOWSTONE, 1, 64)
      .setMaxLevel(5) // +45 mining speed at max, conditionally
      .setSlots(SlotType.UPGRADE, 1)
      .setTools(ingredientFromTags(TinkerTags.Items.HARVEST, TinkerTags.Items.BOOTS))
      .saveSalvage(consumer, prefix(ModifierIds.lightspeed, upgradeSalvage));
    lightspeed
      .setTools(TinkerTags.Items.HARVEST)
      .save(consumer, wrap(ModifierIds.lightspeed, upgradeFolder, "_harvest_from_dust"));
    lightspeed
      .setMaxLevel(3) // 27% running speed at max, conditionally
      .setTools(TinkerTags.Items.BOOTS)
      .save(consumer, wrap(ModifierIds.lightspeed, upgradeFolder, "_boots_from_dust"));
    lightspeed = IncrementalModifierRecipeBuilder.modifier(ModifierIds.lightspeed)
      .setInput(Blocks.GLOWSTONE, 4, 64)
      .setLeftover(Items.GLOWSTONE_DUST)
      .disallowCrystal()
      .setSlots(SlotType.UPGRADE, 1);
    lightspeed
      .setMaxLevel(5)
      .setTools(TinkerTags.Items.HARVEST)
      .save(consumer, wrap(ModifierIds.lightspeed, upgradeFolder, "_harvest_from_block"));
    lightspeed
      .setMaxLevel(3)
      .setTools(TinkerTags.Items.BOOTS)
      .save(consumer, wrap(ModifierIds.lightspeed, upgradeFolder, "_boots_from_block"));

    /*
     * weapon
     */
    ModifierRecipeBuilder.modifier(TinkerModifiers.knockback)
                         .addInput(Items.PISTON)
                         .addInput(TinkerWorld.slime.get(SlimeType.EARTH))
                         .setMaxLevel(3) // max +2.5 knockback points (knockback 5) (whatever that number means in vanilla)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.CHESTPLATES))
                         .saveSalvage(consumer, prefix(TinkerModifiers.knockback, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.knockback, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.padded)
                         .addInput(Items.LEATHER)
                         .addInput(ItemTags.WOOL)
                         .addInput(Items.LEATHER)
                         .setMaxLevel(3) // max 12.5% knockback, or 6.25% on the dagger
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.MELEE)
                         .saveSalvage(consumer, prefix(TinkerModifiers.padded, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.padded, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.severing)
                         .addInput(TinkerTags.Items.WITHER_BONES)
                         .addInput(Items.LIGHTNING_ROD)
                         .addInput(TinkerTags.Items.WITHER_BONES)
                         .addInput(Items.TNT)
                         .setMaxLevel(3) // max +25% head drop chance, combine with +15% chance from luck
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.LAUNCHERS))
                         .saveSalvage(consumer, prefix(TinkerModifiers.severing, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.severing, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.fiery)
                                    .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.BOWS, TinkerTags.Items.FISHING_RODS, TinkerTags.Items.WORN_ARMOR, TinkerTags.Items.SHIELDS))
                                    .setInput(Items.BLAZE_POWDER, 1, 25)
                                    .setMaxLevel(5) // +25 seconds fire damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.fiery, upgradeSalvage))
                                    .save(consumer, prefix(ModifierIds.fiery, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.necrotic)
                         .addInput(TinkerMaterials.necroticBone)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ICHOR))
                         .addInput(Items.GHAST_TEAR)
                         .setMaxLevel(5) // +50% chance of heal, combine with +40% from traits for +90% total
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.BOWS, TinkerTags.Items.FISHING_RODS))
                         .saveSalvage(consumer, prefix(TinkerModifiers.necrotic, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.necrotic, upgradeFolder));

    /*
     * damage boost
     */
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.pierce)
                                    .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.LAUNCHERS))
                                    .setInput(TinkerGadgets.punji, 1, 10)
                                    .setMaxLevel(3) // +3 pierce, +1.5 damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.pierce, upgradeSalvage))
                                    .save(consumer, prefix(ModifierIds.pierce, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.piercing)
                                    .setTools(ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.BOWS))
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.piercing, upgradeSalvage));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.smite)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.GLISTERING_MELON_SLICE, 1, 5)
                                    .setMaxLevel(5) // +12.5 undead damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.smite, upgradeSalvage))
                                    .save(consumer, prefix(ModifierIds.smite, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.baneOfSssss)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.FERMENTED_SPIDER_EYE, 1, 15)
                                    .setMaxLevel(5) // +12.5 spider damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.baneOfSssss, upgradeSalvage))
                                    .save(consumer, prefix(ModifierIds.baneOfSssss, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.antiaquatic)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.PUFFERFISH, 1, 5)
                                    .setMaxLevel(5) // +12.5 fish damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.antiaquatic, upgradeSalvage))
                                    .save(consumer, prefix(ModifierIds.antiaquatic, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.cooling)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Items.PRISMARINE_CRYSTALS, 1, 25)
                                    .setMaxLevel(5) // +10 fire mob damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.cooling, upgradeSalvage))
                                    .save(consumer, prefix(ModifierIds.cooling, upgradeFolder));
    // killager uses both types of lapis
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.killager)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.GEMS_LAPIS, 1, 45)
                                    .setMaxLevel(5) // +12.5 illager damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.killager, upgradeSalvage))
                                    .save(consumer, wrap(ModifierIds.killager, upgradeFolder, "_from_dust"));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.killager)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_LAPIS, 9, 45)
                                    .setMaxLevel(5) // +12.5 illager damage
                                    .disallowCrystal()
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(consumer, wrap(ModifierIds.killager, upgradeFolder, "_from_block"));
    // sharpness can use shards or blocks
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.sharpness)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.GEMS_QUARTZ, 1, 36)
                                    .setMaxLevel(5) // +5 damage
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.sharpness, upgradeSalvage))
                                    .save(consumer, wrap(ModifierIds.sharpness, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.sharpness)
                                    .setTools(TinkerTags.Items.MELEE)
                                    .setInput(Tags.Items.STORAGE_BLOCKS_QUARTZ, 4, 36)
                                    .setLeftover(new ItemStack(Items.QUARTZ))
                                    .setMaxLevel(5)
                                    .disallowCrystal()
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(consumer, wrap(ModifierIds.sharpness, upgradeFolder, "_from_block"));
    IncrementalModifierRecipeBuilder.modifier(TinkerModifiers.sweeping)
                                    .setTools(TinkerTags.Items.SWORD)
                                    .setInput(Blocks.CHAIN, 1, 5) // 5% per chain, costing 55 nuggets, or just above 6 ingots
                                    .setMaxLevel(3) // goes 25%, 50%, 75%
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(TinkerModifiers.sweeping, upgradeSalvage))
                                    .save(consumer, prefix(TinkerModifiers.sweeping, upgradeFolder));
    // swiftstrike works on blocks too, we are nice
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.swiftstrike)
                                    .setTools(TinkerTags.Items.MELEE_WEAPON)
                                    .setInput(Items.AMETHYST_SHARD, 1, 72)
                                    .setMaxLevel(5)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.swiftstrike, upgradeSalvage))
                                    .save(consumer, wrap(ModifierIds.swiftstrike, upgradeFolder, "_from_shard"));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.swiftstrike)
                                    .setTools(TinkerTags.Items.MELEE_WEAPON)
                                    .setInput(Blocks.AMETHYST_BLOCK, 4, 72)
                                    .setLeftover(new ItemStack(Items.AMETHYST_SHARD))
                                    .setMaxLevel(5)
                                    .disallowCrystal()
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .save(consumer, wrap(ModifierIds.swiftstrike, upgradeFolder, "_from_block"));

    /*
     * ranged
     */
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.power)
                                    .setTools(ingredientFromTags(TinkerTags.Items.LONGBOWS, TinkerTags.Items.STAFFS, TinkerTags.Items.FISHING_RODS))
                                    .setInput(TinkerWorld.ichorGeode.asItem(), 1, 72)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setMaxLevel(5)
                                    .saveSalvage(consumer, prefix(ModifierIds.power, upgradeSalvage))
                                    .save(consumer, prefix(ModifierIds.power, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.quickCharge)
                                    .setTools(ingredientFromTags(TinkerTags.Items.CROSSBOWS, TinkerTags.Items.STAFFS, TinkerTags.Items.FISHING_RODS))
                                    .setInput(Items.MAGMA_CREAM, 1, 5)
                                    .setMaxLevel(4)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.quickCharge, upgradeSalvage))
                                    .save(consumer, prefix(ModifierIds.quickCharge, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.trueshot)
                                    .setInput(Items.TARGET, 1, 10)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setMaxLevel(3)
                                    .setTools(TinkerTags.Items.RANGED)
                                    .saveSalvage(consumer, prefix(ModifierIds.trueshot, upgradeSalvage))
                                    .save(consumer, prefix(ModifierIds.trueshot, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.blindshot)
                                    .setInput(Items.DIRT, 1, 10)
                                    .setTools(TinkerTags.Items.RANGED)
                                    .save(consumer, prefix(ModifierIds.blindshot, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.punch)
                         .addInput(Items.PISTON)
                         .addInput(TinkerWorld.slime.get(SlimeType.SKY))
                         .setMaxLevel(3) // vanilla caps at 2, we want to go a bit beyond that, but it becomes broken too high on fishing rods
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.LAUNCHERS)
                         .saveSalvage(consumer, prefix(ModifierIds.punch, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.punch, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.arrowPierce)
                         .addInput(Items.POINTED_DRIPSTONE)
                         .addInput(Items.POINTED_DRIPSTONE)
                         .addInput(Items.POINTED_DRIPSTONE)
                         .setMaxLevel(4) // same max as vanilla
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.BOWS) // salvage for old recipe
                         .saveSalvage(consumer, prefix(ModifierIds.arrowPierce, upgradeSalvage))
                         .setTools(TinkerTags.Items.CROSSBOWS)
                         .save(consumer, prefix(ModifierIds.arrowPierce, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.bounce)
      .addInput(Items.PISTON)
      .addInput(TinkerWorld.slime.get(SlimeType.ICHOR))
      .setMaxLevel(3) // 7 bounces is more than you will ever need
      .setSlots(SlotType.UPGRADE, 1)
      .setTools(TinkerTags.Items.LONGBOWS)
      .saveSalvage(consumer, prefix(ModifierIds.bounce, upgradeSalvage))
      .save(consumer, prefix(ModifierIds.bounce, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.freezing)
                         .addInput(Items.POWDER_SNOW_BUCKET)
                         .setMaxLevel(3)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.BOWS, TinkerTags.Items.FISHING_RODS, TinkerTags.Items.WORN_ARMOR, TinkerTags.Items.SHIELDS))
                         .saveSalvage(consumer, prefix(ModifierIds.freezing, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.freezing, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.bulkQuiver)
                         .addInput(Items.LEATHER)
                         .addInput(TinkerWorld.skySlimeVine)
                         .addInput(Items.LEATHER)
                         .addInput(TinkerWorld.skySlimeVine)
                         .addInput(TinkerWorld.skySlimeVine)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.BOWS)
                         .saveSalvage(consumer, prefix(ModifierIds.bulkQuiver, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.bulkQuiver, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.trickQuiver)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerWorld.skySlimeVine)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerWorld.skySlimeVine)
                         .addInput(TinkerWorld.skySlimeVine)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.BOWS)
                         .saveSalvage(consumer, prefix(ModifierIds.trickQuiver, abilitySalvage))
                         .setTools(IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.BOWS), Ingredient.of(TinkerTags.Items.INTERACTABLE)))
                         .save(consumer, prefix(ModifierIds.trickQuiver, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.ballista)
      .addInput(TinkerMaterials.hepatizon.getIngotTag())
      .addInput(Items.CHAIN)
      .addInput(TinkerMaterials.hepatizon.getIngotTag())
      .setSlots(SlotType.ABILITY, 1)
      .setMaxLevel(1).checkTraitLevel()
      .setTools(TinkerTags.Items.BALLISTAS)
      .saveSalvage(consumer, prefix(ModifierIds.ballista, abilitySalvage))
      .save(consumer, prefix(ModifierIds.ballista, abilityFolder));
    BiConsumer<ItemLike,String> crystalshotRecipe = (item, variant) ->
      SwappableModifierRecipeBuilder.modifier(ModifierIds.crystalshot, variant)
                                    .addInput(item)
                                    .addInput(Items.BLAZE_ROD)
                                    .addInput(item)
                                    .addInput(TinkerMaterials.manyullyn.getIngotTag())
                                    .addInput(TinkerMaterials.manyullyn.getIngotTag())
                                    .setTools(TinkerTags.Items.BOWS)
                                    .setSlots(SlotType.ABILITY, 1)
                                    .save(consumer, wrap(ModifierIds.crystalshot, abilityFolder, "_" + variant));
    crystalshotRecipe.accept(Items.AMETHYST_CLUSTER, "amethyst");
    crystalshotRecipe.accept(TinkerWorld.earthGeode.getBud(BudSize.CLUSTER), "earthslime");
    crystalshotRecipe.accept(TinkerWorld.skyGeode.getBud(BudSize.CLUSTER), "skyslime");
    crystalshotRecipe.accept(TinkerWorld.ichorGeode.getBud(BudSize.CLUSTER), "ichor");
    crystalshotRecipe.accept(TinkerWorld.enderGeode.getBud(BudSize.CLUSTER), "enderslime");
    crystalshotRecipe.accept(Items.NETHER_QUARTZ_ORE, "quartz");
    SwappableModifierRecipeBuilder.modifier(ModifierIds.crystalshot, "random")
                                  .addInput(Ingredient.of(TinkerWorld.earthGeode.getBud(BudSize.CLUSTER), TinkerWorld.skyGeode.getBud(BudSize.CLUSTER)))
                                  .addInput(Ingredient.of(Items.AMETHYST_CLUSTER, Items.NETHER_QUARTZ_ORE))
                                  .addInput(Ingredient.of(TinkerWorld.ichorGeode.getBud(BudSize.CLUSTER), TinkerWorld.enderGeode.getBud(BudSize.CLUSTER)))
                                  .addInput(TinkerMaterials.manyullyn.getIngotTag())
                                  .addInput(TinkerMaterials.manyullyn.getIngotTag())
                                  .setTools(TinkerTags.Items.BOWS)
                                  .setSlots(SlotType.ABILITY, 1)
                                  .allowCrystal() // random is the coolest, and happens to be the easiest to enable
                                  .save(consumer, wrap(ModifierIds.crystalshot, abilityFolder, "_random"));
    ModifierRecipeBuilder.modifier(ModifierIds.crystalshot)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.BOWS)
                         .saveSalvage(consumer, prefix(ModifierIds.crystalshot, abilitySalvage));
    ModifierRecipeBuilder.modifier(ModifierIds.barebow)
      .setTools(TinkerTags.Items.BOWS)
      .addInput(Tags.Items.STRING)
      .addInput(Tags.Items.RODS_WOODEN)
      .addInput(Tags.Items.STRING)
      .setMaxLevel(1)
      .save(consumer, prefix(ModifierIds.barebow, slotlessFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.multishot)
                         .addInput(Items.PISTON)
                         .addInput(TinkerMaterials.amethystBronze.getIngotTag())
                         .addInput(Items.PISTON)
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.BOWS) // allow salvaging multishot from an older bow
                         .saveSalvage(consumer, prefix(TinkerModifiers.multishot, abilitySalvage))
                         .setTools(TinkerTags.Items.CROSSBOWS) // crossbow exclusive now
                         .save(consumer, prefix(TinkerModifiers.multishot, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.sinistral)
                         .addInput(TinkerMaterials.slimesteel.getIngotTag())
                         .addInput(Items.NAUTILUS_SHELL)
                         .addInput(TinkerMaterials.slimesteel.getIngotTag())
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.CROSSBOWS), Ingredient.of(TinkerTags.Items.INTERACTABLE_LEFT))) // this is the same recipes as dual wielding, but crossbows do not interact on left
                         .saveSalvage(consumer, prefix(TinkerModifiers.sinistral, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.sinistral, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.scope)
      .setTools(TinkerTags.Items.INTERACTABLE_CHARGE)
      .addInput(Items.SUGAR)
      .addInput(Items.SPYGLASS)
      .addInput(Items.SUGAR)
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(1).checkTraitLevel()
      .saveSalvage(consumer, prefix(ModifierIds.scope, upgradeSalvage))
      .save(consumer, prefix(ModifierIds.scope, upgradeFolder));

    // fishing
    ModifierRecipeBuilder.modifier(ModifierIds.lure)
      .setTools(TinkerTags.Items.FISHING_RODS)
      .addInput(TinkerCommons.cheeseIngot)
      .addInput(TinkerCommons.cheeseIngot)
      .addInput(TinkerCommons.cheeseIngot)
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(3)
      .saveSalvage(consumer, prefix(ModifierIds.lure, upgradeSalvage))
      .save(consumer, prefix(ModifierIds.lure, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.grapple)
      .setTools(TinkerTags.Items.FISHING_RODS)
      .addInput(Items.CHAIN)
      .addInput(Items.CHAIN)
      .addInput(TinkerMaterials.cinderslime.getIngotTag())
      .setSlots(SlotType.ABILITY, 1)
      .setMaxLevel(1).checkTraitLevel()
      .saveSalvage(consumer, prefix(ModifierIds.grapple, abilitySalvage))
      .save(consumer, prefix(ModifierIds.grapple, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.drillAttack)
      // allow on anything that might get springing, flinging, or grapple
      .setTools(ingredientFromTags(TinkerTags.Items.INTERACTABLE_CHARGE, TinkerTags.Items.FISHING_RODS))
      .addInput(TinkerMaterials.blazingBone)
      .addInput(Items.POINTED_DRIPSTONE)
      .addInput(TinkerMaterials.blazingBone)
      .setSlots(SlotType.ABILITY, 1)
      .setMaxLevel(1).checkTraitLevel()
      .saveSalvage(consumer, prefix(ModifierIds.drillAttack, abilitySalvage))
      .save(consumer, prefix(ModifierIds.drillAttack, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.collecting)
      .setTools(TinkerTags.Items.FISHING_RODS)
      .addInput(Blocks.HOPPER)
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(1).checkTraitLevel()
      .saveSalvage(consumer, prefix(ModifierIds.collecting, upgradeSalvage))
      .save(consumer, prefix(ModifierIds.collecting, upgradeFolder));

    // throwing
    Ingredient bowLimb = MaterialIngredient.of(TinkerToolParts.bowLimb.get());
    ModifierRecipeBuilder.modifier(ModifierIds.throwing)
      .setTools(IntersectionIngredient.of(
        Ingredient.of(TinkerTags.Items.DURABILITY),
        Ingredient.of(TinkerTags.Items.INTERACTABLE_CHARGE),
        ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.HARVEST)
      ))
      .addInput(bowLimb)
      .addInput(TinkerMaterials.cinderslime.getIngotTag())
      .addInput(MaterialIngredient.of(TinkerToolParts.bowGrip.get()))
      .setSlots(SlotType.ABILITY, 1)
      .setMaxLevel(1).checkTraitLevel()
      .saveSalvage(consumer, prefix(ModifierIds.throwing, abilitySalvage))
      .save(consumer, prefix(ModifierIds.throwing, abilityFolder));
    MultilevelModifierRecipeBuilder.modifier(ModifierIds.returning)
      .setTools(ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.HARVEST))
      .addInput(Items.ENDER_PEARL)
      .addInput(Items.CLOCK)
      .addInput(Items.ENDER_PEARL)
      .addLevel(SlotType.ABILITY, 1, 1)
      .addLevelRange(SlotType.UPGRADE, 1, 2, 4)
      .checkTraitLevel()
      .saveSalvage(consumer, prefix(ModifierIds.returning, abilitySalvage))
      .save(consumer, prefix(ModifierIds.returning, abilityFolder));

    /*
     * armor
     */
    // protection
    // all held tools can receive defense slots, so give them something to use it for
    Ingredient protectableTools = ingredientFromTags(TinkerTags.Items.ARMOR, TinkerTags.Items.HELD);
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.meleeProtection)
                                    .setInput(TinkerModifiers.cobaltReinforcement, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(protectableTools)
                                    .saveSalvage(consumer, prefix(ModifierIds.meleeProtection, defenseSalvage))
                                    .save(consumer, prefix(ModifierIds.meleeProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.projectileProtection)
                                    .setInput(TinkerModifiers.ironReinforcement, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(protectableTools)
                                    .saveSalvage(consumer, prefix(ModifierIds.projectileProtection, defenseSalvage))
                                    .save(consumer, prefix(ModifierIds.projectileProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.blastProtection)
                                    .setInput(TinkerModifiers.obsidianReinforcement, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(protectableTools)
                                    .saveSalvage(consumer, prefix(ModifierIds.blastProtection, defenseSalvage))
                                    .save(consumer, prefix(ModifierIds.blastProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.magicProtection)
                                    .setInput(TinkerModifiers.goldReinforcement, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(protectableTools)
                                    .saveSalvage(consumer, prefix(ModifierIds.magicProtection, defenseSalvage))
                                    .save(consumer, prefix(ModifierIds.magicProtection, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.fireProtection)
                                    .setInput(TinkerModifiers.searedReinforcement, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(protectableTools)
                                    .saveSalvage(consumer, prefix(ModifierIds.fireProtection, defenseSalvage))
                                    .save(consumer, prefix(ModifierIds.fireProtection, defenseFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.protection)
                         .addInput(TinkerModifiers.goldReinforcement)
                         .addInput(TinkerModifiers.searedReinforcement)
                         .addInput(TinkerModifiers.obsidianReinforcement)
                         .addInput(TinkerModifiers.ironReinforcement)
                         .addInput(TinkerModifiers.cobaltReinforcement)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.ARMOR)
                         .setMaxLevel(1)
                         .saveSalvage(consumer, prefix(ModifierIds.protection, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.protection, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.boundless)
                         .addInput(TinkerCommons.obsidianPane, 4)
                         .addInput(Items.WRITABLE_BOOK)
                         .addInput(TinkerCommons.obsidianPane, 4)
                         .addInput(TinkerWorld.ichorGeode, 2)
                         .addInput(TinkerWorld.ichorGeode, 2)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.SHIELDS)
                         .setMaxLevel(2)
                         .saveSalvage(consumer, prefix(ModifierIds.boundless, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.boundless, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.knockbackResistance)
                         .setTools(TinkerTags.Items.ARMOR)
                         .addInput(SizedIngredient.fromItems(Blocks.ANVIL, Blocks.CHIPPED_ANVIL, Blocks.DAMAGED_ANVIL))
                         .setSlots(SlotType.DEFENSE, 1)
                         .setMaxLevel(1)
                         .saveSalvage(consumer, prefix(ModifierIds.knockbackResistance, defenseSalvage))
                         .save(consumer, prefix(ModifierIds.knockbackResistance, defenseFolder));
    //noinspection removal
    ModifierRecipeBuilder.modifier(TinkerModifiers.golden)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .setSlots(SlotType.DEFENSE, 1)
                         .setMaxLevel(1).checkTraitLevel()
                         .setTools(TinkerTags.Items.WORN_ARMOR) // allow salvage on all worn armor
                         .saveSalvage(consumer, prefix(TinkerModifiers.golden, defenseSalvage))
                         .setTools(TinkerTags.Items.GOLDEN_ARMOR)
                         .save(withCondition(consumer, new TagFilledCondition<>(TinkerTags.Items.GOLDEN_ARMOR)), prefix(TinkerModifiers.golden, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.turtleShell)
                                    .setInput(Items.SCUTE, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .saveSalvage(consumer, prefix(ModifierIds.turtleShell, defenseSalvage))
                                    .save(consumer, prefix(ModifierIds.turtleShell, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.shulking)
                                    .setInput(Items.SHULKER_SHELL, 1, 5)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .saveSalvage(consumer, prefix(ModifierIds.shulking, defenseSalvage))
                                    .save(consumer, prefix(ModifierIds.shulking, defenseFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.dragonborn)
                                    .setInput(TinkerModifiers.dragonScale, 1, 10)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .setTools(TinkerTags.Items.ARMOR)
                                    .saveSalvage(consumer, prefix(ModifierIds.dragonborn, defenseSalvage))
                                    .save(consumer, prefix(ModifierIds.dragonborn, defenseFolder));
    // 3 each for chest and legs, 2 each for boots and helmet, leads to 10 total
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.revitalizing)
                                    .setTools(ingredientFromTags(TinkerTags.Items.WORN_ARMOR)) // revitalizing would suck on an item you constantly change
                                    .setInput(TinkerCommons.jeweledApple, 1, 2)
                                    .setSlots(SlotType.DEFENSE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.revitalizing, defenseSalvage))
                                    .save(consumer, prefix(ModifierIds.revitalizing, defenseFolder));

    // upgrade - counterattack
    Ingredient wornOrShield = ingredientFromTags(TinkerTags.Items.WORN_ARMOR, TinkerTags.Items.SHIELDS); // held armor may include things that cannot block
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.thorns)
                                    .setTools(wornOrShield)
                                    .setInput(Blocks.CACTUS, 1, 25)
                                    .setMaxLevel(3)
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.thorns, upgradeSalvage))
                                    .save(consumer, prefix(ModifierIds.thorns, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.sticky)
                                    .setTools(ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.WORN_ARMOR, TinkerTags.Items.SHIELDS))
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .saveSalvage(consumer, prefix(ModifierIds.sticky, upgradeSalvage));
    ModifierRecipeBuilder.modifier(ModifierIds.springy)
                         .setTools(wornOrShield)
                         .addInput(Items.PISTON)
                         .addInput(TinkerWorld.slime.get(SlimeType.ICHOR))
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(3)
                         .saveSalvage(consumer, prefix(ModifierIds.springy, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.springy, upgradeFolder));
    // upgrade - helmet
    ModifierRecipeBuilder.modifier(ModifierIds.respiration)
                         .setTools(TinkerTags.Items.HELMETS)
                         .addInput(ItemTags.FISHES)
                         .addInput(Tags.Items.GLASS_COLORLESS)
                         .addInput(ItemTags.FISHES)
                         .addInput(Items.KELP)
                         .addInput(Items.KELP)
                         .setMaxLevel(3)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(consumer, prefix(ModifierIds.respiration, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.respiration, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.itemFrame)
                         .setTools(TinkerTags.Items.HELMETS)
                         .addInput(Ingredient.of(Arrays.stream(FrameType.values())
                                                               .filter(type -> type != FrameType.CLEAR)
                                                               .map(type -> new ItemStack(TinkerGadgets.itemFrame.get(type)))))
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.itemFrame, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.itemFrame, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.minimap)
      .setTools(TinkerTags.Items.HELMETS)
      .addInput(Items.COMPASS)
      .addInput(Tags.Items.SLIMEBALLS)
      .addInput(Items.PAPER)
      .setSlots(SlotType.UPGRADE, 1)
      .saveSalvage(consumer, prefix(ModifierIds.minimap, upgradeSalvage))
      .save(consumer, prefix(ModifierIds.minimap, upgradeFolder));
    // upgrade - leggings
    hasteRecipes(consumer, ModifierIds.speedy, Ingredient.of(TinkerTags.Items.LEGGINGS), 3, upgradeFolder, upgradeSalvage);
    // leaping lets you disable skyslime geodes in case you don't like fun
    // if you are disabling both, you have a ton of recipes to fix anyways
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.leaping)
      .setTools(TinkerTags.Items.LEGGINGS)
      .setInput(TinkerWorld.skyGeode.asItem(), 1, 36)
      .setMaxLevel(1)
      .setSlots(SlotType.UPGRADE, 1)
      .save(consumer, prefix(ModifierIds.leaping, upgradeFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.leaping)
      .setTools(TinkerTags.Items.LEGGINGS)
      .setInput(TinkerWorld.skyGeode.getBlock(), 1, 18)
      .exactLevel(2)
      .setSlots(SlotType.ABILITY, 1)
      .save(consumer, prefix(ModifierIds.leaping, abilityFolder));
    MultilevelModifierRecipeBuilder.modifier(ModifierIds.leaping)
      .setTools(TinkerTags.Items.LEGGINGS)
      .addLevelRange(SlotType.UPGRADE, 1, 1, 1)
      .addLevelRange(SlotType.ABILITY, 1, 2, 2)
      .saveSalvage(consumer, prefix(ModifierIds.leaping, salvageFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.stepUp)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .addInput(Items.LEATHER)
                         .addInput(Items.GOLDEN_CARROT)
                         .addInput(Items.LEATHER)
                         .addInput(Items.SCAFFOLDING)
                         .addInput(Items.SCAFFOLDING)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(2)
                         .saveSalvage(consumer, prefix(ModifierIds.stepUp, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.stepUp, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.swiftSneak)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .addInput(Blocks.SCULK_SENSOR)
                         .setMaxLevel(5)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(consumer, prefix(ModifierIds.swiftSneak, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.swiftSneak, upgradeFolder));

    // upgrade - boots
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.featherFalling)
                                    .setTools(TinkerTags.Items.BOOTS)
                                    .setInput(Items.FEATHER, 1, 25) // 1% per feather
                                    .setSlots(SlotType.UPGRADE, 1)
                                    .setMaxLevel(2)
                                    .saveSalvage(consumer, prefix(ModifierIds.featherFalling, upgradeSalvage))
                                    .save(consumer, prefix(ModifierIds.featherFalling, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.longFall)
      .setTools(TinkerTags.Items.BOOTS)
      .addInput(Items.PISTON)
      .addInput(Items.PHANTOM_MEMBRANE)
      .addInput(Items.PISTON)
      .addInput(TinkerWorld.slime.get(SlimeType.ICHOR))
      .addInput(TinkerWorld.slime.get(SlimeType.ICHOR))
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(1).checkTraitLevel()
      .saveSalvage(consumer, prefix(ModifierIds.longFall, upgradeSalvage))
      .save(consumer, prefix(ModifierIds.longFall, upgradeFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.soulspeed)
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(Items.MAGMA_BLOCK)
                         .addInput(Items.CRYING_OBSIDIAN)
                         .addInput(Items.MAGMA_BLOCK)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(3)
                         .saveSalvage(consumer, prefix(TinkerModifiers.soulspeed, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.soulspeed, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.depthStrider)
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(ItemTags.FISHES)
                         .addInput(Blocks.PRISMARINE_BRICKS)
                         .addInput(ItemTags.FISHES)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(3)
                         .saveSalvage(consumer, prefix(ModifierIds.depthStrider, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.depthStrider, upgradeFolder));

    // upgrade - all
    ModifierRecipeBuilder.modifier(ModifierIds.ricochet)
                         .setTools(wornOrShield)
                         .addInput(Items.PISTON)
                         .addInput(TinkerWorld.slime.get(SlimeType.SKY))
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(2) // 2 per piece gives +160% total
                         .saveSalvage(consumer, prefix(ModifierIds.ricochet, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.ricochet, upgradeFolder));

    // armor ability
    // helmet
    ModifierRecipeBuilder.modifier(ModifierIds.zoom)
                         .setTools(ingredientFromTags(TinkerTags.Items.HELMETS, TinkerTags.Items.INTERACTABLE_CHARGE))
                         .addInput(Tags.Items.STRING)
                         .addInput(Items.SPYGLASS)
                         .addInput(Tags.Items.STRING)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(1).checkTraitLevel()
                         .saveSalvage(consumer, prefix(ModifierIds.zoom, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.zoom, upgradeFolder));
    Ingredient tanks = NoContainerIngredient.of(TinkerTags.Items.TANKS);
    ModifierRecipeBuilder.modifier(TinkerModifiers.slurping)
                         .addInput(Items.GLASS_BOTTLE)
                         .addInput(tanks)
                         .addInput(Items.GLASS_BOTTLE)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.HELMETS, TinkerTags.Items.INTERACTABLE_CHARGE))
                         .saveSalvage(consumer, prefix(TinkerModifiers.slurping, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.slurping, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.aquaAffinity)
                         .addInput(Blocks.PRISMARINE_BRICKS)
                         .addInput(Items.HEART_OF_THE_SEA)
                         .addInput(Blocks.PRISMARINE_BRICKS)
                         .addInput(Blocks.DARK_PRISMARINE)
                         .addInput(Blocks.DARK_PRISMARINE)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HELMETS)
                         .setMaxLevel(1).checkTraitLevel()
                         .saveSalvage(consumer, prefix(ModifierIds.aquaAffinity, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.aquaAffinity, abilityFolder));
    // chestplate
    ModifierRecipeBuilder.modifier(TinkerModifiers.ambidextrous)
                         .setTools(TinkerTags.Items.UNARMED)
                         .addInput(Items.LEATHER)
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .addInput(Items.LEATHER)
                         .addInput(Tags.Items.STRING)
                         .addInput(Tags.Items.STRING)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.ambidextrous, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.ambidextrous, abilityFolder));
    IncrementalModifierRecipeBuilder.modifier(ModifierIds.strength)
                                    .setTools(TinkerTags.Items.CHESTPLATES)
                                    .setInput(TinkerWorld.ichorGeode.asItem(), 1, 72)
                                    .setSlots(SlotType.ABILITY, 1)
                                    .setMaxLevel(2)
                                    .saveSalvage(consumer, prefix(ModifierIds.strength, abilitySalvage))
                                    .save(consumer, prefix(ModifierIds.strength, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.wings)
                         .setTools(TinkerTags.Items.CHESTPLATES)
                         .setMaxLevel(1).checkTraitLevel()
                         .addInput(Items.ELYTRA)
                         .setSlots(SlotType.ABILITY, 2)
                         .saveSalvage(consumer, prefix(ModifierIds.wings, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.wings, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.sleeves)
      .setTools(TinkerTags.Items.CHESTPLATES)
      .addInput(TinkerModifiers.silkyCloth)
      .addInput(TinkerMaterials.cinderslime.getIngotTag())
      .addInput(TinkerModifiers.silkyCloth)
      .setSlots(SlotType.UPGRADE, 1)
      .setMaxLevel(3)
      .saveSalvage(consumer, prefix(TinkerModifiers.sleeves, upgradeSalvage))
      .save(consumer, prefix(TinkerModifiers.sleeves, upgradeFolder));

    // leggings
    ModifierRecipeBuilder.modifier(ModifierIds.pockets)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .addInput(Items.SHULKER_SHELL)
                         .addInput(Tags.Items.INGOTS_IRON)
                         .addInput(Items.SHULKER_SHELL)
                         .addInput(Items.LEATHER)
                         .addInput(Items.LEATHER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(2)
                         .saveSalvage(consumer, prefix(ModifierIds.pockets, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.pockets, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.shieldStrap)
                         .addInput(TinkerWorld.skySlimeVine)
                         .addInput(TinkerMaterials.slimesteel.getIngotTag())
                         .addInput(TinkerWorld.skySlimeVine)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .saveSalvage(consumer, prefix(TinkerModifiers.shieldStrap, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.shieldStrap, upgradeFolder));
    BiConsumer<Integer,TagKey<Item>> toolBeltRecipe = (level, ingot) -> {
      ModifierRecipeBuilder builder = ModifierRecipeBuilder
        .modifier(ModifierIds.toolBelt)
        .addInput(Items.LEATHER)
        .addInput(ingot)
        .addInput(Items.LEATHER)
        .setTools(TinkerTags.Items.LEGGINGS)
        .exactLevel(level)
        .useSalvageMax();
      if (level == 1) {
        builder.setSlots(SlotType.ABILITY, 1);
        builder.saveSalvage(consumer, prefix(ModifierIds.toolBelt, abilitySalvage));
      } else {
        builder.disallowCrystal(); // prevent cheesing cost by extracting level 1
      }
      builder.save(consumer, wrap(ModifierIds.toolBelt, abilityFolder, "_" + level));
    };
    toolBeltRecipe.accept(1, Tags.Items.INGOTS_IRON);
    toolBeltRecipe.accept(2, Tags.Items.INGOTS_GOLD);
    toolBeltRecipe.accept(3, TinkerMaterials.roseGold.getIngotTag());
    toolBeltRecipe.accept(4, TinkerMaterials.cobalt.getIngotTag());
    toolBeltRecipe.accept(5, TinkerMaterials.hepatizon.getIngotTag());
    toolBeltRecipe.accept(6, TinkerMaterials.manyullyn.getIngotTag());
    ModifierRecipeBuilder.modifier(ModifierIds.soulBelt)
                         .addInput(Items.LEATHER)
                         .addInput(Ingredient.of(Items.RECOVERY_COMPASS))
                         .addInput(Items.LEATHER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(1)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .saveSalvage(consumer, prefix(ModifierIds.soulBelt, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.soulBelt, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.workbench)
                         .addInput(Items.LEATHER)
                         .addInput(Blocks.CRAFTING_TABLE)
                         .addInput(Items.LEATHER)
                         .setMaxLevel(1).checkTraitLevel()
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .save(consumer, prefix(ModifierIds.workbench, upgradeFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.craftingTable)
                         .addInput(Items.LEATHER)
                         .addInput(TinkerTables.craftingStation)
                         .addInput(Items.LEATHER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(1).checkTraitLevel()
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .saveSalvage(consumer, prefix(ModifierIds.craftingTable, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.craftingTable, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.wetting)
                         .addInput(Tags.Items.DUSTS_REDSTONE)
                         .addInput(tanks)
                         .addInput(Tags.Items.DUSTS_REDSTONE)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.LEGGINGS, TinkerTags.Items.SHIELDS))
                         .saveSalvage(consumer, prefix(TinkerModifiers.wetting, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.wetting, abilityFolder));
    // boots
    ModifierRecipeBuilder.modifier(ModifierIds.doubleJump)
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(Items.PISTON)
                         .addInput(TinkerWorld.slime.get(SlimeType.SKY))
                         .addInput(Items.PISTON)
                         .addInput(Items.PHANTOM_MEMBRANE)
                         .addInput(Items.PHANTOM_MEMBRANE)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(2)
                         .saveSalvage(consumer, prefix(ModifierIds.doubleJump, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.doubleJump, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.bouncy)
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY),   4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ICHOR), 4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY),   4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH), 4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH), 4)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(1).checkTraitLevel()
                         .saveSalvage(consumer, prefix(ModifierIds.bouncy, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.bouncy, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.frostWalker)
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(Items.BLUE_ICE)
                         .addInput(TinkerWorld.heads.get(TinkerHeadType.STRAY))
                         .addInput(Items.BLUE_ICE)
                         .addInput(Items.BLUE_ICE)
                         .addInput(Items.BLUE_ICE)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(1)
                         .saveSalvage(consumer, prefix(ModifierIds.frostWalker, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.frostWalker, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.snowdrift)
                         .setTools(TinkerTags.Items.BOOTS)
                         .addInput(Items.SNOW_BLOCK)
                         .addInput(Items.CARVED_PUMPKIN)
                         .addInput(Items.SNOW_BLOCK)
                         .addInput(Items.SNOW_BLOCK)
                         .addInput(Items.SNOW_BLOCK)
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(1)
                         .saveSalvage(consumer, prefix(ModifierIds.snowdrift, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.snowdrift, abilityFolder));

    // transform ingredients
    Ingredient bootsWithDuraibility = IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.BOOTS), Ingredient.of(TinkerTags.Items.DURABILITY));
    ModifierRecipeBuilder.modifier(TinkerModifiers.flamewake)
                         .setTools(bootsWithDuraibility)
                         .addInput(Items.FLINT)
                         .addInput(TinkerTags.Items.INGOTS_NETHERITE_SCRAP)
                         .addInput(Items.FLINT)
                         .addInput(Items.FLINT)
                         .addInput(Items.FLINT)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.flamewake, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.flamewake, abilityFolder));

    /*
     * ability
     */
    ModifierRecipeBuilder.modifier(ModifierIds.gilded)
                         .setTools(TinkerTags.Items.BONUS_SLOTS)
                         .addInput(Blocks.GILDED_BLACKSTONE)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(ModifierIds.gilded, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.gilded, abilityFolder));
    // luck is 3 recipes
    // level 1 always requires a slot
    Ingredient luckSupporting = ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.HARVEST, TinkerTags.Items.LAUNCHERS);
    ModifierRecipeBuilder.modifier(ModifierIds.luck)
                         .setTools(luckSupporting)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(SizedIngredient.fromItems(Items.CORNFLOWER, Items.BLUE_ORCHID))
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.STORAGE_BLOCKS_LAPIS)
                         .addInput(Tags.Items.STORAGE_BLOCKS_LAPIS)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .allowCrystal()
                         .save(consumer, wrap(ModifierIds.luck, abilityFolder, "_level_1"));
    ModifierRecipeBuilder.modifier(ModifierIds.luck)
                         .setTools(luckSupporting)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Items.GOLDEN_CARROT)
                         .addInput(Tags.Items.INGOTS_GOLD)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .disallowCrystal() // would allow a cost cheese
                         .exactLevel(2)
                         .save(consumer, wrap(ModifierIds.luck, abilityFolder, "_level_2"));
    ModifierRecipeBuilder.modifier(ModifierIds.luck)
                         .setTools(luckSupporting)
                         .addInput(TinkerMaterials.roseGold.getIngotTag())
                         .addInput(Items.RABBIT_FOOT)
                         .addInput(TinkerMaterials.roseGold.getIngotTag())
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .addInput(Items.NAME_TAG)
                         .disallowCrystal() // would allow a cost cheese
                         .exactLevel(3)
                         .save(consumer, wrap(ModifierIds.luck, abilityFolder, "_level_3"));
    // pants have just one level
    ModifierRecipeBuilder.modifier(ModifierIds.luck)
                         .setTools(TinkerTags.Items.LEGGINGS)
                         .addInput(SizedIngredient.fromItems(Items.CORNFLOWER, Items.BLUE_ORCHID))
                         .addInput(Items.RABBIT_FOOT)
                         .addInput(Items.GOLDEN_CARROT)
                         .addInput(Tags.Items.GEMS_DIAMOND)
                         .addInput(Items.NAME_TAG)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .disallowCrystal() // prevents cheesing cost using luck 1
                         .saveSalvage(consumer, wrap(ModifierIds.luck, abilitySalvage, "_pants"))
                         .save(consumer, wrap(ModifierIds.luck, abilityFolder, "_pants"));
    ModifierRecipeBuilder.modifier(ModifierIds.luck)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST, TinkerTags.Items.LAUNCHERS))
                         .exactLevel(1)
                         .useSalvageMax()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(ModifierIds.luck, abilitySalvage));

    // silky: all the cloth
    ModifierRecipeBuilder.modifier(TinkerModifiers.silky)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .addInput(TinkerModifiers.silkyCloth)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HARVEST)
                         .saveSalvage(consumer, prefix(TinkerModifiers.silky, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.silky, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.exchanging)
                         .addInput(Items.STICKY_PISTON)
                         .addInput(TinkerMaterials.hepatizon.getIngotTag())
                         .addInput(Items.STICKY_PISTON)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.HARVEST)
                         .saveSalvage(consumer, prefix(TinkerModifiers.exchanging, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.exchanging, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.autosmelt)
                         .addInput(Tags.Items.RAW_MATERIALS)
                         .addInput(Blocks.BLAST_FURNACE)
                         .addInput(Tags.Items.INGOTS)
                         .addInput(Tags.Items.STORAGE_BLOCKS_COAL)
                         .addInput(Tags.Items.STORAGE_BLOCKS_COAL)
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.HARVEST, TinkerTags.Items.FISHING_RODS))
                         .saveSalvage(consumer, prefix(TinkerModifiers.autosmelt, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.autosmelt, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.channeling)
      .addInput(Blocks.LIGHTNING_ROD)
      .addInput(Blocks.CREEPER_HEAD)
      .addInput(Blocks.LIGHTNING_ROD)
      .addInput(Blocks.LIGHTNING_ROD)
      .addInput(Blocks.LIGHTNING_ROD)
      .setMaxLevel(1).checkTraitLevel()
      .setSlots(SlotType.ABILITY, 1)
      .setTools(ingredientFromTags(TinkerTags.Items.MELEE_WEAPON, TinkerTags.Items.FISHING_RODS))
      .saveSalvage(consumer, prefix(ModifierIds.channeling, abilitySalvage))
      .save(consumer, prefix(ModifierIds.channeling, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.fins)
      .addInput(ItemTags.FISHES)
      .addInput(Blocks.PRISMARINE_BRICKS)
      .addInput(ItemTags.FISHES)
      .setMaxLevel(1).checkTraitLevel()
      .setSlots(SlotType.UPGRADE, 1)
      .setTools(TinkerTags.Items.MELEE_WEAPON)
      .saveSalvage(consumer, prefix(ModifierIds.fins, upgradeSalvage))
      .save(consumer, prefix(ModifierIds.fins, upgradeFolder));

    // fluid stuff
    ModifierRecipeBuilder.modifier(TinkerModifiers.melting)
                         .addInput(Items.BLAZE_ROD)
                         .addInput(Ingredient.of(TinkerSmeltery.searedMelter, TinkerSmeltery.smelteryController, TinkerSmeltery.foundryController))
                         .addInput(Items.BLAZE_ROD)
                         .addInput(Items.LAVA_BUCKET)
                         .addInput(Items.LAVA_BUCKET)
                         .setMaxLevel(1)
                         .checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.MELEE, TinkerTags.Items.HARVEST))
                         .saveSalvage(consumer, prefix(TinkerModifiers.melting, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.melting, abilityFolder));
    SizedIngredient faucets = SizedIngredient.fromItems(TinkerSmeltery.searedFaucet, TinkerSmeltery.scorchedFaucet); // no salvage as don't want conversion between seared and scorched
    ModifierRecipeBuilder.modifier(TinkerModifiers.bucketing)
                         .addInput(faucets)
                         .addInput(Items.BUCKET)
                         .addInput(faucets)
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .setMaxLevel(1)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.INTERACTABLE)
                         .saveSalvage(consumer, prefix(TinkerModifiers.bucketing, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.bucketing, abilityFolder));
    SizedIngredient channels = SizedIngredient.fromItems(TinkerSmeltery.searedChannel, TinkerSmeltery.scorchedChannel);
    ModifierRecipeBuilder.modifier(ModifierIds.spilling)
                         .addInput(channels)
                         .addInput(tanks)
                         .addInput(channels)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.MELEE)
                         .saveSalvage(consumer, prefix(ModifierIds.spilling, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.spilling, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.splashing)
                         .addInput(MantleTags.Items.SPLASH_BOTTLE)
                         .addInput(tanks)
                         .addInput(MantleTags.Items.SPLASH_BOTTLE)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.DURABILITY), Ingredient.of(TinkerTags.Items.INTERACTABLE)))
                         .saveSalvage(consumer, prefix(TinkerModifiers.splashing, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.splashing, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.bursting)
                         .addInput(Blocks.CACTUS)
                         .addInput(tanks)
                         .addInput(Blocks.CACTUS)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .addInput(Tags.Items.INGOTS_COPPER)
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.CHESTPLATES, TinkerTags.Items.SHIELDS))
                         .saveSalvage(consumer, prefix(TinkerModifiers.bursting, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.bursting, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.spitting)
                         .addInput(bowLimb)
                         .addInput(TinkerSmeltery.searedFluidCannon)
                         .addInput(bowLimb)
                         .setSlots(SlotType.ABILITY, 1)
                         // swasher gets spitting to get multishot, rest get to spit with their non-spit. No spitting with arrows
                         .setTools(IntersectionIngredient.of(
                           Ingredient.of(TinkerTags.Items.DURABILITY),
                           Ingredient.of(TinkerTags.Items.INTERACTABLE_CHARGE_MODIFIER)
                         ))
                         .saveSalvage(consumer, prefix(TinkerModifiers.spitting, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.spitting, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.tank)
                         .addInput(tanks)
                         .setSlots(SlotType.UPGRADE, 1)
                         .setTools(ingredientFromTags(TinkerTags.Items.HELD, TinkerTags.Items.ARMOR))
                         .saveSalvage(consumer, prefix(ModifierIds.tank, upgradeSalvage))
                         .save(consumer, prefix(ModifierIds.tank, upgradeFolder));
    // expanders
    ModifierRecipeBuilder.modifier(TinkerModifiers.expanded)
                         .addInput(Items.PISTON)
                         .addInput(TinkerMaterials.amethystBronze.getIngotTag())
                         .addInput(Items.PISTON)
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .addInput(SlimeType.ICHOR.getSlimeballTag())
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(TinkerTags.Items.AOE)
                         .saveSalvage(consumer, prefix(TinkerModifiers.expanded, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.expanded, abilityFolder));
    // reach expander
    ModifierRecipeBuilder.modifier(ModifierIds.reach)
                         .setTools(TinkerTags.Items.CHESTPLATES)
                         .addInput(Items.PISTON)
                         .addInput(TinkerMaterials.queensSlime.getIngotTag())
                         .addInput(Items.PISTON)
                         .addInput(SlimeType.ENDER.getSlimeballTag())
                         .addInput(SlimeType.ENDER.getSlimeballTag())
                         .setSlots(SlotType.ABILITY, 1)
                         .setMaxLevel(2)
                         .saveSalvage(consumer, prefix(ModifierIds.reach, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.reach, abilityFolder));
    // block transformers
    Ingredient interactableWithDurability = IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.DURABILITY), Ingredient.of(TinkerTags.Items.INTERACTABLE));
    Ingredient interactableBootsWithDurability = IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.DURABILITY), ingredientFromTags(TinkerTags.Items.INTERACTABLE, TinkerTags.Items.BOOTS));
    SizedIngredient roundPlate = SizedIngredient.of(MaterialIngredient.of(TinkerToolParts.adzeHead.get()));
    SizedIngredient smallBlade = SizedIngredient.of(MaterialIngredient.of(TinkerToolParts.smallBlade.get()));
    SizedIngredient toolBinding = SizedIngredient.of(MaterialIngredient.of(TinkerToolParts.toolBinding.get()));
    ModifierRecipeBuilder.modifier(ModifierIds.pathing)
                         .setTools(interactableBootsWithDurability)
                         .addInput(roundPlate)
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(toolBinding)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(ModifierIds.pathing, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.pathing, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.stripping)
                         .setTools(interactableWithDurability)
                         .addInput(SizedIngredient.of(MaterialIngredient.of(TinkerToolParts.smallAxeHead.get())))
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(toolBinding)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(ModifierIds.stripping, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.stripping, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.tilling)
                         .setTools(interactableBootsWithDurability)
                         .addInput(smallBlade)
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(toolBinding)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(ModifierIds.tilling, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.tilling, abilityFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.brushing)
      .setTools(IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.DURABILITY), Ingredient.of(TinkerTags.Items.INTERACTABLE_RIGHT)))
      .addInput(Tags.Items.FEATHERS)
      .addInput(Tags.Items.INGOTS_COPPER)
      .setMaxLevel(1).checkTraitLevel()
      .setSlots(SlotType.ABILITY, 1)
      .saveSalvage(consumer, prefix(ModifierIds.brushing, abilitySalvage))
      .save(consumer, prefix(ModifierIds.brushing, abilityFolder));

    // glowing
    ModifierRecipeBuilder.modifier(ModifierIds.glowing)
                         .setTools(interactableBootsWithDurability)
                         .addInput(Items.GLOWSTONE)
                         .addInput(Items.DAYLIGHT_DETECTOR)
                         .addInput(Items.SHROOMLIGHT)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(ModifierIds.glowing, abilitySalvage))
                         .save(consumer, prefix(ModifierIds.glowing, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.firestarter)
                         .setTools(interactableWithDurability)
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(Items.FLINT)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.firestarter, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.firestarter, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.fireprimer)
                         .setTools(Ingredient.of(TinkerTools.flintAndBrick))
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(Items.FLINT)
                         .setMaxLevel(1)
                         .setSlots(SlotType.UPGRADE, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.fireprimer, upgradeSalvage))
                         .save(consumer, prefix(TinkerModifiers.fireprimer, upgradeFolder));
    // slings
    Ingredient blockWhileCharging = IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.DURABILITY), Ingredient.of(TinkerTags.Items.INTERACTABLE_CHARGE));
    ModifierRecipeBuilder.modifier(TinkerModifiers.flinging)
                         .setTools(blockWhileCharging)
                         .addInput(Tags.Items.STRING)
                         .addInput(TinkerWorld.earthGeode.asItem())
                         .addInput(Tags.Items.STRING)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH))
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH))
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.flinging, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.flinging, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.springing)
                         .setTools(blockWhileCharging)
                         .addInput(Tags.Items.FEATHERS)
                         .addInput(TinkerWorld.skyGeode.asItem())
                         .addInput(Tags.Items.FEATHERS)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY))
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY))
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.springing, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.springing, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.bonking)
                         .setTools(blockWhileCharging)
                         .addInput(Tags.Items.INGOTS_IRON)
                         .addInput(TinkerWorld.ichorGeode.asItem())
                         .addInput(Tags.Items.INGOTS_IRON)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ICHOR))
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ICHOR))
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.bonking, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.bonking, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.warping)
                         .setTools(blockWhileCharging)
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .addInput(TinkerWorld.enderGeode.asItem())
                         .addInput(Tags.Items.ENDER_PEARLS)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ENDER))
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ENDER))
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.warping, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.warping, abilityFolder));

    // unbreakable
    ModifierRecipeBuilder.modifier(TinkerModifiers.unbreakable)
                         .setTools(TinkerTags.Items.DURABILITY)
                         .addInput(Items.SHULKER_SHELL)
                         .addInput(Items.DRAGON_BREATH)
                         .addInput(Items.SHULKER_SHELL)
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .addInput(Tags.Items.INGOTS_NETHERITE)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.unbreakable, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.unbreakable, abilityFolder));
    // weapon
    ModifierRecipeBuilder.modifier(TinkerModifiers.dualWielding)
                         .addInput(TinkerMaterials.slimesteel.getIngotTag())
                         .addInput(Items.NAUTILUS_SHELL)
                         .addInput(TinkerMaterials.slimesteel.getIngotTag())
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .setTools(DifferenceIngredient.of(IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.MELEE_WEAPON), Ingredient.of(TinkerTags.Items.INTERACTABLE_RIGHT)), Ingredient.of(TinkerTools.dagger)))
                         .saveSalvage(consumer, prefix(TinkerModifiers.dualWielding, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.dualWielding, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.blocking)
                         .setTools(DifferenceIngredient.of(
                           IntersectionIngredient.of(Ingredient.of(TinkerTags.Items.INTERACTABLE_CHARGE), Ingredient.of(TinkerTags.Items.DURABILITY)),
                           ingredientFromTags(TinkerTags.Items.PARRY, TinkerTags.Items.SHIELDS)))
                         .addInput(ItemTags.PLANKS)
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(ItemTags.PLANKS)
                         .addInput(ItemTags.PLANKS)
                         .addInput(ItemTags.PLANKS)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.blocking, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.blocking, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.parrying)
                         .setTools(TinkerTags.Items.PARRY)
                         .addInput(ItemTags.PLANKS)
                         .addInput(TinkerMaterials.steel.getIngotTag())
                         .addInput(ItemTags.PLANKS)
                         .setMaxLevel(1).checkTraitLevel()
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.parrying, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.parrying, abilityFolder));
    ModifierRecipeBuilder.modifier(TinkerModifiers.reflecting)
                         .setTools(TinkerTags.Items.SHIELDS)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY),   4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.ICHOR), 4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.SKY),   4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH), 4)
                         .addInput(TinkerWorld.congealedSlime.get(SlimeType.EARTH), 4)
                         .setSlots(SlotType.ABILITY, 1)
                         .saveSalvage(consumer, prefix(TinkerModifiers.reflecting, abilitySalvage))
                         .save(consumer, prefix(TinkerModifiers.reflecting, abilityFolder));

    /*
     * extra modifiers
     */
    ModifierRecipeBuilder.modifier(ModifierIds.writable)
                         .setTools(TinkerTags.Items.BONUS_SLOTS)
                         .addInput(Items.WRITABLE_BOOK)
                         .setMaxLevel(1)
                         .save(consumer, prefix(ModifierIds.writable, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.harmonious)
                         .setTools(TinkerTags.Items.BONUS_SLOTS)
                         .addInput(ItemTags.MUSIC_DISCS)
                         .setMaxLevel(1)
                         .save(consumer, prefix(ModifierIds.harmonious, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.recapitated)
                         .setTools(TinkerTags.Items.BONUS_SLOTS)
                         .addInput(SizedIngredient.of(DifferenceIngredient.of(Ingredient.of(Tags.Items.HEADS), Ingredient.of(Items.DRAGON_HEAD))))
                         .setMaxLevel(1)
                         .save(consumer, prefix(ModifierIds.recapitated, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.forecast)
                         .setTools(TinkerTags.Items.BONUS_SLOTS)
                         .addInput(ingredientFromTags(Tags.Items.ORES_DIAMOND, Tags.Items.ORES_EMERALD, TinkerTags.Items.ORES_COBALT))
                         .setMaxLevel(1)
                         .save(consumer, prefix(ModifierIds.forecast, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.embossed)
      .setTools(TinkerTags.Items.BONUS_SLOTS)
      .addInput(TinkerTags.Items.BOSS_TROPHIES)
      .setMaxLevel(1)
      .save(withCondition(consumer, new TagFilledCondition<>(TinkerTags.Items.BOSS_TROPHIES)), prefix(ModifierIds.embossed, slotlessFolder));
    ModifierRecipeBuilder.modifier(ModifierIds.draconic)
                         .setTools(TinkerTags.Items.BONUS_SLOTS)
                         .addInput(Items.DRAGON_HEAD)
                         .setMaxLevel(1)
                         .save(consumer, wrap(ModifierIds.draconic, slotlessFolder, "_from_head"));
    ModifierRecipeBuilder.modifier(ModifierIds.draconic)
                         .setTools(TinkerTags.Items.BONUS_SLOTS)
                         .addInput(Items.DRAGON_BREATH)
                         .addInput(TinkerModifiers.dragonScale)
                         .addInput(Items.DRAGON_BREATH)
                         .addInput(TinkerModifiers.dragonScale)
                         .addInput(TinkerModifiers.dragonScale)
                         .setMaxLevel(1)
                         .disallowCrystal()
                         .save(consumer, wrap(ModifierIds.draconic, slotlessFolder, "_from_scales"));
    // rebalanced
    SwappableModifierRecipeBuilder.modifier(ModifierIds.rebalanced, SlotType.UPGRADE.getName())
      .setTools(TinkerTags.Items.BONUS_SLOTS)
      .addInput(TinkerMaterials.roseGold.getNuggetTag())
      .addInput(Items.END_CRYSTAL)
      .addInput(TinkerMaterials.roseGold.getNuggetTag())
      .addInput(TinkerWorld.skyGeode.getBlock())
      .addInput(TinkerWorld.skyGeode.getBlock())
      .disallowCrystal()
      .save(consumer, wrap(ModifierIds.rebalanced, slotlessFolder, "_" + SlotType.UPGRADE.getName()));
    SwappableModifierRecipeBuilder.modifier(ModifierIds.rebalanced, SlotType.DEFENSE.getName())
      .setTools(IntersectionIngredient.of(ingredientFromTags(TinkerTags.Items.ARMOR, TinkerTags.Items.HELD), Ingredient.of(TinkerTags.Items.BONUS_SLOTS)))
      .addInput(TinkerMaterials.cobalt.getNuggetTag())
      .addInput(Items.END_CRYSTAL)
      .addInput(TinkerMaterials.cobalt.getNuggetTag())
      .addInput(TinkerWorld.earthGeode.getBlock())
      .addInput(TinkerWorld.earthGeode.getBlock())
      .disallowCrystal()
      .save(consumer, wrap(ModifierIds.rebalanced, slotlessFolder, "_" + SlotType.DEFENSE.getName()));
    SwappableModifierRecipeBuilder.modifier(ModifierIds.rebalanced, SlotType.ABILITY.getName())
      .setTools(TinkerTags.Items.BONUS_SLOTS)
      .addInput(TinkerMaterials.queensSlime.getNuggetTag())
      .addInput(Items.END_CRYSTAL)
      .addInput(TinkerMaterials.queensSlime.getNuggetTag())
      .addInput(TinkerWorld.ichorGeode.getBlock())
      .addInput(TinkerWorld.ichorGeode.getBlock())
      .disallowCrystal()
      .save(consumer, wrap(ModifierIds.rebalanced, slotlessFolder, "_" + SlotType.ABILITY.getName()));
    SwappableModifierRecipeBuilder.modifier(ModifierIds.rebalanced, "traits")
      .setTools(ToolHookIngredient.of(TinkerTags.Items.BONUS_SLOTS, ToolHooks.REBALANCED_TRAIT))
      .addInput(TinkerMaterials.manyullyn.getNuggetTag())
      .addInput(Items.END_CRYSTAL)
      .addInput(TinkerMaterials.manyullyn.getNuggetTag())
      .addInput(TinkerWorld.enderGeode.getBlock())
      .addInput(TinkerWorld.enderGeode.getBlock())
      .disallowCrystal()
      .save(consumer, wrap(ModifierIds.rebalanced, slotlessFolder, "_traits"));
    ModifierRecipeBuilder.modifier(ModifierIds.redirected)
      .setTools(ToolHookIngredient.of(TinkerTags.Items.AMMO, ToolHooks.REBALANCED_TRAIT))
      .addInput(Items.DRAGON_BREATH)
      .save(consumer, prefix(ModifierIds.redirected, slotlessFolder));

    // tipping arrows and shurikens
    PotionCastingRecipeBuilder.tableTipping(ModifierIds.tipped)
      .setBottle(TinkerTags.Items.AMMO)
      .setCoolingTime(20)
      .setFluid(TinkerFluids.potion.ingredient(FluidValues.BOTTLE / 5))
      .save(consumer, location(slotlessFolder + "ammo_tipping"));
    PotionCastingRecipeBuilder.tableClearing(ModifierIds.tipped)
      .setBottle(TinkerTags.Items.AMMO)
      .setCoolingTime(20)
      .setFluid(MantleTags.Fluids.WATER, FluidValues.BOTTLE / 5)
      .save(consumer, location(slotlessFolder + "ammo_tip_clearing"));
    PotionCastingRecipeBuilder.tableTipping(ModifierIds.tipped)
      .setBottle(TinkerTags.Items.FISHING_RODS)
      .setCoolingTime(20)
      .setFluid(TinkerFluids.potion.ingredient(FluidValues.BOTTLE))
      .save(consumer, location(slotlessFolder + "fishing_rod_tipping"));
    PotionCastingRecipeBuilder.tableClearing(ModifierIds.tipped)
      .setBottle(TinkerTags.Items.FISHING_RODS)
      .setCoolingTime(20)
      .setFluid(MantleTags.Fluids.WATER, FluidValues.BOTTLE)
      .save(consumer, location(slotlessFolder + "fishing_rod_tip_clearing"));

    // removal
    IJsonPredicate<ModifierId> removable = ModifierPredicate.tag(TinkerTags.Modifiers.REMOVE_MODIFIER_BLACKLIST).inverted();
    ModifierRemovalRecipeBuilder.removal()
      .addInput(Blocks.WET_SPONGE)
      .addLeftover(Blocks.SPONGE)
      .modifierPredicate(removable)
      .save(consumer, location(worktableFolder + "remove_modifier_sponge"));
    ModifierRemovalRecipeBuilder.removal()
      .addInput(CompoundIngredient.of(
        FluidContainerIngredient.fromFluid(TinkerFluids.venom),
        FluidContainerIngredient.fromIngredient(TinkerFluids.venom.ingredient(FluidValues.BOTTLE), Ingredient.of(TinkerFluids.venomBottle)))
      )
      .modifierPredicate(removable)
      .save(consumer, location(worktableFolder + "remove_modifier_venom"));
    // modifier extracting: sponge + crystal
    IJsonPredicate<ModifierId> extractBlacklist = ModifierPredicate.tag(TinkerTags.Modifiers.EXTRACT_MODIFIER_BLACKLIST).inverted();
    for (boolean dagger : new boolean[]{false, true}) {
      String suffix = dagger ? "_dagger" : "";
      SizedIngredient tools = dagger ? SizedIngredient.fromItems(2, TinkerTools.dagger) : SizedIngredient.of(DifferenceIngredient.of(Ingredient.of(TinkerTags.Items.MODIFIABLE), Ingredient.of(TinkerTags.Items.UNSALVAGABLE)));
      ModifierRemovalRecipeBuilder.extract()
                                  .setTools(tools)
                                  .setName("slotless")
                                  .addInput(Items.AMETHYST_SHARD)
                                  .addInput(Items.WET_SPONGE)
                                  .addLeftover(Items.SPONGE)
                                  .modifierPredicate(ModifierPredicate.and(extractBlacklist, new SlotTypeModifierPredicate(null), ModifierPredicate.tag(TinkerTags.Modifiers.EXTRACT_SLOTLESS_BLACKLIST).inverted()))
                                  .save(consumer, location(worktableFolder + "extract/slotless" + suffix));
      ModifierRemovalRecipeBuilder.extract()
                                  .setTools(tools)
                                  .slotName(SlotType.UPGRADE)
                                  .addInput(TinkerWorld.skyGeode)
                                  .addInput(Items.WET_SPONGE)
                                  .addLeftover(Items.SPONGE)
                                  .modifierPredicate(ModifierPredicate.and(extractBlacklist, new SlotTypeModifierPredicate(SlotType.UPGRADE)))
                                  .save(consumer, location(worktableFolder + "extract/upgrade" + suffix));
      ModifierRemovalRecipeBuilder.extract()
                                  .setTools(tools)
                                  .slotName(SlotType.DEFENSE)
                                  .addInput(TinkerWorld.earthGeode)
                                  .addInput(Items.WET_SPONGE)
                                  .addLeftover(Items.SPONGE)
                                  .modifierPredicate(ModifierPredicate.and(extractBlacklist, new SlotTypeModifierPredicate(SlotType.DEFENSE)))
                                  .save(consumer, location(worktableFolder + "extract/defense" + suffix));
      ModifierRemovalRecipeBuilder.extract()
                                  .setTools(tools)
                                  .slotName(SlotType.ABILITY)
                                  .addInput(TinkerWorld.ichorGeode)
                                  .addInput(Items.WET_SPONGE)
                                  .addLeftover(Items.SPONGE)
                                  .modifierPredicate(ModifierPredicate.and(extractBlacklist, new SlotTypeModifierPredicate(SlotType.ABILITY)))
                                  .save(consumer, location(worktableFolder + "extract/ability" + suffix));
      ModifierRemovalRecipeBuilder.extract()
                                  .setTools(tools)
                                  .addInput(TinkerWorld.enderGeode)
                                  .addInput(Items.WET_SPONGE)
                                  .addLeftover(Items.SPONGE)
                                  .modifierPredicate(extractBlacklist)
                                  .save(consumer, location(worktableFolder + "extract/modifier" + suffix));

    }
    ModifierSortingRecipeBuilder.sorting()
                                .addInput(Items.COMPASS)
                                .save(consumer, location(worktableFolder + "modifier_sorting"));

    // invisible ink
    ResourceLocation hiddenModifiers = TConstruct.getResource("invisible_modifiers");
    IJsonPredicate<ModifierId> blacklist = ModifierPredicate.tag(TinkerTags.Modifiers.INVISIBLE_INK_BLACKLIST).inverted();
    ModifierSetWorktableRecipeBuilder.setAdding(hiddenModifiers)
                                     .modifierPredicate(blacklist)
                                     .addInput(FluidContainerIngredient.fromIngredient(TinkerFluids.skySlime.ingredient(FluidValues.BOTTLE), Ingredient.of(TinkerFluids.slimeBottle.get(SlimeType.SKY))))
                                     .save(consumer, location(worktableFolder + "invisible_ink_adding"));
    ModifierSetWorktableRecipeBuilder.setRemoving(hiddenModifiers)
                                     .modifierPredicate(blacklist)
                                     .addInput(FluidContainerIngredient.fromIngredient(FluidIngredient.of(Fluids.MILK, FluidType.BUCKET_VOLUME), Ingredient.of(Items.MILK_BUCKET)))
                                     .save(consumer, location(worktableFolder + "invisible_ink_removing"));

    // swapping hands
    ToggleInteractionWorktableRecipeBuilder.builder()
      .tools(Ingredient.of(TinkerTags.Items.INTERACTABLE_DUAL))
      .addInput(Items.LEVER)
      .save(consumer, location(worktableFolder + "toggle_interaction_modifier"));

    // conversion
    for (boolean matchBook : new boolean[]{false, true}) {
      String suffix = matchBook ? "_book" : "_tool";
      EnchantmentConvertingRecipeBuilder.converting("slotless", matchBook)
                                        .addInput(Items.AMETHYST_SHARD)
                                        .modifierPredicate(ModifierPredicate.and(new SlotTypeModifierPredicate(null),
                                                                                  ModifierPredicate.tag(TinkerTags.Modifiers.EXTRACT_SLOTLESS_BLACKLIST).inverted()))
                                        .save(consumer, location(worktableFolder + "enchantment_converting/slotless" + suffix));
      EnchantmentConvertingRecipeBuilder.converting("upgrades", matchBook)
                                        .addInput(TinkerWorld.skyGeode.asItem())
                                        .addInput(Tags.Items.GEMS_LAPIS, 3)
                                        .modifierPredicate(ModifierPredicate.and(new SlotTypeModifierPredicate(SlotType.UPGRADE),
                                          ModifierPredicate.tag(TinkerTags.Modifiers.EXTRACT_UPGRADE_BLACKLIST).inverted()))
                                        .save(consumer, location(worktableFolder + "enchantment_converting/upgrade" + suffix));
      EnchantmentConvertingRecipeBuilder.converting("defense", matchBook)
                                        .addInput(TinkerWorld.earthGeode.asItem())
                                        .addInput(Tags.Items.INGOTS_GOLD, 1)
                                        .modifierPredicate(new SlotTypeModifierPredicate(SlotType.DEFENSE))
                                        .save(consumer, location(worktableFolder + "enchantment_converting/defense" + suffix));
      EnchantmentConvertingRecipeBuilder.converting("abilities", matchBook)
                                        .addInput(TinkerWorld.ichorGeode.asItem())
                                        .addInput(Tags.Items.GEMS_DIAMOND)
                                        .modifierPredicate(new SlotTypeModifierPredicate(SlotType.ABILITY))
                                        .save(consumer, location(worktableFolder + "enchantment_converting/ability" + suffix));
      EnchantmentConvertingRecipeBuilder.converting("modifiers", matchBook)
                                        .addInput(TinkerWorld.enderGeode)
                                        .addInput(Items.DRAGON_BREATH, 5)
                                        .returnInput()
                                        .save(consumer, location(worktableFolder + "enchantment_converting/unenchant" + suffix));
    }

    // compatability
    String theOneProbe = "theoneprobe";
    ResourceLocation probe = new ResourceLocation(theOneProbe, "probe");
    Consumer<FinishedRecipe> topConsumer = withCondition(consumer, modLoaded(theOneProbe));
    ModifierRecipeBuilder.modifier(ModifierIds.theOneProbe)
                         .setTools(ingredientFromTags(TinkerTags.Items.HELMETS, TinkerTags.Items.HELD))
                         .addInput(ItemNameIngredient.from(probe))
                         .setSlots(SlotType.UPGRADE, 1)
                         .setMaxLevel(1).checkTraitLevel()
                         .saveSalvage(topConsumer, prefix(ModifierIds.theOneProbe, compatSalvage))
                         .save(topConsumer, prefix(ModifierIds.theOneProbe, compatFolder));
    Consumer<FinishedRecipe> headlightConsumer = withCondition(consumer, modLoaded("headlight"));
    BiConsumer<Ingredient,String> headlight = (ingredient, light) -> {
      SwappableModifierRecipeBuilder builder = SwappableModifierRecipeBuilder.modifier(ModifierIds.headlight, light);
      builder.variantFormatter(VariantFormatter.PARAMETER)
             .setTools(TinkerTags.Items.HELMETS)
             .addInput(Items.LEATHER)
             .addInput(ingredient)
             .addInput(Items.LEATHER)
             .setSlots(SlotType.UPGRADE, 1)
             .disallowCrystal();
      if ("10".equals(light)) {
        builder.saveSalvage(headlightConsumer, prefix(ModifierIds.headlight, compatSalvage));
      } else {
        builder.disallowCrystal();
      }
      builder.save(headlightConsumer, wrap(ModifierIds.headlight, compatFolder, "_" + light));
    };
    headlight.accept(Ingredient.of(Blocks.LANTERN), "15");
    headlight.accept(Ingredient.of(Blocks.SOUL_LANTERN), "10");
    headlight.accept(Ingredient.of(ItemTags.CANDLES), "5");
  }

  private void addTextureRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "tools/modifiers/slotless/";

    // slime staff
    // nether
    woodTexture(consumer, MaterialIds.crimson, Blocks.CRIMSON_PLANKS, folder);
    woodTexture(consumer, MaterialIds.warped, Blocks.WARPED_PLANKS, folder);
    // slimewood
    woodTexture(consumer, MaterialIds.greenheart, TinkerWorld.greenheart, folder);
    woodTexture(consumer, MaterialIds.skyroot, TinkerWorld.skyroot, folder);
    woodTexture(consumer, MaterialIds.bloodshroom, TinkerWorld.bloodshroom, folder);
    woodTexture(consumer, MaterialIds.enderbark, TinkerWorld.enderbark, folder);
    // special
    woodTexture(consumer, MaterialIds.blazewood, TinkerMaterials.blazewood, folder);
    woodTexture(consumer, MaterialIds.nahuatl, TinkerMaterials.nahuatl, folder);
    woodTexture(consumer, MaterialIds.bamboo, Blocks.BAMBOO, folder);
    woodTexture(consumer, MaterialIds.cactus, Blocks.CACTUS, folder);
    // compat
    TagKey<Item> treatedWood = getItemTag(COMMON, "treated_wood");
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, MaterialIds.treatedWood.toString())
      .variantFormatter(VariantFormatter.MATERIAL)
      .setTools(TinkerTags.Items.EMBELLISHMENT_WOOD)
      .addInput(treatedWood).addInput(TinkerTables.pattern).addInput(treatedWood)
      .save(withCondition(consumer, new TagFilledCondition<>(treatedWood)), wrap(TinkerModifiers.embellishment, folder, "/wood/treated"));
    TagKey<Item> ironwood = getItemTag(COMMON, "ingots/ironwood");
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, MaterialIds.ironwood.toString())
      .variantFormatter(VariantFormatter.MATERIAL)
      .setTools(TinkerTags.Items.EMBELLISHMENT_WOOD)
      .addInput(ironwood).addInput(TinkerTables.pattern).addInput(ironwood)
      .save(withCondition(consumer, new TagFilledCondition<>(ironwood)), wrap(TinkerModifiers.embellishment, folder, "/wood/ironwood"));

    // cosmetics //
    consumer.accept(new SimpleFinishedRecipe(location(folder + "dyeing"), TinkerModifiers.armorDyeingSerializer.get()));
    consumer.accept(new SimpleFinishedRecipe(location(folder + "trim"), TinkerModifiers.armorTrimSerializer.get()));

    // slimesuit //
    // basic slime
    slimeTexture(consumer, MaterialIds.earthslime, SlimeType.EARTH, folder);
    slimeTexture(consumer, MaterialIds.skyslime,   SlimeType.SKY, folder);
    slimeTexture(consumer, MaterialIds.ichor,      SlimeType.ICHOR, folder);
    slimeTexture(consumer, MaterialIds.enderslime, SlimeType.ENDER, folder);
    // slimy planks
    slimyWoodTexture(consumer, MaterialIds.earthslime, TinkerWorld.greenheart,  FoliageType.EARTH, folder);
    slimyWoodTexture(consumer, MaterialIds.skyslime,   TinkerWorld.skyroot,     FoliageType.SKY,   folder);
    slimyWoodTexture(consumer, MaterialIds.blood,      TinkerWorld.bloodshroom, FoliageType.BLOOD, folder);
    slimyWoodTexture(consumer, MaterialIds.enderslime, TinkerWorld.enderbark,   FoliageType.ENDER, folder);
    // weird slime
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, MaterialIds.clay.toString())
                                  .variantFormatter(VariantFormatter.MATERIAL)
                                  .setTools(TinkerTags.Items.EMBELLISHMENT_SLIME)
                                  .addInput(Blocks.CLAY).addInput(Items.CLAY_BALL).addInput(Blocks.CLAY)
                                  .save(consumer, wrap(TinkerModifiers.embellishment, folder, "/slime/clay"));
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, MaterialIds.magma.toString())
                                  .variantFormatter(VariantFormatter.MATERIAL)
                                  .setTools(TinkerTags.Items.EMBELLISHMENT_SLIME)
                                  .addInput(Blocks.MAGMA_BLOCK).addInput(Items.MAGMA_CREAM).addInput(Blocks.MAGMA_BLOCK)
                                  .save(consumer, wrap(TinkerModifiers.embellishment, folder, "/slime/magma"));
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, MaterialIds.honey.toString())
                                  .variantFormatter(VariantFormatter.MATERIAL)
                                  .setTools(TinkerTags.Items.EMBELLISHMENT_SLIME)
                                  .addInput(Blocks.HONEY_BLOCK).addInput(Items.HONEY_BOTTLE).addInput(Blocks.HONEY_BLOCK)
                                  .save(consumer, wrap(TinkerModifiers.embellishment, folder, "/slime/honey"));
  }

  private void addHeadRecipes(Consumer<FinishedRecipe> consumer) {
    String folder = "tools/severing/";
    // first, beheading
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.ZOMBIE), Items.ZOMBIE_HEAD)
												 .save(consumer, location(folder + "zombie_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SKELETON), Items.SKELETON_SKULL)
												 .save(consumer, location(folder + "skeleton_skull"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.WITHER_SKELETON, EntityType.WITHER), Items.WITHER_SKELETON_SKULL)
												 .save(consumer, location(folder + "wither_skeleton_skull"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CREEPER), Items.CREEPER_HEAD)
												 .save(consumer, location(folder + "creeper_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.PIGLIN), Items.PIGLIN_HEAD)
                         .save(consumer, location(folder + "piglin_head"));
    consumer.accept(new SimpleFinishedRecipe(location(folder + "player_head"), TinkerModifiers.playerBeheadingSerializer.get()));
    consumer.accept(new SimpleFinishedRecipe(location(folder + "snow_golem_head"), TinkerModifiers.snowGolemBeheadingSerializer.get()));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.IRON_GOLEM), Blocks.CARVED_PUMPKIN)
                         .save(consumer, location(folder + "iron_golem_head"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.ENDER_DRAGON), Items.DRAGON_HEAD)
                         .save(consumer, location(folder + "ender_dragon_head"));
    TinkerWorld.headItems.forEach((type, head) ->
      SeveringRecipeBuilder.severing(EntityIngredient.of(type.getType()), head)
                           .save(consumer, location(folder + type.getSerializedName() + "_head")));

    // other body parts
    // hostile
    // beeyeing
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SPIDER, EntityType.CAVE_SPIDER), Items.SPIDER_EYE)
                         .save(consumer, location(folder + "spider_eye"));
    // besilking
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SPIDER, EntityType.CAVE_SPIDER), Items.COBWEB)
                         .save(consumer, location(folder + "cobweb"));
    // be-internal-combustion-device
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CREEPER), Blocks.TNT)
                         .save(consumer, location(folder + "creeper_tnt"));
    // bemembraning?
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.PHANTOM), Items.PHANTOM_MEMBRANE)
                         .save(consumer, location(folder + "phantom_membrane"));
    // beshelling
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SHULKER), Items.SHULKER_SHELL)
                         .save(consumer, location(folder + "shulker_shell"));
    // deboning
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SKELETON, EntityType.SKELETON_HORSE, EntityType.STRAY), ItemOutput.fromItem(Items.BONE, 2))
                         .save(consumer, location(folder + "skeleton_bone"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.WITHER_SKELETON), ItemOutput.fromItem(TinkerMaterials.necroticBone, 2))
                         .save(consumer, location(folder + "wither_skeleton_bone"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.BLAZE), ItemOutput.fromItem(Items.BLAZE_ROD, 2))
                         .save(consumer, location(folder + "blaze_rod"));
    // desliming (you cut off a chunk of slime)
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.SLIME), Items.SLIME_BALL)
                         .save(consumer, location(folder + "earthslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.skySlimeEntity.get()), TinkerCommons.slimeball.get(SlimeType.SKY))
                         .save(consumer, location(folder + "skyslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.enderSlimeEntity.get()), TinkerCommons.slimeball.get(SlimeType.ENDER))
                         .save(consumer, location(folder + "enderslime_ball"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(TinkerWorld.terracubeEntity.get()), Items.CLAY_BALL)
                         .save(consumer, location(folder + "terracube_clay"));
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.MAGMA_CUBE), Items.MAGMA_CREAM)
                         .save(consumer, location(folder + "magma_cream"));
    // descaling? I don't know what to call those
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.GUARDIAN, EntityType.ELDER_GUARDIAN), ItemOutput.fromItem(Items.PRISMARINE_SHARD, 2))
                         .save(consumer, location(folder + "guardian_shard"));

    // passive
    // befeating
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.RABBIT), Items.RABBIT_FOOT)
                         .noChildOutput()
												 .save(consumer, location(folder + "rabbit_foot"));
    // befeathering
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.CHICKEN), ItemOutput.fromItem(Items.FEATHER, 2))
                         .noChildOutput()
                         .save(consumer, location(folder + "chicken_feather"));
    // beshrooming
    consumer.accept(new SimpleFinishedRecipe(location(folder + "mooshroom_shroom"), TinkerModifiers.mooshroomDemushroomingSerializer.get()));
    // beshelling
    SeveringRecipeBuilder.severing(EntityIngredient.of(EntityType.TURTLE), Items.TURTLE_HELMET)
                         .setChildOutput(ItemOutput.fromItem(Items.SCUTE))
                         .save(consumer, location(folder + "turtle_shell"));
    // befleecing
    consumer.accept(new SimpleFinishedRecipe(location(folder + "sheep_wool"), TinkerModifiers.sheepShearing.get()));
  }

  /** Adds recipes for a plate armor texture with a custom tag */
  private void woodTexture(Consumer<FinishedRecipe> consumer, MaterialVariantId material, ItemLike planks, String folder) {
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, material.toString())
                                  .variantFormatter(VariantFormatter.MATERIAL)
                                  .setTools(TinkerTags.Items.EMBELLISHMENT_WOOD)
                                  .addInput(planks).addInput(TinkerTables.pattern).addInput(planks)
                                  .save(consumer, wrap(TinkerModifiers.embellishment, folder, "/wood/" + material.getLocation('_').getPath()));
  }

  /** Adds recipes for a slime armor texture */
  private void slimeTexture(Consumer<FinishedRecipe> consumer, MaterialId material, SlimeType slime, String folder) {
    ItemLike congealed = TinkerWorld.congealedSlime.get(slime);
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, material.toString())
                                  .variantFormatter(VariantFormatter.MATERIAL)
                                  .setTools(TinkerTags.Items.EMBELLISHMENT_SLIME)
                                  .addInput(congealed).addInput(TinkerCommons.slimeball.get(slime)).addInput(congealed)
                                  .save(consumer, wrap(TinkerModifiers.embellishment, folder, "/slime/" + slime.getSerializedName()));
  }

  /** Adds recipes for a slime armor texture */
  private void slimyWoodTexture(Consumer<FinishedRecipe> consumer, MaterialId material, WoodBlockObject wood, FoliageType foliage, String folder) {
    ItemLike planks = wood.get();
    SwappableModifierRecipeBuilder.modifier(TinkerModifiers.embellishment, material.toString())
                                  .variantFormatter(VariantFormatter.MATERIAL)
                                  .setTools(TinkerTags.Items.EMBELLISHMENT_SLIME)
                                  .addInput(planks).addInput(TinkerWorld.slimeSapling.get(foliage)).addInput(planks)
                                  .save(consumer, wrap(TinkerModifiers.embellishment, folder, "/slime/" + wood.getWoodType().name().split(":", 2)[1]));
  }

  /** Adds haste like recipes using redstone */
  public void hasteRecipes(Consumer<FinishedRecipe> consumer, ModifierId modifier, Ingredient tools, int maxLevel, @Nullable String recipeFolder, @Nullable String salvageFolder) {
    IncrementalModifierRecipeBuilder builder = IncrementalModifierRecipeBuilder
      .modifier(modifier)
      .setTools(tools)
      .setInput(Tags.Items.DUSTS_REDSTONE, 1, 45)
      .setMaxLevel(maxLevel)
      .setSlots(SlotType.UPGRADE, 1);
    if (salvageFolder != null) {
      builder.saveSalvage(consumer, prefix(modifier, salvageFolder));
    }
    if (recipeFolder != null) {
      builder.save(consumer, wrap(modifier, recipeFolder, "_from_dust"));
      IncrementalModifierRecipeBuilder.modifier(modifier)
                                      .setTools(tools)
                                      .setInput(Tags.Items.STORAGE_BLOCKS_REDSTONE, 9, 45)
                                      .setLeftover(new ItemStack(Items.REDSTONE))
                                      .setMaxLevel(maxLevel)
                                      .setSlots(SlotType.UPGRADE, 1)
                                      .disallowCrystal() // avoid redundancy, though in this case the end result is the same
                                      .save(consumer, wrap(modifier, recipeFolder, "_from_block"));
    }
  }

  /** Prefixes the modifier ID with the given prefix */
  public ResourceLocation prefix(LazyModifier modifier, String prefix) {
    return prefix(modifier.getId(), prefix);
  }

  /** Prefixes the modifier ID with the given prefix and suffix */
  public ResourceLocation wrap(LazyModifier modifier, String prefix, String suffix) {
    return wrap(modifier.getId(), prefix, suffix);
  }

  /**
   * Creates a compound ingredient from multiple tags
   * @param tags  Tags to use
   * @return  Compound ingredient
   */
  @SafeVarargs
  private static Ingredient ingredientFromTags(TagKey<Item>... tags) {
    Ingredient[] tagIngredients = new Ingredient[tags.length];
    for (int i = 0; i < tags.length; i++) {
      tagIngredients[i] = Ingredient.of(tags[i]);
    }
    return CompoundIngredient.of(tagIngredients);
  }
}
