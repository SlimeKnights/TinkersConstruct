package slimeknights.tconstruct.library.modifiers;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.modifiers.hook.armor.ArmorWalkModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ElytraFlightModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.armor.ProtectionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.EnchantmentModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.MaterialRepairModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ProcessLootModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.RepairFactorModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.CraftCountModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ModifierTraitHook;
import slimeknights.tconstruct.library.modifiers.hook.build.RawDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ValidateModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.ArmorLootingModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.DamageDealtModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.LootingModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.DurabilityDisplayModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.RequirementsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.AreaOfEffectHighlightModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.SlotStackModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedContext;
import slimeknights.tconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.RemoveBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.LauncherHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileFuseModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileShootModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ScheduledProjectileTaskModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.BlockTransformModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.CapacityBarHook;
import slimeknights.tconstruct.library.modifiers.hook.special.PlantHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.ShearsModifierHook;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;
import slimeknights.tconstruct.library.utils.Schedule.Scheduler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/** Collection of all hooks implemented by the mod natively */
public class ModifierHooks {
  ModifierHooks() {}

  /** Loader for modifier hooks */
  public static final IdAwareComponentRegistry<ModuleHook<?>> LOADER = new IdAwareComponentRegistry<>("Unknown Modifier Hook");

  public static void init() {}


  /* General */

  /** Generic hook for stats conditioned on the entity holding the tool */
  public static final ModuleHook<ConditionalStatModifierHook> CONDITIONAL_STAT = register("conditional_stat", ConditionalStatModifierHook.class, ConditionalStatModifierHook.AllMerger::new, (tool, modifier, living, stat, baseValue, multiplier) -> baseValue);

  /** Hook for modifiers checking if they can perform a tool action */
  public static final ModuleHook<ToolActionModifierHook> TOOL_ACTION = register("tool_action", ToolActionModifierHook.class, ToolActionModifierHook.AnyMerger::new, (tool, modifier, toolAction) -> false);

  /** Hook used when any {@link EquipmentSlot} changes on an entity while using at least one tool */
  public static final ModuleHook<EquipmentChangeModifierHook> EQUIPMENT_CHANGE = register("equipment_change", EquipmentChangeModifierHook.class, EquipmentChangeModifierHook.AllMerger::new, new EquipmentChangeModifierHook() {});

  /** Hook for modifying the repair amount for tools */
  public static final ModuleHook<RepairFactorModifierHook> REPAIR_FACTOR = register("repair_factor", RepairFactorModifierHook.class, RepairFactorModifierHook.ComposeMerger::new, (tool, entry, factor) -> factor);
  /** Hook for allowing an extra material to repair the tool */
  public static final ModuleHook<MaterialRepairModifierHook> MATERIAL_REPAIR = register("material_repair", MaterialRepairModifierHook.class, MaterialRepairModifierHook.MaxMerger::new, new MaterialRepairModifierHook() {
    @Override
    public boolean isRepairMaterial(IToolStackView tool, ModifierEntry modifier, MaterialId material) {
      return false;
    }

    @Override
    public float getRepairAmount(IToolStackView tool, ModifierEntry modifier, MaterialId material) {
      return 0;
    }
  });

  /** Hook for modifying the damage amount for tools */
  public static final ModuleHook<ToolDamageModifierHook> TOOL_DAMAGE = register("tool_damage", ToolDamageModifierHook.class, ToolDamageModifierHook.Merger::new, (tool, modifier, amount, holder) -> amount);

  /** Hook running while the tool is in the inventory */
  public static final ModuleHook<InventoryTickModifierHook> INVENTORY_TICK = register("inventory_tick", InventoryTickModifierHook.class, InventoryTickModifierHook.AllMerger::new, (tool, modifier, world, holder, itemSlot, isSelected, isCorrectSlot, stack) -> {});

  /* Technical */

  /** Hook for working with capacity bars, mainly used for durability bars  */
  public static final ModuleHook<CapacityBarHook> CAPACITY_BAR = register("capacity_bar", CapacityBarHook.class, new CapacityBarHook() {
    @Override
    public int getAmount(IToolStackView tool) {
      return 0;
    }

    @Override
    public int getCapacity(IToolStackView tool, ModifierEntry entry) {
      return 0;
    }

    @Override
    public void setAmount(IToolStackView tool, ModifierEntry entry, int amount) {}
  });


