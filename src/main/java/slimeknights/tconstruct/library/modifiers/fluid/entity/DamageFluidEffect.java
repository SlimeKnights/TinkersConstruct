package slimeknights.tconstruct.library.modifiers.fluid.entity;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext.Entity;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;

import javax.annotation.Nullable;

/**
 * Effect that damages an entity
 * @param damage     Amount of damage to apply
 * @param damageType Damage types to use when hitting
 */
public record DamageFluidEffect(float damage, @Nullable DamageTypePair damageType) implements FluidEffect<FluidEffectContext.Entity> {
  /** Loader for this effect */
  public static final RecordLoadable<DamageFluidEffect> LOADER = RecordLoadable.create(
    FloatLoadable.FROM_ZERO.requiredField("damage", e -> e.damage),
    DamageTypePair.LOADER.nullableField("damage_type", DamageFluidEffect::damageType),
    DamageFluidEffect::new);

  @Override
  public RecordLoadable<DamageFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, Entity context, FluidAction action) {
    float value = level.value();
    if (action.simulate()) {
      return value;
    }
    Projectile projectile = context.getProjectile();
    LivingEntity entity = context.getEntity();

    // if provided a specific damage type, use that
    DamageSource source;
    if (damageType != null) {
      if (projectile != null) {
        source = TinkerDamageTypes.source(context.getLevel().registryAccess(), damageType.ranged, projectile, entity);
      } else {
        source = TinkerDamageTypes.source(context.getLevel().registryAccess(), damageType.melee, entity);
      }
    } else {
      source = context.createDamageSource();
    }
    return ToolAttackUtil.attackEntitySecondary(source, this.damage * value, context.getTarget(), context.getLivingTarget(), true) ? value : 0;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    String translationKey = FluidEffect.getTranslationKey(getLoader());
    if (this.damageType != null) {
      DamageType damageType = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE).get(this.damageType.melee);
      if (damageType != null) {
        translationKey += '.' + damageType.msgId();
      }
    }
    return Component.translatable(translationKey, damage);
  }

  /** Represents a pair of damage types for melee and ranged effects */
  public record DamageTypePair(ResourceKey<DamageType> melee, ResourceKey<DamageType> ranged) {
    public static final RecordLoadable<DamageTypePair> LOADER = RecordLoadable.create(
      Loadables.DAMAGE_TYPE_KEY.requiredField("melee", DamageTypePair::melee),
      Loadables.DAMAGE_TYPE_KEY.requiredField("ranged", DamageTypePair::ranged),
      DamageTypePair::new);

    /** Gets all pair values, useful for datagen */
    @SuppressWarnings("unchecked")
    public ResourceKey<DamageType>[] values() {
      return new ResourceKey[] { melee, ranged };
    }
  }
}
