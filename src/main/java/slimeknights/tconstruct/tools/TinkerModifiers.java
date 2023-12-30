package slimeknights.tconstruct.tools;

import net.minecraft.data.DataGenerator;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
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
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.data.tags.ModifierTagProvider;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.SingleModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.SlotTypeModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.TagModifierPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.dynamic.ComposableModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.ConditionalDamageModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.ConditionalMiningSpeedModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.EnchantmentModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.ExtraModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.InventoryMenuModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.LootModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.MobDisguiseModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.MobEffectModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.StatBoostModifier;
import slimeknights.tconstruct.library.modifiers.dynamic.SwappableExtraSlotModifier;
import slimeknights.tconstruct.library.modifiers.impl.ScaledArmorLevelModifier;
import slimeknights.tconstruct.library.modifiers.impl.SingleLevelModifier;
import slimeknights.tconstruct.library.modifiers.impl.TankModifier;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.MobDisguiseModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.IncrementalModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.RepairModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ToolActionsModule;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierSlotModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.modifiers.modules.build.SetStatModule;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.modifiers.modules.build.SwappableSlotModule;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileFlagModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.LootingModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.MeleeAttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.MobEffectModule;
import slimeknights.tconstruct.library.modifiers.modules.fluid.TankCapacityModule;
import slimeknights.tconstruct.library.modifiers.modules.fluid.TankModule;
import slimeknights.tconstruct.library.modifiers.modules.mining.ConditionalMiningSpeedModule;
import slimeknights.tconstruct.library.modifiers.spilling.ISpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.SpillingFluidManager;
import slimeknights.tconstruct.library.modifiers.spilling.effects.AddBreathSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.AddInsomniaSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.ConditionalSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.CureEffectsSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.DamageSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.EffectSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.ExtinguishSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.PotionFluidEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.RemoveEffectSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.RestoreHungerSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.SetFireSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.SetFreezeSpillingEffect;
import slimeknights.tconstruct.library.modifiers.spilling.effects.TeleportSpillingEffect;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay.UniqueForLevels;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierSalvage;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.MultilevelModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.SwappableModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.AgeableSeveringRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierMaterialRepairKitRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierMaterialRepairRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierMaterialRepairSerializer;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairCraftingRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairRecipeSerializer;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairTinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipe;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.tools.data.EnchantmentToModifierProvider;
import slimeknights.tconstruct.tools.data.ModifierProvider;
import slimeknights.tconstruct.tools.data.ModifierRecipeProvider;
import slimeknights.tconstruct.tools.data.SpillingFluidProvider;
import slimeknights.tconstruct.tools.item.CreativeSlotItem;
import slimeknights.tconstruct.tools.item.DragonScaleItem;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;
import slimeknights.tconstruct.tools.modifiers.ModifierLootModifier;
import slimeknights.tconstruct.tools.modifiers.ability.UnbreakableModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.BouncyModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.DoubleJumpModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.LongFallModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ReflectingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ShieldStrapModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.SlurpingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ToolBeltModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.UnarmedModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.WettingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ZoomModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.walker.BlockTransformWalkerModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.walker.FlamewakeModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.walker.FrostWalkerModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.walker.SnowdriftModifier;
import slimeknights.tconstruct.tools.modifiers.ability.fluid.SpittingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.fluid.SpittingModifier.FluidSpitEntity;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockTransformModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.FirestarterModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.HarvestAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.PathingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.ShearsAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.SilkyShearsAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.ability.ranged.BulkQuiverModifier;
import slimeknights.tconstruct.tools.modifiers.ability.ranged.CrystalshotModifier;
import slimeknights.tconstruct.tools.modifiers.ability.ranged.TrickQuiverModifier;
import slimeknights.tconstruct.tools.modifiers.ability.sling.BonkingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.sling.FlingingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.sling.SpringingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.sling.WarpingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.AutosmeltModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.BucketingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.DuelWieldingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.ExchangingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.GlowingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.MeltingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.OffhandAttackModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.ParryingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.SpillingModifier;
import slimeknights.tconstruct.tools.modifiers.defense.BlastProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.DragonbornModifier;
import slimeknights.tconstruct.tools.modifiers.defense.FireProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.MagicProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.MeleeProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.ProjectileProtectionModifier;
import slimeknights.tconstruct.tools.modifiers.defense.ShulkingModifier;
import slimeknights.tconstruct.tools.modifiers.defense.TurtleShellModifier;
import slimeknights.tconstruct.tools.modifiers.effect.BleedingEffect;
import slimeknights.tconstruct.tools.modifiers.effect.MagneticEffect;
import slimeknights.tconstruct.tools.modifiers.effect.NoMilkEffect;
import slimeknights.tconstruct.tools.modifiers.effect.RepulsiveEffect;
import slimeknights.tconstruct.tools.modifiers.loot.ChrysophiliteBonusFunction;
import slimeknights.tconstruct.tools.modifiers.loot.ChrysophiliteLootCondition;
import slimeknights.tconstruct.tools.modifiers.loot.HasModifierLootCondition;
import slimeknights.tconstruct.tools.modifiers.loot.ModifierBonusLootFunction;
import slimeknights.tconstruct.tools.modifiers.slotless.CreativeSlotModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.DyedModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.EmbellishmentModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.FarsightedModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.NearsightedModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.OverslimeModifier;
import slimeknights.tconstruct.tools.modifiers.slotless.StatOverrideModifier;
import slimeknights.tconstruct.tools.modifiers.traits.DamageSpeedTradeModifier;
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
import slimeknights.tconstruct.tools.modifiers.traits.melee.EnderferenceModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.InsatibleModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.InvariantModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.LaceratingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.NecroticModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.RagingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.ranged.CrystalboundModifier;
import slimeknights.tconstruct.tools.modifiers.traits.ranged.HolyModifier;
import slimeknights.tconstruct.tools.modifiers.traits.ranged.OlympicModifier;
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
import slimeknights.tconstruct.tools.modifiers.traits.skull.SelfDestructiveModifier.SelfDestructiveEffect;
import slimeknights.tconstruct.tools.modifiers.traits.skull.StrongBonesModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.WildfireModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.WitheredModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.FeatherFallingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.HasteModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.ItemFrameModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.LeapingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.LightspeedArmorModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.RicochetModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.SoulSpeedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.SpringyModifier;
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
import slimeknights.tconstruct.tools.modifiers.upgrades.ranged.FreezingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.ranged.ImpalingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.ranged.PunchModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.ranged.ScopeModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.ranged.SinistralModifier;
import slimeknights.tconstruct.tools.recipe.ArmorDyeingRecipe;
import slimeknights.tconstruct.tools.recipe.CreativeSlotRecipe;
import slimeknights.tconstruct.tools.recipe.EnchantmentConvertingRecipe;
import slimeknights.tconstruct.tools.recipe.ExtractModifierRecipe;
import slimeknights.tconstruct.tools.recipe.ModifierRemovalRecipe;
import slimeknights.tconstruct.tools.recipe.ModifierSortingRecipe;
import slimeknights.tconstruct.tools.recipe.severing.MooshroomDemushroomingRecipe;
import slimeknights.tconstruct.tools.recipe.severing.PlayerBeheadingRecipe;
import slimeknights.tconstruct.tools.recipe.severing.SheepShearingRecipe;
import slimeknights.tconstruct.tools.recipe.severing.SnowGolemBeheadingRecipe;