  /* Composable only  */

  /** Hook for supporting modifiers to change the modifier display name */
  public static final ModuleHook<DisplayNameModifierHook> DISPLAY_NAME = register("display_name", DisplayNameModifierHook.class, DisplayNameModifierHook.ComposeMerger::new, (tool, entry, name, access) -> name);


  /* Display */

  /** Hook for modifiers adding additional information to the tooltip */
  public static final ModuleHook<TooltipModifierHook> TOOLTIP = register("tooltip", TooltipModifierHook.class, TooltipModifierHook.AllMerger::new, (tool, modifier, player, tooltip, tooltipKey, tooltipFlag) -> {});

  /** Hook for changing the itemstack durability bar */
  public static final ModuleHook<DurabilityDisplayModifierHook> DURABILITY_DISPLAY = register("durability_display", DurabilityDisplayModifierHook.class, DurabilityDisplayModifierHook.FirstMerger::new, new DurabilityDisplayModifierHook() {
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

  /** Hook for displaying a list of requirements on teh tool and a hint for the requirements in recipe viewers */
  public static final ModuleHook<RequirementsModifierHook> REQUIREMENTS = register("requirements", RequirementsModifierHook.class, RequirementsModifierHook.FirstMerger::new, new RequirementsModifierHook() {});


  /* Tool Building */

  /** Hook called on tool crafting to allow modifying the amount crafted */
  public static final ModuleHook<CraftCountModifierHook> CRAFT_COUNT = register("craft_count", CraftCountModifierHook.class, CraftCountModifierHook.ComposeMerger::new, (context, modifier, amount) -> amount);

  /** Hook for adding raw unconditional stats to a tool */
  public static final ModuleHook<ToolStatsModifierHook> TOOL_STATS = register("modifier_stats", ToolStatsModifierHook.class, ToolStatsModifierHook.AllMerger::new, (context, modifier, builder) -> {});

  /** Hook for adding item stack attributes to a tool when in the proper slot */
  public static final ModuleHook<AttributesModifierHook> ATTRIBUTES = register("attributes", AttributesModifierHook.class, AttributesModifierHook.AllMerger::new, (tool, modifier, slot, consumer) -> {});

  /** Hook for adding item stack attributes to a tool when in the proper slot */
  public static final ModuleHook<EnchantmentModifierHook> ENCHANTMENTS = register("enchantments", EnchantmentModifierHook.class, EnchantmentModifierHook.AllMerger::new, new EnchantmentModifierHook() {
    @Override
    public int updateEnchantmentLevel(IToolStackView tool, ModifierEntry modifier, Enchantment enchantment, int level) {
      return level;
    }

    @Override
    public void updateEnchantments(IToolStackView tool, ModifierEntry modifier, Map<Enchantment,Integer> map) {}
  });

  /** Hook to add data that resets every time stats rebuild */
  public static final ModuleHook<VolatileDataModifierHook> VOLATILE_DATA = register("volatile_data", VolatileDataModifierHook.class, VolatileDataModifierHook.AllMerger::new, (context, modifier, volatileData) -> {});

  /** Hook to add and remove data directly to the tools NBT. It is generally better to use persistent data or volatile data when possible. */
  public static final ModuleHook<RawDataModifierHook> RAW_DATA = register("raw_data", RawDataModifierHook.class, RawDataModifierHook.AllMerger::new, new RawDataModifierHook() {
    @Override
    public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {}

    @Override
    public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {}
  });

  /**
   * Hook called to give a modifier a chance to clean up data while on the tool and to reject the current tool state.
   * TOD0 1.21: rename to disambiguate from {@link #VALIDATE_UPGRADE}.
   */
  public static final ModuleHook<ValidateModifierHook> VALIDATE;
  /** Same as {@link #VALIDATE}, but only called on modifiers in {@link slimeknights.tconstruct.library.tools.nbt.ToolStack#getUpgrades()}. */
  public static final ModuleHook<ValidateModifierHook> VALIDATE_UPGRADE;
  static {
    Function<Collection<ValidateModifierHook>,ValidateModifierHook> merger = ValidateModifierHook.AllMerger::new;
    ValidateModifierHook defaultInstance = (tool, modifier) -> null;
    VALIDATE = register("validate", ValidateModifierHook.class, merger, defaultInstance);
    VALIDATE_UPGRADE = register("validate_upgrade", ValidateModifierHook.class, merger, defaultInstance);
  }

  /** Hook called when a modifier is removed to give it a chance to clean up data */
  public static final ModuleHook<ModifierRemovalHook> REMOVE = register("remove", ModifierRemovalHook.class, ModifierRemovalHook.FirstMerger::new, (tool, modifier) -> null);

  /** Hook for a modifier to add other modifiers to the builder */
  public static final ModuleHook<ModifierTraitHook> MODIFIER_TRAITS = register("modifier_traits", ModifierTraitHook.class, ModifierTraitHook.AllMerger::new, (context, modifier, builder, firstEncounter) -> {});

  /* Combat */

  /** Hook to adjust melee damage when a weapon is attacking an entity */
  public static final ModuleHook<MeleeDamageModifierHook> MELEE_DAMAGE;
  /** Hook to adjust melee damage for monsters that lack player left click actions */
  public static final ModuleHook<MeleeDamageModifierHook> MONSTER_MELEE_DAMAGE;
  static {
    MeleeDamageModifierHook defaultInstance = (tool, modifier, context, baseDamage, damage) -> damage;
    Function<Collection<MeleeDamageModifierHook>,MeleeDamageModifierHook> merger = MeleeDamageModifierHook.AllMerger::new;
    MELEE_DAMAGE = register("melee_damage", MeleeDamageModifierHook.class, merger, defaultInstance);
    MONSTER_MELEE_DAMAGE = register("monster_melee_damage", MeleeDamageModifierHook.class, merger, defaultInstance);
  }

  /** Hook called when an entity is attacked to apply special effects */
  public static final ModuleHook<MeleeHitModifierHook> MELEE_HIT = register("melee_hit", MeleeHitModifierHook.class, MeleeHitModifierHook.AllMerger::new, new MeleeHitModifierHook() {});
  /** Hook called when an entity is attacked by a monster that lacks player left click actions */
  public static final ModuleHook<MonsterMeleeHitModifierHook> MONSTER_MELEE_HIT = register("monster_melee_hit", MonsterMeleeHitModifierHook.class, MonsterMeleeHitModifierHook.AllMerger::new, (tool, modifier, context, damage) -> {});

  /** Hook called when taking damage wearing this armor to reduce the damage, runs after {@link #MODIFY_HURT} and before {@link #MODIFY_DAMAGE} */
  public static final ModuleHook<ProtectionModifierHook> PROTECTION = register("protection", ProtectionModifierHook.class, ProtectionModifierHook.AllMerger::new, (tool, modifier, context, slotType, source, modifierValue) -> modifierValue);

  /** Hook called when taking damage wearing this armor to cancel the damage */
  public static final ModuleHook<DamageBlockModifierHook> DAMAGE_BLOCK = register("damage_block", DamageBlockModifierHook.class, DamageBlockModifierHook.AnyMerger::new, (tool, modifier, context, slotType, source, amount) -> false);
  /** Hook called when taking damage to apply secondary effects such as counterattack or healing. Runs after {@link #DAMAGE_BLOCK} but before vanilla effects that cancel damage. */
  public static final ModuleHook<OnAttackedModifierHook> ON_ATTACKED = register("on_attacked", OnAttackedModifierHook.class, OnAttackedModifierHook.AllMerger::new, (tool, modifier, context, slotType, source, amount, isDirectDamage) -> {});

  /** Hook allowing modifying damage taken or responding when damage is taken. Runs after {@link #ON_ATTACKED} and any vanilla effects that cancel damage, but before armor reduction and {@link #PROTECTION}.  */
  public static final ModuleHook<ModifyDamageModifierHook> MODIFY_HURT;
  /** Hook allowing modifying damage taken or responding when damage is taken. Runs after {@link #PROTECTION}, armor damage reduction, and absorption.  */
  public static final ModuleHook<ModifyDamageModifierHook> MODIFY_DAMAGE;
  static {
    Function<Collection<ModifyDamageModifierHook>,ModifyDamageModifierHook> merger = ModifyDamageModifierHook.AllMerger::new;
    ModifyDamageModifierHook fallback = (tool, modifier, context, slotType, source, amount, isDirectDamage) -> amount;
    MODIFY_HURT = register("modify_hurt", ModifyDamageModifierHook.class, merger, fallback);
    MODIFY_DAMAGE = register("modify_damage", ModifyDamageModifierHook.class, merger, fallback);
  }

  /** Hook called when dealing damage while wearing this equipment */
  public static final ModuleHook<DamageDealtModifierHook> DAMAGE_DEALT = register("damage_dealt", DamageDealtModifierHook.class, DamageDealtModifierHook.AllMerger::new, (tool, modifier, context, slotType, target, source, amount, isDirectDamage) -> {});

  /* Loot */

  /** Hook to modify the results of a loot table */
  public static final ModuleHook<ProcessLootModifierHook> PROCESS_LOOT = register("process_loot", ProcessLootModifierHook.class, ProcessLootModifierHook.AllMerger::new, (tool, modifier, loot, context) -> {});
  /** Hook for a tool boosting the looting value */
  public static final ModuleHook<LootingModifierHook> WEAPON_LOOTING = register("weapon_looting", LootingModifierHook.class, LootingModifierHook.ComposeMerger::new, (tool, modifier, context, looting) -> looting);
  /** Hook for leggings boosting the tool's looting level */
  public static final ModuleHook<ArmorLootingModifierHook> ARMOR_LOOTING = register("armor_looting", ArmorLootingModifierHook.class, ArmorLootingModifierHook.ComposeMerger::new, (tool, modifier, context, equipment, slot, looting) -> looting);
  /** Hook for armor adding harvest enchantments to a held tool based on the tool's modifiers */
  public static final ModuleHook<HarvestEnchantmentsModifierHook> HARVEST_ENCHANTMENTS = register("harvest_enchantments", HarvestEnchantmentsModifierHook.class, HarvestEnchantmentsModifierHook.AllMerger::new, (tool, modifier, context, equipment, slot, map) -> {});


  /* Harvest */

  /** Hook for conditionally modifying the break speed of a block */
  public static final ModuleHook<BreakSpeedModifierHook> BREAK_SPEED = register("break_speed", BreakSpeedModifierHook.class, BreakSpeedModifierHook.AllMerger::new, new BreakSpeedModifierHook() {
    @Override
    public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {}

    @Override
    public float modifyBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeedContext context, float speed) {
      return speed;
    }
  });

  /** Called when a block is broken by a tool to allow the modifier to take over the block removing logic */
  public static final ModuleHook<RemoveBlockModifierHook> REMOVE_BLOCK = register("remove_block", RemoveBlockModifierHook.class, RemoveBlockModifierHook.FirstMerger::new, (tool, modifier, context) -> null);

  /** Called after a block is broken by a tool for every block in the AOE */
  public static final ModuleHook<BlockBreakModifierHook> BLOCK_BREAK = register("block_break", BlockBreakModifierHook.class, BlockBreakModifierHook.AllMerger::new, (tool, modifier, context) -> {});

  /** Called after all blocks in the AOE are broken */
  public static final ModuleHook<BlockHarvestModifierHook> BLOCK_HARVEST = register("block_harvest", BlockHarvestModifierHook.class, BlockHarvestModifierHook.AllMerger::new, (tool, modifier, context, didHarvest) -> {});


  /* Ranged */

  /** Hook for firing arrows or other projectiles to modify the entity post firing */
  public static final ModuleHook<ProjectileLaunchModifierHook> PROJECTILE_LAUNCH;
  /** Hook for throwing a projectile that will not be firing {@link #PROJECTILE_HIT} later, such as javelins. */
  public static final ModuleHook<ProjectileShootModifierHook> PROJECTILE_THROWN;
  /** Hook for when a projectile is launched, but called with the projectile tool rather than the launcher */
  public static final ModuleHook<ProjectileShootModifierHook> PROJECTILE_SHOT;
  static {
    ProjectileLaunchModifierHook defaultInstance = (tool, modifier, shooter, projectile, arrow, persistentData, primary) -> {};
    Function<Collection<ProjectileShootModifierHook>,ProjectileShootModifierHook> merger = ProjectileShootModifierHook.AllMerger::new;
    PROJECTILE_LAUNCH = register("projectile_launch", ProjectileLaunchModifierHook.class, ProjectileLaunchModifierHook.AllMerger::new, defaultInstance);
    PROJECTILE_SHOT = register("projectile_shot", ProjectileShootModifierHook.class, merger, defaultInstance);
    PROJECTILE_THROWN = register("projectile_thrown", ProjectileShootModifierHook.class, merger, defaultInstance);
  }
  /** Hook called when an arrow hits an entity or block on the serverside. TODO 1.21: run this hook on the client too. */
  public static final ModuleHook<ProjectileHitModifierHook> PROJECTILE_HIT;
  /** Hook called when an arrow hits an entity or block on the clientside. Separate from {@link #PROJECTILE_HIT} to prevent a breaking change. TODO 1.21: merge into {@link #PROJECTILE_LAUNCH} */
  public static final ModuleHook<ProjectileHitModifierHook> PROJECTILE_HIT_CLIENT;
  static {
    ProjectileHitModifierHook defaultInstance = new ProjectileHitModifierHook() {};
    Function<Collection<ProjectileHitModifierHook>,ProjectileHitModifierHook> merger = ProjectileHitModifierHook.AllMerger::new;
    PROJECTILE_HIT = register("projectile_hit", ProjectileHitModifierHook.class, merger, defaultInstance);
    PROJECTILE_HIT_CLIENT = register("projectile_hit_client", ProjectileHitModifierHook.class, merger, defaultInstance);
  }

  /** Hook called when a projectile hits an entity with context on the tool that launched it. Allows modifiers such as melting or spilling to work. */
  public static final ModuleHook<LauncherHitModifierHook> LAUNCHER_HIT = register("launcher_hit", LauncherHitModifierHook.class, LauncherHitModifierHook.AllMerger::new, new LauncherHitModifierHook() {});
  /** Hook called when {@link slimeknights.tconstruct.tools.modules.ranged.ammo.ProjectileFuseModule} removes a projectile. */
  public static final ModuleHook<ProjectileFuseModifierHook> PROJECTILE_FUSE = register("projectile_fuse", ProjectileFuseModifierHook.class, ProjectileFuseModifierHook.AllMerger::new, (modifiers, persistentData, modifier, ammo, projectile, arrow) -> {});
  /** Hook called when a bow is looking for ammo. Does not support merging multiple hooks on one modifier */
  public static final ModuleHook<BowAmmoModifierHook> BOW_AMMO = register("bow_ammo", BowAmmoModifierHook.class, BowAmmoModifierHook.EMPTY);

  /** Hook for scheduling tasks to happen later in a projectile's lifetime. Only works on modifiable projectiles, launchers such as bows will never use this hook. */
  public static final ModuleHook<ScheduledProjectileTaskModifierHook> SCHEDULE_PROJECTILE_TASK = register("schedule_projectile_task", ScheduledProjectileTaskModifierHook.class, ScheduledProjectileTaskModifierHook.ScheduleMerger::new, new ScheduledProjectileTaskModifierHook() {
    @Override
    public void scheduleProjectileTask(IToolStackView tool, ModifierEntry modifier, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, Scheduler scheduler) {}

    @Override
    public void onScheduledProjectileTask(IToolStackView tool, ModifierEntry modifier, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow, ModDataNBT persistentData, int task) {}
  });


  /* Misc Armor */

  /** Hook for when the player flies using an elytra, called on the chestplate slot */
  public static final ModuleHook<ElytraFlightModifierHook> ELYTRA_FLIGHT = register("elytra_flight", ElytraFlightModifierHook.class, ElytraFlightModifierHook.FirstMerger::new, (tool, modifier, entity, flightTicks) -> false);

  /** Hook for when the player walks from one position to another, called on the boots slot */
  public static final ModuleHook<ArmorWalkModifierHook> BOOT_WALK = register("boot_walk", ArmorWalkModifierHook.class, ArmorWalkModifierHook.AllMerger::new, (tool, modifier, living, prevPos, newPos) -> {});


  /* Interaction */

  /**
   * Hook for regular interactions not targeting blocks or entities. Needed for charged interactions, while other hooks may be better for most interactions.
   * Note the charged interaction hooks will only fire for the modifier that called {@link GeneralInteractionModifierHook#startUsing(IToolStackView, ModifierId, LivingEntity, InteractionHand)},
   * meaning there is no need to manually track that you were called.
   */
  public static final ModuleHook<GeneralInteractionModifierHook> GENERAL_INTERACT = register("general_interact", GeneralInteractionModifierHook.class, GeneralInteractionModifierHook.FirstMerger::new, ((tool, modifier, player, hand, source) -> InteractionResult.PASS));
  /** Called when the player is actively using this tool, regardless of active modifier. */
  public static final ModuleHook<UsingToolModifierHook> TOOL_USING = register("tool_using", UsingToolModifierHook.class, UsingToolModifierHook.AllMerger::new, new UsingToolModifierHook() {});
  /** Hook for interacting with blocks */
  public static final ModuleHook<BlockInteractionModifierHook> BLOCK_INTERACT = register("block_interact", BlockInteractionModifierHook.class, BlockInteractionModifierHook.FirstMerger::new, new BlockInteractionModifierHook() {});
  /** Hook for interacting with entities */
  public static final ModuleHook<EntityInteractionModifierHook> ENTITY_INTERACT = register("entity_interact", EntityInteractionModifierHook.class, EntityInteractionModifierHook.FirstMerger::new, new EntityInteractionModifierHook() {});
  /** Hook for when the player interacts with an armor slot. Currently, only implemented for helmets and leggings */
  public static final ModuleHook<KeybindInteractModifierHook> ARMOR_INTERACT = register("armor_interact", KeybindInteractModifierHook.class, KeybindInteractModifierHook.InteractMerger::new, new KeybindInteractModifierHook() {});
  /** Hook for determining if a block should be highlighted when in AOE range of the tool */
  public static final ModuleHook<AreaOfEffectHighlightModifierHook> AOE_HIGHLIGHT = register("aoe_highlight", AreaOfEffectHighlightModifierHook.class, AreaOfEffectHighlightModifierHook.AnyMerger::new, (tool, modifier, context, pos, state) -> false);
  /** Hook for overriding behavior of right-clicking tools or with tools */
  public static final ModuleHook<SlotStackModifierHook> SLOT_STACK = register("slot_stack", SlotStackModifierHook.class, SlotStackModifierHook.FirstMerger::new, new SlotStackModifierHook() {});


  /* Modifier sub-hooks */

  /** Hook called on all tool modifiers after the harvest modifier harvests a crop */
  public static final ModuleHook<PlantHarvestModifierHook> PLANT_HARVEST = register("plant_harvest", PlantHarvestModifierHook.class, PlantHarvestModifierHook.AllMerger::new, (tool, modifier, context, world, state, pos) -> {});

  /** Hook called on all tool modifiers after shearing an entity */
  public static final ModuleHook<ShearsModifierHook> SHEAR_ENTITY = register("shear_entity", ShearsModifierHook.class, ShearsModifierHook.AllMerger::new, (tool, modifier, player, entity, isTarget) -> {});

  /** Hook called on all tool modifiers after transforming a block */
  public static final ModuleHook<BlockTransformModifierHook> BLOCK_TRANSFORM = register("block_transform", BlockTransformModifierHook.class, BlockTransformModifierHook.AllMerger::new, (tool, modifier, context, state, pos, action) -> {});


  /* Registration */

  /** Registers a new modifier hook */
  public static <T> ModuleHook<T> register(ResourceLocation name, Class<T> filter, @Nullable Function<Collection<T>,T> merger, T defaultInstance) {
    return LOADER.register(new ModuleHook<>(name, filter, merger, defaultInstance));
  }

  /** Registers a new unmergable modifier hook */
  public static <T> ModuleHook<T> register(ResourceLocation name, Class<T> filter, T defaultInstance) {
    return register(name, filter, null, defaultInstance);
  }

  /** Registers a new modifier hook under {@code tconstruct} */
  private static <T> ModuleHook<T> register(String name, Class<T> filter, @Nullable Function<Collection<T>,T> merger, T defaultInstance) {
    return register(TConstruct.getResource(name), filter, merger, defaultInstance);
  }

  /** Registers a new modifier hook under {@code tconstruct}  that cannot merge */
  @SuppressWarnings("SameParameterValue")
  private static <T> ModuleHook<T> register(String name, Class<T> filter, T defaultInstance) {
    return register(name, filter, null, defaultInstance);
  }
}
