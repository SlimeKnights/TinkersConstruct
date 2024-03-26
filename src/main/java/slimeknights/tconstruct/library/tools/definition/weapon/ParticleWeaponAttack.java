package slimeknights.tconstruct.library.tools.definition.weapon;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Weapon attack that just spawns an extra particle */
public record ParticleWeaponAttack(SimpleParticleType particle) implements IWeaponAttack {
  public static final RecordLoadable<ParticleWeaponAttack> LOADER = RecordLoadable.create(
    Loadables.PARTICLE_TYPE.comapFlatMap((type, error) -> {
      if (type instanceof SimpleParticleType simple) {
        return simple;
      }
      throw error.create("Expected particle " + Registry.PARTICLE_TYPE.getKey(type) + " be a simple particle, got " + type);
    }, type -> type).requiredField("particle", ParticleWeaponAttack::particle), ParticleWeaponAttack::new);

  @Override
  public boolean dealDamage(IToolStackView tool, ToolAttackContext context, float damage) {
    boolean hit = ToolAttackUtil.dealDefaultDamage(context.getAttacker(), context.getTarget(), damage);
    if (hit && context.isFullyCharged()) {
      ToolAttackUtil.spawnAttackParticle(particle, context.getAttacker(), 0.8d);
    }
    return hit;
  }

  @Override
  public IGenericLoader<? extends IWeaponAttack> getLoader() {
    return LOADER;
  }
}
