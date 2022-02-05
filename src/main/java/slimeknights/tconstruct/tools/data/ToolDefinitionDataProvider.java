package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractToolDefinitionDataProvider;
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
import slimeknights.tconstruct.library.tools.definition.harvest.predicate.AndBlockPredicate;
import slimeknights.tconstruct.library.tools.definition.harvest.predicate.TagBlockPredicate;
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
      .trait(TinkerModifiers.piercing, 1)
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
      .trait(TinkerModifiers.smite, 2)
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
      .trait(TinkerModifiers.piercing, 2)
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
      .trait(TinkerModifiers.knockback, 1)
      .trait(TinkerModifiers.hoeTill)
      // harvest
      .action(ToolActions.AXE_DIG)
      .action(ToolActions.SHOVEL_DIG)
      .harvestLogic(ModifiedHarvestLogic
                      .builder(TinkerTags.Blocks.MINABLE_WITH_MATTOCK)
                      // 200% hand speed on any axe block we do not directly target
                      .addModifier(2f, new AndBlockPredicate(new TagBlockPredicate(BlockTags.MINEABLE_WITH_AXE),
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
      .trait(TinkerModifiers.shovelFlatten)
      .trait(TinkerModifiers.baneOfSssss)
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
      .trait(TinkerModifiers.shovelFlatten)
      // harvest
      .action(ToolActions.SHOVEL_DIG)
      .effective(BlockTags.MINEABLE_WITH_SHOVEL)
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
      .trait(TinkerModifiers.axeScrape)
      .trait(TinkerModifiers.axeStrip)
      .trait(TinkerModifiers.axeWaxOff)
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
      .multiplier(ToolStats.ATTACK_DAMAGE, 1.5f)
      .multiplier(ToolStats.MINING_SPEED, 0.3f)
      .multiplier(ToolStats.DURABILITY, 4.25f)
      .largeToolStartingSlots()
      // traits
      .trait(TinkerModifiers.axeScrape)
      .trait(TinkerModifiers.axeStrip)
      .trait(TinkerModifiers.axeWaxOff)
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
      .trait(TinkerModifiers.shears)
      .trait(TinkerModifiers.harvest)
      // harvest
      .action(ToolActions.HOE_DIG)
      .harvestLogic(scytheHarvest)
      .aoe(new CircleAOEIterator(1, true))
      .attack(new CircleWeaponAttack(1));;

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
      .trait(TinkerModifiers.hoeTill)
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

    // special
    define(ToolDefinitions.FLINT_AND_BRONZE)
      // stats
      .stat(ToolStats.DURABILITY, 100)
      .startingSlots(SlotType.UPGRADE, 1)
      // traits
      .trait(TinkerModifiers.firestarterHidden)
      .trait(TinkerModifiers.fiery);


    // travelers armor
    defineArmor(ArmorDefinitions.TRAVELERS)
      .durabilityFactor(10)
      .statEach(ToolStats.ARMOR, 1, 4, 5, 1)
      .startingSlots(SlotType.UPGRADE, 3)
      .startingSlots(SlotType.DEFENSE, 2)
      .startingSlots(SlotType.ABILITY, 1);

    // plate armor
    defineArmor(ArmorDefinitions.PLATE)
      .durabilityFactor(30)
      .statEach(ToolStats.ARMOR, 2, 5, 7, 2)
      .statAll(ToolStats.ARMOR_TOUGHNESS, 2)
      .statAll(ToolStats.KNOCKBACK_RESISTANCE, 0.1f)
      .startingSlots(SlotType.UPGRADE, 1)
      .startingSlots(SlotType.DEFENSE, 4)
      .startingSlots(SlotType.ABILITY, 1);

    // slime suit
    defineArmor(ArmorDefinitions.SLIMESUIT)
      .statEach(ToolStats.DURABILITY, 546, 630, 672, 362)
      .statAll(ToolStats.ARMOR, 0)
      .startingSlots(SlotType.UPGRADE, 5)
      .startingSlots(SlotType.DEFENSE, 0)
      .startingSlots(SlotType.ABILITY, 1, 1, 1, 1)
      .part(ArmorSlotType.HELMET, SkullStats.ID, 1)
      .trait(ArmorSlotType.CHESTPLATE, TinkerModifiers.wings)
      .trait(ArmorSlotType.LEGGINGS, TinkerModifiers.pockets, 1)
      .trait(ArmorSlotType.LEGGINGS, TinkerModifiers.protection, 1)
      .trait(ArmorSlotType.BOOTS, TinkerModifiers.bouncy)
      .trait(ArmorSlotType.BOOTS, TinkerModifiers.leaping, 1);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Tool Definition Data Generator";
  }
}
