package slimeknights.tconstruct.library.modifiers.fluid.entity;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;

/** Effect to push the target entity by the given amount */
public record PushEntityFluidEffect(LevelingValue value) implements FluidEffect<FluidEffectContext.Entity> {
  public static final RecordLoadable<PushEntityFluidEffect> LOADER = RecordLoadable.create(
    LevelingValue.LOADABLE.directField(PushEntityFluidEffect::value),
    PushEntityFluidEffect::new);

  public PushEntityFluidEffect(float flat, float eachLevel) {
    this(new LevelingValue(flat, eachLevel));
  }

  @Override
  public RecordLoadable<? extends FluidEffect<FluidEffectContext.Entity>> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext.Entity context, FluidAction action) {
    if (value.isFlat() && !level.isFull()) {
      return 0;
    }

    Entity target = context.getTarget();
    // to compute the direction, we do one of three things
    Vec3 direction;
    Projectile projectile = context.getProjectile();
    // projectile means use the projectile motion
    if (projectile != null) {
      direction = projectile.getDeltaMovement();
    } else {
      LivingEntity holder = context.getEntity();
      if (holder == target || holder == null) {
        direction = target.getLookAngle();
      } else {
        direction = target.position().subtract(holder.position());
      }
    }
    // this is similar to projectile knockback, except in 3D
    direction = direction.normalize().scale(value.compute(level.value()));
    if (direction.lengthSqr() > 0) {
      if (action.execute()) {
        target.push(direction.x, direction.y, direction.z);
        if (context.getLivingTarget() instanceof ServerPlayer serverPlayer) {
          serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
        }
      }
      return value.isFlat() ? 1 : level.value();
    }
    return 0;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    return Component.translatable(FluidEffect.getTranslationKey(getLoader()) + (value.compute(1) >= 0 ? ".push" : ".pull"));
  }
}
