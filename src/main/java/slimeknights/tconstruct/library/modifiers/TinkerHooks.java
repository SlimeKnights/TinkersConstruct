package slimeknights.tconstruct.library.modifiers;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hook.ArmorWalkModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.BlockTransformModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.BowAmmoModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ElytraFlightModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.LootingModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.PlantHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ShearsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorLootModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorWalkModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IElytraFlightModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IHarvestModifier;
import slimeknights.tconstruct.library.modifiers.hooks.IShearModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Function;

/** Collection of all hooks implemented by the mod natively */
@SuppressWarnings({"deprecation", "SameParameterValue"})
public class TinkerHooks {
  private TinkerHooks() {}

  public static void init() {}


  /* General */

  /** Generic hook for stats conditioned on the entity holding the tool */
  public static final ModifierHook<ConditionalStatModifierHook> CONDITIONAL_STAT = register("conditional_stat", ConditionalStatModifierHook.class, ConditionalStatModifierHook.ALL_MERGER, ConditionalStatModifierHook.EMPTY);

  /* Loot */

  /** Hook for a tool boosting the looting value */
  public static final ModifierHook<LootingModifierHook> TOOL_LOOTING = register("tool_looting", LootingModifierHook.class, LootingModifierHook.SUM_MERGER,
    (tool, modifier, holder, target, damageSource, looting) -> modifier.getModifier().getLootingValue(tool, modifier.getLevel(), holder, target, damageSource, looting));

  /** Hook for leggings boosting the tool's looting level */
  public static final ModifierHook<LootingModifierHook> LEGGINGS_LOOTING = register("leggings_looting", LootingModifierHook.class, LootingModifierHook.SUM_MERGER, (tool, modifier, holder, target, damageSource, looting) -> {
    IArmorLootModifier armorLoot = modifier.getModifier().getModule(IArmorLootModifier.class);
    if (armorLoot != null) {
      return armorLoot.getLootingValue(tool, modifier.getLevel(), holder, target, damageSource, looting);
    }
    return looting;
  });

  /** Hook for looting values on a projectile, will receive {@link slimeknights.tconstruct.library.tools.nbt.DummyToolStack} for the tool */
  public static final ModifierHook<LootingModifierHook> PROJECTILE_LOOTING = register("projectile_looting", LootingModifierHook.class, LootingModifierHook.SUM_MERGER, LootingModifierHook.DEFAULT);

  /** Hook for adding harvest enchantments to a held tool based on the tool's modifiers */
  public static final ModifierHook<HarvestEnchantmentsModifierHook> TOOL_HARVEST_ENCHANTMENTS = register("tool_harvest_enchantments", HarvestEnchantmentsModifierHook.class, HarvestEnchantmentsModifierHook.ALL_MERGER,
    (tool, modifier, context, consumer) -> modifier.getModifier().applyHarvestEnchantments(tool, modifier.getLevel(), context, consumer));

  /** Hook for adding harvest enchantments to a held tool based on the legging's modifiers */
  public static final ModifierHook<HarvestEnchantmentsModifierHook> LEGGINGS_HARVEST_ENCHANTMENTS = register("leggings_harvest_enchantments", HarvestEnchantmentsModifierHook.class, HarvestEnchantmentsModifierHook.ALL_MERGER, (tool, modifier, context, consumer) -> {
    IArmorLootModifier armorLoot = modifier.getModifier().getModule(IArmorLootModifier.class);
    if (armorLoot != null) {
      armorLoot.applyHarvestEnchantments(tool, modifier.getLevel(), context, consumer);
    }
  });


  /* Ranged */

  /** Hook for firing arrows or other projectiles to modify the entity post firing */
  public static final ModifierHook<ProjectileLaunchModifierHook> PROJECTILE_LAUNCH = register("projectile_launch", ProjectileLaunchModifierHook.class, ProjectileLaunchModifierHook.ALL_MERGER, ProjectileLaunchModifierHook.EMPTY);
  /** Hook called when an arrow hits an entity or block */
  public static final ModifierHook<ProjectileHitModifierHook> PROJECTILE_HIT = register("projectile_hit", ProjectileHitModifierHook.class, ProjectileHitModifierHook.FIRST_MERGER, ProjectileHitModifierHook.EMPTY);
  /** Hook called when a bow is looking for ammo. Does not support merging multiple hooks on one modifier */
  public static final ModifierHook<BowAmmoModifierHook> BOW_AMMO = register("bow_ammo", BowAmmoModifierHook.class, BowAmmoModifierHook.EMPTY);

  /* Misc Armor */

  /** Hook for when the player flies using an elytra, called on the chestplate slot */
  public static final ModifierHook<ElytraFlightModifierHook> ELYTRA_FLIGHT = register("elytra_flight", ElytraFlightModifierHook.class, ElytraFlightModifierHook.FIRST_MERGER, (tool, modifier, entity, flightTicks) -> {
    IElytraFlightModifier elytraFlight = modifier.getModifier().getModule(IElytraFlightModifier.class);
    return elytraFlight != null && !elytraFlight.elytraFlightTick(tool, modifier.getLevel(), entity, flightTicks);
  });

