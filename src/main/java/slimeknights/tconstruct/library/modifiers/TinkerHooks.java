package slimeknights.tconstruct.library.modifiers;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hook.armor.ArmorWalkModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ElytraFlightModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EffectiveLevelModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.RepairFactorModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierTraitHook;
import slimeknights.tconstruct.library.modifiers.hook.build.RawDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.DamageDealtModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.LootingModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.FinishHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.RemoveBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.BlockTransformModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.PlantHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.ShearsModifierHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Function;

/** Collection of all hooks implemented by the mod natively */
public class TinkerHooks {
  private TinkerHooks() {}

  public static void init() {}


  /* General */

  /** Generic hook for stats conditioned on the entity holding the tool */
  public static final ModifierHook<ConditionalStatModifierHook> CONDITIONAL_STAT = register("conditional_stat", ConditionalStatModifierHook.class, ConditionalStatModifierHook.AllMerger::new, (tool, modifier, living, stat, baseValue, multiplier) -> baseValue);

  /** Hook for modifiers checking if they can perform a tool action */
  public static final ModifierHook<ToolActionModifierHook> TOOL_ACTION = register("tool_action", ToolActionModifierHook.class, ToolActionModifierHook.AnyMerger::new, (tool, modifier, toolAction) -> false);

  /** Hook used when any {@link EquipmentSlot} changes on an entity while using at least one tool */
  public static final ModifierHook<EquipmentChangeModifierHook> EQUIPMENT_CHANGE = register("equipment_change", EquipmentChangeModifierHook.class, EquipmentChangeModifierHook.AllMerger::new, new EquipmentChangeModifierHook() {});

  /** Hook for modifying the repair amount for tools */
  public static final ModifierHook<RepairFactorModifierHook> REPAIR_FACTOR = register("repair_factor", RepairFactorModifierHook.class, RepairFactorModifierHook.ComposeMerger::new, (tool, entry, factor) -> factor);

  /** Hook for modifying the damage amount for tools */
  public static final ModifierHook<ToolDamageModifierHook> TOOL_DAMAGE = register("tool_damage", ToolDamageModifierHook.class, ToolDamageModifierHook.Merger::new, (tool, modifier, amount, holder) -> amount);

  /** Hook running while the tool is in the inventory */
  public static final ModifierHook<InventoryTickModifierHook> INVENTORY_TICK = register("inventory_tick", InventoryTickModifierHook.class, InventoryTickModifierHook.AllMerger::new, (tool, modifier, world, holder, itemSlot, isSelected, isCorrectSlot, stack) -> {});


  /* Composable only  */

  /** Hook for supporting modifiers to change the effective level */
  public static final ModifierHook<EffectiveLevelModifierHook> EFFECTIVE_LEVEL = register("effective_level", EffectiveLevelModifierHook.class, EffectiveLevelModifierHook.ComposeMerger::new, (tool, modifier, level) -> level);

  /** Hook for supporting modifiers to change the modifier display name */
  public static final ModifierHook<DisplayNameModifierHook> DISPLAY_NAME = register("display_name", DisplayNameModifierHook.class, DisplayNameModifierHook.ComposeMerger::new, (tool, modifier, level, name) -> name);


  /* Display */

  /** Hook for modifiers adding additional information to the tooltip */
  public static final ModifierHook<TooltipModifierHook> TOOLTIP = register("tooltip", TooltipModifierHook.class, TooltipModifierHook.AllMerger::new, (tool, modifier, player, tooltip, tooltipKey, tooltipFlag) -> {});

  /** Hook for changing the itemstack durability bar */
  public static final ModifierHook<DurabilityDisplayModifierHook> DURABILITY_DISPLAY = register("durability_display", DurabilityDisplayModifierHook.class, DurabilityDisplayModifierHook.FirstMerger::new, new DurabilityDisplayModifierHook() {
    @Nullable
    @Override
    public Boolean showDurabilityBar(IToolStackView tool, ModifierEntry modifier) {
      return null;
    }

    @Override
    public int getDurabilityWidth(IToolStackView tool, ModifierEntry modifier) {
      return 0;
    }

    @Override
    public int getDurabilityRGB(IToolStackView tool, ModifierEntry modifier) {
      return -1;
    }
  });


  /* Tool Building */

  /** Hook for adding raw unconditional stats to a tool */
  public static final ModifierHook<ToolStatsModifierHook> TOOL_STATS = register("tool_stats", ToolStatsModifierHook.class, ToolStatsModifierHook.AllMerger::new, (context, modifier, builder) -> {});

