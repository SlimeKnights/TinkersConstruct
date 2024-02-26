package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractToolDefinitionDataProvider;
import slimeknights.tconstruct.library.json.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.json.predicate.block.TagBlockPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.SingleModifierPredicate;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.aoe.BoxAOEIterator;
import slimeknights.tconstruct.library.tools.definition.aoe.CircleAOEIterator;
import slimeknights.tconstruct.library.tools.definition.aoe.FallbackAOEIterator;
import slimeknights.tconstruct.library.tools.definition.aoe.IBoxExpansion;
import slimeknights.tconstruct.library.tools.definition.aoe.TreeAOEIterator;
import slimeknights.tconstruct.library.tools.definition.aoe.VeiningAOEIterator;
import slimeknights.tconstruct.library.tools.definition.harvest.FixedTierHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.harvest.IHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.harvest.ModifiedHarvestLogic;
import slimeknights.tconstruct.library.tools.definition.module.ToolModuleHooks;
import slimeknights.tconstruct.library.tools.definition.module.interaction.DualOptionInteraction;
import slimeknights.tconstruct.library.tools.definition.module.interaction.PreferenceSetInteraction;
import slimeknights.tconstruct.library.tools.definition.weapon.CircleWeaponAttack;
import slimeknights.tconstruct.library.tools.definition.weapon.ParticleWeaponAttack;
import slimeknights.tconstruct.library.tools.definition.weapon.SweepWeaponAttack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.ArmorDefinitions;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolActions;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.ToolDefinitions;
import slimeknights.tconstruct.tools.item.ArmorSlotType;
import slimeknights.tconstruct.tools.stats.SkullStats;

import static slimeknights.tconstruct.tools.TinkerToolParts.bowGrip;
import static slimeknights.tconstruct.tools.TinkerToolParts.bowLimb;
import static slimeknights.tconstruct.tools.TinkerToolParts.bowstring;
import static slimeknights.tconstruct.tools.TinkerToolParts.broadAxeHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.broadBlade;
import static slimeknights.tconstruct.tools.TinkerToolParts.hammerHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.largePlate;
import static slimeknights.tconstruct.tools.TinkerToolParts.pickHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.roundPlate;
import static slimeknights.tconstruct.tools.TinkerToolParts.smallAxeHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.smallBlade;
import static slimeknights.tconstruct.tools.TinkerToolParts.toolBinding;
import static slimeknights.tconstruct.tools.TinkerToolParts.toolHandle;
import static slimeknights.tconstruct.tools.TinkerToolParts.toughHandle;

public class ToolDefinitionDataProvider extends AbstractToolDefinitionDataProvider {
  public ToolDefinitionDataProvider(DataGenerator generator) {
    super(generator, TConstruct.MOD_ID);
  }

  @Override
  protected void addToolDefinitions() {
    // pickaxes
    define(ToolDefinitions.PICKAXE)
      // parts
      .part(pickHead)
      .part(toolHandle)
      .part(toolBinding)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 0.5f) // gains +0.5 damage from tool piercing, hence being lower than vanilla
      .stat(ToolStats.ATTACK_SPEED, 1.2f)
      .smallToolStartingSlots()
      // traits
      .trait(ModifierIds.pierce, 1)
      // harvest
      .action(ToolActions.PICKAXE_DIG)
      .effective(BlockTags.MINEABLE_WITH_PICKAXE)
      .aoe(BoxAOEIterator.builder(0, 0, 0).addDepth(2).addHeight(1).direction(IBoxExpansion.PITCH).build());