  /** Hook for when the player walks from one position to another, called on the boots slot */
  public static final ModifierHook<ArmorWalkModifierHook> BOOT_WALK = register("boot_walk", ArmorWalkModifierHook.class, ArmorWalkModifierHook.ALL_MERGER, (tool, modifier, living, prevPos, newPos) -> {
    IArmorWalkModifier armorWalk = modifier.getModifier().getModule(IArmorWalkModifier.class);
    if (armorWalk != null) {
      armorWalk.onWalk(tool, modifier.getLevel(), living, prevPos, newPos);
    }
  });


  /* Interaction */

  /** @deprecated use {@link #CHARGEABLE_INTERACT} with {@link slimeknights.tconstruct.library.tools.helper.ModifierUtil#startUsingItem(IToolStackView, ModifierId, LivingEntity, InteractionHand)} for chargeable actions.
   * For regular interactions, the two are interchangable, though eventually they will be merged into chargable. */
  @Deprecated
  public static final ModifierHook<GeneralInteractionModifierHook> GENERAL_INTERACT = register("general_interact", GeneralInteractionModifierHook.class, GeneralInteractionModifierHook.FIRST_MERGER, GeneralInteractionModifierHook.FALLBACK);

  /** Hook for interactions not targeting blocks or entities. Needed for charge attacks, but other hooks may be better for most interactions.
   * Unlike {@link #GENERAL_INTERACT}, the charged interaction hooks will only fire for the modifier that called {@link slimeknights.tconstruct.library.tools.helper.ModifierUtil#startUsingItem(IToolStackView, ModifierId, LivingEntity, InteractionHand)},
   * meaning there is no need to manually track that you were called. */
  public static final ModifierHook<GeneralInteractionModifierHook> CHARGEABLE_INTERACT = register("chargable_interact", GeneralInteractionModifierHook.class, GeneralInteractionModifierHook.FIRST_MERGER, ((tool, modifier, player, hand, source) ->
    modifier.getHook(GENERAL_INTERACT).onToolUse(tool, modifier, player, hand, source)));

  /** Hook for interacting with blocks */
  public static final ModifierHook<BlockInteractionModifierHook> BLOCK_INTERACT = register("block_interact", BlockInteractionModifierHook.class, BlockInteractionModifierHook.FIRST_MERGER, BlockInteractionModifierHook.FALLBACK);

  /** Hook for interacting with entities */
  public static final ModifierHook<EntityInteractionModifierHook> ENTITY_INTERACT = register("entity_interact", EntityInteractionModifierHook.class, EntityInteractionModifierHook.FIRST_MERGER, EntityInteractionModifierHook.FALLBACK);

  /** Hook for when the player interacts with an armor slot. Currently, only implemented for helmets and leggings */
  public static final ModifierHook<KeybindInteractModifierHook> ARMOR_INTERACT = register("armor_interact", KeybindInteractModifierHook.class, KeybindInteractModifierHook.MERGER, new KeybindInteractModifierHook() {
    @Override
    public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
      IArmorInteractModifier interact = modifier.getModifier().getModule(IArmorInteractModifier.class);
      if (interact != null) {
        return interact.startArmorInteract(tool, modifier.getLevel(), player, slot, keyModifier);
      }
      return false;
    }

    @Override
    public void stopInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
      IArmorInteractModifier interact = modifier.getModifier().getModule(IArmorInteractModifier.class);
      if (interact != null) {
        interact.stopArmorInteract(tool, modifier.getLevel(), player, slot);
      }
    }
  });

  /* Modifier sub-hooks */

  /** Hook called on all tool modifiers after the harvest modifier harvests a crop */
  public static final ModifierHook<PlantHarvestModifierHook> PLANT_HARVEST = register("plant_harvest", PlantHarvestModifierHook.class, PlantHarvestModifierHook.ALL_MERGER, (tool, modifier, context, world, state, pos) -> {
    IHarvestModifier plantHarvest = modifier.getModifier().getModule(IHarvestModifier.class);
    if (plantHarvest != null) {
      plantHarvest.afterHarvest(tool, modifier.getLevel(), context, world, state, pos);
    }
  });

  /** Hook called on all tool modifiers after shearing an entity */
  public static final ModifierHook<ShearsModifierHook> SHEAR_ENTITY = register("shear_entity", ShearsModifierHook.class, ShearsModifierHook.ALL_MERGER, new ShearsModifierHook() {
    @Override
    public void afterShearEntity(IToolStackView tool, ModifierEntry modifier, Player player, Entity entity, boolean isTarget) {
      IShearModifier shearModifier = modifier.getModifier().getModule(IShearModifier.class);
      if (shearModifier != null) {
        shearModifier.afterShearEntity(tool, modifier.getLevel(), player, entity, isTarget);
      }
    }
  });

  /** Hook called on all tool modifiers after transforming a block */
  public static final ModifierHook<BlockTransformModifierHook> BLOCK_TRANSFORM = register("block_transform",
    BlockTransformModifierHook.class, BlockTransformModifierHook.ALL_MERGER, BlockTransformModifierHook.EMPTY);


  /** Registers a new modifier hook under {@code tconstruct} */
  private static <T> ModifierHook<T> register(String name, Class<T> filter, @Nullable Function<Collection<T>,T> merger, T defaultInstance) {
    return ModifierHooks.register(TConstruct.getResource(name), filter, defaultInstance, merger);
  }

  /** Registers a new modifier hook under {@code tconstruct}  that cannot merge */
  private static <T> ModifierHook<T> register(String name, Class<T> filter, T defaultInstance) {
    return register(name, filter, null, defaultInstance);
  }
}
