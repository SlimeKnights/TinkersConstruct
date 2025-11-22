package slimeknights.tconstruct.common.data;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect.DamageTypePair;

import static slimeknights.tconstruct.TConstruct.prefix;
import static slimeknights.tconstruct.common.TinkerDamageTypes.BLEEDING;
import static slimeknights.tconstruct.common.TinkerDamageTypes.ENTANGLED;
import static slimeknights.tconstruct.common.TinkerDamageTypes.EXPLOSION;
import static slimeknights.tconstruct.common.TinkerDamageTypes.FISHING_HOOK;
import static slimeknights.tconstruct.common.TinkerDamageTypes.FLUID_COLD;
import static slimeknights.tconstruct.common.TinkerDamageTypes.FLUID_FIRE;
import static slimeknights.tconstruct.common.TinkerDamageTypes.FLUID_IMPACT;
import static slimeknights.tconstruct.common.TinkerDamageTypes.FLUID_MAGIC;
import static slimeknights.tconstruct.common.TinkerDamageTypes.FLUID_SPIKE;
import static slimeknights.tconstruct.common.TinkerDamageTypes.KNIGHTMETAL;
import static slimeknights.tconstruct.common.TinkerDamageTypes.MELEE_ARROW;
import static slimeknights.tconstruct.common.TinkerDamageTypes.MOB_EXPLOSION;
import static slimeknights.tconstruct.common.TinkerDamageTypes.PIERCING;
import static slimeknights.tconstruct.common.TinkerDamageTypes.SELF_DESTRUCT;
import static slimeknights.tconstruct.common.TinkerDamageTypes.SHOCK;
import static slimeknights.tconstruct.common.TinkerDamageTypes.SMELTERY_HEAT;
import static slimeknights.tconstruct.common.TinkerDamageTypes.SMELTERY_MAGIC;
import static slimeknights.tconstruct.common.TinkerDamageTypes.THROWN_TOOL;
import static slimeknights.tconstruct.common.TinkerDamageTypes.WATER;

/** Datagen for damage types */
public class DamageTypeProvider implements RegistrySetBuilder.RegistryBootstrap<DamageType> {
  private DamageTypeProvider() {}

  /** Registers this provider with the registry set builder */
  public static void register(RegistrySetBuilder builder) {
    builder.add(Registries.DAMAGE_TYPE, new DamageTypeProvider());
  }

  @Override
  public void run(BootstapContext<DamageType> context) {
    context.register(SMELTERY_HEAT, new DamageType(prefix("smeltery_heat"), DamageScaling.NEVER, 0.1f, DamageEffects.BURNING));
    context.register(SMELTERY_MAGIC, new DamageType(prefix("smeltery_magic"), DamageScaling.NEVER, 0.1f, DamageEffects.BURNING));
    context.register(KNIGHTMETAL, new DamageType(prefix("knightmetal"), DamageScaling.NEVER, 0.1f));
    context.register(THROWN_TOOL, new DamageType(prefix("thrown_tool"), 0.1f));
    context.register(FISHING_HOOK, new DamageType(prefix("fishing_hook"), 0.1f));
    context.register(PIERCING, new DamageType(prefix("piercing"), 0.1f));
    context.register(BLEEDING, new DamageType(prefix("bleed"), DamageScaling.NEVER, 0.1f));
    context.register(ENTANGLED, new DamageType(prefix("entangled"), DamageScaling.NEVER, 0.1f));
    context.register(SHOCK, new DamageType(prefix("shock"), 0.1f, DamageEffects.BURNING));
    context.register(SELF_DESTRUCT, new DamageType(prefix("self_destruct"), DamageScaling.NEVER, 0.1f));
    context.register(MELEE_ARROW, new DamageType("arrow", 0.1f));
    register(context, EXPLOSION, new DamageType("explosion", DamageScaling.NEVER, 0.1f));
    register(context, MOB_EXPLOSION, new DamageType("explosion.player", DamageScaling.NEVER, 0.1f));

    // fluid effects
    register(context, FLUID_IMPACT, new DamageType(prefix("fluid.impact"), 0.1f, DamageEffects.HURT));
    register(context, FLUID_FIRE, new DamageType(prefix("fluid.fire"), 0.1f, DamageEffects.BURNING));
    register(context, FLUID_COLD, new DamageType(prefix("fluid.cold"), 0.1f, DamageEffects.FREEZING));
    register(context, FLUID_MAGIC, new DamageType(prefix("fluid.magic"), 0.1f, DamageEffects.HURT));
    register(context, WATER, new DamageType(prefix("fluid.water"), 0.1f, DamageEffects.DROWNING));
    register(context, FLUID_SPIKE, new DamageType(prefix("fluid.spike"), 0.1f, DamageEffects.THORNS));
  }

  /** Registers a damage type pair for a fluid effect */
  private static void register(BootstapContext<DamageType> context, DamageTypePair pair, DamageType damageType) {
    context.register(pair.melee(), damageType);
    context.register(pair.ranged(), damageType);
  }
}