  /** Hook for adding item stack attributes to a tool when in the proper slot */
  public static final ModifierHook<AttributesModifierHook> ATTRIBUTES = register("attributes", AttributesModifierHook.class, AttributesModifierHook.AllMerger::new, (tool, modifier, slot, consumer) -> {});

  /** Hook to add data that resets every time stats rebuild */
  public static final ModifierHook<VolatileDataModifierHook> VOLATILE_DATA = register("volatile_data", VolatileDataModifierHook.class, VolatileDataModifierHook.AllMerger::new, (context, modifier, volatileData) -> {});

  /** Hook to add and remove data directly to the tools NBT. It is generally better to use persistent data or volatile data when possible. */
  public static final ModifierHook<RawDataModifierHook> RAW_DATA = register("raw_data", RawDataModifierHook.class, RawDataModifierHook.AllMerger::new, new RawDataModifierHook() {
    @Override
    public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {}

    @Override
    public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {}
  });

  /** Hook called to give a modifier a chance to clean up data while on the tool and to reject the current tool state */
  public static final ModifierHook<ValidateModifierHook> VALIDATE = register("validate", ValidateModifierHook.class, ValidateModifierHook.AllMerger::new, (tool, modifier) -> null);

  /** Hook called when a modifier is removed to give it a chance to clean up data */
  public static final ModifierHook<ModifierRemovalHook> REMOVE = register("remove", ModifierRemovalHook.class, ModifierRemovalHook.FirstMerger::new, (tool, modifier) -> null);

  /** Hook for a modifier to add other modifiers to the builder */
  public static final ModifierHook<ModifierTraitHook> MODIFIER_TRAITS = register("modifier_traits", ModifierTraitHook.class, ModifierTraitHook.AllMerger::new, (context, modifier, builder, firstEncounter) -> {});

  /* Combat */

  /** Hook to adjust melee damage when a weapon is attacking an entity */
  public static final ModifierHook<MeleeDamageModifierHook> MELEE_DAMAGE = register("melee_damage", MeleeDamageModifierHook.class, MeleeDamageModifierHook.AllMerger::new, (tool, modifier, context, baseDamage, damage) -> damage);

  /** Hook called when an entity is attacked to apply special effects */
  public static final ModifierHook<MeleeHitModifierHook> MELEE_HIT = register("melee_hit", MeleeHitModifierHook.class, MeleeHitModifierHook.AllMerger::new, new MeleeHitModifierHook() {});

  /** Hook called when taking damage wearing this armor to reduce the damage, runs after {@link #MODIFY_HURT} and before {@link #MODIFY_DAMAGE} */
  public static final ModifierHook<ProtectionModifierHook> PROTECTION = register("protection", ProtectionModifierHook.class, ProtectionModifierHook.AllMerger::new, (tool, modifier, context, slotType, source, modifierValue) -> modifierValue);

  /** Hook called when taking damage wearing this armor to cancel the damage */
  public static final ModifierHook<DamageBlockModifierHook> DAMAGE_BLOCK = register("damage_block", DamageBlockModifierHook.class, DamageBlockModifierHook.AnyMerger::new, (tool, modifier, context, slotType, source, amount) -> false);
  /** Hook called when taking damage to apply secondary effects such as counterattack or healing. Runs after {@link #DAMAGE_BLOCK} but before vanilla effects that cancel damage. */
  public static final ModifierHook<OnAttackedModifierHook> ON_ATTACKED = register("on_attacked", OnAttackedModifierHook.class, OnAttackedModifierHook.AllMerger::new, (tool, modifier, context, slotType, source, amount, isDirectDamage) -> {});

  /** Hook allowing modifying damage taken or responding when damage is taken. Runs after {@link #ON_ATTACKED} and any vanilla effects that cancel damage, but before armor reduction and {@link #PROTECTION}.  */
  public static final ModifierHook<ModifyDamageModifierHook> MODIFY_HURT;
  /** Hook allowing modifying damage taken or responding when damage is taken. Runs after {@link #PROTECTION}, armor damage reduction, and absorption.  */
  public static final ModifierHook<ModifyDamageModifierHook> MODIFY_DAMAGE;
  static {
    Function<Collection<ModifyDamageModifierHook>,ModifyDamageModifierHook> merger = ModifyDamageModifierHook.AllMerger::new;
    ModifyDamageModifierHook fallback = (tool, modifier, context, slotType, source, amount, isDirectDamage) -> amount;
    MODIFY_HURT = register("modify_hurt", ModifyDamageModifierHook.class, merger, fallback);
    MODIFY_DAMAGE = register("modify_damage", ModifyDamageModifierHook.class, merger, fallback);
  }

