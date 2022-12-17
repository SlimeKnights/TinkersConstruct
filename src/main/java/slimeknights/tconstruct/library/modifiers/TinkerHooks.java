package slimeknights.tconstruct.library.modifiers;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hook.ArmorWalkModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ArrowLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ElytraFlightModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.InteractModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.LootingModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.PlantHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ShearsModifierHook;
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
@SuppressWarnings("deprecation")
public class TinkerHooks {
  private TinkerHooks() {}


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

  /** Hook for firing arrows to modify the entity post firing */
  public static final ModifierHook<ArrowLaunchModifierHook> ARROW_LAUNCH = register("arrow_launch", ArrowLaunchModifierHook.class, ArrowLaunchModifierHook.ALL_MERGER, ArrowLaunchModifierHook.EMPTY);


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

  /** Hook for when the player interacts with an armor slot. Currently only impleented for helmets and leggings */
  public static final ModifierHook<InteractModifierHook> ARMOR_INTERACT = register("armor_interact", InteractModifierHook.class, InteractModifierHook.MERGER, new InteractModifierHook() {
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


  /** Registers a new modifier hook under {@code tconstruct} */
  private static <T> ModifierHook<T> register(String name, Class<T> filter, @Nullable Function<Collection<T>,T> merger, T defaultInstance) {
    return ModifierHooks.register(TConstruct.getResource(name), filter, defaultInstance, merger);
  }

  /** Registers a new modifier hook under {@code tconstruct}  that cannot merge */
  private static <T> ModifierHook<T> register(String name, Class<T> filter, T defaultInstance) {
    return register(name, filter, null, defaultInstance);
  }
}
