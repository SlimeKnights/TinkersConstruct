package slimeknights.tconstruct.tools;

import net.minecraft.block.Block;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.potion.EffectType;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.effect.TinkerEffect;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.modifiers.ExtraModifier;
import slimeknights.tconstruct.library.modifiers.ExtraModifier.ExtraType;
import slimeknights.tconstruct.library.modifiers.ExtraModifier.ModifierSource;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.TankModifier;
import slimeknights.tconstruct.library.recipe.modifiers.AgeableSeveringRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.OverslimeModifierRecipe;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.tools.modifiers.EmptyModifier;
import slimeknights.tconstruct.tools.modifiers.ModifierLootModifier;
import slimeknights.tconstruct.tools.modifiers.ability.AutosmeltModifier;
import slimeknights.tconstruct.tools.modifiers.ability.BucketingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.DuelWieldingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.ExchangingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.GlowingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.LuckModifier;
import slimeknights.tconstruct.tools.modifiers.ability.MeltingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.ReachModifier;
import slimeknights.tconstruct.tools.modifiers.ability.SilkyModifier;
import slimeknights.tconstruct.tools.modifiers.ability.UnbreakableModifier;
import slimeknights.tconstruct.tools.modifiers.effect.BleedingEffect;
import slimeknights.tconstruct.tools.modifiers.effect.MagneticEffect;
import slimeknights.tconstruct.tools.modifiers.free.OverslimeModifier;
import slimeknights.tconstruct.tools.modifiers.free.VolatileFlagModifier;
import slimeknights.tconstruct.tools.modifiers.internal.BlockTransformModifier;
import slimeknights.tconstruct.tools.modifiers.internal.HarvestAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.internal.OffhandAttackModifier;
import slimeknights.tconstruct.tools.modifiers.internal.PaddedModifier;
import slimeknights.tconstruct.tools.modifiers.internal.ShearsAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.internal.SilkyShearsAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.internal.TwoHandedAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.shared.ExperiencedModifier;
import slimeknights.tconstruct.tools.modifiers.shared.LevelDamageModifier;
import slimeknights.tconstruct.tools.modifiers.shared.NecroticModifier;
import slimeknights.tconstruct.tools.modifiers.traits.CultivatedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.DamageSpeedTradeModifier;
import slimeknights.tconstruct.tools.modifiers.traits.DuctileModifier;
import slimeknights.tconstruct.tools.modifiers.traits.DwarvenModifier;
import slimeknights.tconstruct.tools.modifiers.traits.HeavyModifier;
import slimeknights.tconstruct.tools.modifiers.traits.InsatibleModifier;
import slimeknights.tconstruct.tools.modifiers.traits.LaceratingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.LightweightModifier;
import slimeknights.tconstruct.tools.modifiers.traits.MaintainedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.MaintainedModifier2;
import slimeknights.tconstruct.tools.modifiers.traits.MomentumModifier;
import slimeknights.tconstruct.tools.modifiers.traits.OvercastModifier;
import slimeknights.tconstruct.tools.modifiers.traits.OvergrowthModifier;
import slimeknights.tconstruct.tools.modifiers.traits.OverlordModifier;
import slimeknights.tconstruct.tools.modifiers.traits.ScorchingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.SearingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.SturdyModifier;
import slimeknights.tconstruct.tools.modifiers.traits.TastyModifier;
import slimeknights.tconstruct.tools.modifiers.traits.TemperateModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.BaneOfSssssModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.BlastingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.CoolingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.DiamondModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.EmeraldModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.FieryModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.FortuneModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.HasteModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.HydraulicModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.KnockbackModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.LightspeedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.LootingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.MagneticModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.NetheriteModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.OverforcedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.PiercingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.ReinforcedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.ScaledTypeDamageModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.SeveringModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.SharpnessModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.SoulboundModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.SweepingEdgeModifier;
import slimeknights.tconstruct.tools.recipe.ModifierRemovalRecipe;
import slimeknights.tconstruct.tools.recipe.severing.MooshroomDemushroomingRecipe;
import slimeknights.tconstruct.tools.recipe.severing.PlayerBeheadingRecipe;
import slimeknights.tconstruct.tools.recipe.severing.SheepShearingRecipe;
import slimeknights.tconstruct.tools.recipe.severing.SnowGolemBeheadingRecipe;

