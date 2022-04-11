package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.dynamic.ExtraModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.MobDisguiseModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.StatBoostModifier;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;

import static slimeknights.tconstruct.common.TinkerTags.Items.ARMOR;
import static slimeknights.tconstruct.common.TinkerTags.Items.DURABILITY;
import static slimeknights.tconstruct.common.TinkerTags.Items.HARVEST;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE;
import static slimeknights.tconstruct.common.TinkerTags.Items.MELEE_OR_UNARMED;

public class ModifierProvider extends AbstractModifierProvider {
  public ModifierProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void addModifiers() {
    // extra modifier slots
    addModifier(ModifierIds.writable, ExtraModifier.builder(SlotType.UPGRADE).build());
    addModifier(ModifierIds.recapitated, ExtraModifier.builder(SlotType.UPGRADE).build());
    addModifier(ModifierIds.harmonious, ExtraModifier.builder(SlotType.UPGRADE).build());
    addModifier(ModifierIds.resurrected, ExtraModifier.builder(SlotType.UPGRADE).build());
    addModifier(ModifierIds.gilded, ExtraModifier.builder(SlotType.UPGRADE).slotsPerLevel(2).multiLevel().build());
    addModifier(ModifierIds.draconic, ExtraModifier.builder(SlotType.ABILITY).build());
    // TODO: redirect red_extra_upgrade, green_extra_upgrade, blue_extra_upgrade, extra_ability

    // tier upgrades
    // emerald
    addModifier(ModifierIds.emerald, StatBoostModifier.builder()
      .rarity(Rarity.UNCOMMON)
      .multiplyBase(ToolStats.DURABILITY, 0.5f, DURABILITY)
      .multiplyConditional(ToolStats.ATTACK_DAMAGE, 0.25f, MELEE)
      .multiplyConditional(ToolStats.MINING_SPEED,  0.25f, HARVEST)
      .update(ToolStats.HARVEST_TIER, Tiers.IRON, HARVEST)
      .add(ToolStats.KNOCKBACK_RESISTANCE, 0.05f, ARMOR)
      .build());
    // diamond
    addModifier(ModifierIds.diamond, StatBoostModifier.builder()
      .rarity(Rarity.UNCOMMON)
      .add(ToolStats.DURABILITY,  500,    DURABILITY)
      // armor grants less durability boost
      .add(ToolStats.DURABILITY, -250,    DURABILITY, ARMOR)
      .add(ToolStats.ARMOR,         1,    ARMOR)
      .add(ToolStats.ATTACK_DAMAGE, 0.5f, MELEE_OR_UNARMED)
      .add(ToolStats.MINING_SPEED,  2,    HARVEST)
      .update(ToolStats.HARVEST_TIER, Tiers.DIAMOND, HARVEST)
      .build());
    // netherite
    addModifier(ModifierIds.netherite, StatBoostModifier.builder()
      .rarity(Rarity.RARE)
      .addFlag(IModifiable.INDESTRUCTIBLE_ENTITY)
      .multiplyBase(ToolStats.DURABILITY,    0.2f,  DURABILITY)
      .add(ToolStats.ARMOR_TOUGHNESS,        1,     ARMOR)
      .add(ToolStats.KNOCKBACK_RESISTANCE,   0.05f, ARMOR)
      .multiplyBase(ToolStats.ATTACK_DAMAGE, 0.2f,  MELEE_OR_UNARMED)
      .multiplyBase(ToolStats.MINING_SPEED,  0.25f, HARVEST)
      .update(ToolStats.HARVEST_TIER, Tiers.NETHERITE, HARVEST)
      .build());

    // general
    addModifier(ModifierIds.worldbound, StatBoostModifier.builder().addFlag(IModifiable.INDESTRUCTIBLE_ENTITY).build());
    addModifier(ModifierIds.shiny,      StatBoostModifier.builder().addFlag(IModifiable.SHINY).rarity(Rarity.EPIC).build());

    // armor
    addModifier(TinkerModifiers.golden, StatBoostModifier.builder().addFlag(ModifiableArmorItem.PIGLIN_NEUTRAL).build());
    addModifier(ModifierIds.wings,  StatBoostModifier.builder().addFlag(ModifiableArmorItem.ELYTRA).build());
    addModifier(ModifierIds.knockbackResistance, StatBoostModifier.builder().add(ToolStats.KNOCKBACK_RESISTANCE, 0.1f, ARMOR).build());

    // traits - tier 1
    addModifier(ModifierIds.stringy, new Modifier());
    // traits - tier 2
    addModifier(ModifierIds.sturdy, StatBoostModifier.builder().multiplyBase(ToolStats.DURABILITY, 0.15f, DURABILITY).build());
    // traits - tier 3
    addModifier(ModifierIds.enhanced, ExtraModifier.builder(SlotType.UPGRADE).alwaysShow().multiLevel().build());
    // traits - tier 3 nether
    addModifier(ModifierIds.lightweight, StatBoostModifier.builder()
      .multiplyBase(ToolStats.ATTACK_SPEED, 0.07f, MELEE)
      .multiplyBase(ToolStats.MINING_SPEED, 0.07f, HARVEST)
      .build());
    // traits - tier 3 compat
    addModifier(ModifierIds.ductile, StatBoostModifier.builder()
      .multiplyBase(ToolStats.DURABILITY,    0.04f, DURABILITY)
      .multiplyBase(ToolStats.ATTACK_DAMAGE, 0.04f, MELEE)
      .multiplyBase(ToolStats.MINING_SPEED,  0.04f, HARVEST)
      .build());

    // mob disguise
    addModifier(ModifierIds.creeperDisguise,         new MobDisguiseModifier(EntityType.CREEPER));
    addModifier(ModifierIds.endermanDisguise,        new MobDisguiseModifier(EntityType.ENDERMAN));
    addModifier(ModifierIds.skeletonDisguise,        new MobDisguiseModifier(EntityType.SKELETON));
    addModifier(ModifierIds.strayDisguise,           new MobDisguiseModifier(EntityType.STRAY));
    addModifier(ModifierIds.witherSkeletonDisguise,  new MobDisguiseModifier(EntityType.WITHER_SKELETON));
    addModifier(ModifierIds.spiderDisguise,          new MobDisguiseModifier(EntityType.SPIDER));
    addModifier(ModifierIds.caveSpiderDisguise,      new MobDisguiseModifier(EntityType.CAVE_SPIDER));
    addModifier(ModifierIds.zombieDisguise,          new MobDisguiseModifier(EntityType.ZOMBIE));
    addModifier(ModifierIds.huskDisguise,            new MobDisguiseModifier(EntityType.HUSK));
    addModifier(ModifierIds.drownedDisguise,         new MobDisguiseModifier(EntityType.DROWNED));
    addModifier(ModifierIds.blazeDisguise,           new MobDisguiseModifier(EntityType.BLAZE));
    addModifier(ModifierIds.piglinDisguise,          new MobDisguiseModifier(EntityType.PIGLIN));
    addModifier(ModifierIds.piglinBruteDisguise,     new MobDisguiseModifier(EntityType.PIGLIN_BRUTE));
    addModifier(ModifierIds.zombifiedPiglinDisguise, new MobDisguiseModifier(EntityType.ZOMBIFIED_PIGLIN));

  }

  @Override
  public String getName() {
    return "Tinkers' Construct Modifiers";
  }
}
