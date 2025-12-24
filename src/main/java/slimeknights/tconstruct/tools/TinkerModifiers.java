package slimeknights.tconstruct.tools;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.SimpleRecipeSerializer;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerEffect;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.data.tags.ModifierTagProvider;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.SingleModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.SlotTypeModifierPredicate;
import slimeknights.tconstruct.library.json.predicate.modifier.TagModifierPredicate;
import slimeknights.tconstruct.library.json.variable.block.BlockVariable;
import slimeknights.tconstruct.library.json.variable.block.ConditionalBlockVariable;
import slimeknights.tconstruct.library.json.variable.block.StatePropertyVariable;
import slimeknights.tconstruct.library.json.variable.entity.AttributeEntityVariable;
import slimeknights.tconstruct.library.json.variable.entity.ConditionalEntityVariable;
import slimeknights.tconstruct.library.json.variable.entity.EntityEffectLevelVariable;
import slimeknights.tconstruct.library.json.variable.entity.EntityLightVariable;
import slimeknights.tconstruct.library.json.variable.entity.EntityVariable;
import slimeknights.tconstruct.library.json.variable.entity.EquipmentCountEntityVariable;
import slimeknights.tconstruct.library.json.variable.melee.EntityMeleeVariable;
import slimeknights.tconstruct.library.json.variable.melee.MeleeVariable;
import slimeknights.tconstruct.library.json.variable.mining.BlockLightVariable;
import slimeknights.tconstruct.library.json.variable.mining.BlockMiningSpeedVariable;
import slimeknights.tconstruct.library.json.variable.mining.BlockTemperatureVariable;
import slimeknights.tconstruct.library.json.variable.mining.MiningSpeedVariable;
import slimeknights.tconstruct.library.json.variable.power.EntityPowerVariable;
import slimeknights.tconstruct.library.json.variable.power.PersistentDataPowerVariable;
import slimeknights.tconstruct.library.json.variable.power.PowerVariable;
import slimeknights.tconstruct.library.json.variable.protection.EntityProtectionVariable;
import slimeknights.tconstruct.library.json.variable.protection.ProtectionVariable;
import slimeknights.tconstruct.library.json.variable.stat.ConditionalStatVariable;
import slimeknights.tconstruct.library.json.variable.stat.EntityConditionalStatVariable;
import slimeknights.tconstruct.library.json.variable.tool.ConditionalToolVariable;
import slimeknights.tconstruct.library.json.variable.tool.ModDataVariable;
import slimeknights.tconstruct.library.json.variable.tool.StatMultiplierVariable;
import slimeknights.tconstruct.library.json.variable.tool.ToolStatVariable;
import slimeknights.tconstruct.library.json.variable.tool.ToolVariable;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.block.BlockInteractFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.BreakBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.MeltBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.MobEffectCloudFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.MoveBlocksFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.OffsetBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.PlaceBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.block.PotionCloudFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.AddBreathFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.AwardStatFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.CureEffectsFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.EntityInteractFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.FireFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.FreezeFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.MobEffectFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.PotionFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.PushEntityFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.RandomTeleportFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.RemoveEffectFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.entity.RestoreHungerFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.AlternativesFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.AreaMobEffectFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.ConditionalFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.DropItemFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.ExplosionFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.ScalingFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.SequenceFluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.general.SetBlockFluidEffect;
import slimeknights.tconstruct.library.modifiers.impl.SingleLevelModifier;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.BlockDamageSourceModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.CoverGroundWalkerModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.EffectImmunityModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.MaxArmorAttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.MobDisguiseModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.ProtectionModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.ReplaceBlockWalkerModule;
import slimeknights.tconstruct.library.modifiers.modules.armor.ToolActionWalkerTransformModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.AttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ConditionalStatModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.InfinityModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.MaterialRepairModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ReduceToolDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.RepairModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ToolActionTransformModule;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ToolActionsModule;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierRequirementsModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierSlotModule;
import slimeknights.tconstruct.library.modifiers.modules.build.ModifierTraitModule;
import slimeknights.tconstruct.library.modifiers.modules.build.RarityModule;
import slimeknights.tconstruct.library.modifiers.modules.build.SetStatModule;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.modifiers.modules.build.StatCopyModule;
import slimeknights.tconstruct.library.modifiers.modules.build.SwappableSlotModule;
import slimeknights.tconstruct.library.modifiers.modules.build.SwappableToolTraitsModule;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileFlagModule;
import slimeknights.tconstruct.library.modifiers.modules.build.VolatileIntModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.CapacityBarModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.DamageToCapacityModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.DurabilityShieldModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.LaunchCapacityModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.LootToCapacityModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.MiningCapacityModule;
import slimeknights.tconstruct.library.modifiers.modules.capacity.TimeToCapacityModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.ConditionalPowerModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.KnockbackModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.LootingModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.MeleeAttributeModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.MobEffectModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.ProjectileExplosionModule;
import slimeknights.tconstruct.library.modifiers.modules.display.DurabilityBarColorModule;
import slimeknights.tconstruct.library.modifiers.modules.display.MaterialVariantColorModule;
import slimeknights.tconstruct.library.modifiers.modules.display.ModifierVariantColorModule;
import slimeknights.tconstruct.library.modifiers.modules.display.ModifierVariantNameModule;
import slimeknights.tconstruct.library.modifiers.modules.mining.ConditionalMiningSpeedModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorStatModule;
import slimeknights.tconstruct.library.modifiers.modules.technical.MaxArmorStatModule;
import slimeknights.tconstruct.library.modifiers.util.DynamicModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay.UniqueForLevels;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierSalvage;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.MultilevelModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeCraftingTableRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.OverslimeModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.adding.SwappableModifierRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.AgeableSeveringRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierMaterialRepairKitRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierMaterialRepairRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairCraftingRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.repairing.ModifierRepairTinkerStationRecipe;
import slimeknights.tconstruct.library.recipe.worktable.ModifierSetWorktableRecipe;
import slimeknights.tconstruct.library.tools.capability.EntityModifierCapability;
import slimeknights.tconstruct.library.tools.capability.PersistentDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.capability.fluid.TankModule;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryMenuModule;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.capability.inventory.InventorySlotMenuModule;
import slimeknights.tconstruct.shared.TinkerEffects;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.data.EnchantmentToModifierProvider;
import slimeknights.tconstruct.tools.data.FluidEffectProvider;
import slimeknights.tconstruct.tools.data.ModifierProvider;
import slimeknights.tconstruct.tools.data.ModifierRecipeProvider;
import slimeknights.tconstruct.tools.entity.FluidEffectProjectile;
import slimeknights.tconstruct.tools.item.CreativeSlotItem;
import slimeknights.tconstruct.tools.item.DragonScaleItem;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;
import slimeknights.tconstruct.tools.modifiers.EnergyHandlerModifier;
import slimeknights.tconstruct.tools.modifiers.ModifierLootModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.AmbidextrousModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.FlamewakeModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ReflectingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.fluid.BurstingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.fluid.SlurpingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.fluid.SpittingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.fluid.SplashingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.fluid.WettingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.FirestarterModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.HarvestAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.ShearsAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.SilkyShearsAbilityModifier;
import slimeknights.tconstruct.tools.modifiers.ability.sling.BonkingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.sling.FlingingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.sling.SpringingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.sling.WarpingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.AutosmeltModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.BucketingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.DuelWieldingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.ExchangingModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.OffhandAttackModifier;
import slimeknights.tconstruct.tools.modifiers.ability.tool.ParryingModifier;
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
import slimeknights.tconstruct.tools.modifiers.slotless.TrimModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.EnderportingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.SolarPoweredModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.TannedModifier;
import slimeknights.tconstruct.tools.modifiers.traits.general.TastyModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.DwarvenModifier;
import slimeknights.tconstruct.tools.modifiers.traits.harvest.MomentumModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.ConductingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.DecayModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.EnderferenceModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.InsatiableModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.LaceratingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.melee.NecroticModifier;
import slimeknights.tconstruct.tools.modifiers.traits.ranged.OlympicModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.BoonOfSssssModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.BreathtakingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.ChrysophiliteModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.EnderdodgingModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.FirebreathModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.FrosttouchModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.GoldGuardModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.PlagueModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.RevengeModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.SelfDestructiveModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.StrongBonesModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.WildfireModifier;
import slimeknights.tconstruct.tools.modifiers.traits.skull.WitheredModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.SoulSpeedModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.general.MagneticModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.PiercingModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.SeveringModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.melee.SweepingEdgeModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.ranged.SinistralModifier;
import slimeknights.tconstruct.tools.modules.CraftCountModule;
import slimeknights.tconstruct.tools.modules.DamageOnUnequipModule;
import slimeknights.tconstruct.tools.modules.HeadlightModule;
import slimeknights.tconstruct.tools.modules.MeltingModule;
import slimeknights.tconstruct.tools.modules.OverburnModule;
import slimeknights.tconstruct.tools.modules.OvergrowthModule;
import slimeknights.tconstruct.tools.modules.SmeltingModule;
import slimeknights.tconstruct.tools.modules.TheOneProbeModule;
import slimeknights.tconstruct.tools.modules.ZoomModule;
import slimeknights.tconstruct.tools.modules.armor.DepthProtectionModule;
import slimeknights.tconstruct.tools.modules.armor.EnderclearanceModule;
import slimeknights.tconstruct.tools.modules.armor.FieryCounterModule;
import slimeknights.tconstruct.tools.modules.armor.FlameBarrierModule;
import slimeknights.tconstruct.tools.modules.armor.FreezingCounterModule;
import slimeknights.tconstruct.tools.modules.armor.GlowWalkerModule;
import slimeknights.tconstruct.tools.modules.armor.KineticModule;
import slimeknights.tconstruct.tools.modules.armor.KnockbackCounterModule;
import slimeknights.tconstruct.tools.modules.armor.LightspeedAttributeModule;
import slimeknights.tconstruct.tools.modules.armor.MinimapModule;
import slimeknights.tconstruct.tools.modules.armor.OvershieldModule;
import slimeknights.tconstruct.tools.modules.armor.RecurrentProtectionModule;
import slimeknights.tconstruct.tools.modules.armor.ShieldStrapModule;
import slimeknights.tconstruct.tools.modules.armor.SleevesModule;
import slimeknights.tconstruct.tools.modules.armor.ThornsModule;
import slimeknights.tconstruct.tools.modules.armor.ToolBeltModule;
import slimeknights.tconstruct.tools.modules.combat.ChannelingModule;
import slimeknights.tconstruct.tools.modules.combat.FieryAttackModule;
import slimeknights.tconstruct.tools.modules.combat.FreezingAttackModule;
import slimeknights.tconstruct.tools.modules.combat.SpillingModule;
import slimeknights.tconstruct.tools.modules.durability.DurabilityAsCapacityModule;
import slimeknights.tconstruct.tools.modules.durability.ShareDurabilityModule;
import slimeknights.tconstruct.tools.modules.interaction.BrushModule;
import slimeknights.tconstruct.tools.modules.interaction.ExtinguishCampfireModule;
import slimeknights.tconstruct.tools.modules.interaction.FishingModule;
import slimeknights.tconstruct.tools.modules.interaction.PlaceGlowModule;
import slimeknights.tconstruct.tools.modules.interaction.ThrowingModule;
import slimeknights.tconstruct.tools.modules.ranged.BulkQuiverModule;
import slimeknights.tconstruct.tools.modules.ranged.RestrictAngleModule;
import slimeknights.tconstruct.tools.modules.ranged.TrickQuiverModule;
import slimeknights.tconstruct.tools.modules.ranged.ammo.ProjectileFuseModule;
import slimeknights.tconstruct.tools.modules.ranged.ammo.ProjectileGravityModule;
import slimeknights.tconstruct.tools.modules.ranged.ammo.SmashingModule;
import slimeknights.tconstruct.tools.modules.ranged.ammo.TippedModule;
import slimeknights.tconstruct.tools.modules.ranged.bow.QuiverInventoryModule;
import slimeknights.tconstruct.tools.modules.ranged.common.ArrowPierceModule;
import slimeknights.tconstruct.tools.modules.ranged.common.ProjectileAttractMobsModule;
import slimeknights.tconstruct.tools.modules.ranged.common.ProjectileBounceModule;
import slimeknights.tconstruct.tools.modules.ranged.common.ProjectilePlaceGlowModule;
import slimeknights.tconstruct.tools.modules.ranged.common.PunchModule;
import slimeknights.tconstruct.tools.modules.ranged.common.ReversePunchModule;
import slimeknights.tconstruct.tools.recipe.ArmorDyeingRecipe;
import slimeknights.tconstruct.tools.recipe.ArmorTrimRecipe;
import slimeknights.tconstruct.tools.recipe.EnchantmentConvertingRecipe;
import slimeknights.tconstruct.tools.recipe.ExtractModifierRecipe;
import slimeknights.tconstruct.tools.recipe.ModifierRemovalRecipe;
import slimeknights.tconstruct.tools.recipe.ModifierSortingRecipe;
import slimeknights.tconstruct.tools.recipe.TippedToolTransformRecipe;
import slimeknights.tconstruct.tools.recipe.ToggleInteractionWorktableRecipe;
import slimeknights.tconstruct.tools.recipe.severing.MooshroomDemushroomingRecipe;
import slimeknights.tconstruct.tools.recipe.severing.PlayerBeheadingRecipe;
import slimeknights.tconstruct.tools.recipe.severing.SheepShearingRecipe;
import slimeknights.tconstruct.tools.recipe.severing.SnowGolemBeheadingRecipe;
import slimeknights.tconstruct.tools.stats.ToolType;

