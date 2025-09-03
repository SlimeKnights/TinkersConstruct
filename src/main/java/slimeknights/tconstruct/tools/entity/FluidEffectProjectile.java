package slimeknights.tconstruct.tools.entity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import slimeknights.mantle.inventory.EmptyItemHandler;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithKnockback;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Projectile that applies a fluid effect on hit, based on {@link LlamaSpit}, but not extending as we want custom movement logic
 */
@Setter
public class FluidEffectProjectile extends Projectile implements ProjectileWithKnockback, ProjectileWithPower {
  private static final EntityDataAccessor<FluidStack> FLUID = SynchedEntityData.defineId(FluidEffectProjectile.class, TinkerFluids.FLUID_DATA_SERIALIZER);
  /** Projectile power determining how much fluid is used at most */
  @Getter
  private float power = 1;
  /** Amount of knockback for the projectile to cause, scaled like arrow knockback */
  private float knockback = 1;
  /** Position of the cannon that fired this projectile */
  @Nullable
  private BlockPos cannon;

  public FluidEffectProjectile(EntityType<? extends FluidEffectProjectile> type, Level level) {
    super(type, level);
  }

  public FluidEffectProjectile(Level level) {
    this(TinkerModifiers.fluidSpitEntity.get(), level);
  }

  public FluidEffectProjectile(Level level, LivingEntity owner, FluidStack fluid, float power) {
    this(level);
    this.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
    this.setOwner(owner);
    this.setFluid(fluid);
    this.setPower(power);
  }

  public FluidEffectProjectile(Level level, BlockPos cannon, Direction facing, FluidStack fluid, float power) {
    this(level);
    this.setCannon(cannon);
    this.setPos(
      cannon.getX() + 0.5 + (0.7 * facing.getStepX()),
      cannon.getY() + 0.5 + (0.7 * facing.getStepY()),
      cannon.getZ() + 0.5 + (0.7 * facing.getStepZ())
    );
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
  public void addKnockback(float amount) {
    this.knockback += amount;
  }

  /** @deprecated use {@link #addKnockback(float)} */
  @Deprecated
  public int getKnockback() {
    return (int) knockback;
  }

  /** @deprecated use {@link #addKnockback(float)} */
  @Deprecated
  public void setKnockback(int knockback) {
    this.knockback = knockback;
  }

  @Override
  protected Component getTypeName() {
    return getFluid().getDisplayName();
  }

  /** Gets the cannon tank */
  @Nullable
  private IItemHandlerModifiable getCannonInventory() {
    Level level = level();
    if (this.cannon != null && level.isLoaded(this.cannon)) {
      BlockEntity cannonBE = level.getBlockEntity(this.cannon);
      if (cannonBE != null && cannonBE.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(EmptyItemHandler.INSTANCE) instanceof IItemHandlerModifiable modifiable) {
        return modifiable;
      }
    }
    return null;
  }

  /** Creates a context builder prepared with the owner and projectile information */
  private FluidEffectContext.Builder buildContext() {
    Level level = level();
    FluidEffectContext.Builder builder = FluidEffectContext.builder(level).projectile(this);
    Entity owner = getOwner();
    if (owner != null) {
      builder.user(owner);
    }
    if (this.cannon != null) {
      IItemHandler handler = getCannonInventory();
      if (handler != null) {
        builder.stack(handler.getStackInSlot(0).copy());
      }
    }
    return builder;
  }

  /** Updates the stack for the fluid cannon */
  private void updateCannonStack(FluidEffectContext context) {
    if (cannon != null) {
      IItemHandlerModifiable handler = getCannonInventory();
      if (handler != null) {
        handler.setStackInSlot(0, context.getStack());
      }
    }
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
        EntityDimensions dimensions = getType().getDimensions();
        float factor = 0.01f;
        if (((BlockHitResult)hitResult).getDirection().getAxis() == Axis.Y) {
          factor += dimensions.height;
        } else {
          factor += dimensions.width / 2;
        }
        newLocation = hitResult.getLocation().add(velocity.normalize().scale(factor));
      } else {
        newLocation = newLocation.add(velocity);
      }
      velocity = velocity.scale(0.99f);
      if (!this.isNoGravity()) {
        FluidStack fluid = getFluid();
        velocity = velocity.add(0, fluid.getFluid().getFluidType().isLighterThanAir() ? 0.06 : -0.06, 0);
      }
      this.setDeltaMovement(velocity);
      this.setPos(newLocation);
    }
    // if the projectile moves above the world, delete it
    // only likely to happen for lighter than air fluids
    if (getY() > level().getMaxBuildHeight() + 64) {
      this.discard();
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
        FluidEffectContext.Entity context = buildContext().location(result.getLocation()).target(target);
        int consumed = recipe.applyToEntity(fluid, power, context, FluidAction.EXECUTE);
        // update the stack
        if (consumed > 0) {
          updateCannonStack(context);
        }
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
          FluidEffectContext.Block context = buildContext().block(hitResult);
          int consumed = recipe.applyToBlock(fluid, power, context, FluidAction.EXECUTE);
          boolean changed = consumed > 0;
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
              changed |= consumed > 0;
            }
          }
          // update the item in the context with the stack changes
          if (changed) {
            updateCannonStack(context);
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
    nbt.putFloat("knockback", knockback);
    if (cannon != null) {
      nbt.put("cannon", NbtUtils.writeBlockPos(cannon));
    }
    FluidStack fluid = getFluid();
    if (!fluid.isEmpty()) {
      nbt.put("fluid", fluid.writeToNBT(new CompoundTag()));
    }
  }

  @Override
  protected void readAdditionalSaveData(CompoundTag nbt) {
    super.readAdditionalSaveData(nbt);
    this.power = nbt.getFloat("power");
    this.knockback = nbt.getFloat("knockback");
    if (nbt.contains("cannon")) {
      this.cannon = NbtUtils.readBlockPos(nbt.getCompound("cannon"));
    } else {
      this.cannon = null;
    }
    setFluid(FluidStack.loadFluidStackFromNBT(nbt.getCompound("fluid")));
  }
}
