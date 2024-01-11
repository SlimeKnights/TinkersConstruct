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
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
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
import slimeknights.tconstruct.library.json.predicate.block.BlockPropertiesPredicate;
import slimeknights.tconstruct.library.json.predicate.damage.DamageSourcePredicate;
import slimeknights.tconstruct.library.json.predicate.damage.SourceMessagePredicate;
import slimeknights.tconstruct.library.json.predicate.entity.HasEnchantmentEntityPredicate;
import slimeknights.tconstruct.library.json.predicate.entity.TinkerLivingEntityPredicate;
import slimeknights.tconstruct.library.json.predicate.item.ItemPredicate;
import slimeknights.tconstruct.library.json.predicate.item.ItemTagPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.tool.HasModifierPredicate.ModifierCheck;
import slimeknights.tconstruct.library.json.predicate.tool.ItemToolPredicate;
import slimeknights.tconstruct.library.json.variable.block.BlockVariable;
import slimeknights.tconstruct.library.json.variable.entity.AttributeEntityVariable;
import slimeknights.tconstruct.library.json.variable.entity.ConditionalEntityVariable;
import slimeknights.tconstruct.library.json.variable.entity.EntityVariable;
import slimeknights.tconstruct.library.json.variable.melee.EntityMeleeVariable;
import slimeknights.tconstruct.library.json.variable.melee.EntityMeleeVariable.WhichEntity;
import slimeknights.tconstruct.library.json.variable.mining.BlockLightVariable;
import slimeknights.tconstruct.library.json.variable.mining.BlockMiningSpeedVariable;
import slimeknights.tconstruct.library.json.variable.mining.EntityMiningSpeedVariable;
import slimeknights.tconstruct.library.json.variable.mining.ToolMiningSpeedVariable;
import slimeknights.tconstruct.library.json.variable.stat.EntityConditionalStatVariable;
import slimeknights.tconstruct.library.json.variable.stat.ToolConditionalStatVariable;
import slimeknights.tconstruct.library.json.variable.tool.ToolStatVariable;
import slimeknights.tconstruct.library.json.variable.tool.ToolVariable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.dynamic.ComposableModifier.TooltipDisplay;
import slimeknights.tconstruct.library.modifiers.dynamic.InventoryMenuModifier;
import slimeknights.tconstruct.library.modifiers.modules.armor.BlockDamageSourceModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.CoverGroundWalkerModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.MobDisguiseModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.ReplaceBlockWalkerModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.ToolActionWalkerTransformModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ConditionalStatModule;
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
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
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
import static slimeknights.tconstruct.library.json.math.ModifierFormula.MULTIPLIER;
import static slimeknights.tconstruct.library.json.math.ModifierFormula.VALUE;
import static slimeknights.tconstruct.library.modifiers.modules.behavior.RepairModule.FACTOR;

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
      .addModule(AttributeModule.builder(ForgeMod.REACH_DISTANCE.get(), Operation.ADDITION).uniqueFrom(ModifierIds.reach).eachLevel(1))
      .addModule(AttributeModule.builder(ForgeMod.ATTACK_RANGE.get(), Operation.ADDITION).uniqueFrom(ModifierIds.reach).eachLevel(1));
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

    // harvest
    buildModifier(ModifierIds.blasting).addModule(
      ConditionalMiningSpeedModule.builder()
        .customVariable("resistance", new BlockMiningSpeedVariable(BlockVariable.BLAST_RESISTANCE, 3))
        .formula()
        .constant(3)
        .constant(6).customVariable("resistance").subtract() // (6 - resistance)
        .constant(1.5f)
        .divide() // above / 1.5
        .power() // 3^above
        .constant(10).min() // min(above, 10)
        .variable(LEVEL).multiply() // above * level
        .variable(MULTIPLIER).multiply() // above * multiplier
        .variable(VALUE).add() // above + newSpeed
        .build());
    buildModifier(ModifierIds.hydraulic).addModule(
      ConditionalMiningSpeedModule.builder()
        .customVariable("bonus", new EntityMiningSpeedVariable(new ConditionalEntityVariable(
          TinkerLivingEntityPredicate.EYES_IN_WATER,
          new ConditionalEntityVariable(new HasEnchantmentEntityPredicate(Enchantments.AQUA_AFFINITY), 8, 40),
          new ConditionalEntityVariable(TinkerLivingEntityPredicate.RAINING, 4, 0)
        ), 8)).formula()
        .variable(MULTIPLIER).customVariable("bonus").multiply()
        .variable(LEVEL).multiply()
        .variable(VALUE).add()
        .build());
    buildModifier(ModifierIds.lightspeed).addModule(
      ConditionalMiningSpeedModule.builder()
        .customVariable("light", new BlockLightVariable(LightLayer.BLOCK, 15))
        .formula()
        .constant(3)
        .customVariable("light").constant(5).subtract()
        .constant(5).divide()
        .power()
        .variable(LEVEL).multiply()
        .variable(MULTIPLIER).multiply()
        .variable(VALUE).add().build());


    // loot
    buildModifier(TinkerModifiers.silky).levelDisplay(ModifierLevelDisplay.NO_LEVELS).addModule(EnchantmentModule.harvest(Enchantments.SILK_TOUCH).build());
    EnchantmentModule FORTUNE = EnchantmentModule.harvest(Enchantments.BLOCK_FORTUNE).build();
    LootingModule LOOTING = new LootingModule(1);
    buildModifier(ModifierIds.luck).levelDisplay(new UniqueForLevels(3)).addModule(FORTUNE).addModule(LOOTING);
    buildModifier(ModifierIds.fortune).addModule(FORTUNE);
    buildModifier(ModifierIds.looting).addModule(LOOTING);


    /// attack
    buildModifier(TinkerModifiers.knockback)
      // do not boost unarmed attacks twice, thats a bit too much knockback for the cost
      .addModule(KnockbackModule.builder().tool(new ItemToolPredicate(new ItemTagPredicate(TinkerTags.Items.UNARMED)).inverted()).eachLevel(0.5f))
      .addModule(AttributeModule.builder(Attributes.ATTACK_KNOCKBACK, Operation.ADDITION).uniqueFrom(TinkerModifiers.knockback.getId()).slots(armorSlots).eachLevel(1));
    buildModifier(TinkerModifiers.padded)
      .priority(75) // run after knockback
      .addModule(KnockbackModule.builder().formula()
        .variable(VALUE)
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
    buildModifier(ModifierIds.smite).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(ConditionalMeleeDamageModule.builder().target(new MobTypePredicate(MobType.UNDEAD)).eachLevel(2.0f));
    buildModifier(ModifierIds.antiaquatic).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(ConditionalMeleeDamageModule.builder().target(new MobTypePredicate(MobType.WATER)).eachLevel(2.0f));
    buildModifier(ModifierIds.cooling).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(ConditionalMeleeDamageModule.builder().target(LivingEntityPredicate.FIRE_IMMUNE).eachLevel(1.6f));
    IJsonPredicate<LivingEntity> baneSssssPredicate = LivingEntityPredicate.OR.create(new MobTypePredicate(MobType.ARTHROPOD), new TagEntityPredicate(TinkerTags.EntityTypes.CREEPERS));
    buildModifier(ModifierIds.baneOfSssss)
      .addModule(IncrementalModule.RECIPE_CONTROLLED)
      .addModule(ConditionalMeleeDamageModule.builder().target(baneSssssPredicate).eachLevel(2.0f))
      .addModule(MobEffectModule.builder(MobEffects.MOVEMENT_SLOWDOWN).level(RandomLevelingValue.flat(4)).time(RandomLevelingValue.random(20, 10)).target(baneSssssPredicate).build(), TinkerHooks.MELEE_HIT);
    buildModifier(ModifierIds.killager).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(ConditionalMeleeDamageModule.builder().target(LivingEntityPredicate.OR.create(
      new MobTypePredicate(MobType.ILLAGER),
      new TagEntityPredicate(TinkerTags.EntityTypes.VILLAGERS))).eachLevel(2.0f));
    addRedirect(id("fractured"), redirect(ModifierIds.sharpness));
    buildModifier(ModifierIds.pierce)
      // less than sharpness, but pierces 1 armor
      .addModule(StatBoostModule.add(ToolStats.ATTACK_DAMAGE).eachLevel(0.5f))
      .addModule(MeleeAttributeModule.builder(Attributes.ARMOR, Operation.ADDITION).uniqueFrom(ModifierIds.pierce).eachLevel(-1))
      // use a mob effect to make this work on ranged, to ensure it automatically cancels
      .addModule(MobEffectModule.builder(TinkerModifiers.pierceEffect.get()).level(RandomLevelingValue.perLevel(0, 1)).time(RandomLevelingValue.flat(2)).build(), TinkerHooks.PROJECTILE_LAUNCH, TinkerHooks.PROJECTILE_HIT);

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
    buildModifier(ModifierIds.revitalizing).addModule(IncrementalModule.RECIPE_CONTROLLED).addModule(AttributeModule.builder(Attributes.MAX_HEALTH, Operation.ADDITION).uniqueFrom(ModifierIds.revitalizing).slots(armorSlots).eachLevel(2));
    // protection
    buildModifier(ModifierIds.protection).addModule(ProtectionModule.source(DamageSourcePredicate.CAN_PROTECT).eachLevel(1.25f));
    buildModifier(ModifierIds.fireProtection)
      .addModule(EnchantmentModule.constant(Enchantments.FIRE_PROTECTION).build())
      .addModule(ProtectionModule.source(DamageSourcePredicate.AND.create(DamageSourcePredicate.CAN_PROTECT, DamageSourcePredicate.FIRE)).subtract(Enchantments.FIRE_PROTECTION).eachLevel(2.5f));
    buildModifier(ModifierIds.turtleShell)
      .addModule(AttributeModule.builder(ForgeMod.SWIM_SPEED.get(), Operation.MULTIPLY_TOTAL).uniqueFrom(ModifierIds.turtleShell).slots(armorSlots).eachLevel(0.05f))
      .addModule(ProtectionModule.source(DamageSourcePredicate.CAN_PROTECT)
                                 .tool(new ItemToolPredicate(ItemPredicate.OR.create(new ItemTagPredicate(TinkerTags.Items.HELMETS), new ItemTagPredicate(TinkerTags.Items.CHESTPLATES))))
                                 .entity(TinkerLivingEntityPredicate.EYES_IN_WATER).eachLevel(2.5f))
      .addModule(ProtectionModule.source(DamageSourcePredicate.CAN_PROTECT)
                                 .tool(new ItemToolPredicate(ItemPredicate.OR.create(new ItemTagPredicate(TinkerTags.Items.LEGGINGS), new ItemTagPredicate(TinkerTags.Items.BOOTS))))
                                 .entity(TinkerLivingEntityPredicate.FEET_IN_WATER).eachLevel(2.5f));
    // helmet
    buildModifier(ModifierIds.respiration).addModule(EnchantmentModule.constant(Enchantments.RESPIRATION).build());
    buildModifier(ModifierIds.aquaAffinity).addModule(EnchantmentModule.constant(Enchantments.AQUA_AFFINITY).build()).levelDisplay(ModifierLevelDisplay.NO_LEVELS);
    // chestplate
    buildModifier(ModifierIds.strength)
      .addModule(IncrementalModule.RECIPE_CONTROLLED)
      .addModule(AttributeModule.builder(Attributes.ATTACK_DAMAGE, Operation.MULTIPLY_TOTAL).uniqueFrom(ModifierIds.strength).slots(armorSlots).eachLevel(0.1f));
    addRedirect(id("armor_power"), redirect(ModifierIds.strength));
    // leggings
    addModifier(ModifierIds.pockets, new InventoryMenuModifier(18));
    addModifier(ModifierIds.toolBelt, new ToolBeltModifier(new int[] {4, 5, 6, 7, 8, 9}));
    addRedirect(id("pocket_chain"), redirect(TinkerModifiers.shieldStrap.getId()));
    buildModifier(ModifierIds.stepUp).addModule(AttributeModule.builder(ForgeMod.STEP_HEIGHT_ADDITION.get(), Operation.ADDITION).uniqueFrom(ModifierIds.stepUp).slots(armorSlots).eachLevel(0.5f));
    buildModifier(ModifierIds.speedy).addModule(AttributeModule.builder(Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_TOTAL).uniqueFrom(ModifierIds.speedy).slots(armorMainHand).eachLevel(0.1f));
    // boots
    buildModifier(ModifierIds.depthStrider).addModule(EnchantmentModule.constant(Enchantments.DEPTH_STRIDER).build());
    buildModifier(ModifierIds.featherFalling).addModule(ProtectionModule.source(DamageSourcePredicate.FALL).eachLevel(3.75f));
    buildModifier(ModifierIds.longFall).levelDisplay(ModifierLevelDisplay.NO_LEVELS).addModule(BlockDamageSourceModule.source(DamageSourcePredicate.FALL).build());
    buildModifier(ModifierIds.frostWalker)
      .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
      .addModule(BlockDamageSourceModule.source(new SourceMessagePredicate(DamageSource.HOT_FLOOR)).build())
      .addModule(ReplaceBlockWalkerModule.builder().replaceAlways(BlockPropertiesPredicate.block(Blocks.WATER).matches(LiquidBlock.LEVEL, 0).build(), Blocks.FROSTED_ICE.defaultBlockState()).amount(2, 1));
    buildModifier(ModifierIds.pathMaker).levelDisplay(ModifierLevelDisplay.NO_LEVELS).addModule(ToolActionWalkerTransformModule.builder(ToolActions.SHOVEL_FLATTEN, SoundEvents.SHOVEL_FLATTEN).amount(0.5f, 1));
    buildModifier(ModifierIds.plowing).levelDisplay(ModifierLevelDisplay.NO_LEVELS).addModule(ToolActionWalkerTransformModule.builder(ToolActions.HOE_TILL, SoundEvents.HOE_TILL).amount(0.5f, 1));
    buildModifier(ModifierIds.snowdrift).priority(90).levelDisplay(ModifierLevelDisplay.NO_LEVELS).addModule(CoverGroundWalkerModule.block(Blocks.SNOW).amount(0.5f, 1));

    // interaction
    buildModifier(ModifierIds.pathing)
      .levelDisplay(ModifierLevelDisplay.NO_LEVELS)
      .addModule(ShowOffhandModule.INSTANCE)
      .addModule(ExtinguishCampfireModule.INSTANCE)
      .addModule(ToolActionTransformModule.builder(ToolActions.SHOVEL_FLATTEN, SoundEvents.SHOVEL_FLATTEN).requireGround().build());
    buildModifier(ModifierIds.stripping)
      .addModule(ShowOffhandModule.INSTANCE)
      .addModule(ToolActionTransformModule.builder(ToolActions.AXE_STRIP, SoundEvents.AXE_STRIP).build())
      .addModule(ToolActionTransformModule.builder(ToolActions.AXE_SCRAPE, SoundEvents.AXE_SCRAPE).eventId(3005).build())
      .addModule(ToolActionTransformModule.builder(ToolActions.AXE_WAX_OFF, SoundEvents.AXE_WAX_OFF).eventId(3004).build());
    buildModifier(ModifierIds.tilling)
      .addModule(ShowOffhandModule.INSTANCE)
      .addModule(ToolActionTransformModule.builder(ToolActions.HOE_TILL, SoundEvents.HOE_TILL).build());
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
    buildModifier(ModifierIds.scorching).addModule(ConditionalMeleeDamageModule.builder().target(LivingEntityPredicate.ON_FIRE).eachLevel(2f));
    buildModifier(ModifierIds.airborne)
      // 400% boost means 5x mining speed
      .addModule(ConditionalMiningSpeedModule.builder().holder(TinkerLivingEntityPredicate.ON_GROUND.inverted()).percent().allowIneffective().flat(4), TinkerHooks.BREAK_SPEED)
      // accuracy gets a 0.5 boost under the stricter version of in air (no boost just for being on a ladder)
      .addModule(ConditionalStatModule.stat(ToolStats.ACCURACY).holder(TinkerLivingEntityPredicate.AIRBORNE).flat(0.5f));
    buildModifier(ModifierIds.raging)
      .addModule(ConditionalMeleeDamageModule.builder()
        .customVariable("health", new EntityMeleeVariable(EntityVariable.HEALTH, WhichEntity.ATTACKER, 0))
        .customVariable("max_health", new EntityMeleeVariable(new AttributeEntityVariable(Attributes.MAX_HEALTH), WhichEntity.ATTACKER, 20))
        .formula()
        .customVariable("health")
        // add (10 - max_health) to health, at minimum 0, to account for low max health
        .constant(10).customVariable("max_health").subtract().nonNegative().add()
        // linear bonus from 2 to 8, max bonus below 2, no bonus above 8
        .constant(10).subtractFlipped().constant(8).divide().percentClamp()
        // get 4 bonus per level, bring in standard multiplier
        .variable(LEVEL).multiply().constant(4).multiply().variable(MULTIPLIER).multiply()
        // finally, add in base damage
        .variable(VALUE).add().build())
      .addModule(ConditionalStatModule.stat(ToolStats.DRAW_SPEED)
        .customVariable("health", new EntityConditionalStatVariable(EntityVariable.HEALTH, 0))
        .customVariable("max", new EntityConditionalStatVariable(new AttributeEntityVariable(Attributes.MAX_HEALTH), 20))
        .formula()
        .customVariable("health")
        // add (10 - max_health) to health, at minimum 0, to account for low max health
        .constant(10).customVariable("max").subtract().nonNegative().add()
        // linear bonus from 2 to 8, max bonus below 2, no bonus above 8
        .constant(10).subtractFlipped().constant(8).divide().percentClamp()
        // get 0.25 bonus per level, bring in standard multiplier
        .variable(LEVEL).multiply().constant(0.25f).multiply().variable(MULTIPLIER).multiply()
        // finally, add in base damage
        .variable(VALUE).add().build());


    // traits - tier 2 compat
    addModifier(ModifierIds.lustrous, new Modifier());
    buildModifier(ModifierIds.sharpweight)
      .addModule(StatBoostModule.multiplyBase(ToolStats.MINING_SPEED).eachLevel(0.1f))
      .addModule(StatBoostModule.multiplyBase(ToolStats.DRAW_SPEED).eachLevel(0.15f))
      .addModule(AttributeModule.builder(Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_BASE).uniqueFrom(ModifierIds.sharpweight).slots(handSlots).eachLevel(-0.1f));
    buildModifier(ModifierIds.heavy)
      .addModule(StatBoostModule.multiplyBase(ToolStats.ATTACK_DAMAGE).eachLevel(0.1f))
      .addModule(StatBoostModule.multiplyBase(ToolStats.PROJECTILE_DAMAGE).eachLevel(0.1f))
      .addModule(AttributeModule.builder(Attributes.MOVEMENT_SPEED, Operation.MULTIPLY_BASE).uniqueFrom(ModifierIds.heavy).slots(handSlots).eachLevel(-0.1f));
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
    buildModifier(ModifierIds.crumbling).addModule(ConditionalMiningSpeedModule.builder().blocks(BlockPredicate.REQUIRES_TOOL.inverted()).allowIneffective().eachLevel(0.5f));
    buildModifier(ModifierIds.enhanced).priority(60).addModule(UPGRADE);
    addRedirect(id("maintained_2"), redirect(ModifierIds.maintained));
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
    buildModifier(ModifierIds.maintained)
      .addModule(ConditionalMiningSpeedModule.builder()
        .customVariable("durability", new ToolMiningSpeedVariable(ToolVariable.CURRENT_DURABILITY))
        .customVariable("max_durability", new ToolMiningSpeedVariable(new ToolStatVariable(ToolStats.DURABILITY)))
        .formula()
        .customVariable("max_durability").constant(0.5f).multiply().duplicate()
        .customVariable("durability").subtractFlipped()
        .nonNegative().divideFlipped()
        .variable(LEVEL).multiply()
        .constant(6).multiply()
        .variable(MULTIPLIER).multiply()
        .variable(VALUE).add().build())
      .addModule(ConditionalStatModule.stat(ToolStats.VELOCITY)
        .customVariable("durability", new ToolConditionalStatVariable(ToolVariable.CURRENT_DURABILITY))
        .customVariable("max_durability", new ToolConditionalStatVariable(new ToolStatVariable(ToolStats.DURABILITY)))
        .formula()
        .customVariable("max_durability").constant(0.5f).multiply().duplicate()
        .customVariable("durability").subtractFlipped()
        .nonNegative().divideFlipped()
        .variable(LEVEL).multiply()
        .constant(0.05f).multiply()
        .variable(MULTIPLIER).multiply()
        .variable(VALUE).add().build());

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
