package slimeknights.tconstruct.tools;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.dynamic.ConditionalDamageModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.ConditionalMiningSpeedModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.ExtraModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.LootModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.MobDisguiseModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.StatBoostModifier;
import slimeknights.tconstruct.library.modifiers.impl.SingleLevelModifier;
import slimeknights.tconstruct.library.modifiers.impl.TankModifier;
import slimeknights.tconstruct.library.modifiers.impl.TotalArmorLevelModifier;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay.UniqueForLevels;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierSalvage;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.SwappableModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.AgeableSeveringRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.SpillingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.ConditionalSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.CureEffectsSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.DamageSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.EffectSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.ExtinguishSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.ISpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.PotionFluidEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.RemoveEffectSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.RestoreHungerSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.SetFireSpillingEffect;
import slimeknights.tconstruct.library.recipe.modifiers.spilling.effects.TeleportSpillingEffect;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairCraftingRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairRecipeSerializer;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairTinkerStationRecipe;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.tools.data.ModifierProvider;
import slimeknights.tconstruct.tools.data.ModifierRecipeProvider;
import slimeknights.tconstruct.tools.item.CreativeSlotItem;
import slimeknights.tconstruct.tools.item.DragonScaleItem;
import slimeknights.tconstruct.tools.modifiers.ModifierLootModifier;
import slimeknights.tconstruct.tools.modifiers.ability.UnbreakableModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.BouncyModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.DoubleJumpModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.PocketsModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ShieldStrapModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.SlurpingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ToolBeltModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.UnarmedModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ZoomModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.walker.BlockTransformWalkerModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.walker.FlamewakeModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.walker.FrostWalkerModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.walker.PlowingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.walker.SnowdriftModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockTransformModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.FirestarterModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.HarvestAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.PathingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.ShearsAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.SilkyShearsAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.TillingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.AutosmeltModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.BucketingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.DuelWieldingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.ExchangingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.GlowingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.MeltingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.OffhandAttackModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.SpillingModifier;
import slimeknights.tconstruct.tools.modifiers.defense.BlastProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.DragonbornModifier;
import slimeknights.tconstruct.tools.modifiers.defense.FireProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.MagicProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.MeleeProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.ProjectileProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.TurtleShellModifier;
import slimeknights.tconstruct.tools.modifiers.effect.BleedingEffect;
import slimeknights.tconstruct.tools.modifiers.effect.MagneticEffect;
import slimeknights.tconstruct.tools.modifiers.effect.NoMilkEffect;
import slimeknights.tconstruct.tools.modifiers.loot.ChrysophiliteBonusFunction;
import slimeknights.tconstruct.tools.modifiers.loot.ChrysophiliteLootCondition;
import slimeknights.tconstruct.tools.modifiers.slotless.CreativeSlotModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.DyedModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.EmbellishmentModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.FarsightedModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.NearsightedModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.StatOverrideModifier;
import slimeknights.tconstruct.tools.modifiers.traits.DamageSpeedTradeModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.CultivatedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.DenseModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.EnderportingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.OvercastModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.OvergrowthModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.OverlordModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.OverworkedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.SolarPoweredModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.StoneshieldModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.TannedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.TastyModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.AirborneModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.DwarvenModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.MaintainedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.MomentumModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.SearingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.TemperateModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.ConductingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.DecayModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.InsatibleModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.InvariantModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.LaceratingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.NecroticModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.RagingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.BoonOfSssssModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.BreathtakingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.ChrysophiliteModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.EnderdodgingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.FirebreathModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.FrosttouchModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.GoldGuardModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.MithridatismModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.PlagueModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.RevengeModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.SelfDestructiveModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.StrongBonesModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.WildfireModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.WitheredModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.FeatherFallingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.HasteArmorModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.ItemFrameModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.LeapingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.LightspeedArmorModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.PocketChainModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.RespirationModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.RicochetModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.SoulSpeedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.SpringyModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.StickyModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.ThornsModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.ExperiencedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.MagneticModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.OffhandedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.OverforcedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.ReinforcedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.SoulboundModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.TOPModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.harvest.BlastingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.harvest.HydraulicModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.harvest.LightspeedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.FieryModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.KnockbackModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.PaddedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.PiercingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.SeveringModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.SweepingEdgeModifier;
import slimeknights.tconstruct.tools.recipe.ArmorDyeingRecipe;
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
  @SuppressWarnings("deprecation")
  public TinkerModifiers() {
    ModifierManager.INSTANCE.init();
    DynamicModifier.init();
  }

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
  public static final ItemObject<Item> dragonScale = ITEMS.register("dragon_scale", () -> new DragonScaleItem(new Item.Properties().tab(TAB_GENERAL).rarity(Rarity.RARE)));
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

  /*
   * Modifiers
   */
  // durability
  public static final StaticModifier<ReinforcedModifier> reinforced = MODIFIERS.register("reinforced", ReinforcedModifier::new);
  public static final StaticModifier<OverforcedModifier> overforced = MODIFIERS.register("overforced", OverforcedModifier::new);
  public static final StaticModifier<SoulboundModifier> soulbound = MODIFIERS.register("soulbound", SoulboundModifier::new);
  public static final StaticModifier<OverslimeModifier> overslime = MODIFIERS.register("overslime", OverslimeModifier::new);

  // general effects
  public static final StaticModifier<ExperiencedModifier> experienced = MODIFIERS.register("experienced", ExperiencedModifier::new);
  public static final StaticModifier<MagneticModifier> magnetic = MODIFIERS.register("magnetic", MagneticModifier::new);
  public static final StaticModifier<OffhandedModifier> offhanded = MODIFIERS.register("offhanded", OffhandedModifier::new);
  public static final StaticModifier<FarsightedModifier> farsighted = MODIFIERS.register("farsighted", FarsightedModifier::new);
  public static final StaticModifier<NearsightedModifier> nearsighted = MODIFIERS.register("nearsighted", NearsightedModifier::new);

  // harvest
  public static final DynamicModifier<Modifier> haste = MODIFIERS.registerDynamic("haste", Modifier.class);
  public static final StaticModifier<BlastingModifier> blasting = MODIFIERS.register("blasting", BlastingModifier::new);
  public static final StaticModifier<HydraulicModifier> hydraulic = MODIFIERS.register("hydraulic", HydraulicModifier::new);
  public static final StaticModifier<LightspeedModifier> lightspeed = MODIFIERS.register("lightspeed", LightspeedModifier::new);

  // weapon
  public static final StaticModifier<KnockbackModifier> knockback = MODIFIERS.register("knockback", KnockbackModifier::new);
  public static final StaticModifier<PaddedModifier> padded = MODIFIERS.register("padded", PaddedModifier::new);
  public static final StaticModifier<FieryModifier> fiery = MODIFIERS.register("fiery", FieryModifier::new);
  public static final StaticModifier<SeveringModifier> severing = MODIFIERS.register("severing", SeveringModifier::new);

  // damage boost
  public static final StaticModifier<PiercingModifier> piercing = MODIFIERS.register("piercing", PiercingModifier::new);
  public static final StaticModifier<SweepingEdgeModifier> sweeping = MODIFIERS.register("sweeping_edge", SweepingEdgeModifier::new);

  // armor
  // protection
  public static final StaticModifier<ProtectionModifier> protection = MODIFIERS.register("protection", ProtectionModifier::new);
  public static final StaticModifier<MeleeProtectionModifier> meleeProtection = MODIFIERS.register("melee_protection", MeleeProtectionModifier::new);
  public static final StaticModifier<FireProtectionModifier> fireProtection = MODIFIERS.register("fire_protection", FireProtectionModifier::new);
  public static final StaticModifier<BlastProtectionModifier> blastProtection = MODIFIERS.register("blast_protection", BlastProtectionModifier::new);
  public static final StaticModifier<MagicProtectionModifier> magicProtection = MODIFIERS.register("magic_protection", MagicProtectionModifier::new);
  public static final StaticModifier<ProjectileProtectionModifier> projectileProtection = MODIFIERS.register("projectile_protection", ProjectileProtectionModifier::new);
  public static final StaticModifier<TurtleShellModifier> turtleShell = MODIFIERS.register("turtle_shell", TurtleShellModifier::new);
  public static final StaticModifier<DragonbornModifier> dragonborn = MODIFIERS.register("dragonborn", DragonbornModifier::new);
  // general
  public static final DynamicModifier<Modifier> golden = MODIFIERS.registerDynamic("golden", Modifier.class);
  public static final StaticModifier<RicochetModifier> ricochet = MODIFIERS.register("ricochet", RicochetModifier::new);
  public static final StaticModifier<EmbellishmentModifier> embellishment = MODIFIERS.register("embellishment", EmbellishmentModifier::new);
  public static final StaticModifier<DyedModifier> dyed = MODIFIERS.register("dyed", DyedModifier::new);
  // counterattack
  public static final StaticModifier<ThornsModifier> thorns = MODIFIERS.register("thorns", ThornsModifier::new);
  public static final StaticModifier<SpringyModifier> springy = MODIFIERS.register("springy", SpringyModifier::new);
  public static final StaticModifier<StickyModifier> sticky = MODIFIERS.register("sticky", StickyModifier::new);
  // helmet
  public static final StaticModifier<RespirationModifier> respiration = MODIFIERS.register("respiration", RespirationModifier::new);
  public static final StaticModifier<ItemFrameModifier> itemFrame = MODIFIERS.register("item_frame", ItemFrameModifier::new);
  public static final StaticModifier<ZoomModifier> zoom = MODIFIERS.register("zoom", ZoomModifier::new);
  public static final StaticModifier<SlurpingModifier> slurping = MODIFIERS.register("slurping", SlurpingModifier::new);
  public static final StaticModifier<TotalArmorLevelModifier> aquaAffinity = MODIFIERS.register("aqua_affinity", () -> new TotalArmorLevelModifier(TinkerDataKeys.AQUA_AFFINITY, true));
  // chestplate
  public static final StaticModifier<HasteArmorModifier> hasteArmor = MODIFIERS.register("haste_armor", HasteArmorModifier::new);
  public static final StaticModifier<UnarmedModifier> unarmed = MODIFIERS.register("unarmed", UnarmedModifier::new);
  // leggings
  public static final StaticModifier<LeapingModifier> leaping = MODIFIERS.register("leaping", LeapingModifier::new);
  public static final StaticModifier<PocketsModifier> pockets = MODIFIERS.register("pockets", PocketsModifier::new);
  public static final StaticModifier<ShieldStrapModifier> shieldStrap = MODIFIERS.register("shield_strap", ShieldStrapModifier::new);
  public static final StaticModifier<ToolBeltModifier> toolBelt = MODIFIERS.register("tool_belt", ToolBeltModifier::new);
  public static final StaticModifier<PocketChainModifier> pocketChain = MODIFIERS.register("pocket_chain", PocketChainModifier::new);
  // boots
  public static final StaticModifier<FeatherFallingModifier> featherFalling = MODIFIERS.register("feather_falling", FeatherFallingModifier::new);
  public static final StaticModifier<SoulSpeedModifier> soulspeed = MODIFIERS.register("soulspeed", SoulSpeedModifier::new);
  public static final StaticModifier<LightspeedArmorModifier> lightspeedArmor = MODIFIERS.register("lightspeed_armor", LightspeedArmorModifier::new);
  public static final StaticModifier<DoubleJumpModifier> doubleJump = MODIFIERS.register("double_jump", DoubleJumpModifier::new);
  public static final StaticModifier<Modifier> bouncy = MODIFIERS.register("bouncy", BouncyModifier::new);
  public static final StaticModifier<FrostWalkerModifier> frostWalker = MODIFIERS.register("frost_walker", FrostWalkerModifier::new);
  public static final StaticModifier<BlockTransformWalkerModifier> pathMaker = MODIFIERS.register("path_maker", () -> new BlockTransformWalkerModifier(ToolActions.SHOVEL_FLATTEN, SoundEvents.SHOVEL_FLATTEN));
  public static final StaticModifier<PlowingModifier> plowing = MODIFIERS.register("plowing", PlowingModifier::new);
  public static final StaticModifier<SnowdriftModifier> snowdrift = MODIFIERS.register("snowdrift", SnowdriftModifier::new);
  public static final StaticModifier<FlamewakeModifier> flamewake = MODIFIERS.register("flamewake", FlamewakeModifier::new);

  // abilities
  public static final StaticModifier<UnbreakableModifier> unbreakable = MODIFIERS.register("unbreakable", UnbreakableModifier::new);
  // weapon
  public static final StaticModifier<DuelWieldingModifier> dualWielding = MODIFIERS.register("dual_wielding", DuelWieldingModifier::new);
  // harvest
  public static final DynamicModifier<Modifier> silky = MODIFIERS.registerDynamic("silky", Modifier.class);
  public static final StaticModifier<AutosmeltModifier> autosmelt = MODIFIERS.register("autosmelt", AutosmeltModifier::new);
  public static final StaticModifier<Modifier> expanded = MODIFIERS.register("expanded", Modifier::new);
  public static final StaticModifier<ExchangingModifier> exchanging = MODIFIERS.register("exchanging", ExchangingModifier::new);

  // fluid abilities
  public static final StaticModifier<MeltingModifier> melting = MODIFIERS.register("melting", MeltingModifier::new);
  public static final StaticModifier<TankModifier> tank = MODIFIERS.register("tank", () -> new TankModifier(FluidAttributes.BUCKET_VOLUME));
  public static final StaticModifier<BucketingModifier> bucketing = MODIFIERS.register("bucketing", BucketingModifier::new);
  public static final StaticModifier<SpillingModifier> spilling = MODIFIERS.register("spilling", SpillingModifier::new);
  
  // right click abilities
  public static final StaticModifier<GlowingModifier> glowing = MODIFIERS.register("glowing", GlowingModifier::new);
  public static final StaticModifier<BlockTransformModifier> pathing = MODIFIERS.register("pathing", () -> new PathingModifier(75));
  public static final StaticModifier<BlockTransformModifier> stripping = MODIFIERS.register("stripping", () -> new BlockTransformModifier(75, ToolActions.AXE_STRIP, SoundEvents.AXE_STRIP, false));
  public static final StaticModifier<BlockTransformModifier> tilling = MODIFIERS.register("tilling", () -> new TillingModifier(75));
  public static final StaticModifier<FirestarterModifier> firestarter = MODIFIERS.register("firestarter", () -> new FirestarterModifier(70));
  public static final StaticModifier<SingleLevelModifier> fireprimer = MODIFIERS.register("fireprimer", SingleLevelModifier::new);

  // internal abilities
  public static final StaticModifier<BlockTransformModifier> shovelFlatten = MODIFIERS.register("shovel_flatten", () -> new PathingModifier(Integer.MIN_VALUE + 50));
  public static final StaticModifier<BlockTransformModifier> axeStrip = MODIFIERS.register("axe_strip", () -> new BlockTransformModifier(Integer.MIN_VALUE + 50, ToolActions.AXE_STRIP, SoundEvents.AXE_STRIP, false));
  public static final StaticModifier<BlockTransformModifier> axeScrape = MODIFIERS.register("axe_scrape", () -> new BlockTransformModifier(Integer.MIN_VALUE + 49, ToolActions.AXE_SCRAPE, SoundEvents.AXE_SCRAPE, false, 3005));
  public static final StaticModifier<BlockTransformModifier> axeWaxOff = MODIFIERS.register("axe_wax_off", () -> new BlockTransformModifier(Integer.MIN_VALUE + 48, ToolActions.AXE_WAX_OFF, SoundEvents.AXE_WAX_OFF, false, 3004));
  public static final StaticModifier<BlockTransformModifier> hoeTill = MODIFIERS.register("hoe_till", () -> new TillingModifier(Integer.MIN_VALUE + 50));
  public static final StaticModifier<FirestarterModifier> firestarterHidden = MODIFIERS.register("firestarter_hidden", () -> new FirestarterModifier(Integer.MIN_VALUE + 50));

  public static final StaticModifier<ShearsAbilityModifier> shears = MODIFIERS.register("shears", () -> new ShearsAbilityModifier(0, Short.MIN_VALUE));
  public static final StaticModifier<SilkyShearsAbilityModifier> silkyShears = MODIFIERS.register("silky_shears", () -> new SilkyShearsAbilityModifier(0, Short.MIN_VALUE));
  public static final StaticModifier<SilkyShearsAbilityModifier> aoeSilkyShears = MODIFIERS.register("silky_aoe_shears", () -> new SilkyShearsAbilityModifier(1, Short.MIN_VALUE));
  public static final StaticModifier<HarvestAbilityModifier> harvest = MODIFIERS.register("harvest", () -> new HarvestAbilityModifier(Integer.MIN_VALUE + 51));
  public static final StaticModifier<OffhandAttackModifier> offhandAttack = MODIFIERS.register("offhand_attack", OffhandAttackModifier::new);

  // creative
  public static final StaticModifier<CreativeSlotModifier> creativeSlot = MODIFIERS.register("creative_slot", CreativeSlotModifier::new);
  public static final StaticModifier<StatOverrideModifier> statOverride = MODIFIERS.register("stat_override", StatOverrideModifier::new);

  // traits - tier 1
  public static final StaticModifier<CultivatedModifier> cultivated = MODIFIERS.register("cultivated", CultivatedModifier::new);
  public static final StaticModifier<DamageSpeedTradeModifier> jagged = MODIFIERS.register("jagged", () -> new DamageSpeedTradeModifier(0.005f));
  public static final StaticModifier<DamageSpeedTradeModifier> stonebound = MODIFIERS.register("stonebound", () -> new DamageSpeedTradeModifier(-0.005f));
  // traits - tier 1 nether
  public static final StaticModifier<NecroticModifier> necrotic = MODIFIERS.register("necrotic", NecroticModifier::new);
  // traits - tier 1 bindings
  public static final StaticModifier<TannedModifier> tanned = MODIFIERS.register("tanned", TannedModifier::new);
  public static final StaticModifier<SolarPoweredModifier> solarPowered = MODIFIERS.register("solar_powered", SolarPoweredModifier::new);
  // traits - tier 2
  public static final StaticModifier<SearingModifier> searing = MODIFIERS.register("searing", SearingModifier::new);
  public static final StaticModifier<DwarvenModifier> dwarven = MODIFIERS.register("dwarven", DwarvenModifier::new);
  public static final StaticModifier<OvergrowthModifier> overgrowth = MODIFIERS.register("overgrowth", OvergrowthModifier::new);
  public static final StaticModifier<RagingModifier> raging = MODIFIERS.register("raging", RagingModifier::new);
  public static final StaticModifier<AirborneModifier> airborne = MODIFIERS.register("airborne", AirborneModifier::new);
  // traits - tier 3
  public static final StaticModifier<OvercastModifier> overcast = MODIFIERS.register("overcast", OvercastModifier::new);
  public static final StaticModifier<LaceratingModifier> lacerating = MODIFIERS.register("lacerating", LaceratingModifier::new);
  public static final StaticModifier<TastyModifier> tasty = MODIFIERS.register("tasty", TastyModifier::new);
  // traits - tier 4
  public static final StaticModifier<OverlordModifier> overlord = MODIFIERS.register("overlord", OverlordModifier::new);
  public static final StaticModifier<MomentumModifier> momentum = MODIFIERS.register("momentum", MomentumModifier::new);
  public static final StaticModifier<InsatibleModifier> insatiable = MODIFIERS.register("insatiable", InsatibleModifier::new);
  public static final StaticModifier<ConductingModifier> conducting = MODIFIERS.register("conducting", ConductingModifier::new);
  // traits - tier 5
  public static final StaticModifier<EnderportingModifier> enderporting = MODIFIERS.register("enderporting", EnderportingModifier::new);

  // traits - mod compat tier 2
  public static final StaticModifier<DenseModifier> dense = MODIFIERS.register("dense", DenseModifier::new);
  public static final StaticModifier<StoneshieldModifier> stoneshield = MODIFIERS.register("stoneshield", StoneshieldModifier::new);
  // traits - mod compat tier 3
  public static final StaticModifier<MaintainedModifier> maintained = MODIFIERS.register("maintained", MaintainedModifier::new);
  public static final StaticModifier<TemperateModifier> temperate = MODIFIERS.register("temperate", TemperateModifier::new);
  public static final StaticModifier<InvariantModifier> invariant = MODIFIERS.register("invariant", InvariantModifier::new);
  public static final StaticModifier<DecayModifier> decay = MODIFIERS.register("decay", DecayModifier::new);
  public static final StaticModifier<OverworkedModifier> overworked = MODIFIERS.register("overworked", OverworkedModifier::new);
  // experienced is also an upgrade

  // traits - slimeskull
  public static final StaticModifier<SelfDestructiveModifier> selfDestructive = MODIFIERS.register("self_destructive", SelfDestructiveModifier::new);
  public static final StaticModifier<EnderdodgingModifier> enderdodging = MODIFIERS.register("enderdodging", EnderdodgingModifier::new);
  public static final StaticModifier<StrongBonesModifier> strongBones = MODIFIERS.register("strong_bones", StrongBonesModifier::new);
  public static final StaticModifier<FrosttouchModifier> frosttouch = MODIFIERS.register("frosttouch", FrosttouchModifier::new);
  public static final StaticModifier<WitheredModifier> withered = MODIFIERS.register("withered", WitheredModifier::new);
  public static final StaticModifier<BoonOfSssssModifier> boonOfSssss = MODIFIERS.register("boon_of_sssss", BoonOfSssssModifier::new);
  public static final StaticModifier<MithridatismModifier> mithridatism = MODIFIERS.register("mithridatism", MithridatismModifier::new);
  public static final StaticModifier<WildfireModifier> wildfire = MODIFIERS.register("wildfire", WildfireModifier::new);
  public static final StaticModifier<PlagueModifier> plague = MODIFIERS.register("plague", PlagueModifier::new);
  public static final StaticModifier<BreathtakingModifier> breathtaking = MODIFIERS.register("breathtaking", BreathtakingModifier::new);
  public static final StaticModifier<FirebreathModifier> firebreath = MODIFIERS.register("firebreath", FirebreathModifier::new);
  public static final StaticModifier<ChrysophiliteModifier> chrysophilite = MODIFIERS.register("chrysophilite", ChrysophiliteModifier::new);
  public static final StaticModifier<GoldGuardModifier> goldGuard = MODIFIERS.register("gold_guard", GoldGuardModifier::new);
  public static final StaticModifier<RevengeModifier> revenge = MODIFIERS.register("revenge", RevengeModifier::new);

  // mod compat
  public static final StaticModifier<TOPModifier> theOneProbe = MODIFIERS.register("the_one_probe", TOPModifier::new);

  /*
   * Internal effects
   */
  private static final IntFunction<Supplier<TinkerEffect>> MARKER_EFFECT = color -> () -> new NoMilkEffect(MobEffectCategory.BENEFICIAL, color, true);
  public static RegistryObject<BleedingEffect> bleeding = MOB_EFFECTS.register("bleeding", BleedingEffect::new);
  public static RegistryObject<MagneticEffect> magneticEffect = MOB_EFFECTS.register("magnetic", MagneticEffect::new);
  public static RegistryObject<TinkerEffect> momentumEffect = MOB_EFFECTS.register("momentum", MARKER_EFFECT.apply(0x60496b));
  public static RegistryObject<TinkerEffect> insatiableEffect = MOB_EFFECTS.register("insatiable", MARKER_EFFECT.apply(0x9261cc));
  public static RegistryObject<TinkerEffect> teleportCooldownEffect = MOB_EFFECTS.register("teleport_cooldown", () -> new NoMilkEffect(MobEffectCategory.HARMFUL, 0xCC00FA, true));
  public static RegistryObject<TinkerEffect> fireballCooldownEffect = MOB_EFFECTS.register("fireball_cooldown", () -> new NoMilkEffect(MobEffectCategory.HARMFUL, 0xFC9600, true));
  public static RegistryObject<TinkerEffect> calcifiedEffect = MOB_EFFECTS.register("calcified", () -> new NoMilkEffect(MobEffectCategory.BENEFICIAL, -1, true));

  /*
   * Recipes
   */
  public static final RegistryObject<ModifierRecipe.Serializer> modifierSerializer = RECIPE_SERIALIZERS.register("modifier", ModifierRecipe.Serializer::new);
  public static final RegistryObject<IncrementalModifierRecipe.Serializer> incrementalModifierSerializer = RECIPE_SERIALIZERS.register("incremental_modifier", IncrementalModifierRecipe.Serializer::new);
  public static final RegistryObject<SwappableModifierRecipe.Serializer> swappableModifierSerializer = RECIPE_SERIALIZERS.register("swappable_modifier", SwappableModifierRecipe.Serializer::new);
  public static final RegistryObject<OverslimeModifierRecipe.Serializer> overslimeSerializer = RECIPE_SERIALIZERS.register("overslime_modifier", OverslimeModifierRecipe.Serializer::new);
  public static final RegistryObject<ModifierRemovalRecipe.Serializer> removeModifierSerializer = RECIPE_SERIALIZERS.register("remove_modifier", ModifierRemovalRecipe.Serializer::new);
  public static final RegistryObject<ModifierSalvage.Serializer> modifierSalvageSerializer = RECIPE_SERIALIZERS.register("modifier_salvage", ModifierSalvage.Serializer::new);
  public static final RegistryObject<ArmorDyeingRecipe.Serializer> armorDyeingSerializer = RECIPE_SERIALIZERS.register("armor_dyeing_modifier", ArmorDyeingRecipe.Serializer::new);
  public static final RegistryObject<SimpleRecipeSerializer<CreativeSlotRecipe>> creativeSlotSerializer = RECIPE_SERIALIZERS.register("creative_slot_modifier", () -> new SimpleRecipeSerializer<>(CreativeSlotRecipe::new));
  // modifiers
  public static final RegistryObject<SpillingRecipe.Serializer> spillingSerializer = RECIPE_SERIALIZERS.register("spilling", SpillingRecipe.Serializer::new);
  public static final RegistryObject<ModifierRepairRecipeSerializer<?>> modifierRepair = RECIPE_SERIALIZERS.register("modifier_repair", () -> new ModifierRepairRecipeSerializer<>((ResourceLocation id, ModifierId modifier, Ingredient ingredient, int repairAmount) -> new ModifierRepairTinkerStationRecipe(id, modifier, ingredient, repairAmount)));
  public static final RegistryObject<ModifierRepairRecipeSerializer<?>> craftingModifierRepair = RECIPE_SERIALIZERS.register("crafting_modifier_repair", () -> new ModifierRepairRecipeSerializer<>(ModifierRepairCraftingRecipe::new));
  // severing
  public static final RegistryObject<SeveringRecipe.Serializer> severingSerializer = RECIPE_SERIALIZERS.register("severing", SeveringRecipe.Serializer::new);
  public static final RegistryObject<AgeableSeveringRecipe.Serializer> ageableSeveringSerializer = RECIPE_SERIALIZERS.register("ageable_severing", AgeableSeveringRecipe.Serializer::new);
  // special severing
  public static final RegistryObject<SimpleRecipeSerializer<PlayerBeheadingRecipe>> playerBeheadingSerializer = RECIPE_SERIALIZERS.register("player_beheading", () -> new SimpleRecipeSerializer<>(PlayerBeheadingRecipe::new));
  public static final RegistryObject<SimpleRecipeSerializer<SnowGolemBeheadingRecipe>> snowGolemBeheadingSerializer = RECIPE_SERIALIZERS.register("snow_golem_beheading", () -> new SimpleRecipeSerializer<>(SnowGolemBeheadingRecipe::new));
  public static final RegistryObject<SimpleRecipeSerializer<MooshroomDemushroomingRecipe>> mooshroomDemushroomingSerializer = RECIPE_SERIALIZERS.register("mooshroom_demushrooming", () -> new SimpleRecipeSerializer<>(MooshroomDemushroomingRecipe::new));
  public static final RegistryObject<SimpleRecipeSerializer<SheepShearingRecipe>> sheepShearing = RECIPE_SERIALIZERS.register("sheep_shearing", () -> new SimpleRecipeSerializer<>(SheepShearingRecipe::new));

  /**
   * Loot
   */
  public static final RegistryObject<ModifierLootModifier.Serializer> modifierLootModifier = GLOBAL_LOOT_MODIFIERS.register("modifier_hook", ModifierLootModifier.Serializer::new);
  public static LootItemConditionType chrysophiliteLootCondition;
  public static LootItemFunctionType chrysophiliteBonusFunction;

  /*
   * Events
   */

  @SubscribeEvent
  void registerSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
    ISpillingEffect.LOADER.register(TConstruct.getResource("conditional"),    ConditionalSpillingEffect.LOADER);
    ISpillingEffect.LOADER.register(TConstruct.getResource("cure_effects"),   CureEffectsSpillingEffect.LOADER);
    ISpillingEffect.LOADER.register(TConstruct.getResource("remove_effect"),  RemoveEffectSpillingEffect.LOADER);
    ISpillingEffect.LOADER.register(TConstruct.getResource("damage"),         DamageSpillingEffect.LOADER);
    ISpillingEffect.LOADER.register(TConstruct.getResource("effect"),         EffectSpillingEffect.LOADER);
    ISpillingEffect.LOADER.register(TConstruct.getResource("extinguish"),     ExtinguishSpillingEffect.LOADER);
    ISpillingEffect.LOADER.register(TConstruct.getResource("potion_fluid"),   PotionFluidEffect.LOADER);
    ISpillingEffect.LOADER.register(TConstruct.getResource("restore_hunger"), RestoreHungerSpillingEffect.LOADER);
    ISpillingEffect.LOADER.register(TConstruct.getResource("set_fire"),       SetFireSpillingEffect.LOADER);
    ISpillingEffect.LOADER.register(TConstruct.getResource("teleport"),       TeleportSpillingEffect.LOADER);
    ISpillingEffect.LOADER.register(TConstruct.getResource("calcified"),      StrongBonesModifier.SPILLING_EFFECT_LOADER);
    // modifier loaders
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("default"), Modifier.DEFAULT_LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("stat_boost"), StatBoostModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("extra_slot"), ExtraModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("mob_disguise"), MobDisguiseModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("conditional_damage"), ConditionalDamageModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("conditional_mining_speed"), ConditionalMiningSpeedModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("loot"), LootModifier.LOADER);
    // modifier names, sometimes I wonder if I have too many registries for tiny JSON pieces
    ModifierLevelDisplay.LOADER.register(TConstruct.getResource("default"), ModifierLevelDisplay.DEFAULT.getLoader());
    ModifierLevelDisplay.LOADER.register(TConstruct.getResource("single_level"), ModifierLevelDisplay.SINGLE_LEVEL.getLoader());
    ModifierLevelDisplay.LOADER.register(TConstruct.getResource("no_levels"), ModifierLevelDisplay.NO_LEVELS.getLoader());
    ModifierLevelDisplay.LOADER.register(TConstruct.getResource("unique"), UniqueForLevels.LOADER);
  }

  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    TinkerDataCapability.register();
    PersistentDataCapability.register();
  }

  @SubscribeEvent
  void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
    chrysophiliteLootCondition = Registry.register(Registry.LOOT_CONDITION_TYPE, ChrysophiliteLootCondition.ID, new LootItemConditionType(ChrysophiliteLootCondition.SERIALIZER));
    chrysophiliteBonusFunction = Registry.register(Registry.LOOT_FUNCTION_TYPE, ChrysophiliteBonusFunction.ID, new LootItemFunctionType(ChrysophiliteBonusFunction.SERIALIZER));
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    if (event.includeServer()) {
      generator.addProvider(new ModifierProvider(generator));
      generator.addProvider(new ModifierRecipeProvider(generator));
    }
  }
}