  /** Hook called when dealing damage while wearing this equipment */
  public static final ModifierHook<DamageDealtModifierHook> DAMAGE_DEALT = register("damage_dealt", DamageDealtModifierHook.class, DamageDealtModifierHook.AllMerger::new, (tool, modifier, context, slotType, target, source, amount, isDirectDamage) -> {});

  /* Loot */

  /** Hook for a tool boosting the looting value */
  public static final ModifierHook<LootingModifierHook> TOOL_LOOTING = register("tool_looting", LootingModifierHook.class, LootingModifierHook.SUM_MERGER, (tool, modifier, holder, target, damageSource, looting) -> looting);

  /** Hook for leggings boosting the tool's looting level */
  public static final ModifierHook<LootingModifierHook> LEGGINGS_LOOTING = register("leggings_looting", LootingModifierHook.class, LootingModifierHook.SUM_MERGER, (tool, modifier, holder, target, damageSource, looting) -> looting);

  /** Hook for looting values on a projectile, will receive {@link slimeknights.tconstruct.library.tools.nbt.DummyToolStack} for the tool */
  public static final ModifierHook<LootingModifierHook> PROJECTILE_LOOTING = register("projectile_looting", LootingModifierHook.class, LootingModifierHook.SUM_MERGER, LootingModifierHook.DEFAULT);

  /** Hook for adding harvest enchantments to a held tool based on the tool's modifiers */
  public static final ModifierHook<HarvestEnchantmentsModifierHook> TOOL_HARVEST_ENCHANTMENTS;
  /** Hook for adding harvest enchantments to a held tool based on the legging's modifiers */
  public static final ModifierHook<HarvestEnchantmentsModifierHook> LEGGINGS_HARVEST_ENCHANTMENTS;
  static {
    HarvestEnchantmentsModifierHook empty = (tool, modifier, context, consumer) -> {};
    Function<Collection<HarvestEnchantmentsModifierHook>,HarvestEnchantmentsModifierHook> merger = HarvestEnchantmentsModifierHook.AllMerger::new;
    TOOL_HARVEST_ENCHANTMENTS = register("tool_harvest_enchantments", HarvestEnchantmentsModifierHook.class, merger, empty);
    LEGGINGS_HARVEST_ENCHANTMENTS = register("leggings_harvest_enchantments", HarvestEnchantmentsModifierHook.class, merger, empty);
  }


  /* Harvest */

  /** Hook for conditionally modifying the break speed of a block */
  public static final ModifierHook<BreakSpeedModifierHook> BREAK_SPEED = register("break_speed", BreakSpeedModifierHook.class, BreakSpeedModifierHook.AllMerger::new, (tool, modifier, event, sideHit, isEffective, miningSpeedModifier) -> {});

  /** Called when a block is broken by a tool to allow the modifier to take over the block removing logic */
  public static final ModifierHook<RemoveBlockModifierHook> REMOVE_BLOCK = register("remove_block", RemoveBlockModifierHook.class, RemoveBlockModifierHook.FirstMerger::new, (tool, modifier, context) -> null);

  /** Called after a block is broken by a tool for every block in the AOE */
  public static final ModifierHook<BlockBreakModifierHook> BLOCK_BREAK = register("block_break", BlockBreakModifierHook.class, BlockBreakModifierHook.AllMerger::new, (tool, modifier, context) -> {});

  /** Called after all blocks in the AOE are broken */
  public static final ModifierHook<FinishHarvestModifierHook> FINISH_HARVEST = register("finish_harvest", FinishHarvestModifierHook.class, FinishHarvestModifierHook.AllMerger::new, (tool, modifier, context) -> {});


  /* Ranged */

