package slimeknights.tconstruct.tools.entity;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.tools.TinkerModifiers;

import static slimeknights.tconstruct.library.tools.helper.ModifierUtil.asLiving;

/**
 * Projectile that applies a fluid effect on hit, styled after llama spit.
 */
public class FluidEffectProjectile extends LlamaSpit {
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
        int consumed = recipe.applyToEntity(fluid, power, new FluidEffectContext.Entity(level, asLiving(getOwner()), this, target), FluidAction.EXECUTE);
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

  @Override
  protected void onHitBlock(BlockHitResult hitResult) {
    // hit the block
    BlockPos hit = hitResult.getBlockPos();
    Level level = level();
    BlockState state = level.getBlockState(hit);
    state.onProjectileHit(level, state, hitResult, this);
    // handle the fluid
    FluidStack fluid = getFluid();
    if (!level.isClientSide) {
      if (!fluid.isEmpty()) {
        FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
        if (recipe.hasEntityEffects()) {
          // run the effect until we run out of fluid or it fails
          FluidEffectContext.Block context = new FluidEffectContext.Block(level, asLiving(getOwner()), this, hitResult);
          int consumed;
          do {
            consumed = recipe.applyToBlock(fluid, power, context, FluidAction.EXECUTE);
            fluid.shrink(consumed);
          } while (consumed > 0 && !fluid.isEmpty());
          // we can continue to live if we have fluid left and we broke our block, allows some neat shenanigans
          // TODO: maybe use a more general check than air?
          if (!fluid.isEmpty() && level.getBlockState(hit).isAir()) {
            return;
          }
        }
      }
      this.discard();
    }
  }

  /* Network */

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(FLUID, FluidStack.EMPTY);
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
