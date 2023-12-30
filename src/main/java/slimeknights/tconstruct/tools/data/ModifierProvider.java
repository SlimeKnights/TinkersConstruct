package slimeknights.tconstruct.tools.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraftforge.common.ToolActions;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.data.predicate.block.BlockPredicate;
import slimeknights.mantle.data.predicate.entity.LivingEntityPredicate;
import slimeknights.mantle.data.predicate.entity.MobTypePredicate;
import slimeknights.mantle.data.predicate.entity.TagEntityPredicate;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierProvider;
import slimeknights.tconstruct.library.json.RandomLevelingValue;
import slimeknights.tconstruct.library.json.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.library.json.predicate.damage.SourceMessagePredicate;
import slimeknights.tconstruct.library.json.predicate.entity.TinkerLivingEntityPredicate;
import slimeknights.tconstruct.library.json.predicate.item.ItemPredicate;
import slimeknights.tconstruct.library.json.predicate.item.ItemTagPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasModifierPredicate.ModifierCheck;
import slimeknights.tconstruct.library.json.predicate.tool.ItemToolPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.dynamic.ComposableModifier.TooltipDisplay;
import slimeknights.tconstruct.library.modifiers.dynamic.InventoryMenuModifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.BlockDamageSourceModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.MobDisguiseModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ExtinguishCampfireModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.IncrementalModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ReduceToolDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.RepairModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ToolActionTransformModule;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierSlotModule;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.modifiers.modules.build.SetStatModule;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.modifiers.modules.build.SwappableSlotModule;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileFlagModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.KnockbackModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.LootingModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.MeleeAttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.MobEffectModule;
import slimeknights.tconstruct.library.modifiers.modules.display.DurabilityBarColorModule;
import slimeknights.tconstruct.library.modifiers.modules.mining.ConditionalMiningSpeedModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay.UniqueForLevels;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ToolBeltModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;

