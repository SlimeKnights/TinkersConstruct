package slimeknights.tconstruct.common;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect.DamageTypePair;

import javax.annotation.Nullable;

/** Handles any custom damage types in the mod */
public class TinkerDamageTypes {
  private TinkerDamageTypes() {}

  /* World */
  /** Standard damage source for melting most mobs */
  public static final ResourceKey<DamageType> SMELTERY_HEAT = create("smeltery_heat");
  /** Special damage source for "absorbing" hot entities */
  public static final ResourceKey<DamageType> SMELTERY_MAGIC = create("smeltery_magic");
  /** Damage from a knightmetal block */
  public static final ResourceKey<DamageType> KNIGHTMETAL = create("knightmetal");

  /** Damage from throwing a tool */
  public static final ResourceKey<DamageType> THROWN_TOOL = create("thrown_tool");
  /** Damage from a fishing rod */
  public static final ResourceKey<DamageType> FISHING_HOOK = create("fishing_hook");

  /* Modifiers */
  /** Damage source that bypasses armor */
  public static final ResourceKey<DamageType> PIERCING = create("piercing");
  /** Damage source for potion effect, bypassing armor */
  public static final ResourceKey<DamageType> BLEEDING = create("bleeding");
  /** Damage source entangled modifier from twisted vines */
  public static final ResourceKey<DamageType> ENTANGLED = create("entangled");
  /** Damage source for shock modifier */
  public static final ResourceKey<DamageType> SHOCK = create("shock");
  /** Damage source for the self-destructing modifier */
  public static final ResourceKey<DamageType> SELF_DESTRUCT = create("self_destruct");

  /** Damage source for a non-projectile with arrow death messages */
  public static final ResourceKey<DamageType> MELEE_ARROW = create("melee_arrow");
  /** Damage source for an explosion that does not scale with difficulty */
  public static final DamageTypePair EXPLOSION = createPair("explosion");
  /** Damage source for an entity caused explosion that does not scale with difficulty */
  public static final DamageTypePair MOB_EXPLOSION = createPair("mob_explosion");

  /* Fluid effects */
  /** Effect for direct fluid damage with no special immunities */
  public static final DamageTypePair FLUID_IMPACT = createPair("fluid_impact");
  /** Effect for a flaming fluid */
  public static final DamageTypePair FLUID_FIRE = createPair("fluid_fire");
  /** Effect for cold damage */
  public static final DamageTypePair FLUID_COLD = createPair("fluid_cold");
  /** Effect for a magic fluid */
  public static final DamageTypePair FLUID_MAGIC = createPair("fluid_magic");
  /** Effect for water */
  public static final DamageTypePair WATER = createPair("water");
  /** Effect for armor piercing fluid damage */
  public static final DamageTypePair FLUID_SPIKE = createPair("fluid_spike");


  /** Creates a new damage type tag */
  private static ResourceKey<DamageType> create(String name) {
    return ResourceKey.create(Registries.DAMAGE_TYPE, TConstruct.getResource(name));
  }
  /** Creates a new damage type tag */
  private static DamageTypePair createPair(String name) {
    return new DamageTypePair(
      create(name + "_melee"),
      create(name + "_ranged")
    );
  }

  /** Creates a new damage source using a custom type */
  public static DamageSource source(RegistryAccess access, ResourceKey<DamageType> type, @Nullable Entity direct, @Nullable Entity causing) {
    return new DamageSource(access.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), direct, causing);
  }

  /** Creates a new damage source using a custom typ with a single entity */
  public static DamageSource source(RegistryAccess access, ResourceKey<DamageType> type, @Nullable Entity entity) {
    return source(access, type, entity, entity);
  }

  /** Creates a new damage source using a custom type with no entity */
  public static DamageSource source(RegistryAccess access, ResourceKey<DamageType> type) {
    return source(access, type, null, null);
  }
}