  /** Hook for firing arrows or other projectiles to modify the entity post firing */
  public static final ModifierHook<ProjectileLaunchModifierHook> PROJECTILE_LAUNCH = register("projectile_launch", ProjectileLaunchModifierHook.class, ProjectileLaunchModifierHook.ALL_MERGER, ProjectileLaunchModifierHook.EMPTY);
  /** Hook called when an arrow hits an entity or block */
  public static final ModifierHook<ProjectileHitModifierHook> PROJECTILE_HIT = register("projectile_hit", ProjectileHitModifierHook.class, ProjectileHitModifierHook.FIRST_MERGER, ProjectileHitModifierHook.EMPTY);
  /** Hook called when a bow is looking for ammo. Does not support merging multiple hooks on one modifier */
  public static final ModifierHook<BowAmmoModifierHook> BOW_AMMO = register("bow_ammo", BowAmmoModifierHook.class, BowAmmoModifierHook.EMPTY);

  /* Misc Armor */

  /** Hook for when the player flies using an elytra, called on the chestplate slot */
  public static final ModifierHook<ElytraFlightModifierHook> ELYTRA_FLIGHT = register("elytra_flight", ElytraFlightModifierHook.class, ElytraFlightModifierHook.FIRST_MERGER, (tool, modifier, entity, flightTicks) -> false);

  /** Hook for when the player walks from one position to another, called on the boots slot */
  public static final ModifierHook<ArmorWalkModifierHook> BOOT_WALK = register("boot_walk", ArmorWalkModifierHook.class, ArmorWalkModifierHook.ALL_MERGER, (tool, modifier, living, prevPos, newPos) -> {});


  /* Interaction */

  /**
   * Hook for regular interactions not targeting blocks or entities. Needed for charged interactions, while other hooks may be better for most interactions.
   * Note the charged interaction hooks will only fire for the modifier that called {@link GeneralInteractionModifierHook#startUsing(IToolStackView, ModifierId, LivingEntity, InteractionHand)},
   * meaning there is no need to manually track that you were called.
   */
  public static final ModifierHook<GeneralInteractionModifierHook> GENERAL_INTERACT = register("general_interact", GeneralInteractionModifierHook.class, GeneralInteractionModifierHook.FirstMerger::new, ((tool, modifier, player, hand, source) -> InteractionResult.PASS));
  /** Hook for interacting with blocks */
  public static final ModifierHook<BlockInteractionModifierHook> BLOCK_INTERACT = register("block_interact", BlockInteractionModifierHook.class, BlockInteractionModifierHook.FirstMerger::new, new BlockInteractionModifierHook() {});
  /** Hook for interacting with entities */
  public static final ModifierHook<EntityInteractionModifierHook> ENTITY_INTERACT = register("entity_interact", EntityInteractionModifierHook.class, EntityInteractionModifierHook.FirstMerger::new, new EntityInteractionModifierHook() {});
  /** Hook for when the player interacts with an armor slot. Currently, only implemented for helmets and leggings */
  public static final ModifierHook<KeybindInteractModifierHook> ARMOR_INTERACT = register("armor_interact", KeybindInteractModifierHook.class, KeybindInteractModifierHook.MERGER, new KeybindInteractModifierHook() {});


  /* Modifier sub-hooks */

  /** Hook called on all tool modifiers after the harvest modifier harvests a crop */
  public static final ModifierHook<PlantHarvestModifierHook> PLANT_HARVEST = register("plant_harvest", PlantHarvestModifierHook.class, PlantHarvestModifierHook.ALL_MERGER, (tool, modifier, context, world, state, pos) -> {});

  /** Hook called on all tool modifiers after shearing an entity */
  public static final ModifierHook<ShearsModifierHook> SHEAR_ENTITY = register("shear_entity", ShearsModifierHook.class, ShearsModifierHook.ALL_MERGER, (tool, modifier, player, entity, isTarget) -> {});

  /** Hook called on all tool modifiers after transforming a block */
  public static final ModifierHook<BlockTransformModifierHook> BLOCK_TRANSFORM = register("block_transform",
    BlockTransformModifierHook.class, BlockTransformModifierHook.ALL_MERGER, BlockTransformModifierHook.EMPTY);


  /** Registers a new modifier hook under {@code tconstruct} */
  private static <T> ModifierHook<T> register(String name, Class<T> filter, @Nullable Function<Collection<T>,T> merger, T defaultInstance) {
    return ModifierHooks.register(TConstruct.getResource(name), filter, defaultInstance, merger);
  }

  /** Registers a new modifier hook under {@code tconstruct}  that cannot merge */
  @SuppressWarnings("SameParameterValue")
  private static <T> ModifierHook<T> register(String name, Class<T> filter, T defaultInstance) {
    return register(name, filter, null, defaultInstance);
  }
}
