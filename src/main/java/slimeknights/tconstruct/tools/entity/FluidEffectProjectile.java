package slimeknights.tconstruct.tools.entity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;

import static slimeknights.tconstruct.library.tools.helper.ModifierUtil.asLiving;

/**
 * Projectile that applies a fluid effect on hit, based on {@link LlamaSpit}, but not extending as we want custom movement logic
 */
public class FluidEffectProjectile extends Projectile {
  private static final EntityDataAccessor<FluidStack> FLUID = SynchedEntityData.defineId(FluidEffectProjectile.class, TinkerFluids.FLUID_DATA_SERIALIZER);

  @Setter
  private float power = 1;
  @Setter
  @Getter
  private int knockback = 1;

  public FluidEffectProjectile(EntityType<? extends FluidEffectProjectile> type, Level level) {
    super(type, level);
  }

  public FluidEffectProjectile(Level level, LivingEntity owner, FluidStack fluid, float power) {
    this(TinkerModifiers.fluidSpitEntity.get(), level);
    this.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
    this.setOwner(owner);
    this.setFluid(fluid);
    this.setPower(power);
  }

  /**
   * Gets the fluid for this spit
   */
  public FluidStack getFluid() {
    return this.entityData.get(FLUID);
  }

  /**
   * Sets the fluid for this spit
   */
  public void setFluid(FluidStack fluid) {
    this.entityData.set(FLUID, fluid);
  }

  @Override
  public void tick() {
    super.tick();
    HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
    HitResult.Type hitType = hitResult.getType();
    if (hitType != HitResult.Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitResult)) {
      this.onHit(hitResult);
    }
    if (!this.isRemoved()) {
      this.updateRotation();
      Vec3 newLocation = position();
      Vec3 velocity = this.getDeltaMovement();
      // if we hit a block and are still alive, relocate ourself to that position so we don't skip blocks
      if (hitType == HitResult.Type.BLOCK) {
        newLocation = hitResult.getLocation();
      } else {
        newLocation = newLocation.add(velocity);
      }
      velocity = velocity.scale(0.99f);
      if (!this.isNoGravity()) {
        velocity = velocity.add(0, -0.06, 0);
      }
      this.setDeltaMovement(velocity);
      this.setPos(newLocation);
    }
  }

  @Override
  protected void onHitEntity(EntityHitResult result) {
    Entity target = result.getEntity();
    // apply knockback to the entity regardless of fluid type
    if (knockback > 0) {
      Vec3 vec3 = this.getDeltaMovement().multiply(1, 0, 1).normalize().scale(knockback * 0.6);
      if (vec3.lengthSqr() > 0) {
        target.push(vec3.x, 0.1, vec3.z);
      }
    }
    FluidStack fluid = getFluid();
    Level level = level();
    if (!level.isClientSide && !fluid.isEmpty()) {
      FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
      if (recipe.hasEntityEffects()) {
        int consumed = recipe.applyToEntity(fluid, power, new FluidEffectContext.Entity(level, asLiving(getOwner()), this, target, result.getLocation()), FluidAction.EXECUTE);
        // shrink our internal fluid, means we get a crossbow piercing like effect if its not all used
        // discarding when empty ensures the fluid won't continue with the block effect
        // unlike blocks, failing is fine, means we just continue through to the block below the entity
        fluid.shrink(consumed);
        if (fluid.isEmpty()) {
          this.discard();
        } else {
          setFluid(fluid);
        }
      }
    }
  }

  /**
   * Gets a list of all directions sorted based on the closest to the given direction.
   * Same as {@link Direction#getNearest(double, double, double)} except it returns all directions in order instead of just the first.
   */
  private static Stream<Direction> orderByNearest(Vec3 delta) {
    record DirectionDistance(Direction direction, double distance) {}
    return Arrays.stream(Direction.values())
                 // negated as we want the largest
                 .map(dir -> new DirectionDistance(dir, -(dir.getStepX() * delta.x + dir.getStepY() * delta.y + dir.getStepZ() * delta.z)))
                 .sorted(Comparator.comparingDouble(DirectionDistance::distance))
                 .map(DirectionDistance::direction);
  }

  @Override
  protected void onHitBlock(BlockHitResult hitResult) {
    super.onHitBlock(hitResult);

    // hit the block
    // handle the fluid
    Level level = level();
    if (!level.isClientSide) {
      FluidStack fluid = getFluid();
      if (!fluid.isEmpty()) {
        FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
        if (recipe.hasBlockEffects()) {
          FluidEffectContext.Block context = new FluidEffectContext.Block(level, asLiving(getOwner()), this, hitResult);
          int consumed = recipe.applyToBlock(fluid, power, context, FluidAction.EXECUTE);
          fluid.shrink(consumed);
          // we can continue to live if we have fluid left and we broke our block
          if (!fluid.isEmpty()) {
            // if we are going to get discarded due to being in a block, apply ourself to our neighbors
            BlockPos hit = hitResult.getBlockPos();
            if (level.getBlockState(hit).isAir()) {
              setFluid(fluid);
              return;
            }
            // if not air, this projectile will be removed, apply effect to neighbors before discarding
            Iterator<Direction> iterator = orderByNearest(getDeltaMovement()).iterator();
            while (iterator.hasNext() && !fluid.isEmpty()) {
              consumed = recipe.applyToBlock(fluid, power, context.withHitResult(Util.offset(hitResult, hit.relative(iterator.next().getOpposite()))), FluidAction.EXECUTE);
              fluid.shrink(consumed);
            }
          }
        }
      }
      this.discard();
    }
  }

  /* Network */

  @Override
  protected void defineSynchedData() {
    this.entityData.define(FLUID, FluidStack.EMPTY);
  }

  @Override
  public void recreateFromPacket(ClientboundAddEntityPacket packet) {
    // copied from llama spit
    super.recreateFromPacket(packet);
    double x = packet.getXa();
    double y = packet.getYa();
    double z = packet.getZa();
    for(int i = 0; i < 7; i++) {
      double offset = 0.4D + 0.1D * i;
      this.level().addParticle(ParticleTypes.SPIT, this.getX(), this.getY(), this.getZ(), x * offset, y, z * offset);
    }
    this.setDeltaMovement(x, y, z);
  }

  @Override
  protected void addAdditionalSaveData(CompoundTag nbt) {
    super.addAdditionalSaveData(nbt);
    nbt.putFloat("power", power);
    nbt.putInt("knockback", knockback);
    FluidStack fluid = getFluid();
    if (!fluid.isEmpty()) {
      nbt.put("fluid", fluid.writeToNBT(new CompoundTag()));
    }
  }

  @Override
  protected void readAdditionalSaveData(CompoundTag nbt) {
    super.readAdditionalSaveData(nbt);
    this.power = nbt.getFloat("power");
    this.knockback = nbt.getInt("knockback");
    setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid")));
  }
}
