package slimeknights.tconstruct.tools;

import net.minecraft.block.Block;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.potion.EffectType;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.modifiers.ExtraModifier;
import slimeknights.tconstruct.library.modifiers.ExtraModifier.ModifierSource;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.SingleLevelModifier;
import slimeknights.tconstruct.library.modifiers.TankModifier;
import slimeknights.tconstruct.library.modifiers.impl.TotalArmorLevelModifier;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.salvage.IncrementalModifierSalvage;
import slimeknights.tconstruct.library.recipe.modifiers.salvage.ModifierSalvage;
import slimeknights.tconstruct.library.recipe.modifiers.severing.AgeableSeveringRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.CureEffectsSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.DamageSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.EffectSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.ExtinguishSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.PotionFluidEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.RestoreHungerSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.SetFireSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.TeleportSpillingEffect;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairCraftingRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairRecipeSerializer;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairTinkerStationRecipe;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.ModifiableArmorItem;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.tools.item.CreativeSlotItem;
import slimeknights.tconstruct.tools.item.DragonScaleItem;
import slimeknights.tconstruct.tools.modifiers.EmptyModifier;
import slimeknights.tconstruct.tools.modifiers.ModifierLootModifier;
import slimeknights.tconstruct.tools.modifiers.ability.ReachModifier;
import slimeknights.tconstruct.tools.modifiers.ability.UnbreakableModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.BouncyModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.DoubleJumpModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.PocketsModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ShieldStrapModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.SlurpingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.StrengthModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ToolBeltModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.UnarmedModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ZoomModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.walker.BlockTransformWalkerModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.walker.FlamewakeModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.walker.FrostWalkerModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.walker.SnowdriftModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.AutosmeltModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.BucketingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.DuelWieldingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.ExchangingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.GlowingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.LuckModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.MeltingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.SilkyModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.SpillingModifier;
import slimeknights.tconstruct.tools.modifiers.defense.BlastProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.DragonbornModifier;
import slimeknights.tconstruct.tools.modifiers.defense.FireProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.KnockbackResistanceModifier;
import slimeknights.tconstruct.tools.modifiers.defense.MagicProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.MeleeProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.ProjectileProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.ProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.RevitalizingModifier;
import slimeknights.tconstruct.tools.modifiers.defense.TurtleShellModifier;
import slimeknights.tconstruct.tools.modifiers.effect.BleedingEffect;
import slimeknights.tconstruct.tools.modifiers.effect.MagneticEffect;
import slimeknights.tconstruct.tools.modifiers.effect.NoMilkEffect;
import slimeknights.tconstruct.tools.modifiers.internal.BlockTransformModifier;
import slimeknights.tconstruct.tools.modifiers.internal.FirestarterModifier;
import slimeknights.tconstruct.tools.modifiers.internal.HarvestAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.internal.OffhandAttackModifier;
import slimeknights.tconstruct.tools.modifiers.internal.PaddedModifier;
import slimeknights.tconstruct.tools.modifiers.internal.ShearsAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.internal.SilkyShearsAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.internal.TwoHandedAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.loot.ChrysophiliteBonusFunction;
import slimeknights.tconstruct.tools.modifiers.loot.ChrysophiliteLootCondition;
import slimeknights.tconstruct.tools.modifiers.slotless.CreativeSlotModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.FarsightedModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.NearsightedModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.StatOverrideModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.VolatileFlagModifier;
import slimeknights.tconstruct.tools.modifiers.traits.DamageSpeedTradeModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.CultivatedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.DenseModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.DuctileModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.EnderportingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.LightweightModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.OvercastModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.OvergrowthModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.OverlordModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.OverworkedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.SolarPoweredModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.StoneshieldModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.SturdyModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.TannedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.TastyModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.AirborneModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.DwarvenModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.LustrousModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.MaintainedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.MaintainedModifier2;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.MomentumModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.SharpweightModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.TemperateModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.ConductingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.DecayModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.HeavyModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.InsatibleModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.InvariantModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.LaceratingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.LevelDamageModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.NecroticModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.RagingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.ScorchingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.SearingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.BoonOfSssssModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.BreathtakingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.ChrysophiliteModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.EnderdodgingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.FirebreathModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.FrosttouchModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.GoldGuardModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.MithridatismModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.MobDisguiseModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.PlagueModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.RevengeModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.SelfDestructiveModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.StrongBonesModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.WildfireModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.WitheredModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.ArmorKnockbackModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.ArmorPowerModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.FeatherFallingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.ItemFrameModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.LeapingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.LightspeedArmorModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.PocketChainModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.RespirationModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.RicochetModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.SoulSpeedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.SpeedyModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.SpringyModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.StickyModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.ThornsModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.DiamondModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.EmeraldModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.ExperiencedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.MagneticModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.NetheriteModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.OffhandedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.OverforcedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.ReinforcedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.SoulboundModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.TOPModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.harvest.BlastingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.harvest.FortuneModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.harvest.HasteModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.harvest.HydraulicModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.harvest.LightspeedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.BaneOfSssssModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.CoolingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.FieryModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.KnockbackModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.LootingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.PiercingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.ScaledTypeDamageModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.SeveringModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.SharpnessModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.SweepingEdgeModifier;
import slimeknights.tconstruct.tools.recipe.CreativeSlotRecipe;
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
  protected static final Supplier<IForgeRegistry<Modifier>> MODIFIER_REGISTRY = MODIFIERS.makeRegistry("modifiers", () -> new RegistryBuilder<Modifier>().setType(Modifier.class).setDefaultKey(TConstruct.getResource("empty")));

  /*
   * Blocks
   */
  // material
  public static final ItemObject<Block> silkyJewelBlock = BLOCKS.register("silky_jewel_block", metalBuilder(MaterialColor.GOLD), HIDDEN_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<Item> silkyCloth = ITEMS.register("silky_cloth", GENERAL_PROPS);
  public static final ItemObject<Item> silkyJewel = ITEMS.register("silky_jewel", HIDDEN_PROPS);
  public static final ItemObject<Item> dragonScale = ITEMS.register("dragon_scale", () -> new DragonScaleItem(new Item.Properties().group(TAB_GENERAL).rarity(Rarity.RARE)));
  // reinforcements
  public static final ItemObject<Item> ironReinforcement = ITEMS.register("iron_reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> slimesteelReinforcement = ITEMS.register("slimesteel_reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> searedReinforcement = ITEMS.register("seared_reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> goldReinforcement = ITEMS.register("gold_reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> emeraldReinforcement = ITEMS.register("emerald_reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> bronzeReinforcement = ITEMS.register("bronze_reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> cobaltReinforcement = ITEMS.register("cobalt_reinforcement", GENERAL_PROPS);
  // creative
  public static final ItemObject<Item> creativeSlotItem = ITEMS.register("creative_slot", () -> new CreativeSlotItem(GENERAL_PROPS));
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
  public static final RegistryObject<VolatileFlagModifier> worldbound = MODIFIERS.register("worldbound", () -> new VolatileFlagModifier(0x7E6059, IModifiable.INDESTRUCTIBLE_ENTITY));
  public static final RegistryObject<SoulboundModifier> soulbound = MODIFIERS.register("soulbound", SoulboundModifier::new);
  public static final RegistryObject<NetheriteModifier> netherite = MODIFIERS.register("netherite", NetheriteModifier::new);
  public static final RegistryObject<OverslimeModifier> overslime = MODIFIERS.register("overslime", OverslimeModifier::new);

  // general effects
  public static final RegistryObject<ExperiencedModifier> experienced = MODIFIERS.register("experienced", ExperiencedModifier::new);
  public static final RegistryObject<MagneticModifier> magnetic = MODIFIERS.register("magnetic", MagneticModifier::new);
  public static final RegistryObject<VolatileFlagModifier> shiny = MODIFIERS.register("shiny", () -> new VolatileFlagModifier(0xFFA3EF, IModifiable.SHINY, Rarity.EPIC));
  public static final RegistryObject<OffhandedModifier> offhanded = MODIFIERS.register("offhanded", OffhandedModifier::new);
  public static final RegistryObject<FarsightedModifier> farsighted = MODIFIERS.register("farsighted", FarsightedModifier::new);
  public static final RegistryObject<NearsightedModifier> nearsighted = MODIFIERS.register("nearsighted", NearsightedModifier::new);

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

  // armor
  // protection
  public static final RegistryObject<ProtectionModifier> protection = MODIFIERS.register("protection", ProtectionModifier::new);
  public static final RegistryObject<MeleeProtectionModifier> meleeProtection = MODIFIERS.register("melee_protection", MeleeProtectionModifier::new);
  public static final RegistryObject<FireProtectionModifier> fireProtection = MODIFIERS.register("fire_protection", FireProtectionModifier::new);
  public static final RegistryObject<BlastProtectionModifier> blastProtection = MODIFIERS.register("blast_protection", BlastProtectionModifier::new);
  public static final RegistryObject<MagicProtectionModifier> magicProtection = MODIFIERS.register("magic_protection", MagicProtectionModifier::new);
  public static final RegistryObject<ProjectileProtectionModifier> projectileProtection = MODIFIERS.register("projectile_protection", ProjectileProtectionModifier::new);
  public static final RegistryObject<KnockbackResistanceModifier> knockbackResistance = MODIFIERS.register("knockback_resistance", KnockbackResistanceModifier::new);
  public static final RegistryObject<TurtleShellModifier> turtleShell = MODIFIERS.register("turtle_shell", TurtleShellModifier::new);
  public static final RegistryObject<DragonbornModifier> dragonborn = MODIFIERS.register("dragonborn", DragonbornModifier::new);
  // general
  public static final RegistryObject<VolatileFlagModifier> golden = MODIFIERS.register("golden", () -> new VolatileFlagModifier(0xFFD83E, ModifiableArmorItem.PIGLIN_NEUTRAL));
  public static final RegistryObject<RicochetModifier> ricochet = MODIFIERS.register("ricochet", RicochetModifier::new);
  public static final RegistryObject<RevitalizingModifier> revitalizing = MODIFIERS.register("revitalizing", RevitalizingModifier::new);
  // counterattack
  public static final RegistryObject<ThornsModifier> thorns = MODIFIERS.register("thorns", ThornsModifier::new);
  public static final RegistryObject<SpringyModifier> springy = MODIFIERS.register("springy", SpringyModifier::new);
  public static final RegistryObject<StickyModifier> sticky = MODIFIERS.register("sticky", StickyModifier::new);
  // helmet
  public static final RegistryObject<RespirationModifier> respiration = MODIFIERS.register("respiration", RespirationModifier::new);
  public static final RegistryObject<ItemFrameModifier> itemFrame = MODIFIERS.register("item_frame", ItemFrameModifier::new);
  public static final RegistryObject<ZoomModifier> zoom = MODIFIERS.register("zoom", ZoomModifier::new);
  public static final RegistryObject<SlurpingModifier> slurping = MODIFIERS.register("slurping", SlurpingModifier::new);
  public static final RegistryObject<TotalArmorLevelModifier> aquaAffinity = MODIFIERS.register("aqua_affinity", () -> new TotalArmorLevelModifier(0x3FA442, TinkerDataKeys.AQUA_AFFINITY, true));
  // chestplate
  public static final RegistryObject<ArmorKnockbackModifier> armorKnockback = MODIFIERS.register("knockback_armor", ArmorKnockbackModifier::new);
  public static final RegistryObject<UnarmedModifier> unarmed = MODIFIERS.register("unarmed", UnarmedModifier::new);
  public static final RegistryObject<ArmorPowerModifier> armorPower = MODIFIERS.register("armor_power", ArmorPowerModifier::new);
  public static final RegistryObject<StrengthModifier> strength = MODIFIERS.register("strength", StrengthModifier::new);
  // leggings
  public static final RegistryObject<SpeedyModifier> speedy = MODIFIERS.register("speedy", SpeedyModifier::new);
  public static final RegistryObject<LeapingModifier> leaping = MODIFIERS.register("leaping", LeapingModifier::new);
  public static final RegistryObject<PocketsModifier> pockets = MODIFIERS.register("pockets", PocketsModifier::new);
  public static final RegistryObject<ShieldStrapModifier> shieldStrap = MODIFIERS.register("shield_strap", ShieldStrapModifier::new);
  public static final RegistryObject<ToolBeltModifier> toolBelt = MODIFIERS.register("tool_belt", ToolBeltModifier::new);
  public static final RegistryObject<PocketChainModifier> pocketChain = MODIFIERS.register("pocket_chain", PocketChainModifier::new);
  // boots
  public static final RegistryObject<FeatherFallingModifier> featherFalling = MODIFIERS.register("feather_falling", FeatherFallingModifier::new);
  public static final RegistryObject<SoulSpeedModifier> soulspeed = MODIFIERS.register("soulspeed", SoulSpeedModifier::new);
  public static final RegistryObject<LightspeedArmorModifier> lightspeedArmor = MODIFIERS.register("lightspeed_armor", LightspeedArmorModifier::new);
  public static final RegistryObject<DoubleJumpModifier> doubleJump = MODIFIERS.register("double_jump", DoubleJumpModifier::new);
  public static final RegistryObject<Modifier> bouncy = MODIFIERS.register("bouncy", BouncyModifier::new);
  public static final RegistryObject<FrostWalkerModifier> frostWalker = MODIFIERS.register("frost_walker", FrostWalkerModifier::new);
  public static final RegistryObject<BlockTransformWalkerModifier> pathMaker = MODIFIERS.register("path_maker", () -> new BlockTransformWalkerModifier(0x8a361e, ToolType.SHOVEL, SoundEvents.ITEM_SHOVEL_FLATTEN));
  public static final RegistryObject<BlockTransformWalkerModifier> plowing = MODIFIERS.register("plowing", () -> new BlockTransformWalkerModifier(0x633c1e, ToolType.HOE, SoundEvents.ITEM_HOE_TILL));
  public static final RegistryObject<SnowdriftModifier> snowdrift = MODIFIERS.register("snowdrift", SnowdriftModifier::new);
  public static final RegistryObject<FlamewakeModifier> flamewake = MODIFIERS.register("flamewake", FlamewakeModifier::new);

  // abilities
  public static final RegistryObject<LuckModifier> luck = MODIFIERS.register("luck", LuckModifier::new);
  public static final RegistryObject<ReachModifier> reach = MODIFIERS.register("reach", ReachModifier::new);
  public static final RegistryObject<UnbreakableModifier> unbreakable = MODIFIERS.register("unbreakable", UnbreakableModifier::new);
  // weapon
  public static final RegistryObject<DuelWieldingModifier> dualWielding = MODIFIERS.register("dual_wielding", DuelWieldingModifier::new);
  // harvest
  public static final RegistryObject<SilkyModifier> silky = MODIFIERS.register("silky", SilkyModifier::new);
  public static final RegistryObject<AutosmeltModifier> autosmelt = MODIFIERS.register("autosmelt", AutosmeltModifier::new);
  public static final RegistryObject<Modifier> expanded = MODIFIERS.register("expanded", () -> new Modifier(0xff9f50));
  public static final RegistryObject<ExchangingModifier> exchanging = MODIFIERS.register("exchanging", ExchangingModifier::new);

  // fluid abilities
  public static final RegistryObject<MeltingModifier> melting = MODIFIERS.register("melting", MeltingModifier::new);
  public static final RegistryObject<TankModifier> tank = MODIFIERS.register("tank", () -> new TankModifier(0x3f3f3f, FluidValues.INGOT * 8));
  public static final RegistryObject<BucketingModifier> bucketing = MODIFIERS.register("bucketing", BucketingModifier::new);
  public static final RegistryObject<SpillingModifier> spilling = MODIFIERS.register("spilling", SpillingModifier::new);
  
  // right click abilities
  public static final RegistryObject<GlowingModifier> glowing = MODIFIERS.register("glowing", GlowingModifier::new);
  public static final RegistryObject<BlockTransformModifier> pathing = MODIFIERS.register("pathing", () -> new BlockTransformModifier(0x8a361e, 75, ToolType.SHOVEL, SoundEvents.ITEM_SHOVEL_FLATTEN, true));
  public static final RegistryObject<BlockTransformModifier> stripping = MODIFIERS.register("stripping", () -> new BlockTransformModifier(0xab7a55, 75, ToolType.AXE, SoundEvents.ITEM_AXE_STRIP, false));
  public static final RegistryObject<BlockTransformModifier> tilling = MODIFIERS.register("tilling", () -> new BlockTransformModifier(0x633c1e, 75, ToolType.HOE, SoundEvents.ITEM_HOE_TILL, true));
  public static final RegistryObject<FirestarterModifier> firestarter = MODIFIERS.register("firestarter", () -> new FirestarterModifier(0x5D342C, 70));
  public static final RegistryObject<SingleLevelModifier> fireprimer = MODIFIERS.register("fireprimer", () -> new SingleLevelModifier(0x4A281D));

  // internal abilities
  public static final RegistryObject<BlockTransformModifier> shovelTransformHidden = MODIFIERS.register("shovel_transform_hidden", () -> new BlockTransformModifier(0x8a361e, Integer.MIN_VALUE + 50, ToolType.SHOVEL, SoundEvents.ITEM_SHOVEL_FLATTEN, true));
  public static final RegistryObject<BlockTransformModifier> axeTransformHidden = MODIFIERS.register("axe_transform_hidden", () -> new BlockTransformModifier(0xab7a55, Integer.MIN_VALUE + 50, ToolType.AXE, SoundEvents.ITEM_AXE_STRIP, false));
  public static final RegistryObject<BlockTransformModifier> hoeTransformHidden = MODIFIERS.register("hoe_transform_hidden", () -> new BlockTransformModifier(0x633c1e, Integer.MIN_VALUE + 50, ToolType.HOE, SoundEvents.ITEM_HOE_TILL, true));
  public static final RegistryObject<FirestarterModifier> firestarterHidden = MODIFIERS.register("firestarter_hidden", () -> new FirestarterModifier(-1, Integer.MIN_VALUE + 50));
  public static final RegistryObject<VolatileFlagModifier> wings = MODIFIERS.register("wings", () -> new VolatileFlagModifier(0xD37CFF, ModifiableArmorItem.ELYTRA));

  public static final RegistryObject<ShearsAbilityModifier> shears = MODIFIERS.register("shears", () -> new ShearsAbilityModifier(0xd8e3e1, 0, Short.MIN_VALUE));
  public static final RegistryObject<SilkyShearsAbilityModifier> silkyShears = MODIFIERS.register("silky_shears", () -> new SilkyShearsAbilityModifier(0xd8e3e1, 0, Short.MIN_VALUE));
  public static final RegistryObject<SilkyShearsAbilityModifier> aoeSilkyShears = MODIFIERS.register("silky_aoe_shears", () -> new SilkyShearsAbilityModifier(0xd8e3e1, 1, Short.MIN_VALUE));
  public static final RegistryObject<HarvestAbilityModifier> harvest = MODIFIERS.register("harvest", () -> new HarvestAbilityModifier(0x3eed78, Integer.MIN_VALUE + 51));
  public static final RegistryObject<TwoHandedAbilityModifier> twoHanded = MODIFIERS.register("two_handed", TwoHandedAbilityModifier::new);
  public static final RegistryObject<OffhandAttackModifier> offhandAttack = MODIFIERS.register("offhand_attack", () -> new OffhandAttackModifier(-1));

  // bonus modifier slots
  public static final RegistryObject<ExtraModifier> writable = MODIFIERS.register("writable", () -> new ExtraModifier(0xffffff));
  public static final RegistryObject<ExtraModifier> recapitated = MODIFIERS.register("recapitated", () -> new ExtraModifier(0x67d755));
  public static final RegistryObject<ExtraModifier> harmonious = MODIFIERS.register("harmonious", () -> new ExtraModifier(0xffd800));
  public static final RegistryObject<ExtraModifier> resurrected = MODIFIERS.register("resurrected", () -> new ExtraModifier(0xbe95d4));
  public static final RegistryObject<ExtraModifier> gilded = MODIFIERS.register("gilded", () -> new ExtraModifier(0xeccb45, SlotType.UPGRADE, ModifierSource.MULTI_LEVEL, 2));
  public static final RegistryObject<ExtraModifier> draconic = MODIFIERS.register("draconic", () -> new ExtraModifier(0x707070, SlotType.ABILITY, ModifierSource.SINGLE_LEVEL));
  // extra modifier slots for modpacks
  public static final RegistryObject<ExtraModifier> redExtraUpgrade = MODIFIERS.register("red_extra_upgrade", () -> new ExtraModifier(0xff0000));
  public static final RegistryObject<ExtraModifier> greenExtraUpgrade = MODIFIERS.register("green_extra_upgrade", () -> new ExtraModifier(0x00ff00));
  public static final RegistryObject<ExtraModifier> blueExtraUpgrade = MODIFIERS.register("blue_extra_upgrade", () -> new ExtraModifier(0x0000ff));
  public static final RegistryObject<ExtraModifier> extraAbility = MODIFIERS.register("extra_ability", () -> new ExtraModifier(0xB8A0FF, SlotType.ABILITY, ModifierSource.SINGLE_LEVEL));
  // creative
  public static final RegistryObject<CreativeSlotModifier> creativeSlot = MODIFIERS.register("creative_slot", CreativeSlotModifier::new);
  public static final RegistryObject<StatOverrideModifier> statOverride = MODIFIERS.register("stat_override", StatOverrideModifier::new);

  // traits - tier 1
  public static final RegistryObject<CultivatedModifier> cultivated = MODIFIERS.register("cultivated", CultivatedModifier::new);
  public static final RegistryObject<DamageSpeedTradeModifier> jagged = MODIFIERS.register("jagged", () -> new DamageSpeedTradeModifier(0x696969, 0.005f));
  public static final RegistryObject<DamageSpeedTradeModifier> stonebound = MODIFIERS.register("stonebound", () -> new DamageSpeedTradeModifier(0x999999, -0.005f));
  public static final RegistryObject<LevelDamageModifier> fractured = MODIFIERS.register("fractured", () -> new LevelDamageModifier(0xE8E5D2, 0.5f));
  // traits - tier 1 nether
  public static final RegistryObject<NecroticModifier> necrotic = MODIFIERS.register("necrotic", NecroticModifier::new);
  // traits - tier 1 bindings
  public static final RegistryObject<Modifier> stringy = MODIFIERS.register("stringy", () -> new Modifier(-1));
  public static final RegistryObject<TannedModifier> tanned = MODIFIERS.register("tanned", TannedModifier::new);
  public static final RegistryObject<SolarPoweredModifier> solarPowered = MODIFIERS.register("solar_powered", SolarPoweredModifier::new);
  // traits - tier 2
  public static final RegistryObject<SturdyModifier> sturdy = MODIFIERS.register("sturdy", SturdyModifier::new);
  public static final RegistryObject<SearingModifier> searing = MODIFIERS.register("searing", SearingModifier::new);
  public static final RegistryObject<ScorchingModifier> scorching = MODIFIERS.register("scorching", ScorchingModifier::new);
  public static final RegistryObject<DwarvenModifier> dwarven = MODIFIERS.register("dwarven", DwarvenModifier::new);
  public static final RegistryObject<OvergrowthModifier> overgrowth = MODIFIERS.register("overgrowth", OvergrowthModifier::new);
  public static final RegistryObject<RagingModifier> raging = MODIFIERS.register("raging", RagingModifier::new);
  public static final RegistryObject<AirborneModifier> airborne = MODIFIERS.register("airborne", AirborneModifier::new);
  // traits - tier 3
  public static final RegistryObject<OvercastModifier> overcast = MODIFIERS.register("overcast", OvercastModifier::new);
  public static final RegistryObject<LaceratingModifier> lacerating = MODIFIERS.register("lacerating", LaceratingModifier::new);
  public static final RegistryObject<MaintainedModifier> wellMaintained = MODIFIERS.register("maintained", MaintainedModifier::new);
  public static final RegistryObject<ExtraModifier> enhanced = MODIFIERS.register("enhanced", () -> new ExtraModifier(0xF7CDBB, SlotType.UPGRADE, ModifierSource.TRAIT));
  public static final RegistryObject<TastyModifier> tasty = MODIFIERS.register("tasty", TastyModifier::new);
  // traits - tier 3 nether
  public static final RegistryObject<LightweightModifier> lightweight = MODIFIERS.register("lightweight", LightweightModifier::new);
  // traits - tier 4
  public static final RegistryObject<OverlordModifier> overlord = MODIFIERS.register("overlord", OverlordModifier::new);
  public static final RegistryObject<MomentumModifier> momentum = MODIFIERS.register("momentum", MomentumModifier::new);
  public static final RegistryObject<InsatibleModifier> insatiable = MODIFIERS.register("insatiable", InsatibleModifier::new);
  public static final RegistryObject<ConductingModifier> conducting = MODIFIERS.register("conducting", ConductingModifier::new);
  // traits - tier 5
  public static final RegistryObject<EnderportingModifier> enderporting = MODIFIERS.register("enderporting", EnderportingModifier::new);

  // traits - mod compat tier 2
  public static final RegistryObject<DenseModifier> dense = MODIFIERS.register("dense", DenseModifier::new);
  public static final RegistryObject<SharpweightModifier> sharpweight = MODIFIERS.register("sharpweight", SharpweightModifier::new);
  public static final RegistryObject<LustrousModifier> lustrous = MODIFIERS.register("lustrous", LustrousModifier::new);
  public static final RegistryObject<HeavyModifier> heavy = MODIFIERS.register("heavy", HeavyModifier::new);
  public static final RegistryObject<StoneshieldModifier> stoneshield = MODIFIERS.register("stoneshield", StoneshieldModifier::new);
  // smite is also an upgrade
  // traits - mod compat tier 3
  public static final RegistryObject<DuctileModifier> ductile = MODIFIERS.register("ductile", DuctileModifier::new);
  public static final RegistryObject<MaintainedModifier2> wellMaintained2 = MODIFIERS.register("maintained_2", MaintainedModifier2::new);
  public static final RegistryObject<TemperateModifier> temperate = MODIFIERS.register("temperate", TemperateModifier::new);
  public static final RegistryObject<InvariantModifier> invariant = MODIFIERS.register("invariant", InvariantModifier::new);
  public static final RegistryObject<DecayModifier> decay = MODIFIERS.register("decay", DecayModifier::new);
  public static final RegistryObject<OverworkedModifier> overworked = MODIFIERS.register("overworked", OverworkedModifier::new);
  // experienced is also an upgrade

  // traits - slimeskull
  public static final RegistryObject<SelfDestructiveModifier> selfDestructive = MODIFIERS.register("self_destructive", SelfDestructiveModifier::new);
  public static final RegistryObject<EnderdodgingModifier> enderdodging = MODIFIERS.register("enderdodging", EnderdodgingModifier::new);
  public static final RegistryObject<StrongBonesModifier> strongBones = MODIFIERS.register("strong_bones", StrongBonesModifier::new);
  public static final RegistryObject<FrosttouchModifier> frosttouch = MODIFIERS.register("frosttouch", FrosttouchModifier::new);
  public static final RegistryObject<WitheredModifier> withered = MODIFIERS.register("withered", WitheredModifier::new);
  public static final RegistryObject<BoonOfSssssModifier> boonOfSssss = MODIFIERS.register("boon_of_sssss", BoonOfSssssModifier::new);
  public static final RegistryObject<MithridatismModifier> mithridatism = MODIFIERS.register("mithridatism", MithridatismModifier::new);
  public static final RegistryObject<WildfireModifier> wildfire = MODIFIERS.register("wildfire", WildfireModifier::new);
  public static final RegistryObject<PlagueModifier> plague = MODIFIERS.register("plague", PlagueModifier::new);
  public static final RegistryObject<BreathtakingModifier> breathtaking = MODIFIERS.register("breathtaking", BreathtakingModifier::new);
  public static final RegistryObject<FirebreathModifier> firebreath = MODIFIERS.register("firebreath", FirebreathModifier::new);
  public static final RegistryObject<ChrysophiliteModifier> chrysophilite = MODIFIERS.register("chrysophilite", ChrysophiliteModifier::new);
  public static final RegistryObject<GoldGuardModifier> goldGuard = MODIFIERS.register("gold_guard", GoldGuardModifier::new);
  public static final RegistryObject<RevengeModifier> revenge = MODIFIERS.register("revenge", RevengeModifier::new);
  // disguise
  public static final RegistryObject<MobDisguiseModifier> creeperDisguise = MODIFIERS.register("creeper_disguise", () -> new MobDisguiseModifier(0x95D78E, EntityType.CREEPER));
  public static final RegistryObject<MobDisguiseModifier> endermanDisguise = MODIFIERS.register("enderman_disguise", () -> new MobDisguiseModifier(0xCC00FA, EntityType.ENDERMAN));
  public static final RegistryObject<MobDisguiseModifier> skeletonDisguise = MODIFIERS.register("skeleton_disguise", () -> new MobDisguiseModifier(0xD3D3D3, EntityType.SKELETON));
  public static final RegistryObject<MobDisguiseModifier> strayDisguise = MODIFIERS.register("stray_disguise", () -> new MobDisguiseModifier(0xC5D6D5, EntityType.STRAY));
  public static final RegistryObject<MobDisguiseModifier> witherSkeletonDisguise = MODIFIERS.register("wither_skeleton_disguise", () -> new MobDisguiseModifier(0x343434, EntityType.WITHER_SKELETON));
  public static final RegistryObject<MobDisguiseModifier> spiderDisguise = MODIFIERS.register("spider_disguise", () -> new MobDisguiseModifier(0x605448, EntityType.SPIDER));
  public static final RegistryObject<MobDisguiseModifier> caveSpiderDisguise = MODIFIERS.register("cave_spider_disguise", () -> new MobDisguiseModifier(0x153A34, EntityType.CAVE_SPIDER));
  public static final RegistryObject<MobDisguiseModifier> zombieDisguise = MODIFIERS.register("zombie_disguise", () -> new MobDisguiseModifier(0x487532, EntityType.ZOMBIE));
  public static final RegistryObject<MobDisguiseModifier> huskDisguise = MODIFIERS.register("husk_disguise", () -> new MobDisguiseModifier(0x59503B, EntityType.HUSK));
  public static final RegistryObject<MobDisguiseModifier> drownedDisguise = MODIFIERS.register("drowned_disguise", () -> new MobDisguiseModifier(0x56847E, EntityType.DROWNED));
  public static final RegistryObject<MobDisguiseModifier> blazeDisguise = MODIFIERS.register("blaze_disguise", () -> new MobDisguiseModifier(0xFC9600, EntityType.BLAZE));
  public static final RegistryObject<MobDisguiseModifier> piglinDisguise = MODIFIERS.register("piglin_disguise", () -> new MobDisguiseModifier(0xE8A074, EntityType.PIGLIN));
  public static final RegistryObject<MobDisguiseModifier> piglinBruteDisguise = MODIFIERS.register("piglin_brute_disguise", () -> new MobDisguiseModifier(0xF5DA2A, EntityType.field_242287_aj));
  public static final RegistryObject<MobDisguiseModifier> zombifiedPiglinDisguise = MODIFIERS.register("zombified_piglin_disguise", () -> new MobDisguiseModifier(0x698E45, EntityType.ZOMBIFIED_PIGLIN));

  // mod compat
  public static final RegistryObject<TOPModifier> theOneProbe = MODIFIERS.register("the_one_probe", TOPModifier::new);

  /*
   * Internal effects
   */
  private static final IntFunction<Supplier<TinkerEffect>> MARKER_EFFECT = color -> () -> new NoMilkEffect(EffectType.BENEFICIAL, color, true);
  public static RegistryObject<BleedingEffect> bleeding = POTIONS.register("bleeding", BleedingEffect::new);
  public static RegistryObject<MagneticEffect> magneticEffect = POTIONS.register("magnetic", MagneticEffect::new);
  public static RegistryObject<TinkerEffect> momentumEffect = POTIONS.register("momentum", MARKER_EFFECT.apply(0x60496b));
  public static RegistryObject<TinkerEffect> insatiableEffect = POTIONS.register("insatiable", MARKER_EFFECT.apply(0x9261cc));
  public static RegistryObject<TinkerEffect> teleportCooldownEffect = POTIONS.register("teleport_cooldown", () -> new NoMilkEffect(EffectType.HARMFUL, 0xCC00FA, true));
  public static RegistryObject<TinkerEffect> fireballCooldownEffect = POTIONS.register("fireball_cooldown", () -> new NoMilkEffect(EffectType.HARMFUL, 0xFC9600, true));
  public static RegistryObject<TinkerEffect> calcifiedEffect = POTIONS.register("calcified", () -> new NoMilkEffect(EffectType.BENEFICIAL, -1, true));

  /*
   * Recipes
   */
  public static final RegistryObject<ModifierRecipe.Serializer> modifierSerializer = RECIPE_SERIALIZERS.register("modifier", ModifierRecipe.Serializer::new);
  public static final RegistryObject<IncrementalModifierRecipe.Serializer> incrementalModifierSerializer = RECIPE_SERIALIZERS.register("incremental_modifier", IncrementalModifierRecipe.Serializer::new);
  public static final RegistryObject<OverslimeModifierRecipe.Serializer> overslimeSerializer = RECIPE_SERIALIZERS.register("overslime_modifier", OverslimeModifierRecipe.Serializer::new);
  public static final RegistryObject<ModifierRemovalRecipe.Serializer> removeModifierSerializer = RECIPE_SERIALIZERS.register("remove_modifier", ModifierRemovalRecipe.Serializer::new);
  public static final RegistryObject<ModifierSalvage.Serializer> modifierSalvageSerializer = RECIPE_SERIALIZERS.register("modifier_salvage", ModifierSalvage.Serializer::new);
  public static final RegistryObject<IncrementalModifierSalvage.Serializer> incrementalModifierSalvageSerializer = RECIPE_SERIALIZERS.register("incremental_modifier_salvage", IncrementalModifierSalvage.Serializer::new);
  public static final RegistryObject<SpecialRecipeSerializer<CreativeSlotRecipe>> creativeSlotSerializer = RECIPE_SERIALIZERS.register("creative_slot_modifier", () -> new SpecialRecipeSerializer<>(CreativeSlotRecipe::new));
  // modifiers
  public static final RegistryObject<SpillingRecipe.Serializer> spillingSerializer = RECIPE_SERIALIZERS.register("spilling", SpillingRecipe.Serializer::new);
  public static final RegistryObject<ModifierRepairRecipeSerializer<?>> modifierRepair = RECIPE_SERIALIZERS.register("modifier_repair", () -> new ModifierRepairRecipeSerializer<>(ModifierRepairTinkerStationRecipe::new));
  public static final RegistryObject<ModifierRepairRecipeSerializer<?>> craftingModifierRepair = RECIPE_SERIALIZERS.register("crafting_modifier_repair", () -> new ModifierRepairRecipeSerializer<>(ModifierRepairCraftingRecipe::new));
  // severing
  public static final RegistryObject<SeveringRecipe.Serializer> severingSerializer = RECIPE_SERIALIZERS.register("severing", SeveringRecipe.Serializer::new);
  public static final RegistryObject<AgeableSeveringRecipe.Serializer> ageableSeveringSerializer = RECIPE_SERIALIZERS.register("ageable_severing", AgeableSeveringRecipe.Serializer::new);
  // special severing
  public static final RegistryObject<SpecialRecipeSerializer<PlayerBeheadingRecipe>> playerBeheadingSerializer = RECIPE_SERIALIZERS.register("player_beheading", () -> new SpecialRecipeSerializer<>(PlayerBeheadingRecipe::new));
  public static final RegistryObject<SpecialRecipeSerializer<SnowGolemBeheadingRecipe>> snowGolemBeheadingSerializer = RECIPE_SERIALIZERS.register("snow_golem_beheading", () -> new SpecialRecipeSerializer<>(SnowGolemBeheadingRecipe::new));
  public static final RegistryObject<SpecialRecipeSerializer<MooshroomDemushroomingRecipe>> mooshroomDemushroomingSerializer = RECIPE_SERIALIZERS.register("mooshroom_demushrooming", () -> new SpecialRecipeSerializer<>(MooshroomDemushroomingRecipe::new));
  public static final RegistryObject<SpecialRecipeSerializer<SheepShearingRecipe>> sheepShearing = RECIPE_SERIALIZERS.register("sheep_shearing", () -> new SpecialRecipeSerializer<>(SheepShearingRecipe::new));

  /**
   * Loot
   */
  public static final RegistryObject<ModifierLootModifier.Serializer> modifierLootModifier = GLOBAL_LOOT_MODIFIERS.register("modifier_hook", ModifierLootModifier.Serializer::new);
  public static LootConditionType chrysophiliteLootCondition;
  public static LootFunctionType chrysophiliteBonusFunction;

  /*
   * Events
   */

  @SubscribeEvent
  void registerSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
    SpillingRecipeLookup.registerEffect(TConstruct.getResource("damage"), DamageSpillingEffect.LOADER);
    SpillingRecipeLookup.registerEffect(TConstruct.getResource("effect"), EffectSpillingEffect.LOADER);
    SpillingRecipeLookup.registerEffect(TConstruct.getResource("set_fire"), SetFireSpillingEffect.LOADER);
    SpillingRecipeLookup.registerEffect(TConstruct.getResource("cure_effects"), CureEffectsSpillingEffect.LOADER);
    SpillingRecipeLookup.registerEffect(TConstruct.getResource("teleport"), TeleportSpillingEffect.LOADER);
    SpillingRecipeLookup.registerEffect(TConstruct.getResource("restore_hunger"), RestoreHungerSpillingEffect.LOADER);
    SpillingRecipeLookup.registerEffect(TConstruct.getResource("extinguish"), ExtinguishSpillingEffect.LOADER);
    SpillingRecipeLookup.registerEffect(TConstruct.getResource("potion_fluid"), PotionFluidEffect.LOADER);
    SpillingRecipeLookup.registerEffect(TConstruct.getResource("calcified"), StrongBonesModifier.SPILLING_EFFECT_LOADER);
  }

  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    TinkerDataCapability.register();
    PersistentDataCapability.register();
  }

  @SubscribeEvent
  void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
    chrysophiliteLootCondition = Registry.register(Registry.LOOT_CONDITION_TYPE, ChrysophiliteLootCondition.ID, new LootConditionType(ChrysophiliteLootCondition.SERIALIZER));
    chrysophiliteBonusFunction = Registry.register(Registry.LOOT_FUNCTION_TYPE, ChrysophiliteBonusFunction.ID, new LootFunctionType(ChrysophiliteBonusFunction.SERIALIZER));
  }
}