import static slimeknights.tconstruct.common.TinkerTags.Items.ARMOR;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.LEVEL;
import static slimeknights.tconstruct.library.modifiers.modules.behavior.RepairModule.FACTOR;
import static slimeknights.tconstruct.library.modifiers.modules.combat.KnockbackModule.KNOCKBACK;

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
    buildModifier(ModifierIds.writable)   .tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL).addModule(UPGRADE);
    buildModifier(ModifierIds.recapitated).tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL).addModule(UPGRADE);
    buildModifier(ModifierIds.harmonious) .tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL).addModule(UPGRADE);
    buildModifier(ModifierIds.resurrected).tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL).addModule(UPGRADE);
    buildModifier(ModifierIds.gilded)     .tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL).addModule(new ModifierSlotModule(SlotType.UPGRADE, 2));
    buildModifier(ModifierIds.draconic)   .tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.SINGLE_LEVEL).addModule(new ModifierSlotModule(SlotType.ABILITY, 1));
    buildModifier(ModifierIds.rebalanced)
      .tooltipDisplay(TooltipDisplay.TINKER_STATION).levelDisplay(ModifierLevelDisplay.NO_LEVELS)
      .addModule(new SwappableSlotModule(1)).addModule(new SwappableSlotModule.BonusSlot(SlotType.ABILITY, SlotType.UPGRADE, -1));
    addRedirect(id("red_extra_upgrade"),   redirect(ModifierIds.writable));
    addRedirect(id("green_extra_upgrade"), redirect(ModifierIds.recapitated));
    addRedirect(id("blue_extra_upgrade"),  redirect(ModifierIds.harmonious));
    addRedirect(id("extra_ability"),       redirect(ModifierIds.draconic));

    // internal modifier migration
    addRedirect(id("shovel_flatten"), redirect(ModifierIds.pathing));
    addRedirect(id("axe_strip"), redirect(ModifierIds.stripping));
    addRedirect(id("hoe_till"), redirect(ModifierIds.tilling));
    addRedirect(id("firestarter_hidden"), redirect(TinkerModifiers.firestarter.getId()));

    // merged some armor modifiers
    addRedirect(id("haste_armor"), redirect(TinkerModifiers.haste.getId()));
    addRedirect(id("knockback_armor"), redirect(TinkerModifiers.knockback.getId()));

    // unarmed rework
    addRedirect(id("unarmed"), redirect(TinkerModifiers.ambidextrous.getId()));

    // tier upgrades
    // emerald
    buildModifier(ModifierIds.emerald)
      .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
      .addModule(new RarityModule(Rarity.UNCOMMON))
      .addModule(StatBoostModule.multiplyBase(ToolStats.DURABILITY).flat(0.5f))
      .addModule(RepairModule.builder().flat(0.5f))
      // armor
      .addModule(StatBoostModule.add(ToolStats.KNOCKBACK_RESISTANCE).flat(0.05f))
      // melee harvest
      .addModule(StatBoostModule.multiplyConditional(ToolStats.ATTACK_DAMAGE).flat(0.25f))
      .addModule(StatBoostModule.multiplyConditional(ToolStats.MINING_SPEED).flat(0.25f))
      .addModule(SetStatModule.set(ToolStats.HARVEST_TIER).value(Tiers.IRON))
      // ranged
      .addModule(StatBoostModule.add(ToolStats.ACCURACY).flat(0.1f));
    // diamond
    buildModifier(ModifierIds.diamond)
      .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
      .addModule(new RarityModule(Rarity.UNCOMMON))
      .addModule(StatBoostModule.add(ToolStats.DURABILITY).flat(500))
      // armor grants less durability boost
      .addModule(StatBoostModule.add(ToolStats.DURABILITY).tool(new ItemToolPredicate(new ItemTagPredicate(ARMOR))).flat(-250))
      .addModule(StatBoostModule.add(ToolStats.ARMOR).flat(1))
      // melee harvest
      .addModule(StatBoostModule.add(ToolStats.ATTACK_DAMAGE).flat(0.5f))
      .addModule(StatBoostModule.add(ToolStats.MINING_SPEED).flat(2))
      .addModule(SetStatModule.set(ToolStats.HARVEST_TIER).value(Tiers.DIAMOND))
      // ranged
      .addModule(StatBoostModule.add(ToolStats.PROJECTILE_DAMAGE).flat(0.5f));
    // netherite
    buildModifier(ModifierIds.netherite)
      .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
      .addModule(new RarityModule(Rarity.RARE))
      .addModule(new VolatileFlagModule(IModifiable.INDESTRUCTIBLE_ENTITY))
      .addModule(StatBoostModule.multiplyBase(ToolStats.DURABILITY).flat(0.2f))
      // armor
      .addModule(StatBoostModule.add(ToolStats.ARMOR_TOUGHNESS).flat(1))
      .addModule(StatBoostModule.add(ToolStats.KNOCKBACK_RESISTANCE).flat(0.05f))
      // melee harvest
      .addModule(StatBoostModule.multiplyBase(ToolStats.ATTACK_DAMAGE).flat(0.2f))
      .addModule(StatBoostModule.multiplyBase(ToolStats.MINING_SPEED).flat(0.25f))
      .addModule(SetStatModule.set(ToolStats.HARVEST_TIER).value(Tiers.NETHERITE))
      // ranged
      .addModule(StatBoostModule.multiplyBase(ToolStats.VELOCITY).flat(0.1f));

    // general
    buildModifier(ModifierIds.worldbound).addModule(new VolatileFlagModule(IModifiable.INDESTRUCTIBLE_ENTITY)).addModule(new RarityModule(Rarity.UNCOMMON)).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    buildModifier(ModifierIds.shiny).addModule(new VolatileFlagModule(IModifiable.SHINY)).addModule(new RarityModule(Rarity.EPIC)).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    // general abilities
    buildModifier(ModifierIds.reach)
      .addModule(IncrementalModule.RECIPE_CONTROLLED)
      .addModule(new AttributeModule("tconstruct.modifier.reach", ForgeMod.REACH_DISTANCE.get(), Operation.ADDITION, 1, EquipmentSlot.values()))
      .addModule(new AttributeModule("tconstruct.modifier.range", ForgeMod.ATTACK_RANGE.get(), Operation.ADDITION, 1, EquipmentSlot.values()));
    IJsonPredicate<IToolContext> noUnbreakable = new HasModifierPredicate(TinkerModifiers.unbreakable.getId(), ModifierCheck.ALL).inverted();
    buildModifier(ModifierIds.reinforced)
      .addModule(IncrementalModule.RECIPE_CONTROLLED)
      // level 0 to 5: 0.025 * LEVEL * (11 - LEVEL)
      .addModule(ReduceToolDamageModule.builder().tool(noUnbreakable).maxLevel(5).formula()
                                       .constant(0.025f).variable(LEVEL).multiply() // 0.025 * level
                                       .constant(11).variable(LEVEL).subtract()     // 11 - level
                                       .multiply().build())
      // level 6+: 0.5 + level * 0.05
      .addModule(ReduceToolDamageModule.builder().tool(noUnbreakable).minLevel(6).amount(0.5f, 0.05f));
    // unbreakable priority is after overslime but before standard modifiers like dense
    buildModifier(TinkerModifiers.unbreakable).levelDisplay(ModifierLevelDisplay.NO_LEVELS).priority(125).addModule(new DurabilityBarColorModule(0xffffff)).addModule(ReduceToolDamageModule.builder().flat(1.0f));

    // loot
    buildModifier(TinkerModifiers.silky).levelDisplay(ModifierLevelDisplay.NO_LEVELS).addModule(new EnchantmentModule.Harvest(Enchantments.SILK_TOUCH));
    EnchantmentModule.Harvest FORTUNE = new EnchantmentModule.Harvest(Enchantments.BLOCK_FORTUNE);
    LootingModule LOOTING = new LootingModule(1);
    buildModifier(ModifierIds.luck).levelDisplay(new UniqueForLevels(3)).addModule(FORTUNE).addModule(LOOTING);
    buildModifier(ModifierIds.fortune).addModule(FORTUNE);
    buildModifier(ModifierIds.looting).addModule(LOOTING);


    /// attack
    buildModifier(TinkerModifiers.knockback)
      // do not boost unarmed attacks twice, thats a bit too much knockback for the cost
      .addModule(KnockbackModule.builder().tool(new ItemToolPredicate(new ItemTagPredicate(TinkerTags.Items.UNARMED)).inverted()).eachLevel(0.5f))
      .addModule(new AttributeModule("tconstruct.modifier.knockback", Attributes.ATTACK_KNOCKBACK, Operation.ADDITION, 1, ModifiableArmorMaterial.ARMOR_SLOTS));
    buildModifier(TinkerModifiers.padded)
      .priority(75) // run after knockback
      .addModule(KnockbackModule.builder().formula()
        .variable(KNOCKBACK)
        .constant(2).variable(LEVEL).power() // 2^LEVEL
        .divide().build()); // KNOCKBACK / 2^LEVEL
    buildModifier(ModifierIds.sticky)
      .addModule(IncrementalModule.RECIPE_CONTROLLED)
      .addModule(MobEffectModule.builder(MobEffects.MOVEMENT_SLOWDOWN).level(RandomLevelingValue.perLevel(0, 0.5f)).time(RandomLevelingValue.random(20, 10)).build());

    // damage boost
    // vanilla give +1, 1.5, 2, 2.5, 3, but that is low
    // we instead do +0.75, +1.5, +2.25, +3, +3.75
    UniqueForLevels uniqueForFive = new UniqueForLevels(5);
    buildModifier(ModifierIds.sharpness).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(StatBoostModule.add(ToolStats.ATTACK_DAMAGE).eachLevel(0.75f)).levelDisplay(uniqueForFive);
    buildModifier(ModifierIds.swiftstrike).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(StatBoostModule.multiplyBase(ToolStats.ATTACK_SPEED).eachLevel(0.05f)).levelDisplay(uniqueForFive);
    buildModifier(ModifierIds.smite).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(new ConditionalDamageModule(new MobTypePredicate(MobType.UNDEAD), 2.0f));
    buildModifier(ModifierIds.antiaquatic).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(new ConditionalDamageModule(new MobTypePredicate(MobType.WATER),  2.0f));
    buildModifier(ModifierIds.cooling).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(new ConditionalDamageModule(LivingEntityPredicate.FIRE_IMMUNE,    1.6f));
    IJsonPredicate<LivingEntity> baneSssssPredicate = LivingEntityPredicate.OR.create(new MobTypePredicate(MobType.ARTHROPOD), new TagEntityPredicate(TinkerTags.EntityTypes.CREEPERS));
    buildModifier(ModifierIds.baneOfSssss)
      .addModule(IncrementalModule.RECIPE_CONTROLLED)
      .addModule(new ConditionalDamageModule(baneSssssPredicate, 2.0f))
      .addModule(MobEffectModule.builder(MobEffects.MOVEMENT_SLOWDOWN).level(RandomLevelingValue.flat(4)).time(RandomLevelingValue.random(20, 10)).entity(baneSssssPredicate).build(), TinkerHooks.MELEE_HIT);
    buildModifier(ModifierIds.killager).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(new ConditionalDamageModule(LivingEntityPredicate.OR.create(
      new MobTypePredicate(MobType.ILLAGER),
      new TagEntityPredicate(TinkerTags.EntityTypes.VILLAGERS)
    ), 2.0f));
    addRedirect(id("fractured"), redirect(ModifierIds.sharpness));
    buildModifier(ModifierIds.pierce)
      // less than sharpness, but pierces 1 armor
      .addModule(StatBoostModule.add(ToolStats.ATTACK_DAMAGE).eachLevel(0.5f))
      .addModule(new MeleeAttributeModule(TConstruct.prefix("modifier.pierce"), Attributes.ARMOR, Operation.ADDITION, -1, true))
      // use a mob effect to make this work on ranged, to ensure it automatically cancels
      .addModule(new MobEffectModule(LivingEntityPredicate.ANY, TinkerModifiers.pierceEffect.get(), RandomLevelingValue.perLevel(0, 1), RandomLevelingValue.flat(2)), TinkerHooks.PROJECTILE_LAUNCH, TinkerHooks.PROJECTILE_HIT);

    // ranged
    buildModifier(ModifierIds.power).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(StatBoostModule.add(ToolStats.PROJECTILE_DAMAGE).eachLevel(0.5f));
    buildModifier(ModifierIds.quickCharge).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(StatBoostModule.multiplyBase(ToolStats.DRAW_SPEED).eachLevel(0.25f));
    buildModifier(ModifierIds.trueshot).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(StatBoostModule.add(ToolStats.ACCURACY).eachLevel(0.1f));
    buildModifier(ModifierIds.blindshot).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(StatBoostModule.add(ToolStats.ACCURACY).eachLevel(-0.1f));

    // armor
    buildModifier(TinkerModifiers.golden).addModule(new VolatileFlagModule(ModifiableArmorItem.PIGLIN_NEUTRAL)).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    buildModifier(ModifierIds.wings).addModule(new VolatileFlagModule(ModifiableArmorItem.ELYTRA)).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    buildModifier(ModifierIds.knockbackResistance).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(StatBoostModule.add(ToolStats.KNOCKBACK_RESISTANCE).eachLevel(0.1f));

    // defense
    // TODO: floor?
    buildModifier(ModifierIds.revitalizing).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(new AttributeModule("tconstruct.modifier.revitalizing", Attributes.MAX_HEALTH, Operation.ADDITION, 2, armorSlots));
    // protection
    buildModifier(ModifierIds.protection).addModule(ProtectionModule.source(DamageSourcePredicate.CAN_PROTECT).eachLevel(1.25f));
    buildModifier(ModifierIds.fireProtection)
      .addModule(new EnchantmentModule.Constant(Enchantments.FIRE_PROTECTION))
      .addModule(ProtectionModule.source(DamageSourcePredicate.AND.create(DamageSourcePredicate.CAN_PROTECT, DamageSourcePredicate.FIRE)).subtract(Enchantments.FIRE_PROTECTION).eachLevel(2.5f));
    buildModifier(ModifierIds.turtleShell)
      .addModule(new AttributeModule("tconstruct.modifiers.turtle_shell", ForgeMod.SWIM_SPEED.get(), Operation.MULTIPLY_TOTAL, 0.05f, ModifiableArmorMaterial.ARMOR_SLOTS))
      .addModule(ProtectionModule.source(DamageSourcePredicate.CAN_PROTECT)
                                 .tool(new ItemToolPredicate(ItemPredicate.OR.create(new ItemTagPredicate(TinkerTags.Items.HELMETS), new ItemTagPredicate(TinkerTags.Items.CHESTPLATES))))
                                 .entity(TinkerLivingEntityPredicate.EYES_IN_WATER).eachLevel(2.5f))
      .addModule(ProtectionModule.source(DamageSourcePredicate.CAN_PROTECT)
                                 .tool(new ItemToolPredicate(ItemPredicate.OR.create(new ItemTagPredicate(TinkerTags.Items.LEGGINGS), new ItemTagPredicate(TinkerTags.Items.BOOTS))))
                                 .entity(TinkerLivingEntityPredicate.FEET_IN_WATER).eachLevel(2.5f));
    // helmet
    buildModifier(ModifierIds.respiration).addModule(new EnchantmentModule.Constant(Enchantments.RESPIRATION));
    buildModifier(ModifierIds.aquaAffinity).addModule(new EnchantmentModule.Constant(Enchantments.AQUA_AFFINITY)).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
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
    buildModifier(ModifierIds.featherFalling).addModule(ProtectionModule.source(DamageSourcePredicate.FALL).eachLevel(3.75f));
    buildModifier(ModifierIds.longFall).levelDisplay(ModifierLevelDisplay.NO_LEVELS).addModule(new BlockDamageSourceModule(DamageSourcePredicate.FALL));

    // interaction
    buildModifier(ModifierIds.pathing)
      .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
      .addModule(ShowOffhandModule.INSTANCE)
      .addModule(ExtinguishCampfireModule.INSTANCE)
      .addModule(new ToolActionTransformModule(ToolActions.SHOVEL_FLATTEN, SoundEvents.SHOVEL_FLATTEN, true));
    buildModifier(ModifierIds.stripping)
      .addModule(ShowOffhandModule.INSTANCE)
      .addModule(new ToolActionTransformModule(ToolActions.AXE_STRIP, SoundEvents.AXE_STRIP, false))
      .addModule(new ToolActionTransformModule(ToolActions.AXE_SCRAPE, SoundEvents.AXE_SCRAPE, false, 3005))
      .addModule(new ToolActionTransformModule(ToolActions.AXE_WAX_OFF, SoundEvents.AXE_WAX_OFF, false, 3004));
    buildModifier(ModifierIds.tilling)
      .addModule(ShowOffhandModule.INSTANCE)
      .addModule(new ToolActionTransformModule(ToolActions.HOE_TILL, SoundEvents.HOE_TILL, false));
    addRedirect(id("axe_scrape"), redirect(ModifierIds.stripping));
    addRedirect(id("axe_wax_off"), redirect(ModifierIds.stripping));

    // internal
    buildModifier(ModifierIds.overslimeFriend).addModule(new VolatileFlagModule(OverslimeModifier.KEY_OVERSLIME_FRIEND)).tooltipDisplay(TooltipDisplay.NEVER);
    buildModifier(ModifierIds.snowBoots).addModule(new VolatileFlagModule(ModifiableArmorItem.SNOW_BOOTS)).tooltipDisplay(TooltipDisplay.NEVER);

    // traits - tier 1
    buildModifier(ModifierIds.cultivated).addModule(RepairModule.builder().eachLevel(0.5f));
    addModifier(ModifierIds.stringy, new Modifier());
    buildModifier(ModifierIds.flexible).addModule(StatBoostModule.add(ToolStats.VELOCITY).eachLevel(0.1f))
                                       .addModule(StatBoostModule.multiplyAll(ToolStats.PROJECTILE_DAMAGE).eachLevel(-0.1f));
    // traits - tier 2
    buildModifier(ModifierIds.sturdy).addModule(StatBoostModule.multiplyBase(ToolStats.DURABILITY).eachLevel(0.15f));
    buildModifier(ModifierIds.scorching).addModule(new ConditionalDamageModule(LivingEntityPredicate.ON_FIRE, 2f));
    // traits - tier 2 compat
    addModifier(ModifierIds.lustrous, new Modifier());
    buildModifier(ModifierIds.sharpweight)
      .addModule(StatBoostModule.multiplyBase(ToolStats.MINING_SPEED).eachLevel(0.1f))
      .addModule(StatBoostModule.multiplyBase(ToolStats.DRAW_SPEED).eachLevel(0.15f))
      .addModule(new AttributeModule("tconstruct.modifier.sharpweight", Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_BASE, -0.1f, handSlots));
    buildModifier(ModifierIds.heavy)
      .addModule(StatBoostModule.multiplyBase(ToolStats.ATTACK_DAMAGE).eachLevel(0.1f))
      .addModule(StatBoostModule.multiplyBase(ToolStats.PROJECTILE_DAMAGE).eachLevel(0.1f))
      .addModule(new AttributeModule("tconstruct.modifier.heavy", Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_BASE, -0.1f, handSlots));
    buildModifier(ModifierIds.featherweight)
      .addModule(StatBoostModule.multiplyBase(ToolStats.DRAW_SPEED).eachLevel(0.07f))
      .addModule(StatBoostModule.multiplyBase(ToolStats.ACCURACY).eachLevel(0.07f));
    buildModifier(ModifierIds.dense)
      // from 0 to 5, repair formula is FACTOR * (1 - 0.025 * LEVEL * (11 - LEVEL))
      .addModule(RepairModule.builder().maxLevel(5).formula()
          .variable(FACTOR)
            .constant(1)
              .constant(0.025f).variable(LEVEL).multiply()
              .constant(11).variable(LEVEL).subtract()
            .multiply()
          .subtract()
        .multiply().build())
      // at 6+, repair formula is FACTOR * (0.5 - LEVEL * 0.05), simple formula supports below
      .addModule(RepairModule.builder().minLevel(6).amount(-0.5f, -0.05f))
      // durability formula is 1 - 1/(1.5^LEVEL)
      .addModule(ReduceToolDamageModule.builder().formula()
          .constant(1)
            .constant(1)
            .constant(1.5f).variable(LEVEL).power()
          .divide()
        .subtract().build());

    // traits - tier 3
    buildModifier(ModifierIds.crumbling).addModule(new ConditionalMiningSpeedModule(BlockPredicate.REQUIRES_TOOL.inverted(), false, 0.5f));
    buildModifier(ModifierIds.enhanced).priority(60).addModule(UPGRADE);
    addRedirect(id("maintained_2"), redirect(TinkerModifiers.maintained.getId()));
    // traits - tier 3 nether
    buildModifier(ModifierIds.lightweight)
      .addModule(StatBoostModule.multiplyBase(ToolStats.ATTACK_SPEED).eachLevel(0.07f))
      .addModule(StatBoostModule.multiplyBase(ToolStats.MINING_SPEED).eachLevel(0.07f))
      .addModule(StatBoostModule.multiplyBase(ToolStats.DRAW_SPEED).eachLevel(0.03f))
      .addModule(StatBoostModule.multiplyBase(ToolStats.VELOCITY).eachLevel(0.03f));
    // traits - tier 3 compat
    buildModifier(ModifierIds.ductile)
      .addModule(StatBoostModule.multiplyBase(ToolStats.DURABILITY).eachLevel(0.04f))
      .addModule(StatBoostModule.multiplyBase(ToolStats.ATTACK_DAMAGE).eachLevel(0.04f))
      .addModule(StatBoostModule.multiplyBase(ToolStats.MINING_SPEED).eachLevel(0.04f))
      .addModule(StatBoostModule.multiplyBase(ToolStats.VELOCITY).eachLevel(0.03f))
      .addModule(StatBoostModule.multiplyBase(ToolStats.PROJECTILE_DAMAGE).eachLevel(0.03f));

    // mob disguise
    buildModifier(ModifierIds.creeperDisguise        ).addModule(new MobDisguiseModule(EntityType.CREEPER));
    buildModifier(ModifierIds.endermanDisguise       ).addModule(new MobDisguiseModule(EntityType.ENDERMAN));
    buildModifier(ModifierIds.skeletonDisguise       ).addModule(new MobDisguiseModule(EntityType.SKELETON));
    buildModifier(ModifierIds.strayDisguise          ).addModule(new MobDisguiseModule(EntityType.STRAY));
    buildModifier(ModifierIds.witherSkeletonDisguise ).addModule(new MobDisguiseModule(EntityType.WITHER_SKELETON));
    buildModifier(ModifierIds.spiderDisguise         ).addModule(new MobDisguiseModule(EntityType.SPIDER));
    buildModifier(ModifierIds.caveSpiderDisguise     ).addModule(new MobDisguiseModule(EntityType.CAVE_SPIDER));
    buildModifier(ModifierIds.zombieDisguise         ).addModule(new MobDisguiseModule(EntityType.ZOMBIE));
    buildModifier(ModifierIds.huskDisguise           ).addModule(new MobDisguiseModule(EntityType.HUSK));
    buildModifier(ModifierIds.drownedDisguise        ).addModule(new MobDisguiseModule(EntityType.DROWNED));
    buildModifier(ModifierIds.blazeDisguise          ).addModule(new MobDisguiseModule(EntityType.BLAZE));
    buildModifier(ModifierIds.piglinDisguise         ).addModule(new MobDisguiseModule(EntityType.PIGLIN));
    buildModifier(ModifierIds.piglinBruteDisguise    ).addModule(new MobDisguiseModule(EntityType.PIGLIN_BRUTE));
    buildModifier(ModifierIds.zombifiedPiglinDisguise).addModule(new MobDisguiseModule(EntityType.ZOMBIFIED_PIGLIN));
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
