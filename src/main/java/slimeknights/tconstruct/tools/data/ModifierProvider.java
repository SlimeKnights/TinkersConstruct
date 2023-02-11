package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ForgeMod;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.json.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.json.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.predicate.entity.MobTypePredicate;
import slimeknights.tconstruct.library.json.predicate.entity.TagEntityPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.dynamic.ConditionalDamageModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.ConditionalMiningSpeedModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.EnchantmentModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.ExtraModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.InventoryMenuModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.LootModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.MobDisguiseModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.MobEffectModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.StatBoostModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.StatBoostModifier.ModifierDisplay;
import slimeknights.tconstruct.library.modifiers.dynamic.SwappableExtraSlotModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay.UniqueForLevels;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ToolBeltModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

import static slimeknights.tconstruct.common.TinkerTags.Items.ARMOR;

public class ModifierProvider extends AbstractModifierProvider {
  public ModifierProvider(DataGenerator generator) {
    super(generator);
  }

  @Override
  protected void addModifiers() {
    EquipmentSlot[] handSlots = {EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};
    EquipmentSlot[] armorSlots = ModifiableArmorMaterial.ARMOR_SLOTS;
    EquipmentSlot[] armorMainHand = {EquipmentSlot.MAINHAND, EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};

    // extra modifier slots
    addModifier(ModifierIds.writable,    ExtraModifier.builder(SlotType.UPGRADE).build());
    addModifier(ModifierIds.recapitated, ExtraModifier.builder(SlotType.UPGRADE).build());
    addModifier(ModifierIds.harmonious,  ExtraModifier.builder(SlotType.UPGRADE).build());
    addModifier(ModifierIds.resurrected, ExtraModifier.builder(SlotType.UPGRADE).build());
    addModifier(ModifierIds.gilded,      ExtraModifier.builder(SlotType.UPGRADE).slotsPerLevel(2).display(ModifierLevelDisplay.DEFAULT).build());
    addModifier(ModifierIds.draconic,    ExtraModifier.builder(SlotType.ABILITY).build());
    addModifier(ModifierIds.rebalanced, SwappableExtraSlotModifier.swappable().penalize(SlotType.ABILITY, SlotType.UPGRADE).build());
    addRedirect(id("red_extra_upgrade"),   redirect(ModifierIds.writable));
    addRedirect(id("green_extra_upgrade"), redirect(ModifierIds.recapitated));
    addRedirect(id("blue_extra_upgrade"),  redirect(ModifierIds.harmonious));
    addRedirect(id("extra_ability"),       redirect(ModifierIds.draconic));

    // internal modifier migration
    addRedirect(id("shovel_flatten"), redirect(TinkerModifiers.pathing.getId()));
    addRedirect(id("axe_strip"), redirect(TinkerModifiers.stripping.getId()));
    addRedirect(id("hoe_till"), redirect(TinkerModifiers.tilling.getId()));
    addRedirect(id("firestarter_hidden"), redirect(TinkerModifiers.firestarter.getId()));

    // merged some armor modifiers
    addRedirect(id("haste_armor"), redirect(TinkerModifiers.haste.getId()));
    addRedirect(id("knockback_armor"), redirect(TinkerModifiers.knockback.getId()));

    // unarmed rework
    addRedirect(id("unarmed"), redirect(TinkerModifiers.ambidextrous.getId()));

    // tier upgrades
    // emerald
    addModifier(ModifierIds.emerald, StatBoostModifier.builder()
      .display(ModifierLevelDisplay.SINGLE_LEVEL)
      .rarity(Rarity.UNCOMMON)
      .multiplyBase(ToolStats.DURABILITY, 0.5f)
      // armor
      .add(ToolStats.KNOCKBACK_RESISTANCE, 0.05f)
      // melee harvest
      .multiplyConditional(ToolStats.ATTACK_DAMAGE, 0.25f)
      .multiplyConditional(ToolStats.MINING_SPEED,  0.25f)
      .update(ToolStats.HARVEST_TIER, Tiers.IRON)
      // ranged
      .add(ToolStats.ACCURACY, 0.1f)
      .build());
    // diamond
    addModifier(ModifierIds.diamond, StatBoostModifier.builder()
      .display(ModifierLevelDisplay.SINGLE_LEVEL)
      .rarity(Rarity.UNCOMMON)
      .add(ToolStats.DURABILITY,  500)
      // armor grants less durability boost
      .add(ToolStats.DURABILITY, -250, ARMOR)
      .add(ToolStats.ARMOR,         1)
      // melee harvest
      .add(ToolStats.ATTACK_DAMAGE, 0.5f)
      .add(ToolStats.MINING_SPEED,  2)
      .update(ToolStats.HARVEST_TIER, Tiers.DIAMOND)
      // ranged
      .add(ToolStats.PROJECTILE_DAMAGE, 0.5f)
      .build());
    // netherite
    addModifier(ModifierIds.netherite, StatBoostModifier.builder()
      .display(ModifierLevelDisplay.SINGLE_LEVEL)
      .rarity(Rarity.RARE)
      .addFlag(IModifiable.INDESTRUCTIBLE_ENTITY)
      .multiplyBase(ToolStats.DURABILITY,    0.2f)
      // armor
      .add(ToolStats.ARMOR_TOUGHNESS,        1)
      .add(ToolStats.KNOCKBACK_RESISTANCE,   0.05f)
      // melee harvest
      .multiplyBase(ToolStats.ATTACK_DAMAGE, 0.2f)
      .multiplyBase(ToolStats.MINING_SPEED,  0.25f)
      .update(ToolStats.HARVEST_TIER, Tiers.NETHERITE)
      // ranged
      .multiplyBase(ToolStats.VELOCITY, 0.1f)
      .build());

    // general
    addModifier(ModifierIds.worldbound, StatBoostModifier.builder().addFlag(IModifiable.INDESTRUCTIBLE_ENTITY).rarity(Rarity.UNCOMMON).display(ModifierLevelDisplay.NO_LEVELS).build());
    addModifier(ModifierIds.shiny,      StatBoostModifier.builder().addFlag(IModifiable.SHINY).rarity(Rarity.EPIC).display(ModifierLevelDisplay.NO_LEVELS).build());
    // general abilities
    addModifier(ModifierIds.reach, StatBoostModifier.builder()
      .attribute("tconstruct.modifier.reach", ForgeMod.REACH_DISTANCE.get(), Operation.ADDITION, 1, EquipmentSlot.MAINHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)
      .attribute("tconstruct.modifier.range", ForgeMod.ATTACK_RANGE.get(),   Operation.ADDITION, 1, EquipmentSlot.MAINHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)
      .build());


    // loot
    addModifier(TinkerModifiers.silky, new LootModifier(Enchantments.SILK_TOUCH, 1, ModifierLevelDisplay.NO_LEVELS));
    addModifier(ModifierIds.luck, new LootModifier(Enchantments.BLOCK_FORTUNE, 1, 1, new UniqueForLevels(3)));
    addModifier(ModifierIds.fortune, new LootModifier(Enchantments.BLOCK_FORTUNE, 1, ModifierLevelDisplay.DEFAULT));
    addModifier(ModifierIds.looting, new LootModifier(1, ModifierLevelDisplay.DEFAULT));

    /// attack
    addModifier(ModifierIds.sticky, MobEffectModifier.Builder.effect(MobEffects.MOVEMENT_SLOWDOWN).level(0, 0.5f).timeBase(20).timeMultiplierRandom(10).build());

    // damage boost
    // vanilla give +1, 1.5, 2, 2.5, 3, but that is low
    // we instead do +0.75, +1.5, +2.25, +3, +3.75
    addModifier(ModifierIds.sharpness,   StatBoostModifier.builder().add(ToolStats.ATTACK_DAMAGE, 0.75f).display(new UniqueForLevels(5)).build());
    addModifier(ModifierIds.swiftstrike, StatBoostModifier.builder().multiplyBase(ToolStats.ATTACK_SPEED, 0.05f).display(new UniqueForLevels(5)).build());
    addModifier(ModifierIds.smite,       new ConditionalDamageModifier(new MobTypePredicate(MobType.UNDEAD), 2.0f));
    addModifier(ModifierIds.antiaquatic, new ConditionalDamageModifier(new MobTypePredicate(MobType.WATER),  2.0f));
    addModifier(ModifierIds.cooling,     new ConditionalDamageModifier(LivingEntityPredicate.FIRE_IMMUNE,    1.6f));
    addModifier(ModifierIds.baneOfSssss, new ConditionalDamageModifier(
      LivingEntityPredicate.OR.create(new MobTypePredicate(MobType.ARTHROPOD), new TagEntityPredicate(TinkerTags.EntityTypes.CREEPERS)),
      2.0f, MobEffects.MOVEMENT_SLOWDOWN, 4));
    addModifier(ModifierIds.killager, new ConditionalDamageModifier(
      LivingEntityPredicate.OR.create(new MobTypePredicate(MobType.ILLAGER), new TagEntityPredicate(TinkerTags.EntityTypes.VILLAGERS)), 2.0f));
    addRedirect(id("fractured"), redirect(ModifierIds.sharpness));

    // ranged
    addModifier(ModifierIds.power, StatBoostModifier.builder().add(ToolStats.PROJECTILE_DAMAGE, 0.5f).build());
    addModifier(ModifierIds.quickCharge, StatBoostModifier.builder().multiplyBase(ToolStats.DRAW_SPEED, 0.25f).build());
    addModifier(ModifierIds.trueshot,  StatBoostModifier.builder().add(ToolStats.ACCURACY, 0.1f).build());
    addModifier(ModifierIds.blindshot, StatBoostModifier.builder().add(ToolStats.ACCURACY, -0.1f).build());

    // armor
    addModifier(TinkerModifiers.golden, StatBoostModifier.builder().addFlag(ModifiableArmorItem.PIGLIN_NEUTRAL).display(ModifierLevelDisplay.NO_LEVELS).build());
    addModifier(ModifierIds.wings,  StatBoostModifier.builder().addFlag(ModifiableArmorItem.ELYTRA).build());
    addModifier(ModifierIds.knockbackResistance, StatBoostModifier.builder().add(ToolStats.KNOCKBACK_RESISTANCE, 0.1f).build());
    // defense
    // TODO: floor?
    addModifier(ModifierIds.revitalizing, StatBoostModifier.builder().attribute("tconstruct.modifier.revitalizing", Attributes.MAX_HEALTH, Operation.ADDITION, 2, armorSlots).build());
    // helmet
    addModifier(ModifierIds.respiration, new EnchantmentModifier(Enchantments.RESPIRATION, 1, ModifierLevelDisplay.DEFAULT));
    // chestplate
    addModifier(ModifierIds.strength, StatBoostModifier.builder().attribute("tconstruct.modifier.strength", Attributes.ATTACK_DAMAGE, Operation.MULTIPLY_TOTAL, 0.1f, armorSlots).build());
    addRedirect(id("armor_power"), redirect(ModifierIds.strength));
    // leggings
    addModifier(ModifierIds.pockets, new InventoryMenuModifier(18));
    addModifier(ModifierIds.toolBelt, new ToolBeltModifier(new int[] {4, 5, 6, 7, 8, 9}));
    addRedirect(id("pocket_chain"), redirect(TinkerModifiers.shieldStrap.getId()));
    addModifier(ModifierIds.stepUp, StatBoostModifier.builder().attribute("tconstruct.modifier.step_up", ForgeMod.STEP_HEIGHT_ADDITION.get(), Operation.ADDITION, 0.5f, armorSlots).build());
    addModifier(ModifierIds.speedy, StatBoostModifier.builder().attribute("tconstruct.modifier.speedy", Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_TOTAL, 0.1f, armorMainHand).build());
    // boots
    addModifier(ModifierIds.depthStrider, new EnchantmentModifier(Enchantments.DEPTH_STRIDER, 1, ModifierLevelDisplay.DEFAULT));

    // internal
    addModifier(ModifierIds.overslimeFriend, StatBoostModifier.builder().addFlag(OverslimeModifier.KEY_OVERSLIME_FRIEND).modifierDisplay(ModifierDisplay.NEVER).build());
    addModifier(ModifierIds.fastUseItem, StatBoostModifier.builder().addFlag(IModifiable.FAST_USE_ITEM).modifierDisplay(ModifierDisplay.NEVER).build());

    // traits - tier 1
    addModifier(ModifierIds.stringy, new Modifier());
    addModifier(ModifierIds.flexible, StatBoostModifier.builder().multiplyBase(ToolStats.VELOCITY, 0.1f).multiplyAll(ToolStats.PROJECTILE_DAMAGE, -0.1f).build());
    // traits - tier 2
    addModifier(ModifierIds.sturdy, StatBoostModifier.builder().multiplyBase(ToolStats.DURABILITY, 0.15f).build());
    addModifier(ModifierIds.scorching, new ConditionalDamageModifier(LivingEntityPredicate.ON_FIRE, 2f));
    // traits - tier 2 compat
    addModifier(ModifierIds.lustrous, new Modifier());
    addModifier(ModifierIds.sharpweight, StatBoostModifier.builder()
      .multiplyBase(ToolStats.MINING_SPEED, 0.1f)
      .multiplyBase(ToolStats.DRAW_SPEED, 0.15f)
      .attribute("tconstruct.modifier.sharpweight", Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_BASE, -0.1f, handSlots)
      .build());
    addModifier(ModifierIds.heavy, StatBoostModifier.builder()
      .multiplyBase(ToolStats.ATTACK_DAMAGE, 0.1f)
      .multiplyBase(ToolStats.PROJECTILE_DAMAGE, 0.1f)
      .attribute("tconstruct.modifier.heavy", Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_BASE, -0.1f, handSlots)
      .build());
    addModifier(ModifierIds.featherweight, StatBoostModifier.builder()
      .multiplyBase(ToolStats.DRAW_SPEED, 0.07f)
      .multiplyBase(ToolStats.ACCURACY, 0.07f)
      .build());

    // traits - tier 3
    addModifier(ModifierIds.crumbling, new ConditionalMiningSpeedModifier(BlockPredicate.REQUIRES_TOOL.inverted(), false, 0.5f));
    addModifier(ModifierIds.enhanced, ExtraModifier.builder(SlotType.UPGRADE).alwaysShow().display(ModifierLevelDisplay.DEFAULT).build());
    addRedirect(id("maintained_2"), redirect(TinkerModifiers.maintained.getId()));
    // traits - tier 3 nether
    addModifier(ModifierIds.lightweight, StatBoostModifier.builder()
      .multiplyBase(ToolStats.ATTACK_SPEED, 0.07f)
      .multiplyBase(ToolStats.MINING_SPEED, 0.07f)
      .multiplyBase(ToolStats.DRAW_SPEED, 0.05f)
      .multiplyBase(ToolStats.VELOCITY, 0.05f)
      .build());
    // traits - tier 3 compat
    addModifier(ModifierIds.ductile, StatBoostModifier.builder()
      .multiplyBase(ToolStats.DURABILITY,        0.04f)
      .multiplyBase(ToolStats.ATTACK_DAMAGE,     0.04f)
      .multiplyBase(ToolStats.MINING_SPEED,      0.04f)
      .multiplyBase(ToolStats.VELOCITY,          0.03f)
      .multiplyBase(ToolStats.PROJECTILE_DAMAGE, 0.03f)
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

  /** Short helper to get a modifier ID */
  private static ModifierId id(String name) {
    return new ModifierId(TConstruct.MOD_ID, name);
  }
}