import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Contains modifiers and the items or blocks used to craft modifiers
 */
@SuppressWarnings("unused")
public final class TinkerModifiers extends TinkerModule {
  protected static final Supplier<IForgeRegistry<Modifier>> MODIFIER_REGISTRY = MODIFIERS.makeRegistry("modifiers", () -> new RegistryBuilder<Modifier>().setType(Modifier.class).setDefaultKey(Util.getResource("empty")));

  /*
   * Blocks
   */
  // material
  public static final ItemObject<Block> silkyJewelBlock = BLOCKS.register("silky_jewel_block", GENERIC_GEM_BLOCK, HIDDEN_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<Item> silkyCloth = ITEMS.register("silky_cloth", GENERAL_PROPS);
  public static final ItemObject<Item> silkyJewel = ITEMS.register("silky_jewel", HIDDEN_PROPS);
  public static final ItemObject<Item> necroticBone = ITEMS.register("necrotic_bone", TOOLTIP_ITEM);
  public static final ItemObject<Item> dragonScale = ITEMS.register("dragon_scale", TOOLTIP_ITEM);
  // reinforcements
  public static final ItemObject<Item> ironReinforcement = ITEMS.register("iron_reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> slimesteelReinforcement = ITEMS.register("slimesteel_reinforcement", GENERAL_PROPS);
  // creative
  public static final ItemObject<Item> creativeUpgradeItem = ITEMS.register("creative_upgrade", TOOLTIP_ITEM);
  public static final ItemObject<Item> creativeAbilityItem = ITEMS.register("creative_ability", TOOLTIP_ITEM);
  public static final EnumObject<SlimeType, Item> slimeCrystal = ITEMS.registerEnum(SlimeType.TRUE_SLIME, "slime_crystal", (type) -> new Item(GENERAL_PROPS));

  /*
   * Modifiers
   */
  public static final RegistryObject<EmptyModifier> empty = MODIFIERS.register("empty", EmptyModifier::new);

  // durability
  public static final RegistryObject<ReinforcedModifier> reinforced = MODIFIERS.register("reinforced", ReinforcedModifier::new);
  public static final RegistryObject<OverforcedModifier> overforced = MODIFIERS.register("overforced", OverforcedModifier::new);
  public static final RegistryObject<EmeraldModifier> emerald = MODIFIERS.register("emerald", EmeraldModifier::new);
  public static final RegistryObject<DiamondModifier> diamond = MODIFIERS.register("diamond", DiamondModifier::new);
  public static final RegistryObject<VolatileFlagModifier> worldbound = MODIFIERS.register("worldbound", () -> new VolatileFlagModifier(0x7E6059, ToolCore.INDESTRUCTIBLE_ENTITY));
  public static final RegistryObject<SoulboundModifier> soulbound = MODIFIERS.register("soulbound", SoulboundModifier::new);
  public static final RegistryObject<NetheriteModifier> netherite = MODIFIERS.register("netherite", NetheriteModifier::new);
  public static final RegistryObject<OverslimeModifier> overslime = MODIFIERS.register("overslime", OverslimeModifier::new);

  // general effects
  public static final RegistryObject<ExperiencedModifier> experienced = MODIFIERS.register("experienced", ExperiencedModifier::new);
  public static final RegistryObject<MagneticModifier> magnetic = MODIFIERS.register("magnetic", MagneticModifier::new);
  public static final RegistryObject<VolatileFlagModifier> shiny = MODIFIERS.register("shiny", () -> new VolatileFlagModifier(0xFFA3EF, ToolCore.SHINY, Rarity.EPIC));

  // harvest
  public static final RegistryObject<HasteModifier> haste = MODIFIERS.register("haste", HasteModifier::new);
  public static final RegistryObject<BlastingModifier> blasting = MODIFIERS.register("blasting", BlastingModifier::new);
  public static final RegistryObject<HydraulicModifier> hydraulic = MODIFIERS.register("hydraulic", HydraulicModifier::new);
  public static final RegistryObject<LightspeedModifier> lightspeed = MODIFIERS.register("lightspeed", LightspeedModifier::new);
  public static final RegistryObject<FortuneModifier> fortune = MODIFIERS.register("fortune", FortuneModifier::new);

  // weapon
  public static final RegistryObject<KnockbackModifier> knockback = MODIFIERS.register("knockback", KnockbackModifier::new);
  public static final RegistryObject<PaddedModifier> padded = MODIFIERS.register("padded", PaddedModifier::new);
  public static final RegistryObject<FieryModifier> fiery = MODIFIERS.register("fiery", FieryModifier::new);
  public static final RegistryObject<SeveringModifier> severing = MODIFIERS.register("severing", SeveringModifier::new);
  public static final RegistryObject<LootingModifier> looting = MODIFIERS.register("looting", LootingModifier::new);

  // damage boost
  public static final RegistryObject<PiercingModifier> piercing = MODIFIERS.register("piercing", PiercingModifier::new);
  public static final RegistryObject<ScaledTypeDamageModifier> smite = MODIFIERS.register("smite", () -> new ScaledTypeDamageModifier(0xCC9720, CreatureAttribute.UNDEAD));
  public static final RegistryObject<BaneOfSssssModifier> baneOfSssss = MODIFIERS.register("bane_of_sssss", BaneOfSssssModifier::new);
  public static final RegistryObject<ScaledTypeDamageModifier> antiaquatic = MODIFIERS.register("antiaquatic", () -> new ScaledTypeDamageModifier(0xD58520, CreatureAttribute.WATER));
  public static final RegistryObject<CoolingModifier> cooling = MODIFIERS.register("cooling", CoolingModifier::new);
  public static final RegistryObject<SharpnessModifier> sharpness = MODIFIERS.register("sharpness", SharpnessModifier::new);
  public static final RegistryObject<SweepingEdgeModifier> sweeping = MODIFIERS.register("sweeping_edge", SweepingEdgeModifier::new);

  // abilities
  public static final RegistryObject<LuckModifier> luck = MODIFIERS.register("luck", LuckModifier::new);
  public static final RegistryObject<ReachModifier> reach = MODIFIERS.register("reach", ReachModifier::new);
  public static final RegistryObject<UnbreakableModifier> unbreakable = MODIFIERS.register("unbreakable", UnbreakableModifier::new);
  // weapon
  public static final RegistryObject<DuelWieldingModifier> duelWielding = MODIFIERS.register("duel_wielding", DuelWieldingModifier::new);
  // harvest
  public static final RegistryObject<SilkyModifier> silky = MODIFIERS.register("silky", SilkyModifier::new);
  public static final RegistryObject<AutosmeltModifier> autosmelt = MODIFIERS.register("autosmelt", AutosmeltModifier::new);
  public static final RegistryObject<Modifier> expanded = MODIFIERS.register("expanded", () -> new Modifier(0xff9f50));
  public static final RegistryObject<ExchangingModifier> exchanging = MODIFIERS.register("exchanging", ExchangingModifier::new);

  // fluid abilities
  public static final RegistryObject<MeltingModifier> melting = MODIFIERS.register("melting", MeltingModifier::new);
  public static final RegistryObject<TankModifier> tank = MODIFIERS.register("tank", () -> new TankModifier(0xf3f3f3f, MaterialValues.INGOT * 8));
  public static final RegistryObject<BucketingModifier> bucketing = MODIFIERS.register("bucketing", BucketingModifier::new);
  
  // right click abilities
  public static final RegistryObject<GlowingModifier> glowing = MODIFIERS.register("glowing", GlowingModifier::new);
  public static final RegistryObject<BlockTransformModifier> pathing = MODIFIERS.register("pathing", () -> new BlockTransformModifier(0x8a361e, 75, ToolType.SHOVEL, SoundEvents.ITEM_SHOVEL_FLATTEN, true));
  public static final RegistryObject<BlockTransformModifier> stripping = MODIFIERS.register("stripping", () -> new BlockTransformModifier(0xab7a55, 75, ToolType.AXE, SoundEvents.ITEM_AXE_STRIP, false));
  public static final RegistryObject<BlockTransformModifier> tilling = MODIFIERS.register("tilling", () -> new BlockTransformModifier(0x633c1e, 75, ToolType.HOE, SoundEvents.ITEM_HOE_TILL, true));
  
  // internal abilities
  public static final RegistryObject<BlockTransformModifier> shovelTransformHidden = MODIFIERS.register("shovel_transform_hidden", () -> new BlockTransformModifier(0x8a361e, Integer.MIN_VALUE + 50, ToolType.SHOVEL, SoundEvents.ITEM_SHOVEL_FLATTEN, true));
  public static final RegistryObject<BlockTransformModifier> axeTransformHidden = MODIFIERS.register("axe_transform_hidden", () -> new BlockTransformModifier(0xab7a55, Integer.MIN_VALUE + 50, ToolType.AXE, SoundEvents.ITEM_AXE_STRIP, false));
  public static final RegistryObject<BlockTransformModifier> hoeTransformHidden = MODIFIERS.register("hoe_transform_hidden", () -> new BlockTransformModifier(0x633c1e, Integer.MIN_VALUE + 50, ToolType.HOE, SoundEvents.ITEM_HOE_TILL, true));

  public static final RegistryObject<ShearsAbilityModifier> shears = MODIFIERS.register("shears", () -> new ShearsAbilityModifier(0xd8e3e1, 0, Short.MIN_VALUE));
  public static final RegistryObject<SilkyShearsAbilityModifier> silkyShears = MODIFIERS.register("silky_shears", () -> new SilkyShearsAbilityModifier(0xd8e3e1, 0, Short.MIN_VALUE));
  public static final RegistryObject<SilkyShearsAbilityModifier> aoeSilkyShears = MODIFIERS.register("silky_aoe_shears", () -> new SilkyShearsAbilityModifier(0xd8e3e1, 1, Short.MIN_VALUE));
  public static final RegistryObject<HarvestAbilityModifier> harvest = MODIFIERS.register("harvest", () -> new HarvestAbilityModifier(0x3eed78, Integer.MIN_VALUE + 51));
  public static final RegistryObject<TwoHandedAbilityModifier> twoHanded = MODIFIERS.register("two_handed", TwoHandedAbilityModifier::new);
  public static final RegistryObject<OffhandAttackModifier> offhandAttack = MODIFIERS.register("offhand_attack", () -> new OffhandAttackModifier(-1, 25));


  // bonus modifier slots
  public static final RegistryObject<ExtraModifier> writable = MODIFIERS.register("writable", () -> new ExtraModifier(0xffffff));
  public static final RegistryObject<ExtraModifier> recapitated = MODIFIERS.register("recapitated", () -> new ExtraModifier(0x67d755));
  public static final RegistryObject<ExtraModifier> harmonious = MODIFIERS.register("harmonious", () -> new ExtraModifier(0xffd800));
  public static final RegistryObject<ExtraModifier> resurrected = MODIFIERS.register("resurrected", () -> new ExtraModifier(0xbe95d4));
  public static final RegistryObject<ExtraModifier> gilded = MODIFIERS.register("gilded", () -> new ExtraModifier(0xeccb45, ExtraType.UPGRADE, ModifierSource.MULTI_LEVEL, 2));
  public static final RegistryObject<ExtraModifier> draconic = MODIFIERS.register("draconic", () -> new ExtraModifier(0x707070, ExtraType.ABILITY, ModifierSource.SINGLE_LEVEL));
  // extra modifier slots for modpacks
  public static final RegistryObject<ExtraModifier> redExtraUpgrade = MODIFIERS.register("red_extra_upgrade", () -> new ExtraModifier(0xff0000));
  public static final RegistryObject<ExtraModifier> greenExtraUpgrade = MODIFIERS.register("green_extra_upgrade", () -> new ExtraModifier(0x00ff00));
  public static final RegistryObject<ExtraModifier> blueExtraUpgrade = MODIFIERS.register("blue_extra_upgrade", () -> new ExtraModifier(0x0000ff));
  // creative
  public static final RegistryObject<ExtraModifier> creativeUpgrade = MODIFIERS.register("creative_upgrade", () -> new ExtraModifier(0xCCBA47, ExtraType.UPGRADE, ModifierSource.MULTI_LEVEL));
  public static final RegistryObject<ExtraModifier> creativeAbility = MODIFIERS.register("creative_ability", () -> new ExtraModifier(0xB8A0FF, ExtraType.ABILITY, ModifierSource.MULTI_LEVEL));

  // traits - tier 1
  public static final RegistryObject<CultivatedModifier> cultivated = MODIFIERS.register("cultivated", CultivatedModifier::new);
  public static final RegistryObject<DamageSpeedTradeModifier> jagged = MODIFIERS.register("jagged", () -> new DamageSpeedTradeModifier(0x696969, 0.005f));
  public static final RegistryObject<DamageSpeedTradeModifier> stonebound = MODIFIERS.register("stonebound", () -> new DamageSpeedTradeModifier(0x999999, -0.005f));
  public static final RegistryObject<LevelDamageModifier> fractured = MODIFIERS.register("fractured", () -> new LevelDamageModifier(0xE8E5D2, 0.5f));
  // traits - tier 1 nether
  public static final RegistryObject<NecroticModifier> necrotic = MODIFIERS.register("necrotic", NecroticModifier::new);
  // traits - tier 2
  public static final RegistryObject<SturdyModifier> sturdy = MODIFIERS.register("sturdy", SturdyModifier::new);
  public static final RegistryObject<SearingModifier> searing = MODIFIERS.register("searing", SearingModifier::new);
  public static final RegistryObject<ScorchingModifier> scorching = MODIFIERS.register("scorching", ScorchingModifier::new);
  public static final RegistryObject<DwarvenModifier> dwarven = MODIFIERS.register("dwarven", DwarvenModifier::new);
  public static final RegistryObject<OvergrowthModifier> overgrowth = MODIFIERS.register("overgrowth", OvergrowthModifier::new);
  // traits - tier 3
  public static final RegistryObject<OvercastModifier> overcast = MODIFIERS.register("overcast", OvercastModifier::new);
  public static final RegistryObject<LaceratingModifier> lacerating = MODIFIERS.register("lacerating", LaceratingModifier::new);
  public static final RegistryObject<MaintainedModifier> wellMaintained = MODIFIERS.register("maintained", MaintainedModifier::new);
  public static final RegistryObject<ExtraModifier> enhanced = MODIFIERS.register("enhanced", () -> new ExtraModifier(0xF7CDBB, ExtraType.UPGRADE, ModifierSource.TRAIT));
  public static final RegistryObject<TastyModifier> tasty = MODIFIERS.register("tasty", TastyModifier::new);
  // traits - tier 3 nether
  public static final RegistryObject<LightweightModifier> lightweight = MODIFIERS.register("lightweight", LightweightModifier::new);
  // traits - tier 4
  public static final RegistryObject<OverlordModifier> overlord = MODIFIERS.register("overlord", OverlordModifier::new);
  public static final RegistryObject<MomentumModifier> momentum = MODIFIERS.register("momentum", MomentumModifier::new);
  public static final RegistryObject<InsatibleModifier> insatiable = MODIFIERS.register("insatiable", InsatibleModifier::new);

  // traits - mod compat tier 2
  public static final RegistryObject<HeavyModifier> heavy = MODIFIERS.register("heavy", HeavyModifier::new);
  // experienced is also an upgrade
  // traits - mod compat tier 3
  public static final RegistryObject<DuctileModifier> ductile = MODIFIERS.register("ductile", DuctileModifier::new);
  public static final RegistryObject<MaintainedModifier2> wellMaintained2 = MODIFIERS.register("maintained_2", MaintainedModifier2::new);
  public static final RegistryObject<TemperateModifier> temperate = MODIFIERS.register("temperate", TemperateModifier::new);

  /*
   * Internal effects
   */
  private static final IntFunction<Supplier<TinkerEffect>> MARKER_EFFECT = color -> () -> new TinkerEffect(EffectType.BENEFICIAL, color, false);
  public static RegistryObject<BleedingEffect> bleeding = POTIONS.register("bleeding", BleedingEffect::new);
  public static RegistryObject<MagneticEffect> magneticEffect = POTIONS.register("magnetic", MagneticEffect::new);
  public static RegistryObject<TinkerEffect> momentumEffect = POTIONS.register("momentum", MARKER_EFFECT.apply(0x60496b));
  public static RegistryObject<TinkerEffect> insatiableEffect = POTIONS.register("insatiable", MARKER_EFFECT.apply(0x9261cc));

  /*
   * Recipes
   */
  public static final RegistryObject<ModifierRecipe.Serializer> modifierSerializer = RECIPE_SERIALIZERS.register("modifier", ModifierRecipe.Serializer::new);
  public static final RegistryObject<IncrementalModifierRecipe.Serializer> incrementalModifierSerializer = RECIPE_SERIALIZERS.register("incremental_modifier", IncrementalModifierRecipe.Serializer::new);
  public static final RegistryObject<OverslimeModifierRecipe.Serializer> overslimeSerializer = RECIPE_SERIALIZERS.register("overslime_modifier", OverslimeModifierRecipe.Serializer::new);
  public static final RegistryObject<ModifierRemovalRecipe.Serializer> removeModifierSerializer = RECIPE_SERIALIZERS.register("remove_modifier", ModifierRemovalRecipe.Serializer::new);
  public static final RegistryObject<ModifierLootModifier.Serializer> modifierLootModifier = GLOBAL_LOOT_MODIFIERS.register("modifier_hook", ModifierLootModifier.Serializer::new);
  // severing
  public static final RegistryObject<SeveringRecipe.Serializer> severingSerializer = RECIPE_SERIALIZERS.register("severing", SeveringRecipe.Serializer::new);
  public static final RegistryObject<AgeableSeveringRecipe.Serializer> ageableSeveringSerializer = RECIPE_SERIALIZERS.register("ageable_severing", AgeableSeveringRecipe.Serializer::new);
  // special severing
  public static final RegistryObject<SpecialRecipeSerializer<PlayerBeheadingRecipe>> playerBeheadingSerializer = RECIPE_SERIALIZERS.register("player_beheading", () -> new SpecialRecipeSerializer<>(PlayerBeheadingRecipe::new));
  public static final RegistryObject<SpecialRecipeSerializer<SnowGolemBeheadingRecipe>> snowGolemBeheadingSerializer = RECIPE_SERIALIZERS.register("snow_golem_beheading", () -> new SpecialRecipeSerializer<>(SnowGolemBeheadingRecipe::new));
  public static final RegistryObject<SpecialRecipeSerializer<MooshroomDemushroomingRecipe>> mooshroomDemushroomingSerializer = RECIPE_SERIALIZERS.register("mooshroom_demushrooming", () -> new SpecialRecipeSerializer<>(MooshroomDemushroomingRecipe::new));
  public static final RegistryObject<SpecialRecipeSerializer<SheepShearingRecipe>> sheepShearing = RECIPE_SERIALIZERS.register("sheep_shearing", () -> new SpecialRecipeSerializer<>(SheepShearingRecipe::new));
}