import java.util.function.IntFunction;
import java.util.function.Supplier;

import static slimeknights.tconstruct.tools.TinkerTools.TAB_TOOLS;

/**
 * Contains modifiers and the items or blocks used to craft modifiers
 */
@SuppressWarnings("unused")
public final class TinkerModifiers extends TinkerModule {
  private static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TConstruct.MOD_ID);

  @SuppressWarnings("deprecation")
  public TinkerModifiers() {
    ModifierManager.INSTANCE.init();
    DynamicModifier.init();
    SpillingFluidManager.INSTANCE.init();
    MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
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
  // special
  public static final ItemObject<Item> modifierCrystal = ITEMS.register("modifier_crystal", () -> new ModifierCrystalItem(new Item.Properties().tab(TAB_TOOLS).stacksTo(16)));
  public static final ItemObject<Item> creativeSlotItem = ITEMS.register("creative_slot", () -> new CreativeSlotItem(new Item.Properties().tab(TAB_TOOLS)));

  // entity
  public static final RegistryObject<EntityType<FluidSpitEntity>> fluidSpitEntity = ENTITIES.register("fluid_spit", () ->
    EntityType.Builder.<FluidSpitEntity>of(FluidSpitEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).setShouldReceiveVelocityUpdates(false));

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
  public static final StaticModifier<HasteModifier> haste = MODIFIERS.register("haste", HasteModifier::new);
  public static final StaticModifier<BlastingModifier> blasting = MODIFIERS.register("blasting", BlastingModifier::new);
  public static final StaticModifier<HydraulicModifier> hydraulic = MODIFIERS.register("hydraulic", HydraulicModifier::new);
  public static final StaticModifier<LightspeedModifier> lightspeed = MODIFIERS.register("lightspeed", LightspeedModifier::new);

  // weapon
  public static final StaticModifier<KnockbackModifier> knockback = MODIFIERS.register("knockback", KnockbackModifier::new);
  public static final StaticModifier<PaddedModifier> padded = MODIFIERS.register("padded", PaddedModifier::new);
  public static final StaticModifier<FieryModifier> fiery = MODIFIERS.register("fiery", FieryModifier::new);
  public static final StaticModifier<SeveringModifier> severing = MODIFIERS.register("severing", SeveringModifier::new);
  public static final StaticModifier<ReflectingModifier> reflecting = MODIFIERS.register("reflecting", ReflectingModifier::new);

  // damage boost
  public static final StaticModifier<PiercingModifier> piercing = MODIFIERS.register("piercing", PiercingModifier::new);
  public static final StaticModifier<SweepingEdgeModifier> sweeping = MODIFIERS.register("sweeping_edge", SweepingEdgeModifier::new);

  // ranged
  public static final StaticModifier<PunchModifier> punch = MODIFIERS.register("punch", PunchModifier::new);
  public static final StaticModifier<ImpalingModifier> impaling = MODIFIERS.register("impaling", ImpalingModifier::new);
  public static final StaticModifier<FreezingModifier> freezing = MODIFIERS.register("freezing", FreezingModifier::new);
  public static final StaticModifier<BulkQuiverModifier> bulkQuiver = MODIFIERS.register("bulk_quiver", BulkQuiverModifier::new);
  public static final StaticModifier<TrickQuiverModifier> trickQuiver = MODIFIERS.register("trick_quiver", TrickQuiverModifier::new);
  public static final StaticModifier<CrystalshotModifier> crystalshot = MODIFIERS.register("crystalshot", CrystalshotModifier::new);
  public static final StaticModifier<Modifier> multishot = MODIFIERS.register("multishot", Modifier::new);
  public static final StaticModifier<SinistralModifier> sinistral = MODIFIERS.register("sinistral", SinistralModifier::new);
  public static final StaticModifier<ScopeModifier> scope = MODIFIERS.register("scope", ScopeModifier::new);

  // armor
  // protection
  public static final StaticModifier<ProtectionModifier> protection = MODIFIERS.register("protection", ProtectionModifier::new);
  public static final StaticModifier<MeleeProtectionModifier> meleeProtection = MODIFIERS.register("melee_protection", MeleeProtectionModifier::new);
  public static final StaticModifier<FireProtectionModifier> fireProtection = MODIFIERS.register("fire_protection", FireProtectionModifier::new);
  public static final StaticModifier<BlastProtectionModifier> blastProtection = MODIFIERS.register("blast_protection", BlastProtectionModifier::new);
  public static final StaticModifier<MagicProtectionModifier> magicProtection = MODIFIERS.register("magic_protection", MagicProtectionModifier::new);
  public static final StaticModifier<ProjectileProtectionModifier> projectileProtection = MODIFIERS.register("projectile_protection", ProjectileProtectionModifier::new);
  public static final StaticModifier<TurtleShellModifier> turtleShell = MODIFIERS.register("turtle_shell", TurtleShellModifier::new);
  public static final StaticModifier<ShulkingModifier> shulking = MODIFIERS.register("shulking", ShulkingModifier::new);
  public static final StaticModifier<DragonbornModifier> dragonborn = MODIFIERS.register("dragonborn", DragonbornModifier::new);
  // general
  public static final DynamicModifier<Modifier> golden = MODIFIERS.registerDynamic("golden", Modifier.class);
  public static final StaticModifier<RicochetModifier> ricochet = MODIFIERS.register("ricochet", RicochetModifier::new);
  public static final StaticModifier<EmbellishmentModifier> embellishment = MODIFIERS.register("embellishment", EmbellishmentModifier::new);
  public static final StaticModifier<DyedModifier> dyed = MODIFIERS.register("dyed", DyedModifier::new);
  public static final StaticModifier<ScaledArmorLevelModifier> boundless = MODIFIERS.register("boundless", () -> new ScaledArmorLevelModifier(TinkerDataKeys.PROTECTION_CAP, 2.5f, true));
  // counterattack
  public static final StaticModifier<ThornsModifier> thorns = MODIFIERS.register("thorns", ThornsModifier::new);
  public static final StaticModifier<SpringyModifier> springy = MODIFIERS.register("springy", SpringyModifier::new);
  // helmet
  public static final StaticModifier<ItemFrameModifier> itemFrame = MODIFIERS.register("item_frame", ItemFrameModifier::new);
  public static final StaticModifier<ZoomModifier> zoom = MODIFIERS.register("zoom", ZoomModifier::new);
  public static final StaticModifier<SlurpingModifier> slurping = MODIFIERS.register("slurping", SlurpingModifier::new);
  // chestplate
  public static final StaticModifier<UnarmedModifier> ambidextrous = MODIFIERS.register("ambidextrous", UnarmedModifier::new);
  /** Renaming this but some addon is probably using it. You might want {@link #ambidextrous}, but you might simply want to update your logic */
  @Deprecated
  public static final StaticModifier<UnarmedModifier> unarmed = ambidextrous;
  // leggings
  public static final StaticModifier<LeapingModifier> leaping = MODIFIERS.register("leaping", LeapingModifier::new);
  public static final StaticModifier<ShieldStrapModifier> shieldStrap = MODIFIERS.register("shield_strap", ShieldStrapModifier::new);
  public static final StaticModifier<WettingModifier> wetting = MODIFIERS.register("wetting", WettingModifier::new);

  // boots
  public static final StaticModifier<FeatherFallingModifier> featherFalling = MODIFIERS.register("feather_falling", FeatherFallingModifier::new);
  public static final StaticModifier<LongFallModifier> longFall = MODIFIERS.register("long_fall", LongFallModifier::new);
  public static final StaticModifier<SoulSpeedModifier> soulspeed = MODIFIERS.register("soulspeed", SoulSpeedModifier::new);
  public static final StaticModifier<LightspeedArmorModifier> lightspeedArmor = MODIFIERS.register("lightspeed_armor", LightspeedArmorModifier::new);
  public static final StaticModifier<DoubleJumpModifier> doubleJump = MODIFIERS.register("double_jump", DoubleJumpModifier::new);
  public static final StaticModifier<Modifier> bouncy = MODIFIERS.register("bouncy", BouncyModifier::new);
  public static final StaticModifier<FrostWalkerModifier> frostWalker = MODIFIERS.register("frost_walker", FrostWalkerModifier::new);
  public static final StaticModifier<BlockTransformWalkerModifier> pathMaker = MODIFIERS.register("path_maker", () -> new BlockTransformWalkerModifier(ToolActions.SHOVEL_FLATTEN, SoundEvents.SHOVEL_FLATTEN));
  public static final StaticModifier<BlockTransformWalkerModifier> plowing = MODIFIERS.register("plowing", () -> new BlockTransformWalkerModifier(ToolActions.HOE_TILL, SoundEvents.HOE_TILL));
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
  public static final StaticModifier<SpittingModifier> spitting = MODIFIERS.register("spitting", SpittingModifier::new);
  
  // right click abilities
  public static final StaticModifier<GlowingModifier> glowing = MODIFIERS.register("glowing", GlowingModifier::new);
  public static final StaticModifier<BlockTransformModifier> pathing = MODIFIERS.register("pathing", () -> new PathingModifier(75));
  public static final StaticModifier<BlockTransformModifier> stripping = MODIFIERS.register("stripping", () -> new BlockTransformModifier(75, ToolActions.AXE_STRIP, SoundEvents.AXE_STRIP, false));
  public static final StaticModifier<BlockTransformModifier> tilling = MODIFIERS.register("tilling", () -> new BlockTransformModifier(75, ToolActions.HOE_TILL, SoundEvents.HOE_TILL, false));
  public static final StaticModifier<FirestarterModifier> firestarter = MODIFIERS.register("firestarter", () -> new FirestarterModifier(75));
  public static final StaticModifier<SingleLevelModifier> fireprimer = MODIFIERS.register("fireprimer", SingleLevelModifier::new);
  public static final StaticModifier<BlockingModifier> blocking = MODIFIERS.register("blocking", BlockingModifier::new);
  public static final StaticModifier<ParryingModifier> parrying = MODIFIERS.register("parrying", ParryingModifier::new);
  // slings
  public static final StaticModifier<FlingingModifier> flinging = MODIFIERS.register("flinging", FlingingModifier::new);
  public static final StaticModifier<SpringingModifier> springing = MODIFIERS.register("springing", SpringingModifier::new);
  public static final StaticModifier<BonkingModifier> bonking = MODIFIERS.register("bonking", BonkingModifier::new);
  public static final StaticModifier<WarpingModifier> warping = MODIFIERS.register("warping", WarpingModifier::new);


  // internal abilities
  public static final StaticModifier<BlockTransformModifier> axeScrape = MODIFIERS.register("axe_scrape", () -> new BlockTransformModifier(Integer.MIN_VALUE + 49, ToolActions.AXE_SCRAPE, SoundEvents.AXE_SCRAPE, false, 3005));
  public static final StaticModifier<BlockTransformModifier> axeWaxOff = MODIFIERS.register("axe_wax_off", () -> new BlockTransformModifier(Integer.MIN_VALUE + 48, ToolActions.AXE_WAX_OFF, SoundEvents.AXE_WAX_OFF, false, 3004));

  public static final StaticModifier<ShearsAbilityModifier> shears = MODIFIERS.register("shears", () -> new ShearsAbilityModifier(0, 70));
  public static final StaticModifier<SilkyShearsAbilityModifier> silkyShears = MODIFIERS.register("silky_shears", () -> new SilkyShearsAbilityModifier(0, 70));
  public static final StaticModifier<SilkyShearsAbilityModifier> aoeSilkyShears = MODIFIERS.register("silky_aoe_shears", () -> new SilkyShearsAbilityModifier(1, 70));
  public static final StaticModifier<HarvestAbilityModifier> harvest = MODIFIERS.register("harvest", () -> new HarvestAbilityModifier(70));
  public static final StaticModifier<OffhandAttackModifier> offhandAttack = MODIFIERS.register("offhand_attack", OffhandAttackModifier::new);

  // creative
  public static final StaticModifier<CreativeSlotModifier> creativeSlot = MODIFIERS.register("creative_slot", CreativeSlotModifier::new);
  public static final StaticModifier<StatOverrideModifier> statOverride = MODIFIERS.register("stat_override", StatOverrideModifier::new);

  // traits - tier 1
  public static final StaticModifier<DamageSpeedTradeModifier> jagged = MODIFIERS.register("jagged", () -> new DamageSpeedTradeModifier(0.005f));
  public static final StaticModifier<DamageSpeedTradeModifier> stonebound = MODIFIERS.register("stonebound", () -> new DamageSpeedTradeModifier(-0.005f));
  // traits - tier 1 nether
  public static final StaticModifier<NecroticModifier> necrotic = MODIFIERS.register("necrotic", NecroticModifier::new);
  // traits - tier 1 nether
  public static final StaticModifier<EnderferenceModifier> enderference = MODIFIERS.register("enderference", EnderferenceModifier::new);
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
  public static final StaticModifier<CrystalboundModifier> crystalbound = MODIFIERS.register("crystalbound", CrystalboundModifier::new);
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
  public static final StaticModifier<HolyModifier> holy = MODIFIERS.register("holy", HolyModifier::new);
  public static final StaticModifier<OlympicModifier> olympic = MODIFIERS.register("olympic", OlympicModifier::new);
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
  public static final RegistryObject<BleedingEffect> bleeding = MOB_EFFECTS.register("bleeding", BleedingEffect::new);
  public static final RegistryObject<MagneticEffect> magneticEffect = MOB_EFFECTS.register("magnetic", MagneticEffect::new);
  public static final RegistryObject<RepulsiveEffect> repulsiveEffect = MOB_EFFECTS.register("repulsive", RepulsiveEffect::new);
  public static final RegistryObject<TinkerEffect> momentumEffect = MOB_EFFECTS.register("momentum", MARKER_EFFECT.apply(0x60496b));
  public static final RegistryObject<TinkerEffect> momentumRangedEffect = MOB_EFFECTS.register("momentum_ranged", MARKER_EFFECT.apply(0x60496b));
  public static final RegistryObject<TinkerEffect> insatiableEffect = MOB_EFFECTS.register("insatiable", MARKER_EFFECT.apply(0x9261cc));
  public static final RegistryObject<TinkerEffect> insatiableRangedEffect = MOB_EFFECTS.register("insatiable_ranged", MARKER_EFFECT.apply(0x9261cc));
  public static final RegistryObject<TinkerEffect> enderferenceEffect = MOB_EFFECTS.register("enderference", () -> new NoMilkEffect(MobEffectCategory.HARMFUL, 0x8F648F, true));
  public static final RegistryObject<TinkerEffect> teleportCooldownEffect = MOB_EFFECTS.register("teleport_cooldown", () -> new NoMilkEffect(MobEffectCategory.HARMFUL, 0xCC00FA, true));
  public static final RegistryObject<TinkerEffect> fireballCooldownEffect = MOB_EFFECTS.register("fireball_cooldown", () -> new NoMilkEffect(MobEffectCategory.HARMFUL, 0xFC9600, true));
  public static final RegistryObject<TinkerEffect> calcifiedEffect = MOB_EFFECTS.register("calcified", () -> new NoMilkEffect(MobEffectCategory.BENEFICIAL, -1, true));
  public static final RegistryObject<TinkerEffect> selfDestructiveEffect = MOB_EFFECTS.register("self_destructing", SelfDestructiveEffect::new);
  public static final RegistryObject<TinkerEffect> pierceEffect = MOB_EFFECTS.register("pierce", () -> new NoMilkEffect(MobEffectCategory.HARMFUL, 0xD1D37A, true).addAttributeModifier(Attributes.ARMOR, "cd45be7c-c86f-4a7e-813b-42a44a054f44", -1, Operation.ADDITION));


  /*
   * Recipes
   */
  public static final RegistryObject<ModifierRecipe.Serializer> modifierSerializer = RECIPE_SERIALIZERS.register("modifier", ModifierRecipe.Serializer::new);
  public static final RegistryObject<IncrementalModifierRecipe.Serializer> incrementalModifierSerializer = RECIPE_SERIALIZERS.register("incremental_modifier", IncrementalModifierRecipe.Serializer::new);
  public static final RegistryObject<SwappableModifierRecipe.Serializer> swappableModifierSerializer = RECIPE_SERIALIZERS.register("swappable_modifier", SwappableModifierRecipe.Serializer::new);
  public static final RegistryObject<MultilevelModifierRecipe.Serializer> multilevelModifierSerializer = RECIPE_SERIALIZERS.register("multilevel_modifier", MultilevelModifierRecipe.Serializer::new);
  public static final RegistryObject<OverslimeModifierRecipe.Serializer> overslimeSerializer = RECIPE_SERIALIZERS.register("overslime_modifier", OverslimeModifierRecipe.Serializer::new);
  public static final RegistryObject<ModifierSalvage.Serializer> modifierSalvageSerializer = RECIPE_SERIALIZERS.register("modifier_salvage", ModifierSalvage.Serializer::new);
  public static final RegistryObject<ArmorDyeingRecipe.Serializer> armorDyeingSerializer = RECIPE_SERIALIZERS.register("armor_dyeing_modifier", ArmorDyeingRecipe.Serializer::new);
  public static final RegistryObject<SimpleRecipeSerializer<CreativeSlotRecipe>> creativeSlotSerializer = RECIPE_SERIALIZERS.register("creative_slot_modifier", () -> new SimpleRecipeSerializer<>(CreativeSlotRecipe::new));
  // modifiers
  public static final RegistryObject<ModifierRepairRecipeSerializer<?>> modifierRepair = RECIPE_SERIALIZERS.register("modifier_repair", () -> new ModifierRepairRecipeSerializer<>(ModifierRepairTinkerStationRecipe::new));
  public static final RegistryObject<ModifierRepairRecipeSerializer<?>> craftingModifierRepair = RECIPE_SERIALIZERS.register("crafting_modifier_repair", () -> new ModifierRepairRecipeSerializer<>(ModifierRepairCraftingRecipe::new));
  public static final RegistryObject<ModifierMaterialRepairSerializer<?>> modifierMaterialRepair = RECIPE_SERIALIZERS.register("modifier_material_repair", () -> new ModifierMaterialRepairSerializer<>(ModifierMaterialRepairRecipe::new));
  public static final RegistryObject<ModifierMaterialRepairSerializer<?>> craftingModifierMaterialRepair = RECIPE_SERIALIZERS.register("crafting_modifier_material_repair", () -> new ModifierMaterialRepairSerializer<>(ModifierMaterialRepairKitRecipe::new));
  // worktable
  public static final RegistryObject<ModifierRemovalRecipe.Serializer> removeModifierSerializer = RECIPE_SERIALIZERS.register("remove_modifier", () -> new ModifierRemovalRecipe.Serializer((ModifierRemovalRecipe.Factory)ModifierRemovalRecipe::new));
  public static final RegistryObject<ModifierRemovalRecipe.Serializer> extractModifierSerializer = RECIPE_SERIALIZERS.register("extract_modifier", () -> new ModifierRemovalRecipe.Serializer((ModifierRemovalRecipe.Factory)ExtractModifierRecipe::new));
  public static final RegistryObject<ModifierSortingRecipe.Serializer> modifierSortingSerializer = RECIPE_SERIALIZERS.register("modifier_sorting", ModifierSortingRecipe.Serializer::new);
  public static final RegistryObject<ModifierSetWorktableRecipe.Serializer> modifierSetWorktableSerializer = RECIPE_SERIALIZERS.register("modifier_set_worktable", ModifierSetWorktableRecipe.Serializer::new);
  public static final RegistryObject<EnchantmentConvertingRecipe.Serializer> enchantmentConvertingSerializer = RECIPE_SERIALIZERS.register("enchantment_converting", EnchantmentConvertingRecipe.Serializer::new);

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
  public static final RegistryObject<LootItemConditionType> hasModifierLootCondition = LOOT_CONDITIONS.register("has_modifier", () -> new LootItemConditionType(new HasModifierLootCondition.ConditionSerializer()));
  public static final RegistryObject<LootItemFunctionType> modifierBonusFunction = LOOT_FUNCTIONS.register("modifier_bonus", () -> new LootItemFunctionType(new ModifierBonusLootFunction.Serializer()));
  public static final RegistryObject<LootItemConditionType> chrysophiliteLootCondition = LOOT_CONDITIONS.register("has_chrysophilite", () -> new LootItemConditionType(ChrysophiliteLootCondition.SERIALIZER));
  public static final RegistryObject<LootItemFunctionType> chrysophiliteBonusFunction = LOOT_FUNCTIONS.register("chrysophilite_bonus", () -> new LootItemFunctionType(ChrysophiliteBonusFunction.SERIALIZER));

  /*
   * Events
   */

  @SubscribeEvent
  void registerSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
    ISpillingEffect.LOADER.registerDeserializer(ConditionalSpillingEffect.ID,   ConditionalSpillingEffect.LOADER);
    ISpillingEffect.LOADER.registerDeserializer(CureEffectsSpillingEffect.ID,   CureEffectsSpillingEffect.LOADER);
    ISpillingEffect.LOADER.registerDeserializer(RemoveEffectSpillingEffect.ID,  RemoveEffectSpillingEffect.LOADER);
    ISpillingEffect.LOADER.registerDeserializer(DamageSpillingEffect.ID,        DamageSpillingEffect.LOADER);
    ISpillingEffect.LOADER.registerDeserializer(EffectSpillingEffect.ID,        EffectSpillingEffect.LOADER);
    ISpillingEffect.LOADER.registerDeserializer(ExtinguishSpillingEffect.ID,    ExtinguishSpillingEffect.LOADER);
    ISpillingEffect.LOADER.registerDeserializer(PotionFluidEffect.ID,           PotionFluidEffect.LOADER);
    ISpillingEffect.LOADER.registerDeserializer(RestoreHungerSpillingEffect.ID, RestoreHungerSpillingEffect.LOADER);
    ISpillingEffect.LOADER.registerDeserializer(SetFireSpillingEffect.ID,       SetFireSpillingEffect.LOADER);
    ISpillingEffect.LOADER.registerDeserializer(SetFreezeSpillingEffect.ID,     SetFreezeSpillingEffect.LOADER);
    ISpillingEffect.LOADER.registerDeserializer(TeleportSpillingEffect.ID,      TeleportSpillingEffect.LOADER);
    ISpillingEffect.LOADER.registerDeserializer(AddInsomniaSpillingEffect.ID,   AddInsomniaSpillingEffect.LOADER);
    ISpillingEffect.LOADER.registerDeserializer(AddBreathSpillingEffect.ID,     AddBreathSpillingEffect.LOADER);
    ISpillingEffect.LOADER.registerDeserializer(StrongBonesModifier.SPILLING_EFFECT_ID, StrongBonesModifier.SPILLING_EFFECT_LOADER);
    // modifier loaders
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("default"), Modifier.DEFAULT_LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("stat_boost"), StatBoostModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("extra_slot"), ExtraModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("swappable_extra_slot"), SwappableExtraSlotModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("mob_disguise"), MobDisguiseModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("conditional_damage"), ConditionalDamageModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("conditional_mining_speed"), ConditionalMiningSpeedModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("loot"), LootModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("enchantment"), EnchantmentModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("mob_effect"), MobEffectModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("inventory_with_menu"), InventoryMenuModifier.LOADER);
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("composable"), ComposableModifier.LOADER);
    // specialized
    ModifierManager.MODIFIER_LOADERS.register(TConstruct.getResource("tool_belt"), ToolBeltModifier.LOADER);
    // modifier names, sometimes I wonder if I have too many registries for tiny JSON pieces
    ModifierLevelDisplay.LOADER.register(TConstruct.getResource("default"), ModifierLevelDisplay.DEFAULT.getLoader());
    ModifierLevelDisplay.LOADER.register(TConstruct.getResource("single_level"), ModifierLevelDisplay.SINGLE_LEVEL.getLoader());
    ModifierLevelDisplay.LOADER.register(TConstruct.getResource("no_levels"), ModifierLevelDisplay.NO_LEVELS.getLoader());
    ModifierLevelDisplay.LOADER.register(TConstruct.getResource("pluses"), ModifierLevelDisplay.PLUSES.getLoader());
    ModifierLevelDisplay.LOADER.register(TConstruct.getResource("unique"), UniqueForLevels.LOADER);

    // modifier modules //
    // armor
    ModifierModule.LOADER.register(TConstruct.getResource("mob_disguise"), MobDisguiseModule.LOADER);
    // behavior
    ModifierModule.LOADER.register(TConstruct.getResource("attribute"), AttributeModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("incremental"), IncrementalModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("repair"), RepairModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("tool_actions"), ToolActionsModule.LOADER);
    // build
    ModifierModule.LOADER.register(TConstruct.getResource("constant_enchantment"), EnchantmentModule.Constant.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("modifier_slot"), ModifierSlotModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("rarity"), RarityModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("swappable_slot"), SwappableSlotModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("swappable_bonus_slot"), SwappableSlotModule.BonusSlot.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("stat_boost"), StatBoostModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("set_stat"), SetStatModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("trait"), ModifierTraitModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("volatile_flag"), VolatileFlagModule.LOADER);
    // combat
    ModifierModule.LOADER.register(TConstruct.getResource("conditional_damage"), ConditionalDamageModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("looting"), LootingModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("melee_attribute"), MeleeAttributeModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("mob_effect"), MobEffectModule.LOADER);
    // mining
    ModifierModule.LOADER.register(TConstruct.getResource("conditional_mining_speed"), ConditionalMiningSpeedModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("harvest_enchantment"), EnchantmentModule.Harvest.LOADER);
    // fluid
    ModifierModule.LOADER.register(TConstruct.getResource("tank_capacity"), TankCapacityModule.LOADER);
    ModifierModule.LOADER.register(TConstruct.getResource("tank"), TankModule.LOADER);

    ModifierPredicate.LOADER.register(TConstruct.getResource("and"), ModifierPredicate.AND);
    ModifierPredicate.LOADER.register(TConstruct.getResource("or"), ModifierPredicate.OR);
    ModifierPredicate.LOADER.register(TConstruct.getResource("inverted"), ModifierPredicate.INVERTED);
    ModifierPredicate.LOADER.register(TConstruct.getResource("always"), ModifierPredicate.ALWAYS.getLoader());
    ModifierPredicate.LOADER.register(TConstruct.getResource("single"), SingleModifierPredicate.LOADER);
    ModifierPredicate.LOADER.register(TConstruct.getResource("tag"), TagModifierPredicate.LOADER);
    ModifierPredicate.LOADER.register(TConstruct.getResource("slot_type"), SlotTypeModifierPredicate.LOADER);
  }

  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    TinkerDataCapability.register();
    PersistentDataCapability.register();
    EntityModifierCapability.register();
    // by default, we support modifying projectiles (arrows or fireworks mainly, but maybe other stuff). other entities may come in the future
    EntityModifierCapability.registerEntityPredicate(entity -> entity instanceof Projectile);
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    if (event.includeServer()) {
      generator.addProvider(new ModifierProvider(generator));
      generator.addProvider(new ModifierRecipeProvider(generator));
      generator.addProvider(new SpillingFluidProvider(generator));
      generator.addProvider(new ModifierTagProvider(generator, event.getExistingFileHelper()));
      generator.addProvider(new EnchantmentToModifierProvider(generator));
    }
  }
}
