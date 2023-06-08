package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
import slimeknights.tconstruct.library.json.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.json.predicate.block.BlockPredicate;
import slimeknights.tconstruct.library.json.predicate.entity.LivingEntityPredicate;
import slimeknights.tconstruct.library.json.predicate.entity.MobTypePredicate;
import slimeknights.tconstruct.library.json.predicate.entity.TagEntityPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.dynamic.ComposableModifier.TooltipDisplay;
import slimeknights.tconstruct.library.modifiers.dynamic.ConditionalMiningSpeedModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.InventoryMenuModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.MobDisguiseModifier;
import slimeknights.tconstruct.library.modifiers.modules.AttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.ConditionalDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.EnchantmentModule;
import slimeknights.tconstruct.library.modifiers.modules.IncrementalModule;
import slimeknights.tconstruct.library.modifiers.modules.LootingModule;
import slimeknights.tconstruct.library.modifiers.modules.MobEffectModule;
import slimeknights.tconstruct.library.modifiers.modules.ModifierSlotModule;
import slimeknights.tconstruct.library.modifiers.modules.RarityModule;
import slimeknights.tconstruct.library.modifiers.modules.SwappableSlotModule;
import slimeknights.tconstruct.library.modifiers.modules.ToolStatModule;
import slimeknights.tconstruct.library.modifiers.modules.VolatileFlagModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay.UniqueForLevels;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.ScalingValue;
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
    ModifierSlotModule UPGRADE = new ModifierSlotModule(SlotType.UPGRADE);
    buildModifier(ModifierIds.writable)   .priority(50).tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL).addModule(UPGRADE);
    buildModifier(ModifierIds.recapitated).priority(50).tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL).addModule(UPGRADE);
    buildModifier(ModifierIds.harmonious) .priority(50).tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL).addModule(UPGRADE);
    buildModifier(ModifierIds.resurrected).priority(50).tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL).addModule(UPGRADE);
    buildModifier(ModifierIds.gilded)     .priority(50).tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL).addModule(new ModifierSlotModule(SlotType.UPGRADE, 2));
    buildModifier(ModifierIds.draconic)   .priority(50).tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL).addModule(new ModifierSlotModule(SlotType.ABILITY, 1));
    buildModifier(ModifierIds.rebalanced)
      .priority(50).tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.NO_LEVELS)
      .addModule(new SwappableSlotModule(1)).addModule(new SwappableSlotModule.BonusSlot(SlotType.ABILITY, SlotType.UPGRADE, -1));
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
    buildModifier(ModifierIds.emerald)
      .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
      .addModule(new RarityModule(Rarity.UNCOMMON))
      .addModule(ToolStatModule.multiplyBase(ToolStats.DURABILITY, 0.5f))
      // armor
      .addModule(ToolStatModule.add(ToolStats.KNOCKBACK_RESISTANCE, 0.05f))
      // melee harvest
      .addModule(ToolStatModule.multiplyConditional(ToolStats.ATTACK_DAMAGE, 0.25f))
      .addModule(ToolStatModule.multiplyConditional(ToolStats.MINING_SPEED, 0.25f))
      .addModule(ToolStatModule.update(ToolStats.HARVEST_TIER, Tiers.IRON))
      // ranged
      .addModule(ToolStatModule.add(ToolStats.ACCURACY, 0.1f));
    // diamond
    buildModifier(ModifierIds.diamond)
      .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
      .addModule(new RarityModule(Rarity.UNCOMMON))
      .addModule(ToolStatModule.add(ToolStats.DURABILITY, 500))
      // armor grants less durability boost
      .addModule(ToolStatModule.add(ToolStats.DURABILITY, -250, ARMOR))
      .addModule(ToolStatModule.add(ToolStats.ARMOR, 1))
      // melee harvest
      .addModule(ToolStatModule.add(ToolStats.ATTACK_DAMAGE, 0.5f))
      .addModule(ToolStatModule.add(ToolStats.MINING_SPEED, 2))
      .addModule(ToolStatModule.update(ToolStats.HARVEST_TIER, Tiers.DIAMOND))
      // ranged
      .addModule(ToolStatModule.add(ToolStats.PROJECTILE_DAMAGE, 0.5f));
    // netherite
    buildModifier(ModifierIds.netherite)
      .levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL)
      .addModule(new RarityModule(Rarity.RARE))
      .addModule(new VolatileFlagModule(IModifiable.INDESTRUCTIBLE_ENTITY))
      .addModule(ToolStatModule.multiplyBase(ToolStats.DURABILITY, 0.2f))
      // armor
      .addModule(ToolStatModule.add(ToolStats.ARMOR_TOUGHNESS, 1))
      .addModule(ToolStatModule.add(ToolStats.KNOCKBACK_RESISTANCE, 0.05f))
      // melee harvest
      .addModule(ToolStatModule.multiplyBase(ToolStats.ATTACK_DAMAGE, 0.2f))
      .addModule(ToolStatModule.multiplyBase(ToolStats.MINING_SPEED, 0.25f))
      .addModule(ToolStatModule.update(ToolStats.HARVEST_TIER, Tiers.NETHERITE))
      // ranged
      .addModule(ToolStatModule.multiplyBase(ToolStats.VELOCITY, 0.1f));

    // general
    buildModifier(ModifierIds.worldbound).addModule(new VolatileFlagModule(IModifiable.INDESTRUCTIBLE_ENTITY)).addModule(new RarityModule(Rarity.UNCOMMON)).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    buildModifier(ModifierIds.shiny).addModule(new VolatileFlagModule(IModifiable.SHINY)).addModule(new RarityModule(Rarity.EPIC)).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    // general abilities
    buildModifier(ModifierIds.reach)
      .addModule(IncrementalModule.RECIPE_CONTROLLED)
      .addModule(new AttributeModule("tconstruct.modifier.reach", ForgeMod.REACH_DISTANCE.get(), Operation.ADDITION, 1, EquipmentSlot.MAINHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET))
      .addModule(new AttributeModule("tconstruct.modifier.range", ForgeMod.ATTACK_RANGE.get(), Operation.ADDITION, 1, EquipmentSlot.MAINHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));

    // loot
    buildModifier(TinkerModifiers.silky).levelDisplay(ModifierLevelDisplay.NO_LEVELS).addModule(new EnchantmentModule.Harvest(Enchantments.SILK_TOUCH));
    EnchantmentModule.Harvest FORTUNE = new EnchantmentModule.Harvest(Enchantments.BLOCK_FORTUNE);
    LootingModule LOOTING = new LootingModule(1);
    buildModifier(ModifierIds.luck).levelDisplay(new UniqueForLevels(3)).addModule(FORTUNE).addModule(LOOTING);
    buildModifier(ModifierIds.fortune).addModule(FORTUNE);
    buildModifier(ModifierIds.looting).addModule(LOOTING);

    /// attack
    buildModifier(ModifierIds.sticky)
      .addModule(IncrementalModule.RECIPE_CONTROLLED)
      .addModule(MobEffectModule.builder(MobEffects.MOVEMENT_SLOWDOWN).level(ScalingValue.leveling(0, 0.5f)).time(ScalingValue.random(20, 10)).build());

    // damage boost
    // vanilla give +1, 1.5, 2, 2.5, 3, but that is low
    // we instead do +0.75, +1.5, +2.25, +3, +3.75
    UniqueForLevels uniqueForFive = new UniqueForLevels(5);
    buildModifier(ModifierIds.sharpness).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(ToolStatModule.add(ToolStats.ATTACK_DAMAGE, 0.75f)).levelDisplay(uniqueForFive);
    buildModifier(ModifierIds.swiftstrike).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(ToolStatModule.multiplyBase(ToolStats.ATTACK_SPEED, 0.05f)).levelDisplay(uniqueForFive);
    buildModifier(ModifierIds.smite).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(new ConditionalDamageModule(new MobTypePredicate(MobType.UNDEAD), 2.0f));
    buildModifier(ModifierIds.antiaquatic).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(new ConditionalDamageModule(new MobTypePredicate(MobType.WATER),  2.0f));
    buildModifier(ModifierIds.cooling).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(new ConditionalDamageModule(LivingEntityPredicate.FIRE_IMMUNE,    1.6f));
    IJsonPredicate<LivingEntity> baneSssssPredicate = LivingEntityPredicate.OR.create(new MobTypePredicate(MobType.ARTHROPOD), new TagEntityPredicate(TinkerTags.EntityTypes.CREEPERS));
    buildModifier(ModifierIds.baneOfSssss)
      .addModule(IncrementalModule.RECIPE_CONTROLLED)
      .addModule(new ConditionalDamageModule(baneSssssPredicate, 2.0f))
      .addModule(MobEffectModule.builder(MobEffects.MOVEMENT_SLOWDOWN).level(ScalingValue.flat(4)).time(ScalingValue.random(20, 10)).entity(baneSssssPredicate).build(), TinkerHooks.MELEE_HIT);
    buildModifier(ModifierIds.killager).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(new ConditionalDamageModule(LivingEntityPredicate.OR.create(
      new MobTypePredicate(MobType.ILLAGER),
      new TagEntityPredicate(TinkerTags.EntityTypes.VILLAGERS)
    ), 2.0f));
    addRedirect(id("fractured"), redirect(ModifierIds.sharpness));

    // ranged
    buildModifier(ModifierIds.power).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(ToolStatModule.add(ToolStats.PROJECTILE_DAMAGE, 0.5f));
    buildModifier(ModifierIds.quickCharge).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(ToolStatModule.multiplyBase(ToolStats.DRAW_SPEED, 0.25f));
    buildModifier(ModifierIds.trueshot).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(ToolStatModule.add(ToolStats.ACCURACY, 0.1f));
    buildModifier(ModifierIds.blindshot).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(ToolStatModule.add(ToolStats.ACCURACY, -0.1f));

    // armor
    buildModifier(TinkerModifiers.golden).addModule(new VolatileFlagModule(ModifiableArmorItem.PIGLIN_NEUTRAL)).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    buildModifier(ModifierIds.wings).addModule(new VolatileFlagModule(ModifiableArmorItem.ELYTRA)).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    buildModifier(ModifierIds.knockbackResistance).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(ToolStatModule.add(ToolStats.KNOCKBACK_RESISTANCE, 0.1f));
    // defense
    // TODO: floor?
    buildModifier(ModifierIds.revitalizing).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(new AttributeModule("tconstruct.modifier.revitalizing", Attributes.MAX_HEALTH, Operation.ADDITION, 2, armorSlots));
    // helmet
    buildModifier(ModifierIds.respiration).addModule(new EnchantmentModule.Constant(Enchantments.RESPIRATION));
    // chestplate
    buildModifier(ModifierIds.strength)
      .addModule(IncrementalModule.RECIPE_CONTROLLED)
      .addModule(new AttributeModule("tconstruct.modifier.strength", Attributes.ATTACK_DAMAGE, Operation.MULTIPLY_TOTAL, 0.1f, armorSlots));
    addRedirect(id("armor_power"), redirect(ModifierIds.strength));
    // leggings
    addModifier(ModifierIds.pockets, new InventoryMenuModifier(18));
    addModifier(ModifierIds.toolBelt, new ToolBeltModifier(new int[] {4, 5, 6, 7, 8, 9}));
    addRedirect(id("pocket_chain"), redirect(TinkerModifiers.shieldStrap.getId()));
    buildModifier(ModifierIds.stepUp).addModule(new AttributeModule("tconstruct.modifier.step_up", ForgeMod.STEP_HEIGHT_ADDITION.get(), Operation.ADDITION, 0.5f, armorSlots));
    buildModifier(ModifierIds.speedy).addModule(new AttributeModule("tconstruct.modifier.speedy", Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_TOTAL, 0.1f, armorMainHand));
    // boots
    buildModifier(ModifierIds.depthStrider).addModule(new EnchantmentModule.Constant(Enchantments.DEPTH_STRIDER));

    // internal
    buildModifier(ModifierIds.overslimeFriend).addModule(new VolatileFlagModule(OverslimeModifier.KEY_OVERSLIME_FRIEND)).tooltipDisplay(TooltipDisplay.NEVER);
    buildModifier(ModifierIds.snowBoots).addModule(new VolatileFlagModule(ModifiableArmorItem.SNOW_BOOTS)).tooltipDisplay(TooltipDisplay.NEVER);

    // traits - tier 1
    addModifier(ModifierIds.stringy, new Modifier());
    buildModifier(ModifierIds.flexible).addModule(ToolStatModule.add(ToolStats.VELOCITY, 0.1f)).addModule(ToolStatModule.multiplyAll(ToolStats.PROJECTILE_DAMAGE, -0.1f));
    // traits - tier 2
    buildModifier(ModifierIds.sturdy).addModule(ToolStatModule.multiplyBase(ToolStats.DURABILITY, 0.15f));
    buildModifier(ModifierIds.scorching).addModule(new ConditionalDamageModule(LivingEntityPredicate.ON_FIRE, 2f));
    // traits - tier 2 compat
    addModifier(ModifierIds.lustrous, new Modifier());
    buildModifier(ModifierIds.sharpweight)
      .addModule(ToolStatModule.multiplyBase(ToolStats.MINING_SPEED, 0.1f))
      .addModule(ToolStatModule.multiplyBase(ToolStats.DRAW_SPEED, 0.15f))
      .addModule(new AttributeModule("tconstruct.modifier.sharpweight", Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_BASE, -0.1f, handSlots));
    buildModifier(ModifierIds.heavy)
      .addModule(ToolStatModule.multiplyBase(ToolStats.ATTACK_DAMAGE, 0.1f))
      .addModule(ToolStatModule.multiplyBase(ToolStats.PROJECTILE_DAMAGE, 0.1f))
      .addModule(new AttributeModule("tconstruct.modifier.heavy", Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_BASE, -0.1f, handSlots));
    buildModifier(ModifierIds.featherweight)
      .addModule(ToolStatModule.multiplyBase(ToolStats.DRAW_SPEED, 0.07f))
      .addModule(ToolStatModule.multiplyBase(ToolStats.ACCURACY, 0.07f));

    // traits - tier 3
    addModifier(ModifierIds.crumbling, new ConditionalMiningSpeedModifier(BlockPredicate.REQUIRES_TOOL.inverted(), false, 0.5f));
    buildModifier(ModifierIds.enhanced).priority(60).addModule(UPGRADE);
    addRedirect(id("maintained_2"), redirect(TinkerModifiers.maintained.getId()));
    // traits - tier 3 nether
    buildModifier(ModifierIds.lightweight)
      .addModule(ToolStatModule.multiplyBase(ToolStats.ATTACK_SPEED, 0.07f))
      .addModule(ToolStatModule.multiplyBase(ToolStats.MINING_SPEED, 0.07f))
      .addModule(ToolStatModule.multiplyBase(ToolStats.DRAW_SPEED, 0.03f))
      .addModule(ToolStatModule.multiplyBase(ToolStats.VELOCITY, 0.03f));
    // traits - tier 3 compat
    buildModifier(ModifierIds.ductile)
      .addModule(ToolStatModule.multiplyBase(ToolStats.DURABILITY,        0.04f))
      .addModule(ToolStatModule.multiplyBase(ToolStats.ATTACK_DAMAGE,     0.04f))
      .addModule(ToolStatModule.multiplyBase(ToolStats.MINING_SPEED,      0.04f))
      .addModule(ToolStatModule.multiplyBase(ToolStats.VELOCITY,          0.03f))
      .addModule(ToolStatModule.multiplyBase(ToolStats.PROJECTILE_DAMAGE, 0.03f));

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
