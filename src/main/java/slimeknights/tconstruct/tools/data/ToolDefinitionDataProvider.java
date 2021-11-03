package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.tinkering.AbstractToolDefinitionDataProvider;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.ToolDefinitions;

import static slimeknights.tconstruct.tools.TinkerToolParts.broadAxeHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.broadBlade;
import static slimeknights.tconstruct.tools.TinkerToolParts.hammerHead;
import static slimeknights.tconstruct.tools.TinkerToolParts.largePlate;
import static slimeknights.tconstruct.tools.TinkerToolParts.pickaxeHead;
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
      .part(pickaxeHead)
      .part(toolHandle)
      .part(toolBinding)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 0.5f) // gains +0.5 damage from tool piercing, hence being lower than vanilla
      .stat(ToolStats.ATTACK_SPEED, 1.2f)
      .smallToolStartingSlots()
      // traits
      .trait(TinkerModifiers.piercing, 1);

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
      .trait(TinkerModifiers.twoHanded);

    define(ToolDefinitions.VEIN_HAMMER)
      // parts
      .part(hammerHead, 2)
      .part(toughHandle)
      .part(pickaxeHead, 1)
      .part(largePlate)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 3f) // gains +1.25 damage from piercing
      .stat(ToolStats.ATTACK_SPEED, 1.1f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 1.25f)
      .multiplier(ToolStats.MINING_SPEED, 0.3f)
      .multiplier(ToolStats.DURABILITY, 5.0f)
      .largeToolStartingSlots()
      // traits
      .trait(TinkerModifiers.piercing, 2)
      .trait(TinkerModifiers.twoHanded);


    // shovels
    define(ToolDefinitions.MATTOCK)
      // parts
      .part(smallAxeHead)
      .part(toolHandle)
      .part(pickaxeHead)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 1.5f)
      .stat(ToolStats.ATTACK_SPEED, 1f)
      .smallToolStartingSlots()
      // traits
      .trait(TinkerModifiers.knockback, 1)
      .trait(TinkerModifiers.shovelTransformHidden);

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
      .trait(TinkerModifiers.shovelTransformHidden)
      .trait(TinkerModifiers.twoHanded);


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
      .trait(TinkerModifiers.axeTransformHidden);

    define(ToolDefinitions.BROAD_AXE)
      // parts
      .part(broadAxeHead, 2)
      .part(toughHandle)
      .part(pickaxeHead, 1)
      .part(toolBinding)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 5f)
      .stat(ToolStats.ATTACK_SPEED, 0.6f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 1.5f)
      .multiplier(ToolStats.MINING_SPEED, 0.3f)
      .multiplier(ToolStats.DURABILITY, 4.25f)
      .largeToolStartingSlots()
      // traits
      .trait(TinkerModifiers.axeTransformHidden)
      .trait(TinkerModifiers.twoHanded);

    // scythes
    define(ToolDefinitions.KAMA)
      // parts
      .part(smallBlade)
      .part(toolHandle)
      .part(toolBinding)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 1f)
      .stat(ToolStats.ATTACK_SPEED, 1.8f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 0.75f)
      .smallToolStartingSlots()
      // traits
      .trait(TinkerModifiers.hoeTransformHidden)
      .trait(TinkerModifiers.shears)
      .trait(TinkerModifiers.harvest);

    define(ToolDefinitions.SCYTHE)
      // parts
      .part(TinkerToolParts.broadBlade)
      .part(TinkerToolParts.toughHandle)
      .part(TinkerToolParts.toolBinding)
      .part(TinkerToolParts.toughHandle)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 1f)
      .stat(ToolStats.ATTACK_SPEED, 0.8f)
      .multiplier(ToolStats.MINING_SPEED, 0.45f)
      .multiplier(ToolStats.DURABILITY, 2.5f)
      .largeToolStartingSlots()
      // traits
      .trait(TinkerModifiers.hoeTransformHidden)
      .trait(TinkerModifiers.aoeSilkyShears)
      .trait(TinkerModifiers.harvest)
      .trait(TinkerModifiers.twoHanded);


    // swords
    define(ToolDefinitions.DAGGER)
      // parts
      .part(smallBlade)
      .part(toolHandle)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 2f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 0.5f)
      .stat(ToolStats.ATTACK_SPEED, 2.0f)
      .multiplier(ToolStats.MINING_SPEED, 0.75f)
      .multiplier(ToolStats.DURABILITY, 0.75f)
      .smallToolStartingSlots()
      // traits
      .trait(TinkerModifiers.padded, 1)
      .trait(TinkerModifiers.offhandAttack)
      .trait(TinkerModifiers.silkyShears);

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
      .trait(TinkerModifiers.silkyShears);

    define(ToolDefinitions.CLEAVER)
      // parts
      .part(broadBlade)
      .part(toughHandle)
      .part(toughHandle)
      .part(largePlate)
      // stats
      .stat(ToolStats.ATTACK_DAMAGE, 3.5f)
      .stat(ToolStats.ATTACK_SPEED, 0.9f)
      .multiplier(ToolStats.ATTACK_DAMAGE, 1.5f)
      .multiplier(ToolStats.MINING_SPEED, 0.25f)
      .multiplier(ToolStats.DURABILITY, 3.5f)
      .largeToolStartingSlots()
      // traits
      .trait(TinkerModifiers.severing, 2)
      .trait(TinkerModifiers.aoeSilkyShears)
      .trait(TinkerModifiers.twoHanded);

    // special
    define(ToolDefinitions.FLINT_AND_BRONZE)
      // stats
      .stat(ToolStats.DURABILITY, 100)
      .startingSlots(SlotType.UPGRADE, 1)
      // traits
      .trait(TinkerModifiers.firestarterHidden)
      .trait(TinkerModifiers.fiery);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Tool Definition Data Generator";
  }
}
