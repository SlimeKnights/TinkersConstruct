package slimeknights.tconstruct.library.modifiers.fluid.general;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect.DamageTypePair;
import slimeknights.tconstruct.library.utils.CustomExplosion;

/** Fluid effect that simply explodes */
public record ExplosionFluidEffect(LevelingValue radius, LevelingValue damage, LevelingValue knockback, boolean placeFire, Explosion.BlockInteraction blockInteraction) implements FluidEffect<FluidEffectContext> {
  public static final RecordLoadable<ExplosionFluidEffect> LOADER = RecordLoadable.create(
    LevelingValue.LOADABLE.requiredField("radius", ExplosionFluidEffect::radius),
    LevelingValue.LOADABLE.requiredField("damage", ExplosionFluidEffect::damage),
    LevelingValue.LOADABLE.defaultField("knockback", LevelingValue.flat(1), true, ExplosionFluidEffect::knockback),
    BooleanLoadable.INSTANCE.defaultField("place_fire", false, ExplosionFluidEffect::placeFire),
    new EnumLoadable<>(Explosion.BlockInteraction.class).requiredField("block_interaction", ExplosionFluidEffect::blockInteraction),
    ExplosionFluidEffect::new);

  /** Use the builder via {@link #radius(float, float)}, directly calling the constructor is subject to break when we add new features. */
  @Internal
  public ExplosionFluidEffect {}

  /** Creates a new builder instance */
  public static Builder radius(float flat, float perLevel) {
    return new Builder(new LevelingValue(flat, perLevel));
  }

  @Override
  public RecordLoadable<? extends FluidEffect<FluidEffectContext>> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext context, FluidAction action) {
    // if nothing scales, charge 1 level. If anything scales, scale it
    boolean isFlat = radius.isFlat() && damage.isFlat() && knockback.isFlat();
    if (isFlat && !level.isFull()) {
      return 0;
    }
    float value = level.value();
    float radius = this.radius.computeForScale(value);
    if (radius < 0.5f) {
      return 0;
    }

    if (action.execute()) {
      // select the damage source based on the entities and whether we want difficulty scaling
      Level world = context.getLevel();
      Entity cause = context.getEntity();
      DamageTypePair damageType = (cause != null ? TinkerDamageTypes.MOB_EXPLOSION : TinkerDamageTypes.EXPLOSION);
      Projectile projectile = context.getProjectile();
      DamageSource damageSource;
      if (projectile != null) {
        damageSource = TinkerDamageTypes.source(world.registryAccess(), damageType.ranged(), projectile, cause);
      } else {
        damageSource = TinkerDamageTypes.source(world.registryAccess(), damageType.melee(), cause, cause);
      }
      // create the explosion
      boolean breakRestricted = context.breakRestricted();
      new CustomExplosion(
        // source is projectile, if null set no source to prevent the user from being immune to the explosion
        world, context.getLocation(), radius, projectile, null,
        damage.computeForScale(value), damageSource, knockback.computeForScale(value), null,
        placeFire && !breakRestricted, breakRestricted ? Explosion.BlockInteraction.KEEP : blockInteraction, true
      ).handleServer();
    }
    return isFlat ? 1 : level.value();
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @Accessors(fluent = true)
  @Setter
  @CanIgnoreReturnValue
  @SuppressWarnings("unused") // API
  public static class Builder {
    /** Explosion distance from target */
    private final LevelingValue radius;
    /** Explosion damage multiplier for entities */
    private LevelingValue damage = LevelingValue.flat(0);
    /** Explosion knockback multiplier for entities */
    private LevelingValue knockback = LevelingValue.flat(1f);
    /** If true, explosion places fires */
    private boolean placeFire = false;
    /** Behavior of explosion against blocks */
    private BlockInteraction blockInteraction = BlockInteraction.DESTROY_WITH_DECAY;

    /** Sets the explosion not damage blocks */
    public Builder ignoreBlocks() {
      return blockInteraction(BlockInteraction.KEEP);
    }

    /** Sets the explosion to drop all blocks */
    public Builder noBlockDecay() {
      return blockInteraction(BlockInteraction.DESTROY);
    }

    /** Sets this to place fire */
    public Builder placeFire() {
      return placeFire(true);
    }

    /** Builds the final effect */
    @CheckReturnValue
    public ExplosionFluidEffect build() {
      return new ExplosionFluidEffect(radius, damage, knockback, placeFire, blockInteraction);
    }
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    return FluidEffect.makeTranslation(getLoader(), radius.compute(1));
  }
}