import static slimeknights.tconstruct.TConstruct.getResource;

/**
 * Contains modifiers and the items or blocks used to craft modifiers
 */
@SuppressWarnings("unused")
public final class TinkerModifiers extends TinkerModule {
  private static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TConstruct.MOD_ID);

  public TinkerModifiers() {
    ModifierManager.INSTANCE.init();
    DynamicModifier.init();
    FluidEffectManager.INSTANCE.init();
    MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    TinkerDataKeys.init();
  }

  /*
   * Items
   */
  public static final ItemObject<Item> silkyCloth = ITEMS.register("silky_cloth", ITEM_PROPS);
  public static final ItemObject<Item> dragonScale = ITEMS.register("dragon_scale", () -> new DragonScaleItem(new Item.Properties().rarity(Rarity.RARE)));
  // durability reinforcements
  public static final ItemObject<Item> emeraldReinforcement = ITEMS.register("emerald_reinforcement", ITEM_PROPS);
  public static final ItemObject<Item> slimesteelReinforcement = ITEMS.register("slimesteel_reinforcement", ITEM_PROPS);
  // armor reinforcements
  public static final ItemObject<Item> ironReinforcement = ITEMS.register("iron_reinforcement", ITEM_PROPS);
  public static final ItemObject<Item> searedReinforcement = ITEMS.register("seared_reinforcement", ITEM_PROPS);
  public static final ItemObject<Item> goldReinforcement = ITEMS.register("gold_reinforcement", ITEM_PROPS);
  public static final ItemObject<Item> cobaltReinforcement = ITEMS.register("cobalt_reinforcement", ITEM_PROPS);
  public static final ItemObject<Item> obsidianReinforcement = ITEMS.register("obsidian_reinforcement", ITEM_PROPS);
  // special
  public static final ItemObject<Item> modifierCrystal = ITEMS.register("modifier_crystal", () -> new ModifierCrystalItem(new Item.Properties().stacksTo(16)));
  public static final ItemObject<CreativeSlotItem> creativeSlotItem = ITEMS.register("creative_slot", () -> new CreativeSlotItem(ITEM_PROPS));

  // entity
  public static final RegistryObject<EntityType<FluidEffectProjectile>> fluidSpitEntity = ENTITIES.register("fluid_spit", () ->
    EntityType.Builder.<FluidEffectProjectile>of(FluidEffectProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).setShouldReceiveVelocityUpdates(false));

  /*
   * Modifiers
   */
  public static final StaticModifier<OverslimeModifier> overslime = MODIFIERS.register("overslime", OverslimeModifier::new);
  public static final StaticModifier<MagneticModifier> magnetic = MODIFIERS.register("magnetic", MagneticModifier::new);
  public static final StaticModifier<FarsightedModifier> farsighted = MODIFIERS.register("farsighted", FarsightedModifier::new);
  public static final StaticModifier<NearsightedModifier> nearsighted = MODIFIERS.register("nearsighted", NearsightedModifier::new);

  // weapon
  public static final DynamicModifier knockback = MODIFIERS.registerDynamic("knockback");
  public static final DynamicModifier padded = MODIFIERS.registerDynamic("padded");
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#fiery} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier fiery = MODIFIERS.registerDynamic("fiery");
  public static final StaticModifier<SeveringModifier> severing = MODIFIERS.register("severing", SeveringModifier::new);
  public static final StaticModifier<ReflectingModifier> reflecting = MODIFIERS.register("reflecting", ReflectingModifier::new);

  // damage boost
  public static final StaticModifier<PiercingModifier> piercing = MODIFIERS.register("piercing", PiercingModifier::new);
  public static final StaticModifier<SweepingEdgeModifier> sweeping = MODIFIERS.register("sweeping_edge", SweepingEdgeModifier::new);

  // ranged
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#punch} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier punch = MODIFIERS.registerDynamic("punch");
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#arrowPierce} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier impaling = MODIFIERS.registerDynamic("impaling");
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#freezing} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier freezing = MODIFIERS.registerDynamic("freezing");
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#crystalshot} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier crystalshot = MODIFIERS.registerDynamic("crystalshot");
  public static final StaticModifier<Modifier> multishot = MODIFIERS.register("multishot", Modifier::new);
  public static final StaticModifier<SinistralModifier> sinistral = MODIFIERS.register("sinistral", SinistralModifier::new);
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#scope} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier scope = MODIFIERS.registerDynamic("scope");

  // armor
  // general
  public static final DynamicModifier golden = MODIFIERS.registerDynamic("golden");
  public static final StaticModifier<EmbellishmentModifier> embellishment = MODIFIERS.register("embellishment", EmbellishmentModifier::new);
  public static final StaticModifier<DyedModifier> dyed = MODIFIERS.register("dyed", DyedModifier::new);
  public static final StaticModifier<TrimModifier> trim = MODIFIERS.register("trim", TrimModifier::new);
  // counterattack
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#thorns} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier thorns = MODIFIERS.registerDynamic("thorns");
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#springy} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier springy = MODIFIERS.registerDynamic("springy");
  // helmet
  public static final DynamicModifier itemFrame = MODIFIERS.registerDynamic("item_frame");
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#zoom} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier zoom = MODIFIERS.registerDynamic("zoom");
  public static final StaticModifier<SlurpingModifier> slurping = MODIFIERS.register("slurping", SlurpingModifier::new);
  // chestplate
  public static final DynamicModifier sleeves = MODIFIERS.registerDynamic("sleeves");
  public static final StaticModifier<AmbidextrousModifier> ambidextrous = MODIFIERS.register("ambidextrous", AmbidextrousModifier::new);
  // leggings
  public static final DynamicModifier shieldStrap = MODIFIERS.registerDynamic("shield_strap");
  public static final StaticModifier<WettingModifier> wetting = MODIFIERS.register("wetting", WettingModifier::new);

  // boots
  public static final StaticModifier<SoulSpeedModifier> soulspeed = MODIFIERS.register("soulspeed", SoulSpeedModifier::new);
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#doubleJump} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier doubleJump = MODIFIERS.registerDynamic("double_jump");
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#bouncy} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier bouncy = MODIFIERS.registerDynamic("bouncy");
  public static final StaticModifier<FlamewakeModifier> flamewake = MODIFIERS.register("flamewake", FlamewakeModifier::new);

  // abilities
  public static final DynamicModifier unbreakable = MODIFIERS.registerDynamic("unbreakable");
  // weapon
  public static final StaticModifier<DuelWieldingModifier> dualWielding = MODIFIERS.register("dual_wielding", DuelWieldingModifier::new);
  // harvest
  public static final DynamicModifier silky = MODIFIERS.registerDynamic("silky");
  public static final StaticModifier<AutosmeltModifier> autosmelt = MODIFIERS.register("autosmelt", AutosmeltModifier::new);
  public static final StaticModifier<Modifier> expanded = MODIFIERS.register("expanded", Modifier::new);
  public static final StaticModifier<ExchangingModifier> exchanging = MODIFIERS.register("exchanging", ExchangingModifier::new);

  public static final StaticModifier<Modifier> energyHandler = MODIFIERS.register("energy_handler", EnergyHandlerModifier::new);
  // fluid abilities
  public static final StaticModifier<Modifier> tankHandler = MODIFIERS.register("tank_handler", () -> ModuleHookMap.builder().addModule(new TankModule(ToolTankHelper.TANK_HELPER)).modifier().levelDisplay(ModifierLevelDisplay.NO_LEVELS).priority(300).build());
  public static final DynamicModifier melting = MODIFIERS.registerDynamic("melting");
  public static final StaticModifier<BucketingModifier> bucketing = MODIFIERS.register("bucketing", BucketingModifier::new);
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#spilling} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier spilling = MODIFIERS.registerDynamic("spilling");
  public static final StaticModifier<SpittingModifier> spitting = MODIFIERS.register("spitting", SpittingModifier::new);
  public static final StaticModifier<BurstingModifier> bursting = MODIFIERS.register("bursting", BurstingModifier::new);
  public static final StaticModifier<SplashingModifier> splashing = MODIFIERS.register("splashing", SplashingModifier::new);
  
  // right click abilities
  public static final StaticModifier<FirestarterModifier> firestarter = MODIFIERS.register("firestarter", () -> new FirestarterModifier(Modifier.DEFAULT_PRIORITY));
  public static final StaticModifier<SingleLevelModifier> fireprimer = MODIFIERS.register("fireprimer", SingleLevelModifier::new);
  public static final StaticModifier<BlockingModifier> blocking = MODIFIERS.register("blocking", BlockingModifier::new);
  public static final StaticModifier<ParryingModifier> parrying = MODIFIERS.register("parrying", ParryingModifier::new);
  // slings
  public static final StaticModifier<FlingingModifier> flinging = MODIFIERS.register("flinging", FlingingModifier::new);
  public static final StaticModifier<SpringingModifier> springing = MODIFIERS.register("springing", SpringingModifier::new);
  public static final StaticModifier<BonkingModifier> bonking = MODIFIERS.register("bonking", BonkingModifier::new);
  public static final StaticModifier<WarpingModifier> warping = MODIFIERS.register("warping", WarpingModifier::new);


  // internal abilities
  public static final StaticModifier<ShearsAbilityModifier> shears = MODIFIERS.register("shears", () -> new ShearsAbilityModifier(0, 70));
  public static final StaticModifier<SilkyShearsAbilityModifier> silkyShears = MODIFIERS.register("silky_shears", () -> new SilkyShearsAbilityModifier(0, 70));
  public static final StaticModifier<SilkyShearsAbilityModifier> aoeSilkyShears = MODIFIERS.register("silky_aoe_shears", () -> new SilkyShearsAbilityModifier(1, 70));
  public static final StaticModifier<HarvestAbilityModifier> harvest = MODIFIERS.register("harvest", () -> new HarvestAbilityModifier(70));
  public static final StaticModifier<OffhandAttackModifier> offhandAttack = MODIFIERS.register("offhand_attack", OffhandAttackModifier::new);

  // creative
  public static final StaticModifier<CreativeSlotModifier> creativeSlot = MODIFIERS.register("creative_slot", CreativeSlotModifier::new);
  public static final StaticModifier<StatOverrideModifier> statOverride = MODIFIERS.register("stat_override", StatOverrideModifier::new);

  // traits - tier 1
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#jagged} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier jagged = MODIFIERS.registerDynamic("jagged");
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#stonebound} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier stonebound = MODIFIERS.registerDynamic("stonebound");
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#frostshield} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier frostshield = MODIFIERS.registerDynamic("frostshield");
  // traits - tier 1 nether
  public static final StaticModifier<NecroticModifier> necrotic = MODIFIERS.register("necrotic", NecroticModifier::new);
  // traits - tier 1 nether
  public static final StaticModifier<EnderferenceModifier> enderference = MODIFIERS.register("enderference", EnderferenceModifier::new);
  // traits - tier 1 bindings
  public static final StaticModifier<TannedModifier> tanned = MODIFIERS.register("tanned", TannedModifier::new);
  public static final StaticModifier<SolarPoweredModifier> solarPowered = MODIFIERS.register("solar_powered", SolarPoweredModifier::new);
  // traits - tier 2
  public static final StaticModifier<DwarvenModifier> dwarven = MODIFIERS.register("dwarven", DwarvenModifier::new);
  // traits - tier 3
  public static final StaticModifier<LaceratingModifier> lacerating = MODIFIERS.register("lacerating", LaceratingModifier::new);
  public static final StaticModifier<TastyModifier> tasty = MODIFIERS.register("tasty", TastyModifier::new);
  public static final StaticModifier<MomentumModifier> momentum = MODIFIERS.register("momentum", MomentumModifier::new);
  public static final StaticModifier<InsatiableModifier> insatiable = MODIFIERS.register("insatiable", InsatiableModifier::new);
  public static final StaticModifier<ConductingModifier> conducting = MODIFIERS.register("conducting", ConductingModifier::new);
  // traits - tier 5
  public static final StaticModifier<EnderportingModifier> enderporting = MODIFIERS.register("enderporting", EnderportingModifier::new);

  // traits - mod compat tier 2
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#stoneshield} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier stoneshield = MODIFIERS.registerDynamic("stoneshield");
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#holy} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier holy = MODIFIERS.registerDynamic("holy");
  public static final StaticModifier<OlympicModifier> olympic = MODIFIERS.register("olympic", OlympicModifier::new);
  // traits - mod compat tier 3
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#temperate} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier temperate = MODIFIERS.registerDynamic("temperate");
  /** @deprecated use {@link slimeknights.tconstruct.tools.data.ModifierIds#invariant} */
  @Deprecated(forRemoval = true)
  public static final DynamicModifier invariant = MODIFIERS.registerDynamic("invariant");
  public static final StaticModifier<DecayModifier> decay = MODIFIERS.register("decay", DecayModifier::new);
  public static final StaticModifier<Modifier> overworked = MODIFIERS.register("overworked", Modifier::new);
  // experienced is also an upgrade

  // traits - slimeskull
  public static final StaticModifier<SelfDestructiveModifier> selfDestructive = MODIFIERS.register("self_destructive", SelfDestructiveModifier::new);
  public static final StaticModifier<EnderdodgingModifier> enderdodging = MODIFIERS.register("enderdodging", EnderdodgingModifier::new);
  public static final StaticModifier<StrongBonesModifier> strongBones = MODIFIERS.register("strong_bones", StrongBonesModifier::new);
  public static final StaticModifier<FrosttouchModifier> frosttouch = MODIFIERS.register("frosttouch", FrosttouchModifier::new);
  public static final StaticModifier<WitheredModifier> withered = MODIFIERS.register("withered", WitheredModifier::new);
  public static final StaticModifier<BoonOfSssssModifier> boonOfSssss = MODIFIERS.register("boon_of_sssss", BoonOfSssssModifier::new);
  public static final StaticModifier<WildfireModifier> wildfire = MODIFIERS.register("wildfire", WildfireModifier::new);
  public static final StaticModifier<PlagueModifier> plague = MODIFIERS.register("plague", PlagueModifier::new);
  public static final StaticModifier<BreathtakingModifier> breathtaking = MODIFIERS.register("breathtaking", BreathtakingModifier::new);
  public static final StaticModifier<FirebreathModifier> firebreath = MODIFIERS.register("firebreath", FirebreathModifier::new);
  public static final StaticModifier<ChrysophiliteModifier> chrysophilite = MODIFIERS.register("chrysophilite", ChrysophiliteModifier::new);
  public static final StaticModifier<GoldGuardModifier> goldGuard = MODIFIERS.register("gold_guard", GoldGuardModifier::new);
  public static final StaticModifier<RevengeModifier> revenge = MODIFIERS.register("revenge", RevengeModifier::new);
  

  /*
   * Effects
   */
  /** @deprecated use {@link TinkerEffects#bleeding} */
  @Deprecated(forRemoval = true)
  public static final RegistryObject<BleedingEffect> bleeding = TinkerEffects.bleeding;
  /** @deprecated use {@link TinkerEffects#magnetic} */
  @Deprecated(forRemoval = true)
  public static final RegistryObject<MagneticEffect> magneticEffect = TinkerEffects.magnetic;
  /** @deprecated use {@link TinkerEffects#repulsive} */
  @Deprecated(forRemoval = true)
  public static final RegistryObject<RepulsiveEffect> repulsiveEffect = TinkerEffects.repulsive;
  /** @deprecated use {@link TinkerEffects#enderference} */
  @Deprecated(forRemoval = true)
  public static final RegistryObject<TinkerEffect> enderferenceEffect = TinkerEffects.enderference;
  /** @deprecated use {@link TinkerEffects#selfDestructing} */
  @Deprecated(forRemoval = true)
  public static final RegistryObject<TinkerEffect> selfDestructiveEffect = TinkerEffects.selfDestructing;
  /** @deprecated use {@link TinkerEffects#pierce} */
  @Deprecated(forRemoval = true)
  public static final RegistryObject<TinkerEffect> pierceEffect = TinkerEffects.pierce;
  // cooldown
  public static final RegistryObject<TinkerEffect> teleportCooldownEffect = MOB_EFFECTS.register("teleport_cooldown", () -> new NoMilkEffect(MobEffectCategory.HARMFUL, 0xCC00FA, true));
  public static final RegistryObject<TinkerEffect> fireballCooldownEffect = MOB_EFFECTS.register("fireball_cooldown", () -> new NoMilkEffect(MobEffectCategory.HARMFUL, 0xFC9600, true));
  // internal
  public static final RegistryObject<TinkerEffect> calcifiedEffect = MOB_EFFECTS.register("calcified", () -> new NoMilkEffect(MobEffectCategory.BENEFICIAL, -1, true));
  // markers
  public static final EnumObject<ToolType,TinkerEffect> momentumEffect = MOB_EFFECTS.registerEnum("momentum", ToolType.NO_MELEE, type -> new NoMilkEffect(MobEffectCategory.BENEFICIAL, 0x60496b, true));
  public static final EnumObject<ToolType,TinkerEffect> insatiableEffect = MOB_EFFECTS.registerEnum("insatiable", new ToolType[] {ToolType.MELEE, ToolType.RANGED, ToolType.ARMOR}, type -> {
    TinkerEffect effect = new NoMilkEffect(MobEffectCategory.BENEFICIAL, 0x9261cc, true);
    if (type == ToolType.ARMOR) {
      effect.addAttributeModifier(Attributes.ATTACK_DAMAGE, "cc6904f7-674a-4e6a-b992-4f3cb8edfef4", 1, AttributeModifier.Operation.ADDITION);
    }
    return effect;
  });

  /*
   * Recipes
   */
  public static final RegistryObject<RecipeSerializer<ModifierRecipe>> modifierSerializer = RECIPE_SERIALIZERS.register("modifier", () -> LoadableRecipeSerializer.of(ModifierRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<IncrementalModifierRecipe>> incrementalModifierSerializer = RECIPE_SERIALIZERS.register("incremental_modifier", () -> LoadableRecipeSerializer.of(IncrementalModifierRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<SwappableModifierRecipe>> swappableModifierSerializer = RECIPE_SERIALIZERS.register("swappable_modifier", () -> LoadableRecipeSerializer.of(SwappableModifierRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<MultilevelModifierRecipe>> multilevelModifierSerializer = RECIPE_SERIALIZERS.register("multilevel_modifier", () -> LoadableRecipeSerializer.of(MultilevelModifierRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<OverslimeModifierRecipe>> overslimeSerializer = RECIPE_SERIALIZERS.register("overslime_modifier", () -> LoadableRecipeSerializer.of(OverslimeModifierRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<OverslimeCraftingTableRecipe>> craftingOverslimeSerializer = RECIPE_SERIALIZERS.register("crafting_overslime_modifier", () -> LoadableRecipeSerializer.of(OverslimeCraftingTableRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ModifierSalvage>> modifierSalvageSerializer = RECIPE_SERIALIZERS.register("modifier_salvage", () -> LoadableRecipeSerializer.of(ModifierSalvage.LOADER));
  public static final RegistryObject<RecipeSerializer<ArmorDyeingRecipe>> armorDyeingSerializer = RECIPE_SERIALIZERS.register("armor_dyeing_modifier", () -> new SimpleRecipeSerializer<>(ArmorDyeingRecipe::new));
  public static final RegistryObject<RecipeSerializer<ArmorTrimRecipe>> armorTrimSerializer = RECIPE_SERIALIZERS.register("armor_trim_modifier", () -> new SimpleRecipeSerializer<>(ArmorTrimRecipe::new));
  public static final RegistryObject<RecipeSerializer<TippedToolTransformRecipe>> tippedToolTransformRecipeSerializer = RECIPE_SERIALIZERS.register("tipped_tool_transform", () -> LoadableRecipeSerializer.of(TippedToolTransformRecipe.LOADER));
  // modifiers
  public static final RegistryObject<RecipeSerializer<ModifierRepairTinkerStationRecipe>> modifierRepair = RECIPE_SERIALIZERS.register("modifier_repair", () -> LoadableRecipeSerializer.of(ModifierRepairTinkerStationRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ModifierRepairCraftingRecipe>> craftingModifierRepair = RECIPE_SERIALIZERS.register("crafting_modifier_repair", () -> LoadableRecipeSerializer.of(ModifierRepairCraftingRecipe.LOADER));
  /** @deprecated use {@link MaterialRepairModule} */
  @SuppressWarnings("removal")
  @Deprecated(forRemoval = true)
  public static final RegistryObject<RecipeSerializer<ModifierMaterialRepairRecipe>> modifierMaterialRepair = RECIPE_SERIALIZERS.register("modifier_material_repair", () -> LoadableRecipeSerializer.deprecated(ModifierMaterialRepairRecipe.LOADER, "use the tconstruct:material_repair modifier module instead"));
  /** @deprecated use {@link MaterialRepairModule} */
  @SuppressWarnings("removal")
  @Deprecated(forRemoval = true)
  public static final RegistryObject<RecipeSerializer<ModifierMaterialRepairKitRecipe>> craftingModifierMaterialRepair = RECIPE_SERIALIZERS.register("crafting_modifier_material_repair", () -> LoadableRecipeSerializer.deprecated(ModifierMaterialRepairKitRecipe.LOADER, "use the tconstruct:material_repair modifier module instead"));
  // worktable
  public static final RegistryObject<RecipeSerializer<ModifierRemovalRecipe>> removeModifierSerializer = RECIPE_SERIALIZERS.register("remove_modifier", () -> LoadableRecipeSerializer.of(ModifierRemovalRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ExtractModifierRecipe>> extractModifierSerializer = RECIPE_SERIALIZERS.register("extract_modifier", () -> LoadableRecipeSerializer.of(ExtractModifierRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ModifierSortingRecipe>> modifierSortingSerializer = RECIPE_SERIALIZERS.register("modifier_sorting", () -> LoadableRecipeSerializer.of(ModifierSortingRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ModifierSetWorktableRecipe>> modifierSetWorktableSerializer = RECIPE_SERIALIZERS.register("modifier_set_worktable", () -> LoadableRecipeSerializer.of(ModifierSetWorktableRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<EnchantmentConvertingRecipe>> enchantmentConvertingSerializer = RECIPE_SERIALIZERS.register("enchantment_converting", () -> LoadableRecipeSerializer.of(EnchantmentConvertingRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ToggleInteractionWorktableRecipe>> toggleInteractionSerializer = RECIPE_SERIALIZERS.register("toggle_interaction", () -> LoadableRecipeSerializer.of(ToggleInteractionWorktableRecipe.LOADER));

  // severing
  public static final RegistryObject<RecipeSerializer<SeveringRecipe>> severingSerializer = RECIPE_SERIALIZERS.register("severing", () -> LoadableRecipeSerializer.of(SeveringRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<AgeableSeveringRecipe>> ageableSeveringSerializer = RECIPE_SERIALIZERS.register("ageable_severing", () -> LoadableRecipeSerializer.of(AgeableSeveringRecipe.LOADER));
  // special severing
  public static final RegistryObject<SimpleRecipeSerializer<PlayerBeheadingRecipe>> playerBeheadingSerializer = RECIPE_SERIALIZERS.register("player_beheading", () -> new SimpleRecipeSerializer<>(PlayerBeheadingRecipe::new));
  public static final RegistryObject<SimpleRecipeSerializer<SnowGolemBeheadingRecipe>> snowGolemBeheadingSerializer = RECIPE_SERIALIZERS.register("snow_golem_beheading", () -> new SimpleRecipeSerializer<>(SnowGolemBeheadingRecipe::new));
  public static final RegistryObject<SimpleRecipeSerializer<MooshroomDemushroomingRecipe>> mooshroomDemushroomingSerializer = RECIPE_SERIALIZERS.register("mooshroom_demushrooming", () -> new SimpleRecipeSerializer<>(MooshroomDemushroomingRecipe::new));
  public static final RegistryObject<SimpleRecipeSerializer<SheepShearingRecipe>> sheepShearing = RECIPE_SERIALIZERS.register("sheep_shearing", () -> new SimpleRecipeSerializer<>(SheepShearingRecipe::new));

  /**
   * Loot
   */
  public static final RegistryObject<Codec<ModifierLootModifier>> modifierLootModifier = GLOBAL_LOOT_MODIFIERS.register("modifier_hook", () -> ModifierLootModifier.CODEC);
  public static final RegistryObject<LootItemConditionType> hasModifierLootCondition = LOOT_CONDITIONS.register("has_modifier", () -> new LootItemConditionType(new HasModifierLootCondition.ConditionSerializer()));
  public static final RegistryObject<LootItemFunctionType> modifierBonusFunction = LOOT_FUNCTIONS.register("modifier_bonus", () -> new LootItemFunctionType(new ModifierBonusLootFunction.Serializer()));
  public static final RegistryObject<LootItemConditionType> chrysophiliteLootCondition = LOOT_CONDITIONS.register("has_chrysophilite", () -> new LootItemConditionType(ChrysophiliteLootCondition.SERIALIZER));
  public static final RegistryObject<LootItemFunctionType> chrysophiliteBonusFunction = LOOT_FUNCTIONS.register("chrysophilite_bonus", () -> new LootItemFunctionType(ChrysophiliteBonusFunction.SERIALIZER));

  /*
   * Events
   */

  @SubscribeEvent
  void registerSerializers(RegisterEvent event) {
    if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER) {
      // combinations
      FluidEffect.BLOCK_EFFECTS.register(getResource("conditional"), ConditionalFluidEffect.Block.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("conditional"), ConditionalFluidEffect.Entity.LOADER);
      FluidEffect.BLOCK_EFFECTS.register(getResource("scaling"), ScalingFluidEffect.BLOCK_LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("scaling"), ScalingFluidEffect.ENTITY_LOADER);
      FluidEffect.BLOCK_EFFECTS.register(getResource("alternatives"), AlternativesFluidEffect.BLOCK_LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("alternatives"), AlternativesFluidEffect.ENTITY_LOADER);
      FluidEffect.BLOCK_EFFECTS.register(getResource("sequence"), SequenceFluidEffect.BLOCK_LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("sequence"), SequenceFluidEffect.ENTITY_LOADER);
      FluidEffect.BLOCK_EFFECTS.register(getResource("offset"), OffsetBlockFluidEffect.LOADER);
      // simple
      FluidEffect.ENTITY_EFFECTS.register(getResource("calcified"), StrongBonesModifier.FLUID_EFFECT.getLoader());
      FluidEffect.ENTITY_EFFECTS.register(getResource("extinguish"), FluidEffect.EXTINGUISH_FIRE.getLoader());
      FluidEffect.ENTITY_EFFECTS.register(getResource("teleport"), RandomTeleportFluidEffect.LOADER);
      FluidEffect.BLOCK_EFFECTS.register(getResource("weather"), FluidEffect.WEATHER.getLoader());
      // potions
      FluidEffect.ENTITY_EFFECTS.register(getResource("cure_effects"), CureEffectsFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("remove_effect"), RemoveEffectFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("mob_effect"), MobEffectFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("potion"), PotionFluidEffect.LOADER);
      // misc
      FluidEffect.ENTITY_EFFECTS.register(getResource("damage"), DamageFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("restore_hunger"), RestoreHungerFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("fire"), FireFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("freeze"), FreezeFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("award_stat"), AwardStatFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("add_breath"), AddBreathFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("push_entity"), PushEntityFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("interact"), EntityInteractFluidEffect.INSTANCE.getLoader());
      // block
      FluidEffect.BLOCK_EFFECTS.register(getResource("place_block"), PlaceBlockFluidEffect.LOADER);
      FluidEffect.BLOCK_EFFECTS.register(getResource("break_block"), BreakBlockFluidEffect.LOADER);
      FluidEffect.BLOCK_EFFECTS.register(getResource("remove_block"), FluidEffect.REMOVE_BLOCK.getLoader());
      FluidEffect.BLOCK_EFFECTS.register(getResource("mob_effect_cloud"), MobEffectCloudFluidEffect.LOADER);
      FluidEffect.BLOCK_EFFECTS.register(getResource("potion_cloud"), PotionCloudFluidEffect.LOADER);
      FluidEffect.BLOCK_EFFECTS.register(getResource("move_block"), MoveBlocksFluidEffect.LOADER);
      FluidEffect.BLOCK_EFFECTS.register(getResource("interact"), BlockInteractFluidEffect.INSTANCE.getLoader());
      FluidEffect.BLOCK_EFFECTS.register(getResource("melt_block"), MeltBlockFluidEffect.LOADER);
      // shared
      FluidEffect.registerGeneral(getResource("drop_item"), DropItemFluidEffect.LOADER);
      FluidEffect.registerGeneral(getResource("explosion"), ExplosionFluidEffect.LOADER);
      FluidEffect.registerGeneral(getResource("set_block"), SetBlockFluidEffect.LOADER);
      FluidEffect.registerGeneral(getResource("area_mob_effect"), AreaMobEffectFluidEffect.LOADER);


      // modifier names, sometimes I wonder if I have too many registries for tiny JSON pieces
      ModifierLevelDisplay.LOADER.register(getResource("default"), ModifierLevelDisplay.DEFAULT.getLoader());
      ModifierLevelDisplay.LOADER.register(getResource("single_level"), ModifierLevelDisplay.SINGLE_LEVEL.getLoader());
      ModifierLevelDisplay.LOADER.register(getResource("no_levels"), ModifierLevelDisplay.NO_LEVELS.getLoader());
      ModifierLevelDisplay.LOADER.register(getResource("pluses"), ModifierLevelDisplay.PLUSES.getLoader());
      ModifierLevelDisplay.LOADER.register(getResource("unique"), UniqueForLevels.LOADER);

      // modifier modules //
      ModifierModule.LOADER.register(getResource("empty"), ModifierModule.EMPTY.getLoader());
      // armor
      ModifierModule.LOADER.register(getResource("max_armor_attribute"), MaxArmorAttributeModule.LOADER);
      ModifierModule.LOADER.register(getResource("effect_immunity"), EffectImmunityModule.LOADER);
      ModifierModule.LOADER.register(getResource("mob_disguise"), MobDisguiseModule.LOADER);
      ModifierModule.LOADER.register(getResource("block_damage"), BlockDamageSourceModule.LOADER);
      ModifierModule.LOADER.register(getResource("cover_ground"), CoverGroundWalkerModule.LOADER);
      ModifierModule.LOADER.register(getResource("protection"), ProtectionModule.LOADER);
      ModifierModule.LOADER.register(getResource("replace_fluid"), ReplaceBlockWalkerModule.LOADER);
      ModifierModule.LOADER.register(getResource("tool_action_walk_transform"), ToolActionWalkerTransformModule.LOADER);
      // behavior
      ModifierModule.LOADER.register(getResource("attribute"), AttributeModule.LOADER);
      ModifierModule.LOADER.register(getResource("reduce_tool_damage"), ReduceToolDamageModule.LOADER);
      // TODO 1.21: rename to repair_factor?
      ModifierModule.LOADER.register(getResource("repair"), RepairModule.LOADER);
      ModifierModule.LOADER.register(getResource("material_repair"), MaterialRepairModule.LOADER);
      ModifierModule.LOADER.register(getResource("show_offhand"), ShowOffhandModule.LOADER);
      ModifierModule.LOADER.register(getResource("tool_actions"), ToolActionsModule.LOADER);
      ModifierModule.LOADER.register(getResource("tool_action_transform"), ToolActionTransformModule.LOADER);
      // build
      ModifierModule.LOADER.register(getResource("conditional_stat"), ConditionalStatModule.LOADER);
      ModifierModule.LOADER.register(getResource("modifier_slot"), ModifierSlotModule.LOADER);
      ModifierModule.LOADER.register(getResource("rarity"), RarityModule.LOADER);
      ModifierModule.LOADER.register(getResource("requirements"), ModifierRequirementsModule.LOADER);
      ModifierModule.LOADER.register(getResource("swappable_slot"), SwappableSlotModule.LOADER);
      ModifierModule.LOADER.register(getResource("swappable_bonus_slot"), SwappableSlotModule.BonusSlot.LOADER);
      ModifierModule.LOADER.register(getResource("swappable_tool_traits"), SwappableToolTraitsModule.LOADER);
      ModifierModule.LOADER.register(getResource("stat_boost"), StatBoostModule.LOADER);
      ModifierModule.LOADER.register(getResource("stat_copy"), StatCopyModule.LOADER);
      ModifierModule.LOADER.register(getResource("set_stat"), SetStatModule.LOADER);
      ModifierModule.LOADER.register(getResource("trait"), ModifierTraitModule.LOADER);
      ModifierModule.LOADER.register(getResource("volatile_flag"), VolatileFlagModule.LOADER);
      ModifierModule.LOADER.register(getResource("volatile_int"), VolatileIntModule.LOADER);
      // combat
      ModifierModule.LOADER.register(getResource("conditional_melee_damage"), ConditionalMeleeDamageModule.LOADER);
      ModifierModule.LOADER.register(getResource("conditional_power"), ConditionalPowerModule.LOADER);
      ModifierModule.LOADER.register(getResource("knockback"), KnockbackModule.LOADER);
      ModifierModule.LOADER.register(getResource("melee_attribute"), MeleeAttributeModule.LOADER);
      ModifierModule.LOADER.register(getResource("mob_effect"), MobEffectModule.LOADER);
      ModifierModule.LOADER.register(getResource("projectile_explosion"), ProjectileExplosionModule.LOADER);
      // display
      ModifierModule.LOADER.register(getResource("durability_color"), DurabilityBarColorModule.LOADER);
      ModifierModule.LOADER.register(getResource("variant_name"), ModifierVariantNameModule.LOADER);
      ModifierModule.LOADER.register(getResource("variant_color"), ModifierVariantColorModule.LOADER);
      ModifierModule.LOADER.register(getResource("material_variant_color"), MaterialVariantColorModule.LOADER);
      // enchantment
      ModifierModule.LOADER.register(getResource("constant_enchantment"), EnchantmentModule.Constant.LOADER);
      ModifierModule.LOADER.register(getResource("main_hand_harvest_enchantment"), EnchantmentModule.MainHandHarvest.LOADER);
      ModifierModule.LOADER.register(getResource("armor_harvest_enchantment"), EnchantmentModule.ArmorHarvest.LOADER);
      ModifierModule.LOADER.register(getResource("enchantment_ignoring_protection"), EnchantmentModule.Protection.LOADER);
      ModifierModule.LOADER.register(getResource("weapon_looting"), LootingModule.Weapon.LOADER);
      ModifierModule.LOADER.register(getResource("armor_looting"), LootingModule.Armor.LOADER);
      // mining
      ModifierModule.LOADER.register(getResource("conditional_mining_speed"), ConditionalMiningSpeedModule.LOADER);
      // capacity
      ModifierModule.LOADER.register(getResource("capacity_bar"), CapacityBarModule.LOADER);
      ModifierModule.LOADER.register(getResource("durability_as_capacity"), DurabilityAsCapacityModule.LOADER);
      ModifierModule.LOADER.register(getResource("durability_shield"), DurabilityShieldModule.LOADER);
      ModifierModule.LOADER.register(getResource("loot_to_capacity"), LootToCapacityModule.LOADER);
      ModifierModule.LOADER.register(getResource("damage_to_capacity"), DamageToCapacityModule.LOADER);
      ModifierModule.LOADER.register(getResource("time_to_capacity"), TimeToCapacityModule.LOADER);
      ModifierModule.LOADER.register(getResource("launch_capacity"), LaunchCapacityModule.LOADER);
      ModifierModule.LOADER.register(getResource("mining_capacity"), MiningCapacityModule.LOADER);
      // technical
      ModifierModule.LOADER.register(getResource("armor_level"), ArmorLevelModule.LOADER);
      ModifierModule.LOADER.register(getResource("max_armor_stat"), MaxArmorStatModule.LOADER);
      ModifierModule.LOADER.register(getResource("armor_stat"), ArmorStatModule.LOADER);
      ModifierModule.LOADER.register(getResource("inventory"), InventoryModule.LOADER);
      ModifierModule.LOADER.register(getResource("inventory_menu"), InventoryMenuModule.LOADER);
      ModifierModule.LOADER.register(getResource("inventory_slot_menu"), InventorySlotMenuModule.INSTANCE.getLoader());

      // special
      ModifierModule.LOADER.register(getResource("smelting"), SmeltingModule.LOADER);
      ModifierModule.LOADER.register(getResource("melting"), MeltingModule.LOADER);
      ModifierModule.LOADER.register(getResource("place_glow"), PlaceGlowModule.LOADER);
      ModifierModule.LOADER.register(getResource("glow_walker"), GlowWalkerModule.LOADER);
      ModifierModule.LOADER.register(getResource("campfire_extinguish"), ExtinguishCampfireModule.LOADER);
      ModifierModule.LOADER.register(getResource("lightspeed_attribute"), LightspeedAttributeModule.LOADER);
      ModifierModule.LOADER.register(getResource("zoom"), ZoomModule.LOADER);
      ModifierModule.LOADER.register(getResource("brush"), BrushModule.LOADER);
      ModifierModule.LOADER.register(getResource("fishing"), FishingModule.LOADER);
      ModifierModule.LOADER.register(getResource("throwing"), ThrowingModule.LOADER);
      ModifierModule.LOADER.register(getResource("damage_on_unequip"), DamageOnUnequipModule.LOADER);
      ModifierModule.LOADER.register(getResource("share_durability"), ShareDurabilityModule.LOADER);
      ModifierModule.LOADER.register(getResource("projectile_place_glow"), ProjectilePlaceGlowModule.LOADER);
      ModifierModule.LOADER.register(getResource("craft_count"), CraftCountModule.LOADER);
      ModifierModule.LOADER.register(getResource("tipped"), TippedModule.LOADER);
      ModifierModule.LOADER.register(getResource("projectile_bounce"), ProjectileBounceModule.LOADER);
      // overslime
      ModifierModule.LOADER.register(getResource("overgrowth"), OvergrowthModule.LOADER);
      ModifierModule.LOADER.register(getResource("overburn"), OverburnModule.INSTANCE.getLoader());
      ModifierModule.LOADER.register(getResource("overshield"), OvershieldModule.LOADER);
      // combat
      ModifierModule.LOADER.register(getResource("fiery_attack"), FieryAttackModule.LOADER);
      ModifierModule.LOADER.register(getResource("freezing_attack"), FreezingAttackModule.LOADER);
      ModifierModule.LOADER.register(getResource("spilling"), SpillingModule.LOADER);
      ModifierModule.LOADER.register(getResource("channeling"), ChannelingModule.LOADER);
      ModifierModule.LOADER.register(getResource("smashing"), SmashingModule.LOADER);
      // armor
      ModifierModule.LOADER.register(getResource("enderclearance"), EnderclearanceModule.LOADER);
      ModifierModule.LOADER.register(getResource("depth_protection"), DepthProtectionModule.LOADER);
      ModifierModule.LOADER.register(getResource("flame_barrier"), FlameBarrierModule.LOADER);
      ModifierModule.LOADER.register(getResource("kinetic"), KineticModule.LOADER);
      ModifierModule.LOADER.register(getResource("recurrent_protection"), RecurrentProtectionModule.LOADER);
      ModifierModule.LOADER.register(getResource("shield_strap"), ShieldStrapModule.LOADER);
      ModifierModule.LOADER.register(getResource("tool_belt"), ToolBeltModule.LOADER);
      ModifierModule.LOADER.register(getResource("minimap"), MinimapModule.LOADER);
      ModifierModule.LOADER.register(getResource("sleeves"), SleevesModule.LOADER);
      // counterattack
      ModifierModule.LOADER.register(getResource("thorns"), ThornsModule.LOADER);
      ModifierModule.LOADER.register(getResource("fiery_counter"), FieryCounterModule.LOADER);
      ModifierModule.LOADER.register(getResource("freezing_counter"), FreezingCounterModule.LOADER);
      ModifierModule.LOADER.register(getResource("knockback_counter"), KnockbackCounterModule.LOADER);
      // ranged
      ModifierModule.LOADER.register(getResource("restrict_projectile_angle"), RestrictAngleModule.LOADER);
      ModifierModule.LOADER.register(getResource("bulk_quiver"), BulkQuiverModule.LOADER);
      ModifierModule.LOADER.register(getResource("trick_quiver"), TrickQuiverModule.LOADER);
      ModifierModule.LOADER.register(getResource("quiver_inventory"), QuiverInventoryModule.LOADER);
      ModifierModule.LOADER.register(getResource("infinity"), InfinityModule.LOADER);
      ModifierModule.LOADER.register(getResource("punch"), PunchModule.LOADER);
      ModifierModule.LOADER.register(getResource("reverse_punch"), ReversePunchModule.LOADER);
      ModifierModule.LOADER.register(getResource("arrow_pierce"), ArrowPierceModule.LOADER);
      ModifierModule.LOADER.register(getResource("projectile_gravity"), ProjectileGravityModule.LOADER);
      ModifierModule.LOADER.register(getResource("projectile_fuse"), ProjectileFuseModule.LOADER);
      ModifierModule.LOADER.register(getResource("projectile_attract_mobs"), ProjectileAttractMobsModule.LOADER);
      // compat
      ModifierModule.LOADER.register(getResource("the_one_probe"), TheOneProbeModule.INSTANCE.getLoader());
      ModifierModule.LOADER.register(getResource("headlight"), HeadlightModule.LOADER);

      // modifier predicates
      ModifierPredicate.LOADER.register(getResource("single"), SingleModifierPredicate.LOADER);
      ModifierPredicate.LOADER.register(getResource("tag"), TagModifierPredicate.LOADER);
      ModifierPredicate.LOADER.register(getResource("slot_type"), SlotTypeModifierPredicate.LOADER);


      // variables
      // block
      BlockVariable.LOADER.register(getResource("constant"), BlockVariable.Constant.LOADER);
      BlockVariable.LOADER.register(getResource("conditional"), ConditionalBlockVariable.LOADER);
      BlockVariable.LOADER.register(getResource("blast_resistance"), BlockVariable.BLAST_RESISTANCE.getLoader());
      BlockVariable.LOADER.register(getResource("hardness"), BlockVariable.HARDNESS.getLoader());
      BlockVariable.LOADER.register(getResource("state_property"), StatePropertyVariable.LOADER);
      // entity
      EntityVariable.LOADER.register(getResource("constant"), EntityVariable.Constant.LOADER);
      EntityVariable.LOADER.register(getResource("conditional"), ConditionalEntityVariable.LOADER);
      EntityVariable.LOADER.register(getResource("health"), EntityVariable.HEALTH.getLoader());
      EntityVariable.LOADER.register(getResource("height"), EntityVariable.HEIGHT.getLoader());
      EntityVariable.LOADER.register(getResource("attribute"), AttributeEntityVariable.LOADER);
      EntityVariable.LOADER.register(getResource("effect_level"), EntityEffectLevelVariable.LOADER);
      EntityVariable.LOADER.register(getResource("light"), EntityLightVariable.LOADER);
      EntityVariable.LOADER.register(getResource("equipment_count"), EquipmentCountEntityVariable.LOADER);
      EntityVariable.LOADER.register(getResource("biome_temperature"), EntityVariable.BIOME_TEMPERATURE.getLoader());
      EntityVariable.LOADER.register(getResource("water"), EntityVariable.WATER.getLoader());
      // tool
      ToolVariable.LOADER.register(getResource("constant"), ToolVariable.Constant.LOADER);
      ToolVariable.register(getResource("tool_conditional"), ConditionalToolVariable.LOADER);
      ToolVariable.register(getResource("tool_durability"), ToolVariable.CURRENT_DURABILITY.getLoader());
      ToolVariable.register(getResource("tool_lost_durability"), ToolVariable.CURRENT_DAMAGE.getLoader());
      ToolVariable.register(getResource("tool_stat"), ToolStatVariable.LOADER);
      ToolVariable.register(getResource("stat_multiplier"), StatMultiplierVariable.LOADER);
      ToolVariable.register(getResource("mod_data"), ModDataVariable.LOADER);
      // stat
      ConditionalStatVariable.LOADER.register(getResource("constant"), ConditionalStatVariable.Constant.LOADER);
      ConditionalStatVariable.register(getResource("entity"), EntityConditionalStatVariable.LOADER);
      // melee
      MeleeVariable.LOADER.register(getResource("constant"), MeleeVariable.Constant.LOADER);
      MeleeVariable.LOADER.register(getResource("entity"), EntityMeleeVariable.LOADER);
      // power
      PowerVariable.LOADER.register(getResource("constant"), PowerVariable.Constant.LOADER);
      PowerVariable.LOADER.register(getResource("entity"), EntityPowerVariable.LOADER);
      PowerVariable.LOADER.register(getResource("persistent_data"), PersistentDataPowerVariable.LOADER);
      // mining speed
      MiningSpeedVariable.LOADER.register(getResource("constant"), MiningSpeedVariable.Constant.LOADER);
      MiningSpeedVariable.LOADER.register(getResource("block"), BlockMiningSpeedVariable.LOADER);
      MiningSpeedVariable.LOADER.register(getResource("block_light"), BlockLightVariable.LOADER);
      MiningSpeedVariable.LOADER.register(getResource("biome_temperature"), BlockTemperatureVariable.LOADER);
      // protection
      ProtectionVariable.LOADER.register(getResource("constant"), ProtectionVariable.Constant.LOADER);
      ProtectionVariable.LOADER.register(getResource("entity"), EntityProtectionVariable.LOADER);
    }
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
    PackOutput packOutput = generator.getPackOutput();
    boolean server = event.includeServer();
    generator.addProvider(server, new ModifierProvider(packOutput));
    generator.addProvider(server, new ModifierRecipeProvider(packOutput));
    generator.addProvider(server, new FluidEffectProvider(packOutput));
    generator.addProvider(server, new ModifierTagProvider(packOutput, event.getExistingFileHelper()));
    generator.addProvider(server, new EnchantmentToModifierProvider(packOutput));
  }

  /** Adds all relevant items to the creative tab, called by general */
  public static void addTabItems(ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
    output.accept(silkyCloth);
    // dragon scale is handled by world
    output.accept(emeraldReinforcement);
    output.accept(slimesteelReinforcement);
    output.accept(TinkerTables.pattern, TabVisibility.PARENT_TAB_ONLY); // extra listing of pattern, also in table as you need it for part builder usage
    output.accept(ironReinforcement);
    output.accept(searedReinforcement);
    output.accept(goldReinforcement);
    output.accept(cobaltReinforcement);
    output.accept(obsidianReinforcement);
    creativeSlotItem.get().addVariants(output::accept);
    // modifier crystal is handled by tool parts tab
  }
}