    define(ToolDefinitions.SLEDGE_HAMMER)
      // parts
      .part(hammerHead, 2)
      .part(toughHandle)
      .part(largePlate, 1)
      .part(largePlate, 1)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 3f) // gains +5 undead damage from smite modifier
      .stat(ToolStats.ATTACK_SPEED, 0.75f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 1.35f)
      .multiplier(ToolStats.MINING_SPEED, 0.4f)
      .multiplier(ToolStats.DURABILITY, 4f)
      .largeToolStartingSlots()
      // traits
      .trait(ModifierIds.smite, 2)
      // harvest
      .action(ToolActions.PICKAXE_DIG)
      .effective(BlockTags.MINEABLE_WITH_PICKAXE)
      .aoe(BoxAOEIterator.builder(1, 1, 0).addWidth(1).addHeight(1).build())
      .attack(new ParticleWeaponAttack(TinkerTools.hammerAttackParticle.get()));

    define(ToolDefinitions.VEIN_HAMMER)
      // parts
      .part(hammerHead, 2)
      .part(toughHandle)
      .part(pickHead, 1)
      .part(largePlate)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 3f) // gains +1.25 damage from piercing
      .stat(ToolStats.ATTACK_SPEED, 0.85f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 1.25f)
      .multiplier(ToolStats.MINING_SPEED, 0.3f)
      .multiplier(ToolStats.DURABILITY, 5.0f)
      .largeToolStartingSlots()
      // traits
      .trait(ModifierIds.pierce, 2)
      // harvest
      .action(ToolActions.PICKAXE_DIG)
      .effective(BlockTags.MINEABLE_WITH_PICKAXE)
      .aoe(new VeiningAOEIterator(2))
      .attack(new ParticleWeaponAttack(TinkerTools.hammerAttackParticle.get()));


    // shovels
    define(ToolDefinitions.MATTOCK)
      // parts
      .part(smallAxeHead)
      .part(toolHandle)
      .part(roundPlate)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 1.5f)
      .stat(ToolStats.ATTACK_SPEED, 0.9f)
      .smallToolStartingSlots()
      .multiplier(ToolStats.DURABILITY, 1.25f)
      .multiplier(ToolStats.MINING_SPEED, 1.1f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 1.1f)
      // traits
      .trait(ModifierIds.tilling)
      // harvest
      .action(ToolActions.AXE_DIG)
      .action(ToolActions.SHOVEL_DIG)
      .harvestLogic(ModifiedHarvestLogic
                      .builder(TinkerTags.Blocks.MINABLE_WITH_MATTOCK)
                      // 200% hand speed on any axe block we do not directly target
                      .addModifier(2f, BlockPredicate.AND.create(new TagBlockPredicate(BlockTags.MINEABLE_WITH_AXE),
                                                                 new TagBlockPredicate(TinkerTags.Blocks.MINABLE_WITH_MATTOCK).inverted()))
                      .build())
      .aoe(new VeiningAOEIterator(0));

    define(ToolDefinitions.PICKADZE)
      // parts
      .part(pickHead)
      .part(toolHandle)
      .part(roundPlate)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 0.5f)
      .stat(ToolStats.ATTACK_SPEED, 1.3f)
      .smallToolStartingSlots()
      .multiplier(ToolStats.DURABILITY, 1.3f)
      .multiplier(ToolStats.MINING_SPEED, 0.75f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 1.15f)
      // traits
      .trait(ModifierIds.pathing)
      // harvest
      .action(ToolActions.PICKAXE_DIG)
      .action(ToolActions.SHOVEL_DIG)
      .harvestLogic(new FixedTierHarvestLogic(TinkerTags.Blocks.MINABLE_WITH_PICKADZE, Tiers.GOLD))
      .aoe(BoxAOEIterator.builder(0, 0, 0).addHeight(1).build());

    define(ToolDefinitions.EXCAVATOR)
      // parts
      .part(largePlate)
      .part(toughHandle)
      .part(largePlate)
      .part(toughHandle)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 1.5f)
      .stat(ToolStats.ATTACK_SPEED, 1.0f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 1.2f)
      .multiplier(ToolStats.MINING_SPEED, 0.3f)
      .multiplier(ToolStats.DURABILITY, 3.75f)
      .largeToolStartingSlots()
      // traits
      .trait(TinkerModifiers.knockback, 2)
      .trait(ModifierIds.pathing)
      // harvest
      .action(ToolActions.SHOVEL_DIG)
      .effective(BlockTags.MINEABLE_WITH_SHOVEL)
      .attack(new ParticleWeaponAttack(TinkerTools.bonkAttackParticle.get()))
      .aoe(BoxAOEIterator.builder(1, 1, 0).addWidth(1).addHeight(1).build());


    // axes
    define(ToolDefinitions.HAND_AXE)
      // parts
      .part(smallAxeHead)
      .part(toolHandle)
      .part(toolBinding)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 6.0f)
      .stat(ToolStats.ATTACK_SPEED, 0.9f)
      .smallToolStartingSlots()
      // traits
      .trait(ModifierIds.stripping)
      // harvest
      .action(ToolActions.AXE_DIG)
      .action(TinkerToolActions.SHIELD_DISABLE)
      .effective(TinkerTags.Blocks.MINABLE_WITH_HAND_AXE)
      .aoe(new CircleAOEIterator(1, false))
      .attack(new ParticleWeaponAttack(TinkerTools.axeAttackParticle.get()));

    define(ToolDefinitions.BROAD_AXE)
      // parts
      .part(broadAxeHead, 2)
      .part(toughHandle)
      .part(pickHead, 1)
      .part(toolBinding)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 5f)
      .stat(ToolStats.ATTACK_SPEED, 0.6f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 1.65f)
      .multiplier(ToolStats.MINING_SPEED, 0.3f)
      .multiplier(ToolStats.DURABILITY, 4.25f)
      .largeToolStartingSlots()
      // traits
      .trait(ModifierIds.stripping)
      // harvest
      .action(ToolActions.AXE_DIG)
      .action(TinkerToolActions.SHIELD_DISABLE)
      .effective(BlockTags.MINEABLE_WITH_AXE)
      .aoe(new FallbackAOEIterator(
        TinkerTags.Blocks.TREE_LOGS, new TreeAOEIterator(0, 0),
        BoxAOEIterator.builder(0, 5, 0).addWidth(1).addDepth(1).direction(IBoxExpansion.HEIGHT).build()))
      .attack(new ParticleWeaponAttack(TinkerTools.axeAttackParticle.get()));

    // scythes
    IHarvestLogic scytheHarvest = ModifiedHarvestLogic
      .builder(TinkerTags.Blocks.MINABLE_WITH_SCYTHE)
      .tagModifier(BlockTags.WOOL, 0.3f)
      .blockModifier(0.10f, Blocks.VINE, Blocks.GLOW_LICHEN)
      .build();
    define(ToolDefinitions.KAMA)
      // parts
      .part(smallBlade)
      .part(toolHandle)
      .part(toolBinding)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 1f)
      .stat(ToolStats.ATTACK_SPEED, 1.6f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 0.5f)
      .smallToolStartingSlots()
      // traits
      .trait(ModifierIds.tilling)
      .trait(TinkerModifiers.shears)
      .trait(TinkerModifiers.harvest)
      // harvest
      .action(ToolActions.HOE_DIG)
      .harvestLogic(scytheHarvest)
      .aoe(new CircleAOEIterator(1, true))
      .attack(new CircleWeaponAttack(1));

    define(ToolDefinitions.SCYTHE)
      // parts
      .part(TinkerToolParts.broadBlade)
      .part(TinkerToolParts.toughHandle)
      .part(TinkerToolParts.toolBinding)
      .part(TinkerToolParts.toughHandle)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 1f)
      .stat(ToolStats.ATTACK_SPEED, 0.7f)
      .multiplier(ToolStats.MINING_SPEED, 0.45f)
      .multiplier(ToolStats.DURABILITY, 2.5f)
      .largeToolStartingSlots()
      // traits
      .trait(ModifierIds.tilling)
      .trait(TinkerModifiers.aoeSilkyShears)
      .trait(TinkerModifiers.harvest)
      // behavior
      .harvestLogic(scytheHarvest)
      .aoe(BoxAOEIterator.builder(1, 1, 2).addExpansion(1, 1, 0).addDepth(2).build())
      .attack(new CircleWeaponAttack(2));


    // swords
    define(ToolDefinitions.DAGGER)
      // parts
      .part(smallBlade)
      .part(toolHandle)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 3f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 0.65f)
      .stat(ToolStats.ATTACK_SPEED, 2.0f)
      .multiplier(ToolStats.MINING_SPEED, 0.75f)
      .multiplier(ToolStats.DURABILITY, 0.75f)
      .stat(ToolStats.BLOCK_AMOUNT, 10)
      .stat(ToolStats.USE_ITEM_SPEED, 1.0f)
      .smallToolStartingSlots()
      // traits
      .trait(TinkerModifiers.padded, 1)
      .trait(TinkerModifiers.offhandAttack)
      .trait(TinkerModifiers.silkyShears)
      // behavior
      .action(ToolActions.SWORD_DIG)
      .action(ToolActions.HOE_DIG)
      .harvestLogic(ModifiedHarvestLogic
                      .builder(TinkerTags.Blocks.MINABLE_WITH_DAGGER)
                      .blockModifier(7.5f, Blocks.COBWEB)
                      .build());

    IHarvestLogic swordLogic = ModifiedHarvestLogic
      .builder(TinkerTags.Blocks.MINABLE_WITH_SWORD)
      .blockModifier(7.5f, Blocks.COBWEB)
      .blockModifier(100f, Blocks.BAMBOO, Blocks.BAMBOO_SAPLING)
      .build();
    define(ToolDefinitions.SWORD)
      // parts
      .part(smallBlade)
      .part(toolHandle)
      .part(toolHandle)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 3f)
      .stat(ToolStats.ATTACK_SPEED, 1.6f)
      .multiplier(ToolStats.MINING_SPEED, 0.5f)
      .multiplier(ToolStats.DURABILITY, 1.1f)
      .smallToolStartingSlots()
      // traits
      .trait(TinkerModifiers.silkyShears)
      .action(ToolActions.SWORD_DIG)
      // behavior
      .harvestLogic(swordLogic)
      .attack(new SweepWeaponAttack(1));

    define(ToolDefinitions.CLEAVER)
      // parts
      .part(broadBlade)
      .part(toughHandle)
      .part(toughHandle)
      .part(largePlate)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 3f)
      .stat(ToolStats.ATTACK_SPEED, 1.0f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 1.5f)
      .multiplier(ToolStats.MINING_SPEED, 0.25f)
      .multiplier(ToolStats.DURABILITY, 3.5f)
      .largeToolStartingSlots()
      // traits
      .trait(TinkerModifiers.severing, 2)
      .trait(TinkerModifiers.aoeSilkyShears)
      // behavior
      .action(ToolActions.SWORD_DIG)
      .harvestLogic(swordLogic)
      .attack(new SweepWeaponAttack(2));

    // bows
    define(ToolDefinitions.CROSSBOW)
      // parts
      .part(bowLimb)
      .part(bowGrip)
      .part(bowstring)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 0f)
      .stat(ToolStats.ATTACK_SPEED, 1.0f)
      .multiplier(ToolStats.DURABILITY, 2f)
      .smallToolStartingSlots();
    define(ToolDefinitions.LONGBOW)
      // parts
      .part(bowLimb)
      .part(bowLimb)
      .part(bowGrip)
      .part(bowstring)
      // stats
      .stat(ToolStats.DURABILITY, 120)
      .stat(ToolStats.ATTACK_DAMAGE, 0f)
      .stat(ToolStats.ATTACK_SPEED, 1.0f)
      .multiplier(ToolStats.DURABILITY, 1.5f) // gets effectively 2x durability from having 2 heads
      .largeToolStartingSlots();

    // special
    define(ToolDefinitions.FLINT_AND_BRICK)
      // stats
      .stat(ToolStats.DURABILITY, 100)
      .startingSlots(SlotType.UPGRADE, 1)
      // traits
      .trait(TinkerModifiers.firestarter)
      .trait(TinkerModifiers.fiery)
      .trait(ModifierIds.scorching);
    // staff
    define(ToolDefinitions.SKY_STAFF)
      .stat(ToolStats.DURABILITY, 375)
      .stat(ToolStats.BLOCK_AMOUNT, 15)
      .stat(ToolStats.USE_ITEM_SPEED, 0.4f)
      .stat(ToolStats.VELOCITY, 0.8f)
      .stat(ToolStats.DRAW_SPEED, 1.25f)
      .startingSlots(SlotType.UPGRADE, 5)
      .startingSlots(SlotType.ABILITY, 2)
      .trait(ModifierIds.overslimeFriend)
      .aoe(new CircleAOEIterator(1, false))
      .module(ToolModuleHooks.INTERACTION, DualOptionInteraction.INSTANCE);
    define(ToolDefinitions.EARTH_STAFF)
      .stat(ToolStats.DURABILITY, 800)
      .stat(ToolStats.BLOCK_AMOUNT, 35)
      .stat(ToolStats.USE_ITEM_SPEED, 0.4f)
      .stat(ToolStats.PROJECTILE_DAMAGE, 1.5f)
      .stat(ToolStats.ACCURACY, 0.9f)
      .startingSlots(SlotType.UPGRADE, 2)
      .startingSlots(SlotType.DEFENSE, 3)
      .startingSlots(SlotType.ABILITY, 2)
      .trait(ModifierIds.overslimeFriend)
      .aoe(new CircleAOEIterator(1, false))
      .module(ToolModuleHooks.INTERACTION, DualOptionInteraction.INSTANCE);
    define(ToolDefinitions.ICHOR_STAFF)
      .stat(ToolStats.DURABILITY, 1225)
      .stat(ToolStats.BLOCK_AMOUNT, 15)
      .stat(ToolStats.USE_ITEM_SPEED, 0.4f)
      .stat(ToolStats.VELOCITY, 1.2f)
      .stat(ToolStats.DRAW_SPEED, 0.75f)
      .startingSlots(SlotType.UPGRADE, 2)
      .startingSlots(SlotType.ABILITY, 3)
      .trait(ModifierIds.overslimeFriend)
      .aoe(new CircleAOEIterator(1, false))
      .module(ToolModuleHooks.INTERACTION, DualOptionInteraction.INSTANCE);
    define(ToolDefinitions.ENDER_STAFF)
      .stat(ToolStats.DURABILITY, 1520)
      .stat(ToolStats.BLOCK_AMOUNT, 15)
      .stat(ToolStats.BLOCK_ANGLE, 140)
      .stat(ToolStats.USE_ITEM_SPEED, 0.4f)
      .stat(ToolStats.PROJECTILE_DAMAGE, 3f)
      .stat(ToolStats.ACCURACY, 0.5f)
      .startingSlots(SlotType.UPGRADE, 3)
      .startingSlots(SlotType.DEFENSE, 1)
      .startingSlots(SlotType.ABILITY, 2)
      .trait(ModifierIds.overslimeFriend)
      .trait(ModifierIds.reach, 2)
      .aoe(new CircleAOEIterator(1, false))
      .module(ToolModuleHooks.INTERACTION, DualOptionInteraction.INSTANCE);


    // travelers armor
    defineArmor(ArmorDefinitions.TRAVELERS)
      .durabilityFactor(10)
      .statEach(ToolStats.ARMOR, 1, 4, 5, 1)
      .multiplier(ArmorSlotType.CHESTPLATE, ToolStats.ATTACK_DAMAGE, 0.55f)
      .startingSlots(SlotType.UPGRADE, 3)
      .startingSlots(SlotType.DEFENSE, 2)
      .startingSlots(SlotType.ABILITY, 1)
      .trait(ArmorSlotType.BOOTS, ModifierIds.snowBoots);
    define(ArmorDefinitions.TRAVELERS_SHIELD)
      .stat(ToolStats.DURABILITY, 200)
      .stat(ToolStats.BLOCK_AMOUNT, 10)
      .stat(ToolStats.BLOCK_ANGLE, 90)
      .stat(ToolStats.USE_ITEM_SPEED, 0.8f)
      .startingSlots(SlotType.UPGRADE, 3)
      .startingSlots(SlotType.DEFENSE, 2)
      .startingSlots(SlotType.ABILITY, 1)
      .trait(TinkerModifiers.blocking)
      .module(ToolModuleHooks.INTERACTION, new PreferenceSetInteraction(InteractionSource.RIGHT_CLICK, new SingleModifierPredicate(TinkerModifiers.blocking.getId())));

    // plate armor
    defineArmor(ArmorDefinitions.PLATE)
      .durabilityFactor(30)
      .statEach(ToolStats.ARMOR, 2, 5, 7, 2)
      .statAll(ToolStats.ARMOR_TOUGHNESS, 2)
      .statAll(ToolStats.KNOCKBACK_RESISTANCE, 0.1f)
      .multiplier(ArmorSlotType.CHESTPLATE, ToolStats.ATTACK_DAMAGE, 0.4f)
      .startingSlots(SlotType.UPGRADE, 1)
      .startingSlots(SlotType.DEFENSE, 4)
      .startingSlots(SlotType.ABILITY, 1);
    define(ArmorDefinitions.PLATE_SHIELD)
      .stat(ToolStats.DURABILITY, 500)
      .stat(ToolStats.BLOCK_AMOUNT, 100)
      .stat(ToolStats.BLOCK_ANGLE, 180)
      .stat(ToolStats.ARMOR_TOUGHNESS, 2)
      .startingSlots(SlotType.UPGRADE, 1)
      .startingSlots(SlotType.DEFENSE, 4)
      .startingSlots(SlotType.ABILITY, 1)
      .trait(TinkerModifiers.blocking)
      .module(ToolModuleHooks.INTERACTION, new PreferenceSetInteraction(InteractionSource.RIGHT_CLICK, new SingleModifierPredicate(TinkerModifiers.blocking.getId())));

    // slime suit
    defineArmor(ArmorDefinitions.SLIMESUIT)
      .statEach(ToolStats.DURABILITY, 546, 630, 672, 362)
      .statAll(ToolStats.ARMOR, 0)
      .multiplier(ArmorSlotType.CHESTPLATE, ToolStats.ATTACK_DAMAGE, 0.4f)
      .startingSlots(SlotType.UPGRADE, 5)
      .startingSlots(SlotType.DEFENSE, 0)
      .startingSlots(SlotType.ABILITY, 1, 1, 1, 1)
      .part(ArmorSlotType.HELMET, SkullStats.ID, 1)
      .trait(ModifierIds.overslimeFriend)
      .trait(ArmorSlotType.CHESTPLATE, ModifierIds.wings)
      .trait(ArmorSlotType.LEGGINGS, ModifierIds.pockets, 1)
      .trait(ArmorSlotType.LEGGINGS, TinkerModifiers.shulking, 1)
      .trait(ArmorSlotType.BOOTS, TinkerModifiers.bouncy)
      .trait(ArmorSlotType.BOOTS, TinkerModifiers.leaping, 1);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Tool Definition Data Generator";
  }
}
